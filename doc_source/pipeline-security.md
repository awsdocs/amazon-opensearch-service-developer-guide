# Securing Amazon OpenSearch Ingestion pipelines within a VPC<a name="pipeline-security"></a>

You can launch Amazon OpenSearch Ingestion pipelines into a *virtual private cloud* \(VPC\)\. A VPC is a virtual network that's dedicated to your AWS account\. It's logically isolated from other virtual networks in the AWS Cloud\. Placing a pipeline within a VPC enables secure communication between OpenSearch Ingestion and other services within the VPC without the need for an internet gateway, NAT device, or VPN connection\. All traffic remains securely within the AWS Cloud\.

Using a VPC allows you to enforce data flow through your OpenSearch Ingestion pipelines within the boundaries of the VPC, rather than over the public internet\. Pipelines that aren't within a VPC send and receive data over public\-facing endpoints and the internet\.

For instructions to provision a pipeline within a VPC, see [Creating pipelines](creating-pipeline.md#create-pipeline)\.

**Topics**
+ [Considerations](#pipeline-vpc-considerations)
+ [Limitations](#pipeline-vpc-limitations)
+ [Prerequisites](#pipeline-vpc-prereqs)
+ [Configuring VPC access for a pipeline](#pipeline-vpc-configure)
+ [Service\-linked role for VPC access](#pipeline-vpc-slr)

## Considerations<a name="pipeline-vpc-considerations"></a>

Consider the following when you configure VPC access for a pipeline\.
+ A public pipeline can write to a VPC domain\. Similarly, a VPC pipeline can write to a public domain\.
+ A pipeline doesn't need to be in the same VPC as its domain sink\. You also don't need to establish a connection between the two VPCs\. OpenSearch Ingestion takes care of connecting them for you\.
+ You can only specify one VPC for your pipeline\.
+ Unlike with public pipelines, a VPC pipeline must be in the same AWS Region as the domain that it's writing to\.
+ You can choose to deploy a pipeline into one, two, or three subnets of your VPC\. The subnets are distributed across the same Availability Zones that your Ingestion OpenSearch Compute Units \(OCUs\) are deployed in\.
+ If you only deploy a pipeline in one subnet and the Availability Zone goes down, you won't be able to ingest data\. To ensure high availability, we recommend that you configure pipelines with two or three subnets\.
+ Specifying a security group is optional\. If you don't provide a security group, we use the default security group that is specified in the VPC\.

## Limitations<a name="pipeline-vpc-limitations"></a>

Pipelines within a VPC have the following limitations\.
+ You can't change a pipeline's network configuration after you create it\. If you launch a pipeline within a VPC, you can't later change it to a public endpoint, and vice versa\.
+ You can either launch your pipeline within a VPC or use a public endpoint, but you can't do both\. You must choose one or the other when you create a pipeline\.
+ After you provision a pipeline within a VPC, you can't move it to a different VPC, and you can't change its subnets or security group settings\.
+ If your pipeline writes to a VPC domain sink, you can't go back later and change the sink to a different domain \(VPC or public\) after the pipeline is created\. You must delete and recreate the pipeline with a new sink\. You can still switch a sink from a public domain to a VPC domain\.
+ You can't provide [cross\-account ingestion access](configure-client.md#configure-client-cross-account) to VPC pipelines\.

## Prerequisites<a name="pipeline-vpc-prereqs"></a>

Before you can provision a pipeline within a VPC, you must do the following:
+ **Create a VPC**

  To create your VPC, you can use the Amazon VPC console, the AWS CLI, or one of the AWS SDKs\. For more information, see [Working with VPCs](https://docs.aws.amazon.com/vpc/latest/userguide/working-with-vpcs.html) in the *Amazon VPC User Guide*\. If you already have a VPC, you can skip this step\.
+ **Reserve IP addresses **

  OpenSearch Ingestion places an *elastic network interface* in each subnet that you specify during pipeline creation\. Each network interface is associated with an IP address\. You must reserve a sufficient number of IP addresses in the subnet for the network interfaces\.

## Configuring VPC access for a pipeline<a name="pipeline-vpc-configure"></a>

You can enable VPC access for a pipeline within the OpenSearch Service console or using the AWS CLI\.

### Console<a name="pipeline-vpc-configure-console"></a>

You configure VPC access during [pipeline creation](creating-pipeline.md#create-pipeline)\. Under **Network**, choose **VPC access** and configure the following settings:


| Setting | Description | 
| --- | --- | 
| VPC |  Choose the ID of the virtual private cloud \(VPC\) that you want to use\. The VPC and pipeline must be in the same AWS Region\.  | 
| Subnets |  Choose one or more subnets\. OpenSearch Service will place a VPC endpoint and *elastic network interfaces* in the subnets\.  | 
| Security groups |  Choose one or more VPC security groups that allow your required application to reach the OpenSearch Ingestion pipeline on the ports \(80 or 443\) and protocols \(HTTP or HTTPs\) exposed by the pipeline\.  | 

### CLI<a name="pipeline-vpc-configure-cli"></a>

To configure VPC access using the AWS CLI, specify the `--vpc-options` parameter:

```
aws osis create-pipeline \
  --pipeline-name vpc-pipeline \
  --min-units 4 \
  --max-units 10 \
  --vpc-options SecurityGroupIds={sg-12345678,sg-9012345},SubnetIds=subnet-1212234567834asdf \
  --pipeline-configuration-body "file://pipeline-config.yaml"
```

## Service\-linked role for VPC access<a name="pipeline-vpc-slr"></a>

A [service\-linked role](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_terms-and-concepts.html#iam-term-service-linked-role) is a unique type of IAM role that delegates permissions to a service so that it can create and manage resources on your behalf\. OpenSearch Ingestion requires a service\-linked role called **AWSServiceRoleForAmazonOpenSearchIngestion** to access your VPC, create the pipeline endpoint, and place network interfaces in a subnet of your VPC\. For more information on this role's permissions and how to delete it, see [Using service\-linked roles to create OpenSearch Ingestion pipelines](slr-osis.md)\.

OpenSearch Ingestion automatically creates the role when you create an ingestion pipeline\. For this automatic creation to succeed, the user creating the first pipeline in an account must have permissions for the `iam:CreateServiceLinkedRole` action\. To learn more, see [Service\-linked role permissions](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#service-linked-role-permissions) in the *IAM User Guide*\. You can view the role in the AWS Identity and Access Management \(IAM\) console after it's created\.