# Tutorial: Ingesting data into a collection using Amazon OpenSearch Ingestion<a name="osis-serverless-get-started"></a>

This tutorial shows you how to use Amazon OpenSearch Ingestion to configure a simple pipeline and ingest data into an Amazon OpenSearch Serverless collection\. A *pipeline* is a resource that OpenSearch Ingestion provisions and manages\. You can use a pipeline to filter, enrich, transform, normalize, and aggregate data for downstream analytics and visualization in OpenSearch Service\.

For a tutorial that demonstrates how to ingest data into a provisioned OpenSearch Service *domain*, see [Tutorial: Ingesting data into a domain using Amazon OpenSearch Ingestion](osis-get-started.md)\.

You'll complete the following steps in this tutorial:

1. [Create the pipeline role](#osis-serverless-get-started-role)\.

1. [Create a collection](#osis-serverless-get-started-access)\.

1. [Create a pipeline](#osis-serverless-get-started-pipeline)\.

1. [Ingest some sample data](#osis-serverless-get-started-ingest)\.

Within the tutorial, you'll create the following resources:
+ A pipeline named `ingestion-pipeline-serverless`
+ A collection named `ingestion-collection` that the pipeline will write to
+ An IAM role named `PipelineRole` that the pipeline will assume in order to write to the collection

## Required permissions<a name="osis-serverless-get-started-permissions"></a>

To complete this tutorial, you must have the correct IAM permissions\. Your user or role must have an attached [identity\-based policy](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/security-iam-serverless.html#security-iam-serverless-id-based-policies) with the following minimum permissions\. These permissions allow you to create a pipeline role \(`iam:Create*`\), create or modify a collection \(`aoss:*`\), and work with pipelines \(`osis:*`\)\.

In addition, the `iam:PassRole` permission is required on the pipeline role resource\. This permission allows you to pass the pipeline role to OpenSearch Ingestion so that it can write data to the collection\.

```
{
   "Version":"2012-10-17",
   "Statement":[
      {
         "Effect":"Allow",
         "Resource":"*",
         "Action":[
            "osis:*",
            "iam:Create*",
            "aoss:*"
         ]
      },
      {
         "Resource":[
            "arn:aws:iam::{your-account-id}:role/PipelineRole"
         ],
         "Effect":"Allow",
         "Action":[
            "iam:PassRole"
         ]
      }
   ]
}
```

## Step 1: Create the pipeline role<a name="osis-serverless-get-started-role"></a>

First, create a role that the pipeline will assume in order to access the OpenSearch Serverless collection sink\. You'll include this role within the pipeline configuration later in this tutorial\.

**To create the pipeline role**

1. Open the AWS Identity and Access Management console at [https://console\.aws\.amazon\.com/iamv2/](https://console.aws.amazon.com/iamv2/ )\.

1. Choose **Policies**, and then choose **Create policy**\.

1. Select **JSON** and paste the following policy into the editor\.

   ```
   {
       "Version": "2012-10-17",
       "Statement": [
           {
               "Effect": "Allow",
               "Action": [
                   "aoss:BatchGetCollection"
               ],
               "Resource": "*"
           }
       ]
   }
   ```

1. Choose **Next**, choose **Next**, and name your policy **collection\-pipeline\-policy**\.

1. Choose **Create policy**\.

1. Next, create a role and attach the policy to it\. Choose **Roles**, and then choose **Create role**\.

1. Choose **Custom trust policy** and paste the following policy into the editor:

   ```
   {
      "Version":"2012-10-17",
      "Statement":[
         {
            "Effect":"Allow",
            "Principal":{
               "Service":"osis-pipelines.amazonaws.com"
            },
            "Action":"sts:AssumeRole"
         }
      ]
   }
   ```

1. Choose **Next**\. Then search for and select **collection\-pipeline\-policy** \(which you just created\)\.

1. Choose **Next** and name the role **PipelineRole**\.

1. Choose **Create role**\.

Remember the Amazon Resource Name \(ARN\) of the role \(for example, `arn:aws:iam::{your-account-id}:role/PipelineRole`\)\. You'll need it when you create your pipeline\.

## Step 2: Create a collection<a name="osis-serverless-get-started-access"></a>

Next, create a collection to ingest data into\. We'll name the collection `ingestion-collection`\.

1. Navigate to the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home)\.

1. Choose **Collections** from the left navigation and choose **Create collection**\.

1. Name the collection **ingestion\-collection**\.

1. Under **Network access settings**, change the access type to **Public**\.

1. Keep all other settings as their defaults and choose **Next**\.

1. For **Definition method**, choose **JSON** and paste the following policy into the editor\. This policy does two things:
   + Allows the pipeline role to write to the collection\.
   + Allows you to *read* from the collection\. Later, after you ingest some sample data into the pipeline, you'll query the collection to ensure that the data was successfully ingested and written to the index\.

     ```
     [
       {
         "Rules": [
           {
             "Resource": [
               "index/ingestion-collection/*"
             ],
             "Permission": [
               "aoss:CreateIndex",
               "aoss:UpdateIndex",
               "aoss:DescribeIndex",
               "aoss:ReadDocument",
               "aoss:WriteDocument"
             ],
             "ResourceType": "index"
           }
         ],
         "Principal": [
           "arn:aws:iam::{your-account-id}:role/PipelineRole",
           "arn:aws:iam::{your-account-id}:role/Admin"
         ],
         "Description": "Rule 1"
       }
     ]
     ```

1. Replace the `Principal` elements\. The first principal should specify the pipeline role that you created\. The second should specify a user or role that you can use to query the collection later\.

1. Choose **Next**\. Name the access policy **pipeline\-domain\-access** and choose **Next** again\.

1. Review your collection configuration and choose **Submit**\.

When the collection is active, note the OpenSearch endpoint under **Endpoint** \(for example, `https://{collection-id}.us-east-1.aoss.amazonaws.com`\)\. You'll need it when you create your pipeline\.

## Step 3: Create a pipeline<a name="osis-serverless-get-started-pipeline"></a>

Now that you have a collection and a role with the appropriate access rights, you can create a pipeline\.

**To create a pipeline**

1. Within the Amazon OpenSearch Service console, choose **Pipelines** from the left navigation pane\.

1. Choose **Create pipeline**\.

1. Name the pipeline **serverless\-ingestion** and keep the capacity settings as their defaults\.

1. In this tutorial, we'll create a simple sub\-pipeline called `log-pipeline` that uses the [HTTP source](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/http-source/) plugin\. The plugin accepts log data in a JSON array format\. We'll specify a single OpenSearch Serverless collection as the sink, and ingest all data into the `my_logs` index\.

   Under **Pipeline configuration**, paste the following YAML configuration into the editor:

   ```
   version: "2"
   log-pipeline:
     source:
       http:
         path: "/${pipelineName}/test_ingestion_path"
     processor:
       - date:
           from_time_received: true
           destination: "@timestamp"
     sink:
       - opensearch:
           hosts: [ "https://{collection-id}.us-east-1.aoss.amazonaws.com" ]
           index: "my_logs"
           aws:
             sts_role_arn: "arn:aws:iam::{your-account-id}:role/PipelineRole"
             region: "us-east-1"
             serverless: true
   ```

1. Replace the `hosts` URL with the endpoint of the collection that you created in the previous section\. Replace the `sts_role_arn` parameter with the ARN of `PipelineRole`\. Optionally, modify the `region`\.

1. Choose **Validate pipeline** and make sure that the validation succeeds\.

1. For simplicity in this tutorial, we'll configure public access for the pipeline\. Under **Network**, choose **Public access**\.

   For information about configuring VPC access, see [Securing Amazon OpenSearch Ingestion pipelines within a VPC](pipeline-security.md)\.

1. Keep log publishing enabled in case you encounter any issues while completing this tutorial\. For more information, see [Monitoring pipeline logs](monitoring-pipeline-logs.md)\.

   Specify the following log group name: `/aws/vendedlogs/OpenSearchIngestion/serverless-ingestion/audit-logs`

1. Choose **Next**\. Review your pipeline configuration and choose **Create pipeline**\. The pipeline takes 5–10 minutes to become active\.

## Step 4: Ingest some sample data<a name="osis-serverless-get-started-ingest"></a>

When the pipeline status is `Active`, you can start ingesting data into it\. You must sign all HTTP requests to the pipeline using [Signature Version 4](https://docs.aws.amazon.com/general/latest/gr/signature-version-4.html)\. Use an HTTP tool such as [Postman](https://www.getpostman.com/) or [awscurl](https://github.com/okigan/awscurl) to send some data to the pipeline\. As with indexing data directly to a collection, ingesting data into a pipeline always requires either an IAM role or an [IAM access key and secret key](https://docs.aws.amazon.com/powershell/latest/userguide/pstools-appendix-sign-up.html)\. 

**Note**  
The principal signing the request must have the `osis:Ingest` IAM permission\.

First, get the ingestion URL from the **Pipeline settings** page:

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/pipeline-endpoint.png)

Then, ingest some sample data\. The following sample request uses [awscurl](https://github.com/okigan/awscurl) to send a single log file to the `my_logs` index:

```
awscurl --service osis --region us-east-1 \
    -X POST \
    -H "Content-Type: application/json" \
    -d '[{"time":"2014-08-11T11:40:13+00:00","remote_addr":"122.226.223.69","status":"404","request":"GET http://www.k2proxy.com//hello.html HTTP/1.1","http_user_agent":"Mozilla/4.0 (compatible; WOW64; SLCC2;)"}]' \
    https://{pipeline-endpoint}.us-east-1.osis.amazonaws.com/log-pipeline/test_ingestion_path
```

You should see a `200 OK` response\.

Now, query the `my_logs` index to ensure that the log entry was successfully ingested:

```
awscurl --service aoss --region us-east-1 \
     -X GET \
     https://{collection-id}.us-east-1.aoss.amazonaws.com/my_logs/_search | json_pp
```

**Sample response**:

```
{
   "took":348,
   "timed_out":false,
   "_shards":{
      "total":0,
      "successful":0,
      "skipped":0,
      "failed":0
   },
   "hits":{
      "total":{
         "value":1,
         "relation":"eq"
      },
      "max_score":1.0,
      "hits":[
         {
            "_index":"my_logs",
            "_id":"1%3A0%3ARJgDvIcBTy5m12xrKE-y",
            "_score":1.0,
            "_source":{
               "time":"2014-08-11T11:40:13+00:00",
               "remote_addr":"122.226.223.69",
               "status":"404",
               "request":"GET http://www.k2proxy.com//hello.html HTTP/1.1",
               "http_user_agent":"Mozilla/4.0 (compatible; WOW64; SLCC2;)",
               "@timestamp":"2023-04-26T05:22:16.204Z"
            }
         }
      ]
   }
}
```

## Related resources<a name="osis-serverless-get-started-next"></a>

This tutorial presented a simple use case of ingesting a single document over HTTP\. In production scenarios, you'll configure your client applications \(such as Fluent Bit, Kubernetes, or the OpenTelemetry Collector\) to send data to one or more pipelines\. Your pipelines will likely be more complex than the simple example in this tutorial\.

To get started configuring your clients and ingesting data, see the following resources:
+ [Creating and managing pipelines](creating-pipeline.md#create-pipeline)
+ [Configuring your clients to send data to OpenSearch Ingestion](configure-client.md)
+ [Data Prepper documentation](https://opensearch.org/docs/latest/clients/data-prepper/index/)