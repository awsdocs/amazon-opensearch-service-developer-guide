# Amazon S3<a name="configure-client-s3"></a>

With OpenSearch Ingestion, you can use Amazon S3 as a source or as a destination\. When you use Amazon S3 as a source, you send data to an OpenSearch Ingestion pipeline\. When you use Amazon S3 as a destination, you write data from an OpenSearch Ingestion pipeline to one or more S3 buckets\.

**Topics**
+ [Amazon S3 as a source](#s3-source)
+ [Amazon S3 as a destination](#s3-destination)

## Amazon S3 as a source<a name="s3-source"></a>

There are two ways that you can use Amazon S3 as a source to process data—with *S3\-SQS processing* and with *scheduled scans*\. 

Use S3\-SQS processing when you require near real\-time scanning of files after they are written to S3\. You can configure Amazon S3 buckets to raise an event any time an object is stored or modified within the bucket\. Use a one\-time or recurring scheduled scan to batch process data in a S3 bucket\. 

**Topics**
+ [Prerequisites](#s3-prereqs)
+ [Step 1: Configure the pipeline role](#s3-pipeline-role)
+ [Step 2: Create the pipeline](#s3-pipeline)

### Prerequisites<a name="s3-prereqs"></a>

To use Amazon S3 as the source for an OpenSearch Ingestion pipeline for both a scheduled scan or S3\-SQS processing, first [create an S3 bucket](https://docs.aws.amazon.com/AmazonS3/latest/userguide/create-bucket-overview.html)\.

If the S3 bucket used as a source in the OpenSearch Ingestion pipeline is in a different AWS account, you also need to enable cross\-account read permissions on the bucket\. This allows the pipeline to read and process the data\. To enable cross\-account permissions, see [Bucket owner granting cross\-account bucket permissions](https://docs.aws.amazon.com/AmazonS3/latest/userguide/example-walkthroughs-managing-access-example2.html) in the *Amazon S3 User Guide*\.

To set up S3\-SQS processing, you also need to perform the following steps:

1. [Create an Amazon SQS queue](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/step-create-queue.html)\.

1. [Enable event notifications](https://docs.aws.amazon.com/AmazonS3/latest/userguide/enable-event-notifications.html) on the S3 bucket with the SQS queue as a destination\.

### Step 1: Configure the pipeline role<a name="s3-pipeline-role"></a>

Unlike other source plugins that *push* data to a pipeline, the [S3 source plugin](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/s3/) has a read\-based architecture in which the pipeline *pulls* data from the source\. 

Therefore, in order for a pipeline to read from S3, you must specify a role within the pipeline's S3 source configuration that has access to both the S3 bucket and the Amazon SQS queue\. The pipeline will assume this role in order to read data from the queue\.

**Note**  
The role that you specify within the S3 source configuration must be the [pipeline role]()\. Therefore, your pipeline role must contain two separate permissions policies—one to write to a sink, and one to pull from the S3 source\. You must use the same `sts_role_arn` in all pipeline components\.

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

### Step 2: Create the pipeline<a name="s3-pipeline"></a>

After you've set up your permissions, you can configure an OpenSearch Ingestion pipeline depending on your Amazon S3 use case\.

#### S3\-SQS processing<a name="s3-sqs-processing"></a>

To set up S3\-SQS processing, configure your pipeline to specify S3 as the source and set up Amazon SQS notifications:

```
version: "2"
s3-pipeline:
  source:
    s3:
      notification_type: "sqs"
      codec:
        newline: null
      sqs:
        queue_url: "https://sqs.us-east-1.amazonaws.com/{account-id}/ingestion-queue"
      compression: "none"
      aws:
        region: "us-east-1"
        # IAM role that the pipeline assumes to read data from the queue. This role must be the same as the pipeline role.
        sts_role_arn: "arn:aws:iam::{account-id}:role/pipeline-role"
  processor:
  - grok:
      match:
        log:
        - "%{COMMONAPACHELOG}"
  - date:
      destination: "@timestamp"
      from_time_received: true
  sink:
  - opensearch:
      hosts: ["https://search-domain-endpoint.us-east-1.es.amazonaws.com"]
      index: "index-name"
      aws:
        # IAM role that the pipeline assumes to access the domain sink
        sts_role_arn: "arn:aws:iam::{account-id}:role/pipeline-role"
        region: "us-east-1"
```

#### Scheduled scan<a name="s3-scheduled-scan"></a>

To set up a scheduled scan, configure your pipeline with a schedule at the scan level that applies to all your S3 buckets, or at the bucket level\. A bucket\-level schedule or a scan\-interval configuration always overwrites a scan\-level configuration\. 

You can configure scheduled scans with either a *one\-time scan*, which is ideal for data migration, or a *recurring scan*, which is ideal for batch processing\. 

To configure your pipeline to read from Amazon S3, use the Amazon S3 blueprints named **AWS\-S3ScanPipeline** or **AWS\-S3ScanSchedulePipeline**\. You can edit the `scan` portion of your pipeline configuration to meet your scheduling needs\. For more information, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.

**One\-time scan**

A one\-time scheduled scan runs once\. In your YAML configuration, you can use a `start_time` and `end_time` to specify when you want the objects in the bucket to be scanned\. Alternatively, you can use `range` to specify the interval of time relative to current time that you want the objects in the bucket to be scanned\. 

For example, a range set to `PT4H` scans all files created in the last four hours\. To configure a one\-time scan to run a second time, you must stop and restart the pipeline\. If you don't have a range configured, you must also update the start and end times\.

The following configuration sets up a one\-time scan for all buckets and all objects in those buckets:

```
version: "2"
log-pipeline:
  source:
    s3:
      codec:
        csv:
      compression: "none"
      aws:
        region: "us-east-1"
        sts_role_arn: "arn:aws:iam::{account-id}:role/pipeline-role"
      acknowledgments: true
      scan:
        buckets:
          - bucket:
              name: my-bucket-1
              filter:
                include_prefix:
                  - Objects1/
                exclude_suffix:
                  - .jpeg
                  - .png
          - bucket:
              name: my-bucket-2
              key_prefix:
                include:
                  - Objects2/
                exclude_suffix:
                  - .jpeg
                  - .png
      delete_s3_objects_on_read: false
  processor:
    - date:
        destination: "@timestamp"
        from_time_received: true
  sink:
    - opensearch:
        hosts: ["https://search-domain-endpoint.us-east-1.es.amazonaws.com"]
        index: "index-name"
        aws:
          sts_role_arn: "arn:aws:iam::{account-id}:role/pipeline-role"
          region: "us-east-1"
        dlq:
          s3:
            bucket: "my-bucket-1"
            region: "us-east-1"
            sts_role_arn: "arn:aws:iam::{account-id}:role/pipeline-role"
```

The following configuration sets up a one\-time scan for all buckets during a specified time window\. This means that S3 processes only those objects with creation times that fall within this window\.

```
scan:
  start_time: 2023-01-21T18:00:00.000Z
  end_time: 2023-04-21T18:00:00.000Z
  buckets:
    - bucket:
        name: my-bucket-1
        filter:
          include:
            - Objects1/
          exclude_suffix:
            - .jpeg
            - .png
    - bucket:
        name: my-bucket-2
        filter:
          include:
            - Objects2/
          exclude_suffix:
            - .jpeg
            - .png
```

The following configuration sets up a one\-time scan at both the scan level and the bucket level\. Start and end times at the bucket level override start and end times at the scan level\. 

```
scan:
  start_time: 2023-01-21T18:00:00.000Z
  end_time: 2023-04-21T18:00:00.000Z
  buckets:
    - bucket:
        start_time: 2023-01-21T18:00:00.000Z
        end_time: 2023-04-21T18:00:00.000Z
        name: my-bucket-1
        filter:
          include:
            - Objects1/
          exclude_suffix:
            - .jpeg
            - .png
    - bucket:
        start_time: 2023-01-21T18:00:00.000Z
        end_time: 2023-04-21T18:00:00.000Z
        name: my-bucket-2
        filter:
          include:
            - Objects2/
          exclude_suffix:
            - .jpeg
            - .png
```

**Recurring scan**

A recurring scheduled scan runs a scan of your specified S3 buckets at regular, scheduled intervals\. You can only configure these intervals at the scan level because individual bucket level configurations aren't supported\. 

In your YAML configuration, the `interval` specifies the frequency of the recurring scan, and can be between 30 seconds and 365 days\. The first of these scans always occurs when you create the pipeline\. The `count` defines the total number of scan instances\.

The following configuration sets up a recurring scan, with a delay of 12 hours between the scans:

```
scan:
  scheduling:
    interval: PT12H
    count: 4
  buckets:
    - bucket:
        name: my-bucket-1
        filter:
          include:
            - Objects1/
          exclude_suffix:
            - .jpeg
            - .png
    - bucket:
        name: my-bucket-2
        filter:
          include:
            - Objects2/
          exclude_suffix:
            - .jpeg
            - .png
```

## Amazon S3 as a destination<a name="s3-destination"></a>

To write data from an OpenSearch Ingestion pipeline to an S3 bucket, use the blueprint named **AWS\-S3SinkLogPipeline** to create a pipeline with an [S3 sink](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sinks/s3/)\. This pipeline routes selective data to an OpenSearch sink and simultaneously sends all data for archival in S3\. For more information, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.

When you create your S3 sink, you can specify your preferred formatting from a variety of [sink codecs](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sinks/s3/#codec)\. For example, if you want to write data in columnar format, choose the Parquet or Avro codec\. If you prefer a row\-based format, choose JSON or ND\-JSON\. To write data to S3 in a specified schema, you can also define an inline schema within sink codecs using the [Avro format](https://avro.apache.org/docs/current/specification/#schema-declaration)\. 

The following example defines an inline schema in an S3 sink:

```
- s3:
  codec:
    parquet:
      schema: >
        {
           "type" : "record",
           "namespace" : "org.vpcFlowLog.examples",
           "name" : "VpcFlowLog",
           "fields" : [
             { "name" : "version", "type" : "string"},
             { "name" : "srcport", "type": "int"},
             { "name" : "dstport", "type": "int"},
             { "name" : "start", "type": "int"},
             { "name" : "end", "type": "int"},
             { "name" : "protocol", "type": "int"},
             { "name" : "packets", "type": "int"},
             { "name" : "bytes", "type": "int"},
             { "name" : "action", "type": "string"},
             { "name" : "logStatus", "type" : "string"}
           ]
         }
```

When you define this schema, specify a superset of all keys that might be present in the different types of events that your pipeline delivers to a sink\. 

For example, if an event has the possibility of a key missing, add that key in your schema with a `null` value\. Null value declarations allow the schema to process non\-uniform data \(where some events have these keys and others don't\)\. When incoming events do have these keys present, their values are written to sinks\. 

This schema definition acts as a filter that only allows defined keys to be sent to sinks, and drops undefined keys from incoming events\. 

You can also use `include_keys` and `exclude_keys` in your sink to filter data that's routed to other sinks\. These two filters are mutually exclusive, so you can only use one at a time in your schema\. Additionally, you can't use them within user\-defined schemas\. 

To create pipelines with such filters, use the **AWSSinkFilterWithSchemaPipeline** blueprint\. For more information, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.