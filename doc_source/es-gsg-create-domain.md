# Step 1: Create an Amazon ES Domain<a name="es-gsg-create-domain"></a>

**Important**  
This process is a concise tutorial for configuring a *test domain*\. It shouldn't be used to create production domains\. For a comprehensive version of the same process, see [Creating and Managing Amazon Elasticsearch Service Domains](es-createupdatedomains.md)\.

An Amazon ES domain is synonymous with an Elasticsearch cluster\. Domains are clusters with the settings, instance types, instance counts, and storage resources that you specify\. You can create an Amazon ES domain by using the console, the AWS CLI, or the AWS SDKs\.

**To create an Amazon ES domain \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. Choose **Create a new domain**\.

1. On the **Create Elasticsearch domain** page, choose **Development and testing**\.

1. For **Elasticsearch version**, choose the latest version and **Next**\.

1. Enter a name for the domain\. In this tutorial, we use the domain name *movies* for the examples that we provide later in the tutorial\.

1. For **Data nodes**, choose the `c5.large.elasticsearch` instance type\. Use the default value of 1 instance\.

1. For **Data nodes storage**, use the default values\.

1. For now, you can ignore the **Dedicated master nodes**, **UltraWarm data nodes**, **Snapshot configuration**, and **Optional Elasticsearch cluster settings** sections\.

1. Choose **Next**\.

1. For simplicity in this tutorial, we recommend a public access domain\. For **Network configuration**, choose **Public access**\.

1. For **Fine\-grained access control**, choose **Create master user**\. Specify a username and password\.

1. For now, you can ignore **Amazon Cognito Authentication**\.

1. For **Access policy**, choose **Allow open access to the domain**\. In this tutorial, fine\-grained access control handles authentication, not the domain access policy\.

1. Leave the encryption settings at their default values, and choose **Next**\.

1. On the **Review** page, double\-check your configuration and choose **Confirm**\. New domains typically take 15\-30 minutes to initialize, but can take longer depending on the configuration\. After your domain initializes, make note of its endpoint\.