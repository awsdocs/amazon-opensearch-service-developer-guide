# Supported Instance Types<a name="aes-supported-instance-types"></a>

An *instance* is a virtual computing environment\. An *instance type* is a specific configuration of CPU, memory, storage, and networking capacity\. Choose an instance type for your Amazon ES domain that is based on the requirements of the application or software that you plan to run on your instance\. If you have enabled dedicated master nodes, you can choose an instance type for the master nodes that differs from the instance type that you choose for the data nodes\.

To learn more, see [[ERROR] BAD/MISSING LINK TEXT](sizing-domains.md), [[ERROR] BAD/MISSING LINK TEXT](aes-limits.md#clusterresource), and [[ERROR] BAD/MISSING LINK TEXT](aes-limits.md#ebsresource)\.

Amazon ES supports the following instance types\.

**T2 Instance Types**

+ `t2.micro.elasticsearch`

+ `t2.small.elasticsearch`

+ `t2.medium.elasticsearch`

**Note**  
You can use the `t2` instance types only if the instance count for your domain is 10 or fewer\.
The `t2.micro.elasticsearch` instance type only supports Elasticsearch 2\.3 and 1\.5\.
The `t2` instance types do not support encryption of data at rest\.

**M3 Instance Types**

+ `m3.medium.elasticsearch`

+ `m3.large.elasticsearch`

+ `m3.xlarge.elasticsearch`

+ `m3.2xlarge.elasticsearch`

**Note**  
The `m3` instance types are not available in the us\-east\-2, ca\-central\-1, eu\-west\-2, eu\-west\-3, ap\-northeast\-2, ap\-south\-1, and cn\-northwest\-1 regions\.
The `m3` instance types do not support encryption of data at rest\.

**M4 Instance Types**

+ `m4.large.elasticsearch`

+ `m4.xlarge.elasticsearch`

+ `m4.2xlarge.elasticsearch`

+ `m4.4xlarge.elasticsearch`

+ `m4.10xlarge.elasticsearch`

**Note**  
The `m4` instance types are not available in the eu\-west\-3 region\.

**C4 Instance Types**

+ `c4.large.elasticsearch`

+ `c4.xlarge.elasticsearch`

+ `c4.2xlarge.elasticsearch`

+ `c4.4xlarge.elasticsearch`

+ `c4.8xlarge.elasticsearch`

**Note**  
The `c4` instance types are not available in the eu\-west\-3 region\.

**R3 Instance Types**

+ `r3.large.elasticsearch`

+ `r3.xlarge.elasticsearch`

+ `r3.2xlarge.elasticsearch`

+ `r3.4xlarge.elasticsearch`

+ `r3.8xlarge.elasticsearch`

**Note**  
The `r3` instance types are not available in the ca\-central\-1, eu\-west\-2, eu\-west\-3, sa\-east\-1, and cn\-northwest\-1 regions\.
The `r3` instance types do not support encryption of data at rest\.

**R4 Instance Types**

+ `r4.large.elasticsearch`

+ `r4.xlarge.elasticsearch`

+ `r4.2xlarge.elasticsearch`

+ `r4.4xlarge.elasticsearch`

+ `r4.8xlarge.elasticsearch`

+ `r4.16xlarge.elasticsearch`

**I2 Instance Types**

+ `i2.xlarge.elasticsearch`

+ `i2.2xlarge.elasticsearch`

**Note**  
The `i2` instance types are not available in the sa\-east\-1, ca\-central\-1, eu\-west\-2, eu\-west\-3, us\-east\-2, and cn\-northwest\-1 regions\.

**I3 Instance Types**

+ `i3.large.elasticsearch`

+ `i3.xlarge.elasticsearch`

+ `i3.2xlarge.elasticsearch`

+ `i3.4xlarge.elasticsearch`

+ `i3.8xlarge.elasticsearch`

+ `i3.16xlarge.elasticsearch`

**Note**  
The `i3` instance types do not support EBS storage volumes and are not available in the eu\-west\-3 region\.
The `i3` instance types do not support Elasticsearch 2\.3 or 1\.5\.