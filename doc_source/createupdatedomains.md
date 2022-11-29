# Creating and managing Amazon OpenSearch Service domains<a name="createupdatedomains"></a>

This chapter describes how to create and manage Amazon OpenSearch Service domains\. An OpenSearch Service domain is synonymous with an OpenSearch cluster\. Domains are clusters with the settings, instance types, instance counts, and storage resources that you specify\.

Unlike the brief instructions in the [Getting started tutorial](gsg.md), this chapter describes all options and provides relevant reference information\. You can complete each procedure by using instructions for the OpenSearch Service console, the AWS Command Line Interface \(AWS CLI\), or the AWS SDKs\.

## Creating OpenSearch Service domains<a name="createdomains"></a>

This section describes how to create OpenSearch Service domains by using the OpenSearch Service console or by using the AWS CLI with the `create-domain` command\.

### Creating OpenSearch Service domains \(console\)<a name="createdomains-console"></a>

Use the following procedure to create an OpenSearch Service domain by using the console\.

**To create an OpenSearch Service domain \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com) and choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Amazon OpenSearch Service**\.

1. Choose **Create domain**\.

1. For **Domain name**, enter a domain name\. The name must meet the following criteria:
   + Unique to your account and AWS Region
   + Starts with a lowercase letter
   + Contains between 3 and 28 characters
   + Contains only lowercase letters a\-z, the numbers 0\-9, and the hyphen \(\-\)

1. If you want to use a custom endpoint rather than the standard one of `https://search-mydomain-1a2a3a4a5a6a7a8a9a0a9a8a7a.us-east-1.es.amazonaws.com` , choose **Enable custom endpoint** and provide a name and certificate\. For more information, see [Creating a custom endpoint for Amazon OpenSearch Service](customendpoint.md)\.

1. For **Deployment type**, choose the option that best matches the purpose of your domain:
   + **Production** domains use Multi\-AZ and dedicated master nodes for higher availability\.
   + **Development and testing** domains use a single Availability Zone\.
   + **Custom** domains let you choose from all configuration options\.
**Important**  
Different deployment types present different options on subsequent pages\. These steps include all options \(the **Custom** deployment type\)\.

1. For **Version**, choose the version of OpenSearch or legacy Elasticsearch OSS to use\. We recommend that you choose the latest version of OpenSearch\. For more information, see [Supported versions of OpenSearch and Elasticsearch](what-is.md#choosing-version)\.

   \(Optional\) If you chose an OpenSearch version for your domain, select **Enable compatibility mode** to make OpenSearch report its version as 7\.10, which allows certain Elasticsearch OSS clients and plugins that check the version before connecting to continue working with the service\.

1. For **Auto\-Tune**, choose whether to allow OpenSearch Service to suggest memory\-related configuration changes to your domain to improve speed and stability\. For more information, see [Auto\-Tune for Amazon OpenSearch Service](auto-tune.md)\.

   \(Optional\) Select **Add maintenance window** to schedule a recurring window during which Auto\-Tune updates the domain\.

1. Under **Data nodes**, choose the number of availability zones\. For more information, see [Configuring a multi\-AZ domain in Amazon OpenSearch Service](managedomains-multiaz.md)\.
**Note**  
The OpenSearch Service console doesn't support moving from multiple availability zones to a single availability zone after the domain is created\. If you choose 2 or 3 availability zones and later want to move to 1, you must disable the `ZoneAwarenessEnabled` parameter using the AWS CLI or configuration API\.

1. For **Instance type**, choose an instance type for your data nodes\. For more information, see [Supported instance types in Amazon OpenSearch Service](supported-instance-types.md)\.
**Note**  
Not all Availability Zones support all instance types\. If you choose **3\-AZ**, we recommend choosing current\-generation instance types such as R5 or I3\.

1. For **Number of nodes**, choose the number of data nodes\.

   For maximum values, see [Domain and instance quotas](limits.md#clusterresource)\. Single\-node clusters are fine for development and testing, but should not be used for production workloads\. For more guidance, see [Sizing Amazon OpenSearch Service domains](sizing-domains.md) and [Configuring a multi\-AZ domain in Amazon OpenSearch Service](managedomains-multiaz.md)\.

1. For **Storage type**, select Amazon EBS or instance store volumes to associate with your instance\. The volume types available in the list depend on the instance type that you've chosen\. For guidance on creating especially large domains, see [Petabyte scale in Amazon OpenSearch Service](petabyte-scale.md)\.

1. If you chose **EBS** as the storage type, configure the following additional settings\. Some settings might not appear depending on the type of volume you choose\.    
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/createupdatedomains.html)

1. \(Optional\) If you selected a `gp3` volume type, expand **Advanced settings** and specify additional IOPS \(up to 1,000 MiB/s\) and throughput \(up to 16,000\) to provision for each node, beyond what is included with the price of storage, for an additional cost\. For more information, see the [Amazon OpenSearch Service pricing](https://aws.amazon.com/opensearch-service/pricing/)\.

1. Choose the type and number of [dedicated master nodes](managedomains-dedicatedmasternodes.md)\. Dedicated master nodes increase cluster stability and are required for domains that have instance counts greater than 10\. We recommend three dedicated master nodes for production domains\.
**Note**  
You can choose different instance types for your dedicated master nodes and data nodes\. For example, you might select general purpose or storage\-optimized instances for your data nodes, but compute\-optimized instances for your dedicated master nodes\.

1. \(Optional\) To enable [UltraWarm storage](ultrawarm.md), choose **Enable UltraWarm data nodes**\. Each instance type has a [maximum amount of storage](limits.md#limits-ultrawarm) that it can address\. Multiply that amount by the number of warm data nodes for the total addressable warm storage\.

1. \(Optional\) To enable [cold storage](cold-storage.md), choose **Enable cold storage**\. You must enable UltraWarm to enable cold storage\.

1. \(Optional\) For domains running OpenSearch or Elasticsearch 5\.3 and later, the **Snapshot configuration** is irrelevant\. For more information about automated snapshots, see [Creating index snapshots in Amazon OpenSearch Service](managedomains-snapshots.md)\.

1. Under **Network**, choose either **VPC access** or **Public access**\. If you choose **Public access**, skip to the next step\. If you choose **VPC access**, make sure you meet the [prerequisites](vpc.md#prerequisites-vpc-endpoints), then configure the following settings:    
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/createupdatedomains.html)

1. Enable or disable fine\-grained access control:
   + If you want to use IAM for user management, choose **Set IAM ARN as master user** and specify the ARN for an IAM role\.
   + If you want to use the internal user database, choose **Create master user** and specify a user name and password\.

   Whichever option you choose, the master user can access all indexes in the cluster and all OpenSearch APIs\. For guidance on which option to choose, see [Key concepts](fgac.md#fgac-concepts)\.

   If you disable fine\-grained access control, you can still control access to your domain by placing it within a VPC, applying a restrictive access policy, or both\. You must enable node\-to\-node encryption and encryption at rest to use fine\-grained access control\.
**Note**  
We *strongly* recommend enabling fine\-grained access control to protect the data on your domain\. Fine\-grained access control provides security at the cluster, index, document, and field levels\.

1. \(Optional\) If you want to use SAML authentication for OpenSearch Dashboards, choose **Prepare SAML authentication**\. After the domain is available, see [SAML authentication for OpenSearch Dashboards](saml.md) for additional steps\.

1. \(Optional\) If you want to use Amazon Cognito authentication for OpenSearch Dashboards, choose **Enable Amazon Cognito authentication**\. Then choose the Amazon Cognito user pool and identity pool that you want to use for OpenSearch Dashboards authentication\. For guidance on creating these resources, see [Configuring Amazon Cognito authentication for OpenSearch Dashboards](cognito-auth.md)\.

1. For **Domain access policy**, choose an access policy or configure one of your own\. If you choose to create a custom policy, you can configure it yourself or import one from another domain\. For more information, see [Identity and Access Management in Amazon OpenSearch Service](ac.md)\.
**Note**  
If you enabled VPC access, you can't use IP\-based policies\. Instead, you can use [security groups](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_SecurityGroups.html) to control which IP addresses can access the domain\. For more information, see [About access policies on VPC domains](vpc.md#vpc-security)\.

1. \(Optional\) To require that all requests to the domain arrive over HTTPS, select **Require HTTPS for all traffic to the domain**\.

1. \(Optional\) To enable node\-to\-node encryption, select **Node\-to\-node encryption**\. For more information, see [Node\-to\-node encryption for Amazon OpenSearch Service](ntn.md)\.

1. \(Optional\) To enable encryption of data at rest, select **Enable encryption of data at rest**\.

   Select **Use AWS owned key** to have OpenSearch Service create an AWS KMS encryption key on your behalf \(or use the one that it already created\)\. Otherwise, choose your own KMS key\. For more information, see [Encryption of data at rest for Amazon OpenSearch Service](encryption-at-rest.md)\.

1. \(Optional\) Add tags to describe your domain so you can categorize and filter on that information\. For more information, see [Tagging Amazon OpenSearch Service domains](managedomains-awsresourcetagging.md)\.

1. \(Optional\) Expand and configure **Advanced cluster settings**\. For a summary of these options, see [Advanced cluster settings](#createdomain-configure-advanced-options)\.

1. Choose **Create**\.

### Creating OpenSearch Service domains \(AWS CLI\)<a name="createdomains-cli"></a>

Instead of creating an OpenSearch Service domain by using the console, you can use the AWS CLI\. For syntax, see Amazon OpenSearch Service in the [AWS CLI command reference](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/opensearch/index.html)a\.

#### Example commands<a name="createdomains-cli-examples"></a>

This first example demonstrates the following OpenSearch Service domain configuration:
+ Creates an OpenSearch Service domain named *mylogs* with OpenSearch version 1\.2
+ Populates the domain with two instances of the `r6g.large.search` instance type
+ Uses a 100 GiB General Purpose \(SSD\) `gp3` EBS volume for storage for each data node
+ Allows anonymous access, but only from a single IP address: 192\.0\.2\.0/32

```
aws opensearch create-domain --domain-name mylogs --engine-version OpenSearch_1.2 --cluster-config  InstanceType=r6g.large.search,InstanceCount=2 --ebs-options EBSEnabled=true,VolumeType=gp3,VolumeSize=100,Iops=3500,Throughput=125 --access-policies '{"Version": "2012-10-17", "Statement": [{"Action": "es:*", "Principal":"*","Effect": "Allow", "Condition": {"IpAddress":{"aws:SourceIp":["192.0.2.0/32"]}}}]}'
```

The next example demonstrates the following OpenSearch Service domain configuration:
+ Creates an OpenSearch Service domain named *mylogs* with Elasticsearch version 7\.10
+ Populates the domain with six instances of the `r6g.large.search` instance type
+ Uses a 100 GiB General Purpose \(SSD\) `gp2` EBS volume for storage for each data node
+ Restricts access to the service to a single user, identified by the user's AWS account ID: 555555555555 
+ Distributes instances across three Availability Zones

```
aws opensearch create-domain --domain-name mylogs --engine-version Elasticsearch_7.10 --cluster-config  InstanceType=r6g.large.search,InstanceCount=6,ZoneAwarenessEnabled=true,ZoneAwarenessConfig={AvailabilityZoneCount=3} --ebs-options EBSEnabled=true,VolumeType=gp2,VolumeSize=100 --access-policies '{"Version": "2012-10-17", "Statement": [ { "Effect": "Allow", "Principal": {"AWS": "arn:aws:iam::555555555555:root" }, "Action":"es:*", "Resource": "arn:aws:es:us-east-1:555555555555:domain/mylogs/*" } ] }'
```

The next example demonstrates the following OpenSearch Service domain configuration:
+ Creates an OpenSearch Service domain named *mylogs* with OpenSearch version 1\.0
+ Populates the domain with ten instances of the `r6g.xlarge.search` instance type
+ Populates the domain with three instances of the `r6g.large.search` instance type to serve as dedicated master nodes
+ Uses a 100 GiB Provisioned IOPS EBS volume for storage, configured with a baseline performance of 1000 IOPS for each data node
+ Restricts access to a single user and to a single subresource, the `_search` API

```
aws opensearch create-domain --domain-name mylogs --engine-version OpenSearch_1.0 --cluster-config  InstanceType=r6g.xlarge.search,InstanceCount=10,DedicatedMasterEnabled=true,DedicatedMasterType=r6g.large.search,DedicatedMasterCount=3 --ebs-options EBSEnabled=true,VolumeType=io1,VolumeSize=100,Iops=1000 --access-policies '{"Version": "2012-10-17", "Statement": [ { "Effect": "Allow", "Principal": { "AWS": "arn:aws:iam::555555555555:root" }, "Action": "es:*", "Resource": "arn:aws:es:us-east-1:555555555555:domain/mylogs/_search" } ] }'
```

**Note**  
If you attempt to create an OpenSearch Service domain and a domain with the same name already exists, the CLI does not report an error\. Instead, it returns details for the existing domain\.

### Creating OpenSearch Service domains \(AWS SDKs\)<a name="createdomains-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all the actions defined in the [Amazon OpenSearch Service API Reference](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/Welcome.html), including `CreateDomain`\. For sample code, see [Using the AWS SDKs to interact with Amazon OpenSearch Service](configuration-samples.md)\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\.

### Creating OpenSearch Service domains \(AWS CloudFormation\)<a name="createdomains-cfn"></a>

OpenSearch Service is integrated with AWS CloudFormation, a service that helps you to model and set up your AWS resources so that you can spend less time creating and managing your resources and infrastructure\. You create a template that describes the OpenSearch domain you want to create, and CloudFormation provisions and configures the domain for you\. For more information, including examples of JSON and YAML templates for OpenSearch domains, see the [Amazon OpenSearch Service resource type reference](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-elasticsearch-domain.html) in the *AWS CloudFormation User Guide*\.

## Configuring access policies<a name="createdomain-configure-access-policies"></a>

Amazon OpenSearch Service offers several ways to configure access to your OpenSearch Service domains\. For more information, see [Identity and Access Management in Amazon OpenSearch Service](ac.md) and [Fine\-grained access control in Amazon OpenSearch Service](fgac.md)\.

The console provides preconfigured access policies that you can customize for the specific needs of your domain\. You also can import access policies from other OpenSearch Service domains\. For information about how these access policies interact with VPC access, see [About access policies on VPC domains](vpc.md#vpc-security)\.

**To configure access policies \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Amazon OpenSearch Service**\.

1. In the navigation pane, under **Domains**, choose the domain you want to update\.

1. Choose **Actions** and **Edit security configuration**\.

1. Edit the access policy JSON, or import a preconfigured option\.

1. Choose **Save changes**\.

## Advanced cluster settings<a name="createdomain-configure-advanced-options"></a>

Use advanced options to configure the following:

**Indices in request bodies**  
Specifies whether explicit references to indexes are allowed inside the body of HTTP requests\. Setting this property to `false` prevents users from bypassing access control for subresources\. By default, the value is `true`\. For more information, see [Advanced options and API considerations](ac.md#ac-advanced)\.

**Fielddata cache allocation**  
Specifies the percentage of Java heap space that is allocated to field data\. By default, this setting is 20% of the JVM heap\.  
Many customers query rotating daily indices\. We recommend that you begin benchmark testing with `indices.fielddata.cache.size` configured to 40% of the JVM heap for most of these use cases\. For very large indices, you might need a large field data cache\.

**Max clause count**  
Specifies the maximum number of clauses allowed in a Lucene boolean query\. The default is 1,024\. Queries with more than the permitted number of clauses result in a `TooManyClauses` error\. For more information, see [the Lucene documentation](https://lucene.apache.org/core/6_6_0/core/org/apache/lucene/search/BooleanQuery.html)\.