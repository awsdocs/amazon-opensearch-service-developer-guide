# Deriving metrics from traces with Amazon OpenSearch Ingestion<a name="use-cases-trace-metrics"></a>

You can use Amazon OpenSearch Ingestion to derive metrics from OpenTelemetry traces\. The following example pipeline receives incoming traces and extracts a metric called `durationInNanos`, aggregated over a tumbling window of 30 seconds\. It then derives a histogram from the incoming traces\.

The pipeline contains the following sub\-pipelines:

- `entry-pipeline` – Receives trace data from the OpenTelemetry collector and forwards it to the `trace_to_metrics_pipeline` sub\-pipeline\.
- `trace-to-metrics-pipeline` – Receives the trace data from the `entry-pipeline` sub\-pipeline, aggregates it, and derives a histogram of `durationInNanos` from the traces based on the value of the `serviceName` field\. It then sends the derived metrics to the OpenSearch index called `metrics_for_traces`\.

```
version: "2"
entry-pipeline:
  source:
    otel_trace_source:
      # Provide the path for ingestion. ${pipelineName} will be replaced with sub-pipeline name.
      # In this case it would be "/entry-pipeline/ingest". This will be endpoint URI path in OpenTelemetry Exporter configuration.
      path: "/${pipelineName}/ingest"
  sink:
    - pipeline:
        name: "trace-to-metrics-pipeline"

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
            # Pick the appropriate values for each of the following fields
            key: "durationInNanos"
            record_minmax: true
            units: "seconds"
            buckets: [0, 10000000, 50000000, 100000000]
        # Specify an aggregation period
        group_duration: "30s"
  sink:
    - opensearch:
        ...
        index: "metrics_for_traces"
```

For another example pipeline, see the **Trace to metric anomaly pipeline** blueprint\. For more information about blueprints, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.
