# Fine\-grained access control in Amazon OpenSearch Service<a name="fgac"></a>

Fine\-grained access control offers additional ways of controlling access to your data on Amazon OpenSearch Service\. For example, depending on who makes the request, you might want a search to return results from only one index\. You might want to hide certain fields in your documents or exclude certain documents altogether\.

Fine\-grained access control offers the following benefits:
+ Role\-based access control
+ Security at the index, document, and field level
+ OpenSearch Dashboards multi\-tenancy
+ HTTP basic authentication for OpenSearch and OpenSearch Dashboards

**Topics**
+ [The bigger picture: fine\-grained access control and OpenSearch Service security](#fgac-access-policies)
+ [Key concepts](#fgac-concepts)
+ [Enabling fine\-grained access control](#fgac-enabling)
+ [Accessing OpenSearch Dashboards as the master user](#fgac-dashboards)
+ [Managing permissions](#fgac-access-control)
+ [Recommended configurations](#fgac-recommendations)
+ [Limitations](#fgac-limitations)
+ [Modifying the master user](#fgac-forget)
+ [Additional master users](#fgac-more-masters)
+ [Manual snapshots](#fgac-snapshots)
+ [Integrations](#fgac-integrations)
+ [REST API differences](#fgac-rest-api)
+ [Tutorial: Configure a domain with an IAM master user and Amazon Cognito authentication](fgac-walkthrough-iam.md)
+ [Step 5: Test the permissions](#fgac-walkthrough-test)
+ [Tutorial: Configure a domain with the internal user database and HTTP basic authentication](fgac-walkthrough-basic.md)

## The bigger picture: fine\-grained access control and OpenSearch Service security<a name="fgac-access-policies"></a>

Amazon OpenSearch Service security has three main layers:

**Network**  
The first security layer is the network, which determines whether requests reach an OpenSearch Service domain\. If you choose **Public access** when you create a domain, requests from any internet\-connected client can reach the domain endpoint\. If you choose **VPC access**, clients must connect to the VPC \(and the associated security groups must permit it\) for a request to reach the endpoint\. For more information, see [Launching your Amazon OpenSearch Service domains within a VPC](vpc.md)\.

**Domain access policy**  
The second security layer is the domain access policy\. After a request reaches a domain endpoint, the [resource\-based access policy](ac.md#ac-types-resource) allows or denies the request access to a given URI\. The access policy accepts or rejects requests at the "edge" of the domain, before they reach OpenSearch itself\.

**Fine\-grained access control**  
The third and final security layer is fine\-grained access control\. After a resource\-based access policy allows a request to reach a domain endpoint, fine\-grained access control evaluates the user credentials and either authenticates the user or denies the request\. If fine\-grained access control authenticates the user, it fetches all roles mapped to that user and uses the complete set of permissions to determine how to handle the request\.

**Note**  
If a resource\-based access policy contains IAM roles or users, clients must send signed requests using AWS Signature Version 4\. As such, access policies can conflict with fine\-grained access control, especially if you use the internal user database and HTTP basic authentication\. You can't sign a request with a username and password *and* IAM credentials\. In general, if you enable fine\-grained access control, we recommend using a domain access policy that doesn't require signed requests\.

The following diagram illustrates a common configuration: a VPC access domain with fine\-grained access control enabled, an IAM\-based access policy, and an IAM master user\.

![\[Fine-grained access control authorization flow with a VPC domain\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/fgac-vpc-iam.png)

The following diagram illustrates another common configuration: a public access domain with fine\-grained access control enabled, an access policy that doesn't use IAM principals, and a master user in the internal user database\.

![\[Fine-grained access control authorization flow with a public access domain\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/fgac-public-basic.png)

### Example<a name="fgac-example"></a>

Consider a `GET` request to `movies/_search?q=thor`\. Does the user have permissions to search the `movies` index? If so, does the user have permissions to see *all* documents within it? Should the response omit or anonymize any fields? For the master user, the response might look like this:

```
{
  "hits": {
    "total": 7,
    "max_score": 8.772789,
    "hits": [{
        "_index": "movies",
        "_type": "_doc",
        "_id": "tt0800369",
        "_score": 8.772789,
        "_source": {
          "directors": [
            "Kenneth Branagh",
            "Joss Whedon"
          ],
          "release_date": "2011-04-21T00:00:00Z",
          "genres": [
            "Action",
            "Adventure",
            "Fantasy"
          ],
          "plot": "The powerful but arrogant god Thor is cast out of Asgard to live amongst humans in Midgard (Earth), where he soon becomes one of their finest defenders.",
          "title": "Thor",
          "actors": [
            "Chris Hemsworth",
            "Anthony Hopkins",
            "Natalie Portman"
          ],
          "year": 2011
        }
      },
      ...
    ]
  }
}
```

If a user with more limited permissions issues the exact same request, the response might look like this:

```
{
  "hits": {
    "total": 2,
    "max_score": 8.772789,
    "hits": [{
        "_index": "movies",
        "_type": "_doc",
        "_id": "tt0800369",
        "_score": 8.772789,
        "_source": {
          "year": 2011,
          "release_date": "3812a72c6dd23eef3c750c2d99e205cbd260389461e19d610406847397ecb357",
          "plot": "The powerful but arrogant god Thor is cast out of Asgard to live amongst humans in Midgard (Earth), where he soon becomes one of their finest defenders.",
          "title": "Thor"
        }
      },
      ...
    ]
  }
}
```

The response has fewer hits and fewer fields for each hit\. Also, the `release_date` field is anonymized\. If a user with no permissions makes the same request, the cluster returns an error:

```
{
  "error": {
    "root_cause": [{
      "type": "security_exception",
      "reason": "no permissions for [indices:data/read/search] and User [name=limited-user, roles=[], requestedTenant=null]"
    }],
    "type": "security_exception",
    "reason": "no permissions for [indices:data/read/search] and User [name=limited-user, roles=[], requestedTenant=null]"
  },
  "status": 403
}
```

If a user provides invalid credentials, the cluster returns an `Unauthorized` exception\.

## Key concepts<a name="fgac-concepts"></a>

*Roles* are the core way of using fine\-grained access control\. In this case, roles are distinct from IAM roles\. Roles contain any combination of permissions: cluster\-wide, index\-specific, document level, and field level\.

After configuring a role, you *map* it to one or more users\. For example, you might map three roles to a single user: one role that provides access to Dashboards, one that provides read\-only access to `index1`, and one that provides write access to `index2`\. Or you could include all of those permissions in a single role\.

*Users* are people or applications that make requests to the OpenSearch cluster\. Users have credentials—either IAM access keys or a username and password—that they specify when they make requests\. With fine\-grained access control on Amazon OpenSearch Service, you choose one or the other for your *master user* when you configure your domain\. The master user has full permissions to the cluster and manages roles and role mappings\.
+ If you choose IAM for your master user, all requests to the cluster must be signed using AWS Signature Version 4\. For sample code, see [Signing HTTP requests to Amazon OpenSearch Service](request-signing.md)\.

  We recommend IAM if you want to use the same users on multiple clusters, if you want to use Amazon Cognito to access Dashboards, or if you have OpenSearch clients that support Signature Version 4 signing\.
+ If you choose the internal user database, you can use HTTP basic authentication \(as well as IAM credentials\) to make requests to the cluster\. Most clients support basic authentication, including [curl](https://curl.haxx.se/), which also supports AWS Signature Version 4 with the [\-\-aws\-sigv4 option](https://curl.se/docs/manpage.html)\. The internal user database is stored in an OpenSearch index, so you can't share it with other clusters\.

  We recommend the internal user database if you don't need to reuse users across multiple clusters, if you want to use HTTP basic authentication to access Dashboards \(rather than Amazon Cognito\), or if you have clients that only support basic authentication\. The internal user database is the simplest way to get started with OpenSearch Service\.

## Enabling fine\-grained access control<a name="fgac-enabling"></a>

Enable fine\-grained access control using the console, AWS CLI, or configuration API\. For steps, see [Creating and managing Amazon OpenSearch Service domains](createupdatedomains.md)\. 

Fine\-grained access control requires OpenSearch or Elasticsearch 6\.7 or later\. It also requires HTTPS for all traffic to the domain, [Encryption of data at rest](encryption-at-rest.md), and [node\-to\-node encryption](ntn.md)\. Depending on how you configure the advanced features of fine\-grained access control, additional processing of your requests may require compute and memory resources on individual data nodes\. After you enable fine\-grained access control, you can't disable it\.

### Enabling fine\-grained access control on existing domains<a name="fgac-enabling-existing"></a>

You can enable fine\-grained access control on existing domains running OpenSearch or Elasticsearch 6\.7 or later\. 

**To enable fine\-grained access control on an existing domain \(console\)**

1. Select your domain and choose **Actions** and **Edit security configuration**\.

1. Select **Enable fine\-grained access control**\.

1. Choose how to create the master user:
   + If you want to use IAM for user management, choose **Set IAM ARN as master user** and specify the ARN for an IAM role\.
   + If you want to use the internal user database, choose **Create master user** and specify a username and password\.

1. \(Optional\) Select **Enable migration period for open/IP\-based access policy**\. This setting enables a 30\-day transition period during which your existing users can continue to access the domain without disruptions, and existing open and [IP\-based access policies](ac.md#ac-types-ip) will continue to work with your domain\. During this migration period, we recommend that administrators [create the necessary roles and map them to users](#fgac-access-control) for the domain\. If you use identity\-based policies instead of an open or IP\-based access policy, you can disable this setting\.

   You also need to update your clients to work with fine\-grained access control during the migration period\. For example, if you map IAM roles with fine\-grained access control, you must update your clients to start signing requests with AWS Signature Version 4\. If you configure HTTP basic authentication with fine\-grained access control, you must update your clients to provide appropriate basic authentication credentials in requests\.

   During the migration period, users who access the OpenSearch Dashboards endpoint for the domain will land directly on the **Discover** page rather than the login page\. Administrators and master users can choose **Login** to log in with admin credentials and configure role mappings\. 
**Important**  
**OpenSearch Service automatically disables the migration period after 30 days**\. We recommend ending it as soon as you create the necessary roles and map them to users\. After the migration period ends, you can't re\-enable it\.

1. Choose **Save changes**\.

The change triggers a [blue/green deployment](managedomains-configuration-changes.md#bg) during which the cluster health becomes red, but all cluster operations remain unaffected\.

**To enable fine\-grained access control on an existing domain \(CLI\)**

Set `AnonymousAuthEnabled` to `true` to enable the migration period with fine\-grained access control:

```
aws opensearch update-domain-config --domain-name test-domain --region us-east-1 \
      --advanced-security-options '{ "Enabled": true, "InternalUserDatabaseEnabled":true, "MasterUserOptions": {"MasterUserName":"master-username","MasterUserPassword":"master-password"},"AnonymousAuthEnabled": true}'
```

### About the default\_role<a name="fgac-enabling-defaultrole"></a>

Fine\-grained access control requires [role mapping](#fgac-mapping)\. If your domain uses [identity\-based access policies](ac.md#ac-types-identity), OpenSearch Service automatically maps your users to a new role called **default\_role** in order to help you properly migrate existing users\. This temporary mapping ensures that your users can still successfully send IAM\-signed GET and PUT requests until you create your own role mappings\.

The role does not add any security vulnerabilities or flaws to your OpenSearch Service domain\. We recommend deleting the default role as soon as you set up your own roles and map them accordingly\.

### Migration scenarios<a name="fgac-enabling-scenarios"></a>

The following table describes the behavior for each authentication method before and after enabling fine\-grained access control on an existing domain, and the steps administrators must take to properly map their users to roles:


| Authentication method | Before enabling fine\-grained access control | After enabling fine\-grained access control | Administrator tasks | 
| --- | --- | --- | --- | 
| Identity\-based policies |  All users satisfying the IAM policy can access the domain\.  |  You don't need to enable the migration period\. OpenSearch Service automatically maps all users that satisfy the IAM policy to the **[default\_role](#fgac-enabling-defaultrole)** so that they can continue to access the domain\.  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/fgac.html)  | 
| IP\-based policies |  All users from the allowed IP addresses or CIDR blocks can access the domain\.  |  During the 30\-day migration period, all users from the allowed IP addresses or CIDR blocks can continue to access the domain\.  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/fgac.html)  | 
| Open access policies |  All users over the internet can access the domain\.  |  During the 30\-day migration period, all users over the internet can continue to access to domain\.  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/fgac.html)  | 

## Accessing OpenSearch Dashboards as the master user<a name="fgac-dashboards"></a>

Fine\-grained access control has an OpenSearch Dashboards plugin that simplifies management tasks\. You can use Dashboards to manage users, roles, mappings, action groups, and tenants\. The OpenSearch Dashboards sign\-in page and underlying authentication method differs, however, depending on how you manage users and configured your domain\.
+ If you want to use IAM for user management, use [Configuring Amazon Cognito authentication for OpenSearch Dashboards](cognito-auth.md) to access Dashboards\. Otherwise, Dashboards shows a nonfunctional sign\-in page\. See [Limitations](#fgac-limitations)\.

  With Amazon Cognito authentication, one of the assumed roles from the identity pool must match the IAM role that you specified for the master user\. For more information about this configuration, see [\(Optional\) Configuring granular access](cognito-auth.md#cognito-auth-granular) and [Tutorial: Configure a domain with an IAM master user and Amazon Cognito authentication](fgac-walkthrough-iam.md)\.  
![\[Cognito sign-in page\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/cognito-auth.png)
+ If you choose to use the internal user database, you can sign in to Dashboards with your master username and password\. You must access Dashboards over HTTPS\. Amazon Cognito and SAML authentication for Dashboards both replace this login screen\.

  For more information about this configuration, see [Tutorial: Configure a domain with the internal user database and HTTP basic authentication](fgac-walkthrough-basic.md)\.  
![\[Basic authentication sign-in page\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/basic-auth-dashboards.png)
+ If you choose to use SAML authentication, you can sign in using credentials from an external identity provider\. For more information, see [SAML authentication for OpenSearch Dashboards](saml.md)\.

## Managing permissions<a name="fgac-access-control"></a>

As noted in [Key concepts](#fgac-concepts), you manage fine\-grained access control permissions using roles, users, and mappings\. This section describes how to create and apply those resources\. We recommend that you [sign in to Dashboards as the master user](#fgac-dashboards) to perform these operations\.

![\[Security home page in Dashboards\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/dashboards-fgac-home.png)

**Note**  
The permissions that you choose to grant to your users vary widely based on use case\. We cannot feasibly cover all scenarios in this documentation\. As you're determining which permissions to grant your users, make sure to reference the OpenSearch cluster and index permissions mentioned in the following sections, and always follow the [principle of least privilege](https://en.wikipedia.org/wiki/Principle_of_least_privilege)\.

### Creating roles<a name="fgac-roles"></a>

You can create new roles for fine\-grained access control using OpenSearch Dashboards or the `_plugins/_security` operation in the REST API\. For more information, see [Create roles](https://opensearch.org/docs/security-plugin/access-control/users-roles/#create-roles)\.

Fine\-grained access control also includes a number of [predefined roles](https://opensearch.org/docs/security-plugin/access-control/users-roles/#predefined-roles)\. Clients such as OpenSearch Dashboards and Logstash make a wide variety of requests to OpenSearch, which can make it hard to manually create roles with the minimum set of permissions\. For example, the `opensearch_dashboards_user` role includes the permissions that a user needs to work with index patterns, visualizations, dashboards, and tenants\. We recommend [mapping it](#fgac-mapping) to any user or backend role that accesses Dashboards, along with additional roles that allow access to other indices\.

Amazon OpenSearch Service doesn't offer the following OpenSearch roles:
+ `observability_full_access`
+ `observability_read_access`
+ `reports_read_access`
+ `reports_full_access`

Amazon OpenSearch Service offers several roles that aren't available with OpenSearch:
+ `ultrawarm_manager`
+ `ml_full_access`
+ `cold_manager`
+ `notifications_full_access`
+ `notifications_read_access`

#### Cluster\-level security<a name="fgac-cluster-level"></a>

Cluster\-level permissions include the ability to make broad requests such as `_mget`, `_msearch`, and `_bulk`, monitor health, take snapshots, and more\. Manage these permissions using the **Cluster Permissions** section when creating a role\. For a full list of cluster\-level permissions, see [Cluster permissions](https://opensearch.org/docs/latest/security-plugin/access-control/permissions/#cluster-permissions)\.

Rather than individual permissions, you can often achieve your desired security posture using a combination of the default action groups\. For a list of cluster\-level action groups, see [Cluster\-level](https://opensearch.org/docs/security-plugin/access-control/default-action-groups/#cluster-level)\.

#### Index\-level security<a name="fgac-index-level"></a>

Index\-level permissions include the ability to create new indices, search indices, read and write documents, delete documents, manage aliases, and more\. Manage these permissions using the **Index Permissions** section when creating a role\. For a full list of index\-level permissions, see [Index permissions](https://opensearch.org/docs/latest/security-plugin/access-control/permissions/#index-permissions)\.

Rather than individual permissions, you can often achieve your desired security posture using a combination of the default action groups\. For a list of index\-level action groups, see [Index\-level](https://opensearch.org/docs/security-plugin/access-control/default-action-groups/#index-level)\.

#### Document\-level security<a name="fgac-document-level"></a>

Document\-level security lets you restrict which documents in an index a user can see\. When creating a role, specify an index pattern and an OpenSearch query\. Any users that you map to that role can see only the documents that match the query\. Document\-level security affects [the number of hits that you receive when you search](#fgac-example)\.

For more information, see [Document\-level security](https://opensearch.org/docs/security-plugin/access-control/document-level-security/)\.

#### Field\-level security<a name="fgac-field-level"></a>

Field\-level security lets you control which document fields a user can see\. When creating a role, add a list of fields to either include or exclude\. If you include fields, any users you map to that role can see only those fields\. If you exclude fields, they can see all fields *except* the excluded ones\. Field\-level security affects [the number of fields included in hits when you search](#fgac-example)\.

For more information, see [Field\-level security](https://opensearch.org/docs/security-plugin/access-control/field-level-security/)\.

#### Field masking<a name="fgac-field-masking"></a>

Field masking is an alternative to field\-level security that lets you anonymize the data in a field rather than remove it altogether\. When creating a role, add a list of fields to mask\. Field masking affects [whether you can see the contents of a field when you search](#fgac-example)\.

**Tip**  
If you apply the standard masking to a field, OpenSearch Service uses a secure, random hash that can cause inaccurate aggregation results\. To perform aggregations on masked fields, use pattern\-based masking instead\.

### Creating users<a name="fgac-users"></a>

If you enabled the internal user database, you can create users using OpenSearch Dashboards or the `_plugins/_security` operation in the REST API\. For more information, see [Create users](https://opensearch.org/docs/security-plugin/access-control/users-roles/#create-users)\.

If you chose IAM for your master user, ignore this portion of Dashboards\. Create IAM roles instead\. For more information, see the [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/)\.

### Mapping roles to users<a name="fgac-mapping"></a>

Role mapping is the most critical aspect of fine\-grained access control\. Fine\-grained access control has some predefined roles to help you get started, but unless you map roles to users, every request to the cluster ends in a permissions error\.

*Backend roles* can help simplify the role mapping process\. Rather than mapping the same role to 100 individual users, you can map the role to a single backend role that all 100 users share\. Backend roles can be IAM roles or arbitrary strings\. 
+ Specify users, user ARNs, and Amazon Cognito user strings in the **Users** section\. Cognito user strings take the form of `Cognito/user-pool-id/username`\.
+ Specify backend roles and IAM role ARNs in the **Backend roles** section\.

![\[Role mapping screen\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/role-mapping-edit.png)

You can map roles to users using OpenSearch Dashboards or the `_plugins/_security` operation in the REST API\. For more information, see [Map users to roles](https://opensearch.org/docs/security-plugin/access-control/users-roles/#map-users-to-roles)\.

### Creating action groups<a name="fgac-ag"></a>

Action groups are sets of permissions that you can reuse across different resources\. You can create new action groups using OpenSearch Dashboards or the `_plugins/_security` operation in the REST API, although the default action groups suffice for most use cases\. For more information about the default action groups, see [Default action groups](https://opensearch.org/docs/security-plugin/access-control/default-action-groups/)\.

### OpenSearch Dashboards multi\-tenancy<a name="fgac-multitenancy"></a>

Tenants are spaces for saving index patterns, visualizations, dashboards, and other Dashboards objects\. Dashboards multi\-tenancy lets you safely share your work with other Dashboards users \(or keep it private\)\. You can control which roles have access to a tenant and whether those roles have read or write access\. The Global tenant is the default\. To learn more, see [OpenSearch Dashboards multi\-tenancy](https://opensearch.org/docs/security-plugin/access-control/multi-tenancy/)\.

**To view your current tenant or change tenants**

1. Navigate to OpenSearch Dashboards and sign in\.

1. Select your user icon in the upper\-right and choose **Switch tenants**\.

1. Verify your tenant before creating visualizations or dashboards\. If you want to share your work with all other Dashboards users, choose **Global**\. To share your work with a subset of Dashboards users, choose a different shared tenant\. Otherwise, choose **Private**\.

**Note**  
OpenSearch Dashboards maintains a separate index for each tenant, and creates an index template called `tenant_template`\. Do not delete or modify the `tenant_template` index, as it could cause OpenSearch Dashboards to malfunction if the tenant index mapping is misconfigured\.

## Recommended configurations<a name="fgac-recommendations"></a>

Due to how fine\-grained access control [interacts with other security features](#fgac-access-policies), we recommend several fine\-grained access control configurations that work well for most use cases\.


| Description | Master user | Domain access policy | 
| --- | --- | --- | 
|  Use IAM credentials for calls to the OpenSearch APIs, and use [SAML authentication](saml.md) to access Dashboards\. Manage fine\-grained access control roles using Dashboards or the REST API\.  | IAM role or user |  <pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [<br />    {<br />      "Effect": "Allow",<br />      "Principal": {<br />        "AWS": "*"<br />      },<br />      "Action": "es:ESHttp*",<br />      "Resource": "domain-arn/*"<br />    }<br />  ]<br />}</pre>  | 
|  Use IAM credentials or basic authentication for calls to the OpenSearch APIs\. Manage fine\-grained access control roles using Dashboards or the REST API\. This configuration offers a lot of flexiblity, especially if you have OpenSearch clients that only support basic authentication\. If you have an existing identity provider, use [SAML authentication](saml.md) to access Dashboards\. Otherwise, manage Dashboards users in the internal user database\.  | Username and password |  <pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [<br />    {<br />      "Effect": "Allow",<br />      "Principal": {<br />        "AWS": "*"<br />      },<br />      "Action": "es:ESHttp*",<br />      "Resource": "domain-arn/*"<br />    }<br />  ]<br />}</pre>  | 
|  Use IAM credentials for calls to the OpenSearch APIs, and use Amazon Cognito to access Dashboards\. Manage fine\-grained access control roles using Dashboards or the REST API\.  | IAM role or user |  <pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [<br />    {<br />      "Effect": "Allow",<br />      "Principal": {<br />        "AWS": "*"<br />      },<br />      "Action": "es:ESHttp*",<br />      "Resource": "domain-arn/*"<br />    }<br />  ]<br />}</pre>  | 
|  Use IAM credentials for calls to the OpenSearch APIs, and block most access to Dashboards\. Manage fine\-grained access control roles using the REST API\.  | IAM role or user |  <pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [<br />    {<br />      "Effect": "Allow",<br />      "Principal": {<br />        "AWS": "*"<br />      },<br />      "Action": "es:ESHttp*",<br />      "Resource": "domain-arn/*"<br />    },<br />    {<br />      "Effect": "Deny",<br />      "Principal": {<br />        "AWS": "*"<br />      },<br />      "Action": "es:ESHttp*",<br />      "Resource": "domain-arn/_dashboards*"<br />    }<br />  ]<br />}</pre>  | 

## Limitations<a name="fgac-limitations"></a>

Fine\-grained access control has several important limitations:
+ The `hosts` aspect of role mappings, which maps roles to hostnames or IP addresses, doesn't work if the domain is within a VPC\. You can still map roles to users and backend roles\.
+ If you choose IAM for the master user and don't enable Amazon Cognito or SAML authentication, Dashboards displays a nonfunctional sign\-in page\.
+ If you choose IAM for the master user, you can still create users in the internal user database\. Because HTTP basic authentication is not enabled under this configuration, however, any requests signed with those user credentials are rejected\.
+ If you use [SQL](sql-support.md) to query an index that you don't have access to, you receive a "no permissions" error\. If the index doesn't exist, you receive a "no such index" error\. This difference in error messages means that you can confirm the existence of an index if you happen to guess its name\.

  To minimize the issue, [don't include sensitive information in index names](indexing.md#indexing-naming)\. To deny all access to SQL, add the following element to your domain access policy:

  ```
  {
    "Effect": "Deny",
    "Principal": {
      "AWS": [
        "*"
      ]
    },
    "Action": [
      "es:*"
    ],
    "Resource": "arn:aws:es:us-east-1:123456789012:domain/my-domain/_plugins/_sql"
  }
  ```

## Modifying the master user<a name="fgac-forget"></a>

If you forget the details of the master user, you can reconfigure it using the console, AWS CLI, or configuration API\.

**To modify the master user \(console\)**

1. Navigate to the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home/](https://console.aws.amazon.com/aos/home/)\.

1. Choose your domain and choose **Actions**, **Edit security configuration**\.

1. Choose either **Set IAM ARN as master user** or **Create master user**\.
   + If you previously used an IAM master user, fine\-grained access control re\-maps the `all_access` role to the new IAM ARN that you specify\.
   + If you previously used the internal user database, fine\-grained access control creates a new master user\. You can use the new master user to delete the old one\.
   + Switching from the internal user database to an IAM master user does *not* delete any users from the internal user database\. Instead, it just disables HTTP basic authentication\. Manually delete users from the internal user database, or keep them in case you ever need to reenable HTTP basic authentication\.

1. Choose **Save changes**\.

## Additional master users<a name="fgac-more-masters"></a>

You designate a master user when you create a domain, but if you want, you can use this master user to create additional master users\. You have two options: OpenSearch Dashboards or the REST API\.
+ In Dashboards, choose **Security**, **Roles**, and then map the new master user to the `all_access` and `security_manager` roles\.  
![\[Role mapping page\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/new-master-users.png)
+ To use the REST API, send the following requests:

  ```
  PUT _plugins/_security/api/rolesmapping/all_access
  {
    "backend_roles": [
      "arn:aws:iam::123456789012:role/fourth-master-user"
    ],
    "hosts": [],
    "users": [
      "master-user",
      "second-master-user",
      "arn:aws:iam::123456789012:user/third-master-user"
    ]
  }
  ```

  ```
  PUT _plugins/_security/api/rolesmapping/security_manager
  {
    "backend_roles": [
      "arn:aws:iam::123456789012:role/fourth-master-user"
    ],
    "hosts": [],
    "users": [
      "master-user",
      "second-master-user",
      "arn:aws:iam::123456789012:user/third-master-user"
    ]
  }
  ```

  These requests *replace* the current role mappings, so perform `GET` requests first so that you can include all current roles in the `PUT` requests\. The REST API is especially useful if you can't access Dashboards and want to map an IAM role from Amazon Cognito to the `all_access` role\.

## Manual snapshots<a name="fgac-snapshots"></a>

Fine\-grained access control introduces some additional complications with taking manual snapshots\. To register a snapshot repository—even if you use HTTP basic authentication for all other purposes—you must map the `manage_snapshots` role to an IAM role that has `iam:PassRole` permissions to assume `TheSnapshotRole`, as defined in [Prerequisites](managedomains-snapshots.md#managedomains-snapshot-prerequisites)\.

Then use that IAM role to send a signed request to the domain, as outlined in [Registering a manual snapshot repository](managedomains-snapshots.md#managedomains-snapshot-registerdirectory)\.

## Integrations<a name="fgac-integrations"></a>

If you use [other AWS services](integrations.md) with OpenSearch Service, you must provide the IAM roles for those services with appropriate permissions\. For example, Kinesis Data Firehose delivery streams often use an IAM role called `firehose_delivery_role`\. In Dashboards, [create a role for fine\-grained access control](#fgac-roles), and [map the IAM role to it](#fgac-mapping)\. In this case, the new role needs the following permissions:

```
{
  "cluster_permissions": [
    "cluster_composite_ops",
    "cluster_monitor"
  ],
  "index_permissions": [{
    "index_patterns": [
      "firehose-index*"
    ],
    "allowed_actions": [
      "create_index",
      "manage",
      "crud"
    ]
  }]
}
```

Permissions vary based on the actions each service performs\. An AWS IoT rule or AWS Lambda function that indexes data likely needs similar permissions to Kinesis Data Firehose, while a Lambda function that only performs searches can use a more limited set\.

## REST API differences<a name="fgac-rest-api"></a>

The fine\-grained access control REST API differs slightly depending on your OpenSearch/Elasticsearch version\. Prior to making a `PUT` request, make a `GET` request to verify the expected request body\. For example, a `GET` request to `_plugins/_security/api/user` returns all users, which you can then modify and use to make valid `PUT` requests\.

On Elasticsearch 6\.*x*, requests to create users look like this:

```
PUT _opendistro/_security/api/user/new-user
{
  "password": "some-password",
  "roles": ["new-backend-role"]
}
```

On OpenSearch or Elasticsearch 7\.x, requests look like this \(change `_plugins` to `_opendistro` if using Elasticsearch\):

```
PUT _plugins/_security/api/user/new-user
{
  "password": "some-password",
  "backend_roles": ["new-backend-role"]
}
```

Further, tenants are properties of roles in Elasticsearch 6\.*x*:

```
GET _opendistro/_security/api/roles/all_access

{
  "all_access": {
    "cluster": ["UNLIMITED"],
    "tenants": {
      "admin_tenant": "RW"
    },
    "indices": {
      "*": {
        "*": ["UNLIMITED"]
      }
    },
    "readonly": "true"
  }
}
```

In OpenSearch and Elasticsearch 7\.x, they're objects with their own URI \(change `_plugins` to `_opendistro` if using Elasticsearch\)::

```
GET _plugins/_security/api/tenants

{
  "global_tenant": {
    "reserved": true,
    "hidden": false,
    "description": "Global tenant",
    "static": false
  }
}
```

For documentation on the OpenSearch REST API, see the [Security plugin API reference](https://opensearch.org/docs/security-plugin/access-control/api/)\.

**Tip**  
If you use the internal user database, you can use [curl](https://curl.haxx.se/) to make requests and test your domain\. Try the following sample commands:  

```
curl -XGET -u 'master-user:master-user-password' 'domain-endpoint/_search'
curl -XGET -u 'master-user:master-user-password' 'domain-endpoint/_plugins/_security/api/user'
```

## Step 5: Test the permissions<a name="fgac-walkthrough-test"></a>

Now that your roles are mapped correctly, you can sign in as the limited user and test the permissions\.

1. In a new, private browser window, navigate to the OpenSearch Dashboards URL for the domain, sign in using the `limited-user` credentials, and choose **Explore on my own**\.

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