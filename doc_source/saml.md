# SAML authentication for OpenSearch Dashboards<a name="saml"></a>

SAML authentication for OpenSearch Dashboards lets you use your existing identity provider to offer single sign\-on \(SSO\) for Dashboards on Amazon OpenSearch Service domains running OpenSearch or Elasticsearch 6\.7 or later\. To use SAML authentication, you must enable [fine\-grained access control](fgac.md)\.

Rather than authenticating through [Amazon Cognito](cognito-auth.md) or the [internal user database](fgac.md#fgac-dashboards), SAML authentication for OpenSearch Dashboards lets you use third\-party identity providers to log in to Dashboards, manage fine\-grained access control, search your data, and build visualizations\. OpenSearch Service supports providers that use the SAML 2\.0 standard, such as Okta, Keycloak, Active Directory Federation Services \(ADFS\), Auth0, and AWS IAM Identity Center \(successor to AWS Single Sign\-On\)\. 

SAML authentication for Dashboards is only for accessing OpenSearch Dashboards through a web browser\. Your SAML credentials do *not* let you make direct HTTP requests to the OpenSearch or Dashboards APIs\. 

## SAML configuration overview<a name="saml-overview"></a>

This documentation assumes that you have an existing identity provider and some familiarity with it\. We can't provide detailed configuration steps for your exact provider, only for your OpenSearch Service domain\.

The OpenSearch Dashboards login flow can take one of two forms:
+ **Service provider \(SP\) initiated**: You navigate to Dashboards \(for example, `https://my-domain.us-east-1.es.amazonaws.com/_dashboards`\), which redirects you to the login screen\. After you log in, the identity provider redirects you to Dashboards\.
+ **Identity provider \(IdP\) initiated**: You navigate to your identity provider, log in, and choose OpenSearch Dashboards from an application directory\. 

OpenSearch Service provides two single sign\-on URLs, SP\-initiated and IdP\-initiated, but you only need the one that matches your desired OpenSearch Dashboards login flow\.

Regardless of which authentication type you use, the goal is to log in through your identity provider and receive a SAML assertion that contains your username \(required\) and any [backend roles](fgac.md#fgac-concepts) \(optional, but recommended\)\. This information allows [fine\-grained access control](fgac.md) to assign permissions to SAML users\. In external identity providers, backend roles are typically called "roles" or "groups\."

## Considerations<a name="saml-considerations"></a>

Consider the following when you configure SAML authentication:
+ You can't change the SSO URL from its service\-provided value, therefore SAML authentication doesn't support proxy servers\.
+ Due to the size of the IdP metadata file, we highly recommend using the AWS console to configure SAML authentication\.
+ Domains only support one Dashboards authentication method at a time\. If you have [Amazon Cognito authentication for OpenSearch Dashboards](cognito-auth.md) enabled, you must disable it before you can enable SAML authentication\.
+ If you use a network load balancer with SAML, you must first create a custom endpoint\. For more information, see [Creating a custom endpoint for Amazon OpenSearch Service](customendpoint.md)\.

## SAML authentication for VPC domains<a name="saml-vpc"></a>

SAML doesn't require direct communication between your identity provider and your service provider\. Therefore, even if your OpenSearch domain is hosted within a private VPC, you can still use SAML as long as your browser can communicate with both your OpenSearch cluster and your identity provider\. Your browser essentially acts as the intermediary between your identity provider and your service provider\. For a useful diagram that explains the SAML authentication flow, see the [Okta documentation](https://developer.okta.com/docs/concepts/saml/#planning-for-saml)\.

## Modifying the domain access policy<a name="saml-domain-access"></a>

Before you configure SAML authentication, you must update the domain access policy to allow SAML users to access the domain\. Otherwise, you'll see access denied errors\.

We recommend the following [domain access policy](ac.md#ac-types-resource), which provides full access to the subresources \(`/*`\) on the domain:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "*"
      },
      "Action": "es:ESHttp*",
      "Resource": "domain-arn/*"
    }
  ]
}
```

## Configuring SP\- or IdP\-initiated authentication<a name="saml-enable-sp-or-idp"></a>

These steps explain how to enable SAML authentication with SP\-initiated *or* IdP\-initiated authentication for OpenSearch Dashboards\. For the extra step required to enable both, see [Configuring both SP\- and IdP\-initiated authentication](#saml-idp-with-sp)\.

### Step 1: Enable SAML authentication<a name="saml-enable"></a>

You can enable SAML authentication either during domain creation, or by choosing **Actions**, **Edit security configuration** on an existing domain\. The following steps vary slightly depending on which one you choose\.

Within the domain configuration, under **SAML authentication for OpenSearch Dashboards/Kibana**, select **Enable SAML authentication**\.

### Step 2: Configure your identity provider<a name="saml-configure-idp"></a>

Perform the following steps depending on when you're configuring SAML authentication\.

#### If you're creating a new domain<a name="saml-configure-new"></a>

If you're in the process of creating a new domain, OpenSearch Service can't yet generate a service provider entity ID or SSO URLs\. Your identity provider requires these values in order to properly enable SAML authentication, but they can only be generated after the domain is created\. To work around this interdependency during domain creation, you can provide temporary values into your IdP configuration to generate the required metadata and then update them once your domain is active\.

If you're using a [custom endpoint](customendpoint.md), you can infer what the URLs will be\. For example, if your custom endpoint is `www.custom-endpoint.com`, the service provider entity ID will be `www.custom-endpoint.com`, the IdP\-initiated SSO URL will be `www.custom-endpoint.com/_dashboards/_opendistro/_security/saml/acs/idpinitiated`, and the SP\-initiated SSO URL will be `www.custom-endpoint.com/_dashboards/_opendistro/_security/saml/acs`\. You can use the values to configure your identity provider before the domain is created\. See the next section for examples\.

If you're not using a custom endpoint, you can enter *temporary* values into your IdP to generate the required metadata, and then update them later after the domain is active\.

For example, within Okta, you can enter `https://temp-endpoint.amazonaws.com` into the **Single sign on URL** and **Audience URI \(SP Entity ID\)** fields, which enables you to generate the metadata\. Then, after the domain is active, you can retrieve the correct values from OpenSearch Service and update them in Okta\. For instructions, see [Step 6: Update your IdP URLs](#saml-update-urls)\.

#### If you're editing an existing domain<a name="saml-configure-existing"></a>

If you're enabling SAML authentication on an existing domain, copy the service provider entity ID and one of the SSO URLs\. For guidance on which URL to use, see [SAML configuration overview](#saml-overview)\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/SAML.png)

Use the values to configure your identity provider\. This is the most complex part of the process, and unfortunately, terminology and steps vary wildly by provider\. Consult your provider's documentation\.

In Okta, for example, you create a SAML 2\.0 web application\. For **Single sign on URL**, specify the SSO URL\. For **Audience URI \(SP Entity ID\)**, specify the SP entity ID\.

Rather than users and backend roles, Okta has users and groups\. For **Group Attribute Statements**, we recommend that you add `role` to the **Name** field and the regular expression `.+` to the **Filter** field\. This statement tells the Okta identity provider to include all user groups under the `role` field of the SAML assertion after a user authenticates\.

In IAM Identity Center, you specify the SP entity ID as the **Application SAML audience**\. You also need to specify the following [attribute mappings](https://docs.aws.amazon.com/singlesignon/latest/userguide/attributemappingsconcept.html): `Subject=${user:name}` and `Role=${user:groups}`\.

In Auth0, you create a regular web application and enable the SAML 2\.0 add\-on\. In Keycloak, you create a client\. 

### Step 3: Import IdP metadata<a name="saml-import-metadata"></a>

After you configure your identity provider, it generates an IdP metadata file\. This XML file contains information about the provider, such as a TLS certificate, single sign\-on endpoints, and the identity provider's entity ID\.

Copy the contents of the IdP metadata file and paste it into the **Metadata from IdP** field in the OpenSearch Service console\. Alternately, choose **Import from XML file** and upload the file\. The metadata file should look something like this:

```
<?xml version="1.0" encoding="UTF-8"?>
<md:EntityDescriptor entityID="entity-id" xmlns:md="urn:oasis:names:tc:SAML:2.0:metadata">
  <md:IDPSSODescriptor WantAuthnRequestsSigned="false" protocolSupportEnumeration="urn:oasis:names:tc:SAML:2.0:protocol">
    <md:KeyDescriptor use="signing">
      <ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
        <ds:X509Data>
          <ds:X509Certificate>tls-certificate</ds:X509Certificate>
        </ds:X509Data>
      </ds:KeyInfo>
    </md:KeyDescriptor>
    <md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified</md:NameIDFormat>
    <md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress</md:NameIDFormat>
    <md:SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST" Location="idp-sso-url"/>
    <md:SingleSignOnService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect" Location="idp-sso-url"/>
  </md:IDPSSODescriptor>
</md:EntityDescriptor>
```

### Step 4: Configure SAML fields<a name="saml-configure-fields"></a>

After you input your IdP metadata, configure the following additional fields within the OpenSearch Service console:
+ **IdP entity ID** – Copy the value of the `entityID` property from your metadata file and paste it into this field\. Many identity providers also display this value as part of a post\-configuration summary\. Some providers call it the "issuer"\.
+ **SAML master username** and **SAML master backend role** – The user and/or backend role that you specify receive full permissions to the cluster, equivalent to a [new master user](fgac.md#fgac-more-masters), but can only use those permissions within OpenSearch Dashboards\.

  In Okta, for example, you might have a user `jdoe` who belongs to the group `admins`\. If you add `jdoe` to the **SAML master username** field, only that user receives full permissions\. If you add `admins` to the SAML master backend role field, any user that belongs to the `admins` group receives full permissions\.
**Note**  
The contents of the SAML assertion must exactly match the strings that you use for the SAML master username and SAML master role\. Some identity providers add a prefix before their usernames, which can cause a hard\-to\-diagnose mismatch\. In the identity provider user interface, you might see `jdoe`, but the SAML assertion might contain `auth0|jdoe`\. Always use the string from the SAML assertion\.

Many identity providers let you view a sample assertion during the configuration process, and tools like [SAML\-tracer](https://addons.mozilla.org/en-US/firefox/addon/saml-tracer/) can help you examine and troubleshoot the contents of real assertions\. Assertions look something like this:

```
<?xml version="1.0" encoding="UTF-8"?>
<saml2:Assertion ID="id67229299299259351343340162" IssueInstant="2020-09-22T22:03:08.633Z" Version="2.0"
  xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion">
  <saml2:Issuer Format="urn:oasis:names:tc:SAML:2.0:nameid-format:entity">idp-issuer</saml2:Issuer>
  <saml2:Subject>
    <saml2:NameID Format="urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified">username</saml2:NameID>
    <saml2:SubjectConfirmation Method="urn:oasis:names:tc:SAML:2.0:cm:bearer">
      <saml2:SubjectConfirmationData NotOnOrAfter="2020-09-22T22:08:08.816Z" Recipient="domain-endpoint/_dashboards/_opendistro/_security/saml/acs"/>
    </saml2:SubjectConfirmation>
  </saml2:Subject>
  <saml2:Conditions NotBefore="2020-09-22T21:58:08.816Z" NotOnOrAfter="2020-09-22T22:08:08.816Z">
    <saml2:AudienceRestriction>
      <saml2:Audience>domain-endpoint</saml2:Audience>
    </saml2:AudienceRestriction>
  </saml2:Conditions>
  <saml2:AuthnStatement AuthnInstant="2020-09-22T19:54:37.274Z">
    <saml2:AuthnContext>
      <saml2:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport</saml2:AuthnContextClassRef>
    </saml2:AuthnContext>
  </saml2:AuthnStatement>
  <saml2:AttributeStatement>
    <saml2:Attribute Name="role" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified">
      <saml2:AttributeValue
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="xs:string">GroupName Match Matches regex ".+" (case-sensitive)
      </saml2:AttributeValue>
    </saml2:Attribute>
  </saml2:AttributeStatement>
</saml2:Assertion>
```

### Step 5: \(Optional\) Configure additional settings<a name="saml-additional-settings"></a>

Under **Additional settings**, configure the following optional fields:
+ **Subject key** – You can leave this field empty to use the `NameID` element of the SAML assertion for the username\. If your assertion doesn't use this standard element and instead includes the username as a custom attribute, specify that attribute here\.
+ **Roles key** – If you want to use backend roles \(recommended\), specify an attribute from the assertion in this field, such as `role` or `group`\. This is another situation in which tools like [SAML\-tracer](https://addons.mozilla.org/en-US/firefox/addon/saml-tracer/) can help\.
+ **Session time to live** – By default, OpenSearch Dashboards logs users out after 24 hours\. You can configure this value to any number between 60 and 1,440 \(24 hours\) by specifying a new value\.

After you're satisfied with your configuration, save the domain\.

### Step 6: Update your IdP URLs<a name="saml-update-urls"></a>

If you [enabled SAML authentication while creating a domain](#saml-configure-new), you had to specify temporary URLs within your IdP in order to generate the XML metadata file\. After the domain status changes to `Active`, you can get the correct URLs and modify your IdP\.

To retrieve the URLs, select the domain and choose **Actions**, **Edit security configuration**\. Under **SAML authentication for OpenSearch Dashboards/Kibana**, you can find the correct service provider entity ID and SSO URLs\. Copy the values and use them to configure your identity provider, replacing the temporary URLs that you provided in step 2\.

### Step 7: Map SAML users to roles<a name="saml-map-users"></a>

Once your domain status is Active and your IdP is configured correctly, navigate to OpenSearch Dashboards\.
+ If you chose the SP\-initiated URL, navigate to `domain-endpoint/_dashboards`\. To log in to a specific tenant directly, you can append `?security_tenant=tenant-name` to the URL\.
+ If you chose the IdP\-initiated URL, navigate to your identity provider's application directory\.

In both cases, log in as either the SAML master user or a user who belongs to the SAML master backend role\. To continue the example from step 7, log in as either `jdoe` or a member of the `admins` group\.

After OpenSearch Dashboards loads, choose **Security**, **Roles**\. Then, [map roles](fgac.md#fgac-mapping) to allow other users to access OpenSearch Dashboards\.

For example, you might map your trusted colleague `jroe` to the `all_access` and `security_manager` roles\. You might also map the backend role `analysts` to the `readall` and `kibana_user` roles\.

If you prefer to use the API rather than OpenSearch Dashboards, see the following sample request:

```
PATCH _plugins/_security/api/rolesmapping
[
  {
    "op": "add", "path": "/security_manager", "value": { "users": ["master-user", "jdoe", "jroe"], "backend_roles": ["admins"] }
  },
  {
    "op": "add", "path": "/all_access", "value": { "users": ["master-user", "jdoe", "jroe"], "backend_roles": ["admins"] }
  },
  {
    "op": "add", "path": "/readall", "value": { "backend_roles": ["analysts"] }
  },
  {
    "op": "add", "path": "/dashboards_user", "value": { "backend_roles": ["analysts"] }
  }
]
```

## Configuring both SP\- and IdP\-initiated authentication<a name="saml-idp-with-sp"></a>

If you want to configure both SP\- and IdP\-initiated authentication, you must do so through your identity provider\. For example, in Okta, you can perform the following steps:

1. Within your SAML application, go to **General**, **SAML settings**\.

1. For the **Single sign on URL**, provide your *IdP*\-initiated SSO URL\. For example, `https://search-domain-hash/_dashboards/_opendistro/_security/saml/acs/idpinitiated`\.

1. Enable **Allow this app to request other SSO URLs**\.

1. Under **Requestable SSO URLs**, add one or more *SP*\-initiated SSO URLs\. For example, `https://search-domain-hash/_dashboards/_opendistro/_security/saml/acs`\.

## Configuring SAML authentication \(AWS CLI\)<a name="saml-enable-cli"></a>

The following AWS CLI command enables SAML authentication for OpenSearch Dashboards on an existing domain:

```
aws opensearch update-domain-config \
  --domain-name my-domain \
  --advanced-security-options '{"SAMLOptions":{"Enabled":true,"MasterUserName":"my-idp-user","MasterBackendRole":"my-idp-group-or-role","Idp":{"EntityId":"entity-id","MetadataContent":"metadata-content-with-quotes-escaped"},"RolesKey":"optional-roles-key","SessionTimeoutMinutes":180,"SubjectKey":"optional-subject-key"}}'
```

You must escape all quotes and newline characters in the metadata XML\. For example, use `<KeyDescriptor use=\"signing\">\n` instead of `<KeyDescriptor use="signing">` and a line break\. For detailed information about using the AWS CLI, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/)\.

## Configuring SAML authentication \(configuration API\)<a name="saml-enable-api"></a>

The following request to the configuration API enables SAML authentication for OpenSearch Dashboards on an existing domain:

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/domain/my-domain/config
{
  "AdvancedSecurityOptions": {
    "SAMLOptions": {
      "Enabled": true,
      "MasterUserName": "my-idp-user",
      "MasterBackendRole": "my-idp-group-or-role",
      "Idp": {
        "EntityId": "entity-id",
        "MetadataContent": "metadata-content-with-quotes-escaped"
      },
      "RolesKey": "optional-roles-key",
      "SessionTimeoutMinutes": 180,
      "SubjectKey": "optional-subject-key"
    }
  }
}
```

You must escape all quotes and newline characters in the metadata XML\. For example, use `<KeyDescriptor use=\"signing\">\n` instead of `<KeyDescriptor use="signing">` and a line break\. For detailed information about using the configuration API, see the [OpenSearch Service API reference](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_Welcome.html)\.

## SAML troubleshooting<a name="saml-troubleshoot"></a>


| Error | Details | 
| --- | --- | 
| Your request: '*/some/path*' is not allowed\. | Verify that you provided the correct [SSO URL](#saml-enable) \(step 3\) to your identity provider\. | 
|  Please provide valid identity provider metadata document to enable SAML\.  |  Your IdP metadata file does not conform to the SAML 2\.0 standard\. Check for errors using a validation tool\.  | 
|  SAML configuration options aren't visible in the console\.  |  Update to the latest [service software](service-software.md)\.  | 
|  SAML configuration error: Something went wrong while retrieving the SAML configuration, please check your settings\.  |  This generic error can occur for many reasons\. [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/saml.html)  | 
|  Missing role: No roles available for this user, please contact your system administrator\.  |  You successfully authenticated, but the username and any backend roles from the SAML assertion are not mapped to any roles and thus have no permissions\. These mappings are case\-sensitive\. Verify the contents of your SAML assertion using a tool like [SAML\-tracer](https://addons.mozilla.org/en-US/firefox/addon/saml-tracer/) and your role mapping using the following call: <pre>GET _plugins/_security/api/rolesmapping</pre>  | 
|  Your browser continuously redirects or receives HTTP 500 errors when trying to access OpenSearch Dashboards\.  |  These errors can occur if your SAML assertion contains a large number of roles totaling approximately 1,500 characters\. For example, if you pass 80 roles, the average length of which is 20 characters, you might exceed the size limit for cookies in your web browser\. Starting with OpenSearch version 2\.7, SAML assertion supports roles up to 5000 characters\.  | 
|  You can't log out of ADFS\.  |  ADFS requires all logout request to be signed, which OpenSearch Service doesn't support\. Remove `<SingleLogoutService />` from the IdP metadata file to force OpenSearch Service to use its own internal logout mechanism\.  | 
|  `Could not find entity descriptor for __PATH__.`  |  The entity ID of the IdP provided in the metadata XML to OpenSearch Service is different than the one in the SAML response\. To fix this, make sure that they match\. Enable **CW Application Error logs** on your domain to find the error message to debug the SAML integration issue\.  | 
|  `Signature validation failed. SAML response rejected.`  |  OpenSearch Service is unable to verify the signature in the SAML response using the certificate of the IdP provided in metadata XML\. This could either be a manual error, or your IdP has rotated its certificate\. Update the latest certificate from your IdP in the metadata XML provided to OpenSearch Service through the AWS Management Console\.   | 
|  `__PATH__ is not a valid audience for this response.`  |  The audience field in the SAML response doesn't match the domain endpoint\. To fix this error, update the SP audience field to match your domain endpoint\. If you've enabled custom endpoints, the audience field should match your custom endpoint\. Enable **CW Application Error logs** on your domain to find the error message to debug the SAML integration issue\.  | 
|  Your browser receives a HTTP 400 error with `Invalid Request Id` in the response\.  |  This error generally happens if you've configured the IdP\-initiated URL with the format `<Kibana/OSDURL>/_opendistro/_security/saml/acs`\. Instead, configure the URL with the format `<Kibana/OSDURL>/_opendistro/_security/saml/acs/idpinitiated`\.  | 
|  The response was received at `__PATH__` instead of `__PATH__`\.  |  The destination field in SAML response doesn't match one of the following URL formats: [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/saml.html) Depending on the login flow you use \(SP\-initiated or IdP\-initiated\), enter in a destination field that matches one of the OpenSearch URLs\.  | 
|  The response has an `InResponseTo` attribute, while no `InResponseTo` was expected\.  |  You're using the IdP\-initiated URL for an SP\-initiated login flow\. Use the SP\-initiated URL instead\.  | 

## Disabling SAML authentication<a name="saml-disable"></a>

**To disable SAML authentication for OpenSearch Dashboards \(console\)**

1. Choose the domain, **Actions**, and **Edit security configuration**\.

1. Uncheck **Enable SAML authentication**\.

1. Choose **Save changes**\.

1. After the domain finishes processing, verify the fine\-grained access control role mapping with the following request:

   ```
   GET _plugins/_security/api/rolesmapping
   ```

   Disabling SAML authentication for Dashboards does *not* remove the mappings for the SAML master username and/or the SAML master backend role\. If you want to remove these mappings, log in to Dashboards using the internal user database \(if enabled\), or use the API to remove them:

   ```
   PUT _plugins/_security/api/rolesmapping/all_access
   {
     "users": [
       "master-user"
     ]
   }
   ```