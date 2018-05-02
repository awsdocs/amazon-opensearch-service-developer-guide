# VPC Support for Amazon Elasticsearch Service Domains<a name="es-vpc"></a>

A *virtual private cloud* \(VPC\) is a virtual network that is dedicated to your AWS account\. It's logically isolated from other virtual networks in the AWS Cloud\. You can launch AWS resources, such as Amazon ES domains, into your VPC\.

Placing an Amazon ES domain within a VPC enables secure communication between Amazon ES and other services within the VPC without the need for an internet gateway, NAT device, or VPN connection\. All traffic remains securely within the AWS Cloud\. Because of their logical isolation, domains that reside within a VPC have an extra layer of security when compared to domains that use public endpoints\.

To support VPCs, Amazon ES places an endpoint into either one or two subnets of your VPC\. A *subnet* is a range of IP addresses in your VPC\. If you enable [zone awareness](es-managedomains.md#es-managedomains-zoneawareness) for your domain, Amazon ES places an endpoint into two subnets\. The subnets must be in different Availability Zones in the same region\. If you don't enable zone awareness, Amazon ES places an endpoint into only one subnet\.

The following illustration shows the VPC architecture if zone awareness is not enabled\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/VPCNoZoneAwareness.png)

The following illustration shows the VPC architecture if zone awareness is enabled\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/VPCZoneAwareness.png)

Amazon ES also places an *elastic network interface* \(ENI\) in the VPC for each of your data nodes\. Amazon ES assigns each ENI a private IP address from the IPv4 address range of your subnet\. The service also assigns a public DNS hostname \(which is the domain endpoint\) for the IP addresses\. You must use a public DNS service to resolve the endpoint \(which is a DNS hostname\) to the appropriate IP addresses for the data nodes:
+ If your VPC uses the Amazon\-provided DNS server by setting the `enableDnsSupport` option to `true` \(the default value\), resolution for the Amazon ES endpoint will succeed\.
+ If your VPC uses a private DNS server and the server can reach the public authoritative DNS servers to resolve DNS hostnames, resolution for the Amazon ES endpoint will also succeed\.

Because the IP addresses might change, you should resolve the domain endpoint periodically so that you can always access the correct data nodes\. We recommend that you set the DNS resolution interval to one minute\. If you’re using a client, you should also ensure that the DNS cache in the client is cleared\.

**Note**  
Amazon ES doesn't support IPv6 addresses with a VPC\. You can use a VPC that has IPv6 enabled, but the domain will use IPv4 addresses\.

**Topics**
+ [Limitations](#es-vpc-limitations)
+ [About Access Policies on VPC Domains](#es-vpc-security)
+ [Before You Begin: Prerequisites for VPC Access](#es-prerequisites-vpc-endpoints)
+ [Creating a VPC](#es-creating-vpc)
+ [Reserving IP Addresses in a VPC Subnet](#es-reserving-ip-vpc-endpoints)
+ [Service\-Linked Role for VPC Access](#es-enabling-slr)
+ [Migrating from Public Access to VPC Access](#es-migrating-public-to-vpc)
+ [Amazon VPC Documentation](#es-vpc-docs)

## Limitations<a name="es-vpc-limitations"></a>

Currently, operating an Amazon ES domain within a VPC has the following limitations:
+ You can either launch your domain within a VPC or use a public endpoint, but you can't do both\. You must choose one or the other when you create your domain\.
+ If you launch a new domain within a VPC, you can't later switch it to use a public endpoint\. The reverse is also true: If you create a domain with a public endpoint, you can't later place it within a VPC\. Instead, you must create a new domain and migrate your data\.
+ You can't launch your domain within a VPC that uses dedicated tenancy\. You must use a VPC with tenancy set to **Default**\.
+ After you place a domain within a VPC, you can't move it to a different VPC\. However, you can change the subnets and security group settings\.
+ Compared to public domains, VPC domains display less information in the Amazon ES console\. Specifically, the **Cluster health** tab does not include shard information, and the **Indices** tab is not present at all\.
+ Currently, Amazon ES does not support integration with Amazon Kinesis Data Firehose for domains that reside within a VPC\. To use this service with Amazon ES, you must use a domain with public access\.
+ To access the default installation of Kibana for a domain that resides within a VPC, users must have access to the VPC\. This process varies by network configuration, but likely involves connecting to a VPN or managed network or using a proxy server\. To learn more, see [About Access Policies on VPC Domains](#es-vpc-security), the [Amazon VPC User Guide](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/), and [Controlling Access to Kibana](es-kibana.md#es-kibana-access)\.

## About Access Policies on VPC Domains<a name="es-vpc-security"></a>

Placing your Amazon ES domain within a VPC provides an inherent, strong layer of security\. When you create a domain with public access, the endpoint takes the following form:

```
https://search-domain-name-identifier.region.es.amazonaws.com
```

As the "public" label suggests, this endpoint is accessible from any internet\-connected device, though you can \(and should\) [control access to it](es-ac.md)\. If you access the endpoint in a web browser, you might receive a `Not Authorized` message, but the request reaches the domain\.

When you create a domain with VPC access, the endpoint *looks* similar to a public endpoint:

```
https://vpc-domain-name-identifier.region.es.amazonaws.com
```

If you try to access the endpoint in a web browser, however, you might find that the request times out\. To perform even basic `GET` requests, your computer must be able to connect to the VPC\. This connection often takes the form of an internet gateway, VPN server, or proxy server\. For details on the various forms it can take, see [Scenarios and Examples](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_Scenarios.html) in the *Amazon VPC User Guide*\.

In addition to this connectivity requirement, VPCs let you manage access to the domain through [security groups](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_SecurityGroups.html)\. For many use cases, this combination of security features is sufficient, and you might feel comfortable applying an open access policy to the domain\.

Operating with an open access policy does *not* mean that anyone on the internet can access the Amazon ES domain\. Rather, it means that if a request reaches the Amazon ES domain and the associated security groups permit it, the domain accepts the request without further security checks\.

For an additional layer of security, we recommend using access policies that specify IAM users or roles\. Applying these policies means that, for the domain to accept a request, the security groups must permit it *and* it must be signed with valid credentials\.

**Note**  
Because security groups already enforce IP\-based access policies, you can't apply IP\-based access policies to Amazon ES domains that reside within a VPC\. If you use public access, IP\-based policies are still available\.

## Before You Begin: Prerequisites for VPC Access<a name="es-prerequisites-vpc-endpoints"></a>

Before you can enable a connection between a VPC and your new Amazon ES domain, you must do the following:
+ **Create a VPC**

  To create your VPC, you can use the Amazon VPC console, the AWS CLI, or one of the AWS SDKs\. You must create a subnet in the VPC, or two subnets if you enable [zone awareness](es-managedomains.md#es-managedomains-zoneawareness)\. For more information, see [Creating A VPC](#es-creating-vpc)\. If you already have a VPC, you can skip this step\.
+ **Reserve IP addresses **

  Amazon ES enables the connection of a VPC to a domain by placing network interfaces in a subnet of the VPC\. Each network interface is associated with an IP address\. You must reserve a sufficient number of IP addresses in the subnet for the network interfaces\. For more information, see [Reserving IP Addresses in a VPC Subnet](#es-reserving-ip-vpc-endpoints)\. 

## Creating a VPC<a name="es-creating-vpc"></a>

To create your VPC, you can use one of the following: the Amazon VPC console, the AWS CLI, or one of the AWS SDKs\. The VPC must have a subnet, or two subnets if you enable [zone awareness](es-managedomains.md#es-managedomains-zoneawareness)\. The two subnets must be in different Availability Zones in the same region\.

The following procedure shows how to use the Amazon VPC console to create a VPC with a public subnet, reserve IP addresses for the subnet, and create a security group to control access to your Amazon ES domain\. For other VPC configurations, see [Scenarios and Examples](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_Scenarios.html) in the *Amazon VPC User Guide*\.

**To create a VPC \(console\)**

1. Sign in to the AWS Management Console, and open the Amazon VPC console at [https://console\.aws\.amazon\.com/vpc/](https://console.aws.amazon.com/vpc/)\.

1. In the navigation pane, choose **VPC Dashboard**\.

1. Choose **Start VPC Wizard**\.

1. On the **Select a VPC Configuration** page, select **VPC with a Single Public Subnet**\.

1. On the **VPC with a Single Public Subnet** page, keep the default options, and then choose **Create VPC**\.

1. In the confirmation message that appears, choose **Close**\.

1. If you intend to enable [zone awareness](es-managedomains.md#es-managedomains-zoneawareness) for your Amazon ES domain, you must create a second subnet in a different Availability Zone in the same region\. If you don't intend to enable zone awareness, skip to step 8\. 

   1. In the navigation pane, choose **Subnets\.**

   1. Choose **Create Subnet**\.

   1. In the **Create Subnet** dialog box, optionally create a name tag to help you identify the subnet later\.

   1. For **VPC**, choose the VPC that you just created\.

   1. For **Availability Zone**, choose an Availability Zone that differs from that of the first subnet\. The Availability Zones for both subnets must be in the same region\.

   1. For **IPv4 CIDR block**, configure a CIDR block large enough to provide sufficient IP addresses for Amazon ES to use during maintenance activities\. For more information, see [Reserving IP Addresses in a VPC Subnet](#es-reserving-ip-vpc-endpoints)\.
**Note**  
Amazon ES domains using VPC access don't support IPv6 addresses\. You can use a VPC that has IPv6 enabled, but the ENIs will have IPv4 addresses\.

   1. Choose **Yes, Create**\.

1. In the navigation pane, choose **Subnets**\.

1. In the list of subnets, find your subnet \(or subnets, if you created a second subnet in step 7\)\. In the **Available IPv4** column, confirm that you have a sufficient number of IPv4 addresses\. 

1. Make a note of the subnet ID and Availability Zone\. You need this information later when you launch your Amazon ES domain and add an Amazon EC2 instance to your VPC\.

1. Create an Amazon VPC security group\. You use this security group to control access to your Amazon ES domain\.

   1. In the navigation pane, choose **Security Groups**\.

   1. Choose **Create Security Group**\.

   1. In the **Create Security Group** dialog box, type a name tag, a group name, and a description\. For **VPC**, choose the ID of your VPC\.

   1. Choose **Yes, Create**\.

1. Define a network ingress rule for your security group\. This rule allows you to connect to your Amazon ES domain\.

   1. In the navigation pane, choose **Security Groups**, and then select the security group that you just created\.

   1. At the bottom of the page, choose the **Inbound Rules** tab\.

   1. Choose **Edit**, and then choose **HTTPS \(443\)**\.

   1. Choose **Save**\.

Now you are ready to [launch an Amazon ES domain](es-createupdatedomains.md#es-createdomains) in your Amazon VPC\.

## Reserving IP Addresses in a VPC Subnet<a name="es-reserving-ip-vpc-endpoints"></a>

Amazon ES connects a domain to a VPC by placing network interfaces in a subnet of the VPC \(or two subnets of the VPC if you enable [zone awareness](es-managedomains.md#es-managedomains-zoneawareness)\)\. Each network interface is associated with an IP address\. Before you create your Amazon ES domain, you must have a sufficient number of IP addresses available in the VPC subnet to accommodate the network interfaces\.

The number of IP addresses that Amazon ES requires depends on the following:
+ Number of data nodes in your domain\. \(Master nodes are not included in the number\.\) 
+ Whether you enable [zone awareness](es-managedomains.md#es-managedomains-zoneawareness)\. If you enable zone awareness, you need only half the number of IP addresses per subnet that you need if you don't enable zone awareness\.

Here is the basic formula: The number of IP addresses reserved in each subnet is three times the number of nodes, divided by two if zone awareness is enabled\.

**Examples**
+ If a domain has 10 data nodes and zone awareness is enabled, the IP count is 10 / 2 \* 3 = 15\.
+ If a domain has 10 data nodes and zone awareness is disabled, the IP count is 10 \* 3 = 30\.

When you create the domain, Amazon ES reserves the IP addresses\. You can see the network interfaces and their associated IP addresses in the **Network Interfaces** section of the Amazon EC2 console at [https://console\.aws\.amazon\.com/ec2/](https://console.aws.amazon.com/ec2/)\. The **Description** column shows which Amazon ES domain the network interface is associated with\.

**Tip**  
We recommend that you create dedicated subnets for the Amazon ES reserved IP addresses\. By using dedicated subnets, you avoid overlap with other applications and services and ensure that you can reserve additional IP addresses if you need to scale your cluster in the future\. To learn more, see [Creating a Subnet in Your VPC](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/working-with-vpcs.html#AddaSubnet)\.

## Service\-Linked Role for VPC Access<a name="es-enabling-slr"></a>

A [service\-linked role](http://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_terms-and-concepts.html#iam-term-service-linked-role) is a unique type of IAM role that delegates permissions to a service so that it can create and manage resources on your behalf\. Amazon ES requires a service\-linked role to access your VPC, create the domain endpoint, and place network interfaces in a subnet of your VPC\.

Amazon ES automatically creates the role when you use the Amazon ES console to create a domain within a VPC\. For this automatic creation to succeed, you must have permissions for the `iam:CreateServiceLinkedRole` action\. To learn more, see [Service\-Linked Role Permissions](http://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#service-linked-role-permissions) in the *IAM User Guide*\.

After Amazon ES creates the role, you can view it \(`AWSServiceRoleForAmazonElasticsearchService`\) using the IAM console\.

**Note**  
If you create a domain that uses a public endpoint, Amazon ES doesn’t need the service\-linked role and doesn't create it\.

For full information on this role's permissions and how to delete it, see [Using Service\-Linked Roles for Amazon ES](slr-es.md)\.

## Migrating from Public Access to VPC Access<a name="es-migrating-public-to-vpc"></a>

When you create a domain, you specify whether it should have a public endpoint or reside within a VPC\. Once created, you cannot switch from one to the other\. Instead, you must create a new domain and either manually reindex or migrate your data\. Snapshots offer a convenient means of migrating data\. For information about taking and restoring snapshots, see [Working with Amazon Elasticsearch Service Index Snapshots](es-managedomains-snapshots.md)\.

## Amazon VPC Documentation<a name="es-vpc-docs"></a>

Amazon VPC has its own set of documentation to describe how to create and use your Amazon VPC\. The following table provides links to the Amazon VPC guides\.


| Description | Documentation | 
| --- | --- | 
| How to get started using Amazon VPC | [Amazon VPC Getting Started Guide](http://docs.aws.amazon.com/AmazonVPC/latest/GettingStartedGuide/) | 
| How to use Amazon VPC through the AWS Management Console | [Amazon VPC User Guide](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/) | 
| Complete descriptions of all the Amazon VPC commands | [Amazon EC2 Command Line Reference](http://docs.aws.amazon.com/AWSEC2/latest/CommandLineReference/) \(The Amazon VPC commands are part of the Amazon EC2 reference\.\) | 
| Complete descriptions of the Amazon VPC API actions, data types, and errors | [Amazon EC2 API Reference](http://docs.aws.amazon.com/AWSEC2/latest/APIReference/) \(The Amazon VPC API actions are part of the Amazon EC2 reference\.\) | 
| Information for the network administrator who configures the gateway at your end of an optional IPsec VPN connection | [Amazon VPC Network Administrator Guide](http://docs.aws.amazon.com/AmazonVPC/latest/NetworkAdminGuide/) | 

For more detailed information about Amazon Virtual Private Cloud, see [Amazon Virtual Private Cloud](https://aws.amazon.com/vpc/)\.