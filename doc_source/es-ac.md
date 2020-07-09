# Identity and Access Management in Amazon Elasticsearch Service<a name="es-ac"></a>

Amazon Elasticsearch Service offers several ways of controlling access to your domains\. This section covers the various policy types, how they interact with each other, and how to create your own, custom policies\.

**Important**  
VPC support introduces some additional considerations to Amazon ES access control\. For more information, see [About Access Policies on VPC Domains](es-vpc.md#es-vpc-security)\.

## Types of Policies<a name="es-ac-types"></a>

Amazon ES supports three types of access policies:
+ [Resource\-based Policies](#es-ac-types-resource)
+ [Identity\-based Policies](#es-ac-types-identity)
+ [IP\-based Policies](#es-ac-types-ip)

### Resource\-based Policies<a name="es-ac-types-resource"></a>

You add a resource\-based policy, sometimes called the domain access policy, when you create a domain\. These policies specify which actions a principal can perform on the domain's *subresources*\. Subresources include Elasticsearch indices and APIs\.

The [Principal](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html) element specifies the accounts, users, or roles that are allowed access\. The [Resource](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_resource.html) element specifies which subresources these principals can access\. The following resource\-based policy grants `test-user` full access \(`es:*`\) to `test-domain`:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": [
          "arn:aws:iam::123456789012:user/test-user"
        ]
      },
      "Action": [
        "es:*"
      ],
      "Resource": "arn:aws:es:us-west-1:987654321098:domain/test-domain/*"
    }
  ]
}
```

Two important considerations apply to this policy:
+ These privileges apply only to this domain\. Unless you create additional policies, `test-user` can't access data from other domains\.
+ The trailing `/*` in the `Resource` element is significant\. Resource\-based policies only apply to the domain's subresources, not the domain itself\.

  For example, `test-user` can make requests against an index \(`GET https://search-test-domain.us-west-1.es.amazonaws.com/test-index`\), but can't update the domain's configuration \(`POST https://es.us-west-1.amazonaws.com/2015-01-01/es/domain/test-domain/config`\)\. Note the difference between the two endpoints\. Accessing the [configuration API](es-configuration-api.md) requires an [identity\-based policy](#es-ac-types-identity)\.

To further restrict `test-user`, you can apply the following policy:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": [
          "arn:aws:iam::123456789012:user/test-user"
        ]
      },
      "Action": [
        "es:ESHttpGet"
      ],
      "Resource": "arn:aws:es:us-west-1:987654321098:domain/test-domain/test-index/_search"
    }
  ]
}
```

Now `test-user` can perform only one operation: searches against `test-index`\. All other indices within the domain are inaccessible, and without permissions to use the `es:ESHttpPut` or `es:ESHttpPost` actions, `test-user` can't add or modify documents\.

Next, you might decide to configure a role for power users\. This policy gives `power-user-role` access to the HTTP GET and PUT methods for all URIs in the index:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": [
          "arn:aws:iam::123456789012:role/power-user-role"
        ]
      },
      "Action": [
        "es:ESHttpGet",
        "es:ESHttpPut"
      ],
      "Resource": "arn:aws:es:us-west-1:987654321098:domain/test-domain/test-index/*"
    }
  ]
}
```

For information about all available actions, see [Policy Element Reference](#es-ac-reference)\.

### Identity\-based Policies<a name="es-ac-types-identity"></a>

Unlike resource\-based policies, which are a part of each Amazon ES domain, you attach identity\-based policies to users or roles using the AWS Identity and Access Management \(IAM\) service\. Just like [resource\-based policies](#es-ac-types-resource), identity\-based policies specify who can access a service, which actions they can perform, and if applicable, the resources on which they can perform those actions\.

While they certainly don't have to be, identity\-based policies tend to be more generic\. They often govern only the configuration API actions a user can perform\. After you have these policies in place, you can use resource\-based policies in Amazon ES to offer users access to Elasticsearch indices and APIs\.

Because identity\-based policies attach to users or roles \(principals\), the JSON doesn't specify a principal\. The following policy grants access to actions that begin with `Describe` and `List`\. This combination of actions provides read\-only access to domain configurations, but not to the data stored in the domain itself:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "es:Describe*",
        "es:List*"
      ],
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}
```

An administrator might have full access to Amazon ES and all data stored on all domains:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "es:*"
      ],
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}
```

For more information about the differences between resource\-based and identity\-based policies, see [IAM Policies](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies.html) in the *IAM User Guide*\.

**Note**  
Users with the AWS\-managed `AmazonESReadOnlyAccess` policy can't see cluster health status on the console\. To allow them to see cluster health status, add the `"es:ESHttpGet"` action to an access policy and attach it to their accounts or roles\.

### IP\-based Policies<a name="es-ac-types-ip"></a>

IP\-based policies restrict access to a domain to one or more IP addresses or CIDR blocks\. Technically, IP\-based policies are not a distinct type of policy\. Instead, they are just resource\-based policies that specify an anonymous principal and include a special [Condition](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_condition.html) element\.

The primary appeal of IP\-based policies is that they allow unsigned requests to an Amazon ES domain, which lets you use clients like [curl](https://curl.haxx.se/) and [Kibana](es-kibana.md) or access the domain through a proxy server\. To learn more, see [Using a Proxy to Access Amazon ES from Kibana](es-kibana.md#es-kibana-proxy)\.

**Note**  
If you enabled VPC access for your domain, you can't configure an IP\-based policy\. Instead, you can use [security groups](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_SecurityGroups.html) to control which IP addresses can access the domain\. For more information, see [About Access Policies on VPC Domains](es-vpc.md#es-vpc-security)\.

The following policy grants all HTTP requests that originate from the specified IP range access to `test-domain`:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "*"
      },
      "Action": [
        "es:ESHttp*"
      ],
      "Condition": {
        "IpAddress": {
          "aws:SourceIp": [
            "192.0.2.0/24"
          ]
        }
      },
      "Resource": "arn:aws:es:us-west-1:987654321098:domain/test-domain/*"
    }
  ]
}
```

If your domain has a public endpoint and doesn't use [fine\-grained access control](fgac.md), we recommend combining IAM principals and IP addresses\. This policy grants `test-user` HTTP access only if the request originates from the specified IP range:

```
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": {
      "AWS": [
        "arn:aws:iam::987654321098:user/test-user"
      ]
    },
    "Action": [
      "es:ESHttp*"
    ],
    "Condition": {
      "IpAddress": {
        "aws:SourceIp": [
          "192.0.2.0/24"
        ]
      }
    },
    "Resource": "arn:aws:es:us-west-1:987654321098:domain/test-domain/*"
  }]
}
```

## Making and Signing Amazon ES Requests<a name="es-managedomains-signing-service-requests"></a>

Even if you configure a completely open resource\-based access policy, *all* requests to the Amazon ES configuration API must be signed\. If your policies specify IAM users or roles, requests to the Elasticsearch APIs also must be signed using AWS Signature Version 4\. The signing method differs by API:
+ To make calls to the Amazon ES configuration API, we recommend that you use one of the [AWS SDKs](https://aws.amazon.com/tools/#sdk)\. The SDKs greatly simplify the process and can save you a significant amount of time compared to creating and signing your own requests\. The configuration API endpoints use the following format:

  ```
  es.region.amazonaws.com/2015-01-01/
  ```

  For example, the following request makes a configuration change to the `movies` domain, but you have to sign it yourself \(not recommended\):

  ```
  POST https://es.us-east-1.amazonaws.com/2015-01-01/es/domain/movies/config
  {
    "ElasticsearchClusterConfig": {
      "InstanceType": "c5.xlarge.elasticsearch"
    }
  }
  ```

  If you use one of the SDKs, such as [Boto 3](https://boto3.readthedocs.io/en/latest/reference/services/es.html#ElasticsearchService.Client.update_elasticsearch_domain_config), the SDK automatically handles the request signing:

  ```
  import boto3
  
  client = boto3.client('es')
  response = client.update_elasticsearch_domain_config(
    DomainName='movies',
    ElasticsearchClusterConfig={
      'InstanceType': 'c5.xlarge.elasticsearch'
    }
  )
  ```

  For a Java code sample, see [Using the AWS SDKs with Amazon Elasticsearch Service](es-configuration-samples.md)\.
+ To make calls to the Elasticsearch APIs, you must sign your own requests\. For sample code in a variety of languages, see [Signing HTTP Requests to Amazon Elasticsearch Service](es-request-signing.md)\. The Elasticsearch APIs use the following format:

  ```
  domain-id.region.es.amazonaws.com
  ```

  For example, the following request searches the `movies` index for *thor*:

  ```
  GET https://my-domain.us-east-1.es.amazonaws.com/movies/_search?q=thor
  ```

**Note**  
The service ignores parameters passed in URLs for HTTP POST requests that are signed with Signature Version 4\.

## When Policies Collide<a name="es-ac-conflict"></a>

Complexities arise when policies disagree or make no explicit mention of a user\. [Understanding How IAM Works](https://docs.aws.amazon.com/IAM/latest/UserGuide/intro-structure.html#intro-structure-authorization) in the *IAM User Guide* provides a concise summary of policy evaluation logic:
+ By default, all requests are denied\.
+ An explicit allow overrides this default\.
+ An explicit deny overrides any allows\.

For example, if a resource\-based policy grants you access to a domain, but an identity\-based policy denies you access, you are denied access\. If an identity\-based policy grants access and a resource\-based policy does not specify whether or not you should have access, you are allowed access\. See the following table of intersecting policies for a full summary of outcomes\.


****  

|  | Allowed in Resource\-based Policy | Denied in Resource\-based Policy | Neither Allowed nor Denied in Resource\-based Policy | 
| --- |--- |--- |--- |
| Allowed in Identity\-based Policy |  Allow  | Deny | Allow | 
| --- |--- |--- |--- |
| Denied in Identity\-based Policy | Deny | Deny | Deny | 
| --- |--- |--- |--- |
| Neither Allowed nor Denied in Identity\-based Policy | Allow | Deny | Deny | 
| --- |--- |--- |--- |

## Policy Element Reference<a name="es-ac-reference"></a>

Amazon ES supports most policy elements in the [IAM Policy Elements Reference](http://docs.aws.amazon.com/IAM/latest/UserGuide/AccessPolicyLanguage_ElementDescriptions.html), with the exception of `NotPrincipal`\. The following table shows the most common elements\.


****  

| JSON Policy Element | Summary | 
| --- | --- | 
| Version | The current version of the policy language is `2012-10-17`\. All access policies should specify this value\. | 
| Effect | This element specifies whether the statement allows or denies access to the specified actions\. Valid values are `Allow` or `Deny`\. | 
| Principal |  This element specifies the AWS account or IAM user or role that is allowed or denied access to a resource and can take several forms: [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-ac.html) Specifying the `*` wildcard enables anonymous access to the domain, which we don't recommend unless you add an [IP\-based condition](#es-ac-types-ip), use [VPC support](es-vpc.md), or enable [fine\-grained access control](fgac.md)\.  | 
| Action  | Amazon ES uses the following actions for HTTP methods:[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-ac.html)Amazon ES uses the following actions for the [configuration API](es-configuration-api.md):[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-ac.html)  You can use wildcards to specify a subset of actions, such as `"Action":"es:*"` or `"Action":"es:Describe*"`\. Certain `es:` actions support resource\-level permissions\. For example, you can give a user permissions to delete one particular domain without giving that user permissions to delete *any* domain\. Other actions apply only to the service itself\. For example, `es:ListDomainNames` makes no sense in the context of a single domain and thus requires a wildcard\.  Resource\-based policies differ from resource\-level permissions\. [Resource\-based policies](#es-ac-types-resource) are full JSON policies that attach to domains\. Resource\-level permissions let you restrict actions to particular domains or subresources\. In practice, you can think of resource\-level permissions as an optional part of a resource\- or identity\-based policy\. The following [identity\-based policy](#es-ac-types-identity) lists all `es:` actions and groups them according to whether they apply to the domain subresources \(`test-domain/*`\), to the domain configuration \(`test-domain`\), or only to the service \(`*`\):<pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [<br />    {<br />      "Effect": "Allow",<br />      "Action": [<br />        "es:ESHttpDelete",<br />        "es:ESHttpGet",<br />        "es:ESHttpHead",<br />        "es:ESHttpPost",<br />        "es:ESHttpPut",<br />        "es:ESHttpPatch"<br />      ],<br />      "Resource": "arn:aws:es:us-west-1:987654321098:domain/test-domain/*"<br />    },<br />    {<br />      "Effect": "Allow",<br />      "Action": [<br />        "es:CreateElasticsearchDomain",<br />        "es:DeleteElasticsearchDomain",<br />        "es:DescribeElasticsearchDomain",<br />        "es:DescribeElasticsearchDomainConfig",<br />        "es:DescribeElasticsearchDomains",<br />        "es:ESCrossClusterGet",<br />        "es:GetCompatibleElasticsearchVersions",<br />        "es:UpdateElasticsearchDomainConfig"<br />      ],<br />      "Resource": "arn:aws:es:us-west-1:987654321098:domain/test-domain"<br />    },<br />    {<br />      "Effect": "Allow",<br />      "Action": [<br />        "es:AddTags",<br />        "es:CreateElasticsearchServiceRole",<br />        "es:DeleteElasticsearchServiceRole",<br />        "es:DescribeElasticsearchInstanceTypeLimits",<br />        "es:DescribeReservedElasticsearchInstanceOfferings",<br />        "es:DescribeReservedElasticsearchInstances",<br />        "es:ESCrossClusterGet",<br />        "es:ListDomainNames",<br />        "es:ListElasticsearchInstanceTypeDetails",<br />        "es:ListElasticsearchInstanceTypes",<br />        "es:ListElasticsearchVersions",<br />        "es:ListTags",<br />        "es:PurchaseReservedElasticsearchInstanceOffering",<br />        "es:RemoveTags"<br />      ],<br />      "Resource": "*"<br />    }<br />  ]<br />}</pre>  While resource\-level permissions for `es:CreateElasticsearchDomain` might seem unintuitive—after all, why give a user permissions to create a domain that already exists?—the use of a wildcard lets you enforce a simple naming scheme for your domains, such as `"Resource": "arn:aws:es:us-west-1:987654321098:domain/my-team-name-*"`\.  Of course, nothing prevents you from including actions alongside less restrictive resource elements, such as the following:<pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [<br />    {<br />      "Effect": "Allow",<br />      "Action": [<br />        "es:ESHttpGet",<br />        "es:DescribeElasticsearchDomain"<br />      ],<br />      "Resource": "*"<br />    }<br />  ]<br />}</pre> To learn more about pairing actions and resources, see the `Resource` element in this table\. | 
| Condition |  Amazon ES supports most conditions that are described in [Available Global Condition Keys](http://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_condition-keys.html#AvailableKeys) in the *IAM User Guide*\. One notable exception is the `aws:SecureTransport` key, which Amazon ES does not support\. When configuring an [IP\-based policy](#es-ac-types-ip), you specify the IP addresses or CIDR block as a condition, such as the following: <pre>"Condition": {<br />  "IpAddress": {<br />    "aws:SourceIp": [<br />      "192.0.2.0/32"<br />    ]<br />  }<br />}</pre>  | 
| Resource |  Amazon ES uses `Resource` elements in three basic ways: [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-ac.html) For details about which actions support resource\-level permissions, see the `Action` element in this table\.  | 

## Advanced Options and API Considerations<a name="es-ac-advanced"></a>

Amazon ES has several advanced options, one of which has access control implications: `rest.action.multi.allow_explicit_index`\. At its default setting of true, it allows users to bypass subresource permissions under certain circumstances\.

For example, consider the following resource\-based policy:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": [
          "arn:aws:iam::123456789012:user/test-user"
        ]
      },
      "Action": [
        "es:ESHttp*"
      ],
      "Resource": [
        "arn:aws:es:us-west-1:987654321098:domain/test-domain/test-index/*",
        "arn:aws:es:us-west-1:987654321098:domain/test-domain/_bulk"
      ]
    },
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": [
          "arn:aws:iam::123456789012:user/test-user"
        ]
      },
      "Action": [
        "es:ESHttpGet"
      ],
      "Resource": "arn:aws:es:us-west-1:987654321098:domain/test-domain/restricted-index/*"
    }
  ]
}
```

This policy grants `test-user` full access to `test-index` and the Elasticsearch bulk API\. It also allows `GET` requests to `restricted-index`\.

The following indexing request, as you might expect, fails due to a permissions error:

```
PUT https://search-test-domain.us-west-1.es.amazonaws.com/restricted-index/movie/1
{
  "title": "Your Name",
  "director": "Makoto Shinkai",
  "year": "2016"
}
```

Unlike the index API, the bulk API lets you create, update, and delete many documents in a single call\. You often specify these operations in the request body, however, rather than in the request URL\. Because Amazon ES uses URLs to control access to domain subresources, `test-user` can, in fact, use the bulk API to make changes to `restricted-index`\. Even though the user lacks `POST` permissions on the index, the following request **succeeds**:

```
POST https://search-test-domain.us-west-1.es.amazonaws.com/_bulk
{ "index" : { "_index": "restricted-index", "_type" : "movie", "_id" : "1" } }
{ "title": "Your Name", "director": "Makoto Shinkai", "year": "2016" }
```

In this situation, the access policy fails to fulfill its intent\. To prevent users from bypassing these kinds of restrictions, you can change `rest.action.multi.allow_explicit_index` to false\. If this value is false, all calls to the bulk, mget, and msearch APIs that specify index names in the request body stop working\. In other words, calls to `_bulk` no longer work, but calls to `test-index/_bulk` do\. This second endpoint contains an index name, so you don't need to specify one in the request body\.

[Kibana](es-kibana.md) relies heavily on mget and msearch, so it is unlikely to work properly after this change\. For partial remediation, you can leave `rest.action.multi.allow_explicit_index` as true and deny certain users access to one or more of these APIs\.

For information about changing this setting, see [Advanced Options](es-createupdatedomains.md#es-createdomain-configure-advanced-options)\.

Similarly, the following resource\-based policy contains two subtle issues:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::123456789012:user/test-user"
      },
      "Action": "es:ESHttp*",
      "Resource": "arn:aws:es:us-west-1:987654321098:domain/test-domain/*"
    },
    {
      "Effect": "Deny",
      "Principal": {
        "AWS": "arn:aws:iam::123456789012:user/test-user"
      },
      "Action": "es:ESHTTP*",
      "Resource": "arn:aws:es:us-west-1:987654321098:domain/test-domain/restricted-index/*"
    }
  ]
}
```
+ Despite the explicit deny, `test-user` can still make calls such as `GET https://search-test-domain.us-west-1.es.amazonaws.com/_all/_search` and `GET https://search-test-domain.us-west-1.es.amazonaws.com/*/_search` to access the documents in `restricted-index`\.
+ Because the `Resource` element references `restricted-index/*`, `test-user` doesn't have permissions to directly access the index's documents\. The user does, however, have permissions to *delete the entire index*\. To prevent access and deletion, the policy instead must specify `restricted-index*`\.

Rather than mixing broad allows and focused denies, the safest approach is to follow the principle of [least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) and grant only the permissions that are required to perform a task\. For more information about controlling access to individual indices or Elasticsearch operations, see [Fine\-Grained Access Control in Amazon Elasticsearch Service](fgac.md)\.

## Configuring Access Policies<a name="es-ac-creating"></a>
+ For instructions on creating or modifying resource\- and IP\-based policies in Amazon ES, see [Configuring Access Policies](es-createupdatedomains.md#es-createdomain-configure-access-policies)\.
+ For instructions on creating or modifying identity\-based policies in IAM, see [Creating IAM Policies](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_create.html) in the *IAM User Guide*\.

## Additional Sample Policies<a name="es-ac-samples"></a>

Although this chapter includes many sample policies, AWS access control is a complex subject that is best understood through examples\. For more, see [Example Policies](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_examples.html) in the *IAM User Guide*\.