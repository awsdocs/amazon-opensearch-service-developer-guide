# Stopping and starting Amazon OpenSearch Ingestion pipelines<a name="pipeline--stop-start"></a>

Stopping and starting Amazon OpenSearch Ingestion pipelines helps you manage costs for development and test environments\. You can temporarily stop a pipeline instead of setting it up and tearing it down each time that you use the pipeline\. 

**Topics**
+ [Overview of stopping and starting an OpenSearch Ingestion pipeline](#pipeline--start-stop-overview)
+ [Stopping an OpenSearch Ingestion pipeline](#pipeline--stop)
+ [Starting an OpenSearch Ingestion pipeline](#pipeline--start)

## Overview of stopping and starting an OpenSearch Ingestion pipeline<a name="pipeline--start-stop-overview"></a>

You can stop a pipeline during periods where you don't need to ingest data into it\. You can start the pipeline again anytime you need to use it\. Starting and stopping simplifies the setup and teardown processes for pipelines used for development, testing, or similar activities that don't require continuous availability\.

While your pipeline is stopped, you aren't charged for any Ingestion OCU hours\. You can still update stopped pipelines, and they receive automatic minor version updates and security patches\. 

Don't use starting and stopping if you need to keep your pipeline running but it has more capacity than you need\. If your pipeline is too costly or not very busy, consider reducing its maximum capacity limits\. For more information, see [Scaling pipelines](ingestion.md#ingestion-scaling)\.

## Stopping an OpenSearch Ingestion pipeline<a name="pipeline--stop"></a>

To use an OpenSearch Ingestion pipeline or perform administration, you always begin with an active pipeline, then stop the pipeline, and then start the pipeline again\. While your pipeline is stopped, you're not charged for Ingestion OCU hours\.

### Console<a name="stop-pipeline-console"></a>

**To stop a pipeline**

1. Sign in to the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home)\.

1. In the navigation pane, choose **Pipelines**, and then choose a pipeline\. You can perform the stop operation from this page, or navigate to the details page for the pipeline that you want to stop\.

1. For **Actions**, choose **Stop pipeline**\.

   If a pipeline can't be stopped and started, the **Stop pipeline** action isn't available\.

### AWS CLI<a name="stop-pipeline-cli"></a>

To stop a pipeline using the AWS CLI, call the [stop\-pipeline](https://docs.aws.amazon.com/cli/latest/reference/osis/stop-pipeline.html) command with the following parameters: 
+ `--pipeline-name` – the name of the pipeline\. 

**Example**  

```
aws osis stop-pipeline --pipeline-name my-pipeline
```

### OpenSearch Ingestion API<a name="stop-pipeline-api"></a>

To stop a pipeline using the OpenSearch Ingestion API, call the [StopPipeline](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_osis_StopPipeline.html) operation with the following parameter: 
+ `PipelineName` – the name of the pipeline\. 

## Starting an OpenSearch Ingestion pipeline<a name="pipeline--start"></a>

You always start an OpenSearch Ingestion pipeline beginning with a pipeline that's already in the stopped state\. The pipeline keeps its configuration settings such as capacity limits, network settings, and log publishing options\.

Restarting a pipeline usually takes several minutes\.

### Console<a name="start-pipeline-console"></a>

**To start a pipeline**

1. Sign in to the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home)\.

1.  In the navigation pane, choose **Pipelines**, and then choose a pipeline\. You can perform the start operation from this page, or navigate to the details page for the pipeline that you want to start\. 

1.  For **Actions**, choose **Start pipeline**\. 

### AWS CLI<a name="start-pipeline-cli"></a>

To start a pipeline by using the AWS CLI, call the [start\-pipeline](https://docs.aws.amazon.com/cli/latest/reference/osis/start-pipeline.html) command with the following parameters: 
+ `--pipeline-name` – the name of the pipeline\.

**Example**  

```
aws osis start-pipeline --pipeline-name my-pipeline
```

### OpenSearch Ingestion API<a name="start-pipeline-api"></a>

To start an OpenSearch Ingestion pipeline using the OpenSearch Ingestion API, call the [StartPipeline](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_osis_StartPipeline.html) operation with the following parameter: 
+ `PipelineName` – the name of the pipeline\.