# Monitoring OpenSearch Service events with Amazon EventBridge<a name="monitoring-events"></a>

Amazon OpenSearch Service integrates with Amazon EventBridge to notify you of certain events that affect your domains\. Events from AWS services are delivered to EventBridge in near real time\. The same events are also sent to [Amazon CloudWatch Events](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/WhatIsCloudWatchEvents.html), the predecessor of Amazon EventBridge\. You can write simple rules to indicate which events are of interest to you, and what automated actions to take when an event matches a rule\. The actions that can be automatically triggered include the following:
+ Invoking an AWS Lambda function
+ Invoking an Amazon EC2 Run Command
+ Relaying the event to Amazon Kinesis Data Streams
+ Activating an AWS Step Functions state machine
+ Notifying an Amazon SNS topic or an Amazon SQS queue

For more information, see [Get started with Amazon EventBridge](https://docs.aws.amazon.com/eventbridge/latest/userguide/eb-get-started.html) in the *Amazon EventBridge User Guide*\.

**Topics**
+ [Service software update events](#monitoring-events-sso)
+ [Auto\-Tune events](#monitoring-events-autotune)
+ [Cluster health events](#monitoring-events-cluster-health)
+ [VPC endpoint events](#monitoring-events-vpc)
+ [Node retirement events](#monitoring-events-nr)
+ [Domain error events](#monitoring-events-errors)
+ [Tutorial: Listening for Amazon OpenSearch Service EventBridge events](listening-events.md)
+ [Tutorial: Sending Amazon SNS alerts for available software updates](sns-events.md)

## Service software update events<a name="monitoring-events-sso"></a>

OpenSearch Service sends events to EventBridge when one of the following [service software update](service-software.md) events occur\.

### Service software update available<a name="monitoring-events-sso-available"></a>

OpenSearch Service sends this event when a service software update is available\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "01234567-0123-0123-0123-012345678901",
  "detail-type": "Amazon OpenSearch Service Software Update Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2016-11-01T13:12:22Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Service Software Update",
    "status": "Available",
    "severity": "Informational",
    "description": "Service software update R20220928 available. Service Software Deployment Mechanism:
                    Blue/Green. For more information on deployment configuration, please
                    see: https://docs.aws.amazon.com/opensearch-service/latest/developerguide/managedomains-configuration-changes.html"
  }
}
```

### Service software update scheduled<a name="monitoring-events-sso-scheduled"></a>

OpenSearch Service sends this event when a service software update has been scheduled\. For *optional* updates, you receive the notification on the scheduled date and you have the option to reschedule at any time\. For *required* updates, you receive the notification three days before the scheduled date, and you have the option to reschedule it within the mandatory window\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "01234567-0123-0123-0123-012345678901",
  "detail-type": "Amazon OpenSearch Service Software Update Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2016-11-01T13:12:22Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Service Software Update",
    "status": "Scheduled",
    "severity": "High",
    "description": "A new service software update [R20200330-p1] has been scheduled at [21st May 2023 12:40 GMT].
                    Please see documentation for more information on scheduling software updates: 
                    https://docs.aws.amazon.com/opensearch-service/latest/developerguide/service-software.html."
  }
}
```

### Service software update rescheduled<a name="monitoring-events-sso-rescheduled"></a>

OpenSearch Service sends this event when an optional service software update has been rescheduled\. For more information, see [Optional versus required updates](service-software.md#service-software-optional-required)\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "01234567-0123-0123-0123-012345678901",
  "detail-type": "Amazon OpenSearch Service Software Update Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2016-11-01T13:12:22Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Service Software Update",
    "status": "Rescheduled",
    "severity": "High",
    "description": "The service software update [R20200330-p1], which was originally scheduled for 
                    [21st May 2023 12:40 GMT], has been rescheduled to [23rd May 2023 12:40 GMT]. 
                    Please see documentation for more information on scheduling software updates: 
                    https://docs.aws.amazon.com/opensearch-service/latest/developerguide/service-software.html."
  }
}
```

### Service software update started<a name="monitoring-events-sso-started"></a>

OpenSearch Service sends this event when a service software update has started\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "01234567-0123-0123-0123-012345678901",
  "detail-type": "Amazon OpenSearch Service Software Update Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2016-11-01T13:12:22Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Service Software Update",
    "status": "Started",
    "severity": "Informational",
    "description": "Service software update [R20200330-p1] started.
  }
}
```

### Service software update completed<a name="monitoring-events-sso-completed"></a>

OpenSearch Service sends this event when a service software update has completed\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "01234567-0123-0123-0123-012345678901",
  "detail-type": "Amazon OpenSearch Service Software Update Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2016-11-01T13:12:22Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Service Software Update",
    "status": "Completed",
    "severity": "Informational",
    "description": "Service software update [R20200330-p1] completed."
  }
}
```

### Service software update cancelled<a name="monitoring-events-sso-cancelled"></a>

OpenSearch Service sends this event when a service software update has been cancelled\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "01234567-0123-0123-0123-012345678901",
  "detail-type": "Amazon OpenSearch Service Software Update Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2016-11-01T13:12:22Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Service Software Update",
    "status": "Cancelled",
    "severity": "Informational",
    "description": "The scheduled service software update [R20200330-p1] has been cancelled as a 
                    newer update is available. Please schedule the latest update."
  }
}
```

### Scheduled service software update cancelled<a name="monitoring-events-scheduled-sso-cancelled"></a>

OpenSearch Service sends this event when a service software update that was previously scheduled for the domain has been cancelled\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "01234567-0123-0123-0123-012345678901",
  "detail-type": "Amazon OpenSearch Service Software Update Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2016-11-01T13:12:22Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Service Software Update",
    "status": "Cancelled",
    "severity": "Informational",
    "description": "The scheduled service software update [R20200330-p1] has been cancelled."
  }
}
```

### Service software update unexecuted<a name="monitoring-events-sso-unexecuted"></a>

OpenSearch Service sends this event when it can't initiate a service software update\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "01234567-0123-0123-0123-012345678901",
  "detail-type": "Amazon OpenSearch Service Software Update Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2016-11-01T13:12:22Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Service Software Update",
    "status": "Unexecuted",
    "severity": "Informational",
    "description": "The scheduled service software update [R20200330-p1] cannot be started. Reason: [reason]"
  }
}
```

### Service software update failed<a name="monitoring-events-sso-failed"></a>

OpenSearch Service sends this event when a service software update fails\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "01234567-0123-0123-0123-012345678901",
  "detail-type": "Amazon OpenSearch Service Software Update Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2016-11-01T13:12:22Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Service Software Update",
    "status": "Failed",
    "severity": "High",
    "description": "Installation of service software update [R20200330-p1] failed. [reason].
  }
}
```

### Service software update required<a name="monitoring-events-sso-required"></a>

OpenSearch Service sends this event when a service software update is required\. For more information, see [Optional versus required updates](service-software.md#service-software-optional-required)\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "01234567-0123-0123-0123-012345678901",
  "detail-type": "Amazon OpenSearch Service Software Update Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2016-11-01T13:12:22Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Service Software Update",
    "status": "Required",
    "severity": "High",
    "description": "Service software update [R20200330-p1] available. Update 
                    will be automatically installed after [21st May 2023] if no
                    action is taken. Service Software Deployment Mechanism: Blue/Green.
                    For more information on deployment configuration, please see: 
                    https://docs.aws.amazon.com/opensearch-service/latest/developerguide/managedomains-configuration-changes.html"
  }
}
```

## Auto\-Tune events<a name="monitoring-events-autotune"></a>

OpenSearch Service sends events to EventBridge when one of the following [Auto\-Tune](auto-tune.md) events occur\.

### Auto\-Tune pending<a name="monitoring-events-autotune-pending"></a>

OpenSearch Service sends this event when Auto\-Tune has identified tuning recommendations for improved cluster performance and availability\. You'll only see this event for domains with Auto\-Tune disabled\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "3acb26c8-397c-4c89-a80a-ce672a864c55",
  "detail-type": "Amazon OpenSearch Service Auto-Tune Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2020-10-30T22:06:31Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Auto-Tune Event",
    "severity": "Informational",
    "status": "Pending",
    "description": "Auto-Tune recommends new settings for your domain. Enable Auto-Tune to improve cluster stability and performance.",
    "scheduleTime": "{iso8601-timestamp}"
  }
}
```

### Auto\-Tune started<a name="monitoring-events-autotune-started"></a>

OpenSearch Service sends this event when Auto\-Tune begins to apply new settings to your domain\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "3acb26c8-397c-4c89-a80a-ce672a864c55",
  "detail-type": "Amazon OpenSearch Service Auto-Tune Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2020-10-30T22:06:31Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Auto-Tune Events",
    "severity": "Informational",
    "status": "Started",
    "scheduleTime": "{iso8601-timestamp}",
    "startTime": "{iso8601-timestamp}",
    "description" : "Auto-Tune is applying new settings to your domain."
  }
}
```

### Auto\-Tune requires a scheduled blue/green deployment<a name="monitoring-events-autotune-schedule"></a>

OpenSearch Service sends this event when Auto\-Tune has identified tuning recommendations that require a scheduled blue/green deployment\. 

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "3acb26c8-397c-4c89-a80a-ce672a864c55",
  "detail-type": "Amazon OpenSearch Service Auto-Tune Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2020-10-30T22:06:31Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Auto-Tune Event",
    "severity": "Low",
    "status": "Pending",
    "startTime": "{iso8601-timestamp}",
    "description": "Auto-Tune has identified new settings for your domain that require a blue/green deployment. 
                    You can schedule the deployment for your preferred time."
  }
}
```

### Auto\-Tune cancelled<a name="monitoring-events-autotune-cancel"></a>

OpenSearch Service sends this event when Auto\-Tune schedule has been cancelled because there is no pending tuning recommendations\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "3acb26c8-397c-4c89-a80a-ce672a864c55",
  "detail-type": "Amazon OpenSearch Service Auto-Tune Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2020-10-30T22:06:31Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Auto-Tune Event",
    "severity": "Low",
    "status": "Cancelled",
    "scheduleTime": "{iso8601-timestamp}",
    "description": "Auto-Tune has cancelled the upcoming blue/green deployment."
  }
}
```

### Auto\-Tune completed<a name="monitoring-events-autotune-complete"></a>

OpenSearch Service sends this event when Auto\-Tune has completed the blue/green deployment and the cluster is operational with new JVM settings in place\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "3acb26c8-397c-4c89-a80a-ce672a864c55",
  "detail-type": "Amazon OpenSearch Service Auto-Tune Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2020-10-30T22:06:31Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Auto-Tune Event",
    "severity": "Informational",
    "status": "Completed",
    "completionTime": "{iso8601-timestamp}",
    "description": "Auto-Tune has completed the blue/green deployment and successfully applied the updated settings."
  }
}
```

### Auto\-Tune disabled and changes reverted<a name="monitoring-events-autotune-disabled"></a>

OpenSearch Service sends this event when Auto\-Tune has been disabled and the applied changes were rolled back\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "3acb26c8-397c-4c89-a80a-ce672a864c55",
  "detail-type": "Amazon OpenSearch Service Auto-Tune Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2020-10-30T22:06:31Z",
  "region": "us-east-1",
  "resources": [ "arn:aws:es:us-east-1:123456789012:domain/test-domain" ],
  "detail": {
    "event": "Auto-Tune Event",
    "severity": "Informational",
    "status": "Completed",
    "description": "Auto-Tune is now disabled. All settings have been reverted. Auto-Tune will continue to evaluate
                    cluster performance and provide recommendations.",
    "completionTime": "{iso8601-timestamp}"
  }
}
```

### Auto\-Tune disabled and changes retained<a name="monitoring-events-autotune-retained"></a>

OpenSearch Service sends this event when Auto\-Tune has been disabled and the applied changes were retained\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "3acb26c8-397c-4c89-a80a-ce672a864c55",
  "detail-type": "Amazon OpenSearch Service Auto-Tune Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2020-10-30T22:06:31Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Auto-Tune Event",
    "severity": "Informational",
    "status": "Completed",
    "description": "Auto-Tune is now disabled. The most-recent settings by Auto-Tune have been retained. 
                    Auto-Tune will continue to evaluate cluster performance and provide recommendations.",
    "completionTime": "{iso8601-timestamp}"
  }
}
```

## Cluster health events<a name="monitoring-events-cluster-health"></a>

OpenSearch Service sends certain events to EventBridge when your cluster's health is compromised\.

### Red cluster recovery started<a name="monitoring-events-red-started"></a>

OpenSearch Service sends this event after your cluster status has been continuously red for more than an hour\. It attempts to automatically restore one or more red indexes from a snapshot in order to fix the cluster status\.

**Example**

The following is an example event of this type:

```
{
   "version":"0",
   "id":"01234567-0123-0123-0123-012345678901",
   "detail-type":"Amazon OpenSearch Service Cluster Status Notification",
   "source":"aws.es",
   "account":"123456789012",
   "time":"2016-11-01T13:12:22Z",
   "region":"us-east-1",
   "resources":[
      "arn:aws:es:us-east-1:123456789012:domain/test-domain"
   ],
   "detail":{
      "event":"Automatic Snapshot Restore for Red Indices",
      "status":"Started",
      "severity":"High",
      "description":"Your cluster status is red. We have started automatic snapshot restore for the red indices. 
                     No action is needed from your side. Red indices [red-index-0, red-index-1]"
   }
}
```

### Red cluster recovery partially completed<a name="monitoring-events-red-partial"></a>

OpenSearch Service sends this event when it was only able to restore a subset of red indexes from a snapshot while attempting to fix a red cluster status\.

**Example**

The following is an example event of this type:

```
{
   "version":"0",
   "id":"01234567-0123-0123-0123-012345678901",
   "detail-type":"Amazon OpenSearch Service Cluster Status Notification",
   "source":"aws.es",
   "account":"123456789012",
   "time":"2016-11-01T13:12:22Z",
   "region":"us-east-1",
   "resources":[
      "arn:aws:es:us-east-1:123456789012:domain/test-domain"
   ],
   "detail":{
      "event":"Automatic Snapshot Restore for Red Indices",
      "status":"Partially Restored",
      "severity":"High",
      "description":"Your cluster status is red. We were able to restore the following Red indices from 
                    snapshot: [red-index-0]. Indices not restored: [red-index-1]. Please refer https://docs.aws.amazon.com/opensearch-service/latest/developerguide/handling-errors.html#handling-errors-red-cluster-status for troubleshooting steps."
   }
}
```

### Red cluster recovery failed<a name="monitoring-events-red-failed"></a>

OpenSearch Service sends this event when it fails to restore any indexes while attempting to fix a red cluster status\.

**Example**

The following is an example event of this type:

```
{
   "version":"0",
   "id":"01234567-0123-0123-0123-012345678901",
   "detail-type":"Amazon OpenSearch Service Cluster Status Notification",
   "source":"aws.es",
   "account":"123456789012",
   "time":"2016-11-01T13:12:22Z",
   "region":"us-east-1",
   "resources":[
      "arn:aws:es:us-east-1:123456789012:domain/test-domain"
   ],
   "detail":{
      "event":"Automatic Snapshot Restore for Red Indices",
      "status":"Failed",
      "severity":"High",
      "description":"Your cluster status is red. We were unable to restore the Red indices automatically. 
                    Indices not restored: [red-index-0, red-index-1]. Please refer https://docs.aws.amazon.com/opensearch-service/latest/developerguide/handling-errors.html#handling-errors-red-cluster-status for troubleshooting steps."
   }
}
```

### Shards to be deleted<a name="monitoring-events-red-to-delete"></a>

OpenSearch Service sends this event when it has attempted to automatically fix your red cluster status after it was continuously red for 14 days, but one or more indexes remains red\. After 7 more days \(21 total days of being continuously red\), OpenSearch Service proceeds to [delete unassigned shards](#monitoring-events-red-deleted) on all red indexes\.

**Example**

The following is an example event of this type:

```
{
   "version":"0",
   "id":"01234567-0123-0123-0123-012345678901",
   "detail-type":"Amazon OpenSearch Service Cluster Status Notification",
   "source":"aws.es",
   "account":"123456789012",
   "time":"2022-04-09T10:36:48Z",
   "region":"us-east-1",
   "resources":[
      "arn:aws:es:us-east-1:123456789012:domain/test-domain"
   ],
   "detail":{
      "severity":"Medium",
      "description":"Your cluster status is red. Please fix the red indices as soon as possible. 
                     If not fixed by 2022-04-12 01:51:47+00:00, we will delete all unassigned shards,
                     the unit of storage and compute, for these red indices to recover your domain and make it green.
                     Please refer to https://docs.aws.amazon.com/opensearch-service/latest/developerguide/handling-errors.html#handling-errors-red-cluster-status for troubleshooting steps.
                     test_data, test_data1",
      "event":"Automatic Snapshot Restore for Red Indices",
      "status":"Shard(s) to be deleted"
   }
}
```

### Shards deleted<a name="monitoring-events-red-deleted"></a>

OpenSearch Service sends this event after your cluster status has been continuously red for 21 days\. It proceeds to delete the unassigned shards \(storage and compute\) on all red indexes\. For details, see [Automatic remediation of red clusters](handling-errors.md#handling-errors-red-cluster-status-auto-recovery)\.

**Example**

The following is an example event of this type:

```
{
   "version":"0",
   "id":"01234567-0123-0123-0123-012345678901",
   "detail-type":"Amazon OpenSearch Service Cluster Status Notification",
   "source":"aws.es",
   "account":"123456789012",
   "time":"2022-04-09T10:54:48Z",
   "region":"us-east-1",
   "resources":[
      "arn:aws:es:us-east-1:123456789012:domain/test-domain"
   ],
   "detail":{
      "severity":"High",
      "description":"We have deleted unassinged shards, the unit of storage and compute, in 
                     red indices: index-1, index-2 because these indices were red for more than
                     21 days and could not be restored with the automated restore process.
                     Please refer to https://docs.aws.amazon.com/opensearch-service/latest/developerguide/handling-errors.html#handling-errors-red-cluster-status for troubleshooting steps.",
      "event":"Automatic Snapshot Restore for Red Indices",
      "status":"Shard(s) deleted"
   }
}
```

### High shard count warning<a name="monitoring-events-shard-warning"></a>

OpenSearch Service sends this event when the average shard count across your hot data nodes has exceeded 90% of the recommended default limit of 1,000\. Although later versions of Elasticsearch and OpenSearch support a configurable max shard count per node limit, we recommend you have no more than 1,000 shards per node\. See [Choosing the number of shards](sizing-domains.md#bp-sharding)\.

**Example**

The following is an example event of this type:

```
{
  "version":"0",
  "id":"01234567-0123-0123-0123-012345678901",
  "detail-type":"Amazon OpenSearch Service Notification",
  "source":"aws.es",
  "account":"123456789012",
  "time":"2016-11-01T13:12:22Z",
  "region":"us-east-1",
  "resources":["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail":{
     "event":"High Shard Count",
     "status":"Warning",
     "severity":"Low",
     "description":"One or more data nodes have close to 1000 shards. To ensure optimum performance and stability of your 
                    cluster, please refer to the best practice guidelines - https://docs.aws.amazon.com/opensearch-service/latest/developerguide/sizing-domains.html#bp-sharding."
  }
}
```

### Shard count limit exceeded<a name="monitoring-events-shard-exceeded"></a>

OpenSearch Service sends this event when the average shard count across your hot data nodes has exceeded the recommended default limit of 1,000\. Although later versions of Elasticsearch and OpenSearch support a configurable max shard count per node limit, we recommend you have no more than 1,000 shards per node\. See [Choosing the number of shards](sizing-domains.md#bp-sharding)\.

**Example**

The following is an example event of this type:

```
{
  "version":"0",
  "id":"01234567-0123-0123-0123-012345678901",
  "detail-type":"Amazon OpenSearch Service Notification",
  "source":"aws.es",
  "account":"123456789012",
  "time":"2016-11-01T13:12:22Z",
  "region":"us-east-1",
  "resources":["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail":{
     "event":"High Shard Count",
     "status":"Warning",
     "severity":"Medium",
     "description":"One or more data nodes have more than 1000 shards. To ensure optimum performance and stability of your 
                    cluster, please refer to the best practice guidelines - https://docs.aws.amazon.com/opensearch-service/latest/developerguide/sizing-domains.html#bp-sharding."
  }
}
```

### Low disk space<a name="monitoring-events-disk"></a>

OpenSearch Service sends this event when one or more nodes in your cluster has less than 25% of available storage space, or less than 25 GB\. 

**Example**

The following is an example event of this type:

```
{
  "version":"0",
  "id":"01234567-0123-0123-0123-012345678901",
  "detail-type":"Amazon OpenSearch Service Notification",
  "source":"aws.es",
  "account":"123456789012",
  "time":"2017-12-01T13:12:22Z",
  "region":"us-east-1",
  "resources":["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail":{
     "event":"Low Disk Space",
     "status":"Warning",
     "severity":"Medium",
     "description":"One or more data nodes in your cluster has less than 25% of storage space or less than 25GB.
                   Your cluster will be blocked for writes at 20% or 20GB. Please refer to the documentation for more information - https://docs.aws.amazon.com/opensearch-service/latest/developerguide/handling-errors.html#troubleshooting-cluster-block"
  }
}
```

### Low disk watermark breach<a name="monitoring-events-watermark"></a>

OpenSearch Service sends this event when all nodes in your cluster have less than 10% of available storage space, or less than 10 GB\. When all nodes breach the low disk watermark, any new index results in a yellow cluster, and when all nodes fall below the high disk watermark, it will lead to a red cluster\.

**Example**

The following is an example event of this type:

```
{
  "version":"0",
  "id":"01234567-0123-0123-0123-012345678901",
  "detail-type":"Amazon OpenSearch Service Notification",
  "source":"aws.es",
  "account":"123456789012",
  "time":"2017-12-01T13:12:22Z",
  "region":"us-east-1",
  "resources":["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail":{
     "event":"Low Disk Watermark Breach",
     "status":"Warning",
     "severity":"Medium",
     "description":"Low Disk Watermark threshold is about to be breached. Once the threshold is breached, new index creation will be blocked on all
                    nodes to prevent the cluster status from turning red. Please increase disk size to suit your storage needs. For more information, 
                    see https://docs.aws.amazon.com/opensearch-service/latest/developerguide/handling-errors.html#troubleshooting-cluster-block".
  }
}
```

### EBS burst balance below 70%<a name="monitoring-events-ebs-burst-70"></a>

OpenSearch Service sends this event when the EBS burst balance on one or more data nodes falls below 70%\. EBS burst balance depletion can cause widespread cluster unavailability and throttling of I/O requests, which can lead to high latencies and timeouts on indexing and search requests\. For steps to fix this issue, see [Low EBS burst balance](handling-errors.md#handling-errors-low-ebs-burst)\.

**Example**

The following is an example event of this type:

```
{
  "version":"0",
  "id":"01234567-0123-0123-0123-012345678901",
  "detail-type":"Amazon OpenSearch Service Notification",
  "source":"aws.es",
  "account":"123456789012",
  "time":"2017-12-01T13:12:22Z",
  "region":"us-east-1",
  "resources":["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail":{
     "event":"EBS Burst Balance",
     "status":"Warning",
     "severity":"Medium",
     "description":"EBS burst balance on one or more data nodes is below 70%. 
                    Follow https://docs.aws.amazon.com/opensearch-service/latest/developerguide/handling-errors.html#handling-errors-low-ebs-burst
                    to fix this issue."
  }
}
```

### EBS burst balance below 20%<a name="monitoring-events-ebs-burst-20"></a>

OpenSearch Service sends this event when the EBS burst balance on one or more data nodes falls below 20%\. EBS burst balance depletion can cause widespread cluster unavailability and throttling of I/O requests, which can lead to high latencies and timeouts on indexing and search requests\. For steps to fix this issue, see [Low EBS burst balance](handling-errors.md#handling-errors-low-ebs-burst)\.

**Example**

The following is an example event of this type:

```
{
  "version":"0",
  "id":"01234567-0123-0123-0123-012345678901",
  "detail-type":"Amazon OpenSearch Service Notification",
  "source":"aws.es",
  "account":"123456789012",
  "time":"2017-12-01T13:12:22Z",
  "region":"us-east-1",
  "resources":["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail":{
     "event":"EBS Burst Balance",
     "status":"Warning",
     "severity":"High",
     "description":"EBS burst balance on one or more data nodes is below 20%.
                    Follow https://docs.aws.amazon.com/opensearch-service/latest/developerguide/handling-errors.html#handling-errors-low-ebs-burst
                    to fix this issue.
  }
}
```

### Disk throughput throttle<a name="monitoring-events-throughput-throttle"></a>

OpenSearch Service sends this event when read and write requests to your domain are being throttled due to the throughput limitations of your EBS volumes or EC2 instance\. If you receive this notification, consider scaling up your volumes or instances following AWS recommended best practices\. If your volume type is `gp2`, increase the volume size\. If your volume type is `gp3`, provision more throughput\. You can also check that your instance base and maximum EBS throughput are greater than or equal to the provisioned volume throughput, and can scale up accordingly\. 

**Example**

The following is an example event of this type:

```
{
  "version":"0",
  "id":"01234567-0123-0123-0123-012345678901",
  "detail-type":"Amazon OpenSearch Service Notification",
  "source":"aws.es",
  "account":"123456789012",
  "time":"2017-12-01T13:12:22Z",
  "region":"us-east-1",
  "resources":["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail":{
     "event":"Disk Throughput Throttle",
     "status":"Warning",
     "severity":"Medium",
     "description":"Your domain is experiencing throttling due to instance or volume throughput limitations. 
                    Please consider scaling your domain to suit your throughput needs. In July 2023, we improved 
                    the accuracy of throughput throttle calculation by replacing ‘Max volume throughput’ with 
                    ‘Provisioned volume throughput’. Please refer to the documentation for more information."
  }
}
```

### Large shard size<a name="monitoring-events-large-shard-size"></a>

OpenSearch Service sends this event when one or more of the shards in your cluster has exceeded either 60GiB or 75GiB\. To fix this issue, see the [sharding best practices](bp.md#bp-sharding-strategy)\.

**Example**

The following is an example event of this type:

```
{
  "version":"0",
  "id":"01234567-0123-0123-0123-012345678901",
  "detail-type":"Amazon OpenSearch Service Notification",
  "source":"aws.es",
  "account":"123456789012",
  "time":"2017-12-01T13:12:22Z",
  "region":"us-east-1",
  "resources":["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail":{
     "event":"Large Shard Size",
     "status":"Warning",
     "severity":"Medium",
     "description":"One or more shards are larger than either 60GiB or 75GiB. To ensure optimum cluster performance and stability, reduce shard sizes.
                    For more information, see https://docs.aws.amazon.com/opensearch-service/latest/developerguide/monitoring-events.html#monitoring-events-large-shard-size."
  }
}
```

### High JVM usage<a name="monitoring-events-high-jvm"></a>

OpenSearch Service sends this event when the `JVMMemoryPressure` metric for your domain has exceeded 80%\. If it exceeds 92% for 30 minutes, all write operations to your cluster will be blocked\. To ensure optimum cluster stability, reduce traffic to the cluster or scale your domain to provide sufficient memory for your workload\.

**Example**

The following is an example event of this type:

```
{
  "version":"0",
  "id":"01234567-0123-0123-0123-012345678901",
  "detail-type":"Amazon OpenSearch Service Notification",
  "source":"aws.es",
  "account":"123456789012",
  "time":"2017-12-01T13:12:22Z",
  "region":"us-east-1",
  "resources":["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail":{
     "event":"High JVM Usage",
     "status":"Warning",
     "severity":"High",
     "description":"JVM memory pressure has exceeded 80%. If it exceeds 92% for 30 minutes, all write operations to your cluster
                    will be blocked. To ensure optimum cluster stability, reduce traffic to the cluster or use larger instance types.
                    For more information, see https://docs.aws.amazon.com/opensearch-service/latest/developerguide/monitoring-events.html#monitoring-events-high-jvm."
  }
}
```

### Insufficient GC<a name="monitoring-events-insufficient-gc"></a>

OpenSearch Service sends this event when maximum JVM is above 70% and difference between the maximum and minimum is less than 30%\. This may indicate that the JVM is unable to reclaim sufficient memory during garbage collection cycles for your workload\. This can lead to increasingly slower responses and higher latencies; and in some cases even node drops due to timed out health checks\. To ensure optimum cluster stability, reduce traffic to the cluster or scale your domain to provide sufficient memory for your workload\.

**Example**

The following is an example event of this type:

```
{
  "version":"0",
  "id":"01234567-0123-0123-0123-012345678901",
  "detail-type":"Amazon OpenSearch Service Notification",
  "source":"aws.es",
  "account":"123456789012",
  "time":"2017-12-01T13:12:22Z",
  "region":"us-east-1",
  "resources":["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail":{
     "event":"Insufficient GC",
     "status":"Warning",
     "severity":"Medium",
     "description":"Maximum JVM is above 70% and JVM range is less than 30%. This may indicate insufficient garbage collection for your workload.
                    For more information, see https://docs.aws.amazon.com/opensearch-service/latest/developerguide/monitoring-events.html#monitoring-events-insufficient-gc."
  }
}
```

### Custom index routing warning<a name="monitoring-events-index-routing"></a>

OpenSearch Service sends this event when your domain is in processing state and contains indices with custom index\.routing\.allocation settings which can cause blue\-green deployments to get stuck\. Verify settings are applied properly\.

**Example**

The following is an example event of this type:

```
{
  "version":"0",
  "id":"01234567-0123-0123-0123-012345678901",
  "detail-type":"Amazon OpenSearch Service Notification",
  "source":"aws.es",
  "account":"123456789012",
  "time":"2017-12-01T13:12:22Z",
  "region":"us-east-1",
  "resources":["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail":{
     "event":"Custom Index Routing Warning",
     "status":"Warning",
     "severity":"Medium",
     "description":"Your domain is in processing state and contains indice(s) with custom index.routing.allocation 
                    settings which can cause blue-green deployments to get stuck. Verify settings are applied properly.
                    For more information, see https://docs.aws.amazon.com/opensearch-service/latest/developerguide/monitoring-events.html#monitoring-events-index-routing."
  }
}
```

### Failed shard lock<a name="monitoring-events-failed-shard-lock"></a>

OpenSearch Service sends this event when your domain is unhealthy due to unassigned shards with `[ShardLockObtainFailedException]`\. For more information, see [How do I resolve the in\-memory shard lock exception in Amazon OpenSearch Service?](https://aws.amazon.com/premiumsupport/knowledge-center/opensearch-in-memory-shard-lock/)

**Example**

The following is an example event of this type:

```
{
  "version":"0",
  "id":"01234567-0123-0123-0123-012345678901",
  "detail-type":"Amazon OpenSearch Service Notification",
  "source":"aws.es",
  "account":"123456789012",
  "time":"2017-12-01T13:12:22Z",
  "region":"us-east-1",
  "resources":["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail":{
     "event":"Failed Shard Lock",
     "status":"Warning",
     "severity":"Medium",
     "description":"Your domain is unhealthy due to unassigned shards with [ShardLockObtainFailedException]. For more information, 
                    see https://docs.aws.amazon.com/opensearch-service/latest/developerguide/monitoring-events.html#monitoring-events-failed-shard-lock."
}
```

## VPC endpoint events<a name="monitoring-events-vpc"></a>

OpenSearch Service sends certain events to EventBridge related to [AWS PrivateLink interface endpoints](vpc-interface-endpoints.md)\.

### VPC endpoint creation failed<a name="monitoring-events-vpc-create-fail"></a>

OpenSearch Service sends this event when it's unable to create a requested VPC endpoint\. This error might occur because you've reached the limit on the number of VPC endoints allowed within a Region\. You will also see this error if a specified subnet or security group doesn't exist\.

**Example**

The following is an example event of this type:

```
{
   "version":"0",
   "id":"01234567-0123-0123-0123-012345678901",
   "detail-type":"Amazon OpenSearch Service VPC Endpoint Notification",
   "source":"aws.es",
   "account":"123456789012",
   "time":"2016-11-01T13:12:22Z",
   "region":"us-east-1",
   "resources":[
      "arn:aws:es:us-east-1:123456789012:domain/test-domain"
   ],
   "detail":{
      "event":"VPC Endpoint Create Validation",
      "status":"Failed",
      "severity":"High",
      "description":"Unable to create VPC endpoint aos-0d4c74c0342343 for domain 
                    arn:aws:es:eu-south-1:123456789012:domain/my-domain due to the following validation failures: You've reached the limit on the
                    number of VPC endpoints that you can create in the AWS Region."
   }
}
```

### VPC endpoint update failed<a name="monitoring-events-vpc-update-fail"></a>

OpenSearch Service sends this event when it's unable to delete a requested VPC endpoint\.

**Example**

The following is an example event of this type:

```
{
   "version":"0",
   "id":"01234567-0123-0123-0123-012345678901",
   "detail-type":"Amazon OpenSearch Service VPC Endpoint Notification",
   "source":"aws.es",
   "account":"123456789012",
   "time":"2016-11-01T13:12:22Z",
   "region":"us-east-1",
   "resources":[
      "arn:aws:es:us-east-1:123456789012:domain/test-domain"
   ],
   "detail":{
      "event":"VPC Endpoint Update Validation",
      "status":"Failed",
      "severity":"High",
      "description":"Unable to update VPC endpoint aos-0d4c74c0342343 for domain 
                    arn:aws:es:eu-south-1:123456789012:domain/my-domain due to the following validation failures: <failure message>."
   }
}
```

### VPC endpoint deletion failed<a name="monitoring-events-vpc-delete-fail"></a>

OpenSearch Service sends this event when it's unable to delete a requested VPC endpoint\.

**Example**

The following is an example event of this type:

```
{
   "version":"0",
   "id":"01234567-0123-0123-0123-012345678901",
   "detail-type":"Amazon OpenSearch Service VPC Endpoint Notification",
   "source":"aws.es",
   "account":"123456789012",
   "time":"2016-11-01T13:12:22Z",
   "region":"us-east-1",
   "resources":[
      "arn:aws:es:us-east-1:123456789012:domain/test-domain"
   ],
   "detail":{
      "event":"VPC Endpoint Delete Validation",
      "status":"Failed",
      "severity":"High",
      "description":"Unable to delete VPC endpoint aos-0d4c74c0342343 for domain 
                    arn:aws:es:eu-south-1:123456789012:domain/my-domain due to the following validation failures: Specified subnet doesn't exist."
   }
}
```

## Node retirement events<a name="monitoring-events-nr"></a>

OpenSearch Service sends events to EventBridge when one of the following node retirement events occur\.

### Node retirement scheduled<a name="monitoring-events-nr-scheduled"></a>

OpenSearch Service sends this event when a node retirement has been scheduled\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "01234567-0123-0123-0123-012345678901",
  "detail-type": "Amazon OpenSearch Service Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2023-04-07T10:07:33Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Node Retirement Notification",
    "status": "Scheduled",
    "severity": "Medium",
    "description": "An automated action to retire and replace a node has been scheduled on your domain. 
                    The node will be replaced in the next off-peak window. For more information, see 
                    https://docs.aws.amazon.com/opensearch-service/latest/developerguide/monitoring-events.html."
  }
}
```

### Node retirement completed<a name="monitoring-events-nr-completed"></a>

OpenSearch Service sends this event when a node retirement has completed\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "01234567-0123-0123-0123-012345678901",
  "detail-type": "Amazon OpenSearch Service Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2023-04-07T10:07:33Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Node Retirement Notification",
    "status": "Completed",
    "severity": "Medium",
    "description": "The node has been retired and replaced with a new node."
  }
}
```

### Node retirement failed<a name="monitoring-events-nr-failed"></a>

OpenSearch Service sends this event when a node retirement fails\.

**Example**

The following is an example event of this type:

```
{
  "version": "0",
  "id": "01234567-0123-0123-0123-012345678901",
  "detail-type": "Amazon OpenSearch Service Notification",
  "source": "aws.es",
  "account": "123456789012",
  "time": "2023-04-07T10:07:33Z",
  "region": "us-east-1",
  "resources": ["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail": {
    "event": "Node Retirement Notification",
    "status": "Failed",
    "severity": "Medium",
    "description": "Node retirement failed. No actions are required from your end. We will automatically 
                    retry replacing the node."
  }
}
```

### <a name="monitoring-events-sso-required"></a>

## Domain error events<a name="monitoring-events-errors"></a>

OpenSearch Service sends events to EventBridge when one of the following domain errors occur\.

### Domain update validation failure<a name="monitoring-events-validation"></a>

OpenSearch Service sends this event if it encounters one or more validation failures when attempting to update or perform a configuration change on a domain\. For steps to resolve these failures, see [Troubleshooting validation errors](managedomains-configuration-changes.md#validation)\.

**Example**

The following is an example event of this type:

```
{
   "version":"0",
   "id":"01234567-0123-0123-0123-012345678901",
   "detail-type":"Amazon OpenSearch Service Domain Update Notification",
   "source":"aws.es",
   "account":"123456789012",
   "time":"2016-11-01T13:12:22Z",
   "region":"us-east-1",
   "resources":[
      "arn:aws:es:us-east-1:123456789012:domain/test-domain"
   ],
   "detail":{
      "event":"Domain Update Validation",
      "status":"Failed",
      "severity":"High",
      "description":"Unable to perform updates to your domain due to the following validation failures: <failures>
                    Please see the documentation for more information https://docs.aws.amazon.com/opensearch-service/latest/developerguide/managedomains-configuration-changes.html#validation"
   }
}
```

### KMS key inaccessible<a name="monitoring-events-kms-inaccessible"></a>

OpenSearch Service sends this event when it [can't access your AWS KMS key](encryption-at-rest.md#disabled-key)\.

**Example**

The following is an example event of this type:

```
{
  "version":"0",
  "id":"01234567-0123-0123-0123-012345678901",
  "detail-type":"Domain Error Notification",
  "source":"aws.es",
  "account":"123456789012",
  "time":"2016-11-01T13:12:22Z",
  "region":"us-east-1",
  "resources":["arn:aws:es:us-east-1:123456789012:domain/test-domain"],
  "detail":{
     "event":"KMS Key Inaccessible",
     "status":"Error",
     "severity":"High",
     "description":"The KMS key associated with this domain is inaccessible. You are at risk of losing access to your domain. 
                    For more information, please refer https://docs.aws.amazon.com/opensearch-service/latest/developerguide/encryption-at-rest.html#disabled-key."
  }
}
```