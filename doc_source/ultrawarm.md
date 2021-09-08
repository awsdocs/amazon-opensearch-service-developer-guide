# UltraWarm storage for Amazon OpenSearch Service<a name="ultrawarm"></a>

UltraWarm provides a cost\-effective way to store large amounts of read\-only data on Amazon OpenSearch Service\. Standard data nodes use "hot" storage, which takes the form of instance stores or Amazon EBS volumes attached to each node\. Hot storage provides the fastest possible performance for indexing and searching new data\.

Rather than attached storage, UltraWarm nodes use Amazon S3 and a sophisticated caching solution to improve performance\. For indices that you are not actively writing to, query less frequently, and don't need the same performance from, UltraWarm offers significantly lower costs per GiB of data\. Because warm indices are read\-only unless you return them to hot storage, UltraWarm is best\-suited to immutable data, such as logs\.

In OpenSearch, warm indices behave just like any other index\. You can query them using the same APIs or use them to create visualizations in OpenSearch Dashboards\.

**Topics**
+ [Prerequisites](#ultrawarm-pp)
+ [UltraWarm storage requirements and performance considerations](#ultrawarm-calc)
+ [UltraWarm pricing](#ultrawarm-pricing)
+ [Enabling UltraWarm](#ultrawarm-enable)
+ [Migrating indices to UltraWarm storage](#ultrawarm-migrating)
+ [Automating migrations](#ultrawarm-ism)
+ [Migration tuning](#ultrawarm-settings)
+ [Cancelling migrations](#ultrawarm-cancel)
+ [Listing hot and warm indices](#ultrawarm-api)
+ [Returning warm indices to hot storage](#ultrawarm-migrating-back)
+ [Restoring warm indices from automated snapshots](#ultrawarm-snapshot)
+ [Manual snapshots of warm indices](#ultrawarm-manual-snapshot)
+ [Migrating warm indices to cold storage](#ultrawarm-cold)
+ [Disabling UltraWarm](#ultrawarm-disable)

## Prerequisites<a name="ultrawarm-pp"></a>

UltraWarm has a few important prerequisites:
+ UltraWarm requires OpenSearch or Elasticsearch 6\.8 or higher\.
+ To use warm storage, domains must have [dedicated master nodes](managedomains-dedicatedmasternodes.md)\.
+ If your domain uses a T2 or T3 instance type for your data nodes, you can't use warm storage\.
+ If the domain uses [fine\-grained access control](fgac.md), users must be mapped to the `ultrawarm_manager` role in OpenSearch Dashboards to make UltraWarm API calls\.

**Note**  
The `ultrawarm_manager` role might not be defined on some preexisting OpenSearch Service domains\. If you don't see the role in Dashboards, you need to [manually create it](#ultrawarm-create-role)\.

### Configure permissions<a name="ultrawarm-create-role"></a>

If you enable UltraWarm on a preexisting OpenSearch Service domain, the `ultrawarm_manager` role might not be defined on the domain\. Non\-admin users must be mapped to this role in order to manage warm indices on domains using fine\-grained access control\. To manually create the `ultrawarm_manager` role, perform the following steps:

1. In OpenSearch Dashboards, go to **Security** and choose **Permissions**\.

1. Choose **Create action group** and configure the following groups:     
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/ultrawarm.html)

1. Choose **Roles** and **Create role**\.

1. Name the role **ultrawarm\_manager**\.

1. For **Cluster permissions, **select `ultrawarm_cluster` and `cluster_monitor`\.

1. For **Index**, type `*`\.

1. For **Index permissions**, select `ultrawarm_index_read`, `ultrawarm_index_write`, and `indices_monitor`\.

1. Choose **Create**\.

1. After you create the role, [map it](fgac.md#fgac-mapping) to any user or backend role that will manage UltraWarm indices\.

## UltraWarm storage requirements and performance considerations<a name="ultrawarm-calc"></a>

As covered in [Calculating storage requirements](sizing-domains.md#bp-storage), data in hot storage incurs significant overhead: replicas, Linux reserved space, and OpenSearch Service reserved space\. For example, a 20 GiB primary shard with one replica shard requires roughly 53 GiB of hot storage\.

Because it uses Amazon S3, UltraWarm incurs none of this overhead\. When calculating UltraWarm storage requirements, you consider only the size of the primary shards\. The durability of data in S3 removes the need for replicas, and S3 abstracts away any operating system or service considerations\. That same 20 GiB shard requires 20 GiB of warm storage\. If you provision an `ultrawarm1.large.search` instance, you can use all 20 TiB of its maximum storage for primary shards\. See [UltraWarm storage limits](limits.md#limits-ultrawarm) for a summary of instance types and the maximum amount of storage that each can address\.

With UltraWarm, we still recommend a maximum shard size of 50 GiB\. The [number of CPU cores and amount of RAM allocated to each UltraWarm instance type](#ultrawarm-pricing) gives you an idea of the number of shards they can simultaneously search\. Note that while only primary shards count toward UltraWarm storage in S3, OpenSearch Dashboards and `_cat/shards` still report UltraWarm index size as the *total* of all primary and replica shards\.

For example, each `ultrawarm1.medium.search` instance has two CPU cores and can address up to 1\.5 TiB of storage on S3\. Two of these instances have a combined 3 TiB of storage, which works out to approximately 62 shards if each shard is 50 GiB\. If a request to the cluster only searches four of these shards, performance might be excellent\. If the request is broad and searches all 62 of them, the four CPU cores might struggle to perform the operation\. Monitor the `WarmCPUUtilization` and `WarmJVMMemoryPressure` [UltraWarm metrics](managedomains-cloudwatchmetrics.md#managedomains-cloudwatchmetrics-uw) to understand how the instances handle your workloads\.

If your searches are broad or frequent, consider leaving the indices in hot storage\. Just like any other OpenSearch workload, the most important step to determining if UltraWarm meets your needs is to perform representative client testing using a realistic dataset\.

## UltraWarm pricing<a name="ultrawarm-pricing"></a>

With hot storage, you pay for what you provision\. Some instances require an attached Amazon EBS volume, while others include an instance store\. Whether that storage is empty or full, you pay the same price\.

With UltraWarm storage, you pay for what you use\. An `ultrawarm1.large.search` instance can address up to 20 TiB of storage on S3, but if you store only 1 TiB of data, you're only billed for 1 TiB of data\. Like all other node types, you also pay an hourly rate for each UltraWarm node\. For more information, see [Pricing for Amazon OpenSearch Service](what-is.md#pricing)\.

## Enabling UltraWarm<a name="ultrawarm-enable"></a>

The console is the simplest way to create a domain that uses warm storage\. While creating the domain, choose **Enable UltraWarm data nodes** and the number of warm nodes that you want\. The same basic process works on existing domains, provided they meet the [prerequisites](#ultrawarm-pp)\. Even after the domain state changes from **Processing** to **Active**, UltraWarm might not be available to use for several hours\.

You can also use the [AWS CLI](https://docs.aws.amazon.com/cli/latest/reference/es/) or [configuration API](configuration-api.md) to enable UltraWarm, specifically the `WarmEnabled`, `WarmCount`, and `WarmType` options in `ClusterConfig`\.

**Note**  
Domains support a maximum number of warm nodes\. For details, see [Amazon OpenSearch Service limits](limits.md)\.

### Sample CLI command<a name="ultrawarm-sample-cli"></a>

The following AWS CLI command creates a domain with three data nodes, three dedicated master nodes, six warm nodes, and fine\-grained access control enabled:

```
aws opensearchservice create-domain \
  --domain-name my-domain \
  --engine-version Opensearch_1.0 \
  --cluster-config InstanceCount=3,InstanceType=r6g.large.search,DedicatedMasterEnabled=true,DedicatedMasterType=r6g.large.search,DedicatedMasterCount=3,ZoneAwarenessEnabled=true,ZoneAwarenessConfig={AvailabilityZoneCount=3},WarmEnabled=true,WarmCount=6,WarmType=ultrawarm1.medium.search \
  --ebs-options EBSEnabled=true,VolumeType=gp2,VolumeSize=11 \
  --node-to-node-encryption-options Enabled=true \
  --encryption-at-rest-options Enabled=true \
  --domain-endpoint-options EnforceHTTPS=true,TLSSecurityPolicy=Policy-Min-TLS-1-2-2019-07 \
  --advanced-security-options Enabled=true,InternalUserDatabaseEnabled=true,MasterUserOptions='{MasterUserName=master-user,MasterUserPassword=master-password}' \
  --access-policies '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"AWS":["123456789012"]},"Action":["es:*"],"Resource":"arn:aws:es:us-west-1:123456789012:domain/my-domain/*"}]}' \
  --region us-east-1
```

For detailed information, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/)\.

### Sample configuration API request<a name="ultrawarm-sample-config-api"></a>

The following request to the configuration API creates a domain with three data nodes, three dedicated master nodes, and six warm nodes with fine\-grained access control enabled and a restrictive access policy:

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
    "WarmCount": 6,
    "WarmType": "ultrawarm1.medium.search"
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
  "DomainName": "my-domain",
  "AccessPolicies": "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"123456789012\"]},\"Action\":[\"es:*\"],\"Resource\":\"arn:aws:es:us-east-1:123456789012:domain/my-domain/*\"}]}"
}
```

For detailed information, see [Configuration API reference for Amazon OpenSearch Service](configuration-api.md)\.

## Migrating indices to UltraWarm storage<a name="ultrawarm-migrating"></a>

If you finished writing to an index and no longer need the fastest possible search performance, migrate it from hot to warm:

```
POST _ultrawarm/migration/my-index/_warm
```

Then check the status of the migration:

```
GET _ultrawarm/migration/my-index/_status

{
  "migration_status": {
    "index": "my-index",
    "state": "RUNNING_SHARD_RELOCATION",
    "migration_type": "HOT_TO_WARM",
    "shard_level_status": {
      "running": 0,
      "total": 5,
      "pending": 3,
      "failed": 0,
      "succeeded": 2
    }
  }
}
```

Index health must be green to perform a migration\. If you migrate several indices in quick succession, you can get a summary of all migrations in plaintext, similar to the `_cat` API:

```
GET _ultrawarm/migration/_status?v

index    migration_type state
my-index HOT_TO_WARM    RUNNING_SHARD_RELOCATION
```

You can have up to 200 simultaneous migrations from hot to warm storage\. To check the current number of migrations in the queue, monitor the `HotToWarmMigrationQueueSize` [metric](managedomains-cloudwatchmetrics.md#managedomains-cloudwatchmetrics-uw)\. Indices remain available throughout the migration process—no downtime\.

The migration process has the following states:

```
PENDING_INCREMENTAL_SNAPSHOT
RUNNING_INCREMENTAL_SNAPSHOT
FAILED_INCREMENTAL_SNAPSHOT
PENDING_FORCE_MERGE
RUNNING_FORCE_MERGE
FAILED_FORCE_MERGE
PENDING_FULL_SNAPSHOT
RUNNING_FULL_SNAPSHOT
FAILED_FULL_SNAPSHOT
PENDING_SHARD_RELOCATION
RUNNING_SHARD_RELOCATION
FINISHED_SHARD_RELOCATION
```

As these states indicate, migrations might fail during snapshots, shard relocations, or force merges\. Failures during snapshots or shard relocation are typically due to node failures or S3 connectivity issues\. Lack of disk space is usually the underlying cause of force merge failures\.

After a migration finishes, the same `_status` request returns an error\. If you check the index at that time, you can see some settings that are unique to warm indices:

```
GET my-index/_settings

{
  "my-index": {
    "settings": {
      "index": {
        "refresh_interval": "-1",
        "auto_expand_replicas": "false",
        "provided_name": "my-index",
        "creation_date": "1599241458998",
        "unassigned": {
          "node_left": {
            "delayed_timeout": "5m"
          }
        },
        "number_of_replicas": "1",
        "uuid": "GswyCdR0RSq0SJYmzsIpiw",
        "version": {
          "created": "7070099"
        },
        "routing": {
          "allocation": {
            "require": {
              "box_type": "warm"
            }
          }
        },
        "number_of_shards": "5",
        "merge": {
          "policy": {
            "max_merge_at_once_explicit": "50"
          }
        }
      }
    }
  }
}
```
+ `number_of_replicas`, in this case, is the number of passive replicas, which don't consume disk space\.
+ `routing.allocation.require.box_type` specifies that the index should use warm nodes rather than standard data nodes\.
+ `merge.policy.max_merge_at_once_explicit` specifies the number of segments to simultaneously merge during the migration\.

Indices in warm storage are read\-only unless you [return them to hot storage](#ultrawarm-migrating-back), which makes UltraWarm best\-suited to immutable data, such as logs\. You can query the indices and delete them, but you can't add, update, or delete individual documents\. If you try, you might encounter the following error:

```
{
  "error": {
    "root_cause": [{
      "type": "cluster_block_exception",
      "reason": "blocked by: [FORBIDDEN/12/index read-only / allow delete (api)];"
    }],
    "type": "cluster_block_exception",
    "reason": "blocked by: [FORBIDDEN/12/index read-only / allow delete (api)];"
  },
  "status": 403
}
```

## Automating migrations<a name="ultrawarm-ism"></a>

We recommend using [Index State Management in Amazon OpenSearch Service](ism.md) to automate the migration process after an index reaches a certain age or meets other conditions\. See the [sample policy](ism.md#ism-example-cold) that demonstrates this workflow\.

## Migration tuning<a name="ultrawarm-settings"></a>

Index migrations to UltraWarm storage require a force merge\. Each OpenSearch index is composed of some number of shards, and each shard is composed of some number of Lucene segments\. The force merge operation purges documents that were marked for deletion and conserves disk space\. By default, UltraWarm merges indices into one segment\.

You can change this value up to 1,000 segments using the `index.ultrawarm.migration.force_merge.max_num_segments` setting\. Higher values speed up the migration process, but increase query latency for the warm index after the migration finishes\. To change the setting, make the following request:

```
PUT my-index/_settings
{
  "index": {
    "ultrawarm": {
      "migration": {
        "force_merge": {
          "max_num_segments": 1
        }
      }
    }
  }
}
```

To check how long this stage of the migration process takes, monitor the `HotToWarmMigrationForceMergeLatency` [metric](managedomains-cloudwatchmetrics.md#managedomains-cloudwatchmetrics-uw)\.

## Cancelling migrations<a name="ultrawarm-cancel"></a>

UltraWarm handles migrations sequentially, in a queue\. If a migration is in the queue, but has not yet started, you can remove it from the queue using the following request:

```
POST _ultrawarm/migration/_cancel/my-index
```

If your domain uses fine\-grained access control, you must have the `indices:admin/ultrawarm/migration/cancel` permission to make this request\.

## Listing hot and warm indices<a name="ultrawarm-api"></a>

UltraWarm adds two additional options, similar to `_all`, to help manage hot and warm indices\. For a list of all warm or hot indices, make the following requests:

```
GET _warm
GET _hot
```

You can use these options in other requests that specify indices, such as:

```
_cat/indices/_warm
_cluster/state/_all/_hot
```

## Returning warm indices to hot storage<a name="ultrawarm-migrating-back"></a>

If you need to write to an index again, migrate it back to hot storage:

```
POST _ultrawarm/migration/my-index/_hot
```

You can have up to 10 simultaneous migrations from warm to hot storage\. To check the current number, monitor the `WarmToHotMigrationQueueSize` [metric](managedomains-cloudwatchmetrics.md#managedomains-cloudwatchmetrics-uw)\.

After the migration finishes, check the index settings to make sure they meet your needs\. Indices return to hot storage with one replica\.

## Restoring warm indices from automated snapshots<a name="ultrawarm-snapshot"></a>

In addition to the standard repository for automated snapshots, UltraWarm adds a second repository for warm indices, `cs-ultrawarm`\. Each snapshot in this repository contains only one index\. If you delete a warm index, its snapshot remains in the `cs-ultrawarm` repository for 14 days, just like any other automated snapshot\.

When you restore a snapshot from `cs-ultrawarm`, it restores to warm storage, not hot storage\. Snapshots in the `cs-automated` and `cs-automated-enc` repositories restore to hot storage\.

**To restore an UltraWarm snapshot to warm storage**

1. Identify the latest snapshot that contains the index you want to restore:

   ```
   GET _snapshot/cs-ultrawarm/_all
   
   {
     "snapshots": [{
       "snapshot": "snapshot-name",
       "version": "1.0",
       "indices": [
         "my-index"
       ]
     }]
   }
   ```

1. If the index already exists, delete it:

   ```
   DELETE my-index
   ```

   If you don't want to delete the index, [return it to hot storage](#ultrawarm-migrating-back) and [reindex](https://opensearch.org/docs/opensearch/reindex-data/) it\.

1. Restore the snapshot:

   ```
   POST _snapshot/cs-ultrawarm/snapshot-name/_restore
   ```

   UltraWarm ignores any index settings you specify in this restore request, but you can specify options like `rename_pattern` and `rename_replacement`\. For a summary of OpenSearch snapshot restore options, see the [OpenSearch documentation](https://opensearch.org/docs/opensearch/snapshot-restore/#restore-snapshots)\.

## Manual snapshots of warm indices<a name="ultrawarm-manual-snapshot"></a>

You *can* take manual snapshots of warm indices, but we don't recommend it\. The automated `cs-ultrawarm` repository already contains a snapshot for each warm index, taken during the migration, at no additional charge\.

By default, OpenSearch Service does not include warm indices in manual snapshots\. For example, the following call only includes hot indices:

```
PUT _snapshot/my-repository/my-snapshot
```

If you choose to take manual snapshots of warm indices, several important considerations apply\.
+ You can't mix hot and warm indices\. For example, the following request fails:

  ```
  PUT _snapshot/my-repository/my-snapshot
  {
    "indices": "warm-index-1,hot-index-1",
    "include_global_state": false
  }
  ```

  If they include a mix of hot and warm indices, wildcard \(`*`\) statements fail, as well\.
+ You can only include one warm index per snapshot\. For example, the following request fails:

  ```
  PUT _snapshot/my-repository/my-snapshot
  {
    "indices": "warm-index-1,warm-index-2,other-warm-indices-*",
    "include_global_state": false
  }
  ```

  This request succeeds:

  ```
  PUT _snapshot/my-repository/my-snapshot
  {
    "indices": "warm-index-1",
    "include_global_state": false
  }
  ```
+ Manual snapshots always restore to hot storage, even if they originally included a warm index\.

## Migrating warm indices to cold storage<a name="ultrawarm-cold"></a>

If you have data in UltraWarm that you query infrequently, consider migrating it to cold storage\. Cold storage is meant for data you only access occasionally or is no longer in active use\. You can't read from or write to cold indices, but you can migrate them back to warm storage at no cost whenever you need to query them\. For instructions, see [Migrating indices to cold storage](cold-storage.md#coldstorage-migrating)\.

## Disabling UltraWarm<a name="ultrawarm-disable"></a>

The console is the simplest way to disable UltraWarm\. Choose the domain, **Edit domain**, deselect **Enable UltraWarm data nodes**, and **Submit**\. You can also use the `WarmEnabled` option in the AWS CLI and configuration API\.

Before you disable UltraWarm, you must either delete all warm indices or migrate them back to hot storage\. After warm storage is empty, wait five minutes before attempting to disable the feature\.