# Monitoring pipeline metrics<a name="monitoring-pipeline-metrics"></a>

You can monitor Amazon OpenSearch Ingestion pipelines using Amazon CloudWatch, which collects raw data and processes it into readable, near real\-time metrics\. These statistics are kept for 15 months, so that you can access historical information and gain a better perspective on how your web application or service is performing\. You can also set alarms that watch for certain thresholds, and send notifications or take actions when those thresholds are met\. For more information, see the [Amazon CloudWatch User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/)\.

The OpenSearch Ingestion console displays a series of charts based on the raw data from CloudWatch on the **Performance** tab for each pipeline\.

OpenSearch Ingestion reports metrics from most [supported plugins](pipeline-config-reference.md#ingestion-plugins)\. If certain plugins don't have their own table below, it means that they don't report any plugin\-specific metrics\. Pipeline metrics are published in the `AWS/OSIS` namespace\.

**Topics**
+ [Common metrics](#common-metrics)
+ [Buffer metrics](#buffer-metrics)
+ [Signature V4 metrics](#sigv4-metrics)
+ [Bounded blocking buffer metrics](#blockingbuffer-metrics)
+ [Otel trace source metrics](#oteltrace-metrics)
+ [Otel metrics source metrics](#otelmetrics-metrics)
+ [Http metrics](#http-metrics)
+ [S3 metrics](#s3-metrics)
+ [Aggregate metrics](#aggregate-metrics)
+ [Date metrics](#date-metrics)
+ [Grok metrics](#grok-metrics)
+ [Otel trace raw metrics](#oteltrace-raw-metrics)
+ [Otel trace group metrics](#oteltracegroup-metrics)
+ [Service map stateful metrics](#servicemapstateful-metrics)
+ [OpenSearch metrics](#opensearch-metrics)
+ [System and metering metrics](#systemmetering-metrics)

## Common metrics<a name="common-metrics"></a>

The following metrics are common to all processors and sinks\.

Each metric is prefixed by the sub\-pipeline name and plugin name, in the format <*sub\_pipeline\_name*><*plugin*><*metric\_name*>\. For example, the full name of the `recordsIn.count` metric for a sub\-pipeline named `my-pipeline` and the [date](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/date/) processor would be `my-pipeline.date.recordsIn.count`\.


| Metric suffix | Description | 
| --- | --- | 
| recordsIn\.count |  The ingress of records to a pipeline component\. This metric applies to processors and sinks\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| recordsOut\.count |  The egress of records from a pipeline component\. This metric applies to processors and sources\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| timeElapsed\.count |  A count of data points recorded during execution of a pipeline component\. This metric applies to processors and sinks\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| timeElapsed\.sum |  The total time elapsed during execution of a pipeline component\. This metric applies to processors and sinks, in milliseconds\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| timeElapsed\.max |  The maximum time elapsed during execution of a pipeline component\. This metric applies to processors and sinks, in milliseconds\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 

## Buffer metrics<a name="buffer-metrics"></a>

The following metrics apply to the default [Bounded blocking](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/buffers/bounded-blocking/) buffer that OpenSearch Ingestion automatically configures for all pipelines\.

Each metric is prefixed by the sub\-pipeline name and buffer name, in the format <*sub\_pipeline\_name*><*buffer\_name*><*metric\_name*>\. For example, the full name of the `recordsWritten.count` metric for a sub\-pipeline named `my-pipeline` would be `my-pipeline.BlockingBuffer.recordsWritten.count`\.


| Metric suffix | Description | 
| --- | --- | 
| recordsWritten\.count |  The number of records written to a buffer\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| recordsRead\.count |  The number of records read from a buffer\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| recordsInFlight\.value |  The number of unchecked records read from a buffer\. **Relevant statistics**: Average **Dimension**: `PipelineName`  | 
| recordsInBuffer\.value |  The number of records currently in a buffer\. **Relevant statistics**: Average **Dimension**: `PipelineName`  | 
| recordsProcessed\.count |  The number of records read from a buffer and processed by a pipeline\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| recordsWriteFailed\.count |  The number of records that the pipeline failed to write to the sink\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| writeTimeElapsed\.count |  A count of data points recorded while writing to a buffer\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| writeTimeElapsed\.sum |  The total time elapsed while writing to a buffer, in milliseconds\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| writeTimeElapsed\.max |  The maximum time elapsed while writing to a buffer, in milliseconds\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 
| writeTimeouts\.count |  The count of write timeouts to a buffer\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| readTimeElapsed\.count |  A count of data points recorded while reading from a buffer\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| readTimeElapsed\.sum |  The total time elapsed while reading from a buffer, in milliseconds\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| readTimeElapsed\.max |  The maximum time elapsed while reading from a buffer, in milliseconds\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 
| checkpointTimeElapsed\.count |  A count of data points recorded while checkpointing\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| checkpointTimeElapsed\.sum |  The total time elapsed while checkpointing, in milliseconds\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| checkpointTimeElapsed\.max |  The maximum time elapsed while checkpointing, in milliseconds\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 

## Signature V4 metrics<a name="sigv4-metrics"></a>

The following metrics apply to the ingestion endpoint for a pipeline and are associate with the source plugins \(`http`, `otel_trace`, and `otel_metrics`\)\. All requests to the ingestion endpoint must be signed using [Signature Version 4](https://docs.aws.amazon.com/general/latest/gr/signature-version-4.html)\. These metrics can help you identify authorization issues when connecting to your pipeline, or confirm that you're successfully authenticating\.

Each metric is prefixed by the sub\-pipeline name and `osis_sigv4_auth`\. For example, `sub_pipeline_name.osis_sigv4_auth.httpAuthSuccess.count`\.


| Metric suffix | Description | 
| --- | --- | 
| httpAuthSuccess\.count |  The number of successful Signature V4 requests to the pipeline\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| httpAuthFailure\.count |  The number of failed Signature V4 requests to the pipeline\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| httpAuthServerError\.count |  The number of Signature V4 requests to the pipeline that returned server errors\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 

## Bounded blocking buffer metrics<a name="blockingbuffer-metrics"></a>

The following metrics apply to the [bounded blocking](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/buffers/bounded-blocking/) buffer\. Each metric is prefixed by the sub\-pipeline name and `BlockingBuffer`\. For example, `sub_pipeline_name.BlockingBuffer.bufferUsage.value`\.


| Metric suffix | Description | 
| --- | --- | 
| bufferUsage\.value |  Percent usage of the `buffer_size` based on the number of records in the buffer\. `buffer_size` represents the maximum number of records written into the buffer as well as in\-flight records that have not been checked\. **Relevant statistics**: Average **Dimension**: `PipelineName`  | 

## Otel trace source metrics<a name="oteltrace-metrics"></a>

The following metrics apply to the [OTel trace](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-trace/) source\. Each metric is prefixed by the sub\-pipeline name and `otel_trace_source`\. For example, `sub_pipeline_name.otel_trace_source.requestTimeouts.count`\.


| Metric suffix | Description | 
| --- | --- | 
| requestTimeouts\.count |  The number of requests that timed out\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| requestsReceived\.count |  The number of requests received by the plugin\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| successRequests\.count |  The number of requests that were successfully processed by the plugin\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| badRequests\.count |  The number of requests with an invalid format that were processed by the plugin\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| requestsTooLarge\.count |  The number of requests of which the number of spans in the content is larger than the buffer capacity\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| internalServerError\.count |  The number of requests processed by the plugin with a custom exception type\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| requestProcessDuration\.count |  A count of data points recorded while processing requests by the plugin\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| requestProcessDuration\.sum |  The total latency of requests processed by the plugin, in milliseconds\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| requestProcessDuration\.max |  The maximum latency of requests processed by the plugin, in milliseconds\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 
| payloadSize\.count |  A count of the distribution of payload sizes of incoming requests, in bytes\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| payloadSize\.sum |  The total distribution of the payload sizes of incoming requests, in bytes\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| payloadSize\.max |  The maximum distribution of payload sizes of incoming requests, in bytes\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 

## Otel metrics source metrics<a name="otelmetrics-metrics"></a>

The following metrics apply to the [OTel metrics](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-metrics-source/) source\. Each metric is prefixed by the sub\-pipeline name and `otel_metrics_source`\. For example, `sub_pipeline_name.otel_metrics_source.requestTimeouts.count`\.


| Metric suffix | Description | 
| --- | --- | 
| requestTimeouts\.count |  The total number of requests to the plugin that time out\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| requestsReceived\.count |  The total number of requests received by the plugin\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| successRequests\.count |  The number of requests successfully processed \(200 response status code\) by the plugin\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| requestProcessDuration\.count |  A count of the latency of requests processed by the plugin, in seconds\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| requestProcessDuration\.sum |  The total latency of requests processed by the plugin, in milliseconds\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| requestProcessDuration\.max |  The maximum latency of requests processed by the plugin, in milliseconds\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 
| payloadSize\.count |  A count of the distribution of payload sizes of incoming requests, in bytes\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| payloadSize\.sum |  The total distribution of the payload sizes of incoming requests, in bytes\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| payloadSize\.max |  The maximum distribution of payload sizes of incoming requests, in bytes\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 

## Http metrics<a name="http-metrics"></a>

The following metrics apply to the [HTTP](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/http-source/) source\. Each metric is prefixed by the sub\-pipeline name and `http`\. For example, `sub_pipeline_name.http.requestsReceived.count`\.


| Metric suffix | Description | 
| --- | --- | 
| requestsReceived\.count |  The number of requests received by the `/log/ingest` endpoint\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| requestsRejected\.count |  The number of requests rejected \(429 response status code\) by the plugin\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| successRequests\.count |  The number of requests successfully processed \(200 response status code\) by the plugin\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| badRequests\.count |  The number of requests with invalid content type or format \(400 response status code\) processed by the plugin\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| requestTimeouts\.count |  The number of requests that time out in the HTTP source server \(415 response status code\)\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| requestsTooLarge\.count |  The number of requests of which the events size in the content is larger than the buffer capacity \(413 response status code\)\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| internalServerError\.count |  The number of requests processed by the plugin with a custom exception type \(500 response status code\)\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| requestProcessDuration\.count |  A count of the latency of requests processed by the plugin, in seconds\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| requestProcessDuration\.sum |  The total latency of requests processed by the plugin, in milliseconds\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| requestProcessDuration\.max |  The maximum latency of requests processed by the plugin, in milliseconds\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 
| payloadSize\.count |  A count of the distribution of payload sizes of incoming requests, in bytes\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| payloadSize\.sum |  The total distribution of the payload sizes of incoming requests, in bytes\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| payloadSize\.max |  The maximum distribution of payload sizes of incoming requests, in bytes\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 

## S3 metrics<a name="s3-metrics"></a>

The following metrics apply to the [S3](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/s3/) source\. Each metric is prefixed by the sub\-pipeline name and `s3`\. For example, `sub_pipeline_name.s3.s3ObjectsFailed.count`\.


| Metric suffix | Description | 
| --- | --- | 
| s3ObjectsFailed\.count |  The total number of S3 objects that the plugin failed to read\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3ObjectsNotFound\.count |  The number of S3 objects that the plugin failed to read due to a `Not Found` error from S3\. These metrics also count toward the `s3ObjectsFailed` metric\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3ObjectsAccessDenied\.count |  The number of S3 objects that the plugin failed to read due to an `Access Denied` or `Forbidden` error from S3\. These metrics also count toward the `s3ObjectsFailed` metric\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3ObjectReadTimeElapsed\.count |  The amount of time the plugin takes to perform a GET request for an S3 object, parse it, and write events to the buffer\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3ObjectReadTimeElapsed\.sum |  The total amount of time that the plugin takes to perform a GET request for an S3 object, parse it, and write events to the buffer, in milliseconds\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3ObjectReadTimeElapsed\.max |  The maximum amount of time that the plugin takes to perform a GET request for an S3 object, parse it, and write events to the buffer, in milliseconds\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 
| s3ObjectSizeBytes\.count |  The count of the distribution of S3 object sizes, in bytes\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3ObjectSizeBytes\.sum |  The total distribution of S3 object sizes, in bytes\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3ObjectSizeBytes\.max |  The maximum distribution of S3 object sizes, in bytes\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 
| s3ObjectProcessedBytes\.count |  The count of the distribution of S3 objects processed by the plugin, in bytes\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3ObjectProcessedBytes\.sum |  The total distribution of S3 objects processed by the plugin, in bytes\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3ObjectProcessedBytes\.max |  The maximum distribution of S3 objects processed by the plugin, in bytes\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 
| s3ObjectsEvents\.count |  The count of the distribution of S3 events received by the plugin\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3ObjectsEvents\.sum |  The total distribution of S3 events received by the plugin\.  **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3ObjectsEvents\.max |  The maximum distribution of S3 events received by the plugin\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 
| sqsMessageDelay\.count |  A count of data points recorded while S3 records an event time for the creation of an object to when it's fully parsed\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| sqsMessageDelay\.sum |  The total amount of time between when S3 records an event time for the creation of an object to when it's fully parsed, in milliseconds\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| sqsMessageDelay\.max |  The maximum amount of time between when S3 records an event time for the creation of an object to when it's fully parsed, in milliseconds\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 
| s3ObjectsSucceeded\.count |  The number of S3 objects that the plugin successfully read\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| sqsMessagesReceived\.count |  The number of Amazon SQS messages received from the queue by the plugin\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| sqsMessagesDeleted\.count |  The number of Amazon SQS messages deleted from the queue by the plugin\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| sqsMessagesFailed\.count |  The number of Amazon SQS messages that the plugin failed to parse\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 

## Aggregate metrics<a name="aggregate-metrics"></a>

The following metrics apply to the [Aggregate](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/aggregate/) processor\. Each metric is prefixed by the sub\-pipeline name and `aggregate`\. For example, `sub_pipeline_name.aggregate.actionHandleEventsOut.count`\.


| Metric suffix | Description | 
| --- | --- | 
| actionHandleEventsOut\.count |  The number of events that have been returned from the `handleEvent` call to the configured action\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| actionHandleEventsDropped\.count |  The number of events that have been returned from the `handleEvent` call to the configured action\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| actionHandleEventsProcessingErrors\.count |  The number of calls made to `handleEvent` for the configured action that resulted in an error\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| actionConcludeGroupEventsOut\.count |  The number of events that have been returned from the `concludeGroup` call to the configured action\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| actionConcludeGroupEventsDropped\.count |  The number of events that have not been returned from the `condludeGroup` call to the configured action\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| actionConcludeGroupEventsProcessingErrors\.count |  The number of calls made to `concludeGroup` for the configured action that resulted in an error\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| currentAggregateGroups\.value |  The current number of groups\. This gauge decreases when groups are concluded, and increases when an event initiates the creation of a new group\. **Relevant statistics**: Average **Dimension**: `PipelineName`  | 

## Date metrics<a name="date-metrics"></a>

The following metrics apply to the [Date](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/date/) processor\. Each metric is prefixed by the sub\-pipeline name and `date`\. For example, `sub_pipeline_name.date.dateProcessingMatchSuccess.count`\.


| Metric suffix | Description | 
| --- | --- | 
| dateProcessingMatchSuccess\.count |  The number of records that match at least one of the patterns specified in the `match` configuration option\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| dateProcessingMatchFailure\.count |  The number of records that didn't match any of the patterns specified in the `match` configuration option\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 

## Grok metrics<a name="grok-metrics"></a>

The following metrics apply to the [Grok](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/grok/) processor\. Each metric is prefixed by the sub\-pipeline name and `grok`\. For example, `sub_pipeline_name.grok.grokProcessingMatch.count`\.


| Metric suffix | Description | 
| --- | --- | 
| grokProcessingMatch\.count |  The number of records that found at least one pattern match from the `match` configuration option\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| grokProcessingMismatch\.count |  The number of records that didn't match any of the patterns specified in the `match` configuration option\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| grokProcessingErrors\.count |  The number of record processing errors\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| grokProcessingTimeouts\.count |  The number of records that timed out while matching\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| grokProcessingTime\.count |  A count of data points recorded while an individual record matched against patterns from the `match` configuration option\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| grokProcessingTime\.sum |  The total amount of time that each individual record takes to match against patterns from the `match` configuration option, in milliseconds\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| grokProcessingTime\.max |  The maximum amount of time that each individual record takes to match against patterns from the `match` configuration option, in milliseconds\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 

## Otel trace raw metrics<a name="oteltrace-raw-metrics"></a>

The following metrics apply to the [OTel trace raw](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/otel-trace-raw/) processor\. Each metric is prefixed by the sub\-pipeline name and `otel_trace_raw`\. For example, `sub_pipeline_name.otel_trace_raw.traceGroupCacheCount.value`\.


| Metric suffix | Description | 
| --- | --- | 
| traceGroupCacheCount\.value |  The number of trace groups in the trace group cache\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| spanSetCount\.value |  The number of span sets in the span set collection\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 

## Otel trace group metrics<a name="oteltracegroup-metrics"></a>

The following metrics apply to the [OTel trace group](https://github.com/opensearch-project/data-prepper/tree/main/data-prepper-plugins/otel-trace-group-processor) processor\. Each metric is prefixed by the sub\-pipeline name and `otel_trace_group`\. For example, `sub_pipeline_name.otel_trace_group.recordsInMissingTraceGroup.count`\.


| Metric suffix | Description | 
| --- | --- | 
| recordsInMissingTraceGroup\.count |  The number of ingress records missing trace group fields\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| recordsOutFixedTraceGroup\.count |  The number of egress records with trace group fields that were filled successfully\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| recordsOutMissingTraceGroup\.count |  The number of egress records missing trace group fields\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 

## Service map stateful metrics<a name="servicemapstateful-metrics"></a>

The following metrics apply to the [Service\-map stateful](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/service-map-stateful/) processor\. Each metric is prefixed by the sub\-pipeline name and `service-map-stateful`\. For example, `sub_pipeline_name.service-map-stateful.spansDbSize.count`\.


| Metric suffix | Description | 
| --- | --- | 
| spansDbSize\.value |  The in\-memory byte sizes of spans in MapDB across the current and previous window durations\. **Relevant statistics**: Average **Dimension**: `PipelineName`  | 
| traceGroupDbSize\.value |  The in\-memory byte sizes of trace groups in MapDB across the current and previous window durations\. **Relevant statistics**: Average **Dimension**: `PipelineName`  | 
| spansDbCount\.value |  The count of spans in MapDB across the current and previous window durations\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| traceGroupDbCount\.value |  The count of trace groups in MapDB across the current and previous window durations\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| relationshipCount\.value |  The count of relationships stored across the current and previous window durations\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 

## OpenSearch metrics<a name="opensearch-metrics"></a>

The following metrics apply to the [OpenSearch](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sinks/opensearch/) sink\. Each metric is prefixed by the sub\-pipeline name and `opensearch`\. For example, `sub_pipeline_name.opensearch.bulkRequestErrors.count`\.


| Metric suffix | Description | 
| --- | --- | 
| bulkRequestErrors\.count |  The total number of errors encountered while sending bulk requests\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| documentsSuccess\.count |  The number of documents successfully sent to the OpenSearch Service by bulk request, including retries\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| documentsSuccessFirstAttempt\.count |  The number of documents successfully sent to OpenSearch Service by bulk request on the first attempt\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| documentErrors\.count |  The number of documents that failed to be sent by bulk requests\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| bulkRequestFailed\.count |  The number of bulk requests that failed\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| bulkRequestNumberOfRetries\.count |  The number of retries of failed bulk requests\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| bulkBadRequestErrors\.count |  The number of `Bad Request` errors encountered while sending bulk requests\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| bulkRequestNotAllowedErrors\.count |  The number of `Request Not Allowed` errors encountered while sending bulk requests\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| bulkRequestInvalidInputErrors\.count |  The number of `Invalid Input` errors encountered while sending bulk requests\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| bulkRequestNotFoundErrors\.count |  The number of `Request Not Found` errors encountered while sending bulk requests\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| bulkRequestTimeoutErrors\.count |  The number of `Request Timeout` errors encountered while sending bulk requests\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| bulkRequestServerErrors\.count |  The number of `Server Error` errors encountered while sending bulk requests\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| bulkRequestSizeBytes\.count |  A count of the distribution of payload sizes of bulk requests, in bytes\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| bulkRequestSizeBytes\.sum |  The total distribution of payload sizes of bulk requests, in bytes\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| bulkRequestSizeBytes\.max |  The maximum distribution of payload sizes of bulk requests, in bytes\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 
| bulkRequestLatency\.count |  A count of data points recorded while requests are sent to the plugin, including retries\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| bulkRequestLatency\.sum |  The total latency of requests sent to the plugin, including retries, in milliseconds\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| bulkRequestLatency\.max |  The maximum latency of requests sent to the plugin, including retries, in milliseconds\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 
| s3\.dlqS3RecordsSuccess\.count |  The number of records successfully sent to the S3 dead letter queue\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3\.dlqS3RecordsFailed\.count |  The number of recourds that failed to be sent to the S3 dead letter queue\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3\.dlqS3RequestSuccess\.count |  The number of successful requests to the S3 dead letter queue\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3\.dlqS3RequestFailed\.count |  The number of failed requests to the S3 dead letter queue\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3\.dlqS3RequestLatency\.count |  A count of data points recorded while requests are sent to the S3 dead letter queue, including retries\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3\.dlqS3RequestLatency\.sum |  The total latency of requests sent to the S3 dead letter queue, including retries, in milliseconds\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3\.dlqS3RequestLatency\.max |  The maximum latency of requests sent to the S3 dead letter queue, including retries, in milliseconds\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 
| s3\.dlqS3RequestSizeBytes\.count |  A count of the distribution of payload sizes of requests to the S3 dead letter queue, in bytes\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3\.dlqS3RequestSizeBytes\.sum |  The total distribution of payload sizes of requests to the S3 dead letter queue, in bytes\. **Relevant statistics**: Sum **Dimension**: `PipelineName`  | 
| s3\.dlqS3RequestSizeBytes\.max |  The maximum distribution of payload sizes of requests to the S3 dead letter queue, in bytes\. **Relevant statistics**: Max **Dimension**: `PipelineName`  | 

## System and metering metrics<a name="systemmetering-metrics"></a>

The following metrics apply to the overall OpenSearch Ingestion system\. These metrics aren't prefixed by anything\.


| Metric | Description | 
| --- | --- | 
| system\.cpu\.usage\.value |  The percentage of available CPU usage for all data nodes\. **Relevant statistics**: Average **Dimension**: `PipelineName`, `area`, `id`  | 
| system\.cpu\.count\.value |  The total amount of CPU usage for all data nodes\. **Relevant statistics**: Average **Dimension**: `PipelineName`, `area`, `id`  | 
| jvm\.memory\.max\.value |  The maximum amount of memory that can be used for memory management, in bytes\. **Relevant statistics**: Average **Dimension**: `PipelineName`, `area`, `id`  | 
| jvm\.memory\.used\.value |  The total amount of memory used, in bytes\. **Relevant statistics**: Average **Dimension**: `PipelineName`, `area`, `id`signa  | 
| jvm\.memory\.committed\.value |  The amount of memory that is committed for use by the Java virtual machine \(JVM\), in bytes\. **Relevant statistics**: Average **Dimension**: `PipelineName`, `area`, `id`  | 
| computeUnits |  The number of Ingestion OpenSearch Compute Units \(Ingestion OCUs\) in use by a pipeline\. **Relevant statistics**: Max, Sum, Average **Dimension**: `PipelineName`  | 