# Monitoring OpenSearch cluster metrics with Amazon CloudWatch<a name="managedomains-cloudwatchmetrics"></a>

Amazon OpenSearch Service publishes data from your domains to Amazon CloudWatch\. CloudWatch lets you retrieve statistics about those data points as an ordered set of time\-series data, known as *metrics*\. OpenSearch Service sends metrics to CloudWatch in 60\-second intervals\. If you use General Purpose or Magnetic EBS volumes, the EBS volume metrics update only every five minutes\. For more information about Amazon CloudWatch, see the [Amazon CloudWatch User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/)\.

The OpenSearch Service console displays a series of charts based on the raw data from CloudWatch\. Depending on your needs, you might prefer to view cluster data in CloudWatch instead of the graphs in the console\. The service archives metrics for two weeks before discarding them\. The metrics are provided at no extra charge, but CloudWatch still charges for creating dashboards and alarms\. For more information, see [Amazon CloudWatch pricing](https://aws.amazon.com/cloudwatch/pricing/)\.

OpenSearch Service publishes the following metrics to CloudWatch:
+ [Cluster metrics](#managedomains-cloudwatchmetrics-cluster-metrics)
+ [Dedicated master node metrics](#managedomains-cloudwatchmetrics-master-node-metrics)
+ [EBS volume metrics](#managedomains-cloudwatchmetrics-master-ebs-metrics)
+ [Instance metrics](#managedomains-cloudwatchmetrics-instance-metrics)
+ [UltraWarm metrics](#managedomains-cloudwatchmetrics-uw)
+ [Cold storage metrics](#managedomains-cloudwatchmetrics-coldstorage)
+ [Alerting metrics](#managedomains-cloudwatchmetrics-alerting)
+ [Anomaly detection metrics](#managedomains-cloudwatchmetrics-anomaly-detection)
+ [Asynchronous search metrics](#managedomains-cloudwatchmetrics-asynchronous-search)
+ [SQL metrics](#managedomains-cloudwatchmetrics-sql)
+ [k\-NN metrics](#managedomains-cloudwatchmetrics-knn)
+ [Cross\-cluster search metrics](#managedomains-cloudwatchmetrics-cross-cluster-search)
+ [Cross\-cluster replication metrics](#managedomains-cloudwatchmetrics-replication)
+ [Learning to Rank metrics](#managedomains-cloudwatchmetrics-learning-to-rank)
+ [Piped Processing Language metrics](#managedomains-cloudwatchmetrics-ppl)

## Viewing metrics in CloudWatch<a name="managedomains-viewmetrics"></a>

CloudWatch metrics are grouped first by the service namespace, and then by the various dimension combinations within each namespace\. 

**To view metrics using the CloudWatch console**

1. Open the CloudWatch console at [https://console\.aws\.amazon\.com/cloudwatch/](https://console.aws.amazon.com/cloudwatch/)\.

1. In the navigation pane, choose **All metrics** and select the **AWS/ES** namespace\.

1. Choose a dimension to view the corresponding metrics\. Metrics for individual nodes are in the `ClientId, DomainName, NodeId` dimension\. Cluster metrics are in the `Per-Domain, Per-Client Metrics` dimension\. Some node metrics are aggregated at the cluster level and thus included in both dimensions\. Shard metrics are in the `ClientId, DomainName, NodeId, ShardRole` dimension\.

**To view a list of metrics using the AWS CLI**

Run the following command:

```
aws cloudwatch list-metrics --namespace "AWS/ES"
```

## Interpreting health charts in OpenSearch Service<a name="managedomains-cloudwatchmetrics-box-charts"></a>

To view metrics in OpenSearch Service, use the **Cluster health** and **Instance health** tabs\. The **Instance health** tab uses box charts to provide at\-a\-glance visibility into the health of each OpenSearch node:

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/box-charts.png)
+ Each colored box shows the range of values for the node over the specified time period\.
+ Blue boxes represent values that are consistent with other nodes\. Red boxes represent outliers\.
+ The white line within each box shows the node's current value\.
+ The “whiskers” on either side of each box show the minimum and maximum values for all nodes over the time period\.

If you make configuration changes to your domain, the list of individual instances in the **Cluster health** and **Instance health** tabs often double in size for a brief period before returning to the correct number\. For an explanation of this behavior, see [Making configuration changes in Amazon OpenSearch Service](managedomains-configuration-changes.md)\.

## Cluster metrics<a name="managedomains-cloudwatchmetrics-cluster-metrics"></a>

Amazon OpenSearch Service provides the following metrics for clusters\.


| Metric | Description | 
| --- | --- | 
| ClusterStatus\.green |  A value of 1 indicates that all index shards are allocated to nodes in the cluster\. Relevant statistics: Maximum  | 
| ClusterStatus\.yellow | A value of 1 indicates that the primary shards for all indexes are allocated to nodes in the cluster, but replica shards for at least one index are not\. For more information, see [Yellow cluster status](handling-errors.md#handling-errors-yellow-cluster-status)\.Relevant statistics: Maximum | 
| ClusterStatus\.red |  A value of 1 indicates that the primary and replica shards for at least one index are not allocated to nodes in the cluster\. For more information, see [Red cluster status](handling-errors.md#handling-errors-red-cluster-status)\. Relevant statistics: Maximum  | 
| Shards\.active |  The total number of active primary and replica shards\. Relevant statistics: Maximum, Sum  | 
| Shards\.unassigned |  The number of shards that are not allocated to nodes in the cluster\. Relevant statistics: Maximum, Sum  | 
| Shards\.delayedUnassigned |  The number of shards whose node allocation has been delayed by the timeout settings\. Relevant statistics: Maximum, Sum  | 
| Shards\.activePrimary |  The number of active primary shards\. Relevant statistics: Maximum, Sum  | 
| Shards\.initializing |  The number of shards that are under initialization\. Relevant statistics: Sum  | 
| Shards\.relocating |  The number of shards that are under relocation\. Relevant statistics: Sum  | 
| Nodes |  The number of nodes in the OpenSearch Service cluster, including dedicated master nodes and UltraWarm nodes\. For more information, see [Making configuration changes in Amazon OpenSearch Service](managedomains-configuration-changes.md)\. Relevant statistics: Maximum  | 
| SearchableDocuments |  The total number of searchable documents across all data nodes in the cluster\. Relevant statistics: Minimum, Maximum, Average  | 
| DeletedDocuments |  The total number of documents marked for deletion across all data nodes in the cluster\. These documents no longer appear in search results, but OpenSearch only removes deleted documents from disk during segment merges\. This metric increases after delete requests and decreases after segment merges\. Relevant statistics: Minimum, Maximum, Average  | 
| CPUUtilization |  The percentage of CPU usage for data nodes in the cluster\. Maximum shows the node with the highest CPU usage\. Average represents all nodes in the cluster\. This metric is also available for individual nodes\. Relevant statistics: Maximum, Average  | 
| FreeStorageSpace |  The free space for data nodes in the cluster\. `Sum` shows total free space for the cluster, but you must leave the period at one minute to get an accurate value\. `Minimum` and `Maximum` show the nodes with the least and most free space, respectively\. This metric is also available for individual nodes\. OpenSearch Service throws a `ClusterBlockException` when this metric reaches `0`\. To recover, you must either delete indexes, add larger instances, or add EBS\-based storage to existing instances\. To learn more, see [Lack of available storage space](handling-errors.md#handling-errors-watermark)\. The OpenSearch Service console displays this value in GiB\. The Amazon CloudWatch console displays it in MiB\.  `FreeStorageSpace` will always be lower than the values that the OpenSearch `_cluster/stats` and `_cat/allocation` APIs provide\. OpenSearch Service reserves a percentage of the storage space on each instance for internal operations\. For more information, see [Calculating storage requirements](sizing-domains.md#bp-storage)\.  Relevant statistics: Minimum, Maximum, Average, Sum  | 
| ClusterUsedSpace |  The total used space for the cluster\. You must leave the period at one minute to get an accurate value\. The OpenSearch Service console displays this value in GiB\. The Amazon CloudWatch console displays it in MiB\. Relevant statistics: Minimum, Maximum  | 
| ClusterIndexWritesBlocked |  Indicates whether your cluster is accepting or blocking incoming write requests\. A value of 0 means that the cluster is accepting requests\. A value of 1 means that it is blocking requests\. Some common factors include the following: `FreeStorageSpace` is too low or `JVMMemoryPressure` is too high\. To alleviate this issue, consider adding more disk space or scaling your cluster\. Relevant statistics: Maximum  | 
| JVMMemoryPressure |  The maximum percentage of the Java heap used for all data nodes in the cluster\. OpenSearch Service uses half of an instance's RAM for the Java heap, up to a heap size of 32 GiB\. You can scale instances vertically up to 64 GiB of RAM, at which point you can scale horizontally by adding instances\. See [Recommended CloudWatch alarms for Amazon OpenSearch Service](cloudwatch-alarms.md)\. Relevant statistics: Maximum  The logic for this metric changed in service software R20220323\. For more information, see the [release notes](release-notes.md)\.   | 
| OldGenJVMMemoryPressure |  The maximum percentage of the Java heap used for the "old generation" on all data nodes in the cluster\. This metric is also available at the node level\. Relevant statistics: Maximum  | 
| AutomatedSnapshotFailure |  The number of failed automated snapshots for the cluster\. A value of `1` indicates that no automated snapshot was taken for the domain in the previous 36 hours\. Relevant statistics: Minimum, Maximum  | 
| CPUCreditBalance |  The remaining CPU credits available for data nodes in the cluster\. A CPU credit provides the performance of a full CPU core for one minute\. For more information, see [CPU credits](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/burstable-credits-baseline-concepts.html) in the *Amazon EC2 Developer Guide*\. This metric is available only for the T2 instance types\. Relevant statistics: Minimum  | 
| OpenSearchDashboardsHealthyNodes \(previously KibanaHealthyNodes\) |  A health check for OpenSearch Dashboards\. If the minimum, maximum, and average are all equal to 1, Dashboards is behaving normally\. If you have 10 nodes with a maximum of 1, minimum of 0, and average of 0\.7, this means 7 nodes \(70%\) are healthy and 3 nodes \(30%\) are unhealthy\. Relevant statistics: Minimum, Maximum, Average  | 
| KibanaReportingFailedRequestSysErrCount |  The number of requests to generate OpenSearch Dashboards reports that failed due to server problems or feature limitations\. Relevant statistics: Sum  | 
| KibanaReportingFailedRequestUserErrCount |  The number of requests to generate OpenSearch Dashboards reports that failed due to client issues\. Relevant statistics: Sum  | 
| KibanaReportingRequestCount |  The total number of requests to generate OpenSearch Dashboards reports\. Relevant statistics: Sum  | 
| KibanaReportingSuccessCount |  The number of successful requests to generate OpenSearch Dashboards reports\. Relevant statistics: Sum  | 
| KMSKeyError |  A value of 1 indicates that the AWS KMS key used to encrypt data at rest has been disabled\. To restore the domain to normal operations, re\-enable the key\. The console displays this metric only for domains that encrypt data at rest\. Relevant statistics: Minimum, Maximum  | 
| KMSKeyInaccessible |  A value of 1 indicates that the AWS KMS key used to encrypt data at rest has been deleted or revoked its grants to OpenSearch Service\. You can't recover domains that are in this state\. If you have a manual snapshot, though, you can use it to migrate the domain's data to a new domain\. The console displays this metric only for domains that encrypt data at rest\. Relevant statistics: Minimum, Maximum  | 
| InvalidHostHeaderRequests |  The number of HTTP requests made to the OpenSearch cluster that included an invalid \(or missing\) host header\. Valid requests include the domain hostname as the host header value\. OpenSearch Service rejects invalid requests for public access domains that don't have a restrictive access policy\. We recommend applying a restrictive access policy to all domains\. If you see large values for this metric, confirm that your OpenSearch clients include the domain hostname \(and not, for example, its IP address\) in their requests\. Relevant statistics: Sum  | 
| OpenSearchRequests\(previously ElasticsearchRequests\) |  The number of requests made to the OpenSearch cluster\. Relevant statistics: Sum  | 
| 2xx, 3xx, 4xx, 5xx |  The number of requests to the domain that resulted in the given HTTP response code \(2*xx*, 3*xx*, 4*xx*, 5*xx*\)\. Relevant statistics: Sum  | 
| ThroughputThrottle | Indicates whether requests are being throttled due to the throughput limitations of your EBS volumes\. A value of 1 indicates that some requests were throttled within the selected timeframe\. A value of 0 indicates normal behavior\.If you conistently see a value of 1 for this metric, you can scale up your instances by following AWS recommended best practices\.Relevant statistics: Minimum, Maximum | 

## Dedicated master node metrics<a name="managedomains-cloudwatchmetrics-master-node-metrics"></a>

Amazon OpenSearch Service provides the following metrics for [dedicated master nodes](managedomains-dedicatedmasternodes.md)\.


| Metric | Description | 
| --- | --- | 
| MasterCPUUtilization |  The maximum percentage of CPU resources used by the dedicated master nodes\. We recommend increasing the size of the instance type when this metric reaches 60 percent\. Relevant statistics: Maximum  | 
| MasterFreeStorageSpace |  This metric is not relevant and can be ignored\. The service does not use master nodes as data nodes\.  | 
| MasterJVMMemoryPressure |  The maximum percentage of the Java heap used for all dedicated master nodes in the cluster\. We recommend moving to a larger instance type when this metric reaches 85 percent\. Relevant statistics: Maximum  The logic for this metric changed in service software R20220323\. For more information, see the [release notes](release-notes.md)\.   | 
| MasterOldGenJVMMemoryPressure |  The maximum percentage of the Java heap used for the "old generation" per master node\. Relevant statistics: Maximum  | 
| MasterCPUCreditBalance |  The remaining CPU credits available for dedicated master nodes in the cluster\. A CPU credit provides the performance of a full CPU core for one minute\. For more information, see [CPU credits](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/burstable-credits-baseline-concepts.html) in the *Amazon EC2 Developer Guide*\. This metric is available only for the T2 instance types\. Relevant statistics: Minimum  | 
| MasterReachableFromNode |  A health check for `MasterNotDiscovered` exceptions\. A value of 1 indicates normal behavior\. A value of 0 indicates that `/_cluster/health/` is failing\. Failures mean that the master node stopped or is not reachable\. They are usually the result of a network connectivity issue or AWS dependency problem\. Relevant statistics: Minimum  | 
| MasterSysMemoryUtilization |  The percentage of the master node's memory that is in use\. Relevant statistics: Maximum  | 

## EBS volume metrics<a name="managedomains-cloudwatchmetrics-master-ebs-metrics"></a>

Amazon OpenSearch Service provides the following metrics for EBS volumes\.


| Metric | Description | 
| --- | --- | 
| ReadLatency |  The latency, in seconds, for read operations on EBS volumes\. This metric is also available for individual nodes\. Relevant statistics: Minimum, Maximum, Average  | 
| WriteLatency |  The latency, in seconds, for write operations on EBS volumes\. This metric is also available for individual nodes\. Relevant statistics: Minimum, Maximum, Average  | 
| ReadThroughput |  The throughput, in bytes per second, for read operations on EBS volumes\. This metric is also available for individual nodes\. Relevant statistics: Minimum, Maximum, Average  | 
| WriteThroughput |  The throughput, in bytes per second, for write operations on EBS volumes\. This metric is also available for individual nodes\. Relevant statistics: Minimum, Maximum, Average  | 
| DiskQueueDepth |  The number of pending input and output \(I/O\) requests for an EBS volume\. Relevant statistics: Minimum, Maximum, Average  | 
| ReadIOPS |  The number of input and output \(I/O\) operations per second for read operations on EBS volumes\. This metric is also available for individual nodes\. Relevant statistics: Minimum, Maximum, Average  | 
| WriteIOPS |  The number of input and output \(I/O\) operations per second for write operations on EBS volumes\. This metric is also available for individual nodes\. Relevant statistics: Minimum, Maximum, Average  | 
| BurstBalance |  The percentage of input and output \(I/O\) credits remaining in the burst bucket for an EBS volume\. A value of 100 means that the volume has accumulated the maximum number of credits\. If this percentage falls below 70%, see [Low EBS burst balance](handling-errors.md#handling-errors-low-ebs-burst)\. Relevant statistics: Minimum, Maximum, Average  | 

## Instance metrics<a name="managedomains-cloudwatchmetrics-instance-metrics"></a>

Amazon OpenSearch Service provides the following metrics for each instance in a domain\. OpenSearch Service also aggregates these instance metrics to provide insight into overall cluster health\. You can verify this behavior using the **Sample Count** statistic in the console\. Note that each metric in the following table has relevant statistics for the node *and* the cluster\.

**Important**  
Different versions of Elasticsearch use different thread pools to process calls to the `_index` API\. Elasticsearch 1\.5 and 2\.3 use the index thread pool\. Elasticsearch 5\.*x*, 6\.0, and 6\.2 use the bulk thread pool\. OpenSearch and Elasticsearch 6\.3 and later use the write thread pool\. Currently, the OpenSearch Service console doesn't include a graph for the bulk thread pool\.  
Use `GET _cluster/settings?include_defaults=true` to check thread pool and queue sizes for your cluster\.


| Metric | Description | 
| --- | --- | 
| IndexingLatency |  The average time, in milliseconds, that it takes a shard to complete an indexing operation\. Relevant node statistics: Average Relevant cluster statistics: Average, Maximum  | 
| IndexingRate |  The number of indexing operations per minute\. A single call to the `_bulk` API that adds two documents and updates two counts as four operations, which might be spread across one or more nodes\. If that index has one or more replicas, other nodes in the cluster also record a total of four indexing operations\. Document deletions do not count towards this metric\. Relevant node statistics: Average Relevant cluster statistics: Average, Maximum, Sum  | 
| SearchLatency |  The average time, in milliseconds, that it takes a shard on a data node to complete a search operation\. Relevant node statistics: Average Relevant cluster statistics: Average, Maximum  | 
| SearchRate |  The total number of search requests per minute for all shards on a data node\. A single call to the `_search` API might return results from many different shards\. If five of these shards are on one node, the node would report 5 for this metric, even though the client only made one request\. Relevant node statistics: Average Relevant cluster statistics: Average, Maximum, Sum  | 
| SegmentCount |  The number of segments on a data node\. The more segments you have, the longer each search takes\. OpenSearch occasionally merges smaller segments into a larger one\. Relevant node statistics: Maximum, Average Relevant cluster statistics: Sum, Maximum, Average  | 
| SysMemoryUtilization |  The percentage of the instance's memory that is in use\. High values for this metric are normal and usually do not represent a problem with your cluster\. For a better indicator of potential performance and stability issues, see the `JVMMemoryPressure` metric\. Relevant node statistics: Minimum, Maximum, Average Relevant cluster statistics: Minimum, Maximum, Average  | 
| JVMGCYoungCollectionCount |  The number of times that "young generation" garbage collection has run\. A large, ever\-growing number of runs is a normal part of cluster operations\. Relevant node statistics: Maximum Relevant cluster statistics: Sum, Maximum, Average  | 
| JVMGCYoungCollectionTime |  The amount of time, in milliseconds, that the cluster has spent performing "young generation" garbage collection\. Relevant node statistics: Maximum Relevant cluster statistics: Sum, Maximum, Average  | 
| JVMGCOldCollectionCount |  The number of times that "old generation" garbage collection has run\. In a cluster with sufficient resources, this number should remain small and grow infrequently\. Relevant node statistics: Maximum Relevant cluster statistics: Sum, Maximum, Average  | 
| JVMGCOldCollectionTime |  The amount of time, in milliseconds, that the cluster has spent performing "old generation" garbage collection\. Relevant node statistics: Maximum Relevant cluster statistics: Sum, Maximum, Average  | 
| OpenSearchDashboardsConcurrentConnections \(previously KibanaConcurrentConnections\) |  The number of active concurrent connections to OpenSearch Dashboards\. If this number is consistently high, consider scaling your cluster\. Relevant node statistics: Maximum Relevant cluster statistics: Sum, Maximum, Average  | 
| OpenSearchDashboardsHealthyNode \(previously KibanaHealthyNode\) |  A health check for the individual OpenSearch Dashboards node\. A value of 1 indicates normal behavior\. A value of 0 indicates that Dashboards is inaccessible\.  Relevant node statistics: Minimum Relevant cluster statistics: Minimum, Maximum, Average  | 
| OpenSearchDashboardsHeapTotal \(previously KibanaHeapTotal\) |  The amount of heap memory allocated to OpenSearch Dashboards in MiB\. Different EC2 instance types can impact the exact memory allocation\.  Relevant node statistics: Maximum Relevant cluster statistics: Sum, Maximum, Average  | 
| OpenSearchDashboardsHeapUsed \(previously KibanaHeapUsed\) |  The absolute amount of heap memory used by OpenSearch Dashboards in MiB\. Relevant node statistics: Maximum Relevant cluster statistics: Sum, Maximum, Average  | 
| OpenSearchDashboardsHeapUtilization \(previously KibanaHeapUtilization\) |  The maximum percentage of available heap memory used by OpenSearch Dashboards\. If this value increases above 80%, consider scaling your cluster\.  Relevant node statistics: Maximum Relevant cluster statistics: Minimum, Maximum, Average  | 
| OpenSearchDashboardsOS1MinuteLoad \(previously KibanaOS1MinuteLoad\) |  The one\-minute CPU load average for OpenSearch Dashboards\. The CPU load should ideally stay below 1\.00\. While temporary spikes are fine, we recommend increasing the size of the instance type if this metric is consistently above 1\.00\. Relevant node statistics: Average Relevant cluster statistics: Average, Maximum  | 
| OpenSearchDashboardsRequestTotal \(previously KibanaRequestTotal\) |  The total count of HTTP requests made to OpenSearch Dashboards\. If your system is slow or you see high numbers of Dashboards requests, consider increasing the size of the instance type\. Relevant node statistics: Sum Relevant cluster statistics: Sum  | 
| OpenSearchDashboardsResponseTimesMaxInMillis \(previously KibanaResponseTimesMaxInMillis\) |  The maximum amount of time, in milliseconds, that it takes for OpenSearch Dashboards to respond to a request\. If requests consistently take a long time to return results, consider increasing the size of the instance type\. Relevant node statistics: Maximum Relevant cluster statistics: Maximum, Average  | 
| ThreadpoolForce\_mergeQueue |  The number of queued tasks in the force merge thread pool\. If the queue size is consistently high, consider scaling your cluster\. Relevant node statistics: Maximum Relevant cluster statistics: Sum, Maximum, Average  | 
| ThreadpoolForce\_mergeRejected |  The number of rejected tasks in the force merge thread pool\. If this number continually grows, consider scaling your cluster\. Relevant node statistics: Maximum Relevant cluster statistics: Sum  | 
| ThreadpoolForce\_mergeThreads |  The size of the force merge thread pool\. Relevant node statistics: Maximum Relevant cluster statistics: Average, Sum  | 
| ThreadpoolIndexQueue |  The number of queued tasks in the index thread pool\. If the queue size is consistently high, consider scaling your cluster\. The maximum index queue size is 200\. Relevant node statistics: Maximum Relevant cluster statistics: Sum, Maximum, Average  | 
| ThreadpoolIndexRejected |  The number of rejected tasks in the index thread pool\. If this number continually grows, consider scaling your cluster\. Relevant node statistics: Maximum Relevant cluster statistics: Sum  | 
| ThreadpoolIndexThreads |  The size of the index thread pool\. Relevant node statistics: Maximum Relevant cluster statistics: Average, Sum  | 
| ThreadpoolSearchQueue |  The number of queued tasks in the search thread pool\. If the queue size is consistently high, consider scaling your cluster\. The maximum search queue size is 1,000\. Relevant node statistics: Maximum Relevant cluster statistics: Sum, Maximum, Average  | 
| ThreadpoolSearchRejected |  The number of rejected tasks in the search thread pool\. If this number continually grows, consider scaling your cluster\. Relevant node statistics: Maximum Relevant cluster statistics: Sum  | 
| ThreadpoolSearchThreads |  The size of the search thread pool\. Relevant node statistics: Maximum Relevant cluster statistics: Average, Sum  | 
| Threadpoolsql\-workerQueue |  The number of queued tasks in the SQL search thread pool\. If the queue size is consistently high, consider scaling your cluster\.  Relevant node statistics: Maximum Relevant cluster statistics: Sum, Maximum, Average  | 
| Threadpoolsql\-workerRejected |  The number of rejected tasks in the SQL search thread pool\. If this number continually grows, consider scaling your cluster\. Relevant node statistics: Maximum Relevant cluster statistics: Sum  | 
| Threadpoolsql\-workerThreads |  The size of the SQL search thread pool\. Relevant node statistics: Maximum Relevant cluster statistics: Average, Sum  | 
| ThreadpoolBulkQueue |  The number of queued tasks in the bulk thread pool\. If the queue size is consistently high, consider scaling your cluster\. Relevant node statistics: Maximum Relevant cluster statistics: Sum, Maximum, Average  | 
| ThreadpoolBulkRejected |  The number of rejected tasks in the bulk thread pool\. If this number continually grows, consider scaling your cluster\. Relevant node statistics: Maximum Relevant cluster statistics: Sum  | 
| ThreadpoolBulkThreads |  The size of the bulk thread pool\. Relevant node statistics: Maximum Relevant cluster statistics: Average, Sum  | 
| ThreadpoolWriteThreads |  The size of the write thread pool\. Relevant node statistics: Maximum Relevant cluster statistics: Average, Sum  | 
| ThreadpoolWriteQueue |  The number of queued tasks in the write thread pool\. Relevant node statistics: Maximum Relevant cluster statistics: Average, Sum  | 
| ThreadpoolWriteRejected |  The number of rejected tasks in the write thread pool\. Relevant node statistics: Maximum Relevant cluster statistics: Average, Sum  Because the default write queue size was increased from 200 to 10000 in version 7\.1, this metric is no longer the only indicator of rejections from OpenSearch Service\. Use the `CoordinatingWriteRejected`, `PrimaryWriteRejected`, and `ReplicaWriteRejected` metrics to monitor rejections in versions 7\.1 and later\.   | 
| CoordinatingWriteRejected |  The total number of rejections happened on the coordinating node due to indexing pressure since the last OpenSearch Service process startup\. Relevant node statistics: Maximum Relevant cluster statistics: Average, Sum This metric is available in version 7\.1 and above\.  | 
| PrimaryWriteRejected |  The total number of rejections happened on the primary shards due to indexing pressure since the last OpenSearch Service process startup\. Relevant node statistics: Maximum Relevant cluster statistics: Average, Sum This metric is available in version 7\.1 and above\.  | 
| ReplicaWriteRejected |  The total number of rejections happened on the replica shards due to indexing pressure since the last OpenSearch Service process startup\. Relevant node statistics: Maximum Relevant cluster statistics: Average, Sum This metric is available in version 7\.1 and above\.  | 

## UltraWarm metrics<a name="managedomains-cloudwatchmetrics-uw"></a>

Amazon OpenSearch Service provides the following metrics for [UltraWarm](ultrawarm.md) nodes\.


| Metric | Description | 
| --- | --- | 
| WarmCPUUtilization |  The percentage of CPU usage for UltraWarm nodes in the cluster\. Maximum shows the node with the highest CPU usage\. Average represents all UltraWarm nodes in the cluster\. This metric is also available for individual UltraWarm nodes\. Relevant statistics: Maximum, Average  | 
| WarmFreeStorageSpace |  The amount of free warm storage space in MiB\. Because UltraWarm uses Amazon S3 rather than attached disks, `Sum` is the only relevant statistic\. You must leave the period at one minute to get an accurate value\. Relevant statistics: Sum  | 
| WarmSearchableDocuments |  The total number of searchable documents across all warm indexes in the cluster\. You must leave the period at one minute to get an accurate value\. Relevant statistics: Sum  | 
|  WarmSearchLatency  |  The average time, in milliseconds, that it takes a shard on an UltraWarm node to complete a search operation\. Relevant node statistics: Average Relevant cluster statistics: Average, Maximum  | 
|  WarmSearchRate  |  The total number of search requests per minute for all shards on an UltraWarm node\. A single call to the `_search` API might return results from many different shards\. If five of these shards are on one node, the node would report 5 for this metric, even though the client only made one request\. Relevant node statistics: Average Relevant cluster statistics: Average, Maximum, Sum  | 
| WarmStorageSpaceUtilization |  The total amount of warm storage space, in MiB, that the cluster is using\.  Relevant statistics: Maximum  | 
|  HotStorageSpaceUtilization  |  The total amount of hot storage space that the cluster is using\.  Relevant statistics: Maximum  | 
| WarmSysMemoryUtilization |  The percentage of the warm node's memory that is in use\. Relevant statistics: Maximum  | 
|  HotToWarmMigrationQueueSize  |  The number of indexes currently waiting to migrate from hot to warm storage\. Relevant statistics: Maximum  | 
|  WarmToHotMigrationQueueSize  |  The number of indexes currently waiting to migrate from warm to hot storage\. Relevant statistics: Maximum  | 
|  HotToWarmMigrationFailureCount  |  The total number of failed hot to warm migrations\. Relevant statistics: Sum  | 
|  HotToWarmMigrationForceMergeLatency  |  The average latency of the force merge stage of the migration process\. If this stage consistently takes too long, consider increasing `index.ultrawarm.migration.force_merge.max_num_segments`\. Relevant statistics: Average  | 
|  HotToWarmMigrationSnapshotLatency  |  The average latency of the snapshot stage of the migration process\. If this stage consistently takes too long, ensure that your shards are appropriately sized and distributed throughout the cluster\. Relevant statistics: Average  | 
|  HotToWarmMigrationProcessingLatency  |  The average latency of successful hot to warm migrations, *not* including time spent in the queue\. This value is the sum of the amount of time it takes to complete the force merge, snapshot, and shard relocation stages of the migration process\. Relevant statistics: Average  | 
| HotToWarmMigrationSuccessCount  |  The total number of successful hot to warm migrations\. Relevant statistics: Sum  | 
| HotToWarmMigrationSuccessLatency  |  The average latency of successful hot to warm migrations, including time spent in the queue\. Relevant statistics: Average  | 
| WarmThreadpoolSearchThreads |  The size of the UltraWarm search thread pool\. Relevant node statistics: Maximum Relevant cluster statistics: Average, Sum  | 
| WarmThreadpoolSearchRejected |  The number of rejected tasks in the UltraWarm search thread pool\. If this number continually grows, consider adding more UltraWarm nodes\. Relevant node statistics: Maximum Relevant cluster statistics: Sum  | 
| WarmThreadpoolSearchQueue | The number of queued tasks in the UltraWarm search thread pool\. If the queue size is consistently high, consider adding more UltraWarm nodes\.Relevant node statistics: MaximumRelevant cluster statistics: Sum, Maximum, Average | 
| WarmJVMMemoryPressure |  The maximum percentage of the Java heap used for the UltraWarm nodes\. Relevant statistics: Maximum  The logic for this metric changed in service software R20220323\. For more information, see the [release notes](release-notes.md)\.   | 
| WarmOldGenJVMMemoryPressure |  The maximum percentage of the Java heap used for the "old generation" per UltraWarm node\. Relevant statistics: Maximum  | 
| WarmJVMGCYoungCollectionCount |  The number of times that "young generation" garbage collection has run on UltraWarm nodes\. A large, ever\-growing number of runs is a normal part of cluster operations\. Relevant node statistics: Maximum Relevant cluster statistics: Sum, Maximum, Average  | 
| WarmJVMGCYoungCollectionTime |  The amount of time, in milliseconds, that the cluster has spent performing "young generation" garbage collection on UltraWarm nodes\. Relevant node statistics: Maximum Relevant cluster statistics: Sum, Maximum, Average  | 
| WarmJVMGCOldCollectionCount |  The number of times that "old generation" garbage collection has run on UltraWarm nodes\. In a cluster with sufficient resources, this number should remain small and grow infrequently\.  Relevant node statistics: Maximum Relevant cluster statistics: Sum, Maximum, Average  | 

## Cold storage metrics<a name="managedomains-cloudwatchmetrics-coldstorage"></a>

Amazon OpenSearch Service provides the following metrics for [cold storage](cold-storage.md)\.


| Metric | Description | 
| --- | --- | 
| ColdStorageSpaceUtilization  |  The total amount of cold storage space, in MiB, that the cluster is using\.  Relevant statistics: Max  | 
| ColdToWarmMigrationFailureCount |  The total number of failed cold to warm migrations\. Relevant statistics: Sum  | 
| ColdToWarmMigrationLatency |  The amount of time for successful cold to warm migrations to complete\. Relevant statistics: Average  | 
| ColdToWarmMigrationQueueSize |  The number of indexes currently waiting to migrate from cold to warm storage\.  Relevant statistics: Maximum  | 
| ColdToWarmMigrationSuccessCount  |  The total number of successful cold to warm migrations\. Relevant statistics: Sum  | 
| WarmToColdMigrationFailureCount  |  The total number of failed warm to cold migrations\. Relevant statistics: Sum  | 
| WarmToColdMigrationLatency |  The amount of time for successful warm to cold migrations to complete\. Relevant statistics: Average  | 
| WarmToColdMigrationQueueSize |  The number of indexes currently waiting to migrate from warm to cold storage\.  Relevant statistics: Maximum  | 
| WarmToColdMigrationSuccessCount |  The total number of successful warm to cold migrations\. Relevant statistics: Sum  | 

## Alerting metrics<a name="managedomains-cloudwatchmetrics-alerting"></a>

Amazon OpenSearch Service provides the following metrics for [alerting](alerting.md)\.


| Metric | Description | 
| --- | --- | 
| AlertingDegraded |  A value of 1 means that either the alerting index is red or one or more nodes is not on schedule\. A value of 0 indicates normal behavior\. Relevant statistics: Maximum  | 
| AlertingIndexExists |  A value of 1 means the `.opensearch-alerting-config` index exists\. A value of 0 means it does not\. Until you use the alerting feature for the first time, this value remains 0\. Relevant statistics: Maximum  | 
| AlertingIndexStatus\.green |  The health of the index\. A value of 1 means green\. A value of 0 means that the index either doesn't exist or isn't green\. Relevant statistics: Maximum  | 
| AlertingIndexStatus\.red |  The health of the index\. A value of 1 means red\. A value of 0 means that the index either doesn't exist or isn't red\. Relevant statistics: Maximum  | 
| AlertingIndexStatus\.yellow |  The health of the index\. A value of 1 means yellow\. A value of 0 means that the index either doesn't exist or isn't yellow\. Relevant statistics: Maximum  | 
| AlertingNodesNotOnSchedule |  A value of 1 means some jobs are not running on schedule\. A value of 0 means that all alerting jobs are running on schedule \(or that no alerting jobs exist\)\. Check the OpenSearch Service console or make a `_nodes/stats` request to see if any nodes show high resource usage\. Relevant statistics: Maximum  | 
| AlertingNodesOnSchedule |  A value of 1 means that all alerting jobs are running on schedule \(or that no alerting jobs exist\)\. A value of 0 means some jobs are not running on schedule\. Relevant statistics: Maximum  | 
| AlertingScheduledJobEnabled |  A value of 1 means that the `opensearch.scheduled_jobs.enabled` cluster setting is true\. A value of 0 means it is false, and scheduled jobs are disabled\. Relevant statistics: Maximum  | 

## Anomaly detection metrics<a name="managedomains-cloudwatchmetrics-anomaly-detection"></a>

Amazon OpenSearch Service provides the following metrics for [anomaly detection](ad.md)\.


| Metric | Description | 
| --- | --- | 
| ADPluginUnhealthy |  A value of 1 means that the anomaly detection plugin is not functioning properly, either because of a high number of failures or because one of the indexes that it uses is red\. A value of 0 indicates the plugin is working as expected\. Relevant statistics: Maximum  | 
| ADExecuteRequestCount |  The number of requests to detect anomalies\. Relevant statistics: Sum  | 
|  ADExecuteFailureCount  |  The number of failed requests to detect anomalies\.  Relevant statistics: Sum  | 
| ADHCExecuteFailureCount |  The number of failed requests to detect anomalies for high cardinality detectors\. Relevant statistics: Sum  | 
| ADHCExecuteRequestCount |  The number of requests to detect anomalies for high cardinality detectors\. Relevant statistics: Sum  | 
| ADAnomalyResultsIndexStatusIndexExists |  A value of 1 means the index that the `.opensearch-anomaly-results` alias points to exists\. Until you use anomaly detection for the first time, this value remains 0\. Relevant statistics: Maximum  | 
| ADAnomalyResultsIndexStatus\.red |  A value of 1 means the index that the `.opensearch-anomaly-results` alias points to is red\. A value of 0 means it is not\. Until you use anomaly detection for the first time, this value remains 0\. Relevant statistics: Maximum  | 
| ADAnomalyDetectorsIndexStatusIndexExists |  A value of 1 means that the `.opensearch-anomaly-detectors` index exists\. A value of 0 means it does not\. Until you use anomaly detection for the first time, this value remains 0\. Relevant statistics: Maximum  | 
| ADAnomalyDetectorsIndexStatus\.red |  A value of 1 means that the `.opensearch-anomaly-detectors` index is red\. A value of 0 means it is not\. Until you use anomaly detection for the first time, this value remains 0\. Relevant statistics: Maximum  | 
| ADModelsCheckpointIndexStatusIndexExists |  A value of 1 means that the `.opensearch-anomaly-checkpoints` index exists\. A value of 0 means it does not\. Until you use anomaly detection for the first time, this value remains 0\. Relevant statistics: Maximum  | 
| ADModelsCheckpointIndexStatus\.red |  A value of 1 means that the `.opensearch-anomaly-checkpoints` index is red\. A value of 0 means it is not\. Until you use anomaly detection for the first time, this value remains 0\. Relevant statistics: Maximum  | 

## Asynchronous search metrics<a name="managedomains-cloudwatchmetrics-asynchronous-search"></a>

Amazon OpenSearch Service provides the following metrics for [asynchronous search](asynchronous-search.md)\.

**Asynchronous search coordinator node statistics \(per coordinator node\)**


| Metric | Description | 
| --- | --- | 
| AsynchronousSearchSubmissionRate |  The number of asynchronous searches submitted in the last minute\.  | 
| AsynchronousSearchInitializedRate |  The number of asynchronous searches initialized in the last minute\.  | 
| AsynchronousSearchRunningCurrent |  The number of asynchronous searches currently running\.  | 
| AsynchronousSearchCompletionRate |  The number of asynchronous searches successfully completed in the last minute\.  | 
| AsynchronousSearchFailureRate |  The number of asynchronous searches that completed and failed in the last minute\.  | 
| AsynchronousSearchPersistRate |  The number of asynchronous searches that persisted in the last minute\.  | 
| AsynchronousSearchPersistFailedRate |  The number of asynchronous searches that failed to persist in the last minute\.  | 
| AsynchronousSearchRejected |  The total number of asynchronous searches rejected since the node up time\.  | 
| AsynchronousSearchCancelled |  The total number of asynchronous searches cancelled since the node up time\.  | 
| AsynchronousSearchMaxRunningTime |  The duration of longest running asynchronous search on a node in the last minute\.  | 

**Asynchronous search cluster statistics**


| Metric | Description | 
| --- | --- | 
| AsynchronousSearchStoreHealth |  The health of the store in the persisted index \(RED/non\-RED\) in the last minute\.  | 
| AsynchronousSearchStoreSize |  The size of the system index across all shards in the last minute\.  | 
| AsynchronousSearchStoredResponseCount |  The numbers of stored responses in the system index in the last minute\.  | 

## SQL metrics<a name="managedomains-cloudwatchmetrics-sql"></a>

Amazon OpenSearch Service provides the following metrics for [SQL support](sql-support.md)\.


| Metric | Description | 
| --- | --- | 
| SQLFailedRequestCountByCusErr |  The number of requests to the `_sql` API that failed due to a client issue\. For example, a request might return HTTP status code 400 due to an `IndexNotFoundException`\. Relevant statistics: Sum  | 
| SQLFailedRequestCountBySysErr |  The number of requests to the `_sql` API that failed due to a server problem or feature limitation\. For example, a request might return HTTP status code 503 due to a `VerificationException`\. Relevant statistics: Sum  | 
| SQLRequestCount |  The number of requests to the `_sql` API\. Relevant statistics: Sum  | 
| SQLDefaultCursorRequestCount |   Similar to `SQLRequestCount` but only counts pagination requests\. Relevant statistics: Sum  | 
| SQLUnhealthy |  A value of 1 indicates that, in response to certain requests, the SQL plugin is returning 5*xx* response codes or passing invalid query DSL to OpenSearch\. Other requests should continue to succeed\. A value of 0 indicates no recent failures\. If you see a sustained value of 1, troubleshoot the requests your clients are making to the plugin\. Relevant statistics: Maximum  | 

## k\-NN metrics<a name="managedomains-cloudwatchmetrics-knn"></a>

Amazon OpenSearch Service includes the following metrics for the k\-nearest neighbor \([k\-NN](knn.md)\) plugin\.


| Metric | Description | 
| --- | --- | 
| KNNCacheCapacityReached |  Per\-node metric for whether cache capacity has been reached\. This metric is only relevant to approximate k\-NN search\. Relevant statistics: Maximum  | 
| KNNCircuitBreakerTriggered |  Per\-cluster metric for whether the circuit breaker is triggered\. If any nodes return a value of 1 for `KNNCacheCapacityReached`, this value will also return 1\. This metric is only relevant to approximate k\-NN search\. Relevant statistics: Maximum  | 
| KNNEvictionCount |  Per\-node metric for the number of graphs that have been evicted from the cache due to memory constraints or idle time\. Explicit evictions that occur because of index deletion are not counted\. This metric is only relevant to approximate k\-NN search\. Relevant statistics: Sum  | 
| KNNGraphIndexErrors |  Per\-node metric for the number of requests to add the `knn_vector` field of a document to a graph that produced an error\. Relevant statistics: Sum  | 
| KNNGraphIndexRequests |  Per\-node metric for the number of requests to add the `knn_vector` field of a document to a graph\. Relevant statistics: Sum  | 
| KNNGraphMemoryUsage |  Per\-node metric for the current cache size \(total size of all graphs in memory\) in kilobytes\. This metric is only relevant to approximate k\-NN search\. Relevant statistics: Average  | 
| KNNGraphQueryErrors |  Per\-node metric for the number of graph queries that produced an error\. Relevant statistics: Sum  | 
| KNNGraphQueryRequests |  Per\-node metric for the number of graph queries\. Relevant statistics: Sum  | 
| KNNHitCount |  Per\-node metric for the number of cache hits\. A cache hit occurs when a user queries a graph that is already loaded into memory\. This metric is only relevant to approximate k\-NN search\. Relevant statistics: Sum  | 
| KNNLoadExceptionCount |  Per\-node metric for the number of times an exception occurred while trying to load a graph into the cache\. This metric is only relevant to approximate k\-NN search\. Relevant statistics: Sum  | 
| KNNLoadSuccessCount |  Per\-node metric for the number of times the plugin successfully loaded a graph into the cache\. This metric is only relevant to approximate k\-NN search\. Relevant statistics: Sum  | 
| KNNMissCount |  Per\-node metric for the number of cache misses\. A cache miss occurs when a user queries a graph that is not yet loaded into memory\. This metric is only relevant to approximate k\-NN search\. Relevant statistics: Sum  | 
| KNNQueryRequests |  Per\-node metric for the number of query requests the k\-NN plugin received\. Relevant statistics: Sum  | 
| KNNScriptCompilationErrors |  Per\-node metric for the number of errors during script compilation\. This statistic is only relevant to k\-NN score script search\. Relevant statistics: Sum  | 
| KNNScriptCompilations |  Per\-node metric for the number of times the k\-NN script has been compiled\. This value should usually be 1 or 0, but if the cache containing the compiled scripts is filled, the k\-NN script might be recompiled\. This statistic is only relevant to k\-NN score script search\. Relevant statistics: Sum  | 
| KNNScriptQueryErrors |  Per\-node metric for the number of errors during script queries\. This statistic is only relevant to k\-NN score script search\. Relevant statistics: Sum  | 
| KNNScriptQueryRequests |  Per\-node metric for the total number of script queries\. This statistic is only relevant to k\-NN score script search\. Relevant statistics: Sum  | 
| KNNTotalLoadTime |  The time in nanoseconds that k\-NN has taken to load graphs into the cache\. This metric is only relevant to approximate k\-NN search\. Relevant statistics: Sum  | 

## Cross\-cluster search metrics<a name="managedomains-cloudwatchmetrics-cross-cluster-search"></a>

Amazon OpenSearch Service provides the following metrics for [cross\-cluster search](cross-cluster-search.md)\.

**Source domain metrics**


| Metric | Dimension | Description | 
| --- | --- | --- | 
| CrossClusterOutboundConnections |  `ConnectionId`  |  Number of connected nodes\. If your response includes one or more skipped domains, use this metric to trace any unhealthy connections\. If this number drops to 0, then the connection is unhealthy\.  | 
| CrossClusterOutboundRequests |  `ConnectionId`  |  Number of search requests sent to the destination domain\. Use to check if the load of cross\-cluster search requests are overwhelming your domain, correlate any spike in this metric with any JVM/CPU spike\.  | 

**Destination domain metric**


| Metric | Dimension | Description | 
| --- | --- | --- | 
| CrossClusterInboundRequests |  `ConnectionId`  |  Number of incoming connection requests received from the source domain\.  | 

Add a CloudWatch alarm in the event that you lose a connection unexpectedly\. For steps to create an alarm, see [Create a CloudWatch Alarm Based on a Static Threshold](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/ConsoleAlarms.html)\.

## Cross\-cluster replication metrics<a name="managedomains-cloudwatchmetrics-replication"></a>

Amazon OpenSearch Service provides the following metrics for [cross\-cluster replication](replication.md)\.


| Metric | Description | 
| --- | --- | 
| ReplicationRate |  The average rate of replication operations per second\. This metric is similar to the `IndexingRate` metric\.  | 
| LeaderCheckPoint |  For a specific connection, the sum of leader checkpoint values across all replicating indexes\. You can use this metric to measure replication latency\.  | 
| FollowerCheckPoint |  For a specific connection, the sum of follower checkpoint values across all replicating indexes\. You can use this metric to measure replication latency\.  | 
| ReplicationNumSyncingIndices |  The number of indexes that have a replication status of `SYNCING`\.  | 
| ReplicationNumBootstrappingIndices |  The number of indexes that have a replication status of `BOOTSTRAPPING`\.  | 
| ReplicationNumPausedIndices |  The number of indexes that have a replication status of `PAUSED`\.  | 
| ReplicationNumFailedIndices |  The number of indexes that have a replication status of `FAILED`\.  | 
| AutoFollowNumSuccessStartReplication |  The number of follower indexes that have been successfully created by a replication rule for a specific connection\.   | 
| AutoFollowNumFailedStartReplication |  The number of follower indexes that failed to be created by a replication rule when there was a matching pattern\. This problem might arise due to a network issue on the remote cluster, or a security issue \(i\.e\. the associated role doesn't have permission to start replication\)\.  | 
| AutoFollowLeaderCallFailure |  Whether there have been any failed queries from the follower index to the leader index to pull new data\. A value of `1` means that there have been 1 or more failed calls in the last minute\.  | 

## Learning to Rank metrics<a name="managedomains-cloudwatchmetrics-learning-to-rank"></a>

Amazon OpenSearch Service provides the following metrics for [Learning to Rank](learning-to-rank.md)\.


| Metric | Description | 
| --- | --- | 
| LTRRequestTotalCount |  Total count of ranking requests\.  | 
| LTRRequestErrorCount |  Total count of unsuccessful requests\.  | 
| LTRStatus\.red |  Tracks if one of the indexes needed to run the plugin is red\.  | 
| LTRMemoryUsage |  Total memory used by the plugin\.  | 
| LTRFeatureMemoryUsageInBytes |  The amount of memory, in bytes, used by Learning to Rank feature fields\.  | 
| LTRFeaturesetMemoryUsageInBytes |  The amount of memory, in bytes, used by all Learning to Rank feature sets\.  | 
| LTRModelMemoryUsageInBytes |  The amount of memory, in bytes, used by all Learning to Rank models\.  | 

## Piped Processing Language metrics<a name="managedomains-cloudwatchmetrics-ppl"></a>

Amazon OpenSearch Service provides the following metrics for [Piped Processing Language](ppl-support.md)\.


| Metric | Description | 
| --- | --- | 
| PPLFailedRequestCountByCusErr |  The number of requests to the `_ppl` API that failed due to a client issue\. For example, a request might return HTTP status code 400 due to an `IndexNotFoundException`\.  | 
| PPLFailedRequestCountBySysErr |  The number of requests to the `_ppl` API that failed due to a server problem or feature limitation\. For example, a request might return HTTP status code 503 due to a `VerificationException`\.  | 
| PPLRequestCount |  The number of requests to the `_ppl` API\.   | 