# Amazon OpenSearch Service quotas<a name="limits"></a>

Your AWS account has default quotas, formerly referred to as limits, for each AWS service\. Unless otherwise noted, each quota is Region\-specific\.

To view the quotas for OpenSearch Service, open the [Service Quotas console](https://console.aws.amazon.com/servicequotas/home)\. In the navigation pane, choose **AWS services** and select **Amazon OpenSearch Service**\. To request a quota increase, see [Requesting a quota increase](https://docs.aws.amazon.com/servicequotas/latest/userguide/request-quota-increase.html) in the *Service Quotas User Guide*\. 

## Domain and instance quotas<a name="clusterresource"></a>

Your AWS account has the following quotas related to OpenSearch Service domains:


| Name | Default | Adjustable | Notes | 
| --- | --- | --- | --- | 
| Dedicated master instances per domain | 5 | No | You can use the T2 and T3 instance types for dedicated master nodes only if the number of data nodes is 10 or fewer\. | 
| Domains per Region | 100 | [Yes](https://console.aws.amazon.com/servicequotas/home/services/es/quotas/L-076D529E) |  | 
| Instances per domain | 80 | [Yes](https://console.aws.amazon.com/servicequotas/home/services/elasticloadbalancing/quotas/LL-6408ABDE) | You can request an increase up to 200 instances\. For example, your domain might have 80 data nodes and 120 warm nodes\. | 
| Instances per domain \(T2 instance type\) | 10 | No | We don't recommend T2 or t3\.small instance types for production domains\. | 
| Warm instances per domain | 150 | No |  | 

Your AWS account has the following additional OpenSearch Service limits:


| Name | Default | Adjustable | Notes | 
| --- | --- | --- | --- | 
|  Total storage per domain  |  3 PiB  | No | This maximum is the sum of all data nodes and warm nodes\. For example, your domain might have 45 r6gd\.16xlarge\.search instances and 140 ultrawarm1\.large\.search instances for a total of 2\.88 PiB of storage\. | 
|  Custom packages per Region  |  25  | No |  | 
|  Custom packages per domain  |  20  | No |  | 

For a list of the instance types that OpenSearch Service supports, see [Supported instance types](supported-instance-types.md)\.

## OpenSearch Serverless quotas<a name="limits-serverless"></a>

Your AWS account has the following quotas related to OpenSearch Serverless objects\. If you want to increase maximum capacity limits for your account, contact [AWS Support](https://console.aws.amazon.com/support/home)\.


| Name | Default | Adjustable | Notes | 
| --- | --- | --- | --- | 
|  Collection tags per account per Region  |  50  | No |  | 
| Collections with unique KMS keys per account per Region | 10 | No |  | 
|  VPC endpoints per account per Region  |  50  | No |  This limit only applies to [OpenSearch Serverless\-managed VPC endpoints](serverless-vpc.md)\. It does not include OpenSearch Ingestion VPC endpoints or OpenSearch Service\-managed VPC endpoints\.  | 
| Data access policies per account per Region | 500 | No |  | 
| Encryption policies per account per Region | 50 | No |  | 
| Network policies per account per Region | 500 | No |  | 
| SAML providers per account per Region | 50 | No |  | 
| On\-disk storage per collection | 1 TiB | No | A single OpenSearch Compute Unit \(OCU\) can hold approximately 180 GiB of data\. Collections can share OCUs, with the exception of collections with unique KMS keys\. | 
| Indexes within search collections per account | 20 | No |  | 
| Indexes within time series collections per account | 120 | No |  | 

## UltraWarm storage quotas<a name="limits-ultrawarm"></a>

The following table lists the UltraWarm instance types and the maximum amount of storage that each type can use\. For more information about UltraWarm, see [UltraWarm storage for Amazon OpenSearch Service](ultrawarm.md)\.


| Instance type | Maximum storage | 
| --- | --- | 
| ultrawarm1\.medium\.search | 1\.5 TiB | 
| ultrawarm1\.large\.search | 20 TiB | 

## EBS volume size quotas<a name="ebsresource"></a>

The following table shows the minimum and maximum sizes for EBS volumes for each instance type that OpenSearch Service supports\. For information about which instance types include instance storage and additional hardware details, see [Amazon OpenSearch Service pricing](https://aws.amazon.com/elasticsearch-service/pricing/)\.
+ If you choose magnetic storage under **EBS volume type** when creating your domain, the maximum volume size is 100 GiB for all instance types except `t2.small` and `t2.medium`, and all Graviton instances \(M6g, C6g, R6g, and R6gd\), which don't support magnetic storage\. For the maximum sizes listed in the following table, choose one of the SSD options\.
+ Some older\-generation instance types include instance storage, but also support EBS storage\. If you choose EBS storage for one of these instance types, the storage volumes are *not* additive\. You can use either an EBS volume or the instance storage, not both\.


| Instance type | Minimum EBS size | Maximum EBS size \(gp2\) | Maximum EBS size \(gp3\) | 
| --- | --- | --- | --- | 
| t2\.micro\.search | 10 GiB | 35 GiB | N/A | 
| t2\.small\.search | 10 GiB | 35 GiB | N/A | 
| t2\.medium\.search | 10 GiB | 35 GiB | N/A | 
| t3\.small\.search | 10 GiB | 100 GiB | 100 GiB | 
| t3\.medium\.search | 10 GiB | 200 GiB | 200 GiB | 
| m3\.medium\.search | 10 GiB | 100 GiB | N/A | 
| m3\.large\.search | 10 GiB | 512 GiB | N/A | 
| m3\.xlarge\.search | 10 GiB | 512 GiB | N/A | 
| m3\.2xlarge\.search | 10 GiB | 512 GiB | N/A | 
| m4\.large\.search | 10 GiB | 512 GiB | N/A | 
| m4\.xlarge\.search | 10 GiB | 1 TiB | N/A | 
| m4\.2xlarge\.search | 10 GiB | 1\.5 TiB | N/A | 
| m4\.4xlarge\.search | 10 GiB | 1\.5 TiB | N/A | 
| m4\.10xlarge\.search | 10 GiB | 1\.5 TiB | N/A | 
| m5\.large\.search | 10 GiB | 512 GiB | 1 TiB | 
| m5\.xlarge\.search | 10 GiB | 1 TiB | 2 TiB | 
| m5\.2xlarge\.search | 10 GiB | 1\.5 TiB | 3 TiB | 
| m5\.4xlarge\.search | 10 GiB | 3 TiB | 6 TiB | 
| m5\.12xlarge\.search | 10 GiB | 9 TiB | 18 TiB | 
| m6g\.large\.search | 10 GiB | 512 GiB | 1 TiB | 
| m6g\.xlarge\.search | 10 GiB | 1 TiB | 2 TiB | 
| m6g\.2xlarge\.search | 10 GiB | 1\.5 TiB | 3 TiB | 
| m6g\.4xlarge\.search | 10 GiB | 3 TiB | 6 TiB | 
| m6g\.8xlarge\.search | 10 GiB | 6 TiB | 12 TiB | 
| m6g\.12xlarge\.search | 10 GiB | 9 TiB | 18 TiB | 
| c4\.large\.search | 10 GiB | 100 GiB | N/A | 
| c4\.xlarge\.search | 10 GiB | 512 GiB | N/A | 
| c4\.2xlarge\.search | 10 GiB | 1 TiB | N/A | 
| c4\.4xlarge\.search | 10 GiB | 1\.5 TiB | N/A | 
| c4\.8xlarge\.search | 10 GiB | 1\.5 TiB | N/A | 
| c5\.large\.search | 10 GiB | 256 GiB | 256 GiB | 
| c5\.xlarge\.search | 10 GiB | 512 GiB | 512 GiB | 
| c5\.2xlarge\.search | 10 GiB | 1 TiB | 1 TiB | 
| c5\.4xlarge\.search | 10 GiB | 1\.5 TiB | 1\.5 TiB | 
| c5\.9xlarge\.search | 10 GiB | 3\.5 TiB | 3\.5 TiB | 
| c5\.18xlarge\.search | 10 GiB | 7 TiB | 7 TiB | 
| c6g\.large\.search | 10 GiB | 256 GiB | 256 GiB | 
| c6g\.xlarge\.search | 10 GiB | 512 GiB | 512 GiB | 
| c6g\.2xlarge\.search | 10 GiB | 1 TiB | 1 TiB | 
| c6g\.4xlarge\.search | 10 GiB | 1\.5 TiB | 1\.5 TiB | 
| c6g\.8xlarge\.search | 10 GiB | 3 TiB | 3 TiB | 
| c6g\.12xlarge\.search | 10 GiB | 4\.5 TiB | 4\.5 TiB | 
| r3\.large\.search | 10 GiB | 512 GiB | N/A | 
| r3\.xlarge\.search | 10 GiB | 512 GiB | N/A | 
| r3\.2xlarge\.search | 10 GiB | 512 GiB | N/A | 
| r3\.4xlarge\.search | 10 GiB | 512 GiB | N/A | 
| r3\.8xlarge\.search | 10 GiB | 512 GiB | N/A | 
| r4\.large\.search | 10 GiB | 1 TiB | N/A | 
| r4\.xlarge\.search | 10 GiB | 1\.5 TiB | N/A | 
| r4\.2xlarge\.search | 10 GiB | 1\.5 TiB | N/A | 
| r4\.4xlarge\.search | 10 GiB | 1\.5 TiB | N/A | 
| r4\.8xlarge\.search | 10 GiB | 1\.5 TiB | N/A | 
| r4\.16xlarge\.search | 10 GiB | 1\.5 TiB | N/A | 
| r5\.large\.search | 10 GiB | 1 TiB | 2 TiB | 
| r5\.xlarge\.search | 10 GiB | 1\.5 TiB | 3 TiB | 
| r5\.2xlarge\.search | 10 GiB | 3 TiB | 6 TiB | 
| r5\.4xlarge\.search | 10 GiB | 6 TiB | 12 TiB | 
| r5\.12xlarge\.search | 10 GiB | 12 TiB | 24 TiB | 
| r6g\.large\.search | 10 GiB | 1 TiB | 2 TiB | 
| r6g\.xlarge\.search | 10 GiB | 1\.5 TiB | 3 TiB | 
| r6g\.2xlarge\.search | 10 GiB | 3 TiB | 6 TiB | 
| r6g\.4xlarge\.search | 10 GiB | 6 TiB | 12 TiB | 
| r6g\.8xlarge\.search | 10 GiB | 8 TiB | 16 TiB | 
| r6g\.12xlarge\.search | 10 GiB | 12 TiB | 24 TiB | 
| r6gd\.large\.search | N/A | N/A | N/A | 
| r6gd\.xlarge\.search | N/A | N/A | N/A | 
| r6gd\.2xlarge\.search | N/A | N/A | N/A | 
| r6gd\.4xlarge\.search | N/A | N/A | N/A | 
| r6gd\.8xlarge\.search | N/A | N/A | N/A | 
| r6gd\.12xlarge\.search | N/A | N/A | N/A | 
| r6gd\.16xlarge\.search | N/A | N/A | N/A | 
| i2\.xlarge\.search | 10 GiB | 512 GiB | N/A | 
| i2\.2xlarge\.search | 10 GiB | 512 GiB | N/A | 
| i3\.large\.search  | N/A | N/A | N/A | 
| i3\.xlarge\.search | N/A | N/A | N/A | 
| i3\.2xlarge\.search | N/A | N/A | N/A | 
| i3\.4xlarge\.search | N/A | N/A | N/A | 
| i3\.8xlarge\.search | N/A | N/A | N/A | 
| i3\.16xlarge\.search | N/A | N/A | N/A | 

## Network quotas<a name="network-limits"></a>

The following table shows the maximum size of HTTP request payloads\.


****  

| Instance type | Maximum size of HTTP request payloads | 
| --- | --- | 
| t2\.micro\.search | 10 MiB | 
| t2\.small\.search | 10 MiB | 
| t2\.medium\.search | 10 MiB | 
| t3\.small\.search | 10 MiB | 
| t3\.medium\.search | 10 MiB | 
| m3\.medium\.search | 10 MiB | 
| m3\.large\.search | 10 MiB | 
| m3\.xlarge\.search | 100 MiB | 
| m3\.2xlarge\.search | 100 MiB | 
| m4\.large\.search | 10 MiB | 
| m4\.xlarge\.search | 100 MiB | 
| m4\.2xlarge\.search | 100 MiB | 
| m4\.4xlarge\.search | 100 MiB | 
| m4\.10xlarge\.search | 100 MiB | 
| m5\.large\.search | 10 MiB | 
| m5\.xlarge\.search | 100 MiB | 
| m5\.2xlarge\.search | 100 MiB | 
| m5\.4xlarge\.search | 100 MiB | 
| m5\.12xlarge\.search | 100 MiB | 
| m6g\.large\.search | 10 MiB | 
| m6g\.xlarge\.search | 100 MiB | 
| m6g\.2xlarge\.search | 100 MiB | 
| m6g\.4xlarge\.search | 100 MiB | 
| m6g\.8xlarge\.search | 100 MiB | 
| m6g\.12xlarge\.search | 100 MiB | 
| c4\.large\.search | 10 MiB | 
| c4\.xlarge\.search | 100 MiB | 
| c4\.2xlarge\.search | 100 MiB | 
| c4\.4xlarge\.search | 100 MiB | 
| c4\.8xlarge\.search | 100 MiB | 
| c5\.large\.search | 10 MiB | 
| c5\.xlarge\.search | 100 MiB | 
| c5\.2xlarge\.search | 100 MiB | 
| c5\.4xlarge\.search | 100 MiB | 
| c5\.9xlarge\.search | 100 MiB | 
| c5\.18xlarge\.search | 100 MiB | 
| c6g\.large\.search | 10 MiB | 
| c6g\.xlarge\.search | 100 MiB | 
| c6g\.2xlarge\.search | 100 MiB | 
| c6g\.4xlarge\.search | 100 MiB | 
| c6g\.8xlarge\.search | 100 MiB | 
| c6g\.12xlarge\.search | 100 MiB | 
| r3\.large\.search | 10 MiB | 
| r3\.xlarge\.search | 100 MiB | 
| r3\.2xlarge\.search | 100 MiB | 
| r3\.4xlarge\.search | 100 MiB | 
| r3\.8xlarge\.search | 100 MiB  | 
| r4\.large\.search | 100 MiB | 
| r4\.xlarge\.search | 100 MiB | 
| r4\.2xlarge\.search | 100 MiB | 
| r4\.4xlarge\.search | 100 MiB | 
| r4\.8xlarge\.search | 100 MiB | 
| r4\.16xlarge\.search | 100 MiB | 
| r5\.large\.search | 100 MiB | 
| r5\.xlarge\.search | 100 MiB | 
| r5\.2xlarge\.search | 100 MiB | 
| r5\.4xlarge\.search | 100 MiB | 
| r5\.12xlarge\.search | 100 MiB | 
| r6g\.large\.search | 100 MiB | 
| r6g\.xlarge\.search | 100 MiB | 
| r6g\.2xlarge\.search | 100 MiB | 
| r6g\.4xlarge\.search | 100 MiB | 
| r6g\.8xlarge\.search | 100 MiB | 
| r6g\.12xlarge\.search | 100 MiB | 
| r6gd\.large\.search | 100 MiB | 
| r6gd\.xlarge\.search | 100 MiB | 
| r6gd\.2xlarge\.search | 100 MiB | 
| r6gd\.4xlarge\.search | 100 MiB | 
| r6gd\.8xlarge\.search | 100 MiB | 
| r6gd\.12xlarge\.search | 100 MiB | 
| r6gd\.16xlarge\.search | 100 MiB | 
| i2\.xlarge\.search | 100 MiB | 
| i2\.2xlarge\.search | 100 MiB | 
| i3\.large\.search | 100 MiB | 
| i3\.xlarge\.search | 100 MiB | 
| i3\.2xlarge\.search | 100 MiB | 
| i3\.4xlarge\.search | 100 MiB | 
| i3\.8xlarge\.search | 100 MiB | 
| i3\.16xlarge\.search | 100 MiB | 

## Java process quota<a name="java-process-limit"></a>

OpenSearch Service limits Java processes to a heap size of 32 GiB\. Advanced users can specify the percentage of the heap used for field data\. For more information, see [Advanced cluster settings](createupdatedomains.md#createdomain-configure-advanced-options) and [JVM OutOfMemoryError](handling-errors.md#handling-errors-jvm_out_of_memory_error)\.

## Domain policy quota<a name="domain-policy-limit"></a>

OpenSearch Service limits [access policies on domains](ac.md#ac-types-resource) to 100 KiB\.