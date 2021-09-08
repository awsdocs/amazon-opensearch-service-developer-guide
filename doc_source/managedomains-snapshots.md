# Creating index snapshots in Amazon OpenSearch Service<a name="managedomains-snapshots"></a>

Snapshots in Amazon OpenSearch Service are backups of a cluster's indices and state\. *State* includes cluster settings, node information, index settings, and shard allocation\.

OpenSearch Service snapshots come in the following forms:
+ **Automated snapshots** are only for cluster recovery\. You can use them to restore your domain in the event of red cluster status or data loss\. For more information, see [Restoring snapshots](#managedomains-snapshot-restore) below\. OpenSearch Service stores automated snapshots in a preconfigured Amazon S3 bucket at no additional charge\.
+ **Manual snapshots** are for cluster recovery *or* for moving data from one cluster to another\. You have to initiate manual snapshots\. These snapshots are stored in your own Amazon S3 bucket and standard S3 charges apply\. If you have a snapshot from a self\-managed OpenSearch cluster, you can use that snapshot to migrate to an OpenSearch Service domain\. For more information, see [Migrating to Amazon OpenSearch Service](migration.md)\.

All OpenSearch Service domains take automated snapshots, but the frequency differs in the following ways:
+ For domains running OpenSearch or Elasticsearch 5\.3 and later, OpenSearch Service takes hourly automated snapshots and retains up to 336 of them for 14 days\.
+ For domains running Elasticsearch 5\.1 and earlier, OpenSearch Service takes daily automated snapshots during the hour you specify, retains up to 14 of them, and doesn't retain any snapshot data for more than 30 days\.

If your cluster enters red status, all automated snapshots fail while the cluster status persists\. If you don't correct the problem within two weeks, you can permanently lose the data in your cluster\. For troubleshooting steps, see [Red cluster status](handling-errors.md#handling-errors-red-cluster-status)\.

**Topics**
+ [Prerequisites](#managedomains-snapshot-prerequisites)
+ [Registering a manual snapshot repository](#managedomains-snapshot-registerdirectory)
+ [Taking manual snapshots](#managedomains-snapshot-create)
+ [Restoring snapshots](#managedomains-snapshot-restore)
+ [Deleting manual snapshots](#managedomains-snapshot-delete)
+ [Automating snapshots with Index State Management](#managedomains-snapshot-ism)
+ [Using Curator for snapshots](#managedomains-snapshot-curator)

## Prerequisites<a name="managedomains-snapshot-prerequisites"></a>

To create snapshots manually, you need to work with IAM and Amazon S3\. Make sure you meet the following prerequisites before you attempt to take a snapshot:


****  

| Prerequisite  | Description | 
| --- | --- | 
| S3 bucket | Create an S3 bucket to store manual snapshots for your OpenSearch Service domain\. For instructions, see [Create a Bucket](http://docs.aws.amazon.com/AmazonS3/latest/gsg/CreatingABucket.html) in the *Amazon Simple Storage Service Getting Started Guide*\. Remember the name of the bucket to use it in the following places:[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/managedomains-snapshots.html) Do not apply an S3 Glacier lifecycle rule to this bucket\. Manual snapshots don't support the S3 Glacier storage class\. | 
| IAM role | Create an IAM role to delegate permissions to OpenSearch Service\. For instructions, see [Creating an IAM role \(console\)](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create_for-user.html#roles-creatingrole-user-console) in the *IAM User Guide*\. The rest of this chapter refers to this role as `TheSnapshotRole`\. **Attach an IAM policy** Attach the following policy to `TheSnapshotRole` to allow access to the S3 bucket: <pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [{<br />      "Action": [<br />        "s3:ListBucket"<br />      ],<br />      "Effect": "Allow",<br />      "Resource": [<br />        "arn:aws:s3:::s3-bucket-name"<br />      ]<br />    },<br />    {<br />      "Action": [<br />        "s3:GetObject",<br />        "s3:PutObject",<br />        "s3:DeleteObject"<br />      ],<br />      "Effect": "Allow",<br />      "Resource": [<br />        "arn:aws:s3:::s3-bucket-name/*"<br />      ]<br />    }<br />  ]<br />}</pre> For instructions to attach a policy to a role, see [Adding IAM Identity Permissions](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_manage-attach-detach.html#add-policies-console) in the *IAM User Guide*\. **Edit the trust relationship** Edit the trust relationship of `TheSnapshotRole` to specify OpenSearch Service in the `Principal` statement as shown in the following example: <pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [{<br />    "Sid": "",<br />    "Effect": "Allow",<br />    "Principal": {<br />      "Service": "es.amazonaws.com"<br />    },<br />    "Action": "sts:AssumeRole"<br />  }]<br />  <br />}</pre> For instructions to edit the trust relationship, see [Modifying a role trust policy](https://docs.aws.amazon.com/IAM/latest/UserGuide/roles-managingrole-editing-console.html#roles-managingrole_edit-trust-policy) in the *IAM User Guide*\. | 
| Permissions |  In order to register the snapshot repository, you need to be able to pass `TheSnapshotRole` to OpenSearch Service\. You also need access to the `es:ESHttpPut` action\. To grant both of these permissions, attach the following policy to the IAM user or role whose credentials are being used to sign the request: <pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [<br />    {<br />      "Effect": "Allow",<br />      "Action": "iam:PassRole",<br />      "Resource": "arn:aws:iam::123456789012:role/TheSnapshotRole"<br />    },<br />    {<br />      "Effect": "Allow",<br />      "Action": "es:ESHttpPut",<br />      "Resource": "arn:aws:es:region:123456789012:domain/domain-name/*"<br />    }<br />  ]<br />}</pre> If your user or role doesn't have `iam:PassRole` permissions to pass `TheSnapshotRole` you might encounter the following common error when you try to register a repository in the next step: <pre>$ python register-repo.py<br />{"Message":"User: arn:aws:iam::123456789012:user/MyUserAccount<br />is not authorized to perform: iam:PassRole on resource:<br />arn:aws:iam::123456789012:role/TheSnapshotRole"}</pre>  | 

## Registering a manual snapshot repository<a name="managedomains-snapshot-registerdirectory"></a>

You need to register a snapshot repository with OpenSearch Service before you can take manual index snapshots\. This one\-time operation requires that you sign your AWS request with credentials that are allowed to access `TheSnapshotRole`, as described in [Prerequisites](#managedomains-snapshot-prerequisites)\.

### Step 1: Map the snapshot role in OpenSearch Dashboards \(if using fine\-grained access control\)<a name="managedomains-snapshot-fgac"></a>

Fine\-grained access control introduces an additional step when registering a repository\. Even if you use HTTP basic authentication for all other purposes, you need to map the `manage_snapshots` role to your IAM user or role that has `iam:PassRole` permissions to pass `TheSnapshotRole`\.

1. Navigate to the OpenSearch Dashboards plugin for your OpenSearch Service domain\. You can find the Dashboards endpoint on your domain dashboard on the OpenSearch Service console\. 

1. From the main menu choose **Security**, **Roles**, and select the **manage\_snapshots** role\.

1. Choose **Mapped users**, **Manage mapping**\. 

1. Add the domain ARN of the user or role that has permissions to pass `TheSnapshotRole`\. Put user ARNs under **Users** and role ARNs under **Backend roles**\.

   ```
   arn:aws:iam::123456789123:user/user-name
   ```

   ```
   arn:aws:iam::123456789123:role/role-name
   ```

1. Select **Map** and confirm the user or role shows up under **Mapped users**\.

### Step 2: Register a repository<a name="managedomains-snapshot-register"></a>

To register a snapshot repository, send a PUT request to the OpenSearch Service domain endpoint\. You can't use `curl` to perform this operation because it doesn't support AWS request signing\. Instead, use the [sample Python client](#managedomains-snapshot-client-python), [Postman](https://www.getpostman.com/), or some other method to send a [signed request](request-signing.md) to register the snapshot repository\. 

The request takes the following format:

```
PUT domain-endpoint/_snapshot/my-snapshot-repo-name
{
  "type": "s3",
  "settings": {
    "bucket": "s3-bucket-name",
    "region": "region",
    "role_arn": "arn:aws:iam::123456789012:role/TheSnapshotRole"
  }
}
```

If your domain resides within a virtual private cloud \(VPC\), your computer must be connected to the VPC for the request to successfully register the snapshot repository\. Accessing a VPC varies by network configuration, but likely involves connecting to a VPN or corporate network\. To check that you can reach the OpenSearch Service domain, navigate to `https://your-vpc-domain.region.es.amazonaws.com` in a web browser and verify that you receive the default JSON response\.

#### Encrypting snapshot repositories<a name="managedomains-snapshot-encryption"></a>

You currently can't use AWS Key Management Service \(KMS\) keys to encrypt manual snapshots, but you can protect them using server\-side encryption \(SSE\)\.

To enable SSE with S3\-managed keys for the bucket you use as a snapshot repository, add `"server_side_encryption": true` to the `"settings"` block of the PUT request\. For more information, see [Protecting data using server\-side encryption with Amazon S3\-managed encryption keys](https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingServerSideEncryption.html) in the *Amazon Simple Storage Service User Guide*\.

Alternatively, you can use AWS KMS keys for server\-side encryption on the S3 bucket you use as a snapshot repository\.

#### Migrating data to a different domain<a name="managedomains-snapshot-migrating"></a>

Registering a snapshot repository is a one\-time operation\. However, to migrate from one domain to another, you have to register the same snapshot repository on the old domain and the new domain\. The repository name is arbitrary\.

Consider the following guidelines when migrating to a new domain or registering the same repository with multiple domains for another reason:
+ When registering the repository on the new domain, add `"readonly": true` to the `"settings"` block of the PUT request\. This setting prevents you from accidentally overwriting data from the old domain\.
+ If you're migrating data to a domain in a different region, \(for example, from an old domain and bucket located in us\-east\-2 to a new domain in us\-west\-2\), you might see this 500 error when sending the PUT request:

  ```
  The bucket is in this region: us-east-2. Please use this region to retry the request. 
  ```

  If you encounter this error, try replacing `"region": "us-east-2"` with `"endpoint": "s3.amazonaws.com"` in the PUT statement and retry the request\.

#### Using the sample Python client<a name="managedomains-snapshot-client-python"></a>

The Python client is easier to automate than a simple HTTP request and has better reusability\. If you choose to use this method to register a snapshot repository, save the following sample Python code as a Python file, such as `register-repo.py`\. The client requires the [AWS SDK for Python \(Boto3\)](https://aws.amazon.com/sdk-for-python/), [requests](http://docs.python-requests.org/) and [requests\-aws4auth](https://pypi.python.org/pypi/requests-aws4auth) packages\. The client contains commented\-out examples for other snapshot operations\.

**Tip**  
A Java\-based code sample is available in [Signing HTTP Requests](request-signing.md#request-signing-java)\.

Update the following variables in the sample code: `host`, `region`, `path`, and `payload`\.

```
import boto3
import requests
from requests_aws4auth import AWS4Auth

host = '' # include https:// and trailing /
region = '' # e.g. us-west-1
service = 'opensearchservice'
credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

# Register repository

path = '_snapshot/my-snapshot-repo-name' # the OpenSearch API endpoint
url = host + path

payload = {
  "type": "s3",
  "settings": {
    "bucket": "s3-bucket-name",
    "region": "us-west-1",
    "role_arn": "arn:aws:iam::123456789012:role/TheSnapshotRole"
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
# # Restore snapshot (all indices except Dashboards and fine-grained access control)
#
# path = '_snapshot/my-snapshot-repo/my-snapshot/_restore'
# url = host + path
#
# payload = {
#   "indices": "-.kibana*,-.opendistro_security",
#   "include_global_state": False
# }
#
# headers = {"Content-Type": "application/json"}
#
# r = requests.post(url, auth=awsauth, json=payload, headers=headers)
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

## Taking manual snapshots<a name="managedomains-snapshot-create"></a>

Snapshots are not instantaneous\. They take time to complete and don't represent perfect point\-in\-time views of the cluster\. While a snapshot is in progress, you can still index documents and make other requests to the cluster, but new documents and updates to existing documents generally aren't included in the snapshot\. The snapshot includes primary shards as they existed when OpenSearch initiated the snapshot\. Depending on the size of your snapshot thread pool, different shards might be included in the snapshot at slightly different times\.

### Snapshot storage and performance<a name="managedomains-snapshot-storage"></a>

OpenSearch snapshots are incremental, meaning they only store data that changed since the last successful snapshot\. This incremental nature means the difference in disk usage between frequent and infrequent snapshots is often minimal\. In other words, taking hourly snapshots for a week \(for a total of 168 snapshots\) might not use much more disk space than taking a single snapshot at the end of the week\. Also, the more frequently you take snapshots, the less time they take to complete\. Some OpenSearch users take snapshots as often as every half hour\.

### Create a snapshot<a name="managedomains-snapshot-take"></a>

You specify the following information when you create a snapshot:
+ The name of your snapshot repository
+ A name for the snapshot

The examples in this chapter use [curl](https://curl.haxx.se/), a common HTTP client, for convenience and brevity\. However, if your access policies specify IAM users or roles, you must sign your snapshot requests\. You can use the commented\-out examples in the [sample Python client](#managedomains-snapshot-client-python) to make signed HTTP requests to the same endpoints that the curl commands use\.

To take a manual snapshot, perform the following steps:

1. You can't take a snapshot if one is currently in progress\. To check, run the following command:

   ```
   curl -XGET 'domain-endpoint/_snapshot/_status'
   ```

1. Run the following command to take a manual snapshot:

   ```
   curl -XPUT 'domain-endpoint/_snapshot/repository-name/snapshot-name'
   ```

**Note**  
The time required to take a snapshot increases with the size of the OpenSearch Service domain\. Long\-running snapshot operations sometimes encounter the following error: `504 GATEWAY_TIMEOUT`\. You can typically ignore these errors and wait for the operation to complete successfully\. Run the following command to verify the state of all snapshots of your domain:  

```
curl -XGET 'domain-endpoint/_snapshot/repository-name/_all?pretty'
```

## Restoring snapshots<a name="managedomains-snapshot-restore"></a>

**Warning**  
If you use index aliases, cease write requests to an alias, or switch the alias to another index, prior to deleting its index\. Halting write requests helps avoid the following scenario:  
You delete an index, which also deletes its alias\.
An errant write request to the now\-deleted alias creates a new index with the same name as the alias\.
You can no longer use the alias due to a naming conflict with the new index\.
If you switched the alias to another index, specify `"include_aliases": false` when you restore from a snapshot\.

To restore a snapshot, perform the following steps:

1. Identify the snapshot you want to restore\. To see all snapshot repositories, run the following command:

   ```
   curl -XGET 'domain-endpoint/_snapshot?pretty'
   ```

   After you identify the repository, run the following command to see all snapshots:

   ```
   curl -XGET 'domain-endpoint/_snapshot/repository-name/_all?pretty'
   ```
**Note**  
Most automated snapshots are stored in the `cs-automated` repository\. If your domain encrypts data at rest, they're stored in the `cs-automated-enc` repository\. If you don't see the manual snapshot repository you're looking for, make sure you [registered it](#managedomains-snapshot-registerdirectory) to the domain\.

1. \(Optional\) Delete or rename one or more indices in the OpenSearch Service domain if you have naming conflicts between indices on the cluster and indices in the snapshot\. You can't restore a snapshot of your indices to an OpenSearch cluster that already contains indices with the same names\.

   You have the following options if you have index naming conflicts:
   + Delete the indices on the existing OpenSearch Service domain and then restore the snapshot\.
   + [Rename the indices as you restore them from the snapshot](handling-errors.md#troubleshooting-close-api) and reindex them later\.
   + Restore the snapshot to a different OpenSearch Service domain \(only possible with manual snapshots\)\.

   The following command deletes all existing indices in a domain:

   ```
   curl -XDELETE 'domain-endpoint/_all'
   ```

   However, if you don't plan to restore all indices, you can just delete one:

   ```
   curl -XDELETE 'domain-endpoint/index-name'
   ```

1. To restore a snapshot, run the following command:

   ```
   curl -XPOST 'domain-endpoint/_snapshot/repository-name/snapshot-name/_restore'
   ```

   Due to special permissions on the OpenSearch Dashboards and fine\-grained access control indices, attempts to restore all indices might fail, especially if you try to restore from an automated snapshot\. The following example restores just one index, `my-index`, from `2020-snapshot` in the `cs-automated` snapshot repository:

   ```
   curl -XPOST 'domain-endpoint/_snapshot/cs-automated/2020-snapshot/_restore' -d '{"indices": "my-index"}' -H 'Content-Type: application/json'
   ```

   Alternately, you might want to restore all indices *except* the Dashboards and fine\-grained access control indices:

   ```
   curl -XPOST 'domain-endpoint/_snapshot/cs-automated/2020-snapshot/_restore' -d '{"indices": "-.kibana*,-.opendistro*"}' -H 'Content-Type: application/json'
   ```

**Note**  
If not all primary shards were available for the indices involved, a snapshot might have a `state` of `PARTIAL`\. This value indicates that data from at least one shard wasn't stored successfully\. You can still restore from a partial snapshot, but you might need to use older snapshots to restore any missing indices\.

## Deleting manual snapshots<a name="managedomains-snapshot-delete"></a>

To delete a manual snapshot, run the following command:

```
DELETE _snapshot/repository-name/snapshot-name
```

## Automating snapshots with Index State Management<a name="managedomains-snapshot-ism"></a>

You can use the Index State Management \(ISM\) `[snapshot](https://opendistro.github.io/for-elasticsearch-docs/docs/im/ism/policies/#snapshot)` operation to automatically trigger snapshots of indices based on changes in their age, size, or number of documents\. For an example ISM policy using the `snapshot` operation, see [Sample Policies](ism.md#ism-example)\.

## Using Curator for snapshots<a name="managedomains-snapshot-curator"></a>

If ISM doesn't work for index and snapshot management, you can use Curator instead\. Use [pip](https://pip.pypa.io/en/stable/installing/) to install Curator:

```
pip install elasticsearch-curator
```

Curator offers advanced filtering functionality that can help simplify management tasks on complex clusters\. OpenSearch Service supports Curator on domains running OpenSearch or Elasticsearch 5\.1 and later\. You can use Curator as a command line interface \(CLI\) or Python API\. If you use the CLI, export your credentials at the command line and configure `curator.yml` as follows:

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

For sample Lambda functions that use the Python API, see [Using Curator to rotate data in Amazon OpenSearch Service](curator.md)\.