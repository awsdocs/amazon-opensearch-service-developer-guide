# Creating and Configuring Amazon Elasticsearch Service Domains<a name="es-createupdatedomains"></a>

This chapter describes how to create and configure Amazon Elasticsearch Service \(Amazon ES\) domains\. An Amazon ES domain is synonymous with an Elasticsearch cluster\. Domains are clusters with the settings, instance types, instance counts, and storage resources that you specify\.

Unlike the brief instructions in the [Getting Started](es-gsg.md) tutorial, this chapter describes all options and provides relevant reference information\. You can complete each procedure by using instructions for the Amazon ES console, the AWS Command Line Interface \(AWS CLI\), or the AWS SDKs\.

**Topics**
+ [Creating Amazon ES Domains](#es-createdomains)
+ [Configuring Amazon ES Domains](#es-createdomains-configure-cluster)
+ [Configuring EBS\-based Storage](#es-createdomain-configure-ebs)
+ [Modifying VPC Access Configuration](#es-createdomain-configure-vpc-endpoints)
+ [Configuring Amazon Cognito Authentication for Kibana](#es-createdomain-configure-cognito-auth)
+ [Configuring Access Policies](#es-createdomain-configure-access-policies)
+ [Configuring Advanced Options](#es-createdomain-configure-advanced-options)
+ [Configuring Logs](#es-createdomain-configure-slow-logs)

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
   + **UltraWarm Preview** domains let you enable [UltraWarm for Amazon Elasticsearch Service \(Preview\)](ultrawarm.md)\.
**Important**  
Different deployment types present different options on subsequent pages\. These steps include all options \(the **Custom** deployment type\)\.

1. For **Elasticsearch version**, we recommend that you choose the latest version\. For more information, see [Supported Elasticsearch Versions](what-is-amazon-elasticsearch-service.md#aes-choosing-version)\.

1. Choose **Next**\.

1. For **Elasticsearch domain name**, enter a domain name\. The name must meet the following criteria:
   + Unique to your account and Region
   + Starts with a lowercase letter
   + Contains between 3 and 28 characters
   + Contains only lowercase letters a\-z, the numbers 0\-9, and the hyphen \(\-\)

1. For **Availability Zones**, choose **1\-AZ**, **2\-AZ**, or **3\-AZ**\. For more information, see [Configuring a Multi\-AZ Domain](es-managedomains.md#es-managedomains-multiaz)\.

1. For **Instance type**, choose an instance type for the data nodes\. For more information, see [Supported Instance Types](aes-supported-instance-types.md)\.
**Note**  
Not all Availability Zones support all instance types\. If you choose **3\-AZ**, we recommend choosing current\-generation instance types such as R5 or I3\.

1. For **Number of nodes**, choose the number of data nodes\.

   For maximum values, see [Cluster and Instance Limits](aes-limits.md#clusterresource)\. Single\-node clusters are fine for development and testing, but should not be used for production workloads\. For more guidance, see [Sizing Amazon ES Domains](sizing-domains.md) and [Configuring a Multi\-AZ Domain](es-managedomains.md#es-managedomains-multiaz)\.

1. For **Data nodes storage type**, choose either **Instance** \(default\) or **EBS**\.

   For guidance on creating especially large domains, see [Petabyte Scale](petabyte-scale.md)\. If you choose **EBS**, the following options appear:

   1. For **EBS volume type**, choose an EBS volume type\.

      If you choose **Provisioned IOPS \(SSD\)** for the EBS volume type, for **Provisioned IOPS**, enter the baseline IOPS performance that you want\. For more information, see [Amazon EBS Volumes](http://docs.aws.amazon.com/AWSEC2/latest/WindowsGuide/EBSVolumes.html) in the Amazon EC2 documentation\.

   1.  For **EBS storage size per node**, enter the size of the EBS volume that you want to attach to each data node\.

      **EBS volume size** is per node\. You can calculate the total cluster size for the Amazon ES domain by multiplying the number of data nodes by the EBS volume size\. The minimum and maximum size of an EBS volume depends on both the specified EBS volume type and the instance type that it's attached to\. To learn more, see [EBS Volume Size Limits](aes-limits.md#ebsresource)\.

1. \(Optional\) Enable or disable [dedicated master nodes](es-managedomains-dedicatedmasternodes.md)\. Dedicated master nodes increase cluster stability and are required for domains that have instance counts greater than 10\. We recommend three dedicated master nodes for production domains\.
**Note**  
You can choose different instance types for your dedicated master nodes and data nodes\. For example, you might select general purpose or storage\-optimized instances for your data nodes, but compute\-optimized instances for your dedicated master nodes\.

1. \(Optional\) To preview [UltraWarm storage](ultrawarm.md), choose **Enable UltraWarm data nodes**\. Each instance type has a [maximum amount of storage](aes-limits.md#limits-ultrawarm) that it can address\. Multiply that amount by the number of warm data nodes for the total addressable warm storage\.

1. \(Optional\) For domains running Elasticsearch 5\.3 and later, **Automated snapshot start hour** has no effect\. For more information about automated snapshots, see [Working with Amazon Elasticsearch Service Index Snapshots](es-managedomains-snapshots.md)\.

1. \(Optional\) Choose **Optional Elasticsearch cluster settings**\. For a summary of these options, see [Configuring Advanced Options](#es-createdomain-configure-advanced-options)\.

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
   + If you want to user the internal user database, choose **Create a master user** and specify a user name and password\.

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

## Configuring Amazon ES Domains<a name="es-createdomains-configure-cluster"></a>

To meet the demands of increased traffic and data, you can update your Amazon ES domain configuration with any of the following changes:
+ Change the instance count
+ Change the instance type
+ Enable or disable dedicated master nodes
+ Enable or disable Multi\-AZ
+ Configure storage configuration
+ Change the VPC subnets and security groups
+ Configure advanced options

### Configuring Amazon ES Domains \(Console\)<a name="es-createdomains-configure-cluster-console"></a>

Use the following procedure to update your Amazon ES configuration by using the console\.

**To configure an Amazon ES domain \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose the domain that you want to update\.

1. Choose **Edit domain**\.

1. On the **Edit domain** page, update the configuration of the domain, and then choose **Submit**\.

### Configuring Amazon ES Domains \(AWS CLI\)<a name="es-createdomains-configure-cluster-cli"></a>

Use the `elasticsearch-cluster-config` option to configure your Amazon ES cluster by using the AWS CLI\. For syntax, see Amazon Elasticsearch Service in the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/es/index.html)\.

### Configuring Amazon ES Domains \(AWS SDKs\)<a name="es-createdomains-configure-cluster-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all the actions defined in the [Amazon ES Configuration API Reference](es-configuration-api.md), including `UpdateElasticsearchDomainConfig`\. For sample code, see [Using the AWS SDKs with Amazon Elasticsearch Service](es-configuration-samples.md)\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\.

## Configuring EBS\-based Storage<a name="es-createdomain-configure-ebs"></a>

An Amazon EBS volume is a block\-level storage device that you can attach to a single instance\. Amazon Elasticsearch Service supports the following EBS volume types:
+ General Purpose \(SSD\)
+ Provisioned IOPS \(SSD\)
+ Magnetic

**Note**  
When changing an EBS volume type from provisioned IOPS to non\-provisioned EBS volume types, set the IOPS value to `0`\.

**Warning**  
Currently, if the data node that is attached to an EBS volume fails, the EBS volume also fails\.

### Configuring EBS\-based Storage \(Console\)<a name="es-createdomain-configure-ebs-console"></a>

Use the following procedure to enable EBS\-based storage by using the console\.

**To enable EBS\-based storage \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose the domain that you want to configure\.

1. Choose **Edit domain**\.

1. For **Storage type**, choose **EBS**\.

1. For **EBS volume type**, choose an EBS volume type\.

   If you choose **Provisioned IOPS \(SSD\)** for the EBS volume type, for **Provisioned IOPS**, enter the baseline IOPS performance that you want\.

1. For **EBS volume size**, enter the size that you want for the EBS volume\.

   **EBS volume size** is per node\. You can calculate the total cluster size for the Amazon ES domain using the following formula: \(number of data nodes\) \* \(EBS volume size\)\. The minimum and maximum size of an EBS volume depends on both the specified EBS volume type and the instance type to which it is attached\. To learn more, see [EBS Volume Size Limits](aes-limits.md#ebsresource)\.

1. Choose **Submit**\. 

**Note**  
Set the IOPS value for a Provisioned IOPS EBS volume to no more than 30 times the maximum storage of the volume\. For example, if your volume has a maximum size of 100 GiB, you can't assign an IOPS value for it that is greater than 3,000\.

For more information, see [Amazon EBS Volumes](http://docs.aws.amazon.com/AWSEC2/latest/WindowsGuide/EBSVolumes.html) in the Amazon EC2 documentation\.

### Configuring EBS\-based Storage \(AWS CLI\)<a name="es-createdomain-configure-ebs-cli"></a>

Use the `--ebs-options` option to configure EBS\-based storage by using the AWS CLI\. The following syntax is used by both the `create-elasticsearch-domain` and `update-elasticsearch-domain-config` commands\.

**Syntax**

```
--ebs-options EBSEnabled=<value>,VolumeType=<value>,VolumeSize=<value>,IOPS=<value>
```


****  

| Parameter | Valid Values | Description | 
| --- | --- | --- | 
| EBSEnabled | true or false | Specifies whether to use an EBS volume for storage rather than the storage provided by the instance\. The default value is false\. | 
| VolumeType | Any of the following:[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-createupdatedomains.html) | The EBS volume type to use with the Amazon ES domain\. | 
| VolumeSize | Integer | Specifies the size of the EBS volume for each data node in GiB\. The minimum and maximum size of an EBS volume depends on both the specified EBS volume type and the instance type to which it is attached\. To see a table that shows the minimum and maximum EBS size for each instance type, see [Service Limits](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-limits.html)\.  | 
| IOPS | Integer | Specifies the baseline I/O performance for the EBS volume\. This parameter is used only by Provisioned IOPS \(SSD\) volumes\. The minimum value is 1,000\. The maximum value is 16,000\. | 

**Note**  
We recommend that you don't set the IOPS value for a Provisioned IOPS EBS volume to more than 30 times the maximum storage of the volume\. For example, if your volume has a maximum size of 100 GiB, you shouldn't assign an IOPS value for it that is greater than 3,000\. For more information, including use cases for each volume type, see [Amazon EBS Volume Types](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/EBSVolumeTypes.html) in the Amazon EC2 documentation\.

**Examples**

The following example creates a domain named `mylogs` with Elasticsearch version 5\.5 with a 10 GiB General Purpose EBS volume:

```
aws es create-elasticsearch-domain --domain-name=mylogs --elasticsearch-version 5.5 --ebs-options EBSEnabled=true,VolumeType=gp2,VolumeSize=10
```

However, you might need a larger EBS volume as the size of your search indices increases\. For example, you might opt for a 100 GiB Provisioned IOPS volume with a baseline I/O performance of 3,000 IOPS\. The following example updates the domain configuration with those changes:

```
aws es update-elasticsearch-domain-config --domain-name=mylogs --ebs-options EBSEnabled=true,VolumeType=io1,VolumeSize=100,IOPS=3000
```

### Configuring EBS\-based Storage \(AWS SDKs\)<a name="es-createdomain-configure-ebs-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all the actions defined in the [Amazon ES Configuration API Reference](es-configuration-api.md), including the `--ebs-options` parameter for `UpdateElasticsearchDomainConfig`\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\.

## Modifying VPC Access Configuration<a name="es-createdomain-configure-vpc-endpoints"></a>

If you configured a domain to reside within a VPC, you can modify the configuration using the Amazon ES console\. To migrate a public domain to a VPC domain, see [Migrating from Public Access to VPC Access](es-vpc.md#es-migrating-public-to-vpc)\.

### Configuring VPC Access \(Console\)<a name="es-createdomain-configure-vpc-endpoints-console"></a>

Use the following procedure to configure VPC access by using the console\.

**To configure VPC access \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose the domain that you want to configure\.

1. Choose **Edit domain**\.

1. In the **Network configuration** section, for **Subnets**, choose a subnet\. If you enabled Multi\-AZ, you must choose two or three subnets\. The subnets must be in different Availability Zones in the same Region\. For more information, see [VPC Support for Amazon Elasticsearch Service Domains](es-vpc.md)\.
**Note**  
You must reserve sufficient IP addresses for the network interfaces in the subnet \(or subnets\)\. For more information, see [Reserving IP Addresses in a VPC Subnet](es-vpc.md#es-reserving-ip-vpc-endpoints)\.

1. For **Security groups**, add the security groups that need access to the domain\. 

1. Choose **Submit**\.

## Configuring Amazon Cognito Authentication for Kibana<a name="es-createdomain-configure-cognito-auth"></a>

After creating a domain, you can enable or disable Amazon Cognito authentication for Kibana\. You can also change the user pool and identity pool\. For more information, see [Amazon Cognito Authentication for Kibana](es-cognito-auth.md)\.

## Configuring Access Policies<a name="es-createdomain-configure-access-policies"></a>

Amazon Elasticsearch Service offers several ways to configure access to your Amazon ES domains\. For more information, see [Identity and Access Management in Amazon Elasticsearch Service](es-ac.md)\.

The console provides preconfigured access policies that you can customize for the specific needs of your domain\. You also can import access policies from other Amazon ES domains\. For information about how these access policies interact with VPC access, see [About Access Policies on VPC Domains](es-vpc.md#es-vpc-security)\.

### Configuring Access Policies \(Console\)<a name="es-createdomain-configure-access-policies-console"></a>

Use the following procedure to configure access policies by using the console\.

**To configure access policies \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose the domain that you want to update\.

1. Choose **Modify access policy**\.

1. Edit the access policy\.

   Alternatively, choose one of the policy templates from the **Select a template** dropdown list, and then edit it as needed for your domain\.  
****    
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-createupdatedomains.html)

1. Choose **Submit**\.

### Configuring Access Policies \(AWS CLI\)<a name="es-createdomain-configure-access-policies-cli"></a>

Use the `--access-policies` option to configure access policies by using the AWS CLI\. The following syntax is used by both the `create-elasticsearch-domain` and `update-elasticsearch-domain-config` commands\.

**Syntax**

```
--access-policies=<value>
```


****  

| Parameter | Valid Values | Description | 
| --- | --- | --- | 
| \-\-access\-policies | JSON | Specifies the access policy for the Amazon ES domain\. | 

**Example**

The following resource\-based policy example restricts access to the service to a single user, identified by the user's AWS account ID, 555555555555, in the `Principal` policy element\. This user receives access to `index1`, but can't access other indices in the domain:

```
aws es update-elasticsearch-domain-config --domain-name mylogs --access-policies '{"Version": "2012-10-17", "Statement": [ { "Effect": "Allow","Principal": {"AWS": "arn:aws:iam::123456789012:root" },"Action":"es:*","Resource":"arn:aws:es:us-east-1:555555555555:domain/index1/*" } ] }'
```

**Tip**  
If you configure access policies using the AWS CLI, you can use one of many online tools to minify the JSON policy statement\.

### Configuring Access Policies \(AWS SDKs\)<a name="es-createdomain-configure-access-policies-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all the actions defined in the [Amazon ES Configuration API Reference](es-configuration-api.md), including the `--access-policies` parameter for `UpdateElasticsearchDomainConfig`\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\.

## Configuring Advanced Options<a name="es-createdomain-configure-advanced-options"></a>

Use advanced options to configure the following:

**rest\.action\.multi\.allow\_explicit\_index**  
Specifies whether explicit references to indices are allowed inside the body of HTTP requests\. Setting this property to `false` prevents users from bypassing access control for subresources\. By default, the value is `true`\. For more information, see [Advanced Options and API Considerations](es-ac.md#es-ac-advanced)\.

**indices\.fielddata\.cache\.size**  
Specifies the percentage of Java heap space that is allocated to field data\. By default, this setting is unbounded\.   
Many customers query rotating daily indices\. We recommend that you begin benchmark testing with `indices.fielddata.cache.size` configured to 40% of the JVM heap for most such use cases\. However, if you have very large indices you might need a large field data cache\.

**indices\.query\.bool\.max\_clause\_count**  
Specifies the maximum number of clauses allowed in a Lucene Boolean query\. The default is 1,024\. Queries with more than the permitted number of clauses result in a `TooManyClauses` error\. For more information, see [the Lucene documentation](https://lucene.apache.org/core/6_6_0/core/org/apache/lucene/search/BooleanQuery.html)\.

### Configuring Advanced Options \(Console\)<a name="es-createdomain-configure-advanced-options-console"></a>

**To configure advanced options \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose the domain that you want to update\.

1. Choose **Edit domain**\.

1. Choose **Advanced options**\.

1.  Specify the options that you want, and then choose **Submit**\.

### Configuring Advanced Options \(AWS CLI\)<a name="es-createdomain-configure-advanced-options-cli"></a>

Use the following syntax for the `--advanced-options` option\. The syntax for the option is the same for both the `create-elasticsearch-domain` and `update-elasticsearch-domain-config` commands\.

**Syntax**

```
--advanced-options rest.action.multi.allow_explicit_index=<true|false>, indices.fielddata.cache.size=<percentage_heap>, indices.query.bool.max_clause_count=<int>
```


****  
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-createupdatedomains.html)

**Example**

The following example disables explicit references to indices in the HTTP request bodies\. It also limits the field data cache to 40 percent of the total Java heap:

```
aws es update-elasticsearch-domain-config --domain-name mylogs --region us-east-1 --advanced-options rest.action.multi.allow_explicit_index=false, indices.fielddata.cache.size=40
```

### Configuring Advanced Options \(AWS SDKs\)<a name="es-createdomain-configure-advanced-options-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all the actions defined in the [Amazon ES Configuration API Reference](es-configuration-api.md), including the `--advanced-options` parameter for `UpdateElasticsearchDomainConfig`\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\.

## Configuring Logs<a name="es-createdomain-configure-slow-logs"></a>

Amazon ES exposes three Elasticsearch logs through Amazon CloudWatch Logs: error logs, search slow logs, and index slow logs\. These logs are useful for troubleshooting performance and stability issues, but are *disabled* by default\. If enabled, [standard CloudWatch pricing](https://aws.amazon.com/cloudwatch/pricing/) applies\.

**Note**  
Error logs are available only for Elasticsearch versions 5\.1 and greater\. Slow logs are available for all Elasticsearch versions\.

For its logs, Elasticsearch uses [Apache Log4j 2](https://logging.apache.org/log4j/2.x/) and its built\-in log levels \(from least to most severe\) of `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, and `FATAL`\.

If you enable error logs, Amazon ES publishes log lines of `WARN`, `ERROR`, and `FATAL` to CloudWatch\. Amazon ES also publishes several exceptions from the `DEBUG` level, including the following:
+ `org.elasticsearch.index.mapper.MapperParsingException`
+ `org.elasticsearch.index.query.QueryShardException`
+ `org.elasticsearch.action.search.SearchPhaseExecutionException`
+ `org.elasticsearch.common.util.concurrent.EsRejectedExecutionException`
+ `java.lang.IllegalArgumentException`

Error logs can help with troubleshooting in many situations, including the following:
+ Painless script compilation issues
+ Invalid queries
+ Indexing issues
+ Snapshot failures

### Enabling Log Publishing \(Console\)<a name="es-createdomain-configure-slow-logs-console"></a>

The Amazon ES console is the simplest way to enable the publishing of logs to CloudWatch\.

**To enable log publishing to CloudWatch \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose the domain that you want to update\.

1. On the **Logs** tab, choose **Enable** for the log that you want\. 

1. Create a CloudWatch log group, or choose an existing one\.
**Note**  
If you plan to enable multiple logs, we recommend publishing each to its own log group\. This separation makes the logs easier to scan\.

1. Choose an access policy that contains the appropriate permissions, or create a policy using the JSON that the console provides:

   ```
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Principal": {
           "Service": "es.amazonaws.com"
         },
         "Action": [
           "logs:PutLogEvents",
           "logs:CreateLogStream"
         ],
         "Resource": "cw_log_group_arn"
       }
     ]
   }
   ```
**Important**  
CloudWatch Logs supports [10 resource policies per Region](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_PutResourcePolicy.html)\. If you plan to enable logs for several Amazon ES domains, you should create and reuse a broader policy that includes multiple log groups to avoid reaching this limit\.

1. Choose **Enable**\.

   The status of your domain changes from **Active** to **Processing**\. The status must return to **Active** before log publishing is enabled\. This process can take up to 30 minutes\.

If you enabled one of the slow logs, see [Setting Elasticsearch Logging Thresholds for Slow Logs](#es-createdomain-configure-slow-logs-indices)\. If you enabled only error logs, you don't need to perform any additional configuration steps\.

### Enabling Log Publishing \(AWS CLI\)<a name="es-createdomain-configure-slow-logs-cli"></a>

Before you can enable log publishing, you need a CloudWatch log group\. If you don't already have one, you can create one using the following command:

```
aws logs create-log-group --log-group-name my-log-group
```

Enter the next command to find the log group's ARN, and then *make a note of it*:

```
aws logs describe-log-groups --log-group-name my-log-group
```

Now you can give Amazon ES permissions to write to the log group\. You must provide the log group's ARN near the end of the command:

```
aws logs put-resource-policy --policy-name my-policy --policy-document '{ "Version": "2012-10-17", "Statement": [{ "Sid": "", "Effect": "Allow", "Principal": { "Service": "es.amazonaws.com"}, "Action":[ "logs:PutLogEvents","logs:PutLogEventsBatch","logs:CreateLogStream"],"Resource": "cw_log_group_arn"}]}'
```

**Important**  
CloudWatch Logs supports [10 resource policies per Region](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_PutResourcePolicy.html)\. If you plan to enable slow logs for several Amazon ES domains, you should create and reuse a broader policy that includes multiple log groups to avoid reaching this limit\.

Finally, you can use the `--log-publishing-options` option to enable publishing\. The syntax for the option is the same for both the `create-elasticsearch-domain` and `update-elasticsearch-domain-config` commands\.


****  
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-createupdatedomains.html)

**Note**  
If you plan to enable multiple logs, we recommend publishing each to its own log group\. This separation makes the logs easier to scan\.

**Example**

The following example enables the publishing of search and index slow logs for the specified domain:

```
aws es update-elasticsearch-domain-config --domain-name my-domain --log-publishing-options "SEARCH_SLOW_LOGS={CloudWatchLogsLogGroupArn=arn:aws:logs:us-east-1:123456789012:log-group:my-log-group,Enabled=true},INDEX_SLOW_LOGS={CloudWatchLogsLogGroupArn=arn:aws:logs:us-east-1:123456789012:log-group:my-other-log-group,Enabled=true}"
```

To disable publishing to CloudWatch, run the same command with `Enabled=false`\.

If you enabled one of the slow logs, see [Setting Elasticsearch Logging Thresholds for Slow Logs](#es-createdomain-configure-slow-logs-indices)\. If you enabled only error logs, you don't need to perform any additional configuration steps\.

### Enabling Log Publishing \(AWS SDKs\)<a name="es-createdomain-configure-slow-logs-sdk"></a>

Before you can enable log publishing, you must first create a CloudWatch log group, get its ARN, and give Amazon ES permissions to write to it\. The relevant operations are documented in the [Amazon CloudWatch Logs API Reference](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/):
+ `CreateLogGroup`
+ `DescribeLogGroup`
+ `PutResourcePolicy`

You can access these operations using the [AWS SDKs](https://aws.amazon.com/tools/#sdk)\.

The AWS SDKs \(except the Android and iOS SDKs\) support all the operations that are defined in the [Amazon ES Configuration API Reference](es-configuration-api.md), including the `--log-publishing-options` option for `CreateElasticsearchDomain` and `UpdateElasticsearchDomainConfig`\.

If you enabled one of the slow logs, see [Setting Elasticsearch Logging Thresholds for Slow Logs](#es-createdomain-configure-slow-logs-indices)\. If you enabled only error logs, you don't need to perform any additional configuration steps\.

### Setting Elasticsearch Logging Thresholds for Slow Logs<a name="es-createdomain-configure-slow-logs-indices"></a>

Elasticsearch disables slow logs by default\. After you enable the *publishing* of slow logs to CloudWatch, you still must specify logging thresholds for each Elasticsearch index\. These thresholds define precisely what should be logged and at which log level\.

You specify these settings through the Elasticsearch REST API:

```
PUT elasticsearch_domain_endpoint/index/_settings
{
  "index.search.slowlog.threshold.query.warn": "5s",
  "index.search.slowlog.threshold.query.info": "2s"
}
```

To test that slow logs are publishing successfully, consider starting with very low values to verify that logs appear in CloudWatch, and then increase the thresholds to more useful levels\.

If the logs don't appear, check the following:
+ Does the CloudWatch log group exist? Check the CloudWatch console\.
+ Does Amazon ES have permissions to write to the log group? Check the Amazon ES console\.
+ Is the Amazon ES domain configured to publish to the log group? Check the Amazon ES console, use the AWS CLI `describe-elasticsearch-domain-config` option, or call `DescribeElasticsearchDomainConfig` using one of the SDKs\.
+ Are the Elasticsearch logging thresholds low enough that your requests are exceeding them? To review your thresholds for an index, use the following command:

  ```
  GET elasticsearch_domain_endpoint/index/_settings?pretty
  ```

If you want to disable slow logs for an index, return any thresholds that you changed to their default values of `-1`\.

Disabling publishing to CloudWatch using the Amazon ES console or AWS CLI does *not* stop Elasticsearch from generating logs; it only stops the *publishing* of those logs\. Be sure to check your index settings if you no longer need the slow logs\.

### Viewing Logs<a name="es-createdomain-configure-slow-logs-viewing"></a>

Viewing the application and slow logs in CloudWatch is just like viewing any other CloudWatch log\. For more information, see [View Log Data](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/Working-with-log-groups-and-streams.html#ViewingLogData) in the *Amazon CloudWatch Logs User Guide*\.

Here are some considerations for viewing the logs:
+ Amazon ES publishes only the first 255,000 characters of each line to CloudWatch\. Any remaining content is truncated\.
+ In CloudWatch, the log stream names have suffixes of `-index-slow-logs`, `-search-slow-logs`, and `-es-application-logs` to help identify their contents\.