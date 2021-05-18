# Index State Management in Amazon Elasticsearch Service<a name="ism"></a>

Index State Management \(ISM\) lets you define custom management policies to automate routine tasks and apply them to indices and index patterns in Amazon Elasticsearch Service \(Amazon ES\)\. You no longer need to set up and manage external processes to run your index operations\.

A policy contains a default state and a list of states for the index to transition between\. Within each state, you can define a list of actions to perform and conditions that trigger these transitions\. A typical use case is to periodically delete old indices after a certain period of time\. For example, you can define a policy that moves your index into a `read_only` state after 30 days and then ultimately deletes it after 90 days\.

After you attach a policy to an index, ISM creates a job that runs every 30 to 48 minutes to perform policy actions, check conditions, and transition the index into different states\. The base time for this job to run is every 30 minutes, plus a random 0\-60% jitter is added to it to make sure you do not see a surge of activity from all your indices at the same time\.

ISM requires Elasticsearch 6\.8 or later\. Full documentation for the feature is available in the [Open Distro for Elasticsearch documentation](https://opendistro.github.io/for-elasticsearch-docs/docs/im/ism/)\.

**Note**  
The `policy_id` setting is no longer supported in index templates\. You can continue to automatically manage newly created indices with the [ISM template field](https://opendistro.github.io/for-elasticsearch-docs/docs/im/ism/policies/#sample-policy-with-ism-template)\.

## Sample policies<a name="ism-example"></a>

The following sample policies demonstrate how to automate common ISM use cases\.

### Hot to warm storage<a name="ism-example-warm"></a>

This sample policy moves an index from hot storage to [UltraWarm](ultrawarm.md) storage after seven days and deletes the index after 90 days\.

In this case, an index is initially in the `hot` state\. After seven days, ISM moves it to the `warm` state\. 83 days later, the service sends a notification to an Amazon Chime room that the index is being deleted and then permanently deletes it\.

```
{
  "policy": {
    "description": "Demonstrate a hot-warm-delete workflow.",
    "default_state": "hot",
    "schema_version": 1,
    "states": [{
        "name": "hot",
        "actions": [],
        "transitions": [{
          "state_name": "warm",
          "conditions": {
            "min_index_age": "7d"
          }
        }]
      },
      {
        "name": "warm",
        "actions": [{
          "warm_migration": {},
          "timeout": "24h",
          "retry": {
            "count": 5,
            "delay": "1h"
          }
        }],
        "transitions": [{
          "state_name": "delete",
          "conditions": {
            "min_index_age": "90d"
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
            "delete": {}
          }
        ]
      }
    ]
  }
}
```

### Hot to warm to cold storage<a name="ism-example-cold"></a>

 This sample policy moves an index from hot storage to UltraWarm, and eventually to  [cold storage](cold-storage.md)\. 

 The index is initially in the `hot` state\. After ten days, ISM moves it  to the `warm` state\. 80 days later, it moves the index to the  `cold` state\. 

```
{
  "policy": {
    "description": "Demonstrate a hot-warm-cold workflow.",
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
              "timestamp_field": "@timestamp"
            }
          }
        ]
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

This sample policy uses the `[snapshot](https://opendistro.github.io/for-elasticsearch-docs/docs/im/ism/policies/#snapshot)` operation to take a snapshot of an index as soon as it contains at least one document\. `repository` is the name of the manual snapshot repository you registered in Amazon S3\. `snapshot` is the name of the snapshot\. For snapshot prerequisites and steps to register a repository, see [Creating index snapshots in Amazon Elasticsearch Service](es-managedomains-snapshots.md)\.

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

## Attach a policy to an index<a name="ism-attach"></a>

After you create a policy, the next step is to attach it to an index or indices:

```
POST _opendistro/_ism/add/my-index
{
  "policy_id": "my-policy-id"
}
```

Alternatively, select the index in Kibana and choose **Apply policy**\.

## ISM templates<a name="ism-template"></a>

You can set up an `ism_template` field in a policy so when you create an index that matches the template pattern, the policy is automatically attached to that index\. In this example, any index you create with a name that begins with "log" is automatically matched to the ISM policy `my-policy-id`:

```
PUT _opendistro/_ism/policies/my-policy-id
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

For an example ISM template policy, see [Sample policy with ISM template](https://opendistro.github.io/for-elasticsearch-docs/docs/im/ism/policies/#sample-policy-with-ism-template)\.

## Differences<a name="ism-diff"></a>

Compared to Open Distro for Elasticsearch, ISM for Amazon Elasticsearch Service has several differences\. 

### ISM operations<a name="alerting-diff-op"></a>
+ Amazon ES supports one unique ISM operation, `warm_migration` and `cold_migration`\.

  If your domain has [UltraWarm](ultrawarm.md) enabled, the `warm_migration` action transitions the index to warm storage\. Even if the `warm_migration` action doesn’t complete within the [set timeout period](https://opendistro.github.io/for-elasticsearch-docs/docs/ism/policies/#actions), the migration to warm indices still continues\.

  Setting an `error_notifcation` for the `warm_migration` action might notify you that the `warm_migration` action failed if it didn’t complete within the timeout period\. This failed notification is only for your own reference\. The actual warm migration operation has no inherent timeout and continues to run until it eventually succeeds or fails\. 
+ If your domain runs Elasticsearch 7\.4 or later, Amazon ES supports the ISM `open` and `close` operations\.
+ If your domain runs Elasticsearch 7\.7 or later, Amazon ES supports the ISM `snapshot` operation\.

### ISM settings<a name="ism-diff-settings"></a>

Open Distro for Elasticsearch lets you change all available ISM settings using the `_cluster/settings` API\. On Amazon ES, you can only change the following settings:
+ **Cluster\-level settings:**
  + `enabled`
  + `history.enabled`
+ **Index\-level settings:**
  + `rollover_alias`

   