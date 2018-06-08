# Managing Amazon Elasticsearch Service Domains<a name="es-managedomains"></a>

As the size and number of documents in your Amazon Elasticsearch Service \(Amazon ES\) domain grow and as network traffic increases, you likely will need to update the configuration of your Elasticsearch cluster\. To know when it's time to reconfigure your domain, you need to monitor domain metrics\. You might also need to audit data\-related API calls to your domain or assign tags to your domain\. This section describes how to perform these and other tasks related to managing your domains\.

**Topics**
+ [About Configuration Changes](#es-managedomains-configuration-changes)
+ [Charges for Configuration Changes](#es-managedomains-config-charges)
+ [Enabling Zone Awareness](#es-managedomains-zoneawareness)
+ [Monitoring Cluster Metrics and Statistics with Amazon CloudWatch \(Console\)](#es-managedomains-cloudwatchmetrics)
+ [Auditing Amazon Elasticsearch Service Domains with AWS CloudTrail](#es-managedomains-cloudtrailauditing)
+ [Tagging Amazon Elasticsearch Service Domains](#es-managedomains-awsresourcetagging)

## About Configuration Changes<a name="es-managedomains-configuration-changes"></a>

Amazon ES uses a *blue/green* deployment process when updating domains\. Blue/green typically refers to the practice of running two production environments, one live and one idle, and switching the two as you make software changes\. In the case of Amazon ES, it refers to the practice of creating a new environment for domain updates and routing users to the new environment after those updates are complete\. The practice minimizes downtime and maintains the original environment in the event that deployment to the new environment is unsuccessful\.

Domain updates occur when you make most configuration changes, but they also occur when the Amazon ES team makes certain software changes to the service\. If you make configuration changes, the domain state changes to **Processing**\. If the Amazon ES team makes software changes, the state remains **Active**\. In both cases, you can review the cluster health and Amazon CloudWatch metrics and see that the number of nodes in the cluster temporarily increases—often doubling—while the domain update occurs\. In the following illustration, you can see the number of nodes doubling from 11 to 22 during a configuration change and returning to 11 when the update is complete\.

![\[Number of nodes doubling from 11 to 22 during a domain configuration change.\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/NodesDoubled.png)

This temporary increase can strain the cluster's [dedicated master nodes](es-managedomains-dedicatedmasternodes.md), which suddenly have many more nodes to manage\. It is important to maintain sufficient capacity on dedicated master nodes to handle the overhead that is associated with these blue/green deployments\.

**Important**  
You do *not* incur any additional charges during configuration changes and service maintenance\. You are billed only for the number of nodes that you request for your cluster\. For specifics, see [Charges for Configuration Changes](#es-managedomains-config-charges)\.

To prevent overloading dedicated master nodes, you can [monitor usage with the Amazon CloudWatch metrics](#es-managedomains-cloudwatchmetrics)\. For recommended maximum values, see [Recommended CloudWatch Alarms](cloudwatch-alarms.md)\.

## Charges for Configuration Changes<a name="es-managedomains-config-charges"></a>

If you change the configuration for a domain, Amazon ES creates a new cluster as described in [About Configuration Changes](#es-managedomains-configuration-changes)\. During the migration of old to new, you incur the following charges:
+ If you change the instance type, you are charged for both clusters for the first hour\. After the first hour, you are charged only for the new cluster\.

  **Example:** You change the configuration from three `m3.xlarge` instances to four `m4.large` instances\. For the first hour, you are charged for both clusters \(3 \* `m3.xlarge` \+ 4 \* `m4.large`\)\. After the first hour, you are charged only for the new cluster \(4 \* `m4.large`\)\.
+ If you don’t change the instance type, you are charged only for the largest cluster for the first hour\. After the first hour, you are charged only for the new cluster\.

  **Example:** You change the configuration from six `m3.xlarge` instances to three `m3.xlarge` instances\. For the first hour, you are charged for the largest cluster \(6 \* `m3.xlarge`\)\. After the first hour, you are charged only for the new cluster \(3 \* `m3.xlarge`\)\.

## Enabling Zone Awareness<a name="es-managedomains-zoneawareness"></a>

Each AWS Region is a separate geographic area with multiple, isolated locations known as *Availability Zones*\. To prevent data loss and minimize downtime in the event of node and data center failure, you can use the Amazon ES console to allocate an Elasticsearch cluster's nodes and shards across two Availability Zones in the same region\. This allocation is known as *zone awareness*\. Zone awareness requires an even number of instances and slightly increases network latencies\.

If you enable zone awareness, you must have at least one replica for each index in your cluster\. Fortunately, the default configuration for any index is a replica count of 1\. Amazon ES distributes primary and replica shards across nodes in different Availability Zones, which increases the availability of your cluster\.

**Important**  
 If you specify a replica count of 0 for an index, enabling zone awareness doesn't provide any additional data durability or availability\. Without replicas, Amazon ES can't distribute copies of your data to other Availability Zones\.

If you enable zone awareness and use VPC access domains, you must specify Availability Zones for the VPC subnets\. For more information about VPCs, see [VPC Support for Amazon Elasticsearch Service Domains](es-vpc.md)\.

The following illustration shows a four\-node cluster with zone awareness enabled\. The service distributes the shards so that no replica shard is in the same Availability Zone as its corresponding primary shard\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/zone-awareness.png)

If one Availability Zone \(AZ\) experiences a service interruption, you have a 50/50 chance of cluster downtime due to how [master node](es-managedomains-dedicatedmasternodes.md) election works\. For example, if you use the recommended three dedicated master nodes, Amazon ES distributes two dedicated master nodes into one AZ and one dedicated master node into the other\. If the AZ with two dedicated master nodes experiences an interruption, your cluster is unavailable until the remaining AZ can automatically replace the now\-missing dedicated master nodes, achieve a quorum, and elect a new master\.

Further, if one AZ experiences an interruption, the cluster's data nodes might experience a period of extreme load while Amazon ES automatically configures new nodes to replace the now\-missing ones\. Suddenly, half as many nodes have to process just as many requests to the cluster\. As they process these requests, the remaining nodes are also struggling to replicate data onto new nodes as they come online\. A cluster with extra resources can alleviate this concern\.

**To enable zone awareness \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose your Amazon ES domain\.

1. Choose **Configure cluster**\.

1. In the **Node configuration** pane, choose **Enable zone awareness**\.

1. Choose **Submit**\.

For more information, see [Regions and Availability Zones](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-regions-availability-zones.html) in the EC2 documentation\.

## Monitoring Cluster Metrics and Statistics with Amazon CloudWatch \(Console\)<a name="es-managedomains-cloudwatchmetrics"></a>

Amazon ES domains send performance metrics to Amazon CloudWatch every minute\. If you use general purpose or magnetic EBS volumes, the EBS volume metrics only update every five minutes\. Use the **Monitoring** tab in the Amazon Elasticsearch Service console to view these metrics, provided at no extra charge\.

Statistics provide you with broader insight into each metric\. For example, view the **Average** statistic for the **CPUUtilization** metric to compute the average CPU utilization for all nodes in the cluster\. Each of the metrics falls into one of three categories:
+ [Cluster metrics](#es-managedomains-cloudwatchmetrics-cluster-metrics)
+ [Dedicated master node metrics](#es-managedomains-cloudwatchmetrics-master-node-metrics)
+ [EBS volume metrics](#es-managedomains-cloudwatchmetrics-master-ebs-metrics)

**Note**  
The service archives the metrics for two weeks before discarding them\.

**To view configurable statistics for a metric \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose your Amazon ES domain\.

1. Choose the **Monitoring** tab\.

1. Choose the metric that you want to view\.

1. From the **Statistic** list, select a statistic\.

   For a list of relevant statistics for each metric, see the tables in [Cluster Metrics](#es-managedomains-cloudwatchmetrics-cluster-metrics)\. Some statistics are not relevant for a given metric\. For example, the **Sum** statistic is not meaningful for the **Nodes** metric\.

1. Choose **Update graph**\.

### Cluster Metrics<a name="es-managedomains-cloudwatchmetrics-cluster-metrics"></a>

**Note**  
To check your cluster metrics if metrics are unavailable in the Amazon Elasticsearch Service console, use Amazon CloudWatch\.

The `AWS/ES` namespace includes the following metrics for clusters\.


| Metric | Description | 
| --- | --- | 
| ClusterStatus\.green | Indicates that all index shards are allocated to nodes in the cluster\. Relevant statistics: Minimum, Maximum | 
| ClusterStatus\.yellow | Indicates that the primary shards for all indices are allocated to nodes in a cluster, but the replica shards for at least one index are not\. Single node clusters always initialize with this cluster status because there is no second node to which a replica can be assigned\. You can either increase your node count to obtain a green cluster status, or you can use the Elasticsearch API to set the number\_of\_replicas setting for your index to 0\. To learn more, see [Configuring Amazon Elasticsearch Service Domains](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-createupdatedomains.html#es-createdomains-configure-cluster)\.Relevant statistics: Minimum, Maximum | 
| ClusterStatus\.red | Indicates that the primary and replica shards of at least one index are not allocated to nodes in a cluster\. To recover, you must delete the indices or restore a snapsnot and then add EBS\-based storage, use larger instance types, or add instances\. For more information, see [Red Cluster Status](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-handling-errors.html#aes-handling-errors-red-cluster-status)\. Relevant statistics: Minimum, Maximum | 
| Nodes | The number of nodes in the Amazon ES cluster\. Relevant Statistics: Minimum, Maximum, Average | 
| SearchableDocuments | The total number of searchable documents across all indices in the cluster\. Relevant statistics: Minimum, Maximum, Average | 
| DeletedDocuments | The total number of documents marked for deletion across all indices in the cluster\. These documents no longer appear in search results, but Elasticsearch only removes deleted documents from disk during segment merges\. This metric increases after delete requests and decreases after segment merges\. Relevant statistics: Minimum, Maximum, Average | 
| CPUUtilization | The maximum percentage of CPU resources used for data nodes in the cluster\. Relevant statistics: Maximum, Average | 
| FreeStorageSpace | The free space, in megabytes, for nodes in the cluster\. `Sum` shows total free space for the cluster\. `Minimum`, `Maximum`, and `Average` show free space for individual nodes\. Amazon ES throws a `ClusterBlockException` when this metric reaches `0`\. To recover, you must either delete indices, add larger instances, or add EBS\-based storage to existing instances\. To learn more, see [Recovering from a Lack of Free Storage Space](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-handling-errors.html#aes-handling-errors-watermark) `FreeStorageSpace` will always be lower than the value that the Elasticsearch `_cluster/stats` API provides\. Amazon ES reserves a percentage of the storage space on each instance for internal operations\. Relevant statistics: Minimum, Maximum, Average, Sum | 
| ClusterUsedSpace | The total used space, in megabytes, for a cluster\. You can view this metric in the Amazon CloudWatch console, but not in the Amazon ES console\. Relevant statistics: Minimum, Maximum | 
| ClusterIndexWritesBlocked | Indicates whether your cluster is accepting or blocking incoming write requests\. A value of 0 means that the cluster is accepting requests\. A value of 1 means that it is blocking requests\. Many factors can cause a cluster to begin blocking requests\. Some common factors include the following: `FreeStorageSpace` is too low, `JVMMemoryPressure` is too high, or `CPUUtilization` is too high\. To alleviate this issue, consider adding more disk space or scaling your cluster\. Relevant statistics: Maximum You can view this metric in the Amazon CloudWatch console, but not the Amazon ES console\. | 
| JVMMemoryPressure | The maximum percentage of the Java heap used for all data nodes in the cluster\. Relevant statistics: Maximum | 
| AutomatedSnapshotFailure | The number of failed automated snapshots for the cluster\. A value of `1` indicates that no automated snapshot was taken for the domain in the previous 36 hours\. Relevant statistics: Minimum, Maximum | 
| CPUCreditBalance | The remaining CPU credits available for data nodes in the cluster\. A CPU credit provides the performance of a full CPU core for one minute\. For more information, see [CPU Credits](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/t2-instances.html#t2-instances-cpu-credits) in the *Amazon EC2 Developer Guide*\. This metric is available only for the t2\.micro\.elasticsearch, t2\.small\.elasticsearch, and t2\.medium\.elasticsearch instance types\. Relevant statistics: Minimum | 
| KibanaHealthyNodes | A health check for Kibana\. A value of 1 indicates normal behavior\. A value of 0 indicates that Kibana is inaccessible\. In most cases, the health of Kibana mirrors the health of the cluster\. Relevant statistics: Minimum You can view this metric on the Amazon CloudWatch console, but not the Amazon ES console\. | 
| KMSKeyError | A value of 1 indicates that the KMS customer master key used to encrypt data at rest has been disabled\. To restore the domain to normal operations, re\-enable the key\. The console displays this metric only for domains that encrypt data at rest\.\. Relevant statistics: Minimum, Maximum | 
| KMSKeyInaccessible | A value of 1 indicates that the KMS customer master key used to encrypt data at rest has been deleted or revoked its grants to Amazon ES\. You can't recover domains that are in this state\. If you have a manual snapshot, though, you can use it to migrate the domain's data to a new domain\. The console displays this metric only for domains that encrypt data at rest\. Relevant statistics: Minimum, Maximum | 

The following screenshot shows the cluster metrics that are described in the preceding table\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/MonitoringTab3.png)

### Dedicated Master Node Metrics<a name="es-managedomains-cloudwatchmetrics-master-node-metrics"></a>

The `AWS/ES` namespace includes the following metrics for dedicated master nodes\.


| Metric | Description | 
| --- | --- | 
| MasterCPUUtilization | The maximum percentage of CPU resources used by the dedicated master nodes\. We recommend increasing the size of the instance type when this metric reaches 60 percent\. Relevant statistics: Average | 
| MasterFreeStorageSpace | This metric is not relevant and can be ignored\. The service does not use master nodes as data nodes\. | 
| MasterJVMMemoryPressure | The maximum percentage of the Java heap used for all dedicated master nodes in the cluster\. We recommend moving to a larger instance type when this metric reaches 85 percent\. Relevant statistics: Maximum | 
| MasterCPUCreditBalance | The remaining CPU credits available for dedicated master nodes in the cluster\. A CPU credit provides the performance of a full CPU core for one minute\. For more information, see [CPU Credits](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/t2-instances.html#t2-instances-cpu-credits) in the *Amazon EC2 User Guide for Linux Instances*\. This metric is available only for the t2\.micro\.elasticsearch, t2\.small\.elasticsearch, and t2\.medium\.elasticsearch instance types\. Relevant statistics: Minimum | 
| MasterReachableFromNode | A health check for `MasterNotDiscovered` exceptions\. A value of 1 indicates normal behavior\. A value of 0 indicates that `/_cluster/health/` is failing\. Failures mean that the master node stopped or is not reachable\. They are usually the result of a network connectivity issue or AWS dependency problem\. Relevant statistics: Minimum You can view this metric on the Amazon CloudWatch console, but not the Amazon ES console\. | 

The following screenshot shows the dedicated master nodes metrics that are described in the preceding table\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/MonitoringTabMasterMetrics2.png)

### EBS Volume Metrics<a name="es-managedomains-cloudwatchmetrics-master-ebs-metrics"></a>

The `AWS/ES` namespace includes the following metrics for EBS volumes\.


| Metric | Description | 
| --- | --- | 
| ReadLatency | The latency, in seconds, for read operations on EBS volumes\. Relevant statistics: Minimum, Maximum, Average | 
| WriteLatency | The latency, in seconds, for write operations on EBS volumes\. Relevant statistics: Minimum, Maximum, Average | 
| ReadThroughput | The throughput, in bytes per second, for read operations on EBS volumes\. Relevant statistics: Minimum, Maximum, Average | 
| WriteThroughput | The throughput, in bytes per second, for write operations on EBS volumes\. Relevant statistics: Minimum, Maximum, Average | 
| DiskQueueDepth | The number of pending input and output \(I/O\) requests for an EBS volume\. Relevant statistics: Minimum, Maximum, Average | 
| ReadIOPS | The number of input and output \(I/O\) operations per second for read operations on EBS volumes\. Relevant statistics: Minimum, Maximum, Average | 
| WriteIOPS | The number of input and output \(I/O\) operations per second for write operations on EBS volumes\. Relevant statistics: Minimum, Maximum, Average | 

The following screenshot shows the EBS volume metrics that are described in the preceding table\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/MonitoringTabEBSMetrics2.png)

## Auditing Amazon Elasticsearch Service Domains with AWS CloudTrail<a name="es-managedomains-cloudtrailauditing"></a>

Amazon Elasticsearch Service \(Amazon ES\) is integrated with AWS CloudTrail, a service that logs all AWS API calls made by, or on behalf of, your AWS account\. The log files are delivered to an Amazon S3 bucket that you create and configure with a bucket policy that grants CloudTrail permissions to write log files to the bucket\. CloudTrail captures all Amazon ES configuration service API calls, including those submitted by the Amazon Elasticsearch Service console\. 

You can use the information collected by CloudTrail to monitor activity for your search domains\. You can determine the request that was made to Amazon ES, the source IP address from which the request was made, who made the request, and when it was made\. To learn more about CloudTrail, including how to configure and enable it, see the [AWS CloudTrail User Guide](http://docs.aws.amazon.com/awscloudtrail/latest/userguide/)\. To learn more about how to create and configure an S3 bucket for CloudTrail, see [Amazon S3 Bucket Policy for CloudTrail](https://docs.aws.amazon.com/awscloudtrail/latest/userguide/create-s3-bucket-policy-for-cloudtrail.html)\.

**Note**  
CloudTrail logs events only for configuration\-related API calls to Amazon Elasticsearch Service\. Data\-related APIs are not logged\.

The following example shows a sample CloudTrail log for Amazon ES:

```
{
    "Records": [
        {
            "eventVersion": "1.03",
            "userIdentity": {
                "type": "Root",
                "principalId": "000000000000",
                "arn": "arn:aws:iam::000000000000:root",
                "accountId": "000000000000",
                "accessKeyId": "A*****************A"
            },
            "eventTime": "2015-07-31T21:28:06Z",
            "eventSource": "es.amazonaws.com",
            "eventName": "CreateElasticsearchDomain",
            "awsRegion": "us-east-1",
            "sourceIPAddress": "Your IP",
            "userAgent": "es/test",
            "requestParameters": {
                "elasticsearchClusterConfig": {},
                "snapshotOptions": {
                    "automatedSnapshotStartHour": "0"
                },
                "domainName": "your-domain-name",
                "eBSOptions": {
                    "eBSEnabled": false
                }
            },
            "responseElements": {
                "domainStatus": {
                    "created": true,
                    "processing": true,
                    "aRN": "arn:aws:es:us-east-1:000000000000:domain/your-domain-name",
                    "domainId": "000000000000/your-domain-name",
                    "elasticsearchClusterConfig": {
                        "zoneAwarenessEnabled": false,
                        "instanceType": "m3.medium.elasticsearch",
                        "dedicatedMasterEnabled": false,
                        "instanceCount": 1
                    },
                    "deleted": false,
                    "domainName": "your-domain-name",
                    "domainVersion": "1.5", 
                    "accessPolicies": "",
                    "advancedOptions": {
                        "rest.action.multi.allow_explicit_index": "true"
                    },
                    "snapshotOptions": {
                        "automatedSnapshotStartHour": "0"
                    },
                    "eBSOptions": {
                        "eBSEnabled": false
                    }
                }
            },
            "requestID": "05dbfc84-37cb-11e5-a2cd-fbc77a4aae72",
            "eventID": "c21da94e-f5ed-41a4-8703-9a5f49e2ec85",
            "eventType": "AwsApiCall",
            "recipientAccountId": "000000000000"
        }
    ]
}
```

### Amazon Elasticsearch Service Information in CloudTrail<a name="cloudsearch-info-in-cloudtrail"></a>

When CloudTrail logging is enabled in your AWS account, API calls made to Amazon Elasticsearch Service \(Amazon ES\) operations are tracked in log files\. Amazon ES records are written together with other AWS service records in a log file\. CloudTrail determines when to create and write to a new file based on a time period and file size\. 

All Amazon ES configuration service operations are logged\. For example, calls to `CreateElasticsearchDomain`, `DescribeElasticsearchDomain`, and `UpdateElasticsearchDomainConfig` generate entries in the CloudTrail log files\. Every log entry contains information about who generated the request\. The user identity information in the log helps you determine whether the request was made with root or IAM user credentials, with temporary security credentials for a role or federated user, or by another AWS service\. For more information, see the `userIdentity` field in the [CloudTrail Event Reference](http://docs.aws.amazon.com/awscloudtrail/latest/userguide/event_reference_top_level.html)\.

You can store your log files in your bucket indefinitely, or you can define Amazon S3 lifecycle rules to archive or delete log files automatically\. By default, your log files are encrypted using Amazon S3 server\-side encryption \(SSE\)\. You can choose to have CloudTrail publish Amazon SNS notifications when new log files are delivered if you want to take quick action upon log file delivery\. For more information, see [Configuring Amazon SNS Notifications for CloudTrail](http://docs.aws.amazon.com/awscloudtrail/latest/userguide/getting_notifications_top_level.html)\. You also can aggregate Amazon ES log files from multiple AWS Regions and multiple AWS accounts into a single Amazon S3 bucket\. For more information, see [Receiving CloudTrail Log Files from Multiple Regions](http://docs.aws.amazon.com/awscloudtrail/latest/userguide/aggregating_logs_top_level.html)\.

### Understanding Amazon Elasticsearch Service Log File Entries<a name="understanding-cloudsearch-event-entries"></a>

CloudTrail log files contain one or more log entries where each entry is made up of multiple JSON\-formatted events\. A log entry represents a single request from any source and includes information about the requested action, any parameters, the date and time of the action, and so on\. The log entries are not guaranteed to be in any particular order—they are not an ordered stack trace of the public API calls\. CloudTrail log files include events for all AWS API calls for your AWS account, not just calls to the Amazon ES configuration service API\. However, you can read the log files and scan for `eventSource` `es.amazonaws.com`\. The `eventName` element contains the name of the configuration service action that was called\.

## Tagging Amazon Elasticsearch Service Domains<a name="es-managedomains-awsresourcetagging"></a>

You can use Amazon ES tags to add metadata to your Amazon ES domains\. AWS does not apply any semantic meaning to your tags\. Tags are interpreted strictly as character strings\. All tags have the following elements\.


****  

| Tag Element | Description | 
| --- | --- | 
| Tag key | The tag key is the required name of the tag\. Tag keys must be unique for the Amazon ES domain to which they are attached\. For a list of basic restrictions on tag keys and values, see [User\-Defined Tag Restrictions](http://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/allocation-tag-restrictions.html)\. | 
| Tag value | The tag value is an optional string value of the tag\. Tag values can be null and do not have to be unique in a tag set\. For example, you can have a key\-value pair in a tag set of project/Trinity and cost\-center/Trinity\. For a list of basic restrictions on tag keys and values, see [User\-Defined Tag Restrictions](http://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/allocation-tag-restrictions.html)\. | 

Each Amazon ES domain has a tag set, which contains all the tags that are assigned to that Amazon ES domain\. AWS does not automatically set any tags on Amazon ES domains\. A tag set can contain up to 50 tags, or it can be empty\. If you add a tag to an Amazon ES domain that has the same key as an existing tag for a resource, the new value overwrites the old value\. 

You can use these tags to track costs by grouping expenses for similarly tagged resources\. An Amazon ES domain tag is a name\-value pair that you define and associate with an Amazon ES domain\. The name is referred to as the *key*\. You can use tags to assign arbitrary information to an Amazon ES domain\. A tag key could be used, for example, to define a category, and the tag value could be an item in that category\. For example, you could define a tag key of “project” and a tag value of “Salix,” indicating that the Amazon ES domain is assigned to the Salix project\. You could also use tags to designate Amazon ES domains as being used for test or production by using a key such as environment=test or environment=production\. We recommend that you use a consistent set of tag keys to make it easier to track metadata that is associated with Amazon ES domains\. 

You also can use tags to organize your AWS bill to reflect your own cost structure\. To do this, sign up to get your AWS account bill with tag key values included\. Then, organize your billing information according to resources with the same tag key values to see the cost of combined resources\. For example, you can tag several Amazon ES domains with key\-value pairs, and then organize your billing information to see the total cost for each domain across several services\. For more information, see [Using Cost Allocation Tags](http://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/cost-alloc-tags.html) in the *AWS Billing and Cost Management* documentation\.

**Note**  
Tags are cached for authorization purposes\. Because of this, additions and updates to tags on Amazon ES domains might take several minutes before they are available\.

### Working with Tags \(Console\)<a name="es-managedomains-awsresourcetagging-console"></a>

Use the following procedure to create a resource tag\.

**To create a tag \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, choose your Amazon ES domain\.

1. On the domain dashboard, choose **Manage tags**\.

1. In the **Key** column, type a tag key\.

1. \(Optional\) In the **Value** column, type a tag value\.

1. Choose **Submit**\.

**To delete a tag \(console\)**

Use the following procedure to delete a resource tag\.

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, choose your Amazon ES domain\.

1. On the domain dashboard, choose **Manage tags**\.

1. Next to the tag that you want to delete, choose **Remove**\.

1. Choose **Submit**\.

For more information about using the console to work with tags, see [Working with Tag Editor](https://docs.aws.amazon.com/awsconsolehelpdocs/latest/gsg/tag-editor.html) in the *AWS Management Console Getting Started Guide*\.

### Working with Tags \(AWS CLI\)<a name="es-managedomains-awsresourcetagging-cli"></a>

You can create resource tags using the AWS CLI with the \-\-add\-tags command\. 

**Syntax**

`add-tags --arn=<domain_arn> --tag-list Key=<key>,Value=<value>`


****  

| Parameter | Description | 
| --- | --- | 
| \-\-arn | Amazon resource name for the Amazon ES domain to which the tag is attached\. | 
| \-\-tag\-list | Set of space\-separated key\-value pairs in the following format: Key=<key>,Value=<value> | 

**Example**

The following example creates two tags for the *logs* domain:

```
aws es add-tags --arn arn:aws:es:us-east-1:379931976431:domain/logs --tag-list Key=service,Value=Elasticsearch Key=instances,Value=m3.2xlarge
```

You can remove tags from an Amazon ES domain using the remove\-tags command\. 

** Syntax **

`remove-tags --arn=<domain_arn> --tag-keys Key=<key>,Value=<value>`


****  

| Parameter | Description | 
| --- | --- | 
| \-\-arn | Amazon Resource Name \(ARN\) for the Amazon ES domain to which the tag is attached\. | 
| \-\-tag\-keys | Set of space\-separated key\-value pairs that you want to remove from the Amazon ES domain\. | 

**Example**

The following example removes two tags from the *logs* domain that were created in the preceding example:

```
aws es remove-tags --arn arn:aws:es:us-east-1:379931976431:domain/logs --tag-keys service instances
```

You can view the existing tags for an Amazon ES domain with the list\-tags command:

**Syntax**

`list-tags --arn=<domain_arn>`


****  

| Parameter | Description | 
| --- | --- | 
| \-\-arn | Amazon Resource Name \(ARN\) for the Amazon ES domain to which the tags are attached\. | 

**Example**

The following example lists all resource tags for the *logs* domain:

```
aws es list-tags --arn arn:aws:es:us-east-1:379931976431:domain/logs
```

### Working with Tags \(AWS SDKs\)<a name="es-managedomains-awsresourcetagging-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all the actions defined in the [Amazon ES Configuration API Reference](es-configuration-api.md), including the `AddTags`, `ListTags`, and `RemoveTags` operations\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\. 