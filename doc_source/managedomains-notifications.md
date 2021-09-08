# Notifications in Amazon OpenSearch Service<a name="managedomains-notifications"></a>

Notifications in Amazon OpenSearch Service currently contain information about available software updates and Auto\-Tune events for your domains\. In the future, they might also include performance optimization recommendations such as moving to the correct instance type for a domain or rebalancing shards to reduce performance bottlenecks\. 

You can view notifications in the **Notifications** panel of the OpenSearch Service console or in [Amazon EventBridge](https://docs.aws.amazon.com/eventbridge/latest/userguide/create-eventbridge-rule.html), but not in the AWS [Personal Health Dashboard](https://docs.aws.amazon.com/health/latest/ug/getting-started-phd.html)\. They're available for all versions of OpenSearch and Elasticsearch OSS, with some minor exceptions\. For the format of different events sent to EventBridge, see [Monitoring OpenSearch Service events with Amazon EventBridge](monitoring-events.md)\.

## Getting started with notifications<a name="managedomains-notifications-start"></a>

Notifications are enabled automatically when you create a domain\. Go to the **Notifications** panel of the OpenSearch Service console to monitor and acknowledge notifications\. Each notification includes information such as the time it was posted, the domain it relates to, a severity and status level, and a brief explanation\. You can view historical notifications for up to 90 days in the console\.

## Notification types<a name="managedomains-notifications-types"></a>

At this time, all notifications in OpenSearch Service are *informational*, which relate to any action you've already taken or the operations of your domain\. In the future, OpenSearch Service might also include *actionable* notifications, which will require you to take specific actions such as applying a mandatory security patch\. 

## Notification severities<a name="managedomains-notifications-severities"></a>

Each notification has a severity associated with it\. Currently, all available notifications have a severity of `Informational`, while future ones might be `Low`, `Medium`, `High`, or `Critical`\. The following table provides a summary of notification severities:


| Severity | Description | Examples | 
| --- | --- | --- | 
| Informational |  Information related to the operation of your domain\.  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/managedomains-notifications.html)  | 
| Low |  A recommended action, but has no adverse impact on domain availability or performance if no action is taken\.  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/managedomains-notifications.html)  | 
| Medium |  There might be an impact if the recommended action is not taken, but comes with an extended time window for the action to be taken\.  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/managedomains-notifications.html)  | 
| High |  Urgent action is required to avoid adverse impact\.  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/managedomains-notifications.html)  | 
| Critical |  Immediate action is required to avoid adverse impact, or to recover from it\.   | None currently available | 

## Sample EventBridge event<a name="managedomains-notifications-cloudwatch"></a>

The following example shows an OpenSearch Service notification event sent to Amazon EventBridge\. The corresponding notification has a severity of `Informational` because the update is optional:

```
{
  "version": "0",
  "id": "01234567-0123-0123-0123-012345678901",
  "detail-type": "Amazon OpenSearch Service Software Update Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2016-11-01T13:12:22Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Service Software Update",
    "status": "Available",
    "severity": "Informational",
    "description": "Service software update [R20200330-p1] available."
  }
}
```