# Notifications in Amazon Elasticsearch Service<a name="es-managedomains-notifications"></a>

Notifications in Amazon Elasticsearch Service \(Amazon ES\) contain information about domain health, software updates, blue/green deployments, and configuration changes\. They also provide performance optimization recommendations such as moving to the correct instance type for a domain or rebalancing shards to reduce performance bottlenecks\. 

You can view notifications in the Notifications panel of the Amazon ES console or in [Amazon EventBridge](https://docs.aws.amazon.com/eventbridge/latest/userguide/create-eventbridge-rule.html), but not in the AWS [Personal Health Dashboard](https://docs.aws.amazon.com/health/latest/ug/getting-started-phd.html)\. They're available for all versions of Elasticsearch, with some minor exceptions\.

## Getting Started with Notifications<a name="es-managedomains-notifications-start"></a>

Notifications are enabled automatically when you create a domain\. Go to the **Notifications** panel of the Amazon ES console to monitor and acknowledge notifications\. Each notification includes information such as the time it was posted, the domain it relates to, a severity and status level, and a brief explanation\. You can view historical notifications for up to 90 days in the console\.

## Notification Types<a name="es-managedomains-notifications-types"></a>

There are two types of notifications \- actionable and informational\. Actionable notifications require you to take specific actions, while informational notifications relate to any action you've already taken, or information related to operations, availability, or performance of your domain\. For example, an actionable notification might require you to apply a mandatory security patch, and an informational notification might indicate that a configuration change has been successfully applied on your domain\. 

While notifications can be broadly classified as actionable and informational, they can be categorized further into specific functional or operational areas of the service, such as Service Software Updates, Domain Health, and Domain Configuration\.

## Notification Severities<a name="es-managedomains-notifications-severities"></a>

Each notification has a severity associated with it\. Available severities are `Critical`, `High`, `Medium`, `Low`, or `Informational`\. Actionable notifications have a severity between `Critical` and `Low`, while informational notifications have a severity of `Informational`\. See the following table for a summary of notification severities:


| Severity | Description | Examples | 
| --- | --- | --- | 
| Informational |  Information related to the operation of your domain\.  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-managedomains-notifications.html)  | 
| Low |  A recommended action, but has no adverse impact on domain availability or performance if no action is taken\.  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-managedomains-notifications.html)  | 
| Medium |  There might be an impact if the recommended action is not taken, but comes with an extended time window for the action to be taken\.  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-managedomains-notifications.html)  | 
| High |  Urgent action is required to avoid adverse impact\.  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-managedomains-notifications.html)  | 
| Critical |  Immediate action is required to avoid adverse impact, or to recover from it\.   |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-managedomains-notifications.html)  | 

## Sample CloudWatch Event<a name="es-managedomains-notifications-cloudwatch"></a>

The following example shows an Amazon ES notification event sent to Amazon CloudWatch\. The corresponding notification has a severity of `High` because it requires a service software update:

```
{
   "version":"7.9",
   "id":"01234567-0123-0123-0123-012345678901",
   "detail-type":"Service Updates",
   "source":"aws.es",
   "account":"123456789012",
   "time":"2016-11-01T13:12:22Z",
   "region":"us-east-1",
   "resources":[
      "arn:aws:es:us-east-1:123456789012:domain/test-domain"
   ],
   "detail":{
      "event":"service_software_update",
      "status":"required",
      "Severity":"high",
      "installbydate":“April 30, 2020”,
      "description":"Service software update [R20200330-p1] available. Update will be automatically installed after [30/04/2020] if no action is taken."
   }
```