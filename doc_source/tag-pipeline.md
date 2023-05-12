# Tagging Amazon OpenSearch Ingestion pipelines<a name="tag-pipeline"></a>

Tags let you assign arbitrary information to an Amazon OpenSearch Ingestion pipeline so you can categorize and filter on that information\. A *tag* is a metadata label that you assign or that AWS assigns to an AWS resource\. Each tag consists of a *key* and a *value*\. For tags that you assign, you define the key and value\. For example, you might define the key as `stage` and the value for one resource as `test`\.

Tags help you do the following:
+ Identify and organize your AWS resources\. Many AWS services support tagging, so you can assign the same tag to resources from different services to indicate that the resources are related\. For example, you could assign the same tag to an OpenSearch Ingestion pipeline that you assign to an Amazon OpenSearch Service domain\.
+ Track your AWS costs\. You activate these tags on the AWS Billing and Cost Management dashboard\. AWS uses the tags to categorize your costs and deliver a monthly cost allocation report to you\. For more information, see [Use Cost Allocation Tags](https://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/cost-alloc-tags.html) in the [AWS Billing User Guide](https://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/)\.
+ Restrict access to pipelines using attribute based access control\. For more information, see [Controlling access based on tag keys](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_tags.html#access_tags_control-tag-keys) in the IAM User Guide\.

In OpenSearch Ingestion, the primary resource is a pipeline\. You can use the OpenSearch Service console, the AWS CLI, OpenSearch Ingestion APIs, or the AWS SDKs to add, manage, and remove tags from a pipeline\.

**Topics**
+ [Permissions required](#pipeline-tag-permissions)
+ [Working with tags \(console\)](#tag-pipeline-console)
+ [Working with tags \(AWS CLI\)](#tag-pipeline-cli)

## Permissions required<a name="pipeline-tag-permissions"></a>

OpenSearch Ingestion uses the following AWS Identity and Access Management Access Analyzer \(IAM\) permissions for tagging pipelines:
+ `osis:TagResource`
+ `osis:ListTagsForResource`
+ `osis:UntagResource`

For more information about each permission, see [Actions, resources, and condition keys for OpenSearch Ingestion](https://docs.aws.amazon.com/service-authorization/latest/reference/list_opensearchingestionservice.html) in the *Service Authorization Reference*\.

## Working with tags \(console\)<a name="tag-pipeline-console"></a>

The console is the simplest way to tag a pipeline\.

****To create a tag****

1. Sign in to the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home )\.

1. Choose **Ingestion** on the left navigation pane\.

1. Select the pipeline you want to add tags to and go to the **Tags** tab\.

1. Choose **Manage** and **Add new tag**\.

1. Enter a tag key and an optional value\.

1. Choose **Save**\.

To delete a tag, follow the same steps and choose **Remove** on the **Manage tags** page\.

For more information about using the console to work with tags, see [Tag Editor](https://docs.aws.amazon.com/awsconsolehelpdocs/latest/gsg/tag-editor.html) in the *AWS Management Console Getting Started Guide*\.

## Working with tags \(AWS CLI\)<a name="tag-pipeline-cli"></a>

To tag a pipeline using the AWS CLI, send a `TagResource` request: 

```
aws osis tag-resource
  --arn arn:aws:osis:us-east-1:123456789012:pipeline/my-pipeline 
  --tags Key=service,Value=osis Key=source,Value=otel
```

Remove tags from a pipeline using the `UntagResource` command:

```
aws osis untag-resource
  --arn arn:aws:osis:us-east-1:123456789012:pipeline/my-pipeline
  --tag-keys service
```

View the existing tags for a pipeline with the `ListTagsForResource` command:

```
aws osis list-tags-for-resource
  --arn arn:aws:osis:us-east-1:123456789012:pipeline/my-pipeline
```