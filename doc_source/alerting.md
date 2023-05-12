# Configuring alerts in Amazon OpenSearch Service<a name="alerting"></a>

Configure alerts in Amazon OpenSearch Service to get notified when data from one or more indices meets certain conditions\. For example, you might want to receive an email if your application logs more than five HTTP 503 errors in one hour, or you might want to page a developer if no new documents have been indexed in the last 20 minutes\. 

Alerting requires OpenSearch or Elasticsearch 6\.2 or later\. For full documentation, including API descriptions, see [Alerting](https://opensearch.org/docs/latest/monitoring-plugins/alerting/index/) in the OpenSearch documentation\. This topic highlights the differences in alerting in OpenSearch Service compared to the open\-source version\.

## Getting started with alerting<a name="alerting-getstarted"></a>

To create an alert, you configure a *monitor*, which is a job that runs on a defined schedule and queries OpenSearch indexes\. You also configure one or more *triggers*, which define the conditions that generate events\. Finally, you configure *actions*, which is what happens after an alert is triggered\. 

**To get started with alerting**

1. Choose **Alerting** from the OpenSearch Dashboards main menu and choose **Create monitor**\.

1. Create a per\-query, per\-bucket, per\-cluster metrics, or per\-document monitor\. For instructions, see [Create a monitor](https://opensearch.org/docs/latest/monitoring-plugins/alerting/monitors/#create-a-monitor)\.

1. For **Triggers**, create one or more triggers\. For instructions, see [Create triggers](https://opensearch.org/docs/latest/monitoring-plugins/alerting/monitors/#create-triggers)\.

1. For **Actions**, set up a [notification channel](#alerting-notifications) for the alert\. Choose between Slack, Amazon Chime, a custom webhook, or Amazon SNS\. As you might imagine, notifications require connectivity to the channel\. For example, your OpenSearch Service domain must be able to connect to the internet to notify a Slack channel or send a custom webhook to a third\-party server\. The custom webhook must have a public IP address in order for an OpenSearch Service domain to send alerts to it\.
**Tip**  
After an action successfully sends a message, securing access to that message \(for example, access to a Slack channel\) is your responsibility\. If your domain contains sensitive data, consider using triggers without actions and periodically checking Dashboards for alerts\.

## Notifications<a name="alerting-notifications"></a>

Alerting integrates with Notifications, which is a unified system for OpenSearch notifications\. Notifications let you configure which communication service you want to use and see relevant statistics and troubleshooting information\. For comprehensive documentation, see [Notifications](https://opensearch.org/docs/latest/notifications-plugin/index/) in the OpenSearch documentation\.

Your domain must be running OpenSearch version 2\.3 or later to use notifications\.

**Note**  
OpenSearch notifications are separate from OpenSearch Service [notifications](managedomains-notifications.md), which provide details about service software updates, Auto\-Tune enhancements, and other important domain\-level information\. OpenSearch notifications are plugin\-specific\.

Notification channels replaced alerting destinations starting with OpenSearch version 2\.0\. Destinations were officially deprecated, and all alerting notification will be managed through channels going forward\.

When you upgrade your domains to version 2\.3 or later \(since OpenSearch Service support for 2\.x starts with 2\.3\), your existing destinations are automatically migrated to notification channels\. If a destination fails to migrate, the monitor will continue to use it until the monitor is migrated to a notification channel\. For more inforation, see [Questions about destinations](https://opensearch.org/docs/latest/observing-your-data/alerting/monitors/#questions-about-destinations) in the OpenSearch documentation\.

To get started with notifications, sign in to OpenSearch Dashboards and choose **Notifications**, **Channels**, and **Create channel**\.

Amazon Simple Notification Service \(Amazon SNS\) is a supported channel type for notifications\. In order to authenticate users, you either need to provide the user with full access to Amazon SNS, or let them assume an IAM role that has permissions to access Amazon SNS\. For instructions, see [Amazon SNS as a channel type](https://opensearch.org/docs/latest/observing-your-data/notifications/index/#amazon-sns-as-a-channel-type)\.

## Differences<a name="alerting-diff"></a>

Compared to the open\-source version of OpenSearch, alerting in Amazon OpenSearch Service has some notable differences\.

### Alerting settings<a name="alerting-diff-settings"></a>

OpenSearch Service lets you modify the following [alerting settings](https://opensearch.org/docs/latest/observing-your-data/alerting/settings/#alerting-settings):
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

Depending on your tolerance for data loss, you might even consider using zero replicas\. For more information about creating and managing index templates, see [Index templates](https://opensearch.org/docs/latest/opensearch/index-templates/) in the OpenSearch documentation\. 

### Alerting permissions<a name="alerting-diff-perms"></a>

Alerting supports [fine\-grained access control](fgac.md)\. For details on mixing and matching permissions to fit your use case, see [Alerting security](https://opensearch.org/docs/latest/monitoring-plugins/alerting/security/) in the OpenSearch documentation\.