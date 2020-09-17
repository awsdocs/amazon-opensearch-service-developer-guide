# Amazon Elasticsearch Service Configuration API Reference<a name="es-configuration-api"></a>

This reference describes the actions, data types, and errors in the Amazon Elasticsearch Service Configuration API\. The configuration API is a REST API that you can use to create and configure Amazon ES domains over HTTP\. You also can use the AWS CLI and the console to configure Amazon ES domains\. For more information, see [Creating and Configuring Amazon ES Domains](es-createupdatedomains.md)\.
+ [Actions](#es-configuration-api-actions)
+ [Data Types](#es-configuration-api-datatypes)
+ [Errors](#es-configuration-api-errors)

## Actions<a name="es-configuration-api-actions"></a>

The following table provides a quick reference to the HTTP method required for each operation for the REST interface to the Amazon Elasticsearch Service configuration API\. The description of each operation also includes the required HTTP method\.

**Note**  
All configuration service requests must be signed\. For more information, see [Signing Amazon Elasticsearch Service Requests](es-ac.md#es-managedomains-signing-service-requests) in this guide and [Signature Version 4 Signing Process](http://docs.aws.amazon.com/general/latest/gr/signature-version-4.html) in the *AWS General Reference*\.


****  

| Action | HTTP Method | 
| --- | --- | 
| [AcceptInboundCrossClusterSearchConnection](#es-configuration-api-actions-accept-inbound-cross-cluster-search-connection) | PUT | 
| [AddTags](#es-configuration-api-actions-addtags) | POST | 
| [AssociatePackage](#es-configuration-api-actions-associatepackage) | POST | 
| [CreateElasticsearchDomain](#es-configuration-api-actions-createelasticsearchdomain) | POST | 
| [CreateOutboundCrossClusterSearchConnection](#es-configuration-api-actions-create-outbound-cross-cluster-search-connection) | POST | 
| [CreatePackage](#es-configuration-api-actions-createpackage) | POST | 
| [DeleteElasticsearchDomain](#es-configuration-api-actions-deleteelasticsearchdomain) | DELETE | 
| [DeleteElasticsearchServiceRole](#es-configuration-api-actions-deleteelasticsearchservicerole) | DELETE | 
| [DeleteInboundCrossClusterSearchConnection](#es-configuration-api-actions-delete-inbound-cross-cluster-search-connection) | DELETE | 
| [DeleteOutboundCrossClusterSearchConnection](#es-configuration-api-actions-delete-outbound-cross-cluster-search-connection) | DELETE | 
| [DeletePackage](#es-configuration-api-actions-deletepackage) | DELETE | 
| [DescribeElasticsearchDomain](#es-configuration-api-actions-describeelasticsearchdomain) | GET | 
| [DescribeElasticsearchDomainConfig](#es-configuration-api-actions-describeelasticsearchdomainconfig) | GET | 
| [DescribeElasticsearchDomains](#es-configuration-api-actions-describeesdomains) | POST | 
| [DescribeElasticsearchInstanceTypeLimits](#es-configuration-api-actions-describeinstancetypelimits) | GET | 
| [DescribeInboundCrossClusterSearchConnections](#es-configuration-api-actions-describe-inbound-cross-cluster-search-connections) | POST | 
| [DescribeOutboundCrossClusterSearchConnections](#es-configuration-api-actions-describe-outbound-cross-cluster-search-connections) | POST | 
| [DescribePackages](#es-configuration-api-actions-describepackages) | POST | 
| [DescribeReservedElasticsearchInstanceOfferings](#es-configuration-api-actions-describereservedelasticsearchinstanceofferings) | GET | 
| [DescribeReservedElasticsearchInstances](#es-configuration-api-actions-describereservedelasticsearchinstances) | GET | 
| [DissociatePackage](#es-configuration-api-actions-dissociatepackage) | POST | 
| [GetCompatibleElasticsearchVersions](#es-configuration-api-actions-get-compat-vers) | GET | 
| [GetUpgradeHistory](#es-configuration-api-actions-get-upgrade-hist) | GET | 
| [GetUpgradeStatus](#es-configuration-api-actions-get-upgrade-stat) | GET | 
| [ListDomainNames](#es-configuration-api-actions-listdomainnames) | GET | 
| [ListDomainsForPackage](#es-configuration-api-actions-listdomainsforpackage) | GET | 
| [ListElasticsearchInstanceTypeDetails](#es-configuration-api-actions-listelasticsearchinstancetypedetails) | GET | 
| [ListElasticsearchInstanceTypes](#es-configuration-api-actions-listelasticsearchinstancetypes) | GET | 
| [ListElasticsearchVersions](#es-configuration-api-actions-listelasticsearchversions) | GET | 
| [ListPackagesForDomain](#es-configuration-api-actions-listpackagesfordomain) | GET | 
| [ListTags](#es-configuration-api-actions-listtags) | GET | 
| [`PurchaseReservedElasticsearchInstanceOffering`](#es-configuration-api-actions-purchasereservedelasticsearchinstance) | POST | 
| [RejectInboundCrossClusterSearchConnection](#es-configuration-api-actions-reject-inbound-cross-cluster-search-connection) | PUT | 
| [RemoveTags](#es-configuration-api-actions-removetags) | POST | 
| [StartElasticsearchServiceSoftwareUpdate](#es-configuration-api-actions-startupdate) | POST | 
| [StopElasticsearchServiceSoftwareUpdate](#es-configuration-api-actions-stopupdate) | POST | 
| [UpdateElasticsearchDomainConfig](#es-configuration-api-actions-updateelasticsearchdomainconfig) | POST | 
| [UpgradeElasticsearchDomain](#es-configuration-api-actions-upgrade-domain) | POST | 

### AcceptInboundCrossClusterSearchConnection<a name="es-configuration-api-actions-accept-inbound-cross-cluster-search-connection"></a>

Allows the destination domain owner to accept an inbound cross\-cluster search connection request\.

#### Syntax<a name="w32aac33b7b9b5"></a>

```
PUT https://es.us-east-1.amazonaws.com/2015-01-01/es/ccs/inboundConnection/{ConnectionId}/accept
```

#### Request Parameters<a name="w32aac33b7b9b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w32aac33b7b9b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7b9c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| CrossClusterSearchConnection | Object | Inbound connection details\. | 

### AddTags<a name="es-configuration-api-actions-addtags"></a>

Attaches resource tags to an Amazon ES domain\. For more information, see [Tagging Amazon ES Domains](es-managedomains-awsresourcetagging.md)\.

#### Syntax<a name="es-configuration-api-actions-addtags-s"></a>

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/tags
{
  "ARN": "domain-arn",
  "TagList": [{
    "Key": "tag-key",
    "Value": "tag-value"
  }]
}
```

#### Request Parameters<a name="es-configuration-api-actions-addtags-p"></a>

This operation does not use request parameters\.

#### Request Body<a name="es-configuration-api-actions-addtags-b"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| TagList | [TagList](#es-configuration-api-datatypes-taglist) | Yes | List of resource tags\. | 
| ARN | [ARN](#es-configuration-api-datatypes-arn) | Yes | Amazon Resource Name \(ARN\) for the Amazon ES domain to which you want to attach resource tags\. | 

#### Response Elements<a name="es-configuration-api-actions-addtags-r"></a>

The `AddTags` operation does not return a data structure\.

### AssociatePackage<a name="es-configuration-api-actions-associatepackage"></a>

Associates a package with an Amazon ES domain\.

#### Syntax<a name="es-configuration-api-actions-associatepackage-s"></a>

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/packages/associate/package-id/domain-name
```

#### Request Parameters<a name="es-configuration-api-actions-associatepackage-p"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| PackageID | String | Yes | Internal ID of the package that you want to associate with a domain\. Use [DescribePackages](#es-configuration-api-actions-describepackages) to find this value\. | 
| DomainName | [DomainName](#es-configuration-api-datatypes-domainname) | Yes | Name of the domain that you want to associate the package with\. | 

#### Request Body<a name="es-configuration-api-actions-associatepackage-b"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="es-configuration-api-actions-associatepackage-r"></a>


****  

| Field | Data Type | 
| --- | --- | 
| DomainPackageDetails | [DomainPackageDetails](#es-configuration-api-datatypes-domainpackagedetails) | 

### CreateElasticsearchDomain<a name="es-configuration-api-actions-createelasticsearchdomain"></a>

Creates an Amazon ES domain\. For more information, see [ Creating Amazon ES Domains](es-createupdatedomains.md#es-createdomains)\.

**Note**  
If you attempt to create an Amazon ES domain and a domain with the same name already exists, the API does not report an error\. Instead, it returns details for the existing domain\.

#### Syntax<a name="es-configuration-api-actions-createelasticsearchdomain-s"></a>

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/es/domain
{
  "ElasticsearchClusterConfig": {
    "ZoneAwarenessConfig": {
      "AvailabilityZoneCount": 3
    },
    "ZoneAwarenessEnabled": true|false,
    "InstanceCount": 3,
    "DedicatedMasterEnabled": true|false,
    "DedicatedMasterType": "c5.large.elasticsearch",
    "DedicatedMasterCount": 3,
    "InstanceType": "r5.large.elasticsearch",
    "WarmCount": 3,
    "WarmEnabled": true|false,
    "WarmType": "ultrawarm1.large.elasticsearch"
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
  "SnapshotOptions": {
    "AutomatedSnapshotStartHour": 3
  },
  "VPCOptions": {
    "VPCId": "vpc-12345678",
    "SubnetIds": ["subnet-abcdefg1", "subnet-abcdefg2", "subnet-abcdefg3"],
    "SecurityGroupIds": ["sg-12345678"]
  },
  "AdvancedOptions": {
    "rest.action.multi.allow_explicit_index": "true|false",
    "indices.fielddata.cache.size": "40",
    "indices.query.bool.max_clause_count": "1024"
  },
  "CognitoOptions": {
    "Enabled": true|false,
    "UserPoolId": "us-east-1_121234567",
    "IdentityPoolId": "us-east-1:12345678-1234-1234-1234-123456789012",
    "RoleArn": "arn:aws:iam::123456789012:role/service-role/CognitoAccessForAmazonES"
  },
  "NodeToNodeEncryptionOptions": {
    "Enabled": true|false
  },
  "DomainEndpointOptions": {
    "EnforceHTTPS": true|false,
    "TLSSecurityPolicy": "Policy-Min-TLS-1-2-2019-07|Policy-Min-TLS-1-0-2019-07"
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
  "ElasticsearchVersion": "7.1",
  "DomainName": "my-domain",
  "AccessPolicies": "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"123456789012\"]},\"Action\":[\"es:es:ESHttp*\"],\"Resource\":\"arn:aws:es:us-east-1:123456789012:domain/my-domain/*\"}]}"
}
```

#### Request Parameters<a name="es-configuration-api-actions-createelasticsearchdomain-p"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="es-configuration-api-actions-createelasticsearchdomain-b"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [DomainName](#es-configuration-api-datatypes-domainname) | Yes | Name of the Amazon ES domain to create\. | 
| ElasticsearchVersion | String | No | Version of Elasticsearch\. If not specified, 1\.5 is used as the default\. For the full list of supported versions, see [Supported Elasticsearch Versions](what-is-amazon-elasticsearch-service.md#aes-choosing-version)\. | 
| ElasticsearchClusterConfig | [ElasticsearchClusterConfig](#es-configuration-api-datatypes-elasticsearchclusterconfig) | No | Container for the cluster configuration of an Amazon ES domain\. | 
| EBSOptions | [EBSOptions](#es-configuration-api-datatypes-ebsoptions) | No | Container for the parameters required to enable EBS\-based storage for an Amazon ES domain\. | 
| VPCOptions | [VPCOptions](#es-configuration-api-datatypes-vpcoptions) | No | Container for the values required to configure VPC access domains\. If you don't specify these values, Amazon ES creates the domain with a public endpoint\. To learn more, see [VPC Support for Amazon Elasticsearch Service Domains](es-vpc.md)\. | 
| CognitoOptions | [CognitoOptions](#es-configuration-api-datatypes-cognitooptions) | No | Key\-value pairs to configure Amazon ES to use Amazon Cognito authentication for Kibana\. | 
| AccessPolicies | String | No | IAM policy document specifying the access policies for the new Amazon ES domain\. For more information, see [Identity and Access Management in Amazon Elasticsearch Service](es-ac.md)\. | 
| SnapshotOptions | [SnapshotOptions](#es-configuration-api-datatypes-snapshotoptions) | No | **DEPRECATED**\. For domains running Elasticsearch 5\.3 and later, Amazon ES takes hourly automated snapshots, making this setting irrelevant\.For domains running earlier versions of Elasticsearch, Amazon ES takes daily automated snapshots\. This value acts as a container for the hour of the day at which you want the service to take the snapshot\. | 
| AdvancedOptions | [AdvancedOptions](#es-configuration-api-datatypes-advancedoptions) | No | Key\-value pairs to specify advanced configuration options\. For more information, see [Configuring Advanced Options](es-createupdatedomains.md#es-createdomain-configure-advanced-options)\. | 
| LogPublishingOptions | [LogPublishingOptions](#es-configuration-api-datatypes-logpublishingoptions) | No | Key\-value pairs to configure slow log publishing\. | 
| EncryptionAtRestOptions | [EncryptionAtRestOptions](#es-configuration-api-datatypes-encryptionatrest) | No | Key\-value pairs to enable encryption at rest\. | 
| NodeToNodeEncryptionOptions | [NodeToNodeEncryptionOptions](#es-configuration-api-datatypes-node-to-node) | No | Enables node\-to\-node encryption\. | 
| DomainEndpointOptions | [DomainEndpointOptions](#es-configuration-api-datatypes-domainendpointoptions) | No | Additional options for the domain endpoint, such as whether to require HTTPS for all traffic\. | 
| AdvancedSecurityOptions | [AdvancedSecurityOptions](#es-configuration-api-datatypes-advancedsec) | No | Options for fine\-grained access control\. | 

#### Response Elements<a name="es-configuration-api-actions-createelasticsearchdomain-r"></a>


****  

| Field | Data Type | 
| --- | --- | 
| DomainStatus | [ElasticsearchDomainStatus](#es-configuration-api-datatypes-elasticsearchdomainstatus) | 

### CreateOutboundCrossClusterSearchConnection<a name="es-configuration-api-actions-create-outbound-cross-cluster-search-connection"></a>

Creates a new cross\-cluster search connection from a source domain to a destination domain\.

#### Syntax<a name="w32aac33b7c17b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/es/ccs/outboundConnection
{
  "ConnectionAlias": "StringValue",
  "SourceDomainInfo": {
    "DomainName": "Domain-name",
    "Region": "us-east-1"
  },
  "DestinationDomainInfo": {
    "OwnerId": "Account-id",
    "DomainName": "Domain-name",
    "Region": "us-east-1"
  }
}
```

#### Request Parameters<a name="w32aac33b7c17b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w32aac33b7c17b9"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| ConnectionAlias | String | Yes | Name of the connection\. | 
| SourceDomainInfo | Object | Yes | Name and region of the source domain\. | 
| DestinationDomainInfo | Object | Yes | Name and region of the destination domain\. | 

#### Response Elements<a name="w32aac33b7c17c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| SourceDomainInfo | Object | Name and region of the source domain\. | 
| DestinationDomainInfo | Object | Name and region of the destination domain\. | 
| ConnectionAlias | String | Name of the connection\. | 
| ConnectionStatus | String | The status of the connection\. | 
| CrossClusterSearchConnectionId | String | The ID for the outbound connection\. | 

### CreatePackage<a name="es-configuration-api-actions-createpackage"></a>

Add a package for use with Amazon ES domains\.

#### Syntax<a name="es-configuration-api-actions-createpackage-s"></a>

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/packages
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

#### Request Parameters<a name="es-configuration-api-actions-createpackage-p"></a>

This operation does not use request parameters\.

#### Request Body<a name="es-configuration-api-actions-createpackage-b"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| PackageName | String | Yes | Unique name for the package\. | 
| PackageType | String | Yes | Type of package\. Currently supports only TXT\-DICTIONARY\. | 
| PackageDescription | String | No | Description of the package\. | 
| PackageSource | [PackageSource](#es-configuration-api-datatypes-packagesource) | Yes | S3 bucket and key for the package\. | 

#### Response Elements<a name="es-configuration-api-actions-createpackage-r"></a>


****  

| Field | Data Type | 
| --- | --- | 
| PackageDetails | [PackageDetails](#es-configuration-api-datatypes-packagedetails) | 

### DeleteElasticsearchDomain<a name="es-configuration-api-actions-deleteelasticsearchdomain"></a>

Deletes an Amazon ES domain and all of its data\. A domain cannot be recovered after it is deleted\.

#### Syntax<a name="es-configuration-api-actions-deleteelasticsearchdomain-s"></a>

```
DELETE https://es.us-east-1.amazonaws.com/2015-01-01/es/domain/domain-name
```

#### Request Parameters<a name="es-configuration-api-actions-deleteelasticsearchdomain-p"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [DomainName](#es-configuration-api-datatypes-domainname) | Yes | Name of the Amazon ES domain that you want to delete\. | 

#### Request Body<a name="es-configuration-api-actions-deleteelasticsearchdomain-b"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="es-configuration-api-actions-deleteelasticsearchdomain-r"></a>


****  

| Field | Data Type | 
| --- | --- | 
| DomainStatus | [ElasticsearchDomainStatus](#es-configuration-api-datatypes-elasticsearchdomainstatus) | 

### DeleteElasticsearchServiceRole<a name="es-configuration-api-actions-deleteelasticsearchservicerole"></a>

Deletes the service\-linked role between Amazon ES and Amazon EC2\. This role gives Amazon ES permissions to place VPC endpoints into your VPC\. A service\-linked role must be in place for domains with VPC endpoints to be created or function properly\.

**Note**  
This action succeeds only if no domains are using the service\-linked role\.

#### Syntax<a name="es-configuration-api-actions-deleteelasticsearchservicerole-s"></a>

```
DELETE https://es.us-east-1.amazonaws.com/2015-01-01/es/role
```

#### Request Parameters<a name="es-configuration-api-actions-deleteelasticsearchservicerole-p"></a>

This operation does not use request parameters\.

#### Request Body<a name="es-configuration-api-actions-deleteelasticsearchservicerole-b"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="es-configuration-api-actions-deleteelasticsearchservicerole-r"></a>

The `DeleteElasticsearchServiceRole` operation does not return a data structure\.

### DeleteInboundCrossClusterSearchConnection<a name="es-configuration-api-actions-delete-inbound-cross-cluster-search-connection"></a>

Allows the destination domain owner to delete an existing inbound cross\-cluster search connection\.

#### Syntax<a name="w32aac33b7c25b5"></a>

```
DELETE https://es.us-east-1.amazonaws.com/2015-01-01/es/ccs/inboundConnection/{ConnectionId}
```

#### Request Parameters<a name="w32aac33b7c25b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w32aac33b7c25b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7c25c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| CrossClusterSearchConnection | Object | Inbound connection details\. | 

### DeleteOutboundCrossClusterSearchConnection<a name="es-configuration-api-actions-delete-outbound-cross-cluster-search-connection"></a>

Allows the source domain owner to delete an existing outbound cross\-cluster search connection\.

#### Syntax<a name="w32aac33b7c27b5"></a>

```
DELETE https://es.us-east-1.amazonaws.com/2015-01-01/es/ccs/outboundConnection/{ConnectionId}
```

#### Request Parameters<a name="w32aac33b7c27b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w32aac33b7c27b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7c27c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| CrossClusterSearchConnection | Object | Outbound connection details\. | 

### DeletePackage<a name="es-configuration-api-actions-deletepackage"></a>

Deletes a package from Amazon ES\. The package must not be associated with any Amazon ES domain\. 

#### Syntax<a name="es-configuration-api-actions-deletepackage-s"></a>

```
DELETE https://es.us-east-1.amazonaws.com/2015-01-01/packages/package-id
```

#### Request Parameters<a name="es-configuration-api-actions-deletepackage-p"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| PackageID | String | Yes | Internal ID of the package that you want to delete\. Use [DescribePackages](#es-configuration-api-actions-describepackages) to find this value\. | 

#### Request Body<a name="es-configuration-api-actions-deletepackage-b"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="es-configuration-api-actions-deletepackage-r"></a>


****  

| Field | Data Type | 
| --- | --- | 
| PackageDetails | [PackageDetails](#es-configuration-api-datatypes-packagedetails) | 

### DescribeElasticsearchDomain<a name="es-configuration-api-actions-describeelasticsearchdomain"></a>

Describes the domain configuration for the specified Amazon ES domain, including the domain ID, domain service endpoint, and domain ARN\.

#### Syntax<a name="w32aac33b7c31b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2015-01-01/es/domain/domain-name
```

#### Request Parameters<a name="w32aac33b7c31b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [DomainName](#es-configuration-api-datatypes-domainname) | Yes | Name of the Amazon ES domain that you want to describe\. | 

#### Request Body<a name="w32aac33b7c31b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7c31c11"></a>


****  

| Field | Data Type | 
| --- | --- | 
| DomainStatus | [ElasticsearchDomainStatus](#es-configuration-api-datatypes-elasticsearchdomainstatus) | 

### DescribeElasticsearchDomainConfig<a name="es-configuration-api-actions-describeelasticsearchdomainconfig"></a>

Displays the configuration of an Amazon ES domain\.

#### Syntax<a name="w32aac33b7c33b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2015-01-01/es/domain/domain-name/config
```

#### Request Parameters<a name="w32aac33b7c33b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [DomainName](#es-configuration-api-datatypes-domainname) | Yes | Name of the Amazon ES domain configuration that you want to describe\. | 

#### Request Body<a name="w32aac33b7c33b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7c33c11"></a>


****  

| Field | Data Type | 
| --- | --- | 
| DomainConfig | [ElasticsearchDomainConfig](#es-configuration-api-datatypes-esdomainconfig) | 

### DescribeElasticsearchDomains<a name="es-configuration-api-actions-describeesdomains"></a>

Describes the domain configuration for up to five specified Amazon ES domains\. Information includes the domain ID, domain service endpoint, and domain ARN\.

#### Syntax<a name="w32aac33b7c35b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/es/domain-info
{
  "DomainNames": [
    "domain-name1",
    "domain-name2",
  ]
}
```

#### Request Parameters<a name="w32aac33b7c35b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w32aac33b7c35b9"></a>


****  

| Field | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainNames | [DomainNameList](#es-configuration-api-datatypes-domainnamelist) | Yes | Array of Amazon ES domain names\. | 

#### Response Elements<a name="w32aac33b7c35c11"></a>


****  

| Field | Data Type | 
| --- | --- | 
| DomainStatusList | [ElasticsearchDomainStatusList](#es-configuration-api-datatypes-esdomainstatuslist) | 

### DescribeElasticsearchInstanceTypeLimits<a name="es-configuration-api-actions-describeinstancetypelimits"></a>

Describes the instance count, storage, and master node limits for a given Elasticsearch version and instance type\.

#### Syntax<a name="w32aac33b7c37b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2015-01-01/es/instanceTypeLimits/elasticsearch-version/instance-type?domainName=domain-name
```

#### Request Parameters<a name="w32aac33b7c37b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| ElasticsearchVersion | String | Yes | Elasticsearch version\. For a list of supported versions, see [Supported Elasticsearch Versions](what-is-amazon-elasticsearch-service.md#aes-choosing-version)\. | 
| InstanceType | String | Yes | Instance type\. To view instance types by Region, see [Amazon Elasticsearch Service Pricing](https://aws.amazon.com/elasticsearch-service/pricing/)\. | 
| DomainName | [DomainName](#es-configuration-api-datatypes-domainname) | No | The name of an existing domain\. Only specify if you need the limits for an existing domain\. | 

#### Request Body<a name="w32aac33b7c37b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7c37c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| LimitsByRole | Map | Map that contains all applicable instance type limits\. "data" refers to data nodes\. "master" refers to dedicated master nodes\. | 

### DescribeInboundCrossClusterSearchConnections<a name="es-configuration-api-actions-describe-inbound-cross-cluster-search-connections"></a>

Lists all the inbound cross\-cluster search connections for a destination domain\.

#### Syntax<a name="w32aac33b7c39b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/es/ccs/inboundConnection/search
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

#### Request Parameters<a name="w32aac33b7c39b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w32aac33b7c39b9"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| Filters | Object | Yes | List of filter names and values that you can use for the "Describe" requests\. The following fields are supported: cross\-cluster\-search\-connection\-id, source\-domain\-info\.domain\-name, source\-domain\-info\.owner\-id, source\-domain\-info\.region, and destination\-domain\-info\.domain\-name\. | 
| MaxResults | Integer | No | Limits the number of results\. The default is 100\.  | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Response Elements<a name="w32aac33b7c39c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| CrossClusterSearchConnections | Object | List of inbound connections\. | 
| NextToken | String | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

### DescribeOutboundCrossClusterSearchConnections<a name="es-configuration-api-actions-describe-outbound-cross-cluster-search-connections"></a>

Lists all outbound cross\-cluster search connections for a source domain\.

#### Syntax<a name="w32aac33b7c41b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/es/ccs/outboundConnection/search
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

#### Request Parameters<a name="w32aac33b7c41b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w32aac33b7c41b9"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| Filters | Object | Yes | List of filter names and values that you can use for the "Describe" requests\. The following fields are supported: cross\-cluster\-search\-connection\-id, source\-domain\-info\.domain\-name, source\-domain\-info\.owner\-id, source\-domain\-info\.region, and destination\-domain\-info\.domain\-name  | 
| MaxResults | Integer | No | Limits the number of results\. The default is 100\.  | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Response Elements<a name="w32aac33b7c41c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| CrossClusterSearchConnections | Object | List of outbound connections\. | 
| NextToken | String | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

### DescribePackages<a name="es-configuration-api-actions-describepackages"></a>

Describes all packages available to Amazon ES\. Includes options for filtering, limiting the number of results, and pagination\.

#### Syntax<a name="es-configuration-api-actions-describepackages-s"></a>

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/packages/describe
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

#### Request Parameters<a name="es-configuration-api-actions-describepackages-p"></a>

This operation does not use request parameters\.

#### Request Body<a name="es-configuration-api-actions-describepackages-b"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| Filters | [Filters](#es-configuration-api-datatypes-filters) | No | Only returns packages that match the provided values\. | 
| MaxResults | Integer | No | Limits results to a maximum number of packages\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call includes a non\-null NextToken value\. If provided, returns results for the next page\. | 

#### Response Elements<a name="es-configuration-api-actions-describepackages-r"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| PackageDetailsList | List | List of [PackageDetails](#es-configuration-api-datatypes-packagedetails) objects\. | 

### DescribeReservedElasticsearchInstanceOfferings<a name="es-configuration-api-actions-describereservedelasticsearchinstanceofferings"></a>

Describes the available Reserved Instance offerings for a given Region\.

#### Syntax<a name="w32aac33b7c45b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2015-01-01/es/reservedInstanceOfferings?offeringId=offering-id&maxResults=max-results&nextToken=next-token
```

#### Request Parameters<a name="w32aac33b7c45b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| OfferingId | String | No | The offering ID\. | 
| MaxResults | Integer | No | Limits the number of results\. Must be between 30 and 100\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result that contains NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request Body<a name="w32aac33b7c45b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7c45c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ReservedElasticsearchInstanceOfferings | ReservedElasticsearchInstanceOfferings | Container for all information about a Reserved Instance offering\. For more information, see [Purchasing Reserved Instances \(AWS CLI\)](aes-ri.md#aes-ri-cli)\. | 

### DescribeReservedElasticsearchInstances<a name="es-configuration-api-actions-describereservedelasticsearchinstances"></a>

Describes the instance that you have reserved in a given Region\.

#### Syntax<a name="w32aac33b7c47b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2015-01-01/es/reservedInstances?reservationId=reservation-id&maxResults=max-results&nextToken=next-token
```

#### Request Parameters<a name="w32aac33b7c47b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| ReservationId | String | No | The reservation ID, assigned after you purchase a reservation\. | 
| MaxResults | Integer | No | Limits the number of results\. Must be between 30 and 100\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result that contains NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request Body<a name="w32aac33b7c47b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7c47c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ReservedElasticsearchInstances |  ReservedElasticsearchInstances  | Container for all information about the instance that you have reserved\. For more information, see [Purchasing Reserved Instances \(AWS CLI\)](aes-ri.md#aes-ri-cli)\. | 

### DissociatePackage<a name="es-configuration-api-actions-dissociatepackage"></a>

Removes the package from the specified Amazon ES domain\. The package must not be in use with any ES index for dissociate to succeed\. The package will still be available in the Amazon ES service for associating later\.

#### Syntax<a name="es-configuration-api-actions-dissociatepackage-s"></a>

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/packages/dissociate/package-id/domain-name
```

#### Request Parameters<a name="es-configuration-api-actions-dissociatepackage-p"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| PackageID | String | Yes | Internal ID of the package that you want to dissociate from the domain\. Use [ListPackagesForDomain](#es-configuration-api-actions-listpackagesfordomain) to find this value\. | 
| DomainName | [DomainName](#es-configuration-api-datatypes-domainname) | Yes | Name of the domain that you want to dissociate the package from\. | 

#### Request Body<a name="es-configuration-api-actions-dissociatepackage-b"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="es-configuration-api-actions-dissociatepackage-r"></a>


****  

| Field | Data Type | 
| --- | --- | 
| DomainPackageDetails | [DomainPackageDetails](#es-configuration-api-datatypes-domainpackagedetails) | 

### GetCompatibleElasticsearchVersions<a name="es-configuration-api-actions-get-compat-vers"></a>

Returns a map of Elasticsearch versions and the versions you can upgrade them to\.

#### Syntax<a name="w32aac33b7c51b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2015-01-01/es/compatibleVersions?domainName=domain-name
```

#### Request Parameters<a name="w32aac33b7c51b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [DomainName](#es-configuration-api-datatypes-domainname) | No | The name of an existing domain\. | 

#### Request Body<a name="w32aac33b7c51b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7c51c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| CompatibleElasticsearchVersions | Map | A map of Elasticsearch versions and the versions that you can upgrade them to:<pre>{<br />  "CompatibleElasticsearchVersions": [{<br />    "SourceVersion": "6.7",<br />    "TargetVersions": ["6.8"]<br />  }]<br />}</pre> | 

### GetUpgradeHistory<a name="es-configuration-api-actions-get-upgrade-hist"></a>

Returns a list of the domain's 10 most\-recent upgrade operations\.

#### Syntax<a name="w32aac33b7c53b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2015-01-01/es/upgradeDomain/domain-name/history?maxResults=max-results&amp;nextToken=next-token
```

#### Request Parameters<a name="w32aac33b7c53b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| MaxResults | Integer | No | Limits the number of results\. Must be between 30 and 100\. | 
| DomainName | [DomainName](#es-configuration-api-datatypes-domainname) | Yes | The name of an existing domain\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request Body<a name="w32aac33b7c53b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7c53c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| UpgradeHistoryList | UpgradeHistoryList | Container for result logs of the past 10 upgrade operations\. | 

### GetUpgradeStatus<a name="es-configuration-api-actions-get-upgrade-stat"></a>

Returns the most\-recent status of a domain's Elasticsearch version upgrade\.

#### Syntax<a name="w32aac33b7c55b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2015-01-01/es/upgradeDomain/domain-name/status
```

#### Request Parameters<a name="w32aac33b7c55b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [DomainName](#es-configuration-api-datatypes-domainname) | Yes | The name of an existing domain\. | 

#### Request Body<a name="w32aac33b7c55b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7c55c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| UpgradeStepItem | UpgradeStepItem | Container for the most\-recent status of a domain's version upgrade\. | 

### ListDomainNames<a name="es-configuration-api-actions-listdomainnames"></a>

Displays the names of all Amazon ES domains owned by the current user *in the active Region*\.

#### Syntax<a name="w32aac33b7c57b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2015-01-01/domain
```

#### Request Parameters<a name="w32aac33b7c57b7"></a>

This operation does not use request parameters\.

#### Request Body<a name="w32aac33b7c57b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7c57c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| DomainNameList | [DomainNameList](#es-configuration-api-datatypes-domainnamelist) | The names of all Amazon ES domains owned by the current user\. | 

### ListDomainsForPackage<a name="es-configuration-api-actions-listdomainsforpackage"></a>

Lists all Amazon ES domains that a package is associated with\.

#### Syntax<a name="es-configuration-api-actions-listdomainsforpackage-s"></a>

```
GET https://es.us-east-1.amazonaws.com/2015-01-01/packages/package-id/domains?maxResults=max-results&amp;nextToken=next-token
```

#### Request Parameters<a name="es-configuration-api-actions-listdomainsforpackage-p"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| PackageID | String | Yes | The package for which to list domains\. | 
| MaxResults | Integer | No | Limits the number of results\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result that contains NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request Body<a name="es-configuration-api-actions-listdomainsforpackage-b"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="es-configuration-api-actions-listdomainsforpackage-r"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| DomainPackageDetailsList | List | List of [DomainPackageDetails](#es-configuration-api-datatypes-domainpackagedetails) objects\. | 
| NextToken | String | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

### ListElasticsearchInstanceTypeDetails<a name="es-configuration-api-actions-listelasticsearchinstancetypedetails"></a>

Lists all Elasticsearch instance types that are supported for a given Elasticsearch version and the features that these instance types support\.

#### Syntax<a name="w32aac33b7c61b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2015-01-01/es/instanceTypeDetails/elasticsearch-version?domainName=domain-name&maxResults=max-results&nextToken=next-token
```

#### Request Parameters<a name="w32aac33b7c61b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| ElasticsearchVersion | String | Yes | The Elasticsearch version\. | 
| DomainName | String | No | The Amazon ES domain name\. | 
| MaxResults | Integer | No | Limits the number of results\. Must be between 30 and 100\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request Body<a name="w32aac33b7c61b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7c61c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ElasticsearchInstanceTypes | List | List of supported instance types for the given Elasticsearch version and the features that these instance types support\. | 
| NextToken | String |  Used for pagination\. Only necessary if a previous API call produced a result containing `NextToken`\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\.  | 

### ListElasticsearchInstanceTypes \(Deprecated\)<a name="es-configuration-api-actions-listelasticsearchinstancetypes"></a>

Lists all Elasticsearch instance types that are supported for a given Elasticsearch version\. This action is deprecated\. Use [ListElasticsearchInstanceTypeDetails](#es-configuration-api-actions-listelasticsearchinstancetypedetails) instead\.

#### Syntax<a name="w32aac33b7c63b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2015-01-01/es/instanceTypes/elasticsearch-version?domainName=domain-name&maxResults=max-results&nextToken=next-token
```

#### Request Parameters<a name="w32aac33b7c63b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| ElasticsearchVersion | String | Yes | The Elasticsearch version\. | 
| DomainName | String | No | The Amazon ES domain name\. | 
| MaxResults | Integer | No | Limits the number of results\. Must be between 30 and 100\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request Body<a name="w32aac33b7c63b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7c63c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ElasticsearchInstanceTypes | List | List of supported instance types for the given Elasticsearch version\. | 
| NextToken | String | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\.  | 

### ListElasticsearchVersions<a name="es-configuration-api-actions-listelasticsearchversions"></a>

Lists all supported Elasticsearch versions on Amazon ES\.

#### Syntax<a name="w32aac33b7c65b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2015-01-01/es/versions?maxResults=max-results&nextToken=next-token
```

#### Request Parameters<a name="w32aac33b7c65b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| MaxResults | Integer | No | Limits the number of results\. Must be between 30 and 100\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request Body<a name="w32aac33b7c65b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7c65c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ElasticsearchVersions | List | Lists all supported Elasticsearch versions\. | 
| NextToken | String | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\.  | 

### ListPackagesForDomain<a name="es-configuration-api-actions-listpackagesfordomain"></a>

Lists all packages associated with the Amazon ES domain\.

#### Syntax<a name="es-configuration-api-actions-listpackagesfordomain-s"></a>

```
GET https://es.us-east-1.amazonaws.com/2015-01-01/domain/domain-name/packages?maxResults=max-results&amp;nextToken=next-token
```

#### Request Parameters<a name="es-configuration-api-actions-listpackagesfordomain-p"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | String | Yes | The name of the domain for which you want to list associated packages\. | 
| MaxResults | Integer | No | Limits the number of results\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result that contains NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request Body<a name="es-configuration-api-actions-listpackagesfordomain-b"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="es-configuration-api-actions-listpackagesfordomain-r"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| DomainPackageDetailsList | List | List of [DomainPackageDetails](#es-configuration-api-datatypes-domainpackagedetails) objects\. | 
| NextToken | String | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page, and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

### ListTags<a name="es-configuration-api-actions-listtags"></a>

Displays all resource tags for an Amazon ES domain\.

#### Syntax<a name="w32aac33b7c69b5"></a>

```
GET https://es.us-east-1.amazonaws.com/2015-01-01/tags?arn=domain-arn
```

#### Request Parameters<a name="w32aac33b7c69b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| ARN | [`ARN`](#es-configuration-api-datatypes-arn) | Yes | Amazon Resource Name \(ARN\) for the Amazon ES domain\. | 

#### Request Body<a name="w32aac33b7c69b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7c69c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| TagList | [`TagList`](#es-configuration-api-datatypes-taglist) | List of resource tags\. For more information, see [Tagging Amazon Elasticsearch Service Domains](es-managedomains-awsresourcetagging.md)\. | 

### PurchaseReservedElasticsearchInstanceOffering<a name="es-configuration-api-actions-purchasereservedelasticsearchinstance"></a>

Purchases a Reserved Instance\.

#### Syntax<a name="w32aac33b7c71b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/es/purchaseReservedInstanceOffering
{
  "ReservationName" : "my-reservation",
  "ReservedElasticsearchInstanceOfferingId" : "1a2a3a4a5-1a2a-3a4a-5a6a-1a2a3a4a5a6a",
  "InstanceCount" : 3
}
```

#### Request Parameters<a name="w32aac33b7c71b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w32aac33b7c71b9"></a>


****  

| Name | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| ReservationName | String | Yes | A descriptive name for your reservation\. | 
|  ReservedElasticsearchInstanceOfferingId  | String | Yes | The offering ID\. | 
| InstanceCount | Integer | Yes | The number of instances that you want to reserve\. | 

#### Response Elements<a name="w32aac33b7c71c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ReservationName | String | The name of your reservation\. | 
|  ReservedElasticsearchInstanceId | String | The reservation ID\. | 

### RejectInboundCrossClusterSearchConnection<a name="es-configuration-api-actions-reject-inbound-cross-cluster-search-connection"></a>

Allows the destination domain owner to reject an inbound cross\-cluster search connection request\.

#### Syntax<a name="w32aac33b7c73b5"></a>

```
PUT https://es.us-east-1.amazonaws.com/2015-01-01/es/ccs/inboundConnection/{ConnectionId}/reject
```

#### Request Parameters<a name="w32aac33b7c73b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w32aac33b7c73b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w32aac33b7c73c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| CrossClusterSearchConnection | Object | Inbound connection details\. | 

### RemoveTags<a name="es-configuration-api-actions-removetags"></a>

Removes the specified resource tags from an Amazon ES domain\.

#### Syntax<a name="w32aac33b7c75b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/tags-removal
{
  "ARN": "arn:aws:es:us-east-1:123456789012:domain/my-domain",
  "TagKeys": [
    "tag-key1",
    "tag-key2"
  ]
}
```

#### Request Parameters<a name="w32aac33b7c75b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w32aac33b7c75b9"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| ARN | [`ARN`](#es-configuration-api-datatypes-arn) | Yes | Amazon Resource Name \(ARN\) of an Amazon ES domain\. For more information, see [Identifiers for IAM Entities](http://docs.aws.amazon.com/IAM/latest/UserGuide/index.html?Using_Identifiers.html) in Using AWS Identity and Access Management\. | 
| TagKeys | [`TagKey`](#es-configuration-api-datatypes-tagkey) | Yes | List of tag keys for resource tags that you want to remove from an Amazon ES domain\. | 

#### Response Elements<a name="w32aac33b7c75c11"></a>

The `RemoveTags` operation does not return a response element\.

### StartElasticsearchServiceSoftwareUpdate<a name="es-configuration-api-actions-startupdate"></a>

Schedules a service software update for an Amazon ES domain\.

#### Syntax<a name="w32aac33b7c77b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/es/serviceSoftwareUpdate/start
{
  "DomainName": "domain-name"
}
```

#### Request Parameters<a name="w32aac33b7c77b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w32aac33b7c77b9"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [`DomainName`](#es-configuration-api-datatypes-domainname) | Yes | Name of the Amazon ES domain that you want to update to the latest service software\. | 

#### Response Elements<a name="w32aac33b7c77c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ServiceSoftwareOptions | ServiceSoftwareOptions | Container for the state of your domain relative to the latest service software\. | 

### StopElasticsearchServiceSoftwareUpdate<a name="es-configuration-api-actions-stopupdate"></a>

Stops a scheduled service software update for an Amazon ES domain\. Only works if the domain's `UpdateStatus` is `PENDING_UPDATE`\.

#### Syntax<a name="w32aac33b7c79b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/es/serviceSoftwareUpdate/stop
{
  "DomainName": "domain-name"
}
```

#### Request Parameters<a name="w32aac33b7c79b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w32aac33b7c79b9"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [`DomainName`](#es-configuration-api-datatypes-domainname) | Yes | Name of the Amazon ES domain that you want to update to the latest service software\. | 

#### Response Elements<a name="w32aac33b7c79c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ServiceSoftwareOptions | [`ServiceSoftwareOptions`](#es-configuration-api-datatypes-servicesoftware) | Container for the state of your domain relative to the latest service software\. | 

### UpdateElasticsearchDomainConfig<a name="es-configuration-api-actions-updateelasticsearchdomainconfig"></a>

Modifies the configuration of an Amazon ES domain, such as the instance type and the number of instances\. You need to specify only the values that you want to update\.

#### Syntax<a name="w32aac33b7c81b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/es/domain/<DOMAIN_NAME>/config
{
  "ElasticsearchClusterConfig": {
    "ZoneAwarenessConfig": {
      "AvailabilityZoneCount": 3
    },
    "ZoneAwarenessEnabled": true|false,
    "InstanceCount": 3,
    "DedicatedMasterEnabled": true|false,
    "DedicatedMasterType": "c5.large.elasticsearch",
    "DedicatedMasterCount": 3,
    "InstanceType": "r5.large.elasticsearch",
    "WarmCount": 6,
    "WarmType": "ultrawarm1.medium.elasticsearch"
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
  "VPCOptions": {
    "SubnetIds": ["subnet-abcdefg1", "subnet-abcdefg2", "subnet-abcdefg3"],
    "SecurityGroupIds": ["sg-12345678"]
  },
  "AdvancedOptions": {
    "rest.action.multi.allow_explicit_index": "true|false",
    "indices.fielddata.cache.size": "40",
    "indices.query.bool.max_clause_count": "1024"
  },
  "CognitoOptions": {
    "Enabled": true|false,
    "UserPoolId": "us-east-1_121234567",
    "IdentityPoolId": "us-east-1:12345678-1234-1234-1234-123456789012",
    "RoleArn": "arn:aws:iam::123456789012:role/service-role/CognitoAccessForAmazonES"
  },
  "DomainEndpointOptions": {
    "EnforceHTTPS": true|false,
    "TLSSecurityPolicy": "Policy-Min-TLS-1-2-2019-07|Policy-Min-TLS-1-0-2019-07"
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
    "InternalUserDatabaseEnabled": true|false,
    "MasterUserOptions": {
      "MasterUserARN": "arn:aws:iam::123456789012:role/my-master-user-role"
      "MasterUserName": "my-master-username",
      "MasterUserPassword": "my-master-password"
    }
  },
  "DomainName": "my-domain",
  "AccessPolicies": "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"es:*\"],\"Resource\":\"arn:aws:es:us-east-1:123456789012:domain/my-domain/*\"}]}"
}
```

#### Request Parameters<a name="w32aac33b7c81b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w32aac33b7c81b9"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [`DomainName`](#es-configuration-api-datatypes-domainname) | Yes | Name of the Amazon ES domain for which you want to update the configuration\. | 
| ElasticsearchClusterConfig | [`ElasticsearchClusterConfig`](#es-configuration-api-datatypes-elasticsearchclusterconfig) | No | Changes that you want to make to the cluster configuration, such as the instance type and number of EC2 instances\. | 
| EBSOptions | [`EBSOptions`](#es-configuration-api-datatypes-ebsoptions) | No | Type and size of EBS volumes attached to data nodes\.  | 
| VPCOptions | [`VPCOptions`](#es-configuration-api-datatypes-vpcoptions) | No | Container for the values required to configure Amazon ES to work with a VPC\. To learn more, see [VPC Support for Amazon Elasticsearch Service Domains](es-vpc.md)\. | 
| SnapshotOptions | [`SnapshotOptions`](#es-configuration-api-datatypes-snapshotoptions) | No | DEPRECATED\. Hour during which the service takes an automated daily snapshot of the indices in the Amazon ES domain\. | 
| AdvancedOptions | [`AdvancedOptions`](#es-configuration-api-datatypes-advancedoptions) | No | Key\-value pairs to specify advanced configuration options\. For more information, see [Configuring Advanced Options](es-createupdatedomains.md#es-createdomain-configure-advanced-options)\. | 
| AccessPolicies | String | No | Specifies the access policies for the Amazon ES domain\. For more information, see [Configuring Access Policies](es-createupdatedomains.md#es-createdomain-configure-access-policies)\. | 
| LogPublishingOptions | [`LogPublishingOptions`](#es-configuration-api-datatypes-logpublishingoptions) | No | Key\-value string pairs to configure slow log publishing\. | 
| CognitoOptions | [`CognitoOptions`](#es-configuration-api-datatypes-cognitooptions) | No | Key\-value pairs to configure Amazon ES to use Amazon Cognito authentication for Kibana\. | 
| DomainEndpointOptions | [DomainEndpointOptions](#es-configuration-api-datatypes-domainendpointoptions) | No | Additional options for the domain endpoint, such as whether to require HTTPS for all traffic\. | 
| AdvancedSecurityOptions | [AdvancedSecurityOptions](#es-configuration-api-datatypes-advancedsec) | No | Options for fine\-grained access control\. | 

#### Response Elements<a name="w32aac33b7c81c11"></a>


****  

| Field | Data Type | 
| --- | --- | 
| DomainConfig | [ElasticsearchDomainConfig](#es-configuration-api-datatypes-esdomainconfig) | 

### UpgradeElasticsearchDomain<a name="es-configuration-api-actions-upgrade-domain"></a>

Upgrades an Amazon ES domain to a new version of Elasticsearch\. Alternately, checks upgrade eligibility\.

#### Syntax<a name="w32aac33b7c83b5"></a>

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/es/upgradeDomain
{
  "DomainName": "domain-name",
  "TargetVersion": "7.7",
  "PerformCheckOnly": true|false
}
```

#### Request Parameters<a name="w32aac33b7c83b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w32aac33b7c83b9"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | String | Yes | Name of the Amazon ES domain that you want to upgrade\. | 
| TargetVersion | String | Yes | Elasticsearch version to which you want to upgrade\. See [GetCompatibleElasticsearchVersions](#es-configuration-api-actions-get-compat-vers)\. | 
| PerformCheckOnly | Boolean | No | Defaults to false\. If true, Amazon ES checks the eligibility of the domain, but does not perform the upgrade\. | 

#### Response Elements<a name="w32aac33b7c83c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| UpgradeElasticsearchDomainResponse | Map | Basic response confirming operation details\. | 

## Data Types<a name="es-configuration-api-datatypes"></a>

This section describes the data types used by the configuration API\.

### AdvancedOptions<a name="es-configuration-api-datatypes-advancedoptions"></a>

Key\-value pairs to specify advanced Elasticsearch configuration options\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| rest\.action\.multi\.allow\_explicit\_index | Key\-value pair: `"rest.action.multi.allow_explicit_index":"true"`  | Note the use of a string rather than a boolean\. Specifies whether explicit references to indices are allowed inside the body of HTTP requests\. If you want to configure access policies for domain sub\-resources, such as specific indices and domain APIs, you must disable this property\. For more information about access policies for sub\-resources, see [Configuring Access Policies](es-createupdatedomains.md#es-createdomain-configure-access-policies)\. | 
| indices\.fielddata\.cache\.size | Key\-value pair:`"indices.fielddata.cache.size":"80"` | Note the use of a string rather than an integer\. Specifies the percentage of Java heap space that is allocated to field data\. By default, this setting is unbounded\. | 
| indices\.query\.bool\.max\_clause\_count | Key\-value pair:`"indices.query.bool.max_clause_count":"1024"` | Note the use of a string rather than an integer\. Specifies the maximum number of clauses allowed in a Lucene boolean query\. 1,024 is the default\. Queries with more than the permitted number of clauses that result in a TooManyClauses error\. To learn more, see [the Lucene documentation](https://lucene.apache.org/core/6_6_0/core/org/apache/lucene/search/BooleanQuery.html)\. | 

### ARN<a name="es-configuration-api-datatypes-arn"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ARN | String | Amazon Resource Name \(ARN\) of an Amazon ES domain\. For more information, see [IAM ARNs](http://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns) in the AWS Identity and Access Management documentation\. | 

### AdvancedSecurityOptions<a name="es-configuration-api-datatypes-advancedsec"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Enabled | Boolean | True to enable [fine\-grained access control](fgac.md)\. | 
| InternalUserDatabaseEnabled | Boolean | True to enable the internal user database\. | 
| MasterUserOptions | [MasterUserOptions](#es-configuration-api-datatypes-masteruser) | Container for information about the master user\. | 

### CognitoOptions<a name="es-configuration-api-datatypes-cognitooptions"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Enabled | Boolean | Whether to enable or disable Amazon Cognito authentication for Kibana\. See [Amazon Cognito Authentication for Kibana](es-cognito-auth.md)\. | 
| UserPoolId | String | The Amazon Cognito user pool ID that you want Amazon ES to use for Kibana authentication\. | 
| IdentityPoolId | String | The Amazon Cognito identity pool ID that you want Amazon ES to use for Kibana authentication\. | 
| RoleArn | String | The AmazonESCognitoAccess role that allows Amazon ES to configure your user pool and identity pool\. | 

### CreateElasticsearchDomainRequest<a name="es-configuration-api-datatypes-createesdomainrequest"></a>

Container for the parameters required by the `CreateElasticsearchDomain` service operation\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| DomainName | [`DomainName`](#es-configuration-api-datatypes-domainname) | Name of the Amazon ES domain to create\. | 
| ElasticsearchClusterConfig | [`ElasticsearchClusterConfig`](#es-configuration-api-datatypes-elasticsearchclusterconfig) | Container for the cluster configuration of an Amazon ES domain\. | 
| EBSOptions | [`EBSOptions`](#es-configuration-api-datatypes-ebsoptions) | Container for the parameters required to enable EBS\-based storage for an Amazon ES domain\. | 
| AccessPolicies | String | IAM policy document that specifies the access policies for the new Amazon ES domain\. For more information, see [Configuring Access Policies](es-createupdatedomains.md#es-createdomain-configure-access-policies)\. | 
| DomainEndpointOptions | [`DomainEndpointOptions`](#es-configuration-api-datatypes-domainendpointoptions) | Additional options for the domain endpoint, such as whether to require HTTPS for all traffic\. | 
| SnapshotOptions | [`SnapshotOptions`](#es-configuration-api-datatypes-snapshotoptions) | DEPRECATED\. Container for parameters required to configure automated snapshots of domain indices\. | 
| VPCOptions | [`VPCOptions`](#es-configuration-api-datatypes-vpcoptions) | Container for the values required to configure Amazon ES to work with a VPC\. | 
| LogPublishingOptions | [`LogPublishingOptions`](#es-configuration-api-datatypes-logpublishingoptions) | Key\-value string pairs to configure slow log publishing\. | 
| AdvancedOptions | [AdvancedOptions](#es-configuration-api-datatypes-advancedoptions) | Key\-value pairs to specify advanced configuration options\. | 
| CognitoOptions | [`CognitoOptions`](#es-configuration-api-datatypes-cognitooptions) | Key\-value pairs to configure Amazon ES to use Amazon Cognito authentication for Kibana\. | 
| NodeToNodeEncryptionOptions | [`NodeToNodeEncryptionOptions`](#es-configuration-api-datatypes-node-to-node) | Specify true to enable node\-to\-node encryption\. | 

### DomainEndpointOptions<a name="es-configuration-api-datatypes-domainendpointoptions"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| EnforceHTTPS | Boolean | true to require that all traffic to the domain arrive over HTTPS\. | 
| TLSSecurityPolicy | String | The minimum TLS version required for traffic to the domain\. Valid values are TLS 1\.0 \(default\) or 1\.2:[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-configuration-api.html) | 

### DomainID<a name="es-configuration-api-datatypes-domainid"></a>


****  

| Data Type | Description | 
| --- | --- | 
| String | Unique identifier for an Amazon ES domain\.  | 

### DomainName<a name="es-configuration-api-datatypes-domainname"></a>

Name of an Amazon ES domain\. 


****  

| Data Type | Description | 
| --- | --- | 
| String | Name of an Amazon ES domain\. Domain names are unique across all domains owned by the same account within an AWS Region\. Domain names must start with a lowercase letter and must be between 3 and 28 characters\. Valid characters are a\-z \(lowercase only\), 0\-9, and – \(hyphen\)\. | 

### DomainNameList<a name="es-configuration-api-datatypes-domainnamelist"></a>

String of Amazon ES domain names\.


****  

| Data Type | Description | 
| --- | --- | 
| String Array | Array of Amazon ES domains in the following format:`["<Domain_Name>","<Domain_Name>"...]` | 

### DomainPackageDetails<a name="es-configuration-api-datatypes-domainpackagedetails"></a>

Information on a package that is associated with a domain\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| DomainName | String | Name of the domain you've associated a package with\. | 
| DomainPackageStatus | String | State of the association\. Values are ASSOCIATING, ASSOCIATION\_FAILED, ACTIVE, DISSOCIATING, and DISSOCIATION\_FAILED\.  | 
| ErrorDetails | String | Additional information if the package is in an error state\. Null otherwise\. | 
| LastUpdated | Timestamp | Timestamp of the most\-recent update to the association status\. | 
| PackageID | String | Internal ID of the package\. | 
| PackageName | String | User\-specified name of the package\. | 
| PackageType | String | Currently supports only TXT\-DICTIONARY\. | 
| ReferencePath | String | Denotes the location of the package on the Amazon ES cluster nodes\. It's the same as synonym\_path for dictionary files\. | 

### EBSOptions<a name="es-configuration-api-datatypes-ebsoptions"></a>

Container for the parameters required to enable EBS\-based storage for an Amazon ES domain\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| EBSEnabled | Boolean | Indicates whether EBS volumes are attached to data nodes in an Amazon ES domain\. | 
| VolumeType | String | Specifies the type of EBS volumes attached to data nodes\.  | 
| VolumeSize | String | Specifies the size \(in GiB\) of EBS volumes attached to data nodes\. | 
| Iops | String | Specifies the baseline input/output \(I/O\) performance of EBS volumes attached to data nodes\. Applicable only for the Provisioned IOPS EBS volume type\. | 

### ElasticsearchClusterConfig<a name="es-configuration-api-datatypes-elasticsearchclusterconfig"></a>

Container for the cluster configuration of an Amazon ES domain\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| InstanceType | String | Instance type of data nodes in the cluster\. | 
| InstanceCount | Integer | Number of instances in the cluster\. | 
| DedicatedMasterEnabled | Boolean | Indicates whether dedicated master nodes are enabled for the cluster\. True if the cluster will use a dedicated master node\. False if the cluster will not\. For more information, see [About Dedicated Master Nodes](es-managedomains-dedicatedmasternodes.md)\. | 
| DedicatedMasterType | String | Amazon ES instance type of the dedicated master nodes in the cluster\. | 
| DedicatedMasterCount | Integer | Number of dedicated master nodes in the cluster\. | 
| ZoneAwarenessEnabled | Boolean | Indicates whether multiple Availability Zones are enabled\. For more information, see [Configuring a Multi\-AZ Domain](es-managedomains-multiaz.md)\. | 
| ZoneAwarenessConfig | [`ZoneAwarenessConfig`](#es-configuration-api-datatypes-az) | Container for zone awareness configuration options\. Only required if ZoneAwarenessEnabled is true\. | 
| WarmEnabled | Boolean | Whether to enable warm storage for the cluster\. | 
| WarmCount | Integer | The number of warm nodes in the cluster\. | 
| WarmType | String | The instance type for the cluster's warm nodes\. | 
| WarmStorage | Integer | The total provisioned amount of warm storage in GiB\. | 

### ElasticsearchDomainConfig<a name="es-configuration-api-datatypes-esdomainconfig"></a>

Container for the configuration of an Amazon ES domain\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ElasticsearchVersion | String | Elasticsearch version\. | 
| ElasticsearchClusterConfig | [`ElasticsearchClusterConfig`](#es-configuration-api-datatypes-elasticsearchclusterconfig) | Container for the cluster configuration of an Amazon ES domain\. | 
| EBSOptions | [`EBSOptions`](#es-configuration-api-datatypes-ebsoptions) | Container for EBS options configured for an Amazon ES domain\. | 
| AccessPolicies | String | Specifies the access policies for the Amazon ES domain\. For more information, see [Configuring Access Policies](es-createupdatedomains.md#es-createdomain-configure-access-policies)\. | 
| SnapshotOptions | [`SnapshotOptions`](#es-configuration-api-datatypes-snapshotoptions) | DEPRECATED\. Hour during which the service takes an automated daily snapshot of the indices in the Amazon ES domain\. | 
| DomainEndpointOptions | [`DomainEndpointOptions`](#es-configuration-api-datatypes-domainendpointoptions) | Additional options for the domain endpoint, such as whether to require HTTPS for all traffic\. | 
| VPCOptions | [`VPCDerivedInfo`](#es-configuration-api-datatypes-vpcderivedinfo) | The current [VPCOptions](#es-configuration-api-datatypes-vpcoptions) for the domain and the status of any updates to their configuration\. | 
| LogPublishingOptions | [`LogPublishingOptions`](#es-configuration-api-datatypes-logpublishingoptions) | Key\-value pairs to configure slow log publishing\. | 
| AdvancedOptions | [`AdvancedOptions`](#es-configuration-api-datatypes-advancedoptions) | Key\-value pairs to specify advanced configuration options\. | 
| EncryptionAtRestOptions | [`EncryptionAtRestOptions`](#es-configuration-api-datatypes-encryptionatrest) | Key\-value pairs to enable encryption at rest\. | 
| NodeToNodeEncryptionOptions | [`NodeToNodeEncryptionOptions`](#es-configuration-api-datatypes-node-to-node) | Whether node\-to\-node encryption is enabled or disabled\. | 

### ElasticsearchDomainStatus<a name="es-configuration-api-datatypes-elasticsearchdomainstatus"></a>

Container for the contents of a `DomainStatus` data structure\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| DomainID | [`DomainID`](#es-configuration-api-datatypes-domainid) | Unique identifier for an Amazon ES domain\. | 
| DomainName | [`DomainName`](#es-configuration-api-datatypes-domainname) | Name of an Amazon ES domain\. Domain names are unique across all domains owned by the same account within an AWS Region\. Domain names must start with a lowercase letter and must be between 3 and 28 characters\. Valid characters are a\-z \(lowercase only\), 0\-9, and – \(hyphen\)\. | 
| ARN | [`ARN`](#es-configuration-api-datatypes-arn) | Amazon Resource Name \(ARN\) of an Amazon ES domain\. For more information, see [Identifiers for IAM Entities](http://docs.aws.amazon.com/IAM/latest/UserGuide/index.html?Using_Identifiers.html) in Using AWS Identity and Access Management\. | 
| Created | Boolean | Status of the creation of an Amazon ES domain\. True if creation of the domain is complete\. False if domain creation is still in progress\. | 
| Deleted | Boolean | Status of the deletion of an Amazon ES domain\. True if deletion of the domain is complete\. False if domain deletion is still in progress\. | 
| Endpoint | [`ServiceUrl`](#es-configuration-api-datatypes-serviceurl) | Domain\-specific endpoint used to submit index, search, and data upload requests to an Amazon ES domain\. | 
| Endpoints | [`EndpointsMap`](#es-configuration-api-datatypes-endpointsmap) | The key\-value pair that exists if the Amazon ES domain uses VPC endpoints\. | 
| Processing | Boolean | Status of a change in the configuration of an Amazon ES domain\. True if the service is still processing the configuration changes\. False if the configuration change is active\. You must wait for a domain to reach active status before submitting index, search, and data upload requests\. | 
| ElasticsearchVersion | String | Elasticsearch version\. | 
| ElasticsearchClusterConfig | [`ElasticsearchClusterConfig`](#es-configuration-api-datatypes-elasticsearchclusterconfig) | Container for the cluster configuration of an Amazon ES domain\. | 
| EBSOptions | [`EBSOptions`](#es-configuration-api-datatypes-ebsoptions) | Container for the parameters required to enable EBS\-based storage for an Amazon ES domain\. | 
| AccessPolicies | String | IAM policy document specifying the access policies for the new Amazon ES domain\. For more information, see [Configuring Access Policies](es-createupdatedomains.md#es-createdomain-configure-access-policies)\. | 
| SnapshotOptions | [`SnapshotOptions`](#es-configuration-api-datatypes-snapshotoptions) | DEPRECATED\. Container for parameters required to configure the time of daily automated snapshots of Amazon ES domain indices\.  | 
| VPCOptions | [`VPCDerivedInfo`](#es-configuration-api-datatypes-vpcoptions) | Information that Amazon ES derives based on [VPCOptions](#es-configuration-api-datatypes-vpcoptions) for the domain\. | 
| LogPublishingOptions | [`LogPublishingOptions`](#es-configuration-api-datatypes-logpublishingoptions) | Key\-value pairs to configure slow log publishing\. | 
| AdvancedOptions | [`AdvancedOptions`](#es-configuration-api-datatypes-advancedoptions) | Key\-value pairs to specify advanced configuration options\. | 
| EncryptionAtRestOptions | [`EncryptionAtRestOptions`](#es-configuration-api-datatypes-encryptionatrest) | Key\-value pairs to enable encryption at rest\. | 
| CognitoOptions | [`CognitoOptions`](#es-configuration-api-datatypes-cognitooptions) | Key\-value pairs to configure Amazon ES to use Amazon Cognito authentication for Kibana\. | 
| NodeToNodeEncryptionOptions | [`NodeToNodeEncryptionOptions`](#es-configuration-api-datatypes-node-to-node) | Whether node\-to\-node encryption is enabled or disabled\. | 
| UpgradeProcessing | Boolean | True if an upgrade to a new Elasticsearch version is in progress\. | 
| ServiceSoftwareOptions | [ServiceSoftwareOptions](#es-configuration-api-datatypes-servicesoftware) | The status of the domain's service software\. | 

### ElasticsearchDomainStatusList<a name="es-configuration-api-datatypes-esdomainstatuslist"></a>

List that contains the status of each specified Amazon ES domain\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| DomainStatusList | [`ElasticsearchDomainStatus`](#es-configuration-api-datatypes-elasticsearchdomainstatus) | List that contains the status of each specified Amazon ES domain\. | 

### EncryptionAtRestOptions<a name="es-configuration-api-datatypes-encryptionatrest"></a>

Specifies whether the domain should encrypt data at rest, and if so, the AWS Key Management Service \(KMS\) key to use\. Can be used only to create a new domain, not update an existing one\. To learn more, see [Enabling Encryption of Data at Rest](encryption-at-rest.md#enabling-ear)\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Enabled | Boolean | Specify true to enable encryption at rest\. | 
| KmsKeyId | String | The KMS key ID\. Takes the form 1a2a3a4\-1a2a\-3a4a\-5a6a\-1a2a3a4a5a6a\. | 

### EndpointsMap<a name="es-configuration-api-datatypes-endpointsmap"></a>

The key\-value pair that contains the VPC endpoint\. Only exists if the Amazon ES domain resides in a VPC\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Endpoints | Key\-value string pair: "vpc": "<VPC\_ENDPOINT>" | The VPC endpoint for the domain\. | 

### Filters<a name="es-configuration-api-datatypes-filters"></a>

Filters the packages included in a [DescribePackages](#es-configuration-api-actions-describepackages) response\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Name | String | Any field from [PackageDetails](#es-configuration-api-datatypes-packagedetails)\. | 
| Value | List | A list of values for the specified field\. | 

### LogPublishingOptions<a name="es-configuration-api-datatypes-logpublishingoptions"></a>

Specifies whether the Amazon ES domain publishes the Elasticsearch application and slow logs to Amazon CloudWatch\. You still have to enable the *collection* of slow logs using the Elasticsearch REST API\. To learn more, see [Setting Elasticsearch Logging Thresholds for Slow Logs](es-createdomain-configure-slow-logs.md#es-createdomain-configure-slow-logs-indices)\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| INDEX\_SLOW\_LOGS | Key\-value | Two key\-value pairs that define the CloudWatch log group and whether the Elasticsearch index slow log should be published there: <pre>"CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:264071961897:log-group:sample-domain",<br />"Enabled":true</pre> | 
| SEARCH\_SLOW\_LOGS | Key\-value | Two key\-value pairs that define the CloudWatch log group and whether the Elasticsearch search slow log should be published there: <pre>"CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:264071961897:log-group:sample-domain",<br />"Enabled":true</pre> | 
| ES\_APPLICATION\_LOGS | Key\-value | Two key\-value pairs that define the CloudWatch log group and whether the Elasticsearch error logs should be published there:<pre>"CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:264071961897:log-group:sample-domain",<br />"Enabled":true</pre> | 

### MasterUserOptions<a name="es-configuration-api-datatypes-masteruser"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| MasterUserARN | String | ARN for the master user\. Only specify if InternalUserDatabaseEnabled is false in [AdvancedSecurityOptions](#es-configuration-api-datatypes-advancedsec)\. | 
| MasterUserName | String | Username for the master user\. Only specify if InternalUserDatabaseEnabled is true in [AdvancedSecurityOptions](#es-configuration-api-datatypes-advancedsec)\. | 
| MasterUserPassword | String | Password for the master user\. Only specify if InternalUserDatabaseEnabled is true in [AdvancedSecurityOptions](#es-configuration-api-datatypes-advancedsec)\. | 

### NodeToNodeEncryptionOptions<a name="es-configuration-api-datatypes-node-to-node"></a>

Enables or disables node\-to\-node encryption\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Enabled | Boolean | Enable with true\. | 

### OptionState<a name="es-configuration-api-datatypes-optionsstate"></a>

State of an update to advanced options for an Amazon ES domain\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| OptionStatus | String | One of three valid values:[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-configuration-api.html) | 

### OptionStatus<a name="es-configuration-api-datatypes-optionstatus"></a>

Status of an update to configuration options for an Amazon ES domain\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| CreationDate | Timestamp | Date and time when the Amazon ES domain was created\. | 
| UpdateDate | Timestamp | Date and time when the Amazon ES domain was updated\. | 
| UpdateVersion | Integer | Whole number that specifies the latest version for the entity\. | 
| State | [`OptionState`](#es-configuration-api-datatypes-optionsstate) | State of an update to configuration options for an Amazon ES domain\. | 
| PendingDeletion | Boolean | Indicates whether the service is processing a request to permanently delete the Amazon ES domain and all of its resources\. | 

### PackageDetails<a name="es-configuration-api-datatypes-packagedetails"></a>

Basic information about a package\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| CreatedAt | Timestamp | Name of the bucket containing the package\. | 
| ErrorDetails | String | Additional information if the package is in an error state\. Null otherwise\. | 
| PackageDescription | String | User\-specified description of the package\. | 
| PackageID | String | Internal ID of the package\. | 
| PackageName | String | User\-specified name of the package\. | 
| PackageStatus | String | Values are COPYING, COPY\_FAILED, AVAILABLE, DELETING, or DELETE\_FAILED \. | 
| PackageType | String | Currently supports only TXT\-DICTIONARY\. | 

### PackageSource<a name="es-configuration-api-datatypes-packagesource"></a>

Bucket and key for the package you want to add to Amazon ES\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| S3BucketName | String | Name of the bucket containing the package\. | 
| S3Key | String | Key \(file name\) of the package\. | 

### ServiceSoftwareOptions<a name="es-configuration-api-datatypes-servicesoftware"></a>

Container for the state of your domain relative to the latest service software\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| UpdateAvailable | Boolean | Whether a service software update is available for your domain\. | 
| Cancellable | Boolean | If you have requested a domain update, whether or not you can cancel the update\. | 
| AutomatedUpdateDate | Timestamp | The Epoch time that the deployment window closes for required updates\. After this time, Amazon ES schedules the software upgrade automatically\. | 
| UpdateStatus | String | The status of the update\. Values are ELIGIBLE, PENDING\_UPDATE, IN\_PROGRESS, COMPLETED, and NOT\_ELIGIBLE\. | 
| Description | String | More detailed description of the status\. | 
| CurrentVersion | String | Your current service software version\. | 
| NewVersion | String | The latest service software version\. | 
| OptionalDeployment | Boolean | Whether the service software update is optional\. | 

### ServiceURL<a name="es-configuration-api-datatypes-serviceurl"></a>

Domain\-specific endpoint used to submit index, search, and data upload requests to an Amazon ES domain\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ServiceURL | String | Domain\-specific endpoint used to submit index, search, and data upload requests to an Amazon ES domain\. | 

### SnapshotOptions<a name="es-configuration-api-datatypes-snapshotoptions"></a>

**DEPRECATED**\. See [Working with Amazon Elasticsearch Service Index Snapshots](es-managedomains-snapshots.md)\. Container for parameters required to configure the time of daily automated snapshots of the indices in an Amazon ES domain\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| AutomatedSnapshotStartHour | Integer | DEPRECATED\. Hour during which the service takes an automated daily snapshot of the indices in the Amazon ES domain\. | 

### Tag<a name="es-configuration-api-datatypes-tag"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Key | [`TagKey`](#es-configuration-api-datatypes-tagkey) | Required name of the tag\. Tag keys must be unique for the Amazon ES domain to which they are attached\. For more information, see [Tagging Amazon Elasticsearch Service Domains](es-managedomains-awsresourcetagging.md)\. | 
| Value | [`TagValue`](#es-configuration-api-datatypes-tagvalue) | Optional string value of the tag\. Tag values can be null and do not have to be unique in a tag set\. For example, you can have a key\-value pair in a tag set of project/Trinity and cost\-center/Trinity\.  | 

### TagKey<a name="es-configuration-api-datatypes-tagkey"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Key | String | Name of the tag\. String can have up to 128 characters\. | 

### TagList<a name="es-configuration-api-datatypes-taglist"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Tag | [`Tag`](#es-configuration-api-datatypes-tag) | Resource tag attached to an Amazon ES domain\. | 

### TagValue<a name="es-configuration-api-datatypes-tagvalue"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Value | String | Holds the value for a TagKey\. String can have up to 256 characters\. | 

### VPCDerivedInfo<a name="es-configuration-api-datatypes-vpcderivedinfo"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| VPCId | String | The ID for your VPC\. Amazon VPC generates this value when you create a VPC\. | 
| SubnetIds | StringList | A list of subnet IDs associated with the VPC endpoints for the domain\. For more information, see [VPCs and Subnets](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_Subnets.html) in the Amazon VPC User Guide\. | 
| AvailabilityZones | StringList | The list of Availability Zones associated with the VPC subnets\. For more information, see [VPC and Subnet Basics](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_Subnets.html#vpc-subnet-basics) in the Amazon VPC User Guide\. | 
| SecurityGroupIds | StringList | The list of security group IDs associated with the VPC endpoints for the domain\. For more information, see [Security Groups for your VPC](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_SecurityGroups.html) in the Amazon VPC User Guide\. | 

### VPCOptions<a name="es-configuration-api-datatypes-vpcoptions"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| SubnetIds | StringList | A list of subnet IDs associated with the VPC endpoints for the domain\. If your domain uses multiple Availability Zones, you need to provide two subnet IDs, one per zone\. Otherwise, provide only one\. To learn more, see [VPCs and Subnets](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_Subnets.html) in the Amazon VPC User Guide\. | 
| SecurityGroupIds | StringList | The list of security group IDs associated with the VPC endpoints for the domain\. If you do not provide a security group ID, Amazon ES uses the default security group for the VPC\. To learn more, see [Security Groups for your VPC](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_SecurityGroups.html) in the Amazon VPC User Guide\. | 
| VPCId | String | ID for the VPC\. | 

### ZoneAwarenessConfig<a name="es-configuration-api-datatypes-az"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| AvailabilityZoneCount | Integer | If you enabled multiple Availability Zones, this field is the number of zones that you want the domain to use\. Valid values are 2 and 3\. | 

## Errors<a name="es-configuration-api-errors"></a>

Amazon ES throws the following errors:


****  

| Exception | Description | 
| --- | --- | 
| <a name="es-configuration-api-errors-baseexception"></a>BaseException | Thrown for all service errors\. Contains the HTTP status code of the error\. | 
| <a name="es-configuration-api-errors-validationexception"></a>ValidationException | Thrown when the HTTP request contains invalid input or is missing required input\. Returns HTTP status code 400\. | 
| <a name="es-configuration-api-errors-disabledoperation"></a>DisabledOperationException | Thrown when the client attempts to perform an unsupported operation\. Returns HTTP status code 409\. | 
| <a name="es-configuration-api-errors-internal"></a>InternalException | Thrown when an error internal to the service occurs while processing a request\. Returns HTTP status code 500\. | 
| <a name="es-configuration-api-errors-invalidtype"></a>InvalidTypeException | Thrown when trying to create or access an Amazon ES domain sub\-resource that is either invalid or not supported\. Returns HTTP status code 409\. | 
| <a name="es-configuration-api-errors-limitexceeded"></a>LimitExceededException | Thrown when trying to create more than the allowed number and type of Amazon ES domain resources and sub\-resources\. Returns HTTP status code 409\. | 
| <a name="es-configuration-api-errors-resourcenotfound"></a>ResourceNotFoundException | Thrown when accessing or deleting a resource that does not exist\. Returns HTTP status code 400\. | 
| <a name="es-configuration-api-errors-resourcealreadyexists"></a>ResourceAlreadyExistsException | Thrown when a client attempts to create a resource that already exists in an Amazon ES domain\. Returns HTTP status code 400\. | 
| <a name="es-configuration-api-errors-accessdenied"></a>AccessDeniedException |  | 
| <a name="es-configuration-api-errors-conflict"></a>ConflictException |  | 