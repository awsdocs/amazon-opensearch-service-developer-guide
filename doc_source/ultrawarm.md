# UltraWarm for Amazon Elasticsearch Service \(Preview\)<a name="ultrawarm"></a>

UltraWarm provides a cost\-effective way to store large amounts of read\-only data on Amazon Elasticsearch Service\. Standard data nodes use "hot" storage, which takes the form of instance stores or Amazon EBS volumes attached to each node\. Hot storage provides the fastest possible performance for indexing and searching new data\.

Rather than attached storage, UltraWarm nodes use Amazon S3 and a sophisticated caching solution to improve performance\. For indices that you are not actively writing to and query less frequently, UltraWarm offers significantly lower costs per GiB\. In Elasticsearch, these warm indices behave just like any other index\. You can query them using the same APIs or use them to create dashboards in Kibana\.

**Topics**
+ [Public Preview Limitations](#ultrawarm-pp)
+ [Calculating UltraWarm Storage Requirements](#ultrawarm-calc)
+ [UltraWarm Pricing](#ultrawarm-pricing)
+ [Creating Domains with UltraWarm](#ultrawarm-new-domain)
+ [Migrating Indices to UltraWarm Storage](#ultrawarm-migrating)
+ [Listing Hot and Warm Indices](#ultrawarm-es-api)
+ [Restoring Warm Indices from Snapshots](#ultrawarm-snapshot)

## Public Preview Limitations<a name="ultrawarm-pp"></a>

For the public preview, UltraWarm has several important limitations:
+ We don't yet recommend using warm storage for critical workloads\. Preview features are for testing and evaluation\.
+ You can't add warm storage to existing domains, only new ones\. After creating a domain with warm storage, you *can* increase or decrease the number of warm nodes, but you can't disable warm storage entirely\.
+ UltraWarm is available only in the `us-east-1` \(N\. Virginia\), `us-east-2` \(Ohio\), and `us-west-2` \(Oregon\) Regions\.
+ To use warm storage, domains must be deployed across [three Availability Zones](es-managedomains.md#es-managedomains-multiaz) and use [dedicated master nodes](es-managedomains-dedicatedmasternodes.md)\.
+ If your domain uses a T2 instance type for your data nodes, you can't use warm storage\.
+ You can use warm storage only with Elasticsearch 6\.8\.
+ UltraWarm doesn't support fine\-grained access control at this time\.
+ You can migrate indices from hot storage to warm storage, but not the other way around\. To migrate indices back to hot storage, see [Restoring Warm Indices from Snapshots](#ultrawarm-snapshot)\.

## Calculating UltraWarm Storage Requirements<a name="ultrawarm-calc"></a>

As covered in [Calculating Storage Requirements](sizing-domains.md#aes-bp-storage), data in hot storage incurs significant overhead: replicas, Linux reserved space, and Amazon ES reserved space\. For example, a 10 GiB primary shard with one replica shard requires roughly 26 GiB of hot storage\.

Because it uses Amazon S3, UltraWarm incurs none of this overhead\. When calculating UltraWarm storage requirements, you consider only the size of the primary shards\. The durability of data in S3 removes the need for replicas, and S3 abstracts away any operating system or service considerations\. That same 10 GiB shard requires 10 GiB of warm storage\. If you provision an `ultrawarm1.large.elasticsearch` instance, you can use all 20 TiB of its maximum storage for primary shards\. See [UltraWarm Storage Limits](aes-limits.md#limits-ultrawarm) for a summary of instance types and the maximum amount of storage that each can address\.

**Tip**  
With UltraWarm, we still recommend a maximum shard size of 50 GiB\.

## UltraWarm Pricing<a name="ultrawarm-pricing"></a>

With hot storage, you pay for what you provision\. Some instances require an attached Amazon EBS volume, while others include an instance store\. Whether that storage is empty or full, you pay the same price\.

With UltraWarm storage, you pay for what you use\. An `ultrawarm1.large.elasticsearch` instance can address up to 20 TiB of storage on S3, but if you store only 1 TiB of data, you're only billed for 1 TiB of data\. Like all other node types, you also pay an hourly rate for each UltraWarm node\. For more information, see [Pricing for Amazon Elasticsearch Service](what-is-amazon-elasticsearch-service.md#aes-pricing)\.

## Creating Domains with UltraWarm<a name="ultrawarm-new-domain"></a>

The console is the simplest way to create a domain that uses warm storage\. While creating the domain, choose **UltraWarm Preview**, enable **UltraWarm**, and choose the number of warm nodes that you want\. For more information, see [Creating Amazon ES Domains \(Console\)](es-createupdatedomains.md#es-createdomains-console)\.

You can also use the [AWS CLI](https://docs.aws.amazon.com/cli/latest/reference/es/) or [configuration API](es-configuration-api.md), specifically the `WarmEnabled`, `WarmCount`, and `WarmType` options in `ElasticsearchClusterConfig`\.

**Note**  
Domains support a maximum number of warm nodes\. For details, see [Amazon Elasticsearch Service Limits](aes-limits.md)\.

### Sample CLI Command<a name="ultrawarm-sample-cli"></a>

The following AWS CLI command creates a domain with three data nodes, three dedicated master nodes, and six warm nodes with a restrictive access policy:

```
aws es create-elasticsearch-domain --domain-name my-domain --elasticsearch-cluster-config InstanceCount=3,InstanceType=r5.large.elasticsearch,DedicatedMasterEnabled=true,DedicatedMasterType=c5.large.elasticsearch,DedicatedMasterCount=3,ZoneAwarenessEnabled=true,ZoneAwarenessConfig={AvailabilityZoneCount=3},WarmEnabled=true,WarmCount=6,WarmType=ultrawarm1.medium.elasticsearch --elasticsearch-version 6.8 --access-policies '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"AWS":["123456789012"]},"Action":["es:*"],"Resource":"arn:aws:es:us-east-1:123456789012:domain/my-domain/*"}]}' --region us-east-1
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
        "blocks": {
          "ultrawarm_allow_delete": "true"
        },
        "provided_name": "my-index",
        "creation_date": "1572886951679",
        "unassigned": {
          "node_left": {
            "delayed_timeout": "5m"
          }
        },
        "number_of_replicas": "1",
        "uuid": "3iyTkhXvR8Cytc6sWKBirg",
        "version": {
          "created": "6080099"
        },
        "routing": {
          "allocation": {
            "require": {
              "box_type": "warm"
            }
          },
          "search_preference": "_primary_first"
        },
        "number_of_shards": "5",
        "migration": {
          "state": "WARM"
        },
        "snapshot": {
          "id": "7WPneO-5QQKgKoCi7esXGw"
        }
      }
    }
  }
} disables
```
+ `blocks.ultrawarm_allow_delete` specifies whether to block most `_settings` updates to the index \(`true`\) or allow them \(`false`\)\.
+ `number_of_replicas`, in this case, is the number of passive replicas, which don't consume disk space\.
+ `routing.allocation.require.box_type` specifies that the index should use warm nodes rather than standard data nodes\.
+ `routing.search_preference` instructs Amazon ES to query primary shards first and only use the passive replicas if the query fails\. This setting reduces disk usage\.
+ `migration.state` specifies that the index successfully migrated to warm storage\. During the migration process, you might also see `HOT2WARM`\.

Indices in warm storage are read\-only unless you [restore them to hot storage](#ultrawarm-snapshot)\. You can query the indices, but you can't add data to them\. If you try, you encounter the following error:

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

## Listing Hot and Warm Indices<a name="ultrawarm-es-api"></a>

UltraWarm adds two additional options, similar to `_all`, to help manage hot and warm indices\. For a list of all warm or hot indices, make the following requests:

```
GET _warm
GET _hot
```

You can use these options in other requests that specify indices:

```
_cat/indices/_warm
_cluster/state/_all/_hot
```

## Restoring Warm Indices from Snapshots<a name="ultrawarm-snapshot"></a>

In addition to the standard repository for automated snapshots, UltraWarm adds a second repository, `cs-ultrawarm`\. Snapshots in `cs-ultrawarm` have the same 14\-day retention period as other automated snapshots\. For more information, see [Working with Amazon Elasticsearch Service Index Snapshots](es-managedomains-snapshots.md)\.

**To restore a warm index back to hot storage**

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

1. Delete the index:

   ```
   DELETE my-index
   ```

1. Restore the index from the snapshot, and change a few settings rather than using the settings in the snapshot:

   ```
   POST _snapshot/cs-ultrawarm/snapshot-name/_restore
   {
     "index_settings": {
       "index.auto_expand_replicas": true|false
     },
     "ignore_index_settings": [
       "index.blocks.ultrawarm_allow_delete",
       "index.refresh_interval"
     ]
   }
   ```