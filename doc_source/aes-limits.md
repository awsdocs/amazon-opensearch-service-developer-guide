# Amazon Elasticsearch Service Limits<a name="aes-limits"></a>

The following tables show limits for Amazon ES resources, including the number of nodes per cluster, the minimum and maximum sizes for EBS volumes, and network limits\.

## Cluster and Instance Limits<a name="clusterresource"></a>

The following table shows Amazon ES limits for clusters and instances\.


| Clusters and Instances | Limit | 
| --- | --- | 
| Maximum number of data nodes per cluster |  40 \(except for the T2 instance types, which have a maximum of 10\) To request an increase up to 200 data nodes \(for Elasticsearch 2\.3 or later\), create a case with the [AWS Support Center](https://console.aws.amazon.com/support/home#/)\. For more information about requesting an increase, see [AWS Service Limits](http://docs.aws.amazon.com/general/latest/gr/aws_service_limits.html)\.  | 
| Maximum number of [warm](ultrawarm.md) nodes per cluster | 150 | 
|  Maximum total number of data and warm nodes per cluster  |  200 You might have to request a data node limit increase to reach this total\. For example, your domain might have 80 data nodes and 120 warm nodes\.  | 
| Maximum number of dedicated master nodes per cluster |  5 You can use the T2 instance types for dedicated master nodes \(not recommended for production domains\) only if the number of data nodes is 10 or fewer\.  | 
|  Maximum total storage per cluster  |  3 PiB This maximum is the sum of all data nodes and warm nodes\. For example, your domain might have 36 `i3.8xlarge.elasticsearch` instances and 140 `ultrawarm1.large.elasticsearch` instances for a total of 2\.98 PiB of storage\.  | 
| Smallest supported instance type per Elasticsearch version | `t2.micro.elasticsearch` \(versions 1\.5 and 2\.3\) and `t2.small.elasticsearch` \(version 5\.*x* and 6\.*x*\)\. | 
| Maximum number of domains per account \(per Region\) | 100 | 
|  Maximum number of custom packages per account \(per Region\)  |  25  | 
|  Maximum number of custom packages per domain  |  20  | 

For a list of the instance types that Amazon ES supports, see [Supported Instance Types](aes-supported-instance-types.md)\.

## UltraWarm Storage Limits<a name="limits-ultrawarm"></a>

The following table lists the UltraWarm instance types and the maximum amount of storage that each type can use\. For more information about UltraWarm, see [UltraWarm for Amazon Elasticsearch Service](ultrawarm.md)\.


****  

| Instance Type | Maximum Storage | 
| --- | --- | 
| ultrawarm1\.medium\.elasticsearch | 1\.5 TiB | 
| ultrawarm1\.large\.elasticsearch | 20 TiB | 

## EBS Volume Size Limits<a name="ebsresource"></a>

The following table shows the minimum and maximum sizes for EBS volumes for each instance type that Amazon ES supports\. For information about which instance types include instance storage and additional hardware details, see [Amazon Elasticsearch Service Pricing](https://aws.amazon.com/elasticsearch-service/pricing/)\.
+ If you choose magnetic storage under **EBS volume type** when creating your domain, the maximum volume size is 100 GiB for all instance types except `t2.micro`, `t2.small`, and `t2.medium`\. For the maximum sizes listed in the following table, choose one of the SSD options\.
+ 512 GiB is the maximum volume size for Elasticsearch version 1\.5\.
+ Some older\-generation instance types include instance storage, but also support EBS storage\. If you choose EBS storage for one of these instance types, the storage volumes are *not* additive\. You can use either an EBS volume or the instance storage, not both\.


****  

| Instance Type | Minimum EBS Size | Maximum EBS Size | 
| --- | --- | --- | 
| t2\.micro\.elasticsearch | 10 GiB | 35 GiB | 
| t2\.small\.elasticsearch | 10 GiB | 35 GiB | 
| t2\.medium\.elasticsearch | 10 GiB | 35 GiB | 
| m3\.medium\.elasticsearch | 10 GiB | 100 GiB | 
| m3\.large\.elasticsearch | 10 GiB | 512 GiB | 
| m3\.xlarge\.elasticsearch | 10 GiB | 512 GiB | 
| m3\.2xlarge\.elasticsearch | 10 GiB | 512 GiB | 
| m4\.large\.elasticsearch | 10 GiB | 512 GiB | 
| m4\.xlarge\.elasticsearch | 10 GiB | 1 TiB | 
| m4\.2xlarge\.elasticsearch | 10 GiB | 1\.5 TiB | 
| m4\.4xlarge\.elasticsearch | 10 GiB | 1\.5 TiB | 
| m4\.10xlarge\.elasticsearch | 10 GiB | 1\.5 TiB | 
| m5\.large\.elasticsearch | 10 GiB | 512 GiB | 
| m5\.xlarge\.elasticsearch | 10 GiB | 1 TiB | 
| m5\.2xlarge\.elasticsearch | 10 GiB | 1\.5 TiB | 
| m5\.4xlarge\.elasticsearch | 10 GiB | 3 TiB | 
| m5\.12xlarge\.elasticsearch | 10 GiB | 9 TiB | 
| c4\.large\.elasticsearch | 10 GiB | 100 GiB | 
| c4\.xlarge\.elasticsearch | 10 GiB | 512 GiB | 
| c4\.2xlarge\.elasticsearch | 10 GiB | 1 TiB | 
| c4\.4xlarge\.elasticsearch | 10 GiB | 1\.5 TiB | 
| c4\.8xlarge\.elasticsearch | 10 GiB | 1\.5 TiB | 
| c5\.large\.elasticsearch | 10 GiB | 256 GiB | 
| c5\.xlarge\.elasticsearch | 10 GiB | 512 GiB | 
| c5\.2xlarge\.elasticsearch | 10 GiB | 1 TiB | 
| c5\.4xlarge\.elasticsearch | 10 GiB | 1\.5 TiB | 
| c5\.9xlarge\.elasticsearch | 10 GiB | 3\.5 TiB | 
| c5\.18xlarge\.elasticsearch | 10 GiB | 7 TiB | 
| r3\.large\.elasticsearch | 10 GiB | 512 GiB | 
| r3\.xlarge\.elasticsearch | 10 GiB | 512 GiB | 
| r3\.2xlarge\.elasticsearch | 10 GiB | 512 GiB | 
| r3\.4xlarge\.elasticsearch | 10 GiB | 512 GiB | 
| r3\.8xlarge\.elasticsearch | 10 GiB | 512 GiB | 
| r4\.large\.elasticsearch | 10 GiB | 1 TiB | 
| r4\.xlarge\.elasticsearch | 10 GiB | 1\.5 TiB | 
| r4\.2xlarge\.elasticsearch | 10 GiB | 1\.5 TiB | 
| r4\.4xlarge\.elasticsearch | 10 GiB | 1\.5 TiB | 
| r4\.8xlarge\.elasticsearch | 10 GiB | 1\.5 TiB | 
| r4\.16xlarge\.elasticsearch | 10 GiB | 1\.5 TiB | 
| r5\.large\.elasticsearch | 10 GiB | 1 TiB | 
| r5\.xlarge\.elasticsearch | 10 GiB | 1\.5 TiB | 
| r5\.2xlarge\.elasticsearch | 10 GiB | 3 TiB | 
| r5\.4xlarge\.elasticsearch | 10 GiB | 6 TiB | 
| r5\.12xlarge\.elasticsearch | 10 GiB | 12 TiB | 
| i2\.xlarge\.elasticsearch | 10 GiB | 512 GiB | 
| i2\.2xlarge\.elasticsearch | 10 GiB | 512 GiB | 
| i3\.large\.elasticsearch  | N/A | N/A | 
| i3\.xlarge\.elasticsearch | N/A | N/A | 
| i3\.2xlarge\.elasticsearch | N/A | N/A | 
| i3\.4xlarge\.elasticsearch | N/A | N/A | 
| i3\.8xlarge\.elasticsearch | N/A | N/A | 
| i3\.16xlarge\.elasticsearch | N/A | N/A | 

## Network Limits<a name="network-limits"></a>

The following table shows the maximum size of HTTP request payloads\.


****  

| Instance Type | Maximum Size of HTTP Request Payloads | 
| --- | --- | 
| t2\.micro\.elasticsearch | 10 MiB | 
| t2\.small\.elasticsearch | 10 MiB | 
| t2\.medium\.elasticsearch | 10 MiB | 
| m3\.medium\.elasticsearch | 10 MiB | 
| m3\.large\.elasticsearch | 10 MiB | 
| m3\.xlarge\.elasticsearch | 100 MiB | 
| m3\.2xlarge\.elasticsearch | 100 MiB | 
| m4\.large\.elasticsearch | 10 MiB | 
| m4\.xlarge\.elasticsearch | 100 MiB | 
| m4\.2xlarge\.elasticsearch | 100 MiB | 
| m4\.4xlarge\.elasticsearch | 100 MiB | 
| m4\.10xlarge\.elasticsearch | 100 MiB | 
| m5\.large\.elasticsearch | 10 MiB | 
| m5\.xlarge\.elasticsearch | 100 MiB | 
| m5\.2xlarge\.elasticsearch | 100 MiB | 
| m5\.4xlarge\.elasticsearch | 100 MiB | 
| m5\.12xlarge\.elasticsearch | 100 MiB | 
| c4\.large\.elasticsearch | 10 MiB | 
| c4\.xlarge\.elasticsearch | 100 MiB | 
| c4\.2xlarge\.elasticsearch | 100 MiB | 
| c4\.4xlarge\.elasticsearch | 100 MiB | 
| c4\.8xlarge\.elasticsearch | 100 MiB | 
| c5\.large\.elasticsearch | 10 MiB | 
| c5\.xlarge\.elasticsearch | 100 MiB | 
| c5\.2xlarge\.elasticsearch | 100 MiB | 
| c5\.4xlarge\.elasticsearch | 100 MiB | 
| c5\.9xlarge\.elasticsearch | 100 MiB | 
| c5\.18xlarge\.elasticsearch | 100 MiB | 
| r3\.large\.elasticsearch | 10 MiB | 
| r3\.xlarge\.elasticsearch | 100 MiB | 
| r3\.2xlarge\.elasticsearch | 100 MiB | 
| r3\.4xlarge\.elasticsearch | 100 MiB | 
| r3\.8xlarge\.elasticsearch | 100 MiB  | 
| r4\.large\.elasticsearch | 100 MiB | 
| r4\.xlarge\.elasticsearch | 100 MiB | 
| r4\.2xlarge\.elasticsearch | 100 MiB | 
| r4\.4xlarge\.elasticsearch | 100 MiB | 
| r4\.8xlarge\.elasticsearch | 100 MiB | 
| r4\.16xlarge\.elasticsearch | 100 MiB | 
| r5\.large\.elasticsearch | 100 MiB | 
| r5\.xlarge\.elasticsearch | 100 MiB | 
| r5\.2xlarge\.elasticsearch | 100 MiB | 
| r5\.4xlarge\.elasticsearch | 100 MiB | 
| r5\.12xlarge\.elasticsearch | 100 MiB | 
| i2\.xlarge\.elasticsearch | 100 MiB | 
| i2\.2xlarge\.elasticsearch | 100 MiB | 
| i3\.large\.elasticsearch | 100 MiB | 
| i3\.xlarge\.elasticsearch | 100 MiB | 
| i3\.2xlarge\.elasticsearch | 100 MiB | 
| i3\.4xlarge\.elasticsearch | 100 MiB | 
| i3\.8xlarge\.elasticsearch | 100 MiB | 
| i3\.16xlarge\.elasticsearch | 100 MiB | 

## Java Process Limit<a name="aes-java-process-limit"></a>

Amazon ES limits Java processes to a heap size of 32 GiB\. Advanced users can specify the percentage of the heap used for field data\. For more information, see [Advanced Options](es-createupdatedomains.md#es-createdomain-configure-advanced-options) and [JVM OutOfMemoryError](aes-handling-errors.md#aes-handling-errors-jvm_out_of_memory_error)\.

## Domain Policy Limit<a name="aes-domain-policy-limit"></a>

Amazon ES limits [access policies on domains](es-ac.md#es-ac-types-resource) to 100 KiB\.