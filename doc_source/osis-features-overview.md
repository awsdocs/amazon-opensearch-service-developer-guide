# Overview of pipeline features in Amazon OpenSearch Ingestion<a name="osis-features-overview"></a>

Amazon OpenSearch Ingestion provisions *pipelines*, which consist of a source, a buffer, zero or more processors, and one or more sinks\. Ingestion pipelines are powered by Data Prepper as the data engine\. For an overview of the various components of a pipeline, see [Key concepts](ingestion.md#ingestion-process)\.

The following sections provide an overview of some of the most commonly used features in Amazon OpenSearch Ingestion\.

**Note**  
This is not an exhaustive list of features that are available for pipelines\. For comprehensive documentation of all available pipeline functionality, see the [Data Prepper documentation](https://opensearch.org/docs/latest/data-prepper/pipelines/pipelines/)\. Note that OpenSearch Ingestion places some constraints on the plugins and options that you can use\. For more information, see [Supported plugins and options for Amazon OpenSearch Ingestion pipelines](pipeline-config-reference.md)\.

**Topics**
+ [Splitting](#osis-features-splitting)
+ [Chaining](#osis-features-chaining)
+ [Dead\-letter queues](#osis-features-dlq)
+ [Index management](#osis-features-index-management)
+ [End\-to\-end acknowledgement](#osis-features-e2e)
+ [Source backpressure](#osis-features-backpressure)

## Splitting<a name="osis-features-splitting"></a>

You can configure an OpenSearch Ingestion pipeline to *split* incoming events into a sub\-pipeline, allowing you to perform different types of processing on the same incoming event\.

The following example pipeline splits incoming events into two sub\-pipelines\. Each sub\-pipeline uses its own processor to enrich and manipulate the data, and then sends the data to different OpenSearch indexes\.

```
version: "2"
log-pipeline:
  source:
    http:
    ...
  sink:
    - pipeline:
        name: "logs_enriched_one_pipeline"
    - pipeline:
        name: "logs_enriched_two_pipeline"

logs_enriched_one_pipeline:
  source:
    log-pipeline
  processor:
   ...
  sink:
    - opensearch:
        # Provide a domain or collection endpoint
        # Enable the 'serverless' flag if the sink is an OpenSearch Serverless collection
        aws:
          ...
        index: "encriched_one_logs"

logs_enriched_two_pipeline:
  source:
    log-pipeline
  processor:
   ...
  sink:
    - opensearch:
        # Provide a domain or collection endpoint
        # Enable the 'serverless' flag if the sink is an OpenSearch Serverless collection
        aws:
          ...
          index: "encriched_two_logs"
```

## Chaining<a name="osis-features-chaining"></a>

You can *chain* multiple sub\-pipelines together in order to perform data processing and enrichment in chunks\. In other words, you can enrich an incoming event with certain processing capabilities in one sub\-pipeline, then send it to another sub\-pipeline for additional enrichment with a different processor, and finally send it to its OpenSearch sink\.

In the following example, the `log_pipeline` sub\-pipeline enriches an incoming log event with a set of processors, then sends the event to an OpenSearch index named `enriched_logs`\. The pipeline sends the same event to the `log_advanced_pipeline` sub\-pipeline, which processes it and sends it to a different OpenSearch index named `encriched_advanced_logs`\. 

```
version: "2"
log-pipeline:
  source:
    http:
    ...
  processor:
    ...
  sink:
    - opensearch:
        # Provide a domain or collection endpoint
        # Enable the 'serverless' flag if the sink is an OpenSearch Serverless collection
        aws:
          ...
          index: "enriched_logs"
    - pipeline:
        name: "log_advanced_pipeline"

log_advanced_pipeline:
  source:
    log-pipeline
  processor:
   ...
  sink:
    - opensearch:
        # Provide a domain or collection endpoint
        # Enable the 'serverless' flag if the sink is an OpenSearch Serverless collection
        aws:
          ...
          index: "encriched_advanced_logs"
```

## Dead\-letter queues<a name="osis-features-dlq"></a>

Dead\-letter queues \(DLQs\) are destinations for events that a pipeline fails to write to a sink\. In OpenSearch Ingestion, you must specify a Amazon S3 bucket with appropriate write permissions to be used as the DLQ\. You can add a DLQ configuration to every sink within a pipeline\. When a pipeline encounters write errors, it creates DLQ objects in the configured S3 bucket\. DLQ objects exist within a JSON file as an array of failed events\.

A pipeline writes events to the DLQ when either of the following conditions are met:
+ The `max_retries` for the OpenSearch sink have been exhausted\. OpenSearch Ingestion requires a minimum of 16 for this option\.
+ Events are rejected by the sink due to an error condition\.

### Configuration<a name="osis-features-dlq-config"></a>

To configure a dead\-letter queue for a sub\-pipeline, specify the `dlq` option within the `opensearch` sink configuration:

```
apache-log-pipeline:
  ...
  sink:
    opensearch:
      dlq:
        s3:
          bucket: "my-dlq-bucket"
          key_path_prefix: "dlq-files"
          region: "us-west-2"
          sts_role_arn: "arn:aws:iam::123456789012:role/dlq-role"
```

Files written to this S3 DLQ will have the following naming pattern:

```
dlq-v${version}-${pipelineName}-${pluginId}-${timestampIso8601}-${uniqueId}
```

For more information, see [Dead\-Letter Queues \(DLQ\)](https://opensearch.org/docs/latest/data-prepper/pipelines/dlq/)\.

For instructions to configure the `sts_role_arn` role, see [Writing to a dead\-letter queue](pipeline-security-overview.md#pipeline-security-dlq)\.

### Example<a name="osis-features-dlq-example"></a>

Consider the following example DLQ file:

```
dlq-v2-apache-log-pipeline-opensearch-2023-04-05T15:26:19.152938Z-e7eb675a-f558-4048-8566-dac15a4f8343
```

Here's an example of data that failed to be written to the sink, and is sent to the DLQ S3 bucket for further analysis:

```
Record_0	
pluginId            "opensearch"
pluginName          "opensearch"
pipelineName        "apache-log-pipeline"
failedData	
index		  "logs"
indexId		 null
status		  0
message		"Number of retries reached the limit of max retries (configured value 15)"
document	
log		    "sample log"
timestamp	    "2023-04-14T10:36:01.070Z"

Record_1	
pluginId            "opensearch"
pluginName          "opensearch"
pipelineName        "apache-log-pipeline"
failedData	
index               "logs"
indexId		 null
status		  0
message		"Number of retries reached the limit of max retries (configured value 15)"
document	
log                 "another sample log"
timestamp           "2023-04-14T10:36:01.071Z"
```

## Index management<a name="osis-features-index-management"></a>

Amazon OpenSearch Ingestion has many index management capabilities, including the following\.

### Creating indexes<a name="osis-features-index-management-create"></a>

You can specify an index name in a pipeline sink and OpenSearch Ingestion creates the index when it provisions the pipeline\. If an index already exists, the pipeline uses it to index incoming events\. If you stop and restart a pipeline, or if you update its YAML configuration, the pipeline attempts to create new indexes if they don't already exist\. A pipeline can never delete an index\.

The following example sinks create two indexes when the pipeline is provisioned:

```
sink:
  - opensearch:
      index: apache_logs
  - opensearch:
      index: nginx_logs
```

### Generating index names and patterns<a name="osis-features-index-management-patterns"></a>

You can generate dynamic index names by using variables from the fields of incoming events\. In the sink configuration, specify `index_type` as `custom` and use the format `string${}` to signal string interpolation, and use a JSON pointer to extract fields from events\.

For example, the following pipeline selects the `metadataType` field from incoming events to generate index names\.

```
pipeline:
  ...
  sink:
    opensearch:
      index_type: custom
      index: "metadata-${metadataType}"
```

The following configuration continues to generate a new index every day or every hour\.

```
pipeline:
  ...
  sink:
    opensearch:
      index_type: custom
      index: "metadata-${metadataType}-%{yyyy.MM.dd}"

pipeline:
  ...
  sink:
    opensearch:
      index_type: custom
      index: "metadata-${metadataType}-%{yyyy.MM.dd.HH}"
```

The index name can also be a plain string with a date\-time pattern as a suffix, such as `my-index-%{yyyy.MM.dd}`\. When the sink sends data to OpenSearch, it replaces the date\-time pattern with UTC time and creates a new index for each day, such as `my-index-2022.01.25`\. For more information, see the [DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html) class\.

This index name can also be a formatted string \(with or without a date\-time pattern suffix\), such as `my-${index}-name`\. When the sink sends data to OpenSearch, it replaces the `"${index}"` portion with the value in the event being processed\. If the format is `"${index1/index2/index3}"`, it replaces the field `index1/index2/index3` with its value in the event\.

### Generating document IDs<a name="osis-features-index-management-ids"></a>

A pipeline can generate a document ID while indexing documents to OpenSearch\. It can infer these document IDs from the fields within incoming events\.

This example uses the `uuid` field from an incoming event to generate a document ID\.

```
pipeline:
  ...
  sink:
    opensearch:
      index_type: custom
      index: "metadata-${metadataType}-%{yyyy.MM.dd}" 
      document_id_field: "uuid"
```

In the following example, the [Add entries](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/add-entries/) processor merges the fields `uuid` and `other_field` from the incoming event to generate a document ID\.

The `create` action ensures that documents with identical IDs aren't overwritten\. The pipeline drops duplicate documents without any retry or DLQ event\. This is a reasonable expectation for pipeline authors who use this action, because the goals is to avoid updating existing documents\.

```
pipeline:
  ...
  processor:
   - add_entries:
      entries:
        - key: "my_doc_id_field"
          format: "${uuid}-${other_field}"
  sink:
    - opensearch:
       ...
       action: "create"
       document_id_field: "my_doc_id_field"
```

You might want to set an event's document ID to a field from a sub\-object\. In the following example, the OpenSearch sink plugin uses the sub\-object `info/id` to generate a document ID\.

```
sink:
  - opensearch:
       ...
       document_id_field: info/id
```

Given the following event, the pipeline will generate a document with the `_id` field set to `json001`:

```
{
   "fieldA":"arbitrary value",
   "info":{
      "id":"json001",
      "fieldA":"xyz",
      "fieldB":"def"
   }
}
```

### Generating routing IDs<a name="osis-features-index-management-routing-ids"></a>

You can use the `routing_field` option within the OpenSearch sink plugin to set the value of a document routing property \(`_routing`\) to a value from an incoming event\.

Routing supports JSON pointer syntax, so nested fields also are available, not just top\-level fields\.

```
sink:
  - opensearch:
       ...
       routing_field: metadata/id
       document_id_field: id
```

Given the following event, the plugin generates a document with the `_routing` field set to `abcd`:

```
{
   "id":"123",
   "metadata":{
      "id":"abcd",
      "fieldA":"valueA"
   },
   "fieldB":"valueB"
}
```

For instructions to create index templates that pipelines can use during index creation, see [Index templates](https://opensearch.org/docs/latest/im-plugin/index-templates/)\.

## End\-to\-end acknowledgement<a name="osis-features-e2e"></a>

OpenSearch Ingestion ensures the durability and reliability of data by tracking its delivery from source to sinks in stateless pipelines using *end\-to\-end acknowledgement*\. Currently, only the [S3 source](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/s3/) plugin supports end\-to\-end acknowledgement\.

With end\-to\-end acknowledgement, the pipeline source plugin creates an *acknowledgement set* to monitor a batch of events\. It receives a positive acknowledgement when those events are successfully sent to their sinks, or a negative acknowledgement when any of the events could not be sent to their sinks\.

In the event of a failure or crash of a pipeline component, or if a source fails to receive an acknowledgement, the source times out and takes necessary actions such as retrying or logging the failure\. If the pipeline has multiple sinks or multiple sub\-pipelines configured, event\-level acknowledgements are sent only after the event is sent to *all* sinks in *all* sub\-pipelines\. If a sink has a DLQ configured, end\-to\-end acknowledgements also tracks events written to the DLQ\.

To enable end\-to\-end acknowledgement, include the `acknowledgments` option within the source configuration:

```
s3-pipeline:
  source:
    s3:
      acknowledgments: true
...
```

## Source backpressure<a name="osis-features-backpressure"></a>

A pipeline can experience back pressure when it's busy processing data, or if its sinks are temporarily down or slow to ingest data\. OpenSearch Ingestion has different ways of handling back pressure depending on the source plugin that a pipeline is using\.

### HTTP source<a name="osis-features-backpressure-http"></a>

Pipelines that use the [HTTP source](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/http-source/) plugin handle back pressure differently depending on which pipeline component is congested:
+ **Buffers** – When buffers are full, the pipeline starts returning HTTP status `REQUEST_TIMEOUT` with error code 408 back to the source endpoint\. As buffers are freed up, the pipeline starts processing HTTP events again\.
+ **Source threads** – When all HTTP source threads are busy executing requests and the unprocessed request queue size has exceeded the maximum allowed number of requests, the pipeline starts to return HTTP status `TOO_MANY_REQUESTS` with error code 429 back to the source endpoint\. When the request queue drops below the maximum allowed queue size, the pipeline starts processing requests again\.

### OTel source<a name="osis-features-backpressure-otel"></a>

When buffers are full for pipelines that use OpenTelemetry sources \([OTel logs](https://github.com/opensearch-project/data-prepper/tree/main/data-prepper-plugins/otel-logs-source), [OTel metrics](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-metrics-source/), and [OTel trace](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-trace/)\), the pipeline starts to return HTTP status `REQUEST_TIMEOUT` with error code 408 to the source endpoint\. As buffers are freed up, the pipeline starts processing events again\.

### S3 source<a name="osis-features-backpressure-s3"></a>

When buffers are full for pipelines with an [S3](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/s3/) source, the pipelines stop processing SQS notifications\. As the buffers are freed up, the pipelines start processing notifications again\. 

If a sink is down or unable to ingest data and and end\-to\-end acknowledgement is enabled for the source, the pipeline stops processing SQS notifications until it receives a successful acknowledgement from all sinks\.