# Supported instance types in Amazon OpenSearch Service<a name="supported-instance-types"></a>

Amazon OpenSearch Service supports the following instance types\. Not all Regions support all instance types\. For availability details, see [Amazon OpenSearch Service pricing](https://aws.amazon.com/opensearch-service/pricing/)\.

For information about which instance type is appropriate for your use case, see [Sizing Amazon OpenSearch Service domains](sizing-domains.md), [EBS volume size limits](limits.md#ebsresource), and [Network limits](limits.md#network-limits)\.

## Latest generation instance types<a name="latest-gen"></a>


| Instance type | Restrictions | 
| --- | --- | 
|  C5  |  The C5 instance types require Elasticsearch 5\.1 or later or any version of OpenSearch\.  | 
| C6G  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-instance-types.html)  | 
|  I3  | The I3 instance types require Elasticsearch 5\.1 or later or any version of OpenSearch, and do not support EBS storage volumes\. | 
|  M5  |  The M5 instance types require Elasticsearch 5\.1 or later or any version of OpenSearch\.  | 
| M6G |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-instance-types.html)  | 
|  R5  |  The R5 instance types require Elasticsearch 5\.1 or later or any version of OpenSearch\.  | 
| R6G |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-instance-types.html)  | 
| R6GD |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-instance-types.html)  | 
|  T3  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-instance-types.html)  | 

## Previous generation instance types<a name="previous-gen"></a>

The following instance types are from a previous generation\. We recommend using the instance types in the table above, which offer better performance at a lower cost\.


| Instance type | Restrictions | 
| --- | --- | 
|  C4  |  | 
|  I2  |    | 
|  M3  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-instance-types.html)  | 
|  M4  |    | 
|  R3  | The R3 instance types do not support encryption of data at rest or fine\-grained access control\. | 
|  R4  |    | 
|  T2  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-instance-types.html)  | 

**Tip**  
We often recommend different instance types for [dedicated master nodes](managedomains-dedicatedmasternodes.md) and data nodes\.