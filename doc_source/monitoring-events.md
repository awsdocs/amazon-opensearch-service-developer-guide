# Monitoring OpenSearch Service events with Amazon EventBridge<a name="monitoring-events"></a>

Amazon OpenSearch Service integrates with Amazon EventBridge to notify you of certain events that affect your domains\. Events from AWS services are delivered to EventBridge in near real time\. The same events are also sent to [Amazon CloudWatch Events](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/WhatIsCloudWatchEvents.html), the predecessor of Amazon EventBridge\. You can write simple rules to indicate which events are of interest to you, and what automated actions to take when an event matches a rule\. The actions that can be automatically triggered include the following:
+ Invoking an AWS Lambda function
+ Invoking an Amazon EC2 Run Command
+ Relaying the event to Amazon Kinesis Data Streams
+ Activating an AWS Step Functions state machine
+ Notifying an Amazon SNS topic or an Amazon SQS queue

For more information, see [Get started with Amazon EventBridge](https://docs.aws.amazon.com/eventbridge/latest/userguide/eb-get-started.html) in the *Amazon EventBridge User Guide*\.

## Service software update events<a name="monitoring-events-sso-available"></a>

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
    "description": "Service software update [R20200330-p1] available."
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
    "description": "Service software update [R20200330-p1] started."
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

### Service software update failed<a name="monitoring-events-sso-failed"></a>

OpenSearch Service sends this event when a service software update failed\.

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
    "severity": "Medium",
    "description": "Service software update [R20200330-p1] failed."
  }
}
```

### Service software update required<a name="monitoring-events-sso-required"></a>

OpenSearch Service sends this event when a service software update is required\.

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
    "description": "Service software update [R20200330-p1] available. Update will be automatically installed after [30/04/2020] if no action is taken."
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
    "description": "Auto-Tune has identified new settings for your domain that require a blue/green deployment. You can schedule the deployment for your preferred time."
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
    "description": "Auto-Tune is now disabled. All settings have been reverted. Auto-Tune will continue to evaluate cluster performance and provide recommendations.",
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
    "description": "Auto-Tune is now disabled. The most-recent settings by Auto-Tune have been retained. Auto-Tune will continue to evaluate cluster performance and provide recommendations.",
    "completionTime": "{iso8601-timestamp}"
  }
}
```