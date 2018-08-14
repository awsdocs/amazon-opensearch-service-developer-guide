# What Is Amazon Elasticsearch Service?<a name="what-is-amazon-elasticsearch-service"></a>

Amazon Elasticsearch Service \(Amazon ES\) is a managed service that makes it easy to deploy, operate, and scale Elasticsearch clusters in the AWS Cloud\. Elasticsearch is a popular open\-source search and analytics engine for use cases such as log analytics, real\-time application monitoring, and clickstream analysis\. With Amazon ES, you get direct access to the Elasticsearch APIs; existing code and applications work seamlessly with the service\.

Amazon ES provisions all the resources for your Elasticsearch cluster and launches it\. It also automatically detects and replaces failed Elasticsearch nodes, reducing the overhead associated with self\-managed infrastructures\. You can scale your cluster with a single API call or a few clicks in the console\.

To get started using Amazon ES, you create a *domain*\. An Amazon ES domain is synonymous with an Elasticsearch cluster\. Domains are clusters with the settings, instance types, instance counts, and storage resources that you specify\.

You can use the Amazon ES console to set up and configure a domain in minutes\. If you prefer programmatic access, you can use the [AWS CLI](http://docs.aws.amazon.com/cli/latest/userguide/) or the [AWS SDKs](http://aws.amazon.com/code)\.

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
+ Up to 1\.5 PB of instance storage
+ Amazon EBS storage volumes

**Security**
+ AWS Identity and Access Management \(IAM\) access control
+ Easy integration with Amazon VPC and VPC security groups
+ Encryption of data at rest
+ Amazon Cognito authentication for Kibana

**Stability**
+ Multiple geographical locations for your resources, known as *regions* and *Availability Zones*
+ Dedicated master nodes to offload cluster management tasks
+ Automated snapshots to back up and restore Amazon ES domains
+ Cluster node allocation across two Availability Zones in the same region, known as *zone awareness*

**Integration with Popular Services**
+ Data visualization using Kibana
+ Integration with Amazon CloudWatch for monitoring Amazon ES domain metrics and setting alarms
+ Integration with AWS CloudTrail for auditing configuration API calls to Amazon ES domains
+ Integration with Amazon S3, Amazon Kinesis, and Amazon DynamoDB for loading streaming data into Amazon ES

## Supported Elasticsearch Versions<a name="aes-choosing-version"></a>

Amazon ES currently supports the following Elasticsearch versions:
+ 6\.3
+ 6\.2
+ 6\.0
+ 5\.6
+ 5\.5
+ 5\.3
+ 5\.1
+ 2\.3
+ 1\.5

Compared to earlier versions of Elasticsearch, the 6\.*x* versions offer powerful features that make them faster, more secure, and easier to use\. Here are some highlights:
+ **Index splitting** – If an index outgrows its original number of shards, the `_split` API offers a convenient way to split each primary shard into two or more shards in a new index\.
+ **Vega visualizations** – Kibana 6\.2 and newer support the [Vega](https://vega.github.io/vega/) visualization language, which lets you make context\-aware Elasticsearch queries, combine multiple data sources into a single graph, add user interactivity to graphs, and much more\.
+ **Ranking evaluation** – The `_rank_eval` API lets you measure and track how ranked search results perform against a set of queries to ensure that your searches perform as expected\.
+ **Composite aggregations** – These aggregations build composite buckets from one or more fields and sort them in "natural order" \(alphabetically for terms, numerically or by date for histograms\)\.
+ **Higher indexing performance** – Newer versions of Elasticsearch provide superior indexing capabilities that significantly increase the throughput of data updates\.
+ **Better safeguards** – The 6\.*x* versions of Elasticsearch offer many safeguards that are designed to prevent overly broad or complex queries from negatively affecting the performance and stability of the cluster\.
+ **Kibana autocomplete** – Kibana 6\.3 and newer support autocomplete for queries, which greatly improves the day\-to\-day user experience\.

For more information about the differences between Elasticsearch versions and the APIs that Amazon ES supports, see [Supported Elasticsearch Operations](aes-supported-es-operations.md)\.

If you start a new Elasticsearch project, we strongly recommend that you choose the latest supported Elasticsearch version\. If you have an existing domain that uses an older Elasticsearch version, you can choose to keep the domain or migrate your data\. For more information, see [Upgrading Elasticsearch](es-version-migration.md)\.

## Pricing for Amazon Elasticsearch Service<a name="aes-pricing"></a>

With AWS, you pay only for what you use\. For Amazon ES, you pay for each hour of use of an EC2 instance and for the cumulative size of any EBS storage volumes attached to your instances\. [Standard AWS data transfer charges](https://aws.amazon.com/ec2/pricing/) also apply\.

However, a notable data transfer exception exists\. If you use [zone awareness](es-managedomains.md#es-managedomains-zoneawareness), Amazon ES does not bill for traffic between the two Availability Zones in which the domain resides\. Significant data transfer occurs within a domain during shard allocation and rebalancing\. Amazon ES neither meters nor bills for this traffic\.

For full pricing details, see [Amazon Elasticsearch Service Pricing](https://aws.amazon.com/elasticsearch-service/pricing/)\. For information about charges incurred during configuration changes, see [Charges for Configuration Changes](es-managedomains.md#es-managedomains-config-charges)\.

If you qualify for the AWS Free Tier, you receive up to 750 hours per month of use with the `t2.micro.elasticsearch` or `t2.small.elasticsearch` instance types\. You also receive up to 10 GB of Amazon EBS storage \(Magnetic or General Purpose\)\. For more information, see [AWS Free Tier](http://aws.amazon.com/free/)\.

**Note**  
Throughout this guide, 1 MB refers to 10242 or 1,048,576 bytes\. Likewise, 1 GB refers to 10243 or 1,073,741,824 bytes\.

## Getting Started with Amazon Elasticsearch Service<a name="aes-get-started"></a>

To get started, [sign up for an AWS account](https://aws.amazon.com/) if you don't already have one\. After you are set up with an account, complete the [Getting Started](es-gsg.md) tutorial for Amazon Elasticsearch Service\. Consult the following introductory topics if you need more information while learning about the service:
+ [Create a domain](es-createupdatedomains.md)
+ [Size your domain appropriately](sizing-domains.md)
+ [Control access to your domain](es-ac.md)
+ Index data [manually](es-indexing.md) or from [other services](es-aws-integrations.md)
+ Use [Kibana](es-kibana.md#es-managedomains-kibana) to search your data

## Related Services<a name="aes-related-services"></a>

Amazon ES commonly is used with the following services:

[AWS CloudTrail](http://aws.amazon.com/documentation/cloudtrail/)  
Use AWS CloudTrail to get a history of the Amazon ES configuration API calls and related events for your account\. For more information, see [Auditing Amazon Elasticsearch Service Domains with AWS CloudTrail](es-managedomains.md#es-managedomains-cloudtrailauditing)\.

[Amazon CloudWatch](http://aws.amazon.com/documentation/cloudwatch/)  
An Amazon ES domain automatically sends metrics to Amazon CloudWatch so that you can gather and analyze performance statistics\. For more information, see [Monitoring Cluster Metrics and Statistics with Amazon CloudWatch \(Console\)](es-managedomains.md#es-managedomains-cloudwatchmetrics)\.  
CloudWatch Logs can also go the other direction\. You might configure CloudWatch Logs to stream data to Amazon ES for analysis\. To learn more, see [Loading Streaming Data into Amazon ES from Amazon CloudWatch](es-aws-integrations.md#es-aws-integrations-cloudwatch-es)\.

[Amazon Kinesis](http://aws.amazon.com/documentation/kinesis/)  
Kinesis is a managed service that scales elastically for real\-time processing of streaming data at a massive scale\. For more information, see [Loading Streaming Data into Amazon ES from Amazon Kinesis Data Streams](es-aws-integrations.md#es-aws-integrations-kinesis) and [Loading Streaming Data into Amazon ES from Amazon Kinesis Data Firehose](es-aws-integrations.md#es-aws-integrations-fh)\.

[Amazon S3](http://aws.amazon.com/documentation/s3/)  
Amazon Simple Storage Service \(Amazon S3\) provides storage for the internet\. Amazon ES provides Lambda sample code for integration with Amazon S3\. For more information, see [Loading Streaming Data into Amazon ES from Amazon S3](es-aws-integrations.md#es-aws-integrations-s3-lambda-es)\.

[AWS IAM](http://aws.amazon.com/iam/)  
AWS Identity and Access Management \(IAM\) is a web service that you can use to manage for your Amazon ES domains\. For more information, see the [IAM](http://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html) documentation and [Amazon Elasticsearch Service Access Control](es-ac.md)\.

[AWS Lambda](http://aws.amazon.com/documentation/lambda/)  
AWS Lambda is a compute service that lets you run code without provisioning or managing servers\. Amazon ES provides Lambda sample code to stream data from DynamoDB, Amazon S3, and Kinesis\. For more information, see [Loading Streaming Data into Amazon Elasticsearch Service](es-aws-integrations.md)\.

[Amazon DynamoDB](http://aws.amazon.com/documentation/dynamodb/)  
Amazon DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with seamless scalability\. To learn more, see [Loading Streaming Data into Amazon ES from Amazon DynamoDB](es-aws-integrations.md#es-aws-integrations-dynamodb-es)\.