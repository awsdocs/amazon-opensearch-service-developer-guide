# SAML Authentication for Kibana<a name="saml"></a>

SAML authentication for Kibana lets you use your existing identity provider to offer single sign\-on \(SSO\) for Kibana\. Rather than authenticating through [Amazon Cognito](es-cognito-auth.md) or the [internal user database](fgac.md#fgac-kibana), SAML authentication for Kibana lets you use third\-party identity providers to log in to Kibana, manage fine\-grained access control, search your data, and build visualizations\.

SAML authentication for Kibana is only for accessing Kibana through a web browser\. Your SAML credentials do *not* let you make direct HTTP requests to the Elasticsearch or Kibana APIs\.

## Prerequisites<a name="saml-prerequisites"></a>
+ An Amazon ES domain running Elasticsearch 6\.7 or later with [fine\-grained access control](fgac.md) enabled
+ A third\-party identity provider \(IdP\) that supports the SAML 2\.0 standard, such as Okta, Keycloak, Active Directory Federation Services, Auth0, and many more

## SAML Configuration Overview<a name="saml-overview"></a>

This page assumes you have an existing identity provider and some familiarity with it\. We can't provide detailed configuration steps for your exact provider, only for your Amazon ES domain\.

The Kibana login flow can take one of two forms:
+ Service provider \(SP\) initiated: You navigate to Kibana \(for example, `https://my-domain.us-east-1.es.amazonaws.com/_plugin/kibana`\), which redirects you to the login screen\. After you log in, the identity provider redirects you to Kibana\.
+ Identity provider \(IdP\) initiated: You navigate to your identity provider, log in, and choose Kibana from an application directory\.

Amazon ES provides two single sign\-on URLs, SP\-initiated and IdP\-initiated, but you only need the one that matches your desired Kibana login flow\.

In either case, the goal is to log in through your identity provider and receive a SAML assertion that contains your username \(required\) and any [backend roles](fgac.md#fgac-concepts) \(optional, but recommended\)\. This information allows [fine\-grained access control](fgac.md) to assign permissions to SAML users\. In external identity providers, backend roles are typically called "roles" or "groups\."

## Limitations<a name="saml-limitations"></a>
+ You can't change the SSO URL, so SAML authentication for Kibana does not support proxy servers\.
+ Domains only support one Kibana authentication method at a time\. If you have [Amazon Cognito authentication for Kibana](es-cognito-auth.md) enabled, you must disable it before you can enable SAML\.

## Enabling SAML Authentication<a name="saml-enable"></a>

You can only enable SAML authentication for Kibana on existing domains, not during the creation of new ones\. Due to the size of the IdP metadata file, we highly recommend using the AWS console\.

**To enable SAML authentication for Kibana \(console\)**

1. Choose the domain, **Actions**, and **Modify authentication**\.

1. Check **Enable SAML authentication**\.

1. Note the service provider entity ID and the two SSO URLs\. You only need one of the SSO URLs\. For guidance, see [SAML Configuration Overview](#saml-overview)\.

   These URLs change if you later enable a [custom endpoint](es-customendpoint.md) for your domain\. In that situation, you must update your IdP\.

1. Use these values to configure your identity provider\. This is the most complex part of the process, and unfortunately, terminology and steps vary wildly by provider\. Consult your provider's documentation and see the following sections:
   + [Tips for Okta](#saml-okta)
   + [Tips for Keycloak](#saml-keycloak)
   + [Tips for Auth0](#saml-auth0)

1. After you configure your identity provider, it generates an IdP metadata file\. This XML file contains information on the provider, such as a TLS certificate, single sign\-on endpoints, and the identity provider's entity ID\.

   Copy and paste the contents of the IdP metadata file into the **Metadata from IDP** field in the AWS console\. Alternately, upload the metadata file using the **Import from XML file** button\. The metadata file should look something like this:

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

1. Copy and paste the contents of the `entityID` property from your metadata file into the **IDP entity ID** field in the AWS console\. Many identity providers also display this value as part of a post\-configuration summary\. Some providers call it the "issuer\."

1. Provide a **SAML master username** and/or a **SAML master backend role**\. This username and/or backend role receives full permissions to the cluster, equivalent to a [new master user](fgac.md#fgac-more-masters), but can only use those permissions within Kibana\.

   In Okta, for example, you might have a user `jdoe` who belongs to the group `admins`\. If you add `jdoe` to the **SAML master username** field, only that user receives full permissions\. If you add `admins` to the **SAML master backend role** field, any user who belongs to the `admins` group receives full permissions\.

   The contents of the SAML assertion must exactly match the strings that you use for the SAML master username and/or SAML master role\. Some identity providers add a prefix before their usernames, which can cause a hard\-to\-diagnose mismatch\. In the identity provider user interface, you might see `jdoe`, but the SAML assertion might contain `auth0|jdoe`\. Always use the string from the SAML assertion\.

   Many identity providers let you view a sample assertion during the configuration process, and tools like [SAML\-tracer](https://addons.mozilla.org/en-US/firefox/addon/saml-tracer/) can help you examine and troubleshoot the contents of real assertions\. Assertions look something like this:

   ```
   <?xml version="1.0" encoding="UTF-8"?>
   <saml2:Assertion ID="id67229299299259351343340162" IssueInstant="2020-09-22T22:03:08.633Z" Version="2.0"
     xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion">
     <saml2:Issuer Format="urn:oasis:names:tc:SAML:2.0:nameid-format:entity">idp-issuer</saml2:Issuer>
     <saml2:Subject>
       <saml2:NameID Format="urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified">username</saml2:NameID>
       <saml2:SubjectConfirmation Method="urn:oasis:names:tc:SAML:2.0:cm:bearer">
         <saml2:SubjectConfirmationData NotOnOrAfter="2020-09-22T22:08:08.816Z" Recipient="domain-endpoint/_plugin/kibana/_opendistro/_security/saml/acs"/>
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

1. Expand the **Optional SAML settings** section\.

1. Leave the **Subject key** field empty to use the `NameID` element of the SAML assertion for the username\. If your assertion doesn't use this standard element and instead includes the username as a custom attribute, specify that attribute here\.

   If you want to use backend roles \(recommended\), specify an attribute from the assertion in the **Role key** field, such as `role` or `group`\. This is another situation in which tools like [SAML\-tracer](https://addons.mozilla.org/en-US/firefox/addon/saml-tracer/) can help\.

1. By default, Kibana logs users out after 60 minutes\. You can increase this value up to 1,440 \(24 hours\) using the **Session time to live** field\.

1. Choose **Submit**\. The domain enters a processing state for approximately one minute, during which time Kibana is unavailable\.

1. After the domain finishes processing, open Kibana\.
   + If you chose the SP\-initiated URL, navigate to `domain-endpoint/_plugin/kibana/`\.
   + If you chose the IdP\-initiated URL, navigate to your identity provider's application directory\.

   In both cases, log in as either the SAML master user or a user who belongs to the SAML master backend role\. To continue the example from step 7, log in as either `jdoe` or a member of the `admins` group\.

1. After Kibana loads, choose **Security** and **Roles**\.

1. [Map roles](fgac.md#fgac-mapping) to allow other users to access Kibana\.

   For example, you might map your trusted colleague `jroe` to the `all_access` and `security_manager` roles\. You might also map the backend role `analysts` to the `readall` and `kibana_user` roles\.

   If you prefer to use the API rather than Kibana, see the following sample request:

   ```
   PATCH _opendistro/_security/api/rolesmapping
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
       "op": "add", "path": "/kibanauser", "value": { "backend_roles": ["analysts"] }
     }
   ]
   ```

### Sample CLI Command<a name="saml-enable-cli"></a>

The following AWS CLI command enables SAML authentication for Kibana on an existing domain:

```
aws es update-elasticsearch-domain-config \
  --domain-name my-domain \
  --advanced-security-options '{"SAMLOptions":{"Enabled":true,"MasterUserName":"my-idp-user","MasterBackendRole":"my-idp-group-or-role","Idp":{"EntityId":"entity-id","MetadataContent":"metadata-content-with-quotes-escaped"},"RolesKey":"optional-roles-key","SessionTimeoutMinutes":180,"SubjectKey":"optional-subject-key"}}'
```

You must escape all quotes and newline characters in the metadata XML\. For example, use `<KeyDescriptor use=\"signing\">\n` instead of `<KeyDescriptor use="signing">` and a line break\. For detailed information about using the AWS CLI, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/)\.

### Sample Configuration API Request<a name="saml-enable-api"></a>

The following request to the configuration API enables SAML authentication for Kibana on an existing domain:

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/es/domain/my-domain/config
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

You must escape all quotes and newline characters in the metadata XML\. For example, use `<KeyDescriptor use=\"signing\">\n` instead of `<KeyDescriptor use="signing">` and a line break\. For detailed information about using the configuration API, see [Amazon Elasticsearch Service Configuration API Reference](es-configuration-api.md)\.

## SAML Troubleshooting<a name="saml-troubleshoot"></a>


****  

| Error | Details | 
| --- | --- | 
|  Your request: '*/some/path*' is not allowed\.  |  Verify that you provided the correct [SSO URL](#saml-enable) \(step 3\) to your identity provider\.  | 
|  Please provide valid identity provider metadata document to enable SAML\.  |  Your IdP metadata file does not conform to the SAML 2\.0 standard\. Check for errors using a validation tool\.  | 
|  SAML configuration options aren't visible in the console\.  |  Update to the latest [service software](es-service-software.md)\.  | 
|  SAML configuration error: Something went wrong while retrieving the SAML configuration, please check your settings\.  |  This generic error can occur for many reasons\. [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/saml.html)  | 
|  Missing role: No roles available for this user, please contact your system administrator\.  |  You successfully authenticated, but the username and any backend roles from the SAML assertion are not mapped to any roles and thus have no permissions\. These mappings are case\-sensitive\. Verify the contents of your SAML assertion using a tool like [SAML\-tracer](https://addons.mozilla.org/en-US/firefox/addon/saml-tracer/) and your role mapping using the following call: <pre>GET _opendistro/_security/api/rolesmapping</pre>  | 
|  Your browser continuously redirects or receives HTTP 500 errors when trying to access Kibana\.  |  These errors can occur if your SAML assertion contains a large number of roles totaling approximately 1,500 characters\. For example, if you pass 80 roles, the average length of which is 20 characters, you might exceed the size limit for cookies in your web browser\.  | 

## Disabling SAML Authentication<a name="saml-disable"></a>

**To disable SAML authentication for Kibana \(console\)**

1. Choose the domain, **Actions**, and **Modify authentication**\.

1. Uncheck **Enable SAML authentication**\.

1. Choose **Submit**\.

1. After the domain finishes processing, verify the fine\-grained access control role mapping using the following call:

   ```
   GET _opendistro/_security/api/rolesmapping
   ```

   Disabling SAML authentication for Kibana does *not* remove the mappings for the SAML master username and/or SAML master backend role\. If you want to remove these mappings, log in to Kibana using the internal user database \(if enabled\), or use the API to remove them:

   ```
   PUT _opendistro/_security/api/rolesmapping/all_access
   {
     "users": [
       "master-user"
     ]
   }
   ```

## Tips for Okta<a name="saml-okta"></a>

In Okta, create a "SAML 2\.0 web application\." For **Single sign on URL**, specify the SSO URL that you chose in step 3 of [Enabling SAML Authentication](#saml-enable)\. For **Audience URI \(SP Entity ID\)**, specify the SP entity ID\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/okta1.png)

Rather than users and backend roles, Okta has users and groups\. For **Group Attribute Statements**, we recommend adding `role` to the **Name** field and the regular expression `.+` to the **Filter** field\. This statement tells the Okta identity provider to include all user groups under the `role` field of the SAML assertion after a user authenticates\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/okta2.png)

## Tips for Keycloak<a name="saml-keycloak"></a>

Depending on your IdP configuration, you might want to create a new *realm* in Keycloak for your Kibana users\. Whether you use a new or existing realm, choose **SAML 2\.0 Identify Provider Metadata** under the **Endpoints** section of your realm settings to get your IdP metadata file, which you need in step 5 of [Enabling SAML Authentication](#saml-enable)\.

The realm must have a *client* for Kibana\. Use the `saml` protocol and `RSA_SHA512` for the signature algorithm\. Provide the SP\-initiated SSO URL for **Valid Redirect URIs**, **Master SAML Processing URL**, and **Logout Service Redirect Binding URL**\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/keycloak3.png)

Keycloak offers several ways to map users to roles, but for testing, you might find it convenient to create two new client roles and two new users\. Then map those new roles directly to the new users\. For your roles, choose Basic for **SAML Attribute NameFormat**

## Tips for Auth0<a name="saml-auth0"></a>

In Auth0, you create a "regular web application" and then enable the SAML 2\.0 add\-on\. 