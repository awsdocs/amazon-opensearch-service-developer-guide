# Working with Amazon OpenSearch Ingestion pipeline integrations<a name="configure-client"></a>

In order to successfully ingest data into an Amazon OpenSearch Ingestion pipeline, you must configure your client application \(the *source*\) to send data to the pipeline endpoint\. Your source might be clients like Fluent Bit logs, the OpenTelemetry Collector, or a simple S3 bucket\. The exact configuration differs for each client\.

The important differences during source configuration \(compared to sending data directly to an OpenSearch Service domain or OpenSearch Serverless collection\) are the AWS service name \(`osis`\) and the host endpoint, which must be the pipeline endpoint\.

**Topics**
+ [Constructing the ingestion endpoint](#configure-client-endpoint)
+ [Creating an ingestion role](#configure-client-auth)
+ [Amazon Managed Streaming for Apache Kafka](configure-client-msk.md)
+ [Amazon S3](configure-client-s3.md)
+ [Amazon Security Lake](configure-client-security-lake.md)
+ [Fluent Bit](configure-client-fluentbit.md)
+ [OpenTelemetry Collector](configure-client-otel.md)
+ [Next steps](#configure-client-next)

## Constructing the ingestion endpoint<a name="configure-client-endpoint"></a>

In order to ingest data into a pipeline, send it to the ingestion endpoint\. To locate the ingestion URL, navigate to the **Pipeline settings** page and copy the **Ingestion URL**:

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/pipeline-endpoint.png)

To construct the full ingestion endpoint for pull\-based sources like [OTel trace](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-trace/) and [OTel metrics](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-metrics-source/), add the ingestion path from your pipeline configuration to the ingestion URL\.

For example, say that your pipeline configuration has the following ingestion path:

```
entry-pipeline:
  source:
    http:
      path: "/my/test_path"
```

The full ingestion endpoint, which you specify in your client configuration, will take the following format: `https://ingestion-pipeline-abcdefg.us-west-2.osis.amazonaws.com/my/test_path`\.

For more information, see [Specifying the ingestion path](creating-pipeline.md#pipeline-path)\.

## Creating an ingestion role<a name="configure-client-auth"></a>

All requests to OpenSearch Ingestion must be signed with [Signature Version 4](https://docs.aws.amazon.com/general/latest/gr/signature-version-4.html)\. At minimum, the role that signs the request must be granted permission for the `osis:Ingest` action, which allows it to send data to an OpenSearch Ingestion pipeline\.

For example, the following AWS Identity and Access Management \(IAM\) policy allows the corresponding role to send data to a single pipeline:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "osis:Ingest",
      "Resource": "arn:aws:osis:us-east-1:{account-id}:pipeline/pipeline-name"
    }
  ]
}
```

**Note**  
To use the role for *all* pipelines, replace the ARN in the `Resource` element with a wildcard \(\*\)\.

### Providing cross\-account ingestion access<a name="configure-client-cross-account"></a>

**Note**  
You can only provide cross\-account ingestion access for public pipelines, not VPC pipelines\.

You might need to ingest data into a pipeline from a different AWS account, such as an account that houses your source application\. If the principal that is writing to a pipeline is in a different account than the pipeline itself, you need to configure the principal to trust another IAM role to ingest data into the pipeline\.

**To configure cross\-account ingestion permissions**

1. Create the ingestion role with `osis:Ingest` permission \(described in the previous section\) within the same AWS account as the pipeline\. For instructions, see [Creating IAM roles](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create.html)\.

1. Attach a [trust policy](https://docs.aws.amazon.com/IAM/latest/UserGuide/roles-managingrole-editing-console.html#roles-managingrole_edit-trust-policy) to the ingestion role that allows a principal in another account to assume it:

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

1. In the other account, configure your client application \(for example, Fluent Bit\) to assume the ingestion role\. In order for this to work, the application account must grant permissions to the application user or role to assume the ingestion role\.

   The following example identity\-based policy allows the attached principal to assume `ingestion-role` from the pipeline account:

   ```
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Action": "sts:AssumeRole",
         "Resource": "arn:aws:iam::{account-id}:role/ingestion-role"
       }
     ]
   }
   ```

The client application can then use the [AssumeRole](https://docs.aws.amazon.com/STS/latest/APIReference/API_AssumeRole.html) operation to assume `ingestion-role` and ingest data into the associated pipeline\.

## Next steps<a name="configure-client-next"></a>

After you export your data to a pipeline, you can [query it](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/searching.html) from the OpenSearch Service domain that is configured as a sink for the pipeline\. The following resources can help you get started:
+ [Observability in Amazon OpenSearch Service](observability.md)
+ [Trace Analytics for Amazon OpenSearch Service](trace-analytics.md)
+ [Querying Amazon OpenSearch Service data using Piped Processing Language](ppl-support.md)