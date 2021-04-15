# Alerting for Amazon Elasticsearch Service<a name="alerting"></a>

Configure alerts in Amazon Elasticsearch Service \(Amazon ES\) to get notified when data from one or more Elasticsearch indices meets certain conditions\. For example, you might want to receive an email if your application logs more than five HTTP 503 errors in one hour, or you might want to page a developer if no new documents have been indexed in the last 20 minutes\. 

Alerting requires Elasticsearch version 6\.2 or later\. For full documentation, including API descriptions, see the [Open Distro for Elasticsearch documentation](https://opendistro.github.io/for-elasticsearch-docs/docs/alerting/)\. This topic highlights the differences in alerting in \(Amazon ES\) compared to the open\-source version\.

**To get started with alerting**

1. Choose **Alerting** from the Kibana main menu\.

1. Set up a destination for the alert\. Choose between Slack, Amazon Chime, a custom webhook, or Amazon SNS\.

1. Create a monitor in one of three ways: visually, using a query, or using an anomaly detector\.

1. Define a condition to trigger the monitor\.

1. Add one or more actions to the monitor\.

For detailed steps, see [Monitors](https://opendistro.github.io/for-elasticsearch-docs/docs/alerting/monitors/) in the Open Distro for Elasticsearch documentation\.

## Differences<a name="alerting-diff"></a>

Compared to Open Distro for Elasticsearch, alerting in Amazon ES has some notable differences\.

### Amazon SNS Support<a name="alerting-diff-sns"></a>

Amazon ES supports Amazon Simple Notification Service \([Amazon SNS](https://aws.amazon.com/sns/)\) for notifications\. This integration means that in addition to standard destinations \(Slack, custom webhooks, and Amazon Chime\), you can also send emails, text messages, and even run AWS Lambda functions using SNS topics\. For more information about Amazon SNS, see the [Amazon Simple Notification Service Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/)\.

**To add Amazon SNS as a destination**

1. Choose **Alerting** from the Kibana main menu\.

1. Go to the **Destinations** tab and then choose **Add destination**\.

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

   For more information, see [Adding IAM Identity Permissions](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_manage-attach-detach.html#add-policies-console) in the *IAM User Guide*\.

1. Choose **Create**\.

### Alerting Settings<a name="alerting-diff-settings"></a>

Amazon ES lets you modify the following [alerting settings](https://opendistro.github.io/for-elasticsearch-docs/docs/alerting/settings/#alerting-settings):
+ `opendistro.scheduled_jobs.enabled`
+ `opendistro.alerting.alert_history_enabled`
+ `opendistro.alerting.alert_history_max_age`
+ `opendistro.alerting.alert_history_max_docs`
+ `opendistro.alerting.alert_history_retention_period`
+ `opendistro.alerting.alert_history_rollover_period`
+ `opendistro.alerting.filter_by_backend_roles`

All other settings use the default values which you can't change\.

To disable alerting, send the following request:

```
PUT _cluster/settings
{
  "persistent" : {
    "opendistro.scheduled_jobs.enabled" : false
  }
}
```

The following request configures alerting to automatically delete history indices after seven days, rather than the default 30 days:

```
PUT _cluster/settings
{
  "persistent": {
    "opendistro.alerting.alert_history_retention_period": "7d"
  }
}
```

If you previously created monitors and want to stop the creation of daily alerting indices, delete all alert history indices:

```
DELETE .opendistro-alerting-alert-history-*
```

To reduce shard count for history indices, create an index template\. The following request sets history indices for both alerting and [Index State Management](ism.md) to one shard and one replica:

```
PUT _template/template-name
{
  "index_patterns": [".opendistro-alerting-alert-history-*", ".opendistro-ism-managed-index-history-*"],
  "template": {
    "settings": {
      "number_of_shards": 1,
      "number_of_replicas": 1
    }
  }
}
```

Depending on your tolerance for data loss, you might even consider using zero replicas\. For more information about creating and managing index templates, see [Index template](https://opendistro.github.io/for-elasticsearch-docs/docs/elasticsearch/index-templates/) in the Open Distro for Elasticsearch documentation\. 

### Alerting Permissions<a name="alerting-diff-perms"></a>

Alerting supports [fine\-grained access control](fgac.md)\. For details on mixing and matching permissions to fit your use case, see [Alerting security](https://opendistro.github.io/for-elasticsearch-docs/docs/alerting/security/) in the Open Distro for Elasticsearch documentation\.