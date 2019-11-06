# Supported Instance Types<a name="aes-supported-instance-types"></a>

Amazon ES supports the following instance types\. Not all regions support all instance types\. For availability details, see [Amazon Elasticsearch Service Pricing](https://aws.amazon.com/elasticsearch-service/pricing/)\.

For information about which instance type is appropriate for your use case, see [Sizing Amazon ES Domains](sizing-domains.md), [EBS Volume Size Limits](aes-limits.md#ebsresource), and [Network Limits](aes-limits.md#network-limits)\.


****  

| Instance Type | Restrictions | 
| --- | --- | 
|  C4  |   | 
|  C5  | The C5 instance types require Elasticsearch version 5\.1 or newer\. | 
|  I2  |   | 
|  I3  | The I3 instance types require Elasticsearch version 5\.1 or newer and do not support EBS storage volumes\. | 
|  M3  |  The M3 instance types do not support encryption of data at rest\.  | 
|  M4  |   | 
| M5 | The M5 instance types require Elasticsearch version 5\.1 or newer\. | 
|  R3  |  The R3 instance types do not support encryption of data at rest\.  | 
|  R4  |   | 
|  R5  | The R5 instance types require Elasticsearch version 5\.1 or newer\. | 
|  T2  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-instance-types.html)  | 

**Tip**  
You can use different instance types for [dedicated master nodes](es-managedomains-dedicatedmasternodes.md) and data nodes\.