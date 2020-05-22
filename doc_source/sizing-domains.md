# Sizing Amazon ES Domains<a name="sizing-domains"></a>

No surefire method of sizing Amazon ES domains exists, but by starting with an understanding of your storage needs, the service, and Elasticsearch itself, you can make an educated initial estimate on your hardware needs\. This estimate can serve as a useful starting point for the most critical aspect of sizing domains: testing them with representative workloads and monitoring their performance\.

**Topics**
+ [Calculating Storage Requirements](#aes-bp-storage)
+ [Choosing the Number of Shards](#aes-bp-sharding)
+ [Choosing Instance Types and Testing](#aes-bp-instances)

## Calculating Storage Requirements<a name="aes-bp-storage"></a>

Most Elasticsearch workloads fall into one of two broad categories:
+ Long\-lived index: You write code that processes data into one or more Elasticsearch indices and then updates those indices periodically as the source data changes\. Some common examples are website, document, and ecommerce search\.
+ Rolling indices: Data continuously flows into a set of temporary indices, with an indexing period and retention window, such as a set of daily indices that is retained for two weeks\. Some common examples are log analytics, time\-series processing, and clickstream analytics\.

For long\-lived index workloads, you can examine the source data on disk and easily determine how much storage space it consumes\. If the data comes from multiple sources, just add those sources together\.

For rolling indices, you can multiply the amount of data generated during a representative time period by the retention period\. For example, if you generate 200 MiB of log data per hour, that's 4\.7 GiB per day, which is 66 GiB of data at any given time if you have a two\-week retention period\.

The size of your source data, however, is just one aspect of your storage requirements\. You also have to consider the following:

1. Number of replicas: Each replica is a full copy of an index and needs the same amount of disk space\. By default, each Elasticsearch index has one replica\. We recommend at least one to prevent data loss\. Replicas also improve search performance, so you might want more if you have a read\-heavy workload\.

1. Elasticsearch indexing overhead: The on\-disk size of an index varies, but is often 10% larger than the source data\. After indexing your data, you can use the `_cat/indices?v` API and `pri.store.size` value to calculate the exact overhead\. `_cat/allocation?v` also provides a useful summary\.

1. Operating system reserved space: By default, Linux reserves 5% of the file system for the `root` user for critical processes, system recovery, and to safeguard against disk fragmentation problems\.

1. Amazon ES overhead: Amazon ES reserves 20% of the storage space of each instance \(up to 20 GiB\) for segment merges, logs, and other internal operations\.

   Because of this 20 GiB maximum, the total amount of reserved space can vary dramatically depending on the number of instances in your domain\. For example, a domain might have three `m4.xlarge.elasticsearch` instances, each with 500 GiB of storage space, for a total of 1\.46 TiB\. In this case, the total reserved space is only 60 GiB\. Another domain might have 10 `m3.medium.elasticsearch` instances, each with 100 GiB of storage space, for a total of 0\.98 TiB\. Here, the total reserved space is 200 GiB, even though the first domain is 50% larger\.

   In the following formula, we apply a "worst\-case" estimate for overhead that includes additional free space to help minimize the impact of node failures and Availability Zone outages\.

In summary, if you have 66 GiB of data at any given time and want one replica, your *minimum* storage requirement is closer to 66 \* 2 \* 1\.1 / 0\.95 / 0\.8 = 191 GiB\. You can generalize this calculation as follows:

**Source Data \* \(1 \+ Number of Replicas\) \* \(1 \+ Indexing Overhead\) / \(1 \- Linux Reserved Space\) / \(1 \- Amazon ES Overhead\) = Minimum Storage Requirement**

Or you can use this simplified version:

**Source Data \* \(1 \+ Number of Replicas\) \* 1\.45 = Minimum Storage Requirement**

Insufficient storage space is one of the most common causes of cluster instability, so you should cross\-check the numbers when you [choose instance types, instance counts, and storage volumes](#aes-bp-instances)\.

Other storage considerations exist:
+ If your minimum storage requirement exceeds 1 PB, see [Petabyte Scale for Amazon Elasticsearch Service](petabyte-scale.md)\.
+ If you have rolling indices and want to use a hot\-warm architecture, see [UltraWarm for Amazon Elasticsearch Service](ultrawarm.md)\.

## Choosing the Number of Shards<a name="aes-bp-sharding"></a>

After you understand your storage requirements, you can investigate your indexing strategy\. Each Elasticsearch index is split into some number of shards\. Because you can't easily change the number of primary shards for an existing index, you should decide about shard count *before* indexing your first document\.

The overarching goal of choosing a number of shards is to distribute an index evenly across all data nodes in the cluster\. However, these shards shouldn't be too large or too numerous\. A good rule of thumb is to try to keep shard size between 10–50 GiB\. Large shards can make it difficult for Elasticsearch to recover from failure, but because each shard uses some amount of CPU and memory, having too many small shards can cause performance issues and out of memory errors\. In other words, shards should be small enough that the underlying Amazon ES instance can handle them, but not so small that they place needless strain on the hardware\.

For example, suppose you have 66 GiB of data\. You don't expect that number to increase over time, and you want to keep your shards around 30 GiB each\. Your number of shards therefore should be approximately 66 \* 1\.1 / 30 = 3\. You can generalize this calculation as follows:

 **\(Source Data \+ Room to Grow\) \* \(1 \+ Indexing Overhead\) / Desired Shard Size = Approximate Number of Primary Shards**

This equation helps compensate for growth over time\. If you expect those same 67 GiB of data to quadruple over the next year, the approximate number of shards is \(66 \+ 198\) \* 1\.1 / 30 = 10\. Remember, though, you don't have those extra 198 GiB of data *yet*\. Check to make sure that this preparation for the future doesn't create unnecessarily tiny shards that consume huge amounts of CPU and memory in the present\. In this case, 66 \* 1\.1 / 10 shards = 7\.26 GiB per shard, which will consume extra resources and is below the recommended size range\. You might consider the more middle\-of\-the\-road approach of six shards, which leaves you with 12 GiB shards today and 48 GiB shards in the future\. Then again, you might prefer to start with three shards and reindex your data when the shards exceed 50 GiB\.

A far less common issue involves limiting the number of shards per node\. If you size your shards appropriately, you typically run out of disk space long before encountering this limit\. For example, an `m5.large.elasticsearch` instance has a maximum disk size of 512 GiB\. If you stay below 80% disk usage and size your shards at 20 GiB, it can accommodate approximately 20 shards\. Elasticsearch 7\.*x* and later have a limit of *1,000* shards per node, adjustable using the `cluster.max_shards_per_node` setting\.

Sizing shards appropriately almost always keeps you below this limit, but you can also consider the number of shards for each GiB of Java heap\. On a given node, have no more than 20 shards per GiB of Java heap\. For example, an `m5.large.elasticsearch` instance has a 4 GiB heap, so each node should have no more than 80 shards\. At that shard count, each shard is roughly 5 GiB in size, which is well below our recommendation\.

## Choosing Instance Types and Testing<a name="aes-bp-instances"></a>

After you calculate your storage requirements and choose the number of shards that you need, you can start to make hardware decisions\. Hardware requirements vary dramatically by workload, but we can still offer some basic recommendations\.

In general, [the storage limits](aes-limits.md) for each instance type map to the amount of CPU and memory that you might need for light workloads\. For example, an `m4.large.elasticsearch` instance has a maximum EBS volume size of 512 GiB, 2 vCPU cores, and 8 GiB of memory\. If your cluster has many shards, performs taxing aggregations, updates documents frequently, or processes a large number of queries, those resources might be insufficient for your needs\. If you believe your cluster falls into one of these categories, try starting with a configuration closer to 2 vCPU cores and 8 GiB of memory for every 100 GiB of your storage requirement\.

**Tip**  
For a summary of the hardware resources that are allocated to each instance type, see [Amazon Elasticsearch Service Pricing](https://aws.amazon.com/elasticsearch-service/pricing/)\.

Still, even those resources might be insufficient\. Some Elasticsearch users report that they need many times those resources to fulfill their requirements\. Finding the right hardware for your workload means making an educated initial estimate, testing with representative workloads, adjusting, and testing again:

1. To start, we recommend a minimum of three nodes to avoid potential Elasticsearch issues, such as split brain\. If you have three [dedicated master nodes](es-managedomains-dedicatedmasternodes.md), we still recommend a minimum of two data nodes for replication\.

1. If you have a 184 GiB storage requirement and the recommended minimum number of three nodes, use the equation 184 / 3 = 61 GiB to find the amount of storage that each node needs\. In this example, you might select three `m5.large.elasticsearch` instances, each using a 90 GiB EBS storage volume so that you have a safety net and some room for growth over time\. This configuration provides 6 vCPU cores and 24 GiB of memory, so it's suited to lighter workloads\.

   For a more substantial example, consider a 14 TiB \(14,336 GiB\) storage requirement and a heavy workload\. In this case, you might choose to begin testing with 2 \* 144 = 288 vCPU cores and 8 \* 144 = 1152 GiB of memory\. These numbers work out to approximately 18 `i3.4xlarge.elasticsearch` instances\. If you don't need the fast, local storage, you could also test 18 `r5.4xlarge.elasticsearch` instances, each using a 1 TiB EBS storage volume\.

   If your cluster includes hundreds of terabytes of data, see [Petabyte Scale for Amazon Elasticsearch Service](petabyte-scale.md)\.

1. After configuring the cluster, you can [add your indices](es-indexing.md) using the number of shards you calculated earlier, perform some representative client testing using a realistic dataset, and [monitor CloudWatch metrics](es-managedomains-cloudwatchmetrics.md) to see how the cluster handles the workload\.

1. If performance satisfies your needs, tests succeed, and CloudWatch metrics are normal, the cluster is ready to use\. Remember to [set CloudWatch alarms](cloudwatch-alarms.md) to detect unhealthy resource usage\.

   If performance isn't acceptable, tests fail, or `CPUUtilization` or `JVMMemoryPressure` are high, you might need to choose a different instance type \(or add instances\) and continue testing\. As you add instances, Elasticsearch automatically rebalances the distribution of shards throughout the cluster\.

   Because it is easier to measure the excess capacity in an overpowered cluster than the deficit in an underpowered one, we recommend starting with a larger cluster than you think you need\. Next, test and scale down to an efficient cluster that has the extra resources to ensure stable operations during periods of increased activity\.

Production clusters or clusters with complex states benefit from [dedicated master nodes](es-managedomains-dedicatedmasternodes.md), which improve performance and cluster reliability\.