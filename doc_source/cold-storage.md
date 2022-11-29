# Cold storage for Amazon OpenSearch Service<a name="cold-storage"></a>

Cold storage lets you store any amount of infrequently accessed or historical data on your Amazon OpenSearch Service domain and analyze it on demand, at a lower cost than other storage tiers\. Cold storage is appropriate if you need to do periodic research or forensic analysis on your older data\. Practical examples of data suitable for cold storage include infrequently accessed logs, data that must be preserved to meet compliance requirements, or logs that have historical value\. 

Similar to [UltraWarm](ultrawarm.md) storage, cold storage is backed by Amazon S3\. When you need to query cold data, you can selectively attach it to existing UltraWarm nodes\. You can manage the migration and lifecycle of your cold data manually or with Index State Management policies\.

**Topics**
+ [Prerequisites](#coldstorage-pp)
+ [Cold storage requirements and performance considerations](#coldstorage-calc)
+ [Cold storage pricing](#coldstorage-pricing)
+ [Enabling cold storage](#coldstorage-enable)
+ [Managing cold indexes in OpenSearch Dashboards](#coldstorage-dashboards)
+ [Migrating indexes to cold storage](#coldstorage-migrating)
+ [Automating migrations to cold storage](#coldstorage-ism)
+ [Canceling migrations to cold storage](#coldstorage-cancel)
+ [Listing cold indices](#coldstorage-list)
+ [Migrating cold indexes to warm storage](#coldstorage-migrating-back)
+ [Restoring cold indexes from snapshots](#cold-snapshot)
+ [Canceling migrations from cold to warm storage](#coldtowarm-cancel)
+ [Updating cold index metadata](#cold-update-metadata)
+ [Deleting cold indices](#cold-delete)
+ [Disabling cold storage](#coldstorage-disable)

## Prerequisites<a name="coldstorage-pp"></a>

Cold storage has the following prerequisites:
+ Cold storage requires OpenSearch or Elasticsearch version 7\.9 or later\.
+ To enable cold storage on an OpenSearch Service domain, you must also enable UltraWarm on the same domain\.
+ To use cold storage, domains must have [dedicated master nodes](managedomains-dedicatedmasternodes.md)\.
+ If your domain uses a T2 or T3 instance type for your data nodes, you can't use cold storage\.
+ If your index uses [approximate k\-NN](https://opensearch.org/docs/latest/search-plugins/knn/approximate-knn/) \(`"index.knn": true`\), you can't move it to cold storage\.
+ If the domain uses [fine\-grained access control](fgac.md), non\-admin users must be [mapped](fgac.md#fgac-mapping) to the `cold_manager` role in OpenSearch Dashboards in order to manage cold indices\.

**Note**  
The `cold_manager` role might not exist on some preexisting OpenSearch Service domains\. If you don't see the role in Dashboards, you need to [manually create it](#coldstorage-create-role)\.

### Configure permissions<a name="coldstorage-create-role"></a>

If you enable cold storage on a preexisting OpenSearch Service domain, the `cold_manager` role might not be defined on the domain\. If the domain uses [fine\-grained access control](fgac.md), non\-admin users must be mapped to this role in order to manage cold indices\. To manually create the `cold_manager` role, perform the following steps:

1. In OpenSearch Dashboards, go to **Security** and choose **Permissions**\.

1. Choose **Create action group** and configure the following groups:     
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/cold-storage.html)

1. Choose **Roles** and **Create role**\.

1. Name the role **cold\_manager**\.

1. For **Cluster permissions**, choose the `cold_cluster` group you created\.

1. For **Index**, enter `*`\.

1. For **Index permissions**, choose the `cold_index` group you created\.

1. Choose **Create**\.

1. After you create the role, [map it](fgac.md#fgac-mapping) to any user or backend role that manages cold indices\.

## Cold storage requirements and performance considerations<a name="coldstorage-calc"></a>

Because cold storage uses Amazon S3, it incurs none of the overhead of hot storage, such as replicas, Linux reserved space, and OpenSearch Service reserved space\. Cold storage doesn't have specific instance types because it doesn't have any compute capacity attached to it\. You can store any amount of data in cold storage\. Monitor the `ColdStorageSpaceUtilization` metric in Amazon CloudWatch to see how much cold storage space you're using\.

## Cold storage pricing<a name="coldstorage-pricing"></a>

Similar to UltraWarm storage, with cold storage you only pay for data storage\. There's no compute cost for cold data and you wont get billed if theres no data in cold storage\.

You don't incur any transfer charges when moving data between cold and warm storage\. While indexes are being migrated between warm and cold storage, you continue to pay for only one copy of the index\. After the migration completes, the index is billed according to the storage tier it was migrated to\. For more information about cold storage pricing, see [Amazon OpenSearch Service pricing](https://aws.amazon.com/opensearch-service/pricing/)\.

## Enabling cold storage<a name="coldstorage-enable"></a>

The console is the simplest way to create a domain that uses cold storage\. While creating the domain, choose **Enable cold storage**\. The same process works on existing domains as long as you meet the [prerequisites](#coldstorage-pp)\. Even after the domain state changes from **Processing** to **Active**, cold storage might not be available for several hours\.

You can also use the [AWS CLI](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/opensearch/index.html) or [configuration API](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/Welcome.html) to enable cold storage\.

### Sample CLI command<a name="coldstorage-sample-cli"></a>

The following AWS CLI command creates a domain with three data nodes, three dedicated master nodes, cold storage enabled, and fine\-grained access control enabled:

```
aws opensearch create-domain \
  --domain-name my-domain \
  --engine-version Opensearch_1.0 \
  --cluster-config ColdStorageOptions={Enabled=true},WarmEnabled=true,WarmCount=4,WarmType=ultrawarm1.medium.search,InstanceType=r6g.large.search,DedicatedMasterEnabled=true,DedicatedMasterType=r6g.large.search,DedicatedMasterCount=3,InstanceCount=3 \
  --ebs-options EBSEnabled=true,VolumeType=gp2,VolumeSize=11 \
  --node-to-node-encryption-options Enabled=true \
  --encryption-at-rest-options Enabled=true \
  --domain-endpoint-options EnforceHTTPS=true,TLSSecurityPolicy=Policy-Min-TLS-1-2-2019-07 \
  --advanced-security-options Enabled=true,InternalUserDatabaseEnabled=true,MasterUserOptions='{MasterUserName=master-user,MasterUserPassword=master-password}' \
  --region us-east-2
```

For detailed information, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/)\.

### Sample configuration API request<a name="coldstorage-sample-config-api"></a>

The following request to the configuration API creates a domain with three data nodes, three dedicated master nodes, cold storage enabled, and fine\-grained access control enabled:

```
POST https://es.us-east-2.amazonaws.com/2021-01-01/opensearch/domain
{
  "ClusterConfig": {
    "InstanceCount": 3,
    "InstanceType": "r6g.large.search",
    "DedicatedMasterEnabled": true,
    "DedicatedMasterType": "r6g.large.search",
    "DedicatedMasterCount": 3,
    "ZoneAwarenessEnabled": true,
    "ZoneAwarenessConfig": {
      "AvailabilityZoneCount": 3
     },
    "WarmEnabled": true,
    "WarmCount": 4,
    "WarmType": "ultrawarm1.medium.search",
    "ColdStorageOptions": {
       "Enabled": true
     }
  },
  "EBSOptions": {
    "EBSEnabled": true,
    "VolumeType": "gp2",
    "VolumeSize": 11
  },
  "EncryptionAtRestOptions": {
    "Enabled": true
  },
  "NodeToNodeEncryptionOptions": {
    "Enabled": true
  },
  "DomainEndpointOptions": {
    "EnforceHTTPS": true,
    "TLSSecurityPolicy": "Policy-Min-TLS-1-2-2019-07"
  },
   "AdvancedSecurityOptions": {
    "Enabled": true,
    "InternalUserDatabaseEnabled": true,
    "MasterUserOptions": {
      "MasterUserName": "master-user",
      "MasterUserPassword": "master-password"
    }
  },
  "EngineVersion": "Opensearch_1.0",
  "DomainName": "my-domain"
}
```

For detailed information, see the [Amazon OpenSearch Service API Reference](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/Welcome.html)\.

## Managing cold indexes in OpenSearch Dashboards<a name="coldstorage-dashboards"></a>

You can manage hot, warm and cold indexes with the existing Dashboards interface in your OpenSearch Service domain\. Dashboards enables you to migrate indexes between warm and cold storage, and monitor index migration status, without using the CLI or configuration API\. For more information, see [Managing indexes in OpenSearch Dashboards](dashboards.md#dashboards-indices)\.

## Migrating indexes to cold storage<a name="coldstorage-migrating"></a>

When you migrate indexes to cold storage, you provide a time range for the data to make discovery easier\. You can select a timestamp field based on the data in your index, manually provide a start and end timestamp, or choose to not specify one\.


| Parameter | Supported value | Description | 
| --- | --- | --- | 
| timestamp\_field | The date/time field from the index mapping\. |  The minimum and maximum values of the provided field are computed and stored as the `start_time` and `end_time` metadata for the cold index\.  | 
| start\_time and end\_time |  One of the following formats: [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/cold-storage.html)  |  The provided values are stored as the `start_time` and `end_time` metadata for the cold index\.   | 

If you don't want to specify a timestamp, add `?ignore=timestamp` to the request instead\.

The following request migrates a warm index to cold storage and provides start and end times for the data in that index:

```
POST _ultrawarm/migration/my-index/_cold
  {
    "start_time": "2020-03-09",
    "end_time": "2020-03-09T23:00:00Z"
  }
```

Then check the status of the migration:

```
GET _ultrawarm/migration/my-index/_status

{
  "migration_status": {
    "index": "my-index",
    "state": "RUNNING_METADATA_RELOCATION",
    "migration_type": "WARM_TO_COLD"
  }
}
```

OpenSearch Service migrates one index at a time to cold storage\. You can have up to 100 migrations in the queue\. Any request that exceeds the limit will be rejected\. To check the current number of migrations in the queue, monitor the `WarmToColdMigrationQueueSize` [metric](managedomains-cloudwatchmetrics.md#managedomains-cloudwatchmetrics-coldstorage)\. The migration process has the following states:

```
ACCEPTED_COLD_MIGRATION - Migration request is accepted and queued.
RUNNING_METADATA_MIGRATION - The migration request was selected for execution and metadata is migrating to cold storage.
FAILED_METADATA_MIGRATION - The attempt to add index metadata has failed and all retries are exhausted.
PENDING_INDEX_DETACH - Index metadata migration to cold storage is completed. Preparing to detach the warm index state from the local cluster.
RUNNING_INDEX_DETACH - Local warm index state from the cluster is being removed. Upon success, the migration request will be completed.
FAILED_INDEX_DETACH - The index detach process failed and all retries are exhausted.
```

## Automating migrations to cold storage<a name="coldstorage-ism"></a>

You can use [Index State Management](ism.md) to automate the migration process after an index reaches a certain age or meets other conditions\. See the [sample policy](ism.md#ism-example-cold), which demonstrates how to automatically migrate indexes from hot to UltraWarm to cold storage\.

**Note**  
An explicit `timestamp_field` is required in order to move indexes to cold storage using an Index State Management policy\.

## Canceling migrations to cold storage<a name="coldstorage-cancel"></a>

If a migration to cold storage is queued or in a failed state, you can cancel the migration using the following request:

```
POST _ultrawarm/migration/_cancel/my-index

{
  "acknowledged" : true
}
```

If your domain uses fine\-grained access control, you need the `indices:admin/ultrawarm/migration/cancel` permission to make this request\.

## Listing cold indices<a name="coldstorage-list"></a>

Before querying, you can list the indexes in cold storage to decide which ones to migrate to UltraWarm for further analysis\. The following request lists all cold indices, sorted by index name:

```
GET _cold/indices/_search
```

**Sample response**

```
{
  "pagination_id" : "je7MtGbClwBF/2Zp9Utk/h3yCo8nvbEXAMPLEKEY",
  "total_results" : 3,
  "indices" : [
    {
      "index" : "my-index-1",
      "index_cold_uuid" : "hjEoh26mRRCFxRIMdgvLmg",
      "size" : 10339,
      "creation_date" : "2021-06-28T20:23:31.206Z",
      "start_time" : "2020-03-09T00:00Z",
      "end_time" : "2020-03-09T23:00Z"
    },
    {
      "index" : "my-index-2",
      "index_cold_uuid" : "0vIS2n-oROmOWDFmwFIgdw",
      "size" : 6068,
      "creation_date" : "2021-07-15T19:41:18.046Z",
      "start_time" : "2020-03-09T00:00Z",
      "end_time" : "2020-03-09T23:00Z"
    },
    {
      "index" : "my-index-3",
      "index_cold_uuid" : "EaeXOBodTLiDYcivKsXVLQ",
      "size" : 32403,
      "creation_date" : "2021-07-08T00:12:01.523Z",
      "start_time" : "2020-03-09T00:00Z",
      "end_time" : "2020-03-09T23:00Z"
    }
  ]
}
```

### Filtering<a name="coldstorage-filter"></a>

You can filter cold indexes based on a prefix\-based index pattern and time range offsets\. 

The following request lists indexes that match the prefix pattern of `event-*`:

```
GET _cold/indices/_search
 {
   "filters":{
      "index_pattern": "event-*"
   }
 }
```

**Sample response**

```
{
  "pagination_id" : "je7MtGbClwBF/2Zp9Utk/h3yCo8nvbEXAMPLEKEY",
  "total_results" : 1,
  "indices" : [
    {
      "index" : "events-index",
      "index_cold_uuid" : "4eFiab7rRfSvp3slrIsIKA",
      "size" : 32263273,
      "creation_date" : "2021-08-18T18:25:31.845Z",
      "start_time" : "2020-03-09T00:00Z",
      "end_time" : "2020-03-09T23:00Z"
    }
  ]
}
```

The following request returns indexes with `start_time` and `end_time` metadata fields between `2019-03-01` and `2020-03-01`:

```
GET _cold/indices/_search
{
  "filters": {
    "time_range": {
      "start_time": "2019-03-01",
      "end_time": "2020-03-01"
    }
  }
}
```

**Sample response**

```
{
  "pagination_id" : "je7MtGbClwBF/2Zp9Utk/h3yCo8nvbEXAMPLEKEY",
  "total_results" : 1,
  "indices" : [
    {
      "index" : "my-index",
      "index_cold_uuid" : "4eFiab7rRfSvp3slrIsIKA",
      "size" : 32263273,
      "creation_date" : "2021-08-18T18:25:31.845Z",
      "start_time" : "2019-05-09T00:00Z",
      "end_time" : "2019-09-09T23:00Z"
    }
  ]
}
```

### Sorting<a name="coldstorage-sort"></a>

You can sort cold indexes by metadata fields such as index name or size\. The following request lists all indexes sorted by size in descending order:

```
GET _cold/indices/_search
 {
 "sort_key": "size:desc"
 }
```

**Sample response**

```
{
  "pagination_id" : "je7MtGbClwBF/2Zp9Utk/h3yCo8nvbEXAMPLEKEY",
  "total_results" : 5,
  "indices" : [
    {
      "index" : "my-index-6",
      "index_cold_uuid" : "4eFiab7rRfSvp3slrIsIKA",
      "size" : 32263273,
      "creation_date" : "2021-08-18T18:25:31.845Z",
      "start_time" : "2020-03-09T00:00Z",
      "end_time" : "2020-03-09T23:00Z"
    },
    {
      "index" : "my-index-9",
      "index_cold_uuid" : "mbD3ZRVDRI6ONqgEOsJyUA",
      "size" : 57922,
      "creation_date" : "2021-07-07T23:41:35.640Z",
      "start_time" : "2020-03-09T00:00Z",
      "end_time" : "2020-03-09T23:00Z"
    },
    {
      "index" : "my-index-5",
      "index_cold_uuid" : "EaeXOBodTLiDYcivKsXVLQ",
      "size" : 32403,
      "creation_date" : "2021-07-08T00:12:01.523Z",
      "start_time" : "2020-03-09T00:00Z",
      "end_time" : "2020-03-09T23:00Z"
    }
  ]
}
```

Other valid sort keys are `start_time:asc/desc`, `end_time:asc/desc`, and `index_name:asc/desc`\.

### Pagination<a name="coldstorage-pagination"></a>

You can paginate a list of cold indices\. Configure the number of indexes to be returned per page with the `page_size` parameter \(default is 10\)\. Every `_search` request on your cold indexes returns a `pagination_id` which you can use for subsequent calls\.

The following request paginates the results of a `_search` request of your cold indexes and displays the next 100 results:

```
GET _cold/indices/_search?page_size=100
{
"pagination_id": "je7MtGbClwBF/2Zp9Utk/h3yCo8nvbEXAMPLEKEY"
}
```

## Migrating cold indexes to warm storage<a name="coldstorage-migrating-back"></a>

After you narrow down your list of cold indexes with the filtering criteria in the previous section, migrate them back to UltraWarm where you can query the data and use it to create visualizations\. 

The following request migrates two cold indexes back to warm storage:

```
POST _cold/migration/_warm
 {
 "indices": "my-index1,my-index2"
 }


{
  "acknowledged" : true
}
```

To check the status of the migration and retrieve the migration ID, send the following request:

```
GET _cold/migration/_status
```

**Sample response**

```
{
  "cold_to_warm_migration_status" : [
    {
      "migration_id" : "tyLjXCA-S76zPQbPVHkOKA",
      "indices" : [
        "my-index1,my-index2"
      ],
      "state" : "RUNNING_INDEX_CREATION"
    }
  ]
}
```

To get index\-specific migration information, include the index name:

```
GET _cold/migration/my-index/_status
```

Rather than specifying an index, you can list the indexes by their current migration status\. Valid values are `_failed`, `_accepted`, and `_all`\.

The following command gets the status of all indexes in a single migration request:

```
GET _cold/migration/_status?migration_id=my-migration-id
```

Retrieve the migration ID using the status request\. For detailed migration information, add `&verbose=true`\.

You can migrate indexes from cold to warm storage in batches of 10 or less, with a maximum of 100 indexes being migrated simultaneously\. Any request that exceeds the limit will be rejected\. To check the current number of migrations currently taking place, monitor the `ColdToWarmMigrationQueueSize` [metric](managedomains-cloudwatchmetrics.md#managedomains-cloudwatchmetrics-coldstorage)\. The migration process has the following states:

```
ACCEPTED_MIGRATION_REQUEST - Migration request is accepted and queued.
RUNNING_INDEX_CREATION - Migration request is picked up for processing and will create warm indexes in the cluster.
PENDING_COLD_METADATA_CLEANUP - Warm index is created and the migration service will attempt to clean up cold metadata.
RUNNING_COLD_METADATA_CLEANUP - Cleaning up cold metadata from the indexes migrated to warm storage.
FAILED_COLD_METADATA_CLEANUP - Failed to clean up metadata in the cold tier.
FAILED_INDEX_CREATION - Failed to create an index in the warm tier.
```

## Restoring cold indexes from snapshots<a name="cold-snapshot"></a>

Contact [AWS Support](https://console.aws.amazon.com/support/home) if you need to restore cold indexes from an automated snapshot, including in situations where an entire domain was accidentally deleted\. OpenSearch Service retains cold indexes for 14 days after they've been deleted\.

## Canceling migrations from cold to warm storage<a name="coldtowarm-cancel"></a>

If an index migration from cold to warm storage is queued or in a failed state, you can cancel it with the following request:

```
POST _cold/migration/my-index/_cancel

{
  "acknowledged" : true
}
```

To cancel migration for a batch of indexes \(maximum of 10 at a time\), specify the migration ID:

```
POST _cold/migration/_cancel?migration_id=my-migration-id

{
  "acknowledged" : true
}
```

Retrieve the migration ID using the status request\.

## Updating cold index metadata<a name="cold-update-metadata"></a>

You can update the `start_time` and `end_time` fields for a cold index:

```
PATCH _cold/my-index
 {
 "start_time": "2020-01-01",
 "end_time": "2020-02-01"
 }
```

You can't update the `timestamp_field` of an index in cold storage\.

**Note**  
OpenSearch Dashboards doesn't support the PATCH method\. Use [curl](https://curl.haxx.se/), [Postman](https://www.getpostman.com/), or some other method to update cold metadata\.

## Deleting cold indices<a name="cold-delete"></a>

If you're not using an ISM policy you can delete cold indexes manually\. The following request deletes a cold index:

```
DELETE _cold/my-index

{
  "acknowledged" : true
}
```

## Disabling cold storage<a name="coldstorage-disable"></a>

The OpenSearch Service console is the simplest way to disable cold storage\. Select the domain and choose **Actions**, **Edit cluster configuration**, then deselect **Enable cold storage**\. 

To use the AWS CLI or configuration API, under `ColdStorageOptions`, set `"Enabled"="false"`\.

Before you disable cold storage, you must either delete all cold indexes or migrate them back to warm storage, otherwise the disable action fails\. 