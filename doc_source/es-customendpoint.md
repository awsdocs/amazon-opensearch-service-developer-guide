# Creating a Custom Endpoint<a name="es-customendpoint"></a>

Creating a custom endpoint for your Amazon Elasticsearch Service domain makes it easier for you to refer to your Elasticsearch and Kibana URLs\. You can include your company's branding or just use a shorter, easier\-to\-remember endpoint than the standard one\.

If you ever need to switch to a new domain, just update your DNS to point to the new URL and continue using the same endpoint as before\.

You secure custom endpoints by either generating a certificate in AWS Certificate Manager \(ACM\) or importing one of your own\.

## Custom Endpoints for New Domains<a name="es-customize-endpoint"></a>

You can enable a custom endpoint for a new Amazon ES domain by using the Amazon Elasticsearch Service console, AWS CLI, or configuration API\.

**To customize your endpoint \(console\)**

1. From the Amazon Elasticsearch dashboard, choose **Create a new domain**\.

1. For **Elasticsearch domain name**, enter your domain name\.

1. To add a **Custom endpoint**, select the **Enable custom endpoint** check box\.

1. For **Custom hostname**, enter your preferred custom endpoint hostname\. Your custom endpoint hostname should be a fully qualified domain name \(FQDN\), such as www\.yourdomain\.com or example\.yourdomain\.com\. 
**Note**  
You must obtain a new certificate for your custom endpoint's subdomains if you don't have a [wildcard certificate](https://en.wikipedia.org/wiki/Wildcard_certificate)\. 

1. For **AWS certificate**, choose the SSL certificate that you want to use for your domain\. If you don't see a certificate that is available to choose, you can import a certificate into ACM or use ACM to provision one for you\. For more information, see [Issuing and Managing Certificates](https://docs.aws.amazon.com/acm/latest/userguide/gs.html) in the *AWS Certificate Manager User Guide*\. 
**Note**  
The certificate must have the custom endpoint name and be in the same account as your Amazon ES domain\.
   + Choose **Confirm**\.
   + After the new domain finishes processing, you can view your custom endpoint by choosing your domain and checking the **Overview** tab\.

   To use the CLI or configuration API, use the `CreateElasticsearchDomain` and ` UpdateElasticsearchDomainConfig` operations\. For more information, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/) and [Amazon Elasticsearch Service Configuration API Reference](es-configuration-api.md)\.

## Custom Endpoints for Existing Domains<a name="es-enable-disable-custom-endpoint"></a>

To add or remove a custom endpoint on an existing Amazon ES domain, choose **Edit domain** and follow steps 3–6 above\.

## Next Steps<a name="es-customize-endpoint-next-steps"></a>

After you enable a custom endpoint for your Amazon ES domain, you must create an alias or CNAME mapping in Amazon Route 53 \(or your preferred DNS service provider\) to route traffic to the custom endpoint and its subdomains\. Without this mapping, your custom endpoint will not work\. For steps on performing this mapping in Route 53, see [Configuring DNS routing for a new domain ](https://docs.aws.amazon.com/Route53/latest/DeveloperGuide/dns-configuring-new-domain.html) and [Creating a hosted zone for a subdomain](https://docs.aws.amazon.com/Route53/latest/DeveloperGuide/dns-routing-traffic-for-subdomains.html#dns-routing-traffic-for-subdomains-creating-hosted-zone)\. For other providers, consult their documentation\.

If you use [SAML authentication for Kibana](saml.md), you must update your IdP with the new SSO URL\.