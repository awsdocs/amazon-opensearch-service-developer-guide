# Monitoring Amazon Elasticsearch Service<a name="monitoring-es"></a>

Monitoring is an important part of maintaining the reliability, availability, and performance of Amazon Elasticsearch Service \(Amazon ES\) and your other AWS solutions\. AWS provides the following tools to monitor your Amazon ES resources, report issues, and take automatic actions when appropriate:

**Amazon CloudWatch**  
Amazon CloudWatch monitors your Amazon ES resources in real time\. You can collect and track metrics, create customized dashboards, and set alarms that notify you or take actions when a metric reaches a certain threshold\. For more information, see the [Amazon CloudWatch User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/)\.

**Amazon CloudWatch Logs**  
Amazon CloudWatch Logs lets you monitor, store, and access your Elasticsearch log files\. CloudWatch Logs monitors the information in log files and can notify you when certain thresholds are met\. For more information, see the [Amazon CloudWatch Logs User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/)\.

**Amazon EventBridge**  
Amazon EventBridge delivers a near real\-time stream of system events that describe changes in your Amazon ES domains\. You can create rules that watch for certain events, and trigger automated actions in other AWS services when these events occur\. For more information, see the [Amazon EventBridge User Guide](https://docs.aws.amazon.com/eventbridge/latest/userguide/)\.

**AWS CloudTrail**  
AWS CloudTrail captures configuration API calls made to Amazon ES as events\. It can deliver these events to an Amazon S3 bucket that you specify\. Using this information, you can identify which users and accounts made requests, the source IP address from which the requests were made, and when the requests occurred\. For more information, see the [AWS CloudTrail User Guide](https://docs.aws.amazon.com/awscloudtrail/latest/userguide/)\.

**Topics**
+ [Monitoring Amazon Elasticsearch Service cluster metrics with Amazon CloudWatch](es-managedomains-cloudwatchmetrics.md)
+ [Monitoring Elasticsearch logs with Amazon CloudWatch Logs](es-createdomain-configure-slow-logs.md)
+ [Monitoring audit logs in Amazon Elasticsearch Service](audit-logs.md)
+ [Monitoring Amazon Elasticsearch Service events with Amazon EventBridge](es-monitoring-events.md)
+ [Monitoring Amazon Elasticsearch Service API calls with AWS CloudTrail](es-managedomains-cloudtrailauditing.md)