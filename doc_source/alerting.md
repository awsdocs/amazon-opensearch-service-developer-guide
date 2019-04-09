# Alerting<a name="alerting"></a>

The alerting feature notifies you when data from one or more Elasticsearch indices meets certain conditions\. For example, you might want to receive an email if your application logs more than five HTTP 503 errors in one hour, or you might want to page a developer if no new documents have been indexed in the past 20 minutes\. To get started, open Kibana and choose **Alerting**\.

Alerting requires Elasticsearch 6\.2 or higher\. Full documentation for the feature is available in the [Open Distro for Elasticsearch documentation](https://opendistro.github.io/for-elasticsearch-docs/docs/alerting/)\.

## Differences<a name="alerting-diff"></a>

Compared to Open Distro for Elasticsearch, the Amazon Elasticsearch Service alerting feature has two notable differences: Amazon SNS support and fixed settings\.

### Amazon SNS Support<a name="alerting-diff-sns"></a>

Amazon ES supports [Amazon SNS](https://aws.amazon.com/sns/) for notifications\. This integration with Amazon SNS means that, in addition to standard destinations \(Slack, custom webhooks, and Amazon Chime\), the alerting feature can send emails, text messages, and even execute AWS Lambda functions using SNS topics\. For more information about Amazon SNS, see the [Amazon Simple Notification Service Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/)\.

**To add Amazon SNS as a destination**

1. Open Kibana\.

1. Choose **Alerting**\.

1. Choose the **Destinations** tab and then **Add Destination**\.

1. Provide a unique name for the destination\.

1. For **Type**, choose **Amazon SNS**\.

1. Provide the SNS topic ARN\.

1. Provide the ARN for an IAM role within your account that has the following trust relationship and permissions \(at minimum\):

   ```
   {
     "Version": "2012-10-17",
     "Statement": [{
       "Effect": "Allow",
       "Principal": {
         "Service": "es.amazonaws.com"
       },
       "Action": "sts:AssumeRole"
     }]
   }
   ```

   ```
   {
     "Version": "2012-10-17",
     "Statement": [{
       "Effect": "Allow",
       "Action": "sns:Publish",
       "Resource": "sns-topic-arn"
     }]
   }
   ```

1. Choose **Create**\.

### Alerting Settings<a name="alerting-diff-settings"></a>

Open Distro for Elasticsearch lets you modify certain alerting settings using the `_cluster/settings` API \(for example, `opendistro.alerting.monitor.max_monitors`\)\. On Amazon ES, you can't change these settings\.