# Creating and Configuring Amazon Elasticsearch Service Domains<a name="es-createupdatedomains"></a>

This chapter describes how to create and configure Amazon Elasticsearch Service \(Amazon ES\) domains\. An Amazon ES domain is the hardware, software, and data exposed by Amazon Elasticsearch Service endpoints\.

Unlike the brief instructions in the Getting Started tutorial, this chapter describes all options and provides relevant reference information\. You can complete each procedure by using instructions for the Amazon ES console, the AWS Command Line Interface \(AWS CLI\), or the AWS SDKs\.


+ [Creating Amazon ES Domains](#es-createdomains)
+ [Configuring Amazon ES Domains](#es-createdomains-configure-cluster)
+ [Configuring EBS\-based Storage](#es-createdomain-configure-ebs)
+ [Modifying VPC Access Configuration](#es-createdomain-configure-vpc-endpoints)
+ [Configuring Access Policies](#es-createdomain-configure-access-policies)
+ [Configuring Automatic Snapshots](#es-createdomain-configure-snapshots)
+ [Configuring Advanced Options](#es-createdomain-configure-advanced-options)
+ [Configuring Slow Logs](#es-createdomain-configure-slow-logs)

## Creating Amazon ES Domains<a name="es-createdomains"></a>

This section describes how to create Amazon ES domains by using the Amazon ES console or by using the AWS CLI with the `create-elasticsearch-domain` command\. The procedures for the AWS CLI include syntax and examples\.

### Creating Amazon ES Domains \(Console\)<a name="es-createdomains-console"></a>

Use the following procedure to create an Amazon ES domain by using the console\.

**To create an Amazon ES domain \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. Choose **Create a new domain**\.

   Alternatively, choose **Get Started** if this is the first Amazon ES domain that you will create for your AWS account\.

1. On the **Define domain** page, for **Domain name**, type a name for your domain\. The domain name must meet the following criteria:

   + Uniquely identifies a domain

   + Starts with a lowercase letter

   + Contains between 3 and 28 characters

   + Contains only lowercase letters a\-z, the numbers 0\-9, and the hyphen \(\-\)

1. For **Version**, choose an Elasticsearch version for your domain\. We recommend that you choose the latest version\. For more information, see [[ERROR] BAD/MISSING LINK TEXT](what-is-amazon-elasticsearch-service.md#aes-choosing-version)\.

1. Choose **Next**\.

1. For **Instance count**, choose the number of instances that you want\.

   The default is one\. For maximum values, see [[ERROR] BAD/MISSING LINK TEXT](aes-limits.md#clusterresource)\. We recommend a minimum of three instances to avoid potential Elasticsearch issues, such as the [split brain](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-node.html#split-brain) issue\. If you have three dedicated master nodes, we still recommend a minimum of two data nodes for [replication](https://www.elastic.co/guide/en/elasticsearch/reference/current/_basic_concepts.html#getting-started-shards-and-replicas)\. Single node clusters are fine for development and testing, but should not be used for production workloads\. For more guidance, see [[ERROR] BAD/MISSING LINK TEXT](sizing-domains.md)\.

1. For **Instance type**, choose an instance type for the data nodes\.

   To see a list of the instance types that Amazon ES supports, see Supported Instance Types\.

1. \(Optional\) If you must ensure cluster stability or if you have a domain that has more than 10 instances, enable a dedicated master node\. Dedicated master nodes increase cluster stability and are required for a domain that has an instance count greater than 10\. For more information, see About Dedicated Master Nodes\.

   1. Select the **Enable dedicated master** check box\.

   1. For **Dedicated master instance type**, choose an instance type for the dedicated master node\. 

      For a list of the instance types that Amazon ES supports, see Supported Instance Types\.
**Note**  
You can choose an instance type for the dedicated master node that differs from the instance type that you choose for the data nodes\. For example, you might select general purpose or storage\-optimized instances for your data nodes, but compute\-optimized instances for your dedicated master nodes\.

   1. For **Dedicated master instance count**, choose the number of instances for the dedicated master node\.

      We recommend choosing an odd number of instances to avoid potential Elasticsearch issues, such as the [split brain](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-node.html#split-brain) issue\. The default and recommended number is three\.

1. \(Optional\) To provide high availability for data nodes, select the **Enable zone awareness** check box\.

   Zone awareness distributes Amazon ES data nodes across two Availability Zones in the same region\. If you enable zone awareness, you must have an even number of instances in the instance count, and you must use the native Elasticsearch API to create replica shards for your cluster\. This process allows for the even distribution of shards across two Availability Zones\. For more information, see Enabling Zone Awareness\.

1. For **Storage type**, choose either **Instance** \(the default\) or **EBS**\. 

   If your Amazon ES domain requires more storage, use an EBS volume for storage rather than the storage that is attached to the selected instance type\. Domains with large indices or large numbers of indices often benefit from the increased storage capacity of EBS volumes\. For guidance on creating especially large domains, see Petabyte Scale\. If you choose **EBS**, the following boxes appear:

   1. For **EBS volume type**, choose an EBS volume type\.

      If you choose Provisioned IOPS \(SSD\) for the EBS volume type, for **Provisioned IOPS**, type the baseline IOPS performance that you want\. For more information, see [Amazon EBS Volumes](http://docs.aws.amazon.com/AWSEC2/latest/WindowsGuide/EBSVolumes.html) in the Amazon EC2 documentation\.

   1.  For **EBS volume size**, type the size of the EBS volume that you want to attach to each data node\.

      **EBS volume size** is per node\. You can calculate the total cluster size for the Amazon ES domain using the following formula: \(number of data nodes\) \* \(EBS volume size\)\. The minimum and maximum size of an EBS volume depends on both the specified EBS volume type and the instance type that it's attached to\. To learn more, see EBS Volume Size Limits\.

1. \(Optional\) To enable encryption of data at rest, select the **Enable encryption at rest** check box\.

   Select **\(Default\) aws/es** to have Amazon ES create a KMS encryption key on your behalf \(or use the one that it already created\)\. Otherwise, choose your own KMS encryption key from the **KMS master key** menu\. To learn more, see [[ERROR] BAD/MISSING LINK TEXT](encryption-at-rest.md)\.

1. For **Automated snapshot start hour**, choose the hour for automated daily snapshots of domain indices\.

   For more information and recommendations, see [[ERROR] BAD/MISSING LINK TEXT](#es-createdomain-configure-snapshots)\.

1. \(Optional\) Choose **Advanced options**\. For a summary of options, see [[ERROR] BAD/MISSING LINK TEXT](#es-createdomain-configure-advanced-options)

1. Choose **Next**\.

1. On the **Set up access** page, in the **Network configuration** section, choose either **Public Access** or **VPC access**\. If you choose **Public access**, skip to step 17\. If you choose **VPC access**, ensure that you have met the prerequisites, and then do the following:

   1. For **VPC**, choose the ID of the VPC that you want to use\.
**Note**  
The VPC and domain must be in the same AWS Region, and you must select a VPC with tenancy set to **Default**\. Amazon ES does not yet support VPCs that use dedicated tenancy\.

   1. For **Subnet**, choose a subnet\. If you enabled zone awareness in step 10, you must choose two subnets\. Amazon ES will place a VPC endpoint and *elastic network interfaces* \(ENIs\) in the subnet or subnets\.
**Note**  
You must reserve sufficient IP addresses for the network interfaces in the subnet \(or subnets\)\. For more information, see Reserving IP Addresses in a VPC Subnet\.

   1. For **Security groups**, choose the VPC security groups that need access to the Amazon ES domain\. For more information, see [[ERROR] BAD/MISSING LINK TEXT](es-vpc.md)\.

   1. For **IAM role**, keep the default role\. Amazon ES uses this predefined role \(also known as a *service\-linked role*\) to access your VPC and to place a VPC endpoint and network interfaces in the subnet of the VPC\. For more information, see Service\-Linked Role for VPC Access\.

1. For **Set the domain access policy to**, choose a preconfigured policy from the **Select a template** dropdown list and edit it to meet the needs of your domain\. Alternatively, you can add one or more Identity and Access Management \(IAM\) policy statements in the **Add or edit the access policy** box\. For more information, see [[ERROR] BAD/MISSING LINK TEXT](es-ac.md), [[ERROR] BAD/MISSING LINK TEXT](#es-createdomain-configure-access-policies), and [[ERROR] BAD/MISSING LINK TEXT](es-vpc.md#es-vpc-security)\.
**Note**  
If you chose **VPC access** in step 16, the IP\-based policy template is not available in the dropdown list, and you can't configure an IP\-based policy manually\. Instead, you can use [security groups](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_SecurityGroups.html) to control which IP addresses can access the domain\. To learn more, see [[ERROR] BAD/MISSING LINK TEXT](es-vpc.md#es-vpc-security)\.

1. Choose **Next**\.

1. On the **Review** page, review your domain configuration, and then choose **Confirm and create**\.

1. Choose **OK**\.

**Note**  
New domains take up to ten minutes to initialize\. After your domain is initialized, you can upload data and make changes to the domain\.

### Creating Amazon ES Domains \(AWS CLI\)<a name="es-createdomains-cli"></a>

Instead of creating an Amazon ES domain by using the console, you can create a domain by using the AWS CLI\. Use the following syntax to create an Amazon ES domain\.

```
aws es create-elasticsearch-domain --domain-name <value>

  [--elasticsearch-version <value>]
  [--elasticsearch-cluster-config <value>]
  [--ebs-options <value>]
  [--access-policies <value>]
  [--snapshot-options <value>]
  [--vpc-options <value>]
  [--advanced-options <value>]
  [--log-publishing-options <value>]
  [--cli-input-json <value>]
  [--generate-cli-skeleton <value>]
  [--encryption-at-rest-options <value>]
```

The following table provides more information about each of the optional parameters\.


****  

| Optional Parameter | Description | 
| --- | --- | 
| \-\-elasticsearch\-version | Specifies the Elasticsearch version of the domain\. If not specified, the default value is 1\.5\. For more information, see Choosing an Elasticsearch Version\. | 
| \-\-elasticsearch\-cluster\-config | Specifies the instance type and count of the domain, whether zone awareness is enabled, and whether the domain uses a dedicated master node\. Dedicated master nodes increase cluster stability and are required for a domain that has an instance count greater than 10\. For more information, see Configuring Amazon ES Domains\. | 
| \-\-ebs\-options | Specifies whether the domain uses an EBS volume for storage\. If true, this parameter must also specify the EBS volume type, size, and, if applicable, IOPS value\. For more information, see Configuring EBS\-based Storage\. | 
| \-\-access\-policies | Specifies the access policy for the domain\. For more information, see Configuring Access Policies\. | 
| \-\-snapshot\-options | Specifies the hour in UTC during which the service performs a daily automated snapshot of the indices in the domain\. The default value is 0, or midnight, which means that the snapshot is taken anytime between midnight and 1:00 AM\. For more information, see Configuring Snapshots\. | 
| \-\-advanced\-options | Specifies whether to allow references to indices in the bodies of HTTP request objects\. For more information, see  Configuring Advanced Options\.  | 
| \-\-generate\-cli\-skeleton | Displays JSON for all specified parameters\. Save the output to a file so that you can later read the file with the \-\-cli\-input\-json parameter rather than typing the parameters at the command line\. For more information, see [Generate CLI Skeleton and CLI Input JSON Parameters](http://docs.aws.amazon.com/cli/latest/userguide/generate-cli-skeleton.html) in the AWS Command Line Interface User Guide\. | 
| \-\-cli\-input\-json | Specifies the name of a JSON file that contains a set of CLI parameters\. For more information, see [Generate CLI Skeleton and CLI Input JSON Parameters](http://docs.aws.amazon.com/cli/latest/userguide/generate-cli-skeleton.html) in the AWS Command Line Interface User Guide\. | 
| \-\-log\-publishing\-options | Specifies whether Amazon ES should publish Elasticsearch slow logs to CloudWatch\. For more information, see [[ERROR] BAD/MISSING LINK TEXT](#es-createdomain-configure-slow-logs)\. | 
| \-\-vpc\-options | Specifies whether to launch the Amazon ES domain within an Amazon VPC \(VPC\)\. To learn more, see [[ERROR] BAD/MISSING LINK TEXT](es-vpc.md)\. | 
| \-\-encryption\-at\-rest\-options | Specifies whether to enable encryption of data at rest\. | 

**Examples**

The first example demonstrates the following Amazon ES domain configuration:

+ Creates an Amazon ES domain named *weblogs* with Elasticsearch version 5\.5

+ Populates the domain with two instances of the m4\.large\.elasticsearch instance type

+ Uses a 100 GB Magnetic disk EBS volume for storage for each data node

+ Allows anonymous access, but only from a single IP address: 192\.0\.2\.0/32

```
aws es create-elasticsearch-domain --domain-name weblogs --elasticsearch-version 5.5 --elasticsearch-cluster-config  InstanceType=m4.large.elasticsearch,InstanceCount=2 --ebs-options EBSEnabled=true,VolumeType=standard,VolumeSize=100 --access-policies '{"Version": "2012-10-17", "Statement": [{"Action": "es:*", "Principal":"*","Effect": "Allow", "Condition": {"IpAddress":{"aws:SourceIp":["192.0.2.0/32"]}}}]}'
```

The next example demonstrates the following Amazon ES domain configuration:

+ Creates an Amazon ES domain named *weblogs* with Elasticsearch version 5\.5

+ Populates the domain with six instances of the m4\.large\.elasticsearch instance type

+ Uses a 100 GB General Purpose \(SSD\) EBS volume for storage for each data node

+ Restricts access to the service to a single user, identified by the user's AWS account ID: 555555555555 

+ Enables zone awareness

```
aws es create-elasticsearch-domain --domain-name weblogs --elasticsearch-version 5.5 --elasticsearch-cluster-config  InstanceType=m4.large.elasticsearch,InstanceCount=6,ZoneAwarenessEnabled=true --ebs-options EBSEnabled=true,VolumeType=gp2,VolumeSize=100 --access-policies '{"Version": "2012-10-17", "Statement": [ { "Effect": "Allow", "Principal": {"AWS": "arn:aws:iam::555555555555:root" }, "Action":"es:*", "Resource": "arn:aws:es:us-east-1:555555555555:domain/logs/*" } ] }'
```

The next example demonstrates the following Amazon ES domain configuration:

+ Creates an Amazon ES domain named *weblogs* with Elasticsearch version 5\.5

+ Populates the domain with ten instances of the m4\.xlarge\.elasticsearch instance type

+ Populates the domain with three instances of the m4\.large\.elasticsearch instance type to serve as dedicated master nodes

+ Uses a 100 GB Provisioned IOPS EBS volume for storage, configured with a baseline performance of 1000 IOPS for each data node

+ Restricts access to a single user and to a single subresource, the `_search` API

+ Configures automated daily snapshots of the indices for 03:00 UTC 

```
aws es create-elasticsearch-domain --domain-name weblogs --elasticsearch-version 5.5 --elasticsearch-cluster-config  InstanceType=m4.xlarge.elasticsearch,InstanceCount=10,DedicatedMasterEnabled=true,DedicatedMasterType=m4.large.elasticsearch,DedicatedMasterCount=3 --ebs-options EBSEnabled=true,VolumeType=io1,VolumeSize=100,Iops=1000 --access-policies '{"Version": "2012-10-17", "Statement": [ { "Effect": "Allow", "Principal": { "AWS": "arn:aws:iam::555555555555:root" }, "Action": "es:*", "Resource": "arn:aws:es:us-east-1:555555555555:domain/mylogs/_search" } ] }' --snapshot-options AutomatedSnapshotStartHour=3
```

**Note**  
If you attempt to create an Amazon ES domain and a domain with the same name already exists, the CLI does not report an error\. Instead, it returns details for the existing domain\.

### Creating Amazon ES Domains \(AWS SDKs\)<a name="es-createdomains-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all the actions defined in the Amazon ES Configuration API Reference, including `CreateElasticsearchDomain`\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\. 

## Configuring Amazon ES Domains<a name="es-createdomains-configure-cluster"></a>

To meet the demands of increased traffic and data, you can update your Amazon ES domain configuration with any of the following changes:

+ Change the instance count

+ Change the instance type

+ Enable or disable dedicated master nodes

+ Enable or disable Zone Awareness

+ Configure storage configuration

+ Change the start time for automated snapshots of domain indices

+ Change the VPC subnets and security groups

+ Configure advanced options

**Note**  
For information about configuring a domain to use an EBS volume for storage, see Configuring EBS\-based Storage\. 

### Configuring Amazon ES Domains \(Console\)<a name="es-createdomains-configure-cluster-console"></a>

Use the following procedure to update your Amazon ES configuration by using the console\.

**To configure an Amazon ES domain \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose the domain that you want to update\.

1. Choose **Configure cluster**\.

1. On the **Configure cluster** page, update the configuration of the domain\.

   The cluster is a collection of one or more data nodes, optional dedicated master nodes, and storage required to run Amazon ES and operate your domain\.

   1. If you want to change the instance type for data nodes, for **Instance type**, choose a new instance type\. 

      To see a list of the instance types that Amazon ES supports, see Supported Instance Types\.

   1. If you want to change the instance count, for **Instance count**, choose an integer from one to twenty\. To request an increase up to 100 instances per domain, create a case with the [AWS Support Center](https://console.aws.amazon.com/support/home#/)\. 

   1. If you want to improve cluster stability or if your domain has an instance count greater than 10, enable a dedicated master node for your cluster\. For more information, see About Dedicated Master Nodes\.

      1. Select the **Enable dedicated master** check box\.

      1. For **Dedicated master instance type**, choose an instance type for the dedicated master node\.

         You can choose an instance type for the dedicated master node that differs from the instance type that you choose for the data nodes\.

         To see a list of the instance types that Amazon ES supports, see Supported Instance Types\. 

      1. For **Dedicated master instance count**, choose the number of instances for the dedicated master node\.

         We recommend choosing an odd number of instances to avoid potential Amazon ES issues, such as the [split brain](https://www.elastic.co/guide/en/elasticsearch/reference/5.3/modules-node.html#split-brain) issue\. The default and recommended number is three\.

   1. If you want to enable zone awareness, select the **Enable zone awareness** check box\. If you enable zone awareness, you must have an even number of instances in your instance count\. This allows for the even distribution of shards across two Availability Zones in the same region\.

   1. If you want to change the hour during which the service takes automated daily snapshots of the primary index shards of your Amazon ES domain, for **Automated snapshot start hour**, choose an integer\.

   1. If you didn't enable VPC access when you created the domain, skip to step 7\. If you enabled VPC access, you can change the subnet that the VPC endpoint is placed in, and you can change the security groups:

      1. For **Subnets**, choose a subnet\. The subnet must have a sufficient number of IP addresses reserved for the network interfaces\. If you enabled zone awareness, you must choose two subnets\. The subnets must be in different Availability Zones in the same region\. For more information, see [[ERROR] BAD/MISSING LINK TEXT](es-vpc.md)\.

      1. For **Security groups**, add the security groups that need access to the domain\.

   1. \(Optional\) Choose **Advanced options**\. For a summary of options, see [[ERROR] BAD/MISSING LINK TEXT](#es-createdomain-configure-advanced-options)

   1. Choose **Submit**\.

### Configuring Amazon ES Domains \(AWS CLI\)<a name="es-createdomains-configure-cluster-cli"></a>

Use the `elasticsearch-cluster-config` option to configure your Amazon ES cluster by using the AWS CLI\. The following syntax is used by both the `create-elasticsearch-domain` and `update-elasticsearch-domain-config` commands\.

**Syntax**

```
--elasticsearch-cluster-config InstanceType=<value>,InstanceCount=<value>,DedicatedMasterEnabled=<value>,DedicatedMasterType=<value>,DedicatedMasterCount=<value>,ZoneAwarenessEnabled=<value>
```

**Note**  
 Do not include spaces between parameters for the same option\.

The following table describes the parameters in more detail\.


****  

| Parameter | Valid Values | Description | 
| --- | --- | --- | 
| InstanceType | Any supported instance type\. See [Supported Instance Types](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-instance-types.html)\. | The hardware configuration of the computer that hosts the instance\. The default is m4\.large\.elasticsearch\. | 
| InstanceCount | Integer | The number of instances in the Amazon ES domain\. The default is one, and the maximum default limit is twenty\. To request an increase up to 100 instances per domain, create a case with the [AWS Support Center](https://console.aws.amazon.com/support/home#/)\.  | 
| DedicatedMasterEnabled | true or false | Specifies whether to use a dedicated master node for the Amazon ES domain\. The default value is false\. | 
| DedicatedMasterType | Any supported instance type | The hardware configuration of the computer that hosts the master node\. The default is m4\.large\.elasticsearch\. | 
| DedicatedMasterCount | Integer | The number of instances used for the dedicated master node\. The default is three\. | 
| ZoneAwarenessEnabled | true or false | Specifies whether to enable zone awareness for the Amazon ES domain\. The default value is false\. | 

**Examples**

The following example creates an Amazon ES domain named `mylogs` with Elasticsearch version 5\.5 with two instances of the m4\.large\.elasticsearch instance type and zone awareness enabled:

```
aws es create-elasticsearch-domain --domain-name mylogs --elasticsearch-version 5.5 --elasticsearch-cluster-config InstanceType=m4.large.elasticsearch,InstanceCount=2,DedicatedMasterEnabled=false,ZoneAwarenessEnabled=true
```

However, you likely will want to reconfigure your new Amazon ES domain as network traffic grows and as the quantity and size of documents increase\. For example, you might decide to use a larger instance type, use more instances, and enable a dedicated master node\. The following example updates the domain configuration with these changes:

```
aws es update-elasticsearch-domain-config --domain-name mylogs --elasticsearch-cluster-config InstanceType=m4.xlarge.elasticsearch,InstanceCount=3,DedicatedMasterEnabled=true,DedicatedMasterType=m4.large.elasticsearch,DedicatedMasterCount=3
```

### Configuring Amazon ES Domains \(AWS SDKs\)<a name="es-createdomains-configure-cluster-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all the actions defined in the Amazon ES Configuration API Reference, including `UpdateElasticsearchDomainConfig`\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\.

## Configuring EBS\-based Storage<a name="es-createdomain-configure-ebs"></a>

An Amazon EBS volume is a block\-level storage device that you can attach to a single instance\. EBS volumes enable you to independently scale the storage resources of your Amazon ES domain from its compute resources\. EBS volumes are most useful for domains with large datasets, but without the need for large compute resources\. EBS volumes are much larger than the default storage provided by the instance\. Amazon Elasticsearch Service supports the following EBS volume types:

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

1. Choose **Configure cluster**\.

1. For **Storage type**, choose **EBS**\.

1. For **EBS volume type**, choose an EBS volume type\.

   If you choose **Provisioned IOPS \(SSD\)** for the EBS volume type, for **Provisioned IOPS**, type the baseline IOPS performance that you want\.

1. For **EBS volume size**, type the size that you want for the EBS volume\.

   **EBS volume size** is per node\. You can calculate the total cluster size for the Amazon ES domain using the following formula: \(number of data nodes\) \* \(EBS volume size\)\. The minimum and maximum size of an EBS volume depends on both the specified EBS volume type and the instance type to which it is attached\. To learn more, see EBS Volume Size Limits\.

1. Choose **Submit**\. 

**Note**  
Set the IOPS value for a Provisioned IOPS EBS volume to no more than 30 times the maximum storage of the volume\. For example, if your volume has a maximum size of 100 GB, you can't assign an IOPS value for it that is greater than 3000\.

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
| VolumeSize | Integer | Specifies the size of the EBS volume for each data node\. The minimum and maximum size of an EBS volume depends on both the specified EBS volume type and the instance type to which it is attached\. To see a table that shows the minimum and maximum EBS size for each instance type, see [Service Limits](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-limits.html)\.  | 
| IOPS | Integer | Specifies the baseline I/O performance for the EBS volume\. This parameter is used only by Provisioned IOPS \(SSD\) volumes\. The minimum value is 1000\. The maximum value is 16000\. | 

**Note**  
We recommend that you do not set the IOPS value for a Provisioned IOPS EBS volume to more than 30 times the maximum storage of the volume\. For example, if your volume has a maximum size of 100 GB, you should not assign an IOPS value for it that is greater than 3000\. For more information, including use cases for each volume type, see [Amazon EBS Volume Types](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/EBSVolumeTypes.html) in the Amazon EC2 documentation\.

**Examples**

The following example creates a domain named `mylogs` with Elasticsearch version 5\.5 with a 10 GB General Purpose EBS volume:

```
aws es create-elasticsearch-domain --domain-name=mylogs --elasticsearch-version 5.5 --ebs-options EBSEnabled=true,VolumeType=gp2,VolumeSize=10
```

However, you might need a larger EBS volume as the size of your search indices increases\. For example, you might opt for a 100 GB Provisioned IOPS volume with a baseline I/O performance of 3000 IOPS\. The following example updates the domain configuration with those changes:

```
aws es update-elasticsearch-domain-config --domain-name=mylogs --ebs-options EBSEnabled=true,VolumeType=io1,VolumeSize=100,IOPS=3000
```

### Configuring EBS\-based Storage \(AWS SDKs\)<a name="es-createdomain-configure-ebs-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all the actions defined in the Amazon ES Configuration API Reference, including the `--ebs-options` parameter for `UpdateElasticsearchDomainConfig`\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\.

## Modifying VPC Access Configuration<a name="es-createdomain-configure-vpc-endpoints"></a>

If you configured a domain to reside within a VPC, you can modify the configuration using the Amazon ES console\. To migrate a public domain to a VPC domain, see [[ERROR] BAD/MISSING LINK TEXT](es-vpc.md#es-migrating-public-to-vpc)\.

### Configuring VPC Access \(Console\)<a name="es-createdomain-configure-vpc-endpoints-console"></a>

Use the following procedure to configure VPC access by using the console\.

**To configure VPC access \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose the domain that you want to configure\.

1. Choose **Configure cluster**\.

1. In the **Network configuration** section, for **Subnets**, choose a subnet\. If you enabled zone awareness, you must choose two subnets\. The subnets must be in different Availability Zones in the same region\. For more information, see [[ERROR] BAD/MISSING LINK TEXT](es-vpc.md)\.
**Note**  
You must reserve sufficient IP addresses for the network interfaces in the subnet \(or subnets\)\. For more information, see [[ERROR] BAD/MISSING LINK TEXT](es-vpc.md#es-reserving-ip-vpc-endpoints)\.

1. For **Security groups**, add the security groups that need access to the domain\. 

1. Choose **Submit**\.

## Configuring Access Policies<a name="es-createdomain-configure-access-policies"></a>

Amazon Elasticsearch Service offers several ways to configure access to your Amazon ES domains\. For more information, see [[ERROR] BAD/MISSING LINK TEXT](es-ac.md)\.

The console provides preconfigured access policies that you can customize for the specific needs of your domain\. You also can import access policies from other Amazon ES domains\. For information on how these access policies interact with VPC access, see [[ERROR] BAD/MISSING LINK TEXT](es-vpc.md#es-vpc-security)\.

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

The AWS SDKs \(except the Android and iOS SDKs\) support all the actions defined in the Amazon ES Configuration API Reference, including the `--access-policies` parameter for `UpdateElasticsearchDomainConfig`\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\.

## Configuring Automatic Snapshots<a name="es-createdomain-configure-snapshots"></a>

Amazon Elasticsearch Service provides automatic daily snapshots of a domain's primary index shards and the number of replica shards\. By default, the service takes automatic snapshots at midnight, but you should choose a time when the service is under minimal load\.

For information on working with these snapshots, see [[ERROR] BAD/MISSING LINK TEXT](es-managedomains-snapshots.md#es-managedomains-snapshot-restore)\.

**Warning**  
The service stops taking snapshots of Amazon ES indices while the health of a cluster is red\. Any documents that you add to a red cluster, even to indices with a health status of green, can be lost in the event of a cluster failure due to this lack of backups\. To prevent loss of data, return the health of your cluster to green before uploading additional data to any index in the cluster\.

### Configuring Snapshots \(Console\)<a name="es-createdomain-configure-snapshots-console"></a>

 Use the following procedure to configure daily automatic index snapshots by using the console\.

**To configure automatic snapshots**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose the domain that you want to update\.

1. Choose **Configure cluster**\.

1. For **Automated snapshot start hour**, choose the new hour for the service to take automated snapshots\.

1. Choose **Submit**\.

### Configuring Snapshots \(AWS CLI\)<a name="es-createdomain-configure-snapshots-cli"></a>

Use the following syntax for the `--snapshot-options` option\. The syntax for the option is the same for both the `create-elasticsearch-domain` and `update-elasticsearch-domain-config` commands\.

**Syntax**

```
--snapshot-options AutomatedSnapshotStartHour=<value>
```


****  

| Parameter | Valid Values | Description | 
| --- | --- | --- | 
| AutomatedSnapshotStartHour | Integer between 0 and 23 | Specifies the hour in UTC during which the service performs a daily automated snapshot of the indices in the new domain\. The default value is 0, or midnight, which means that the snapshot is taken anytime between midnight and 1:00 AM\. | 

**Example**

The following example configures automatic snapshots at 01:00 UTC:

```
aws es update-elasticsearch-domain-config --domain-name mylogs --region us-east-2 --snapshot-options AutomatedSnapshotStartHour=1
```

### Configuring Snapshots \(AWS SDKs\)<a name="es-createdomain-configure-snapshots-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all the actions that are defined in the Amazon ES Configuration API Reference\. This includes the `--snapshots-options` parameter for `UpdateElasticsearchDomainConfig`\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\. 

## Configuring Advanced Options<a name="es-createdomain-configure-advanced-options"></a>

Use advanced options to configure the following:

**rest\.action\.multi\.allow\_explicit**  
Specifies whether explicit references to indices are allowed inside the body of HTTP requests\. Setting this property to false prevents users from bypassing access control for subresources\. By default, the value is true\. For more information, see [URL\-based access control](https://www.elastic.co/guide/en/elasticsearch/reference/current/url-access-control.html) and [[ERROR] BAD/MISSING LINK TEXT](es-ac.md#es-ac-advanced)\.

**indices\.fielddata\.cache\.size**  
Specifies the percentage of Java heap space that is allocated to field data\. By default, this setting is unbounded\.   
Many customers query rotating daily indices\. We recommend that you begin benchmark testing with `indices.fielddata.cache.size` configured to 40% of the JVM heap for most such use cases\. However, if you have very large indices you might need a large field data cache\.

**indices\.query\.bool\.max\_clause\_count**  
Specifies the maximum number of clauses allowed in a Lucene Boolean query\. 1024 is the default\. Queries with more than the permitted number of clauses result in a `TooManyClauses` error\. For more information, see [the Lucene documentation](https://lucene.apache.org/core/6_6_0/core/org/apache/lucene/search/BooleanQuery.html)\.

### Configuring Advanced Options \(Console\)<a name="es-createdomain-configure-advanced-options-console"></a>

**To configure advanced options \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose the domain that you want to update\.

1. Choose **Configure cluster**\.

1. Choose **Advanced options**\.

1.  Specify the options that you want and choose **Submit**\.

### Configuring Advanced Options \(AWS CLI\)<a name="es-createdomain-configure-advanced-options-cli"></a>

Use the following syntax for the `--advanced-options` option\. The syntax for the option is the same for both the `create-elasticsearch-domain` and `update-elasticsearch-domain-config` commands\.

**Syntax**

```
--advanced-options rest.action.multi.allow_explicit=<true|false>, indices.fielddata.cache.size=<percentage_heap>, indices.query.bool.max_clause_count=<int>
```


****  
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-createupdatedomains.html)

**Example**

The following example disables explicit references to indices in the HTTP request bodies\. It also limits the field data cache to 40 percent of the total Java heap:

```
aws es update-elasticsearch-domain-config --domain-name mylogs --region us-east-1 --advanced-options rest.action.multi.allow_explicit_index=false, indices.fielddata.cache.size=40
```

### Configuring Advanced Options \(AWS SDKs\)<a name="es-createdomain-configure-advanced-options-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all of the actions defined in the Amazon ES Configuration API Reference, including the `--advanced-options` parameter for `UpdateElasticsearchDomainConfig`\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\.

## Configuring Slow Logs<a name="es-createdomain-configure-slow-logs"></a>

Slow logs are an Elasticsearch feature that Amazon ES exposes through Amazon CloudWatch Logs\. These logs are useful for troubleshooting performance issues, but are *disabled* by default\. If enabled, [standard CloudWatch pricing](https://aws.amazon.com/cloudwatch/pricing/) applies\.

Amazon ES exposes two slow logs: search and index\. To learn more about these logs, see [Slow Log](https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-slowlog.html) in the Elasticsearch documentation\.

### Enabling Slow Logs Publishing \(Console\)<a name="es-createdomain-configure-slow-logs-console"></a>

Use the following procedure to enable the publishing of slow logs to CloudWatch\.

**To enable slow logs publishing to CloudWatch**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose the domain that you want to update\.

1. On the **Logs** tab, choose **Enable** for the log that you want\. 

1. Create a CloudWatch log group, or choose an existing one\.
**Note**  
If you plan to enable search *and* index slow logs, we recommend publishing each to its own log group\. This separation makes the logs easier to scan\.

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
CloudWatch Logs supports [10 resource policies per region](http://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_PutResourcePolicy.html)\. If you plan to enable slow logs for several Amazon ES domains, you should create and reuse a broader policy that includes multiple log groups to avoid reaching this limit\.

1. Choose **Enable**\.

   The status of your domain changes from **Active** to **Processing**\. The status must return to **Active** before log publishing is enabled\. This process can take up to 30 minutes\.

After you enable log publishing, see [[ERROR] BAD/MISSING LINK TEXT](#es-createdomain-configure-slow-logs-indices)\.

### Enabling Slow Logs Publishing \(AWS CLI\)<a name="es-createdomain-configure-slow-logs-cli"></a>

Before you can enable log publishing, you need a CloudWatch log group\. If you don't already have one, you can create one using the following command:

```
aws logs create-log-group --log-group-name my-log-group
```

Type the next command to find the log group's ARN, and then *make a note of it*:

```
aws logs describe-log-groups --log-group-name my-log-group
```

Now you can give Amazon ES permissions to write to the log group\. You must provide the log group's ARN near the end of the command:

```
aws logs put-resource-policy --policy-name my-policy --policy-document '{ "Version": "2012-10-17", "Statement": [{ "Sid": "", "Effect": "Allow", "Principal": { "Service": "es.amazonaws.com"}, "Action":[ "logs:PutLogEvents"," logs:PutLogEventsBatch","logs:CreateLogStream"],"Resource": "cw_log_group_arn"}]}'
```

**Important**  
CloudWatch Logs supports [10 resource policies per region](http://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_PutResourcePolicy.html)\. If you plan to enable slow logs for several Amazon ES domains, you should create and reuse a broader policy that includes multiple log groups to avoid reaching this limit\.

Finally, you can use the `--log-publishing-options` option to enable publishing\. The syntax for the option is the same for both the `create-elasticsearch-domain` and `update-elasticsearch-domain-config` commands\.


****  
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-createupdatedomains.html)

**Note**  
If you plan to enable search *and* index slow logs, we recommend publishing each to its own log group\. This separation makes the logs easier to scan\.

**Example**

The following example enables the publishing of search and index slow logs for the specified domain:

```
aws es update-elasticsearch-domain-config --domain-name my-domain --log-publishing-options "SEARCH_SLOW_LOGS={CloudWatchLogsLogGroupArn=arn:aws:logs:us-east-1:123456789012:log-group:my-log-group,Enabled=true},INDEX_SLOW_LOGS={CloudWatchLogsLogGroupArn=arn:aws:logs:us-east-1:123456789012:log-group:my-other-log-group,Enabled=true}"
```

To disable publishing to CloudWatch, run the same command with `Enabled=false`\.

After you enable log publishing, see [[ERROR] BAD/MISSING LINK TEXT](#es-createdomain-configure-slow-logs-indices)\.

### Enabling Slow Logs Publishing \(AWS SDKs\)<a name="es-createdomain-configure-slow-logs-sdk"></a>

Before you can enable slow logs publishing, you must first create a CloudWatch log group, get its ARN, and give Amazon ES permissions to write to it\. The relevant operations are documented in the [Amazon CloudWatch Logs API Reference](http://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/):

+ `CreateLogGroup`

+ `DescribeLogGroup`

+ `PutResourcePolicy`

You can access these operations using the [AWS SDKs](https://aws.amazon.com/tools/#sdk)\.

The AWS SDKs \(except the Android and iOS SDKs\) support all the operations that are defined in the Amazon ES Configuration API Reference, including the `--log-publishing-options` option for `CreateElasticsearchDomain` and `UpdateElasticsearchDomainConfig`\.

After you enable log publishing, see [[ERROR] BAD/MISSING LINK TEXT](#es-createdomain-configure-slow-logs-indices)\.

### Setting Elasticsearch Logging Thresholds<a name="es-createdomain-configure-slow-logs-indices"></a>

Elasticsearch disables slow logs by default\. After you enable the *publishing* of slow logs to CloudWatch, you still must specify logging thresholds for each Elasticsearch index\. These thresholds define precisely what should be logged and at which log level\. Settings vary slightly by Elasticsearch version\. For summaries of each version, see [Slow Log](https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-slowlog.html) in the Elasticsearch documentation\.

You specify these settings through the Elasticsearch REST API\. An example follows:

```
curl -XPUT elasticsearch_domain_endpoint/index/_settings --data '{"index.search.slowlog.threshold.query.warn": "5s","index.search.slowlog.threshold.query.info": "2s"}' -H 'Content-Type: application/json'
```

To test that slow logs are publishing successfully, consider starting with very low values to verify that logs appear in CloudWatch, and then increase the thresholds to more useful levels\.

If the logs don't appear, check the following:

+ Does the CloudWatch log group exist? Check the CloudWatch console\.

+ Does Amazon ES have permissions to write to the log group? Check the Amazon ES console\.

+ Is the Amazon ES domain configured to publish to the log group? Check the Amazon ES console, use the AWS CLI `describe-elasticsearch-domain-config` option, or call `DescribeElasticsearchDomainConfig` using one of the SDKs\.

+ Are the Elasticsearch logging thresholds low enough that your requests are exceeding them? To review your thresholds for an index, use the following command:

  ```
  curl -XGET elasticsearch_domain_endpoint/index/_settings?pretty
  ```

If you want to disable slow logs for an index, return any thresholds that you changed to their default values of `-1`\.

Disabling publishing to CloudWatch using the Amazon ES console or AWS CLI does *not* stop Elasticsearch from generating logs; it only stops the *publishing* of those logs\. Be sure to check your index settings if you no longer need the slow logs\.

### Viewing Slow Logs<a name="es-createdomain-configure-slow-logs-viewing"></a>

Viewing the slow logs in CloudWatch is just like viewing any other CloudWatch log\. For more information, see [View Log Data](http://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/ViewingLogData.html) in the *Amazon CloudWatch Logs User Guide*\.

Here are some considerations for viewing the logs:

+ Amazon ES publishes only the first 255,000 characters of each line of the slow logs to CloudWatch\. Any remaining content is truncated\.

+ In CloudWatch, the log stream names have suffixes of `-index-slow-logs` or `-search-slow-logs` to help identify their contents\.