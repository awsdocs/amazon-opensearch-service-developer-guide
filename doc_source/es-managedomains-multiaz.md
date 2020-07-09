# Configuring a Multi\-AZ Domain<a name="es-managedomains-multiaz"></a>

Each AWS Region is a separate geographic area with multiple, isolated locations known as *Availability Zones*\. To prevent data loss and minimize cluster downtime in the event of a service disruption, you can distribute nodes across two or three Availability Zones in the same Region, a configuration known as *Multi\-AZ*\.

For domains that run production workloads, we recommend the following configuration:
+ Choose a Region that supports three Availability Zones with Amazon ES\.
+ Deploy the domain across three zones\.
+ Choose current\-generation instance types for dedicated master nodes and data nodes\.
+ Use three dedicated master nodes and at least three data nodes\.
+ Create at least one replica for each index in your cluster\.

The rest of this section provides explanations for and context around these recommendations\.

## Shard Distribution<a name="es-managedomains-za-shards"></a>

If you enable Multi\-AZ, you should create at least one replica for each index in your cluster\. Without replicas, Amazon ES can't distribute copies of your data to other Availability Zones, which largely defeats the purpose of Multi\-AZ\. Fortunately, the default configuration for any index is a replica count of 1\. As the following diagram shows, Amazon ES makes a best effort to distribute primary shards and their corresponding replica shards to different zones\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/za-3-az.png)

In addition to distributing shards by Availability Zone, Amazon ES distributes them by node\. Still, certain domain configurations can result in imbalanced shard counts\. Consider the following domain:
+ 5 data nodes
+ 5 primary shards
+ 2 replicas
+ 3 Availability Zones

In this situation, Amazon ES has to overload one node in order to distribute the primary and replica shards across the zones, as shown in the following diagram\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/za-3-az-imbal.png)

To avoid these kinds of situations, which can strain individual nodes and hurt performance, we recommend that you choose an instance count that is a multiple of three if you plan to have two or more replicas per index\.

## Dedicated Master Node Distribution<a name="es-managedomains-za-dm"></a>

Even if you select two Availability Zones when configuring your domain, Amazon ES automatically distributes [dedicated master nodes](es-managedomains-dedicatedmasternodes.md) across three Availability Zones\. This distribution helps prevent cluster downtime if a zone experiences a service disruption\. If you use the recommended three dedicated master nodes and one Availability Zone goes down, your cluster still has a quorum \(2\) of dedicated master nodes and can elect a new master\. The following diagram demonstrates this configuration\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/za-2-az.png)

This automatic distribution has some notable exceptions:
+ If you choose an older\-generation instance type that is not available in three Availability Zones, the following scenarios apply:
  + If you chose three Availability Zones for the domain, Amazon ES throws an error\. Choose a different instance type, and try again\.
  + If you chose two Availability Zones for the domain, Amazon ES distributes the dedicated master nodes across two zones\.
+ Not all AWS Regions have three Availability Zones\. In these Regions, you can only configure a domain to use two zones \(and Amazon ES can only distribute dedicated master nodes across two zones\)\.

## Availability Zone Disruptions<a name="es-managedomains-za-summary"></a>

Availability Zone disruptions are rare, but do occur\. The following table lists different Multi\-AZ configurations and behaviors during a disruption\.


| Number of Availability Zones in a Region | Number of Availability Zones That You Chose | Number of Dedicated Master Nodes | Behavior if One Availability Zone Experiences a Disruption | 
| --- | --- | --- | --- | 
| 2 or more | 2 | 0 |  Downtime\. Your cluster loses half of its data nodes and must replace at least one in the remaining Availability Zone before it can elect a master\.  | 
| 2 | 2 | 3 |  50/50 chance of downtime\. Amazon ES distributes two dedicated master nodes into one Availability Zone and one into the other: [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-managedomains-multiaz.html)  | 
| 3 or more | 2 | 3 |  No downtime\. Amazon ES automatically distributes the dedicated master nodes across three Availability Zones, so the remaining two dedicated master nodes can elect a master\.  | 
| 3 or more | 3 | 0 |  No downtime\. Roughly two\-thirds of your data nodes are still available to elect a master\.  | 
| 3 or more | 3 | 3 |  No downtime\. The remaining two dedicated master nodes can elect a master\.  | 

In *all* configurations, regardless of the cause, node failures can cause the cluster's remaining data nodes to experience a period of increased load while Amazon ES automatically configures new nodes to replace the now\-missing ones\.

For example, in the event of an Availability Zone disruption in a three\-zone configuration, two\-thirds as many data nodes have to process just as many requests to the cluster\. As they process these requests, the remaining nodes are also replicating shards onto new nodes as they come online, which can further impact performance\. If availability is critical to your workload, consider adding resources to your cluster to alleviate this concern\.

**Note**  
Amazon ES manages Multi\-AZ domains transparently, so you can't manually simulate Availability Zone disruptions\.