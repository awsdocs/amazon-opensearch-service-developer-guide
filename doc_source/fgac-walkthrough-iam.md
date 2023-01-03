# Tutorial: Configure a domain with an IAM master user and Amazon Cognito authentication<a name="fgac-walkthrough-iam"></a>

This tutorial covers a popular Amazon OpenSearch Service use case for [fine\-grained access control](fgac.md): an IAM master user with Amazon Cognito authentication for OpenSearch Dashboards\. 

In the tutorial, we'll configure a *master* IAM role and a *limited* IAM role, which we'll then associate with users in Amazon Cognito\. The master user can then sign in to OpenSearch Dashboards, map the limited user to a role, and use fine\-grained access control to limit the user's permissions\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/fgac-cognito.png)

Although these steps use the Amazon Cognito user pool for authentication, this same basic process works for any Cognito authentication provider that lets you assign different IAM roles to different users\.

You'll complete the following steps in this tutorial:

1. [Create master and limited IAM roles](#fgac-walkthrough-roles)

1. [Create a domain with Cognito authentication](#fgac-walkthrough-domain)

1. [Configure Cognito users and groups](#fgac-walkthrough-cognito)

1. [Map roles in OpenSearch Dashboards](#fgac-walkthrough-dashboards)

1. [Test the permissions](fgac.md#fgac-walkthrough-test)

## Prerequisites<a name="fgac-walkthrough-prereqs"></a>

In order to perform the steps in this tutorial, you must complete the following prerequisites:
+ [Create a Cognito user pool](https://docs.aws.amazon.com/cognito/latest/developerguide/tutorial-create-user-pool.html)\. For **Cognito user pool sign\-in options**, make sure **User name** is selected\.
+ [Create a Cognito identity pool](https://docs.aws.amazon.com/cognito/latest/developerguide/tutorial-create-identity-pool.html)\. Note the **Identity ID** of the pool, as you'll need it in the next step when you create your IAM roles\.

The user pool and identity pool must be in the same AWS Region\.

## Step 1: Create master and limited IAM roles<a name="fgac-walkthrough-roles"></a>

Navigate to the AWS Identity and Access Management \(IAM\) console and create two separate roles:
+ `MasterUserRole` – The master user, which will have full permissions to the cluster and manage roles and role mappings\.
+ `LimitedUserRole` – A more restricted role, which you'll grant limited access to as the master user\.

For instructions to create the roles, see [Creating a role using custom trust policies](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create_for-custom.html)\.

Both roles must have the following trust policy, which allows your Cognito identity pool to assume the roles:

```
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": {
      "Federated": "cognito-identity.amazonaws.com"
    },
    "Action": "sts:AssumeRoleWithWebIdentity",
    "Condition": {
      "StringEquals": {
        "cognito-identity.amazonaws.com:aud": "identity-pool-id"
      },
      "ForAnyValue:StringLike": {
        "cognito-identity.amazonaws.com:amr": "authenticated"
      }
    }
  }]
}
```

**Note**  
Replace `identity-pool-id` with the unique identifier of your Amazon Cognito identity pool\. For example, `us-east-1:0c6cdba7-3c3c-443b-a958-fb9feb207aa6`\.

## Step 2: Create a domain with Cognito authentication<a name="fgac-walkthrough-domain"></a>

Navigate to the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/esv3/](https://console.aws.amazon.com/esv3/ ) and [create a domain](createupdatedomains.md) with the following settings:
+ OpenSearch 1\.0 or later, or Elasticsearch 7\.8 or later
+ Public access
+ Fine\-grained access control enabled with `MasterUserRole` as the master user \(created in the previous step\) 
+ Amazon Cognito authentication enabled for OpenSearch Dashboards\. For instructions to enable Cognito authentication and select a user and identity pool, see [Configuring a domain to use Amazon Cognito authentication](cognito-auth.md#cognito-auth-config)\.
+ The following domain access policy:

  ```
  {
    "Version": "2012-10-17",
    "Statement": [
      {
        "Effect": "Allow",
        "Principal": {
          "AWS": [
            "*"
          ]
        },
        "Action": [
          "es:ESHttp*"
        ],
        "Resource": "arn:aws:es:region:account:domain/domain-name/*"
      }
    ]
  }
  ```
+ HTTPS required for all traffic to the domain
+ Node\-to\-node encryption
+ Encryption of data at rest

## Step 3: Configure Cognito users and groups<a name="fgac-walkthrough-cognito"></a>

While your domain is being created, configure the master and limited users and groups within Amazon Cognito\.

1. Navigate to the Amazon Cognito console at [https://console\.aws\.amazon\.com/cognito/v2/](https://console.aws.amazon.com/cognito/v2/home) and choose **User pools**\.

1. Choose your user pool to open its configuration, then choose **Create user**\.

1. Specify a user name of `master-user` and a password, and then choose **Create user**\.

1. Create another user named `limited-user`\.

1. Go to the **Groups** tab and choose **Create group**\. Name the group `master-user-group`\.

1. Select `MasterUserRole` in the **IAM role** dropdown, and then choose **Create group**\.

1. Create another group named `limited-user-group` that uses the `LimitedUserRole` IAM role\.

Then, add the users to their corresponding groups\.

1. Choose `master-user-group`, choose **Add user to group**, and select `master-user`\.

1. Choose `limited-user-group`, choose **Add user to group**, and select `limited-user`\.

Lastly, configure your identity pool\.

1. Go to the **App integration** tab\. Under **App clients and analytics**, note the client ID for your domain\.

1. Choose **Federated Identities** from the left navigation pane\.

1. Select your identity pool and choose **Edit identity pool**\.

1. Expand **Authentication providers**, find your user pool ID and the app client ID for your domain, and change **Use default role** to **Choose role from token**\.

1. For **Role resolution**, choose **DENY**\. With this setting, users must be in a group to receive an IAM role after authenticating\.

1. Choose **Save Changes**\.

## Step 4: Map roles in OpenSearch Dashboards<a name="fgac-walkthrough-dashboards"></a>

Now that your users and groups are configured, you can sign in to OpenSearch Dashboards as the master user and map users to roles\.

1. Go back to the OpenSearch Service console and navigate to the OpenSearch Dashboards URL for the domain you created\. The URL follows this format: `domain-endpoint/_dashboards/`\.

1. Sign in with the `master-user` credentials\.

1. Choose **Add sample data** and add the sample flight data\.

1. In the left navigation pane, choose **Security**, **Roles**, **Create role**\.

1. Name the role `new-role`\.

1. For **Index**, specify `opensearch_dashboards_sample_data_fli*` \(`kibana_sample_data_fli*` on Elasticsearch domains\)\.

1. For **Index permissions**, choose **read**\.

1. For **Document level security**, specify the following query:

   ```
   {
     "match": {
       "FlightDelay": true
     }
   }
   ```

1. For field\-level security, choose **Exclude** and specify `FlightNum`\.

1. For **Anonymization**, specify `Dest`\.

1. Choose **Create**\.

1. Choose **Mapped users**, **Manage mapping**\. Add the Amazon Resource Name \(ARN\) for `LimitedUserRole` as an external identity and choose **Map**\.

1. Return to the list of roles and choose **opensearch\_dashboards\_user**\. Choose **Mapped users**, **Manage mapping**\. Add the ARN for `LimitedUserRole` as a backend role and choose **Map**\.