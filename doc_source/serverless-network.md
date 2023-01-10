# Network access for Amazon OpenSearch Serverless<a name="serverless-network"></a>

****  
***This is prerelease documentation for Amazon OpenSearch Serverless, which is in preview release\. The documentation and the feature are both subject to change\. We recommend that you use this feature only in test environments, and not in production environments\. For preview terms and conditions, see *Beta Service Participation* in [AWS Service Terms](https://aws.amazon.com/service-terms/)\. *** 

The network settings for an Amazon OpenSearch Serverless collection determine whether the collection is accessible over the internet from public networks, or whether it must be accessed through OpenSearch Serverless–managed VPC endpoints\. You can configure network access separately for a collection's *OpenSearch* endpoint and its corresponding *OpenSearch Dashboards* endpoint\.

Network access is the isolation mechanism for allowing access from different source networks\. For example, if a collection's OpenSearch Dashboards endpoint is publically accessible but theOpenSearch API endpoint isn't, then a user can access the collection data only through Dashboards when connecting from a public network\. If they try to call the OpenSearch APIs directly from a public network, it would be blocked\. Network settings can be used for such permutations of source to resource\-type\.

**Topics**
+ [Network policies](#serverless-network-policies)
+ [Considerations](#serverless-network-considerations)
+ [Permissions required](#serverless-network-permissions)
+ [Policy precedence](#serverless-network-precedence)
+ [Creating network policies \(console\)](#serverless-network-console)
+ [Creating network policies \(AWS CLI\)](#serverless-network-cli)
+ [Viewing network policies](#serverless-network-list)
+ [Updating network policies](#serverless-network-update)
+ [Deleting network policies](#serverless-network-delete)

## Network policies<a name="serverless-network-policies"></a>

Network policies let you manage many collections at scale by automatically assigning network access settings to collections that match the rules defined in the policy\.

In a network policy, you specify a series of *rules*\. These rule define access permissions to collection endpoints and OpenSearch Dashboards endpoints\. Each rule consists of an access type \(public or VPC\) and a resource type \(collection and/or OpenSearch Dashboards endpoint\)\. For each resource type \(`collection` and `dashboard`\), you specify a series of rules that define which collection\(s\) the policy will apply to\.

In this sample policy, the first rule specifies VPC access to both the collection endpoint and the Dashboards endpoint for all collections beginning with the term `marketing*`\. The second rule specifies public access to the `finance` collection, but only for the collection endpoint \(no Dashboards access\)\.

```
[
   {
      "Description":"Marketing access",
      "Rules":[
         {
            "ResourceType":"collection",
            "Resource":[
               "collection/marketing*"
            ]
         },
         {
            "ResourceType":"dashboards",
            "Resource":[
               "collection/marketing*"
            ]
         }
      ],
      "AllowFromPublic":false,
      "SourceVPCEs":[
         "vpce-050f79086ee71ac05"
      ]
   },
   {
      "Description":"Sales access",
      "Rules":[
         {
            "ResourceType":"collection",
            "Resource":[
               "collection/finance"
            ]
         }
      ],
      "AllowFromPublic":true
   }
]
```

This policy provides public access only to OpenSearch Dashboards for collections beginning with "finance"\. Programmatic access is not provided, so any attempts to directly access the OpenSearch API will fail\.

```
[
  {
    "Description": "Dashboards access",
    "Rules": [
      {
        "ResourceType": "dashboard",
        "Resource": [
          "collection/finance*"
        ]
      }
    ],
    "AllowFromPublic": true
  }
]
```

Network policies can apply to existing collections as well as future collections\. For example, you can create a collection and then create a network policy with a rule that matches the collection name\. You don't need to create network policies before you create collections\.

## Considerations<a name="serverless-network-considerations"></a>

Consider the following when you configure network access for your collections:
+ If you plan to configure VPC access for a collection, you must first create at least one [OpenSearch Serverless\-managed VPC endpoint](serverless-vpc.md)\.
+ If a collection is accessible from public networks, it's also accessible from all OpenSearch Serverless–managed VPCs\.
+ Multiple network policies can apply to a single collection\. For more information, see [Policy precedence](#serverless-network-precedence)\.

## Permissions required<a name="serverless-network-permissions"></a>

Network access for OpenSearch Serverless uses the following AWS Identity and Access Management \(IAM\) permissions\. You can specify IAM conditions to restrict users to network policies associated with specific collections\.
+ `aoss:CreateSecurityPolicy` – Create a network access policy\.
+ `aoss:ListSecurityPolicies` – List all network policies in the current account\.
+ `aoss:GetSecurityPolicy` – View a network access policy specification\.
+ `aoss:UpdateSecurityPolicy` – Modify a given network access policy, and change the VPC ID or public access designation\.
+ `aoss:DeleteSecurityPolicy` – Delete a network access policy \(after it's detached from all collections\)\.

The following identity\-based access policy allows a user to view all network policies, and update policies with the resource pattern `collection/application-logs`:

```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "aoss:UpdateSecurityPolicy"
            ],
            "Resource": "*",
            "Condition": {
                "StringEquals": {
                    "aoss:collection": "application-logs"
                }
            }
        },
        {
            "Effect": "Allow",
            "Action": [
                "aoss:ListSecurityPolicies",
                "aoss:GetSecurityPolicy"
            ],
            "Resource": "*"
        }
    ]
}
```

## Policy precedence<a name="serverless-network-precedence"></a>

There can be situations where network policy rules overlap, within or across policies\. When this happens, a rule that specifies public access overrides a rule that specifies VPC access for any collections that are common to *both* rules\.

For example, in the following policy, both rules assign network access to the `finance` collection, but one rule specifies VPC access while the other specifies public access\. In this situation, public access overrides VPC access *only for the finance collection* \(because it exists in both rules\), so the finance collection will be accessible from public networks\. The sales collection will have VPC access from the specified endpoint\.

```
[
   {
      "Description":"Rule 1",
      "Rules":[
         {
            "ResourceType":"collection",
            "Resource":[
               "collection/sales",
               "collection/finance"
            ]
         }
      ],
      "AllowFromPublic":false,
      "SourceVPCEs":[
         "vpce-050f79086ee71ac05"
      ]
   },
   {
      "Description":"Rule 2",
      "Rules":[
         {
            "ResourceType":"collection",
            "Resource":[
               "collection/finance"
            ]
         }
      ],
      "AllowFromPublic":true
   }
]
```

If multiple VPC endpoints from different rules apply to a collection, the rules are additive and the collection will be accessible from all specified endpoints\. If you set `AllowFromPublic` to `true` but also provide one or more `SourceVPCEs`, the VPC endpoints are ignored and the associated collections will have public access\.

## Creating network policies \(console\)<a name="serverless-network-console"></a>

Network policies can apply to existing policies as well as future policies\. We recommend that you create network policies before you start creating collections\.

**To create an OpenSearch Serverless network policy**

1. Open the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/esv3/](https://console.aws.amazon.com/esv3/ )\.

1. On the left navigation panel, expand **Serverless** and choose **Network policies**\.

1. Choose **Create network policy**\.

1. Provide a name and description for the policy\.

1. Provide one or more *rules*\. These rules define access permissions for your OpenSearch Serverless collections and their OpenSearch Dashboards endpoints\.

   Each rule contains the following elements:    
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-network.html)

   For each resource type that you select, you can choose existing collections to apply the policy settings to, and/or create one or more resource patterns\. Resource patterns consist of a prefix and a wildcard \(\*\), and define which collections the policy settings will apply to\. 

   For example, if you include a pattern called `Marketing*`, any new or existing collections whose names start with "Marketing" will have the network settings in this policy automatically applied to them\. A single wildcard \(`*`\) applies the policy to all current and future collections\.

   In addition, you can specify the name of a *future* collection without a wildcard, such as `Finance`\. OpenSearch Serverless will apply the policy settings to any newly created collection with that exact name\.

1. When you're satisfied with your policy configuration, choose **Create**\.

## Creating network policies \(AWS CLI\)<a name="serverless-network-cli"></a>

To create a network policy using the OpenSearch Serverless API operations, you specify rules in JSON format\. The [CreateSecurityPolicy](https://docs.aws.amazon.com/opensearch-service/latest/ServerlessAPIReference/API_CreateSecurityPolicy.html) request accepts both inline policies and \.json files\. All collections and patterns must take the form `collection/<collection name|pattern>`\.

**Note**  
The resource type `dashboards` only allows permission to OpenSearch Dashboards, but in order for OpenSearch Dashboards to function, you must also allow collection access from the same sources\. See the second policy below for an example\.

The following sample network policy provides VPC access to collection endpoints only for collections beginning with the prefix `log*`\. Authenticated users can't sign in to OpenSearch Dashboards; they can only access the collection endpoint programmatically\.

```
[
   {
      "Description":"VPC access for log collections",
      "Rules":[
         {
            "ResourceType":"collection",
            "Resource":[
               "collection/log*"
            ]
         }
      ],
      "AllowFromPublic":false,
      "SourceVPCEs":[
         "vpce-050f79086ee71ac05"
      ]
   }
]
```

The following policy provides public access to the OpenSearch endpoint *and* OpenSearch Dashboards for a single collection named `finance`\. If the collection doesn't exist, the network settings will be applied to the collection if and when it's created\.

```
[
   {
      "Description":"Public access for finance collection",
      "Rules":[
         {
            "ResourceType":"dashboard",
            "Resource":[
               "collection/finance"
            ]
         },
         {
            "ResourceType":"collection",
            "Resource":[
               "collection/finance"
            ]
         }
      ],
      "AllowFromPublic":true
   }
]
```

The following request creates the above network policy:

```
aws opensearchserverless create-security-policy \
    --name sales-inventory \
    --type network \
    --policy "[{\"Description\":\"Public access for finance collection\",\"Rules\":[{\"ResourceType\":\"dashboard\",\"Resource\":[\"collection\/finance\"]},{\"ResourceType\":\"collection\",\"Resource\":[\"collection\/finance\"]}],\"AllowFromPublic\":true}]"
```

To provide the policy in a JSON file, use the format `--policy file://my-policy.json`

## Viewing network policies<a name="serverless-network-list"></a>

Before you create a collection, you might want to preview the existing network policies in your account to see which one has a resource pattern that matches your collection's name\. The following [ListSecurityPolicies](https://docs.aws.amazon.com/opensearch-service/latest/ServerlessAPIReference/API_ListSecurityPolicies.html) request lists all network policies in your account:

```
aws opensearchserverless list-security-policies --type network
```

The request returns information about all configured network policies\. To view the pattern rules defined in the policy, use the contents of the `policy` element:

```
{
    "securityPolicyDetails": [
        {
            "type": "network",
            "name": "my-policy",
            "policyVersion": "MTY2MzY5MTY1MDA3Ml8x",
            "policy": "[{\"Description\":\"My network policy rule\",\"Rules\":[{\"ResourceType\":\"dashboard\",\"Resource\":[\"collection/*\"]}],\"AllowFromPublic\":true}]",
            "createdDate": 1663691650072,
            "lastModifiedDate": 1663691650072
        }
    ]
}
```

To view detailed information about a specific policy, use the [GetSecurityPolicy](https://docs.aws.amazon.com/opensearch-service/latest/ServerlessAPIReference/API_GetSecurityPolicy.html) command\.

## Updating network policies<a name="serverless-network-update"></a>

When you modify the VPC endpoints or public access designation for a network, all associated collections are impacted\. To update a network policy in the OpenSearch Serverless console, expand **Network policies**, select the policy to modify, and choose **Edit**\. Make your changes and choose **Save**\.

To update a network policy using the OpenSearch Serverless API, use the [UpdateSecurityPolicy](https://docs.aws.amazon.com/opensearch-service/latest/ServerlessAPIReference/API_UpdateSecurityPolicy.html) command\. You must include a policy version in the request\. You can retrieve the policy version by using the `ListSecurityPolicies` or `GetSecurityPolicy` commands\. Including the most recent policy version ensures that you don't inadvertently override a change made by someone else\. 

The following request updates a network policy with a new policy JSON document:

```
aws opensearchserverless update-security-policy \
    --name sales-inventory \
    --type network \
    --policy-version MTY2MzY5MTY1MDA3Ml8x \
    --policy file://my-new-policy.json
```

## Deleting network policies<a name="serverless-network-delete"></a>

Before you can delete a network policy, you must detach it from all collections\. To delete a policy in the OpenSearch Serverless console, select the policy and choose **Delete**\.

You can also use the [DeleteSecurityPolicy](https://docs.aws.amazon.com/opensearch-service/latest/ServerlessAPIReference/API_DeleteSecurityPolicy.html) command:

```
aws opensearchserverless delete-security-policy --name my-policy --type network
```