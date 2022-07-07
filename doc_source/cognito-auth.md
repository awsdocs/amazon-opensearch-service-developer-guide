# Configuring Amazon Cognito authentication for OpenSearch Dashboards<a name="cognito-auth"></a>

You can authenticate and protect your Amazon OpenSearch Service default installation of OpenSearch Dashboards using [Amazon Cognito](https://docs.aws.amazon.com/cognito/latest/developerguide/what-is-amazon-cognito.html)\. Amazon Cognito authentication is optional and available only for domains using OpenSearch or Elasticsearch 5\.1 or later\. If you don't configure Amazon Cognito authentication, you can still protect Dashboards using an [IP\-based access policy](ac.md#ac-types-ip) and a [proxy server](dashboards.md#dashboards-proxy), HTTP basic authentication, or [SAML](saml.md)\.

Much of the authentication process occurs in Amazon Cognito, but this section offers guidelines and requirements for configuring Amazon Cognito resources to work with OpenSearch Service domains\. [Standard pricing](https://aws.amazon.com/cognito/pricing/) applies to all Amazon Cognito resources\.

**Tip**  
The first time you configure a domain to use Amazon Cognito authentication for Dashboards, we recommend using the console\. Amazon Cognito resources are extremely customizable, and the console can help you identify and understand the features that matter to you\.

**Topics**
+ [Prerequisites](#cognito-auth-prereq)
+ [Configuring a domain to use Amazon Cognito](#cognito-auth-config)
+ [Allowing the authenticated role](#cognito-auth-config-ac)
+ [Configuring identity providers](#cognito-auth-identity-providers)
+ [\(Optional\) Configuring granular access](#cognito-auth-granular)
+ [\(Optional\) Customizing the sign\-in page](#cognito-auth-customize)
+ [\(Optional\) Configuring advanced security](#cognito-auth-advanced)
+ [Testing](#cognito-auth-testing)
+ [Limits](#cognito-auth-limits)
+ [Common configuration issues](#cognito-auth-troubleshooting)
+ [Disabling Amazon Cognito authentication for OpenSearch Dashboards](#cognito-auth-disable)
+ [Deleting domains that use Amazon Cognito authentication for OpenSearch Dashboards](#cognito-auth-delete)

## Prerequisites<a name="cognito-auth-prereq"></a>

Before you can configure Amazon Cognito authentication for OpenSearch Dashboards, you must fulfill several prerequisites\. The OpenSearch Service console helps streamline the creation of these resources, but understanding the purpose of each resource helps with configuration and troubleshooting\. Amazon Cognito authentication for Dashboards requires the following resources:
+ Amazon Cognito [user pool](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-identity-pools.html)
+ Amazon Cognito [identity pool](https://docs.aws.amazon.com/cognito/latest/developerguide/identity-pools.html)
+ IAM role that has the `AmazonOpenSearchServiceCognitoAccess` policy attached \(`CognitoAccessForAmazonOpenSearch`\)

**Note**  
The user pool and identity pool must be in the same AWS Region\. You can use the same user pool, identity pool, and IAM role to add Amazon Cognito authentication for Dashboards to multiple OpenSearch Service domains\. To learn more, see [Limits](#cognito-auth-limits)\.

### About the user pool<a name="cognito-auth-prereq-up"></a>

User pools have two main features: create and manage a directory of users, and let users sign up and log in\. For instructions to create a user pool, see [Create a User Pool](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-pool-as-user-directory.html) in the *Amazon Cognito Developer Guide*\.

When you create a user pool to use with OpenSearch Service, consider the following:
+ Your Amazon Cognito user pool must have a [domain name](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-pools-domain.html)\. OpenSearch Service uses this domain name to redirect users to a login page for accessing Dashboards\. Other than a domain name, the user pool doesn't require any non\-default configuration\.
+ You must specify the pool's required [standard attributes](https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-settings-attributes.html#cognito-user-pools-standard-attributes)—attributes like name, birth date, email address, and phone number\. You can't change these attributes after you create the user pool, so choose the ones that matter to you at this time\.
+ While creating your user pool, choose whether users can create their own accounts, the minimum password strength for accounts, and whether to enable multi\-factor authentication\. If you plan to use an [external identity provider](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-pools-identity-federation.html), these settings are inconsequential\. Technically, you can enable the user pool as an identity provider *and* enable an external identity provider, but most people prefer one or the other\.

User pool IDs take the form of `region_ID`\. If you plan to use the AWS CLI or an AWS SDK to configure OpenSearch Service, make note of the ID\.

### About the identity pool<a name="cognito-auth-prereq-ip"></a>

Identity pools let you assign temporary, limited\-privilege roles to users after they log in\. For instructions about creating an identity pool, see [Identity Pools](https://docs.aws.amazon.com/cognito/latest/developerguide/identity-pools.html) in the *Amazon Cognito Developer Guide*\. When you create an identity pool to use with OpenSearch Service, consider the following: 
+ If you use the Amazon Cognito console, you must select the **Enable access to unauthenticated identities** check box to create the identity pool\. After you create the identity pool and [configure the OpenSearch Service domain](#cognito-auth-config), Amazon Cognito disables this setting\.
+ You don't need to add [external identity providers](https://docs.aws.amazon.com/cognito/latest/developerguide/external-identity-providers.html) to the identity pool\. When you configure OpenSearch Service to use Amazon Cognito authentication, it configures the identity pool to use the user pool that you just created\.
+ After you create the identity pool, you must choose unauthenticated and authenticated IAM roles\. These roles specify the access policies that users have before and after they log in\. If you use the Amazon Cognito console, it can create these roles for you\. After you create the authenticated role, make note of the ARN, which takes the form of `arn:aws:iam::123456789012:role/Cognito_identitypoolAuth_Role`\.

Identity pool IDs take the form of `region:ID-ID-ID-ID-ID`\. If you plan to use the AWS CLI or an AWS SDK to configure OpenSearch Service, make note of the ID\.

### About the CognitoAccessForAmazonOpenSearch role<a name="cognito-auth-role"></a>

OpenSearch Service needs permissions to configure the Amazon Cognito user and identity pools and use them for authentication\. You can use `AmazonOpenSearchServiceCognitoAccess`, which is an AWS\-managed policy, for this purpose\. `AmazonESCognitoAccess` is a legacy policy that was replaced by `AmazonOpenSearchServiceCognitoAccess` when Amazon Elasticsearch Service was renamed to Amazon OpenSearch Service\. Both policies provide the minimum Amazon Cognito permissions necessary to enable [Cognito authentication](#cognito-auth)\. For the policy JSON, see the [IAM console](https://console.aws.amazon.com/iam/home#/policies/arn:aws:iam::aws:policy/AmazonOpenSearchServiceCognitoAccess)\. 

If you use the console to create or configure your OpenSearch Service domain, it creates an IAM role for you and attaches the `AmazonOpenSearchServiceCognitoAccess` policy \(or the `AmazonESCognitoAccess` policy if it's an Elasticsearch domain\) to the role\. The default name for this role is `CognitoAccessForAmazonOpenSearch`\.

The role permissions policies `AmazonOpenSearchServiceCognitoAccess` and `AmazonESCognitoAccess` both allow OpenSearch Service to complete the following actions on all identity and user pools:
+ Action: `cognito-idp:DescribeUserPool`
+ Action: `cognito-idp:CreateUserPoolClient`
+ Action: `cognito-idp:DeleteUserPoolClient`
+ Action: `cognito-idp:UpdateUserPoolClient`
+ Action: `cognito-idp:DescribeUserPoolClient`
+ Action: `cognito-idp:AdminInitiateAuth`
+ Action: `cognito-idp:AdminUserGlobalSignOut`
+ Action: `cognito-idp:ListUserPoolClients`
+ Action: `cognito-identity:DescribeIdentityPool`
+ Action: `cognito-identity:SetIdentityPoolRoles`
+ Action: `cognito-identity:GetIdentityPoolRoles`

If you use the AWS CLI or one of the AWS SDKs, you must create your own role, attach the policy, and specify the ARN for this role when you configure your OpenSearch Service domain\. The role must have the following trust relationship:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "opensearchservice.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
```

For instructions, see [Creating a Role to Delegate Permissions to an AWS Service](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create_for-service.html) and [Attaching and Detaching IAM Policies](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_manage-attach-detach.html) in the *IAM User Guide*\.

## Configuring a domain to use Amazon Cognito<a name="cognito-auth-config"></a>

After you complete the prerequisites, you can configure an OpenSearch Service domain to use Amazon Cognito for Dashboards\.

**Note**  
Amazon Cognito is not available in all AWS Regions\. For a list of supported Regions, see [AWS Regions and Endpoints](https://docs.aws.amazon.com/general/latest/gr/cognito_identity.html)\. You don't need to use the same Region for Amazon Cognito that you use for OpenSearch Service\.

### Configuring Amazon Cognito authentication \(console\)<a name="cognito-auth-config-console"></a>

Because it creates the [CognitoAccessForAmazonOpenSearch](#cognito-auth-role) role for you, the console offers the simplest configuration experience\. In addition to the standard OpenSearch Service permissions, you need the following set of permissions to use the console to create a domain that uses Amazon Cognito authentication for OpenSearch Dashboards\.

```
{
  "Version": "2012-10-17",
  "Statement": [{
      "Effect": "Allow",
      "Action": [
        "ec2:DescribeVpcs",
        "cognito-identity:ListIdentityPools",
        "cognito-idp:ListUserPools",
        "iam:CreateRole",
        "iam:AttachRolePolicy"
      ],
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "iam:GetRole",
        "iam:PassRole"
      ],
      "Resource": "arn:aws:iam::123456789012:role/service-role/CognitoAccessForAmazonOpenSearch"
    }
  ]
}
```

For instructions to add permissions to an identity \(user, user group, or role\), see [Adding IAM identity permissions \(console\)](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_manage-attach-detach.html#add-policies-console)\.

If [CognitoAccessForAmazonOpenSearch](#cognito-auth-role) already exists, you need fewer permissions:

```
{
  "Version": "2012-10-17",
  "Statement": [{
      "Effect": "Allow",
      "Action": [
        "ec2:DescribeVpcs",
        "cognito-identity:ListIdentityPools",
        "cognito-idp:ListUserPools"
      ],
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "iam:GetRole",
        "iam:PassRole"
      ],
      "Resource": "arn:aws:iam::123456789012:role/service-role/CognitoAccessForAmazonOpenSearch"
    }
  ]
}
```

**To configure Amazon Cognito authentication for Dashboards \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Amazon OpenSearch Service**\.

1. Under **Domains**, select the domain you want to configure\.

1. Choose **Actions**, **Edit security configuration**\.

1. Select **Enable Amazon Cognito authentication**\.

1. For **Region**, select the Region that contains your Amazon Cognito user pool and identity pool\.

1. For **Cognito user pool**, select a user pool or create one\. For guidance, see [About the user pool](#cognito-auth-prereq-up)\.

1. For **Cognito identity pool**, select an identity pool or create one\. For guidance, see [About the identity pool](#cognito-auth-prereq-ip)\.
**Note**  
The **Create user pool** and **Create identity pool** links direct you to the Amazon Cognito console and require you to create these resources manually\. The process is not automatic\. To learn more, see [Prerequisites](#cognito-auth-prereq)\.

1. For **IAM role name**, use the default value of `CognitoAccessForAmazonOpenSearch` \(recommended\) or enter a new name\. To learn more about the purpose of this role, see [About the CognitoAccessForAmazonOpenSearch role](#cognito-auth-role)\.

1. Choose **Save changes**\.

After your domain finishes processing, see [Allowing the authenticated role](#cognito-auth-config-ac) and [Configuring identity providers](#cognito-auth-identity-providers) for additional configuration steps\.

### Configuring Amazon Cognito authentication \(AWS CLI\)<a name="cognito-auth-config-cli"></a>

Use the `--cognito-options` parameter to configure your OpenSearch Service domain\. The following syntax is used by both the `create-domain` and `update-domain-config` commands:

```
--cognito-options Enabled=true,UserPoolId="user-pool-id",IdentityPoolId="identity-pool-id",RoleArn="arn:aws:iam::123456789012:role/CognitoAccessForAmazonOpenSearch"
```

**Example**

The following example creates a domain in the `us-east-1` Region that enables Amazon Cognito authentication for Dashboards using the `CognitoAccessForAmazonOpenSearch` role and provides domain access to `Cognito_Auth_Role`:

```
aws opensearch create-domain --domain-name my-domain --region us-east-1 --access-policies '{ "Version":"2012-10-17", "Statement":[{"Effect":"Allow","Principal":{"AWS": ["arn:aws:iam::123456789012:role/Cognito_Auth_Role"]},"Action":"es:ESHttp*","Resource":"arn:aws:es:us-east-1:123456789012:domain/*" }]}' --engine-version "OpenSearch_1.0" --cluster-config InstanceType=m4.xlarge.search,InstanceCount=1 --ebs-options EBSEnabled=true,VolumeSize=10 --cognito-options Enabled=true,UserPoolId="us-east-1_123456789",IdentityPoolId="us-east-1:12345678-1234-1234-1234-123456789012",RoleArn="arn:aws:iam::123456789012:role/CognitoAccessForAmazonOpenSearch"
```

After your domain finishes processing, see [Allowing the authenticated role](#cognito-auth-config-ac) and [Configuring identity providers](#cognito-auth-identity-providers) for additional configuration steps\.

### Configuring Amazon Cognito Authentication \(AWS SDKs\)<a name="cognito-auth-config-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all the operations that are defined in the [Configuration API reference for Amazon OpenSearch Service](configuration-api.md), including the `CognitoOptions` parameter for the `CreateDomain` and `UpdateDomainConfig` operations\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\.

After your domain finishes processing, see [Allowing the authenticated role](#cognito-auth-config-ac) and [Configuring identity providers](#cognito-auth-identity-providers) for additional configuration steps\.

## Allowing the authenticated role<a name="cognito-auth-config-ac"></a>

By default, the authenticated IAM role that you configured by following the guidelines in [About the identity pool](#cognito-auth-prereq-ip) does not have the necessary privileges to access OpenSearch Dashboards\. You must provide the role with additional permissions\.

**Important**  
If you configured [fine\-grained access control](fgac.md) and use an "open" or IP\-based access policy, you can skip this step\.

You can include these permissions in an [identity\-based](ac.md#ac-types-identity) policy, but unless you want authenticated users to have access to all OpenSearch Service domains, a [resource\-based](ac.md#ac-types-resource) policy attached to a single domain is the better approach:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": [
          "arn:aws:iam::123456789012:role/Cognito_identitypoolAuth_Role"
        ]
      },
      "Action": [
        "es:ESHttp*"
      ],
      "Resource": "arn:aws:es:region:123456789012:domain/domain-name/*"
    }
  ]
}
```

 For instructions about adding a resource\-based policy to an OpenSearch Service domain, see [Configuring access policies](createupdatedomains.md#createdomain-configure-access-policies)\.

## Configuring identity providers<a name="cognito-auth-identity-providers"></a>

When you configure a domain to use Amazon Cognito authentication for Dashboards, OpenSearch Service adds an [app client](https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-settings-client-apps.html) to the user pool and adds the user pool to the identity pool as an authentication provider\. The following screenshot shows the **App client settings** page in the Amazon Cognito console\.

![\[Amazon Cognito console showing app client settings\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/cognito-app-client.png)

**Warning**  
Don't rename or delete the app client\.

Depending on how you configured your user pool, you might need to create user accounts manually, or users might be able to create their own\. If these settings are acceptable, you don't need to take further action\. Many people, however, prefer to use external identity providers\.

To enable a SAML 2\.0 identity provider, you must provide a SAML metadata document\. To enable social identity providers like Login with Amazon, Facebook, and Google, you must have an app ID and app secret from those providers\. You can enable any combination of identity providers\. The sign\-in page adds options as you add providers, as shown in the following screenshot\.

![\[Sign-in page with several options\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/auth-providers.png)

The easiest way to configure your user pool is to use the Amazon Cognito console\. Use the **Identity Providers** page to add external identity providers and the **App client settings** page to enable and disable identity providers for the OpenSearch Service domain's app client\. For example, you might want to enable your own SAML identity provider and disable **Cognito User Pool** as an identity provider\.

For instructions, see [Using Federation from a User Pool](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-pools-identity-federation.html) and [Specifying Identity Provider Settings for Your User Pool App](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-pools-app-idp-settings.html) in the *Amazon Cognito Developer Guide*\.

## \(Optional\) Configuring granular access<a name="cognito-auth-granular"></a>

You might have noticed that the default identity pool settings assign every user who logs in the same IAM role \(`Cognito_identitypoolAuth_Role`\), which means that every user can access the same AWS resources\. If you want to use [fine\-grained access control](fgac.md) with Amazon Cognito—for example, if you want your organization's analysts to have read\-only access to several indices, but developers to have write access to all indices—you have two options:
+ Create user groups and configure your identity provider to choose the IAM role based on the user's authentication token \(recommended\)\.
+ Configure your identity provider to choose the IAM role based on one or more rules\.

You configure these options using the **Edit identity pool** page of the Amazon Cognito console, as shown in the following screenshot\. For a walkthrough that includes fine\-grained access control, see [Tutorial: IAM master user and Amazon Cognito](fgac-walkthrough-iam.md)\.

![\[Role options for an authentication provider\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/cognito-roles.png)

**Important**  
Just like the default role, Amazon Cognito must be part of each additional role's trust relationship\. For details, see [Creating Roles for Role Mapping](https://docs.aws.amazon.com/cognito/latest/developerguide/role-based-access-control.html#creating-roles-for-role-mapping) in the *Amazon Cognito Developer Guide*\.

### User groups and tokens<a name="cognito-auth-granular-tokens"></a>

When you create a user group, you choose an IAM role for members of the group\. For information about creating groups, see [User Groups](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-pools-user-groups.html) in the *Amazon Cognito Developer Guide*\.

After you create one or more user groups, you can configure your authentication provider to assign users their groups' roles rather than the identity pool's default role\. Select **Choose role from token**, then choose either **Use default Authenticated role** or **DENY** to specify how the identity pool handles users who aren't part of a group\.

### Rules<a name="cognito-auth-granular-rules"></a>

Rules are essentially a series of `if` statements that Amazon Cognito evaluates sequentially\. For example, if a user's email address contains `@corporate`, Amazon Cognito assigns that user `Role_A`\. If a user's email address contains `@subsidiary`, it assigns that user `Role_B`\. Otherwise, it assigns the user the default authenticated role\.

To learn more, see [Using Rule\-Based Mapping to Assign Roles to Users](https://docs.aws.amazon.com/cognito/latest/developerguide/role-based-access-control.html#using-rules-to-assign-roles-to-users) in the *Amazon Cognito Developer Guide*\.

## \(Optional\) Customizing the sign\-in page<a name="cognito-auth-customize"></a>

The **UI customization** page of the Amazon Cognito console lets you upload a custom logo and make CSS changes to the sign\-in page\. For instructions and a full list of CSS properties, see [Specifying App UI Customization Settings for Your User Pool](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-pools-app-ui-customization.html) in the *Amazon Cognito Developer Guide*\.

## \(Optional\) Configuring advanced security<a name="cognito-auth-advanced"></a>

Amazon Cognito user pools support advanced security features like multi\-factor authentication, compromised credential checking, and adaptive authentication\. To learn more, see [Managing Security](https://docs.aws.amazon.com/cognito/latest/developerguide/managing-security.html) in the *Amazon Cognito Developer Guide*\.

## Testing<a name="cognito-auth-testing"></a>

After you're satisfied with your configuration, verify that the user experience meets your expectations\.

**To access OpenSearch Dashboards**

1. Navigate to `https://opensearch-domain/_dashboards/` in a web browser\.

1. Sign in using your preferred credentials\.

1. After OpenSearch Dashboards loads, configure at least one index pattern\. Dashboards uses these patterns to identity which indices that you want to analyze\. Enter `*`, choose **Next step**, and then choose **Create index pattern**\.

1. To search or explore your data, choose **Discover**\.

If any step of this process fails, see [Common configuration issues](#cognito-auth-troubleshooting) for troubleshooting information\.

## Limits<a name="cognito-auth-limits"></a>

Amazon Cognito has soft limits on many of its resources\. If you want to enable Dashboards authentication for a large number of OpenSearch Service domains, review [Limits in Amazon Cognito](https://docs.aws.amazon.com/cognito/latest/developerguide/limits.html) and [request limit increases](https://docs.aws.amazon.com/general/latest/gr/aws_service_limits.html) as necessary\.

Each OpenSearch Service domain adds an [app client](https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-settings-client-apps.html) to the user pool, which adds an [authentication provider](https://docs.aws.amazon.com/cognito/latest/developerguide/external-identity-providers.html) to the identity pool\. If you enable OpenSearch Dashboards authentication for more than 10 domains, you might encounter the "maximum Amazon Cognito user pool providers per identity pool" limit\. If you exceed a limit, any OpenSearch Service domains that you try to configure to use Amazon Cognito authentication for Dashboards can get stuck in a configuration state of **Processing**\.

## Common configuration issues<a name="cognito-auth-troubleshooting"></a>

The following tables list common configuration issues and solutions\.


**Configuring OpenSearch Service**  

| Issue | Solution | 
| --- | --- | 
|  `OpenSearch Service can't create the role` \(console\)  | You don't have the correct IAM permissions\. Add the permissions specified in [Configuring Amazon Cognito authentication \(console\)](#cognito-auth-config-console)\. | 
|  `User is not authorized to perform: iam:PassRole on resource CognitoAccessForAmazonOpenSearch` \(console\)  | You don't have iam:PassRole permissions for the [CognitoAccessForAmazonOpenSearch](#cognito-auth-role) role\. Attach the following policy to your account:<pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [<br />    {<br />      "Effect": "Allow",<br />      "Action": [<br />        "iam:PassRole"<br />      ],<br />      "Resource": "arn:aws:iam::123456789012:role/service-role/CognitoAccessForAmazonOpenSearch"<br />    }<br />  ]<br />}</pre>Alternately, you can attach the `IAMFullAccess` policy\. | 
|  `User is not authorized to perform: cognito-identity:ListIdentityPools on resource`  |  You don't have read permissions for Amazon Cognito\. Attach the `AmazonCognitoReadOnly` policy to your account\.  | 
|  `An error occurred (ValidationException) when calling the CreateDomain operation: OpenSearch Service must be allowed to use the passed role`  |  OpenSearch Service isn't specified in the trust relationship of the `CognitoAccessForAmazonOpenSearch` role\. Check that your role uses the trust relationship that is specified in [About the CognitoAccessForAmazonOpenSearch role](#cognito-auth-role)\. Alternately, use the console to configure Amazon Cognito authentication\. The console creates a role for you\.  | 
|  `An error occurred (ValidationException) when calling the CreateDomain operation: User is not authorized to perform: cognito-idp:action on resource: user pool`  | The role specified in \-\-cognito\-options does not have permissions to access Amazon Cognito\. Check that the role has the AWS managed AmazonOpenSearchServiceCognitoAccess policy attached\. Alternately, use the console to configure Amazon Cognito authentication\. The console creates a role for you\. | 
| An error occurred \(ValidationException\) when calling the CreateDomain operation: User pool does not exist |  OpenSearch Service can't find the user pool\. Confirm that you created one and have the correct ID\. To find the ID, you can use the Amazon Cognito console or the following AWS CLI command: <pre>aws cognito-idp list-user-pools --max-results 60 --region region</pre>  | 
|  `An error occurred (ValidationException) when calling the CreateDomain operation: IdentityPool not found`  |  OpenSearch Service can't find the identity pool\. Confirm that you created one and have the correct ID\. To find the ID, you can use the Amazon Cognito console or the following AWS CLI command: <pre>aws cognito-identity list-identity-pools --max-results 60 --region region</pre>  | 
|  `An error occurred (ValidationException) when calling the CreateDomain operation: Domain needs to be specified for user pool`  | The user pool does not have a domain name\. You can configure one using the Amazon Cognito console or the following AWS CLI command:<pre>aws cognito-idp create-user-pool-domain --domain name --user-pool-id id</pre> | 


**Accessing OpenSearch Dashboards**  

| Issue | Solution | 
| --- | --- | 
| The login page doesn't show my preferred identity providers\. |  Check that you enabled the identity provider for the OpenSearch Service app client as specified in [Configuring identity providers](#cognito-auth-identity-providers)\.  | 
|  The login page doesn't look as if it's associated with my organization\.  |  See [\(Optional\) Customizing the sign\-in page](#cognito-auth-customize)\.  | 
| My login credentials don't work\. |  Check that you have configured the identity provider as specified in [Configuring identity providers](#cognito-auth-identity-providers)\. If you use the user pool as your identity provider, check that the account exists and is confirmed on the **User and groups** page of the Amazon Cognito console\.  | 
|  OpenSearch Dashboards either doesn't load at all or doesn't work properly\.  |  The Amazon Cognito authenticated role needs `es:ESHttp*` permissions for the domain \(`/*`\) to access and use Dashboards\. Check that you added an access policy as specified in [Allowing the authenticated role](#cognito-auth-config-ac)\.  | 
| Invalid identity pool configuration\. Check assigned IAM roles for this pool\. | Amazon Cognito doesn't have permissions to assume the IAM role on behalf of the authenticated user\. Modify the trust relationship for the role to include:<pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [{<br />    "Effect": "Allow",<br />    "Principal": {<br />      "Federated": "cognito-identity.amazonaws.com"<br />    },<br />    "Action": "sts:AssumeRoleWithWebIdentity",<br />    "Condition": {<br />      "StringEquals": {<br />        "cognito-identity.amazonaws.com:aud": "identity-pool-id"<br />      },<br />      "ForAnyValue:StringLike": {<br />        "cognito-identity.amazonaws.com:amr": "authenticated"<br />      }<br />    }<br />  }]<br />}</pre> | 
| Token is not from a supported provider of this identity pool\. | This uncommon error can occur when you remove the app client from the user pool\. Try opening Dashboards in a new browser session\. | 

## Disabling Amazon Cognito authentication for OpenSearch Dashboards<a name="cognito-auth-disable"></a>

Use the following procedure to disable Amazon Cognito authentication for Dashboards\.

**To disable Amazon Cognito authentication for Dashboards \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Amazon OpenSearch Service**\.

1. In the navigation pane, under **Domains**, choose the domain you want to configure\.

1. Choose **Actions**, **Edit security configuration**\.

1. Deselect **Enable Amazon Cognito authentication**\.

1. Choose **Save changes**\.

**Important**  
If you no longer need the Amazon Cognito user pool and identity pool, delete them\. Otherwise, you can continue to incur charges\.

## Deleting domains that use Amazon Cognito authentication for OpenSearch Dashboards<a name="cognito-auth-delete"></a>

To prevent domains that use Amazon Cognito authentication for Dashboards from becoming stuck in a configuration state of **Processing**, delete OpenSearch Service domains *before* deleting their associated Amazon Cognito user pools and identity pools\.