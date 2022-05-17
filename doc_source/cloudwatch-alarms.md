# Recommended CloudWatch alarms for Amazon OpenSearch Service<a name="cloudwatch-alarms"></a>

CloudWatch alarms perform an action when a CloudWatch metric exceeds a specified value for some amount of time\. For example, you might want AWS to email you if your cluster health status is `red` for longer than one minute\. This section includes some recommended alarms for Amazon OpenSearch Service and how to respond to them\.

For more information about setting alarms, see [Creating Amazon CloudWatch Alarms](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/AlarmThatSendsEmail.html) in the *Amazon CloudWatch User Guide*\.


****  
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/cloudwatch-alarms.html)

**Note**  
If you just want to *view* metrics, see [Monitoring OpenSearch cluster metrics with Amazon CloudWatch](managedomains-cloudwatchmetrics.md)\.

## Other alarms you might consider<a name="cw-alarms-additional"></a>

Consider configuring the following alarms depending on which OpenSearch Service features you regularly use\. 


| Alarm | Issue | 
| --- | --- | 
| WarmFreeStorageSpace minimum is <= 10240 for 1 minute, 1 consecutive time | An UltraWarm node in your cluster is down to 10 GiB of free storage space\. See [Lack of available storage space](handling-errors.md#handling-errors-watermark)\. This value is in MiB, so rather than 10240, we recommend setting it to 10% of the storage space for each UltraWarm node\. | 
| HotToWarmMigrationQueueSize is >= 20 for 1 minute, 3 consecutive times |  A high number of indices are concurrently moving from hot to UltraWarm storage\. Consider scaling your cluster\.   | 
| HotToWarmMigrationSuccessLatency is >= 1 day, 1 consecutive time |  Configure this alarm so that you're notified if the `HotToWarmMigrationSuccessCount` x latency is greater than 24 hours if you’re trying to roll daily indices\.  | 
| WarmJVMMemoryPressure maximum is >= 80% for 5 minutes, 3 consecutive times | The cluster could encounter out of memory errors if usage increases\. Consider scaling vertically\. OpenSearch Service uses half of an instance's RAM for the Java heap, up to a heap size of 32 GiB\. You can scale instances vertically up to 64 GiB of RAM, at which point you can scale horizontally by adding instances\. | 
| WarmToColdMigrationQueueSize is >= 20 for 1 minute, 3 consecutive times |  A high number of indices are concurrently moving from UltraWarm to cold storage\. Consider scaling your cluster\.   | 
| HotToWarmMigrationFailureCount is >= 1 for 1 minute, 1 consecutive time |  Migrations might fail during snapshots, shard relocations, or force merges\. Failures during snapshots or shard relocation are typically due to node failures or S3 connectivity issues\. Lack of disk space is usually the underlying cause of force merge failures\.  | 
| WarmToColdMigrationFailureCount is >= 1 for 1 minute, 1 consecutive time | Migrations usually fail when attempts to migrate index metadata to cold storage fail\. Failures can also happen when the warm index cluster state is being removed\. | 
| WarmToColdMigrationLatency is >= 1 day, 1 consecutive time |  Configure this alarm so that you're notified if the `WarmToColdMigrationSuccessCount` x latency is greater than 24 hours if you’re trying to roll daily indices\.  | 
| AlertingDegraded is >= 1 for 1 minute, 1 consecutive time |  Either the alerting index is red, or one or more nodes is not on schedule\.   | 
| ADPluginUnhealthy is >= 1 for 1 minute, 1 consecutive time |  The anomaly detection plugin isn't functioning properly, either because of high failure rates or because one of the indices being used is red\.  | 
| AsynchronousSearchFailureRate is >= 1 for 1 minute, 1 consecutive time |  At least one asynchronous search failed in the last minute, which likely means the coordinator node failed\. The lifecycle of an asynchronous search request is managed solely on the coordinator node, so if the coordinator goes down, the request fails\.  | 
| AsynchronousSearchStoreHealth is >= 1 for 1 minute, 1 consecutive time |  The health of the asynchronous search response store in the persisted index is red\. You might be storing large asynchronous responses, which can destabilize a cluster\. Try to limit your asynchronous search responses to 10 MB or less\.  | 
| SQLUnhealthy is >= 1 for 1 minute, 3 consecutive times |  The SQL plugin is returning 5*xx* response codes or passing invalid query DSL to OpenSearch\. Troubleshoot the requests your clients are making to the plugin\.   | 
| LTRStatus\.red is >= 1 for 1 minute, 1 consecutive time |  At least one of the indices needed to run the Learning to Rank plugin has missing primary shards and is not functional\.  | 
