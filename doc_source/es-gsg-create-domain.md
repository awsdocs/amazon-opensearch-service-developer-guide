# Step 1: Create an Amazon ES Domain<a name="es-gsg-create-domain"></a>

**Important**  
This process is a concise tutorial for configuring a *test domain*\. It shouldn't be used to create production domains\. For a comprehensive version of the same process, see [Creating and Configuring Amazon Elasticsearch Service Domains](es-createupdatedomains.md)\.

An Amazon ES domain is synonymous with an Elasticsearch cluster\. Domains are clusters with the settings, instance types, instance counts, and storage resources that you specify\. You can create an Amazon ES domain by using the console, the AWS CLI, or the AWS SDKs\.

**To create an Amazon ES domain \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. Choose **Create a new domain**\.

1. On the **Create Elasticsearch domain** page, for **Deployment type**, choose **Development and testing**\.

1. For **Version**, choose an Elasticsearch version for your domain\. We recommend that you choose the latest supported version\. For more information, see [Supported Elasticsearch Versions](what-is-amazon-elasticsearch-service.md#aes-choosing-version)\.

1. Choose **Next**\.

1. Enter a name for the domain\. In this tutorial, we use the domain name *movies* for the examples that we provide later in the tutorial\.

1. For **Instance type**, choose an instance type for the Amazon ES domain\. For this tutorial, we recommend `t2.small.elasticsearch`, a small and inexpensive instance type suitable for testing purposes\.

1. For **Instance count**, choose the number of instances that you want\. For this tutorial, use the default value of 1\.

1. For **Storage type**, choose **EBS**\.

   1. For **EBS volume type**, choose General Purpose \(SSD\)\. For more information, see [Amazon EBS Volume Types\.](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/EBSVolumeTypes.html)

   1. For **EBS volume size**, enter the size in GiB of the external storage for *each* data node\. For this tutorial, use the default value of 10\.

1. For now, you can ignore the **Dedicated master nodes**, **Snapshot configuration**, and **Optional Elasticsearch cluster settings** sections\.

1. Choose **Next**\.

1. For simplicity in this tutorial, we recommend an IP\-based access policy\. For **Network configuration**, choose **Public access**\.

1. For now, you can ignore **Amazon Cognito Authentication**\.

1. For **Access policy**, choose **IPv4 address** for **Type**, and then enter your public IP address into the **Enter Principal** field\. You can find your IP address by searching for "What is my IP?" on most search engines\.

1. Under **Select Action**, choose **Allow**\.

1. Under **Encryption**, keep all default values\.

1. Choose **Next**\.

1. On the **Review** page, review your domain configuration, and then choose **Confirm**\.
**Note**  
New domains take about ten minutes to initialize\. After your domain is initialized, you can upload data and make changes to the domain\.

**To create an Amazon ES domain \(AWS CLI\)**
+ Run the following command to create an Amazon ES domain\.

  The command creates a domain named *movies* with Elasticsearch version 6\.0\. It specifies one instance of the `t2.small.elasticsearch` instance type\. The instance type requires EBS storage, so it specifies a 10 GiB volume\. Finally, the command applies an IP\-based access policy that restricts access to the domain to a single IP address\.

  You need to replace `your_ip_address` in the command with your public IP address, which you can find by searching for "What is my IP?" on [Google](https://www.google.com)\.

  ```
  aws es create-elasticsearch-domain --domain-name movies --elasticsearch-version 6.0 --elasticsearch-cluster-config InstanceType=t2.small.elasticsearch,InstanceCount=1 --ebs-options EBSEnabled=true,VolumeType=standard,VolumeSize=10 --access-policies '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"AWS":"*"},"Action":["es:*"],"Condition":{"IpAddress":{"aws:SourceIp":["your_ip_address"]}}}]}'
  ```

**Note**  
New domains take about ten minutes to initialize\. After your domain is initialized, you can upload data and make changes to the domain\.

Use the following command to query the status of the new domain:

```
aws es describe-elasticsearch-domain --domain movies
```

**To create an Amazon ES domain \(AWS SDKs\)**

The AWS SDKs \(except the Android and iOS SDKs\) support all the actions defined in the [Amazon ES Configuration API Reference](es-configuration-api.md), including the `CreateElasticsearchDomain` action\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\. 