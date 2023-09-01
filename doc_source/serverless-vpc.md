# Access Amazon OpenSearch Serverless using an interface endpoint \(AWS PrivateLink\)<a name="serverless-vpc"></a>

You can use AWS PrivateLink to create a private connection between your VPC and Amazon OpenSearch Serverless\. You can access OpenSearch Serverless as if it were in your VPC, without the use of an internet gateway, NAT device, VPN connection, or AWS Direct Connect connection\. Instances in your VPC don't need public IP addresses to access OpenSearch Serverless\.

You establish this private connection by creating an *interface endpoint*, powered by AWS PrivateLink\. We create an endpoint network interface in each subnet that you specify for the interface endpoint\. These are requester\-managed network interfaces that serve as the entry point for traffic destined for OpenSearch Serverless\.

For more information, see [Access AWS services through AWS PrivateLink](https://docs.aws.amazon.com/vpc/latest/privatelink/privatelink-access-aws-services.html) in the *AWS PrivateLink Guide*\.

**Topics**
+ [DNS resolution of collection endpoints](#vpc-endpoint-dnc)
+ [VPCs and network access policies](#vpc-endpoint-network)
+ [Considerations](#vpc-endpoint-considerations)
+ [Permissions required](#serverless-vpc-permissions)
+ [Create an interface endpoint for OpenSearch Serverless](#serverless-vpc-create)
+ [Next step: Grant the endpoint access to a collection](#serverless-vpc-access)

## DNS resolution of collection endpoints<a name="vpc-endpoint-dnc"></a>

When you create a VPC endpoint, the service creates a new Amazon Route 53 [private hosted zone](https://docs.aws.amazon.com/Route53/latest/DeveloperGuide/hosted-zones-private.html) and attaches it to the VPC\. This private hosted zone consists of a record to resolve the wildcard DNS record for OpenSearch Serverless collections \(`*.aoss.us-east-1.amazonaws.com`\) to the interface addresses used for the endpoint\. You only need one OpenSearch Serverless VPC endpoint in a VPC to access any and all collections and Dashboards in each AWS Region\. Every VPC with an endpoint for OpenSearch Serverless has its own private hosted zone attached\. 

OpenSearch Serverless also creates a public Route 53 wildcard DNS record for all collections in the Region\. The DNS name resolves to the OpenSearch Serverless public IP addresses\. Clients in VPCs that don't have an OpenSearch Serverless VPC endpoint or clients in public networks can use the public Route 53 resolver and access the collections and Dashboards with those IP addresses\. 

The DNS resolver address for a given VPC is the second IP address of the VPC CIDR\. Any client in the VPC needs to use that resolver to get the VPC endpoint address for any collection\. The resolver uses private hosted zone created by OpenSearch Serverless\. It's sufficient to use that resolver for all collections in any account\. It's also possible to use the VPC resolver for some collection endpoints and the public resolver for others, although it's not typically necessary\.

## VPCs and network access policies<a name="vpc-endpoint-network"></a>

To grant network permission to OpenSearch APIs and Dashboards for your collections, you can use OpenSearch Serverless [network access policies](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-network.html)\. You can control this network access either from your VPC endpoint\(s\) or the public internet\. Since your network policy only controls traffic permissions, you must also set up a [data access policy](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-data-access.html) that specifies permission to operate on the data in a collection and its indices\. Think of an OpenSearch Serverless VPC endpoint as an access point to the service, a network access policy as the network\-level access point to collections and Dashboards, and a data access policy as the access point for fine\-grained access control for any operation on data in the collection\. 

Since you can specify multiple VPC endpoint IDs in a network policy, we recommend that you create a VPC endpoint for every VPC that needs to access a collection\. These VPCs can belong to different AWS accounts than the account that owns the OpenSearch Serverless collection and network policy\. We don’t recommend that you create a VPC\-to\-VPC peering or other proxying solution between two accounts so that one account's VPC can use another account's VPC endpoint\. This is less secure and cost effective than each VPC having its own endpoint\. The first VPC will not be easily visible to the other VPC’s admin, who has set up access to that VPC's endpoint in the network policy\. 

## Considerations<a name="vpc-endpoint-considerations"></a>

Before you set up an interface endpoint for OpenSearch Serverless, consider the following:
+ OpenSearch Serverless supports making calls to all supported [OpenSearch API operations](serverless-genref.md#serverless-operations) \(not configuration API operations\) through the interface endpoint\.
+ After you create an interface endpoint for OpenSearch Serverless, you still need to include it in [network access policies](serverless-network.md) in order for it to access serverless collections\.
+ VPC endpoint policies are not supported for OpenSearch Serverless\. By default, full access to OpenSearch Serverless is allowed through the interface endpoint\. You can associate a security group with the endpoint network interfaces to control traffic to OpenSearch Serverless through the interface endpoint\.
+ A single AWS account can have a maximum of 50 OpenSearch Serverless VPC endpoints\.
+ If you enable public internet access to your collection’s API or Dashboards in a network policy, your collection is accessible by any VPC and by the public internet\.
+ If you're on\-premises and outside of the VPC, you can't use a DNS resolver for the OpenSearch Serverless VPC endpoint resolution directly\. If you need VPN access, the VPC needs a DNS proxy resolver for external clients to use\. Route 53 provides an inbound endpoint option that you can set up in the VPC to resolve the endpoints that the VPC needs to access\.
+ For other considerations, see [Considerations](https://docs.aws.amazon.com/vpc/latest/privatelink/create-interface-endpoint.html#considerations-interface-endpoints) in the *AWS PrivateLink Guide*\.

## Permissions required<a name="serverless-vpc-permissions"></a>

VPC access for OpenSearch Serverless uses the following AWS Identity and Access Management \(IAM\) permissions\. You can specify IAM conditions to restrict users to specific collections\.
+ `aoss:CreateVpcEndpoint` – Create a VPC endpoint\.
+ `aoss:ListVpcEndpoints` – List all VPC endpoints\.
+ `aoss:BatchGetVpcEndpoint` – See details about a subset of VPC endpoints\.
+ `aoss:UpdateVpcEndpoint` – Modify a VPC endpoint\.
+ `aoss:DeleteVpcEndpoint` – Delete a VPC endpoint\.

In addition, you need the following Amazon EC2 and Route 53 permissions in order to create a VPC endpoint\.
+ `ec2:CreateTags`
+ `ec2:CreateVpcEndpoint`
+ `ec2:DeleteVpcEndPoints`
+ `ec2:DescribeSecurityGroups`
+ `ec2:DescribeSubnets`
+ `ec2:DescribeVpcEndpoints`
+ `ec2:DescribeVpcs`
+ `ec2:ModifyVpcEndPoint`
+ `route53:AssociateVPCWithHostedZone`
+ `route53:ChangeResourceRecordSets`
+ `route53:CreateHostedZone`
+ `route53:DeleteHostedZone`
+ `route53:GetChange`
+ `route53:GetHostedZone`
+ `route53:ListHostedZonesByName`
+ `route53:ListHostedZonesByVPC`
+ `route53:ListResourceRecordSets`

## Create an interface endpoint for OpenSearch Serverless<a name="serverless-vpc-create"></a>

You can create an interface endpoint for OpenSearch Serverless using either the console or the OpenSearch Serverless API\. 

**To create an interface endpoint for an OpenSearch Serverless collection**

1. Open the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home)\.

1. In the left navigation pane, expand **Serverless** and choose **VPC endpoints**\.

1. Choose **Create VPC endpoint**\.

1. Provide a name for the endpoint\.

1. For **VPC**, select the VPC that you'll access OpenSearch Serverless from\.

1. For **Subnets**, select one subnet that you'll access OpenSearch Serverless from\.

1. For **Security groups**, select the security groups to associate with the endpoint network interfaces\. This is a critical step where you limit the ports, protocols, and sources for inbound traffic that you’re authorizing into your endpoint\. Make sure that the security group rules allow the resources that will use the VPC endpoint to communicate with OpenSearch Serverless to communicate with the endpoint network interface\.

1. Choose **Create endpoint**\.

To create a VPC endpoint using the OpenSearch Serverless API, use the `CreateVpcEndpoint` command\.

**Note**  
After you create an endpoint, note its ID \(for example, `vpce-050f79086ee71ac05`\. In order to provide the endpoint access to your collections, you must include this ID in one or more network access policies\. 

## Next step: Grant the endpoint access to a collection<a name="serverless-vpc-access"></a>

After you create an interface endpoint, you must provide it access to collections through network access policies\. For more information, see [Network access for Amazon OpenSearch Serverless](serverless-network.md)\.