# Allowing Amazon OpenSearch Ingestion pipelines to write to collections<a name="pipeline-collection-access"></a>

An Amazon OpenSearch Ingestion pipeline needs permission to write to the OpenSearch Serverless collection that is configured as its sink\. To provide access, you configure an AWS Identity and Access Management \(IAM\) role with a restrictive permissions policy that limits access to the collection that a pipeline is sending data to\.

Before you specify the role in your pipeline configuration, you must configure it with an appropriate trust relationship, and then grant it data access permissions to the collection indexes\.

**Topics**
+ [Limitations](#pipeline-collection-access-limitations)
+ [Step 1: Create a pipeline role](#pipeline-collection-access-configure)
+ [Step 2: Create a collection](#pipeline-access-collection)
+ [Step 3: Create a pipeline](#pipeline-access-add-role-serverless)

## Limitations<a name="pipeline-collection-access-limitations"></a>

The following limitations apply for pipelines that write to OpenSearch Serverless collections:
+ The [OTel trace group](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/otel-trace-group/) processor doesn't currently work with OpenSearch Serverless collection sinks\.
+ The collection sink must have public [network access](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-network.html) for both the OpenSearch endpoint and OpenSearch Dashboards\. A pipeline can't write to a collection that must be accessed through OpenSearch Serverless–managed VPC endpoints\.
+ Currently, OpenSearch Ingestion only supports the legacy `_template` operation, while OpenSearch Serverless supports the composable `_index_template` operation\. Therefore, if your pipeline configuration includes the `index_type` option, it must be set to `management_disabled`\.

## Step 1: Create a pipeline role<a name="pipeline-collection-access-configure"></a>

The role that you specify in the **sts\_role\_arn** parameter of a pipeline configuration must have an attached permissions policy that allows it to send data to the collection sink\. It must also have a trust relationship that allows OpenSearch Ingestion to assume the role\. For instructions on how to attach a policy to a role, see [Adding IAM identity permissions](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_manage-attach-detach.html#add-policies-console) in the *IAM User Guide*\.

The following sample policy demonstrates the [least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) that you can provide in a pipeline configuration's **sts\_role\_arn** role for it to write to collections:

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

The role must have the following [trust relationship](https://docs.aws.amazon.com/IAM/latest/UserGuide/roles-managingrole-editing-console.html#roles-managingrole_edit-trust-policy), which allows OpenSearch Ingestion to assume it:

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

In addition, we recommend that you add the `aws:SourceAccount` and `aws:SourceArn` condition keys to the policy to protect yourself against the [confused deputy problem](https://docs.aws.amazon.com/IAM/latest/UserGuide/confused-deputy.html)\. The source account is the owner of the pipeline\.

For example, you could add the following condition block to the policy:

```
"Condition": {
    "StringEquals": {
        "aws:SourceAccount": "{your-account-id}"
    },
    "ArnLike": {
        "aws:SourceArn": "arn:aws:osis:{region}:{your-account-id}:pipeline/*"
    }
}
```

## Step 2: Create a collection<a name="pipeline-access-collection"></a>

Create an OpenSearch Serverless collection with the following settings:
+ Public [network access](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-network.html) to both the OpenSearch endpoint and OpenSearch Dashboards\.
+ The following [data access policy](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-data-access.html), which grants the required permissions to the pipeline role:

  ```
  [
    {
      "Rules": [
        {
          "Resource": [
            "index/collection-name/*"
          ],
          "Permission": [
            "aoss:CreateIndex",
            "aoss:UpdateIndex",
            "aoss:DescribeIndex",
            "aoss:WriteDocument"
          ],
          "ResourceType": "index"
        }
      ],
      "Principal": [
        "arn:aws:iam::{account-id}:role/pipeline-role"
      ],
      "Description": "Pipeline role access"
    }
  ]
  ```
**Note**  
In the `Principal` element, specify the Amazon Resource Name \(ARN\) of the pipeline role that you created in the previous step\.

For instructions to create a collection, see [Creating collections](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-manage.html#serverless-create)\.

## Step 3: Create a pipeline<a name="pipeline-access-add-role-serverless"></a>

Finally, create a pipeline in which you specify the pipeline role\. The pipeline assumes this role in order to sign requests to the OpenSearch Serverless collection sink\.

Make sure to do the following:
+ For the `hosts` option, specify the endpoint of the collection that you created in step 2\.
+ For the `sts_role_arn` option, specify the Amazon Resource Name \(ARN\) of the pipeline role that you created in step 1\.
+ Set the `serverless` option to `true`\.

```
version: "2"
log-pipeline:
  source:
    http:
        path: "/log/ingest"
  processor:
    - date:
        from_time_received: true
        destination: "@timestamp"
  sink:
    - opensearch:
        hosts: [ "https://{collection-id}.us-east-1.aoss.amazonaws.com" ]
        index: "my-index"
        aws:
          serverless: true
          region: "us-east-1"
          sts_role_arn: "arn:aws:iam::{account-id}:role/pipeline-role"
```

For a full reference of required and unsupported parameters, see [Supported plugins and options for Amazon OpenSearch Ingestion pipelines](pipeline-config-reference.md)\.