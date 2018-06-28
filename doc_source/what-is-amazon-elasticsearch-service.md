# What Is Amazon Elasticsearch Service?<a name="what-is-amazon-elasticsearch-service"></a>

Amazon Elasticsearch Service \(Amazon ES\) is a managed service that makes it easy to deploy, operate, and scale Elasticsearch clusters in the AWS Cloud\. Elasticsearch is a popular open\-source search and analytics engine for use cases such as log analytics, real\-time application monitoring, and clickstream analytics\. With Amazon ES, you get direct access to the Elasticsearch APIs so that existing code and applications work seamlessly with the service\.

Amazon ES provisions all the resources for your Elasticsearch cluster and launches the cluster\. It also automatically detects and replaces failed Elasticsearch nodes, reducing the overhead associated with self\-managed infrastructures\. You can scale your cluster with a single API call or a few clicks in the console\.

To get started using the service, you create an Amazon ES domain\. An Amazon ES domain is an Elasticsearch cluster in the AWS Cloud that has the compute and storage resources that you specify\. For example, you can specify the number of instances, instance types, and storage options\.

Additionally, Amazon ES offers the following benefits of a managed service:
+ Cluster scaling options
+ Self\-healing clusters
+ Replication for data durability
+ Enhanced security
+ Node monitoring

You can use the Amazon ES console to set up and configure your domain in minutes\. If you prefer programmatic access, you can use the [AWS SDKs](http://aws.amazon.com/code) or the [AWS CLI](http://docs.aws.amazon.com/cli/latest/userguide/)\. 

There are no upfront costs to set up clusters, and you pay only for the service resources that you use\.

**Topics**
+ [Features of Amazon Elasticsearch Service](#what-is-aes-features)
+ [Supported Elasticsearch Versions](#aes-choosing-version)
+ [Getting Started with Amazon Elasticsearch Service](#aes-get-started)
+ [Signing Up for AWS](#aws-sign-up)
+ [Accessing Amazon Elasticsearch Service](#accessing-amazon-elasticsearch)
+ [Regions and Endpoints for Amazon Elasticsearch Service](#endpoints)
+ [Choosing Instance Types](#aes-choosing-instance-type)
+ [Scaling in Amazon Elasticsearch Service](#concepts-scaling)
+ [Using Amazon EBS Volumes for Storage](#ebs-volumes-storage)
+ [Related Services](#aes-related-services)
+ [Pricing for Amazon Elasticsearch Service](#aes-pricing)

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
+ 6\.2
+ 6\.0
+ 5\.5
+ 5\.3
+ 5\.1
+ 2\.3
+ 1\.5

Compared to earlier versions of Elasticsearch, the 6\.*x* versions offer powerful features that make them faster, more secure, and easier to use\. Here are some highlights:
+ **Index splitting** – If an index outgrows its original number of shards, the `_split` API offers a convenient way to split each primary shard into two or more shards in a new index\.
+ **Vega visualizations** – Kibana 6\.2 supports the [Vega](https://vega.github.io/vega/) visualization language, which lets you make context\-aware Elasticsearch queries, combine multiple data sources into a single graph, add user interactivity to graphs, and much more\.
+ **Ranking evaluation** – The `_rank_eval` API lets you measure and track how ranked search results perform against a set of queries to ensure that your searches perform as expected\.
+ **Composite aggregations** – These aggregations build composite buckets from one or more fields and sort them in "natural order" \(alphabetically for terms, numerically or by date for histograms\)\.
+ **Higher indexing performance** – Newer versions of Elasticsearch provide superior indexing capabilities that significantly increase the throughput of data updates\.
+ **Better safeguards** – The 6\.*x* versions of Elasticsearch offer many safeguards that are designed to prevent overly broad or complex queries from negatively affecting the performance and stability of the cluster\.

For more information about the differences between Elasticsearch versions and the APIs that Amazon ES supports, see [Supported Elasticsearch Operations](aes-supported-es-operations.md)\.

If you start a new Elasticsearch project, we strongly recommend that you choose the latest supported Elasticsearch version\. If you have an existing domain that uses an older Elasticsearch version, you can choose to keep the domain or migrate your data\. For more information, see [Migrating to a Different Elasticsearch Version](es-version-migration.md)\.

## Getting Started with Amazon Elasticsearch Service<a name="aes-get-started"></a>

To get started, sign up for an AWS account if you don't already have one\. For more information, see [Signing Up for AWS](#aws-sign-up)\.

After you are set up with an account, complete the [Getting Started](es-gsg.md) tutorial for Amazon Elasticsearch Service\. Consult the following introductory topics if you need more information while learning about the service\.

**Get up and Running**
+ [Signing Up for AWS](#aws-sign-up)
+ [Accessing Amazon ES](#accessing-amazon-elasticsearch)
+ [Getting Started with Amazon ES Domains](es-gsg.md)

**Learn the Basics**
+ [Regions and Endpoints for Amazon ES](#endpoints)
+ [Amazon Resource Names and AWS Namespaces](http://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html)
+ [Choosing an Elasticsearch Version](#aes-choosing-version)

**Choose Instance Types and Storage**
+ [Choosing an Instance Type](#aes-choosing-instance-type)
+ [Scaling in Amazon ES](#concepts-scaling)
+ [Configuring EBS\-based Storage](es-createupdatedomains.md#es-createdomain-configure-ebs)

**Stay Secure**
+ [Amazon Elasticsearch Service Access Control](es-ac.md)\.
+ [Signing Amazon ES Requests](es-ac.md#es-managedomains-signing-service-requests)

## Signing Up for AWS<a name="aws-sign-up"></a>

If you're not already an AWS customer, your first step is to create an AWS account\. If you already have an AWS account, you are automatically signed up for Amazon ES\. Your AWS account enables you to access Amazon ES and other AWS services such as Amazon S3 and Amazon EC2\. There are no sign\-up fees, and you don't incur charges until you create a domain\. As with other AWS services, you pay only for the resources that you use\.<a name="setting-up-sign-up-for-aws-procedure"></a>

**To create an AWS account**

1. Open [https://aws\.amazon\.com/](https://aws.amazon.com/), and then choose **Create an AWS Account**\.
**Note**  
This might be unavailable in your browser if you previously signed into the AWS Management Console\. In that case, choose **Sign in to a different account**, and then choose **Create a new AWS account**\.

1. Follow the online instructions\.

   Part of the sign\-up procedure involves receiving a phone call and entering a PIN using the phone keypad\.

You must enter payment information before you can begin using Amazon ES\. Note your AWS account number, because you will need it later\.

## Accessing Amazon Elasticsearch Service<a name="accessing-amazon-elasticsearch"></a>

You can access Amazon ES through the Amazon ES console, the AWS SDKs, or the AWS CLI\. 
+ The [Amazon ES console](https://console.aws.amazon.com/es/) lets you create, configure, and monitor your domains\. Using the console is the easiest way to get started with Amazon ES\. 
+ The [AWS SDKs](http://aws.amazon.com/code) support all the Amazon ES configuration API operations, making it easy to manage your domains using your preferred technology\. The SDKs automatically sign requests as needed using your AWS credentials\.
+ The [AWS CLI](http://docs.aws.amazon.com/cli/latest/userguide/) wraps all the Amazon ES configuration API operations, providing a simple way to create and configure domains\. The AWS CLI automatically signs requests as needed using your AWS credentials\.

## Regions and Endpoints for Amazon Elasticsearch Service<a name="endpoints"></a>

Amazon ES provides regional endpoints for accessing the configuration API and domain\-specific endpoints for accessing the Elasticsearch APIs\. You use the configuration service to create and manage your domains\. The regional configuration service endpoints have this format: 

```
es.region.amazonaws.com
```

For a list of supported regions, see [Regions and Endpoints](http://docs.aws.amazon.com/general/latest/gr/rande.html#elasticsearch-service-regions) in the *AWS General Reference*\.

Domain endpoints have the following format:

```
http://search-domainname-domainid.us-east-1.es.amazonaws.com
```

You use a domain's endpoint to upload data, submit search requests, and perform any other [supported Elasticsearch operations](aes-supported-es-operations.md)\.

## Choosing Instance Types<a name="aes-choosing-instance-type"></a>

An instance type defines the CPU, RAM, storage capacity, and hourly cost for an *instance*, the Amazon Machine Image \(AMI\) that runs as a virtual server in the AWS Cloud\. Choose the instance type and the number of instances for your domain based on the amount of data that you have and number of requests that you anticipate\. For guidance, see [Sizing Amazon ES Domains](sizing-domains.md)\.

For general information about instance types, see [Amazon Elasticsearch Service Pricing](https://aws.amazon.com/elasticsearch-service/pricing/)\.

## Scaling in Amazon Elasticsearch Service<a name="concepts-scaling"></a>

When you create a domain, you choose an initial number of Elasticsearch instances and an instance type\. However, these initial choices might not be adequate over time\. You can easily accommodate growth by scaling your Amazon ES domain horizontally \(more instances\) or vertically \(larger instance types\)\. Scaling is simple and requires no downtime\. To learn more, see [Configuring Amazon ES Domains](es-createupdatedomains.md#es-createdomains-configure-cluster) and [About Configuration Changes](es-managedomains.md#es-managedomains-configuration-changes)\.

## Using Amazon EBS Volumes for Storage<a name="ebs-volumes-storage"></a>

You have the option of configuring your Amazon ES domain to use an Amazon EBS volume for storing indices rather than the default storage provided by the instance\. An Amazon EBS volume is a durable, block\-level storage device that you can attach to a single instance\. Amazon ES supports the following EBS volume types:
+ Magnetic
+ General Purpose \(SSD\)
+ Provisioned IOPS \(SSD\)

For an overview, see [Amazon EBS Volumes](http://docs.aws.amazon.com/AWSEC2/latest/WindowsGuide/EBSVolumes.html) in the Amazon EC2 documentation\. For procedures that show you how to use Amazon EBS volumes for your Amazon ES domain, see [Configuring EBS\-based Storage](es-createupdatedomains.md#es-createdomain-configure-ebs)\. For information about the minimum and maximum size of supported EBS volumes in an Amazon ES domain, see [EBS Volume Size Limits](aes-limits.md#ebsresource)\.

## Related Services<a name="aes-related-services"></a>

Amazon ES commonly is used with the following services:

[AWS CloudTrail](http://aws.amazon.com/documentation/cloudtrail/)  
Use AWS CloudTrail to get a history of the Amazon ES API calls and related events for your account\. CloudTrail is a web service that records API calls from your accounts and delivers the resulting log files to your Amazon S3 bucket\. You also can use CloudTrail to track changes that were made to your AWS resources\. For more information, see [Auditing Amazon Elasticsearch Service Domains with AWS CloudTrail](es-managedomains.md#es-managedomains-cloudtrailauditing)\.

[Amazon CloudWatch](http://aws.amazon.com/documentation/cloudwatch/)  
An Amazon ES domain automatically sends metrics to Amazon CloudWatch so that you can gather and analyze performance statistics\. You can monitor these metrics by using the AWS CLI or the AWS SDKs\. For more information, see [Monitoring Cluster Metrics and Statistics with Amazon CloudWatch \(Console\)](es-managedomains.md#es-managedomains-cloudwatchmetrics)\.  
CloudWatch Logs can also go the other direction\. You might configure CloudWatch Logs to stream data to Amazon ES for analysis\. To learn more, see [Loading Streaming Data into Amazon ES from Amazon CloudWatch](es-aws-integrations.md#es-aws-integrations-cloudwatch-es)\.

[Kinesis](http://aws.amazon.com/documentation/kinesis/)  
Kinesis is a managed service that scales elastically for real\-time processing of streaming data at a massive scale\. For more information, see [Loading Streaming Data into Amazon ES from Amazon Kinesis](es-aws-integrations.md#es-aws-integrations-kinesis)\.

[Amazon S3](http://aws.amazon.com/documentation/s3/)  
Amazon Simple Storage Service \(Amazon S3\) provides storage for the internet\. You can use Amazon S3 to store and retrieve any amount of data at any time, from anywhere on the web\. Amazon ES provides Lambda sample code for integration with Amazon S3\. For more information, see [Loading Streaming Data into Amazon ES from Amazon S3](es-aws-integrations.md#es-aws-integrations-s3-lambda-es)\.

[AWS IAM](http://aws.amazon.com/iam/)  
AWS Identity and Access Management \(IAM\) is a web service that you can use to manage users and user permissions in AWS\. You can use IAM to create user\-based access policies for your Amazon ES domains\. For more information, see the [IAM](http://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html) documentation and [Amazon Elasticsearch Service Access Control](es-ac.md)\.

Amazon ES integrates with the following services to provide data ingestion:

[AWS Lambda](http://aws.amazon.com/documentation/lambda/)  
AWS Lambda is a compute service that lets you run code without provisioning or managing servers\. Amazon ES provides Lambda sample code to stream data from DynamoDB, Amazon S3, and Kinesis\. For more information, see [Loading Streaming Data into Amazon Elasticsearch Service](es-aws-integrations.md)\.

[Amazon DynamoDB](http://aws.amazon.com/documentation/dynamodb/)  
Amazon DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with seamless scalability\. To learn more, see [Loading Streaming Data into Amazon ES from Amazon DynamoDB](es-aws-integrations.md#es-aws-integrations-dynamodb-es)\.

## Pricing for Amazon Elasticsearch Service<a name="aes-pricing"></a>

With AWS, you pay only for what you use\. For Amazon ES, you pay for each hour of use of an EC2 instance and for the cumulative size of any EBS storage volumes attached to your instances\. [Standard AWS data transfer charges](https://aws.amazon.com/ec2/pricing/) also apply\.

However, a notable data transfer exception exists\. If you use [zone awareness](es-managedomains.md#es-managedomains-zoneawareness), Amazon ES does not bill for traffic between the two Availability Zones in which the domain resides\. Significant data transfer occurs within a domain during shard allocation and rebalancing\. Amazon ES neither meters nor bills for this traffic\.

For full pricing details, see [Amazon Elasticsearch Service Pricing](https://aws.amazon.com/elasticsearch-service/pricing/)\. For information about charges incurred during configuration changes, see [Charges for Configuration Changes](es-managedomains.md#es-managedomains-config-charges)\.

If you qualify for the AWS Free Tier, you receive up to 750 hours per month of use with the `t2.micro.elasticsearch` or `t2.small.elasticsearch` instance types\. You also receive up to 10 GB of Amazon EBS storage \(Magnetic or General Purpose\)\. For more information, see [AWS Free Tier](http://aws.amazon.com/free/)\.

**Note**  
Throughout this guide, 1 MB refers to 10242 or 1,048,576 bytes\. Likewise, 1 GB refers to 10243 or 1,073,741,824 bytes\.