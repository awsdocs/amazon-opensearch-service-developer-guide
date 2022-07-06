# Configuration API reference for Amazon OpenSearch Service<a name="configuration-api"></a>

This reference describes the actions, data types, and errors in the Amazon OpenSearch Service configuration API\. The configuration API is a REST API that you can use to create and configure OpenSearch Service domains over HTTP\. You also can use the AWS CLI and the console to configure OpenSearch Service domains\. For more information, see [Creating and managing Amazon OpenSearch Service domains](createupdatedomains.md)\.
+ [Authentication](#configuration-api-authentication)
+ [New API version and deprecated actions](#configuration-api-deprecated)
+ [Actions](#configuration-api-actions)
+ [Data types](#configuration-api-datatypes)
+ [Errors](#configuration-api-errors)

## Authentication<a name="configuration-api-authentication"></a>

All configuration service requests must be signed\. For more information, see [Signing Amazon OpenSearch Service requests](ac.md#managedomains-signing-service-requests) in this guide and [Signature Version 4 signing process](http://docs.aws.amazon.com/general/latest/gr/signature-version-4.html) in the *AWS General Reference*\.

## New API version and deprecated actions<a name="configuration-api-deprecated"></a>

**Important**  
The following actions were deprecated in version 2021\-01\-01 of the Amazon OpenSearch Service API and replaced by more concise and engine\-agnostic endpoints\. However, the AWS CLI and configuration API continue to accept them\.


****  

| Deprecated action | Replacement | 
| --- | --- | 
| AcceptInboundCrossClusterSearchConnection | AcceptInboundConnection | 
| CreateElasticsearchDomain | CreateDomain | 
| CreateOutboundCrossClusterSearchConnection | CreateOutboundConnection | 
| CreateElasticsearchServiceRole | No replacement\. Use the [IAM API](https://docs.aws.amazon.com/IAM/latest/APIReference/API_CreateServiceLinkedRole.html) to create service\-linked roles\.  | 
| DeleteElasticsearchDomain | DeleteDomain | 
| DeleteElasticsearchServiceRole | No replacement\. Use the [IAM API](https://docs.aws.amazon.com/IAM/latest/APIReference/API_DeleteServiceLinkedRole.html) to delete service\-linked roles\.  | 
| DeleteInboundCrossClusterSearchConnection | DeleteInboundConnection | 
| DescribeElasticsearchDomain | DescribeDomain | 
| DescribeElasticsearchDomainConfig | DescribeDomainConfig | 
| DescribeElasticsearchInstanceTypeLimits | DescribeInstanceTypeLimits | 
| DescribeInboundCrossClusterSearchConnections | DescribeInboundConnections | 
| DescribeOutboundCrossClusterSearchConnections | DescribeOutboundConnections | 
| DescribeReservedElasticsearchInstanceOfferings | DescribeReservedInstanceOfferings | 
| DescribeReservedElasticsearchInstances | DescribeReservedInstances | 
| GetCompatibleElasticsearchVersions | GetCompatibleVersions | 
| ListElasticsearchInstanceTypeDetails | ListInstanceTypeDetails | 
| ListElasticsearchVersions | ListVersions | 
| PurchaseReservedElasticsearchInstanceOffering | PurchaseReservedInstanceOffering | 
| RejectInboundCrossClusterSearchConnection | RejectInboundConnection | 
| StartElasticsearchServiceSoftwareUpdate | StartServiceSoftwareUpdate | 
| StopElasticsearchServiceSoftwareUpdate | StopServiceSoftwareUpdate | 
| UpdateElasticsearchDomainConfig | UpdateDomainConfig | 
| UpgradeElasticsearchDomain | UpgradeDomain | 

## Actions<a name="configuration-api-actions"></a>

The following table provides a quick reference to the HTTP method required for each operation for the REST interface to the Amazon OpenSearch Service configuration API\. The description of each operation also includes the required HTTP method\.


| Action | HTTP method | 
| --- | --- | 
| [AcceptInboundConnection](#configuration-api-actions-accept-inbound-cross-cluster-search-connection) | PUT | 
| [AddTags](#configuration-api-actions-addtags) | POST | 
| [AssociatePackage](#configuration-api-actions-associatepackage) | POST | 
| [CancelServiceSoftwareUpdate](#configuration-api-actions-stopupdate) | POST | 
| [CreateDomain](#configuration-api-actions-createdomain) | POST | 
| [CreateOutboundConnection](#configuration-api-actions-create-outbound-cross-cluster-search-connection) | POST | 
| [CreatePackage](#configuration-api-actions-createpackage) | POST | 
| [DeleteDomain](#configuration-api-actions-deletedomain) | DELETE | 
| [DeleteInboundConnection](#configuration-api-actions-delete-inbound-cross-cluster-search-connection) | DELETE | 
| [DeleteOutboundConnection](#configuration-api-actions-delete-outbound-cross-cluster-search-connection) | DELETE | 
| [DeletePackage](#configuration-api-actions-deletepackage) | DELETE | 
| [DescribeDomainAutoTunes](#configuration-api-actions-describeautotune) | GET | 
| [DescribeDomain](#configuration-api-actions-describedomain) | GET | 
| [DescribeDomainChangeProgress](#configuration-api-actions-describedomainchangeprogress) | GET | 
| [DescribeDomainConfig](#configuration-api-actions-describedomainconfig) | GET | 
| [DescribeDomains](#configuration-api-actions-describedomains) | POST | 
| [DescribeInstanceTypeLimits](#configuration-api-actions-describeinstancetypelimits) | GET | 
| [DescribeInboundConnections](#configuration-api-actions-describe-inbound-cross-cluster-search-connections) | POST | 
| [DescribeOutboundConnections](#configuration-api-actions-describe-outbound-cross-cluster-search-connections) | POST | 
| [DescribePackages](#configuration-api-actions-describepackages) | POST | 
| [DescribeReservedInstanceOfferings](#configuration-api-actions-describereservedinstanceofferings) | GET | 
| [DescribeReservedInstances](#configuration-api-actions-describereservedinstances) | GET | 
| [DissociatePackage](#configuration-api-actions-dissociatepackage) | POST | 
| [GetCompatibleVersions](#configuration-api-actions-get-compat-vers) | GET | 
| [GetPackageVersionHistory](#configuration-api-actions-get-pac-ver-hist) | GET | 
| [GetUpgradeHistory](#configuration-api-actions-get-upgrade-hist) | GET | 
| [GetUpgradeStatus](#configuration-api-actions-get-upgrade-stat) | GET | 
| [ListDomainNames](#configuration-api-actions-listdomainnames) | GET | 
| [ListDomainsForPackage](#configuration-api-actions-listdomainsforpackage) | GET | 
| [ListVersions](#configuration-api-actions-listversions) | GET | 
| [ListInstanceTypeDetails](#configuration-api-actions-listinstancetypedetails) | GET | 
| [ListPackagesForDomain](#configuration-api-actions-listpackagesfordomain) | GET | 
| [ListTags](#configuration-api-actions-listtags) | GET | 
| [PurchaseReservedInstanceOffering](#configuration-api-actions-purchasereservedinstance) | POST | 
| [RejectInboundConnection](#configuration-api-actions-reject-inbound-cross-cluster-search-connection) | PUT | 
| [RemoveTags](#configuration-api-actions-removetags) | POST | 
| [StartServiceSoftwareUpdate](#configuration-api-actions-startupdate) | POST | 
| [UpdateDomainConfig](#configuration-api-actions-updatedomainconfig) | POST | 
| [UpdatePackage](#configuration-api-actions-updatepackage) | POST | 
| [UpgradeDomain](#configuration-api-actions-upgrade-domain) | POST | 

### AcceptInboundConnection<a name="configuration-api-actions-accept-inbound-cross-cluster-search-connection"></a>

Allows the destination domain owner to accept an inbound cross\-cluster search connection request\.

#### Syntax<a name="w54aac39c11b7b5"></a>

```
PUT https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/cc/inboundConnection/connection-id/accept
```

#### Request parameters<a name="w54aac39c11b7b7"></a>

This operation does not use HTTP request parameters\.

#### Request body<a name="w54aac39c11b7b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11b7c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| CrossClusterSearchConnection | Object | Inbound connection details\. | 

### AddTags<a name="configuration-api-actions-addtags"></a>

Attaches resource tags to an OpenSearch Service domain\. For more information, see [Tagging Amazon OpenSearch Service domains](managedomains-awsresourcetagging.md)\.

#### Syntax<a name="configuration-api-actions-addtags-s"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/tags
{
  "ARN": "domain-arn",
  "TagList": [{
    "Key": "tag-key",
    "Value": "tag-value"
  }]
}
```

#### Request parameters<a name="configuration-api-actions-addtags-p"></a>

This operation does not use request parameters\.

#### Request body<a name="configuration-api-actions-addtags-b"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| TagList | [TagList](#configuration-api-datatypes-taglist) | Yes | List of resource tags\. | 
| ARN | [ARN](#configuration-api-datatypes-arn) | Yes | Amazon Resource Name \(ARN\) for the OpenSearch Service domain to which you want to attach resource tags\. | 

#### Response elements<a name="configuration-api-actions-addtags-r"></a>

The `AddTags` operation does not return a data structure\.

### AssociatePackage<a name="configuration-api-actions-associatepackage"></a>

Associates a package with an OpenSearch Service domain\.

#### Syntax<a name="configuration-api-actions-associatepackage-s"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/packages/associate/package-id/domain-name
```

#### Request parameters<a name="configuration-api-actions-associatepackage-p"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| PackageID | String | Yes | Internal ID of the package that you want to associate with a domain\. Use [DescribePackages](#configuration-api-actions-describepackages) to find this value\. | 
| DomainName | [DomainName](#configuration-api-datatypes-domainname) | Yes | Name of the domain that you want to associate the package with\. | 

#### Request body<a name="configuration-api-actions-associatepackage-b"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="configuration-api-actions-associatepackage-r"></a>


****  

| Field | Data type | 
| --- | --- | 
| DomainPackageDetails | [DomainPackageDetails](#configuration-api-datatypes-domainpackagedetails) | 

### CreateDomain<a name="configuration-api-actions-createdomain"></a>

Creates an OpenSearch Service domain\. For more information, see [ Creating OpenSearch Service domains](createupdatedomains.md#createdomains)\.

**Note**  
If you attempt to create an OpenSearch Service domain and a domain with the same name already exists, the API does not report an error\. Instead, it returns details for the existing domain\.

#### Syntax<a name="configuration-api-actions-createdomain-s"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/domain
{
  "ClusterConfig": {
    "ZoneAwarenessConfig": {
      "AvailabilityZoneCount": 3
    },
    "ZoneAwarenessEnabled": true|false,
    "InstanceCount": 3,
    "DedicatedMasterEnabled": true|false,
    "DedicatedMasterType": "c5.large.search",
    "DedicatedMasterCount": 3,
    "InstanceType": "r5.large.search",
    "WarmCount": 3,
    "WarmEnabled": true|false,
    "WarmType": "ultrawarm1.large.search",
    "ColdStorageOptions": {
       "Enabled": true|false
    },
  "EBSOptions": {
    "EBSEnabled": true|false,
    "VolumeType": "io1|gp2|standard",
    "Iops": 1000,
    "VolumeSize": 35
  },
  "EncryptionAtRestOptions": {
    "Enabled": true|false,
    "KmsKeyId":"arn:aws:kms:us-east-1:123456789012:alias/my-key"
  },
  "VPCOptions": {
    "VPCId": "vpc-12345678",
    "SubnetIds": ["subnet-abcdefg1", "subnet-abcdefg2", "subnet-abcdefg3"],
    "SecurityGroupIds": ["sg-12345678"]
  },
  "AdvancedOptions": {
    "rest.action.multi.allow_explicit_index": "true|false",
    "indices.fielddata.cache.size": "40",
    "indices.query.bool.max_clause_count": "1024",
    "override_main_response_version": "true|false"
  },
  "CognitoOptions": {
    "Enabled": true|false,
    "UserPoolId": "us-east-1_121234567",
    "IdentityPoolId": "us-east-1:12345678-1234-1234-1234-123456789012",
    "RoleArn": "arn:aws:iam::123456789012:role/service-role/CognitoAccessForAmazonOpenSearch"
  },
  "NodeToNodeEncryptionOptions": {
    "Enabled": true|false
  },
  "DomainEndpointOptions": {
    "EnforceHTTPS": true|false,
    "TLSSecurityPolicy": "Policy-Min-TLS-1-2-2019-07|Policy-Min-TLS-1-0-2019-07",
    "CustomEndpointEnabled": "true|false",
    "CustomEndpoint": "www.my-custom-endpoint.com",
    "CustomEndpointCertificateArn": "arn:aws:iam::123456789012:certificate/my-certificate"
  },
  "LogPublishingOptions": {
    "SEARCH_SLOW_LOGS": {
      "CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:264071961897:log-group1:sample-domain",
      "Enabled":true|false
    },
    "INDEX_SLOW_LOGS": {
      "CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:264071961897:log-group2:sample-domain",
      "Enabled":true|false
    },
    "ES_APPLICATION_LOGS": {
      "CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:264071961897:log-group3:sample-domain",
      "Enabled":true|false
    }
  },
  "AdvancedSecurityOptions": {
    "Enabled": true|false,
    "InternalUserDatabaseEnabled": true|false,
    "MasterUserOptions": {
      "MasterUserARN": "arn:aws:iam::123456789012:role/my-master-user-role"
      "MasterUserName": "my-master-username",
      "MasterUserPassword": "my-master-password"
    }
  },
  "AutoTuneOptions": {
    "DesiredState": "ENABLED|DISABLED",
    "MaintenanceSchedules": [{
      "StartAt": 1234567890,
      "Duration": {
        "Value": 2,
        "Unit": "HOURS"
      },
      "CronExpressionForRecurrence": "cron(0 0 ? * 3 *)"
    }]
  },
  "TagList": [
    {
      "Key": "stack",
      "Value": "prod"
    }
  ],
  "EngineVersion": "OpenSearch_1.0",
  "DomainName": "my-domain",
  "AccessPolicies": "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"123456789012\"]},\"Action\":[\"esListDomainNames:ESHttp*\"],\"Resource\":\"arn:aws:es:us-east-1:123456789012:domain/my-domain/*\"}]}"
}
```

#### Request parameters<a name="configuration-api-actions-createdomain-p"></a>

This operation does not use HTTP request parameters\.

#### Request body<a name="configuration-api-actions-createdomain-b"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [DomainName](#configuration-api-datatypes-domainname) | Yes | Name of the OpenSearch Service domain to create\. | 
| EngineVersion | String | No | Version of OpenSearch or Elasticsearch, in the format Elasticsearch\_X\.Y or OpenSearch\_X\.Y\. Defaults to the latest version of OpenSearch\. For the full list of supported versions, see [Supported versions of OpenSearch and Elasticsearch](what-is.md#choosing-version)\. | 
| ClusterConfig | [ClusterConfig](#configuration-api-datatypes-clusterconfig) | No | Container for the cluster configuration of an OpenSearch Service domain\. | 
| EBSOptions | [EBSOptions](#configuration-api-datatypes-ebsoptions) | No | Container for the parameters required to enable EBS\-based storage for an OpenSearch Service domain\. | 
| VPCOptions | [VPCOptions](#configuration-api-datatypes-vpcoptions) | No | Container for the values required to configure VPC access domains\. If you don't specify these values, OpenSearch Service creates the domain with a public endpoint\. To learn more, see [Launching your Amazon OpenSearch Service domains within a VPC](vpc.md)\. | 
| CognitoOptions | [CognitoOptions](#configuration-api-datatypes-cognitooptions) | No | Key\-value pairs to configure OpenSearch Service to use Amazon Cognito authentication for OpenSearch Dashboards\. | 
| AccessPolicies | String | No | IAM policy document specifying the access policies for the new OpenSearch Service domain\. For more information, see [Identity and Access Management in Amazon OpenSearch Service](ac.md)\. | 
| SnapshotOptions | [SnapshotOptions](#configuration-api-datatypes-snapshotoptions) | No |  **DEPRECATED**\. Container for parameters required to configure automated snapshots of domain indices\.  | 
| AdvancedOptions | [AdvancedOptions](#configuration-api-datatypes-advancedoptions) | No | Key\-value pairs to specify advanced configuration options\. For more information, see [Advanced cluster settings](createupdatedomains.md#createdomain-configure-advanced-options)\. | 
| LogPublishingOptions | [LogPublishingOptions](#configuration-api-datatypes-logpublishingoptions) | No | Key\-value pairs to configure slow log publishing\. | 
| EncryptionAtRestOptions | [EncryptionAtRestOptions](#configuration-api-datatypes-encryptionatrest) | No | Key\-value pairs to enable encryption at rest\. | 
| NodeToNodeEncryptionOptions | [NodeToNodeEncryptionOptions](#configuration-api-datatypes-node-to-node) | No | Enables node\-to\-node encryption\. | 
| DomainEndpointOptions | [DomainEndpointOptions](#configuration-api-datatypes-domainendpointoptions) | No | Additional options for the domain endpoint, such as whether to require HTTPS for all traffic\. | 
| AdvancedSecurityOptions | [AdvancedSecurityOptions](#configuration-api-datatypes-advancedsec) | No | Options for fine\-grained access control\. | 
| AutoTuneOptions | [AutoTuneOptions](#configuration-api-datatypes-autotune) | No | Options for Auto\-Tune\. | 
| TagList | [TagList](#configuration-api-datatypes-taglist) | No | List of tags you want to add to the domain on creation\. | 

#### Response elements<a name="configuration-api-actions-createdomain-r"></a>


****  

| Field | Data type | 
| --- | --- | 
| DomainStatus | [DomainStatus](#configuration-api-datatypes-domainstatus) | 

### CreateOutboundConnection<a name="configuration-api-actions-create-outbound-cross-cluster-search-connection"></a>

Creates a new cross\-cluster search connection from a source domain to a destination domain\.

#### Syntax<a name="w54aac39c11c15b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/cc/outboundConnection
{
   "ConnectionAlias":"connection-name",
   "LocalDomainInfo":{
      "AWSDomainInformation":{
         "DomainName":"domain-name",
         "Region":"us-east-1"
      }
   },
   "RemoteDomainInfo":{
      "AWSDomainInformation":{
         "OwnerId":"account-id",
         "DomainName":"domain-name",
         "Region":"us-east-1"
      }
   }
}
```

#### Request parameters<a name="w54aac39c11c15b7"></a>

This operation does not use HTTP request parameters\.

#### Request body<a name="w54aac39c11c15b9"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| ConnectionAlias | String | Yes | Name of the connection\. | 
| LocalDomainInfo | Object | Yes | Name and Region of the source domain\. | 
| RemoteDomainInfo | Object | Yes | Name and Region of the destination domain\. | 

#### Response elements<a name="w54aac39c11c15c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| SourceDomainInfo | Object | Name and Region of the source domain\. | 
| DestinationDomainInfo | Object | Name and Region of the destination domain\. | 
| ConnectionAlias | String | Name of the connection\. | 
| ConnectionStatus | String | The status of the connection\. | 
| ConnectionId | String | The ID for the outbound connection\. | 

### CreatePackage<a name="configuration-api-actions-createpackage"></a>

Add a package for use with OpenSearch Service domains\.

#### Syntax<a name="configuration-api-actions-createpackage-s"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/packages
{
  "PackageName": "my-package-name",
  "PackageType": "TXT-DICTIONARY",
  "PackageDescription": "My synonym file.",
  "PackageSource": {
    "S3BucketName": "my-s3-bucket",
    "S3Key": "synonyms.txt"
  }
}
```

#### Request parameters<a name="configuration-api-actions-createpackage-p"></a>

This operation does not use request parameters\.

#### Request body<a name="configuration-api-actions-createpackage-b"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| PackageName | String | Yes | Unique name for the package\. | 
| PackageType | String | Yes | Type of package\. Currently supports only TXT\-DICTIONARY\. | 
| PackageDescription | String | No | Description of the package\. | 
| PackageSource | [PackageSource](#configuration-api-datatypes-packagesource) | Yes | S3 bucket and key for the package\. | 

#### Response elements<a name="configuration-api-actions-createpackage-r"></a>


****  

| Field | Data type | 
| --- | --- | 
| PackageDetails | [PackageDetails](#configuration-api-datatypes-packagedetails) | 

### CreateElasticsearchServiceRole \(Deprecated\)<a name="configuration-api-actions-createservicerole"></a>

Creates the service\-linked role between OpenSearch Service and Amazon EC2\. This action is deprecated\. OpenSearch Service handles the creation and deletion of service\-linked roles automatically\.

This role gives OpenSearch Service permissions to place VPC endpoints into your VPC\. A service\-linked role must be in place for domains with VPC endpoints to be created or function properly\.

#### Syntax<a name="configuration-api-actions-deleteservicerole-s"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/role
```

#### Request parameters<a name="configuration-api-actions-deleteservicerole-p"></a>

This operation does not use request parameters\.

#### Request body<a name="configuration-api-actions-deleteservicerole-b"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="configuration-api-actions-deleteservicerole-r"></a>

The `CreateServiceRole` operation does not return a data structure\.

### DeleteDomain<a name="configuration-api-actions-deletedomain"></a>

Deletes an OpenSearch Service domain and all of its data\. You can't recover a domain after it's deleted\.

#### Syntax<a name="configuration-api-actions-deletedomain-s"></a>

```
DELETE https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/domain/domain-name
```

#### Request parameters<a name="configuration-api-actions-deletedomain-p"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [DomainName](#configuration-api-datatypes-domainname) | Yes | Name of the OpenSearch Service domain that you want to delete\. | 

#### Request body<a name="configuration-api-actions-deletedomain-b"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="configuration-api-actions-deletedomain-r"></a>


****  

| Field | Data type | 
| --- | --- | 
| DomainStatus | [DomainStatus](#configuration-api-datatypes-domainstatus) | 

### DeleteElasticsearchServiceRole \(Deprecated\)<a name="configuration-api-actions-deleteservicerole"></a>

Deletes the service\-linked role between OpenSearch Service and Amazon EC2\. This action is deprecated\. OpenSearch Service handles the creation and deletion of roles automatically\.

This role gives OpenSearch Service permissions to place VPC endpoints into your VPC\. A service\-linked role must be in place for domains with VPC endpoints to be created or function properly\.

**Note**  
This action succeeds only if no domains are using the service\-linked role\.

#### Syntax<a name="configuration-api-actions-deleteservicerole-s"></a>

```
DELETE https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/role
```

#### Request parameters<a name="configuration-api-actions-deleteservicerole-p"></a>

This operation does not use request parameters\.

#### Request body<a name="configuration-api-actions-deleteservicerole-b"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="configuration-api-actions-deleteservicerole-r"></a>

The `DeleteElasticsearchServiceRole` operation does not return a data structure\.

### DeleteInboundConnection<a name="configuration-api-actions-delete-inbound-cross-cluster-search-connection"></a>

Allows the destination domain owner to delete an existing inbound cross\-cluster search connection\.

#### Syntax<a name="w54aac39c11c25b5"></a>

```
DELETE https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/cc/inboundConnection/connection-id
```

#### Request parameters<a name="w54aac39c11c25b7"></a>

This operation does not use HTTP request parameters\.

#### Request body<a name="w54aac39c11c25b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11c25c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| CrossClusterSearchConnection | Object | Inbound connection details\. | 

### DeleteOutboundConnection<a name="configuration-api-actions-delete-outbound-cross-cluster-search-connection"></a>

Allows the source domain owner to delete an existing outbound cross\-cluster search connection\.

#### Syntax<a name="w54aac39c11c27b5"></a>

```
DELETE https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/cc/outboundConnection/connection-id
```

#### Request parameters<a name="w54aac39c11c27b7"></a>

This operation does not use HTTP request parameters\.

#### Request body<a name="w54aac39c11c27b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11c27c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| CrossClusterSearchConnection | Object | Outbound connection details\. | 

### DeletePackage<a name="configuration-api-actions-deletepackage"></a>

Deletes a package from OpenSearch Service\. The package can't be associated with any OpenSearch Service domain\. 

#### Syntax<a name="configuration-api-actions-deletepackage-s"></a>

```
DELETE https://es.us-east-1.amazonaws.com/2021-01-01/packages/package-id
```

#### Request parameters<a name="configuration-api-actions-deletepackage-p"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| PackageID | String | Yes | Internal ID of the package that you want to delete\. Use [DescribePackages](#configuration-api-actions-describepackages) to find this value\. | 

#### Request body<a name="configuration-api-actions-deletepackage-b"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="configuration-api-actions-deletepackage-r"></a>


****  

| Field | Data type | 
| --- | --- | 
| PackageDetails | [PackageDetails](#configuration-api-datatypes-packagedetails) | 

### DescribeDomainAutoTunes<a name="configuration-api-actions-describeautotune"></a>

Returns the list of optimizations that Auto\-Tune has made to the domain\.

#### Syntax<a name="w54aac39c11c31b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/domain/domain-name/autoTunes
```

#### Request parameters<a name="w54aac39c11c31b7"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [DomainName](#configuration-api-datatypes-domainname) | Yes | Name of the OpenSearch Service domain that you want Auto\-Tune details about\. | 

#### Request body<a name="w54aac39c11c31b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11c31c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| AutoTunes | List | List of optimizations\. | 
| NextToken | String | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

### DescribeDomain<a name="configuration-api-actions-describedomain"></a>

Describes the domain configuration for the specified OpenSearch Service domain, including the domain ID, domain service endpoint, and domain ARN\.

#### Syntax<a name="w54aac39c11c33b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/domain/domain-name
```

#### Request parameters<a name="w54aac39c11c33b7"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [DomainName](#configuration-api-datatypes-domainname) | Yes | Name of the OpenSearch Service domain that you want to describe\. | 

#### Request body<a name="w54aac39c11c33b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11c33c11"></a>


****  

| Field | Data type | 
| --- | --- | 
| DomainStatus | [DomainStatus](#configuration-api-datatypes-domainstatus) | 

### DescribeDomainChangeProgress<a name="configuration-api-actions-describedomainchangeprogress"></a>

Displays status information for a domain [configuration change](managedomains-configuration-changes.md#managedomains-config-stages)\.

#### Syntax<a name="w54aac39c11c35b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/domain/domain-name/progress
```

#### Request parameters<a name="w54aac39c11c35b7"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [DomainName](#configuration-api-datatypes-domainname) | Yes | Name of the OpenSearch Service domain\. | 

#### Request body<a name="w54aac39c11c35b9"></a>


****  

| Field | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| ChangeId | String | No | The ID of the configuration change\. Retrieved from an [UpdateDomainConfig](#configuration-api-actions-updatedomainconfig) request\. If not included, OpenSearch Service returns details for the most recent configuration change\. | 

#### Response elements<a name="w54aac39c11c35c11"></a>


****  

| Field | Data type | 
| --- | --- | 
| ChangeProgressStatus | [ChangeProgressStatus](#configuration-api-datatypes-changeprogressstatus) | 

### DescribeDomainConfig<a name="configuration-api-actions-describedomainconfig"></a>

Displays the configuration of an OpenSearch Service domain\.

#### Syntax<a name="w54aac39c11c37b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/domain/domain-name/config
```

#### Request parameters<a name="w54aac39c11c37b7"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [DomainName](#configuration-api-datatypes-domainname) | Yes | Name of the OpenSearch Service domain configuration that you want to describe\. | 

#### Request body<a name="w54aac39c11c37b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11c37c11"></a>


****  

| Field | Data type | 
| --- | --- | 
| DomainConfig | [DomainConfig](#configuration-api-datatypes-domainconfig) | 

### DescribeDomains<a name="configuration-api-actions-describedomains"></a>

Describes the domain configuration for up to five specified OpenSearch Service domains\. Information includes the domain ID, domain service endpoint, and domain ARN\.

#### Syntax<a name="w54aac39c11c39b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/domain-info
{
  "DomainNames": [
    "domain-name1",
    "domain-name2",
  ]
}
```

#### Request parameters<a name="w54aac39c11c39b7"></a>

This operation does not use HTTP request parameters\.

#### Request body<a name="w54aac39c11c39b9"></a>


****  

| Field | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainNames | [DomainNameList](#configuration-api-datatypes-domainnamelist) | Yes | Array of OpenSearch Service domain names\. | 

#### Response elements<a name="w54aac39c11c39c11"></a>


****  

| Field | Data type | 
| --- | --- | 
| DomainStatusList | [DomainStatusList](#configuration-api-datatypesdomainstatuslist) | 

### DescribeInstanceTypeLimits<a name="configuration-api-actions-describeinstancetypelimits"></a>

Describes the instance count, storage, and master node limits for a given OpenSearch or Elasticsearch version and instance type\.

#### Syntax<a name="w54aac39c11c41b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/instanceTypeLimits/engine-version/instance-type?domainName=domain-name
```

#### Request parameters<a name="w54aac39c11c41b7"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| EngineVersion | String | Yes | Version of OpenSearch or Elasticsearch, in the format Elasticsearch\_X\.Y or OpenSearch\_X\.Y\. Defaults to the latest version of OpenSearch\. For a full list of supported versions, see [Supported versions of OpenSearch and Elasticsearch](what-is.md#choosing-version)\. version\. For a list of supported versions, see [Supported versions of OpenSearch and Elasticsearch](what-is.md#choosing-version)\. | 
| InstanceType | String | Yes | Instance type\. To view instance types by Region, see [Amazon OpenSearch Service pricing](https://aws.amazon.com/elasticsearch-service/pricing/)\. | 
| DomainName | [DomainName](#configuration-api-datatypes-domainname) | No | The name of an existing domain\. Only specify if you need the limits for an existing domain\. | 



#### Request body<a name="w54aac39c11c41b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11c41c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| LimitsByRole | Map | Map that contains all applicable instance type limits\. "data" refers to data nodes\. "master" refers to dedicated master nodes\. | 

### DescribeInboundConnections<a name="configuration-api-actions-describe-inbound-cross-cluster-search-connections"></a>

Lists all the inbound cross\-cluster search connections for a destination domain\.

#### Syntax<a name="w54aac39c11c43b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/cc/inboundConnection/search
{
  "Filters": [
    {
      "Name": filter-name (str),
      "Values" : [val1, val2, ..] (list of strings)
    },
    ....
    "MaxResults": int (Optional, default value - 100), 
    "NextToken": "next-token-string (optional)"
  ]
}
```

#### Request parameters<a name="w54aac39c11c43b7"></a>

This operation does not use HTTP request parameters\.

#### Request body<a name="w54aac39c11c43b9"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| Filters | Object | No | List of filter names and values that you can use for the describe requests\. The following fields are supported: connection\-id, local\-domain\-info\.domain\-name, local\-domain\-info\.owner\-id, local\-domain\-info\.region, and remote\-domain\-info\.domain\-name\. | 
| MaxResults | Integer | No | Limits the number of results\. The default is 100\.  | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Response elements<a name="w54aac39c11c43c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| CrossClusterSearchConnections | Object | List of inbound connections\. | 
| NextToken | String | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

### DescribeOutboundConnections<a name="configuration-api-actions-describe-outbound-cross-cluster-search-connections"></a>

Lists all outbound cross\-cluster search connections for a source domain\.

#### Syntax<a name="w54aac39c11c45b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/cc/outboundConnection/search
{
  "Filters": [
    {
      "Name": filter-name (str),
      "Values" : [val1, val2, ..] (list of strings)
    },
    ....
    "MaxResults": int (Optional, default value - 100), 
    "NextToken": "next-token-string (optional)"
  ]
}
```

#### Request parameters<a name="w54aac39c11c45b7"></a>

This operation does not use HTTP Request parameters\.

#### Request body<a name="w54aac39c11c45b9"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| Filters | Object | No | List of filter names and values that you can use for the describe requests\. The following fields are supported: connection\-id, remote\-domain\-info\.domain\-name, remote\-domain\-info\.owner\-id, remote\-domain\-info\.region, and local\-domain\-info\.domain\-name  | 
| MaxResults | Integer | No | Limits the number of results\. The default is 100\.  | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Response elements<a name="w54aac39c11c45c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| CrossClusterSearchConnections | Object | List of outbound connections\. | 
| NextToken | String | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

### DescribePackages<a name="configuration-api-actions-describepackages"></a>

Describes all packages available to OpenSearch Service\. Includes options for filtering, limiting the number of results, and pagination\.

#### Syntax<a name="configuration-api-actions-describepackages-s"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/packages/describe
{
  "Filters": [{
    "Name": "PackageStatus",
    "Value": [
      "DELETING", "AVAILABLE"
    ]
  }],
  "MaxResults": 5,
  "NextToken": "next-token",
}
```

#### Request parameters<a name="configuration-api-actions-describepackages-p"></a>

This operation does not use request parameters\.

#### Request body<a name="configuration-api-actions-describepackages-b"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| Filters | [Filters](#configuration-api-datatypes-filters) | No | Only returns packages that match the provided values\. | 
| MaxResults | Integer | No | Limits results to a maximum number of packages\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call includes a non\-null NextToken value\. If provided, returns results for the next page\. | 

#### Response elements<a name="configuration-api-actions-describepackages-r"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| PackageDetailsList | List | List of [PackageDetails](#configuration-api-datatypes-packagedetails) objects\. | 

### DescribeReservedInstanceOfferings<a name="configuration-api-actions-describereservedinstanceofferings"></a>

Describes the available Reserved Instance offerings for a given Region\.

#### Syntax<a name="w54aac39c11c49b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/reservedInstanceOfferings?offeringId=offering-id&maxResults=max-results&nextToken=next-token
```

#### Request parameters<a name="w54aac39c11c49b7"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| OfferingId | String | No | The offering ID\. | 
| MaxResults | Integer | No | Limits the number of results\. Must be between 30 and 100\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result that contains NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 



#### Request body<a name="w54aac39c11c49b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11c49c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| ReservedInstanceOfferings | ReservedInstanceOfferings | Container for all information about a Reserved Instance offering\. For more information, see [Purchasing Reserved Instances \(AWS CLI\)](ri.md#ri-cli)\. | 

### DescribeReservedInstances<a name="configuration-api-actions-describereservedinstances"></a>

Describes the instance that you have reserved in a given Region\.

#### Syntax<a name="w54aac39c11c51b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/reservedInstances?reservationId=reservation-id&maxResults=max-results&nextToken=next-token
```

#### Request parameters<a name="w54aac39c11c51b7"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| ReservationId | String | No | The reservation ID, assigned after you purchase a reservation\. | 
| MaxResults | Integer | No | Limits the number of results\. Must be between 30 and 100\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result that contains NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 



#### Request body<a name="w54aac39c11c51b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11c51c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| ReservedInstances |  ReservedInstances  | Container for all information about the instance that you have reserved\. For more information, see [Purchasing Reserved Instances \(AWS CLI\)](ri.md#ri-cli)\. | 

### DissociatePackage<a name="configuration-api-actions-dissociatepackage"></a>

Removes the package from the specified OpenSearch Service domain\. The package can't be in use with any OpenSearch index for the dissociation to succeed\. The package is still available in OpenSearch Service for association later\.

#### Syntax<a name="configuration-api-actions-dissociatepackage-s"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/packages/dissociate/package-id/domain-name
```

#### Request parameters<a name="configuration-api-actions-dissociatepackage-p"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| PackageID | String | Yes | Internal ID of the package that you want to dissociate from the domain\. Use [ListPackagesForDomain](#configuration-api-actions-listpackagesfordomain) to find this value\. | 
| DomainName | [DomainName](#configuration-api-datatypes-domainname) | Yes | Name of the domain that you want to dissociate the package from\. | 

#### Request body<a name="configuration-api-actions-dissociatepackage-b"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="configuration-api-actions-dissociatepackage-r"></a>


****  

| Field | Data type | 
| --- | --- | 
| DomainPackageDetails | [DomainPackageDetails](#configuration-api-datatypes-domainpackagedetails) | 

### GetCompatibleVersions<a name="configuration-api-actions-get-compat-vers"></a>

Returns a map of OpenSearch or Elasticsearch versions and the versions you can upgrade them to\.

#### Syntax<a name="w54aac39c11c55b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/compatibleVersions?domainName=domain-name
```

#### Request parameters<a name="w54aac39c11c55b7"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [DomainName](#configuration-api-datatypes-domainname) | No | The name of an existing domain\. | 

#### Request body<a name="w54aac39c11c55b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11c55c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| CompatibleVersions | Map | A map of OpenSearch or Elasticsearch versions and the versions you can upgrade them to:<pre>{<br />  "CompatibleVersions": [{<br />    "SourceVersion": "Elasticsearch_7.10",<br />    "TargetVersions": ["OpenSearch_1.0"]<br />  }]<br />}</pre> | 

### GetPackageVersionHistory<a name="configuration-api-actions-get-pac-ver-hist"></a>

Returns a map of OpenSearch versions and the versions you can upgrade them to\.

#### Syntax<a name="w54aac39c11c57b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/packages/package-id/history?maxResults=max-results&amp;nextToken=next-token
```

#### Request parameters<a name="w54aac39c11c57b7"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| PackageID | String | Yes | The name of an existing domain\. | 
| MaxResults | Integer | No | Limits the number of results\. Must be between 30 and 100\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request body<a name="w54aac39c11c57b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11c57c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| PackageVersionHistoryList | Map | A list of commit messages, updates tmies, and versions for the given package:<pre>"PackageVersionHistoryList": [<br />  {<br />    CommitMessage": "Add new synonyms",<br />    "CreatedAt": 1.605225005466E9,<br />    "PackageVersion": "v4"<br />  }<br />]</pre> | 

### GetUpgradeHistory<a name="configuration-api-actions-get-upgrade-hist"></a>

Returns a list of the domain's 10 most\-recent upgrade operations\.

#### Syntax<a name="w54aac39c11c59b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/upgradeDomain/domain-name/history?maxResults=max-results&amp;nextToken=next-token
```

#### Request parameters<a name="w54aac39c11c59b7"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| MaxResults | Integer | No | Limits the number of results\. Must be between 30 and 100\. | 
| DomainName | [DomainName](#configuration-api-datatypes-domainname) | Yes | The name of an existing domain\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request body<a name="w54aac39c11c59b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11c59c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| UpgradeHistoryList | UpgradeHistoryList | Container for result logs of the past 10 upgrade operations\. | 

### GetUpgradeStatus<a name="configuration-api-actions-get-upgrade-stat"></a>

Returns the most recent status of a domain's OpenSearch or Elasticsearch version upgrade\.

#### Syntax<a name="w54aac39c11c61b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/upgradeDomain/domain-name/status
```

#### Request parameters<a name="w54aac39c11c61b7"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [DomainName](#configuration-api-datatypes-domainname) | Yes | The name of an existing domain\. | 

#### Request body<a name="w54aac39c11c61b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11c61c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| UpgradeStepItem | UpgradeStepItem | Container for the most recent status of a domain's version upgrade\. | 

### ListDomainNames<a name="configuration-api-actions-listdomainnames"></a>

Displays the names of all OpenSearch Service domains owned by the current user *in the active Region*\.

#### Syntax<a name="w54aac39c11c63b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/domain
```

#### Request parameters<a name="w54aac39c11c63b7"></a>


****  

| Field | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| EngineType | String | No | Filters the output by domain engine type\. Acceptable values are Elasticsearch and OpenSearch\.  | 

This operation does not use request parameters\.

#### Request body<a name="w54aac39c11c63b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11c63c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| DomainNameList | [DomainNameList](#configuration-api-datatypes-domainnamelist) | The names of all OpenSearch Service domains owned by the current user\. | 

### ListDomainsForPackage<a name="configuration-api-actions-listdomainsforpackage"></a>

Lists all OpenSearch Service domains that a package is associated with\.

#### Syntax<a name="configuration-api-actions-listdomainsforpackage-s"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/packages/package-id/domains?maxResults=max-results&amp;nextToken=next-token
```

#### Request parameters<a name="configuration-api-actions-listdomainsforpackage-p"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| PackageID | String | Yes | The package for which to list domains\. | 
| MaxResults | Integer | No | Limits the number of results\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result that contains NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request body<a name="configuration-api-actions-listdomainsforpackage-b"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="configuration-api-actions-listdomainsforpackage-r"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| DomainPackageDetailsList | List | List of [DomainPackageDetails](#configuration-api-datatypes-domainpackagedetails) objects\. | 
| NextToken | String | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

### ListVersions<a name="configuration-api-actions-listversions"></a>

Lists all supported OpenSearch and Elasticsearch versions on OpenSearch Service\.

#### Syntax<a name="w54aac39c11c67b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/versions?maxResults=max-results&nextToken=next-token
```

#### Request parameters<a name="w54aac39c11c67b7"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| MaxResults | Integer | No | Limits the number of results\. Must be between 30 and 100\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request body<a name="w54aac39c11c67b9"></a>

This operation does not use the HTTP request body\.

### ListInstanceTypeDetails<a name="configuration-api-actions-listinstancetypedetails"></a>

Lists all instance types and available features for a given OpenSearch or Elasticsearch version\.

#### Syntax<a name="w54aac39c11c69b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/instanceTypeDetails/engine-version?domainName=domain-name?maxResults=max-results&nextToken=next-token
```

#### Request parameters<a name="w54aac39c11c69b7"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| EngineVersion | String | Yes | Version of OpenSearch or Elasticsearch, in the format Elasticsearch\_X\.Y or OpenSearch\_X\.Y\. Defaults to the latest version of OpenSearch\. For the full list of supported versions, see [Supported versions of OpenSearch and Elasticsearch](what-is.md#choosing-version)\. | 
| DomainName | [DomainName](#configuration-api-datatypes-domainname) | Yes | Name of the domain that you want to list instance type details for\. | 
|  `MaxResults`  |  Integer  |  No  | Limits the number of results\. Must be between 30 and 100\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request body<a name="w54aac39c11c69b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11c69c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| InstanceTypeDetails | List | Lists all supported instance types and features for the given OpenSearch or Elasticsearch version\. | 
| NextToken | String | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\.  | 

### ListPackagesForDomain<a name="configuration-api-actions-listpackagesfordomain"></a>

Lists all packages associated with the OpenSearch Service domain\.

#### Syntax<a name="configuration-api-actions-listpackagesfordomain-s"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/domain/domain-name/packages?maxResults=max-results&amp;nextToken=next-token
```

#### Request parameters<a name="configuration-api-actions-listpackagesfordomain-p"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | String | Yes | The name of the domain for which you want to list associated packages\. | 
| MaxResults | Integer | No | Limits the number of results\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result that contains NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request body<a name="configuration-api-actions-listpackagesfordomain-b"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="configuration-api-actions-listpackagesfordomain-r"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| DomainPackageDetailsList | List | List of [DomainPackageDetails](#configuration-api-datatypes-domainpackagedetails) objects\. | 
| NextToken | String | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

### ListTags<a name="configuration-api-actions-listtags"></a>

Displays all resource tags for an OpenSearch Service domain\.

#### Syntax<a name="w54aac39c11c73b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2021-01-01/tags?arn=domain-arn
```

#### Request parameters<a name="w54aac39c11c73b7"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| ARN | [`ARN`](#configuration-api-datatypes-arn) | Yes | Amazon Resource Name \(ARN\) for the OpenSearch Service domain\. | 

#### Request body<a name="w54aac39c11c73b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11c73c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| TagList | [`TagList`](#configuration-api-datatypes-taglist) | List of resource tags\. For more information, see [Tagging Amazon OpenSearch Service domains](managedomains-awsresourcetagging.md)\. | 

### PurchaseReservedInstanceOffering<a name="configuration-api-actions-purchasereservedinstance"></a>

Purchases a Reserved Instance\.

#### Syntax<a name="w54aac39c11c75b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/purchaseReservedInstanceOffering
{
  "ReservationName" : "my-reservation",
  "ReservedInstanceOfferingId" : "1a2a3a4a5-1a2a-3a4a-5a6a-1a2a3a4a5a6a",
  "InstanceCount" : 3
}
```

#### Request parameters<a name="w54aac39c11c75b7"></a>

This operation does not use HTTP request parameters\.

#### Request body<a name="w54aac39c11c75b9"></a>


****  

| Name | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| ReservationName | String | Yes | A descriptive name for your reservation\. Must be between 5 and 64 characters\. | 
|  ReservedInstanceOfferingId  | String | Yes | The offering ID\. | 
| InstanceCount | Integer | Yes | The number of instances that you want to reserve\. | 

#### Response elements<a name="w54aac39c11c75c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| ReservationName | String | The name of your reservation\. | 
|  ReservedInstanceId | String | The reservation ID\. | 

### RejectInboundConnection<a name="configuration-api-actions-reject-inbound-cross-cluster-search-connection"></a>

Allows the destination domain owner to reject an inbound cross\-cluster search connection request\.

#### Syntax<a name="w54aac39c11c77b5"></a>

```
PUT https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/cc/inboundConnection/connection-id/reject
```

#### Request parameters<a name="w54aac39c11c77b7"></a>

This operation does not use HTTP request parameters\.

#### Request body<a name="w54aac39c11c77b9"></a>

This operation does not use the HTTP request body\.

#### Response elements<a name="w54aac39c11c77c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| CrossClusterSearchConnection | Object | Inbound connection details\. | 

### RemoveTags<a name="configuration-api-actions-removetags"></a>

Removes the specified resource tags from an OpenSearch Service domain\.

#### Syntax<a name="w54aac39c11c79b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/tags-removal
{
  "ARN": "arn:aws:es:us-east-1:123456789012:domain/my-domain",
  "TagKeys": [
    "tag-key1",
    "tag-key2"
  ]
}
```

#### Request parameters<a name="w54aac39c11c79b7"></a>

This operation does not use HTTP request parameters\.

#### Request body<a name="w54aac39c11c79b9"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| ARN | [`ARN`](#configuration-api-datatypes-arn) | Yes | Amazon Resource Name \(ARN\) of an OpenSearch Service domain\. For more information, see [IAM identifiers](http://docs.aws.amazon.com/IAM/latest/UserGuide/index.html?Using_Identifiers.html) in the AWS Identity and Access Management User Guide\.  | 
| TagKeys | [`TagKey`](#configuration-api-datatypes-tagkey) | Yes | List of tag keys for resource tags that you want to remove from an OpenSearch Service domain\. | 

#### Response elements<a name="w54aac39c11c79c11"></a>

The `RemoveTags` operation does not return a response element\.

### StartServiceSoftwareUpdate<a name="configuration-api-actions-startupdate"></a>

Schedules a service software update for an OpenSearch Service domain\.

#### Syntax<a name="w54aac39c11c81b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/serviceSoftwareUpdate/start
{
  "DomainName": "domain-name"
}
```

#### Request parameters<a name="w54aac39c11c81b7"></a>

This operation does not use HTTP request parameters\.

#### Request body<a name="w54aac39c11c81b9"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [`DomainName`](#configuration-api-datatypes-domainname) | Yes | Name of the OpenSearch Service domain that you want to update to the latest service software\. | 

#### Response elements<a name="w54aac39c11c81c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| ServiceSoftwareOptions | ServiceSoftwareOptions | Container for the state of your domain relative to the latest service software\. | 

### CancelServiceSoftwareUpdate<a name="configuration-api-actions-stopupdate"></a>

Stops a scheduled service software update for an OpenSearch Service domain\. Only works if the domain's `UpdateStatus` is `PENDING_UPDATE`\.

#### Syntax<a name="w54aac39c11c83b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/serviceSoftwareUpdate/stop
{
  "DomainName": "domain-name"
}
```

#### Request parameters<a name="w54aac39c11c83b7"></a>

This operation does not use HTTP request parameters\.

#### Request body<a name="w54aac39c11c83b9"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [`DomainName`](#configuration-api-datatypes-domainname) | Yes | Name of the OpenSearch Service domain that you want to update to the latest service software\. | 

#### Response elements<a name="w54aac39c11c83c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| ServiceSoftwareOptions | [`ServiceSoftwareOptions`](#configuration-api-datatypes-servicesoftware) | Container for the state of your domain relative to the latest service software\. | 

### UpdateDomainConfig<a name="configuration-api-actions-updatedomainconfig"></a>

Modifies the configuration of an OpenSearch Service domain, such as the instance type and the number of instances\. You only need to specify the values that you want to update\.

#### Syntax<a name="w54aac39c11c85b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/domain/domain-name/config
{
  "ClusterConfig": {
    "ZoneAwarenessConfig": {
      "AvailabilityZoneCount": 3
    },
    "ZoneAwarenessEnabled": true|false,
    "InstanceCount": 3,
    "DedicatedMasterEnabled": true|false,
    "DedicatedMasterType": "c5.large.search",
    "DedicatedMasterCount": 3,
    "InstanceType": "r5.large.search",
    "WarmCount": 6,
    "WarmType": "ultrawarm1.medium.search",
    "ColdStorageOptions": {
       "Enabled": true|false
    },
  "EBSOptions": {
    "EBSEnabled": true|false,
    "VolumeType": "io1|gp2|standard",
    "Iops": 1000,
    "VolumeSize": 35
  },
  "SnapshotOptions": {
    "AutomatedSnapshotStartHour": 3
  },
  "EncryptionAtRestOptions": {
    "Enabled": true|false,
    "KmsKeyId":"arn:aws:kms:us-east-1:123456789012:alias/my-key"
  },
  "NodeToNodeEncryptionOptions": {
    "Enabled": true|false
  },
  "VPCOptions": {
    "SubnetIds": ["subnet-abcdefg1", "subnet-abcdefg2", "subnet-abcdefg3"],
    "SecurityGroupIds": ["sg-12345678"]
  },
  "AdvancedOptions": {
    "rest.action.multi.allow_explicit_index": "true|false",
    "indices.fielddata.cache.size": "40",
    "indices.query.bool.max_clause_count": "1024",
    "override_main_response_version": "true|false"
  },
  "CognitoOptions": {
    "Enabled": true|false,
    "UserPoolId": "us-east-1_121234567",
    "IdentityPoolId": "us-east-1:12345678-1234-1234-1234-123456789012",
    "RoleArn": "arn:aws:iam::123456789012:role/service-role/CognitoAccessForAmazonOpenSearch"
  },
  "DomainEndpointOptions": {
    "EnforceHTTPS": true|false,
    "TLSSecurityPolicy": "Policy-Min-TLS-1-2-2019-07|Policy-Min-TLS-1-0-2019-07",
    "CustomEndpointEnabled": "true|false",
    "CustomEndpoint": "www.my-custom-endpoint.com",
    "CustomEndpointCertificateArn": "arn:aws:iam::123456789012:certificate/my-certificate"
  },
  "LogPublishingOptions": {
    "SEARCH_SLOW_LOGS": {
      "CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:264071961897:log-group1:sample-domain",
      "Enabled":true|false
    },
    "INDEX_SLOW_LOGS": {
      "CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:264071961897:log-group2:sample-domain",
      "Enabled":true|false
    },
    "ES_APPLICATION_LOGS": {
      "CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:264071961897:log-group3:sample-domain",
      "Enabled":true|false
    }
  },
  "AdvancedSecurityOptions": {
    "Enabled": true|false,
    "InternalUserDatabaseEnabled": true|false,
    "AnonymousAuthEnabled": true|false,
    "AnonymousAuthDisableDate": 1234567890, 
    "MasterUserOptions": {
      "MasterUserARN": "arn:aws:iam::123456789012:role/my-master-user-role"
      "MasterUserName": "my-master-username",
      "MasterUserPassword": "my-master-password"
    },
    "SAMLOptions": {
      "Enabled": true,
      "Idp": {
        "EntityId": "entity-id",
        "MetadataContent": "metadata-content-with-quotes-escaped"
      },
      "RolesKey": "optional-roles-key",
      "SessionTimeoutMinutes": 180,
      "SubjectKey": "optional-subject-key"
    }
  },
  "AutoTuneOptions": {
    "DesiredState": "ENABLED|DISABLED",
    "MaintenanceSchedules": [{
      "StartAt": 1234567890,
      "Duration": {
        "Value": 2,
        "Unit": "HOURS"
      },
      "CronExpressionForRecurrence": "cron(0 0 ? * 3 *)"
    }],
    "RollbackOnDisable": "NO_ROLLBACK|DEFAULT_ROLLBACK"
  },
  "DomainName": "my-domain",
  "AccessPolicies": "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"es:*\"],\"Resource\":\"arn:aws:es:us-east-1:123456789012:domain/my-domain/*\"}]}",
  "DryRun": true|false
}
```

#### Request parameters<a name="w54aac39c11c85b7"></a>

This operation does not use HTTP request parameters\.

#### Request body<a name="w54aac39c11c85b9"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [`DomainName`](#configuration-api-datatypes-domainname) | Yes | Name of the OpenSearch Service domain for which you want to update the configuration\. | 
| ClusterConfig | [`ClusterConfig`](#configuration-api-datatypes-clusterconfig) | No | Changes that you want to make to the cluster configuration, such as the instance type and number of EC2 instances\. | 
| EBSOptions | [`EBSOptions`](#configuration-api-datatypes-ebsoptions) | No | Type and size of EBS volumes attached to data nodes\.  | 
| VPCOptions | [`VPCOptions`](#configuration-api-datatypes-vpcoptions) | No | Container for the values required to configure OpenSearch Service to work with a VPC\. To learn more, see [Launching your Amazon OpenSearch Service domains within a VPC](vpc.md)\. | 
| SnapshotOptions | [`SnapshotOptions`](#configuration-api-datatypes-snapshotoptions) | No | DEPRECATED\. Hour during which the service takes an automated daily snapshot of the indices in the OpenSearch Service domain\. | 
| AdvancedOptions | [`AdvancedOptions`](#configuration-api-datatypes-advancedoptions) | No | Key\-value pairs to specify advanced configuration options\. For more information, see [Advanced cluster settings](createupdatedomains.md#createdomain-configure-advanced-options)\. | 
| AccessPolicies | String | No | Specifies the access policies for the OpenSearch Service domain\. For more information, see [Configuring access policies](createupdatedomains.md#createdomain-configure-access-policies)\. | 
| LogPublishingOptions | [`LogPublishingOptions`](#configuration-api-datatypes-logpublishingoptions) | No | Key\-value string pairs to configure slow log publishing\. | 
| CognitoOptions | [`CognitoOptions`](#configuration-api-datatypes-cognitooptions) | No | Key\-value pairs to configure OpenSearch Service to use Amazon Cognito authentication for OpenSearch Dashboards\. | 
| DomainEndpointOptions | [DomainEndpointOptions](#configuration-api-datatypes-domainendpointoptions) | No | Additional options for the domain endpoint, such as whether to require HTTPS for all traffic\. | 
| AdvancedSecurityOptions | [AdvancedSecurityOptions](#configuration-api-datatypes-advancedsec) | No | Options for fine\-grained access control\. | 
| AutoTuneOptions | [AutoTuneOptions](#configuration-api-datatypes-autotune) | No | Options for Auto\-Tune\. | 
| NodeToNodeEncryptionOptions | [NodeToNodeEncryptionOptions](#configuration-api-datatypes-node-to-node) | No | Enables node\-to\-node encryption\. | 
| EncryptionAtRestOptions | [EncryptionAtRestOptions](#configuration-api-datatypes-encryptionatrest) | No | Key\-value pairs to enable encryption at rest\. | 
| DryRun | Boolean | No | Defaults to false\. If true, OpenSearch Service checks whether the configuration change will cause a blue/green deployment, but does not perform the update\. | 

#### Response elements<a name="w54aac39c11c85c11"></a>


****  

| Field | Data type | 
| --- | --- | 
| DomainConfig | [DomainConfig](#configuration-api-datatypes-domainconfig) | 
| DryRunResults | [DryRunResults](#configuration-api-datatypes-dryrunresults) | 

### UpdatePackage<a name="configuration-api-actions-updatepackage"></a>

Update a package for use with OpenSearch Service domains\.

#### Syntax<a name="configuration-api-actions-updatepackage-s"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/packages/update
{
  "PackageID": "F11111111",
  "PackageDescription": "My synonym file.",
  "CommitMessage": "Added some synonyms.",
  "PackageSource": {
    "S3BucketName": "my-s3-bucket",
    "S3Key": "synonyms.txt"
  }
}
```

#### Request parameters<a name="configuration-api-actions-updatepackage-p"></a>

This operation does not use request parameters\.

#### Request body<a name="configuration-api-actions-updatepackage-b"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| PackageID | String | Yes | Unique ID for the package\. | 
| PackageDescription | String | No | Description of the package\. | 
| CommitMessage | String | No | Commit message for the updated file\. | 
| PackageSource | [PackageSource](#configuration-api-datatypes-packagesource) | Yes | S3 bucket and key for the package\. | 

#### Response elements<a name="configuration-api-actions-updatepackage-r"></a>


****  

| Field | Data type | 
| --- | --- | 
| PackageDetails | [PackageDetails](#configuration-api-datatypes-packagedetails) | 

### UpgradeDomain<a name="configuration-api-actions-upgrade-domain"></a>

Upgrades an OpenSearch Service domain to a new version of OpenSearch or Elasticsearch\. Alternately, checks upgrade eligibility\.

#### Syntax<a name="w54aac39c11c89b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/upgradeDomain
{
  "DomainName": "domain-name",
  "TargetVersion": "OpenSearch_1.0",
  "PerformCheckOnly": true|false
}
```

#### Request parameters<a name="w54aac39c11c89b7"></a>

This operation does not use HTTP request parameters\.

#### Request body<a name="w54aac39c11c89b9"></a>


****  

| Parameter | Data type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | String | Yes | Name of the OpenSearch Service domain that you want to upgrade\. | 
| TargetVersion | String | Yes | OpenSearch or Elasticsearch version to which you want to upgrade, in the format Opensearch\_X\.Y or Elasticsearch\_X\.Y\. See [GetCompatibleVersions](#configuration-api-actions-get-compat-vers)\. | 
| PerformCheckOnly | Boolean | No | Defaults to false\. If true, OpenSearch Service checks the eligibility of the domain, but does not perform the upgrade\. | 
|  `AdvancedOptions`  | [AdvancedOptions](#configuration-api-datatypes-advancedoptions) | No | Only supports the override\_main\_response\_version parameter and not other advanced options\. You can only include this option when upgrading to an OpenSearch version\. Specifies whether the domain reports its version as 7\.10 so that it continues to work with Elasticsearch OSS clients and plugins\. | 

#### Response elements<a name="w54aac39c11c89c11"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| UpgradeDomainResponse | Map | Basic response confirming operation details\. | 

## Data types<a name="configuration-api-datatypes"></a>

This section describes the data types used by the configuration API\.

### AdvancedOptions<a name="configuration-api-datatypes-advancedoptions"></a>

Key\-value pairs to specify advanced OpenSearch configuration options\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| rest\.action\.multi\.allow\_explicit\_index | Key\-value pair: `"rest.action.multi.allow_explicit_index":"true"`  | Note the use of a string rather than a boolean\. Specifies whether explicit references to indices are allowed inside the body of HTTP requests\. If you want to configure access policies for domain sub\-resources, such as specific indices and domain APIs, you must disable this property\. For more information about access policies for sub\-resources, see [Configuring access policies](createupdatedomains.md#createdomain-configure-access-policies)\. | 
| indices\.fielddata\.cache\.size | Key\-value pair:`"indices.fielddata.cache.size":"80"` | Note the use of a string rather than an integer\. Specifies the percentage of Java heap space that is allocated to field data\. By default, this setting is unbounded\. | 
| indices\.query\.bool\.max\_clause\_count | Key\-value pair:`"indices.query.bool.max_clause_count":"1024"` | Note the use of a string rather than an integer\. Specifies the maximum number of clauses allowed in a Lucene boolean query\. 1,024 is the default\. Queries with more than the permitted number of clauses that result in a TooManyClauses error\. To learn more, see the [Lucene documentation](https://lucene.apache.org/core/6_6_0/core/org/apache/lucene/search/BooleanQuery.html)\. | 
| override\_main\_response\_version | Key\-value pair:`"override_main_response_version":"true"` | Note the use of a string rather than a boolean\. Specifies whether the domain reports its version as 7\.10 to allow Elasticsearch OSS clients and plugins to continue working with it\. Only relevant when creating an OpenSearch domain or upgrading to OpenSearch from an Elasticsearch OSS version\. Default is false when creating a domain and true when upgrading a domain\. | 

### AdvancedSecurityOptions<a name="configuration-api-datatypes-advancedsec"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| Enabled | Boolean | True to enable [fine\-grained access control](fgac.md)\. | 
| AnonymousAuthEnabled | Boolean | True to enable a 30\-day migration period during which administrators can create role mappings\. Only necessary [enabling fine\-grained access control on an existing domain](fgac.md#fgac-enabling-existing)\. | 
| AnonymousAuthDisableDate | Timestamp | Date and time when the migration period will be disabled\. | 
| InternalUserDatabaseEnabled | Boolean | True to enable the internal user database\. | 
| MasterUserOptions | [MasterUserOptions](#configuration-api-datatypes-masteruser) | Container for information about the master user\. | 
| SAMLOptions | SAMLOptions | Container for information about the SAML configuration for OpenSearch Dashboards\. | 

### ARN<a name="configuration-api-datatypes-arn"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| ARN | String | Amazon Resource Name \(ARN\) of an OpenSearch Service domain\. For more information, see [IAM ARNs](http://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns) in the AWS Identity and Access Management User Guide\. | 

### AutoTuneOptions<a name="configuration-api-datatypes-autotune"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| DesiredState | String | Either ENABLED or DISABLED\. | 
| MaintenanceSchedules | List |  A list of maintenance schedules during which Auto\-Tune can deploy changes: <pre>{<br />  "StartAt": 1234567890,<br />  "Duration": {<br />    "Value": 2,<br />    "Unit": "HOURS"<br />  },<br />  "CronExpressionForRecurrence": "cron(* * ? * * *)"<br />}</pre> Maintenance schedules are overwrite, not append\. If your request includes no schedules, the request deletes all existing schedules\. To preserve existing schedules, make a call to [DescribeDomainConfig](#configuration-api-actions-describedomainconfig) first and use the `MaintenanceSchedules` portion of the response as the basis for this section\. `StartAt` is Epoch time, and `Value` is a long integer\.  | 
| RollbackOnDisable | String | When disabling Auto\-Tune, specify NO\_ROLLBACK to retain all prior Auto\-Tune settings or DEFAULT\_ROLLBACK to revert to the OpenSearch Service defaults\.If you specify DEFAULT\_ROLLBACK, you must include a `MaintenanceSchedule` in the request\. Otherwise, OpenSearch Service is unable to perform the rollback\. | 

### ChangeProgressDetails<a name="configuration-api-datatypes-changeprogress"></a>

Container for information about a configuration change happening on a domain\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| ChangeId | String | The ID of the configuration change\. | 
| Message | String | A message corresponding to the status of the configuration change\. | 

### ChangeProgressStatus<a name="configuration-api-datatypes-changeprogressstatus"></a>

Container for information about the stages of a configuration change happening on a domain\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| ChangeId | String | ID of the configuration change\. | 
| ChangeProgressStages | Object | Progress details for each stage of the update process\. Each stage includes a Description, LastUpdated, Name, and Status field\. | 
| CompletedProperties | StringList | List of domain properties that have already been updated\. | 
| PendingProperties | StringList | List of domain properties that still need to be updated\. | 
| StartTime | Timestamp | Date and time when the configuration change started\.  | 
| Status | String | Current status of the configuration change\. | 
| TotalNumberOfStages | Integer | Total number of stages required for the configuration change to complete\. | 

### ClusterConfig<a name="configuration-api-datatypes-clusterconfig"></a>

Container for the cluster configuration of an OpenSearch Service domain\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| InstanceType | String | Instance type of data nodes in the cluster\. | 
| InstanceCount | Integer | Number of instances in the cluster\. | 
| DedicatedMasterEnabled | Boolean | Indicates whether dedicated master nodes are enabled for the cluster\. True if the cluster will use a dedicated master node\. False if the cluster will not\. For more information, see [Dedicated master nodes in Amazon OpenSearch Service](managedomains-dedicatedmasternodes.md)\. | 
| DedicatedMasterType | String | OpenSearch Service instance type of the dedicated master nodes in the cluster\. | 
| DedicatedMasterCount | Integer | Number of dedicated master nodes in the cluster\. This number must be greater than 1, otherwise you receive a validation exception\. | 
| ZoneAwarenessEnabled | Boolean | Indicates whether multiple Availability Zones are enabled\. For more information, see [Configuring a multi\-AZ domain in Amazon OpenSearch Service](managedomains-multiaz.md)\. | 
| ZoneAwarenessConfig | [`ZoneAwarenessConfig`](#configuration-api-datatypes-az) | Container for zone awareness configuration options\. Only required if ZoneAwarenessEnabled is true\. | 
| WarmEnabled | Boolean | Whether to enable warm storage for the cluster\. | 
| WarmCount | Integer | The number of warm nodes in the cluster\. | 
| WarmType | String | The instance type for the cluster's warm nodes\. | 
| WarmStorage | Integer | The total provisioned amount of warm storage in GiB\. | 
| ColdStorageOptions | [`ColdStorageOptions`](#configuration-api-datatypes-cs) | Container for cold storage configuration options\. | 

### CognitoOptions<a name="configuration-api-datatypes-cognitooptions"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| Enabled | Boolean | Whether to enable or disable Amazon Cognito authentication for OpenSearch Dashboards\. See [Configuring Amazon Cognito authentication for OpenSearch Dashboards](cognito-auth.md)\. | 
| UserPoolId | String | The Amazon Cognito user pool ID that you want OpenSearch Service to use for OpenSearch Dashboards authentication\. | 
| IdentityPoolId | String | The Amazon Cognito identity pool ID that you want OpenSearch Service to use for OpenSearch Dashboards authentication\. | 
| RoleArn | String | The AmazonOpenSearchServiceCognitoAccess role that allows OpenSearch Service to configure your user pool and identity pool\. | 

### ColdStorageOptions<a name="configuration-api-datatypes-cs"></a>

Container for the parameters required to enable cold storage for an OpenSearch Service domain\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| Enabled | Boolean | Whether to enable or disable cold storage on the domain\. See [Cold storage for Amazon OpenSearch Service](cold-storage.md)\. | 

### CreateDomainRequest<a name="configuration-api-datatypes-createesdomainrequest"></a>

Container for the parameters required by the `CreateDomain` service operation\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| DomainName | [`DomainName`](#configuration-api-datatypes-domainname) | Name of the OpenSearch Service domain to create\. | 
| ClusterConfig | [`ClusterConfig`](#configuration-api-datatypes-clusterconfig) | Container for the cluster configuration of an OpenSearch Service domain\. | 
| EBSOptions | [`EBSOptions`](#configuration-api-datatypes-ebsoptions) | Container for the parameters required to enable EBS\-based storage for an OpenSearch Service domain\. | 
| AccessPolicies | String | IAM policy document that specifies the access policies for the new OpenSearch Service domain\. For more information, see [Configuring access policies](createupdatedomains.md#createdomain-configure-access-policies)\. | 
| DomainEndpointOptions | [`DomainEndpointOptions`](#configuration-api-datatypes-domainendpointoptions) | Additional options for the domain endpoint, such as whether to require HTTPS for all traffic\. | 
| SnapshotOptions | [`SnapshotOptions`](#configuration-api-datatypes-snapshotoptions) | DEPRECATED\. Container for parameters required to configure automated snapshots of domain indices\. | 
| VPCOptions | [`VPCOptions`](#configuration-api-datatypes-vpcoptions) | Container for the values required to configure OpenSearch Service to work with a VPC\. | 
| LogPublishingOptions | [`LogPublishingOptions`](#configuration-api-datatypes-logpublishingoptions) | Key\-value string pairs to configure slow log publishing\. | 
| AdvancedOptions | [AdvancedOptions](#configuration-api-datatypes-advancedoptions) | Key\-value pairs to specify advanced configuration options\. | 
| CognitoOptions | [`CognitoOptions`](#configuration-api-datatypes-cognitooptions) | Key\-value pairs to configure OpenSearch Service to use Amazon Cognito authentication for OpenSearch Dashboards\. | 
| NodeToNodeEncryptionOptions | [`NodeToNodeEncryptionOptions`](#configuration-api-datatypes-node-to-node) | Specify true to enable node\-to\-node encryption\. | 

### DomainEndpointOptions<a name="configuration-api-datatypes-domainendpointoptions"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| EnforceHTTPS | Boolean | true to require that all traffic to the domain arrive over HTTPS\. | 
| TLSSecurityPolicy | String | The minimum TLS version required for traffic to the domain\. Valid values are TLS 1\.0 \(default\) or 1\.2:[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/configuration-api.html) | 
| CustomEndpointEnabled | Boolean | Whether to enable a custom endpoint for the domain\. | 
| CustomEndpoint | String | The fully qualified URL for the custom endpoint\. | 
| CustomEndpointCertificateArn | String | The ARN for your security certificate, managed in ACM\. | 

### DomainID<a name="configuration-api-datatypes-domainid"></a>


****  

| Data type | Description | 
| --- | --- | 
| String | Unique identifier for an OpenSearch Service domain\.  | 

### DomainName<a name="configuration-api-datatypes-domainname"></a>

Name of an OpenSearch Service domain\. 


****  

| Data type | Description | 
| --- | --- | 
| String | Name of an OpenSearch Service domain\. Domain names are unique across all domains owned by the same account within an AWS Region\. Domain names must start with a lowercase letter and must be between 3 and 28 characters\. Valid characters are a\-z \(lowercase only\), 0\-9, and – \(hyphen\)\. | 

### DomainNameList<a name="configuration-api-datatypes-domainnamelist"></a>

String of OpenSearch Service domain names\.


****  

| Data type | Description | 
| --- | --- | 
| String Array | Array of OpenSearch Service domains in the following format:`["<Domain_Name>","<Domain_Name>"...]` | 

### DomainPackageDetails<a name="configuration-api-datatypes-domainpackagedetails"></a>

Information about a package that is associated with a domain\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| DomainName | String | Name of the domain you associated a package with\. | 
| DomainPackageStatus | String | State of the association\. Values are ASSOCIATING, ASSOCIATION\_FAILED, ACTIVE, DISSOCIATING, and DISSOCIATION\_FAILED\.  | 
| ErrorDetails | String | Additional information if the package is in an error state\. Null otherwise\. | 
| LastUpdated | Timestamp | Timestamp of the most\-recent update to the association status\. | 
| PackageID | String | Internal ID of the package\. | 
| PackageName | String | User\-specified name of the package\. | 
| PackageType | String | Currently supports only TXT\-DICTIONARY\. | 
| ReferencePath | String | Denotes the location of the package on the OpenSearch Service cluster nodes\. It's the same as synonym\_path for dictionary files\. | 

### DomainConfig<a name="configuration-api-datatypes-domainconfig"></a>

Container for the configuration of an OpenSearch Service domain\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| EngineVersion | String | OpenSearch or Elasticsearch version\. | 
| ClusterConfig | [`ClusterConfig`](#configuration-api-datatypes-clusterconfig) | Container for the cluster configuration of an OpenSearch Service domain\. | 
| EBSOptions | [`EBSOptions`](#configuration-api-datatypes-ebsoptions) | Container for EBS options configured for an OpenSearch Service domain\. | 
| AccessPolicies | String | Specifies the access policies for the OpenSearch Service domain\. For more information, see [Configuring access policies](createupdatedomains.md#createdomain-configure-access-policies)\. | 
| SnapshotOptions | [`SnapshotOptions`](#configuration-api-datatypes-snapshotoptions) | DEPRECATED\. Container for parameters required to configure automated snapshots of domain indices\. | 
| DomainEndpointOptions | [`DomainEndpointOptions`](#configuration-api-datatypes-domainendpointoptions) | Additional options for the domain endpoint, such as whether to require HTTPS for all traffic\. | 
| VPCOptions | [`VPCDerivedInfo`](#configuration-api-datatypes-vpcderivedinfo) | The current [VPCOptions](#configuration-api-datatypes-vpcoptions) for the domain and the status of any updates to their configuration\. | 
| LogPublishingOptions | [`LogPublishingOptions`](#configuration-api-datatypes-logpublishingoptions) | Key\-value pairs to configure slow log publishing\. | 
| AdvancedOptions | [`AdvancedOptions`](#configuration-api-datatypes-advancedoptions) | Key\-value pairs to specify advanced configuration options\. | 
| EncryptionAtRestOptions | [`EncryptionAtRestOptions`](#configuration-api-datatypes-encryptionatrest) | Key\-value pairs to enable encryption at rest\. | 
| NodeToNodeEncryptionOptions | [`NodeToNodeEncryptionOptions`](#configuration-api-datatypes-node-to-node) | Whether node\-to\-node encryption is enabled or disabled\. | 
| ChangeProgressDetails | [`ChangeProgressDetails`](#configuration-api-datatypes-changeprogress) | Container for information about the progress of the configuration change\. | 

### DomainStatus<a name="configuration-api-datatypes-domainstatus"></a>

Container for the contents of a `DomainStatus` data structure\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| DomainID | [`DomainID`](#configuration-api-datatypes-domainid) | Unique identifier for an OpenSearch Service domain\. | 
| DomainName | [`DomainName`](#configuration-api-datatypes-domainname) | Name of an OpenSearch Service domain\. Domain names are unique across all domains owned by the same account within an AWS Region\. Domain names must start with a lowercase letter and must be between 3 and 28 characters\. Valid characters are a\-z \(lowercase only\), 0\-9, and – \(hyphen\)\. | 
| ARN | [`ARN`](#configuration-api-datatypes-arn) | Amazon Resource Name \(ARN\) of an OpenSearch Service domain\. For more information, see [IAM identifiers](http://docs.aws.amazon.com/IAM/latest/UserGuide/index.html?Using_Identifiers.html) in the AWS Identity and Access Management User Guide\. | 
| Created | Boolean | Status of the creation of an OpenSearch Service domain\. True if creation of the domain is complete\. False if domain creation is still in progress\. | 
| Deleted | Boolean | Status of the deletion of an OpenSearch Service domain\. True if deletion of the domain is complete\. False if domain deletion is still in progress\. | 
| Endpoint | [`ServiceUrl`](#configuration-api-datatypes-serviceurl) | Domain\-specific endpoint used to submit index, search, and data upload requests to an OpenSearch Service domain\. | 
| Endpoints | [`EndpointsMap`](#configuration-api-datatypes-endpointsmap) | The key\-value pair that exists if the OpenSearch Service domain uses VPC endpoints\. | 
| Processing | Boolean | Status of a change in the configuration of an OpenSearch Service domain\. True if the service is still processing the configuration changes\. False if the configuration change is active\. You must wait for a domain to reach active status before submitting index, search, and data upload requests\. | 
| EngineVersion | String | OpenSearch or Elasticsearch version\. | 
| ClusterConfig | [`ClusterConfig`](#configuration-api-datatypes-clusterconfig) | Container for the cluster configuration of an OpenSearch Service domain\. | 
| EBSOptions | [`EBSOptions`](#configuration-api-datatypes-ebsoptions) | Container for the parameters required to enable EBS\-based storage for an OpenSearch Service domain\. | 
| AccessPolicies | String | IAM policy document specifying the access policies for the new OpenSearch Service domain\. For more information, see [Configuring access policies](createupdatedomains.md#createdomain-configure-access-policies)\. | 
| SnapshotOptions | [`SnapshotOptions`](#configuration-api-datatypes-snapshotoptions) | DEPRECATED\. Container for parameters required to configure the time of daily automated snapshots of OpenSearch Service domain indices\.  | 
| VPCOptions | [`VPCDerivedInfo`](#configuration-api-datatypes-vpcoptions) | Information that OpenSearch Service derives based on [VPCOptions](#configuration-api-datatypes-vpcoptions) for the domain\. | 
| LogPublishingOptions | [`LogPublishingOptions`](#configuration-api-datatypes-logpublishingoptions) | Key\-value pairs to configure slow log publishing\. | 
| AdvancedOptions | [`AdvancedOptions`](#configuration-api-datatypes-advancedoptions) | Key\-value pairs to specify advanced configuration options\. | 
| EncryptionAtRestOptions | [`EncryptionAtRestOptions`](#configuration-api-datatypes-encryptionatrest) | Key\-value pairs to enable encryption at rest\. | 
| CognitoOptions | [`CognitoOptions`](#configuration-api-datatypes-cognitooptions) | Key\-value pairs to configure OpenSearch Service to use Amazon Cognito authentication for OpenSearch Dashboards\. | 
| NodeToNodeEncryptionOptions | [`NodeToNodeEncryptionOptions`](#configuration-api-datatypes-node-to-node) | Whether node\-to\-node encryption is enabled or disabled\. | 
| UpgradeProcessing | Boolean | True if an upgrade to a new OpenSearch or Elasticsearch version is in progress\. | 
| ServiceSoftwareOptions | [ServiceSoftwareOptions](#configuration-api-datatypes-servicesoftware) | The status of the domain's service software\. | 

### DomainStatusList<a name="configuration-api-datatypesdomainstatuslist"></a>

List that contains the status of each specified OpenSearch Service domain\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| DomainStatusList | [`DomainStatus`](#configuration-api-datatypes-domainstatus) | List that contains the status of each specified OpenSearch Service domain\. | 

### DryRunResults<a name="configuration-api-datatypes-dryrunresults"></a>

Results of a dry run performed in an update domain request\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| DeploymentType | String | The results of a dry run performed in an [UpdateDomainConfig](#configuration-api-actions-updatedomainconfig) operation\. Describes the type of deployment the update will cause\. One of four values: [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/configuration-api.html) | 
| Message | String |  A message corresponding to the deployment type\.  | 

### EBSOptions<a name="configuration-api-datatypes-ebsoptions"></a>

Container for the parameters required to enable EBS\-based storage for an OpenSearch Service domain\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| EBSEnabled | Boolean | Indicates whether EBS volumes are attached to data nodes in an OpenSearch Service domain\. | 
| VolumeType | String | Specifies the type of EBS volumes attached to data nodes\.  | 
| VolumeSize | String | Specifies the size \(in GiB\) of EBS volumes attached to data nodes\. | 
| Iops | String | Specifies the baseline input/output \(I/O\) performance of EBS volumes attached to data nodes\. Applicable only for the provisioned IOPS EBS volume type\. | 

### EncryptionAtRestOptions<a name="configuration-api-datatypes-encryptionatrest"></a>

Specifies whether the domain should encrypt data at rest, and if so, the AWS Key Management Service \(KMS\) key to use\. Can be used only to create a new domain, not update an existing one\. To learn more, see [Enabling encryption of data at rest](encryption-at-rest.md#enabling-ear)\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| Enabled | Boolean | Specify true to enable encryption at rest\. | 
| KmsKeyId | String | The KMS key ID\. Takes the form 1a2a3a4\-1a2a\-3a4a\-5a6a\-1a2a3a4a5a6a\. | 

### EndpointsMap<a name="configuration-api-datatypes-endpointsmap"></a>

The key\-value pair that contains the VPC endpoint\. Only exists if the OpenSearch Service domain resides in a VPC\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| Endpoints | Key\-value string pair: "vpc": "<VPC\_ENDPOINT>" | The VPC endpoint for the domain\. | 

### Filters<a name="configuration-api-datatypes-filters"></a>

Filters the packages included in a [DescribePackages](#configuration-api-actions-describepackages) response\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| Name | String | Any field from [PackageDetails](#configuration-api-datatypes-packagedetails)\. | 
| Value | List | A list of values for the specified field\. | 

### LogPublishingOptions<a name="configuration-api-datatypes-logpublishingoptions"></a>

Specifies whether the OpenSearch Service domain publishes the OpenSearch application and slow logs to Amazon CloudWatch\. You still have to enable the *collection* of slow logs using the OpenSearch REST API\. To learn more, see [Setting OpenSearch logging thresholds for slow logs](createdomain-configure-slow-logs.md#createdomain-configure-slow-logs-indices)\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| INDEX\_SLOW\_LOGS | Key\-value | Two key\-value pairs that define the CloudWatch log group and whether the OpenSearch index slow log should be published there: <pre>"CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:264071961897:log-group:sample-domain",<br />"Enabled":true</pre> | 
| SEARCH\_SLOW\_LOGS | Key\-value | Two key\-value pairs that define the CloudWatch log group and whether the OpenSearch search slow log should be published there: <pre>"CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:264071961897:log-group:sample-domain",<br />"Enabled":true</pre> | 
| ES\_APPLICATION\_LOGS | Key\-value | Two key\-value pairs that define the CloudWatch log group and whether the OpenSearch error logs should be published there:<pre>"CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:264071961897:log-group:sample-domain",<br />"Enabled":true</pre> | 

### MasterUserOptions<a name="configuration-api-datatypes-masteruser"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| MasterUserARN | String | ARN for the master user\. Only specify if InternalUserDatabaseEnabled is false in [AdvancedSecurityOptions](#configuration-api-datatypes-advancedsec)\. | 
| MasterUserName | String | Username for the master user\. Only specify if InternalUserDatabaseEnabled is true in [AdvancedSecurityOptions](#configuration-api-datatypes-advancedsec)\. | 
| MasterUserPassword | String | Password for the master user\. Only specify if InternalUserDatabaseEnabled is true in [AdvancedSecurityOptions](#configuration-api-datatypes-advancedsec)\. | 

### NodeToNodeEncryptionOptions<a name="configuration-api-datatypes-node-to-node"></a>

Enables or disables node\-to\-node encryption\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| Enabled | Boolean | Enable with true\. | 

### OptionState<a name="configuration-api-datatypes-optionsstate"></a>

State of an update to advanced options for an OpenSearch Service domain\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| OptionStatus | String | One of three valid values:[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/configuration-api.html) | 

### OptionStatus<a name="configuration-api-datatypes-optionstatus"></a>

Status of an update to configuration options for an OpenSearch Service domain\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| CreationDate | Timestamp | Date and time when the OpenSearch Service domain was created\. | 
| UpdateDate | Timestamp | Date and time when the OpenSearch Service domain was updated\. | 
| UpdateVersion | Integer | Whole number that specifies the latest version for the entity\. | 
| State | [`OptionState`](#configuration-api-datatypes-optionsstate) | State of an update to configuration options for an OpenSearch Service domain\. | 
| PendingDeletion | Boolean | Indicates whether the service is processing a request to permanently delete the OpenSearch Service domain and all of its resources\. | 

### PackageDetails<a name="configuration-api-datatypes-packagedetails"></a>

Basic information about a package\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| CreatedAt | Timestamp | The time the package was created\. | 
| ErrorDetails | String | Additional information if the package is in an error state\. Null otherwise\. | 
| PackageDescription | String | User\-specified description of the package\. | 
| PackageID | String | Internal ID of the package\. | 
| PackageName | String | User\-specified name of the package\. | 
| PackageStatus | String | Values are COPYING, COPY\_FAILED, AVAILABLE, DELETING, or DELETE\_FAILED \. | 
| PackageType | String | Currently supports only TXT\-DICTIONARY\. | 

### PackageSource<a name="configuration-api-datatypes-packagesource"></a>

Bucket and key for the package you want to add to OpenSearch Service\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| S3BucketName | String | Name of the bucket containing the package\. | 
| S3Key | String | Key \(file name\) of the package\. | 

### SAMLOptions<a name="configuration-api-datatypes-saml"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| Enabled | Boolean | Whether to enable SAML authentication for OpenSearch Dashboards\. | 
| MasterUserName | String | This username from the SAML IdP receives full permissions to the cluster, equivalent to a [new master user](fgac.md#fgac-more-masters)\. | 
| MasterBackendRole | String | This backend role from the SAML IdP receives full permissions to the cluster, equivalent to a [new master user](fgac.md#fgac-more-masters)\. | 
| Idp | Object |  Container for information from your identity provider\. Contains two elements: <pre>"Idp": {<br />  "EntityId": "entity-id",<br />  "MetadataContent": "metadata-content-with-quotes-escaped"<br />}</pre>  | 
| RolesKey | String | Element of the SAML assertion to use for backend roles\. Default is roles\. | 
| SubjectKey | String | Element of the SAML assertion to use for username\. Default is NameID\. | 
| SessionTimeoutMinutes | Integer | Duration of a session in minutes after a user logs in\. Default is 60\. Maximum value is 1,440 \(24 hours\)\. | 

### ServiceSoftwareOptions<a name="configuration-api-datatypes-servicesoftware"></a>

Container for the state of your domain relative to the latest service software\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| UpdateAvailable | Boolean | Whether a service software update is available for your domain\. | 
| Cancellable | Boolean | If you have requested a domain update, whether or not you can cancel the update\. | 
| AutomatedUpdateDate | Timestamp | The Epoch time that the deployment window closes for required updates\. After this time, OpenSearch Service schedules the software upgrade automatically\. | 
| UpdateStatus | String | The status of the update\. Values are ELIGIBLE, PENDING\_UPDATE, IN\_PROGRESS, COMPLETED, and NOT\_ELIGIBLE\. | 
| Description | String | More detailed description of the status\. | 
| CurrentVersion | String | Your current service software version\. | 
| NewVersion | String | The latest service software version\. | 
| OptionalDeployment | Boolean | Whether the service software update is optional\. | 

### ServiceURL<a name="configuration-api-datatypes-serviceurl"></a>

Domain\-specific endpoint used to submit index, search, and data upload requests to an OpenSearch Service domain\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| ServiceURL | String | Domain\-specific endpoint used to submit index, search, and data upload requests to an OpenSearch Service domain\. | 

### SnapshotOptions<a name="configuration-api-datatypes-snapshotoptions"></a>

**DEPRECATED**\. See [Creating index snapshots in Amazon OpenSearch Service](managedomains-snapshots.md)\. Container for parameters required to configure the time of daily automated snapshots of the indices in an OpenSearch Service domain\.


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| AutomatedSnapshotStartHour | Integer | DEPRECATED\. Hour during which the service takes an automated daily snapshot of the indices in the OpenSearch Service domain\. | 

### Tag<a name="configuration-api-datatypes-tag"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| Key | [`TagKey`](#configuration-api-datatypes-tagkey) | Required name of the tag\. Tag keys must be unique for the OpenSearch Service domain to which they are attached\. For more information, see [Tagging Amazon OpenSearch Service domains](managedomains-awsresourcetagging.md)\. | 
| Value | [`TagValue`](#configuration-api-datatypes-tagvalue) | Optional string value of the tag\. Tag values can be null and do not have to be unique in a tag set\. For example, you can have a key\-value pair in a tag set of project/Trinity and cost\-center/Trinity\.  | 

### TagKey<a name="configuration-api-datatypes-tagkey"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| Key | String | Name of the tag\. String can have up to 128 characters\. | 

### TagList<a name="configuration-api-datatypes-taglist"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| Tag | [`Tag`](#configuration-api-datatypes-tag) | Resource tag attached to an OpenSearch Service domain\. | 

### TagValue<a name="configuration-api-datatypes-tagvalue"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| Value | String | Holds the value for a TagKey\. String can have up to 256 characters\. | 

### VPCDerivedInfo<a name="configuration-api-datatypes-vpcderivedinfo"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| VPCId | String | The ID for your VPC\. Amazon VPC generates this value when you create a VPC\. | 
| SubnetIds | StringList | A list of subnet IDs associated with the VPC endpoints for the domain\. For more information, see [VPCs and subnets](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_Subnets.html) in the Amazon VPC User Guide\. | 
| AvailabilityZones | StringList | The list of Availability Zones associated with the VPC subnets\. For more information, see [VPC and subnet basics](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_Subnets.html#vpc-subnet-basics) in the Amazon VPC User Guide\. | 
| SecurityGroupIds | StringList | The list of security group IDs associated with the VPC endpoints for the domain\. For more information, see [Security groups for your VPC](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_SecurityGroups.html) in the Amazon VPC User Guide\. | 

### VPCOptions<a name="configuration-api-datatypes-vpcoptions"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| SubnetIds | StringList | A list of subnet IDs associated with the VPC endpoints for the domain\. If your domain uses multiple Availability Zones, you need to provide two subnet IDs, one per zone\. Otherwise, provide only one\. To learn more, see [VPCs and subnets](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_Subnets.html) in the Amazon VPC User Guide\. | 
| SecurityGroupIds | StringList | The list of security group IDs associated with the VPC endpoints for the domain\. If you do not provide a security group ID, OpenSearch Service uses the default security group for the VPC\. To learn more, see [Security groups for your VPC](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_SecurityGroups.html) in the Amazon VPC User Guide\. | 
| VPCId | String | ID for the VPC\. | 

### ZoneAwarenessConfig<a name="configuration-api-datatypes-az"></a>


****  

| Field | Data type | Description | 
| --- | --- | --- | 
| AvailabilityZoneCount | Integer | If you enabled multiple Availability Zones, this field is the number of zones that you want the domain to use\. Valid values are 2 and 3\. | 

## Errors<a name="configuration-api-errors"></a>

OpenSearch Service throws the following errors:


****  

| Exception | Description | 
| --- | --- | 
| <a name="configuration-api-errors-baseexception"></a>BaseException | Thrown for all service errors\. Contains the HTTP status code of the error\. | 
| <a name="configuration-api-errors-validationexception"></a>ValidationException | Thrown when the HTTP request contains invalid input or is missing required input\. Returns HTTP status code 400\. | 
| <a name="configuration-api-errors-disabledoperation"></a>DisabledOperationException | Thrown when the client attempts to perform an unsupported operation\. Returns HTTP status code 409\. | 
| <a name="configuration-api-errors-internal"></a>InternalException | Thrown when an error internal to the service occurs while processing a request\. Returns HTTP status code 500\. | 
| <a name="configuration-api-errors-invalidtype"></a>InvalidTypeException | Thrown when trying to create or access an OpenSearch Service domain sub\-resource that is either invalid or not supported\. Returns HTTP status code 409\. | 
| <a name="configuration-api-errors-limitexceeded"></a>LimitExceededException | Thrown when trying to create more than the allowed number and type of OpenSearch Service domain resources and sub\-resources\. Returns HTTP status code 409\. | 
| <a name="configuration-api-errors-resourcenotfound"></a>ResourceNotFoundException | Thrown when accessing or deleting a resource that does not exist\. Returns HTTP status code 400\. | 
| <a name="configuration-api-errors-resourcealreadyexists"></a>ResourceAlreadyExistsException | Thrown when a client attempts to create a resource that already exists in an OpenSearch Service domain\. Returns HTTP status code 400\. | 