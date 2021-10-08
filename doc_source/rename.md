# Amazon OpenSearch Service \- Summary of changes<a name="rename"></a>

On September 8, 2021, Amazon Elasticsearch Service was renamed to Amazon OpenSearch Service\. OpenSearch Service supports OpenSearch as well as legacy Elasticsearch OSS\. The following sections describe the different parts of the service that changed with the service rename, and what actions you need to take to ensure that your domains continue to function properly\. 

Some of these changes only apply when you upgrade your domains from Elasticsearch to OpenSearch\. In other cases, such as in the Billing and Cost Management console, the experience changes immediately\.

Note that this list is not exhaustive\. While other parts of the product also changed, these updates are the most relevant\.

**Topics**
+ [New API version](#rename-sdk)
+ [Renamed instance types](#rename-instances)
+ [Access policy changes](#rename-iam)
+ [New resource types](#rename-resource)
+ [Kibana renamed to OpenSearch Dashboards](#rename-dashboards)
+ [Renamed CloudWatch metrics](#rename-metrics)
+ [Billing and Cost Management console changes](#rename-billing)
+ [New event format](#rename-events)
+ [What's staying the same?](#rename-nochange)
+ [Get started: Upgrade your domains to OpenSearch 1\.0](#rename-upgrade)

## New API version<a name="rename-sdk"></a>

The new version of the OpenSearch Service configuration API \(2021\-01\-01\) works with OpenSearch as well as legacy Elasticsearch OSS\. 21 API operations were replaced with more concise and engine\-agnostic names \(for example, `CreateElasticsearchDomain` changed to `CreateDomain`\), but OpenSearch Service continues to support both API versions\. For a full list of actions that are no longer supported and their replacements, see the [Configuration API reference for Amazon OpenSearch Service](configuration-api.md)\.

We recommend that you use the new API operations to create and manage domains going forward\. Note that when you use the new API operations to create a domain, you need to specify the `EngineVersion` parameter in the format `Elasticsearch_X.Y` or `OpenSearch_X.Y`, rather than just the version number\. If you don't specify a version, it defaults to the latest version of OpenSearch\.

Upgrade your AWS CLI to version 1\.20\.40 or later in order to use `aws opensearch ...` to create and manage your domains\. For the new CLI format, see the [OpenSearch CLI reference](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/opensearch/index.html)\.

## Renamed instance types<a name="rename-instances"></a>

Instance types in Amazon OpenSearch Service are now in the format `<type>.<size>.search`—for example, `m6g.large.search` rather than `m6g.large.elasticsearch`\. You don't need to take any action\. Existing domains will start automatically referring to the new instance types within the API and in the Billing and Cost Management console\. 

If you have Reserved Instances \(RIs\), your contract won't be impacted by the change\. The old configuration API version is still compatible with the old naming format, but if you want to use the new API version, you need to use the new format\.

## Access policy changes<a name="rename-iam"></a>

The following sections describe what actions you need to take to update your access policies\.

### IAM policies<a name="rename-iam-only"></a>

We recommend that you update your [IAM policies](ac.md) to use the renamed API operations\. However, OpenSearch Service will continue to respect existing policies by internally replicating the old API permissions\. For example, if you currently have permission to perform the `CreateElasticsearchDomain` operation, you can now make calls to both `CreateElasticsearchDomain` \(old API operation\) and `CreateDomain` \(new API operation\)\. The same applies to explicit denies\. For a list of updated API operations, see the [policy element reference](ac.md#ac-reference)\.

### SCP policies<a name="rename-iam-only"></a>

[Service control policies \(SCPs\)](https://docs.aws.amazon.com/organizations/latest/userguide/orgs_manage_policies_scps.html) introduce an additional layer of complexity compared to standard IAM\. To prevent your SCP policies from breaking, you need to add both the old *and* the new API operations to each of your SCP policies\. For example, if a user currently has allow permissions for `CreateElasticsearchDomain`, you also need to grant them allow permissions for `CreateDomain` so they can retain the ability to create domains\. The same applies to explicit denies\.

For example:

```
"Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "es:CreateElasticsearchDomain",
        "es:CreateDomain"
         ...
      ],
    },
      "Effect": "Deny",
      "Action:" [
        "es:DeleteElasticsearchDomain",
        "es:DeleteDomain"
         ...
```

## New resource types<a name="rename-resource"></a>

OpenSearch Service introduces the following new resource types:


| Resource | Description | 
| --- | --- | 
|  `AWS::OpenSearchService::Domain`  |  Represents an Amazon OpenSearch Service domain\. This resource exists at the service level and isn't specific to the software running on the domain\. It applies to services like [AWS CloudFormation](https://aws.amazon.com/cloudformation/) and [AWS Resource Groups](https://docs.aws.amazon.com/ARG/latest/userguide/welcome.html), in which you create and manage resources for the service as a whole\.  | 
|  `AWS::OpenSearch::Domain`  | Represents OpenSearch/Elasticsearch software running on a domain\. This resource applies to services like [AWS CloudTrail](http://aws.amazon.com/documentation/cloudtrail/) and [AWS Config](http://aws.amazon.com/config/), which reference the software running on the domain rather than OpenSearch Service as a whole\. These services now contain separate resource types for domains running Elasticsearch \(AWS::Elasticsearch::Domain\) versus domains running OpenSearch \(AWS::OpenSearch::Domain\)\.  | 

Some AWS services haven't yet added support for the new resource types:
+ In [AWS Config](http://aws.amazon.com/config/), you'll continue to see your data under the existing `AWS::Elasticsearch::Domain` resource type for several weeks, even if you upgrade one or more domains to OpenSearch\.
+ In [AWS Security Hub](https://aws.amazon.com/security-hub/), the existing controls for domains running Elasticsearch are not yet supported for OpenSearch domains\. If you upgrade a domain to an OpenSearch version or create new OpenSearch domains, there will be a time period in which your AWS Config rules won’t check for compliance\. For more information, see [How Security Hub uses AWS Config rules to run security checks](https://docs.aws.amazon.com/securityhub/latest/userguide/securityhub-standards-awsconfigrules.html)\.

## Kibana renamed to OpenSearch Dashboards<a name="rename-dashboards"></a>

[OpenSearch Dashboards](dashboards.md), the successor to Kibana, is an open\-source visualization tool designed to work with OpenSearch\. After you upgrade a domain from Elasticsearch to OpenSearch, the `/_plugin/kibana` endpoint changes to `/_dashboards`\. OpenSearch Service will redirect all requests to the new endpoint, but if you use the Kibana endpoint in any of your IAM policies, update those policies to include the new `/_dashboards` endpoint as well\.

If you're using [SAML authentication for OpenSearch Dashboards](saml.md), before you upgrade your domain to OpenSearch, you need to change all Kibana URLs configured in your identity provider \(IdP\) from `/_plugin/kibana` to `/_dashboards`\. The most common URLs are assertion consumer service \(ACS\) URLs and recipient URLs\.

## Renamed CloudWatch metrics<a name="rename-metrics"></a>

Several CloudWatch metrics change for domains running OpenSearch\. When you upgrade a domain to OpenSearch, the metrics change automatically and your current CloudWatch alarms will break\. Before upgrading your cluster from an Elasticsearch version to an OpenSearch version, make sure to update your CloudWatch alarms to use the new metrics\. 

The following metrics changed:


| Original metric name | New name | 
| --- | --- | 
|  `KibanaHealthyNodes`  |  `OpenSearchDashboardsHealthyNodes`  | 
|  `KibanaConcurrentConnections`  |  `OpenSearchDashboardsConcurrentConnections`  | 
|  `KibanaHeapTotal`  |  `OpenSearchDashboardsHeapTotal`  | 
|  `KibanaHeapUsed`  |  `OpenSearchDashboardsHeapUsed`  | 
|  `KibanaHeapUtilization`  |  `OpenSearchDashboardsHeapUtilization`  | 
|  `KibanaOS1MinuteLoad`  |  `OpenSearchDashboardsOS1MinuteLoad`  | 
|  `KibanaRequestTotal`  |  `OpenSearchDashboardsRequestTotal`  | 
|  `KibanaResponseTimesMaxInMillis`  |  `OpenSearchDashboardsResponseTimesMaxInMillis`  | 
|  `ESReportingFailedRequestSysErrCount`  |  `KibanaReportingFailedRequestSysErrCount`  | 
|  `ESReportingRequestCount`  |  `KibanaReportingRequestCount`  | 
|  `ESReportingFailedRequestUserErrCount`  |  `KibanaReportingFailedRequestUserErrCount`  | 
|  `ESReportingSuccessCount`  |  `KibanaReportingSuccessCount`  | 

For a full list of metrics that OpenSearch Service sends to Amazon CloudWatch, see [Monitoring OpenSearch cluster metrics with Amazon CloudWatch](managedomains-cloudwatchmetrics.md)\.

## Billing and Cost Management console changes<a name="rename-billing"></a>

Historic data in the [Billing and Cost Management](https://console.aws.amazon.com/billing/home) console and in [Cost and Usage Reports](https://aws.amazon.com/aws-cost-management/aws-cost-and-usage-reporting/) will continue to use the old service name, so you need to start using filters for both **Amazon Elasticsearch Service** and **Amazon OpenSearch Service** when searching for data\. If you have existing saved reports, update the filters to make sure they also include OpenSearch Service\. You might initially receive an alert when your usage decreases for Elasticsearch and increases for OpenSearch, but it disappears within several days\. 

The following fields will change for all reports, bills, and price list API operations:


| Field | Old format | New format | 
| --- | --- | --- | 
| Instance type |  `m5.large.elasticsearch`  |  `m5.large.search`  | 
|  Product name  |  Amazon Elasticsearch Service  | Amazon OpenSearch Service | 
| Product family |  Elasticsearch Instance Elasticsearch Volume  |  Amazon OpenSearch Service Instance Amazon OpenSearch Service Volume  | 
| Pricing description |  $5\.098 per c5\.18xlarge\.elasticsearch instance hour \(or partial hour\) \- EU  |  $5\.098 per c5\.18xlarge\.search instance hour \(or partial hour\) \- EU  | 
| Service name |  Amazon Elasticsearch Service  |  Amazon OpenSearch Service  | 
|  Instance family  |  `ultrawarm.elasticsearch`  |  `ultrawarm.search`  | 

## New event format<a name="rename-events"></a>

The format of events that OpenSearch Service sends to Amazon EventBridge and Amazon CloudWatch has changed, specifically the `detail-type` field\. The source field \(`aws.es`\) remains the same\. For the complete format for each event type, see [Monitoring OpenSearch Service events with Amazon EventBridge](monitoring-events.md)\. If you have existing event rules that depend on the old format, make sure to update them to conform to the new format\.

## What's staying the same?<a name="rename-nochange"></a>

The following features and functionality, among others not listed, will remain the same:
+ Service principal \(`es.amazon.com`\)
+ Vendor code
+ Domain ARNs
+ Domain endpoints
+ CloudWatch namespace \(`AWS/ES`\)
+ `kibana*` roles in OpenSearch Dashboards

## Get started: Upgrade your domains to OpenSearch 1\.0<a name="rename-upgrade"></a>

OpenSearch 1\.0 supports upgrades from Elasticsearch versions 6\.*x* and 7\.*x*\. For instructions to upgrade your domain, see [Starting an upgrade](version-migration.md#starting-upgrades)\. If you're using the AWS CLI or configuration API to upgrade your domain, you need to specify the `TargetVersion` as `OpenSearch_1.0`\.

OpenSearch 1\.0 introduces an additional domain setting called **Enable compatibility mode**\. Because certain Elasticsearch OSS clients and plugins check the cluster version before connecting, compatibility mode sets OpenSearch to report its version as 7\.10 so these clients continue to work\. 

You can enable compatibility mode when you create OpenSearch domains for the first time, or when you upgrade to OpenSearch from an Elasticsearch version\. If it's not set, the parameter defaults to `false` when you create a domain, and `true` when you upgrade a domain\.

To enable compatibility mode using the [configuration API](configuration-api.md), set `override_main_response_version` to `true`:

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/upgradeDomain
{
  "DomainName": "domain-name",
  "TargetVersion": "OpenSearch_1.0",
  "AdvancedOptions": {
    "override_main_response_version": "true"
   }
}
```

To enable or disable compatibility mode on *existing* OpenSearch domains, you need to use the OpenSearch [\_cluster/settings](https://opensearch.org/docs/opensearch/rest-api/cluster-settings/) API operation:

```
PUT /_cluster/settings
{
  "persistent" : {
    "compatibility.override_main_response_version" : true
  }
}
```