# Creating index snapshots in Amazon OpenSearch Service<a name="managedomains-snapshots"></a>

Snapshots in Amazon OpenSearch Service are backups of a cluster's indexes and state\. *State* includes cluster settings, node information, index settings, and shard allocation\.

OpenSearch Service snapshots come in the following forms:
+ **Automated snapshots** are only for cluster recovery\. You can use them to restore your domain in the event of red cluster status or data loss\. For more information, see [Restoring snapshots](#managedomains-snapshot-restore) below\. OpenSearch Service stores automated snapshots in a preconfigured Amazon S3 bucket at no additional charge\.
+ **Manual snapshots** are for cluster recovery *or* for moving data from one cluster to another\. You have to initiate manual snapshots\. These snapshots are stored in your own Amazon S3 bucket and standard S3 charges apply\. If you have a snapshot from a self\-managed OpenSearch cluster, you can use that snapshot to migrate to an OpenSearch Service domain\. For more information, see [Migrating to Amazon OpenSearch Service](migration.md)\.

All OpenSearch Service domains take automated snapshots, but the frequency differs in the following ways:
+ For domains running OpenSearch or Elasticsearch 5\.3 and later, OpenSearch Service takes hourly automated snapshots and retains up to 336 of them for 14 days\. Hourly snapshots are less disruptive because of their incremental nature\. They also provide a more recent recovery point in case of domain problems\.
+ For domains running Elasticsearch 5\.1 and earlier, OpenSearch Service takes daily automated snapshots during the hour you specify, retains up to 14 of them, and doesn't retain any snapshot data for more than 30 days\.

If your cluster enters red status, all automated snapshots fail while the cluster status persists\. If you don't correct the problem within two weeks, you can permanently lose the data in your cluster\. For troubleshooting steps, see [Red cluster status](handling-errors.md#handling-errors-red-cluster-status)\.

**Topics**
+ [Prerequisites](#managedomains-snapshot-prerequisites)
+ [Registering a manual snapshot repository](#managedomains-snapshot-registerdirectory)
+ [Taking manual snapshots](#managedomains-snapshot-create)
+ [Restoring snapshots](#managedomains-snapshot-restore)
+ [Deleting manual snapshots](#managedomains-snapshot-delete)
+ [Automating snapshots with Snapshot Management](#managedomains-snapshot-mgmt)
+ [Automating snapshots with Index State Management](#managedomains-snapshot-ism)
+ [Using Curator for snapshots](#managedomains-snapshot-curator)

## Prerequisites<a name="managedomains-snapshot-prerequisites"></a>

To create snapshots manually, you need to work with IAM and Amazon S3\. Make sure you meet the following prerequisites before you attempt to take a snapshot:


****  

| Prerequisite  | Description | 
| --- | --- | 
| S3 bucket | Create an S3 bucket to store manual snapshots for your OpenSearch Service domain\. For instructions, see [Create a Bucket](http://docs.aws.amazon.com/AmazonS3/latest/gsg/CreatingABucket.html) in the *Amazon Simple Storage Service User Guide*\. Remember the name of the bucket to use it in the following places:[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/managedomains-snapshots.html)  Do not apply an S3 Glacier lifecycle rule to this bucket\. Manual snapshots don't support the S3 Glacier storage class\.  | 
| IAM role | Create an IAM role to delegate permissions to OpenSearch Service\. For instructions, see [Creating an IAM role \(console\)](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create_for-user.html#roles-creatingrole-user-console) in the *IAM User Guide*\. The rest of this chapter refers to this role as `TheSnapshotRole`\. **Attach an IAM policy** Attach the following policy to `TheSnapshotRole` to allow access to the S3 bucket: <pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [{<br />      "Action": [<br />        "s3:ListBucket"<br />      ],<br />      "Effect": "Allow",<br />      "Resource": [<br />        "arn:aws:s3:::s3-bucket-name"<br />      ]<br />    },<br />    {<br />      "Action": [<br />        "s3:GetObject",<br />        "s3:PutObject",<br />        "s3:DeleteObject"<br />      ],<br />      "Effect": "Allow",<br />      "Resource": [<br />        "arn:aws:s3:::s3-bucket-name/*"<br />      ]<br />    }<br />  ]<br />}</pre> For instructions to attach a policy to a role, see [Adding IAM Identity Permissions](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_manage-attach-detach.html#add-policies-console) in the *IAM User Guide*\. **Edit the trust relationship** Edit the trust relationship of `TheSnapshotRole` to specify OpenSearch Service in the `Principal` statement as shown in the following example: <pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [{<br />    "Sid": "",<br />    "Effect": "Allow",<br />    "Principal": {<br />      "Service": "es.amazonaws.com"<br />    },<br />    "Action": "sts:AssumeRole"<br />  }]<br />  <br />}</pre> We recommend that you use the `aws:SourceAccount` and `aws:SourceArn` condition keys to protect yourself against the [confused deputy problem](https://docs.aws.amazon.com/IAM/latest/UserGuide/confused-deputy.html)\. The source account is the owner of the domain and the source ARN is the ARN of the domain\. Your domain must be on service software R20211203 or later in order to add these condition keys\. For example, you could add the following condition block to the trust policy: <pre>"Condition": {<br />    "StringEquals": {<br />        "aws:SourceAccount": "account-id"<br />    },<br />    "ArnLike": {<br />        "aws:SourceArn": "arn:aws:es:region:account-id:domain/domain-name"<br />    }<br />}</pre> For instructions to edit the trust relationship, see [Modifying a role trust policy](https://docs.aws.amazon.com/IAM/latest/UserGuide/roles-managingrole-editing-console.html#roles-managingrole_edit-trust-policy) in the *IAM User Guide*\. | 
| Permissions |  In order to register the snapshot repository, you need to be able to pass `TheSnapshotRole` to OpenSearch Service\. You also need access to the `es:ESHttpPut` action\. To grant both of these permissions, attach the following policy to the IAM role whose credentials are being used to sign the request: <pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [<br />    {<br />      "Effect": "Allow",<br />      "Action": "iam:PassRole",<br />      "Resource": "arn:aws:iam::123456789012:role/TheSnapshotRole"<br />    },<br />    {<br />      "Effect": "Allow",<br />      "Action": "es:ESHttpPut",<br />      "Resource": "arn:aws:es:region:123456789012:domain/domain-name/*"<br />    }<br />  ]<br />}</pre> If your user or role doesn't have `iam:PassRole` permissions to pass `TheSnapshotRole`, you might encounter the following common error when you try to register a repository in the next step: <pre>$ python register-repo.py<br />{"Message":"User: arn:aws:iam::123456789012:user/MyUserAccount<br />is not authorized to perform: iam:PassRole on resource:<br />arn:aws:iam::123456789012:role/TheSnapshotRole"}</pre>  | 

## Registering a manual snapshot repository<a name="managedomains-snapshot-registerdirectory"></a>

You need to register a snapshot repository with OpenSearch Service before you can take manual index snapshots\. This one\-time operation requires that you sign your AWS request with credentials that are allowed to access `TheSnapshotRole`, as described in [Prerequisites](#managedomains-snapshot-prerequisites)\.

### Step 1: Map the snapshot role in OpenSearch Dashboards \(if using fine\-grained access control\)<a name="managedomains-snapshot-fgac"></a>

Fine\-grained access control introduces an additional step when registering a repository\. Even if you use HTTP basic authentication for all other purposes, you need to map the `manage_snapshots` role to your IAM role that has `iam:PassRole` permissions to pass `TheSnapshotRole`\.

1. Navigate to the OpenSearch Dashboards plugin for your OpenSearch Service domain\. You can find the Dashboards endpoint on your domain dashboard on the OpenSearch Service console\. 

1. From the main menu choose **Security**, **Roles**, and select the **manage\_snapshots** role\.

1. Choose **Mapped users**, **Manage mapping**\. 

1. Add the ARN of the role that has permissions to pass `TheSnapshotRole`\. Put role ARNs under **Backend roles**\.

   ```
   arn:aws:iam::123456789123:role/role-name
   ```

1. Select **Map** and confirm the user or role shows up under **Mapped users**\.

### Step 2: Register a repository<a name="managedomains-snapshot-register"></a>

The following **Snapshots** tab demonstrates how to register a snapshot directory\. For options specific to encrypting a manual snapshot and registering a snapshot after migrating to a new domain, see the relevant tabs\.

------
#### [ Snapshots ]

To register a snapshot repository, send a PUT request to the OpenSearch Service domain endpoint\. You can use [curl](https://curl.se/docs/manpage.html#--aws-sigv4), the [sample Python client](#managedomains-snapshot-client-python), [Postman](https://www.getpostman.com/), or some other method to send a signed request to register the snapshot repository\. Note that you can't use a PUT request in the Kibana console to register the repository\.

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

**Note**  
Repository names cannot start with "cs\-"\. Additionally, you shouldn't write to the same repository from multiple domains\. Only one domain should have write access to the repository\.

If your domain resides within a virtual private cloud \(VPC\), your computer must be connected to the VPC for the request to successfully register the snapshot repository\. Accessing a VPC varies by network configuration, but likely involves connecting to a VPN or corporate network\. To check that you can reach the OpenSearch Service domain, navigate to `https://your-vpc-domain.region.es.amazonaws.com` in a web browser and verify that you receive the default JSON response\.

When your Amazon S3 bucket is in another AWS Region than your OpenSearch domain, replace the `"region"` parmeter with `"endpoint": "s3.amazonaws.com"`\.

------
#### [ Encrypted snapshots ]

You currently can't use AWS Key Management Service \(KMS\) keys to encrypt manual snapshots, but you can protect them using server\-side encryption \(SSE\)\.

To turn on SSE with S3\-managed keys for the bucket you use as a snapshot repository, add `"server_side_encryption": true` to the `"settings"` block of the PUT request\. For more information, see [Protecting data using server\-side encryption with Amazon S3\-managed encryption keys](https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingServerSideEncryption.html) in the *Amazon Simple Storage Service User Guide*\.

Alternatively, you can use AWS KMS keys for server\-side encryption on the S3 bucket that you use as a snapshot repository\. If you use this approach, make sure to provide `TheSnapshotRole` permission to the AWS KMS key used to encrypt the S3 bucket\. For more information, see [Key policies in AWS KMS](https://docs.aws.amazon.com/kms/latest/developerguide/key-policies.html)\.

------
#### [ Domain migration ]

Registering a snapshot repository is a one\-time operation\. However, to migrate from one domain to another, you have to register the same snapshot repository on the old domain and the new domain\. The repository name is arbitrary\.

Consider the following guidelines when migrating to a new domain or registering the same repository with multiple domains:
+ When registering the repository on the new domain, add `"readonly": true` to the `"settings"` block of the PUT request\. This setting prevents you from accidentally overwriting data from the old domain\. Only one domain should have write access to the repository\.
+ If you're migrating data to a domain in a different AWS Region, \(for example, from an old domain and bucket located in us\-east\-2 to a new domain in us\-west\-2\), replace `"region": "region"` with `"endpoint": "s3.amazonaws.com"` in the PUT statement and retry the request\.

------

#### Using the sample Python client<a name="managedomains-snapshot-client-python"></a>

The Python client is easier to automate than a simple HTTP request and has better reusability\. If you choose to use this method to register a snapshot repository, save the following sample Python code as a Python file, such as `register-repo.py`\. The client requires the [AWS SDK for Python \(Boto3\)](https://aws.amazon.com/sdk-for-python/), [requests](http://docs.python-requests.org/) and [requests\-aws4auth](https://pypi.python.org/pypi/requests-aws4auth) packages\. The client contains commented\-out examples for other snapshot operations\.

Update the following variables in the sample code: `host`, `region`, `path`, and `payload`\.

```
import boto3
import requests
from requests_aws4auth import AWS4Auth

host = '' # domain endpoint with trailing /
region = '' # e.g. us-west-1
service = 'es'
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
    "role_arn": "arn:aws:iam::123456789012:role/snapshot-role"
  }
}

headers = {"Content-Type": "application/json"}

r = requests.put(url, auth=awsauth, json=payload, headers=headers)

print(r.status_code)
print(r.text)

# # Take snapshot
#
# path = '_snapshot/my-snapshot-repo-name/my-snapshot'
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
# # Restore snapshot (all indexes except Dashboards and fine-grained access control)
#
# path = '_snapshot/my-snapshot-repo-name/my-snapshot/_restore'
# url = host + path
#
# payload = {
#   "indices": "-.kibana*,-.opendistro_security,-.opendistro-*",
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
# path = '_snapshot/my-snapshot-repo-name/my-snapshot/_restore'
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

OpenSearch snapshots are incremental, meaning they only store data that changed since the last successful snapshot\. This incremental nature means the difference in disk usage between frequent and infrequent snapshots is often minimal\. In other words, taking hourly snapshots for a week \(for a total of 168 snapshots\) might not use much more disk space than taking a single snapshot at the end of the week\. Also, the more frequently you take snapshots, the less time they take to complete\. For example, daily snapshots can take 20\-30 minutes to complete, whereas hourly snapshots might complete within a few minutes\. Some OpenSearch users take snapshots as often as every half hour\.

### Take a snapshot<a name="managedomains-snapshot-take"></a>

You specify the following information when you create a snapshot:
+ The name of your snapshot repository
+ A name for the snapshot

The examples in this chapter use [curl](https://curl.haxx.se/), a common HTTP client, for convenience and brevity\. To pass a username and password to your curl request, see the [Getting started tutorial](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/gsgupload-data.html#gsgsingle-document)\.

If your access policies specify users or roles, you must sign your snapshot requests\. For curl, you can use the [`--aws-sigv4` option](https://curl.se/docs/manpage.html#--aws-sigv4) with version 7\.75\.0 or later\. You can also use the commented\-out examples in the [sample Python client](#managedomains-snapshot-client-python) to make signed HTTP requests to the same endpoints that the curl commands use\.

To take a manual snapshot, perform the following steps:

1. You can't take a snapshot if one is currently in progress\. To check, run the following command:

   ```
   curl -XGET 'domain-endpoint/_snapshot/_status'
   ```

1. Run the following command to take a manual snapshot:

   ```
   curl -XPUT 'domain-endpoint/_snapshot/repository-name/snapshot-name'
   ```

   To include or exclude certain indexes and specify other settings, add a request body\. For the request structure, see [Take snapshots](https://opensearch.org/docs/1.1/opensearch/snapshot-restore/#take-snapshots) in the OpenSearch documentation\.

**Note**  
The time required to take a snapshot increases with the size of the OpenSearch Service domain\. Long\-running snapshot operations sometimes encounter the following error: `504 GATEWAY_TIMEOUT`\. You can typically ignore these errors and wait for the operation to complete successfully\. Run the following command to verify the state of all snapshots of your domain:  

```
curl -XGET 'domain-endpoint/_snapshot/repository-name/_all?pretty'
```

## Restoring snapshots<a name="managedomains-snapshot-restore"></a>

**Warning**  
If you use index aliases, you should either cease write requests to an alias or switch the alias to another index prior to deleting its index\. Halting write requests helps avoid the following scenario:  
You delete an index, which also deletes its alias\.
An errant write request to the now\-deleted alias creates a new index with the same name as the alias\.
You can no longer use the alias due to a naming conflict with the new index\. If you switched the alias to another index, specify `"include_aliases": false` when you restore from a snapshot\.

To restore a snapshot, perform the following steps:

1. Identify the snapshot you want to restore\. Ensure that all settings for this index, such as custom analyzer packages or allocation requirement settings, are compatible with the domain\. To see all snapshot repositories, run the following command:

   ```
   curl -XGET 'domain-endpoint/_snapshot?pretty'
   ```

   After you identify the repository, run the following command to see all snapshots:

   ```
   curl -XGET 'domain-endpoint/_snapshot/repository-name/_all?pretty'
   ```
**Note**  
Most automated snapshots are stored in the `cs-automated` repository\. If your domain encrypts data at rest, they're stored in the `cs-automated-enc` repository\. If you don't see the manual snapshot repository you're looking for, make sure you [registered it](#managedomains-snapshot-registerdirectory) to the domain\.

1. \(Optional\) Delete or rename one or more indexes in the OpenSearch Service domain if you have naming conflicts between indexes on the cluster and indexes in the snapshot\. You can't restore a snapshot of your indexes to an OpenSearch cluster that already contains indexes with the same names\.

   You have the following options if you have index naming conflicts:
   + Delete the indexes on the existing OpenSearch Service domain and then restore the snapshot\.
   + [Rename the indexes as you restore them from the snapshot](handling-errors.md#troubleshooting-close-api) and reindex them later\.
   + Restore the snapshot to a different OpenSearch Service domain \(only possible with manual snapshots\)\.

   The following command deletes all existing indexes in a domain:

   ```
   curl -XDELETE 'domain-endpoint/_all'
   ```

   However, if you don't plan to restore all indexes, you can just delete one:

   ```
   curl -XDELETE 'domain-endpoint/index-name'
   ```

1. To restore a snapshot, run the following command:

   ```
   curl -XPOST 'domain-endpoint/_snapshot/repository-name/snapshot-name/_restore'
   ```

   Due to special permissions on the OpenSearch Dashboards and fine\-grained access control indexes, attempts to restore all indexes might fail, especially if you try to restore from an automated snapshot\. The following example restores just one index, `my-index`, from `2020-snapshot` in the `cs-automated` snapshot repository:

   ```
   curl -XPOST 'domain-endpoint/_snapshot/cs-automated/2020-snapshot/_restore' \
   -d '{"indices": "my-index"}' \
   -H 'Content-Type: application/json'
   ```

   Alternately, you might want to restore all indexes *except* the Dashboards and fine\-grained access control indexes:

   ```
   curl -XPOST 'domain-endpoint/_snapshot/cs-automated/2020-snapshot/_restore' \
   -d '{"indices": "-.kibana*,-.opendistro*"}' \
   -H 'Content-Type: application/json'
   ```

   You can restore a snapshot without deleting its data by using the `rename_pattern` and `rename_replacement` parameters\. For more information on these parameters, see the Restore Snapshot API [request fields](https://opensearch.org/docs/latest/api-reference/snapshots/restore-snapshot/#request-fields) and [example request](https://opensearch.org/docs/latest/api-reference/snapshots/restore-snapshot/#example-request) in the OpenSearch documentation\.

**Note**  
If not all primary shards were available for the indexes involved, a snapshot might have a `state` of `PARTIAL`\. This value indicates that data from at least one shard wasn't stored successfully\. You can still restore from a partial snapshot, but you might need to use older snapshots to restore any missing indexes\.

## Deleting manual snapshots<a name="managedomains-snapshot-delete"></a>

To delete a manual snapshot, run the following command:

```
DELETE _snapshot/repository-name/snapshot-name
```

## Automating snapshots with Snapshot Management<a name="managedomains-snapshot-mgmt"></a>

You can set up a Snapshot Management \(SM\) policy in OpenSearch Dashboards to automate periodic snapshot creation and deletion\. SM can snapshot of a group of indices, whereas [Index State Management](#managedomains-snapshot-ism) can only take one snapshot per index\. To use SM in OpenSearch Service, you need to register your own Amazon S3 repository\. For instructions to register your repository, see [Registering a manual snapshot repository](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/managedomains-snapshots.html#managedomains-snapshot-registerdirectory)\.

Prior to SM, OpenSearch Service offered a free, automated snapshot feature that's still turned on by default\. This feature sends snapshots into the service\-maintained `cs-*` repository\. To deactivate the feature, reach out to AWS Support\. 

For more information on the SM feature, see [Snapshot management](https://opensearch.org/docs/latest/dashboards/sm-dashboards/) in the OpenSearch documentation\.

SM doesn't currently support snapshot creation on multiple index types\. For example, if you try to create snapshot on multiple indices with `*` and some indices are in the [warm tier](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/ultrawarm.html#ultrawarm-manual-snapshot), the snapshot creation will fail\. If you need your snapshot to contain multiple index types, use the [ISM snapshot action](https://opensearch.org/docs/latest/im-plugin/ism/policies/#snapshot) until SM supports this option\.

### Configure permissions<a name="sm-security"></a>

If you're upgrading to 2\.5 from a previous OpenSearch Service domain version, the snapshot management security permissions might not be defined on the domain\. Non\-admin users must be mapped to this role in order to use snapshot management on domains using fine\-grained access control\. To manually create the snapshot management role, perform the following steps:

1. In OpenSearch Dashboards, go to **Security** and choose **Permissions**\.

1. Choose **Create action group** and configure the following groups:     
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/managedomains-snapshots.html)

1. Choose **Roles** and **Create role**\.

1. Name the role **snapshot\_management\_role**\.

1. For **Cluster permissions**, select `snapshot_management_full_access` or `snapshot_management_read_access`\.

1. Choose **Create**\.

1. After you create the role, [map it](fgac.md#fgac-mapping) to any user or backend role that will manage snapshots\.

### Considerations<a name="sm-considerations"></a>

Consider the following when you configure snapshot management:
+ One policy is allowed per repository\.
+ Up to 400 snapshots are allowed for one policy\.
+ This feature won't run if your domain has a red status, is under high JVM pressure \(85% or above\), or has a stuck snapshot function\. When the overall indexing and searching performance of your cluster is impacted, SM may also be impacted\.
+ A snapshot operation only starts after the previous operation finishes, so that no concurrent snapshot operations are activated by one policy\.
+ Multiple policies with the same schedule can cause a resource spike\. If the policies' snapshotted indices overlap, the shard\-level snapshot operations can only run sequentially, which can cause a cascaded performance problem\. If the policies share a repository, there will be spike of write operations to that repository\.
+ We recommend that you schedule your snapshot operations automation to no more than once per hour, unless you have a special use case\.

## Automating snapshots with Index State Management<a name="managedomains-snapshot-ism"></a>

You can use the Index State Management \(ISM\) `[snapshot](https://opendistro.github.io/for-elasticsearch-docs/docs/im/ism/policies/#snapshot)` operation to automatically trigger snapshots of indexes based on changes in their age, size, or number of documents\. ISM is best when you need one snapshot per index\. If you need to snapshot of a group of indices, see [Automating snapshots with Snapshot Management](#managedomains-snapshot-mgmt)\.

To use SM in OpenSearch Service, you need to register your own Amazon S3 repository\. For an example ISM policy using the `snapshot` operation, see [Sample Policies](ism.md#ism-example)\.

## Using Curator for snapshots<a name="managedomains-snapshot-curator"></a>

If ISM doesn't work for index and snapshot management, you can use Curator instead\. It offers advanced filtering functionality that can help simplify management tasks on complex clusters\. Use [pip](https://pip.pypa.io/en/stable/installing/) to install Curator:

```
pip install elasticsearch-curator
```

You can use Curator as a command line interface \(CLI\) or Python API\. If you use the Python API, you must use version 7\.13\.4 or earlier of the legacy [elasticsearch\-py](https://elasticsearch-py.readthedocs.io/) client\. It doesn't support the opensearch\-py client\. 

If you use the CLI, export your credentials at the command line and configure `curator.yml` as follows:

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