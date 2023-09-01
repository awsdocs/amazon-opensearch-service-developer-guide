# Creating Amazon OpenSearch Ingestion pipelines<a name="creating-pipeline"></a>

A *pipeline* is the mechanism that Amazon OpenSearch Ingestion uses to move data from its *source* \(where the data comes from\) to its *sink* \(where the data goes\)\. In OpenSearch Ingestion, the sink will always be a single Amazon OpenSearch Service domain, while the source of your data could be clients like Amazon S3, Fluent Bit, or the OpenTelemetry Collector\.

For more information, see [Pipelines](https://opensearch.org/docs/latest/clients/data-prepper/pipelines/) in the OpenSearch documentation\.

**Topics**
+ [Prerequisites and required roles](#manage-pipeline-prerequisites)
+ [Permissions required](#create-pipeline-permissions)
+ [Specifying the pipeline version](#pipeline-version)
+ [Specifying the ingestion path](#pipeline-path)
+ [Creating pipelines](#create-pipeline)
+ [Tracking the status of pipeline creation](#get-pipeline-progress)
+ [Using blueprints to create a pipeline](#pipeline-blueprint)

## Prerequisites and required roles<a name="manage-pipeline-prerequisites"></a>

In order to create an OpenSearch Ingestion pipeline, you must have the following resources:
+ An IAM role that OpenSearch Ingestion will assume in order to write to the sink\. You will include this role ARN in your pipeline configuration\.
+ An OpenSearch Service domain or OpenSearch Serverless collection to act as the sink\. If you're writing to a domain, it must be running OpenSearch 1\.0 or later, or Elasticsearch 7\.4 or later\. The sink must have an access policy that grants the appropriate permissions to your IAM pipeline role\.

For instructions to create these resources, see the following topics:
+ [Allowing Amazon OpenSearch Ingestion pipelines to write to domains](pipeline-domain-access.md)
+ [Allowing Amazon OpenSearch Ingestion pipelines to write to collections](pipeline-collection-access.md)

**Note**  
If you're writing to a domain that uses fine\-grained access control, there are extra steps you need to complete\. See [Fine\-grained access control](pipeline-domain-access.md#pipeline-access-domain-fgac)\.

## Permissions required<a name="create-pipeline-permissions"></a>

OpenSearch Ingestion uses the following IAM permissions for creating pipelines:
+ `osis:CreatePipeline` – Create a pipeline\.
+ `osis:ValidatePipeline` – Check whether a pipeline configuration is valid\.
+ `iam:PassRole` – Pass the pipeline role to OpenSearch Ingestion so that it can write data to the domain\. This permission must be on the [pipeline role resource](pipeline-domain-access.md#pipeline-access-configure) \(the ARN that you specify for the `sts_role_arn` option in the pipeline configuration\), or simply `*` if you plan to use different roles in each pipeline\.

For example, the following policy grants permission to create a pipeline:

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
            "osis:ValidatePipeline"
         ]
      },
      {
         "Resource":[
            "arn:aws:iam::{your-account-id}:role/{pipeline-role}"
         ],
         "Effect":"Allow",
         "Action":[
            "iam:PassRole"
         ]
      }
   ]
}
```

OpenSearch Ingestion also includes a permission called `osis:Ingest`, which is required in order to send signed requests to the pipeline using [Signature Version 4](https://docs.aws.amazon.com/general/latest/gr/signature-version-4.html)\. For more information, see [Creating an ingestion role](configure-client.md#configure-client-auth)\.

**Note**  
In addition, the first user to create a pipeline in an account must have permissions for the `iam:CreateServiceLinkedRole` action\. For more information, see [pipeline role resource](pipeline-security.md#pipeline-vpc-slr)\.

For more information about each permission, see [Actions, resources, and condition keys for OpenSearch Ingestion](https://docs.aws.amazon.com/service-authorization/latest/reference/list_opensearchingestionservice.html) in the *Service Authorization Reference*\.

## Specifying the pipeline version<a name="pipeline-version"></a>

When you configure a pipeline, you must specify the major [version of Data Prepper](https://github.com/opensearch-project/data-prepper/releases) that the pipeline will run\. To specify the verson, include the `version` option in your pipeline configuration:

```
version: "2"
log-pipeline:
  source:
    ...
```

When you choose **Create**, OpenSearch Ingestion determines the latest available *minor* version of the major version that you specify, and provisions the pipeline with that version\. For example, if you specify `version: "2"`, and the latest supported version of Data Prepper is 2\.1\.1, OpenSearch Ingestion provisions your pipeline with version 2\.1\.1\. We don't publicly display the minor version that your pipeline is running\.

In order to upgrade your pipeline when a new major version of Data Prepper is available, edit the pipeline configuration and specify the new version\. You can't downgrade a pipeline to an earlier version\.

**Note**  
OpenSearch Ingestion doesn't immediately support new versions of Data Prepper as soon as they're released\. There will be some lag between when a new version is publicly available and when it's supported in OpenSearch Ingestion\. In addition, OpenSearch Ingestion might explicitly not support certain major or minor versions altogether\. For a comprehensive list, see [Supported Data Prepper versions](ingestion.md#ingestion-supported-versions)\.

Any time you make a change to your pipeline that initiates a blue/green deployment, OpenSearch Ingestion can upgrade it to the latest minor version of the major version that's currently configured in the pipeline YAML file\. For more information, see [Blue/green deployments for pipeline updates](update-pipeline.md#pipeline-bg)\. OpenSearch Ingestion can't change the major version of your pipeline unless you explicitly update the `version` option within the pipeline configuration\.

## Specifying the ingestion path<a name="pipeline-path"></a>

For pull\-based sources like [OTel trace](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-trace/) and [OTel metrics](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-metrics-source/), OpenSearch Ingestion requires the additional `path` option in your source configuration\. The path is a string such as `/log/ingest`, which represents the URI path for ingestion\. This path defines the URI that you use to send data to the pipeline\. 

For example, say you specify the following entry sub\-pipeline for an ingestion pipeline named `logs`:

```
entry-pipeline:
  source:
    http:
      path: "/my/test_path"
```

When you [ingest data](configure-client.md) into the pipeline, you must specify the following endpoint in your client configuration: `https://logs-abcdefgh.us-west-2.osis.amazonaws.com/my/test_path`\.

The path must start with a slash \(/\) and can contain the special characters '\-', '\_', '\.', and '/', as well as the `${pipelineName}` placeholder\. If you use `${pipelineName}` \(such as `path: "/${pipelineName}/test_path"`\), the variable is replaced with the name of the associated sub\-pipeline\. In this example, it would be `https://logs.us-west-2.osis.amazonaws.com/entry-pipeline/test_path`\.

## Creating pipelines<a name="create-pipeline"></a>

This section describes how to create OpenSearch Ingestion pipelines using the OpenSearch Service console and the AWS CLI\.

### Console<a name="create-pipeline-console"></a>

**To create a pipeline**

1. Sign in to the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home)\.

1. Choose **Pipelines** in the left navigation pane and choose **Create pipeline**\.

1. Enter a name for the pipeline\.

1. Configure the minimum and maximum pipeline capacity in Ingestion OpenSearch Compute Units \(OCUs\)\. For more information, see [Scaling pipelines](ingestion.md#ingestion-scaling)\.

1. Under **Pipeline configuration**, provide your pipeline configuration in YAML format\. A single pipeline configuration file can contain 1\-10 sub\-pipelines\. Each sub\-pipeline is a combination of a single source, zero or more processors, and a single sink\. For OpenSearch Ingestion, the sink must always be an OpenSearch Service domain\. For a list of supported options, see [Supported plugins and options for Amazon OpenSearch Ingestion pipelines](pipeline-config-reference.md)\.
**Note**  
You must include the `sts_role_arn` and `sigv4` options in each sub\-pipeline\. The pipeline assumes the rule defined in `sts_role_arn` to sign requests to the domain\. For more information, see [Allowing Amazon OpenSearch Ingestion pipelines to write to domains](pipeline-domain-access.md)\.

   The following sample configuration file uses the HTTP source and Grok plugins to process unstructured log data and send it to an OpenSearch Service domain\. The sub\-pipeline is named `log-pipeline`\.

   ```
   version: "2"
   log-pipeline:
     source:
       http:
         path: "/log/ingest"
     processor:
       - grok:
           match:
             log: [ '%{COMMONAPACHELOG}' ]
       - date:
           from_time_received: true
           destination: "@timestamp"
     sink:
       - opensearch:
           hosts: [ "https://search-my-domain.us-east-1.es.amazonaws.com" ]
           index: "apache_logs"
           aws:
             sts_role_arn: "arn:aws:iam::123456789012:role/{pipeline-role}"
             region: "us-east-1"
   ```
**Note**  
If you specify multiple sinks within a YAML pipeline definition, they must all be the *same* OpenSearch Service domain\. An OpenSearch Ingestion pipeline can't write to multiple different domains\.

   You can build your own pipeline configuration, or choose **Upload file** and import an existing configuration for a self\-managed Data Prepper pipeline\. Alternatively, you can use a [configuration blueprint](#pipeline-blueprint)\.

1. After you configure your pipeline, choose **Validate pipeline** to confirm that your configuration is correct\. If the validation fails, fix the errors and re\-run the validation\.

1. Under **Network**, choose either **VPC access** or **Public access**\. If you choose **Public access**, skip to the next step\. If you choose **VPC access**, configure the following settings:    
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/creating-pipeline.html)

   For more information, see [Securing Amazon OpenSearch Ingestion pipelines within a VPC](pipeline-security.md)\.

1. \(Optional\) Under **Tags**, add one or more tags \(key\-value pairs\) to your pipeline\. For more information, see [Tagging Amazon OpenSearch Ingestion pipelines](tag-pipeline.md)\.

1. \(Optional\) Under **Log publishing options**, turn on pipeline log publishing to Amazon CloudWatch Logs\. We recommend that you enable log publishing so that you can more easily troubleshoot pipeline issues\. For more information, see [Monitoring pipeline logs](monitoring-pipeline-logs.md)\.

1. Choose **Next**\.

1. Review your pipeline configuration and choose **Create**\.

OpenSearch Ingestion runs an asynchronous process to build the pipeline\. Once the pipeline status is `Active`, you can start ingesting data\.

### AWS CLI<a name="create-pipeline-cli"></a>

The [create\-pipeline](https://docs.aws.amazon.com/cli/latest/reference/osis/create-pipeline.html) command accepts the pipeline configuration as a string or within a \.yaml file\. If you provide the configuration as a string, each new line must be escaped with `\n`\. For example, `"log-pipeline:\n source:\n http:\n processor:\n - grok:\n ...`

The following sample command creates a pipeline with the following configuration:
+ Minimum of 4 Ingestion OCUs, maximum of 10 Ingestion OCUs
+ Provisioned within a virtual private cloud \(VPC\)
+ Log publishing enabled

```
aws osis create-pipeline \
  --pipeline-name my-pipeline \
  --min-units 4 \
  --max-units 10 \
  --log-publishing-options  IsLoggingEnabled=true,CloudWatchLogDestination={LogGroup="MyLogGroup"} \
  --vpc-options SecurityGroupIds={sg-12345678,sg-9012345},SubnetIds=subnet-1212234567834asdf \
  --pipeline-configuration-body "file://pipeline-config.yaml"
```

OpenSearch Ingestion runs an asynchronous process to build the pipeline\. Once the pipeline status is `Active`, you can start ingesting data\. To check the status of the pipeline, use the [GetPipeline](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_osis_GetPipeline.html) command\.

### OpenSearch Ingestion API<a name="create-pipeline-api"></a>

To create an OpenSearch Ingestion pipeline using the OpenSearch Ingestion API, call the [CreatePipeline](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_osis_CreatePipeline.html) operation\.

After your pipeline is successfully created, you can configure your client and start ingesting data into your OpenSearch Service domain\. For more information, see [Working with Amazon OpenSearch Ingestion pipeline integrations](configure-client.md)\.

## Tracking the status of pipeline creation<a name="get-pipeline-progress"></a>

You can track the status of a pipeline as OpenSearch Ingestion provisions it and prepares it to ingest data\.

### Console<a name="get-pipeline-progress-console"></a>

After you initially create a pipeline, it goes through multiple stages as OpenSearch Ingestion prepares it to ingest data\. To view the various stages of pipeline creation, choose the pipeline name to see its **Pipeline settings** page\. Under **Status**, choose **View details**\.

A pipeline goes through the following stages before it's available to ingest data:
+ **Validation** – Validating pipeline configuration\. When this stage is complete, all validations have succeeded\.
+ **Create environment** – Preparing and provisioning resources\. When this stage is complete, the new pipeline environment has been created\.
+ **Deploy pipeline** – Deploying the pipeline\. When this stage is complete, the pipeline has been successfully deployed\.
+ **Check pipeline health** – Checking the health of the pipeline\. When this stage is complete, all health checks have passed\.
+ **Enable traffic** – Enabling the pipeline to ingest data\. When this stage is complete, you can start ingesting data into the pipeline\.

### CLI<a name="get-pipeline-progress-cli"></a>

Use the [get\-pipeline\-change\-progress](https://docs.aws.amazon.com/cli/latest/reference/osis/get-pipeline-change-progress.html) command to check the status of a pipeline\. The following AWS CLI request checks the status of a pipeline named `my-pipeline`:

```
aws osis get-pipeline-change-progress \
    --pipeline-name my-pipeline
```

**Response**:

```
{
   "ChangeProgressStatuses": {
      "ChangeProgressStages": [ 
         { 
            "Description": "Validating pipeline configuration",
            "LastUpdated": 1.671055851E9,
            "Name": "VALIDATION",
            "Status": "PENDING"
         }
      ],
      "StartTime": 1.671055851E9,
      "Status": "PROCESSING",
      "TotalNumberOfStages": 5
   }
}
```

### OpenSearch Ingestion API<a name="get-pipeline-progress-api"></a>

To track the status of pipeline creation using the OpenSearch Ingestion API, call the [GetPipelineChangeProgress](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_osis_GetPipelineChangeProgress.html) operation\.

## Using blueprints to create a pipeline<a name="pipeline-blueprint"></a>

Rather than creating a pipeline definition from scratch, you can use *configuration blueprints*, which are preconfigured YAML templates for common ingestion scenarios such as Trace Analytics or Apache logs\. Configuration blueprints help you easily provision pipelines without having to author a configuration from scratch\.

OpenSearch Ingestion includes the following blueprints:
+ **ALB access log pipeline** – Extracts data from ALB access logs\.
+ **Apache log pipeline** – Extracts data from Apache using grok patterns\.
+ **Apache log sampling** – Extracts data from Apache logs and routes them to various indexes\.
+ **CloudTrail log S3 pipeline** – Enriches AWS CloudTrail logs by pulling from an SQS queue\.
+ **ELB access log S3 pipeline** – Extracts data from ELB access logs using grok patterns\.
+ **Generic log pipeline** – Converts unstructured data to structured data using grok patterns and index mapping templates\.
+ **Log aggregation with conditional routing** – Aggregates various logs received in a time window and conditionally routes them to different indexes\.
+ **Log to metric anomaly pipeline** – Derives metrics from incoming logs and identifies anomalies\.
+ **Log to metric pipeline** – Derives metrics from incoming logs\.
+ **Security Lake S3 parquet OCSF pipeline** – Parses Open Cybersecurity Schema Framework \(OCSF\) parquet files from Security Lake\.
+ **S3 log pipeline** – Listens to S3 Amazon SQS notifications and pulls data from S3 buckets\.
+ **S3 select pipeline** – Performs selective download from an S3 bucket\.
+ **Trace Analytics pipeline** – Enriches spans and generates a service\-map \(dependency graph of services\)\.
+ **Trace to metric anomaly pipeline** – Derives RED \(rate, error, and duration\) metrics from traces and finds anomalies\.
+ **VPC flow log pipeline** – Extracts data from VPC flow logs using grok patterns\.
+ **WAF access log pipeline** – Parses Web Application Firewall \(WAF\) access logs and extracts data using grok\.

### Console<a name="pipeline-blueprint-console"></a>

**To use a pipeline blueprint**

1. Sign in to the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home)\.

1. Choose **Pipelines** in the left navigation pane and choose **Create pipeline**\.

1. Under **Pipeline configuration**, choose **Configuration blueprints**\.

1. Select a blueprint\. The pipeline configuration populates with a sub\-pipeline for the use case you selected\.

1. Review the commented\-out text which guides you through configuring the blueprint\.
**Important**  
The pipeline blueprint isn't valid as\-is\. You need to make some modifications, such as providing the AWS Region and the role ARN to use for authentication, otherwise pipeline validation will fail\.

### CLI<a name="pipeline-blueprint-cli"></a>

To get a list of all available blueprints using the AWS CLI, send a [list\-pipeline\-blueprints](https://docs.aws.amazon.com/cli/latest/reference/osis/list-pipeline-blueprints.html) request\.

```
aws osis list-pipeline-blueprints 
```

The request returns a list of all available blueprints\.

To get more detailed information about a specific blueprint, use the [get\-pipeline\-blueprint](https://docs.aws.amazon.com/cli/latest/reference/osis/get-pipeline-blueprint.html) command:

```
aws osis get-pipeline-blueprint --blueprint-name AWS-ApacheLogPipeline
```

This request returns the contents of the Apache log pipeline blueprint:

```
{
   "Blueprint":{
      "PipelineConfigurationBody":"###\n  # Limitations: https://docs.aws.amazon.com/opensearch-service/latest/ingestion/ingestion.html#ingestion-limitations\n###\n###\n  # apache-log-pipeline:\n    # This pipeline receives logs via http (e.g. FluentBit), extracts important values from the logs by matching\n    # the value in the 'log' key against the grok common Apache log pattern. The grokked logs are then sent\n    # to OpenSearch to an index named 'logs'\n###\n\nversion: \"2\"\napache-log-pipeline:\n  source:\n    http:\n      # Provide the path for ingestion. ${pipelineName} will be replaced with pipeline name configured for this pipeline.\n      # In this case it would be \"/apache-log-pipeline/logs\". This will be the FluentBit output URI value.\n      path: \"/${pipelineName}/logs\"\n  processor:\n    - grok:\n        match:\n          log: [ \"%{COMMONAPACHELOG_DATATYPED}\" ]\n  sink:\n    - opensearch:\n        # Provide an AWS OpenSearch Service domain endpoint\n        # hosts: [ \"https://search-mydomain-1a2a3a4a5a6a7a8a9a0a9a8a7a.us-east-1.es.amazonaws.com\" ]\n        aws:\n          # Provide a Role ARN with access to the domain. This role should have a trust relationship with osis-pipelines.amazonaws.com\n          # sts_role_arn: \"arn:aws:iam::123456789012:role/Example-Role\"\n          # Provide the region of the domain.\n          # region: \"us-east-1\"\n          # Enable the 'serverless' flag if the sink is an Amazon OpenSearch Serverless collection\n          # serverless: true\n        index: \"logs\"\n        # Enable the S3 DLQ to capture any failed requests in an S3 bucket\n        # dlq:\n          # s3:\n            # Provide an S3 bucket\n            # bucket: \"your-dlq-bucket-name\"\n            # Provide a key path prefix for the failed requests\n            # key_path_prefix: \"${pipelineName}/logs/dlq\"\n            # Provide the region of the bucket.\n            # region: \"us-east-1\"\n            # Provide a Role ARN with access to the bucket. This role should have a trust relationship with osis-pipelines.amazonaws.com\n            # sts_role_arn: \"arn:aws:iam::123456789012:role/Example-Role\"\n",
      "BlueprintName":"AWS-ApacheLogPipeline"
   }
}
```

### OpenSearch Ingestion API<a name="pipeline-blueprint-api"></a>

To get information about pipeline blueprints using the OpenSearch Ingestion API, use the the [ListPipelineBlueprints](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_osis_ListPipelineBlueprints.html) and [GetPipelineBlueprint](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_osis_GetPipelineBlueprint.html) operations\.