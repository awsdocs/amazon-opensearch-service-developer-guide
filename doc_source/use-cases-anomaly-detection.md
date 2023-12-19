# Anomaly detection with Amazon OpenSearch Ingestion<a name="use-cases-anomaly-detection"></a>

You can use Amazon OpenSearch Ingestion to train models and generate anomalies in near real\-time on timeseries aggregated events\. You can generate anomalies either on events generated within the pipeline, or on events coming directly into the pipeline, like OpenTelemetry metrics\.

You can feed these tumbling window aggregated timeseries events to the [Anomaly detector](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/anomaly-detector/) processor, which trains a model and generate anomalies with a grade score\. Then, write the anomalies to a separate index to create document monitors and trigger fast alerting\.

In addition to these examples, you can also use the **Log to metric anomaly pipeline** and **Trace to metric anomaly pipeline** blueprints\. For more information about blueprints, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.

**Topics**

- [Metrics from logs](#use-cases-anomaly-detection-metrics-logs)
- [Metrics from traces](#use-cases-anomaly-detection-metrics-traces)
- [OpenTelemetry metrics](#use-cases-anomaly-detection-otel)

## Metrics from logs<a name="use-cases-anomaly-detection-metrics-logs"></a>

The following pipeline receives logs via an HTTP source like FluentBit, extracts important values from the logs by matching the value in the `log` key against the grok common Apache log pattern, and then forwards the grokked logs to both the `log-to-metrics-pipeline` sub\-pipeline, as well as to an OpenSearch index named `logs`\.

The `log-to-metrics-pipeline` sub\-pipeline receives the grokked logs from the `apache-log-pipeline-with-metrics` sub\-pipeline, aggregates them, and derives histogram metrics based on the values in the `clientip` and `request` keys\. It then sends the histogram metrics to an OpenSearch index named `histogram_metrics`, as well as to the `log-to-metrics-anomaly-detector` sub\-pipeline\.

The `log-to-metrics-anomaly-detector-pipeline` sub\-pipeline receives the aggregated histogram metrics from the `log-to-metrics-pipeline` sub\-pipeline and sends them to the Anomaly detector processor to detect anomalies using the Random Cut Forest algorithm\. If it detects anomalies, it sends them to an OpenSearch index named `log-metric-anomalies`\.

```
version: "2"
apache-log-pipeline-with-metrics:
  source:
    http:
      # Provide the path for ingestion. ${pipelineName} will be replaced with pipeline name configured for this pipeline.
      # In this case it would be "/apache-log-pipeline-with-metrics/logs". This will be the FluentBit output URI value.
      path: "/${pipelineName}/logs"
  processor:
    - grok:
        match:
          log: [ "%{COMMONAPACHELOG_DATATYPED}" ]
  sink:
    - opensearch:
        ...
        index: "logs"
    - pipeline:
        name: "log-to-metrics-pipeline"

log-to-metrics-pipeline:
  source:
    pipeline:
      name: "apache-log-pipeline-with-metrics"
  processor:
    - aggregate:
        # Specify the required identification keys
        identification_keys: ["clientip", "request"]
        action:
          histogram:
            # Specify the appropriate values for each the following fields
            key: "bytes"
            record_minmax: true
            units: "bytes"
            buckets: [0, 25000000, 50000000, 75000000, 100000000]
        # Pick the required aggregation period
        group_duration: "30s"
  sink:
    - opensearch:
        ...
        index: "histogram_metrics"
    - pipeline:
        name: "log-to-metrics-anomaly-detector-pipeline"

log-to-metrics-anomaly-detector-pipeline:
  source:
    pipeline:
      name: "log-to-metrics-pipeline"
  processor:
    - anomaly_detector:
        # Specify the key on which to run anomaly detection
        keys: [ "bytes" ]
        mode:
          random_cut_forest:
  sink:
    - opensearch:
        ...
        index: "log-metric-anomalies"
```

## Metrics from traces<a name="use-cases-anomaly-detection-metrics-traces"></a>

You can derive metrics from traces and find anomalies in these generated metrics\. In this example, the `entry-pipeline` sub\-pipeline receives trace data from the OpenTelemetry Collector and forwards it to the following sub\-pipelines:

- `span-pipeline` – Extracts the raw spans from the traces\. It sends the raw spans to any indexes OpenSearch prefixed with `otel-v1-apm-span`\.
- `service-map-pipeline` – Aggregates and analyzes it to create documents that represent connections between services\. It sends these documents to an OpenSearch index named `otel-v1-apm-service-map`\. You can then see a visualization of the service map through the Trace Analytics plugin for OpenSearch Dashboards\.
- `trace-to-metrics-pipeline` \-–Aggregates and derives histogram metrics from the traces based on the value of the `serviceName`\. It then sends the derived metrics to an OpenSearch index named `metrics_for_traces`, as well as to the `trace-to-metrics-anomaly-detector-pipeline` sub\-pipeline\.

The `trace-to-metrics-anomaly-detector-pipeline` sub\-pipeline receives the aggregated histogram metrics from the `trace-to-metrics-pipeline` and sends them to the Anomaly detector processor to detect anomalies using the Random Cut Forest algorithm\. If it detects any anomalies, it sends them to an OpenSearch index named `trace-metric-anomalies`\.

```
version: "2"
entry-pipeline:
  source:
    otel_trace_source:
      # Provide the path for ingestion. ${pipelineName} will be replaced with pipeline name configured for this pipeline.
      # In this case it would be "/entry-pipeline/ingest". This will be endpoint URI path in OpenTelemetry Exporter
      # configuration.
      # path: "/${pipelineName}/ingest"
  processor:
    - trace_peer_forwarder:
  sink:
    - pipeline:
        name: "span-pipeline"
    - pipeline:
        name: "service-map-pipeline"
    - pipeline:
        name: "trace-to-metrics-pipeline"

span-pipeline:
  source:
    pipeline:
      name: "entry-pipeline"
  processor:
    - otel_trace_raw:
  sink:
    - opensearch:
        ...
        index_type: "trace-analytics-raw"

service-map-pipeline:
  source:
    pipeline:
      name: "entry-pipeline"
  processor:
    - service_map:
  sink:
    - opensearch:
        ...
        index_type: "trace-analytics-service-map"

trace-to-metrics-pipeline:
  source:
    pipeline:
      name: "entry-pipeline"
  processor:
    - aggregate:
        # Pick the required identification keys
        identification_keys: ["serviceName"]
        action:
          histogram:
            # Pick the appropriate values for each the following fields
            key: "durationInNanos"
            record_minmax: true
            units: "seconds"
            buckets: [0, 10000000, 50000000, 100000000]
        # Pick the required aggregation period
        group_duration: "30s"
  sink:
    - opensearch:
        ...
        index: "metrics_for_traces"
    - pipeline:
        name: "trace-to-metrics-anomaly-detector-pipeline"

trace-to-metrics-anomaly-detector-pipeline:
  source:
    pipeline:
      name: "trace-to-metrics-pipeline"
  processor:
    - anomaly_detector:
        # Below Key will find anomalies in the max value of histogram generated for durationInNanos.
        keys: [ "max" ]
        mode:
          random_cut_forest:
  sink:
    - opensearch:
        ...
        index: "trace-metric-anomalies"
```

## OpenTelemetry metrics<a name="use-cases-anomaly-detection-otel"></a>

You can create a pipeline that receives OpenTelemetry metrics and detects anomalies in these metrics\. In this example, `entry-pipeline` receives metrics data from the OpenTelemetry Collector\. If a metric is of type `GAUGE` and the name of the metric is `totalApiBytesSent`, the processor sends it to the `ad-pipeline` sub\-pipeline\.

The `ad-pipeline` sub\-pipeline receives the metrics data from the entry pipeline and performs anomaly detection on the value of the metric using the [Anomaly detector](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/anomaly-detector/) processor\.

```
entry-pipeline:
  source:
    otel_metrics_source:
  processor:
    - otel_metrics:
  route:
    - gauge_route: '/kind = "GAUGE" and /name = "totalApiBytesSent"'
  sink:
    - pipeline:
        name: "ad-pipeline"
        routes:
          - gauge_route
    - opensearch:
        ...
        index: "otel-metrics"

ad-pipeline:
  source:
    pipeline:
      name: "entry-pipeline"
    processor:
      - anomaly_detector:
        # Use "value" as the key on which anomaly detector needs to be run
        keys: [ "value" ]
        mode:
          random_cut_forest:
    sink:
      - opensearch:
        ...
        index: otel-metrics-anomalies
```

In addition to this example, you can also use the **Trace to metric anomaly pipeline** blueprint\. For more information about blueprints, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.
