# Setting up roles and users in Amazon OpenSearch Ingestion<a name="pipeline-security-overview"></a>

Amazon OpenSearch Ingestion uses a variety of permissions models and IAM roles in order to allow source applications to write to pipelines, and to allow pipelines to write to sinks\. Before you can start ingesting data, you need to create one or more IAM roles with specific permissions based on your use case\.

At minimum, the following roles are required to set up a successful pipeline\.


| Name | Description | 
| --- | --- | 
|  [**Management role**](#pipeline-security-create) |  Any principal that's managing pipelines \(generally a "pipeline admin"\) needs management access, which includes permissions like `osis:CreatePipeline` and `osis:UpdatePipeline`\. These permissions allow a user to administer pipelines but not necessarily write data to them\.  | 
| [**Pipeline role**](#pipeline-security-sink) |  The pipeline role, which you specify within the pipeline's YAML configuration, provides the required permissions for a pipeline to write to the domain or collection sink and read from pull\-based sources\. For more information, see the following topics: [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/pipeline-security-overview.html)  | 
| [**Ingestion role**](#pipeline-security-same-account) |  The ingestion role contains the `osis:Ingest` permission for the pipeline resource\. This permission allows push\-based sources to ingest data into a pipeline\.  | 

The following image demonstrates a typical pipeline setup, where a data source such as Amazon S3 or Fluent Bit is writing to a pipeline in a different account\. In this case, the client needs to assume the ingestion role in order to access the pipeline\. For more information, see [Cross\-account ingestion](#pipeline-security-different-account)\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/pipeline-security.png)

For a simple setup guide, see [Tutorial: Ingesting data into a domain using Amazon OpenSearch Ingestion](osis-get-started.md)\.

**Topics**
+ [Management role](#pipeline-security-create)
+ [Ingestion role](#pipeline-security-same-account)
+ [Pipeline role](#pipeline-security-sink)
+ [Cross\-account ingestion](#pipeline-security-different-account)
+ [Reading from Amazon S3](#pipeline-security-read-data)

## Management role<a name="pipeline-security-create"></a>

In addition to the basic `osis:*` permissions needed to create and modify a pipeline, you also need the `iam:PassRole` permission for the pipeline role resource\. Any AWS service that accepts a role must use this permission\. OpenSearch Ingestion assumes the role every time it needs to write data to a sink\. This helps administrators ensure that only approved users can configure OpenSearch Ingestion with a role that grants permissions\. For more information, see [Granting a user permissions to pass a role to an AWS service](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_use_passrole.html)\.

You need the following permissions to create and update a pipeline:

```
{
   "Version":"2012-10-17",
   "Statement":[
      {
         "Effect":"Allow",
         "Resource":"*",
         "Action":[
            "osis:CreatePipeline",
            "osis:ListPipelineBlueprints",
            "osis:ValidatePipeline",
            "osis:UpdatePipeline"
         ]
      },
      {
         "Resource":[
            "arn:aws:iam::{your-account-id}:role/pipeline-role"
         ],
         "Effect":"Allow",
         "Action":[
            "iam:PassRole"
         ]
      }
   ]
}
```

## Pipeline role<a name="pipeline-security-sink"></a>

A pipeline needs certain permissions to write to its sink\. These permissions depend on whether the sink is an OpenSearch Service domain or an OpenSearch Serverless collection\.

In addition, a pipeline might need permissions to pull from the source application \(if the source is a pull\-based plugin\), and permissions to write to an S3 dead letter queue, if configured\.

**Topics**
+ [Writing to a domain sink](#pipeline-security-domain-sink)
+ [Writing to a collection sink](#pipeline-security--collection-sink)
+ [Reading from Amazon S3](#pipeline-security-read-data)
+ [Writing to a dead\-letter queue](#pipeline-security-dlq)

### Writing to a domain sink<a name="pipeline-security-domain-sink"></a>

An OpenSearch Ingestion pipeline needs permission to write to an OpenSearch Service domain that is configured as its sink\. These permissions include the ability to describe the domain and send HTTP requests to it\.

In order to provide your pipeline with the required permissions to write to a sink, first create an AWS Identity and Access Management \(IAM\) role with the [required permissions](pipeline-domain-access.md#pipeline-access-configure)\. These permissions are the same for public and VPC pipelines\. Then, specify the pipeline role in the domain access policy so that the domain can accept write requests from the pipeline\.

Finally, specify the role ARN as the value of the **sts\_role\_arn** option within the pipeline configuration:

```
version: "2"
source:
  http:
    ...
processor:
  ...
sink:
  - opensearch:
      ...
      aws:
        sts_role_arn: arn:aws:iam::{your-account-id}:role/pipeline-role
```

For instructions to complete each of these steps, see [Allowing pipelines to access domains](pipeline-domain-access.md)\.

### Writing to a collection sink<a name="pipeline-security--collection-sink"></a>

An OpenSearch Ingestion pipeline needs permission to write to an OpenSearch Serverless collection that is configured as its sink\. These permissions include the ability to describe the collection and send HTTP requests to it\.

First, create an IAM role that has the `aoss:BatchGetCollection` permission against all resources \(`*`\)\. Then, include this role in a data access policy and provide it permissions to create indexes, update indexes, describe indexes, and write documents within the collection\. Finally, specify the role ARN as the value of the **sts\_role\_arn** option within the pipeline configuration\.

For instructions to complete each of these steps, see [Allowing pipelines to access collections](pipeline-collection-access.md)\.

### Reading from Amazon S3<a name="pipeline-security-read-data"></a>

Unlike other source plugins that *push* data to a pipeline, the [S3 source plugin](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/s3/) has a read\-based architecture in which the pipeline *pulls* data from the source\. Therefore, in order for a pipeline to read from S3, you must specify a role within the pipeline's S3 source configuration that has access to both the S3 bucket and the Amazon SQS queue\. The pipeline will assume this role in order to read data from the queue\.

**Note**  
The S3 access role that you specify must be the same as the [pipeline role]()\. Therefore, your pipeline role must contain two separate permissions policies—one to write to a sink, and one to pull from the S3 source\. You must use the same `sts_role_arn` in all pipeline components\.

The following sample policy shows the required permissions for using S3 as a source:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "ReadFromS3",
      "Effect": "Allow",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::my-bucket/*"
    },
    {
      "Sid": "ReceiveAndDeleteSqsMessages",
      "Effect": "Allow",
      "Action": [
        "sqs:DeleteMessage",
        "sqs:ReceiveMessage"
      ],
      "Resource": "arn:aws:sqs:us-west-2:{your-account-id}:MyS3EventSqsQueue"
    }
  ]
}
```

 You must attach these permissions to the IAM role that you specify in the `sts_role_arn` option within the S3 source plugin configuration:

```
version: "2"
source:
  s3:
    ...
    aws:
      ...
      sts_role_arn: arn:aws:iam::{your-account-id}:role/pipeline-role
processor:
  ...
sink:
  - opensearch:
      ...
```

For a more complete example, see [Writing from Amazon S3](configure-client.md#configure-client-s3)\.

### Writing to a dead\-letter queue<a name="pipeline-security-dlq"></a>

If you configure your pipeline to write to a [dead\-letter queue](https://opensearch.org/docs/latest/data-prepper/pipelines/dlq/) \(DLQ\), you must include the `sts_role_arn` option within the DLQ configuration\. The permissions included in this role allow the pipeline to access the S3 bucket that you specify as the destination for DLQ events\.

You must use the same `sts_role_arn` in all pipeline components\. Therefore, you must attach a separate permissions policy to your pipeline role that provides DLQ access\. At minimum, the role must be allowed the `S3:PutObject` action on the bucket resource:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "WriteToS3DLQ",
      "Effect": "Allow",
      "Action": "s3:PutObject",
      "Resource": "arn:aws:s3:::my-dlq-bucket/*"
    }
  ]
}
```

You can then specify the role within the pipeline's DLQ configuration:

```
  ...
  sink:
    opensearch:
      dlq:
        s3:
          bucket: "my-dlq-bucket"
          key_path_prefix: "dlq-files"
          region: "us-west-2"
          sts_role_arn: "arn:aws:iam::123456789012:role/pipeline-role"
```

## Ingestion role<a name="pipeline-security-same-account"></a>

All source plugins that OpenSearch Ingestion currently supports, with the exception of S3, use a push\-based architecture\. This means that the source application *pushes* the data to the pipeline, rather than the pipeline *pulling* the data from the source\.

Therefore, you must grant your source applications the required permissions to ingest data into an OpenSearch Ingestion pipeline\. At minimum, the role that signs the request must be granted permission for the `osis:Ingest` action, which allows it to send data to a pipeline\. The same permissions are required for public and VPC pipeline endpoints\.

The following example policy allows the associated principal to ingest data into a single pipeline called `my-pipeline`:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PermitsWriteAccessToPipeline",
      "Effect": "Allow",
      "Action": "osis:Ingest",
      "Resource": "arn:aws:osis:us-west-2:{your-account-id}:pipeline/my-pipeline"
    }
  ]
}
```

For more information, see [Sending data to Amazon OpenSearch Ingestion pipelines](configure-client.md)\.

### Cross\-account ingestion<a name="pipeline-security-different-account"></a>

You might need to ingest data into a pipeline from a different AWS account, such as an application account\. To configure cross\-account ingestion, define an ingestion role within the same account as the pipeline and establish a trust relationship between the ingestion role and the application account:

```
{
  "Version": "2012-10-17",
  "Statement": [{
     "Effect": "Allow",
     "Principal": {
       "AWS": "arn:aws:iam::{external-account-id}:root"
      },
     "Action": "sts:AssumeRole"
  }]
}
```

Then, configure your application to assume the ingestion role\. The application account must grant the application role [AssumeRole](https://docs.aws.amazon.com/STS/latest/APIReference/API_AssumeRole.html) permissions for the ingestion role in the pipeline account\.

For detailed steps and example IAM policies, see [Providing cross\-account ingestion access](configure-client.md#configure-client-cross-account)\.