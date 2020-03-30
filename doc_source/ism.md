# Index State Management<a name="ism"></a>

Index State Management \(ISM\) lets you define custom management policies to automate routine tasks and apply them to indices and index patterns\. You no longer need to set up and manage external processes to run your index operations\.

A policy contains a default state and a list of states for the index to transition between\. Within each state, you can define a list of actions to perform and conditions that trigger these transitions\. A typical use would be that you want to periodically delete old indices after a certain period of time\.

For example, you can define a policy that moves your index into a `read_only` state after 30 days and then ultimately deletes it after 90 days\.

ISM requires Elasticsearch 7\.1 or later\. Full documentation for the feature is available in the [Open Distro for Elasticsearch documentation](https://opendistro.github.io/for-elasticsearch-docs/docs/ism/)\.

**Note**  
After you attach a policy to an index, ISM creates a job that runs every 30 to 48 minutes to perform policy actions, check conditions, and transition the index into different states\. The base time for this job to run is every 30 minutes, plus a random 0\-60% jitter is added to it to make sure you do not see a surge of activity from all your indices at the same time\.

## Example Policy<a name="ism-example"></a>

The following example policy implements a `hot-delete` workflow\.

In this case, an index is initially in a `hot` state\. After a month, it changes to a `delete` state\. The service sends a notification to an Amazon Chime room that the index is being deleted and then permanently deletes it\.

```
{
  "policy": {
    "policy_id": "sample policy",
    "description": "hot-delete workflow",
    "default_state": "hot",
    "schema_version": 1,
    "states": [
      {
        "name": "hot",
        "actions": [],
        "transitions": [
          {
            "state_name": "delete",
            "conditions": {
              "min_index_age": "30d"
            }
          }
        ]
      },
      {
        "name": "delete",
        "actions": [
          {
            "notification": {
              "destination": {
                "chime": {
                  "url": "<URL>"
                }
              },
              "message_template": {
                "source": "The index {{ctx.index}} is being deleted"
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

## Differences<a name="ism-diff"></a>

Compared to Open Distro for Elasticsearch, the Amazon Elasticsearch Service \(Amazon ES\) ISM feature has two notable differences in ISM operations support and configurable settings\. 

### ISM Operations Support<a name="alerting-diff-op"></a>

Amazon ES does not support the following ISM operations:
+ `force_merge`
+ `open`
+ `close`

### ISM Settings<a name="ism-diff-settings"></a>

Open Distro for Elasticsearch lets you change all available ISM settings using the `_cluster/settings` API\. On Amazon ES, you can only change the following settings:
+ **Cluster\-level settings:**
  + `enabled`
  + `history.enabled`
+ **Index\-level settings:**
  + `roll_over`
  + `policy_id`