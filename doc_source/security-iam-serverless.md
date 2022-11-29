# Identity and Access Management for Amazon OpenSearch Serverless<a name="security-iam-serverless"></a>

****  
***This is prerelease documentation for Amazon OpenSearch Serverless, which is in preview release\. The documentation and the feature are both subject to change\. We recommend that you use this feature only in test environments, and not in production environments\. For preview terms and conditions, see *Beta Service Participation* in [AWS Service Terms](https://aws.amazon.com/service-terms/)\. *** 

AWS Identity and Access Management \(IAM\) is an AWS service that helps an administrator securely control access to AWS resources\. IAM administrators control who can be *authenticated* \(signed in\) and *authorized* \(have permissions\) to use OpenSearch Serverless resources\. IAM is an AWS service that you can use with no additional charge\.

**Topics**
+ [Identity\-based policies for OpenSearch Serverless](#security-iam-serverless-id-based-policies)
+ [Policy actions for OpenSearch Serverless](#security-iam-serverless-id-based-policies-actions)
+ [Policy resources for OpenSearch Serverless](#security-iam-serverless-id-based-policies-resources)
+ [Policy condition keys for Amazon OpenSearch Serverless](#security_iam_serverless-conditionkeys)
+ [ABAC with OpenSearch Serverless](#security_iam_serverless-with-iam-tags)
+ [Using temporary credentials with OpenSearch Serverless](#security_iam_serverless-tempcreds)
+ [Service\-linked roles for OpenSearch Serverless](#security_iam_serverless-slr)
+ [Identity\-based policy examples for OpenSearch Serverless](#security_iam_serverless_id-based-policy-examples)

## Identity\-based policies for OpenSearch Serverless<a name="security-iam-serverless-id-based-policies"></a>


|  |  | 
| --- |--- |
|  Supports identity\-based policies  |    Yes  | 

Identity\-based policies are JSON permissions policy documents that you can attach to an identity, such as an IAM user, group of users, or role\. These policies control what actions users and roles can perform, on which resources, and under what conditions\. To learn how to create an identity\-based policy, see [Creating IAM policies](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_create.html) in the *IAM User Guide*\.

With IAM identity\-based policies, you can specify allowed or denied actions and resources as well as the conditions under which actions are allowed or denied\. You can't specify the principal in an identity\-based policy because it applies to the user or role to which it is attached\. To learn about all of the elements that you can use in a JSON policy, see [IAM JSON policy elements reference](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements.html) in the *IAM User Guide*\.

### Identity\-based policy examples for OpenSearch Serverless<a name="security_iam_id-based-policy-examples"></a>

To view examples of OpenSearch Serverless identity\-based policies, see [Identity\-based policy examples for OpenSearch Serverless](#security_iam_serverless_id-based-policy-examples)\.

## Policy actions for OpenSearch Serverless<a name="security-iam-serverless-id-based-policies-actions"></a>


|  |  | 
| --- |--- |
|  Supports policy actions  |    Yes  | 

The `Action` element of a JSON policy describes the actions that you can use to allow or deny access in a policy\. Policy actions usually have the same name as the associated AWS API operation\. There are some exceptions, such as *permission\-only actions* that don't have a matching API operation\. There are also some operations that require multiple actions in a policy\. These additional actions are called *dependent actions*\.

Include actions in a policy to grant permissions to perform the associated operation\.

Policy actions in OpenSearch Serverless use the following prefix before the action:

```
aoss
```

To specify multiple actions in a single statement, separate them with commas\.

```
"Action": [
      "aoss:action1",
      "aoss:action2"
         ]
```

You can specify multiple actions using wildcard characters \(\*\)\. For example, to specify all actions that begin with the word `Describe`, include the following action:

```
"Action": "aoss:List*"
```

To view examples of OpenSearch Serverless identity\-based policies, see [ Identity\-based policy examples for OpenSearch Serverless](#security_iam_id-based-policy-examples)\.

## Policy resources for OpenSearch Serverless<a name="security-iam-serverless-id-based-policies-resources"></a>


|  |  | 
| --- |--- |
|  Supports policy resources  |    Yes  | 

Administrators can use AWS JSON policies to specify who has access to what\. That is, which **principal** can perform **actions** on what **resources**, and under what **conditions**\.

The `Resource` JSON policy element specifies the object or objects to which the action applies\. Statements must include either a `Resource` or a `NotResource` element\. As a best practice, specify a resource using its [Amazon Resource Name \(ARN\)](https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html)\. You can do this for actions that support a specific resource type, known as *resource\-level permissions*\.

For actions that don't support resource\-level permissions, such as listing operations, use a wildcard \(\*\) to indicate that the statement applies to all resources\.

```
"Resource": "*"
```

## Policy condition keys for Amazon OpenSearch Serverless<a name="security_iam_serverless-conditionkeys"></a>


|  |  | 
| --- |--- |
|  Supports service\-specific policy condition keys  |    Yes  | 

Administrators can use AWS JSON policies to specify who has access to what\. That is, which **principal** can perform **actions** on what **resources**, and under what **conditions**\.

The `Condition` element \(or `Condition` *block*\) lets you specify conditions in which a statement is in effect\. The `Condition` element is optional\. You can create conditional expressions that use [condition operators](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_condition_operators.html), such as equals or less than, to match the condition in the policy with values in the request\. 

If you specify multiple `Condition` elements in a statement, or multiple keys in a single `Condition` element, AWS evaluates them using a logical `AND` operation\. If you specify multiple values for a single condition key, AWS evaluates the condition using a logical `OR` operation\. All of the conditions must be met before the statement's permissions are granted\.

 You can also use placeholder variables when you specify conditions\. For example, you can grant an IAM user permission to access a resource only if it is tagged with their IAM user name\. For more information, see [IAM policy elements: variables and tags](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_variables.html) in the *IAM User Guide*\. 

AWS supports global condition keys and service\-specific condition keys\. To see all AWS global condition keys, see [AWS global condition context keys](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_condition-keys.html) in the *IAM User Guide*\.

In addition to attribute\-based access control \(ABAC\), OpenSearch Serverless supports the following condition keys:
+ `aoss:collection`
+ `aoss:CollectionId`
+ `aoss:index`

You can use these condition keys even when providing permissions for access policies and security policies\. For example:

```
[
   {
      "Effect":"Allow",
      "Action":[
         "aoss:CreateAccessPolicy",
         "aoss:CreateSecurityPolicy"
      ],
      "Resource":"*",
      "Condition":{
         "StringLike":{
            "aoss:collection":"log"
         }
      }
   }
]
```

In this example, the condition applies to policies that contain *rules* that match a collection name or pattern\. The conditions have the following behavior:
+ `StringEquals` \- Applies to policies with rules that contain the *exact* resource string "log" \(i\.e\. `collection/log`\)\.
+ `StringLike` \- Applies to policies with rules that contain a resource string that *includes* the string "log" \(i\.e\. `collection/log` but also `collection/logs-application` or `collection/applogs123`\)\.

**Note**  
*Collection* condition keys don't apply at the index level\. For example, in the policy above, the condition wouldn't apply to an access or security policy containing the resource string `index/logs-application/*`\.

## ABAC with OpenSearch Serverless<a name="security_iam_serverless-with-iam-tags"></a>


|  |  | 
| --- |--- |
|  Supports ABAC \(tags in policies\)  |    Yes  | 

Attribute\-based access control \(ABAC\) is an authorization strategy that defines permissions based on attributes\. In AWS, these attributes are called *tags*\. You can attach tags to IAM entities \(users or roles\) and to many AWS resources\. Tagging entities and resources is the first step of ABAC\. Then you design ABAC policies to allow operations when the principal's tag matches the tag on the resource that they are trying to access\.

ABAC is helpful in environments that are growing rapidly and helps with situations where policy management becomes cumbersome\.

To control access based on tags, you provide tag information in the [condition element](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_condition.html) of a policy using the `aws:ResourceTag/key-name`, `aws:RequestTag/key-name`, or `aws:TagKeys` condition keys\.

If a service supports all three condition keys for every resource type, then the value is **Yes** for the service\. If a service supports all three condition keys for only some resource types, then the value is **Partial**\.

For more information about ABAC, see [What is ABAC?](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction_attribute-based-access-control.html) in the *IAM User Guide*\. To view a tutorial with steps for setting up ABAC, see [Use attribute\-based access control \(ABAC\)](https://docs.aws.amazon.com/IAM/latest/UserGuide/tutorial_attribute-based-access-control.html) in the *IAM User Guide*\.

For more information about tagging OpenSearch Serverless resources, see [Tagging Amazon OpenSearch Serverless collections](tag-collection.md)\.

## Using temporary credentials with OpenSearch Serverless<a name="security_iam_serverless-tempcreds"></a>


|  |  | 
| --- |--- |
|  Supports temporary credentials  |    Yes  | 

Some AWS services don't work when you sign in using temporary credentials\. For additional information, including which AWS services work with temporary credentials, see [AWS services that work with IAM](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_aws-services-that-work-with-iam.html) in the *IAM User Guide*\.

You are using temporary credentials if you sign in to the AWS Management Console using any method except a user name and password\. For example, when you access AWS using your company's single sign\-on \(SSO\) link, that process automatically creates temporary credentials\. You also automatically create temporary credentials when you sign in to the console as a user and then switch roles\. For more information about switching roles, see [Switching to a role \(console\)](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_use_switch-role-console.html) in the *IAM User Guide*\.

You can manually create temporary credentials using the AWS CLI or AWS API\. You can then use those temporary credentials to access AWS\. AWS recommends that you dynamically generate temporary credentials instead of using long\-term access keys\. For more information, see [Temporary security credentials in IAM](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_temp.html)\.

## Service\-linked roles for OpenSearch Serverless<a name="security_iam_serverless-slr"></a>


|  |  | 
| --- |--- |
|  Supports service\-linked roles  |    Yes  | 

  A service\-linked role is a type of service role that is linked to an AWS service\. The service can assume the role to perform an action on your behalf\. Service\-linked roles appear in your IAM account and are owned by the service\. An IAM administrator can view, but not edit the permissions for service\-linked roles\. 

For details about creating and managing OpenSearch Serverless service\-linked roles, see [Using service\-linked roles to create OpenSearch Serverless collections](serverless-service-linked-roles.md)\.

## Identity\-based policy examples for OpenSearch Serverless<a name="security_iam_serverless_id-based-policy-examples"></a>

By default, users and roles don't have permission to create or modify OpenSearch Serverless resources\. They also can't perform tasks by using the AWS Management Console, AWS Command Line Interface \(AWS CLI\), or AWS API\. An IAM administrator must create IAM policies that grant users and roles permission to perform actions on the resources that they need\. The administrator must then attach those policies for users that require them\.

To learn how to create an IAM identity\-based policy by using these example JSON policy documents, see [Creating IAM policies](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_create-console.html) in the *IAM User Guide*\.

For details about actions and resource types defined by Amazon OpenSearch Serverless, including the format of the ARNs for each of the resource types, see [Actions, resources, and condition keys for Amazon OpenSearch Serverless](https://docs.aws.amazon.com/service-authorization/latest/reference/list_your_service.html) in the *Service Authorization Reference*\.

**Topics**
+ [Policy best practices](#security_iam_serverless-policy-best-practices)
+ [Using OpenSearch Serverless in the console](#security_iam_serverless_id-based-policy-examples-console)
+ [Administering OpenSearch Serverless collections](#security_iam_id-based-policy-examples-collection-admin)
+ [Viewing OpenSearch Serverless collections](#security_iam_id-based-policy-examples-view-collections)

### Policy best practices<a name="security_iam_serverless-policy-best-practices"></a>

Identity\-based policies are very powerful\. They determine whether someone can create, access, or delete OpenSearch Serverless resources in your account\. These actions can incur costs for your AWS account\. When you create or edit identity\-based policies, follow these guidelines and recommendations:
+ **Grant least privilege** – When you create custom policies, grant only the permissions required to perform a task\. Start with a minimum set of permissions and grant additional permissions as necessary\. Doing so is more secure than starting with permissions that are too lenient and then trying to tighten them later\. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the *IAM User Guide*\.
+ **Enable MFA for sensitive operations** – For extra security, require IAM users to use multi\-factor authentication \(MFA\) to access sensitive resources or API operations\. For more information, see [Using multi\-factor authentication \(MFA\) in AWS](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_mfa.html) in the *IAM User Guide*\.
+ **Use policy conditions for extra security** – To the extent that it's practical, define the conditions under which your identity\-based policies allow access to a resource\. For example, you can write conditions to specify a range of allowable IP addresses that a request must come from\. You can also write conditions to allow requests only within a specified date or time range, or to require the use of SSL or MFA\. For more information, see [IAM JSON policy elements: Condition](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_condition.html) in the *IAM User Guide*\.

### Using OpenSearch Serverless in the console<a name="security_iam_serverless_id-based-policy-examples-console"></a>

To access OpenSearch Serverless within the OpenSearch Service console, you must have a minimum set of permissions\. These permissions must allow you to list and view details about the OpenSearch Serverless resources in your AWS account\. If you create an identity\-based policy that is more restrictive than the minimum required permissions, the console won't function as intended for entities \(IAM users or roles\) with that policy\.

You don't need to allow minimum console permissions for users that are making calls only to the AWS CLI or the AWS API\. Instead, allow access to only the actions that match the API operation that you're trying to perform\.

The following policy allows a user to access OpenSearch Serverless within the OpenSearch Service console:

```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Resource": "*",
            "Effect": "Allow",
            "Action": [
                "aoss:ListCollections",
                "aoss:BatchGetCollection",
                "aoss:ListAccessPolicies",
                "aoss:ListSecurityConfigs",
                "aoss:ListSecurityPolicies",
                "aoss:ListTagsForResource",
                "aoss:ListVpcEndpoints",
                "aoss:GetAccessPolicy",
                "aoss:GetAccountSettings",
                "aoss:GetSecurityConfig",
                "aoss:GetSecurityPolicy"
            ]
        }
    ]
}
```

### Administering OpenSearch Serverless collections<a name="security_iam_id-based-policy-examples-collection-admin"></a>

This policy is an example of a "collection admin" policy that allows a user to manage and administer Amazon OpenSearch Serverless collections\. The user can create, view, and delete collections\. You can attach this policy to one or more IAM users in your account\.

```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Resource": "arn:aws:aoss:*:123456789012:collection/*",
            "Action": [
                "aoss:CreateCollection",
                "aoss:DeleteCollection",
                "aoss:UpdateCollection"
            ],
            "Effect": "Allow"
        },
        {
            "Resource": "*",
            "Action": [
                "aoss:BatchGetCollection",
                "aoss:ListCollections",
                "aoss:CreateAccessPolicy",
                "aoss:CreateSecurityPolicy"
            ],
            "Effect": "Allow"
        }
    ]
}
```

### Viewing OpenSearch Serverless collections<a name="security_iam_id-based-policy-examples-view-collections"></a>

This example policy allows a user to view details for all Amazon OpenSearch Serverless collections in their account\. The user can't modify the collections or any associated security policies\.

```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Resource": "*",
            "Action": [
                "aoss:ListAccessPolicies",
                "aoss:ListCollections",
                "aoss:ListSecurityPolicies",
                "aoss:ListTagsForResource",
                "aoss:BatchGetCollection"
            ],
            "Effect": "Allow"
        }
    ]
}
```