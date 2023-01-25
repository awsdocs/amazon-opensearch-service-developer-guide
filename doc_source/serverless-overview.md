# What is Amazon OpenSearch Serverless?<a name="serverless-overview"></a>

Amazon OpenSearch Serverless is an on\-demand serverless configuration for Amazon OpenSearch Service\. Serverless removes the operational complexities of provisioning, configuring, and tuning your OpenSearch clusters\. It's a good option for organizations that don't want to self\-manage their OpenSearch clusters, or organizations that don't have the dedicated resources or expertise to operate large clusters\. With OpenSearch Serverless, you can easily search and analyze petabytes of data without having to worry about the underlying infrastructure and data management\.

An OpenSearch Serverless *collection* is a group of OpenSearch indexes that work together to support a specific workload or use case\. Collections are easier to use than self\-managed OpenSearch *clusters,* which require manual provisioning\.

Collections have the same kind of high\-capacity, distributed, and highly available storage volume that's used by provisioned OpenSearch Service domains, but they remove more complexity because they don't require manual configuration and tuning\. OpenSearch Serverless also supports OpenSearch Dashboards, which provides an intuitive interface for analyzing data\.

Serverless collections currently run OpenSearch version 2\.0\.x\. As new versions are released, OpenSearch Serverless will automatically upgrade your collections to consume new features, bug fixes, and performance improvements\.

**Topics**
+ [Use cases for OpenSearch Serverless](#serverless-use-cases)
+ [Getting started](#serverless-start)
+ [How it works](#serverless-process)
+ [Choosing a collection type](#serverless-usecase)
+ [Pricing for OpenSearch Serverless](#serverless-pricing)
+ [Supported AWS Regions](#serverless-regions)
+ [Limitations](#serverless-limitations)
+ [Comparing OpenSearch Service and OpenSearch Serverless](#serverless-comparison)

## Use cases for OpenSearch Serverless<a name="serverless-use-cases"></a>

OpenSearch Serverless supports two primary use cases:
+ **Log analytics** \- The log analytics segment focuses on analyzing large volumes of semi\-structured, machine\-generated time series data for operational and user behavior insights\.
+ **Full\-text search** \- The full\-text search segment powers applications in your internal networks \(content management systems, legal documents\) and internet\-facing applications, such as ecommerce website content search\. 

 When you create a collection, you choose one of these use cases\. For more information, see [Choosing a collection type](#serverless-usecase)\.

## Getting started<a name="serverless-start"></a>

To get started with OpenSearch Serverless, create one or more collections using the OpenSearch Service console, the AWS CLI, or one of the AWS SDKs\. For a tutorial that helps you get a collection up and running quickly, see [Getting started with Amazon OpenSearch Serverless](serverless-getting-started.md)\.

OpenSearch Serverless supports the same ingest and query API operations as the OpenSearch open source suite, so you can continue to use your existing clients and applications\. Your clients must be compatible with OpenSearch 2\.x in order to work with OpenSearch Serverless\. For more information, see [Ingesting data into Amazon OpenSearch Serverless collections](serverless-clients.md)\.

## How it works<a name="serverless-process"></a>

Traditional OpenSearch clusters have a single set of instances that perform both indexing and search operations, and index storage is tightly coupled with compute capacity\. In contrast, OpenSearch Serverless uses a cloud\-native architecture that separates the indexing \(ingest\) components from the search \(query\) components, with Amazon S3 as the primary data storage for indexes\. 

This decoupled architecture lets you scale search and indexing functions independently of each other, and independently of the indexed data in S3\. The architecture also provides isolation for ingest and query operations so that they can run concurrently without resource contention\. 

When you write data to a collection, OpenSearch Serverless distributes it to the *indexing* compute units\. The indexing compute units ingest the incoming data and move the indexes to S3\. When you perform a search on the collection data, OpenSearch Serverless routes requests to the *search* compute units that hold the data being queried\. The search compute units download the indexed data directly from S3 \(if it's not already cached locally\), run search operations, and perform aggregations\. 

The following image illustrates this decoupled architecture:

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/Serverless.png)

OpenSearch Serverless compute capacity for data ingestion, searching, and querying are measured in OpenSearch Compute Units \(OCUs\)\. Each OCU is a combination of 6 GiB of memory and corresponding virtual CPU \(vCPU\), as well as data transfer to Amazon S3\. Each OCU includes enough hot ephemeral storage for 120 GiB of index data\.

When you create your first collection, OpenSearch Serverless instantiates two OCUs—one for indexing and one for search\. To ensure high availability, it also launches a standby set of nodes in another Availability Zone\. This means that a total of four OCUs are instantiated for the first collection in an account\.

These OCUs exist even when there's no activity on any collection endpoints\. All subsequent collections share these OCUs\. When you create additional collections in the same account, OpenSearch Serverless only adds additional OCUs for search and ingest as needed to support the collections, according to the [capacity limits](serverless-scaling.md#serverless-scaling-configure) that you specify\. Capacity does scale back down as your compute usage decreases\.

For information about how you're billed for these OCUs, see [Pricing for OpenSearch Serverless](#serverless-pricing)\.

## Choosing a collection type<a name="serverless-usecase"></a>

OpenSearch Serverless supports two primary collection types:

**Time series** – The log analytics segment that focuses on analyzing large volumes of semi\-structured, machine\-generated data in real\-time for operational, security, user behavior, and business insights\.

**Search** – Full\-text search that powers applications in your internal networks \(content management systems, legal documents\) and internet\-facing applications, such as ecommerce website search and content search\. 

You choose a collection type when you first create a collection:

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/serverless-collection-type.png)

The collection type that you choose depends on the kind of data that you plan to ingest into the collection, and how you plan to query it\. You can't change the collection type after you create it\.

The collection types have the following notable **differences**:
+ For *search* collections, all data is stored in hot storage to ensure fast query response times\. *Time series* collections use a combination of hot and warm caches, where the most recent data is kept in the hot cache to optimize query response times for more frequently accessed data\.
+ For *time series* collections, you can't index by custom document ID\. This operation is reserved for search use cases\. For more information, see [Supported OpenSearch API operations and permissions](serverless-genref.md#serverless-operations)\.

## Pricing for OpenSearch Serverless<a name="serverless-pricing"></a>

In OpenSearch Serverless, you're charged for the following components:
+ Data ingestion compute
+ Search and query compute
+ Storage retained in Amazon S3

OCUs are billed on an hourly basis, with per\-second granularity\. In your account statement, you see an entry for compute in OCU\-hours with a label for data ingestion and a label for search\. You're also billed on a monthly basis for data stored in Amazon S3\. You aren't charged for using OpenSearch Dashboards\.

You're billed for a minimum of 4 OCUs for the first collection in your account \(2 x ingest includes primary and standby, and 2 x search includes one active replica for high availability\)\. All subsequent collections can share those OCUs\. OpenSearch Serverless adds additional OCUs based on the compute needed to support your collections\.

**Note**  
Collections with unique AWS KMS keys can't share OCUs with other collections\.

You can configure a maximum number of OCUs for your account in order to control costs\. You're billed for a minimum of 4 OCUs allocated for your workloads when you create a collection\.

For full pricing details, see [Amazon OpenSearch Service pricing](https://aws.amazon.com/opensearch-service/pricing/)\.

## Supported AWS Regions<a name="serverless-regions"></a>

OpenSearch Serverless is available in a subset of AWS Regions that OpenSearch Service is available in\. For a list of supported Regions, see [Amazon OpenSearch Serverless endpoints and quotas](https://docs.aws.amazon.com/general/latest/gr/opensearch-service.html) in the *AWS General Reference*\.

## Limitations<a name="serverless-limitations"></a>

OpenSearch Serverless has the following limitations:
+ Some OpenSearch API operations aren't supported\. See [Supported OpenSearch API operations and permissions](serverless-genref.md#serverless-operations)\.
+ Some OpenSearch plugins aren't supported\. See [Supported OpenSearch plugins](serverless-genref.md#serverless-plugins)\.
+ OpenSearch Serverless currently can't scale down as search and index queries decrease\.
+ There's currently no way to automatically migrate your data from a managed OpenSearch Service domain to a serverless collection\. You must reindex your data from a domain to a collection\.
+ Cross\-account access to collections isn't supported\. You can't include collections from other accounts in your encryption or data access policies\.
+ Custom OpenSearch plugins aren't supported\.
+ You can't take or restore snapshots of OpenSearch Serverless collections\.
+ Cross\-Region search and replication aren't supported\.
+ There are limits on the number of serverless resources that you can have in a single account and Region\. See [OpenSearch Serverless quotas](limits.md#limits-serverless)\.
+ The refresh interval for indexes might be between 10 and 30 seconds depending on the size of your requests\.

## Comparing OpenSearch Service and OpenSearch Serverless<a name="serverless-comparison"></a>

In OpenSearch Serverless, some concepts and features are different than their corresponding feature for a provisioned OpenSearch Service domain\. For example, one importand difference is that OpenSearch Serverless doesn't have the concept of a cluster or node\.

The following table describes how important features and concepts in OpenSearch Serverless differ from the equivalent feature in a provisioned OpenSearch Service domain\.


| Feature | OpenSearch Service | OpenSearch Serverless | 
| --- | --- | --- | 
|  **Domains versus collections**  |  Indexes are held in *domains*, which are pre\-provisioned OpenSearch clusters\. For more information, see [Creating and managing Amazon OpenSearch Service domains](createupdatedomains.md)\.  |  Indexes are held in *collections*, which are logical groupings of indexes that represent a specific workload or use case\. For more information, see [Creating, listing, and deleting Amazon OpenSearch Serverless collections](serverless-manage.md)\.  | 
|  **Node types and capacity management**  |  You build a cluster with node types that meet your cost and performance specifications\. You must calculate your own storage requirements and choose an instance type for your domain\. For more information, see [Sizing Amazon OpenSearch Service domains](sizing-domains.md)\.  |  OpenSearch Serverless automatically scales and provisions additional compute units for your account based on your capacity usage\. For more information, see [Managing capacity limits for Amazon OpenSearch Serverless](serverless-scaling.md)\.  | 
|  **Billing**  |  You pay for each hour of use of an EC2 instance and for the cumulative size of any EBS storage volumes attached to your instances\. For more information, see [Pricing for Amazon OpenSearch Service](what-is.md#pricing)\.  |  You're charged in OCU\-hours for compute for data ingestion, compute for search and query, and storage retained in S3\. For more information, see [Pricing for OpenSearch Serverless](#serverless-pricing)\.  | 
|  **Encryption**  |  Encryption at rest is *optional* for domains\. For more information, see [Encryption of data at rest for Amazon OpenSearch Service](encryption-at-rest.md)\.  |  Encryption at rest is *required* for collections\. For more information, see [Encryption at rest for Amazon OpenSearch Serverless](serverless-encryption.md)\.  | 
|  **Data access control**  |  Access to the data within domains is determined by IAM policies and [fine\-grained access control](fgac.md)\.  |  Access to data within collections is determined by [data access policies](serverless-data-access.md)\.  | 
| Supported OpenSearch operations |  OpenSearch Service supports a subset of all of the OpenSearch API operations\. For more information, see [Supported operations in Amazon OpenSearch Service](supported-operations.md)\.  |  OpenSearch Serverless supports a different subset of OpenSearch API operations\. For more information, see [Supported operations and plugins in Amazon OpenSearch Serverless](serverless-genref.md)\.  | 
| Dashboards sign\-in |  Sign in with a user name and password\. For more information, see [Accessing OpenSearch Dashboards as the master user](fgac.md#fgac-dashboards)\.  |  Sign in with your access key and secret key\. For more information, see [Accessing OpenSearch Dashboards](serverless-manage.md#serverless-dashboards)\.  | 
| APIs |  Interact programmatically with OpenSearch Service using the [OpenSearch Service API operations](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/Welcome.html)\.  |  Interact programmatically with OpenSearch Serverless using the [OpenSearch Serverless API operations](https://docs.aws.amazon.com/opensearch-service/latest/ServerlessAPIReference/Welcome.html)\.  | 
| Network access |  Network settings for a domain apply to the domain endpoint as well as the OpenSearch Dashboards endpoint\. Network access for both is tightly coupled\.  |  Network settings for the domain endpoint and the OpenSearch Dashboards endpoint are decoupled\. You can choose to not configure network access for OpenSearch Dashboards\. For more information, see [Network access for Amazon OpenSearch Serverless](serverless-network.md)\.  | 
| Signing requests |  Use the OpenSearch high and low\-level REST clients to sign requests\. Specify the service name as `es`\. For more information, see [Signing HTTP requests to Amazon OpenSearch Service](request-signing.md)\.  |  At this time, OpenSearch Serverless supports a subset of clients that OpenSearch Service supports\. We will continue to add support for additional clients during the public preview\. When you sign requests, specify the service name as `aoss`\. The `x-amz-content-sha256` header is required\. For more information, see [Signing requests to OpenSearch Serverless](serverless-clients.md#serverless-signing)\.  | 
| OpenSearch version upgrades |  You manually upgrade your domains as new versions of OpenSearch become available\. You're responsible for ensuring that your domain meets the upgrade requirements, and that you've addressed any breaking changes\.  |  OpenSearch Serverless automatically upgrades your collections to new OpenSearch versions\. Upgrades don't necessarily happen as soon as a new version is available\.  | 
| Service software updates |  You manually apply service software updates to your domain as they become available\.  |  OpenSearch Serverless automatically updates your collections to consume the latest bug fixes, features, and performance improvements\.  | 
| VPC access |  You can [provision your domain within a VPC](vpc.md)\. You can also create additional [OpenSearch Service\-managed VPC endpoints](vpc-interface-endpoints.md) to access the domain\.  |  You create one or more [OpenSearch Serverless\-managed VPC endpoints](serverless-vpc.md) for your account\. Then, you include these endpoints within [network policies](serverless-network.md)\.  | 
| SAML authentication |  You enable SAML authentication on a per\-domain basis\. For more information, see [SAML authentication for OpenSearch Dashboards](saml.md)\.  |  You configure one or more SAML providers at the account level, then you include the associated user and group IDs within data access policies\. For more information, see [SAML authentication for Amazon OpenSearch Serverless](serverless-saml.md)\.  | 