# Best practices for Amazon OpenSearch Service<a name="bp"></a>

This chapter addresses some best practices for operating Amazon OpenSearch Service domains and provides general guidelines that apply to many use cases\. Production domains should adhere to the following standards:
+ Apply a restrictive [resource\-based access policy](ac.md#ac-types-resource) to the domain \(or enable fine\-grained access control\), and follow the [principle of least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) when granting access to the configuration API and the OpenSearch APIs\.
+ Configure at least one replica, the OpenSearch default, for each index\.
+ Use three [dedicated master nodes](managedomains-dedicatedmasternodes.md)\.
+ Deploy the domain across [three Availability Zones](managedomains-multiaz.md)\. This configuration lets OpenSearch Service distribute replica shards to different Availability Zones than their corresponding primary shards\.
+ [Upgrade to the latest OpenSearch versions](version-migration.md) as they become available on Amazon OpenSearch Service\.
+ [Update to the latest service software](service-software.md) as it becomes available\.
+ Size the domain appropriately for your workload\. For storage volume, shard size, and data node recommendations, see [Sizing Amazon OpenSearch Service domains](sizing-domains.md) and [Petabyte scale for Amazon OpenSearch Service](petabyte-scale.md)\. For dedicated master node recommendations, see [Dedicated master nodes in Amazon OpenSearch Service](managedomains-dedicatedmasternodes.md)\.
+ Have no more than 1,000 shards on any data node\. This limit is the default in OpenSearch and Elasticsearch 7\.*x* and later\. For a more nuanced guideline, see [Choosing the number of shards](sizing-domains.md#bp-sharding)\.
+ Use the latest\-generation instances available on the service\. For example, use I3 instances rather than I2 instances\.
+ Don't use T2 or `t3.small` instances for production domains; they can become unstable under sustained heavy load\. `t3.medium` instances are an option for small production workloads \(both as data nodes and dedicated master nodes\)\.
+ If appropriate for your network configuration, [create the domain within a VPC](vpc.md)\.
+ If your domain stores sensitive data, enable [encryption of data at rest](encryption-at-rest.md) and [node\-to\-node encryption](ntn.md)\.

For more information, see the remaining topics in this chapter\.

**Topics**
+ [Sizing Amazon OpenSearch Service domains](sizing-domains.md)
+ [Petabyte scale for Amazon OpenSearch Service](petabyte-scale.md)
+ [Dedicated master nodes in Amazon OpenSearch Service](managedomains-dedicatedmasternodes.md)
+ [Recommended CloudWatch alarms for Amazon OpenSearch Service](cloudwatch-alarms.md)