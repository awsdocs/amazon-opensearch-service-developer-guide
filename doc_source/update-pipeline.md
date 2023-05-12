# Updating Amazon OpenSearch Ingestion pipelines<a name="update-pipeline"></a>

You can update Amazon OpenSearch Ingestion pipelines using the AWS Management Console, the AWS CLI, or the OpenSearch Ingestion API\. OpenSearch Ingestion initiates a blue/green deployment when you update a pipeline's YAML configuration\. For more information, see [Blue/green deployments for pipeline updates](#pipeline-bg)\.

**Topics**
+ [Considerations](#update-pipeline-permissions)
+ [Permissions required](#update-pipeline-permissions)
+ [Updating pipelines](#update-pipeline-steps)
+ [Blue/green deployments for pipeline updates](#pipeline-bg)

## Considerations<a name="update-pipeline-permissions"></a>

Consider the following when you update a pipeline:
+ You can edit a pipeline's capacity limits, log publishing options, and YAML configuration\. You can't edit its name or network settings\.
+ If your pipeline writes to a VPC domain sink, you can't go back and change the sink to a different VPC domain after the pipeline is created\. You must delete and recreate the pipeline with the new sink\. You can still switch the sink from a VPC domain to a public domain, from a public domain to a VPC domain, or from a public domain to another public domain\.
+ You can switch the pipeline sink at any time between a public OpenSearch Service domain and an OpenSearch Serverless collection\.
+ When you update a pipeline's YAML configuration, OpenSearch Ingestion initiates a blue/green deployment\. For more information, see [Blue/green deployments for pipeline updates](#pipeline-bg)\.
+ When you update a pipeline's YAML configuration, OpenSearch Ingestion can automatically upgrade your pipeline to the latest supported minor version of the major version of Data Prepper that's specified in the pipeline configuration\. This process keeps your pipeline up to date with the latest bug fixes and performance improvements\.
+ You can still make updates to your pipeline when it's stopped\. 

## Permissions required<a name="update-pipeline-permissions"></a>

OpenSearch Ingestion uses the following IAM permissions for updating pipelines:
+ `osis:UpdatePipeline` – Update a pipeline\.
+ `osis:ValidatePipeline` – Check whether a pipeline configuration is valid\.
+ `iam:PassRole` – Pass the pipeline role to OpenSearch Ingestion so that it can write data to the domain\. This permission is only required if you're updating the pipeline YAML configuration, not if you're modifying other settings such as log publishing or capacity limits\.

For example, the following policy grants permission to update a pipeline:

```
{
   "Version":"2012-10-17",
   "Statement":[
      {
         "Effect":"Allow",
         "Resource":"*",
         "Action":[
            "osis:UpdatePipeline",
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

## Updating pipelines<a name="update-pipeline-steps"></a>

You can update Amazon OpenSearch Ingestion pipelines using the AWS Management Console, the AWS CLI, or the OpenSearch Ingestion API\. 

### Console<a name="update-pipeline-console"></a>

**To update a pipeline**

1. Sign in to the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home)\.

1. Choose **Pipelines** in the left navigation pane\.

1. Choose a pipeline to open its settings\. You can edit a pipeline's capacity limits, log publishing options, and YAML configuration\. You can't edit its name or network settings\.

1. When you're done making changes, choose **Save**\.

### CLI<a name="update-pipeline-cli"></a>

To update a pipeline using the AWS CLI, send an [update\-pipeline](https://docs.aws.amazon.com/cli/latest/reference/osis/update-pipeline.html) request\. The following sample request uploads a new configuration file and updates the minimum and maximum capacity values:

```
aws osis update-pipeline \
  --pipeline-name "my-pipeline" \
  --pipline-configuration-body "file://new-pipeline-config.yaml" \
  --min-units 11 \
  --max-units 18
```

### OpenSearch Ingestion API<a name="update-pipeline-api"></a>

To update an OpenSearch Ingestion pipeline using the OpenSearch Ingestion API, call the [UpdatePipeline](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_UpdatePipeline.html) operation\.

## Blue/green deployments for pipeline updates<a name="pipeline-bg"></a>

OpenSearch Ingestion initiates a *blue/green* deployment process when you update a pipeline's YAML configuration\.

Blue/green refers to the practice of creating a new environment for pipeline updates and routing traffic to the new environment after those updates are complete\. The practice minimizes downtime and maintains the original environment in the event that deployment to the new environment is unsuccessful\. Blue/green deployments themselves don't have any performance impact, but performance might change if your pipeline configuration changes in a way that alters performance\.

OpenSearch Ingestion blocks auto\-scaling during blue/green deployments\. You continue to be charged only for traffic to the old pipeline until it's redirected to the new pipeline\. Once traffic has been redirected, you're only charged for the new pipeline\. You're never charged for two pipelines simultaneously\.

When you update a pipeline's YAML configuration file, OpenSearch Ingestion can automatically upgrade your pipeline to the latest supported minor version of the major version of Data Prepper that's specified in the pipeline configuration\. For example, you might have `version: "2"` in your pipeline configuration, and OpenSearch Ingestion initially provisioned the pipeline with version 2\.1\.0\. When support for version 2\.1\.1 is added, and you make a change to your pipeline configuration, OpenSearch Ingestion upgrades your pipeline to version 2\.1\.1\.

This process keeps your pipeline up to date with the latest bug fixes and performance improvements\. OpenSearch Ingestion can't update the major version of your pipeline unless you manually change the `version` option within the pipeline configuration\.