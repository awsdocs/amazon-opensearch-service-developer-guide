# Dedicated master nodes in Amazon OpenSearch Service<a name="managedomains-dedicatedmasternodes"></a>

Amazon OpenSearch Service uses *dedicated master nodes* to increase cluster stability\. A dedicated master node performs cluster management tasks, but does not hold data or respond to data upload requests\. This offloading of cluster management tasks increases the stability of your domain\. Just like all other node types, you pay an hourly rate for each dedicated master node\.

Dedicated master nodes perform the following cluster management tasks:
+ Track all nodes in the cluster
+ Track the number of indexes in the cluster
+ Track the number of shards belonging to each index
+ Maintain routing information for nodes in the cluster
+ Update the cluster state after state changes, such as creating an index and adding or removing nodes in the cluster
+ Replicate changes to the cluster state across all nodes in the cluster
+ Monitor the health of all cluster nodes by sending *heartbeat signals*, periodic signals that monitor the availability of the data nodes in the cluster

The following illustration shows an OpenSearch Service domain with ten instances\. Seven of the instances are data nodes and three are dedicated master nodes\. Only one of the dedicated master nodes is active; the two gray dedicated master nodes wait as backup in case the active dedicated master node fails\. All data upload requests are served by the seven data nodes, and all cluster management tasks are offloaded to the active dedicated master node\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/DedicatedMasterNodes_no-caption.png)

## Choosing the number of dedicated master nodes<a name="dedicatedmasternodes-number"></a>

We recommend that you add **three** dedicated master nodes to each production OpenSearch Service domain\. Never choose an even number of dedicated master nodes\. Consider the following when choosing the number of dedicated master nodes:

1. One dedicated master node is explicitly prohibited by OpenSearch Service because you have no backup in the event of a failure\. You receive a validation exception if you try to create a domain with only one dedicated master node\.

1. Two dedicated master nodes means that your cluster doesn't have the necessary quorum of nodes to elect a new master node in the event of a failure\.

   A quorum is the number of dedicated master nodes / 2 \+ 1 \(rounded down to the nearest whole number\), which OpenSearch Service sets to `discovery.zen.minimum_master_nodes` when you create your domain\.

   In this case, 2 / 2 \+ 1 = 2\. Because one dedicated master node has failed and only one backup exists, the cluster doesn't have a quorum and can't elect a new master\.

1. Three dedicated master nodes, the recommended number, provides two backup nodes in the event of a master node failure and the necessary quorum \(2\) to elect a new master\.

1. Four dedicated master nodes are no better than three and can cause issues if you use [multiple Availability Zones](managedomains-multiaz.md)\.
   + If one master node fails, you have the quorum \(3\) to elect a new master\. If two nodes fail, you lose that quorum, just as you do with three dedicated master nodes\.
   + In a three Availability Zone configuration, two AZs have one dedicated master node, and one AZ has two\. If that AZ experiences a disruption, the remaining two AZs don't have the necessary quorum \(3\) to elect a new master\.

1. Having five dedicated master nodes works as well as three and allows you to lose two nodes while maintaining a quorum\. But because only one dedicated master node is active at any given time, this configuration means paying for four idle nodes\. Many users find this level of failover protection excessive\.

If a cluster has an even number of master\-eligible nodes, OpenSearch and Elasticsearch versions 7\.*x* and later ignore one node so that the voting configuration is always an odd number\. In this case, four dedicated master nodes are essentially equivalent to three \(and two to one\)\.

**Note**  
If your cluster doesn't have the necessary quorum to elect a new master node, write *and* read requests to the cluster both fail\. This behavior differs from the OpenSearch default\.

## Choosing instance types for dedicated master nodes<a name="dedicatedmasternodes-instance"></a>

Although dedicated master nodes don't process search and query requests, their size is highly correlated with the number of instances, indexes, and shards that they can manage\. For production clusters, we recommend the following instance types for dedicated master nodes\. These recommendations are based on typical workloads and can vary based on your needs\. Clusters with many shards or field mappings can benefit from larger instance types\. Monitor the [dedicated master node metrics](cloudwatch-alarms.md) to see if you need to use a larger instance type\.


|  **Instance count**  |  **Recommended minimum dedicated master instance type**  | 
| --- | --- | 
|  1–10  |  `m5.large.search` or `m6g.large.search`  | 
|  10–30  |  `c5.xlarge.search` or `c6g.xlarge.search`  | 
| 30–75 |  `c5.2xlarge.search` or `c6g.2xlarge.search`  | 
|  75–200  |  `r5.4xlarge.search` or `r6g.4xlarge.search`  | 
+ For information about how certain configuration changes can affect dedicated master nodes, see [Making configuration changes in Amazon OpenSearch Service](managedomains-configuration-changes.md)\.
+ For clarification on instance count limits, see [Domain and instance quotas](limits.md#clusterresource)\.
+ For more information about specific instance types, including vCPU, memory, and pricing, see [Amazon OpenSearch Service prices](https://aws.amazon.com/opensearch-service/pricing/)\.