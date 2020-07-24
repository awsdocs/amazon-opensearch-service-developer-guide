# Petabyte Scale for Amazon Elasticsearch Service<a name="petabyte-scale"></a>

Amazon Elasticsearch Service domains offer attached storage of up to 3 PB\. You can configure a domain with 200 `i3.16xlarge.elasticsearch` instance types, each with 15 TB of storage\. Because of the sheer difference in scale, recommendations for domains of this size differ from [our general recommendations](aes-bp.md)\. This section discusses considerations for creating domains, costs, storage, and shard size\.

While this section frequently references the `i3.16xlarge.elasticsearch` instance types, you can use several other instance types to reach 1 PB of total domain storage\.

**Creating domains**  
Domains of this size exceed the default limit of 40 instances per domain\. To request a service limit increase of up to 200 instances per domain, open a case at the [AWS Support Center](https://console.aws.amazon.com/support/home#/)\.

**Pricing**  
Before creating a domain of this size, check the [Amazon Elasticsearch Service Pricing](https://aws.amazon.com/elasticsearch-service/pricing/) page to ensure that the associated costs match your expectations\. Examine [UltraWarm for Amazon Elasticsearch Service](ultrawarm.md) to see if a hot\-warm architecture fits your use case\.

**Storage**  
The `i3` instance types are designed to provide fast, local non\-volatile memory express \(NVMe\) storage\. Because this local storage tends to offer performance benefits when compared to Amazon Elastic Block Store, EBS volumes are not an option when you select these instance types in Amazon ES\. If you prefer EBS storage, use another instance type, such as `r5.12xlarge.elasticsearch`\.

**Shard size and count**  
A common Elasticsearch guideline is not to exceed 50 GB per shard\. Given the number of shards necessary to accommodate large domains and the resources available to `i3.16xlarge.elasticsearch` instances, we recommend a shard size of 100 GB\.  
For example, if you have 450 TB of source data and want one replica, your *minimum* storage requirement is closer to 450 TB \* 2 \* 1\.1 / 0\.95 = 1\.04 PB\. For an explanation of this calculation, see [Calculating Storage Requirements](sizing-domains.md#aes-bp-storage)\. Although 1\.04 PB / 15 TB = 70 instances, you might select 90 or more `i3.16xlarge.elasticsearch` instances to give yourself a storage safety net, deal with node failures, and account for some variance in the amount of data over time\. Each instance adds another 20 GiB to your minimum storage requirement, but for disks of this size, those 20 GiB are almost negligible\.  
Controlling the number of shards is tricky\. Elasticsearch users often rotate indices on a daily basis and retain data for a week or two\. In this situation, you might find it useful to distinguish between "active" and "inactive" shards\. Active shards are, well, actively being written to or read from\. Inactive shards might service some read requests, but are largely idle\. In general, you should keep the number of active shards below a few thousand\. As the number of active shards approaches 10,000, considerable performance and stability risks emerge\.  
To calculate the number of primary shards, use this formula: 450,000 GB \* 1\.1 / 100 GB per shard = 4,950 shards\. Doubling that number to account for replicas is 9,900 shards, which represents a major concern if all shards are active\. But if you rotate indices and only 1/7th or 1/14th of the shards are active on any given day \(1,414 or 707 shards, respectively\), the cluster might work well\. As always, the most important step of sizing and configuring your domain is to perform representative client testing using a realistic dataset\.