# Step 1: Create an Amazon ES domain<a name="es-gsg-create-domain"></a>

**Important**  
This is a concise tutorial for configuring a *test * Amazon Elasticsearch Service \(Amazon ES\) domain\. Do not use this process to create production domains\. For a comprehensive version of the same process, see [Creating and managing Amazon Elasticsearch Service domains](es-createupdatedomains.md)\.

An Amazon ES domain is synonymous with an Elasticsearch cluster\. Domains are clusters with the settings, instance types, instance counts, and storage resources that you specify\. You can create an Amazon ES domain by using the console, the AWS CLI, or the AWS SDKs\.

**To create an Amazon ES domain using the console**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com) and choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. Choose **Create a new domain**\.

1. For the deployment type, choose **Development and testing**\.

1. For **Elasticsearch version**, choose the latest version and then choose **Next**\.

1. Provide a name for the domain\. The examples in this tutorial use the name *movies*\.

1. Ignore the **Custom endpoint** setting\.

1. Under **Data nodes**, choose the `t3.small.elasticsearch` instance type with the default value of one node\.

1. Ignore the rest of the settings for now and choose **Next**\.

1. For simplicity in this tutorial, use a public access domain\. Under **Network configuration**, choose **Public access**\.

1. For **Fine\-grained access control**, choose **Create master user**\. Provide a user name and password\.

1. For now, ignore the **SAML authentication** and **Amazon Cognito authentication** sections\.

1. For **Domain access policy**, choose **Allow open access to the domain**\. In this tutorial, fine\-grained access control handles authentication, not the domain access policy\.

1. Keep the encryption settings at their default values and choose **Next**\.

1. Ignore the tags option and choose **Next**\.

1. Confirm your domain configuration and choose **Confirm**\. New domains typically take 15–30 minutes to initialize, but can take longer depending on the configuration\. After your domain initializes, make note of its endpoint\.

**Next**: [Upload data to an Amazon ES domain for indexing](es-gsg-upload-data.md)