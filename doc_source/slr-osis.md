# Using service\-linked roles to create OpenSearch Ingestion pipelines<a name="slr-osis"></a>

Amazon OpenSearch Ingestion uses AWS Identity and Access Management \(IAM\) [service\-linked roles](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_terms-and-concepts.html#iam-term-service-linked-role)\. A service\-linked role is a unique type of IAM role that is linked directly to OpenSearch Ingestion\. Service\-linked roles are predefined by OpenSearch Ingestion and include all the permissions that the service requires to call other AWS services on your behalf\.

OpenSearch Ingestion uses the service\-linked role named **AWSServiceRoleForAmazonOpenSearchIngestion**\. The attached policy provides the permissions necessary for the role to create a virtual private cloud \(VPC\) between your account and OpenSearch Ingestion, and to publish CloudWatch metrics to your account\.

## Permissions<a name="slr-osis-permissions"></a>

The `AWSServiceRoleForAmazonOpenSearchIngestion` service\-linked role trusts the following services to assume the role:
+ `osis.amazon.com`

The role permissions policy named `AmazonOpenSearchIngestionServiceRolePolicy` allows OpenSearch Ingestion to complete the following actions on the specified resources:
+ Action: `ec2:DescribeSubnets` on `*`
+ Action: `ec2:DescribeSecurityGroups` on `*`
+ Action: `ec2:DeleteVpcEndpoints` on `*`
+ Action: `ec2:CreateVpcEndpoint` on `*`
+ Action: `ec2:DescribeVpcEndpoints` on `*`
+ Action: `ec2:CreateTags` on `arn:aws:ec2:*:*:network-interface/*`
+ Action: `cloudwatch:PutMetricData` on `cloudwatch:namespace": "AWS/OSIS"`

You must configure permissions to allow an IAM entity \(such as a user, group, or role\) to create, edit, or delete a service\-linked role\. For more information, see [Service\-linked role permissions](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#service-linked-role-permissions) in the *IAM User Guide*\.

## Creating the service\-linked role for OpenSearch Ingestion<a name="slr-osis-create"></a>

You don't need to manually create a service\-linked role\. When you [create an OpenSearch Ingestion pipeline](creating-pipeline.md#create-pipeline) in the AWS Management Console, the AWS CLI, or the AWS API, OpenSearch Ingestion creates the service\-linked role for you\.

If you delete this service\-linked role, and then need to create it again, you can use the same process to recreate the role in your account\. When you create an OpenSearch Ingestion pipeline, OpenSearch Ingestion creates the service\-linked role for you again\. 

## Editing the service\-linked role for OpenSearch Ingestion<a name="slr-osis-edit"></a>

OpenSearch Ingestion does not allow you to edit the `AWSServiceRoleForAmazonOpenSearchIngestion` service\-linked role\. After you create a service\-linked role, you cannot change the name of the role because various entities might reference the role\. However, you can edit the description of the role using IAM\. For more information, see [Editing a service\-linked role](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#edit-service-linked-role) in the *IAM User Guide*\.

## Deleting the service\-linked role for OpenSearch Ingestion<a name="slr-osis-deleting"></a>

If you no longer need to use a feature or service that requires a service\-linked role, we recommend that you delete that role\. That way you don't have an unused entity that is not actively monitored or maintained\. However, you must clean up the resources for your service\-linked role before you can manually delete it\.

### Cleaning up a service\-linked role<a name="slr-osis-cleanup"></a>

Before you can use IAM to delete a service\-linked role, you must first delete any resources used by the role\.

**Note**  
If OpenSearch Ingestion is using the role when you try to delete the resources, then the deletion might fail\. If that happens, wait for a few minutes and try the operation again\.

**To delete OpenSearch Ingestion resources used by the `AWSServiceRoleForAmazonOpenSearchIngestion`**

1. Navigate to the Amazon OpenSearch Service console and choose **Ingestion**\.

1. Delete all pipelines\. For instructions, see [Deleting Amazon OpenSearch Ingestion pipelines](delete-pipeline.md)\.

### Delete the service\-linked role for OpenSearch Ingestion<a name="slr-osis-delete"></a>

You can use the OpenSearch Ingestion console to delete a service\-linked role\.

**To delete a service\-linked role \(console\)**

1. Navigate to the IAM console\.

1. Choose **Roles** and search for the **AWSServiceRoleForAmazonOpenSearchIngestion** role\.

1. Select the role and choose **Delete**\.