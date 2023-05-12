# Sending data to Amazon OpenSearch Ingestion pipelines<a name="configure-client"></a>

In order to successfully ingest data into an Amazon OpenSearch Ingestion pipeline, you must configure your client application \(the *source*\) to send data to the pipeline endpoint\. Your source might be clients like Fluent Bit logs, the OpenTelemetry Collector, or a simple S3 bucket\. The exact configuration differs for each client\.

The important differences during source configuration \(compared to sending data directly to an OpenSearch Service domain or OpenSearch Serverless collection\) are the AWS service name \(`osis`\) and the host endpoint, which must be the pipeline endpoint\.

**Topics**
+ [Locating the ingestion endpoint](#configure-client-endpoint)
+ [Creating an ingestion role](#configure-client-auth)
+ [Writing from Fluent Bit](#configure-client-fluentbit)
+ [Writing from Amazon S3](#configure-client-s3)
+ [Writing from OpenTelemetry Collector](#configure-client-otel)
+ [Next steps](#configure-client-next)

## Locating the ingestion endpoint<a name="configure-client-endpoint"></a>

In order to ingest data into a domain, send it to the pipeline's ingestion URL\. To locate the endpoint, navigate to the **Pipeline settings** page and copy the **Ingestion URL**:

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/pipeline-endpoint.png)

## Creating an ingestion role<a name="configure-client-auth"></a>

All requests to OpenSearch Ingestion must be signed with [Signature Version 4](https://docs.aws.amazon.com/general/latest/gr/signature-version-4.html)\. At minimum, the role that signs the request must be granted permission for the `osis:Ingest` action, which allows it to send data to an OpenSearch Ingestion pipeline\.

For example, the following AWS Identity and Access Management \(IAM\) policy allows the corresponding role to send data to a single pipeline:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "osis:Ingest",
      "Resource": "arn:aws:osis:us-east-1:{account-id}:pipeline/pipeline-name"
    }
  ]
}
```

**Note**  
To use the role for *all* pipelines, replace the ARN in the `Resource` element with a wildcard \(\*\)\.

### Providing cross\-account ingestion access<a name="configure-client-cross-account"></a>

**Note**  
You can only provide cross\-account ingestion access for public pipelines, not VPC pipelines\.

You might need to ingest data into a pipeline from a different AWS account, such as an account that houses your source application\. If the principal that is writing to a pipeline is in a different account than the pipeline itself, you need to configure the principal to trust another IAM role to ingest data into the pipeline\.

**To configure cross\-account ingestion permissions**

1. Create the ingestion role with `osis:Ingest` permission \(described in the previous section\) within the same AWS account as the pipeline\. For instructions, see [Creating IAM roles](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create.html)\.

1. Attach a [trust policy](https://docs.aws.amazon.com/IAM/latest/UserGuide/roles-managingrole-editing-console.html#roles-managingrole_edit-trust-policy) to the ingestion role that allows a principal in another account to assume it:

   ```
   {
     "Version": "2012-10-17",
     "Statement": [{
        "Effect": "Allow",
        "Principal": {
          "AWS": "arn:aws:iam::{external-account-id}:root"
         },
        "Action": "sts:AssumeRole"
     }]
   }
   ```

1. In the other account, configure your client application \(for example, Fluent Bit\) to assume the ingestion role\. In order for this to work, the application account must grant permissions to the application user or role to assume the ingestion role\.

   The following example identity\-based policy allows the attached principal to assume `ingestion-role` from the pipeline account:

   ```
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Action": "sts:AssumeRole",
         "Resource": "arn:aws:iam::{account-id}:role/ingestion-role"
       }
     ]
   }
   ```

The client application can then use the [AssumeRole](https://docs.aws.amazon.com/STS/latest/APIReference/API_AssumeRole.html) operation to assume `ingestion-role` and ingest data into the associated pipeline\.

## Writing from Fluent Bit<a name="configure-client-fluentbit"></a>

This sample [Fluent Bit configuration file](https://docs.fluentbit.io/manual/pipeline/outputs/http) sends log data from Fluent Bit to an OpenSearch Ingestion pipeline\. For more information about ingesting log data, see [Log Analytics](https://github.com/opensearch-project/data-prepper/blob/main/docs/log_analytics.md) in the Data Prepper documentation\.

Note the following:
+ The `host` value must be your pipeline endpoint\. For example, `pipeline-endpoint.us-east-1.osis.amazonaws.com`\.
+ The `aws_service` value must be `osis`\.
+ The `aws_role_arn` value is the ARN of the AWS IAM role for the client to assume and use for Signature Version 4 authentication\.

```
[INPUT]
  name                  tail
  refresh_interval      5
  path                  /var/log/test.log
  read_from_head        true

[OUTPUT]
  Name http 
  Match *
  Host pipeline-endpoint.us-east-1.osis.amazonaws.com
  Port 443
  URI /log/ingest
  Format json
  aws_auth true
  aws_region us-east-1
  aws_service osis
  aws_role_arn arn:aws:iam::{account-id}:role/ingestion-role
  Log_Level trace
  tls On
```

You can then configure an OpenSearch Ingestion pipeline like the following, which has HTTP as the source:

```
version: "2"
unaggregated-log-pipeline:
  source:
    http:
      path: "/log/ingest"
  processor:
    - grok:
        match:
          log:
            - "%{TIMESTAMP_ISO8601:timestamp} %{NOTSPACE:network_node} %{NOTSPACE:network_host} %{IPORHOST:source_ip}:%{NUMBER:source_port:int} -> %{IPORHOST:destination_ip}:%{NUMBER:destination_port:int} %{GREEDYDATA:details}"
    - grok:
        match:
          details:
            - "'%{NOTSPACE:http_method} %{NOTSPACE:http_uri}' %{NOTSPACE:protocol}"
            - "TLS%{NOTSPACE:tls_version} %{GREEDYDATA:encryption}"
            - "%{NUMBER:status_code:int} %{NUMBER:response_size:int}"
    - delete_entries:
        with_keys: ["details", "log"]

  sink:
    - opensearch:
        hosts: "https://search-domain-endpoint.us-east-1.es.amazonaws.com"
        index: "index_name"
        index_type: custom
        bulk_size: 20
        aws:
          # IAM role that the pipeline assumes to access the domain sink
          sts_role_arn: "arn:aws:iam::{account-id}:role/pipeline-role"
          region: "us-east-1"
```

## Writing from Amazon S3<a name="configure-client-s3"></a>

To use Amazon S3 as the source for an OpenSearch Ingestion pipeline, perform the following steps:
+ [Create an Amazon SQS queue](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/step-create-queue.html)
+ [Create an S3 bucket](https://docs.aws.amazon.com/AmazonS3/latest/userguide/create-bucket-overview.html)
+ [Enable event notifications](https://docs.aws.amazon.com/AmazonS3/latest/userguide/enable-event-notifications.html) on the S3 bucket with the SQS queue as a destination
+ [Add an S3 permissions policy to your pipeline role](pipeline-security-overview.md#pipeline-security-read-data)

You can then configure an OpenSearch Ingestion pipeline like the following, which specifies S3 as the source:

```
version: "2"
s3-pipeline:
  source:
    s3:
      notification_type: "sqs"
      codec:
        newline: null
      sqs:
        queue_url: "https://sqs.us-west-2.amazonaws.com/{account-id}/ingestion-queue"
      compression: "none"
      aws:
        region: "us-west-2"
        # IAM role that the pipeline assumes to read data from the queue. This role must be the same as the pipeline role.
        sts_role_arn: "arn:aws:iam::{account-id}:role/pipeline-role"
  processor:
  - grok:
      match:
        log:
        - "%{COMMONAPACHELOG}"
  - date:
      destination: "@timestamp"
      from_time_received: true
  sink:
  - opensearch:
      hosts: "https://search-domain-endpoint.us-east-1.es.amazonaws.com"
      index: "index_name"
      aws:
        # IAM role that the pipeline assumes to access the domain sink
        sts_role_arn: "arn:aws:iam::{account-id}:role/pipeline-role"
        region: "us-east-1"
```

## Writing from OpenTelemetry Collector<a name="configure-client-otel"></a>

This sample [OpenTelemetry configuration file](https://opentelemetry.io/docs/collector/configuration/) exports trace data from the OpenTelemetry Collector and sends it to an OpenSearch Ingestion pipeline\. For more information about ingesting trace data, see [Trace Analytics](https://github.com/opensearch-project/data-prepper/blob/main/docs/trace_analytics.md) in the Data Prepper documentation\.

Note the following:
+ The `endpoint` value must include your pipeline endpoint\. For example, `https://pipeline-endpoint.us-east-1.osis.amazonaws.com`\.
+ The `service` value must be `osis`\.

```
extensions:
  sigv4auth:
    region: "us-east-1"
    service: "osis"
 
receivers:
  jaeger:
    protocols:
      grpc:
 
exporters:
  otlphttp:
    traces_endpoint: "https://pipeline-endpoint.us-east-1.osis.amazonaws.com/opentelemetry.proto.collector.trace.v1.TraceService/Export"
    auth:
      authenticator: sigv4auth
    compression: none
 
service:
  extensions: [sigv4auth]
  pipelines:
    traces:
      receivers: [jaeger]
      exporters: [otlphttp]
```

You can then configure an OpenSearch Ingestion pipeline like the following, which specifies the [OTel trace](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-trace/) plugin as the source:

```
version: "2"
otel-trace-pipeline:
  source:
    otel_trace_source:
      path: "/otel/ingest"
  processor:
    - trace_peer_forwarder:
  sink:
    - pipeline:
        name: "trace_pipeline"
    - pipeline:
        name: "service_map_pipeline"
trace-pipeline:
  source:
    pipeline:
      name: "otel-trace-pipeline"
  processor:
    - otel_traces:
  sink:
    - opensearch:
        hosts:  "https://search-domain-endpoint.us-east-1.es.amazonaws.com"
        index_type: trace-analytics-raw
        aws:
          # IAM role that OpenSearch Ingestion assumes to access the domain sink   
          sts_role_arn: "arn:aws:iam::{account-id}:role/pipeline-role"
          region: "us-east-1"
        
service-map-pipeline:
  source:
    pipeline:
      name: "otel-trace-pipeline"
  processor:
    - service_map:
  sink:
    - opensearch:
        hosts: "https://search-domain-endpoint.us-east-1.es.amazonaws.com"
        index_type: trace-analytics-service-map
        aws:
          # IAM role that the pipeline assumes to access the domain sink   
          sts_role_arn: "arn:aws:iam::{account-id}:role/pipeline-role"
          region: "us-east-1"
```

For another example pipeline, see the **Trace Analytics pipeline** blueprint\. For more information, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.

## Next steps<a name="configure-client-next"></a>

After you export your data to a pipeline, you can [query it](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/searching.html) from the OpenSearch Service domain that is configured as a sink for the pipeline\. The following resources can help you get started:
+ [Observability in Amazon OpenSearch Service](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/observability.html)
+ [Trace Analytics for Amazon OpenSearch Service](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/trace-analytics.html)
+ [Querying Amazon OpenSearch Service data using Piped Processing Language](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/ppl-support.html)