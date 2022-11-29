# Using service\-linked roles for Amazon OpenSearch Service<a name="slr"></a>

Amazon OpenSearch Service uses AWS Identity and Access Management \(IAM\) [service\-linked roles](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_terms-and-concepts.html#iam-term-service-linked-role)\. A service\-linked role is a unique type of IAM role that is linked directly to OpenSearch Service\. Service\-linked roles are predefined by OpenSearch Service and include all the permissions that the service requires to call other AWS services on your behalf\. 

A service\-linked role makes setting up OpenSearch Service easier because you don’t have to manually add the necessary permissions\. OpenSearch Service defines the permissions of its service\-linked roles, and unless defined otherwise, only OpenSearch Service can assume its roles\. The defined permissions include the trust policy and the permissions policy, and that permissions policy cannot be attached to any other IAM entity\.

For information about other services that support service\-linked roles, see [AWS services that work with IAM](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_aws-services-that-work-with-iam.html) and look for the services that have **Yes **in the **Service\-linked roles** column\. Choose a **Yes** with a link to view the service\-linked role documentation for that service\.

**Topics**
+ [Using service\-linked roles to create VPC domains](slr-aos.md)
+ [Using service\-linked roles to create OpenSearch Serverless collections](serverless-service-linked-roles.md)