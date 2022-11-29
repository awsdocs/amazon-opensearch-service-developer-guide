# Using service\-linked roles to create OpenSearch Serverless collections<a name="serverless-service-linked-roles"></a>

Amazon OpenSearch Service uses AWS Identity and Access Management \(IAM\) [service\-linked roles](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_terms-and-concepts.html#iam-term-service-linked-role)\. A service\-linked role is a unique type of IAM role that is linked directly to OpenSearch Service\. Service\-linked roles are predefined by OpenSearch Service and include all the permissions that the service requires to call other AWS services on your behalf\.

OpenSearch Serverless uses the service\-linked role named **AmazonOpenSearchServerlessServiceRole**, which provides the permissions necessary for the role to publish serverless\-related CloudWatch metrics to your account\.

## Service\-linked role permissions for OpenSearch Serverless<a name="serverless-slr-permissions"></a>

OpenSearch Serverless uses the service\-linked role named AmazonOpenSearchServerlessServiceRole, which allows OpenSearch Serverless to call AWS services on your behalf\.

The AmazonOpenSearchServerlessServiceRole service\-linked role trusts the following services to assume the role:
+ `observability.aoss.amazonaws.com`

The role permissions policy named `AmazonOpenSearchServerlessServiceRolePolicy` allows OpenSearch Serverless to complete the following actions on the specified resources:
+ Action: `cloudwatch:PutMetricData` on all AWS resources

**Note**  
The policy includes the condition key `{"StringEquals": {"cloudwatch:namespace": "AWS/AOSS"}}`, which means that the service\-linked role can only send metric data to the `AWS/AOSS` CloudWatch namespace\.  
 

You must configure permissions to allow an IAM entity \(such as a user, group, or role\) to create, edit, or delete a service\-linked role\. For more information, see [Service\-linked role permissions](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#service-linked-role-permissions) in the *IAM User Guide*\.

## Creating a service\-linked role for OpenSearch Serverless<a name="create-serverless-slr"></a>

You don't need to manually create a service\-linked role\. When you create an OpenSearch Serverless collection in the AWS Management Console, the AWS CLI, or the AWS API, OpenSearch Serverless creates the service\-linked role for you\.

**Note**  
The first time you create a collection, you must be assigned the `iam:CreateServiceLinkedRole` in an identity\-based policy\. 

If you delete this service\-linked role, and then need to create it again, you can use the same process to recreate the role in your account\. When you create an OpenSearch Serverless collection, OpenSearch Serverless creates the service\-linked role for you again\. 

You can also use the IAM console to create a service\-linked role with the **Amazon OpenSearch Serverless** use case\. In the AWS CLI or the AWS API, create a service\-linked role with the `observability.aoss.amazonaws.com` service name:

```
aws iam create-service-linked-role --aws-service-name "observability.aoss.amazonaws.com"
```

For more information, see [Creating a service\-linked role](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#create-service-linked-role) in the *IAM User Guide*\. If you delete this service\-linked role, you can use this same process to create the role again\.

## Editing a service\-linked role for OpenSearch Serverless<a name="edit-serverless-slr"></a>

OpenSearch Serverless does not allow you to edit the AmazonOpenSearchServerlessServiceRole service\-linked role\. After you create a service\-linked role, you can't change the name of the role because various entities might reference the role\. However, you can edit the description of the role using IAM\. For more information, see [Editing a service\-linked role](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#edit-service-linked-role) in the *IAM User Guide*\.

## Deleting a service\-linked role for OpenSearch Serverless<a name="delete-serverless-slr"></a>

If you no longer need to use a feature or service that requires a service\-linked role, we recommend that you delete that role\. This prevents you from having an unused entity that isn't actively monitored or maintained\. However, you must clean up the resources for your service\-linked role before you can manually delete it\.

To delete the AmazonOpenSearchServerlessServiceRole, you must first [delete all OpenSearch Serverless collections](serverless-manage.md#serverless-delete) in your AWS account\.

**Note**  
If OpenSearch Serverless is using the role when you try to delete the resources, then the deletion might fail\. If that happens, wait for a few minutes and try the operation again\.

**To manually delete the service\-linked role using IAM**

Use the IAM console, the AWS CLI, or the AWS API to delete the AmazonOpenSearchServerlessServiceRole service\-linked role\. For more information, see [Deleting a service\-linked role](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#delete-service-linked-role) in the *IAM User Guide*\.

## Supported Regions for OpenSearch Serverless service\-linked roles<a name="serverless-slr-regions"></a>

OpenSearch Serverless does not support using service\-linked roles in every Region where OpenSearch Service is available\. You can use the AmazonOpenSearchServerlessServiceRole role in the following Regions\.


| Region name | Region identity | Support in OpenSearch Serverless | 
| --- | --- | --- | 
| US East \(N\. Virginia\) | us\-east\-1 | Yes | 
| US East \(Ohio\) | us\-east\-2 | Yes | 
| US West \(Oregon\) | us\-west\-2 | Yes | 
| Asia Pacific \(Tokyo\) | ap\-northeast\-1 | Yes | 
| Europe \(Ireland\) | eu\-west\-1 | Yes | 