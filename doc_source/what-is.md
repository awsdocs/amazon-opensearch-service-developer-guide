# What is Amazon OpenSearch Service?<a name="what-is"></a>

Amazon OpenSearch Service is a managed service that makes it easy to deploy, operate, and scale OpenSearch clusters in the AWS Cloud\. Amazon OpenSearch Service is the successor to Amazon Elasticsearch Service and supports OpenSearch and legacy Elasticsearch OSS \(up to 7\.10, the final open source version of the software\)\. When you create a cluster, you have the option of which search engine to use\. For information about what changed with the recent service rename, and the actions you might need to take, see [Amazon OpenSearch Service \- Summary of changes](rename.md)\.

*OpenSearch* is a fully open\-source search and analytics engine for use cases such as log analytics, real\-time application monitoring, and clickstream analysis\. For more information, see the [OpenSearch documentation](https://opensearch.org/docs/)\.

*OpenSearch Service* provisions all the resources for your cluster and launches it\. It also automatically detects and replaces failed OpenSearch Service nodes, reducing the overhead associated with self\-managed infrastructures\. You can scale your cluster with a single API call or a few clicks in the console\.

To get started using OpenSearch Service, you create an OpenSearch Service *cluster*\. Each EC2 instance in the cluster acts as one OpenSearch Service node\.

You can use the OpenSearch Service console to set up and configure a domain in minutes\. If you prefer programmatic access, you can use the [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/) or the [AWS SDKs](http://aws.amazon.com/code)\.

## Features of Amazon OpenSearch Service<a name="what-is-features"></a>

OpenSearch Service includes the following features:

**Scale**
+ Numerous configurations of CPU, memory, and storage capacity known as *instance types*, including cost\-effective Graviton instances
+ Up to 3 PB of attached storage
+ Cost\-effective [UltraWarm](ultrawarm.md) and [cold storage](cold-storage.md) for read\-only data

**Security**
+ AWS Identity and Access Management \(IAM\) access control
+ Easy integration with Amazon VPC and VPC security groups
+ Encryption of data at rest and node\-to\-node encryption
+ Amazon Cognito, HTTP basic, or SAML authentication for OpenSearch Dashboards
+ Index\-level, document\-level, and field\-level security
+ Audit logs
+ Dashboards multi\-tenancy

**Stability**
+ Numerous geographical locations for your resources, known as *Regions* and *Availability Zones*
+ Node allocation across two or three Availability Zones in the same AWS Region, known as *Multi\-AZ*
+ Dedicated master nodes to offload cluster management tasks
+ Automated snapshots to back up and restore OpenSearch Service domains

**Flexibility**
+ SQL support for integration with business intelligence \(BI\) applications
+ Custom packages to improve search results

**Integration with popular services**
+ Data visualization using OpenSearch Dashboards
+ Integration with Amazon CloudWatch for monitoring OpenSearch Service domain metrics and setting alarms
+ Integration with AWS CloudTrail for auditing configuration API calls to OpenSearch Service domains
+ Integration with Amazon S3, Amazon Kinesis, and Amazon DynamoDB for loading streaming data into OpenSearch Service
+ Alerts from Amazon SNS when your data exceeds certain thresholds

## Supported versions of OpenSearch and Elasticsearch<a name="choosing-version"></a>

OpenSearch Service currently supports the following OpenSearch versions:
+ 1\.1, 1\.0

OpenSearch Service also supports the following legacy Elasticsearch OSS versions:
+ 7\.10, 7\.9, 7\.8, 7\.7, 7\.4, 7\.1
+ 6\.8, 6\.7, 6\.5, 6\.4, 6\.3, 6\.2, 6\.0
+ 5\.6, 5\.5, 5\.3, 5\.1
+ 2\.3
+ 1\.5

For more information, see [Supported operations](supported-operations.md), [Features by engine version](features-by-version.md), and [Plugins by engine version](supported-plugins.md)\.

If you start a new OpenSearch Service project, we strongly recommend that you choose the latest supported OpenSearch version\. If you have an existing domain that uses an older Elasticsearch version, you can choose to keep the domain or migrate your data\. For more information, see [Upgrading Amazon OpenSearch Service domains](version-migration.md)\.

## Pricing for Amazon OpenSearch Service<a name="pricing"></a>

For OpenSearch Service, you pay for each hour of use of an EC2 instance and for the cumulative size of any EBS storage volumes attached to your instances\. [Standard AWS data transfer charges](https://aws.amazon.com/ec2/pricing/) also apply\.

However, some notable data transfer exceptions exist\. If a domain uses [multiple Availability Zones](managedomains-multiaz.md), OpenSearch Service does not bill for traffic between the Availability Zones\. Significant data transfer occurs within a domain during shard allocation and rebalancing\. OpenSearch Service neither meters nor bills for this traffic\. Similarly, OpenSearch Service does not bill for data transfer between [UltraWarm](ultrawarm.md)/[cold](cold-storage.md) nodes and Amazon S3\.

For full pricing details, see [Amazon OpenSearch Service pricing](https://aws.amazon.com/elasticsearch-service/pricing/)\. For information about charges incurred during configuration changes, see [Charges for configuration changes](managedomains-configuration-changes.md#managedomains-config-charges)\.

## Getting started with Amazon OpenSearch Service<a name="get-started"></a>

To get started, [sign up for an AWS account](https://aws.amazon.com/) if you don't already have one\. After you are set up with an account, complete the [getting started](gsg.md) tutorial for Amazon OpenSearch Service\. Consult the following introductory topics if you need more information while learning about the service:
+ [Create a domain](createupdatedomains.md)
+ [Size the domain](sizing-domains.md) appropriately for your workload
+ Control access to your domain using a [domain access policy](ac.md) or [fine\-grained access control](fgac.md)
+ Index data [manually](indexing.md) or from [other AWS services](integrations.md)
+ Use [OpenSearch Dashboards](dashboards.md) to search your data and create visualizations

For information on migrating to OpenSearch Service from a self\-managed OpenSearch cluster, see [Migrating to Amazon OpenSearch Service](migration.md)\.

## Related services<a name="related-services"></a>

OpenSearch Service commonly is used with the following services:

[Amazon CloudWatch](http://aws.amazon.com/documentation/cloudwatch/)  
OpenSearch Service domains automatically send metrics to CloudWatch so that you can monitor domain health and performance\. For more information, see [Monitoring OpenSearch cluster metrics with Amazon CloudWatch](managedomains-cloudwatchmetrics.md)\.  
CloudWatch Logs can also go the other direction\. You might configure CloudWatch Logs to stream data to OpenSearch Service for analysis\. To learn more, see [Loading streaming data from Amazon CloudWatch](integrations.md#integrations-cloudwatch)\.

[AWS CloudTrail](http://aws.amazon.com/documentation/cloudtrail/)  
Use AWS CloudTrail to get a history of the OpenSearch Service configuration API calls and related events for your account\. For more information, see [Monitoring Amazon OpenSearch Service API calls with AWS CloudTrail](managedomains-cloudtrailauditing.md)\.

[Amazon Kinesis](http://aws.amazon.com/documentation/kinesis/)  
Kinesis is a managed service for real\-time processing of streaming data at a massive scale\. For more information, see [Loading streaming data from Amazon Kinesis Data Streams](integrations.md#integrations-kinesis) and [Loading streaming data from Amazon Kinesis Data Firehose](integrations.md#integrations-fh)\.

[Amazon S3](http://aws.amazon.com/documentation/s3/)  
Amazon Simple Storage Service \(Amazon S3\) provides storage for the internet\. This guide provides Lambda sample code for integration with Amazon S3\. For more information, see [Loading streaming data from Amazon S3](integrations.md#integrations-s3-lambda)\.

[AWS IAM](http://aws.amazon.com/iam/)  
AWS Identity and Access Management \(IAM\) is a web service that you can use to manage access to your OpenSearch Service domains\. For more information, see [Identity and Access Management in Amazon OpenSearch Service](ac.md)\.

[AWS Lambda](http://aws.amazon.com/documentation/lambda/)  
AWS Lambda is a compute service that lets you run code without provisioning or managing servers\. This guide provides Lambda sample code to stream data from DynamoDB, Amazon S3, and Kinesis\. For more information, see [Loading streaming data into Amazon OpenSearch Service](integrations.md)\.

[Amazon DynamoDB](http://aws.amazon.com/documentation/dynamodb/)  
Amazon DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with seamless scalability\. To learn more about streaming data to OpenSearch Service, see [Loading streaming data from Amazon DynamoDB](integrations.md#integrations-dynamodb)\.

[Amazon QuickSight](http://aws.amazon.com/documentation/quicksight/)  
You can visualize data from OpenSearch Service using Amazon QuickSight dashboards\. For more information, see [Using Amazon OpenSearch Service with Amazon QuickSight](https://docs.aws.amazon.com/quicksight/latest/user/connecting-to-es.html) in the *Amazon QuickSight User Guide*\.

**Note**
OpenSearch includes certain Apache-licensed Elasticsearch code from Elasticsearch B\.V\. and other source code\. Elasticsearch B\.V\. is not the source of that other source code\. ELASTICSEARCH is a registered trademark of Elasticsearch B\.V\.