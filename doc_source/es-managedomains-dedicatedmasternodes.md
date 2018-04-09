# Dedicated Master Nodes<a name="es-managedomains-dedicatedmasternodes"></a>

Amazon Elasticsearch Service uses *dedicated master nodes* to increase cluster stability\. A dedicated master node performs cluster management tasks, but does not hold data or respond to data upload requests\. This offloading of cluster management tasks increases the stability of your domain\.

We recommend that you allocate **three** dedicated master nodes for each production Amazon ES domain:

1. One dedicated master node means that you have no backup in the event of a failure\.

1. Two dedicated master nodes means that your cluster does not have the necessary quorum of nodes to elect a new master node in the event of a failure\.

   A quorum is Number of Dedicated Master Nodes / 2 \+ 1 \(rounded down to the nearest whole number\), which Amazon ES sets to `discovery.zen.minimum_master_nodes` when you create your domain\.

   In this case, 2 / 2 \+ 1 = 2\. Because one dedicated master node has failed and only one backup exists, the cluster does not have a quorum and cannot elect a new master\.

1. Three dedicated master nodes, the recommended number, provides two backup nodes in the event of a master node failure and the necessary quorum \(2\) to elect a new master\.

1. Four dedicated master nodes is no better than three and can cause issues if you use [zone awareness](es-managedomains.md#es-managedomains-zoneawareness)\.
   + If one master node fails, you have the quorum \(3\) to elect a new master\. If two nodes fail, you lose that quorum, just as you do with three dedicated master nodes\.
   + If each Availability Zone has two dedicated master nodes and the zones are unable to communicate with each other, neither zone has the quorum to elect a new master\.

1. Having five dedicated master nodes works as well as three and allows you to lose two nodes while maintaining a quorum, but because only one dedicated master node is active at any given time, this configuration means paying for four idle nodes\. Many customers find this level of failover protection excessive\.

Dedicated master nodes perform the following cluster management tasks:
+ Track all nodes in the cluster
+ Track the number of indices in the cluster
+ Track the number of shards belonging to each index
+ Maintain routing information for nodes in the cluster
+ Update the cluster state after state changes, such as creating an index and adding or removing nodes in the cluster
+ Replicate changes to the cluster state across all nodes in the cluster
+ Monitor the health of all cluster nodes by sending *heartbeat signals*, periodic signals that monitor the availability of the data nodes in the cluster

The following illustration shows an Amazon ES domain with ten instances\. Seven of the instances are data nodes and three are dedicated master nodes\. Only one of the dedicated master nodes is active; the two gray dedicated master nodes wait as backup in case the active dedicated master node fails\. All data upload requests are served by the seven data nodes, and all cluster management tasks are offloaded to the active dedicated master node\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/DedicatedMasterNodes_no-caption.png)

Although dedicated master nodes do not process search and query requests, their size is highly correlated with the number of instances, indices, and shards that they can manage\. For production clusters, we recommend the following instance types for dedicated master nodes\. These recommendations are based on typical workloads and can vary based on your needs\.


****  

|  **Instance Count**  |  **Recommended Minimum Dedicated Master Instance Type**  | 
| --- | --- | 
|  5–10  |  `m3.medium.elasticsearch`  | 
|  10–20  |  `m4.large.elasticsearch`  | 
| 20–50 |  `c4.xlarge.elasticsearch`  | 
|  50–100  |  `c4.2xlarge.elasticsearch`  | 
+ For recommendations on dedicated master nodes for large clusters, see [Petabyte Scale for Amazon Elasticsearch Service](petabyte-scale.md)\.
+ For information about how certain configuration changes can affect dedicated master nodes, see [About Configuration Changes](es-managedomains.md#es-managedomains-configuration-changes)\.
+ For clarification on instance count limits, see [Cluster and Instance Limits](aes-limits.md#clusterresource)\.
+ For more information about specific instance types, including vCPU, memory, and pricing, see [Amazon Elasticsearch Instance Prices](https://aws.amazon.com/elasticsearch-service/pricing/)\.