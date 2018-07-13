# Amazon Elasticsearch Service Configuration API Reference<a name="es-configuration-api"></a>

This reference describes the actions, data types, and errors in the Amazon Elasticsearch Service Configuration API\. The Configuration API is a REST API that you can use to create and configure Amazon ES domains over HTTP\. You also can use the AWS CLI and the console to configure Amazon ES domains\. For more information, see [Creating and Configuring Amazon ES Domains](es-createupdatedomains.md)\.
+ [Actions](#es-configuration-api-actions)
+ [Data Types](#es-configuration-api-datatypes)
+ [Errors](#es-configuration-api-errors)

## Actions<a name="es-configuration-api-actions"></a>

The following table provides a quick reference to the HTTP method required for each operation for the REST interface to the Amazon Elasticsearch Service Configuration API\. The description of each operation also includes the required HTTP method\.

**Note**  
All configuration service requests must be signed\. For more information, see [Signing Amazon Elasticsearch Service Requests](es-ac.md#es-managedomains-signing-service-requests) in this guide and [Signature Version 4 Signing Process](http://docs.aws.amazon.com/general/latest/gr/signature-version-4.html) in the *AWS General Reference*\.


****  

| Action | HTTP Method | 
| --- | --- | 
| [`AddTags`](#es-configuration-api-actions-addtags) | POST | 
| [`CreateElasticsearchDomain`](#es-configuration-api-actions-createelasticsearchdomain) | POST | 
| [`DeleteElasticsearchDomain`](#es-configuration-api-actions-deleteelasticsearchdomain) | DELETE | 
| [`DeleteElasticsearchServiceRole`](#es-configuration-api-actions-deleteelasticsearchservicerole) | DELETE | 
| [`DescribeElasticsearchDomain`](#es-configuration-api-actions-describeelasticsearchdomain) | GET | 
| [`DescribeElasticsearchDomainConfig`](#es-configuration-api-actions-describeelasticsearchdomainconfig) | GET | 
| [`DescribeElasticsearchDomains`](#es-configuration-api-actions-describeesdomains) | POST | 
| [`DescribeElasticsearchInstanceTypeLimits`](#es-configuration-api-actions-describeinstancetypelimits) | GET | 
| [`DescribeReservedElasticsearchInstanceOfferings`](#es-configuration-api-actions-describereservedelasticsearchinstanceofferings) | GET | 
| [`DescribeReservedElasticsearchInstances`](#es-configuration-api-actions-describereservedelasticsearchinstances) | GET | 
| [`ListDomainNames`](#es-configuration-api-actions-listdomainnames) | GET | 
| [`ListElasticsearchInstanceTypeDetails`](#es-configuration-api-actions-listelasticsearchinstancetypedetails) | GET | 
| [`ListElasticsearchInstanceTypes`](#es-configuration-api-actions-listelasticsearchinstancetypes) | GET | 
| [`ListElasticsearchVersions`](#es-configuration-api-actions-listelasticsearchversions) | GET | 
| [`ListTags`](#es-configuration-api-actions-listtags) | GET | 
| [`PurchaseReservedElasticsearchInstance`](#es-configuration-api-actions-purchasereservedelasticsearchinstance) | POST | 
| [`RemoveTags`](#es-configuration-api-actions-removetags) | POST | 
| [`UpdateElasticsearchDomainConfig`](#es-configuration-api-actions-updateelasticsearchdomainconfig) | POST | 

### AddTags<a name="es-configuration-api-actions-addtags"></a>

Attaches resource tags to an Amazon ES domain\. For more information, see [Tagging Amazon ES Domains](es-managedomains.md#es-managedomains-awsresourcetagging)\.

#### Syntax<a name="w3ab1c43b7b9b5"></a>

```
POST /2015-01-01/tags
{
    "ARN": "<DOMAIN_ARN>",
    "TagList": [
        {
            "Key": "<TAG_KEY>",
            "Value": "<TAG_VALUE>"
        }
    ]
}
```

#### Request Parameters<a name="w3ab1c43b7b9b7"></a>

This operation does not use request parameters\.

#### Request Body<a name="w3ab1c43b7b9b9"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| TagList | [`TagList`](#es-configuration-api-datatypes-taglist) | Yes | List of resource tags | 
| ARN | [`ARN`](#es-configuration-api-datatypes-arn) | Yes | Amazon Resource Name \(ARN\) for the Amazon ES domain to which you want to attach resource tags\. | 

#### Response Elements<a name="w3ab1c43b7b9c11"></a>

Not applicable\. The `AddTags` operation does not return a data structure\.

#### Errors<a name="w3ab1c43b7b9c13"></a>

The `AddTags` operation can return any of the following errors:
+ [`BaseException`](#es-configuration-api-errors-baseexception)
+ [`LimitExceededException`](#es-configuration-api-errors-limitexceeded)
+ [`ValidationException`](#es-configuration-api-errors-validationexception)
+ [`InternalException`](#es-configuration-api-errors-internal)

#### Example<a name="w3ab1c43b7b9c15"></a>

The following example attaches a single resource tag with a tag key of `project` to the `logs` Amazon ES domain:

Request

```
POST es.<AWS_REGION>.amazonaws.com/2015-01-01/tags
{
    "ARN": "<DOMAIN_ARN>",
    "TagList": [
        {
            "Key": "project",
            "Value": "trident"
        }
    ]
}
```

Response

```
HTTP/1.1 200 OK
x-amzn-RequestId: 5a6a5790-536c-11e5-9cd2-b36dbf43d89e
Content-Type: application/json
Content-Length: 0
Date: Sat, 05 Sep 2015 01:20:55 GMT
```

### CreateElasticsearchDomain<a name="es-configuration-api-actions-createelasticsearchdomain"></a>

Creates a new Amazon ES domain\. For more information, see [ Creating Amazon ES Domains](es-createupdatedomains.md#es-createdomains)\.

**Note**  
If you attempt to create an Amazon ES domain and a domain with the same name already exists, the API does not report an error\. Instead, it returns details for the existing domain\.

#### Syntax<a name="w3ab1c43b7c11b7"></a>

```
POST /2015-01-01/es/domain
{
    "DomainName": "<DOMAIN_NAME>",
    "ElasticsearchVersion": "<VERSION>",
    "ElasticsearchClusterConfig": {
        "InstanceType": "<INSTANCE_TYPE>",
        "InstanceCount": <INSTANCE_COUNT>,
        "DedicatedMasterEnabled": "<TRUE|FALSE>",
        "DedicatedMasterCount": <INSTANCE_COUNT>,
        "DedicatedMasterType": "<INSTANCE_TYPE>",
        "ZoneAwarenessEnabled": "<TRUE|FALSE>"
    },
    "EBSOptions": {
        "EBSEnabled": "<TRUE|FALSE>",
        "VolumeType": "<VOLUME_TYPE>",
        "VolumeSize": "<VOLUME_SIZE>",
        "Iops": "<VALUE>"
    },
    "VPCOptions": {
        "SubnetIds": [
            "<SUBNET_ID>"
        ],
        "SecurityGroupIds": [
            "<SECURITY_GROUP_ID>"
        ]
    },
    "CognitoOptions": {
        "IdentityPoolId": "us-west-1:12345678-1234-1234-1234-123456789012",
        "RoleArn": "arn:aws:iam::123456789012:role/my-kibana-role",
        "Enabled": true,
        "UserPoolId": "us-west-1_121234567"
    },
    "AccessPolicies": "<ACCESS_POLICY_DOCUMENT>",
    "SnapshotOptions": {
        "AutomatedSnapshotStartHour": <START_HOUR>
    },
    "LogPublishingOptions": {
        "SEARCH_SLOW_LOGS": {
            "CloudWatchLogsLogGroupArn":"<ARN>",
            "Enabled":true
        },
        "INDEX_SLOW_LOGS": {
            "CloudWatchLogsLogGroupArn":"<ARN>",
            "Enabled":true
        }
    },
    "EncryptionAtRestOptions": {
        "Enabled": true,
        "KmsKeyId": "<KEY_ID>"
    },
    "AdvancedOptions": {
            "rest.action.multi.allow_explicit_index": "<TRUE|FALSE>",
            "indices.fielddata.cache.size": "<PERCENTAGE_OF_HEAP>"
    }
}
```

#### Request Parameters<a name="w3ab1c43b7c11b9"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w3ab1c43b7c11c11"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [`DomainName`](#es-configuration-api-datatypes-domainname) | Yes | Name of the Amazon ES domain to create\. | 
| ElasticsearchVersion | String | No | Version of Elasticsearch\. If not specified, 1\.5 is used as the default\. For the full list of supported versions, see [Supported Elasticsearch Versions](what-is-amazon-elasticsearch-service.md#aes-choosing-version)\. | 
| ElasticsearchClusterConfig | [`ElasticsearchClusterConfig`](#es-configuration-api-datatypes-elasticsearchclusterconfig) | No | Container for the cluster configuration of an Amazon ES domain\. | 
| EBSOptions | [`EBSOptions`](#es-configuration-api-datatypes-ebsoptions) | No | Container for the parameters required to enable EBS\-based storage for an Amazon ES domain\. For more information, see [Configuring EBS\-based Storage](es-createupdatedomains.md#es-createdomain-configure-ebs)\. | 
| VPCOptions | [`VPCOptions`](#es-configuration-api-datatypes-vpcoptions) | No | Container for the values required to configure Amazon ES to work with a VPC\. To learn more, see [VPC Support for Amazon Elasticsearch Service Domains](es-vpc.md)\. | 
| CognitoOptions | [`CognitoOptions`](#es-configuration-api-datatypes-cognitooptions) | No | Key\-value pairs to configure Amazon ES to use Amazon Cognito authentication for Kibana\. | 
| AccessPolicies | String | No | IAM policy document specifying the access policies for the new Amazon ES domain\. For more information, see [Amazon Elasticsearch Service Access Control](es-ac.md)\. | 
| SnapshotOptions | [`SnapshotOptions`](#es-configuration-api-datatypes-snapshotoptions) | No | Container for parameters required to configure automated snapshots of domain indices\. For more information, see [Configuring Snapshots](es-createupdatedomains.md#es-createdomain-configure-snapshots)\. | 
| AdvancedOptions | [`AdvancedOptions`](#es-configuration-api-datatypes-advancedoptions) | No | Key\-value pairs to specify advanced configuration options\. For more information, see [Configuring Advanced Options](es-createupdatedomains.md#es-createdomain-configure-advanced-options)\. | 
| LogPublishingOptions | [`LogPublishingOptions`](#es-configuration-api-datatypes-logpublishingoptions) | No | Key\-value pairs to configure slow log publishing\. | 
| EncryptionAtRestOptions | [`EncryptionAtRestOptions`](#es-configuration-api-datatypes-encryptionatrest) | No | Key\-value pairs to enable encryption at rest\. | 

#### Response Elements<a name="w3ab1c43b7c11c13"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| DomainStatus | [ElasticsearchDomainStatus](#es-configuration-api-datatypes-elasticsearchdomainstatus) | Specifies the status and configuration of a new Amazon ES domain\. | 

#### Errors<a name="w3ab1c43b7c11c15"></a>

`CreateElasticsearchDomain` can return any of the following errors:
+ [ `BaseException`](#es-configuration-api-errors-baseexception)
+ [ `DisabledOperationException`](#es-configuration-api-errors-disabledoperation)
+ [`InternalException`](#es-configuration-api-errors-internal)
+ [`InvalidTypeException`](#es-configuration-api-errors-invalidtype)
+ [`LimitExceededException`](#es-configuration-api-errors-limitexceeded)
+ [`ResourceAlreadyExistsException`](#es-configuration-api-errors-resourcealreadyexists)
+ [`ValidationException`](#es-configuration-api-errors-validationexception)

#### Example<a name="w3ab1c43b7c11c17"></a>

This example demonstrates the following:
+ Creates an Amazon ES domain named `streaming-logs`
+ Configures a cluster with six instances of the m3\.medium\.elasticsearch instance type and three dedicated master nodes of the same type
+ Enables zone awareness
+ Configures VPC endpoints for the domain

Request

```
POST es.<AWS_REGION>.amazonaws.com/2015-01-01/es/domain
{
    "DomainName": "streaming-logs",
    "ElasticsearchVersion": "5.5",
    "ElasticsearchClusterConfig": {
        "InstanceType": "m3.medium.elasticsearch",
        "InstanceCount": 6,
        "DedicatedMasterEnabled": "true",
        "DedicatedMasterCount": 3,
        "DedicatedMasterType": "m3.medium.elasticsearch",
        "ZoneAwarenessEnabled": "true"
    },
    "VPCOptions": {
        "SubnetIds": [
            "subnet-87654321",
            "subnet-12345678"
        ]
    },
    "EncryptionAtRestOptions": {
        "Enabled": true,
        "KmsKeyId": "1a2a3a4-1a2a-3a4a-5a6a-1a2a3a4a5a6a"
    }
}
```

Response

```
HTTP/1.1 200 OK
x-amzn-RequestId: 30b03e92-536f-11e5-9cd2-b36dbf43d89e
Content-Type: application/json
Content-Length: 645
Date: Sat, 05 Sep 2015 01:41:15 GMT
{
    "DomainStatus": {
        "ARN": "arn:aws:es:us-west-1:123456789012:domain/streaming-logs",
        "AccessPolicies": "",
        "AdvancedOptions": {
            "rest.action.multi.allow_explicit_index": "true"
        },
        "Created": true,
        "Deleted": false,
        "DomainId": "123456789012/streaming-logs",
        "DomainName": "streaming-logs",
        "EBSOptions": {
            "EBSEnabled": false,
            "EncryptionEnabled": null,
            "Iops": null,
            "VolumeSize": null,
            "VolumeType": null
        },
        "ElasticsearchClusterConfig": {
            "DedicatedMasterCount": 3,
            "DedicatedMasterEnabled": true,
            "DedicatedMasterType": "m3.medium.elasticsearch",
            "InstanceCount": 6,
            "InstanceType": "m3.medium.elasticsearch",
            "ZoneAwarenessEnabled": true
        },
        "ElasticsearchVersion": "5.5",
        "EncryptionAtRestOptions": {
            "Enabled": true,
            "KmsKeyId": "arn:aws:kms:us-west-1:123456789012:key/1a2a3a4-1a2a-3a4a-5a6a-1a2a3a4a5a6a"
        },
        "Endpoint": null,
        "Endpoints": null,
        "Processing": true,
        "SnapshotOptions": {
            "AutomatedSnapshotStartHour": 0
        },
        "VPCOptions": {
            "AvailabilityZones": [
                "us-west-1b",
                "us-west-1c"
            ],
            "SecurityGroupIds": [
                "sg-12345678"
            ],
            "SubnetIds": [
                "subnet-87654321",
                "subnet-12345678"
            ],
            "VPCId": "vpc-12345678"
        }
    }
}
```

### DeleteElasticsearchDomain<a name="es-configuration-api-actions-deleteelasticsearchdomain"></a>

Deletes an Amazon ES domain and all of its data\. A domain cannot be recovered after it is deleted\.

#### Syntax<a name="w3ab1c43b7c15b5"></a>

```
DELETE /2015-01-01/es/domain/<DOMAIN_NAME>
```

#### Request Parameters<a name="w3ab1c43b7c15b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [`DomainName`](#es-configuration-api-datatypes-domainname) | Yes | Name of the Amazon ES domain that you want to delete\. | 

#### Request Body<a name="w3ab1c43b7c15b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w3ab1c43b7c15c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| DomainStatus | [ElasticsearchDomainStatus](#es-configuration-api-datatypes-elasticsearchdomainstatus) | Specifies the configuration of the specified Amazon ES domain\. | 

#### Errors<a name="w3ab1c43b7c15c13"></a>

The `DeleteElasticsearchDomain` operation can return any of the following errors:
+ [`BaseException`](#es-configuration-api-errors-baseexception)
+ [`InternalException`](#es-configuration-api-errors-internal)
+ [`ResourceNotFoundException`](#es-configuration-api-errors-resourcenotfound)
+ [`ValidationException`](#es-configuration-api-errors-validationexception)

#### Example<a name="w3ab1c43b7c15c15"></a>

The following example deletes the `weblogs` domain:

Request

```
DELETE es.<AWS_REGION>.amazonaws.com/2015-01-01/es/domain/weblogs
```

Response

```
HTTP/1.1 200 OK
{
    "DomainStatus": {
        "ARN": "arn:aws:es:us-west-1:123456789012:domain/weblogs",
        "AccessPolicies": "",
        "AdvancedOptions": {
            "rest.action.multi.allow_explicit_index": "true"
        },
        "Created": true,
        "Deleted": true,
        "DomainId": "123456789012/weblogs",
        "DomainName": "weblogs",
        "EBSOptions": {
            "EBSEnabled": false,
            "EncryptionEnabled": null,
            "Iops": null,
            "VolumeSize": null,
            "VolumeType": null
        },
        "ElasticsearchClusterConfig": {
            "DedicatedMasterCount": 3,
            "DedicatedMasterEnabled": true,
            "DedicatedMasterType": "m3.medium.elasticsearch",
            "InstanceCount": 6,
            "InstanceType": "m3.medium.elasticsearch",
            "ZoneAwarenessEnabled": true
        },
        "ElasticsearchVersion": "5.5",
        "EncryptionAtRestOptions": {
            "Enabled": true,
            "KmsKeyId": "arn:aws:kms:us-west-1:123456789012:key/1a2a3a4-1a2a-3a4a-5a6a-1a2a3a4a5a6a"
        },
        "Endpoint": null,
        "Endpoints": null,
        "Processing": true,
        "SnapshotOptions": {
            "AutomatedSnapshotStartHour": 0
        },
        "VPCOptions": {
            "AvailabilityZones": [
                "us-west-1b",
                "us-west-1c"
            ],
            "SecurityGroupIds": [
                "sg-12345678"
            ],
            "SubnetIds": [
                "subnet-87654321",
                "subnet-12345678"
            ],
            "VPCId": "vpc-12345678"
        }
    }
}
```

### DeleteElasticsearchServiceRole<a name="es-configuration-api-actions-deleteelasticsearchservicerole"></a>

Deletes the service\-linked role between Amazon ES and Amazon EC2\. This role gives Amazon ES permissions to place VPC endpoints into your VPC\. A service\-linked role must be in place for domains with VPC endpoints to be created or function properly\.

**Note**  
This action only succeeds if no domains are using the service\-linked role\.

#### Syntax<a name="w3ab1c43b7c17b7"></a>

```
DELETE /2015-01-01/es/role
```

#### Request Parameters<a name="w3ab1c43b7c17b9"></a>

This operation does not use request parameters\.

#### Request Body<a name="w3ab1c43b7c17c11"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w3ab1c43b7c17c13"></a>

Not applicable\. The `DeleteElasticsearchServiceRole` operation does not return a data structure\.

#### Errors<a name="w3ab1c43b7c17c15"></a>

`DeleteElasticsearchServiceRole` can return any of the following errors:
+ [ `BaseException`](#es-configuration-api-errors-baseexception)
+ [`InternalException`](#es-configuration-api-errors-internal)
+ [`ValidationException`](#es-configuration-api-errors-validationexception)

#### Example<a name="w3ab1c43b7c17c17"></a>

The following example demonstrates deletion of the service\-linked role:

Request

```
DELETE es.<AWS_REGION>.amazonaws.com/2015-01-01/es/role
```

Response

If successful, this action provides no response\.

### DescribeElasticsearchDomain<a name="es-configuration-api-actions-describeelasticsearchdomain"></a>

Describes the domain configuration for the specified Amazon ES domain, including the domain ID, domain service endpoint, and domain ARN\.

#### Syntax<a name="w3ab1c43b7c19b5"></a>

```
GET /2015-01-01/es/domain/<DOMAIN_NAME>
```

#### Request Parameters<a name="w3ab1c43b7c19b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [`DomainName`](#es-configuration-api-datatypes-domainname) | Yes | Name of the Amazon ES domain that you want to describe\. | 

#### Request Body<a name="w3ab1c43b7c19b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w3ab1c43b7c19c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| DomainStatus | [ElasticsearchDomainStatus](#es-configuration-api-datatypes-elasticsearchdomainstatus) | Configuration of the specified Amazon ES domain\. | 

#### Errors<a name="w3ab1c43b7c19c13"></a>

`DescribeElasticsearchDomain` can return any of the following errors:
+ [`BaseException`](#es-configuration-api-errors-baseexception)
+ [`InternalException`](#es-configuration-api-errors-internal)
+ [`ResourceNotFoundException`](#es-configuration-api-errors-resourcenotfound)
+ [`ValidationException`](#es-configuration-api-errors-validationexception)

#### Example<a name="w3ab1c43b7c19c15"></a>

The following example returns a description of the `streaming-logs` domain:

Request

```
GET es.<AWS_REGION>.amazonaws.com/2015-01-01/es/domain/streaming-logs
```

Response

```
{
    "DomainStatus": {
        "ARN": "arn:aws:es:us-west-1:123456789012:domain/streaming-logs",
        "AccessPolicies": "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"*\"},\"Action\":\"es:*\",\"Resource\":\"arn:aws:es:us-west-1:123456789012:domain/streaming-logs/*\",\"Condition\":{\"IpAddress\":{\"aws:SourceIp\":[\"11.222.333.11\",\"11.222.333.12\",\"11.222.333.13\",\"11.222.333.14\",\"11.222.333.15\"]}}}]}",
        "AdvancedOptions": {
            "rest.action.multi.allow_explicit_index": "true"
        },
        "Created": true,
        "Deleted": false,
        "DomainId": "123456789012/streaming-logs",
        "DomainName": "streaming-logs",
        "EBSOptions": {
            "EBSEnabled": true,
            "EncryptionEnabled": false,
            "Iops": null,
            "VolumeSize": 11,
            "VolumeType": "gp2"
        },
        "ElasticsearchClusterConfig": {
            "DedicatedMasterCount": 2,
            "DedicatedMasterEnabled": false,
            "DedicatedMasterType": "m4.large.elasticsearch",
            "InstanceCount": 2,
            "InstanceType": "t2.small.elasticsearch",
            "ZoneAwarenessEnabled": false
        },
        "ElasticsearchVersion": "5.5",
        "EncryptionAtRestOptions": {
            "Enabled": true,
            "KmsKeyId": "arn:aws:kms:us-west-1:123456789012:key/1a2a3a4-1a2a-3a4a-5a6a-1a2a3a4a5a6a"
        },
        "CognitoOptions": {
            "IdentityPoolId": "us-west-1:12345678-1234-1234-1234-123456789012",
            "RoleArn": "arn:aws:iam::123456789012:role/my-kibana-role",
            "Enabled": true,
            "UserPoolId": "us-west-1_121234567"
        },
        "Endpoint": "search-streaming-logs-oojmrbhufr27n44zdri52wukdy.us-west-1.es.amazonaws.com",
        "Endpoints": null,
        "Processing": false,
        "SnapshotOptions": {
            "AutomatedSnapshotStartHour": 8
        },
        "VPCOptions": null
    }
}
```

### DescribeElasticsearchDomainConfig<a name="es-configuration-api-actions-describeelasticsearchdomainconfig"></a>

Displays the configuration of an Amazon ES domain\.

#### Syntax<a name="w3ab1c43b7c21b5"></a>

```
GET /2015-01-01/es/domain/<DOMAIN_NAME>/config
```

#### Request Parameters<a name="w3ab1c43b7c21b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [`DomainName`](#es-configuration-api-datatypes-domainname) | Yes | Name of the Amazon ES domain\. | 

#### Request Body<a name="w3ab1c43b7c21b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w3ab1c43b7c21c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| DomainConfig | [`ElasticsearchDomainConfig`](#es-configuration-api-datatypes-esdomainconfig) | Configuration of the Amazon ES domain\. | 

#### Errors<a name="w3ab1c43b7c21c13"></a>

The `DescribeElasticsearchDomainConfig` operation can return any of the following errors:
+ [`BaseException`](#es-configuration-api-errors-baseexception)
+ [`InternalException`](#es-configuration-api-errors-internal)
+ [`ResourceNotFoundException`](#es-configuration-api-errors-resourcenotfound)

#### Example<a name="w3ab1c43b7c21c15"></a>

The following example returns a description of the configuration of the `logs` domain:

Request

```
GET es.<AWS_REGION>.amazonaws.com/2015-01-01/es/domain/logs/config
```

Response

```
HTTP/1.1 200 OK
{
    "DomainConfig": {
        "AccessPolicies": {
            "Options": "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"arn:aws:iam::123456789012:root\"},\"Action\":\"es:*\",\"Resource\":\"arn:aws:es:us-west-1:123456789012:domain/logs/*\"}]}",
            "Status": {
                "CreationDate": 1499817484.04,
                "PendingDeletion": false,
                "State": "Active",
                "UpdateDate": 1500308955.652,
                "UpdateVersion": 17
            }
        },
        "AdvancedOptions": {
            "Options": {
                "indices.fielddata.cache.size": "",
                "rest.action.multi.allow_explicit_index": "true"
            },
            "Status": {
                "CreationDate": 1499817484.04,
                "PendingDeletion": false,
                "State": "Active",
                "UpdateDate": 1499818054.108,
                "UpdateVersion": 5
            }
        },
        "EBSOptions": {
            "Options": {
                "EBSEnabled": true,
                "EncryptionEnabled": false,
                "Iops": 0,
                "VolumeSize": 10,
                "VolumeType": "gp2"
            },
            "Status": {
                "CreationDate": 1499817484.04,
                "PendingDeletion": false,
                "State": "Active",
                "UpdateDate": 1499818054.108,
                "UpdateVersion": 5
            }
        },
        "ElasticsearchClusterConfig": {
            "Options": {
                "DedicatedMasterCount": 2,
                "DedicatedMasterEnabled": false,
                "DedicatedMasterType": "m4.large.elasticsearch",
                "InstanceCount": 2,
                "InstanceType": "m4.large.elasticsearch",
                "ZoneAwarenessEnabled": false
            },
            "Status": {
                "CreationDate": 1499817484.04,
                "PendingDeletion": false,
                "State": "Active",
                "UpdateDate": 1499966854.612,
                "UpdateVersion": 13
            }
        },
        "ElasticsearchVersion": {
            "Options": "5.5",
            "Status": {
                "PendingDeletion": false,
                "State": "Active",
                "CreationDate": 1436913638.995,
                "UpdateVersion": 6,
                "UpdateDate": 1436914324.278
            },
            "Options": "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Sid\":\"\",\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"*\"},\"Action\":\"es:*\",\"Resource\":\"arn:aws:es:us-east-1:123456789012:domain/logs/*\"}]}"
        },
        "EncryptionAtRestOptions": {
            "Options": {
                "Enabled": true,
                "KmsKeyId": "arn:aws:kms:us-west-1:123456789012:key/1a2a3a4-1a2a-3a4a-5a6a-1a2a3a4a5a6a"
            },
            "Status": {
                "CreationDate": 1509490412.757,
                "PendingDeletion": false,
                "State": "Active",
                "UpdateDate": 1509490953.717,
                "UpdateVersion": 6
            }
        },
        "LogPublishingOptions":{
            "Status":{
                "CreationDate":1502774634.546,
                "PendingDeletion":false,
                "State":"Processing",
                "UpdateDate":1502779590.448,
                "UpdateVersion":60
            },
            "Options":{
                "INDEX_SLOW_LOGS":{
                    "CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:123456789012:log-group:sample-domain",
                    "Enabled":true
                },
                "SEARCH_SLOW_LOGS":{
                    "CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:123456789012:log-group:sample-domain",
                    "Enabled":true
                }
            }
        },
        "SnapshotOptions": {
            "Options": {
                "AutomatedSnapshotStartHour": 6
            },
            "Status": {
                "CreationDate": 1499817484.04,
                "PendingDeletion": false,
                "State": "Active",
                "UpdateDate": 1499818054.108,
                "UpdateVersion": 5
            }
        },
        "VPCOptions": {
            "Options": {
                "AvailabilityZones": [
                    "us-west-1b"
                ],
                "SecurityGroupIds": [
                    "sg-12345678"
                ],
                "SubnetIds": [
                    "subnet-12345678"
                ],
                "VPCId": "vpc-12345678"
            },
            "Status": {
                "CreationDate": 1499817484.04,
                "PendingDeletion": false,
                "State": "Active",
                "UpdateDate": 1499818054.108,
                "UpdateVersion": 5
            }
        }
    }
}
```

### DescribeElasticsearchDomains<a name="es-configuration-api-actions-describeesdomains"></a>

Describes the domain configuration for up to five specified Amazon ES domains\. Information includes the domain ID, domain service endpoint, and domain ARN\.

#### Syntax<a name="w3ab1c43b7c23b5"></a>

```
POST /2015-01-01/es/domain-info
{
    "DomainNames": [
        "<DOMAIN_NAME>",
        "<DOMAIN_NAME>",
    ]
}
```

#### Request Parameters<a name="w3ab1c43b7c23b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w3ab1c43b7c23b9"></a>


****  

| Field | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainNames | [DomainNameList](#es-configuration-api-datatypes-domainnamelist) | Yes | Array of Amazon ES domains in the following format:`{"DomainNames":["<Domain_Name>","<Domain_Name>"...]` | 

#### Response Elements<a name="w3ab1c43b7c23c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| DomainStatusList | [`ElasticsearchDomainStatusList`](#es-configuration-api-datatypes-esdomainstatuslist) | List that contains the status of each requested Amazon ES domain\. | 

#### Errors<a name="w3ab1c43b7c23c13"></a>

The `DescribeElasticsearchDomains` operation can return any of the following errors:
+ [`BaseException`](#es-configuration-api-errors-baseexception)
+ [`InternalException`](#es-configuration-api-errors-internal)
+ [`ValidationException`](#es-configuration-api-errors-validationexception)

#### Example<a name="w3ab1c43b7c23c15"></a>

The following example returns a description of the `logs` and `streaming-logs` domains:

Request

```
POST es.<AWS_REGION>.amazonaws.com/2015-01-01/es/domain-info/
{
    "DomainNames": [
        "logs",
        "streaming-logs"
    ]
}
```

Response

```
HTTP/1.1 200 OK
{
    "DomainStatusList": [
        {
            "ElasticsearchClusterConfig": {
                "DedicatedMasterEnabled": true,
                "InstanceCount": 3,
                "ZoneAwarenessEnabled": false,
                "DedicatedMasterType": "m3.medium.elasticsearch",
                "InstanceType": "m3.medium.elasticsearch",
                "DedicatedMasterCount": 3
            },
            "ElasticsearchVersion": "5.5",
            "EncryptionAtRestOptions": {
                "Enabled": true,
                "KmsKeyId": "arn:aws:kms:us-west-1:123456789012:key/1a2a3a4-1a2a-3a4a-5a6a-1a2a3a4a5a6a"
            },
            "Endpoint": "search-streaming-logs-okga24ftzsbz2a2hzhsqw73jpy.us-east-1.es.example.com",
            "Created": true,
            "Deleted": false,
            "DomainName": "streaming-logs",
            "EBSOptions": {
                "EBSEnabled": false
            },
            "VPCOptions": {
                "SubnetIds": [
                    "subnet-d1234567"
                ],
                "VPCId": "vpc-12345678",
                "SecurityGroupIds": [
                    "sg-123456789"
                ],
                "AvailabilityZones": [
                    "us-east-1"
                ]
            },
            "SnapshotOptions": {
                "AutomatedSnapshotStartHour": 0
            },
            "DomainId": "123456789012/streaming-logs",
            "AccessPolicies": "",
            "Processing": false,
            "AdvancedOptions": {
                "rest.action.multi.allow_explicit_index": "true",
                "indices.fielddata.cache.size": ""
            },
            "ARN": "arn:aws:es:us-east-1:123456789012:domain/streaming-logs"
        },
        {
            "ElasticsearchClusterConfig": {
                "DedicatedMasterEnabled": true,
                "InstanceCount": 1,
                "ZoneAwarenessEnabled": false,
                "DedicatedMasterType": "search.m3.medium",
                "InstanceType": "search.m3.xlarge",
                "DedicatedMasterCount": 3
            },
            "ElasticsearchVersion": "5.5",
            "EncryptionAtRestOptions": {
                "Enabled": true,
                "KmsKeyId": "arn:aws:kms:us-west-1:123456789012:key/1a2a3a4-1a2a-3a4a-5a6a-1a2a3a4a5a6a"
            },
            "Endpoint": "search-logs-p5st2kbt77diuihoqi6omd7jiu.us-east-1.es.example.com",
            "Created": true,
            "Deleted": false,
            "DomainName": "logs",
            "EBSOptions": {
                "Iops": 4000,
                "VolumeSize": 512,
                "VolumeType": "io1",
                "EBSEnabled": true
            },
            "VPCOptions": {
                "SubnetIds": [
                    "subnet-d1234567"
                ],
                "VPCId": "vpc-12345678",
                "SecurityGroupIds": [
                    "sg-123456789"
                ],
                "AvailabilityZones": [
                    "us-east-1"
                ]
            },
            "SnapshotOptions": {
                "AutomatedSnapshotStartHour": 0
            },
            "DomainId": "123456789012/logs",
            "AccessPolicies": "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Sid\":\"\",\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"*\"},\"Action\":\"es:*\",\"Resource\":\"arn:aws:es:us-east-1:123456789012:domain/logs/*\"}]}",
            "Processing": false,
            "AdvancedOptions": {
                "rest.action.multi.allow_explicit_index": "true"
            },
            "ARN": "arn:aws:es:us-east-1:123456789012:domain/logs"
        }
    ]
}
```

### DescribeElasticsearchInstanceTypeLimits<a name="es-configuration-api-actions-describeinstancetypelimits"></a>

Describes the instance count, storage, and master node limits for a given Elasticsearch version and instance type\.

#### Syntax<a name="w3ab1c43b7c25b5"></a>

```
GET 2015-01-01/es/instanceTypeLimits/{ElasticsearchVersion}/{InstanceType}?domainName={DomainName}
```

#### Request Parameters<a name="w3ab1c43b7c25b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| ElasticsearchVersion | String | Yes | Elasticsearch version\. For a list of supported versions, see [Supported Elasticsearch Versions](what-is-amazon-elasticsearch-service.md#aes-choosing-version)\. | 
| InstanceType | String | Yes | Instance type\. To view instance types by region, see [Amazon Elasticsearch Service Pricing](https://aws.amazon.com/elasticsearch-service/pricing/)\. | 
| DomainName | [`DomainName`](#es-configuration-api-datatypes-domainname) | No | The name of an existing domain\. Only specify if you need the limits for an existing domain\. | 

#### Request Body<a name="w3ab1c43b7c25b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w3ab1c43b7c25c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| LimitsByRole | Map | Map containing all applicable instance type limits\. "data" refers to data nodes\. "master" refers to dedicated master nodes\. | 

#### Errors<a name="w3ab1c43b7c25c13"></a>

The `DescribeElasticsearchInstanceTypeLimits` operation can return any of the following errors:
+ [`BaseException`](#es-configuration-api-errors-baseexception)
+ [`InternalException`](#es-configuration-api-errors-internal)
+ [`InvalidTypeException`](#es-configuration-api-errors-invalidtype)
+ [`LimitExceededException`](#es-configuration-api-errors-limitexceeded)
+ [`ResourceNotFoundException`](#es-configuration-api-errors-resourcenotfound)
+ [`ValidationException`](#es-configuration-api-errors-validationexception)

#### Example<a name="w3ab1c43b7c25c15"></a>

The following example returns a description of the `logs` and `streaming-logs` domains:

Request

```
GET es.<AWS_REGION>.amazonaws.com/2015-01-01/es/instanceTypeLimits/6.0/m4.large.elasticsearch
```

Response

```
HTTP/1.1 200 OK
{
    "LimitsByRole": {
        "data": {
            "AdditionalLimits": [
                {
                    "LimitName": "MaximumNumberOfDataNodesWithoutMasterNode",
                    "LimitValues": [
                        "10"
                    ]
                }
            ],
            "InstanceLimits": {
                "InstanceCountLimits": {
                    "MaximumInstanceCount": 20,
                    "MinimumInstanceCount": 1
                }
            },
            "StorageTypes": [
                {
                    "StorageSubTypeName": "standard",
                    "StorageTypeLimits": [
                        {
                            "LimitName": "MaximumVolumeSize",
                            "LimitValues": [
                                "100"
                            ]
                        },
                        {
                            "LimitName": "MinimumVolumeSize",
                            "LimitValues": [
                                "10"
                            ]
                        }
                    ],
                    "StorageTypeName": "ebs"
                },
                {
                    "StorageSubTypeName": "io1",
                    "StorageTypeLimits": [
                        {
                            "LimitName": "MaximumVolumeSize",
                            "LimitValues": [
                                "512"
                            ]
                        },
                        {
                            "LimitName": "MinimumVolumeSize",
                            "LimitValues": [
                                "35"
                            ]
                        },
                        {
                            "LimitName": "MaximumIops",
                            "LimitValues": [
                                "16000"
                            ]
                        },
                        {
                            "LimitName": "MinimumIops",
                            "LimitValues": [
                                "1000"
                            ]
                        }
                    ],
                    "StorageTypeName": "ebs"
                },
                {
                    "StorageSubTypeName": "gp2",
                    "StorageTypeLimits": [
                        {
                            "LimitName": "MaximumVolumeSize",
                            "LimitValues": [
                                "512"
                            ]
                        },
                        {
                            "LimitName": "MinimumVolumeSize",
                            "LimitValues": [
                                "10"
                            ]
                        }
                    ],
                    "StorageTypeName": "ebs"
                }
            ]
        },
        "master": {
            "AdditionalLimits": [
                {
                    "LimitName": "MaximumNumberOfDataNodesSupported",
                    "LimitValues": [
                        "100"
                    ]
                }
            ],
            "InstanceLimits": {
                "InstanceCountLimits": {
                    "MaximumInstanceCount": 5,
                    "MinimumInstanceCount": 2
                }
            },
            "StorageTypes": null
        }
    }
}
```

### DescribeReservedElasticsearchInstanceOfferings<a name="es-configuration-api-actions-describereservedelasticsearchinstanceofferings"></a>

Describes the available reserved instance offerings for a given region\.

#### Syntax<a name="w3ab1c43b7c27b5"></a>

```
GET /2015-01-01/es/reservedInstanceOfferings?offeringId={OfferingId}&maxResults={MaxResults}&nextToken={NextToken}
```

#### Request Parameters<a name="w3ab1c43b7c27b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| OfferingId | String | No | The offering ID\. | 
| MaxResults | Integer | No | Limits the number of results\. Must be between 30 and 100\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request Body<a name="w3ab1c43b7c27b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w3ab1c43b7c27c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ReservedElasticsearchInstanceOfferings | ReservedElasticsearchInstanceOfferings | Container for all information on a reserved instance offering\. To learn more, see [Purchasing Reserved Instances \(AWS CLI\)](aes-ri.md#aes-ri-cli)\. | 

#### Errors<a name="w3ab1c43b7c27c13"></a>

The `DescribeReservedElasticsearchInstanceOfferings` operation can return any of the following errors:
+ [`DisabledOperationException`](#es-configuration-api-errors-disabledoperation)
+ [`InternalException`](#es-configuration-api-errors-internal)
+ [`ResourceNotFoundException`](#es-configuration-api-errors-resourcenotfound)
+ [`ValidationException`](#es-configuration-api-errors-validationexception)

#### Example<a name="w3ab1c43b7c27c15"></a>

Request

```
GET es.<AWS_REGION>.amazonaws.com/2015-01-01/es/reservedInstanceOfferings
```

Response

```
{
  "ReservedElasticsearchInstanceOfferings": [
    {
      "FixedPrice": 100.0,
      "ReservedElasticsearchInstanceOfferingId": "1a2a3a4a5-1a2a-3a4a-5a6a-1a2a3a4a5a6a",
      "RecurringCharges": [
        {
          "RecurringChargeAmount": 0.603,
          "RecurringChargeFrequency": "Hourly"
        }
      ],
      "UsagePrice": 0.0,
      "PaymentOption": "PARTIAL_UPFRONT",
      "Duration": 31536000,
      "ElasticsearchInstanceType": "m4.2xlarge.elasticsearch",
      "CurrencyCode": "USD"
    }
  ]
}
```

### DescribeReservedElasticsearchInstances<a name="es-configuration-api-actions-describereservedelasticsearchinstances"></a>

Describes the instances you have reserved in a given region\.

#### Syntax<a name="w3ab1c43b7c29b5"></a>

```
GET 2015-01-01/es/reservedInstances?reservationId={ReservationId}&maxResults={PageSize}&nextToken={NextToken}
```

#### Request Parameters<a name="w3ab1c43b7c29b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| ReservationId | String | No | The reservation ID, assigned after you purchase a reservation\. | 
| MaxResults | Integer | No | Limits the number of results\. Must be between 30 and 100\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request Body<a name="w3ab1c43b7c29b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w3ab1c43b7c29c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ReservedElasticsearchInstances |  `ReservedElasticsearchInstances`  | Container for all information on the instance you have reserved\. To learn more, see [Purchasing Reserved Instances \(AWS CLI\)](aes-ri.md#aes-ri-cli)\. | 

#### Errors<a name="w3ab1c43b7c29c13"></a>

The `DescribeReservedElasticsearchInstances` operation can return any of the following errors:
+ [`DisabledOperationException`](#es-configuration-api-errors-disabledoperation)
+ [`InternalException`](#es-configuration-api-errors-internal)
+ [`ResourceNotFoundException`](#es-configuration-api-errors-resourcenotfound)
+ [`ValidationException`](#es-configuration-api-errors-validationexception)

#### Example<a name="w3ab1c43b7c29c15"></a>

Request

```
GET es.<AWS_REGION>.amazonaws.com/2015-01-01/es/reservedInstances
```

Response

```
{
  "ReservedElasticsearchInstances": [
    {
      "FixedPrice": 100.0,
      "ReservedElasticsearchInstanceOfferingId": "1a2a3a4a5-1a2a-3a4a-5a6a-1a2a3a4a5a6a",
      "ReservationName": "my-reservation",
      "PaymentOption": "PARTIAL_UPFRONT",
      "UsagePrice": 0.0,
      "ReservedElasticsearchInstanceId": "9a8a7a6a-5a4a-3a2a-1a0a-9a8a7a6a5a4a",
      "RecurringCharges": [
        {
          "RecurringChargeAmount": 0.603,
          "RecurringChargeFrequency": "Hourly"
        }
      ],
      "State": "payment-pending",
      "StartTime": 1522872571.229,
      "ElasticsearchInstanceCount": 3,
      "Duration": 31536000,
      "ElasticsearchInstanceType": "m4.2xlarge.elasticsearch",
      "CurrencyCode": "USD"
    }
  ]
}
```

### ListDomainNames<a name="es-configuration-api-actions-listdomainnames"></a>

Displays the names of all Amazon ES domains owned by the current user *in the active region*\.

#### Syntax<a name="w3ab1c43b7c31b5"></a>

```
GET /2015-01-01/domain
```

#### Request Parameters<a name="w3ab1c43b7c31b7"></a>

This operation does not use request parameters\.

#### Request Body<a name="w3ab1c43b7c31b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w3ab1c43b7c31c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| DomainNameList | [`DomainNameList`](#es-configuration-api-datatypes-domainnamelist) | The names of all Amazon ES domains owned by the current user\. | 

#### Errors<a name="w3ab1c43b7c31c13"></a>

The `ListDomainNames` operation can return any of the following errors:
+ [BaseException](#es-configuration-api-errors-baseexception)
+ [ValidationException](#es-configuration-api-errors-validationexception)

#### Example<a name="w3ab1c43b7c31c15"></a>

The following example lists all three domains owned by the current user:

Request

```
GET es.<AWS_REGION>.amazonaws.com/2015-01-01/domain
```

Response

```
{
    "DomainNames": [
        {
            "DomainName": "logs"
        },
        {
            "DomainName": "streaming-logs"
        }
    ]
}
```

### ListElasticsearchInstanceTypeDetails<a name="es-configuration-api-actions-listelasticsearchinstancetypedetails"></a>

Lists all Elasticsearch instance types that are supported for a given Elasticsearch version and the features that these instance types support\.

#### Syntax<a name="w3ab1c43b7c33b5"></a>

```
GET 2015-01-01/es/instanceTypeDetails/{ElasticsearchVersion}?domainName={DomainName}&maxResults={MaxResults}&nextToken={NextToken}
```

#### Request Parameters<a name="w3ab1c43b7c33b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| ElasticsearchVersion | String | Yes | The Elasticsearch version\. | 
| DomainName | String | No | The Amazon ES domain name\. | 
| MaxResults | Integer | No | Limits the number of results\. Must be between 30 and 100\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request Body<a name="w3ab1c43b7c33b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w3ab1c43b7c33c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ElasticsearchInstanceTypes | List | List of supported instance types for the given Elasticsearch version and the features that these instance types support\. | 
| NextToken | String |  Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page and provides a next\-token output in the response, which clients can use to retrieve more results\.  | 

#### Errors<a name="w3ab1c43b7c33c13"></a>

`ListElasticsearchInstanceTypeDetails` can return any of the following errors:
+ [ `BaseException`](#es-configuration-api-errors-baseexception)
+ [`InternalException`](#es-configuration-api-errors-internal)
+ [`ResourceNotFoundException`](#es-configuration-api-errors-resourcenotfound)
+ [`ValidationException`](#es-configuration-api-errors-validationexception)

#### Example<a name="w3ab1c43b7c33c15"></a>

Request

```
GET es.us-west-1.amazonaws.com/2015-01-01/es/instanceTypeDetails/6.2
```

Response

```
{
    "ElasticsearchInstanceTypeDetails": [
        {
            "AppLogsEnabled": true,
            "CognitoEnabled": true,
            "EncryptionEnabled": false,
            "InstanceType": "t2.small.elasticsearch"
        },
        {
            "AppLogsEnabled": true,
            "CognitoEnabled": true,
            "EncryptionEnabled": false,
            "InstanceType": "t2.medium.elasticsearch"
        },
        {
            "AppLogsEnabled": true,
            "CognitoEnabled": true,
            "EncryptionEnabled": true,
            "InstanceType": "c4.large.elasticsearch"
        },
        {
            "AppLogsEnabled": true,
            "CognitoEnabled": true,
            "EncryptionEnabled": true,
            "InstanceType": "c4.xlarge.elasticsearch"
        },
        ...
    ],
    "NextToken": null
}
```

### ListElasticsearchInstanceTypes \(Deprecated\)<a name="es-configuration-api-actions-listelasticsearchinstancetypes"></a>

Lists all Elasticsearch instance types that are supported for a given Elasticsearch version\. This action is deprecated\. Use [ListElasticsearchInstanceTypeDetails](#es-configuration-api-actions-listelasticsearchinstancetypedetails) instead\.

#### Syntax<a name="w3ab1c43b7c35b5"></a>

```
GET 2015-01-01/es/instanceTypes/{ElasticsearchVersion}?domainName={DomainName}&maxResults={MaxResults}&nextToken={NextToken}
```

#### Request Parameters<a name="w3ab1c43b7c35b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| ElasticsearchVersion | String | Yes | The Elasticsearch version\. | 
| DomainName | String | No | The Amazon ES domain name\. | 
| MaxResults | Integer | No | Limits the number of results\. Must be between 30 and 100\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request Body<a name="w3ab1c43b7c35b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w3ab1c43b7c35c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ElasticsearchInstanceTypes | List | List of supported instance types for the given Elasticsearch version\. | 
| NextToken | String |  Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page and provides a next\-token output in the response, which clients can use to retrieve more results\.  | 

#### Errors<a name="w3ab1c43b7c35c13"></a>

`ListElasticsearchInstanceTypes` can return any of the following errors:
+ [ `BaseException`](#es-configuration-api-errors-baseexception)
+ [`InternalException`](#es-configuration-api-errors-internal)
+ [`ResourceNotFoundException`](#es-configuration-api-errors-resourcenotfound)
+ [`ValidationException`](#es-configuration-api-errors-validationexception)

#### Example<a name="w3ab1c43b7c35c15"></a>

Request

```
GET es.<AWS_REGION>.amazonaws.com/2015-01-01/es/instanceTypes/6.0
```

Response

```
{
    "ElasticsearchInstanceTypes": [
        "t2.small.elasticsearch",
        "t2.medium.elasticsearch",
        "r4.large.elasticsearch",
        "r4.xlarge.elasticsearch",
        "r4.2xlarge.elasticsearch",
        "r4.4xlarge.elasticsearch",
        "r4.8xlarge.elasticsearch",
        "r4.16xlarge.elasticsearch",
        "m4.large.elasticsearch",
        "m4.xlarge.elasticsearch",
        "m4.2xlarge.elasticsearch",
        "m4.4xlarge.elasticsearch",
        "m4.10xlarge.elasticsearch",
        "c4.large.elasticsearch",
        "c4.xlarge.elasticsearch",
        "c4.2xlarge.elasticsearch",
        "c4.4xlarge.elasticsearch",
        "c4.8xlarge.elasticsearch"
    ],
    "NextToken": null
}
```

### ListElasticsearchVersions<a name="es-configuration-api-actions-listelasticsearchversions"></a>

Lists all supported Elasticsearch versions on Amazon ES\.

#### Syntax<a name="w3ab1c43b7c37b5"></a>

```
GET 2015-01-01/es/versions?maxResults={MaxResults}&nextToken={NextToken}
```

#### Request Parameters<a name="w3ab1c43b7c37b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| MaxResults | Integer | No | Limits the number of results\. Must be between 30 and 100\. | 
| NextToken | String | No | Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page and provides a next\-token output in the response, which clients can use to retrieve more results\. | 

#### Request Body<a name="w3ab1c43b7c37b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w3ab1c43b7c37c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ElasticsearchVersions | List | Lists all supported Elasticsearch versions\. | 
| NextToken | String |  Used for pagination\. Only necessary if a previous API call produced a result containing NextToken\. Accepts a next\-token input to return results for the next page and provides a next\-token output in the response, which clients can use to retrieve more results\.  | 

#### Errors<a name="w3ab1c43b7c37c13"></a>

`ListElasticsearchVersions` can return any of the following errors:
+ [ `BaseException`](#es-configuration-api-errors-baseexception)
+ [`InternalException`](#es-configuration-api-errors-internal)
+ [`ResourceNotFoundException`](#es-configuration-api-errors-resourcenotfound)
+ [`ValidationException`](#es-configuration-api-errors-validationexception)

#### Example<a name="w3ab1c43b7c37c15"></a>

Request

```
GET es.<AWS_REGION>.amazonaws.com/2015-01-01/es/versions
```

Response

```
{
    "ElasticsearchVersions": [
        "6.0",
        "5.5",
        "5.3",
        "5.1",
        "2.3",
        "1.5"
    ],
    "NextToken": null
}
```

### ListTags<a name="es-configuration-api-actions-listtags"></a>

Displays all resource tags for an Amazon ES domain\.

#### Syntax<a name="w3ab1c43b7c39b5"></a>

```
GET /2015-01-01/tags?arn=<DOMAIN_ARN>
```

#### Request Parameters<a name="w3ab1c43b7c39b7"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| ARN | [`ARN`](#es-configuration-api-datatypes-arn) | Yes | Amazon Resource Name \(ARN\) for the Amazon ES domain\. | 

#### Request Body<a name="w3ab1c43b7c39b9"></a>

This operation does not use the HTTP request body\.

#### Response Elements<a name="w3ab1c43b7c39c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| TagList | [`TagList`](#es-configuration-api-datatypes-taglist) | List of resource tags\. For more information, see [Tagging Amazon Elasticsearch Service Domains](es-managedomains.md#es-managedomains-awsresourcetagging)\. | 

#### Errors<a name="w3ab1c43b7c39c13"></a>

The `ListTags` operation can return any of the following errors:
+ [`BaseException`](#es-configuration-api-errors-baseexception)
+ [`ResourceNotFoundException`](#es-configuration-api-errors-resourcenotfound)
+ [`ValidationException`](#es-configuration-api-errors-validationexception)
+ [`InternalException`](#es-configuration-api-errors-internal)

#### Example<a name="w3ab1c43b7c39c15"></a>

The following example lists the tags attached to the `logs` domain:

Request

```
GET es.<AWS_REGION>.amazonaws.com/2015-01-01/tags?arn=arn:aws:es:us-west-1:123456789012:domain/logs
```

Response

```
HTTP/1.1 200 OK
{
    "TagList": [
        {
            "Key": "Environment",
            "Value": "MacOS"
        },
        {
            "Key": "project",
            "Value": "trident"
        }
    ]
}
```

### PurchaseReservedElasticsearchInstance<a name="es-configuration-api-actions-purchasereservedelasticsearchinstance"></a>

Purchase a reserved instance\.

#### Syntax<a name="w3ab1c43b7c41b5"></a>

```
POST /2015-01-01/es/purchaseReservedInstanceOffering
```

#### Request Parameters<a name="w3ab1c43b7c41b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w3ab1c43b7c41b9"></a>


****  

| Name | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| ReservationName | String | Yes | A descriptive name for your reservation\. | 
|  ReservedElasticsearchInstanceOfferingId  | String | Yes | The offering ID\. | 
| InstanceCount | Integer | Yes | The number of instances you want to reserve\. | 

#### Response Elements<a name="w3ab1c43b7c41c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ReservationName | String | The name of your reservation\. | 
|  ReservedElasticsearchInstanceId | String | The reservation ID\. | 

#### Errors<a name="w3ab1c43b7c41c13"></a>

The `PurchaseReservedElasticsearchInstance` operation can return any of the following errors:
+ [`DisabledOperationException`](#es-configuration-api-errors-disabledoperation)
+ [`InternalException`](#es-configuration-api-errors-internal)
+ [`ResourceNotFoundException`](#es-configuration-api-errors-resourcenotfound)
+ [`ValidationException`](#es-configuration-api-errors-validationexception)
+ [`LimitExceededException`](#es-configuration-api-errors-limitexceeded)
+ [`ResourceAlreadyExistsException`](#es-configuration-api-errors-resourcealreadyexists)

#### Example<a name="w3ab1c43b7c41c15"></a>

Request

```
POST es.<AWS_REGION>.amazonaws.com/2015-01-01/es/purchaseReservedInstanceOffering
{
  "ReservationName" : "my-reservation",
  "ReservedElasticsearchInstanceOfferingId" : "1a2a3a4a5-1a2a-3a4a-5a6a-1a2a3a4a5a6a",
  "InstanceCount" : 3
}
```

Response

```
{
  "ReservationName": "my-reservation",
  "ReservedElasticsearchInstanceId": "9a8a7a6a-5a4a-3a2a-1a0a-9a8a7a6a5a4a"
}
```

### RemoveTags<a name="es-configuration-api-actions-removetags"></a>

Removes the specified resource tags from an Amazon ES domain\.

#### Syntax<a name="w3ab1c43b7c43b5"></a>

```
POST es.<AWS_REGION>.amazonaws.com/2015-01-01/tags-removal
{
    "ARN": "<DOMAIN_ARN>",
    "TagKeys": [
        "<TAG_KEY>",
        "<TAG_KEY>",
        ...
    ]
}
```

#### Request Parameters<a name="w3ab1c43b7c43b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w3ab1c43b7c43b9"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| ARN | [`ARN`](#es-configuration-api-datatypes-arn) | Yes | Amazon Resource Name \(ARN\) of an Amazon ES domain\. For more information, see [Identifiers for IAM Entities](http://docs.aws.amazon.com/IAM/latest/UserGuide/index.html?Using_Identifiers.html) in Using AWS Identity and Access Management\. | 
| TagKeys | [`TagKey`](#es-configuration-api-datatypes-tagkey) | Yes | List of tag keys for resource tags that you want to remove from an Amazon ES domain\. | 

#### Response Elements<a name="w3ab1c43b7c43c11"></a>

Not applicable\. The `RemoveTags` operation does not return a response element\.

#### Errors<a name="w3ab1c43b7c43c13"></a>

The `RemoveTags` operation can return any of the following errors:
+ [`BaseException`](#es-configuration-api-errors-baseexception)
+ [`ValidationException`](#es-configuration-api-errors-validationexception)
+ [`InternalException`](#es-configuration-api-errors-internal)

#### Example<a name="w3ab1c43b7c43c15"></a>

The following example deletes a resource tag with a tag key of `project` from the Amazon ES domain:

Request

```
POST /2015-01-01/tags-removal
{
    "ARN": "<DOMAIN_ARN>",
    "TagKeys": [
        "project"
    ]
}
```

This operation does not return a response element\.

### UpdateElasticsearchDomainConfig<a name="es-configuration-api-actions-updateelasticsearchdomainconfig"></a>

Modifies the configuration of an Amazon ES domain, such as the instance type and the number of instances\. You only need to specify the values that you want to update\.

#### Syntax<a name="w3ab1c43b7c45b5"></a>

```
POST /2015-01-01/es/domain/<DOMAIN_NAME>/config
{
    "ElasticsearchClusterConfig": {
        "InstanceType": "<INSTANCE_TYPE>",
        "Instance_Count": <INSTANCE_COUNT>,
        "DedicatedMasterEnabled": "<TRUE|FALSE>",
        "DedicatedMasterCount": <INSTANCE_COUNT>,
        "DedicatedMasterType": "<INSTANCE_COUNT>",
        "ZoneAwarenessEnabled": "<TRUE|FALSE>"
    },
    "EBSOptions": {
        "EBSEnabled": "<TRUE|FALSE>",
        "VolumeType": "<VOLUME_TYPE>",
        "VolumeSize": "<VOLUME_SIZE>",
        "Iops": "<VALUE>"
    },
    "VPCOptions": {
        "SubnetIds": [
            "<SUBNET_ID>"
        ],
        "SecurityGroupIds": [
            "<SECURITY_GROUP_ID>"
        ]
    },
    "AccessPolicies": "<ACCESS_POLICY_DOCUMENT>",
    "SnapshotOptions": {
        "AutomatedSnapshotStartHour": <START_HOUR>,
        "AdvancedOptions": {
            "rest.action.multi.allow_explicit_index": "<TRUE|FALSE>",
            "indices.fielddata.cache.size": "<PERCENTAGE_OF_HEAP>"
        }
    },
    "LogPublishingOptions": {
        "SEARCH_SLOW_LOGS": {
            "CloudWatchLogsLogGroupArn":"<ARN>",
            "Enabled":true
        },
        "INDEX_SLOW_LOGS": {
            "CloudWatchLogsLogGroupArn":"<ARN>",
            "Enabled":true
        }
    }
}
```

#### Request Parameters<a name="w3ab1c43b7c45b7"></a>

This operation does not use HTTP request parameters\.

#### Request Body<a name="w3ab1c43b7c45b9"></a>


****  

| Parameter | Data Type | Required? | Description | 
| --- | --- | --- | --- | 
| DomainName | [`DomainName`](#es-configuration-api-datatypes-domainname) | Yes | Name of the Amazon ES domain for which you want to update the configuration\. | 
| ElasticsearchClusterConfig | [`ElasticsearchClusterConfig`](#es-configuration-api-datatypes-elasticsearchclusterconfig) | No | Desired changes to the cluster configuration, such as the instance type and number of EC2 instances\. | 
| EBSOptions | [`EBSOptions`](#es-configuration-api-datatypes-ebsoptions) | No | Type and size of EBS volumes attached to data nodes\.  | 
| VPCOptions | [`VPCOptions`](#es-configuration-api-datatypes-vpcoptions) | No | Container for the values required to configure Amazon ES to work with a VPC\. To learn more, see [VPC Support for Amazon Elasticsearch Service Domains](es-vpc.md)\. | 
| SnapshotOptions | [`SnapshotOptions`](#es-configuration-api-datatypes-snapshotoptions) | No | Hour during which the service takes an automated daily snapshot of the indices in the Amazon ES domain\. | 
| AdvancedOptions | [`AdvancedOptions`](#es-configuration-api-datatypes-advancedoptions) | No | Key\-value pairs to specify advanced configuration options\. For more information, see [Configuring Advanced Options](es-createupdatedomains.md#es-createdomain-configure-advanced-options)\. | 
| AccessPolicies | String | No | Specifies the access policies for the Amazon ES domain\. For more information, see [Configuring Access Policies](es-createupdatedomains.md#es-createdomain-configure-access-policies)\. | 
| LogPublishingOptions | [`LogPublishingOptions`](#es-configuration-api-datatypes-logpublishingoptions) | No | Key\-value string pairs to configure slow log publishing\. | 
| CognitoOptions | [`CognitoOptions`](#es-configuration-api-datatypes-cognitooptions) | No | Key\-value pairs to configure Amazon ES to use Amazon Cognito authentication for Kibana\. | 

#### Response Elements<a name="w3ab1c43b7c45c11"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| DomainConfig | String | Status of the Amazon ES domain after updating its configuration\. | 

#### Errors<a name="w3ab1c43b7c45c13"></a>

`UpdateElasticsearchDomainConfig` can return any of the following errors:
+ [`BaseException`](#es-configuration-api-errors-baseexception)
+ [`InternalException`](#es-configuration-api-errors-internal)
+ [`InvalidTypeException`](#es-configuration-api-errors-invalidtype)
+ [`LimitExceededException`](#es-configuration-api-errors-limitexceeded)
+ [`ValidationException`](#es-configuration-api-errors-validationexception)

#### Example<a name="w3ab1c43b7c45c15"></a>

The following example configures the daily automatic snapshot for the `streaming-logs` domain to occur during the hour starting at 3:00 AM GMT:

Request

```
POST es.<AWS_REGION>.amazonaws.com/2015-01-01/es/domain/streaming-logs/config
{
    "SnapshotOptions": {
        "AutomatedSnapshotStartHour": 3
    }
}
```

Response

```
{
    "DomainConfig": {
        "AccessPolicies": {
            "Options": "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"*\"},\"Action\":\"es:*\",\"Resource\":\"arn:aws:es:us-west-1:123456789012:domain/streaming-logs/*\",\"Condition\":{\"IpAddress\":{\"aws:SourceIp\":[\"11.222.333.11\",\"11.222.333.12\",\"11.222.333.13\",\"11.222.333.14\",\"11.222.333.15\"]}}}]}",
            "Status": {
                "CreationDate": 1502213150.329,
                "PendingDeletion": false,
                "State": "Active",
                "UpdateDate": 1502213466.93,
                "UpdateVersion": 6
            }
        },
        "AdvancedOptions": {
            "Options": {
                "rest.action.multi.allow_explicit_index": "true"
            },
            "Status": {
                "CreationDate": 1502213150.329,
                "PendingDeletion": false,
                "State": "Active",
                "UpdateDate": 1502213466.93,
                "UpdateVersion": 6
            }
        },
        "EBSOptions": {
            "Options": {
                "EBSEnabled": true,
                "EncryptionEnabled": false,
                "Iops": null,
                "VolumeSize": 11,
                "VolumeType": "gp2"
            },
            "Status": {
                "CreationDate": 1502213150.329,
                "PendingDeletion": false,
                "State": "Active",
                "UpdateDate": 1502929669.653,
                "UpdateVersion": 23
            }
        },
        "ElasticsearchClusterConfig": {
            "Options": {
                "DedicatedMasterCount": 2,
                "DedicatedMasterEnabled": false,
                "DedicatedMasterType": "m4.large.elasticsearch",
                "InstanceCount": 2,
                "InstanceType": "t2.small.elasticsearch",
                "ZoneAwarenessEnabled": false
            },
            "Status": {
                "CreationDate": 1502213150.329,
                "PendingDeletion": false,
                "State": "Active",
                "UpdateDate": 1502929669.653,
                "UpdateVersion": 23
            }
        },
        "ElasticsearchVersion": {
            "Options": "5.5",
            "Status": {
                "CreationDate": 1502213150.329,
                "PendingDeletion": false,
                "State": "Active",
                "UpdateDate": 1502213466.93,
                "UpdateVersion": 6
            }
        },
        "EncryptionAtRestOptions": {
            "Options": {
                "Enabled": true,
                "KmsKeyId": "arn:aws:kms:us-west-1:123456789012:key/1a2a3a4-1a2a-3a4a-5a6a-1a2a3a4a5a6a"
            },
            "Status": {
                "CreationDate": 1509490412.757,
                "PendingDeletion": false,
                "State": "Active",
                "UpdateDate": 1509490953.717,
                "UpdateVersion": 6
            }
        },
        "LogPublishingOptions":{
            "Options":{
                "INDEX_SLOW_LOGS":{
                    "CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:123456789012:log-group:sample-domain",
                    "Enabled":true
                },
                "SEARCH_SLOW_LOGS":{
                    "CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:123456789012:log-group:sample-domain",
                    "Enabled":true
                }
            },
            "Status":{
                "CreationDate":1502774634.546,
                "PendingDeletion":false,
                "State":"Processing",
                "UpdateDate":1502779590.448,
                "UpdateVersion":60
            }
        },
        "SnapshotOptions": {
            "Options": {
                "AutomatedSnapshotStartHour": 3
            },
            "Status": {
                "CreationDate": 1502213150.329,
                "PendingDeletion": false,
                "State": "Active",
                "UpdateDate": 1503093165.447,
                "UpdateVersion": 25
            }
        },
        "VPCOptions": {
            "Options": {
                "AvailabilityZones": null,
                "SecurityGroupIds": null,
                "SubnetIds": null,
                "VPCId": null
            },
            "Status": {
                "CreationDate": 1503093165.597,
                "PendingDeletion": false,
                "State": "Active",
                "UpdateDate": 1503093165.597,
                "UpdateVersion": 25
            }
        }
    }
}
```

## Data Types<a name="es-configuration-api-datatypes"></a>

This section describes the data types used by the REST Configuration API\.

### AdvancedOptions<a name="es-configuration-api-datatypes-advancedoptions"></a>

Key\-value string pairs to specify advanced Elasticsearch configuration options\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| rest\.action\.multi\.allow\_explicit\_index | Key\-value pair:`rest.action.multi.allow_explicit_index=<true\|false>` | Specifies whether explicit references to indices are allowed inside the body of HTTP requests\. If you want to configure access policies for domain sub\-resources, such as specific indices and domain APIs, you must disable this property\. For more information, see URL\-based Access Control\. For more information about access policies for sub\-resources, see [Configuring Access Policies](es-createupdatedomains.md#es-createdomain-configure-access-policies)\. | 
| indices\.fielddata\.cache\.size | Key\-value pair:`indices.fielddata.cache.size=<percentage_of_heap>` | Specifies the percentage of Java heap space that is allocated to field data\. By default, this setting is unbounded\. | 
| indices\.query\.bool\.max\_clause\_count | Key\-value pair:`indices.query.bool.max_clause_count=<int>` | Specifies the maximum number of clauses allowed in a Lucene boolean query\. 1024 is the default\. Queries with more than the permitted number of clauses result in a TooManyClauses error\. To learn more, see [the Lucene documentation](https://lucene.apache.org/core/6_6_0/core/org/apache/lucene/search/BooleanQuery.html)\. | 

### AdvancedOptionsStatus<a name="es-configuration-api-datatypes-advancedoptionsstatus"></a>

Status of an update to advanced configuration options for an Amazon ES domain\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Options | [`AdvancedOptions`](#es-configuration-api-datatypes-advancedoptions) | Key\-value pairs to specify advanced Elasticsearch configuration options\. | 
| Status | [`OptionStatus`](#es-configuration-api-datatypes-optionstatus) | Status of an update to advanced configuration options for an Amazon ES domain\. | 

### ARN<a name="es-configuration-api-datatypes-arn"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ARN | String | Amazon Resource Name \(ARN\) of an Amazon ES domain\. For more information, see [IAM ARNs](http://docs.aws.amazon.com/IAM/latest/UserGuide/reference_identifiers.html#identifiers-arns) in the AWS Identity and Access Management documentation\. | 

### CognitoOptions<a name="es-configuration-api-datatypes-cognitooptions"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Enabled | Boolean | Whether to enable or disable Amazon Cognito authentication for Kibana\. See [Amazon Cognito Authentication for Kibana](es-cognito-auth.md)\. | 
| UserPoolId | String | The Amazon Cognito user pool ID that you want Amazon ES to use for Kibana authentication\. | 
| IdentityPoolId | String | The Amazon Cognito identity pool ID that you want Amazon ES to use for Kibana authentication\. | 
| RoleArn | String | The AmazonESCognitoAccess role that allows Amazon ES to configure your user pool and identity pool\. | 

### CognitoOptionsStatus<a name="es-configuration-api-datatypes-cognitooptionsstatus"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Options | [`CognitoOptions`](#es-configuration-api-datatypes-cognitooptions) | Key\-value pairs to configure Amazon ES to use Amazon Cognito authentication for Kibana\. | 
| Status | [`OptionStatus`](#es-configuration-api-datatypes-optionstatus) | Status of an update to the Cognito configuration options for an Amazon ES domain\. | 

### CreateElasticsearchDomainRequest<a name="es-configuration-api-datatypes-createesdomainrequest"></a>

Container for the parameters required by the `CreateElasticsearchDomain` service operation\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| DomainName | [`DomainName`](#es-configuration-api-datatypes-domainname) | Name of the Amazon ES domain to create\. | 
| ElasticsearchClusterConfig | [`ElasticsearchClusterConfig`](#es-configuration-api-datatypes-elasticsearchclusterconfig) | Container for the cluster configuration of an Amazon ES domain\. | 
| EBSOptions | [`EBSOptions`](#es-configuration-api-datatypes-ebsoptions) | Container for the parameters required to enable EBS\-based storage for an Amazon ES domain\. For more information, see [Configuring EBS\-based Storage](es-createupdatedomains.md#es-createdomain-configure-ebs)\. | 
| AccessPolicies | String | IAM policy document specifying the access policies for the new Amazon ES domain\. For more information, see [Configuring Access Policies](es-createupdatedomains.md#es-createdomain-configure-access-policies)\. | 
| SnapshotOptions | [`SnapshotOptionsStatus`](#es-configuration-api-datatypes-snapshotoptionsstatus) | Container for parameters required to configure automated snapshots of domain indices\. For more information, see [Configuring Snapshots](es-createupdatedomains.md#es-createdomain-configure-snapshots)\. | 
| VPCOptions | [`VPCOptions`](#es-configuration-api-datatypes-vpcoptions) | Container for the values required to configure Amazon ES to work with a VPC\. | 
| LogPublishingOptions | [`LogPublishingOptions`](#es-configuration-api-datatypes-logpublishingoptions) | Key\-value string pairs to configure slow log publishing\. | 
| SnapshotOptions | [`SnapshotOptionsStatus`](#es-configuration-api-datatypes-snapshotoptionsstatus) | Container for parameters required to configure automated snapshots of domain indices\. For more information, see [Configuring Snapshots](es-createupdatedomains.md#es-createdomain-configure-snapshots)\. | 
| AdvancedOptions | [`AdvancedOptionsStatus`](#es-configuration-api-datatypes-advancedoptionsstatus) | Key\-value pairs to specify advanced configuration options\. | 
| CognitoOptions | [`CognitoOptions`](#es-configuration-api-datatypes-cognitooptions) | Key\-value pairs to configure Amazon ES to use Amazon Cognito authentication for Kibana\. | 

### DomainID<a name="es-configuration-api-datatypes-domainid"></a>


****  

| Data Type | Description | 
| --- | --- | 
| String | Unique identifier for an Amazon ES domain  | 

### DomainName<a name="es-configuration-api-datatypes-domainname"></a>

Name of an Amazon ES domain\. 


****  

| Data Type | Description | 
| --- | --- | 
| String | Name of an Amazon ES domain\. Domain names are unique across all domains owned by the same account within an AWS region\. Domain names must start with a lowercase letter and must be between 3 and 28 characters\. Valid characters are a\-z \(lowercase only\), 0\-9, and – \(hyphen\)\. | 

### DomainNameList<a name="es-configuration-api-datatypes-domainnamelist"></a>

String of Amazon ES domain names\.


****  

| Data Type | Description | 
| --- | --- | 
| String Array | Array of Amazon ES domains in the following format:`["<Domain_Name>","<Domain_Name>"...]` | 

### EBSOptions<a name="es-configuration-api-datatypes-ebsoptions"></a>

Container for the parameters required to enable EBS\-based storage for an Amazon ES domain\. For more information, see [Configuring EBS\-based Storage](es-createupdatedomains.md#es-createdomain-configure-ebs)\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| EBSEnabled | Boolean | Indicates whether EBS volumes are attached to data nodes in an Amazon ES domain\. | 
| VolumeType | String | Specifies the type of EBS volumes attached to data nodes\.  | 
| VolumeSize | String | Specifies the size of EBS volumes attached to data nodes\. | 
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
| ZoneAwarenessEnabled | Boolean | Indicates whether zone awareness is enabled\. Zone awareness allocates the nodes and replica index shards belonging to a cluster across two Availability Zones in the same region\.If you enable zone awareness, you must have an even number of instances in the instance count, and you also must use the Amazon ES Configuration API to replicate your data for your Elasticsearch cluster\. For more information, see [Enabling Zone Awareness](es-managedomains.md#es-managedomains-zoneawareness)\. | 

### ElasticsearchDomainConfig<a name="es-configuration-api-datatypes-esdomainconfig"></a>

Container for the configuration of an Amazon ES domain\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ElasticsearchVersion | String | Elasticsearch version\. | 
| ElasticsearchClusterConfig | [`ElasticsearchClusterConfig`](#es-configuration-api-datatypes-elasticsearchclusterconfig) | Container for the cluster configuration of an Amazon ES domain\. | 
| EBSOptions | [`EBSOptions`](#es-configuration-api-datatypes-ebsoptions) | Container for EBS options configured for an Amazon ES domain\. | 
| AccessPolicies | String | Specifies the access policies for the Amazon ES domain\. For more information, see [Configuring Access Policies](es-createupdatedomains.md#es-createdomain-configure-access-policies)\. | 
| SnapshotOptions | [`SnapshotOptionsStatus`](#es-configuration-api-datatypes-snapshotoptionsstatus) | Hour during which the service takes an automated daily snapshot of the indices in the Amazon ES domain\. For more information, see [Configuring Snapshots](es-createupdatedomains.md#es-createdomain-configure-snapshots)\. | 
| VPCOptions | [`VPCDerivedInfoStatus`](#es-configuration-api-datatypes-vpcderivedinfostatus) | The current [VPCOptions](#es-configuration-api-datatypes-vpcoptions) for the domain and the status of any updates to their configuration\. | 
| LogPublishingOptions | [`LogPublishingOptions`](#es-configuration-api-datatypes-logpublishingoptions) | Key\-value pairs to configure slow log publishing\. | 
| AdvancedOptions | [`AdvancedOptionsStatus`](#es-configuration-api-datatypes-advancedoptions) | Key\-value pairs to specify advanced configuration options\. | 
| EncryptionAtRestOptions | [`EncryptionAtRestOptionsStatus`](#es-configuration-api-datatypes-encryptionatreststatus) | Key\-value pairs to enable encryption at rest\. | 

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
| EBSOptions | [`EBSOptions`](#es-configuration-api-datatypes-ebsoptions) | Container for the parameters required to enable EBS\-based storage for an Amazon ES domain\. For more information, see [Configuring EBS\-based Storage](es-createupdatedomains.md#es-createdomain-configure-ebs)\. | 
| AccessPolicies | String | IAM policy document specifying the access policies for the new Amazon ES domain\. For more information, see [Configuring Access Policies](es-createupdatedomains.md#es-createdomain-configure-access-policies)\. | 
| SnapshotOptions | [`SnapshotOptions`](#es-configuration-api-datatypes-snapshotoptions) | Container for parameters required to configure the time of daily automated snapshots of Amazon ES domain indices\.  | 
| VPCOptions | [`VPCDerivedInfo`](#es-configuration-api-datatypes-vpcoptions) | Information that Amazon ES derives based on [VPCOptions](#es-configuration-api-datatypes-vpcoptions) for the domain\. | 
| LogPublishingOptions | [`LogPublishingOptions`](#es-configuration-api-datatypes-logpublishingoptions) | Key\-value pairs to configure slow log publishing\. | 
| AdvancedOptions | [`AdvancedOptions`](#es-configuration-api-datatypes-advancedoptions) | Key\-value pairs to specify advanced configuration options\. | 
| EncryptionAtRestOptions | [`EncryptionAtRestOptions`](#es-configuration-api-datatypes-encryptionatrest) | Key\-value pairs to enable encryption at rest\. | 
| CognitoOptions | [`CognitoOptions`](#es-configuration-api-datatypes-cognitooptions) | Key\-value pairs to configure Amazon ES to use Amazon Cognito authentication for Kibana\. | 

### ElasticsearchDomainStatusList<a name="es-configuration-api-datatypes-esdomainstatuslist"></a>

List that contains the status of each specified Amazon ES domain\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| DomainStatusList | [`ElasticsearchDomainStatus`](#es-configuration-api-datatypes-elasticsearchdomainstatus) | List that contains the status of each specified Amazon ES domain\. | 

### EncryptionAtRestOptions<a name="es-configuration-api-datatypes-encryptionatrest"></a>

Specifies whether the domain should encrypt data at rest, and if so, the AWS Key Management Service \(KMS\) key to use\. Can only be used to create a new domain, not update an existing one\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Enabled | Boolean | Specify true to enable encryption at rest\. | 
| KmsKeyId | String | The KMS key ID\. Takes the form 1a2a3a4\-1a2a\-3a4a\-5a6a\-1a2a3a4a5a6a\. | 

### EncryptionAtRestOptionsStatus<a name="es-configuration-api-datatypes-encryptionatreststatus"></a>

Status of the domain's encryption at rest options\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Options | [`EncryptionAtRestOptions`](#es-configuration-api-datatypes-encryptionatrest) | Encryption at rest options for the domain\. | 
| Status | [`OptionStatus`](#es-configuration-api-datatypes-optionstatus) | Status of the domain's encryption at rest options\. | 

### EndpointsMap<a name="es-configuration-api-datatypes-endpointsmap"></a>

The key\-value pair that contains the VPC endpoint\. Only exists if the Amazon ES domain resides in a VPC\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Endpoints | Key\-value string pair: "vpc": "<VPC\_ENDPOINT>" | The VPC endpoint for the domain\. | 

### LogPublishingOptions<a name="es-configuration-api-datatypes-logpublishingoptions"></a>

Specifies whether the Amazon ES domain publishes the Elasticsearch slow logs to Amazon CloudWatch\. You still have to enable the *collection* of slow logs using the Elasticsearch REST API\. To learn more, see [Setting Elasticsearch Logging Thresholds](es-createupdatedomains.md#es-createdomain-configure-slow-logs-indices)\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| INDEX\_SLOW\_LOGS | Key\-value | Two key\-value pairs that define the CloudWatch log group and whether or not the Elasticsearch index slow log should be published there: <pre>"CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:264071961897:log-group:sample-domain",<br />"Enabled":true</pre> | 
| SEARCH\_SLOW\_LOGS | Key\-value | Two key\-value pairs that define the CloudWatch log group and whether or not the Elasticsearch search slow log should be published there: <pre>"CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:264071961897:log-group:sample-domain",<br />"Enabled":true</pre> | 

### LogPublishingOptionsStatus<a name="es-configuration-api-datatypes-logpublishingoptions-status"></a>

Status of an update to the configuration of the slow log publishing options for the Amazon ES domain\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Options | [`LogPublishingOptions`](#es-configuration-api-datatypes-logpublishingoptions) | Slow log publishing options for the domain | 
| Status | [`OptionStatus`](#es-configuration-api-datatypes-optionstatus) | Status of an update to snapshot options for an Amazon ES domain | 

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
| CreationDate | Timestamp | Date and time when the Amazon ES domain was created | 
| UpdateDate | Timestamp | Date and time when the Amazon ES domain was updated | 
| UpdateVersion | Integer | Whole number that specifies the latest version for the entity | 
| State | [`OptionState`](#es-configuration-api-datatypes-optionsstate) | State of an update to configuration options for an Amazon ES domain | 
| PendingDeletion | Boolean | Indicates whether the service is processing a request to permanently delete the Amazon ES domain and all of its resources | 

### ServiceURL<a name="es-configuration-api-datatypes-serviceurl"></a>

Domain\-specific endpoint used to submit index, search, and data upload requests to an Amazon ES domain\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| ServiceURL | String | Domain\-specific endpoint used to submit index, search, and data upload requests to an Amazon ES domain | 

### SnapshotOptions<a name="es-configuration-api-datatypes-snapshotoptions"></a>

Container for parameters required to configure the time of daily automated snapshots of the indices in an Amazon ES domain\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| AutomatedSnapshotStartHour | Integer | Hour during which the service takes an automated daily snapshot of the indices in the Amazon ES domain | 

### SnapshotOptionsStatus<a name="es-configuration-api-datatypes-snapshotoptionsstatus"></a>

Status of an update to the configuration of the daily automated snapshot for an Amazon ES domain\.


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Options | [`SnapshotOptions`](#es-configuration-api-datatypes-snapshotoptions) | Container for parameters required to configure the time of daily automated snapshots of indices in an Amazon ES domain | 
| Status | [`OptionStatus`](#es-configuration-api-datatypes-optionstatus) | Status of an update to snapshot options for an Amazon ES domain | 

### Tag<a name="es-configuration-api-datatypes-tag"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Key | [`TagKey`](#es-configuration-api-datatypes-tagkey) | Required name of the tag\. Tag keys must be unique for the Amazon ES domain to which they are attached\. For more information, see [Tagging Amazon Elasticsearch Service Domains](es-managedomains.md#es-managedomains-awsresourcetagging)\. | 
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
| SubnetIds | StringList | A list of subnet IDs associated with the VPC endpoints for the domain\. To learn more, see [VPCs and Subnets](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_Subnets.html) in the Amazon VPC User Guide\. | 
| AvailabilityZones | StringList | The list of availability zones associated with the VPC subnets\. To learn more, see [VPC and Subnet Basics](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_Subnets.html#vpc-subnet-basics) in the Amazon VPC User Guide\. | 
| SecurityGroupIds | StringList | The list of security group IDs associated with the VPC endpoints for the domain\. To learn more, see [Security Groups for your VPC](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_SecurityGroups.html) in the Amazon VPC User Guide\. | 

### VPCDerivedInfoStatus<a name="es-configuration-api-datatypes-vpcderivedinfostatus"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Options | [`VPCDerivedInfo`](#es-configuration-api-datatypes-vpcderivedinfo) | Information that Amazon ES derives based on [VPCOptions](#es-configuration-api-datatypes-vpcoptions) for the domain\. | 
| Status | [`OptionStatus`](#es-configuration-api-datatypes-optionstatus) | Status of an update to VPC configuration options for an Amazon ES domain\. | 

### VPCOptions<a name="es-configuration-api-datatypes-vpcoptions"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| SubnetIds | StringList | A list of subnet IDs associated with the VPC endpoints for the domain\. If your domain has zone awareness enabled, you need to provide two subnet IDs, one per zone\. Otherwise, you only need to provide one\. To learn more, see [VPCs and Subnets](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_Subnets.html) in the Amazon VPC User Guide\. | 
| SecurityGroupIds | StringList | The list of security group IDs associated with the VPC endpoints for the domain\. If you do not provide a security group ID, Amazon ES uses the default security group for the VPC\. To learn more, see [Security Groups for your VPC](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_SecurityGroups.html) in the Amazon VPC User Guide\. | 

### VPCOptionsStatus<a name="es-configuration-api-datatypes-vpcoptionsstatus"></a>


****  

| Field | Data Type | Description | 
| --- | --- | --- | 
| Options | [`VPCOptions`](#es-configuration-api-datatypes-vpcoptions) | Container for the values required to configure Amazon ES to work with a VPC\. | 
| Status | [`OptionStatus`](#es-configuration-api-datatypes-optionstatus) | Status of an update to VPC configuration options for an Amazon ES domain\. | 

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