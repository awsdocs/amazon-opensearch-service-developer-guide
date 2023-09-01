# Amazon Security Lake<a name="configure-client-security-lake"></a>

You can use the [S3 source plugin](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/s3/) to ingest data from [Amazon Security Lake](https://docs.aws.amazon.com/security-lake/latest/userguide/what-is-security-lake.html) into your OpenSearch Ingestion pipeline\. Security Lake automatically centralizes security data from AWS environments, on\-premises environments, and SaaS providers into a purpose\-built data lake\. You can create a subscription that replicates data from Security Lake to your OpenSearch Ingestion pipeline, which then writes it to your OpenSearch Service domain or OpenSearch Serverless collection\.

To configure your pipeline to read from Security Lake, use the Security Lake blueprint named **AWS\-SecurityLakeS3ParquetOCSFPipeline**\. The blueprint includes a default configuration for ingesting Open Cybersecurity Schema Framework \(OCSF\) parquet files from Security Lake\. For more information, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.

**Topics**
+ [Prerequisites](#sl-prereqs)
+ [Step 1: Configure the pipeline role](#sl-pipeline-role)
+ [Step 2: Create the pipeline](#sl-pipeline)

## Prerequisites<a name="sl-prereqs"></a>

Before you create your OpenSearch Ingestion pipeline, perform the following steps:
+ [Enable Security Lake](https://docs.aws.amazon.com/security-lake/latest/userguide/getting-started.html#enable-service)\.
+ [Create a subscriber](https://docs.aws.amazon.com/security-lake/latest/userguide/subscriber-data-access.html#create-subscriber-data-access) in Security Lake\.
  + Choose the sources that you want to ingest into your pipeline\.
  + For **Subscriber credentials**, add the ID of the AWS account where you intend to create the pipeline\. For the external ID, specify `OpenSearchIngestion-{accountid}`\.
  + For **Data access method**, choose **S3**\.
  + For **Notification details**, choose **SQS queue**\.

When you create a subscriber, Security Lake automatically creates two inline permissions policies—one for S3 and one for SQS\. The policies take the following format: `AmazonSecurityLake-{12345}-S3` and `AmazonSecurityLake-{12345}-SQS`\. To allow your pipeline to access the subscriber sources, you must associate the required permissions with your pipeline role\.

## Step 1: Configure the pipeline role<a name="sl-pipeline-role"></a>

Create a new permissions policy in IAM that combines only the required permissions from the two policies that Security Lake automatically created\. The following example policy shows the least privilege required for an OpenSearch Ingestion pipeline to read data from multiple Security Lake sources:

```
{
   "Version":"2012-10-17",
   "Statement":[
      {
         "Effect":"Allow",
         "Action":[
            "s3:GetObject"
         ],
         "Resource":[
            "arn:aws:s3:::aws-security-data-lake-{region}-abcde/aws/LAMBDA_EXECUTION/1.0/*",
            "arn:aws:s3:::aws-security-data-lake-{region}-abcde/aws/S3_DATA/1.0/*",
            "arn:aws:s3:::aws-security-data-lake-{region}-abcde/aws/VPC_FLOW/1.0/*",
            "arn:aws:s3:::aws-security-data-lake-{region}-abcde/aws/ROUTE53/1.0/*",
            "arn:aws:s3:::aws-security-data-lake-{region}-abcde/aws/SH_FINDINGS/1.0/*"
         ]
      },
      {
         "Effect":"Allow",
         "Action":[
            "sqs:ReceiveMessage",
            "sqs:DeleteMessage"
         ],
         "Resource":[
            "arn:aws:sqs:{region}:{account-id}:AmazonSecurityLake-abcde-Main-Queue"
         ]
      }
   ]
}
```

**Important**  
Security Lake doesn’t manage the pipeline role policy for you\. If you add or remove sources from your Security Lake subscription, you must manually update the policy\. Security Lake creates partitions for each log source, so you need to manually add or remove permissions in the pipeline role\.

You must attach these permissions to the IAM role that you specify in the `sts_role_arn` option within the S3 source plugin configuration, under `sqs`\.

```
version: "2"
source:
  s3:
    ...
    sqs:
      queue_url: "https://sqs.{region}.amazonaws.com/{your-account-id}/AmazonSecurityLake-abcde-Main-Queue"
    aws:
      ...
      sts_role_arn: arn:aws:iam::{your-account-id}:role/pipeline-role
processor:
  ...
sink:
  - opensearch:
      ...
```

## Step 2: Create the pipeline<a name="sl-pipeline"></a>

After you add the permissions to the pipeline role, use the **AWS\-SecurityLakeS3ParquetOCSFPipeline** blueprint to create the pipeline\. For more information, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.

You must specify the `queue_url` option within the `s3` source configuration, which is the Amazon SQS queue URL to read from\. To format the URL, locate the **Subscription endpoint** in the subscriber configuration and change `arn:aws:` to `https://`\. For example, `https://sqs.{region}.amazonaws.com/{account-id}/AmazonSecurityLake-abdcef-Main-Queue`\.

The `sts_role_arn` that you specify within the S3 source configuration must be the ARN of the pipeline role\.