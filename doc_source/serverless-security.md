# Overview of security in Amazon OpenSearch Serverless<a name="serverless-security"></a>

****  
***This is prerelease documentation for Amazon OpenSearch Serverless, which is in preview release\. The documentation and the feature are both subject to change\. We recommend that you use this feature only in test environments, and not in production environments\. For preview terms and conditions, see *Beta Service Participation* in [AWS Service Terms](https://aws.amazon.com/service-terms/)\. *** 

Security in Amazon OpenSearch Serverless differs fundamentally from security in Amazon OpenSearch Service in the following ways:


| Feature | OpenSearch Service | OpenSearch Serverless | 
| --- | --- | --- | 
| Data access control | Data access is determined by IAM policies and fine\-grained access control\. | Data access is determined by data access policies\. | 
| Encryption at rest | Encryption at rest is optional for domains\. | Encryption at rest is required for collections\. | 
| Security setup and administration | You must configure network, encryption, and data access individually for each domain\. | You can use security policies to manage security settings for multiple collections at scale\. | 

The following diagram illustrates the security components that make up a functional collection\. A collection must have an assigned encryption key, network access settings, and a matching data access policy that grants permission to its resources\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/serverless-security.png)

**Topics**
+ [Encryption policies](#serverless-security-encryption)
+ [Network policies](#serverless-security-network)
+ [Data access policies](#serverless-security-data-access)
+ [IAM and SAML authentication](#serverless-security-authentication)
+ [Getting started with security in Amazon OpenSearch Serverless](serverless-tutorials.md)
+ [Identity and Access Management for Amazon OpenSearch Serverless](security-iam-serverless.md)
+ [Encryption at rest for Amazon OpenSearch Serverless](serverless-encryption.md)
+ [Network access for Amazon OpenSearch Serverless](serverless-network.md)
+ [Data access control for Amazon OpenSearch Serverless](serverless-data-access.md)
+ [Access Amazon OpenSearch Serverless using an interface endpoint \(AWS PrivateLink\)](serverless-vpc.md)
+ [SAML authentication for Amazon OpenSearch Serverless](serverless-saml.md)

## Encryption policies<a name="serverless-security-encryption"></a>

[Encryption policies](serverless-encryption.md) define whether your collections are encrypted with an AWS owned key or a customer managed key\. Encryption policies consist of two components: a **resource pattern** and an **encryption key**\. The resource pattern defines which collection or collections the policy applies to\. The encryption key determines how the associated collections will be secured\.

To apply a policy to multiple collections, you include a wildcard \(\*\) in the policy rule\. For example, the following policy applies to all collections with names that begin with "logs"\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/serverless-security-encryption.png)

Encryption policies streamline the process of creating and managing collections, especially when you do so programmatically\. You can create a collection by simply specifying a name, and an encryption key is automatically assigned to it upon creation\. 

## Network policies<a name="serverless-security-network"></a>

[Network policies](serverless-network.md) define whether your collections are accessible over the internet from public networks, or whether they must be accessed through OpenSearch Serverless–managed VPC endpoints\. Just like encryption policies, network policies can apply to multiple collections, which allows you to manage network access for many collections at scale\.

Network policies consist of two components: an **access type** and a **resource type**\. The access type can either be public or VPC access\. The resource type determines whether the access you choose applies to the collection endpoint, the OpenSearch Dashboards endpoint, or both\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/serverless-security-network.png)

If you plan to configure VPC access within a network policy, you must first create one or more [OpenSearch Serverless\-managed VPC endpoints](serverless-vpc.md)\. These endpoints let you access OpenSearch Serverless as if it were in your VPC, without the use of an internet gateway, NAT device, VPN connection, or AWS Direct Connect connection\.

## Data access policies<a name="serverless-security-data-access"></a>

[Data access policies](serverless-data-access.md) define how your users access the data within your collections\. Data access policies help you manage collections at scale by automatically assigning access permissions to collections and indexes that match a specific pattern\. Multiple policies can apply to a single resource\.

Data access policies consist of a set of rules, each with three components: a **resource type**, **granted resources**, and a set of **permissions**\. The resource type can be a collection or index\. The granted resources can be collection/index names or patterns with a wildcard \(\*\)\. The list of permissions specifies which [OpenSearch API operations](serverless-genref.md#serverless-operations) the policy grants access to\. In addition, the policy contains a list of **principals**, which specify the IAM users, roles, and SAML identities to grant access to\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/serverless-data-access.png)

For more information about the format of a data access policy, see the [policy syntax](serverless-data-access.md#serverless-data-access-syntax)\.

Before you create a data access policy, you must have one or more IAM users or roles, or SAML identities, to provide access to in the policy\. For details, see the next section\.

## IAM and SAML authentication<a name="serverless-security-authentication"></a>

 IAM principals and SAML identities are one of the building blocks of a data access policy\. Within the `principal` statement of an access policy, you can include IAM users, IAM roles, and SAML identities\. These principals are then granted the permissions that you specify in the associated policy rules\.

```
[
   {
      "Rules":[
         {
            "ResourceType":"index",
            "Resource":[
               "index/marketing/orders*"
            ],
            "Permission":[
               "aoss:*"
            ]
         }
      ],
      "Principal":[
         "arn:aws:iam::123456789012:user/Dale",
         "arn:aws:iam::123456789012:role/RegulatoryCompliance",
         "saml/123456789012/myprovider/user/Annie"
      ]
   }
]
```

You configure SAML authentication directly within OpenSearch Serverless\. For more information, see [SAML authentication for Amazon OpenSearch Serverless](serverless-saml.md)\. 