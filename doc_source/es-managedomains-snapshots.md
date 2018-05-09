# Working with Amazon Elasticsearch Service Index Snapshots<a name="es-managedomains-snapshots"></a>

Snapshots are backups of a cluster's data and state\. They provide a convenient way to migrate data across Amazon ES domains and recover from failure\. The service supports restoring from snapshots taken on both Amazon ES domains and self\-managed Elasticsearch clusters\.

Amazon ES takes daily automated snapshots of the primary index shards in a domain, as described in [Configuring Automatic Snapshots](es-createupdatedomains.md#es-createdomain-configure-snapshots)\. It stores these automated snapshots in a preconfigured Amazon S3 bucket for 14 days at no additional charge to you\. You can use these snapshots to restore the domain\.

You cannot, however, use automated snapshots to migrate to new domains\. Automated snapshots are read\-only from within a given domain\. For migrations, you must use manual snapshots stored in your own repository \(an S3 bucket\)\. Standard S3 charges apply to manual snapshots\.

**Tip**  
Some users find tools like the [Curator](https://www.elastic.co/guide/en/elasticsearch/client/curator/current/index.html) CLI convenient for index and snapshot management\. The Curator CLI offers advanced filtering functionality that can help simplify tasks on complex clusters\.

**Topics**
+ [Manual Snapshot Prerequisites](#es-managedomains-snapshot-prerequisites)
+ [Registering a Manual Snapshot Repository](#es-managedomains-snapshot-registerdirectory)
+ [Taking Manual Snapshots](#es-managedomains-snapshot-create)
+ [Restoring Snapshots](#es-managedomains-snapshot-restore)

## Manual Snapshot Prerequisites<a name="es-managedomains-snapshot-prerequisites"></a>

To create index snapshots manually, you must work with IAM and Amazon S3\. Verify that you have met the following prerequisites before you attempt to take a snapshot\.


****  

| Prerequisite  | Description | 
| --- | --- | 
| S3 bucket | Stores manual snapshots for your Amazon ES domain\. | 
| IAM role | Delegates permissions to Amazon Elasticsearch Service\. The trust relationship for the role must specify Amazon Elasticsearch Service in the Principal statement\. The IAM role also is required to register your snapshot repository with Amazon ES\. Only IAM users with access to this role may register the snapshot repository\. | 
| IAM policy | Specifies the actions that Amazon S3 may perform with your S3 bucket\. The policy must be attached to the IAM role that delegates permissions to Amazon Elasticsearch Service\. The policy must specify an S3 bucket in a Resource statement\. | 

**S3 Bucket**

You need an S3 bucket to store manual snapshots\. Make a note of its Amazon Resource Name \(ARN\), which takes the form of `arn:aws:s3:::bucket-name`\. You need it in two places:
+ `Resource` statement of the IAM policy that is attached to your IAM role
+ Python client that is used to register a snapshot repository

For more information, see [Create a Bucket](http://docs.aws.amazon.com/AmazonS3/latest/gsg/CreatingABucket.html) in the *Amazon S3 Getting Started Guide*\.

**IAM Role**

You must have a role that specifies Amazon Elasticsearch Service, `es.amazonaws.com,` in a `Service` statement in its trust relationship, as shown in the following example:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "",
      "Effect": "Allow",
      "Principal": {
        "Service": "es.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
```

If you create this role using the IAM console, Amazon ES is not included in the **Choose the service that will use this role** list\. However, you can still create the role by choosing **Amazon EC2**, following the steps to create the role, and then editing the role's trust relationships to `es.amazonaws.com` instead of `ec2.amazonaws.com`\. For instructions and additional information, see [Creating a Role for an AWS Service](http://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create_for-service.html#roles-creatingrole-service-console) and [Modifying a Role](http://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_manage_modify.html#roles-managingrole-editing-console) in the *IAM User Guide*\.

**Note**  
Only IAM users or roles with access to this service role may [register snapshot repositories](#es-managedomains-snapshot-registerdirectory)\. A common way to provide access is to attach the following policy to a different user or role:  

```
{
    "Version": "2012-10-17",
    "Statement": {
        "Effect": "Allow",
        "Action": "iam:PassRole",
        "Resource": "arn:aws:iam::123456789012:role/TheServiceRole"
    }
}
```

**IAM Policy**

You must attach an IAM policy to the IAM role\. The policy specifies the S3 bucket that is used to store manual snapshots for your Amazon ES domain\. The following example specifies the ARN of the `es-index-backups` bucket:

```
 {
    "Version":"2012-10-17",
    "Statement":[
        {
            "Action":[
                "s3:ListBucket"
            ],
            "Effect":"Allow",
            "Resource":[
                "arn:aws:s3:::es-index-backups"
            ]
        },
        {
            "Action":[
                "s3:GetObject",
                "s3:PutObject",
                "s3:DeleteObject"
            ],
            "Effect":"Allow",
            "Resource":[
                "arn:aws:s3:::es-index-backups/*"
            ]
        }
    ]
}
```

For more information, see [Creating Customer Managed Policies](http://docs.aws.amazon.com/IAM/latest/UserGuide/policies_using-managed.html#create-managed-policy-console) and [Attaching Managed Policies](http://docs.aws.amazon.com/IAM/latest/UserGuide/policies_using-managed.html#attach-managed-policy-console) in the *IAM User Guide*\.

## Registering a Manual Snapshot Repository<a name="es-managedomains-snapshot-registerdirectory"></a>

You must register a snapshot repository with Amazon Elasticsearch Service before you can take manual index snapshots\. This one\-time operation requires that you sign your AWS request with credentials for one of the users or roles specified in the IAM role's trust relationship, as described in [Manual Snapshot Prerequisites](#es-managedomains-snapshot-prerequisites)\.

You can't use `curl` to perform this operation because it doesn't support AWS request signing\. Instead, use the [sample Python client](#es-managedomains-snapshot-client-python) to register your snapshot directory\.

If your domain resides within a VPC, your computer must be connected to the VPC in order for the Python client to successfully register the snapshot repository\. Accessing a VPC varies by network configuration, but likely involves connecting to a VPN or corporate network\. To check that you can reach the Amazon ES domain, navigate to `https://your-vpc-domain.region.es.amazonaws.com` in a web browser and verify that you receive the default JSON response\.

### Sample Python Client<a name="es-managedomains-snapshot-client-python"></a>

Save the following sample Python code as a Python file, such as `register-repo.py`\. The client requires the [requests](http://docs.python-requests.org/) and [requests\-aws4auth](https://pypi.python.org/pypi/requests-aws4auth) packages\.

**Tip**  
A Java\-based code sample is available in [Indexing Data](es-indexing.md#es-indexing-programmatic-java)\.

Registering a snapshot directory is a one\-time operation, but to migrate from one domain to another, you must register the same snapshot repository on the old domain and the new domain\. The client also contains commented\-out examples for other snapshot operations\.

You must update the following in your code:

`AWS_ACCESS_KEY_ID`  
IAM credential

`AWS_SECRET_ACCESS_KEY`  
IAM credential

`region`  
AWS region where you created the snapshot repository

`host`  
Endpoint for your Amazon ES domain

`path`  
Name of the snapshot repository

`payload`  
Must include the name of the S3 bucket and the ARN for the IAM role that you created in [Manual Snapshot Prerequisites](#es-managedomains-snapshot-prerequisites)\. To enable [server\-side encryption with S3\-managed keys](http://docs.aws.amazon.com/AmazonS3/latest/dev/UsingServerSideEncryption.html) for the snapshot repository, add `"server_side_encryption": true` to the `"settings"` JSON\.

```
import requests
from requests_aws4auth import AWS4Auth

AWS_ACCESS_KEY_ID=''
AWS_SECRET_ACCESS_KEY=''
region = 'us-west-1'
service = 'es'

awsauth = AWS4Auth(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, region, service)

host = 'https://elasticsearch-domain.us-west-1.es.amazonaws.com/' # include https:// and trailing /

# REGISTER REPOSITORY

path = '_snapshot/my-snapshot-repo' # the Elasticsearch API endpoint
url = host + path

payload = {
  "type": "s3",
  "settings": {
    "bucket": "s3-bucket-name",
    "region": "us-west-1",
    "role_arn": "arn:aws:iam::123456789012:role/TheServiceRole"
  }
}

headers = {"Content-Type": "application/json"}

r = requests.put(url, auth=awsauth, json=payload, headers=headers) # requests.get, post, put, and delete all have similar syntax

print(r.text)

# # TAKE SNAPSHOT
# 
# path = '_snapshot/my-snapshot-repo/my-snapshot'
# url = host + path
# 
# r = requests.put(url, auth=awsauth)
# 
# print(r.text)
# 
# # DELETE INDEX
# 
# path = 'my-index'
# url = host + path
# 
# r = requests.delete(url, auth=awsauth)
# 
# print(r.text)
# 
# # RESTORE SNAPSHOT (ALL INDICES)
# 
# path = '_snapshot/my-snapshot-repo/my-snapshot/_restore'
# url = host + path
# 
# r = requests.post(url, auth=awsauth)
# 
# print(r.text)
# 
# # RESTORE SNAPSHOT (ONE INDEX)
# 
# path = '_snapshot/my-snapshot-repo/my-snapshot/_restore'
# url = host + path
# 
# payload = {"indices": "my-index"}
# 
# headers = {"Content-Type": "application/json"}
# 
# r = requests.post(url, auth=awsauth, json=payload, headers=headers)
# 
# print(r.text)
```

**Important**  
If the S3 bucket is in the us\-east\-1 region, you need to use `"endpoint": "s3.amazonaws.com"` instead of `"region": "us-east-1"`\.

## Taking Manual Snapshots<a name="es-managedomains-snapshot-create"></a>

You specify two pieces of information when you create a snapshot:
+ Name of your snapshot repository
+ Name for the snapshot

The examples in this chapter use [curl](https://curl.haxx.se/), a common HTTP client, for convenience and brevity\. If your access policies specify IAM users or roles, however, you must sign your snapshot requests\. You can use the commented\-out examples in the [sample Python client](#es-managedomains-snapshot-client-python) to make signed HTTP requests to the same endpoints that the curl commands use\.

**To manually take a snapshot**
+ Run the following command to manually take a snapshot:

  ```
  curl -XPUT 'elasticsearch-domain-endpoint/_snapshot/repository/snapshot-name'
  ```

**Note**  
The time required to take a snapshot increases with the size of the Amazon ES domain\. Long\-running snapshot operations commonly encounter the following error: `504 GATEWAY_TIMEOUT`\. Typically, you can ignore these errors and wait for the operation to complete successfully\. Use the following command to verify the state of all snapshots of your domain:   

```
curl -XGET 'elasticsearch-domain-endpoint/_snapshot/repository/_all?pretty'
```

For more information about the options available to you when taking a snapshot, see [Snapshot and Restore](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-snapshots.html#_snapshot) in the Elasticsearch documentation\.

## Restoring Snapshots<a name="es-managedomains-snapshot-restore"></a>

**Warning**  
If you use [index aliases](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-aliases.html), cease write requests to an alias \(or switch the alias to another index\) prior to deleting its index\. Halting write requests helps avoid the following scenario:  
You delete an index, which also deletes its alias\.
An errant write request to the now\-deleted alias creates a new index with the same name as the alias\.
You can no longer use the alias due to a naming conflict with the new index\.
If you switched the alias to another index, specify `"include_aliases": false` when you restore from a snapshot\.

**To restore a snapshot**

1. Identify the snapshot that you want to restore\. To see all snapshot repositories, run the following command:

   ```
   curl -XGET 'elasticsearch-domain-endpoint/_snapshot?pretty'
   ```

   After you identify the repository, run the following command to see all snapshots:

   ```
   curl -XGET 'elasticsearch-domain-endpoint/_snapshot/repository/_all?pretty'
   ```
**Note**  
Most automated snapshots are stored in the `cs-automated` repository\. If your domain encrypts data at rest, they are stored in the `cs-automated-enc` repository\. If you don't see the manual snapshot repository that you're looking for, make sure that you [registered it](#es-managedomains-snapshot-registerdirectory) to the domain\.

1. Delete or rename all open indices in the Amazon ES domain\.

   You can't restore a snapshot of your indices to an Elasticsearch cluster that already contains indices with the same names\. Currently, Amazon ES does not support the Elasticsearch `_close` API, so you must use one of the following alternatives:
   + Delete the indices on the same Amazon ES domain, and then restore the snapshot\.
   + Restore the snapshot to a different Amazon ES domain \(only possible with manual snapshots\)\.

   The following example shows how to delete *all* existing indices for a domain:

   ```
   curl -XDELETE 'elasticsearch-domain-endpoint/_all'
   ```

   If you don't plan to restore all indices, though, you might want to delete only one:

   ```
   curl -XDELETE 'elasticsearch-domain-endpoint/index-name'
   ```

1. To restore a snapshot, run the following command:

   ```
   curl -XPOST 'elasticsearch-domain-endpoint/_snapshot/repository/snapshot/_restore'
   ```

   Due to special permissions on the `.kibana` index, attempts to restore all indices might fail, especially if you try to restore from an automated snapshot\. The following example restores just one index, `my-index`, from `2017-snapshot` in the `cs-automated` snapshot repository:

   ```
   curl -XPOST 'elasticsearch-domain-endpoint/_snapshot/cs-automated/2017-snapshot/_restore' -d '{"indices": "my-index"}' -H 'Content-Type: application/json'
   ```

For more information about restoring only certain indices from a snapshot, see [Snapshot and Restore](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-snapshots.html#_restore) in the Elasticsearch documentation\.

**Note**  
If not all primary shards were available for the indices involved, a snapshot might have a `state` of `PARTIAL`\. This value indicates that data from at least one shard was not stored successfully\. You can still restore from a partial snapshot, but you might need to use older snapshots to restore any missing indices\.