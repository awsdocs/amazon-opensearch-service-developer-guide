# Using service\-linked roles to create VPC domains<a name="slr-aos"></a>

Amazon OpenSearch Service uses AWS Identity and Access Management \(IAM\) [service\-linked roles](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_terms-and-concepts.html#iam-term-service-linked-role)\. A service\-linked role is a unique type of IAM role that is linked directly to OpenSearch Service\. Service\-linked roles are predefined by OpenSearch Service and include all the permissions that the service requires to call other AWS services on your behalf\. 

OpenSearch Service uses the service\-linked role named **AWSServiceRoleForAmazonOpenSearchService**, which provides the minimum Amazon EC2 and Elastic Load Balancing permissions necessary for the role to enable [VPC access](cognito-auth.md) for a domain\.

## Legacy Elasticsearch role<a name="slr-replacement"></a>

Amazon OpenSearch Service uses a service\-linked role called `AWSServiceRoleForAmazonOpenSearchService`\. Your accounts might also contain a legacy service\-linked role called `AWSServiceRoleForAmazonElasticsearchService`, which works with the deprecated Elasticsearch API endpoints\. 

If the legacy Elasticsearch role doesn't exist in your account, OpenSearch Service automatically creates a new OpenSearch service\-linked role the first time you create an OpenSearch domain\. Otherwise your account continues to use the Elasticsearch role\. In order for this automatic creation to succeed, you must have permissions for the `iam:CreateServiceLinkedRole` action\.

## Permissions<a name="slr-permissions"></a>

The `AWSServiceRoleForAmazonOpenSearchService` service\-linked role trusts the following services to assume the role:
+ `opensearchservice.amazonaws.com`

The role permissions policy named `AmazonOpenSearchServiceRolePolicy` allows OpenSearch Service to complete the following actions on the specified resources:
+ Action: `ec2:CreateNetworkInterface` on `*`
+ Action: `ec2:DeleteNetworkInterface` on `*`
+ Action: `ec2:DescribeNetworkInterfaces` on `*`
+ Action: `ec2:ModifyNetworkInterfaceAttribute` on `*`
+ Action: `ec2:DescribeSecurityGroups` on `*`
+ Action: `ec2:DescribeSubnets` on `*`
+ Action: `ec2:DescribeVpcs` on `*`
+ Action: `elasticloadbalancing:AddListenerCertificates` on `*`
+ Action: `elasticloadbalancing:RemoveListenerCertificates` on `*`
+ Action: `ec2:CreateTags` on all network interfaces and VPC endpoints
+ Action: `ec2:DescribeTags` on `*`
+ Action: `acm:DescribeCertificate` on `*`
+ Action: `cloudwatch:PutMetricData` on `*`
+ Action: `ec2:CreateVpcEndpoint` on all VPCs, security groups, subnets, and route tables, as well as all VPC endpoints when the request contains the tag `OpenSearchManaged=true`
+ Action: `ec2:ModifyVpcEndpoint` on all VPCs, security groups, subnets, and route tables, as well as all VPC endpoints when the request contains the tag `OpenSearchManaged=true`
+ Action: `ec2:DeleteVpcEndpoints` on all endpoints when the request contains the tag `OpenSearchManaged=true`
+ Action: `ec2:DescribeVpcEndpoints` on `*`

You must configure permissions to allow an IAM entity \(such as a user, group, or role\) to create, edit, or delete a service\-linked role\. For more information, see [Service\-linked role permissions](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#service-linked-role-permissions) in the *IAM User Guide*\.

## Creating the service\-linked role<a name="create-slr"></a>

You don't need to manually create a service\-linked role\. When you create a VPC\-enabled domain using the AWS Management Console, OpenSearch Service creates the service\-linked role for you\. In order for this automatic creation to succeed, you must have permissions for the `iam:CreateServiceLinkedRole` action\.

You can also use the IAM console, the IAM CLI, or the IAM API to create a service\-linked role manually\. For more information, see [Creating a service\-linked role](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#create-service-linked-role) in the *IAM User Guide*\.

## Editing the service\-linked role<a name="edit-slr"></a>

OpenSearch Service doesn't let you edit the `AWSServiceRoleForAmazonOpenSearchService` service\-linked role\. After you create a service\-linked role, you can't change the name of the role because various entities might reference the role\. However, you can edit the description of the role using IAM\. For more information, see [Editing a service\-linked role](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#edit-service-linked-role) in the *IAM User Guide*\.

## Deleting the service\-linked role<a name="delete-slr"></a>

If you no longer need to use a feature or service that requires a service\-linked role, we recommend that you delete that role\. That way you don’t have an unused entity that is not actively monitored or maintained\. However, you must clean up your service\-linked role before you can manually delete it\.

### Cleaning up the service\-linked role<a name="slr-review-before-delete"></a>

Before you can use IAM to delete a service\-linked role, you must first confirm that the role has no active sessions and remove any resources used by the role\.

**To check whether the service\-linked role has an active session in the IAM console**

1. Sign in to the AWS Management Console and open the IAM console at [https://console\.aws\.amazon\.com/iam/](https://console.aws.amazon.com/iam/)\.

1. In the navigation pane of the IAM console, choose **Roles**\. Then choose the name \(not the check box\) of the `AWSServiceRoleForAmazonOpenSearchService` role\.

1. On the **Summary** page for the selected role, choose the **Access Advisor** tab\.

1. On the **Access Advisor** tab, review recent activity for the service\-linked role\.
**Note**  
If you're unsure whether OpenSearch Service is using the `AWSServiceRoleForAmazonOpenSearchService` role, you can try to delete the role\. If the service is using the role, then the deletion fails and you can view the resources using the role\. If the role is being used, then you must wait for the session to end before you can delete the role, and/or delete the resources using the role\. You cannot revoke the session for a service\-linked role\. 

### Manually deleting a service\-linked role<a name="slr-manual-delete"></a>

Delete service\-linked roles from the IAM console, API, or AWS CLI\. For instructions, see [Deleting a service\-linked role](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#delete-service-linked-role) in the *IAM User Guide*\.