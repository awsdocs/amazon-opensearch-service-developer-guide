# Monitoring pipeline logs<a name="monitoring-pipeline-logs"></a>

You can enable logging for Amazon OpenSearch Ingestion pipelines to expose error and warning messages raised during pipeline operations and ingestion activity\. OpenSearch Ingestion publishes all logs to *Amazon CloudWatch Logs*\. CloudWatch Logs can monitor information in the log files and notify you when certain thresholds are met\. You can also archive your log data in highly durable storage\. For more information, see the [Amazon CloudWatch Logs User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/)\.

Logs from OpenSearch Ingestion might indicate failed processing of requests, authentication errors from the source to the sink, and other warnings that can be helpful for troubleshooting\. For its logs, OpenSearch Ingestion uses the log levels of `INFO`, `WARN`, `ERROR`, and `FATAL`\. We recommend enabling log publishing for all pipelines\.

## Permissions required<a name="monitoring-pipeline-logs-permissions"></a>

In order to enable OpenSearch Ingestion to send logs to CloudWatch Logs, you must be signed in as a user that has certain IAM permissions\. 

You need the following CloudWatch Logs permissions in order to create and update log delivery resources:

```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Resource": "*",
            "Action": [
                "logs:CreateLogDelivery",
                "logs:PutResourcePolicy",
                "logs:UpdateLogDelivery",
                "logs:DeleteLogDelivery",
                "logs:DescribeResourcePolicies",
                "logs:GetLogDelivery",
                "logs:ListLogDeliveries"
            ]
        }
    ]
}
```

## Enabling log publishing<a name="monitoring-pipeline-logs-enable"></a>

You can enable log publishing on existing pipelines, or while creating a pipeline\. For steps to enable log publishing during pipeline creation, see [Creating pipelines](creating-pipeline.md#create-pipeline)\.

### Console<a name="monitoring-pipeline-logs-enable-console"></a>

**To enable log publishing on an existing pipeline**

1. Sign in to the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home)\.

1. Choose **Ingestion** in the left navigation pane and select the pipeline that you want to enable logs for\.

1. Choose **Edit log publishing options**\.

1. Select **Publish to CloudWatch Logs**\.

1. Either create a new log group or select an existing one\. We recommend that you format the name as a path, such as `/aws/vendedlogs/OpenSearchIngestion/pipeline-name/audit-logs`\. This format makes it easier to apply a CloudWatch access policy that grants permissions to all log groups under a specific path such as `/aws/vendedlogs/OpenSearchService/OpenSearchIngestion`\.
**Important**  
You must include the prefix `vendedlogs` in the log group name, otherwise creation fails\.

1. Choose **Save**\.

### CLI<a name="monitoring-pipeline-logs-enable-cli"></a>

To enable log publishing using the AWS CLI, send the following request:

```
aws osis update-pipeline \
  --pipeline-name my-pipeline \
  --log-publishing-options  IsLoggingEnabled=true,CloudWatchLogDestination={LogGroup="/aws/vendedlogs/OpenSearchIngestion/pipeline-name"}
```