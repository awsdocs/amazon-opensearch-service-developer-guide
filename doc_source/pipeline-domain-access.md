# Allowing Amazon OpenSearch Ingestion pipelines to write to domains<a name="pipeline-domain-access"></a>

An Amazon OpenSearch Ingestion pipeline needs permission to write to the OpenSearch Service domain that is configured as its sink\. To provide access, you configure an AWS Identity and Access Management \(IAM\) role with a restrictive permissions policy that limits access to the domain that a pipeline is sending data to\. For example, you might want to limit an ingestion pipeline to only the domain and indexes that are required to support its use case\.

Before you specify the role in your pipeline configuration, you must configure it with an appropriate trust relationship, and then grant it access to the domain within the domain access policy\.

**Topics**
+ [Step 1: Create a pipeline role](#pipeline-access-configure)
+ [Step 2: Include the pipeline role in the domain access policy](#pipeline-access-domain)
+ [Step 3: Specify the role in the pipeline configuration](#pipeline-access-add-role)

## Step 1: Create a pipeline role<a name="pipeline-access-configure"></a>

The role that you specify in the **sts\_role\_arn** parameter of a pipeline configuration must have an attached permissions policy that allows it to send data to the domain sink\. It must also have a trust relationship that allows OpenSearch Ingestion to assume the role\. For instructions on how to attach a policy to a role, see [Adding IAM identity permissions](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_manage-attach-detach.html#add-policies-console) in the *IAM User Guide*\.

The following sample policy demonstrates the [least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) that you can provide in a pipeline configuration's **sts\_role\_arn** role for it to write to a single domain:

```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": "es:DescribeDomain",
            "Resource": "arn:aws:es:*:{your-account-id}:domain/*"
        },
        {
            "Effect": "Allow",
            "Action": "es:ESHttp*",
            "Resource": "arn:aws:es:*:{your-account-id}:domain/ingestion-domain/*"
        }
    ]
}
```

If you plan to reuse the role to write to multiple domains, you can make the policy more broad by replacing the domain name with a wildcard character \(`*`\)\.

The role must have the following [trust relationship](https://docs.aws.amazon.com/IAM/latest/UserGuide/roles-managingrole-editing-console.html#roles-managingrole_edit-trust-policy), which allows OpenSearch Ingestion to assume the pipeline role:

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

## Step 2: Include the pipeline role in the domain access policy<a name="pipeline-access-domain"></a>

In order for a pipeline to write data to a domain, the domain must have a [domain\-level access policy](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/ac.html#ac-types-resource) that allows the **sts\_role\_arn** pipeline role to access it\.

The following sample domain access policy allows the pipeline role named `pipeline-role`, which you created in the previous step, to write data to the domain named `ingestion-domain`:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::{your-account-id}:role/pipeline-role"
      },
      "Action": ["es:DescribeDomain", "es:ESHttp*"],
      "Resource": "arn:aws:es:us-east-1:{your-account-id}:domain/ingestion-domain/*"
    }
  ]
}
```

### Fine\-grained access control<a name="pipeline-access-domain-fgac"></a>

If your domain uses [fine\-grained access control](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/fgac.html) for authentication, there are extra steps you need to take to provide your pipeline access to a domain\. The steps differ depending on your domain configuration:

**Scenario 1: Different master role and pipeline role** – If you're using an IAM Amazon Resource Name \(ARN\) as the master user and it's *different* than the pipeline role \(`sts_role_arn`\), you need to map the pipeline role to the OpenSearch `all_access` backend role\. This essentially adds the pipeline role as an additional master user\. For more information, see [Additional master users](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/fgac.html#fgac-more-masters)\.

**Scenario 2: Master user in the internal user database** – If your domain uses a master user in the internal user database and HTTP basic authentication for OpenSearch Dashboards, you can't pass the master username and password directly into the pipeline configuration\. Instead, you need to map the pipeline role \(`sts_role_arn`\) to the OpenSearch `all_access` backend role\. This essentially adds the pipeline role as an additional master user\. For more information, see [Additional master users](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/fgac.html#fgac-more-masters)\.

**Scenario 3: Same master role and pipeline role \(uncommon\)** – If you're using an IAM ARN as the master user, and it's the same ARN that you're using as the pipeline role \(`sts_role_arn`\), you don't need to take any further action\. The pipeline has the required permissions to write to the domain\. This scenario is uncommon because most environments use an admin role or some other role as the master role\.

The following image shows how to map the pipeline role to a backend role:

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/ingestion-fgac.png)

## Step 3: Specify the role in the pipeline configuration<a name="pipeline-access-add-role"></a>

In order to successfully create a pipeline, you must specify the pipeline role that you created in step 1 as the **sts\_role\_arn** parameter in your pipeline configuration\. The pipeline assumes this role in order to sign requests to the OpenSearch Service domain sink\.

In the `sts_role_arn` field, specify the ARN of the IAM pipeline role:

```
version: "2"
log-pipeline:
  source:
    http:
      path: "/${pipelineName}/logs"
  processor:
    - grok:
        match:
          log: [ "%{COMMONAPACHELOG}" ]
  sink:
    - opensearch:
        hosts: [ "https://search-ingestion-domain.us-east-1.es.amazonaws.com" ]
        index: "my-index"
        aws:
          region: "us-east-1"
          sts_role_arn: "arn:aws:iam::{your-account-id}:role/pipeline-role"
```

For a full reference of required and unsupported parameters, see [Supported plugins and options for Amazon OpenSearch Ingestion pipelines](pipeline-config-reference.md)\.