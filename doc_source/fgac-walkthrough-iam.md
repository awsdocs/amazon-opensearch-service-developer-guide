# Tutorial: IAM master user and Amazon Cognito<a name="fgac-walkthrough-iam"></a>

This tutorial covers a popular [fine\-grained access control](fgac.md) use case: an IAM master user with Amazon Cognito authentication for OpenSearch Dashboards\. Although these steps use the Amazon Cognito user pool for authentication, this same basic process works for any Cognito authentication provider that lets you assign different IAM roles to different users\.

**Note**  
This tutorial assumes you have two existing IAM roles, one for the master user and one for more limited users\. If you don't have two roles, [create them](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create.html)\.

**To get started with fine\-grained access control**

1. [Create a domain](createupdatedomains.md) with the following settings:
   + OpenSearch 1\.0 or later, or Elasticsearch 7\.8 or later
   + Public access
   + Fine\-grained access control enabled with an IAM role as the master user \(`IAMMasterUserRole` for the rest of this tutorial\)
   + Amazon Cognito authentication enabled for OpenSearch Dashboards\. For instructions to enable Cognito authentication and select a user and identity pool, see [Configuring a domain to use Amazon Cognito](cognito-auth.md#cognito-auth-config)\.
   + The following access policy:

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

1. Navigate to the IAM console and choose **Roles**\.

1. Choose `IAMMasterUserRole` and go to the **Trust relationships** tab\.

1. Choose **Edit trust relationship**, and ensure that the Amazon Cognito identity pool can assume the role\. You should see the following statement:

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

1. Choose **Update Trust Policy**\.

1. Add the same trust policy to a second IAM role \(`IAMLimitedUserRole` for the rest of this tutorial\)\.

1. Navigate to the Amazon Cognito console and choose **Manage User Pools**\.

1. Choose your user pool, and then choose **Users and groups**\.

1. Choose **Create user**, specify a user name of `master-user` and a password, and then choose **Create user**\.

1. Create another user named `limited-user`\.

1. Go to the **Groups** tab and then choose **Create group**\.

1. Name the group `master-user-group`, choose `IAMMasterUserRole` in the **IAM role** dropdown list, and then choose **Create group**\.

1. Create another group named `limited-user-group` that uses `IAMLimitedUserRole`\.

1. Choose `master-user-group`, choose **Add users**, and then add `master-user`\.

1. Choose `limited-user-group`, choose **Add users**, and then add `limited-user`\.

1. Choose **App client settings** and note the app client ID for your domain\.

1. Choose **Federated Identities**, choose your identity pool, and then choose **Edit identity pool**\.

1. Expand **Authentication providers**, find your user pool ID and the app client ID for your domain, and then change **Use default role** to **Choose role from token**\.

1. For **Role resolution**, choose **DENY**\. With this setting, users must be in a group to receive an IAM role after authenticating\.

1. Choose **Save Changes**\.

1. Navigate to OpenSearch Dashboards\.

1. Sign in with `master-user`\.

1. Choose **Add sample data** and add some sample flight data\.

1. Choose **Security**, **Roles**, **Create role**\.

1. Name the role `new-role`\.

1. For index permissions, specify `opensearch_dashboards_sample_data_fli*` for the index pattern \(`kibana_sample_data_fli*` on Elasticsearch domains\)\.

1. For the action group, choose **read**\.

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

1. Choose **Mapped users**, **Manage mapping**\. Then add the ARN for `IAMLimitedUserRole` as an external identity and choose **Map**\.

1. Return to the list of roles and choose **opensearch\_dashboards\_user**\. Choose **Mapped users**, **Manage mapping**\. Add the ARN for `IAMLimitedUserRole` as a backend role and choose **Map**\.

1. In a new, private browser window, navigate to Dashboards, sign in using `limited-user`, and then choose **Explore on my own**\.

1. Go to **Dev Tools** and run the default search:

   ```
   GET _search
   {
     "query": {
       "match_all": {}
     }
   }
   ```

   Note the permissions error\. `limited-user` doesn't have permissions to run cluster\-wide searches\.

1. Run another search:

   ```
   GET opensearch_dashboards_sample_data_flights/_search
   {
     "query": {
       "match_all": {}
     }
   }
   ```

   Note that all matching documents have a `FlightDelay` field of `true`, an anonymized `Dest` field, and no `FlightNum` field\.

1. In your original browser window, signed in as `master-user`, choose **Dev Tools**, and then perform the same searches\. Note the difference in permissions, number of hits, matching documents, and included fields\.