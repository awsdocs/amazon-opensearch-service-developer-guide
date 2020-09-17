# Using Service\-Linked Roles for Amazon ES<a name="slr-es"></a>

Amazon Elasticsearch Service uses AWS Identity and Access Management \(IAM\)[ service\-linked roles](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_terms-and-concepts.html#iam-term-service-linked-role)\. A service\-linked role is a unique type of IAM role that is linked directly to Amazon ES\. Service\-linked roles are predefined by Amazon ES and include all the permissions that the service requires to call other AWS services on your behalf\. 

A service\-linked role makes setting up Amazon ES easier because you don’t have to manually add the necessary permissions\. Amazon ES defines the permissions of its service\-linked roles, and unless defined otherwise, only Amazon ES can assume its roles\. The defined permissions include the trust policy and the permissions policy, and that permissions policy cannot be attached to any other IAM entity\.

You can delete a service\-linked role only after first deleting its related resources\. This protects your Amazon ES resources because you can't inadvertently remove permission to access the resources\.

For information about other services that support service\-linked roles, see [AWS Services That Work with IAM](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_aws-services-that-work-with-iam.html) and look for the services that have **Yes **in the **Service\-Linked Role** column\. Choose a **Yes** with a link to view the service\-linked role documentation for that service\.

## Service\-Linked Role Permissions for Amazon ES<a name="slr-permissions"></a>

Amazon ES uses the service\-linked role named **AWSServiceRoleForAmazonElasticsearchService**\.

The AWSServiceRoleForAmazonElasticsearchService service\-linked role trusts the following services to assume the role:
+ `es.amazonaws.com`

The role permissions policy allows Amazon ES to complete the following actions on the specified resources:
+ Action: `ec2:CreateNetworkInterface` on `*`
+ Action: `ec2:DeleteNetworkInterface` on `*`
+ Action: `ec2:DescribeNetworkInterfaces` on `*`
+ Action: `ec2:ModifyNetworkInterfaceAttribute` on `*`
+ Action: `ec2:DescribeSecurityGroups` on `*`
+ Action: `ec2:DescribeSubnets` on `*`
+ Action: `ec2:DescribeVpcs` on `*`

You must configure permissions to allow an IAM entity \(such as a user, group, or role\) to create, edit, or delete a service\-linked role\. For more information, see [Service\-Linked Role Permissions](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#service-linked-role-permissions) in the *IAM User Guide*\.

## Creating a Service\-Linked Role for Amazon ES<a name="create-slr"></a>

You don't need to manually create a service\-linked role\. When you create a VPC access domain using the AWS Management Console, Amazon ES creates the service\-linked role for you\. In order for this automatic creation to succeed, you must have permissions for the `es:CreateElasticsearchServiceRole` and `iam:CreateServiceLinkedRole` actions\.

If you delete this service\-linked role and then need to create it again, you can use the same process to recreate the role in your account\.

You can also use the IAM console, the IAM CLI, or the IAM API to create a service\-linked role manually\. For more information, see [Creating a Service\-Linked Role](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#create-service-linked-role) in the *IAM User Guide*\.

## Editing a Service\-Linked Role for Amazon ES<a name="edit-slr"></a>

Amazon ES does not allow you to edit the AWSServiceRoleForAmazonElasticsearchService service\-linked role\. After you create a service\-linked role, you cannot change the name of the role because various entities might reference the role\. However, you can edit the description of the role using IAM\. For more information, see [Editing a Service\-Linked Role](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#edit-service-linked-role) in the *IAM User Guide*\.

## Deleting a Service\-Linked Role for Amazon ES<a name="delete-slr"></a>

If you no longer need to use a feature or service that requires a service\-linked role, we recommend that you delete that role\. That way you don’t have an unused entity that is not actively monitored or maintained\. However, you must clean up your service\-linked role before you can manually delete it\.

### Cleaning Up a Service\-Linked Role<a name="slr-review-before-delete"></a>

Before you can use IAM to delete a service\-linked role, you must first confirm that the role has no active sessions and remove any resources used by the role\.

**To check whether the service\-linked role has an active session in the IAM console**

1. Sign in to the AWS Management Console and open the IAM console at [https://console\.aws\.amazon\.com/iam/](https://console.aws.amazon.com/iam/)\.

1. In the navigation pane of the IAM console, choose **Roles**\. Then choose the name \(not the check box\) of the AWSServiceRoleForAmazonElasticsearchService role\.

1. On the **Summary** page for the selected role, choose the **Access Advisor** tab\.

1. On the **Access Advisor** tab, review recent activity for the service\-linked role\.
**Note**  
If you are unsure whether Amazon ES is using the AWSServiceRoleForAmazonElasticsearchService role, you can try to delete the role\. If the service is using the role, then the deletion fails and you can view the regions where the role is being used\. If the role is being used, then you must wait for the session to end before you can delete the role\. You cannot revoke the session for a service\-linked role\. 

**To remove Amazon ES resources used by the AWSServiceRoleForAmazonElasticsearchService**

1. Sign in to the AWS Management Console and open the Amazon ES console\.

1. Delete any domains that list **VPC** under the **Endpoint** column\.

### Manually Delete a Service\-Linked Role<a name="slr-manual-delete"></a>

Use the Amazon ES configuration API to delete the AWSServiceRoleForAmazonElasticsearchService service\-linked role\. For more information, see [DeleteElasticsearchServiceRole](es-configuration-api.md#es-configuration-api-actions-deleteelasticsearchservicerole)\.