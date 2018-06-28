# Troubleshooting<a name="aes-troubleshooting"></a>

The following sections offer solutions to common problems that you might encounter when you use services and products that integrate with Amazon Elasticsearch Service \(Amazon ES\):

**Topics**
+ [Kibana: Can't Access Kibana](#aes-troubleshooting-kibana-configure-anonymous-access)
+ [Snapshots: "Not Valid for the Object's Storage Class" Error](#aes-troubleshooting-glacier-snapshots)
+ [Kibana: Browser Error When Viewing Data](#aes-troubleshooting-kibana-debug-browser-errors)
+ [Domain Creation: Unauthorized Operation When Selecting VPC Access](#es-vpc-permissions)
+ [Domain Creation: Stuck at Loading After Choosing VPC Access](#es-vpc-sts)
+ [SDKs: Certificate Errors](#aes-troubleshooting-certificates)

For information about service\-specific errors, see [Handling AWS Service Errors](aes-handling-errors.md) in this guide\.

## Kibana: Can't Access Kibana<a name="aes-troubleshooting-kibana-configure-anonymous-access"></a>

The Kibana endpoint doesn't support signed requests\. If the access control policy for your domain only grants access to certain IAM users or roles, you might receive the following error when you attempt to access Kibana:

```
"User: anonymous is not authorized to perform: es:ESHttpGet"
```

If your Amazon ES domain uses VPC access, you might not receive that error\. Instead, the request might time out\. To learn more about correcting this issue and the various configuration options available to you, see [Controlling Access to Kibana](es-kibana.md#es-kibana-access), [About Access Policies on VPC Domains](es-vpc.md#es-vpc-security), and [Amazon Elasticsearch Service Access Control](es-ac.md)\.

## Snapshots: "Not Valid for the Object's Storage Class" Error<a name="aes-troubleshooting-glacier-snapshots"></a>

Amazon ES snapshots do not support the Amazon Glacier storage class\. You might encounter this error when you attempt to list snapshots if your S3 bucket includes a lifecycle rule that transitions objects to the Amazon Glacier storage class\.

If you need to restore a snapshot from the bucket, restore the objects from Amazon Glacier, copy the objects to a new bucket, and [register the new bucket](es-managedomains-snapshots.md#es-managedomains-snapshot-registerdirectory) as a snapshot respository\.

## Kibana: Browser Error When Viewing Data<a name="aes-troubleshooting-kibana-debug-browser-errors"></a>

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

## Domain Creation: Unauthorized Operation When Selecting VPC Access<a name="es-vpc-permissions"></a>

When you create a new domain using the Amazon ES console, you have the option to select public access or VPC access\. If you select **VPC access**, Amazon ES queries for VPC information and fails if you don't have the right policies associated with your user credentials\. The error message follows:

```
You are not authorized to perform this operation. (Service: AmazonEC2; Status Code: 403; Error Code: UnauthorizedOperation
```

To enable this query, you must have access to the `ec2:DescribeVpcs`, `ec2:DescribeSubnets`, and `ec2:DescribeSecurityGroups` operations\. This requirement is only for the console\. If you use the AWS CLI to create and configure a domain with a VPC endpoint, you don't need access to those operations\.

## Domain Creation: Stuck at Loading After Choosing VPC Access<a name="es-vpc-sts"></a>

After creating a new domain that uses VPC access, the domain's **Configuration state** might never progress beyond **Loading**\. If this issue occurs, you likely have AWS Security Token Service \(AWS STS\) *disabled* for your region\.

To add VPC endpoints to your VPC, Amazon ES needs to assume the `AWSServiceRoleForAmazonElasticsearchService` role\. Thus, AWS STS must be enabled to create new domains that use VPC access in a given region\. To learn more about enabling and disabling AWS STS, see the [IAM User Guide](http://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_temp_enable-regions.html)\.

## SDKs: Certificate Errors<a name="aes-troubleshooting-certificates"></a>

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