# Amazon Managed Streaming for Apache Kafka<a name="configure-client-msk"></a>

You can use the [Kafka plugin](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/kafka/) to ingest data from [Amazon Managed Streaming for Apache Kafka](https://docs.aws.amazon.com/msk/latest/developerguide/) \(Amazon MSK\) into your OpenSearch Ingestion pipeline\. With Amazon MSK, you can build and run applications that use Apache Kafka to process streaming data\. OpenSearch Ingestion uses AWS PrivateLink to connect to Amazon MSK\.

**Topics**
+ [Prerequisites](#msk-prereqs)
+ [Step 1: Configure the pipeline role](#msk-pipeline-role)
+ [Step 2: Create the pipeline](#msk-pipeline)
+ [Step 3: \(Optional\) Use the AWS Glue Schema Registry](#msk-glue)
+ [Step 4: \(Optional\) Configure recommended compute units \(OCUs\) for the Amazon MSK pipeline](#msk-ocu)

## Prerequisites<a name="msk-prereqs"></a>

Before you create your OpenSearch Ingestion pipeline, perform the following steps:

1. Create an Amazon MSK cluster by following the steps in [Creating a cluster](https://docs.aws.amazon.com/msk/latest/developerguide/msk-create-cluster.html#create-cluster-console) in the *Amazon Managed Streaming for Apache Kafka Developer Guide*\.
   + For **Cluster type**, choose **Provisioned**\. OpenSearch Ingestion doesn't support Serverless MSK clusters\.

1. After the cluster has an **Active** status, follow the steps in [Turn on multi\-VPC connectivity](https://docs.aws.amazon.com/msk/latest/developerguide/aws-access-mult-vpc.html#mvpc-cluster-owner-action-turn-on)\.

1. Follow the steps in [Attach a cluster policy to the MSK cluster](https://docs.aws.amazon.com/msk/latest/developerguide/aws-access-mult-vpc.html#mvpc-cluster-owner-action-policy) to attach the following policy:

   ```
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Principal": {
           "Service": "osis.amazonaws.com"
         },
         "Action": [
           "kafka:CreateVpcConnection",
           "kafka:DescribeCluster",
           "kafka:DescribeClusterV2"
         ],
         "Resource": "arn:aws:kafka:us-east-1:{account-id}:cluster/cluster-name/cluster-id"
       },
       {
         "Effect": "Allow",
         "Principal": {
           "Service": "osis-pipelines.amazonaws.com"
         },
         "Action": [
           "kafka:CreateVpcConnection",
           "kafka:GetBootstrapBrokers",
           "kafka:DescribeCluster",
           "kafka:DescribeClusterV2"
         ],
         "Resource": "arn:aws:kafka:us-east-1:{account-id}:cluster/cluster-name/cluster-id"
       },
       {
         "Effect": "Allow",
         "Principal": {
           "AWS": "arn:aws:iam::{account-id}:role/pipeline-role"
         },
         "Action": [
           "kafka-cluster:*",
           "kafka:*"
         ],
         "Resource": [
           "arn:aws:kafka:us-east-1:{account-id}:cluster/cluster-name/cluster-id",
           "arn:aws:kafka:us-east-1:{account-id}:topic/cluster-name/cluster-id/*",
           "arn:aws:kafka:us-east-1:{account-id}:group/cluster-name/*"
         ]
       }
     ]
   }
   ```

1. Create a Kafka topic by following the steps in [Create a topic](https://docs.aws.amazon.com/msk/latest/developerguide/create-topic.html)\. Make sure that `BootstrapServerString` is one of the private endpoint \(single\-VPC\) bootstrap URLs\. The value for `--replication-factor` should be `2` or `3`, based on the number of zones your MSK cluster has\. The value for `--partitions` should be at least `10`\.

1. Produce and consume data by following the steps in [Produce and consume data](https://docs.aws.amazon.com/msk/latest/developerguide/produce-consume.html)\. Again, make sure that `BootstrapServerString` is one of your private endpoint \(single\-VPC\) bootstrap URLs\.

## Step 1: Configure the pipeline role<a name="msk-pipeline-role"></a>

After you have your MSK cluster set up, add the following Kafka permissions in the pipeline role that you want to use in your pipeline configuration:

```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "kafka-cluster:Connect",
                "kafka-cluster:AlterCluster",
                "kafka-cluster:DescribeCluster",
                "kafka:DescribeClusterV2",
                "kafka:GetBootstrapBrokers"
            ],
            "Resource": [
                "arn:aws:kafka:us-east-1:{account-id}:cluster/cluster-name/cluster-id"
            ]
        },
        {
            "Effect": "Allow",
            "Action": [
                "kafka-cluster:*Topic*",
                "kafka-cluster:ReadData"
            ],
            "Resource": [
                "arn:aws:kafka:us-east-1:{account-id}:topic/cluster-name/cluster-id/topic-name"
            ]
        },
        {
            "Effect": "Allow",
            "Action": [
                "kafka-cluster:AlterGroup",
                "kafka-cluster:DescribeGroup"
            ],
            "Resource": [
                "arn:aws:kafka:us-east-1:{account-id}:group/cluster-name/*"
            ]
        }
    ]
}
```

## Step 2: Create the pipeline<a name="msk-pipeline"></a>

You can then configure an OpenSearch Ingestion pipeline like the following, which specifies Kafka as the source:

```
version: "2"
log-pipeline:
  source:
    kafka:
      acknowledgements: true 
      topics:
      - name: "topic-name"  
        group_id: "group-id"
        serde_format: "json"/"plaintext"
      aws:
        msk:
          arn: "arn:aws:iam::{account-id}:role/cluster-role"
        region: "us-west-2"
        sts_role_arn: "arn:aws:iam::{account-id}:role/pipeline-role"
      schema:                               # Optional
        type: "aws_glue"  
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
      index: "index_name"
      aws_sts_role_arn: "arn:aws:iam::{account-id}:role/pipeline-role"
      aws_region: "us-east-1"
      aws_sigv4: true
```

You can use the **AWS\-MSKPipeline** blueprint to create this pipeline\. For more information, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.

## Step 3: \(Optional\) Use the AWS Glue Schema Registry<a name="msk-glue"></a>

When you use OpenSearch Ingestion with Amazon MSK, you can use the AVRO data format for schemas hosted in the AWS Glue Schema Registry\. With the [AWS Glue Schema Registry](https://docs.aws.amazon.com/glue/latest/dg/schema-registry.html), you can centrally discover, control, and evolve data stream schemas\. 

To use this option, enable the schema `type` in your pipeline configuration:

```
schema:
  type: "aws_glue"
```

You must also provide AWS Glue with read access permissions in your pipeline role\. You can use the AWS managed policy called [AWSGlueSchemaRegistryReadonlyAccess](https://docs.aws.amazon.com/aws-managed-policy/latest/reference/AWSGlueSchemaRegistryReadonlyAccess.html)\. Additionally, your registry must be in the same AWS account and Region as your OpenSearch Ingestion pipeline\.

## Step 4: \(Optional\) Configure recommended compute units \(OCUs\) for the Amazon MSK pipeline<a name="msk-ocu"></a>

Each compute unit has one consumer per topic\. Brokers balance partitions among these consumers for a given topic\. However, when the number of partitions is greater than the number of consumers, Amazon MSK hosts multiple partitions on every consumer\. OpenSearch Ingestion has built\-in auto scaling to scale up or down based on CPU usage or number of pending records in the pipeline\. 

For optimal performance, distribute your partitions across many compute units for parallel processing\. If topics have a large number of partitions \(for example, more than 96, which is the maximum OCUs per pipeline\), we recommend that you configure a pipeline with 1–96 OCUs\. This is because it will automatically scale as needed\. If a topic has a low number of partitions \(for example, less than 96\), keep the maximum compute unit the same as the number of partitions\. 

When a pipeline has more than one topic, choose the topic with the highest number of partitions as a reference to configure maximum computes units\. By adding another pipeline with a new set of OCUs to the same topic and consumer group, you can scale the throughput almost linearly\.