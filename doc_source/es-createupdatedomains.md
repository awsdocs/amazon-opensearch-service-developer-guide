# Creating and Managing Amazon Elasticsearch Service Domains<a name="es-createupdatedomains"></a>

This chapter describes how to create and manage Amazon Elasticsearch Service \(Amazon ES\) domains\. An Amazon ES domain is synonymous with an Elasticsearch cluster\. Domains are clusters with the settings, instance types, instance counts, and storage resources that you specify\.

Unlike the brief instructions in the [Getting Started](es-gsg.md) tutorial, this chapter describes all options and provides relevant reference information\. You can complete each procedure by using instructions for the Amazon ES console, the AWS Command Line Interface \(AWS CLI\), or the AWS SDKs\.

## Creating Amazon ES Domains<a name="es-createdomains"></a>

This section describes how to create Amazon ES domains by using the Amazon ES console or by using the AWS CLI with the `create-elasticsearch-domain` command\.

### Creating Amazon ES Domains \(Console\)<a name="es-createdomains-console"></a>

Use the following procedure to create an Amazon ES domain by using the console\.

**To create an Amazon ES domain \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. Choose **Create a new domain**\.

1. For **Choose deployment type**, choose the option that best matches the purpose of your domain:
   + **Production** domains use Multi\-AZ and dedicated master nodes for higher availability\.
   + **Development and testing** domains use a single Availability Zone\.
   + **Custom** domains let you choose from all configuration options\.
**Important**  
Different deployment types present different options on subsequent pages\. These steps include all options \(the **Custom** deployment type\)\.

1. For **Elasticsearch version**, we recommend that you choose the latest version\. For more information, see [Supported Elasticsearch Versions](what-is-amazon-elasticsearch-service.md#aes-choosing-version)\.

1. Choose **Next**\.

1. For **Elasticsearch domain name**, enter a domain name\. The name must meet the following criteria:
   + Unique to your account and Region
   + Starts with a lowercase letter
   + Contains between 3 and 28 characters
   + Contains only lowercase letters a\-z, the numbers 0\-9, and the hyphen \(\-\)

1. For **Availability Zones**, choose **1\-AZ**, **2\-AZ**, or **3\-AZ**\. For more information, see [Configuring a Multi\-AZ Domain](es-managedomains-multiaz.md)\.

1. For **Instance type**, choose an instance type for the data nodes\. For more information, see [Supported Instance Types](aes-supported-instance-types.md)\.
**Note**  
Not all Availability Zones support all instance types\. If you choose **3\-AZ**, we recommend choosing current\-generation instance types such as R5 or I3\.

1. For **Number of nodes**, choose the number of data nodes\.

   For maximum values, see [Cluster and Instance Limits](aes-limits.md#clusterresource)\. Single\-node clusters are fine for development and testing, but should not be used for production workloads\. For more guidance, see [Sizing Amazon ES Domains](sizing-domains.md) and [Configuring a Multi\-AZ Domain](es-managedomains-multiaz.md)\.

1. For **Data nodes storage type**, choose either **Instance** \(default\) or **EBS**\.

   For guidance on creating especially large domains, see [Petabyte Scale](petabyte-scale.md)\. If you choose **EBS**, the following options appear:

   1. For **EBS volume type**, choose an EBS volume type\.

      If you choose **Provisioned IOPS \(SSD\)** for the EBS volume type, for **Provisioned IOPS**, enter the baseline IOPS performance that you want\. For more information, see [Amazon EBS Volumes](http://docs.aws.amazon.com/AWSEC2/latest/WindowsGuide/EBSVolumes.html) in the Amazon EC2 documentation\.

   1.  For **EBS storage size per node**, enter the size of the EBS volume that you want to attach to each data node\.

      **EBS volume size** is per node\. You can calculate the total cluster size for the Amazon ES domain by multiplying the number of data nodes by the EBS volume size\. The minimum and maximum size of an EBS volume depends on both the specified EBS volume type and the instance type that it's attached to\. To learn more, see [EBS Volume Size Limits](aes-limits.md#ebsresource)\.

1. \(Optional\) Enable or disable [dedicated master nodes](es-managedomains-dedicatedmasternodes.md)\. Dedicated master nodes increase cluster stability and are required for domains that have instance counts greater than 10\. We recommend three dedicated master nodes for production domains\.
**Note**  
You can choose different instance types for your dedicated master nodes and data nodes\. For example, you might select general purpose or storage\-optimized instances for your data nodes, but compute\-optimized instances for your dedicated master nodes\.

1. \(Optional\) To enable [UltraWarm storage](ultrawarm.md), choose **Enable UltraWarm data nodes**\. Each instance type has a [maximum amount of storage](aes-limits.md#limits-ultrawarm) that it can address\. Multiply that amount by the number of warm data nodes for the total addressable warm storage\.

1. \(Optional\) For domains running Elasticsearch 5\.3 and later, **Automated snapshot start hour** has no effect\. For more information about automated snapshots, see [Working with Amazon Elasticsearch Service Index Snapshots](es-managedomains-snapshots.md)\.

1. \(Optional\) Choose **Optional Elasticsearch cluster settings**\. For a summary of these options, see [Advanced Options](#es-createdomain-configure-advanced-options)\.

1. Choose **Next**\.

1. In the **Network configuration** section, choose either **VPC access** or **Public access**\. If you choose **Public access**, skip to the next step\. If you choose **VPC access**, ensure that you have met the [prerequisites](es-vpc.md#es-prerequisites-vpc-endpoints), and then do the following:

   1. For **VPC**, choose the ID of the VPC that you want to use\.
**Note**  
The VPC and domain must be in the same AWS Region, and you must select a VPC with tenancy set to **Default**\. Amazon ES does not yet support VPCs that use dedicated tenancy\.

   1. For **Subnet**, choose a subnet\. If you enabled Multi\-AZ, you must choose two or three subnets\. Amazon ES will place a VPC endpoint and *elastic network interfaces* in the subnets\.
**Note**  
You must reserve sufficient IP addresses for the network interfaces in the subnet \(or subnets\)\. For more information, see [Reserving IP Addresses in a VPC Subnet](es-vpc.md#es-reserving-ip-vpc-endpoints)\.

   1. For **Security groups**, choose the VPC security groups that need access to the Amazon ES domain\. For more information, see [VPC Support for Amazon Elasticsearch Service Domains](es-vpc.md)\.

   1. For **IAM role**, keep the default role\. Amazon ES uses this predefined role \(also known as a *service\-linked role*\) to access your VPC and to place a VPC endpoint and network interfaces in the subnet of the VPC\. For more information, see [Service\-Linked Role for VPC Access](es-vpc.md#es-enabling-slr)\.

1. In the **Fine\-grained access control** section, enable or disable fine\-grained access control:
   + If you want to use IAM for user management, choose **Set IAM role as master user** and specify the ARN for an IAM role\.
   + If you want to use the internal user database, choose **Create a master user** and specify a user name and password\.

   Whichever option you choose, the master user can access all indices in the cluster and all Elasticsearch APIs\. For guidance on which option to choose, see [Key Concepts](fgac.md#fgac-concepts)\.

   If you disable fine\-grained access control, you can still control access to your domain by placing it within a VPC, applying a restrictive access policy, or both\. You must enable node\-to\-node encryption and encryption at rest to use fine\-grained access control\.

1. \(Optional\) If you want to use Amazon Cognito authentication for Kibana, choose **Enable Amazon Cognito authentication**\.

   1. Choose the Amazon Cognito user pool and identity pool that you want to use for Kibana authentication\. For guidance on creating these resources, see [Amazon Cognito Authentication for Kibana](es-cognito-auth.md)\.

1. For **Domain access policy**, add the ARNs or IP addresses that you want or choose a preconfigured policy from the dropdown list\. For more information, see [Identity and Access Management in Amazon Elasticsearch Service](es-ac.md) and [About Access Policies on VPC Domains](es-vpc.md#es-vpc-security)\.
**Note**  
If you chose **VPC access** in step 17, IP\-based policies are prohibited\. Instead, you can use [security groups](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_SecurityGroups.html) to control which IP addresses can access the domain\. For more information, see [About Access Policies on VPC Domains](es-vpc.md#es-vpc-security)\.

1. \(Optional\) To require that all requests to the domain arrive over HTTPS, select the **Require HTTPS for all traffic to the domain** check box\.

1. \(Optional\) To enable node\-to\-node encryption, select the **Node\-to\-node encryption** check box\. For more information, see [Node\-to\-node Encryption for Amazon Elasticsearch Service](ntn.md)\.

1. \(Optional\) To enable encryption of data at rest, select the **Enable encryption of data at rest** check box\.

   Select **\(Default\) aws/es** to have Amazon ES create a KMS encryption key on your behalf \(or use the one that it already created\)\. Otherwise, choose your own KMS encryption key from the **KMS master key** menu\. For more information, see [Encryption of Data at Rest for Amazon Elasticsearch Service](encryption-at-rest.md)\.

1. Choose **Next**\.

1. On the **Review** page, review your domain configuration, and then choose **Confirm**\.

### Creating Amazon ES Domains \(AWS CLI\)<a name="es-createdomains-cli"></a>

Instead of creating an Amazon ES domain by using the console, you can use the AWS CLI\. For syntax, see Amazon Elasticsearch Service in the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/es/index.html)\.

#### Example Commands<a name="es-createdomains-cli-examples"></a>

This first example demonstrates the following Amazon ES domain configuration:
+ Creates an Amazon ES domain named *mylogs* with Elasticsearch version 5\.5
+ Populates the domain with two instances of the `m4.large.elasticsearch` instance type
+ Uses a 100 GiB Magnetic disk EBS volume for storage for each data node
+ Allows anonymous access, but only from a single IP address: 192\.0\.2\.0/32

```
aws es create-elasticsearch-domain --domain-name mylogs --elasticsearch-version 5.5 --elasticsearch-cluster-config  InstanceType=m4.large.elasticsearch,InstanceCount=2 --ebs-options EBSEnabled=true,VolumeType=standard,VolumeSize=100 --access-policies '{"Version": "2012-10-17", "Statement": [{"Action": "es:*", "Principal":"*","Effect": "Allow", "Condition": {"IpAddress":{"aws:SourceIp":["192.0.2.0/32"]}}}]}'
```

The next example demonstrates the following Amazon ES domain configuration:
+ Creates an Amazon ES domain named *mylogs* with Elasticsearch version 5\.5
+ Populates the domain with six instances of the `m4.large.elasticsearch` instance type
+ Uses a 100 GiB General Purpose \(SSD\) EBS volume for storage for each data node
+ Restricts access to the service to a single user, identified by the user's AWS account ID: 555555555555 
+ Distributes instances across three Availability Zones

```
aws es create-elasticsearch-domain --domain-name mylogs --elasticsearch-version 5.5 --elasticsearch-cluster-config  InstanceType=m4.large.elasticsearch,InstanceCount=6,ZoneAwarenessEnabled=true,ZoneAwarenessConfig={AvailabilityZoneCount=3} --ebs-options EBSEnabled=true,VolumeType=gp2,VolumeSize=100 --access-policies '{"Version": "2012-10-17", "Statement": [ { "Effect": "Allow", "Principal": {"AWS": "arn:aws:iam::555555555555:root" }, "Action":"es:*", "Resource": "arn:aws:es:us-east-1:555555555555:domain/mylogs/*" } ] }'
```

The next example demonstrates the following Amazon ES domain configuration:
+ Creates an Amazon ES domain named *mylogs* with Elasticsearch version 5\.5
+ Populates the domain with ten instances of the `m4.xlarge.elasticsearch` instance type
+ Populates the domain with three instances of the `m4.large.elasticsearch` instance type to serve as dedicated master nodes
+ Uses a 100 GiB Provisioned IOPS EBS volume for storage, configured with a baseline performance of 1000 IOPS for each data node
+ Restricts access to a single user and to a single subresource, the `_search` API

```
aws es create-elasticsearch-domain --domain-name mylogs --elasticsearch-version 5.5 --elasticsearch-cluster-config  InstanceType=m4.xlarge.elasticsearch,InstanceCount=10,DedicatedMasterEnabled=true,DedicatedMasterType=m4.large.elasticsearch,DedicatedMasterCount=3 --ebs-options EBSEnabled=true,VolumeType=io1,VolumeSize=100,Iops=1000 --access-policies '{"Version": "2012-10-17", "Statement": [ { "Effect": "Allow", "Principal": { "AWS": "arn:aws:iam::555555555555:root" }, "Action": "es:*", "Resource": "arn:aws:es:us-east-1:555555555555:domain/mylogs/_search" } ] }'
```

**Note**  
If you attempt to create an Amazon ES domain and a domain with the same name already exists, the CLI does not report an error\. Instead, it returns details for the existing domain\.

### Creating Amazon ES Domains \(AWS SDKs\)<a name="es-createdomains-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all the actions defined in the [Amazon ES Configuration API Reference](es-configuration-api.md), including `CreateElasticsearchDomain`\. For sample code, see [Using the AWS SDKs with Amazon Elasticsearch Service](es-configuration-samples.md)\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\.

## Configuring Access Policies<a name="es-createdomain-configure-access-policies"></a>

Amazon Elasticsearch Service offers several ways to configure access to your Amazon ES domains\. For more information, see [Identity and Access Management in Amazon Elasticsearch Service](es-ac.md) and [Fine\-Grained Access Control in Amazon Elasticsearch Service](fgac.md)\.

The console provides preconfigured access policies that you can customize for the specific needs of your domain\. You also can import access policies from other Amazon ES domains\. For information about how these access policies interact with VPC access, see [About Access Policies on VPC Domains](es-vpc.md#es-vpc-security)\.

**To configure access policies \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose the domain that you want to update\.

1. Choose **Actions** and **Modify access policy**\.

1. Edit the access policy JSON, or use the dropdown list to choose a preconfigured option\.

1. Choose **Submit**\.

## Advanced Options<a name="es-createdomain-configure-advanced-options"></a>

Use advanced options to configure the following:

**rest\.action\.multi\.allow\_explicit\_index**  
Specifies whether explicit references to indices are allowed inside the body of HTTP requests\. Setting this property to `false` prevents users from bypassing access control for subresources\. By default, the value is `true`\. For more information, see [Advanced Options and API Considerations](es-ac.md#es-ac-advanced)\.

**indices\.fielddata\.cache\.size**  
Specifies the percentage of Java heap space that is allocated to field data\. By default, this setting is unbounded\.   
Many customers query rotating daily indices\. We recommend that you begin benchmark testing with `indices.fielddata.cache.size` configured to 40% of the JVM heap for most such use cases\. However, if you have very large indices you might need a large field data cache\.

**indices\.query\.bool\.max\_clause\_count**  
Specifies the maximum number of clauses allowed in a Lucene boolean query\. The default is 1,024\. Queries with more than the permitted number of clauses result in a `TooManyClauses` error\. For more information, see [the Lucene documentation](https://lucene.apache.org/core/6_6_0/core/org/apache/lucene/search/BooleanQuery.html)\.
