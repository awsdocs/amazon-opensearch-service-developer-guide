# Access Amazon OpenSearch Serverless using an interface endpoint \(AWS PrivateLink\)<a name="serverless-vpc"></a>

You can use AWS PrivateLink to create a private connection between your VPC and Amazon OpenSearch Serverless\. You can access OpenSearch Serverless as if it were in your VPC, without the use of an internet gateway, NAT device, VPN connection, or AWS Direct Connect connection\. Instances in your VPC don't need public IP addresses to access OpenSearch Serverless\.

You establish this private connection by creating an *interface endpoint*, powered by AWS PrivateLink\. We create an endpoint network interface in each subnet that you specify for the interface endpoint\. These are requester\-managed network interfaces that serve as the entry point for traffic destined for OpenSearch Serverless\.

For more information, see [Access AWS services through AWS PrivateLink](https://docs.aws.amazon.com/vpc/latest/privatelink/privatelink-access-aws-services.html) in the *AWS PrivateLink Guide*\.

**Topics**
+ [Considerations](#vpc-endpoint-considerations)
+ [Permissions required](#serverless-vpc-permissions)
+ [Create an interface endpoint for OpenSearch Serverless](#serverless-vpc-create)
+ [Next step: Grant the endpoint access to a collection](#serverless-vpc-access)

## Considerations<a name="vpc-endpoint-considerations"></a>

Before you set up an interface endpoint for OpenSearch Serverless, consider the following:
+ OpenSearch Serverless supports making calls to all supported [OpenSearch API operations](serverless-genref.md#serverless-operations) \(not configuration API operations\) through the interface endpoint\.
+ After you create an interface endpoint for OpenSearch Serverless, you still need to include it in [network access policies](serverless-network.md) in order for it to access serverless collections\.
+ VPC endpoint policies are not supported for OpenSearch Serverless\. By default, full access to OpenSearch Serverless is allowed through the interface endpoint\. Alternatively, you can associate a security group with the endpoint network interfaces to control traffic to OpenSearch Serverless through the interface endpoint\.
+ A single AWS account can have a maximum of 50 OpenSearch Serverless VPC endpoints\.
+ For other considerations, see [Considerations](https://docs.aws.amazon.com/vpc/latest/privatelink/create-interface-endpoint.html#considerations-interface-endpoints) in the *AWS PrivateLink Guide*\.

## Permissions required<a name="serverless-vpc-permissions"></a>

VPC access for OpenSearch Serverless uses the following AWS Identity and Access Management \(IAM\) permissions\. You can specify IAM conditions to restrict users to specific collections\.
+ `aoss:CreateVpcEndpoint` – Create a VPC endpoint\.
+ `aoss:ListVpcEndpoints` – List all VPC endpoints\.
+ `aoss:BatchGetVpcEndpoint` – See details about a subset of VPC endpoints\.
+ `aoss:UpdateVpcEndpoint` – Modify a VPC endpoint\.
+ `aoss:DeleteVpcEndpoint` – Delete a VPC endpoint\.

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