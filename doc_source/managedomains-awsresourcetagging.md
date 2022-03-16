# Tagging Amazon OpenSearch Service domains<a name="managedomains-awsresourcetagging"></a>

Tags let you assign arbitrary information to an Amazon OpenSearch Service domain so you can categorize and filter on that information\. A tag is a key\-value pair that you define and associate with an OpenSearch Service domain\. You can use these tags to track costs by grouping expenses for similarly tagged resources\. AWS doesn't apply any semantic meaning to your tags\. Tags are interpreted strictly as character strings\. All tags have the following elements:


****  

| Tag Element | Description | Required | 
| --- | --- | --- | 
| Tag key |  The tag key is the name of the tag\. Key must be unique to the OpenSearch Service domain to which they're attached\. For a list of basic restrictions on tag keys and values, see [User\-Defined Tag Restrictions](http://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/allocation-tag-restrictions.html)\.  | Yes | 
| Tag value |  The tag value is the string value of the tag\. Tag values can be `null` and don't have to be unique in a tag set\. For example, you can have a key\-value pair in a tag set of project/Trinity and cost\-center/Trinity\. For a list of basic restrictions on tag keys and values, see [User\-Defined Tag Restrictions](http://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/allocation-tag-restrictions.html)\.  | No | 

Each OpenSearch Service domain has a tag set, which contains all the tags assigned to that OpenSearch Service domain\. AWS doesn't automatically assign any tags to OpenSearch Service domains\. A tag set can contain between 0 and 50 tags\. If you add a tag to a domain with the same key as an existing tag, the new value overwrites the old value\. 

## Tagging examples<a name="managedomains-awsresourcetagging-examples"></a>

You can use a key to define a category, and the value could be an item in that category\. For example, you could define a tag key of `project` and a tag value of `Salix`, indicating that the OpenSearch Service domain is assigned to the Salix project\. You could also use tags to designate OpenSearch Service domains as being used for test or production by using a key such as `environment=test` or `environment=production`\. Try to use a consistent set of tag keys to make it easier to track metadata that is associated with OpenSearch Service domains\. 

You also can use tags to organize your AWS bill to reflect your own cost structure\. To do this, sign up to get your AWS account bill with tag key values included\. Then, organize your billing information according to resources with the same tag key values to see the cost of combined resources\. For example, you can tag several OpenSearch Service domains with key\-value pairs, and then organize your billing information to see the total cost for each domain across several services\. For more information, see [Using Cost Allocation Tags](http://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/cost-alloc-tags.html) in the *AWS Billing and Cost Management* documentation\.

**Note**  
Tags are cached for authorization purposes\. Because of this, additions and updates to tags on OpenSearch Service domains might take several minutes before they're available\.

## Working with tags \(console\)<a name="managedomains-awsresourcetagging-console"></a>

The console is the simplest way to tag a domain\.

****To create a tag \(console\)****

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Amazon OpenSearch Service**\.

1. Select the domain you want to add tags to and go to the **Tags** tab\.

1. Choose **Manage** and **Add new tag**\.

1. Enter a tag key and an optional value\.

1. Choose **Save**\.

To delete a tag, follow the same steps and choose **Remove** on the **Manage tags** page\.

For more information about using the console to work with tags, see [Working with Tag Editor](https://docs.aws.amazon.com/awsconsolehelpdocs/latest/gsg/tag-editor.html) in the *AWS Management Console Getting Started Guide*\.

## Working with tags \(AWS CLI\)<a name="managedomains-awsresourcetagging-cli"></a>

You can create resource tags using the AWS CLI with the \-\-add\-tags command\. 

**Syntax**

`add-tags --arn=<domain_arn> --tag-list Key=<key>,Value=<value>`


****  

| Parameter | Description | 
| --- | --- | 
| \-\-arn | Amazon resource name for the OpenSearch Service domain to which the tag is attached\. | 
| \-\-tag\-list | Set of space\-separated key\-value pairs in the following format: Key=<key>,Value=<value> | 

**Example**

The following example creates two tags for the *logs* domain:

```
aws opensearch add-tags --arn arn:aws:es:us-east-1:379931976431:domain/logs --tag-list Key=service,Value=OpenSearch Key=instances,Value=m3.2xlarge
```

You can remove tags from an OpenSearch Service domain using the remove\-tags command\. 

** Syntax **

`remove-tags --arn=<domain_arn> --tag-keys Key=<key>,Value=<value>`


****  

| Parameter | Description | 
| --- | --- | 
| \-\-arn | Amazon Resource Name \(ARN\) for the OpenSearch Service domain to which the tag is attached\. | 
| \-\-tag\-keys | Set of space\-separated key\-value pairs that you want to remove from the OpenSearch Service domain\. | 

**Example**

The following example removes two tags from the *logs* domain that were created in the preceding example:

```
aws opensearch remove-tags --arn arn:aws:es:us-east-1:379931976431:domain/logs --tag-keys service instances
```

You can view the existing tags for an OpenSearch Service domain with the list\-tags command:

**Syntax**

`list-tags --arn=<domain_arn>`


****  

| Parameter | Description | 
| --- | --- | 
| \-\-arn | Amazon Resource Name \(ARN\) for the OpenSearch Service domain to which the tags are attached\. | 

**Example**

The following example lists all resource tags for the *logs* domain:

```
aws opensearch list-tags --arn arn:aws:es:us-east-1:379931976431:domain/logs
```

## Working with tags \(AWS SDKs\)<a name="managedomains-awsresourcetagging-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all the actions defined in the [OpenSearch Service configuration API reference](configuration-api.md), including the `AddTags`, `ListTags`, and `RemoveTags` operations\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\. 