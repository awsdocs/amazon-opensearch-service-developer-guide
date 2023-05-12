# Trace Analytics with Amazon OpenSearch Ingestion<a name="use-cases-trace-analytics"></a>

You can use Amazon OpenSearch Ingestion to collect OpenTelemetry trace data and transform it for use in OpenSearch Service\. The following example pipeline uses three sub\-pipelines to monitor Trace Analytics: `entry-pipeline`, `span-pipeline`, and `service-map-pipeline`\.

## OpenTelemetry trace source<a name="use-cases-trace-analytics-source"></a>

The [Otel trace source](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-trace/) plugin accepts trace data from the [OpenTelemetry Collector](https://opentelemetry.io/docs/collector/)\. The plugin follows the [OpenTelemetry Protocol](https://opentelemetry.io/docs/reference/specification/protocol/) and officially supports industry\-standard encryption HTTPS\.

## Processors<a name="use-cases-trace-analytics-processors"></a>

You can use the following processors for Trace Analytics:
+ [OTel trace](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/otel-trace-raw/) – Receives a collection of span records from the source and performs stateful processing, extraction, and completion of fields\.
+ [OTel trace group](https://github.com/opensearch-project/data-prepper/tree/main/data-prepper-plugins/otel-trace-group-processor) – Fills in missing trace group fields in the collection of span records\.
+ [Service\-map](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/service-map-stateful/) – Performs preprocessing for trace data and builds metadata to display service\-map dashboards\.

## OpenSearch sink<a name="use-cases-trace-analytics-sink"></a>

The [OpenSearch sink](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sinks/opensearch/) plugin provides indexes and index templates that are specific to Trace Analytics\. The following OpenSearch indexes are specific to Trace Analytics:
+ `otel-v1-apm-span` – Stores the output from the OTel trace processor\.
+ `otel-v1-apm-service-map` – Stores the output from the Service\-map processor\.

## Pipeline configuration<a name="use-cases-trace-analytics-config"></a>

The following example pipeline supports [Observability for OpenSearch Dashboards](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/observability.html)\. The first sub\-pipeline \(`entry-pipeline`\) receives data from the OpenTelemetry Collector and uses two other sub\-pipelines as sinks\.

The `span-pipeline` sub\-pipeline parses the trace data and enriches and ingests the span documents into a span index\. The `service-map-pipeline` sub\-pipeline aggregates traces into a service map and writes documents to a service map index\.

```
version: "2"
entry-pipeline:
  source:
    otel_trace_source:
      # Provide the path for ingestion. This will be the endpoint URI path in the OpenTelemetry Exporter configuration.
      # ${pipelineName} will be replaced with the sub-pipeline name. In this case it would be "/entry-pipeline/v1/traces". 
      path: "/${pipelineName}/v1/traces"
  processor:
    - trace_peer_forwarder
  sink:
    - pipeline:
        name: "span-pipeline"
    - pipeline:
        name: "service-map-pipeline"

span-pipeline:
  source:
    pipeline:
      name: "entry-pipeline"
  processor:
    - otel_traces
  sink:
    - opensearch:
        ...
        index_type: trace-analytics-raw

service-map-pipeline:
  source:
    pipeline:
      name: "entry-pipeline"
  processor:
    - service_map
  sink:
    - opensearch:
        ...
        index_type: trace-analytics-service-map
```

You must run the OpenTelemetry Collector in your environment to send data to the ingestion endpoint\. For another example pipeline, see the **Trace Analytics pipeline** blueprint\. For more information, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.