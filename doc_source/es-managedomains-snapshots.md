# Working with Amazon Elasticsearch Service Index Snapshots<a name="es-managedomains-snapshots"></a>

Snapshots are backups of a cluster's data and state\. They provide a convenient way to migrate data across Amazon ES domains and recover from failure\. The service supports restoring from snapshots taken on both Amazon ES domains and self\-managed Elasticsearch clusters\.

Amazon ES takes daily automated snapshots of the primary index shards in a domain, as described in [[ERROR] BAD/MISSING LINK TEXT](es-createupdatedomains.md#es-createdomain-configure-snapshots)\. It stores these automated snapshots in a preconfigured Amazon S3 bucket for 14 days at no additional charge to you\. You can use these snapshots to restore the domain\.

You cannot, however, use automated snapshots to migrate to new domains\. Automated snapshots are read\-only from within a given domain\. For migrations, you must use manual snapshots stored in your own repository \(an S3 bucket\)\. Standard S3 charges apply to manual snapshots\.

**Tip**  
Some users find tools like the [Curator](https://www.elastic.co/guide/en/elasticsearch/client/curator/current/index.html) CLI convenient for index and snapshot management\. The Curator CLI offers advanced filtering functionality that can help simplify tasks on complex clusters\.


+ [Manual Snapshot Prerequisites](#es-managedomains-snapshot-prerequisites)
+ [Registering a Manual Snapshot Directory](#es-managedomains-snapshot-registerdirectory)
+ [Taking Manual Snapshots](#es-managedomains-snapshot-create)
+ [Restoring Snapshots](#es-managedomains-snapshot-restore)

## Manual Snapshot Prerequisites<a name="es-managedomains-snapshot-prerequisites"></a>

To create and restore index snapshots manually, you must work with IAM and Amazon S3\. Verify that you have met the following prerequisites before you attempt to take a snapshot\.


****  

| Prerequisite  | Description | 
| --- | --- | 
| S3 bucket | Stores manual snapshots for your Amazon ES domain\. | 
| IAM role | Delegates permissions to Amazon Elasticsearch Service\. The trust relationship for the role must specify Amazon Elasticsearch Service in the Principal statement\. The IAM role also is required to register your snapshot repository with Amazon ES\. Only IAM users with access to this role may register the snapshot repository\. | 
| IAM policy | Specifies the actions that Amazon S3 may perform with your S3 bucket\. The policy must be attached to the IAM role that delegates permissions to Amazon Elasticsearch Service\. The policy must specify an S3 bucket in a Resource statement\. | 

**S3 Bucket**

You need an S3 bucket to store manual snapshots\. Make a note of its Amazon Resource Name \(ARN\)\. You need it for the following:

+ `Resource` statement of the IAM policy that is attached to your IAM role

+ Python client that is used to register a snapshot repository

The following example shows an ARN for an S3 bucket:

```
arn:aws:s3:::es-index-backups
```

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

When you create an AWS service role using the IAM console, Amazon ES is not included in the **Select role type** list\. However, you can still create the role by choosing **Amazon EC2**, following the steps to create the role, and then editing the role's trust relationships to `es.amazonaws.com` instead of `ec2.amazonaws.com`\. For instructions and additional information, see [Creating a Role for an AWS Service](http://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create_for-service.html#roles-creatingrole-service-console) and [Modifying a Role](http://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_manage_modify.html#roles-managingrole-editing-console) in the *IAM User Guide*\.

**Note**  
Only IAM users or roles with access to this service role may register snapshot repositories\. A common way to provide access is to attach the following policy to the user or role:  

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

## Registering a Manual Snapshot Directory<a name="es-managedomains-snapshot-registerdirectory"></a>

You must register the snapshot directory with Amazon Elasticsearch Service before you can take manual index snapshots\. This one\-time operation requires that you sign your AWS request with credentials for one of the users or roles specified in the IAM role's trust relationship, as described in [[ERROR] BAD/MISSING LINK TEXT](#es-managedomains-snapshot-prerequisites)\.

**Note**  
You can't use `curl` to perform this operation because it doesn't support AWS request signing\. Instead, use the sample Python client to register your snapshot directory\.  
If your domain resides within a VPC, your computer must be able to access the VPC in order for the Python client to successfully register the snapshot directory\. Accessing a VPC varies by network configuration, but likely involves connecting to a VPN or corporate network\. To check that you can reach the Amazon ES domain, navigate to `https://your-vpc-domain.region.es.amazonaws.com` in a web browser and verify that you receive the default JSON response\.

<a name="es-managedomains-snapshot-client-python"></a>**Sample Python Client**

Save the following sample Python code as a Python file, such as `snapshot.py`\. Registering a snapshot directory is a one\-time operation, but to migrate from one domain to another, you need to perform these steps on the old domain and the new domain\.

You must update the following in your code:

`region`  
AWS Region where you created the snapshot repository

`host`  
Endpoint for your Amazon ES domain

`aws_access_key_id`  
IAM credential

`aws_secret_access_key`  
IAM credential

`path`  
Name of the snapshot repository

`data`  
Must include the name of the S3 bucket and the ARN for the IAM role that you created in [[ERROR] BAD/MISSING LINK TEXT](#es-managedomains-snapshot-prerequisites)\. To enable [server\-side encryption with S3\-managed keys](http://docs.aws.amazon.com/AmazonS3/latest/dev/UsingServerSideEncryption.html) for the snapshot repository, add `"server_side_encryption": true` to the `"settings"` JSON\.

**Note**  
This sample Python client requires that you install version 2\.*x* of the [boto](https://github.com/boto/boto#installation) package on the computer where you register your snapshot repository\.

```
from boto.connection import AWSAuthConnection

class ESConnection(AWSAuthConnection):

    def __init__(self, region, **kwargs):
        super(ESConnection, self).__init__(**kwargs)
        self._set_auth_region_name(region)
        self._set_auth_service_name("es")

    def _required_auth_capability(self):
        return ['hmac-v4']

if __name__ == "__main__":

    client = ESConnection(
            region='us-west-1',
            host='search-weblogs-etrt4mbbu254nsfupy6oiytuz4.us-west-1.es.example.com',
            aws_access_key_id='my-access-key-id',
            aws_secret_access_key='my-access-key', is_secure=False)

    print 'Registering Snapshot Repository'
    resp = client.make_request(method='PUT',
            path='/_snapshot/weblogs-index-backups',
            data='{"type": "s3","settings": { "bucket": "es-index-backups","region": "us-west-1","role_arn": "arn:aws:iam::123456789012:role/TheServiceRole"}}',
            headers={'Content-Type': 'application/json'})
    body = resp.read()
    print body
```

**Important**  
If the S3 bucket is in the us\-east\-1 region, you need to use `"endpoint": "s3.amazonaws.com"` in place of `"region": "us-east-1"`\.

## Taking Manual Snapshots<a name="es-managedomains-snapshot-create"></a>

You specify two pieces of information when you create a snapshot:

+ Name of your snapshot repository

+ Name for the snapshot

The following example requests use [curl](https://curl.haxx.se/), a common HTTP client, for convenience\. If your access policies specify IAM users or roles, however, snapshot requests must be signed\. Clients like curl can't perform this signing\. For sample code that demonstrates how to make signed requests, see [[ERROR] BAD/MISSING LINK TEXT](es-indexing.md#es-indexing-programmatic)\.

**To manually take a snapshot**

+ Run the following command to manually take a snapshot:

  ```
  curl -XPUT 'elasticsearch-domain-endpoint/_snapshot/repository/snapshot_name'
  ```

  The following example takes a snapshot named `snapshot_1` and stores it in the `weblogs-index-backups` snapshot repository:

  ```
  curl -XPUT 'elasticsearch-domain-endpoint/_snapshot/weblogs-index-backups/snapshot_1'
  ```

**Note**  
The time required to take a snapshot increases with the size of the Amazon ES domain\. Long\-running snapshot operations commonly encounter the following error: `504 GATEWAY_TIMEOUT`\. Typically, you can ignore these errors and wait for the operation to complete successfully\. Use the following command to verify the state of all snapshots of your domain:   

```
curl -XGET 'elasticsearch-domain-endpoint/_snapshot/repository/_all?pretty'
```

For more information about the options available to you when taking a snapshot, see [Snapshot and Restore](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-snapshots.html#_snapshot) in the Elasticsearch documentation\.

## Restoring Snapshots<a name="es-managedomains-snapshot-restore"></a>

To restore a snapshot, perform the following procedure:

1. Identify the snapshot that you want to restore\. To see all snapshot repositories, run the following command:

   ```
   curl -XGET 'elasticsearch-domain-endpoint/_snapshot?pretty'
   ```

   After you identify the repository, you run the following command to see all snapshots:

   ```
   curl -XGET 'elasticsearch-domain-endpoint/_snapshot/repository/_all?pretty'
   ```
**Note**  
Most automated snapshots are stored in the `cs-automated` repository\. If your domain encrypts data at rest, they are stored in the `cs-automated-enc` repository\. If you don't see the manual snapshot repository that you're looking for, make sure that you registered it to the domain\.

1. Delete or rename all open indices in the Amazon ES domain\.

   You can't restore a snapshot of your indices to an Elasticsearch cluster that already contains indices with the same names\. Currently, Amazon ES does not support the Elasticsearch `_close` API, so you must use one of the following alternatives:

   + Delete the indices on the same Amazon ES domain, then restore the snapshot\.

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