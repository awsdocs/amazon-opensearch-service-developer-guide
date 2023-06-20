# Configuring a multi\-AZ domain in Amazon OpenSearch Service<a name="managedomains-multiaz"></a>

To prevent data loss and minimize Amazon OpenSearch Service cluster downtime in the event of a service disruption, you can distribute nodes across two or three *Availability Zones* in the same Region, a configuration known as Multi\-AZ\. Availability Zones are isolated locations within each AWS Region\.

For domains that run production workloads, we recommend the Multi\-AZ with Standby deployment option, which creates the following configuration:
+ The domain deployed across three zones\.
+ Current\-generation instance types for dedicated master nodes and data nodes\.
+ Three dedicated master nodes and three \(or a multiple of three\) data nodes\. 
+ At least two replicas for each index in your domain, or a multiple of three copies of data \(including both primary nodes and replicas\)\.

The rest of this section provides explanations for and context around these configurations\.

## Multi\-AZ with Standby<a name="managedomains-za-standby"></a>

Multi\-AZ with Standby is a deployment option for Amazon OpenSearch Service domains that offers 99\.99% availability, consistent performance for production workloads, and simplified domain configuration and management\. When you use Multi\-AZ with Standby, domains are resilient to infrastructure failures, with no impact to performance or availability\. This deployment option achieves this standard by mandating a number best practices, such as a specified data node count, master node count, instance type, replica count, software update settings, and Auto\-Tune turned on\.

When you use Multi\-AZ with Standby, OpenSearch Service creates a domain across three Availability Zones, with each zone containing a complete copy of data and with the data equally distributed in each of the zones\. Your domain reserves nodes in one of these zones as standby, which means that they don't serve search requests\. When OpenSearch Service detects a failure in the underlying infrastructure, it automatically activates the standby nodes in less than a minute\. The domain continues to serve indexing and search requests, and any impact is limited to the time it takes to perform the failover\. There is no redistribution of data or resources, which results in unaffected cluster performance and no risk of degraded availability\. Multi\-AZ with Standby is available at no extra cost\.

You have two options to create a domain with standby on the AWS Management Console\. First, you can create a domain with the **Easy create** creation method, and OpenSearch Service will automatically use a predetermined configuration, which includes the following:
+ Three Availability Zones, with one acting as a standby
+ Three dedicated master node and data nodes
+ Auto\-Tune enabled on the domain
+ GP3 storage for the data nodes

You can also choose the **Standard create** creation method and select **Domain with standby** as your deployment option\. This allows you to customize your domain while still mandating key features of standby, such as three zones and three master nodes\. We recommend choosing a data node count that's a multiple of three \(the number of Availability Zones\)\.

Once you've created your domain, you can navigate to the domain details pages and, in the **Cluster configuration** tab, confirm that *3\-AZ with standby* appears under Availability Zone\(s\)\.

If you have problems migrating an existing domain to Multi\-AZ with Standby, see [Error migrating to Multi\-AZ with Standby](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/handling-errors.html#troubleshooting-multi-az-standby) in the troubleshooting guide\.

### Limitations<a name="limitations"></a>

When you set up a domain with Multi\-AZ with Standby, consider the following limitations: 
+ The total number of shards on a node can't exceed 1000, the total number of shards on a cluster can't exceed 75000, and the size of a single shard can't exceed 65 GB\.
+ Multi\-AZ with Standby only works with the `m5`, `c5`, `r5`, `r6g`, `c6g`, `m6g`, `r6gd` and `i3` instance types\. For more information on supported instances, see [Supported instance types](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-instance-types.html)\.
+ You can only used GP3 and instance\-backed storage with standby\.

## Multi\-AZ without Standby<a name="managedomains-za-no-standby"></a>

OpenSearch Service still supports multi\-AZ without Standby, which offers 99\.9% availability\. Nodes are distributed across Availability Zone\(s\), and availability depends on the number of Availability Zones and copies of data\. Whereas with standby you have to configure your domain with best practices, without standby you can choose your own number of Availability Zones, nodes, and replicas\. We don't recommend this option unless you have existing workflows that would be disrupted by creating domains with standby\.

If you choose this option, we still recommend that you select three Availability Zones in order to remain resilient to node, disk, and single\-AZ failures\. When a failure occurs, the cluster redistributes data across the remaining resources to maintain availability and redundancy\. This data movement increases resource usage on the cluster, and can have an impact on the performance\. If the cluster isn't sized properly, it can experience degraded availability, which largely defeats the purpose of multi\-AZ\.

The only way to configure a domain without standby on the AWS Management Console is to choose the **Standard create** creation method, and select **Domain without standby** as your deployment option\.

### Shard distribution<a name="shards"></a>

If you enable multi\-AZ without Standby, you should create at least one replica for each index in your cluster\. Without replicas, OpenSearch Service can't distribute copies of your data to other Availability Zones\. Fortunately, the default configuration for any index is a replica count of 1\. As the following diagram shows, OpenSearch Service makes a best effort to distribute primary shards and their corresponding replica shards to different zones\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/za-3-az.png)

In addition to distributing shards by Availability Zone, OpenSearch Service distributes them by node\. Still, certain domain configurations can result in imbalanced shard counts\. Consider the following domain:
+ 5 data nodes
+ 5 primary shards
+ 2 replicas
+ 3 Availability Zones

In this situation, OpenSearch Service has to overload one node in order to distribute the primary and replica shards across the zones, as shown in the following diagram\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/za-3-az-imbal.png)

To avoid these kinds of situations, which can strain individual nodes and hurt performance, we recommend that you choose multi\-AZ with Standby, or choose an instance count that is a multiple of three when you plan to have two or more replicas per index\.

### Dedicated master node distribution<a name="dm"></a>

Even if you select two Availability Zones when configuring your domain, OpenSearch Service automatically distributes [dedicated master nodes](managedomains-dedicatedmasternodes.md) across three Availability Zones\. This distribution helps prevent cluster downtime if a zone experiences a service disruption\. If you use the recommended three dedicated master nodes and one Availability Zone goes down, your cluster still has a quorum \(2\) of dedicated master nodes and can elect a new master\. The following diagram demonstrates this configuration\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/za-2-az.png)

This automatic distribution has some notable exceptions:
+ If you choose an older\-generation instance type that is not available in three Availability Zones, the following scenarios apply:
  + If you chose three Availability Zones for the domain, OpenSearch Service throws an error\. Choose a different instance type, and try again\.
  + If you chose two Availability Zones for the domain, OpenSearch Service distributes the dedicated master nodes across two zones\.
+ Not all AWS Regions have three Availability Zones\. In these Regions, you can only configure a domain to use two zones \(and OpenSearch Service can only distribute dedicated master nodes across two zones\)\.

## Availability zone disruptions<a name="managedomains-za-summary"></a>

Availability Zone disruptions are rare, but do occur\. The following table lists different Multi\-AZ configurations and behaviors during a disruption\. The last row in the table applies to Multi\-AZ with Standby, while all other rows have configurations that only apply to Multi\-AZ without Standby\.


| Number of Availability Zones in a region | Number of Availability Zones you chose | Number of dedicated master nodes | Behavior if one Availability Zone experiences a disruption | 
| --- | --- | --- | --- | 
| 2 or more | 2 | 0 |  Downtime\. Your cluster loses half of its data nodes and must replace at least one in the remaining Availability Zone before it can elect a master\.  | 
| 2 | 2 | 3 |  50/50 chance of downtime\. OpenSearch Service distributes two dedicated master nodes into one Availability Zone and one into the other: [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/managedomains-multiaz.html)  | 
| 3 or more | 2 | 3 |  No downtime\. OpenSearch Service automatically distributes the dedicated master nodes across three Availability Zones, so the remaining two dedicated master nodes can elect a master\.  | 
| 3 or more | 3 | 0 |  No downtime\. Roughly two\-thirds of your data nodes are still available to elect a master\.  | 
| 3 or more | 3 | 3 |  No downtime\. The remaining two dedicated master nodes can elect a master\.  | 

In *all* configurations, regardless of the cause, node failures can cause the cluster's remaining data nodes to experience a period of increased load while OpenSearch Service automatically configures new nodes to replace the now\-missing ones\.

For example, in the event of an Availability Zone disruption in a three\-zone configuration, two\-thirds as many data nodes have to process just as many requests to the cluster\. As they process these requests, the remaining nodes are also replicating shards onto new nodes as they come online, which can further impact performance\. If availability is critical to your workload, consider adding resources to your cluster to alleviate this concern\.

**Note**  
OpenSearch Service manages Multi\-AZ domains transparently, so you can't manually simulate Availability Zone disruptions\.