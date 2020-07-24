# Index State Management<a name="ism"></a>

Index State Management \(ISM\) lets you define custom management policies to automate routine tasks and apply them to indices and index patterns\. You no longer need to set up and manage external processes to run your index operations\.

A policy contains a default state and a list of states for the index to transition between\. Within each state, you can define a list of actions to perform and conditions that trigger these transitions\. A typical use case is to periodically delete old indices after a certain period of time\.

For example, you can define a policy that moves your index into a `read_only` state after 30 days and then ultimately deletes it after 90 days\.

ISM requires Elasticsearch 6\.8 or later\. Full documentation for the feature is available in the [Open Distro for Elasticsearch documentation](https://opendistro.github.io/for-elasticsearch-docs/docs/ism/)\.

**Note**  
After you attach a policy to an index, ISM creates a job that runs every 30 to 48 minutes to perform policy actions, check conditions, and transition the index into different states\. The base time for this job to run is every 30 minutes, plus a random 0\-60% jitter is added to it to make sure you do not see a surge of activity from all your indices at the same time\.

## Sample Policies<a name="ism-example"></a>

This first sample policy moves an index from hot storage to [UltraWarm](ultrawarm.md) storage after seven days and deletes the index after 90 days\.

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

This second, simpler sample policy reduces replica count to zero after seven days to conserve disk space and then deletes the index after 21 days\. This policy assumes your index is non\-critical and no longer receiving write requests; having zero replicas carries some risk of data loss\.

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

## Differences<a name="ism-diff"></a>

Compared to Open Distro for Elasticsearch, ISM for Amazon Elasticsearch Service has several differences\. 

### ISM Operations<a name="alerting-diff-op"></a>

Amazon ES supports a unique ISM operation, `warm_migration`\. If your domain has [UltraWarm](ultrawarm.md) enabled, this action transitions the index to warm storage\. The `warm_migration` action has a [default timeout](https://opendistro.github.io/for-elasticsearch-docs/docs/ism/policies/#actions) of 12 hours\. For large clusters, you might need to change this value, as shown in the [sample policy](#ism-example)\.

Amazon ES does not support the following ISM operations:
+ `open`
+ `close`
+ `snapshot`

### ISM Settings<a name="ism-diff-settings"></a>

Open Distro for Elasticsearch lets you change all available ISM settings using the `_cluster/settings` API\. On Amazon ES, you can only change the following settings:
+ **Cluster\-level settings:**
  + `enabled`
  + `history.enabled`
+ **Index\-level settings:**
  + `rollover_alias`
  + `policy_id`