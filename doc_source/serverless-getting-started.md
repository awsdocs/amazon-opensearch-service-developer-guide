# Getting started with Amazon OpenSearch Serverless<a name="serverless-getting-started"></a>

This tutorial walks you through the basic steps to get an Amazon OpenSearch Serverless collection up and running quickly\. For more detailed information, see [Creating, listing, and deleting Amazon OpenSearch Serverless collections](serverless-manage.md) and the other topics within this guide\.

You'll complete the following steps in this tutorial:

1. [Configure permissions](#serverless-gsg-permissions)

1. [Create a collection](#serverless-gsg-create)

1. [Upload and search data](#serverless-gsg-index)

1. [Delete the collection](#serverless-gsg-delete)

## Step 1: Configure permissions<a name="serverless-gsg-permissions"></a>

In order to complete this tutorial, and to use OpenSearch Serverless in general, you must have the correct IAM permissions\. In this tutorial, you will create a collection, upload and search data, and then delete the collection\.

Your user or role must have an attached [identity\-based policy](security-iam-serverless.md#security-iam-serverless-id-based-policies) with the following minimum permissions:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "aoss:CreateCollection",
        "aoss:ListCollections",
        "aoss:BatchGetCollection",
        "aoss:DeleteCollection",
        "aoss:CreateAccessPolicy",
        "aoss:ListAccessPolicies",
        "aoss:UpdateAccessPolicy",
        "aoss:CreateSecurityPolicy",
        "iam:ListUsers",
        "iam:ListRoles"
      ],
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}
```

For more information about OpenSearch Serverless IAM permissions, see [Identity and Access Management for Amazon OpenSearch Serverless](security-iam-serverless.md)\.

## Step 2: Create a collection<a name="serverless-gsg-create"></a>

A collection is a group of OpenSearch indexes that work together to support a specific workload or use case\.

**To create an OpenSearch Serverless collection**

1. Open the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home )\.

1. Choose **Collections** in the left navigation pane and choose **Create collection**\.

1. Name the collection **movies**\.

1. For collection type, choose **Search**\. For more information, see [Choosing a collection type](serverless-overview.md#serverless-usecase)\.

1. Under **Encryption**, select **Use AWS owned key**\. This is the AWS KMS key that OpenSearch Serverless will use to encrypt your data\.

1. Under **Network**, configure network settings for the collection\.
   + For the access type, select **Public**\.
   + For the resource type, enable access to both **OpenSearch endpoints** and **OpenSearch Dashboards**\. Since you'll upload and search data using OpenSearch Dashboards, you need to enable both\.

1. Choose **Next**\.

1. For **Configure data access**, set up access settings for the collection\. [Data access policies](serverless-data-access.md) allow users and roles to access the data within a collection\. In this tutorial, we'll provide a single user the permissions required to index and search data in the *movies* collection\.

   Create a single rule that provides access to the *movies* collection\. Name the rule **Movies collection access**\.

1. Choose **Add principals**, **IAM users and roles** and select the IAM user that you'll use to sign in to OpenSearch Dashboards and index data\. Choose **Save**\.

1. Under **Index permissions**, select all of the permissions\.

1. Choose **Next**\.

1. For the access policy settings, choose **Create a new data access policy** and name the policy **movies**\.

1. Choose **Next**\.

1. Review your collection settings and choose **Submit**\. Wait several minutes for the collection status to become `Active`\.

## Step 3: Upload and search data<a name="serverless-gsg-index"></a>

You can upload data to an OpenSearch Serverless collection using [Postman](https://www.postman.com/downloads/) or curl\. For brevity, these examples use **Dev Tools** within the OpenSearch Dashboards console\.

**To index and search data in the movies collection**

1. Choose **Collections** in the left navigation pane and choose the **movies** collection to open its details page\.

1. Choose the OpenSearch Dashboards URL for the collection\. The URL takes the format `https://collection-id.us-east-1.aoss.amazonaws.com/_dashboards`\. 

1. Sign in using the [AWS access and secret keys](https://docs.aws.amazon.com/powershell/latest/userguide/pstools-appendix-sign-up.html) for the user that you specified in your data access policy\.

1. Within OpenSearch Dashboards, open the left navigation pane and choose **Dev Tools**\.

1. To create a single index called *movies\-index*, send the following request:

   ```
   PUT movies-index 
   ```  
![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/serverless-gsg-create.png)

1. To index a single document into *movies\-index*, send the following request:

   ```
   PUT movies-index/_doc/1
   { 
     "title": "Shawshank Redemption",
     "genre": "Drama",
     "year": 1994
   }
   ```

1. To search data in OpenSearch Dashboards, you need to configure at least one index pattern\. OpenSearch uses these patterns to identify which indexes you want to analyze\. Open the left navigation pane, choose **Stack Management**, choose **Index Patterns**, and then choose **Create index pattern**\. For this tutorial, enter *movies*\.

1. Choose **Next step** and then choose **Create index pattern**\. After the pattern is created, you can view the various document fields such as `title` and `genre`\.

1. To begin searching your data, open the left navigation pane again and choose **Discover**, or use the [search API](https://opensearch.org/docs/latest/opensearch/rest-api/search/) within Dev Tools\.

## Step 4: Delete the collection<a name="serverless-gsg-delete"></a>

Because the *movies* collection is for test purposes, make sure to delete it when you're done experimenting\.

**To delete an OpenSearch Serverless collection**

1. Go back to the **Amazon OpenSearch Service** console\.

1. Choose **Collections** in the left navigation pane and select the **movies** collection\.

1. Choose **Delete** and confirm deletion\.

## Next steps<a name="serverless-gsg-next"></a>

Now that you know how to create a collection and index data, you might want to try some of the following exercises:
+ See more advanced options for creating a collection\. For more information, see [Creating, listing, and deleting Amazon OpenSearch Serverless collections](serverless-manage.md)\.
+ Learn how to configure security policies to manage collection security at scale\. For more information, see [Overview of security in Amazon OpenSearch Serverless](serverless-security.md)\.
+ Discover other ways to index data into collections\. For more information, see [Ingesting data into Amazon OpenSearch Serverless collections](serverless-clients.md)\.
