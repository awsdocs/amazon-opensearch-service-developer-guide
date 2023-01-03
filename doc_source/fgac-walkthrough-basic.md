# Tutorial: Configure a domain with the internal user database and HTTP basic authentication<a name="fgac-walkthrough-basic"></a>

This tutorial covers another popular [fine\-grained access control](fgac.md) use case: a master user in the internal user database and HTTP basic authentication for OpenSearch Dashboards\.

**To get started with fine\-grained access control**

1. [Create a domain](createupdatedomains.md) with the following settings:
   + OpenSearch 1\.0 or later, or Elasticsearch 7\.9 or later
   + Public access
   + Fine\-grained access control with a master user in the internal user database \(`TheMasterUser` for the rest of this tutorial\)
   + Amazon Cognito authentication for Dashboards *disabled*
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

1. Navigate to OpenSearch Dashboards\.

1. Sign in using `TheMasterUser`\.

1. Choose **Try our sample data**\.

1. Add the sample flight data\.

1. Choose **Security**, **Internal users**, **Create internal user**\.

1. Name the user `new-user` and specify a password\. Then choose **Create**\.

1. Choose **Roles**, **Create role**\.

1. Name the role `new-role`\.

1. For index permissions, specify `dashboards_sample_data_fli*` for the index pattern\.

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

1. Choose **Mapped users**, **Manage mapping**\. Then add `new-user` to **Users** and choose **Map**\.

1. Return to the list of roles and choose **opensearch\_dashboards\_user**\. Choose **Mapped users**, **Manage mapping**\. Then add `new-user` to **Users** and choose **Map**\.

1. In a new, private browser window, navigate to Dashboards, sign in using `new-user`, and then choose **Explore on my own**\.

1. Go to **Dev Tools** and run the default search:

   ```
   GET _search
   {
     "query": {
       "match_all": {}
     }
   }
   ```

   Note the permissions error\. `new-user` doesn't have permissions to run cluster\-wide searches\.

1. Run another search:

   ```
   GET dashboards_sample_data_flights/_search
   {
     "query": {
       "match_all": {}
     }
   }
   ```

   Note that all matching documents have a `FlightDelay` field of `true`, an anonymized `Dest` field, and no `FlightNum` field\.

1. In your original browser window, signed in as `TheMasterUser`, choose **Dev Tools** and perform the same searches\. Note the difference in permissions, number of hits, matching documents, and included fields\.