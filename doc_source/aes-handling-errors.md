# Handling AWS Service Errors<a name="aes-handling-errors"></a>

This section describes how to respond to common AWS service errors\. Consult the information in this section before contacting [AWS Support](https://aws.amazon.com/premiumsupport/)\. 

## Failed Cluster Nodes<a name="aes-handling-errors-failed-cluster-nodes"></a>

EC2 instances might experience unexpected terminations and restarts\. Typically, Amazon ES restarts the nodes for you\. However, it's possible for one or more nodes in an Elasticsearch cluster to remain in a failed condition\.

To check for this condition, open your domain dashboard on the Amazon ES console\. Choose the **Monitoring** tab, and then choose the **Nodes** metric\. See if the reported number of nodes is fewer than the number that you configured for your cluster\. If the metric shows that one or more nodes is down for more than one day, contact [AWS Support](https://aws.amazon.com/premiumsupport/)\.

You can also set a CloudWatch alarm to notify you when this issue occurs\.

**Note**  
The **Nodes** metric is not accurate during changes to your cluster configuration and during routine maintenance for the service\. This behavior is expected\. The metric will report the correct number of cluster nodes soon\. To learn more, see [[ERROR] BAD/MISSING LINK TEXT](es-managedomains.md#es-managedomains-configuration-changes)\.

To protect your clusters from unexpected node terminations and restarts, create at least one replica for each index in your Amazon ES domain\. For more information, see [Shards and Replicas](https://www.elastic.co/guide/en/elasticsearch/reference/current/_basic_concepts.html#getting-started-shards-and-replicas) in the Elasticsearch documentation\. 

## Red Cluster Status<a name="aes-handling-errors-red-cluster-status"></a>

A red cluster status means that primary and replica shards aren't allocated to nodes in your cluster, as described in [[ERROR] BAD/MISSING LINK TEXT](es-managedomains.md#es-managedomains-cloudwatchmetrics)\. Amazon ES doesn't take automatic snapshots, even of healthy indices, while the red cluster status persists\.

This state is commonly caused by the following:

+ Data nodes in the Elasticsearch cluster lack free storage space\.

+ The Elasticsearch process crashed due to a continuous heavy processing load on a data node\.

You must take action if the red cluster status is due to either of these common root causes\.

**Note**  
If your Amazon ES domain enters a red cluster status, AWS Support might contact you to ask whether you want to address the problem yourself or you want the support team to restore the latest automatic snapshot of the domain\. If you don't respond within seven days, AWS Support restores the latest automatic snapshot\.

### Recovering from a Lack of Free Storage Space<a name="aes-handling-errors-red-cluster-status-lack-of-free-space"></a>

To ensure that the data nodes in an Elasticsearch cluster don't run out of free storage space, monitor the **FreeStorageSpace** cluster metric\. Amazon ES can throw a `ClusterBlockException` when used space exceeds the Elasticsearch "watermarks\." To learn more, see [[ERROR] BAD/MISSING LINK TEXT](#aes-handling-errors-watermark)\. When this exception occurs, the affected cluster can't process configuration changes or benefit from software upgrades to the service\.

**To resolve red cluster status caused by red indices**

1. Use the `/_cat/indices` Elasticsearch API to determine which of the indices are unassigned to nodes in your cluster:

   ```
   curl -XGET 'http://<Elasticsearch_domain_endpoint>/_cat/indices'
   ```

   You can also use the `_cat/allocation?v` API to check shard allocation and disk usage:

   ```
   curl -XGET 'http://<Elasticsearch_domain_endpoint>/_cat/allocation?v'
   ```

   For more information, see [cat allocation](https://www.elastic.co/guide/en/elasticsearch/reference/current/cat-allocation.html) and [cat indices](https://www.elastic.co/guide/en/elasticsearch/reference/current/cat-indices.html) in the Elasticsearch documentation\.

1. Add EBS\-based storage, use larger instance types, or delete data from the affected indices\. For more information, see Configuring EBS Storage and Configuring Amazon ES Domains\.

You can create Amazon CloudWatch alarms to notify you when your cluster enters the red or yellow status\. For more information, see [[ERROR] BAD/MISSING LINK TEXT](cloudwatch-alarms.md)\.

### Recovering from a Continuous Heavy Processing Load<a name="aes-handling-errors-red-cluster-status-heavy-processing-load"></a>

To determine if a red cluster status is due to a continuous heavy processing load on a data node, monitor the following cluster metrics\.


****  

| Relevant Metric | Description | Recovery | 
| --- | --- | --- | 
| JVMMemoryPressure |  Specifies the percentage of the Java heap used for all data nodes in a cluster\. View the **Maximum** statistic for this metric, and look for smaller and smaller drops in memory pressure as the Java garbage collector fails to reclaim sufficient memory\. This pattern likely is due to complex queries or large data fields\. At 75% memory usage, Elasticsearch triggers the Concurrent Mark Sweep \(CMS\) garbage collector, which runs alongside other processes to keep pauses and disruptions to a minimum\. If CMS fails to reclaim enough memory and usage remains above 75%, Elasticsearch triggers a different garbage collection algorithm that halts or slows other processes in order to free up sufficient memory to prevent an out of memory error\. At 95% memory usage, Elasticsearch kills processes that attempt to allocate memory\. It might kill a critical process and bring down one or more nodes in the cluster\. The `_nodes/stats/jvm` command offers a useful summary of JVM statistics, memory pool usage, and garbage collection information: 

```
GET elasticsearch_domain_endpoint/_nodes/stats/jvm?pretty
```  |  Set memory circuit breakers for the JVM\. For more information, see [[ERROR] BAD/MISSING LINK TEXT](#aes-handling-errors-jvm_out_of_memory_error)\. If the problem persists, delete unnecessary indices, reduce the number or complexity of requests to the domain, add instances, or use larger instance types\.  | 
| CPUUtilization | Specifies the percentage of CPU resources used for data nodes in a cluster\. View the Maximum statistic for this metric, and look for a continuous pattern of high usage\. | Add data nodes or increase the size of the instance types of existing data nodes\. For more information, see Configuring Amazon ES Domains\.  | 
| Nodes | Specifies the number of nodes in a cluster\. View the Minimum statistic for this metric\. This value fluctuates when the service deploys a new fleet of instances for a cluster\. | Add data nodes\. For more information, see Configuring Amazon ES Domains\.  | 

## Yellow Cluster Status<a name="aes-handling-errors-yellow-cluster-status"></a>

A yellow cluster status means that the primary shards for all indices are allocated to nodes in a cluster, but the replica shards for at least one index are not\. Single\-node clusters always initialize with a yellow cluster status because there is no other node that Amazon ES can assign a replica to\. To achieve green cluster status, increase your node count\. For more information, see Configuring Amazon ES Domains in this guide and [Update Indices Settings](https://www.elastic.co/guide/en/elasticsearch/reference/1.4/indices-update-settings.html) in the Elasticsearch documentation\.

## ClusterBlockException<a name="aes-handling-errors-yellow-index-error"></a>

You might receive a `ClusterBlockException` error for the following issues\. 

### Logstash with Zero FreeStorageSpace<a name="aes-handling-errors-logstash-zero-space"></a>

Logstash might throw a `ClusterBlockException` for many reasons, including a lack of storage space that it can write to\. If you receive a `ClusterBlockException` error from Logstash while loading bulk data to your cluster, check if your cluster has run out of storage space\. For more information, see [[ERROR] BAD/MISSING LINK TEXT](#aes-handling-errors-watermark), Configuring Amazon ES Domains, and Configuring EBS Storage\.

### Block Disks Due to Low Memory<a name="aes-handling-errors-block-disks"></a>

When the **JVMMemoryPressure** metric exceeds 92% for 30 minutes, Amazon ES triggers a protection mechanism and blocks all write operations to prevent the cluster from reaching red status\. When the protection is on, write operations fail with a `ClusterBlockException` error, new indexes can't be created, and the `IndexCreateBlockException` error is thrown\.

When the **JVMMemoryPressure** metric returns to 88% or lower for five minutes, the protection is disabled, and write operations to the cluster are unblocked\.

### FreeStorageSpace Below Watermark<a name="aes-handling-errors-watermark"></a>

Elasticsearch has a default "low watermark" of 85%, meaning that once disk usage exceeds 85%, Elasticsearch no longer allocates shards to that node\. Elasticsearch also has a default "high watermark" of 90%, at which point it attempts to relocate shards to other nodes\. If no nodes have enough storage space to accommodate shard relocation, basic write operations like adding documents and creating indices can begin to fail\. To learn more, see [Disk\-based Shard Allocation](https://www.elastic.co/guide/en/elasticsearch/reference/current/disk-allocator.html) in the Elasticsearch documentation\.

To avoid these issues, you can monitor the `FreeStorageSpace` metric in the Amazon ES console and create CloudWatch alarms to trigger when `FreeStorageSpace` drops below a certain threshold\. For more information about correcting `FreeStorageSpace` issues, see [[ERROR] BAD/MISSING LINK TEXT](#aes-handling-errors-red-cluster-status-lack-of-free-space)\.

## JVM OutOfMemoryError<a name="aes-handling-errors-jvm_out_of_memory_error"></a>

A JVM `OutOfMemoryError` typically means that one of the following JVM circuit breakers was reached\.


****  

| Circuit Breaker | Description | Cluster Setting Property | 
| --- | --- | --- | 
| Parent Breaker | Total percentage of JVM heap memory allowed for all circuit breakers\. The default value is 70%\. | indices\.breaker\.total\.limitFor more information, see [Cluster Update Settings](https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-update-settings.html) in the Elasticsearch documentation\. | 
| Field Data Breaker | Percentage of JVM heap memory allowed to load a single data field into memory\. The default value is 60%\. If you upload data with large fields, we recommend raising this limit\. | indices\.breaker\.fielddata\.limitFor more information, see [Field data](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-fielddata.html) in the Elasticsearch documentation\. | 
| Request Breaker | Percentage of JVM heap memory allowed for data structures used to respond to a service request\. The default value is 40%\. If your service requests involve calculating aggregations, we recommend raising this limit\. | indices\.breaker\.request\.limitFor more information, see [Field data](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-fielddata.html) in the Elasticsearch documentation\. | 