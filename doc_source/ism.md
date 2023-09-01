# Index State Management in Amazon OpenSearch Service<a name="ism"></a>

Index State Management \(ISM\) in Amazon OpenSearch Service lets you define custom management policies that automate routine tasks, and apply them to indexes and index patterns\. You no longer need to set up and manage external processes to run your index operations\.

A policy contains a default state and a list of states for the index to transition between\. Within each state, you can define a list of actions to perform and conditions that trigger these transitions\. A typical use case is to periodically delete old indexes after a certain period of time\. For example, you can define a policy that moves your index into a `read_only` state after 30 days and then ultimately deletes it after 90 days\.

After you attach a policy to an index, ISM creates a job that runs every 5 to 8 minutes \(or 30 to 48 minutes for pre\-1\.3 clusters\) to perform policy actions, check conditions, and transition the index into different states\. The base time for this job to run is every 5 minutes, plus a random 0\-60% jitter is added to it to make sure you do not see a surge of activity from all your indexes at the same time\. ISM doesn't run jobs if the cluster state is red\.

ISM requires OpenSearch or Elasticsearch 6\.8 or later\. Full documentation is available in the [OpenSearch documentation](https://opensearch.org/docs/im-plugin/ism/index/)\.

**Important**  
You can no longer use index templates to apply ISM policies to newly created indexes\. You can continue to automatically manage newly created indexes with the [ISM template field](https://opensearch.org/docs/latest/im-plugin/ism/policies/#sample-policy-with-ism-template-for-auto-rollover)\. This update introduces a breaking change that affects existing CloudFormation templates using this setting\. 

## Create an ISM policy<a name="ism-start"></a>

**To get started with Index State Management**

1. Open the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home)\.

1. Select the domain that you want to create an ISM policy for\.

1. From the domain's dashboard, navigate to the OpenSearch Dashboards URL and sign in with your master username and password\. The URL follows this format:

   ```
   domain-endpoint/_dashboards/
   ```

1. Open the left navigation panel within OpenSearch Dashboards and choose **Index Management**, then **Create policy**\. 

1. Use the [visual editor](https://opensearch.org/docs/latest/im-plugin/ism/index/#visual-editor) or [JSON editor](https://opensearch.org/docs/latest/im-plugin/ism/index/#json-editor) to create policies\. We recommend using the visual editor as it offers a more structured way of defining policies\. For help creating policies, see the [sample policies](#ism-example) below\.

1. After you create a policy, attach it to one or more indexes:

   ```
   POST _plugins/_ism/add/my-index
   {
     "policy_id": "my-policy-id"
   }
   ```
**Note**  
If your domain is running a legacy Elasticsearch version, use `_opendistro` instead of `_plugins`\.

   Alternatively, select the index in OpenSearch Dashboards and choose **Apply policy**\.

## Sample policies<a name="ism-example"></a>

The following sample policies demonstrate how to automate common ISM use cases\.

### Hot to warm to cold storage<a name="ism-example-cold"></a>

This sample policy moves an index from hot storage to [UltraWarm](ultrawarm.md), and eventually to  [cold storage](cold-storage.md)\. Then, it deletes the index\.

The index is initially in the `hot` state\. After ten days, ISM moves it  to the `warm` state\. 80 days later, after the index is 90 days old, ISM moves the index to the  `cold` state\. After a year, the service sends a notification to an Amazon Chime room that the index is being deleted and then permanently deletes it\. 

Note that cold indexes require the `cold_delete` operation rather than the normal `delete` operation\. Also note that an explicit `timestamp_field` is required in your data in order to manage cold indexes with ISM\.

```
{
  "policy": {
    "description": "Demonstrate a hot-warm-cold-delete workflow.",
    "default_state": "hot",
    "schema_version": 1,
    "states": [{
        "name": "hot",
        "actions": [],
        "transitions": [{
          "state_name": "warm",
          "conditions": {
            "min_index_age": "10d"
          }
        }]
      },
      {
        "name": "warm",
        "actions": [{
          "warm_migration": {},
          "retry": {
            "count": 5,
            "delay": "1h"
          }
        }],
        "transitions": [{
          "state_name": "cold",
          "conditions": {
            "min_index_age": "90d"
          }
        }]
      },
      {
        "name": "cold",
        "actions": [{
            "cold_migration": {
              "timestamp_field": "<your timestamp field>"
            }
          }
        ],
        "transitions": [{
          "state_name": "delete",
          "conditions": {
             "min_index_age": "365d"
          }
        }]
      },
      {
        "name": "delete",
        "actions": [{
          "notification": {
            "destination": {
              "chime": {
                "url": "<URL>"
              }
            },
            "message_template": {
              "source": "The index {{ctx.index}} is being deleted."
            }
          }
        },
        {
          "cold_delete": {}
        }]
      }
    ]
  }
}
```

### Reduce replica count<a name="ism-example-replica"></a>

This sample policy reduces replica count to zero after seven days to conserve disk space and then deletes the index after 21 days\. This policy assumes your index is non\-critical and no longer receiving write requests; having zero replicas carries some risk of data loss\.

```
{
  "policy": {
    "description": "Changes replica count and deletes.",
    "schema_version": 1,
    "default_state": "current",
    "states": [{
        "name": "current",
        "actions": [],
        "transitions": [{
          "state_name": "old",
          "conditions": {
            "min_index_age": "7d"
          }
        }]
      },
      {
        "name": "old",
        "actions": [{
          "replica_count": {
            "number_of_replicas": 0
          }
        }],
        "transitions": [{
          "state_name": "delete",
          "conditions": {
            "min_index_age": "21d"
          }
        }]
      },
      {
        "name": "delete",
        "actions": [{
          "delete": {}
        }],
        "transitions": []
      }
    ]
  }
}
```

### Take an index snapshot<a name="ism-example-snapshot"></a>

This sample policy uses the `[snapshot](https://opensearch.org/docs/im-plugin/ism/policies/#snapshot)` operation to take a snapshot of an index as soon as it contains at least one document\. `repository` is the name of the manual snapshot repository you registered in Amazon S3\. `snapshot` is the name of the snapshot\. For snapshot prerequisites and steps to register a repository, see [Creating index snapshots in Amazon OpenSearch Service](managedomains-snapshots.md)\.

```
{
  "policy": {
    "description": "Takes an index snapshot.",
    "schema_version": 1,
    "default_state": "empty",
    "states": [{
        "name": "empty",
        "actions": [],
        "transitions": [{
          "state_name": "occupied",
          "conditions": {
            "min_doc_count": 1
          }
        }]
      },
      {
        "name": "occupied",
        "actions": [{
          "snapshot": {
            "repository": "<my-repository>",
            "snapshot": "<my-snapshot>"
            }
          }],
          "transitions": []
      }
    ]
  }
}
```

## ISM templates<a name="ism-template"></a>

You can set up an `ism_template` field in a policy so when you create an index that matches the template pattern, the policy is automatically attached to that index\. In this example, any index you create with a name that begins with "log" is automatically matched to the ISM policy `my-policy-id`:

```
PUT _plugins/_ism/policies/my-policy-id
{
  "policy": {
    "description": "Example policy.",
    "default_state": "...",
    "states": [...],
    "ism_template": {
      "index_patterns": ["log*"],
      "priority": 100
    }
  }
}
```

For a more detailed example, see [Sample policy with ISM template for auto rollover](https://opensearch.org/docs/latest/im-plugin/ism/policies/#sample-policy-with-ism-template-for-auto-rollover)\.

## Differences<a name="ism-diff"></a>

Compared to OpenSearch and Elasticsearch, ISM for Amazon OpenSearch Service has several differences\. 

### ISM operations<a name="alerting-diff-op"></a>
+ OpenSearch Service supports three unique ISM operations, `warm_migration`, `cold_migration`, and `cold_delete`:
  + If your domain has [UltraWarm](ultrawarm.md) enabled, the `warm_migration` action transitions the index to warm storage\.
  + If your domain has [cold storage](cold-storage.md) enabled, the `cold_migration` action transitions the index to cold storage, and the `cold_delete` action deletes the index from cold storage\.

  Even if one of these actions doesn’t complete within the [set timeout period](https://opensearch.org/docs/im-plugin/ism/policies/#actions), the migration or deletion of indexes still continues\. Setting an [error\_notification](https://opensearch.org/docs/latest/im-plugin/ism/policies/#error-notifications) for one of the above actions will notify you that the action failed if it didn’t complete within the timeout period, but the notification is only for your own reference\. The actual operation has no inherent timeout and continues to run until it eventually succeeds or fails\. 
+ If your domain runs OpenSearch or Elasticsearch 7\.4 or later, OpenSearch Service supports the ISM `open` and `close` operations\.
+ If your domain runs OpenSearch or Elasticsearch 7\.7 or later, OpenSearch Service supports the ISM `snapshot` operation\.

### Cold storage ISM operations<a name="ism-cold-storage"></a>

For cold indexes, you must specify a `?type=_cold` parameter when you use the following ISM APIs:
+ [Add policy](https://opensearch.org/docs/latest/im-plugin/ism/api/#add-policy)
+ [Remove policy](https://opensearch.org/docs/latest/im-plugin/ism/api/#remove-policy-from-index)
+ [Update policy](https://opensearch.org/docs/latest/im-plugin/ism/api/#update-policy)
+ [Retry failed index](https://opensearch.org/docs/latest/im-plugin/ism/api/#retry-failed-index)
+ [Explain index](https://opensearch.org/docs/latest/im-plugin/ism/api/#explain-index)

These APIs for cold indexes have the following additional differences:
+ Wildcard operators are not supported except when you use it at the end\. For example, `_plugins/_ism/<add, remove, change_policy, retry, explain>/logstash-*` is supported but `_plugins/_ism/<add, remove, change_policy, retry, explain>/iad-*-prod` isn’t supported\.
+ Multiple index names and patterns are not supported\. For example, `_plugins/_ism/<add, remove, change_policy, retry, explain>/app-logs` is supported but `_plugins/_ism/<add, remove, change_policy, retry, explain>/app-logs,sample-data` isn’t supported\.

### ISM settings<a name="ism-diff-settings"></a>

OpenSearch and Elasticsearch let you change all available ISM settings using the `_cluster/settings` API\. On Amazon OpenSearch Service, you can only change the following [ISM settings](https://opensearch.org/docs/latest/im-plugin/ism/settings/):
+ **Cluster\-level settings:**
  + `plugins.index_state_management.enabled`
  + `plugins.index_state_management.history.enabled`
+ **Index\-level settings:**
  + `plugins.index_state_management.rollover_alias`

   