# Best practices for Amazon OpenSearch Ingestion<a name="osis-best-practices"></a>

This topic provides best practices for creating and managing Amazon OpenSearch Ingestion pipelines and includes general guidelines that apply to many use cases\. Each workload is unique, with unique characteristics, so no generic recommendation is exactly right for every use case\.

**Topics**
+ [General best practices](#osis-best-practices-general)
+ [Recommended CloudWatch alarms](#osis-cloudwatch-alarms)

## General best practices<a name="osis-best-practices-general"></a>

The following general best practices apply to creating and managing pipelines\.
+ To ensure high availability, configure VPC pipelines with two or three subnets\. If you only deploy a pipeline in one subnet and the Availability Zone goes down, you won't be able to ingest data\.
+ Within each pipeline, we recommend limiting the number of sub\-pipelines to 5 or fewer\.
+ If you're using the S3 source plugin, use evenly\-sized S3 files for optimal performance\.
+ If you're using the S3 source plugin, add 30 seconds of additional visibility timeout for every 0\.25 GB of file size in the S3 bucket for optimal performance\.
+ Include a [dead\-letter queue](https://opensearch.org/docs/latest/data-prepper/pipelines/dlq/) \(DLQ\) in your pipeline configuration so that you can offload failed events and make them accessible for analysis\. If your sinks reject data due to incorrect mappings or other issues, you can route the data to the DLQ in order to troubleshoot and fix the issue\.

## Recommended CloudWatch alarms<a name="osis-cloudwatch-alarms"></a>

CloudWatch alarms perform an action when a CloudWatch metric exceeds a specified value for some amount of time\. For example, you might want AWS to email you if your cluster health status is `red` for longer than one minute\. This section includes some recommended alarms for Amazon OpenSearch Ingestion and how to respond to them\.

For more information about configuring alarms, see [Creating Amazon CloudWatch Alarms](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/AlarmThatSendsEmail.html) in the *Amazon CloudWatch User Guide*\.


| Alarm | Issue | 
| --- | --- | 
|  `computeUnits` maximum is = the configured `maxUnits` for 15 minute, 3 consecutive times  | The pipeline has reached the maximum capacity and might require a maxUnits update\. Increase the maximum capacity of your pipeline | 
|  `opensearch.documentErrors.count` sum is = `{sub_pipeline_name}.opensearch.recordsIn.count` sum for 1 minute, 1 consecutive time  | The pipeline is unable to write to the OpenSearch sink\. Check the pipeline permissions and confirm that the domain or collection is healthy\. You can also check the dead letter queue \(DLQ\) for failed events, if it's configured\. | 
|  `bulkRequestLatency.max` max is >= *x* for 1 minute, 1 consecutive time  | The pipeline is experiencing high latency sending data to the OpenSearch sink\. This is likely due to the sink being undersized, or a poor sharding strategy, which is causing the sink to fall behind\. Sustained high latency can impact pipeline performance and will likely lead to backpressure on the clients\. | 
|  `httpAuthFailure.count` sum >= 1 for 1 minute, 1 consecutive time  | Ingestion requests are not being authenticated\. Confirm that all clients have Signature Version 4 authentication enabled correctly\. | 
|  `system.cpu.usage.value` average >= 80% for 15 minutes, 3 consecutive times  | Sustained high CPU usage can be problematic\. Consider increasing the maximum capacity for the pipeline\. | 
|  `bufferUsage.value` average >= 80% for 15 minutes, 3 consecutive times  | Sustained high buffer usage can be problematic\. Consider increasing the maximum capacity for the pipeline\. | 

### Other alarms you might consider<a name="osis-cw-alarms-additional"></a>

Consider configuring the following alarms depending on which Amazon OpenSearch Ingestion features you regularly use\. 


| Alarm | Issue | 
| --- | --- | 
|  `opensearch.s3.dlqS3RecordsSuccess.count` sum >= `opensearch.documentSuccess.count` sum for 1 minute, 1 consecutive time  | A larger number of records are being sent to the DLQ than the OpenSearch sink\. Review the OpenSearch sink plugin metrics to investigate and determine the root cause\. | 
|  `grok.grokProcessingTimeouts.count` sum = recordsIn\.count sum for 1 minute, 5 consecutive times  | All data is timing out while the Grok processor is trying to pattern match\. This is likely impacting performance and slowing your pipeline down\. Consider adjusting your patterns to reduce timeouts\.  | 
|  `grok.grokProcessingErrors.count` sum is >= 1 for 1 minute, 1 consecutive time  | The Grok processor is failing to match patterns to the data in the pipeline, resulting in errors\. Review your data and Grok plugin configurations to ensure the pattern matching is expected  | 
|  `grok.grokProcessingMismatch.count` sum = recordsIn\.count sum for 1 minute, 5 consecutive times  | The Grok processor is unable to match patterns to the data in the pipeline\. Review your data and Grok plugin configurations to ensure the pattern matching is expected  | 
|  `date.dateProcessingMatchFailure.count` sum = recordsIn\.count sum for 1 minut, 5 consecutive times  | The Date processor is unable to match any patterns to the data in the pipeline\. Review your data and Date plugin configurations to ensure the pattern is expected\. | 
|  `s3.s3ObjectsFailed.count` sum >= 1 for 1 minute, 1 consecutive time  | This issue is either occurring because the S3 object doesn't exist, or the pipeline has insufficient privileges\. Reivew the s3ObjectsNotFound\.count and s3ObjectsAccessDenied\.count metrics to determine the root cause\. Confirm that the S3 object exists and/or update the permissions\. | 
|  `s3.sqsMessagesFailed.count` sum >= 1 for 1 minute, 1 consecutive time  | The S3 plugin failed to process an Amazon SQS message\. If you have a DLQ enabled on your SQS queue, review the failed message\. The queue might be receiving invalid data that the pipeline is attempting to process | 
|  `http.badRequests.count` sum >= 1 for 1 minute, 1 consecutive times  | The client is sending a bad request\. Confirm that all clients are sending the proper payload | 
|  `http.requestsTooLarge.count` sum >= 1 for 1 minute, 1 consecutive time  | Requests from the HTTP source plugin contain too much data, which is exceeding the buffer capacity\. Adjust the batch size for your clients\. | 
|  `http.internalServerError.count` sum >= 0 for 1 minute, 1 consecutive time  | The HTTP source plugin is having trouble receiving events\. | 
|  `http.requestTimeouts.count` sum >= 0 for 1 minute, 1 consecutive time  | Source timeouts are likely the result of the pipeline being underprovisioned\. Consider increasing the pipeline maxUnits to handle additional workload\. | 
|  `otel_trace.badRequests.count` sum >= 1 for 1 minute, 1 consecutive time  | The client is sending a bad request\. Confirm that all clients are sending the proper payload\. | 
|  `otel_trace.requestsTooLarge.count` sum >= 1 for 1 minute, 1 consecutive time  | Requests from the Otel Trace source plugin contain too much data, which is exceeding the buffer capacity\. Adjust the batch size for your clients\. | 
|  `otel_trace.internalServerError.count` sum >= 0 for 1 minute, 1 consecutive time  | The Otel Trace source plugin is having trouble receiving events\. | 
|  `otel_trace.requestTimeouts.count` sum >= 0 for 1 minute, 1 consecutive time  | Source timeouts are likely the result of the pipeline being underprovisioned\. Consider increasing the pipeline maxUnits to handle additional workload\. | 
|  `otel_metrics.requestTimeouts.count` sum >= 0 for 1 minute, 1 consecutive time  | Source timeouts are likely the result of the pipeline being underprovisioned\. Consider increasing the pipeline maxUnits to handle additional workload\. | 