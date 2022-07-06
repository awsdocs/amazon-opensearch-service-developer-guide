# Configuring alerts in Amazon OpenSearch Service<a name="alerting"></a>

Configure alerts in Amazon OpenSearch Service to get notified when data from one or more indices meets certain conditions\. For example, you might want to receive an email if your application logs more than five HTTP 503 errors in one hour, or you might want to page a developer if no new documents have been indexed in the last 20 minutes\. 

Alerting requires OpenSearch or Elasticsearch 6\.2 or later\. For full documentation, including API descriptions, see the [OpenSearch documentation](https://docs-beta.opensearch.org/monitoring-plugins/alerting/index/)\. This topic highlights the differences in alerting in OpenSearch Service compared to the open\-source version\.

****To get started with alerting****

1. Choose **Alerting** from the OpenSearch Dashboards main menu\.

1. Set up a destination for the alert\. Choose between Slack, Amazon Chime, a custom webhook, or Amazon SNS\. As you might imagine, notifications require connectivity to the destination\. For example, your OpenSearch Service domain must be able to connect to the internet to notify a Slack channel or send a custom webhook to a third\-party server\. The custom webhook must have a public IP address in order for an OpenSearch Service domain to send alerts to it\.

1. Create a monitor in one of three ways: visually, using a query, or using an anomaly detector\.

1. Define a condition to trigger the monitor\.

1. \(Optional\) Add one or more actions to the monitor\.
**Tip**  
After an action successfully sends a message, securing access to that message \(for example, access to a Slack channel\) is your responsibility\. If your domain contains sensitive data, consider using triggers without actions and periodically checking Dashboards for alerts\.

For detailed steps, see [Monitors](https://docs-beta.opensearch.org/monitoring-plugins/alerting/monitors/) in the OpenSearch documentation\.

## Differences<a name="alerting-diff"></a>

Compared to the open\-source version of OpenSearch, alerting in Amazon OpenSearch Service has some notable differences\.

### Amazon SNS support<a name="alerting-diff-sns"></a>

OpenSearch Service supports Amazon Simple Notification Service \([Amazon SNS](https://aws.amazon.com/sns/)\) for notifications\. This integration means that in addition to standard destinations \(Slack, custom webhooks, and Amazon Chime\), you can also send emails, text messages, and even run AWS Lambda functions using SNS topics\. For more information about Amazon SNS, see the [Amazon Simple Notification Service Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/)\.

**To add Amazon SNS as a destination**

1. Choose **Alerting** from the OpenSearch Dashboards main menu\.

1. Go to the **Destinations** tab and then choose **Add destination**\.

1. Provide a unique name for the destination\.

1. For **Type**, choose **Amazon SNS**\.

1. Provide the SNS topic ARN\.

1. Provide the ARN for an IAM role within your account that has the following trust relationship and permissions \(at minimum\):

   **Trust relationship**

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

   We recommend that you use the `aws:SourceAccount` and `aws:SourceArn` condition keys to protect yourself against the [confused deputy problem](https://docs.aws.amazon.com/IAM/latest/UserGuide/confused-deputy.html)\. The source account is the owner of the domain and the source ARN is the ARN of the domain\. Your domain must be on service software R20211203 or later in order to add these condition keys\.

   For example, you could add the following condition block to the trust policy:

   ```
   "Condition": {
       "StringEquals": {
           "aws:SourceAccount": "account-id"
       },
       "ArnLike": {
           "aws:SourceArn": "arn:aws:es:region:account-id:domain/domain-name"
       }
   }
   ```

   **Permissions**

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

### Alerting settings<a name="alerting-diff-settings"></a>

OpenSearch Service lets you modify the following [alerting settings](https://docs-beta.opensearch.org/monitoring-plugins/alerting/settings/#alerting-settings):
+ `plugins.scheduled_jobs.enabled`
+ `plugins.alerting.alert_history_enabled`
+ `plugins.alerting.alert_history_max_age`
+ `plugins.alerting.alert_history_max_docs`
+ `plugins.alerting.alert_history_retention_period`
+ `plugins.alerting.alert_history_rollover_period`
+ `plugins.alerting.filter_by_backend_roles`

All other settings use the default values which you can't change\.

To disable alerting, send the following request:

```
PUT _cluster/settings
{
  "persistent" : {
    "plugins.scheduled_jobs.enabled" : false
  }
}
```

The following request configures alerting to automatically delete history indices after seven days, rather than the default 30 days:

```
PUT _cluster/settings
{
  "persistent": {
    "plugins.alerting.alert_history_retention_period": "7d"
  }
}
```

If you previously created monitors and want to stop the creation of daily alerting indices, delete all alert history indices:

```
DELETE .plugins-alerting-alert-history-*
```

To reduce shard count for history indices, create an index template\. The following request sets history indexes for alerting to one shard and one replica:

```
PUT _index_template/template-name
{
  "index_patterns": [".opendistro-alerting-alert-history-*"],
  "template": {
    "settings": {
      "number_of_shards": 1,
      "number_of_replicas": 1
    }
  }
}
```

Depending on your tolerance for data loss, you might even consider using zero replicas\. For more information about creating and managing index templates, see [Index templates](https://docs-beta.opensearch.org/opensearch/index-templates/) in the OpenSearch documentation\. 

### Alerting permissions<a name="alerting-diff-perms"></a>

Alerting supports [fine\-grained access control](fgac.md)\. For details on mixing and matching permissions to fit your use case, see [Alerting security](https://docs-beta.opensearch.org/monitoring-plugins/alerting/security/) in the OpenSearch documentation\.