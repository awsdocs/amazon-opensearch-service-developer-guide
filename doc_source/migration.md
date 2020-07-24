# Migrating to Amazon Elasticsearch Service<a name="migration"></a>

Index snapshots are a popular way to migrate from a self\-managed Elasticsearch cluster to Amazon Elasticsearch Service\. Broadly, the process consists of the following steps:

1. Take a snapshot of the existing cluster, and upload the snapshot to an Amazon S3 bucket\.

1. Create an Amazon ES domain\.

1. Give Amazon ES permissions to access the bucket, and give your user account permissions to work with snapshots\.

1. Restore the snapshot on the Amazon ES domain\.

This walkthrough provides more detailed steps and alternate options, where applicable\.

## Take and Upload the Snapshot<a name="migration-take-snapshot"></a>

Although you can use the [repository\-s3](https://opendistro.github.io/for-elasticsearch-docs/docs/elasticsearch/snapshot-restore/#amazon-s3) plugin to take snapshots directly to S3, you have to install the plugin on every node, tweak `elasticsearch.yml`, restart each node, add your AWS credentials, and finally take the snapshot\. The plugin is a great option for ongoing use or for migrating larger clusters\.

For smaller clusters, a one\-time approach is to take a [shared file system snapshot](https://opendistro.github.io/for-elasticsearch-docs/docs/elasticsearch/snapshot-restore/#shared-file-system) and then use the AWS CLI to upload it to S3\. If you already have a snapshot, skip to step 4\.

**To take a snapshot and upload it to Amazon S3**

1. Add the `path.repo` setting to `elasticsearch.yml` on all nodes, and then restart each node\.

   ```
   path.repo: ["/my/shared/directory/snapshots"]
   ```

1. Register the snapshot repository:

   ```
   PUT _snapshot/migration-repository
   {
     "type": "fs",
     "settings": {
       "location": "/my/shared/directory/snapshots"
     }
   }
   ```

1. Take the snapshot:

   ```
   PUT _snapshot/migration-repository/migration-snapshot
   {
     "indices": "migration-index1,migration-index2,other-indices-*",
     "include_global_state": false
   }
   ```

1. Install the [AWS CLI](https://aws.amazon.com/cli/), and run `aws configure` to add your credentials\.

1. Navigate to the snapshot directory\. Then run the following commands to create a new S3 bucket and upload the contents of the snapshot directory to that bucket:

   ```
   aws s3 mb s3://migration-bucket --region us-west-2
   aws s3 sync . s3://migration-bucket --sse AES256
   ```

   Depending on the size of the snapshot and the speed of your internet connection, this operation can take a while\.

## Create the Domain<a name="migration-create-domain"></a>

Although the console is the easiest way to create a domain, in this case, you already have the terminal open and the AWS CLI installed\. Modify the following command to create a domain that fits your needs:

```
aws es create-elasticsearch-domain \
  --domain-name migration-domain \
  --elasticsearch-version 7.7 \
  --elasticsearch-cluster-config InstanceType=c5.large.elasticsearch,InstanceCount=2 \
  --ebs-options EBSEnabled=true,VolumeType=gp2,VolumeSize=100 \
  --node-to-node-encryption-options Enabled=true \
  --encryption-at-rest-options Enabled=true \
  --domain-endpoint-options EnforceHTTPS=true,TLSSecurityPolicy=Policy-Min-TLS-1-2-2019-07 \
  --advanced-security-options Enabled=true,InternalUserDatabaseEnabled=true,MasterUserOptions='{MasterUserName=master-user,MasterUserPassword=master-user-password}' \
  --access-policies '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"AWS":["*"]},"Action":["es:ESHttp*"],"Resource":"arn:aws:es:us-west-2:123456789012:domain/migration-domain/*"}]}' \
  --region us-west-2
```

As is, the command creates an internet\-accessible domain with two data nodes, each with 100 GiB of storage\. It also enables [fine\-grained access control](fgac.md) with HTTP basic authentication and all encryption settings\. Use the Amazon ES console if you need a more advanced security configuration, such as a VPC\.

Before issuing the command, change the domain name, master user credentials, and account number\. Specify the same region that you used for the S3 bucket and an Elasticsearch version that is compatible with your snapshot\.

**Important**  
Snapshots are only forward\-compatible, and only by one major version\. For example, you can't restore a snapshot from a 2\.*x* cluster on a 1\.*x* cluster or a 6\.*x* cluster, only a 2\.*x* or 5\.*x* cluster\. Minor version matters, too\. You can't restore a snapshot from a self\-managed 5\.3\.3 cluster on a 5\.3\.2 Amazon ES domain\. We recommend choosing the most\-recent version of Elasticsearch that your snapshot supports\.

## Provide Permissions<a name="migration-permissions"></a>

In the AWS Identity and Access Management \(IAM\) console, [create a role](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create.html) with the following permissions and trust relationship\. Name the role `AmazonESSnapshotRole` so that it's easy to find\.

**Permissions**

```
{
  "Version": "2012-10-17",
  "Statement": [{
      "Action": [
        "s3:ListBucket"
      ],
      "Effect": "Allow",
      "Resource": [
        "arn:aws:s3:::migration-bucket"
      ]
    },
    {
      "Action": [
        "s3:GetObject",
        "s3:PutObject",
        "s3:DeleteObject"
      ],
      "Effect": "Allow",
      "Resource": [
        "arn:aws:s3:::migration-bucket/*"
      ]
    }
  ]
}
```

**Trust Relationship**

```
{
  "Version": "2012-10-17",
  "Statement": [{
      "Effect": "Allow",
      "Principal": {
        "Service": "es.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
```

Then give your personal IAM user or role—whatever you used to configure the AWS CLI earlier—permissions to assume `AmazonESSnapshotRole`\. Create the following policy and [attach it](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_manage-attach-detach.html) to your identity\.

**Permissions**

```
{
  "Version": "2012-10-17",
  "Statement": [{
      "Effect": "Allow",
      "Action": "iam:PassRole",
      "Resource": "arn:aws:iam::123456789012:role/AmazonESSnapshotRole"
    }
  ]
}
```

Then log in to Kibana using the master user credentials you specified when you created the Amazon ES domain\. You can find the Kibana URL in the Amazon ES console\. It takes the form of `https://domain-endpoint/_plugin/kibana/`\.

In Kibana, choose **Security**, **Role Mappings**, and **Add**\. For **Role,** choose **manage\_snapshots**\. Then specify the ARN for your personal IAM user or role in the appropriate field\. User ARNs go in the **Users** section\. Role ARNs go in the **Backend roles** section\. This step uses [fine\-grained access control](fgac.md#fgac-mapping) to give your identity permissions to work with snapshots\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/migration1.png)

## Restore the Snapshot<a name="migration-restore"></a>

At this point, you have two ways to access your Amazon ES domain: HTTP basic authentication with your master user credentials or AWS authentication using your IAM credentials\. Because snapshots use Amazon S3, which has no concept of the master user, you must use your IAM credentials to register the snapshot repository with your Amazon ES domain\.

Most programming languages have libraries to assist with [signing requests](es-request-signing.md), but the simpler approach is to use a tool like [Postman](https://www.postman.com/downloads/) and put your IAM credentials into the **Authorization** section\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/migration2.png)

**To restore the snapshot**

1. Regardless of how you choose to sign your requests, the first step is to register the repository:

   ```
   PUT _snapshot/migration-repository
   {
     "type": "s3",
     "settings": {
       "bucket": "migration-bucket",
       "region": "us-west-2",
       "role_arn": "arn:aws:iam::123456789012:role/AmazonESSnapshotRole"
     }
   }
   ```

1. Then list the snapshots in the repository, and find the one you want to restore\. At this point, you can continue using Postman or switch to a tool like [curl](https://curl.haxx.se/)\.

   **Shorthand**

   ```
   GET _snapshot/migration-repository/_all
   ```

   **curl**

   ```
   curl -XGET -u master-user:master-user-password https://domain-endpoint/_snapshot/migration-repository/_all
   ```

1. Restore the snapshot\.

   **Shorthand**

   ```
   POST _snapshot/migration-repository/migration-snapshot/_restore
   {
     "indices": "migration-index1,migration-index2,other-indices-*",
     "include_global_state": false
   }
   ```

   **curl**

   ```
   curl -XPOST -u master-user:master-user-password https://domain-endpoint/_snapshot/migration-repository/migration-snapshot/_restore \
     -H 'Content-Type: application/json' \
     -d '{"indices":"migration-index1,migration-index2,other-indices-*","include_global_state":false}'
   ```

1. Finally, verify that your indices restored as expected\.

   **Shorthand**

   ```
   GET _cat/indices?v
   ```

   **curl**

   ```
   curl -XGET -u master-user:master-user-password https://domain-endpoint/_cat/indices?v
   ```

At this point, the migration is complete\. You might configure your clients to use the new Amazon ES endpoint, [resize the domain](sizing-domains.md) to suit your workload, check the shard count for your indices, switch to an [IAM master user](fgac.md#fgac-concepts), or start building Kibana dashboards\.