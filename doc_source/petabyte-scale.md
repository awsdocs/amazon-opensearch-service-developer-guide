# Petabyte Scale for Amazon Elasticsearch Service<a name="petabyte-scale"></a>

Amazon Elasticsearch Service offers domain storage of up to 1\.5 PB\. You can configure a domain with 100 `i3.16xlarge.elasticsearch` instance types, each with 15 TB of storage\. Because of the sheer difference in scale, recommendations for domains of this size differ from [our general recommendations](aes-bp.md)\. This section discusses considerations for creating domains, costs, storage, shard size, and dedicated master nodes\. Despite frequent references to the `i3` instance types, the shard size and dedicated master node recommendations in this section apply to any domain approaching petabyte scale\.

**Creating domains**  
Domains of this size exceed the default limit of 20 instances per domain\. To request a service limit increase of up to 100 instances per domain, open a case at the [AWS Support Center](https://console.aws.amazon.com/support/home#/)\.

**Pricing**  
Before creating a domain of this size, check the [Amazon Elasticsearch Service Pricing](https://aws.amazon.com/elasticsearch-service/pricing/) page to ensure that the associated costs match your expectations\.

**Storage**  
The `i3` instance types are specifically designed to provide fast, local non\-volatile memory express \(NVMe\) storage\. Because this local storage tends to offer considerable performance benefits when compared to Amazon Elastic Block Store, EBS volumes are not an option when you select these instance types in Amazon ES\.

**Shard size**  
A common Elasticsearch guideline is not to exceed 50 GB per shard\. Given the number of shards necessary to accommodate a 1\.5 PB storage requirement, we recommend a shard size of *at least* 100 GB\.  
For example, if you have 450 TB of source data and want one replica, your *minimum* storage requirement is closer to 450 TB \* 2 \* 1\.1 / 0\.95 = 1\.04 PB\. For an explanation of this calculation, see [Calculating Storage Requirements](sizing-domains.md#aes-bp-storage)\. Although 1\.04 PB / 15 TB = 70 instances, you might select 77 or more `i3.16xlarge.elasticsearch` instances to give yourself a storage safety net and account for some variance in the amount of data over time\. Each instance adds another 20 GB to your minimum storage requirement, but for disks of this size, those 20 GB are almost negligible\.  
To calculate the number of primary shards, use this formula: 450,000 GB \* 1\.1 / 150 GB per shard = 3,300 shards\. As always, the most important step of sizing and configuring your domain is to perform representative client testing using a realistic data set\.

**Dedicated master nodes**  
We recommend that you allocate three dedicated master nodes to each production Amazon ES domain\. Rather than our [usual guidelines for dedicated master nodes](es-managedomains-dedicatedmasternodes.md), however, we recommend more powerful instance types for domains of this size\. The following table shows recommended instance types for dedicated master nodes for large domains\.    
****    
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/petabyte-scale.html)