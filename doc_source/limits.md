# Amazon OpenSearch Service limits<a name="limits"></a>

The following tables show limits for Amazon OpenSearch Service resources, including the number of nodes per cluster, the minimum and maximum sizes for EBS volumes, and network limits\.

## Cluster and instance limits<a name="clusterresource"></a>

The following table shows OpenSearch Service limits for clusters and instances\.


| Clusters and instances | Limit | 
| --- | --- | 
| Maximum number of data nodes per cluster |  80 \(except for the T2 and T3 instance types, which have a maximum of 10\) To request an increase up to 200 data nodes, create a case with the [AWS Support Center](https://console.aws.amazon.com/support/home#/)\. For more information about requesting an increase, see [AWS Service Limits](http://docs.aws.amazon.com/general/latest/gr/aws_service_limits.html)\.  | 
| Maximum number of [warm](ultrawarm.md) nodes per cluster | 150 | 
|  Maximum total number of data and warm nodes per cluster  |  200 You might have to request a data node limit increase to reach this total\. For example, your domain might have 80 data nodes and 120 warm nodes\.  | 
| Maximum number of dedicated master nodes per cluster |  5 You can use the T2 and T3 instance types for dedicated master nodes only if the number of data nodes is 10 or fewer\. We don't recommend T2 or `t3.small` instance types for production domains\.  | 
|  Maximum total storage per cluster  |  3 PiB This maximum is the sum of all data nodes and warm nodes\. For example, your domain might have 45 `r6gd.16xlarge.search` instances and 140 `ultrawarm1.large.search` instances for a total of 2\.88 PiB of storage\.  | 
| Smallest supported instance type per OpenSearch version |  `t2.small.search` | 
| Maximum number of domains per account \(per AWS Region\) | 100 | 
|  Maximum number of custom packages per account \(per AWS Region\)  |  25  | 
|  Maximum number of custom packages per domain  |  20  | 

For a list of the instance types that OpenSearch Service supports, see [Supported Instance Types](supported-instance-types.md)\.

## UltraWarm storage limits<a name="limits-ultrawarm"></a>

The following table lists the UltraWarm instance types and the maximum amount of storage that each type can use\. For more information about UltraWarm, see [UltraWarm storage for Amazon OpenSearch Service](ultrawarm.md)\.


****  

| Instance type | Maximum storage | 
| --- | --- | 
| ultrawarm1\.medium\.search | 1\.5 TiB | 
| ultrawarm1\.large\.search | 20 TiB | 

## EBS volume size limits<a name="ebsresource"></a>

The following table shows the minimum and maximum sizes for EBS volumes for each instance type that OpenSearch Service supports\. For information about which instance types include instance storage and additional hardware details, see [Amazon OpenSearch Service Pricing](https://aws.amazon.com/elasticsearch-service/pricing/)\.
+ If you choose magnetic storage under **EBS volume type** when creating your domain, the maximum volume size is 100 GiB for all instance types except `t2.small` and `t2.medium`, and all Graviton instances \(M6G, C6G, R6G, and R6GD\), which don't support magnetic storage\. For the maximum sizes listed in the following table, choose one of the SSD options\.
+ Some older\-generation instance types include instance storage, but also support EBS storage\. If you choose EBS storage for one of these instance types, the storage volumes are *not* additive\. You can use either an EBS volume or the instance storage, not both\.


****  

| Instance type | Minimum EBS size | Maximum EBS size | 
| --- | --- | --- | 
| t2\.micro\.search | 10 GiB | 35 GiB | 
| t2\.small\.search | 10 GiB | 35 GiB | 
| t2\.medium\.search | 10 GiB | 35 GiB | 
| t3\.small\.search | 10 GiB | 100 GiB | 
| t3\.medium\.search | 10 GiB | 200 GiB | 
| m3\.medium\.search | 10 GiB | 100 GiB | 
| m3\.large\.search | 10 GiB | 512 GiB | 
| m3\.xlarge\.search | 10 GiB | 512 GiB | 
| m3\.2xlarge\.search | 10 GiB | 512 GiB | 
| m4\.large\.search | 10 GiB | 512 GiB | 
| m4\.xlarge\.search | 10 GiB | 1 TiB | 
| m4\.2xlarge\.search | 10 GiB | 1\.5 TiB | 
| m4\.4xlarge\.search | 10 GiB | 1\.5 TiB | 
| m4\.10xlarge\.search | 10 GiB | 1\.5 TiB | 
| m5\.large\.search | 10 GiB | 512 GiB | 
| m5\.xlarge\.search | 10 GiB | 1 TiB | 
| m5\.2xlarge\.search | 10 GiB | 1\.5 TiB | 
| m5\.4xlarge\.search | 10 GiB | 3 TiB | 
| m5\.12xlarge\.search | 10 GiB | 9 TiB | 
| m6g\.large\.search | 10 GiB | 512 GiB | 
| m6g\.xlarge\.search | 10 GiB | 1 TiB | 
| m6g\.2xlarge\.search | 10 GiB | 1\.5 TiB | 
| m6g\.4xlarge\.search | 10 GiB | 3 TiB | 
| m6g\.8xlarge\.search | 10 GiB | 6 TiB | 
| m6g\.12xlarge\.search | 10 GiB | 9 TiB | 
| c4\.large\.search | 10 GiB | 100 GiB | 
| c4\.xlarge\.search | 10 GiB | 512 GiB | 
| c4\.2xlarge\.search | 10 GiB | 1 TiB | 
| c4\.4xlarge\.search | 10 GiB | 1\.5 TiB | 
| c4\.8xlarge\.search | 10 GiB | 1\.5 TiB | 
| c5\.large\.search | 10 GiB | 256 GiB | 
| c5\.xlarge\.search | 10 GiB | 512 GiB | 
| c5\.2xlarge\.search | 10 GiB | 1 TiB | 
| c5\.4xlarge\.search | 10 GiB | 1\.5 TiB | 
| c5\.9xlarge\.search | 10 GiB | 3\.5 TiB | 
| c5\.18xlarge\.search | 10 GiB | 7 TiB | 
| c6g\.large\.search | 10 GiB | 256 GiB | 
| c6g\.xlarge\.search | 10 GiB | 512 GiB | 
| c6g\.2xlarge\.search | 10 GiB | 1 TiB | 
| c6g\.4xlarge\.search | 10 GiB | 1\.5 TiB | 
| c6g\.8xlarge\.search | 10 GiB | 3 TiB | 
| c6g\.12xlarge\.search | 10 GiB | 4\.5 TiB | 
| r3\.large\.search | 10 GiB | 512 GiB | 
| r3\.xlarge\.search | 10 GiB | 512 GiB | 
| r3\.2xlarge\.search | 10 GiB | 512 GiB | 
| r3\.4xlarge\.search | 10 GiB | 512 GiB | 
| r3\.8xlarge\.search | 10 GiB | 512 GiB | 
| r4\.large\.search | 10 GiB | 1 TiB | 
| r4\.xlarge\.search | 10 GiB | 1\.5 TiB | 
| r4\.2xlarge\.search | 10 GiB | 1\.5 TiB | 
| r4\.4xlarge\.search | 10 GiB | 1\.5 TiB | 
| r4\.8xlarge\.search | 10 GiB | 1\.5 TiB | 
| r4\.16xlarge\.search | 10 GiB | 1\.5 TiB | 
| r5\.large\.search | 10 GiB | 1 TiB | 
| r5\.xlarge\.search | 10 GiB | 1\.5 TiB | 
| r5\.2xlarge\.search | 10 GiB | 3 TiB | 
| r5\.4xlarge\.search | 10 GiB | 6 TiB | 
| r5\.12xlarge\.search | 10 GiB | 12 TiB | 
| r6g\.large\.search | 10 GiB | 1 TiB | 
| r6g\.xlarge\.search | 10 GiB | 1\.5 TiB | 
| r6g\.2xlarge\.search | 10 GiB | 3 TiB | 
| r6g\.4xlarge\.search | 10 GiB | 6 TiB | 
| r6g\.8xlarge\.search | 10 GiB | 8 TiB | 
| r6g\.12xlarge\.search | 10 GiB | 12 TiB | 
| r6gd\.large\.search | N/A | N/A | 
| r6gd\.xlarge\.search | N/A | N/A | 
| r6gd\.2xlarge\.search | N/A | N/A | 
| r6gd\.4xlarge\.search | N/A | N/A | 
| r6gd\.8xlarge\.search | N/A | N/A | 
| r6gd\.12xlarge\.search | N/A | N/A | 
| r6gd\.16xlarge\.search | N/A | N/A | 
| i2\.xlarge\.search | 10 GiB | 512 GiB | 
| i2\.2xlarge\.search | 10 GiB | 512 GiB | 
| i3\.large\.search  | N/A | N/A | 
| i3\.xlarge\.search | N/A | N/A | 
| i3\.2xlarge\.search | N/A | N/A | 
| i3\.4xlarge\.search | N/A | N/A | 
| i3\.8xlarge\.search | N/A | N/A | 
| i3\.16xlarge\.search | N/A | N/A | 

## Network limits<a name="network-limits"></a>

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

## Java process limit<a name="java-process-limit"></a>

OpenSearch Service limits Java processes to a heap size of 32 GiB\. Advanced users can specify the percentage of the heap used for field data\. For more information, see [Advanced cluster settings](createupdatedomains.md#createdomain-configure-advanced-options) and [JVM OutOfMemoryError](handling-errors.md#handling-errors-jvm_out_of_memory_error)\.

## Domain policy limit<a name="domain-policy-limit"></a>

OpenSearch Service limits [access policies on domains](ac.md#ac-types-resource) to 100 KiB\.