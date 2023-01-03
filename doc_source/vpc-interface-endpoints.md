# Access Amazon OpenSearch Service using an OpenSearch Service\-managed VPC endpoint \(AWS PrivateLink\)<a name="vpc-interface-endpoints"></a>

You can access an Amazon OpenSearch Service domain by setting up an OpenSearch Service\-managed VPC endpoint \(powered by AWS PrivateLink\)\. These endpoints create a private connection between your VPC and Amazon OpenSearch Service\. You can access OpenSearch Service VPC domains as if they were in your VPC, without the use of an internet gateway, NAT device, VPN connection, or AWS Direct Connect connection\. Instances in your VPC don't need public IP addresses to access OpenSearch Service\. 

You can configure OpenSearch Service domains to expose additional endpoints running on public or private subnets within the same VPC, different VPC, or different AWS accounts\. This enables you to add an additional layer of security to access your domains regardless of where they run, with no infrastructure to manage\. The following diagram illustrates OpenSearch Service\-managed VPC endpoints within the same VPC:

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/Privatelink-Diagram.png)

You establish this private connection by creating an OpenSearch Service\-managed *interface VPC endpoint*, powered by AWS PrivateLink\. We create an endpoint network interface in each subnet that you enable for the interface VPC endpoint\. These are service\-managed network interfaces that serve as the entry point for traffic destined for OpenSearch Service\. Standard [AWS PrivateLink interface endpoint pricing](https://aws.amazon.com/privatelink/pricing/) applies for OpenSearch Service\-managed VPC endpoints billed under AWS PrivateLink\.

You can create VPC endpoints for domains running all versions of OpenSearch and legacy Elasticsearch\. For more information, see [Access AWS services through AWS PrivateLink](https://docs.aws.amazon.com/vpc/latest/privatelink/privatelink-access-aws-services.html) in the *AWS PrivateLink Guide*\.

## Considerations and limitations for OpenSearch Service<a name="vpc-endpoint-considerations"></a>

Before you set up an interface VPC endpoint for OpenSearch Service, review [Considerations](https://docs.aws.amazon.com/vpc/latest/privatelink/create-interface-endpoint.html#considerations-interface-endpoints) in the *AWS PrivateLink Guide*\. 

When using OpenSearch Service\-managed VPC endpoints, consider the following:
+ You can only use interface VPC endpoints to connect to [VPC domains](vpc.md)\. Public domains aren't supported\.
+ VPC endpoints can only connect to domains within the same AWS Region\.
+ HTTPS is the only supported protocol for VPC endpoints\. HTTP is not allowed\.
+ OpenSearch Service supports making calls to all of the [supported OpenSearch API operations](supported-operations.md) through an interface VPC endpoint\.
+ You can configure a maximum of 50 endpoints per account, and a maximum of 10 endpoints per domain\. A single domain can have a maximum of 10 [authorized principals](#vpc-endpoint-access)\.
+ You currently can't use AWS CloudFormation to create interface VPC endpoints\.
+ You can only create interface VPC endpoints through the OpenSearch Service console or using the [OpenSearch Service API](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/Welcome.html)\. You can't create interface VPC endpoints for OpenSearch Service using the Amazon VPC console\.
+ OpenSearch Service\-managed VPC endpoints aren't accessible from the internet\. An OpenSearch Service\-managed VPC endpoint is accessible only within the VPC where the endpoint is provisioned or any VPCs peered with the VPC where the endpoint is provisioned, as permitted by the route tables and security groups\.
+ VPC endpoint policies are not supported for OpenSearch Service\. You can associate a security group with the endpoint network interfaces to control traffic to OpenSearch Service through the interface VPC endpoint\.

## Provide access to a domain<a name="vpc-endpoint-access"></a>

If the VPC that you want to access your domain is in another AWS account, you need to authorize it from the owner's account before you can create an interface VPC endpoint\.

**To allow a VPC in another AWS account to access your domain**

1. Open the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/esv3/](https://console.aws.amazon.com/esv3/ )\.

1. In the navigation pane, choose **Domains** and open the domain that you want to provide access to\.

1. Go to the **VPC endpoints** tab, which shows the accounts and corresponding VPCs that have access to your domain\. 

1. Choose **Authorize principal**\.

1. Enter the AWS account ID of the account that will access your domain\. This step authorizes the specified account to create VPC endpoints against the domain\.

1. Choose **Authorize**\.

## Create an interface VPC endpoint for a VPC domain<a name="vpc-endpoint-create"></a>

You can create an interface VPC endpoint for OpenSearch Service using either the OpenSearch Service console or the AWS Command Line Interface \(AWS CLI\)\.

**To create an interface VPC endpoint for an OpenSearch Service domain**

1. Open the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/esv3/](https://console.aws.amazon.com/esv3/ )\.

1. In the left navigation pane, choose **VPC endpoints**\.

1. Choose **Create endpoint**\.

1. Select whether to connect a domain in the current AWS account or another AWS account\. 

1. Select the domain that you connect to with this endpoint\. If the domain is in the current AWS account, use the dropdown to choose the domain\. If the domain is in a different account, enter the Amazon Resource Name \(ARN\) of the domain to connect to\. To choose a domain in a different account, the owner needs to [provide you access](#vpc-endpoint-access) to the domain\.

1. For **VPC**, select the VPC from which you'll access OpenSearch Service\.

1. For **Subnets**, select one or more subnets from which you'll access OpenSearch Service\.

1. For **Security groups**, select the security groups to associate with the endpoint network interfaces\. This is a critical step in which you limit what ports, protocols, and sources for inbound traffic that you’re authorizing into your endpoint\. The security group rules must allow the resources that will use the VPC endpoint to communicate with OpenSearch Service to communicate with the endpoint network interface\.

1. Choose **Create endpoint**\. The endpoint should be active within 2\-5 minutes\.

## Working with OpenSearch Service\-managed VPC endpoints using the configuration API<a name="vpc-endpoint-api"></a>

Use the following API operations to create and manage OpenSearch Service\-managed VPC endpoints\.
+ [CreateVpcEndpoint](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_CreateVpcEndpoint.html)
+ [ListVpcEndpoints](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_ListVpcEndpoints.html)
+ [UpdateVpcEndpoint](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_UpdateVpcEndpoint.html)
+ [DeleteVpcEndpoint](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_DeleteVpcEndpoint.html)

Use the following API operations to manage endpoint access to VPC domains:
+ [AuthorizeVpcEndpointAccess](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_AuthorizeVpcEndpointAccess.html)
+ [ListVpcEndpointAccess](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_ListVpcEndpointAccess.html)
+ [ListVpcEndpointsForDomain](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_ListVpcEndpointsForDomain.html)
+ [RevokeVpcEndpointAccess](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_RevokeVpcEndpointAccess.html)