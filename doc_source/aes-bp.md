# Best practices for Amazon Elasticsearch Service<a name="aes-bp"></a>

This chapter addresses some best practices for operating Amazon Elasticsearch Service \(Amazon ES\) domains and provides general guidelines that apply to many use cases\. Production domains should adhere to the following standards:
+ Apply a restrictive [resource\-based access policy](es-ac.md#es-ac-types-resource) to the domain \(or enable fine\-grained access control\), and follow the [principle of least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) when granting access to the configuration API and the Elasticsearch APIs\.
+ Configure at least one replica, the Elasticsearch default, for each index\.
+ Use three [dedicated master nodes](es-managedomains-dedicatedmasternodes.md)\.
+ Deploy the domain across [three Availability Zones](es-managedomains-multiaz.md)\. This configuration lets Amazon ES distribute replica shards to different Availability Zones than their corresponding primary shards\.
+ [Upgrade to the latest Elasticsearch versions](es-version-migration.md) as they become available on Amazon Elasticsearch Service\.
+ [Update to the latest service software](es-service-software.md) as it becomes available\.
+ Size the domain appropriately for your workload\. For storage volume, shard size, and data node recommendations, see [Sizing Amazon Elasticsearch Service domains](sizing-domains.md) and [Petabyte scale for Amazon Elasticsearch Service](petabyte-scale.md)\. For dedicated master node recommendations, see [Dedicated master nodes in Amazon Elasticsearch Service](es-managedomains-dedicatedmasternodes.md)\.
+ Have no more than 1,000 shards on any data node\. This limit is the default in Elasticsearch 7\.*x* and later\. For a more nuanced guideline, see [Choosing the number of shards](sizing-domains.md#aes-bp-sharding)\.
+ Use the latest\-generation instances available on the service\. For example, use I3 instances rather than I2 instances\.
+ Don't use T2 or `t3.small` instances for production domains; they can become unstable under sustained heavy load\. `t3.medium` instances are an option for small production workloads \(both as data nodes and dedicated master nodes\)\.
+ If appropriate for your network configuration, [create the domain within a VPC](es-vpc.md)\.
+ If your domain stores sensitive data, enable [encryption of data at rest](encryption-at-rest.md) and [node\-to\-node encryption](ntn.md)\.

For more information, see the remaining topics in this chapter\.

**Topics**
+ [Sizing Amazon Elasticsearch Service domains](sizing-domains.md)
+ [Petabyte scale for Amazon Elasticsearch Service](petabyte-scale.md)
+ [Dedicated master nodes in Amazon Elasticsearch Service](es-managedomains-dedicatedmasternodes.md)
+ [Recommended CloudWatch alarms for Amazon Elasticsearch Service](cloudwatch-alarms.md)