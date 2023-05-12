# Deriving metrics from logs with Amazon OpenSearch Ingestion<a name="use-cases-metrics-logs"></a>

You can use Amazon OpenSearch Ingestion to derive metrics from logs\. The following example pipeline receives incoming logs using the [HTTP source](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/http-source/) plugin and the [Grok processor](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/grok/)\. It then uses the [Aggregate processor](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/aggregate/) to extract the metric `bytes` aggregated over a 30\-second window and derives histograms from the results\.

The overall pipeline contains two sub\-pipelines:
+ `apache-log-pipeline-with-metrics` – Receives logs via an HTTP client like FluentBit, extracts important values from the logs by matching the value in the `log` key against the grok common Apache log pattern, and then forwards the grokked logs to both the `log-to-metrics-pipeline` sub\-pipeline and to an OpenSearch index named `logs`\.
+ `log-to-metrics-pipeline` – Receives the grokked logs from the `apache-log-pipeline-with-metrics` sub\-pipeline, aggregates the logs and derives histogram metrics of `bytes` based on the values in the `clientip` and `request` keys\. Finally, it sends the histogram metrics to an OpenSearch index named `histogram_metrics`\.

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
            # Specify the appropriate values for each of the following fields
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
```

In addition to this example, you can also use the **Log to metric pipeline** blueprint\. For more information about blueprints, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.