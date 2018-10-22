# Amazon Elasticsearch Service Limits<a name="aes-limits"></a>

The following tables show limits for Amazon ES resources, including the number of instances per cluster, the minimum and maximum sizes for EBS volumes, and network limits\.

## Cluster and Instance Limits<a name="clusterresource"></a>

The following table shows Amazon ES limits for clusters and instances\.


| Clusters and Instances | Limit | 
| --- | --- | 
| Maximum number of data instances \(instance count\) per cluster | 20 \(except for the T2 instance types, which have a maximum of 10\)  The default limit is 20 data instances per domain\. To request an increase up to 100 per domain \(for Elasticsearch 2\.3 or later\), create a case with the [AWS Support Center](https://console.aws.amazon.com/support/home#/)\.   For more information about requesting an increase, see [AWS Service Limits](http://docs.aws.amazon.com/general/latest/gr/aws_service_limits.html)\.   | 
| Maximum number of dedicated master nodes | 5  You can use the T2 instance types as dedicated master nodes only if the instance count is 10 or fewer\.  | 
| Smallest supported instance type | `t2.micro.elasticsearch` \(versions 1\.5 and 2\.3\) and `t2.small.elasticsearch` \(version 5\.*x* and 6\.*x*\)\. | 
| Maximum number of domains per account \(per region\) | 100 | 

For a list of the instance types that Amazon ES supports, see [Supported Instance Types](aes-supported-instance-types.md)\.

## EBS Volume Size Limits<a name="ebsresource"></a>

The following table shows the minimum and maximum sizes for EBS volumes for each instance type that Amazon ES supports\. See [Amazon Elasticsearch Service Pricing](https://aws.amazon.com/elasticsearch-service/pricing/) for information on which instance types offer instance storage\.

**Note**  
If you select magnetic storage under **EBS volume type** when creating your domain, maximum volume size is 100 GB for all instance types except `t2.micro`, `t2.small`, and `t2.medium`\. For the maximum sizes listed in the following table, select one of the SSD options\.


****  

| Instance Type | Minimum EBS Size | Maximum EBS Size | 
| --- | --- | --- | 
| t2\.micro\.elasticsearch | 10 GB | 35 GB | 
| t2\.small\.elasticsearch | 10 GB | 35 GB | 
| t2\.medium\.elasticsearch | 10 GB | 35 GB | 
| m3\.medium\.elasticsearch | 10 GB | 100 GB | 
| m3\.large\.elasticsearch | 10 GB | 512 GB | 
| m3\.xlarge\.elasticsearch | 10 GB | 512 GB | 
| m3\.2xlarge\.elasticsearch | 10 GB | 512 GB | 
| m4\.large\.elasticsearch | 10 GB | 512 GB | 
| m4\.xlarge\.elasticsearch | 10 GB | 1 TB\* | 
| m4\.2xlarge\.elasticsearch | 10 GB | 1\.5 TB\* | 
| m4\.4xlarge\.elasticsearch | 10 GB | 1\.5 TB\* | 
| m4\.10xlarge\.elasticsearch | 10 GB | 1\.5 TB\* | 
| c4\.large\.elasticsearch | 10 GB | 100 GB | 
| c4\.xlarge\.elasticsearch | 10 GB | 512 GB | 
| c4\.2xlarge\.elasticsearch | 10 GB | 1 TB\* | 
| c4\.4xlarge\.elasticsearch | 10 GB | 1\.5 TB\* | 
| c4\.8xlarge\.elasticsearch | 10 GB | 1\.5 TB\* | 
| r3\.large\.elasticsearch | 10 GB | 512 GB | 
| r3\.xlarge\.elasticsearch | 10 GB | 512 GB | 
| r3\.2xlarge\.elasticsearch | 10 GB | 512 GB | 
| r3\.4xlarge\.elasticsearch | 10 GB | 512 GB | 
| r3\.8xlarge\.elasticsearch | 10 GB | 512 GB | 
| r4\.large\.elasticsearch | 10 GB | 1 TB\* | 
| r4\.xlarge\.elasticsearch | 10 GB | 1\.5 TB\* | 
| r4\.2xlarge\.elasticsearch | 10 GB | 1\.5 TB\* | 
| r4\.4xlarge\.elasticsearch | 10 GB | 1\.5 TB\* | 
| r4\.8xlarge\.elasticsearch | 10 GB | 1\.5 TB\* | 
| r4\.16xlarge\.elasticsearch | 10 GB | 1\.5 TB\* | 
| i2\.xlarge\.elasticsearch | 10 GB | 512 GB | 
| i2\.2xlarge\.elasticsearch | 10 GB | 512 GB | 
| i3\.large\.elasticsearch  | N/A | N/A | 
| i3\.xlarge\.elasticsearch | N/A | N/A | 
| i3\.2xlarge\.elasticsearch | N/A | N/A | 
| i3\.4xlarge\.elasticsearch | N/A | N/A | 
| i3\.8xlarge\.elasticsearch | N/A | N/A | 
| i3\.16xlarge\.elasticsearch | N/A | N/A | 

\* 512 GB is the maximum volume size that is supported with Elasticsearch version 1\.5\.

## Network Limits<a name="network-limits"></a>

The following table shows the maximum size of HTTP request payloads\.


****  

| Instance Type | Maximum Size of HTTP Request Payloads | 
| --- | --- | 
| t2\.micro\.elasticsearch | 10 MB | 
| t2\.small\.elasticsearch | 10 MB | 
| t2\.medium\.elasticsearch | 10 MB | 
| m3\.medium\.elasticsearch | 10 MB | 
| m3\.large\.elasticsearch | 10 MB | 
| m3\.xlarge\.elasticsearch | 100 MB | 
| m3\.2xlarge\.elasticsearch | 100 MB | 
| m4\.large\.elasticsearch | 10 MB | 
| m4\.xlarge\.elasticsearch | 100 MB | 
| m4\.2xlarge\.elasticsearch | 100 MB | 
| m4\.4xlarge\.elasticsearch | 100 MB | 
| m4\.10xlarge\.elasticsearch | 100 MB | 
| c4\.large\.elasticsearch | 10 MB | 
| c4\.xlarge\.elasticsearch | 100 MB | 
| c4\.2xlarge\.elasticsearch | 100 MB | 
| c4\.4xlarge\.elasticsearch | 100 MB | 
| c4\.8xlarge\.elasticsearch | 100 MB | 
| r3\.large\.elasticsearch | 10 MB | 
| r3\.xlarge\.elasticsearch | 100 MB | 
| r3\.2xlarge\.elasticsearch | 100 MB | 
| r3\.4xlarge\.elasticsearch | 100 MB | 
| r3\.8xlarge\.elasticsearch | 100 MB  | 
| r4\.large\.elasticsearch | 100 MB | 
| r4\.xlarge\.elasticsearch | 100 MB | 
| r4\.2xlarge\.elasticsearch | 100 MB | 
| r4\.4xlarge\.elasticsearch | 100 MB | 
| r4\.8xlarge\.elasticsearch | 100 MB | 
| r4\.16xlarge\.elasticsearch | 100 MB | 
| i2\.xlarge\.elasticsearch | 100 MB | 
| i2\.2xlarge\.elasticsearch | 100 MB | 
| i3\.large\.elasticsearch | 100 MB | 
| i3\.xlarge\.elasticsearch | 100 MB | 
| i3\.2xlarge\.elasticsearch | 100 MB | 
| i3\.4xlarge\.elasticsearch | 100 MB | 
| i3\.8xlarge\.elasticsearch | 100 MB | 
| i3\.16xlarge\.elasticsearch | 100 MB | 

## Java Process Limit<a name="aes-java-process-limit"></a>

Amazon ES limits Java processes to a heap size of 32 GB\. Advanced users can specify the percentage of the heap used for field data\. For more information, see [Configuring Advanced Options](es-createupdatedomains.md#es-createdomain-configure-advanced-options) and [JVM OutOfMemoryError](aes-handling-errors.md#aes-handling-errors-jvm_out_of_memory_error)\.