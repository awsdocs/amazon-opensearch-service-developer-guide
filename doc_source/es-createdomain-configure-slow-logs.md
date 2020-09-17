# Configuring Elasticsearch Logs<a name="es-createdomain-configure-slow-logs"></a>

Amazon ES exposes four Elasticsearch logs through Amazon CloudWatch Logs: error logs, search slow logs, index slow logs, and [audit logs](audit-logs.md)\. Search slow logs, index slow logs, and error logs are useful for troubleshooting performance and stability issues\. Audit logs track user activity for compliance purposes\. All the logs are *disabled* by default\. If enabled, [standard CloudWatch pricing](https://aws.amazon.com/cloudwatch/pricing/) applies\.

**Note**  
Error logs are available only for Elasticsearch versions 5\.1 and greater\. Slow logs are available for all Elasticsearch versions\.

For its logs, Elasticsearch uses [Apache Log4j 2](https://logging.apache.org/log4j/2.x/) and its built\-in log levels \(from least to most severe\) of `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, and `FATAL`\.

If you enable error logs, Amazon ES publishes log lines of `WARN`, `ERROR`, and `FATAL` to CloudWatch\. Amazon ES also publishes several exceptions from the `DEBUG` level, including the following:
+ `org.elasticsearch.index.mapper.MapperParsingException`
+ `org.elasticsearch.index.query.QueryShardException`
+ `org.elasticsearch.action.search.SearchPhaseExecutionException`
+ `org.elasticsearch.common.util.concurrent.EsRejectedExecutionException`
+ `java.lang.IllegalArgumentException`

Error logs can help with troubleshooting in many situations, including the following:
+ Painless script compilation issues
+ Invalid queries
+ Indexing issues
+ Snapshot failures

## Enabling Log Publishing \(Console\)<a name="es-createdomain-configure-slow-logs-console"></a>

The Amazon ES console is the simplest way to enable the publishing of logs to CloudWatch\.

**To enable log publishing to CloudWatch \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose the domain that you want to update\.

1. On the **Logs** tab, choose **Enable** for the log that you want\. 

1. Create a CloudWatch log group, or choose an existing one\.
**Note**  
If you plan to enable multiple logs, we recommend publishing each to its own log group\. This separation makes the logs easier to scan\.

1. Choose an access policy that contains the appropriate permissions, or create a policy using the JSON that the console provides:

   ```
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Principal": {
           "Service": "es.amazonaws.com"
         },
         "Action": [
           "logs:PutLogEvents",
           "logs:CreateLogStream"
         ],
         "Resource": "cw_log_group_arn"
       }
     ]
   }
   ```
**Important**  
CloudWatch Logs supports [10 resource policies per Region](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_PutResourcePolicy.html)\. If you plan to enable logs for several Amazon ES domains, you should create and reuse a broader policy that includes multiple log groups to avoid reaching this limit\. For steps on updating your policy, see [Enabling Log Publishing \(AWS CLI\)](#es-createdomain-configure-slow-logs-cli)\.

1. Choose **Enable**\.

   The status of your domain changes from **Active** to **Processing**\. The status must return to **Active** before log publishing is enabled\. This change typically takes 30 minutes, but can take longer depending on your domain configuration\.

If you enabled one of the slow logs, see [Setting Elasticsearch Logging Thresholds for Slow Logs](#es-createdomain-configure-slow-logs-indices)\. If you enabled audit logs, see [Audit Log Kibana UI](audit-logs.md#audit-log-kibana-ui)\. If you enabled only error logs, you don't need to perform any additional configuration steps\. 

## Enabling Log Publishing \(AWS CLI\)<a name="es-createdomain-configure-slow-logs-cli"></a>

Before you can enable log publishing, you need a CloudWatch log group\. If you don't already have one, you can create one using the following command:

```
aws logs create-log-group --log-group-name my-log-group
```

Enter the next command to find the log group's ARN, and then *make a note of it*:

```
aws logs describe-log-groups --log-group-name my-log-group
```

Now you can give Amazon ES permissions to write to the log group\. You must provide the log group's ARN near the end of the command:

```
aws logs put-resource-policy --policy-name my-policy --policy-document '{ "Version": "2012-10-17", "Statement": [{ "Sid": "", "Effect": "Allow", "Principal": { "Service": "es.amazonaws.com"}, "Action":[ "logs:PutLogEvents","logs:PutLogEventsBatch","logs:CreateLogStream"],"Resource": "cw_log_group_arn"}]}'
```

**Important**  
CloudWatch Logs supports [10 resource policies per Region](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_PutResourcePolicy.html)\. If you plan to enable slow logs for several Amazon ES domains, you should create and reuse a broader policy that includes multiple log groups to avoid reaching this limit\.

If you need to review this policy at a later time, use the `aws logs describe-resource-policies` command\. To update the policy, issue the same `aws logs put-resource-policy` command with a new policy document\.

Finally, you can use the `--log-publishing-options` option to enable publishing\. The syntax for the option is the same for both the `create-elasticsearch-domain` and `update-elasticsearch-domain-config` commands\.


****  
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-createdomain-configure-slow-logs.html)

**Note**  
If you plan to enable multiple logs, we recommend publishing each to its own log group\. This separation makes the logs easier to scan\.

**Example**

The following example enables the publishing of search and index slow logs for the specified domain:

```
aws es update-elasticsearch-domain-config --domain-name my-domain --log-publishing-options "SEARCH_SLOW_LOGS={CloudWatchLogsLogGroupArn=arn:aws:logs:us-east-1:123456789012:log-group:my-log-group,Enabled=true},INDEX_SLOW_LOGS={CloudWatchLogsLogGroupArn=arn:aws:logs:us-east-1:123456789012:log-group:my-other-log-group,Enabled=true}"
```

To disable publishing to CloudWatch, run the same command with `Enabled=false`\.

If you enabled one of the slow logs, see [Setting Elasticsearch Logging Thresholds for Slow Logs](#es-createdomain-configure-slow-logs-indices)\. If you enabled audit logs, see [Audit Log Kibana UI](audit-logs.md#audit-log-kibana-ui)\. If you enabled only error logs, you don't need to perform any additional configuration steps\.

## Enabling Log Publishing \(AWS SDKs\)<a name="es-createdomain-configure-slow-logs-sdk"></a>

Before you can enable log publishing, you must first create a CloudWatch log group, get its ARN, and give Amazon ES permissions to write to it\. The relevant operations are documented in the [Amazon CloudWatch Logs API Reference](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/):
+ `CreateLogGroup`
+ `DescribeLogGroup`
+ `PutResourcePolicy`

You can access these operations using the [AWS SDKs](https://aws.amazon.com/tools/#sdk)\.

The AWS SDKs \(except the Android and iOS SDKs\) support all the operations that are defined in [Amazon Elasticsearch Service Configuration API Reference](es-configuration-api.md), including the `--log-publishing-options` option for `CreateElasticsearchDomain` and `UpdateElasticsearchDomainConfig`\.

If you enabled one of the slow logs, see [Setting Elasticsearch Logging Thresholds for Slow Logs](#es-createdomain-configure-slow-logs-indices)\. If you enabled only error logs, you don't need to perform any additional configuration steps\.

## Setting Elasticsearch Logging Thresholds for Slow Logs<a name="es-createdomain-configure-slow-logs-indices"></a>

Elasticsearch disables slow logs by default\. After you enable the *publishing* of slow logs to CloudWatch, you still must specify logging thresholds for each Elasticsearch index\. These thresholds define precisely what should be logged and at which log level\.

You specify these settings through the Elasticsearch REST API:

```
PUT elasticsearch_domain_endpoint/index/_settings
{
  "index.search.slowlog.threshold.query.warn": "5s",
  "index.search.slowlog.threshold.query.info": "2s"
}
```

To test that slow logs are publishing successfully, consider starting with very low values to verify that logs appear in CloudWatch, and then increase the thresholds to more useful levels\.

If the logs don't appear, check the following:
+ Does the CloudWatch log group exist? Check the CloudWatch console\.
+ Does Amazon ES have permissions to write to the log group? Check the Amazon ES console\.
+ Is the Amazon ES domain configured to publish to the log group? Check the Amazon ES console, use the AWS CLI `describe-elasticsearch-domain-config` option, or call `DescribeElasticsearchDomainConfig` using one of the SDKs\.
+ Are the Elasticsearch logging thresholds low enough that your requests are exceeding them? To review your thresholds for an index, use the following command:

  ```
  GET elasticsearch_domain_endpoint/index/_settings?pretty
  ```

If you want to disable slow logs for an index, return any thresholds that you changed to their default values of `-1`\.

Disabling publishing to CloudWatch using the Amazon ES console or AWS CLI does *not* stop Elasticsearch from generating logs; it only stops the *publishing* of those logs\. Be sure to check your index settings if you no longer need the slow logs\.

## Viewing Logs<a name="es-createdomain-configure-slow-logs-viewing"></a>

Viewing the application and slow logs in CloudWatch is just like viewing any other CloudWatch log\. For more information, see [View Log Data](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/Working-with-log-groups-and-streams.html#ViewingLogData) in the *Amazon CloudWatch Logs User Guide*\.

Here are some considerations for viewing the logs:
+ Amazon ES publishes only the first 255,000 characters of each line to CloudWatch\. Any remaining content is truncated\. For audit logs, it's 10,000 characters per message\. 
+ In CloudWatch, the log stream names have suffixes of `-index-slow-logs`, `-search-slow-logs`, `-es-application-logs`, and `-audit-logs` to help identify their contents\.