# Viewing Amazon OpenSearch Ingestion pipelines<a name="list-pipeline"></a>

You can view the details about an Amazon OpenSearch Ingestion pipeline using the AWS Management Console, the AWS CLI, or the OpenSearch Ingestion API\.

## Console<a name="list-pipeline-console"></a>

**To view a pipeline**

1. Sign in to the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home)\.

1. Choose **Pipelines** in the left navigation pane\.

1. \(Optional\) To view pipelines with a particular status, choose **Any status** and select a status to filter by\.

   A pipeline can have the following statuses:
   + `Creating` – The pipeline is being created\.
   + `Active` – The pipeline is active and ready to ingest data\.
   + `Updating` – The pipeline is being updated\.
   + `Deleting` – The pipeline is being deleted\.
   + `Create failed` – The pipeline could not be created\.
   + `Update failed` – The pipeline could not be updated\.
   + `Starting` – The pipeline is starting\.
   + `Start failed` – The pipeline could not be started\.
   + `Stopping` – The pipeline is being stopped\.
   + `Stopped` – The pipeline is stopped and can be restarted at any time\.

You're not billed for Ingestion OCUs when a pipeline is in the `Create failed`, `Creating`, `Deleting`, and `Stopped` states\.

## CLI<a name="list-pipeline-cli"></a>

To view pipelines using the AWS CLI, send a [list\-pipelines](https://docs.aws.amazon.com/cli/latest/reference/osis/list-pipelines.html) request:

```
aws osis list-pipelines  
```

The request returns a list of all existing pipelines:

```
{
    "NextToken": null,
    "Pipelines": [
        {,
            "CreatedAt": 1.671055851E9,
            "LastUpdatedAt": 1.671055851E9,
            "MaxUnits": 4,
            "MinUnits": 2,
            "PipelineArn": "arn:aws:osis:us-west-2:123456789012:pipeline/log-pipeline",
            "PipelineName": "log-pipeline",
            "Status": "ACTIVE",
            "StatusReason": {
                "Description": "The pipeline is ready to ingest data."
            }
        },
            "CreatedAt": 1.671055851E9,
            "LastUpdatedAt": 1.671055851E9,
            "MaxUnits": 2,
            "MinUnits": 8,
            "PipelineArn": "arn:aws:osis:us-west-2:123456789012:pipeline/another-pipeline",
            "PipelineName": "another-pipeline",
            "Status": "CREATING",
            "StatusReason": {
                "Description": "The pipeline is being created. It is not able to ingest data."
            }
        }
    ]
}
```

To get information about a single pipeline, use the [get\-pipeline](https://docs.aws.amazon.com/cli/latest/reference/osis/get-pipeline.html) command:

```
aws osis get-pipeline --pipeline-name "my-pipeline"
```

The request returns configuration information for the specified pipeline:

```
{
    "Pipeline": {
        "PipelineName": "my-pipeline",
        "PipelineArn": "arn:aws:osis:us-east-1:123456789012:pipeline/my-pipeline",
        "MinUnits": 9,
        "MaxUnits": 10,
        "Status": "ACTIVE",
        "StatusReason": {
            "Description": "The pipeline is ready to ingest data."
        },
        "PipelineConfigurationBody": "log-pipeline:\n source:\n http:\n processor:\n - grok:\n match:\nlog: [ '%{COMMONAPACHELOG}' ]\n - date:\n from_time_received: true\n destination: \"@timestamp\"\n  sink:\n - opensearch:\n hosts: [ \"https://search-mdp-performance-test-duxkb4qnycd63rpy6svmvyvfpi.us-east-1.es.amazonaws.com\" ]\n index: \"apache_logs\"\n aws_sts_role_arn: \"arn:aws:iam::123456789012:role/my-domain-role\"\n  aws_region: \"us-east-1\"\n  aws_sigv4: true",,
        "CreatedAt": "2022-10-01T15:28:05+00:00",
        "LastUpdatedAt": "2022-10-21T21:41:08+00:00",
        "IngestEndpointUrls": [
            "my-pipeline-123456789012.us-east-1.osis.amazonaws.com"
        ]
    }
}
```

## OpenSearch Ingestion API<a name="list-pipelines-api"></a>

To view OpenSearch Ingestion pipelines using the OpenSearch Ingestion API, call the [ListPipelines](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_osis_ListPipelines.html) and [GetPipeline](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_osis_GetPipeline.html) operations\.