# Troubleshooting<a name="aes-troubleshooting"></a>

The following sections offer solutions to common problems that you might encounter when you use services \(such as Amazon S3, Kinesis, and IAM\) and products \(such as Kibana\) that integrate with Amazon Elasticsearch Service \(Amazon ES\):

**Topics**
+ [Kibana: I Can't Sign AWS Service Requests to the Kibana Service Endpoint](#aes-troubleshooting-kibana-configure-anonymous-access)
+ [Kibana: I Don't See the Indices for My Elasticsearch Domain in Kibana 4](#aes-troubleshooting-kibana-find-indices)
+ [Kibana: I Get a Browser Error When I Use Kibana to View My Data](#aes-troubleshooting-kibana-debug-browser-errors)
+ [Integrations: I Don't See a Service Role for Amazon ES in the IAM Console](#aes-troubleshooting-integrations)
+ [Domain Creation: Unauthorized Operation When Selecting VPC Access](#es-vpc-permissions)
+ [Domain Creation: Stuck at Loading After Choosing VPC Access](#es-vpc-sts)
+ [SDKs: I Get Certificate Errors When I Try to Use an SDK](#aes-troubleshooting-certificates)

For information about service\-specific errors, see [Handling AWS Service Errors](aes-handling-errors.md) in this guide\.

## Kibana: I Can't Sign AWS Service Requests to the Kibana Service Endpoint<a name="aes-troubleshooting-kibana-configure-anonymous-access"></a>

The Kibana endpoint doesn't support signed AWS service requests\. We recommend that you access Kibana with one of the configuration options described in the following table\.


****  

| Kibana Configuration | Description | 
| --- | --- | 
| Anonymous, IP\-based Access | If the Kibana host is behind a firewall, configure the Kibana endpoint to accept anonymous requests from the IP address of the firewall\. Use CIDR notation if you need to specify a range of IP addresses\. | 
| NAT Gateway with Amazon VPC | Amazon VPC supports NAT gateways\. When you create a NAT gateway, you must specify an Elastic IP address to associate with the gateway\. The NAT gateway sends traffic to the public Internet gateway using the Elastic IP address as the source IP address\. Specify this Elastic IP address in the access policy for Kibana to allow all requests from the gateway\. For more information, see [NAT Gateway Basics](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/vpc-nat-gateway.html#nat-gateway-basics) and [Creating a NAT Gateway](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/vpc-nat-gateway.html#nat-gateway-creating) in the Amazon Virtual Private Cloud User Guide\. | 

**Examples**

The following example is an anonymous, IP\-based access policy that specifies a range of IP addresses and an 18\-bit routing prefix:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "",
      "Effect": "Allow",
      "Principal": {
        "AWS": "*"
      },
      "Action": "es:*",
      "Resource": "arn:aws:es:us-west-2:123456789012:domain/mydomain/_plugin/kibana",
      "Condition": {
        "IpAddress": {
          "aws:SourceIp": "192.240.192.0/18"
        }
      }
    }
  ]
}
```

The following example specifies anonymous access from the Elastic IP address associated with a NAT gateway:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "",
      "Effect": "Allow",
      "Principal": {
        "AWS": "*"
      },
      "Action": "es:*",
      "Resource": "arn:aws:es:us-west-2:123456789012:domain/mydomain/_plugin/kibana",
      "Condition": {
        "IpAddress": {
          "aws:SourceIp": "198.51.100.4"
        }
      }
    }
  ]
}
```

## Kibana: I Don't See the Indices for My Elasticsearch Domain in Kibana 4<a name="aes-troubleshooting-kibana-find-indices"></a>

Users familiar with Kibana 3 but new to Kibana 4 sometimes have difficulty finding their indices in the interface\. Unlike Kibana 3, which provides default dashboards to view your data, Kibana 4 requires you to first specify an index name or pattern that matches the name of an index in your Amazon ES domain\. For example, if your Amazon ES domain contains an index named `movies-2013`, any of the following patterns would match this index:
+ movies\-2013
+ movies\-\*
+ mov\*

You can configure visualizations for the data in your Amazon ES domain index after you specify the index name or pattern\. You can find the names of your domain indices on the **Indices** tab in the Amazon ES console\. For more information about using Kibana 4, see the [Kibana User Guide](https://www.elastic.co/guide/en/kibana/4.0/index.html)\. 

**Note**  
Amazon ES also supports Kibana 3, so you can configure access to that version of the tool if you prefer to use it\. Specify `/_plugin/kibana3` as the resource in your access policy rather than `/_plugin/kibana`\. After the service finishes processing the configuration change, you can access Kibana 3 by manually editing the Kibana service endpoint provided in the Amazon ES console\. For example, if the console indicates that your Kibana endpoint is `mydomain-6w5y8xjt5ydwsrubmdk4m5kcpa.us-west-2.es.amazonaws.com/_plugin/kibana/`, point your browser to `mydomain-6w5y8xjt5ydwsrubmdk4m5kcpa.us-west-2.es.amazonaws.com/_plugin/kibana3/` instead\.

## Kibana: I Get a Browser Error When I Use Kibana to View My Data<a name="aes-troubleshooting-kibana-debug-browser-errors"></a>

Your browser wraps service error messages in HTTP response objects when you use Kibana to view data in your Amazon ES domain\. You can use developer tools commonly available in web browsers, such as Developer Mode in Chrome, to view the underlying service errors and assist your debugging efforts\.

**To view service errors in Chrome**

1. From the menu, choose **View**, **Developer**, **Developer Tools**\.

1. Choose the **Network** tab\.

1. In the **Status** column, choose any HTTP session with a status of 500\.

   For example, the following service error message indicates that a search request likely failed for one of the reasons shown in the following table:

   `"Request to Elasticsearch failed: {"error":"SearchP…be larger than limit of [5143501209/4.7gb]]; }]"}"`   
****    
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-troubleshooting.html)

**To view service errors in Firefox**

1. From the menu, choose **Tools**, **Web Developer**, **Network**\.

1. Choose any HTTP session with a status of 500\.

1. Choose the **Response** tab to view the service response\.

## Integrations: I Don't See a Service Role for Amazon ES in the IAM Console<a name="aes-troubleshooting-integrations"></a>

You can integrate Amazon ES with other services, such as Amazon S3 and Kinesis, as described in [Loading Streaming Data into Amazon ES from Amazon S3](es-aws-integrations.md#es-aws-integrations-s3-lambda-es) and [Loading Streaming Data into Amazon ES from Kinesis](es-aws-integrations.md#es-aws-integrations-kinesis-lambda-es) in this guide\. Both of these integrations use AWS Lambda as an event handler in the cloud\. When you create a Lambda function using the AWS Lambda console, the console automatically opens the IAM console to help you create the required execution role\. You don't need to open the IAM console yourself and select a service role\. However, you must open the IAM console after AWS Lambda helps you to create the new role and attach the following IAM policy to it:

```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": [
                "es:*"
            ],
            "Effect": "Allow",
            "Resource": "arn:aws:es:us-west-2:123456789012:domain/<my_domain_name>/*"
        }
    ]
}
```

For step\-by\-step procedures, see [Loading Streaming Data into Amazon ES from Amazon S3](es-aws-integrations.md#es-aws-integrations-s3-lambda-es) and [Loading Streaming Data into Amazon ES from Kinesis](es-aws-integrations.md#es-aws-integrations-kinesis-lambda-es) in this guide\. 

## Domain Creation: Unauthorized Operation When Selecting VPC Access<a name="es-vpc-permissions"></a>

When you create a new domain using the Amazon ES console, you have the option to select public access or VPC access\. If you select **VPC access**, Amazon ES queries for VPC information and fails if you don't have the right policies associated with your user credentials\. The error message follows:

```
You are not authorized to perform this operation. (Service: AmazonEC2; Status Code: 403; Error Code: UnauthorizedOperation
```

To enable this query, you must have access to the `ec2:DescribeVpcs`, `ec2:DescribeSubnets`, and `ec2:DescribeSecurityGroups` operations\. This requirement is only for the console\. If you use the AWS CLI to create and configure a domain with a VPC endpoint, you don't need access to those operations\.

## Domain Creation: Stuck at Loading After Choosing VPC Access<a name="es-vpc-sts"></a>

After creating a new domain that uses VPC access, the domain's **Configuration state** might never progress beyond **Loading**\. If this issue occurs, you likely have AWS Security Token Service \(AWS STS\) *disabled* for your region\.

To add VPC endpoints to your VPC, Amazon ES needs to assume the `AWSServiceRoleForAmazonElasticsearchService` role\. Thus, AWS STS must be enabled to create new domains that use VPC access in a given region\. To learn more about enabling and disabling AWS STS, see the [IAM User Guide](http://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_temp_enable-regions.html)\.

## SDKs: I Get Certificate Errors When I Try to Use an SDK<a name="aes-troubleshooting-certificates"></a>

Because AWS SDKs use the CA certificates from your computer, changes to the certificates on the AWS servers can cause connection failures when you attempt to use an SDK\. Error messages vary, but typically contain the following text:

```
Failed to query Elasticsearch
...
SSL3_GET_SERVER_CERTIFICATE:certificate verify failed
```

You can prevent these failures by keeping your computer's CA certificates and operating system up\-to\-date\. If you encounter this issue in a corporate environment and do not manage your own computer, you might need to ask an administrator to assist with the update process\.

The following list shows minimum operating system and Java versions:
+ Microsoft Windows versions that have updates from January 2005 or later installed contain at least one of the required CAs in their trust list\.
+ Mac OS X 10\.4 with Java for Mac OS X 10\.4 Release 5 \(February 2007\), Mac OS X 10\.5 \(October 2007\), and later versions contain at least one of the required CAs in their trust list\.
+ Red Hat Enterprise Linux 5 \(March 2007\), 6, and 7 and CentOS 5, 6, and 7 all contain at least one of the required CAs in their default trusted CA list\.
+ Java 1\.4\.2\_12 \(May 2006\), 5 Update 2 \(March 2005\), and all later versions, including Java 6 \(December 2006\), 7, and 8, contain at least one of the required CAs in their default trusted CA list\.

The three certificate authorities are:
+ Amazon Root CA 1
+ Starfield Services Root Certificate Authority \- G2
+ Starfield Class 2 Certification Authority

Root certificates from the first two authorities are available from [Amazon Trust Services](https://www.amazontrust.com/repository/), but keeping your computer up\-to\-date is the more straightforward solution\. To learn more about ACM\-provided certificates, see [AWS Certificate Manager FAQs](https://aws.amazon.com/certificate-manager/faqs/#certificates)\.

**Note**  
Currently, Amazon ES domains in the us\-east\-1 region use certificates from a different authority\. We plan to update the region to use these new certificate authorities in the near future\.