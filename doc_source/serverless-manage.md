# Creating, listing, and deleting Amazon OpenSearch Serverless collections<a name="serverless-manage"></a>

A *collection* in Amazon OpenSearch Serverless is a logical grouping of one or more indexes that represent an analytics workload\. OpenSearch Service automatically manages and tunes the collection, requiring minimal manual input\.

**Topics**
+ [Permissions required](#serverless-collection-permissions)
+ [Creating collections](#serverless-create)
+ [Accessing OpenSearch Dashboards](#serverless-dashboards)
+ [Viewing collections](#serverless-list)
+ [Deleting collections](#serverless-delete)

## Permissions required<a name="serverless-collection-permissions"></a>

OpenSearch Serverless uses the following AWS Identity and Access Management \(IAM\) permissions for creating and managing collections\. You can specify IAM conditions to restrict users to specific collections\.
+ `aoss:CreateCollection` – Create a collection\.
+ `aoss:ListCollections` – List collections in the current account\.
+ `aoss:BatchGetCollection` – Get details about one or more collections\.
+ `aoss:UpdateCollection` – Modify a collection\.
+ `aoss:DeleteCollection` – Delete a collection\.

The following sample identity\-based access policy provides the minimum permissions necessary for a user to manage a single collection named `Logs`:

```
[
   {
      "Sid":"Allows managing logs collections",
      "Effect":"Allow",
      "Action":[
         "aoss:CreateCollection",
         "aoss:ListCollections",
         "aoss:BatchGetCollection",
         "aoss:UpdateCollection",
         "aoss:DeleteCollection",
         "aoss:CreateAccessPolicy",
         "aoss:CreateSecurityPolicy"
      ],
      "Resource":"*",
      "Condition":{
         "StringEquals":{
            "aoss:collection":"Logs"
         }
      }
   }
]
```

`aoss:CreateAccessPolicy` and `aoss:CreateSecurityPolicy` are included because encryption, network, and data access policies are required in order for a collection to function properly\. For more information, see [Identity and Access Management for Amazon OpenSearch Serverless](security-iam-serverless.md)\.

**Note**  
If you're creating the first collection in your account, you also need the `iam:CreateServiceLinkedRole` permission\. For more information, see [Using service\-linked roles to create OpenSearch Serverless collections](serverless-service-linked-roles.md)\.

## Creating collections<a name="serverless-create"></a>

You can use the console or the AWS CLI to create a serverless collection\.

### Create a collection \(console\)<a name="serverless-create-console"></a>

**To create a collection using the console**

1. Navigate to the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home/](https://console.aws.amazon.com/aos/home/)\.

1. Expand **Serverless** in the left navigation pane and choose **Collections**\. 

1. Choose **Create collection**\.

1. Provide a name and description for the collection\. The name must meet the following criteria:
   + Is unique to your account and AWS Region
   + Starts with a lowercase letter
   + Contains between 3 and 32 characters
   + Contains only lowercase letters a\-z, the numbers 0–9, and the hyphen \(\-\)

1. Choose a collection type:
   + **Search** – Full\-text search that powers applications in your internal networks and internet\-facing applications\. All search data is stored in hot storage to ensure fast query response times\.
   + **Time series** – Log analytics segment that focuses on analyzing large volumes of semi\-structured, machine\-generated data\. At least 24 hours of data is cached in hot storage, and the rest remains in warm storage\.

   For more information, see [Choosing a collection type](serverless-overview.md#serverless-usecase)\.

1. Under **Encryption**, choose an AWS KMS key to encrypt your data with\. OpenSearch Serverless notifies you if the collection name that you entered matches a pattern defined in an encryption policy\. You can choose to keep this match or override it with unique encryption settings\. For more information, see [Encryption in Amazon OpenSearch Serverless](serverless-encryption.md)\.

1. Under **Network access settings**, configure network access for the collection\.
   + For **Access type**, select public or VPC access\. If you choose to enable access through a virtual private cloud \(VPC\), select one or more VPC endpoints to allow access through\. To create a VPC endpoint, see [Access Amazon OpenSearch Serverless using an interface endpoint \(AWS PrivateLink\)](serverless-vpc.md)\.
   + For **Resource type**, select whether the collection will be accessible through its *OpenSearch* endpoint \(to make API calls through curl, Postman, and so on\), through the *OpenSearch Dashboards* endpoint \(to work with visualizations and make API calls through the console\), or through both\.

   OpenSearch Serverless notifies you if the collection name that you entered matches a pattern defined in a network policy\. You can choose to keep this match or override it with custom network settings\. For more information, see [Network access for Amazon OpenSearch Serverless](serverless-network.md)\.

1. \(Optional\) Add one or more tags to the collection\. For more information, see [Tagging Amazon OpenSearch Serverless collections](tag-collection.md)\.

1. Choose **Next**\.

1. Configure data access rules for the collection, which define who can access the data within the collection\. For each rule that you create, perform the following steps:
   + Choose **Add principals** and select one or more IAM roles or [SAML users and groups](serverless-saml.md) to provide data access to\.
   + Under **Grant permissions**, select the alias, template, and index permissions to grant the associated principals\. For a full list of permissions and the access they allow, see [Supported OpenSearch API operations and permissions](serverless-genref.md#serverless-operations)\.

   OpenSearch Serverless notifies you if the collection name that you entered matches a pattern defined in a data access policy\. You can choose to keep this match or override it with unique data access settings\. For more information, see [Data access control for Amazon OpenSearch Serverless](serverless-data-access.md)\.

1. Choose **Next**\.

1. Under **Data access policy settings**, choose what to do with the rules you just created\. You can either use them to create a new data access policy, or add them to an existing policy\.

1. Review your collection configuration and choose **Submit**\.

The collection status changes to `Creating` as OpenSearch Serverless creates the collection\.

### Create a collection \(CLI\)<a name="serverless-create-cli"></a>

Before you create a collection using the AWS CLI, you must have an [encryption policy](serverless-encryption.md) with a resource pattern that matches the intended name of the collection\. For example, if you plan to name your collection *logs\-application*, you might create an encryption policy like this:

```
aws opensearchserverless create-security-policy \
  --name logs-policy \
  --type encryption --policy "{\"Rules\":[{\"ResourceType\":\"collection\",\"Resource\":[\"collection\/logs-application\"]}],\"AWSOwnedKey\":true}"
```

If you plan to use the policy for additional collections, you can make the rule more broad, such as `collection/logs*` or `collection/*`\.

You also need to configure network settings for the collection in the form of a [network policy](serverless-network.md)\. Using the previous *logs\-application* example, you might create the following network policy:

```
aws opensearchserverless create-security-policy \
  --name logs-policy \
  --type network --policy "[{\"Description\":\"Public access for logs collection\",\"Rules\":[{\"ResourceType\":\"dashboard\",\"Resource\":[\"collection\/logs-application\"]},{\"ResourceType\":\"collection\",\"Resource\":[\"collection\/logs-application\"]}],\"AllowFromPublic\":true}]"
```

**Note**  
You can create network policies after you create a collection, but we recommend doing it beforehand\.

To create a collection, send a [CreateCollection](https://docs.aws.amazon.com/opensearch-service/latest/ServerlessAPIReference/API_CreateCollection.html) request:

```
aws opensearchserverless create-collection --name "logs-application" --type SEARCH --description "A collection for storing log data"
```

For `type`, specify either `SEARCH` or `TIMESERIES`\. For more information, see [Choosing a collection type](serverless-overview.md#serverless-usecase)\.

**Sample response**

```
{
    "createCollectionDetail": {
        "id": "07tjusf2h91cunochc",
        "name": "books",
        "description":"A collection for storing log data",
        "status": "CREATING",
        "type": "SEARCH",
        "kmsKeyArn": "auto",
        "arn": "arn:aws:aoss:us-east-1:123456789012:collection/07tjusf2h91cunochc",
        "createdDate": 1665952577473
    }
}
```

If you don't specify a collection type in the request, it defaults to `TIMESERIES`\. If your collection is encrypted with an AWS owned key, the `kmsKeyArn` is `auto` rather than an ARN\.

**Important**  
After you create a collection, you won't be able to access it unless it matches a data access policy\. For instructions to create data access policies, see [Data access control for Amazon OpenSearch Serverless](serverless-data-access.md)\.

## Accessing OpenSearch Dashboards<a name="serverless-dashboards"></a>

After you create a collection with the AWS Management Console, you can navigate to the collection's OpenSearch Dashboards URL\. You can find the Dashboards URL by choosing **Collections** in the left navigation pane and selecting the collection to open its details page\. The URL takes the format `https://dashboards.us-east-1.aoss.amazonaws.com/_login/?collectionId=07tjusf2h91cunochc`\. Once you navigate to the URL, you'll automatically log into Dashboards\.

If you already have the OpenSearch Dashboards URL available but aren't on the AWS Management Console, calling the Dashboards URL from the browser will redirect to the console\. Once you enter your AWS credentials, you'll automatically log in to Dashboards\. For information about accessing collections for SAML, see [Accessing OpenSearch Dashboards with SAML](serverless-saml.md#serverless-saml-dashboards)\.

The OpenSearch Dashboards console timeout is one hour and isn't configurable\.

**Note**  
On May 10, 2023, OpenSearch introduced a common global endpoint for OpenSearch Dashboards\. You can now navigate to OpenSearch Dashboards in the browser with a URL that takes the format `https://dashboards.us-east-1.aoss.amazonaws.com/_login/?collectionId=07tjusf2h91cunochc`\. To ensure backward compatibility, we'll continue to support the existing collection specific OpenSearch Dashboards endpoints with the format `https://07tjusf2h91cunochc.us-east-1.aoss.amazonaws.com/_dashboards`\.

## Viewing collections<a name="serverless-list"></a>

You can view the existing collections in your AWS account on the **Collections** tab of the Amazon OpenSearch Service console\.

To list collections along with their IDs, send a [ListCollections](https://docs.aws.amazon.com/opensearch-service/latest/ServerlessAPIReference/API_ListCollections.html) request\.

```
aws opensearchserverless list-collections
```

**Sample response**

```
{
   "collectionSummaries":[
      {
         "arn":"arn:aws:aoss:us-east-1:123456789012:collection/07tjusf2h91cunochc",
         "id":"07tjusf2h91cunochc",
         "name":"my-collection",
         "status":"CREATING"
      }
   ]
}
```

To limit the search results, use collection filters\. This request filters the response to collections in the `ACTIVE` state: 

```
aws opensearchserverless list-collections --collection-filters '{ "status": "ACTIVE" }'
```

To get more detailed information about one or more collections, including the OpenSearch endpoint and the OpenSearch Dashboards endpoint, send a [BatchGetCollection](https://docs.aws.amazon.com/opensearch-service/latest/ServerlessAPIReference/API_BatchGetCollection.html) request:

```
aws opensearchserverless batch-get-collection --ids ["07tjusf2h91cunochc", "1iu5usc4rame"]
```

**Note**  
You can include `--names` or `--ids` in the request, but not both\.

**Sample response**

```
{
   "collectionDetails":[
      {
         "id": "07tjusf2h91cunochc",
         "name": "my-collection",
         "status": "ACTIVE",
         "type": "SEARCH",
         "description": "",
         "arn": "arn:aws:aoss:us-east-1:123456789012:collection/07tjusf2h91cunochc",
         "kmsKeyArn": "arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab",
         "createdDate": 1667446262828,
         "lastModifiedDate": 1667446300769,
         "collectionEndpoint": "https://07tjusf2h91cunochc.us-east-1.aoss.amazonaws.com",
         "dashboardEndpoint": "https://07tjusf2h91cunochc.us-east-1.aoss.amazonaws.com/_dashboards"
      },
      {
         "id": "178ukvtg3i82dvopdid",
         "name": "another-collection",
         "status": "ACTIVE",
         "type": "TIMESERIES",
         "description": "",
         "arn": "arn:aws:aoss:us-east-1:123456789012:collection/178ukvtg3i82dvopdid",
         "kmsKeyArn": "arn:aws:kms:us-east-1:123456789012:key/1234abcd-12ab-34cd-56ef-1234567890ab",
         "createdDate": 1667446262828,
         "lastModifiedDate": 1667446300769,
         "collectionEndpoint": "https://178ukvtg3i82dvopdid.us-east-1.aoss.amazonaws.com",
         "dashboardEndpoint": "https://178ukvtg3i82dvopdid.us-east-1.aoss.amazonaws.com/_dashboards"
      }
   ],
   "collectionErrorDetails":[]
}
```

## Deleting collections<a name="serverless-delete"></a>

Deleting a collection deletes all data and indexes in the collection\. You can't recover collections after you delete them\.

**To delete a collection using the console**

1. From the **Collections** panel of the Amazon OpenSearch Service console, select the collection you want to delete\.

1. Choose **Delete** and confirm deletion\.

To delete a collection using the AWS CLI, send a [DeleteCollection](https://docs.aws.amazon.com/opensearch-service/latest/ServerlessAPIReference/API_DeleteCollection.html) request:

```
aws opensearchserverless delete-collection --id 07tjusf2h91cunochc
```

**Sample response**

```
{
   "deleteCollectionDetail":{
      "id":"07tjusf2h91cunochc",
      "name":"my-collection",
      "status":"DELETING"
   }
}
```