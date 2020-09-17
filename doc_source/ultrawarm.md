# UltraWarm for Amazon Elasticsearch Service<a name="ultrawarm"></a>

UltraWarm provides a cost\-effective way to store large amounts of read\-only data on Amazon Elasticsearch Service\. Standard data nodes use "hot" storage, which takes the form of instance stores or Amazon EBS volumes attached to each node\. Hot storage provides the fastest possible performance for indexing and searching new data\.

Rather than attached storage, UltraWarm nodes use Amazon S3 and a sophisticated caching solution to improve performance\. For indices that you are not actively writing to and query less frequently, UltraWarm offers significantly lower costs per GiB of data\. In Elasticsearch, these warm indices behave just like any other index\. You can query them using the same APIs or use them to create dashboards in Kibana\.

**Topics**
+ [Prerequisites](#ultrawarm-pp)
+ [Calculating UltraWarm Storage Requirements](#ultrawarm-calc)
+ [UltraWarm Pricing](#ultrawarm-pricing)
+ [Enabling UltraWarm](#ultrawarm-enable)
+ [Migrating Indices to UltraWarm Storage](#ultrawarm-migrating)
+ [Automating Migrations](#ultrawarm-ism)
+ [Migration Tuning](#ultrawarm-settings)
+ [Cancelling Migrations](#ultrawarm-cancel)
+ [Listing Hot and Warm Indices](#ultrawarm-es-api)
+ [Returning Warm Indices to Hot Storage](#ultrawarm-migrating-back)
+ [Restoring Warm Indices from Automated Snapshots](#ultrawarm-snapshot)
+ [Manual Snapshots of Warm Indices](#ultrawarm-manual-snapshot)
+ [Disabling UltraWarm](#ultrawarm-disable)

## Prerequisites<a name="ultrawarm-pp"></a>

UltraWarm has a few important prerequisites:
+ UltraWarm requires Elasticsearch 6\.8 or higher\.
+ To use warm storage, domains must have [dedicated master nodes](es-managedomains-dedicatedmasternodes.md)\.
+ If your domain uses a T2 instance type for your data nodes, you can't use warm storage\.

## Calculating UltraWarm Storage Requirements<a name="ultrawarm-calc"></a>

As covered in [Calculating Storage Requirements](sizing-domains.md#aes-bp-storage), data in hot storage incurs significant overhead: replicas, Linux reserved space, and Amazon ES reserved space\. For example, a 10 GiB primary shard with one replica shard requires roughly 26 GiB of hot storage\.

Because it uses Amazon S3, UltraWarm incurs none of this overhead\. When calculating UltraWarm storage requirements, you consider only the size of the primary shards\. The durability of data in S3 removes the need for replicas, and S3 abstracts away any operating system or service considerations\. That same 10 GiB shard requires 10 GiB of warm storage\. If you provision an `ultrawarm1.large.elasticsearch` instance, you can use all 20 TiB of its maximum storage for primary shards\. See [UltraWarm Storage Limits](aes-limits.md#limits-ultrawarm) for a summary of instance types and the maximum amount of storage that each can address\.

**Tip**  
With UltraWarm, we still recommend a maximum shard size of 50 GiB\.

## UltraWarm Pricing<a name="ultrawarm-pricing"></a>

With hot storage, you pay for what you provision\. Some instances require an attached Amazon EBS volume, while others include an instance store\. Whether that storage is empty or full, you pay the same price\.

With UltraWarm storage, you pay for what you use\. An `ultrawarm1.large.elasticsearch` instance can address up to 20 TiB of storage on S3, but if you store only 1 TiB of data, you're only billed for 1 TiB of data\. Like all other node types, you also pay an hourly rate for each UltraWarm node\. For more information, see [Pricing for Amazon Elasticsearch Service](what-is-amazon-elasticsearch-service.md#aes-pricing)\.

## Enabling UltraWarm<a name="ultrawarm-enable"></a>

The console is the simplest way to create a domain that uses warm storage\. While creating the domain, choose **Enable UltraWarm data nodes** and the number of warm nodes that you want\. The same basic process works on existing domains, provided they meet the [prerequisites](#ultrawarm-pp)\. Even after the domain state changes from **Processing** to **Active**, UltraWarm might not be available to use for several hours\.

You can also use the [AWS CLI](https://docs.aws.amazon.com/cli/latest/reference/es/) or [configuration API](es-configuration-api.md) to enable UltraWarm, specifically the `WarmEnabled`, `WarmCount`, and `WarmType` options in `ElasticsearchClusterConfig`\.

**Note**  
Domains support a maximum number of warm nodes\. For details, see [Amazon Elasticsearch Service Limits](aes-limits.md)\.

### Sample CLI Command<a name="ultrawarm-sample-cli"></a>

The following AWS CLI command creates a domain with three data nodes, three dedicated master nodes, and six warm nodes with a restrictive access policy:

```
aws es create-elasticsearch-domain --domain-name my-domain --elasticsearch-cluster-config InstanceCount=3,InstanceType=r5.large.elasticsearch,DedicatedMasterEnabled=true,DedicatedMasterType=c5.large.elasticsearch,DedicatedMasterCount=3,ZoneAwarenessEnabled=true,ZoneAwarenessConfig={AvailabilityZoneCount=3},WarmEnabled=true,WarmCount=6,WarmType=ultrawarm1.medium.elasticsearch --elasticsearch-version 6.8 --ebs-options EBSEnabled=true,VolumeType=gp2,VolumeSize=11 --access-policies '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"AWS":["123456789012"]},"Action":["es:*"],"Resource":"arn:aws:es:us-east-1:123456789012:domain/my-domain/*"}]}' --region us-east-1
```

For detailed information, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/)\.

### Sample Configuration API Request<a name="ultrawarm-sample-config-api"></a>

The following request to the configuration API creates a domain with three data nodes, three dedicated master nodes, and six warm nodes with all encryption features enabled and a restrictive access policy:

```
POST https://es.us-east-2.amazonaws.com/2015-01-01/es/domain
{
  "ElasticsearchClusterConfig": {
    "InstanceCount": 3,
    "InstanceType": "r5.large.elasticsearch",
    "DedicatedMasterEnabled": true,
    "DedicatedMasterType": "c5.large.elasticsearch",
    "DedicatedMasterCount": 3,
    "ZoneAwarenessEnabled": true,
    "ZoneAwarenessConfig": {
      "AvailabilityZoneCount": 3
    },
    "WarmEnabled": true,
    "WarmCount": 6,
    "WarmType": "ultrawarm1.medium.elasticsearch"
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
  "ElasticsearchVersion": "6.8",
  "DomainName": "my-domain",
  "AccessPolicies": "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"123456789012\"]},\"Action\":[\"es:*\"],\"Resource\":\"arn:aws:es:us-east-1:123456789012:domain/my-domain/*\"}]}"
}
```

For detailed information, see [Amazon Elasticsearch Service Configuration API Reference](es-configuration-api.md)\.

## Migrating Indices to UltraWarm Storage<a name="ultrawarm-migrating"></a>

If you finish writing to an index and no longer need the fastest possible search performance, migrate it from hot to warm:

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

If you migrate several indices in quick succession, you can get a summary of all migrations in plaintext, similar to the `_cat` API:

```
GET _ultrawarm/migration/_status?v

index    migration_type state
my-index HOT_TO_WARM    RUNNING_SHARD_RELOCATION
```

You can have up to 200 simultaneous migrations from hot to warm storage\. To check the current number of migrations in the queue, monitor the `HotToWarmMigrationQueueSize` [metric](es-managedomains-cloudwatchmetrics.md#es-managedomains-cloudwatchmetrics-uw)\.

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

Indices in warm storage are read\-only unless you [return them to hot storage](#ultrawarm-migrating-back)\. You can query the indices and delete them, but you can't add, update, or delete individual documents\. If you try, you might encounter the following error:

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

## Automating Migrations<a name="ultrawarm-ism"></a>

We recommend using [Index State Management](ism.md) to automate the migration process after an index reaches a certain age or meets other conditions\. The sample policy [here](ism.md#ism-example) demonstrates that workflow\.

## Migration Tuning<a name="ultrawarm-settings"></a>

Index migrations to UltraWarm storage require a force merge\. Each Elasticsearch index is composed of some number of shards, and each shard is composed of some number of Lucene segments\. The force merge operation purges documents that were marked for deletion and conserves disk space\. By default, UltraWarm merges indices into one segment\.

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

To check how long this stage of the migration process takes, monitor the `HotToWarmMigrationForceMergeLatency` [metric](es-managedomains-cloudwatchmetrics.md#es-managedomains-cloudwatchmetrics-uw)\.

## Cancelling Migrations<a name="ultrawarm-cancel"></a>

UltraWarm handles migrations sequentially, in a queue\. If a migration is in the queue, but has not yet started, you can remove it from the queue using the following request:

```
POST _ultrawarm/migration/_cancel/my-index
```

If your domain uses fine\-grained access control, you must have the `indices:admin/ultrawarm/migration/cancel` permission to make this request\.

## Listing Hot and Warm Indices<a name="ultrawarm-es-api"></a>

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

## Returning Warm Indices to Hot Storage<a name="ultrawarm-migrating-back"></a>

If you need to write to an index again, migrate it back to hot storage:

```
POST _ultrawarm/migration/my-index/_hot
```

You can have up to 10 simultaneous migrations from warm to hot storage\. To check the current number, monitor the `WarmToHotMigrationQueueSize` [metric](es-managedomains-cloudwatchmetrics.md#es-managedomains-cloudwatchmetrics-uw)\.

After the migration finishes, check the index settings to make sure they meet your needs\. Indices return to hot storage with one replica\.

## Restoring Warm Indices from Automated Snapshots<a name="ultrawarm-snapshot"></a>

In addition to the standard repository for automated snapshots, UltraWarm adds a second repository, `cs-ultrawarm`\. Snapshots in `cs-ultrawarm` have the same 14\-day retention period as other automated snapshots\.

Unlike other automated snapshots, each snapshot in this repository contains only one index\. When you restore a snapshot from `cs-ultrawarm`, it restores to warm storage, not hot storage\. Snapshots in the `cs-automated` and `cs-automated-enc` repositories restore to hot storage\.

**To restore an UltraWarm snapshot to warm storage**

1. Identify the latest snapshot that contains the index that you want to restore:

   ```
   GET _snapshot/cs-ultrawarm/_all
   
   {
     "snapshots": [{
       "snapshot": "snapshot-name",
       "version": "6.8.0",
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

   If you don't want to delete the index, [return it to hot storage](#ultrawarm-migrating-back) and [reindex](https://opendistro.github.io/for-elasticsearch-docs/docs/elasticsearch/reindex-data/) it\.

1. Restore the snapshot:

   ```
   POST _snapshot/cs-ultrawarm/snapshot-name/_restore
   ```

   UltraWarm ignores any index settings you specify in this restore request, but you can specify options like `rename_pattern` and `rename_replacement`\. For a summary of Elasticsearch snapshot restore options, see the [Open Distro for Elasticsearch documentation](https://opendistro.github.io/for-elasticsearch-docs/docs/elasticsearch/snapshot-restore/#restore-snapshots)\.

## Manual Snapshots of Warm Indices<a name="ultrawarm-manual-snapshot"></a>

You *can* take manual snapshots of warm indices, but we don't recommend it\. The automated `cs-ultrawarm` repository already contains a snapshot for each warm index at no additional charge\.

By default, Amazon ES does not include warm indices in manual snapshots\. For example, the following call only includes hot indices:

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

## Disabling UltraWarm<a name="ultrawarm-disable"></a>

The console is the simplest way to disable UltraWarm\. Choose the domain, **Edit domain**, uncheck **Enable UltraWarm data nodes**, and **Submit**\. You can also use the `WarmEnabled` option in the AWS CLI and configuration API\.

Before you disable UltraWarm, you must either delete all warm indices or migrate them back to hot storage\. After warm storage is empty, wait five minutes before attempting to disable the feature\.