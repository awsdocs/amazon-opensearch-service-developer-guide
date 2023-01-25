# Tagging Amazon OpenSearch Serverless collections<a name="tag-collection"></a>

Tags let you assign arbitrary information to an Amazon OpenSearch Serverless collection so you can categorize and filter on that information\. A *tag* is a metadata label that you assign or that AWS assigns to an AWS resource\. 

Each tag consists of a *key* and a *value*\. For tags that you assign, you define the key and value\. For example, you might define the key as `stage` and the value for one resource as `test`\.

With tags, you can do the following:
+ Identify and organize your AWS resources\. Many AWS services support tagging, so you can assign the same tag to resources from different services to indicate that the resources are related\. For example, you could assign the same tag to an OpenSearch Serverless collection that you assign to an Amazon OpenSearch Service domain\.
+ Track your AWS costs\. You activate these tags on the AWS Billing and Cost Management dashboard\. AWS uses the tags to categorize your costs and deliver a monthly cost allocation report to you\. For more information, see [Use Cost Allocation Tags](https://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/cost-alloc-tags.html) in the [AWS Billing User Guide](https://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/)\.

In OpenSearch Serverless, the primary resource is a collection\. You can use the OpenSearch Service console, the AWS CLI, the OpenSearch Serverless API operations, or the AWS SDKs to add, manage, and remove tags from a collection\.

## Permissions required<a name="collection-tag-permissions"></a>

OpenSearch Serverless uses the following AWS Identity and Access Management Access Analyzer \(IAM\) permissions for tagging collections:
+ `aoss:TagResource`
+ `aoss:ListTagsForResource`
+ `aoss:UntagResource`

## Working with tags \(console\)<a name="tag-collection-console"></a>

The console is the simplest way to tag a collection\.

****To create a tag \(console\)****

1. Sign in to the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home )\.

1. Expand **Serverless** in the left navigation pane and choose **Collections**\.

1. Select the collection that you want to add tags to, and go to the **Tags** tab\.

1. Choose **Manage** and **Add new tag**\.

1. Enter a tag key and an optional value\.

1. Choose **Save**\.

To delete a tag, follow the same steps and choose **Remove** on the **Manage tags** page\.

For more information about using the console to work with tags, see [Tag Editor](https://docs.aws.amazon.com/awsconsolehelpdocs/latest/gsg/tag-editor.html) in the *AWS Management Console Getting Started Guide*\.

## Working with tags \(AWS CLI\)<a name="tag-collection-cli"></a>

To tag a collection using the AWS CLI, send a [TagResource](https://docs.aws.amazon.com/opensearch-service/latest/ServerlessAPIReference/API_TagResource.html) request: 

```
aws opensearchserverless tag-resource
  --resource-arn arn:aws:aoss:us-east-1:123456789012:collection/my-collection 
  --tags Key=service,Value=aoss Key=source,Value=logs
```

View the existing tags for a collection with the [ListTagsForResource](https://docs.aws.amazon.com/opensearch-service/latest/ServerlessAPIReference/API_ListTagsForResource.html) command:

```
aws opensearchserverless list-tags-for-resource
  --resource-arn arn:aws:aoss:us-east-1:123456789012:collection/my-collection
```

Remove tags from a collection using the [UntagResource](https://docs.aws.amazon.com/opensearch-service/latest/ServerlessAPIReference/API_UntagResource.html) command:

```
aws opensearchserverless untag-resource
  --resource-arn arn:aws:aoss:us-east-1:123456789012:collection/my-collection
  --tag-keys service
```