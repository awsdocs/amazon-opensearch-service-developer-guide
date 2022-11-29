# Recommended CloudWatch alarms for Amazon OpenSearch Service<a name="cloudwatch-alarms"></a>

CloudWatch alarms perform an action when a CloudWatch metric exceeds a specified value for some amount of time\. For example, you might want AWS to email you if your cluster health status is `red` for longer than one minute\. This section includes some recommended alarms for Amazon OpenSearch Service and how to respond to them\.

You can automatically deploy these alarms using AWS CloudFormation\. For a sample stack, see the related [GitHub repository](https://github.com/ev2900/OpenSearch_CloudWatch_Alarms)\.

**Note**  
If you deploy the CloudFormation stack, the `KMSKeyError` and `KMSKeyInaccessible` alarms will exists in an `Insufficient Data` state because these metrics only appear if a domain encounters a problem with its encryption key\.

For more information about configuring alarms, see [Creating Amazon CloudWatch Alarms](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/AlarmThatSendsEmail.html) in the *Amazon CloudWatch User Guide*\.

[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/cloudwatch-alarms.html)

**Note**  
If you just want to *view* metrics, see [Monitoring OpenSearch cluster metrics with Amazon CloudWatch](managedomains-cloudwatchmetrics.md)\.

## Other alarms you might consider<a name="cw-alarms-additional"></a>

Consider configuring the following alarms depending on which OpenSearch Service features you regularly use\. 

[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/cloudwatch-alarms.html)