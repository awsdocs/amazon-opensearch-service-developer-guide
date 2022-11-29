# Creating a custom endpoint for Amazon OpenSearch Service<a name="customendpoint"></a>

Creating a custom endpoint for your Amazon OpenSearch Service domain makes it easier for you to refer to your OpenSearch and OpenSearch Dashboards URLs\. You can include your company's branding or just use a shorter, easier\-to\-remember endpoint than the standard one\.

If you ever need to switch to a new domain, just update your DNS to point to the new URL and continue using the same endpoint as before\.

You secure custom endpoints by either generating a certificate in AWS Certificate Manager \(ACM\) or importing one of your own\.

## Custom endpoints for new domains<a name="customize-endpoint"></a>

You can enable a custom endpoint for a new OpenSearch Service domain using the OpenSearch Service console, AWS CLI, or configuration API\.

**To customize your endpoint \(console\)**

1. From the OpenSearch Service console, choose **Create domain** and provide a name for the domain\.

1. Under **Custom endpoint**, select **Enable custom endpoint**\.

1. For **Custom hostname**, enter your preferred custom endpoint hostname\. The hostname should be a fully qualified domain name \(FQDN\), such as www\.yourdomain\.com or example\.yourdomain\.com\. 
**Note**  
If you don't have a [wildcard certificate](https://en.wikipedia.org/wiki/Wildcard_certificate) you must obtain a new certificate for your custom endpoint's subdomains\.

1. For **AWS certificate**, choose the SSL certificate to use for your domain\. If no certificates are available, you can import one into ACM or use ACM to provision one\. For more information, see [Issuing and Managing Certificates](https://docs.aws.amazon.com/acm/latest/userguide/gs.html) in the *AWS Certificate Manager User Guide*\. 
**Note**  
The certificate must have the custom endpoint name and be in the same account as your OpenSearch Service domain\. The certificate status should be ISSUED\. 
   + Follow the rest of the steps to create your domain and choose **Create**\.
   + Select the domain when it's finished processing to view your custom endpoint\.

   To use the CLI or configuration API, use the `CreateDomain` and `UpdateDomainConfig` operations\. For more information, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/) and [Amazon OpenSearch Service API Reference](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/Welcome.html)\.

## Custom endpoints for existing domains<a name="enable-disable-custom-endpoint"></a>

To add a custom endpoint to an existing OpenSearch Service domain, choose **Edit** and perform steps 2–4 above\. Editing a domain's custom endpoint triggers a [blue/green deployment](managedomains-configuration-changes.md)\.

## Next steps<a name="customize-endpoint-next-steps"></a>

After you enable a custom endpoint for your OpenSearch Service domain, you must create a CNAME mapping in Amazon Route 53 \(or your preferred DNS service provider\) to route traffic to the custom endpoint and its subdomains\. Create the CNAME record pointing the custom endpoint to the auto\-generated domain endpoint\. The custom endpoint hostname is the *name* of the CNAME record, and the domain endpoint hostname is the *value* of the CNAME record\. Without this mapping, your custom endpoint won't work\. For steps to create this mapping in Route 53, see [Configuring DNS routing for a new domain ](https://docs.aws.amazon.com/Route53/latest/DeveloperGuide/dns-configuring-new-domain.html) and [Creating a hosted zone for a subdomain](https://docs.aws.amazon.com/Route53/latest/DeveloperGuide/dns-routing-traffic-for-subdomains.html#dns-routing-traffic-for-subdomains-creating-hosted-zone)\. For other providers, consult their documentation\.

If you use [SAML authentication for OpenSearch Dashboards](saml.md), you must update your IdP with the new SSO URL\.