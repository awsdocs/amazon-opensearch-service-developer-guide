# VPC Support for Amazon Elasticsearch Service Domains<a name="es-vpc"></a>

A *virtual private cloud* \(VPC\) is a virtual network that is dedicated to your AWS account\. It's logically isolated from other virtual networks in the AWS Cloud\. You can launch AWS resources, such as Amazon ES domains, into your VPC\.

Placing an Amazon ES domain within a VPC enables secure communication between Amazon ES and other services within the VPC without the need for an internet gateway, NAT device, or VPN connection\. All traffic remains securely within the AWS Cloud\. Because of their logical isolation, domains that reside within a VPC have an extra layer of security when compared to domains that use public endpoints\.

To support VPCs, Amazon ES places an endpoint into one, two, or three subnets of your VPC\. A *subnet* is a range of IP addresses in your VPC\. If you enable [multiple Availability Zones](es-managedomains-multiaz.md) for your domain, each subnet must be in a different Availability Zone in the same region\. If you only use one Availability Zone, Amazon ES places an endpoint into only one subnet\.

The following illustration shows the VPC architecture for one Availability Zone\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/VPCNoZoneAwareness.png)

The following illustration shows the VPC architecture for two Availability Zones\.

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
+ [Testing VPC Domains](#kibana-test)
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
+ To access the default installation of Kibana for a domain that resides within a VPC, users must have access to the VPC\. This process varies by network configuration, but likely involves connecting to a VPN or managed network or using a proxy server\. To learn more, see [About Access Policies on VPC Domains](#es-vpc-security), the [Amazon VPC User Guide](https://docs.aws.amazon.com/vpc/latest/userguide/), and [Controlling Access to Kibana](es-kibana.md#es-kibana-access)\.

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

If you try to access the endpoint in a web browser, however, you might find that the request times out\. To perform even basic `GET` requests, your computer must be able to connect to the VPC\. This connection often takes the form of a VPN, managed network, or proxy server\. For details on the various forms it can take, see [Scenarios and Examples](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_Scenarios.html) in the *Amazon VPC User Guide*\. For a development\-focused example, see [Testing VPC Domains](#kibana-test)\.

In addition to this connectivity requirement, VPCs let you manage access to the domain through [security groups](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_SecurityGroups.html)\. For many use cases, this combination of security features is sufficient, and you might feel comfortable applying an open access policy to the domain\.

Operating with an open access policy does *not* mean that anyone on the internet can access the Amazon ES domain\. Rather, it means that if a request reaches the Amazon ES domain and the associated security groups permit it, the domain accepts the request without further security checks\.

For an additional layer of security, we recommend using access policies that specify IAM users or roles\. Applying these policies means that, for the domain to accept a request, the security groups must permit it *and* it must be signed with valid credentials\.

**Note**  
Because security groups already enforce IP\-based access policies, you can't apply IP\-based access policies to Amazon ES domains that reside within a VPC\. If you use public access, IP\-based policies are still available\.

## Testing VPC Domains<a name="kibana-test"></a>

The enhanced security of a VPC can make connecting to your domain and running basic tests a real challenge\. If you already have an Amazon ES VPC domain and would rather not create a VPN server, try the following process:

1. For your domain's access policy, choose **Allow open access to the domain**\. You can always update this setting after you finish testing\.

1. Create an Amazon Linux Amazon EC2 instance in the same VPC, subnet, and security group as your Amazon ES domain\.

   Because this instance is for testing purposes and needs to do very little work, choose an inexpensive instance type like `t2.micro`\. Assign the instance a public IP address and either create a new key pair or choose an existing one\. If you create a new key, download it to your `~/.ssh` directory\.

   To learn more about creating instances, see [Getting Started with Amazon EC2 Linux Instances](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/EC2_GetStarted.html#ec2-launch-instance)\.

1. Add an [internet gateway](https://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_Internet_Gateway.html) to your VPC\.

1. In the [route table](https://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_Route_Tables.html) for your VPC, add a new route\. For **Destination**, specify a [CIDR block](https://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing#IPv4_CIDR_blocks) that contains your computer's public IP address\. For **Target**, specify the internet gateway you just created\.

   For example, you might specify `123.123.123.123/32` for just your computer or `123.123.123.0/24` for a range of computers\.

1. For the security group, specify two inbound rules:    
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-vpc.html)

   The first rule lets you SSH into your EC2 instance\. The second allows the EC2 instance to communicate with the Amazon ES domain over HTTPS\.

1. From the terminal, run the following command:

   ```
   ssh -i ~/.ssh/your-key.pem ec2-user@your-ec2-instance-public-ip -N -L 9200:vpc-your-amazon-es-domain.region.es.amazonaws.com:443
   ```

   This command creates an SSH tunnel that forwards requests to [https://localhost:9200](https://localhost:9200) to your Amazon ES domain through the EC2 instance\. By default, Elasticsearch listens for traffic on port 9200\. Specifying this port simulates a local Elasticsearch install, but use whichever port you'd like\.

   The command provides no feedback and runs indefinitely\. To stop it, press `Ctrl + C`\.

1. Navigate to [https://localhost:9200/\_plugin/kibana/](https://localhost:9200/_plugin/kibana/) in your web browser\. You might need to acknowledge a security exception\.

   Alternately, you can send requests to [https://localhost:9200](https://localhost:9200) using [curl](https://curl.haxx.se/), [Postman](https://www.getpostman.com/), or your favorite programming language\.
**Tip**  
If you encounter curl errors due to a certificate mismatch, try the `--insecure` flag\.

As an alternative to this approach, if your domain is in a region that AWS Cloud9 supports, you can [create an EC2 environment](https://docs.aws.amazon.com/cloud9/latest/user-guide/create-environment.html#create-environment-main) in the same VPC as your domain, add the environment's security group to your Amazon ES domain configuration, add the HTTPS rule from step 5 to your security group, and use the web\-based Bash in AWS Cloud9 to issue curl commands\.

## Before You Begin: Prerequisites for VPC Access<a name="es-prerequisites-vpc-endpoints"></a>

Before you can enable a connection between a VPC and your new Amazon ES domain, you must do the following:
+ **Create a VPC**

  To create your VPC, you can use the Amazon VPC console, the AWS CLI, or one of the AWS SDKs\. For more information, see [Creating A VPC](#es-creating-vpc)\. If you already have a VPC, you can skip this step\.
+ **Reserve IP addresses **

  Amazon ES enables the connection of a VPC to a domain by placing network interfaces in a subnet of the VPC\. Each network interface is associated with an IP address\. You must reserve a sufficient number of IP addresses in the subnet for the network interfaces\. For more information, see [Reserving IP Addresses in a VPC Subnet](#es-reserving-ip-vpc-endpoints)\. 

## Creating a VPC<a name="es-creating-vpc"></a>

To create your VPC, you can use one of the following: the Amazon VPC console, the AWS CLI, or one of the AWS SDKs\. The VPC must have between one and three subnets, depending on the number of [Availability Zones](es-managedomains-multiaz.md) for your domain\.

The following procedure shows how to use the Amazon VPC console to create a VPC with a public subnet, reserve IP addresses for the subnet, and create a security group to control access to your Amazon ES domain\. For other VPC configurations, see [Scenarios and Examples](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_Scenarios.html) in the *Amazon VPC User Guide*\.

**To create a VPC \(console\)**

1. Sign in to the AWS Management Console, and open the Amazon VPC console at [https://console\.aws\.amazon\.com/vpc/](https://console.aws.amazon.com/vpc/)\.

1. In the navigation pane, choose **VPC Dashboard**\.

1. Choose **Start VPC Wizard**\.

1. On the **Select a VPC Configuration** page, select **VPC with a Single Public Subnet**\.

1. On the **VPC with a Single Public Subnet** page, keep the default options, and then choose **Create VPC**\.

1. In the confirmation message that appears, choose **Close**\.

1. If you intend to enable [multiple Availability Zones](es-managedomains-multiaz.md) for your Amazon ES domain, you must create additional subnets\. Otherwise, skip to step 8\. 

   1. In the navigation pane, choose **Subnets\.**

   1. Choose **Create Subnet**\.

   1. In the **Create Subnet** dialog box, optionally create a name tag to help you identify the subnet later\.

   1. For **VPC**, choose the VPC that you just created\.

   1. For **Availability Zone**, choose an Availability Zone that differs from that of the first subnet\. The Availability Zones for all subnets must be in the same region\.

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

Amazon ES connects a domain to a VPC by placing network interfaces in a subnet of the VPC \(or multiple subnets of the VPC if you enable [multiple Availability Zones](es-managedomains-multiaz.md)\)\. Each network interface is associated with an IP address\. Before you create your Amazon ES domain, you must have a sufficient number of IP addresses available in the VPC subnet to accommodate the network interfaces\.

The number of IP addresses that Amazon ES requires depends on the following:
+ Number of data nodes in your domain\. \(Master nodes are not included in the number\.\) 
+ Number of Availability Zones\. If you enable two or three Availability Zones, you need only half or one\-third the number of IP addresses per subnet that you need for one Availability Zone\.

Here is the basic formula: The number of IP addresses reserved in each subnet is three times the number of nodes, divided by the number of Availability Zones\.

**Examples**
+ If a domain has 10 data nodes and two Availability Zones, the IP count is 10 / 2 \* 3 = 15\.
+ If a domain has 10 data nodes and one Availability Zone, the IP count is 10 \* 3 = 30\.

When you create the domain, Amazon ES reserves the IP addresses, uses some for the domain, and reserves the rest for [blue/green deployments](es-managedomains-configuration-changes.md)\. You can see the network interfaces and their associated IP addresses in the **Network Interfaces** section of the Amazon EC2 console at [https://console\.aws\.amazon\.com/ec2/](https://console.aws.amazon.com/ec2/)\. The **Description** column shows which Amazon ES domain the network interface is associated with\.

**Tip**  
We recommend that you create dedicated subnets for the Amazon ES reserved IP addresses\. By using dedicated subnets, you avoid overlap with other applications and services and ensure that you can reserve additional IP addresses if you need to scale your cluster in the future\. To learn more, see [Creating a Subnet in Your VPC](https://docs.aws.amazon.com/vpc/latest/userguide/working-with-vpcs.html#AddaSubnet)\.

## Service\-Linked Role for VPC Access<a name="es-enabling-slr"></a>

A [service\-linked role](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_terms-and-concepts.html#iam-term-service-linked-role) is a unique type of IAM role that delegates permissions to a service so that it can create and manage resources on your behalf\. Amazon ES requires a service\-linked role to access your VPC, create the domain endpoint, and place network interfaces in a subnet of your VPC\.

Amazon ES automatically creates the role when you use the Amazon ES console to create a domain within a VPC\. For this automatic creation to succeed, you must have permissions for the `es:CreateElasticsearchServiceRole` and `iam:CreateServiceLinkedRole` actions\. To learn more, see [Service\-Linked Role Permissions](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#service-linked-role-permissions) in the *IAM User Guide*\.

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
| How to get started using Amazon VPC | [Amazon VPC Getting Started Guide](https://docs.aws.amazon.com/AmazonVPC/latest/GettingStartedGuide/) | 
| How to use Amazon VPC through the AWS Management Console | [Amazon VPC User Guide](https://docs.aws.amazon.com/vpc/latest/userguide/) | 
| Complete descriptions of all the Amazon VPC commands | [Amazon EC2 Command Line Reference](https://docs.aws.amazon.com/AWSEC2/latest/CommandLineReference/) \(The Amazon VPC commands are part of the Amazon EC2 reference\.\) | 
| Complete descriptions of the Amazon VPC API actions, data types, and errors | [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/) \(The Amazon VPC API actions are part of the Amazon EC2 reference\.\) | 

For more detailed information about Amazon Virtual Private Cloud, see [Amazon Virtual Private Cloud](https://aws.amazon.com/vpc/)\.