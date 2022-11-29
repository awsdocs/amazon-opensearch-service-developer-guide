# Supported instance types in Amazon OpenSearch Service<a name="supported-instance-types"></a>

Amazon OpenSearch Service supports the following instance types\. Not all Regions support all instance types\. For availability details, see [Amazon OpenSearch Service pricing](https://aws.amazon.com/opensearch-service/pricing/)\.

For information about which instance type is appropriate for your use case, see [Sizing Amazon OpenSearch Service domains](sizing-domains.md), [EBS volume size quotas](limits.md#ebsresource), and [Network quotas](limits.md#network-limits)\.

## Current generation instance types<a name="latest-gen"></a>

For the best performance, we recommend that you use the following instance types when you create new OpenSearch Service domains\.


| Instance type | Instances | Restrictions | 
| --- | --- | --- | 
| C5 |  `c5.large.search` `c5.xlarge.search` `c5.2xlarge.search` `c5.4xlarge.search` `c5.9xlarge.search` `c5.18xlarge.search`  |  The C5 instance types require Elasticsearch 5\.1 or later or any version of OpenSearch\.  | 
| C6g |  `c6g.large.search` `c6g.xlarge.search` `c6g.2xlarge.search` `c6g.4xlarge.search` `c6g.8xlarge.search` `c6g.12xlarge.search`  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-instance-types.html)  | 
|  I3  |  `i3.large.search` `i3.xlarge.search` `i3.2xlarge.search` `i3.4xlarge.search` `i3.8xlarge.search` `i3.16xlarge.search`  | The I3 instance types require Elasticsearch 5\.1 or later or any version of OpenSearch, and do not support EBS storage volumes\. | 
|  M5  |  `m5.large.search` `m5.xlarge.search` `m5.2xlarge.search` `m5.4xlarge.search` `m5.12xlarge.search`  |  The M5 instance types require Elasticsearch 5\.1 or later or any version of OpenSearch\.  | 
| M6g |  `m6g.large.search` `m6g.xlarge.search` `m6g.2xlarge.search` `m6g.4xlarge.search` `m6g.8xlarge.search` `m6g.12xlarge.search`  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-instance-types.html)  | 
|  R5  |  `r5.large.search` `r5.xlarge.search` `r5.2xlarge.search` `r5.4xlarge.search` `r5.12xlarge.search`  |  The R5 instance types require Elasticsearch 5\.1 or later or any version of OpenSearch\.  | 
| R6g |  `r6g.large.search` `r6g.xlarge.search` `r6g.2xlarge.search` `r6g.4xlarge.search` `r6g.8xlarge.search` `r6g.12xlarge.search`  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-instance-types.html)  | 
| R6gd | `r6gd.large.search` `r6gd.xlarge.search` `r6gd.2xlarge.search` `r6gd.4xlarge.search` `r6gd.8xlarge.search` `r6gd.12xlarge.search` `r6gd.16xlarge.search` |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-instance-types.html)  | 
|  T3  | `t3.small.search` `t3.medium.search` |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-instance-types.html)  | 

## Previous generation instance types<a name="previous-gen"></a>

OpenSearch Service offers previous generation instance types for users who have optimized their applications around them and have yet to upgrade\. We encourage you to use current generation instance types to get the best performance, but we continue to support the following previous generation instance types\.


| Instance type | Instances | Restrictions | 
| --- | --- | --- | 
|  C4  | `c4.large.search` `c4.xlarge.search` `c4.2xlarge.search` `c4.4xlarge.search` `c4.8xlarge.search` |  | 
| I2 |  `i2.xlarge.search` `i2.2xlarge.search`  |  | 
|  M3  |  `m3.medium.search` `m3.large.search` `m3.xlarge.search` `m3.2xlarge.search`  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-instance-types.html)  | 
| M4 |  `m4.large.search` `m4.xlarge.search` `m4.2xlarge.search` `m4.4xlarge.search` `m4.10xlarge.search`  |  | 
|  R3  |  `r3.large.search` `r3.xlarge.search` `r3.2xlarge.search` `r3.4xlarge.search` `r3.8xlarge.search`  | The R3 instance types do not support encryption of data at rest or fine\-grained access control\. | 
| R4 |  `r4.large.search` `r4.xlarge.search` `r4.2xlarge.search` `r4.4xlarge.search` `r4.8xlarge.search` `r4.16xlarge.search`  |  | 
| T2 |  `t2.micro.search` `t2.small.search` `t2.medium.search`  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-instance-types.html)  | 

**Tip**  
We often recommend different instance types for [dedicated master nodes](managedomains-dedicatedmasternodes.md) and data nodes\.