# Tagging Amazon Elasticsearch Service Domains<a name="es-managedomains-awsresourcetagging"></a>

You can use Amazon ES tags to add metadata to your Amazon ES domains\. AWS does not apply any semantic meaning to your tags\. Tags are interpreted strictly as character strings\. All tags have the following elements\.


****  

| Tag Element | Description | 
| --- | --- | 
| Tag key | The tag key is the required name of the tag\. Tag keys must be unique for the Amazon ES domain to which they are attached\. For a list of basic restrictions on tag keys and values, see [User\-Defined Tag Restrictions](http://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/allocation-tag-restrictions.html)\. | 
| Tag value | The tag value is an optional string value of the tag\. Tag values can be null and do not have to be unique in a tag set\. For example, you can have a key\-value pair in a tag set of project/Trinity and cost\-center/Trinity\. For a list of basic restrictions on tag keys and values, see [User\-Defined Tag Restrictions](http://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/allocation-tag-restrictions.html)\. | 

Each Amazon ES domain has a tag set, which contains all the tags that are assigned to that Amazon ES domain\. AWS does not automatically set any tags on Amazon ES domains\. A tag set can contain up to 50 tags, or it can be empty\. If you add a tag to an Amazon ES domain that has the same key as an existing tag for a resource, the new value overwrites the old value\. 

You can use these tags to track costs by grouping expenses for similarly tagged resources\. An Amazon ES domain tag is a name\-value pair that you define and associate with an Amazon ES domain\. The name is referred to as the *key*\. You can use tags to assign arbitrary information to an Amazon ES domain\. A tag key could be used, for example, to define a category, and the tag value could be an item in that category\. For example, you could define a tag key of “project” and a tag value of “Salix,” indicating that the Amazon ES domain is assigned to the Salix project\. You could also use tags to designate Amazon ES domains as being used for test or production by using a key such as `environment=test` or `environment=production`\. We recommend that you use a consistent set of tag keys to make it easier to track metadata that is associated with Amazon ES domains\. 

You also can use tags to organize your AWS bill to reflect your own cost structure\. To do this, sign up to get your AWS account bill with tag key values included\. Then, organize your billing information according to resources with the same tag key values to see the cost of combined resources\. For example, you can tag several Amazon ES domains with key\-value pairs, and then organize your billing information to see the total cost for each domain across several services\. For more information, see [Using Cost Allocation Tags](http://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/cost-alloc-tags.html) in the *AWS Billing and Cost Management* documentation\.

**Note**  
Tags are cached for authorization purposes\. Because of this, additions and updates to tags on Amazon ES domains might take several minutes before they are available\.

## Working with Tags \(Console\)<a name="es-managedomains-awsresourcetagging-console"></a>

Use the following procedure to create a resource tag\.

**To create a tag \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, choose your Amazon ES domain\.

1. On the domain dashboard, choose **Manage tags**\.

1. In the **Key** column, enter a tag key\.

1. \(Optional\) In the **Value** column, enter a tag value\.

1. Choose **Submit**\.

**To delete a tag \(console\)**

Use the following procedure to delete a resource tag\.

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, choose your Amazon ES domain\.

1. On the domain dashboard, choose **Manage tags**\.

1. Next to the tag that you want to delete, choose **Remove**\.

1. Choose **Submit**\.

For more information about using the console to work with tags, see [Working with Tag Editor](https://docs.aws.amazon.com/awsconsolehelpdocs/latest/gsg/tag-editor.html) in the *AWS Management Console Getting Started Guide*\.

## Working with Tags \(AWS CLI\)<a name="es-managedomains-awsresourcetagging-cli"></a>

You can create resource tags using the AWS CLI with the \-\-add\-tags command\. 

**Syntax**

`add-tags --arn=<domain_arn> --tag-list Key=<key>,Value=<value>`


****  

| Parameter | Description | 
| --- | --- | 
| \-\-arn | Amazon resource name for the Amazon ES domain to which the tag is attached\. | 
| \-\-tag\-list | Set of space\-separated key\-value pairs in the following format: Key=<key>,Value=<value> | 

**Example**

The following example creates two tags for the *logs* domain:

```
aws es add-tags --arn arn:aws:es:us-east-1:379931976431:domain/logs --tag-list Key=service,Value=Elasticsearch Key=instances,Value=m3.2xlarge
```

You can remove tags from an Amazon ES domain using the remove\-tags command\. 

** Syntax **

`remove-tags --arn=<domain_arn> --tag-keys Key=<key>,Value=<value>`


****  

| Parameter | Description | 
| --- | --- | 
| \-\-arn | Amazon Resource Name \(ARN\) for the Amazon ES domain to which the tag is attached\. | 
| \-\-tag\-keys | Set of space\-separated key\-value pairs that you want to remove from the Amazon ES domain\. | 

**Example**

The following example removes two tags from the *logs* domain that were created in the preceding example:

```
aws es remove-tags --arn arn:aws:es:us-east-1:379931976431:domain/logs --tag-keys service instances
```

You can view the existing tags for an Amazon ES domain with the list\-tags command:

**Syntax**

`list-tags --arn=<domain_arn>`


****  

| Parameter | Description | 
| --- | --- | 
| \-\-arn | Amazon Resource Name \(ARN\) for the Amazon ES domain to which the tags are attached\. | 

**Example**

The following example lists all resource tags for the *logs* domain:

```
aws es list-tags --arn arn:aws:es:us-east-1:379931976431:domain/logs
```

## Working with Tags \(AWS SDKs\)<a name="es-managedomains-awsresourcetagging-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all the actions defined in the [Amazon ES Configuration API Reference](es-configuration-api.md), including the `AddTags`, `ListTags`, and `RemoveTags` operations\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\. 