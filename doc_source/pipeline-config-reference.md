# Supported plugins and options for Amazon OpenSearch Ingestion pipelines<a name="pipeline-config-reference"></a>

Amazon OpenSearch Ingestion supports a subset of sources, processors, and sinks compared to open source Data Prepper\. In addition, there are some constraints that OpenSearch Ingestion places on the available options for each supported plugin\. The following sections describe the plugins and associated options that OpenSearch Ingestion supports\.

**Note**  
OpenSearch Ingestion doesn't support any buffer plugins because it automatically configures a default buffer\. You receive a validation error if you include a buffer in your pipeline configuration\.

**Topics**
+ [Supported plugins](#ingestion-plugins)
+ [Stateless versus stateful processors](#processor-stateful-stateless)
+ [Configuration requirements and constraints](#ingestion-parameters)

## Supported plugins<a name="ingestion-plugins"></a>

OpenSearch Ingestion supports the following Data Prepper plugins:

**Sources**:
+ [HTTP](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/http-source/)
+ [OTel logs](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-logs-source/)
+ [OTel metrics](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-metrics-source/)
+ [OTel trace](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-trace/)
+ [S3](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/s3/)

**Processors**:
+ [Aggregate](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/aggregate/)
+ [Anomaly detector](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/anomaly-detector/)
+ [CSV](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/csv/)
+ [Date](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/date/)
+ [Drop events](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/drop-events/)
+ [Grok](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/grok/)
+ [Key value](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/key-value/)
+ [Mutate event](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/mutate-event/)
+ [Mutate string](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/mutate-string/)
+ [OTel metrics](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/otel-metrics/)
+ [OTel trace group](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/otel-trace-group/)
+ [OTel trace](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/otel-trace-raw/)
+ [Parse JSON](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/parse-json/)
+ [Service\-map](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/service-map-stateful/)
+ [Trace peer forwarder](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/trace-peer-forwarder/)

**Sinks**:
+ [OpenSearch](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sinks/opensearch/) \(supports OpenSearch Service and OpenSearch Serverless\)

## Stateless versus stateful processors<a name="processor-stateful-stateless"></a>

*Stateless* processors perform operations like transformations and filtering, while *stateful* processors perform operations like aggregations that remember the result of the previous execution\. OpenSearch Ingestion supports the stateful processors [Aggregate](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/aggregate/) and [Service\-map](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/service-map-stateful/)\. All other supported processors are stateless\.

For pipelines that contain stateless processors, the maximum capacity limit is 96 Ingestion OCUs\. For stateful processors, the maximum capacity limit is 48 Ingestion OCUs\. For more information, see [Scaling pipelines](ingestion.md#ingestion-scaling)\.

End\-to\-end acknowledgment is only supported for stateless processors\. For more information, see [End\-to\-end acknowledgement](osis-features-overview.md#osis-features-e2e)\.

## Configuration requirements and constraints<a name="ingestion-parameters"></a>

Unless otherwise specified below, all options described in the Data Prepper configuration reference for the supported plugins listed above are allowed in OpenSearch Ingestion pipelines\. The following sections explain the constraints that OpenSearch Ingestion places on certain plugin options\.

**Note**  
OpenSearch Ingestion doesn't support any buffer plugins because it automatically configures a default buffer\. You receive a validation error if you include a buffer in your pipeline configuration\.

Many options are configured and managed internally by OpenSearch Ingestion, such as `authentication` and `acm_certificate_arn`\. Other options, such as `thread_count` and `request_timeout`, have performance impacts if changed manually\. Therefore, these values are set internally to ensure optimal performance of your pipelines\.

Lastly, some options can't be passed to OpenSearch Ingestion, such as `ism_policy_file` and `sink_template`, because they're local files when run in open source Data Prepper\. These values aren't supported\.

**Topics**
+ [General pipeline options](#ingestion-params-general)
+ [Grok processor](#ingestion-params-grok)
+ [HTTP source](#ingestion-params-http)
+ [OpenSearch sink](#ingestion-params-opensearch)
+ [OTel metrics source, OTel trace source, and OTel logs source](#ingestion-params-otel-source)
+ [OTel trace group processor](#ingestion-params-otel-trace)
+ [OTel trace processor](#ingestion-params-otel-raw)
+ [Service\-map processor](#ingestion-params-servicemap)
+ [S3 source](#ingestion-params-s3)

### General pipeline options<a name="ingestion-params-general"></a>

The following [general pipeline options](https://opensearch.org/docs/latest/data-prepper/pipelines/pipelines-configuration-options/) are set by OpenSearch Ingestion and aren't supported in pipeline configurations:
+ `workers`
+ `delay`

### Grok processor<a name="ingestion-params-grok"></a>

The following [Grok](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/grok/) processor options aren't supported:
+ `patterns_directories`
+ `patterns_files_glob`

### HTTP source<a name="ingestion-params-http"></a>

The [HTTP](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/http-source/) source plugin has the following requirements and constraints:
+ The `path` option is *required*\. The path is a string such as `/log/ingest`, which represents the URI path for log ingestion\. This path defines the URI that you use to send data to the pipeline\. For example, `https://log-pipeline.us-west-2.osis.amazonaws.com/log/ingest`\. The path must start with a slash \(/\), and can contain the special characters '\-', '\_', '\.', and '/', as well as the `${pipelineName}` placeholder\.
+ The following HTTP source options are set by OpenSearch Ingestion and aren't supported in pipeline configurations:
  + `port`
  + `ssl`
  + `ssl_key_file`
  + `ssl_certificate_file`
  + `aws_region`
  + `authentication`
  + `unauthenticated_health_check`
  + `use_acm_certificate_for_ssl`
  + `thread_count`
  + `request_timeout`
  + `max_connection_count`
  + `max_pending_requests`
  + `health_check_service`
  + `acm_private_key_password`
  + `acm_certificate_timeout_millis`
  + `acm_certificate_arn`

### OpenSearch sink<a name="ingestion-params-opensearch"></a>

The [OpenSearch](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sinks/opensearch/) sink plugin has the following requirements and limitations\.
+ The `aws` option is *required*, and must contain the following options:
  + `sts_role_arn`
  + `region`
  + `hosts`
  + `serverless` \(if the sink is an OpenSearch Serverless collection\)
+ The `sts_role_arn` option must point to the same role for each sink within a YAML definition file\.
+ The `hosts` option must specify an OpenSearch Service domain endpoint or an OpenSearch Serverless collection endpoint\. All hosts within a YAML definition file must point to the same endpoint\. You can't specify a [custom endpoint](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/customendpoint.html) for a domain; it must be the standard endpoint\.
+ If the `hosts` option is a serverless collection endpoint, you must set the `serverless` option to `true`\. In addition, if your YAML definition file contains the `index_type` option, it must be set to `management_disabled`, otherwise validation fails\.
+ The following options aren't supported:
  + `username`
  + `password`
  + `cert`
  + `proxy`
  + `dlq_file` \- If you want to offload failed events to a dead letter queue \(DLQ\), you must use the `dlq` option and specify an S3 bucket\.
  + `ism_policy_file`
  + `socket_timeout`
  + `template_file`
  + `insecure`
  + `bulk_size`

### OTel metrics source, OTel trace source, and OTel logs source<a name="ingestion-params-otel-source"></a>

The [OTel metrics](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-metrics-source/) source, [OTel trace](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-trace/) source, and [OTel logs](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-logs-source/) source plugins have the following requirements and limitations:
+ The `path` option is *required*\. The path is a string such as `/log/ingest`, which represents the URI path for log ingestion\. This path defines the URI that you use to send data to the pipeline\. For example, `https://log-pipeline.us-west-2.osis.amazonaws.com/log/ingest`\. The path must start with a slash \(/\), and can contain the special characters '\-', '\_', '\.', and '/', as well as the `${pipelineName}` placeholder\.
+ The following options are set by OpenSearch Ingestion and aren't supported in pipeline configurations:
  + `port`
  + `ssl`
  + `sslKeyFile`
  + `sslKeyCertChainFile`
  + `authentication`
  + `unauthenticated_health_check`
  + `useAcmCertForSSL`
  + `unframed_requests`
  + `proto_reflection_service`
  + `thread_count`
  + `request_timeout`
  + `max_connection_count`
  + `acmPrivateKeyPassword`
  + `acmCertIssueTimeOutMillis`
  + `health_check_service`
  + `acmCertificateArn`
  + `awsRegion`

### OTel trace group processor<a name="ingestion-params-otel-trace"></a>

The [OTel trace group](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/otel-trace-group/) processor has the following requirements and limitations:
+ The `aws` option is *required*, and must contain the following options:
  + `sts_role_arn`
  + `region`
  + `hosts`
+ The `sts_role_arn` option specify the same role as the pipeline role that you specify in the OpenSearch sink configuration\.
+ The `username`, `password`, `cert`, and `insecure` options aren't supported\.
+ The `aws_sigv4` option is required and must be set to true\.
+ The `serverless` option within the OpenSearch sink plugin isn't supported\. The Otel trace group processor doesn't currently work with OpenSearch Serverless collections\.
+ The number of `otel_trace_group` processors within the pipeline configuration body can't exceed 8\.

### OTel trace processor<a name="ingestion-params-otel-raw"></a>

The [OTel trace](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/otel-trace-raw/) processor has the following requirements and limitations:
+ The value of the `trace_flush_interval` option can't exceed 300 seconds\.

### Service\-map processor<a name="ingestion-params-servicemap"></a>

The [Service\-map](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/service-map-stateful/) processor has the following requirements and limitations:
+ The value of the `window_duration` option can't exceed 300 seconds\.

### S3 source<a name="ingestion-params-s3"></a>

The [S3](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/s3/) source plugin has the following requirements and limitations:
+ The `aws` option is *required*, and must contain `region` and `sts_role_arn` options\.
+ The value of the `records_to_accumulate` option can't exceed 200\.
+ The value of the `maximum_messages` option can't exceed 10\.
+ If specified, the `disable_bucket_ownership_validation` option must be set to false\.
+ If specified, the `input_serialization` option must be set to `parquet`\.