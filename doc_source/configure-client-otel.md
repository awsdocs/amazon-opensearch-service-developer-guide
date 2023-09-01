# OpenTelemetry Collector<a name="configure-client-otel"></a>

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
    traces_endpoint: "https://pipeline-endpoint.us-east-1.osis.amazonaws.com/v1/traces"
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
      path: "/v1/traces"
  processor:
    - trace_peer_forwarder:
  sink:
    - pipeline:
        name: "trace-pipeline"
    - pipeline:
        name: "service-map-pipeline"
trace-pipeline:
  source:
    pipeline:
      name: "otel-trace-pipeline"
  processor:
    - otel_traces:
  sink:
    - opensearch:
        hosts: ["https://search-domain-endpoint.us-east-1.es.amazonaws.com"]
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
        hosts: ["https://search-domain-endpoint.us-east-1.es.amazonaws.com"]
        index_type: trace-analytics-service-map
        aws:
          # IAM role that the pipeline assumes to access the domain sink   
          sts_role_arn: "arn:aws:iam::{account-id}:role/pipeline-role"
          region: "us-east-1"
```

For another example pipeline, see the **Trace Analytics pipeline** blueprint\. For more information, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.