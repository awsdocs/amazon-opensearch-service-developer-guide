# What Is Amazon Elasticsearch Service?<a name="what-is-amazon-elasticsearch-service"></a>

Amazon Elasticsearch Service \(Amazon ES\) is a managed service that makes it easy to deploy, operate, and scale Elasticsearch clusters in the AWS Cloud\. Elasticsearch is a popular open\-source search and analytics engine for use cases such as log analytics, real\-time application monitoring, and clickstream analysis\. With Amazon ES, you get direct access to the Elasticsearch APIs; existing code and applications work seamlessly with the service\.

Amazon ES provisions all the resources for your Elasticsearch cluster and launches it\. It also automatically detects and replaces failed Elasticsearch nodes, reducing the overhead associated with self\-managed infrastructures\. You can scale your cluster with a single API call or a few clicks in the console\.

To get started using Amazon ES, you create a *domain*\. An Amazon ES domain is synonymous with an Elasticsearch cluster\. Domains are clusters with the settings, instance types, instance counts, and storage resources that you specify\. Each instance acts as one Elasticsearch node\.

You can use the Amazon ES console to set up and configure a domain in minutes\. If you prefer programmatic access, you can use the [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/) or the [AWS SDKs](http://aws.amazon.com/code)\.

**Topics**
+ [Features of Amazon Elasticsearch Service](#what-is-aes-features)
+ [Supported Elasticsearch Versions](#aes-choosing-version)
+ [Pricing for Amazon Elasticsearch Service](#aes-pricing)
+ [Getting Started with Amazon Elasticsearch Service](#aes-get-started)
+ [Related Services](#aes-related-services)

## Features of Amazon Elasticsearch Service<a name="what-is-aes-features"></a>

Amazon ES includes the following features:

**Scale**
+ Numerous configurations of CPU, memory, and storage capacity, known as *instance types*
+ Up to 3 PB of attached storage
+ Cost\-effective [UltraWarm](ultrawarm.md) storage for read\-only data

**Security**
+ AWS Identity and Access Management \(IAM\) access control
+ Easy integration with Amazon VPC and VPC security groups
+ Encryption of data at rest and node\-to\-node encryption
+ Amazon Cognito or HTTP basic authentication for Kibana
+ Index\-level, document\-level, and field\-level security
+ Kibana multi\-tenancy

**Stability**
+ Numerous geographical locations for your resources, known as *Regions* and *Availability Zones*
+ Node allocation across two or three Availability Zones in the same AWS Region, known as *Multi\-AZ*
+ Dedicated master nodes to offload cluster management tasks
+ Automated snapshots to back up and restore Amazon ES domains

**Flexibility**
+ SQL support for integration with business intelligence \(BI\) applications
+ Custom packages to improve search results

**Integration with Popular Services**
+ Data visualization using Kibana
+ Integration with Amazon CloudWatch for monitoring Amazon ES domain metrics and setting alarms
+ Integration with AWS CloudTrail for auditing configuration API calls to Amazon ES domains
+ Integration with Amazon S3, Amazon Kinesis, and Amazon DynamoDB for loading streaming data into Amazon ES
+ Alerts from Amazon SNS when your data exceeds certain thresholds

## Supported Elasticsearch Versions<a name="aes-choosing-version"></a>

Amazon ES currently supports the following Elasticsearch versions:
+ 7\.7, 7\.4, 7\.1
+ 6\.8, 6\.7, 6\.5, 6\.4, 6\.3, 6\.2, 6\.0
+ 5\.6, 5\.5, 5\.3, 5\.1
+ 2\.3
+ 1\.5

Compared to earlier versions of Elasticsearch, the 7\.*x* and 6\.*x* versions offer powerful features that make them faster, more secure, and easier to use\. Here are a few highlights:
+ **Higher indexing performance** – Newer versions of Elasticsearch provide superior indexing capabilities that significantly increase the throughput of data updates\.
+ **Better safeguards** – Newer versions of Elasticsearch help prevent overly broad or complex queries from negatively affecting the performance and stability of the cluster\.
+ **Vega visualizations** – Kibana 6\.2 and later versions support the [Vega](https://vega.github.io/vega/) visualization language, which lets you make context\-aware Elasticsearch queries, combine multiple data sources into a single graph, add user interactivity to graphs, and much more\.
+ **Java high\-level REST client** – Compared to the low\-level client, this client offers a simplified development experience and supports most Elasticsearch APIs\. For a code example, see [Signing HTTP Requests](es-request-signing.md#es-request-signing-java)\.

For more information, see [Supported Elasticsearch Operations](aes-supported-es-operations.md), [Features by Elasticsearch Version](aes-features-by-version.md), and [Plugins by Elasticsearch Version](aes-supported-plugins.md)\.

If you start a new Elasticsearch project, we strongly recommend that you choose the latest supported Elasticsearch version\. If you have an existing domain that uses an older Elasticsearch version, you can choose to keep the domain or migrate your data\. For more information, see [Upgrading Elasticsearch](es-version-migration.md)\.

## Pricing for Amazon Elasticsearch Service<a name="aes-pricing"></a>

For Amazon ES, you pay for each hour of use of an EC2 instance and for the cumulative size of any EBS storage volumes attached to your instances\. [Standard AWS data transfer charges](https://aws.amazon.com/ec2/pricing/) also apply\.

However, some notable data transfer exceptions exist\. If a domain uses [multiple Availability Zones](es-managedomains-multiaz.md), Amazon ES does not bill for traffic between the Availability Zones\. Significant data transfer occurs within a domain during shard allocation and rebalancing\. Amazon ES neither meters nor bills for this traffic\. Similarly, Amazon ES does not bill for data transfer between [UltraWarm](ultrawarm.md) nodes and Amazon S3\.

For full pricing details, see [Amazon Elasticsearch Service Pricing](https://aws.amazon.com/elasticsearch-service/pricing/)\. For information about charges incurred during configuration changes, see [Charges for Configuration Changes](es-managedomains-configuration-changes.md#es-managedomains-config-charges)\.

## Getting Started with Amazon Elasticsearch Service<a name="aes-get-started"></a>

To get started, [sign up for an AWS account](https://aws.amazon.com/) if you don't already have one\. After you are set up with an account, complete the [Getting Started](es-gsg.md) tutorial for Amazon Elasticsearch Service\. Consult the following introductory topics if you need more information while learning about the service:
+ [Create a domain](es-createupdatedomains.md)
+ [Size the domain](sizing-domains.md) appropriately for your workload
+ Control access to your domain using a [domain access policy](es-ac.md) or [fine\-grained access control](fgac.md)
+ Index data [manually](es-indexing.md) or from [other AWS services](es-aws-integrations.md)
+ Use [Kibana](es-kibana.md) to search your data and create visualizations

For information on migrating to Amazon ES from a self\-managed Elasticsearch cluster, see [Migrating to Amazon Elasticsearch Service](migration.md)\.

## Related Services<a name="aes-related-services"></a>

Amazon ES commonly is used with the following services:

[Amazon CloudWatch](http://aws.amazon.com/documentation/cloudwatch/)  
Amazon ES domains automatically send metrics to CloudWatch so that you can monitor domain health and performance\. For more information, see [Monitoring Cluster Metrics with Amazon CloudWatch](es-managedomains-cloudwatchmetrics.md)\.  
CloudWatch Logs can also go the other direction\. You might configure CloudWatch Logs to stream data to Amazon ES for analysis\. To learn more, see [Loading Streaming Data into Amazon ES from Amazon CloudWatch](es-aws-integrations.md#es-aws-integrations-cloudwatch-es)\.

[AWS CloudTrail](http://aws.amazon.com/documentation/cloudtrail/)  
Use AWS CloudTrail to get a history of the Amazon ES configuration API calls and related events for your account\. For more information, see [Configuration API Monitoring in Amazon Elasticsearch Service](es-managedomains-cloudtrailauditing.md)\.

[Amazon Kinesis](http://aws.amazon.com/documentation/kinesis/)  
Kinesis is a managed service for real\-time processing of streaming data at a massive scale\. For more information, see [Loading Streaming Data into Amazon ES from Amazon Kinesis Data Streams](es-aws-integrations.md#es-aws-integrations-kinesis) and [Loading Streaming Data into Amazon ES from Amazon Kinesis Data Firehose](es-aws-integrations.md#es-aws-integrations-fh)\.

[Amazon S3](http://aws.amazon.com/documentation/s3/)  
Amazon Simple Storage Service \(Amazon S3\) provides storage for the internet\. This guide provides Lambda sample code for integration with Amazon S3\. For more information, see [Loading Streaming Data into Amazon ES from Amazon S3](es-aws-integrations.md#es-aws-integrations-s3-lambda-es)\.

[AWS IAM](http://aws.amazon.com/iam/)  
AWS Identity and Access Management \(IAM\) is a web service that you can use to manage access to your Amazon ES domains\. For more information, see [Identity and Access Management in Amazon Elasticsearch Service](es-ac.md)\.

[AWS Lambda](http://aws.amazon.com/documentation/lambda/)  
AWS Lambda is a compute service that lets you run code without provisioning or managing servers\. This guide provides Lambda sample code to stream data from DynamoDB, Amazon S3, and Kinesis\. For more information, see [Loading Streaming Data into Amazon Elasticsearch Service](es-aws-integrations.md)\.

[Amazon DynamoDB](http://aws.amazon.com/documentation/dynamodb/)  
Amazon DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with seamless scalability\. To learn more about streaming data to Amazon ES, see [Loading Streaming Data into Amazon ES from Amazon DynamoDB](es-aws-integrations.md#es-aws-integrations-dynamodb-es)\.