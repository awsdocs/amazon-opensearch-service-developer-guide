# Monitoring OpenSearch logs with Amazon CloudWatch Logs<a name="createdomain-configure-slow-logs"></a>

Amazon OpenSearch Service exposes the following OpenSearch logs through Amazon CloudWatch Logs: 
+ Error logs
+ Search slow logs
+ Index slow logs
+ [Audit logs](audit-logs.md)

Search slow logs, index slow logs, and error logs are useful for troubleshooting performance and stability issues\. Audit logs track user activity for compliance purposes\. All the logs are *disabled* by default\. If enabled, [standard CloudWatch pricing](https://aws.amazon.com/cloudwatch/pricing/) applies\.

**Note**  
Error logs are available only for OpenSearch and Elasticsearch versions 5\.1 and later\. Slow logs are available for all OpenSearch and Elasticsearch versions\.

For its logs, OpenSearch uses [Apache Log4j 2](https://logging.apache.org/log4j/2.x/) and its built\-in log levels \(from least to most severe\) of `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, and `FATAL`\.

If you enable error logs, OpenSearch Service publishes log lines of `WARN`, `ERROR`, and `FATAL` to CloudWatch\. OpenSearch Service also publishes several exceptions from the `DEBUG` level, including the following:
+ `org.opensearch.index.mapper.MapperParsingException`
+ `org.opensearch.index.query.QueryShardException`
+ `org.opensearch.action.search.SearchPhaseExecutionException`
+ `org.opensearch.common.util.concurrent.OpenSearchRejectedExecutionException`
+ `java.lang.IllegalArgumentException`

Error logs can help with troubleshooting in many situations, including the following:
+ Painless script compilation issues
+ Invalid queries
+ Indexing issues
+ Snapshot failures

## Enabling log publishing \(console\)<a name="createdomain-configure-slow-logs-console"></a>

The OpenSearch Service console is the simplest way to enable the publishing of logs to CloudWatch\.

**To enable log publishing to CloudWatch \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Amazon OpenSearch Service**\.

1. Select the domain you want to update\.

1. On the **Logs** tab, select a log type and choose **Enable**\.

1. Create a new CloudWatch log group or choose an existing one\.
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
           "logs:PutLogEventsBatch",
           "logs:CreateLogStream"
         ],
         "Resource": "cw_log_group_arn:*"
       }
     ]
   }
   ```

   We recommend that you add the `aws:SourceAccount` and `aws:SourceArn` condition keys to the policy to protect yourself against the [confused deputy problem](https://docs.aws.amazon.com/IAM/latest/UserGuide/confused-deputy.html)\. The source account is the owner of the domain and the source ARN is the ARN of the domain\. Your domain must be on service software R20211203 or later in order to add these condition keys\.

   For example, you could add the following condition block to the policy:

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
**Important**  
CloudWatch Logs supports [10 resource policies per Region](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_PutResourcePolicy.html)\. If you plan to enable logs for several OpenSearch Service domains, you should create and reuse a broader policy that includes multiple log groups to avoid reaching this limit\. For steps on updating your policy, see [Enabling log publishing \(AWS CLI\)](#createdomain-configure-slow-logs-cli)\.

1. Choose **Enable**\.

   The status of your domain changes from **Active** to **Processing**\. The status must return to **Active** before log publishing is enabled\. This change typically takes 30 minutes, but can take longer depending on your domain configuration\.

If you enabled one of the slow logs, see [Setting OpenSearch logging thresholds for slow logs](#createdomain-configure-slow-logs-indices)\. If you enabled audit logs, see [Step 2: Turn on audit logs in OpenSearch Dashboards](audit-logs.md#audit-log-dashboards-ui)\. If you enabled only error logs, you don't need to perform any additional configuration steps\. 

## Enabling log publishing \(AWS CLI\)<a name="createdomain-configure-slow-logs-cli"></a>

Before you can enable log publishing, you need a CloudWatch log group\. If you don't already have one, you can create one using the following command:

```
aws logs create-log-group --log-group-name my-log-group
```

Enter the next command to find the log group's ARN, and then *make a note of it*:

```
aws logs describe-log-groups --log-group-name my-log-group
```

Now you can give OpenSearch Service permissions to write to the log group\. You must provide the log group's ARN near the end of the command:

```
aws logs put-resource-policy \
  --policy-name my-policy \
  --policy-document '{ "Version": "2012-10-17", "Statement": [{ "Sid": "", "Effect": "Allow", "Principal": { "Service": "es.amazonaws.com"}, "Action":[ "logs:PutLogEvents","logs:PutLogEventsBatch","logs:CreateLogStream"],"Resource": "cw_log_group_arn:*"}]}'
```

**Important**  
CloudWatch Logs supports [10 resource policies per Region](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_PutResourcePolicy.html)\. If you plan to enable slow logs for several OpenSearch Service domains, you should create and reuse a broader policy that includes multiple log groups to avoid reaching this limit\.

If you need to review this policy at a later time, use the `aws logs describe-resource-policies` command\. To update the policy, issue the same `aws logs put-resource-policy` command with a new policy document\.

Finally, you can use the `--log-publishing-options` option to enable publishing\. The syntax for the option is the same for both the `create-domain` and `update-domain-config` commands\.


****  
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/createdomain-configure-slow-logs.html)

**Note**  
If you plan to enable multiple logs, we recommend publishing each to its own log group\. This separation makes the logs easier to scan\.

**Example**

The following example enables the publishing of search and index slow logs for the specified domain:

```
aws opensearch update-domain-config \
  --domain-name my-domain \
  --log-publishing-options "SEARCH_SLOW_LOGS={CloudWatchLogsLogGroupArn=arn:aws:logs:us-east-1:123456789012:log-group:my-log-group,Enabled=true},INDEX_SLOW_LOGS={CloudWatchLogsLogGroupArn=arn:aws:logs:us-east-1:123456789012:log-group:my-other-log-group,Enabled=true}"
```

To disable publishing to CloudWatch, run the same command with `Enabled=false`\.

If you enabled one of the slow logs, see [Setting OpenSearch logging thresholds for slow logs](#createdomain-configure-slow-logs-indices)\. If you enabled audit logs, see [Step 2: Turn on audit logs in OpenSearch Dashboards](audit-logs.md#audit-log-dashboards-ui)\. If you enabled only error logs, you don't need to perform any additional configuration steps\.

## Enabling log publishing \(AWS SDKs\)<a name="createdomain-configure-slow-logs-sdk"></a>

Before you can enable log publishing, you must first create a CloudWatch log group, get its ARN, and give OpenSearch Service permissions to write to it\. The relevant operations are documented in the [Amazon CloudWatch Logs API Reference](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/):
+ `CreateLogGroup`
+ `DescribeLogGroup`
+ `PutResourcePolicy`

You can access these operations using the [AWS SDKs](https://aws.amazon.com/tools/#sdk)\.

The AWS SDKs \(except the Android and iOS SDKs\) support all the operations that are defined in [Configuration API reference for Amazon OpenSearch Service](configuration-api.md), including the `--log-publishing-options` option for `CreateDomain` and `UpdateDomainConfig`\.

If you enabled one of the slow logs, see [Setting OpenSearch logging thresholds for slow logs](#createdomain-configure-slow-logs-indices)\. If you enabled only error logs, you don't need to perform any additional configuration steps\.

## Enabling log publishing \(CloudFormation\)<a name="createdomain-configure-slow-logs-cfn"></a>

In this example, we use CloudFormation to create a log group called `opensearch-logs`, assign the appropriate permissions, and then create a domain with log publishing enabled for application logs, search slow logs, and index slow logs\.

Before you can enable log publishing, you need to create a CloudWatch log group:

```
Resources:
  OpenSearchLogGroup:
    Type: AWS::Logs::LogGroup
    Properties: 
      LogGroupName: opensearch-logs
Outputs:
  Arn:
    Value:
      'Fn::GetAtt':
        - OpenSearchLogGroup
        - Arn
```

The template outputs the ARN of the log group\. In this case, the ARN is `arn:aws:logs:us-east-1:123456789012:log-group:opensearch-logs`\.

Using the ARN, create a resource policy that gives OpenSearch Service permissions to write to the log group:

```
Resources:
 OpenSearchLogPolicy:
   Type: AWS::Logs::ResourcePolicy
   Properties:
     PolicyName: my-policy
     PolicyDocument: "{ \"Version\": \"2012-10-17\", \"Statement\": [{ \"Sid\": \"\", \"Effect\": \"Allow\", \"Principal\": { \"Service\": \"es.amazonaws.com\"}, \"Action\":[ \"logs:PutLogEvents\",\"logs:PutLogEventsBatch\",\"logs:CreateLogStream\"],\"Resource\": \"arn:aws:logs:us-east-1:123456789012:log-group:opensearch-logs:*\"}]}"
```

Finally, create the following CloudFormation stack which generates an OpenSearch Service domain with log publishing enabled\. The access policy permits the root user for the AWS account to make all HTTP requests to the domain:

```
Resources:
  OpenSearchServiceDomain:
    Type: "AWS::OpenSearchService::Domain"
    Properties:
      DomainName: my-domain
      EngineVersion: "OpenSearch_1.0"
      ClusterConfig:
        InstanceCount: 2
        InstanceType: "r6g.xlarge.search"
        DedicatedMasterEnabled: true
        DedicatedMasterCount: 3
        DedicatedMasterType: "r6g.xlarge.search"
      EBSOptions:
        EBSEnabled: true
        VolumeSize: 10
        VolumeType: "gp2"
      AccessPolicies:
        Version: "2012-10-17"
        Statement:
            Effect: "Allow"
            Principal:
                AWS: "arn:aws:iam::123456789012:user/es-user"
            Action: "es:*"
            Resource: "arn:aws:es:us-east-1:123456789012:domain/my-domain/*"
      LogPublishingOptions:
        ES_APPLICATION_LOGS:
          CloudWatchLogsLogGroupArn: "arn:aws:logs:us-east-1:123456789012:log-group:opensearch-logs"
          Enabled: true
        SEARCH_SLOW_LOGS:
          CloudWatchLogsLogGroupArn: "arn:aws:logs:us-east-1:123456789012:log-group:opensearch-logs"
          Enabled: true
        INDEX_SLOW_LOGS:
          CloudWatchLogsLogGroupArn: "arn:aws:logs:us-east-1:123456789012:log-group:opensearch-logs"
          Enabled: true
```

For detailed syntax information, see the [log publishing options](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-elasticsearch-domain-logpublishingoption.html) in the *AWS CloudFormation User Guide\.*

## Setting OpenSearch logging thresholds for slow logs<a name="createdomain-configure-slow-logs-indices"></a>

OpenSearch disables slow logs by default\. After you enable the *publishing* of slow logs to CloudWatch, you still must specify logging thresholds for each OpenSearch index\. These thresholds define precisely what should be logged and at which log level\.

You specify these settings through the OpenSearch REST API:

```
PUT domain-endpoint/index/_settings
{
  "index.search.slowlog.threshold.query.warn": "5s",
  "index.search.slowlog.threshold.query.info": "2s"
}
```

To test that slow logs are publishing successfully, consider starting with very low values to verify that logs appear in CloudWatch, and then increase the thresholds to more useful levels\.

If the logs don't appear, check the following:
+ Does the CloudWatch log group exist? Check the CloudWatch console\.
+ Does OpenSearch Service have permissions to write to the log group? Check the OpenSearch Service console\.
+ Is the OpenSearch Service domain configured to publish to the log group? Check the OpenSearch Service console, use the AWS CLI `describe-domain-config` option, or call `DescribeDomainConfig` using one of the SDKs\.
+ Are the OpenSearch logging thresholds low enough that your requests are exceeding them? To review your thresholds for an index, use the following command:

  ```
  GET domain-endpoint/index/_settings?pretty
  ```

If you want to disable slow logs for an index, return any thresholds that you changed to their default values of `-1`\.

Disabling publishing to CloudWatch using the OpenSearch Service console or AWS CLI does *not* stop OpenSearch from generating logs; it only stops the *publishing* of those logs\. Be sure to check your index settings if you no longer need the slow logs\.

## Viewing logs<a name="createdomain-configure-slow-logs-viewing"></a>

Viewing the application and slow logs in CloudWatch is just like viewing any other CloudWatch log\. For more information, see [View Log Data](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/Working-with-log-groups-and-streams.html#ViewingLogData) in the *Amazon CloudWatch Logs User Guide*\.

Here are some considerations for viewing the logs:
+ OpenSearch Service publishes only the first 255,000 characters of each line to CloudWatch\. Any remaining content is truncated\. For audit logs, it's 10,000 characters per message\. 
+ In CloudWatch, the log stream names have suffixes of `-index-slow-logs`, `-search-slow-logs`, `-application-logs`, and `-audit-logs` to help identify their contents\.