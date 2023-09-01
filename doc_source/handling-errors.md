# Troubleshooting Amazon OpenSearch Service<a name="handling-errors"></a>

This topic describes how to identify and solve common Amazon OpenSearch Service issues\. Consult the information in this section before contacting [AWS Support](https://aws.amazon.com/premiumsupport/)\.

## Can't access OpenSearch Dashboards<a name="troubleshooting-dashboards-configure-anonymous-access"></a>

The OpenSearch Dashboards endpoint doesn't support signed requests\. If the access control policy for your domain only grants access to certain IAM roles and you haven't configured [Amazon Cognito authentication](cognito-auth.md), you might receive the following error when you attempt to access Dashboards:

```
"User: anonymous is not authorized to perform: es:ESHttpGet"
```

If your OpenSearch Service domain uses VPC access, you might not receive this error, but the request might time out\. To learn more about correcting this issue and the various configuration options available to you, see [Controlling access to OpenSearch Dashboards](dashboards.md#dashboards-access), [About access policies on VPC domains](vpc.md#vpc-security), and [Identity and Access Management in Amazon OpenSearch Service](ac.md)\.

## Can't access VPC domain<a name="troubleshooting-vpc-domain"></a>

See [About access policies on VPC domains](vpc.md#vpc-security) and [Testing VPC domains](vpc.md#vpc-test)\.

## Cluster in read\-only state<a name="troubleshooting-read-only-cluster"></a>

Compared to earlier Elasticsearch versions, OpenSearch and Elasticsearch 7\.*x* use a different system for cluster coordination\. In this new system, when the cluster loses quorum, the cluster is unavailable until you take action\. Loss of quorum can take two forms:
+ If your cluster uses dedicated master nodes, quorum loss occurs when half or more are unavailable\.
+ If your cluster does not use dedicated master nodes, quorum loss occurs when half or more of your data nodes are unavailable\.

If quorum loss occurs and your cluster has more than one node, OpenSearch Service restores quorum and places the cluster into a read\-only state\. You have two options:
+ Remove the read\-only state and use the cluster as\-is\.
+ [Restore the cluster or individual indexes from a snapshot](managedomains-snapshots.md#managedomains-snapshot-restore)\.

If you prefer to use the cluster as\-is, verify that cluster health is green using the following request:

```
GET _cat/health?v
```

If cluster health is red, we recommend restoring the cluster from a snapshot\. You can also see [Red cluster status](#handling-errors-red-cluster-status) for troubleshooting steps\. If cluster health is green, check that all expected indexes are present using the following request:

```
GET _cat/indices?v
```

Then run some searches to verify that the expected data is present\. If it is, you can remove the read\-only state using the following request:

```
PUT _cluster/settings
{
  "persistent": {
    "cluster.blocks.read_only": false
  }
}
```

If quorum loss occurs and your cluster has only one node, OpenSearch Service replaces the node and does *not* place the cluster into a read\-only state\. Otherwise, your options are the same: use the cluster as\-is or restore from a snapshot\.

In both situations, OpenSearch Service sends two events to your [AWS Health Dashboard](https://phd.aws.amazon.com/phd/home#/)\. The first informs you of the loss of quorum\. The second occurs after OpenSearch Service successfully restores quorum\. For more information about using the AWS Health Dashboard, see the [AWS Health User Guide](https://docs.aws.amazon.com/health/latest/ug/)\.

## Red cluster status<a name="handling-errors-red-cluster-status"></a>

A red cluster status means that at least one primary shard and its replicas are not allocated to a node\. OpenSearch Service keeps trying to take automated snapshots of all indexes regardless of their status, but the snapshots fail while the red cluster status persists\.

The most common causes of a red cluster status are [failed cluster nodes](#handling-errors-failed-cluster-nodes) and the OpenSearch process crashing due to a continuous heavy processing load\.

**Note**  
OpenSearch Service stores automated snapshots for 14 days regardless of the cluster status\. Therefore, if the red cluster status persists for more than two weeks, the last healthy automated snapshot will be deleted and you could permanently lose your cluster's data\. If your OpenSearch Service domain enters a red cluster status, AWS Support might contact you to ask whether you want to address the problem yourself or you want the support team to assist\. You can [set a CloudWatch alarm](cloudwatch-alarms.md) to notify you when a red cluster status occurs\.

Ultimately, red shards cause red clusters, and red indexes cause red shards\. To identify the indexes causing the red cluster status, OpenSearch has some helpful APIs\.
+ `GET /_cluster/allocation/explain` chooses the first unassigned shard that it finds and explains why it cannot be allocated to a node:

  ```
  {
      "index": "test4",
      "shard": 0,
      "primary": true,
      "current_state": "unassigned",
      "can_allocate": "no",
      "allocate_explanation": "cannot allocate because allocation is not permitted to any of the nodes"
  }
  ```
+ `GET /_cat/indices?v` shows the health status, number of documents, and disk usage for each index:

  ```
  health status index            uuid                   pri rep docs.count docs.deleted store.size pri.store.size
  green  open   test1            30h1EiMvS5uAFr2t5CEVoQ   5   0        820            0       14mb           14mb
  green  open   test2            sdIxs_WDT56afFGu5KPbFQ   1   0          0            0       233b           233b
  green  open   test3            GGRZp_TBRZuSaZpAGk2pmw   1   1          2            0     14.7kb          7.3kb
  red    open   test4            BJxfAErbTtu5HBjIXJV_7A   1   0
  green  open   test5            _8C6MIXOSxCqVYicH3jsEA   1   0          7            0     24.3kb         24.3kb
  ```

Deleting red indexes is the fastest way to fix a red cluster status\. Depending on the reason for the red cluster status, you might then scale your OpenSearch Service domain to use larger instance types, more instances, or more EBS\-based storage and try to recreate the problematic indexes\.

If deleting a problematic index isn't feasible, you can [restore a snapshot](managedomains-snapshots.md#managedomains-snapshot-restore), delete documents from the index, change the index settings, reduce the number of replicas, or delete other indexes to free up disk space\. The important step is to resolve the red cluster status *before* reconfiguring your OpenSearch Service domain\. Reconfiguring a domain with a red cluster status can compound the problem and lead to the domain being stuck in a configuration state of **Processing** until you resolve the status\.

### Automatic remediation of red clusters<a name="handling-errors-red-cluster-status-auto-recovery"></a>

If your cluster's status is continuously red for more than an hour, OpenSearch Service attempts to automatically fix it by rerouting unallocated shards or restoring from past snapshots\.

If it fails to fix one or more red indexes and the cluster status remains red for a total of 14 days, OpenSearch Service takes further action only if the cluster meets *at least one* of the following criteria:
+ Has only one availability zone
+ Has no dedicated master nodes
+ Contains burstable instance types \(T2 or T3\)

At this time, if your cluster meets one of these criteria, OpenSearch Service sends you daily [notifications](managedomains-notifications.md) over the next 7 days explaining that if you don't fix these indexes, all unassigned shards will be deleted\. If your cluster status is still red after 21 days, OpenSearch Service deletes the unassigned shards \(storage and compute\) on all red indexes\. You receive notifications in the **Notifications** panel of the OpenSearch Service console for each of these events\. For more information, see [Cluster health events](monitoring-events.md#monitoring-events-cluster-health)\.

### Recovering from a continuous heavy processing load<a name="handling-errors-red-cluster-status-heavy-processing-load"></a>

To determine if a red cluster status is due to a continuous heavy processing load on a data node, monitor the following cluster metrics\.


| Relevant metric | Description | Recovery | 
| --- | --- | --- | 
| JVMMemoryPressure |  Specifies the percentage of the Java heap used for all data nodes in a cluster\. View the **Maximum** statistic for this metric, and look for smaller and smaller drops in memory pressure as the Java garbage collector fails to reclaim sufficient memory\. This pattern likely is due to complex queries or large data fields\. x86 instance types use the Concurrent Mark Sweep \(CMS\) garbage collector, which runs alongside application threads to keep pauses short\. If CMS is unable to reclaim enough memory during its normal collections, it triggers a full garbage collection, which can lead to long application pauses and impact cluster stability\. ARM\-based Graviton instance types use the Garbage\-First \(G1\) garbage collector, which is similar to CMS, but uses additional short pauses and heap defragmentation to further reduce the need for full garbage collections\. In either case, if memory usage continues to grow beyond what the garbage collector can reclaim during full garbage collections, OpenSearch crashes with an out of memory error\. On all instance types, a good rule of thumb is to keep usage below 80%\. The `_nodes/stats/jvm` API offers a useful summary of JVM statistics, memory pool usage, and garbage collection information: <pre>GET domain-endpoint/_nodes/stats/jvm?pretty</pre>  |  Set memory circuit breakers for the JVM\. For more information, see [JVM OutOfMemoryError](#handling-errors-jvm_out_of_memory_error)\. If the problem persists, delete unnecessary indexes, reduce the number or complexity of requests to the domain, add instances, or use larger instance types\.  | 
| CPUUtilization | Specifies the percentage of CPU resources used for data nodes in a cluster\. View the Maximum statistic for this metric, and look for a continuous pattern of high usage\. | Add data nodes or increase the size of the instance types of existing data nodes\. | 
| Nodes | Specifies the number of nodes in a cluster\. View the Minimum statistic for this metric\. This value fluctuates when the service deploys a new fleet of instances for a cluster\. | Add data nodes\. | 

## Yellow cluster status<a name="handling-errors-yellow-cluster-status"></a>

A yellow cluster status means the primary shards for all indexes are allocated to nodes in a cluster, but the replica shards for at least one index aren't\. Single\-node clusters always initialize with a yellow cluster status because there's no other node to which OpenSearch Service can assign a replica\. To achieve green cluster status, increase your node count\. For more information, see [Sizing Amazon OpenSearch Service domains](sizing-domains.md)\.

Multi\-node clusters might briefly have a yellow cluster status after creating a new index or after a node failure\. This status self\-resolves as OpenSearch replicates data across the cluster\. [Lack of disk space](#handling-errors-watermark) can also cause yellow cluster status; the cluster can only distribute replica shards if nodes have the disk space to accommodate them\.

## ClusterBlockException<a name="troubleshooting-cluster-block"></a>

You might receive a `ClusterBlockException` error for the following reasons\.

### Lack of available storage space<a name="handling-errors-watermark"></a>

If one or more nodes in your cluster has storage space less than the minimum value of 1\) 20% of available storage space, or 2\) 20 GB of storage space, basic write operations like adding documents and creating indexes can start to fail\. [Calculating storage requirements](sizing-domains.md#bp-storage) provides a summary of how OpenSearch Service uses disk space\.

To avoid issues, monitor the `FreeStorageSpace` metric in the OpenSearch Service console and [create CloudWatch alarms](cloudwatch-alarms.md) to trigger when `FreeStorageSpace` drops below a certain threshold\. `GET /_cat/allocation?v` also provides a useful summary of shard allocation and disk usage\. To resolve issues associated with a lack of storage space, scale your OpenSearch Service domain to use larger instance types, more instances, or more EBS\-based storage\.

### High JVM memory pressure<a name="handling-errors-block-disks"></a>

When the **JVMMemoryPressure** metric exceeds 92% for 30 minutes, OpenSearch Service triggers a protection mechanism and blocks all write operations to prevent the cluster from reaching red status\. When the protection is on, write operations fail with a `ClusterBlockException` error, new indexes can't be created, and the `IndexCreateBlockException` error is thrown\.

When the **JVMMemoryPressure** metric returns to 88% or lower for five minutes, the protection is disabled, and write operations to the cluster are unblocked\.

High JVM memory pressure can be caused by spikes in the number of requests to the cluster, unbalanced shard allocations across nodes, too many shards in a cluster, field data or index mapping explosions, or instance types that can't handle incoming loads\. It can also be caused by using aggregations, wildcards, or wide time ranges in queries\.

To reduce traffic to the cluster and resolve high JVM memory pressure issues, try one or more of the following:
+ Scale the domain so that the maximum heap size per node is 32 GB\.
+ Reduce the number of shards by deleting old or unused indexes\.
+ Clear the data cache with the `POST index-name/_cache/clear?fielddata=true` API operation\. Note that clearing the cache can disrupt in\-progress queries\.

In general, to avoid high JVM memory pressure in the future, follow these best practices:
+ Avoid aggregating on text fields, or change the [mapping type](https://opensearch.org/docs/latest/opensearch/mappings/#dynamic-mapping) for your indexes to `keyword`\.
+ Optimize search and indexing requests by [choosing the correct number of shards](sizing-domains.md#bp-sharding)\.
+ Set up Index State Management \(ISM\) policies to regularly [remove unused indexes](bp.md#bp-stability-remove)\.

## Error migrating to Multi\-AZ with Standby<a name="troubleshooting-multi-az-standby"></a>

The following issues might occur when you migrate an existing domain to Multi\-AZ with standby\.

### Creating an index, index template, or ISM policy during migration from domains without standby to domains with standby<a name="index"></a>

If you create an index while migrating a domain from Multi\-AZ without Standby to with Standby, and the index template or ISM policy doesn't follow the recommended data copy guidelines, this can cause a data inconsistency and the migration may fail\. To avoid this situation, create the new index with a data copy count \(including both primary nodes and replicas\) that is multiple of three\. You can check the migratation progress using the `DescribeDomainChangeProgress` API\. If you encounter a replica count error, fix the error and then contact [AWS Support](https://aws.amazon.com/premiumsupport/) to retry the migration\.

### Incorrect number of data copies<a name="data-copy"></a>

If you don't have the right number of data copies in your domain, the migrating to Multi\-AZ with Standby will fail\.

## JVM OutOfMemoryError<a name="handling-errors-jvm_out_of_memory_error"></a>

A JVM `OutOfMemoryError` typically means that one of the following JVM circuit breakers was reached\.


| Circuit breaker | Description | Cluster setting property | 
| --- | --- | --- | 
| Parent Breaker | Total percentage of JVM heap memory allowed for all circuit breakers\. The default value is 95%\. | indices\.breaker\.total\.limit | 
| Field Data Breaker | Percentage of JVM heap memory allowed to load a single data field into memory\. The default value is 40%\. If you upload data with large fields, you might need to raise this limit\. | indices\.breaker\.fielddata\.limit | 
| Request Breaker | Percentage of JVM heap memory allowed for data structures used to respond to a service request\. The default value is 60%\. If your service requests involve calculating aggregations, you might need to raise this limit\. | indices\.breaker\.request\.limit | 

## Failed cluster nodes<a name="handling-errors-failed-cluster-nodes"></a>

Amazon EC2 instances might experience unexpected terminations and restarts\. Typically, OpenSearch Service restarts the nodes for you\. However, it's possible for one or more nodes in an OpenSearch cluster to remain in a failed condition\.

To check for this condition, open your domain dashboard on the OpenSearch Service console\. Go to the **Cluster health** tab and find the **Total nodes** metric\. See if the reported number of nodes is fewer than the number that you configured for your cluster\. If the metric shows that one or more nodes is down for more than one day, contact [AWS Support](https://aws.amazon.com/premiumsupport/)\.

You can also [set a CloudWatch alarm](cloudwatch-alarms.md) to notify you when this issue occurs\.

**Note**  
The **Total nodes** metric is not accurate during changes to your cluster configuration and during routine maintenance for the service\. This behavior is expected\. The metric will report the correct number of cluster nodes soon\. To learn more, see [Making configuration changes in Amazon OpenSearch Service](managedomains-configuration-changes.md)\.

To protect your clusters from unexpected node terminations and restarts, create at least one replica for each index in your OpenSearch Service domain\.

## Exceeded maximum shard limit<a name="troubleshooting-shard-limit"></a>

OpenSearch as well as 7\.*x* versions of Elasticsearch have a default setting of no more than 1,000 shards per node\. OpenSearch/Elasticsearch throw an error if a request, such as creating a new index, would cause you to exceed this limit\. If you encounter this error, you have several options:
+ Add more data nodes to the cluster\.
+ Increase the `_cluster/settings/cluster.max_shards_per_node` setting\.
+ Use the [\_shrink API](supported-operations.md#version_api_notes-shrink) to reduce the number of shards on the node\.

## Domain stuck in processing state<a name="stuck-in-processing"></a>

Your OpenSearch Service domain enters the "Processing" state when it's in the middle of a [configuration change](managedomains-configuration-changes.md)\. When you initiate a configuration change, the domain status changes to "Processing" while OpenSearch Service creates a new environment\. In the new environment, OpenSearch Service launches a new set of applicable nodes \(such as data, master, or UltraWarm\)\. After the migration completes, the older nodes are terminated\.

The cluster can get stuck in the "Processing" state if either of these situations occurs:
+ A new set of data nodes fails to launch\.
+ Shard migration to the new set of data nodes is unsuccessful\.
+ Validation check has failed with errors\.

For detailed resolution steps in each of these situations, see [Why is my Amazon OpenSearch Service domain stuck in the "Processing" state?](https://aws.amazon.com/premiumsupport/knowledge-center/opensearch-domain-stuck-processing/)\. 

## Low EBS burst balance<a name="handling-errors-low-ebs-burst"></a>

OpenSearch Service sends you a console notification when the EBS burst balance on one of your General Purpose \(SSD\) volumes is below 70%, and a follow\-up notification if the balance falls below 20%\. To fix this issue, you can either scale up your cluster, or reduce the read and write IOPS so that the burst balance can be credited\. The burst balance stays at 0 for domains with gp3 volumes types, and domains with gp2 volumes that have a volume size above 1000 GiB\. For more information, see [General Purpose SSD volumes \(gp2\)](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ebs-volume-types.html#EBSVolumeTypes_gp2)\. You can monitor EBS burst balance with the `BurstBalance` CloudWatch metric\.

## Can't enable audit logs<a name="troubleshooting-audit-logs-error"></a>

You might encounter the following error when you try to enable audit log publishing using the OpenSearch Service console:

The Resource Access Policy specified for the CloudWatch Logs log group does not grant sufficient permissions for Amazon OpenSearch Service to create a log stream\. Please check the Resource Access Policy\.

If you encounter this error, verify that the `resource` element of your policy includes the correct log group ARN\. If it does, take the following steps:

1. Wait several minutes\.

1. Refresh the page in your web browser\.

1. Choose **Select existing group**\.

1. For **Existing log group**, choose the log group that you created before receiving the error message\.

1. In the access policy section, choose **Select existing policy**\.

1. For **Existing policy**, choose the policy that you created before receiving the error message\.

1. Choose **Enable**\.

If the error persists after repeating the process several times, contact [AWS Support](https://aws.amazon.com/premiumsupport/)\.

## Can't close index<a name="troubleshooting-close-api"></a>

OpenSearch Service supports the [https://opensearch.org/docs/latest/api-reference/index-apis/close-index/](https://opensearch.org/docs/latest/api-reference/index-apis/close-index/) API only for OpenSearch and Elasticsearch versions 7\.4 and later\. If you're using an older version and are restoring an index from a snapshot, you can delete the existing index \(before or after reindexing it\)\.

## Client license checks<a name="troubleshooting-license"></a>

The default distributions of Logstash and Beats include a proprietary license check and fail to connect to the open source version of OpenSearch\. Make sure you use the Apache 2\.0 \(OSS\) distributions of these clients with OpenSearch Service\.

## Request throttling<a name="troubleshooting-throttle-api"></a>

If you receive persistent `403 Request throttled due to too many requests` or `429 Too Many Requests` errors, consider scaling vertically\. Amazon OpenSearch Service throttles requests if the payload would cause memory usage to exceed the maximum size of the Java heap\.

## Can't SSH into node<a name="troubleshooting-ssh"></a>

You can't use SSH to access any of the nodes in your OpenSearch cluster, and you can't directly modify `opensearch.yml`\. Instead, use the console, AWS CLI, or SDKs to configure your domain\. You can specify a few cluster\-level settings using the OpenSearch REST APIs, as well\. To learn more, see the [Amazon OpenSearch Service API Reference](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/Welcome.html) and [Supported operations in Amazon OpenSearch Service](supported-operations.md)\.

If you need more insight into the performance of the cluster, you can [publish error logs and slow logs to CloudWatch](createdomain-configure-slow-logs.md)\.

## "Not Valid for the Object's Storage Class" snapshot error<a name="troubleshooting-glacier-snapshots"></a>

OpenSearch Service snapshots do not support the S3 Glacier storage class\. You might encounter this error when you attempt to list snapshots if your S3 bucket includes a lifecycle rule that transitions objects to the S3 Glacier storage class\.

If you need to restore a snapshot from the bucket, restore the objects from S3 Glacier, copy the objects to a new bucket, and [register the new bucket](managedomains-snapshots.md#managedomains-snapshot-registerdirectory) as a snapshot repository\.

## Invalid host header<a name="troubleshooting-host-header"></a>

OpenSearch Service requires that clients specify `Host` in the request headers\. A valid `Host` value is the domain endpoint without `https://`, such as:

```
Host: search-my-sample-domain-ih2lhn2ew2scurji.us-west-2.es.amazonaws.com
```

If you receive an `Invalid Host Header` error when making a request, check that your client or proxy includes the OpenSearch Service domain endpoint \(and not, for example, its IP address\) in the `Host` header\.

## Invalid M3 instance type<a name="m3-instance-types"></a>

OpenSearch Service doesn't support adding or modifying M3 instances to existing domains running OpenSearch or Elasticsearch versions 6\.7 and later\. You can continue to use M3 instances with Elasticsearch 6\.5 and earlier\.

We recommend choosing a newer instance type\. For domains running OpenSearch or Elasticsearch 6\.7 or later, the following restriction apply:
+ If your existing domain does not use M3 instances, you can no longer change to them\.
+ If you change an existing domain from an M3 instance type to another instance type, you can't switch back\.

## Hot queries stop working after enabling UltraWarm<a name="troubleshooting-uw"></a>

When you enable UltraWarm on a domain, if there are no preexisting overrides to the `search.max_buckets` setting, OpenSearch Service automatically sets the value to `10000` to prevent memory\-heavy queries from saturating warm nodes\. If your hot queries are using more than 10,000 buckets, they might stop working when you enable UltraWarm\. 

Because you can't modify this setting due to the managed nature of Amazon OpenSearch Service, you need to open a support case to increase the limit\. Limit increases don't require a premium support subscription\.

## Can't downgrade after upgrade<a name="troubleshooting-upgrade-snapshot"></a>

[In\-place upgrades](version-migration.md) are irreversible, but if you contact [AWS Support](https://aws.amazon.com/premiumsupport/), they can help you restore the automatic, pre\-upgrade snapshot on a new domain\. For example, if you upgrade a domain from Elasticsearch 5\.6 to 6\.4, AWS Support can help you restore the pre\-upgrade snapshot on a new Elasticsearch 5\.6 domain\. If you took a manual snapshot of the original domain, you can [perform that step yourself](managedomains-snapshots.md)\.

## Need summary of domains for all AWS Regions<a name="troubleshooting-domain-summary"></a>

The following script uses the Amazon EC2 [describe\-regions](https://docs.aws.amazon.com/cli/latest/reference/ec2/describe-regions.html) AWS CLI command to create a list of all Regions in which OpenSearch Service could be available\. Then it calls [list\-domain\-names](https://docs.aws.amazon.com/cli/latest/reference/es/list-domain-names.html) for each Region:

```
for region in `aws ec2 describe-regions --output text | cut -f4`
do
    echo "\nListing domains in region '$region':"
    aws opensearch list-domain-names --region $region --query 'DomainNames'
done
```

You receive the following output for each Region:

```
Listing domains in region:'us-west-2'...
[
  {
    "DomainName": "sample-domain"
  }
]
```

Regions in which OpenSearch Service is not available return "Could not connect to the endpoint URL\."

## Browser error when using OpenSearch Dashboards<a name="troubleshooting-dashboards-debug-browser-errors"></a>

Your browser wraps service error messages in HTTP response objects when you use Dashboards to view data in your OpenSearch Service domain\. You can use developer tools commonly available in web browsers, such as Developer Mode in Chrome, to view the underlying service errors and assist your debugging efforts\.

**To view service errors in Chrome**

1. From the Chrome top menu bar, choose **View**, **Developer**, **Developer Tools**\.

1. Choose the **Network** tab\.

1. In the **Status** column, choose any HTTP session with a status of 500\.

**To view service errors in Firefox**

1. From the menu, choose **Tools**, **Web Developer**, **Network**\.

1. Choose any HTTP session with a status of 500\.

1. Choose the **Response** tab to view the service response\.

## Node shard and storage skew<a name="handling-errors-node-skew"></a>

Node *shard skew* is when one or more nodes within a cluster has significantly more shards than the other nodes\. Node *storage skew* is when one or more nodes within a cluster has significantly more storage \(`disk.indices`\) than the other nodes\. While both of these conditions can occur temporarily, like when a domain has replaced a node and is still allocating shards to it, you should address them if they persist\.

To identify both types of skew, run the [\_cat/allocation](https://opensearch.org/docs/latest/opensearch/rest-api/cat/cat-allocation/) API operation and compare the `shards` and `disk.indices` entries in the response:

```
 shards    | disk.indices  | disk.used  | disk.avail   | disk.total   | disk.percent |  host     | ip       | node
    264    |      465.3mb  |   229.9mb  |      1.4tb   |      1.5tb   |            0 |  x.x.x.x  | x.x.x.x  | node1
    115    |        7.9mb  |    83.7mb  |     49.1gb   |     49.2gb   |            0 |  x.x.x.x  | x.x.x.x  | node2
    264    |      465.3mb  |   235.3mb  |      1.4tb   |      1.5tb   |            0 |  x.x.x.x  | x.x.x.x  | node3
    116    |        7.9mb  |    82.8mb  |     49.1gb   |     49.2gb   |            0 |  x.x.x.x  | x.x.x.x  | node4
    115    |        8.4mb  |      85mb  |     49.1gb   |     49.2gb   |            0 |  x.x.x.x  | x.x.x.x  | node5
```

While some storage skew is normal, anything over 10% from the average is significant\. When shard distribution is skewed, CPU, network, and disk bandwidth usage can also become skewed\. Because more data generally means more indexing and search operations, the heaviest nodes also tend to be the most resource\-strained nodes, while the lighter nodes represent underutilized capacity\.

**Remediation**: Use shard counts that are multiples of the data node count to ensure that each index is distributed evenly across data nodes\.

## Index shard and storage skew<a name="handling-errors-index-skew"></a>

Index *shard skew* is when one or more nodes hold more of an index's shards than the other nodes\. Index *storage skew* is when one or more nodes hold a disproportionately large amount of an index's total storage\.

Index skew is harder to identify than node skew because it requires some manipulation of the [\_cat/shards](https://opensearch.org/docs/latest/opensearch/rest-api/cat/cat-shards/) API output\. Investigate index skew if there's some indication of skew in the cluster or node metrics\. The following are common indications of index skew:
+ HTTP 429 errors occurring on a subset of data nodes
+ Uneven index or search operation queueing across data nodes
+ Uneven JVM heap and/or CPU utilization across data nodes

**Remediation**: Use shard counts that are multiples of the data node count to ensure that each index is distributed evenly across data nodes\. If you still see index storage or shard skew, you might need to force a shard reallocation, which occurs with every [blue/green deployment](managedomains-configuration-changes.md) of your OpenSearch Service domain\.

## Unauthorized operation after selecting VPC access<a name="vpc-permissions"></a>

When you create a new domain using the OpenSearch Service console, you have the option to select VPC or public access\. If you select **VPC access**, OpenSearch Service queries for VPC information and fails if you don't have the proper permissions:

```
You are not authorized to perform this operation. (Service: AmazonEC2; Status Code: 403; Error Code: UnauthorizedOperation
```

To enable this query, you must have access to the `ec2:DescribeVpcs`, `ec2:DescribeSubnets`, and `ec2:DescribeSecurityGroups` operations\. This requirement is only for the console\. If you use the AWS CLI to create and configure a domain with a VPC endpoint, you don't need access to those operations\.

## Stuck at loading after creating VPC domain<a name="vpc-sts"></a>

After creating a new domain that uses VPC access, the domain's **Configuration state** might never progress beyond **Loading**\. If this issue occurs, you likely have AWS Security Token Service \(AWS STS\) *disabled* for your Region\.

To add VPC endpoints to your VPC, OpenSearch Service needs to assume the `AWSServiceRoleForAmazonOpenSearchService` role\. Thus, AWS STS must be enabled to create new domains that use VPC access in a given Region\. To learn more about enabling and disabling AWS STS, see the [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_temp_enable-regions.html)\.

## Denied requests to the OpenSearch API<a name="troubleshooting-tbac"></a>

With the introduction of tag\-based access control for the OpenSearch API, you might start seeing access denied errors where you didn't before\. This might be because one or more of your access policies contains `Deny` using the `ResourceTag` condition, and those conditions are now being honored\.

For example, the following policy used to only deny access to the `CreateDomain` action from the configuration API, if the domain had the tag `environment=production`\. Even though the action list also includes `ESHttpPut`, the deny statement didn't apply to that action or any other `ESHttp*` actions\.

```
{
  "Version": "2012-10-17",
  "Statement": [{
    "Action": [
      "es:CreateDomain",
      "es:ESHttpPut"
    ],
    "Effect": "Deny",
    "Resource": "*",
    "Condition": {
      "ForAnyValue:StringEquals": {
        "aws:ResourceTag/environment": [
          "production"
        ]
      }
    }
  }]
}
```

With the added support of tags for OpenSearch HTTP methods, an IAM identity\-based policy like the above will result in the attached user being denied access to the `ESHttpPut` action\. Previously, in the absence of tags validation, the attached user would have still been able to send PUT requests\.

If you start seeing access denied errors after updating your domains to service software R20220323 or later, check your identity\-based access policies to see if this is the case and update them if necessary to allow access\.

## Can't connect from Alpine Linux<a name="troubleshooting-alpine"></a>

Alpine Linux limits DNS response size to 512 bytes\. If you try to connect to your OpenSearch Service domain from Alpine Linux version 3\.18\.0 or lower, DNS resolution can fail if the domain is in a VPC and has more than 20 nodes\. If you use Alpine Linux version 3\.18\.0 or higher, you should to be able to resolve more than 20 hosts\. For more information, see the [Alpine Linux 3\.18\.0 release notes](https://alpinelinux.org/posts/Alpine-3.18.0-released.html)\.

If your domain is in a VPC, we recommend using other Linux distributions, such as Debian, Ubuntu, CentOS, Red Hat Enterprise Linux, or Amazon Linux 2, to connect to it\.

## Too many requests for Search Backpressure<a name="troubleshooting-search-backpressure"></a>

CPU\-based admission control is a gatekeeping mechanism that proactively limits the number of requests to a node based on its current capacity, both for organic increases and spikes in traffic\. Excessive requests return an HTTP 429 “Too Many Requests” status code upon rejection\. This errors indicates either insufficient cluster resources, resource\-intensive search requests, or an unintended spike in the workload\.

Search Backpressure provides the reason for rejection, which can help fine\-tune resource\-intensive search requests\. For traffic spikes, we recommend client\-side retries with exponential backoff and jitter\.

## Certificate error when using SDK<a name="troubleshooting-certificates"></a>

Because AWS SDKs use the CA certificates from your computer, changes to the certificates on the AWS servers can cause connection failures when you attempt to use an SDK\. Error messages vary, but typically contain the following text:

```
Failed to query OpenSearch
...
SSL3_GET_SERVER_CERTIFICATE:certificate verify failed
```

You can prevent these failures by keeping your computer's CA certificates and operating system up\-to\-date\. If you encounter this issue in a corporate environment and do not manage your own computer, you might need to ask an administrator to assist with the update process\.

The following list shows minimum operating system and Java versions:
+ Microsoft Windows versions that have updates from January 2005 or later installed contain at least one of the required CAs in their trust list\.
+ Mac OS X 10\.4 with Java for Mac OS X 10\.4 Release 5 \(February 2007\), Mac OS X 10\.5 \(October 2007\), and later versions contain at least one of the required CAs in their trust list\.
+ Red Hat Enterprise Linux 5 \(March 2007\), 6, and 7 and CentOS 5, 6, and 7 all contain at least one of the required CAs in their default trusted CA list\.
+ Java 1\.4\.2\_12 \(May 2006\), 5 Update 2 \(March 2005\), and all later versions, including Java 6 \(December 2006\), 7, and 8, contain at least one of the required CAs in their default trusted CA list\.

The three certificate authorities are:
+ Amazon Root CA 1
+ Starfield Services Root Certificate Authority \- G2
+ Starfield Class 2 Certification Authority

Root certificates from the first two authorities are available from [Amazon Trust Services](https://www.amazontrust.com/repository/), but keeping your computer up\-to\-date is the more straightforward solution\. To learn more about ACM\-provided certificates, see [AWS Certificate Manager FAQs](https://aws.amazon.com/certificate-manager/faqs/#certificates)\.

**Note**  
Currently, OpenSearch Service domains in the us\-east\-1 Region use certificates from a different authority\. We plan to update the Region to use these new certificate authorities in the near future\.