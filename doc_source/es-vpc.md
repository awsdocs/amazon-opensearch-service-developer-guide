# Launching your Amazon Elasticsearch Service domains using a VPC<a name="es-vpc"></a>

You can launch AWS resources, such as Amazon Elasticsearch Service \(Amazon ES\) domains, into a *virtual private cloud* \(VPC\)\. A VPC is a virtual network that's dedicated to your AWS account\. It's logically isolated from other virtual networks in the AWS Cloud\. 

Placing an Amazon ES domain within a VPC enables secure communication between Amazon ES and other services within the VPC without the need for an internet gateway, NAT device, or VPN connection\. All traffic remains securely within the AWS Cloud\. Because of their logical isolation, domains that reside within a VPC have an extra layer of security when compared to domains that use public endpoints\.

To support VPCs, Amazon ES places an endpoint into one, two, or three subnets of your VPC\. If you enable [multiple Availability Zones](es-managedomains-multiaz.md) for your domain, each subnet must be in a different Availability Zone in the same region\. If you only use one Availability Zone, Amazon ES places an endpoint into only one subnet\.

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
+ [About access policies on VPC domains](#es-vpc-security)
+ [Testing VPC domains](#kibana-test)
+ [Before you begin: prerequisites for VPC access](#es-prerequisites-vpc-endpoints)
+ [Reserving IP addresses in a VPC subnet](#es-reserving-ip-vpc-endpoints)
+ [Service\-linked role for VPC access](#es-enabling-slr)
+ [Migrating from public access to VPC access](#es-migrating-public-to-vpc)

## Limitations<a name="es-vpc-limitations"></a>

Currently, operating an Amazon ES domain within a VPC has the following limitations:
+ You can either launch your domain within a VPC or use a public endpoint, but you can't do both\. You must choose one or the other when you create your domain\.
+ If you launch a new domain within a VPC, you can't later switch it to use a public endpoint\. The reverse is also true: If you create a domain with a public endpoint, you can't later place it within a VPC\. Instead, you must create a new domain and migrate your data\.
+ You can't launch your domain within a VPC that uses dedicated tenancy\. You must use a VPC with tenancy set to **Default**\.
+ After you place a domain within a VPC, you can't move it to a different VPC\. However, you can change the subnets and security group settings\.
+ Compared to public domains, VPC domains display less information in the Amazon ES console\. Specifically, the **Cluster health** tab does not include shard information, and the **Indices** tab is not present at all\.
+ To access the default installation of Kibana for a domain that resides within a VPC, users must have access to the VPC\. This process varies by network configuration, but likely involves connecting to a VPN or managed network or using a proxy server or transit gateway\. To learn more, see [About access policies on VPC domains](#es-vpc-security), the [Amazon VPC User Guide](https://docs.aws.amazon.com/vpc/latest/userguide/), and [Controlling access to Kibana](es-kibana.md#es-kibana-access)\.

## About access policies on VPC domains<a name="es-vpc-security"></a>

Placing your Amazon ES domain within a VPC provides an inherent, strong layer of security\. When you create a domain with public access, the endpoint takes the following form:

```
https://search-domain-name-identifier.region.es.amazonaws.com
```

As the "public" label suggests, this endpoint is accessible from any internet\-connected device, though you can \(and should\) [control access to it](es-ac.md)\. If you access the endpoint in a web browser, you might receive a `Not Authorized` message, but the request reaches the domain\.

When you create a domain with VPC access, the endpoint *looks* similar to a public endpoint:

```
https://vpc-domain-name-identifier.region.es.amazonaws.com
```

If you try to access the endpoint in a web browser, however, you might find that the request times out\. To perform even basic `GET` requests, your computer must be able to connect to the VPC\. This connection often takes the form of a VPN, transit gateway, managed network, or proxy server\. For details on the various forms it can take, see [Scenarios and Examples](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_Scenarios.html) in the *Amazon VPC User Guide*\. For a development\-focused example, see [Testing VPC domains](#kibana-test)\.

In addition to this connectivity requirement, VPCs let you manage access to the domain through [security groups](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_SecurityGroups.html)\. For many use cases, this combination of security features is sufficient, and you might feel comfortable applying an open access policy to the domain\.

Operating with an open access policy does *not* mean that anyone on the internet can access the Amazon ES domain\. Rather, it means that if a request reaches the Amazon ES domain and the associated security groups permit it, the domain accepts the request without further security checks\.

For an additional layer of security, we recommend using fine\-grained access control or an access policy that specifies IAM users or roles\. In these situations, for the domain to accept a request, the security groups must permit it *and* it must be signed with valid credentials\.

**Note**  
Because security groups already enforce IP\-based access policies, you can't apply IP\-based access policies to Amazon ES domains that reside within a VPC\. If you use public access, IP\-based policies are still available\.

## Testing VPC domains<a name="kibana-test"></a>

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

## Before you begin: prerequisites for VPC access<a name="es-prerequisites-vpc-endpoints"></a>

Before you can enable a connection between a VPC and your new Amazon ES domain, you must do the following:
+ **Create a VPC**

  To create your VPC, you can use the Amazon VPC console, the AWS CLI, or one of the AWS SDKs\. For more information, see [Working with VPCs](https://docs.aws.amazon.com/vpc/latest/userguide/working-with-vpcs.html) in the *Amazon VPC User Guide*\. If you already have a VPC, you can skip this step\.
+ **Reserve IP addresses **

  Amazon ES enables the connection of a VPC to a domain by placing network interfaces in a subnet of the VPC\. Each network interface is associated with an IP address\. You must reserve a sufficient number of IP addresses in the subnet for the network interfaces\. For more information, see [Reserving IP addresses in a VPC subnet](#es-reserving-ip-vpc-endpoints)\. 

## Reserving IP addresses in a VPC subnet<a name="es-reserving-ip-vpc-endpoints"></a>

Amazon ES connects a domain to a VPC by placing network interfaces in a subnet of the VPC \(or multiple subnets of the VPC if you enable [multiple Availability Zones](es-managedomains-multiaz.md)\)\. Each network interface is associated with an IP address\. Before you create your Amazon ES domain, you must have a sufficient number of IP addresses available in the VPC subnet to accommodate the network interfaces\.

The number of IP addresses that Amazon ES requires depends on the ratio of data nodes to master nodes\.

Here's the basic formula: The number of IP addresses reserved in each subnet is three times the number of data nodes, plus the number of master nodes\.

**Examples**
+ If a domain has 10 data nodes and three master nodes, the IP count is \(10 \* 3\) \+ 3 = 33\.
+ If a domain has five data nodes and three master nodes, the IP count is \(5 \* 3\) \+ 3 = 18\.

When you create the domain, Amazon ES reserves the IP addresses, uses some for the domain, and reserves the rest for [blue/green deployments](es-managedomains-configuration-changes.md)\. You can see the network interfaces and their associated IP addresses in the **Network Interfaces** section of the Amazon EC2 console\. The **Description** column shows which Amazon ES domain the network interface is associated with\.

**Tip**  
We recommend that you create dedicated subnets for the Amazon ES reserved IP addresses\. By using dedicated subnets, you avoid overlap with other applications and services and ensure that you can reserve additional IP addresses if you need to scale your cluster in the future\. To learn more, see [Creating a subnet in your VPC](https://docs.aws.amazon.com/vpc/latest/userguide/working-with-vpcs.html#AddaSubnet)\.

## Service\-linked role for VPC access<a name="es-enabling-slr"></a>

A [service\-linked role](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_terms-and-concepts.html#iam-term-service-linked-role) is a unique type of IAM role that delegates permissions to a service so that it can create and manage resources on your behalf\. Amazon ES requires a service\-linked role to access your VPC, create the domain endpoint, and place network interfaces in a subnet of your VPC\.

Amazon ES automatically creates the role when you use the Amazon ES console to create a domain within a VPC\. For this automatic creation to succeed, you must have permissions for the `es:CreateElasticsearchServiceRole` and `iam:CreateServiceLinkedRole` actions\. To learn more, see [Service\-Linked Role Permissions](https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html#service-linked-role-permissions) in the *IAM User Guide*\.

After Amazon ES creates the role, you can view it \(`AWSServiceRoleForAmazonElasticsearchService`\) using the IAM console\.

**Note**  
If you create a domain that uses a public endpoint, Amazon ES doesn’t need the service\-linked role and doesn't create it\.

For full information on this role's permissions and how to delete it, see [Using service\-linked roles to provide Amazon Elasticsearch Service access to resources](slr-es.md)\.

## Migrating from public access to VPC access<a name="es-migrating-public-to-vpc"></a>

When you create a domain, you specify whether it should have a public endpoint or reside within a VPC\. Once created, you cannot switch from one to the other\. Instead, you must create a new domain and either manually reindex or migrate your data\. Snapshots offer a convenient means of migrating data\. For information about taking and restoring snapshots, see [Creating index snapshots in Amazon Elasticsearch Service](es-managedomains-snapshots.md)\.