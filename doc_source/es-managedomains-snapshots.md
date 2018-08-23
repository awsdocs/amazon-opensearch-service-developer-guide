# Working with Amazon Elasticsearch Service Index Snapshots<a name="es-managedomains-snapshots"></a>

Snapshots are backups of a cluster's data and state\. State includes cluster settings, node information, index settings, and shard allocation\.

Snapshots provide a convenient way to migrate data across Amazon Elasticsearch Service domains and recover from failure\. The service supports restoring from snapshots taken on both Amazon ES domains and self\-managed Elasticsearch clusters\.

Amazon ES takes daily automated snapshots of the primary index shards in a domain, as described in [Configuring Automatic Snapshots](es-createupdatedomains.md#es-createdomain-configure-snapshots)\. The service stores up to 14 of these snapshots for no more than 30 days in a preconfigured Amazon S3 bucket at no additional charge to you\. You can use these snapshots to restore the domain\.

If the cluster enters red status and you don't correct the problem, you start to lose automated snapshots after 16 days\. For troubleshooting steps, see [Red Cluster Status](aes-handling-errors.md#aes-handling-errors-red-cluster-status)\.

You cannot use automated snapshots to migrate to new domains\. Automated snapshots are read\-only from within a given domain\. For migrations, you must use manual snapshots stored in your own repository \(an S3 bucket\)\. Standard S3 charges apply to manual snapshots\.

**Tip**  
Many users find tools like Curator convenient for index and snapshot management\. Use [pip](https://pip.pypa.io/en/stable/installing/) to install Curator:  

```
pip install elasticsearch-curator
```
Curator offers advanced filtering functionality that can help simplify management tasks on complex clusters\. Amazon ES supports Curator on domains running Elasticsearch version 5\.1 and above\. You can use Curator as a command line interface \(CLI\) or Python API\. If you use the CLI, export your credentials at the command line and configure `curator.yml` as follows:  

```
client:
  hosts: search-my-domain.us-west-1.es.amazonaws.com
  port: 443
  use_ssl: True
  aws_region: us-west-1
  aws_sign_request: True
  ssl_no_validate: False
  timeout: 60
 
logging:
  loglevel: INFO
```
For a sample Lambda function that uses the Python API, see [Using Curator to Rotate Data in Amazon Elasticsearch Service](curator.md)\.

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
| S3 bucket | Stores manual snapshots for your Amazon ES domain\. Make a note of the bucket's Amazon Resource Name \(ARN\), which takes the form of `arn:aws:s3:::s3-bucket-name`\. You need it in two places:[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-managedomains-snapshots.html)For more information, see [Create a Bucket](http://docs.aws.amazon.com/AmazonS3/latest/gsg/CreatingABucket.html) in the *Amazon Simple Storage Service Getting Started Guide*\. Do **not** apply an Amazon Glacier lifecycle rule to this bucket\. Manual snapshots do not support the Amazon Glacier storage class\. | 
| IAM role | Delegates permissions to Amazon Elasticsearch Service\. The rest of this document refers to this role as `TheServiceRole`\. The trust relationship for the role must specify Amazon Elasticsearch Service in the `Principal` statement, as shown in the following example:<pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [{<br />    "Sid": "",<br />    "Effect": "Allow",<br />    "Principal": {<br />      "Service": "es.amazonaws.com"<br />    },<br />    "Action": "sts:AssumeRole"<br />  }]<br />}</pre>The role must have the following policy attached to it: <pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [{<br />      "Action": [<br />        "s3:ListBucket"<br />      ],<br />      "Effect": "Allow",<br />      "Resource": [<br />        "arn:aws:s3:::s3-bucket-name"<br />      ]<br />    },<br />    {<br />      "Action": [<br />        "s3:GetObject",<br />        "s3:PutObject",<br />        "s3:DeleteObject"<br />      ],<br />      "Effect": "Allow",<br />      "Resource": [<br />        "arn:aws:s3:::s3-bucket-name/*"<br />      ]<br />    }<br />  ]<br />}</pre> For more information, see [Creating Customer Managed Policies](http://docs.aws.amazon.com/IAM/latest/UserGuide/policies_using-managed.html#create-managed-policy-console) and [Attaching Managed Policies](http://docs.aws.amazon.com/IAM/latest/UserGuide/policies_using-managed.html#attach-managed-policy-console) in the *IAM User Guide*\. | 
| Permissions |  You must be able to assume the IAM role in order to register the snapshot repository\. You also need access to the `es:ESHttpPut` action\. A common way to provide access is to attach the following policy to your account: <pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [<br />    {<br />      "Effect": "Allow",<br />      "Action": "iam:PassRole",<br />      "Resource": "arn:aws:iam::123456789012:role/TheServiceRole"<br />    },<br />    {<br />      "Effect": "Allow",<br />      "Action": "es:ESHttpPut",<br />      "Resource": "arn:aws:es:region:123456789012:domain/my-domain/*"<br />    }<br />  ]<br />}</pre> If your account does not have `iam:PassRole` permissions to assume `TheServiceRole`, you might encounter encounter the following common error: <pre>$ python register-repo.py<br />{"Message":"User: arn:aws:iam::123456789012:user/MyUserAccount<br />is not authorized to perform: iam:PassRole on resource:<br />arn:aws:iam::123456789012:role/TheServiceRole"}</pre>  | 

## Registering a Manual Snapshot Repository<a name="es-managedomains-snapshot-registerdirectory"></a>

You must register a snapshot repository with Amazon Elasticsearch Service before you can take manual index snapshots\. This one\-time operation requires that you sign your AWS request with credentials that are allowed to access `TheServiceRole`, as described in [Manual Snapshot Prerequisites](#es-managedomains-snapshot-prerequisites)\.

You can't use `curl` to perform this operation, because it doesn't support AWS request signing\. Instead, use the [sample Python client](#es-managedomains-snapshot-client-python), [Postman](https://www.getpostman.com/), or some other method to send a signed request to register the snapshot repository\. The request takes the following form:

```
PUT https://elasticsearch-domain.region.es.amazonaws.com/_snapshot/my-snapshot-repo
{
  "type": "s3",
  "settings": {
    "bucket": "s3-bucket-name",
    "region": "region",
    "role_arn": "arn:aws:iam::123456789012:role/TheServiceRole"
  }
}
```

Registering a snapshot directory is a one\-time operation, but to migrate from one domain to another, you must register the same snapshot repository on the old domain and the new domain\. To enable [server\-side encryption with S3\-managed keys](http://docs.aws.amazon.com/AmazonS3/latest/dev/UsingServerSideEncryption.html) for the snapshot repository, add `"server_side_encryption": true` to the `"settings"` JSON\.

**Important**  
If the S3 bucket is in the us\-east\-1 region, you need to use `"endpoint": "s3.amazonaws.com"` instead of `"region": "us-east-1"`\.

If your domain resides within a VPC, your computer must be connected to the VPC in order for the request to successfully register the snapshot repository\. Accessing a VPC varies by network configuration, but likely involves connecting to a VPN or corporate network\. To check that you can reach the Amazon ES domain, navigate to `https://your-vpc-domain.region.es.amazonaws.com` in a web browser and verify that you receive the default JSON response\.

### Sample Python Client<a name="es-managedomains-snapshot-client-python"></a>

Save the following sample Python code as a Python file, such as `register-repo.py`\. The client requires the [AWS SDK for Python \(Boto 3\)](https://aws.amazon.com/sdk-for-python/), [requests](http://docs.python-requests.org/) and [requests\-aws4auth](https://pypi.python.org/pypi/requests-aws4auth) packages\. The client contains commented\-out examples for other snapshot operations\.

**Tip**  
A Java\-based code sample is available in [Programmatic Indexing](es-indexing-programmatic.md#es-indexing-programmatic-java)\.

You must update the following variables in your code:

`region`  
AWS region where you created the snapshot repository

`host`  
Endpoint for your Amazon ES domain

`path`  
Name of the snapshot repository

`payload`  
Must include the name of the S3 bucket, region, and the ARN for the IAM role that you created in [Manual Snapshot Prerequisites](#es-managedomains-snapshot-prerequisites)\.

```
import boto3
import requests
from requests_aws4auth import AWS4Auth

host = '' # include https:// and trailing /
region = '' # e.g. us-west-1
service = 'es'
credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

# Register repository

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

r = requests.put(url, auth=awsauth, json=payload, headers=headers)

print(r.status_code)
print(r.text)

# # Take snapshot
#
# path = '_snapshot/my-snapshot-repo/my-snapshot'
# url = host + path
#
# r = requests.put(url, auth=awsauth)
#
# print(r.text)
#
# # Delete index
#
# path = 'my-index'
# url = host + path
#
# r = requests.delete(url, auth=awsauth)
#
# print(r.text)
#
# # Restore snapshots (all indices)
#
# path = '_snapshot/my-snapshot-repo/my-snapshot/_restore'
# url = host + path
#
# r = requests.post(url, auth=awsauth)
#
# print(r.text)
#
# # Restore snapshot (one index)
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

## Taking Manual Snapshots<a name="es-managedomains-snapshot-create"></a>

Snapshots are not instantaneous; they take some time to complete\. While a snapshot is in\-progress, you can still index documents and make other requests to the cluster, but new documents \(and updates to existing documents\) aren't included in the snapshot\. The snapshot includes indices as they existed at the moment you initiated the snapshot\.

Elasticsearch snapshots are incremental, meaning that they only store data that has changed since the last successful snapshot\. This incremental nature means that the difference in disk usage between frequent and infreqent snapshots is often minimal\. In other words, taking hourly snapshots for a week \(for a total of 168 snapshots\) might not use much more disk space than taking a single snapshot at the end of the week\. Also, the more frequently you take snapshots, the less time they take to complete\. Some Elasticsearch users take snapshots as often as every half hour\.

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

## Restoring Snapshots<a name="es-managedomains-snapshot-restore"></a>

**Warning**  
If you use index aliases, cease write requests to an alias \(or switch the alias to another index\) prior to deleting its index\. Halting write requests helps avoid the following scenario:  
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
   + [Rename the indices as you restore them from the snapshot](aes-handling-errors.md#aes-troubleshooting-close-api), and later, reindex them\.
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

**Note**  
If not all primary shards were available for the indices involved, a snapshot might have a `state` of `PARTIAL`\. This value indicates that data from at least one shard was not stored successfully\. You can still restore from a partial snapshot, but you might need to use older snapshots to restore any missing indices\.