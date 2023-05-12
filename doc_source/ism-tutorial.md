# Tutorial: Automating Index State Management processes<a name="ism-tutorial"></a>

This tutorial demonstrates how to implement an ISM policy that automates routine index management tasks and apply them to indexes and index patterns\.

[Index State Management \(ISM\)](ism.md) in Amazon OpenSearch Service lets you automate recurring index management activities, so you can avoid using additional tools to manage index lifecycles\. You can create a policy that automates these operations based on index age, size, and other conditions, all from within your Amazon OpenSearch Service domain\.

OpenSearch Service supports three storage tiers: the default "hot" state for active writing and low\-latency analytics, UltraWarm for read\-only data up to three petabytes, and cold storage for unlimited long\-term archival\.

This tutorial presents a sample use case of handling time\-series data in daily indexes\. In this tutorial, you set up a policy that takes an automated snapshot of each attached index after 24 hours\. It then migrates the index from the default hot state to UltraWarm storage after two days, cold storage after 30 days, and finally deletes the index after 60 days\.

## Prerequisites<a name="ism-tutorialprerequisites"></a>
+ Your OpenSearch Service domain must be running Elasticsearch version 6\.8 or later\.
+ Your domain must have [UltraWarm](ultrawarm.md) and [cold storage](cold-storage.md) enabled\.
+ You must [register a manual snapshot repository](managedomains-snapshots.md#managedomains-snapshot-registerdirectory) for your domain\. 
+ Your user role needs sufficient permissions to access the OpenSearch Service console\. If necessary, validate and [configure access to your domain](ac.md)\.

## Step 1: Configure the ISM policy<a name="ism-tutorial-policy"></a>

First, configure an ISM policy in OpenSearch Dashboards\.

1. From your domain dashboard in the OpenSearch Service console, navigate to the OpenSearch Dashboards URL and sign in with your master username and password\. The URL follows this format: `domain-endpoint/_dashboards/`\.

1. In OpenSearch Dashboards, choose **Add sample data** and add one or more of the sample indexes to your domain\.

1. Open the left navigation panel and choose **Index Management**, then choose **Create policy**\.

1. Name the policy `ism-policy-example`\.

1. Replace the default policy with the following policy:

   ```
   {
     "policy": {
       "description": "Move indexes between storage tiers",
       "default_state": "hot",
       "states": [
         {
           "name": "hot",
           "actions": [],
           "transitions": [
             {
               "state_name": "snapshot",
               "conditions": {
                 "min_index_age": "24h"
               }
             }
           ]
         },
         {
           "name": "snapshot",
           "actions": [
             {
               "retry": {
                 "count": 5,
                 "backoff": "exponential",
                 "delay": "30m"
               },
               "snapshot": {
                 "repository": "snapshot-repo",
                 "snapshot": "ism-snapshot"
               }
             }
           ],
           "transitions": [
             {
               "state_name": "warm",
               "conditions": {
                 "min_index_age": "2d"
               }
             }
           ]
         },
         {
           "name": "warm",
           "actions": [
             {
               "retry": {
                 "count": 5,
                 "backoff": "exponential",
                 "delay": "1h"
               },
               "warm_migration": {}
             }
           ],
           "transitions": [
             {
               "state_name": "cold",
               "conditions": {
                 "min_index_age": "30d"
               }
             }
           ]
         },
         {
           "name": "cold",
           "actions": [
             {
               "retry": {
                 "count": 5,
                 "backoff": "exponential",
                 "delay": "1h"
               },
               "cold_migration": {
                 "start_time": null,
                 "end_time": null,
                 "timestamp_field": "@timestamp",
                 "ignore": "none"
               }
             }
           ],
           "transitions": [
             {
               "state_name": "delete",
               "conditions": {
                 "min_index_age": "60d"
               }
             }
           ]
         },
         {
           "name": "delete",
           "actions": [
             {
               "cold_delete": {}
             }
           ],
           "transitions": []
         }
       ],
       "ism_template": [
         {
           "index_patterns": [
             "index-*"
           ],
           "priority": 100
         }
       ]
     }
   }
   ```
**Note**  
The `ism_template` field automatically attaches the policy to any newly created index that matches one of the specified `index_patterns`\. In this case, all indexes that start with `index-`\. You can modify this field to match an index format in your environment\. For more information, see [ISM templates](ism.md#ism-template)\. 

1. In the `snapshot` section of the policy, replace `snapshot-repo` with the name of the [snapshot repository](managedomains-snapshots.md#managedomains-snapshot-registerdirectory) that you registered for your domain\. You can also optionally replace `ism-snapshot`, which will be the name of snapshot when it's created\.

1. Choose **Create**\. The policy is now visible on the **State management policies** page\.

## Step 2: Attach the policy to one or more indexes<a name="ism-tutorial-attach"></a>

Now that you created your policy, attach it to one or more indexes in your cluster\.

1. Go to the **Hot indicies** tab and search for `opensearch_dashboards_sample`, which lists all of the sample indexes that you added in step 1\.

1. Select all of the indexes and choose **Apply policy**, then choose the **ism\-policy\-example** policy that you just created\.

1. Choose **Apply**\.

You can monitor the indexes as they move through the various states on the **Policy managed indices** page\.