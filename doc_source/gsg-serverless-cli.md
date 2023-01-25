# Tutorial: Getting started with security in Amazon OpenSearch Serverless \(CLI\)<a name="gsg-serverless-cli"></a>

This tutorial walks you through the steps described in the [console getting started tutorial](gsg-serverless.md) for security, but uses the AWS CLI rather than the OpenSearch Service console\. 

You'll complete the following steps in this tutorial:

1. Configure permissions

1. Create an encryption policy

1. Configure network settings

1. Create a collection

1. Configure data access

1. Upload and search data

1. Delete the collection

The goal of this tutorial is to set up a single OpenSearch Serverless collection with fairly simple encryption, network, and data access settings\. For example, we'll configure public network access, an AWS managed key for encryption, and a simplified data access policy that grants minimal permissions to a single user\. 

In a production scenario, consider implementing a more robust configuration, including SAML authentication, a custom encryption key, and VPC access\.

**To get started with security policies in OpenSearch Serverless**

1. 
**Note**  
You can skip this step if you're already using a more broad identity\-based policy, such as `Action":"aoss:*"` or `Action":"*"`\. In production environments, however, we recommend that you follow the principal of least privilege and only assign the minimum permissions necessary to complete a task\.

   To start, create an AWS Identity and Access Management policy with the minimum required permissions to perform the steps in this tutorial\. We'll name the policy `TutorialPolicy`:

   ```
   aws iam create-policy \
     --policy-name TutorialPolicy \
     --policy-document "{\"Version\": \"2012-10-17\",\"Statement\": [{\"Action\": [\"aoss:ListCollections\",\"aoss:BatchGetCollection\",\"aoss:CreateCollection\",\"aoss:CreateSecurityPolicy\",\"aoss:GetSecurityPolicy\",\"aoss:ListSecurityPolicies\",\"aoss:CreateAccessPolicy\",\"aoss:GetAccessPolicy\",\"aoss:ListAccessPolicies\"],\"Effect\": \"Allow\",\"Resource\": \"*\"}]}"
   ```

   **Sample response**

   ```
   {
       "Policy": {
           "PolicyName": "TutorialPolicy",
           "PolicyId": "ANPAW6WRAECKG6QJWUV7U",
           "Arn": "arn:aws:iam::123456789012:policy/TutorialPolicy",
           "Path": "/",
           "DefaultVersionId": "v1",
           "AttachmentCount": 0,
           "PermissionsBoundaryUsageCount": 0,
           "IsAttachable": true,
           "CreateDate": "2022-10-16T20:57:18+00:00",
           "UpdateDate": "2022-10-16T20:57:18+00:00"
       }
   }
   ```

1. Attach `TutorialPolicy` to the IAM user who will index and search data in the collection\. We'll name the user `TutorialUser`:

   ```
   aws iam attach-user-policy \
     --user-name TutorialUser \
     --policy-arn arn:aws:iam::123456789012:policy/TutorialPolicy
   ```

1. Before you create a collection, you need to create an [encryption policy](serverless-encryption.md) that assigns an AWS owned key to the *books* collection that you'll create in a later step\.

   Send the following request to create an encryption policy for the *books* collection:

   ```
   aws opensearchserverless create-security-policy \
     --name books-policy \
     --type encryption --policy "{\"Rules\":[{\"ResourceType\":\"collection\",\"Resource\":[\"collection\/books\"]}],\"AWSOwnedKey\":true}"
   ```

   **Sample response**

   ```
   {
       "securityPolicyDetail": {
           "type": "encryption",
           "name": "books-policy",
           "policyVersion": "MTY2OTI0MDAwNTk5MF8x",
           "policy": {
               "Rules": [
                   {
                       "Resource": [
                           "collection/books"
                       ],
                       "ResourceType": "collection"
                   }
               ],
               "AWSOwnedKey": true
           },
           "createdDate": 1669240005990,
           "lastModifiedDate": 1669240005990
       }
   }
   ```

1. Create a [network policy](serverless-network.md) that provides public access to the *books* collection:

   ```
   aws opensearchserverless create-security-policy --name books-policy --type network \
     --policy "[{\"Description\":\"Public access for books collection\",\"Rules\":[{\"ResourceType\":\"dashboard\",\"Resource\":[\"collection\/books\"]},{\"ResourceType\":\"collection\",\"Resource\":[\"collection\/books\"]}],\"AllowFromPublic\":true}]"
   ```

   **Sample response**

   ```
   {
       "securityPolicyDetail": {
           "type": "network",
           "name": "books-policy",
           "policyVersion": "MTY2OTI0MDI1Njk1NV8x",
           "policy": [
               {
                   "Rules": [
                       {
                           "Resource": [
                               "collection/books"
                           ],
                           "ResourceType": "dashboard"
                       },
                       {
                           "Resource": [
                               "collection/books"
                           ],
                           "ResourceType": "collection"
                       }
                   ],
                   "AllowFromPublic": true,
                   "Description": "Public access for books collection"
               }
           ],
           "createdDate": 1669240256955,
           "lastModifiedDate": 1669240256955
       }
   }
   ```

1. Create the *books* collection:

   ```
   aws opensearchserverless create-collection --name books --type SEARCH
   ```

   **Sample response**

   ```
   {
       "createCollectionDetail": {
           "id": "8kw362bpwg4gx9b2f6e0",
           "name": "books",
           "status": "CREATING",
           "type": "SEARCH",
           "arn": "arn:aws:aoss:us-east-1:123456789012:collection/8kw362bpwg4gx9b2f6e0",
           "kmsKeyArn": "auto",
           "createdDate": 1669240325037,
           "lastModifiedDate": 1669240325037
       }
   }
   ```

1. Create a [data access policy](serverless-data-access.md) that provides the minimum permissions to index and search data in the *books* collection\. Replace the principal ARN with the ARN of `TutorialUser` from step 1:

   ```
   aws opensearchserverless create-access-policy \
     --name books-policy \
     --type data \
     --policy "[{\"Rules\":[{\"ResourceType\":\"index\",\"Resource\":[\"index\/books\/books-index\"],\"Permission\":[\"aoss:CreateIndex\",\"aoss:DescribeIndex\",\"aoss:ReadDocument\",\"aoss:WriteDocument\",\"aoss:UpdateIndex\",\"aoss:DeleteIndex\"]}],\"Principal\":[\"arn:aws:iam::123456789012:user\/TutorialUser\"]}]"
   ```

   **Sample response**

   ```
   {
       "accessPolicyDetail": {
           "type": "data",
           "name": "books-policy",
           "policyVersion": "MTY2OTI0MDM5NDY1M18x",
           "policy": [
               {
                   "Rules": [
                       {
                           "Resource": [
                               "index/books/books-index"
                           ],
                           "Permission": [
                               "aoss:CreateIndex",
                               "aoss:DescribeIndex",
                               "aoss:ReadDocument",
                               "aoss:WriteDocument",
                               "aoss:UpdateDocument",
                               "aoss:DeleteDocument"
                           ],
                           "ResourceType": "index"
                       }
                   ],
                   "Principal": [
                       "arn:aws:iam::123456789012:user/TutorialUser"
                   ]
               }
           ],
           "createdDate": 1669240394653,
           "lastModifiedDate": 1669240394653
       }
   }
   ```

   `TutorialUser` should now be able to index and search documents in the *books* collection\. 

1. To make calls to the OpenSearch API, you need the collection endpoint\. Send the following request to retrieve the `collectionEndpoint` parameter:

   ```
   aws opensearchserverless batch-get-collection --names books  
   ```

   **Sample response**

   ```
   {
       "collectionDetails": [
           {
               "id": "8kw362bpwg4gx9b2f6e0",
               "name": "books",
               "status": "ACTIVE",
               "type": "SEARCH",
               "description": "",
               "arn": "arn:aws:aoss:us-east-1:123456789012:collection/8kw362bpwg4gx9b2f6e0",
               "createdDate": 1665765327107,
               "collectionEndpoint": "https://8kw362bpwg4gx9b2f6e0.us-east-1.aoss.amazonaws.com",
               "dashboardEndpoint": "https://8kw362bpwg4gx9b2f6e0.us-east-1.aoss.amazonaws.com/_dashboards"
           }
       ],
       "collectionErrorDetails": []
   }
   ```
**Note**  
You won't be able to see the collection endpoint until the collection status changes to `ACTIVE`\. You might have to make multiple calls to check the status until the collection is successfully created\.

1. Use an HTTP tool such as [Postman](https://www.getpostman.com/) or curl to index data into the *books* collection\. We'll create an index called *books\-index* and add a single document\.

   Send the following request to the collection endpoint that you retrieved in the previous step, using the credentials for `TutorialUser`\.

   ```
   PUT https://8kw362bpwg4gx9b2f6e0.us-east-1.aoss.amazonaws.com/books-index/_doc/1
   { 
     "title": "The Shining",
     "author": "Stephen King",
     "year": 1977
   }
   ```

   **Sample response**

   ```
   {
     "_index" : "books-index",
     "_id" : "1",
     "_version" : 1,
     "result" : "created",
     "_shards" : {
       "total" : 0,
       "successful" : 0,
       "failed" : 0
     },
     "_seq_no" : 0,
     "_primary_term" : 0
   }
   ```

1. To begin searching data in your collection, use the [search API](https://opensearch.org/docs/latest/opensearch/rest-api/search/)\. The following query performs a basic search:

   ```
   GET https://8kw362bpwg4gx9b2f6e0.us-east-1.aoss.amazonaws.com/books-index/_search
   ```

   **Sample response**

   ```
   {
       "took": 405,
       "timed_out": false,
       "_shards": {
           "total": 6,
           "successful": 6,
           "skipped": 0,
           "failed": 0
       },
       "hits": {
           "total": {
               "value": 2,
               "relation": "eq"
           },
           "max_score": 1.0,
           "hits": [
               {
                   "_index": "books-index:0::3xJq14MBUaOS0wL26UU9:0",
                   "_id": "F_bt4oMBLle5pYmm5q4T",
                   "_score": 1.0,
                   "_source": {
                       "title": "The Shining",
                       "author": "Stephen King",
                       "year": 1977
                   }
               }
           ]
       }
   }
   ```