# Service software updates in Amazon OpenSearch Service<a name="service-software"></a>

**Note**  
For explanations of the changes and additions made in each *major* \(non\-patch\) service software update, see the [release notes](release-notes.md)\.

Amazon OpenSearch Service regularly releases service software updates that add features or otherwise improve your domains\. The **Notifications** panel in the console is the easiest way to see if an update is available or to check the status of an update\. Each notification includes details about the service software update\. All service software updates use blue/green deployments to minimize downtime\.

Service software updates differ from OpenSearch *version* upgrades\. For information about upgrading to a later version of OpenSearch, see [Upgrading Amazon OpenSearch Service domains](version-migration.md)\.

**Topics**
+ [Optional versus required updates](#service-software-optional-required)
+ [Patch updates](#service-software-patches)
+ [Considerations](#service-software-considerations)
+ [Starting a service software update](#service-software-requesting)
+ [Scheduling software updates during off\-peak windows](#service-software-offpeak)
+ [Monitoring service software updates](#service-software-monitor)
+ [When domains are ineligible for an update](#service-software-ineligible)

## Optional versus required updates<a name="service-software-optional-required"></a>

OpenSearch Service has two broad categories of service software updates:

### Optional updates<a name="service-software-optional"></a>

Optional service software updates generally include enhancements and support for new features or functionality\. Optional updates aren't enforced on your domains, and there's no hard deadline to install them\. The availability of the update is communicated through email and a console notification\. You can choose to apply the update immediately or reschedule it for a more appropriate date and time\. You can also schedule it during the domain's [off\-peak window](off-peak.md)\. The majority of software updates are optional\.

Regardless of whether or not you schedule an update, if you make a change on the domain that causes a [blue/green deployment](managedomains-configuration-changes.md), OpenSearch Service automatically updates your domain for you\.

You can configure your domain to automatically apply optional updates during [off\-peak hours](off-peak.md)\. When this option is turned on, OpenSearch Service waits 13 days from when an optional update is available and then schedules the update after 72 hours \(three days\)\. You receive a console notification when the update is scheduled and you can choose to reschedule it for a later date\.

To turn on automatic software updates, select **Enable automatic software update** when you create or update your domain\. To configure the same setting using the AWS CLI, set `--software-update-options` to `true` when you create or update your domain\.

### Required updates<a name="service-software-required"></a>

Required service software updates generally include critical security fixes or other mandatory updates to ensure the continued integrity and functionality of your domain\. Examples of required updates are Log4j Common Vulnerabilities and Exposures \(CVEs\) and enforcement of Instance Metadata Service Version 2 \(IMDSv2\)\. The number of mandatory updates in a year is usually less than three\.

OpenSearch Service automatically schedules these updates and notifies you 72 hours \(three days\) before the scheduled update through email and a console notification\. You can choose to apply the update immediately or reschedule it for a more appropriate date and time *within the allowed timeframe\.* You can also schedule it during the domain's next [off\-peak window](off-peak.md)\. If you take no action on a required update and you don't make any domain changes that cause a blue/green deployment, OpenSearch Service can initiate the update at any time beyond the specified deadline \(typically 14 days from availability\), within the domain's off\-peak window\.

Regardless of when the update is scheduled for, if you make a change on the domain that causes a [blue/green deployment](managedomains-configuration-changes.md), OpenSearch Service automatically updates your domain for you\.

## Patch updates<a name="service-software-patches"></a>

Service software versions that end in "\-P" and a number, such as R20211203\-*P4*, are patch releases\. Patches are likely to include performance improvements, minor bug fixes, and security fixes or posture improvements\. Patch releases do not include new features or breaking changes, and they generally don't have a direct or noticeable impact on users\. The service software notification tells you if a patch release is optional or mandatory\.

## Considerations<a name="service-software-considerations"></a>

Consider the following when deciding whether to update your domain:
+ Manually updating your domain lets you take advantage of new features more quickly\. When you choose **Update**, OpenSearch Service places the request in a queue and begins the update when it has time\. 
+ When you initiate a service software update, OpenSearch Service sends a notification when the update starts and when it completes\.
+ Software updates use blue/green deployments to minimize downtime\. Updates can temporarily strain a cluster's dedicated master nodes, so make sure to maintain sufficient capacity to handle the associated overhead\.
+ Updates typically complete within minutes, but can also take several hours or even days if your system is experiencing heavy load\. Consider updating your domain during the configured [off\-peak window](off-peak.md) to avoid long update periods\. 

## Starting a service software update<a name="service-software-requesting"></a>

You can request a service software update through the OpenSearch Service console, the AWS CLI, or one of the SDKs\. 

### Console<a name="service-software-request-console"></a>

**To request a service software update**

1. Open the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home)\.

1. Select the domain name to open its configuration\.

1. Choose **Actions**, **Update** and select one of the following options:
   + **Apply update now** \- Immediately schedules the action to happen in the current hour *if there's capacity available*\. If capacity isn't available, we provide other available time slots to choose from\.
   + **Schedule it in off\-peak window** – Only available if the off\-peak window is enabled for the domain\. Schedules the update to take place during the domain's configured off\-peak window\. There's no guarantee that the update will happen during the next immediate window\. Depending on capacity, it might happen in subsequent days\. For more information, see [Scheduling software updates during off\-peak windows](#service-software-offpeak)\.
   + **Schedule for specific date and time** – Schedules the update to take place at a specific date and time\. If the time that you specify is unavailable for capacity reasons, you can select a different time slot\.

   If you schedule the update for a later date \(within or outside the domain's off\-peak window\), you can reschedule it at any time\. For instructions, see [Rescheduling actions](off-peak.md#off-peak-reschedule)\.

1. Choose **Confirm**\.

### AWS CLI<a name="service-software-request-cli"></a>

Send a [start\-service\-software\-update](https://docs.aws.amazon.com/cli/latest/reference/opensearch/start-service-software-update.html) AWS CLI request to initiate a service software update\. This example adds the update to the queue immediately:

```
aws opensearch start-service-software-update \
  --domain-name my-domain \
  --schedule-at "NOW"
```

**Response**:

```
{
    "ServiceSoftwareOptions": {
        "CurrentVersion": "R20220928-P1",
        "NewVersion": "R20220928-P2",
        "UpdateAvailable": true,
        "Cancellable": true,
        "UpdateStatus": "PENDING_UPDATE",
        "Description": "",
        "AutomatedUpdateDate": "1969-12-31T16:00:00-08:00",
        "OptionalDeployment": true
    }
}
```

**Tip**  
After you request an update, you have a narrow window of time in which you can cancel it\. The duration of this `PENDING_UPDATE` state can vary greatly and depends on your AWS Region and the number of concurrent updates that OpenSearch Service is performing\. To cancel an update, use the console or `cancel-service-software-update` AWS CLI command\.

If the request fails with a `BaseException`, it means that the time you specified isn't available for capacity reasons, and you must specify a different time\. OpenSearch Service provides alternate available slot suggestions in the response\.

### AWS SDKs<a name="service-software-request-sdk"></a>

This sample Python script uses the [describe\_domain](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/opensearch.html#OpenSearchService.Client.describe_domain) and [start\_service\_software\_update](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/opensearch.html#OpenSearchService.Client.start_service_software_update) methods from the AWS SDK for Python \(Boto3\) to check whether a domain is eligible for a service software update and if so, starts the update\. You must provide a value for `domain_name`\.

```
import boto3
from botocore.config import Config
import time

# Build the client using the default credential configuration.
# You can use the CLI and run 'aws configure' to set access key, secret
# key, and default region.

my_config = Config(
    # Optionally lets you specify a Region other than your default.
    region_name='us-east-1'
)

domain_name = ''  # The name of the domain to check and update

client = boto3.client('opensearch', config=my_config)


def getUpdateStatus(client):
    """Determines whether the domain is eligible for an update"""
    response = client.describe_domain(
        DomainName=domain_name
    )
    sso = response['DomainStatus']['ServiceSoftwareOptions']
    if sso['UpdateStatus'] == 'ELIGIBLE':
        print('Domain [' + domain_name + '] is eligible for a service software update from version ' +
              sso['CurrentVersion'] + ' to version ' + sso['NewVersion'])
        updateDomain(client)
    else:
        print('Domain is not eligible for an update at this time.')


def updateDomain(client):
    """Starts a service software update for the eligible domain"""
    response = client.start_service_software_update(
        DomainName=domain_name
    )
    print('Updating domain [' + domain_name + '] to version ' +
          response['ServiceSoftwareOptions']['NewVersion'] + '...')
    waitForUpdate(client)


def waitForUpdate(client):
    """Waits for the domain to finish updating"""
    response = client.describe_domain(
        DomainName=domain_name
    )
    status = response['DomainStatus']['ServiceSoftwareOptions']['UpdateStatus']
    if status == 'PENDING_UPDATE' or status == 'IN_PROGRESS':
        time.sleep(30)
        waitForUpdate(client)
    elif status == 'COMPLETED':
        print('Domain [' + domain_name +
              '] successfully updated to the latest software version')
    else:
        print('Domain is not currently being updated.')

def main():
    getUpdateStatus(client)
```

## Scheduling software updates during off\-peak windows<a name="service-software-offpeak"></a>

Each OpenSearch Service domain created after February 16, 2023 has a daily 10\-hour window between 10:00 P\.M\. and 8:00 A\.M\. local time that we consider the [off\-peak window](off-peak.md)\. OpenSearch Service uses this window to schedule service software updates for the domain\. Off\-peak updates help to minimize strain on a cluster's dedicated master nodes during higher traffic periods\. OpenSearch Service can't initiate updates outside of this 10\-hour window without your consent\.
+ For *optional* updates, OpenSearch Service notifies you of the update's availability and prompts you to schedule the update during an upcoming off\-peak window\.
+ For *required* updates, OpenSearch Service automatically schedules the update during an upcoming off\-peak window and notifies you three days ahead of time\. You can reschedule the update \(for within or outside the off\-peak window\), but only within the required timeframe for the update to be completed\.

For each domain, you can choose to override the default 10:00 P\.M\. start time with a custom time\. For instructions, see [Configuring a custom off\-peak window](off-peak.md#off-peak-custom)\.

### Console<a name="service-software-offpeak-console"></a>

**To schedule an update during an upcoming off\-peak window**

1. Open the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home )\.

1. Select the domain name to open its configuration\.

1. Choose **Actions**, **Update**\.

1. Select **Schedule it in off\-peak window**\.

1. Choose **Confirm**\.

You can view the scheduled action on the **Off\-peak window** tab and reschedule it at any time\. See [Viewing scheduled actions](off-peak.md#off-peak-view)\.

### CLI<a name="service-software-offpeak-cli"></a>

To schedule an update during an upcoming off\-peak window using the AWS CLI, send a [StartServiceSoftwareUpdate](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_StartServiceSoftwareUpdate.html) request and specify `OFF_PEAK_WINDOW` for the `--schedule-at` parameter:

```
aws opensearch start-service-software-update \
  --domain-name my-domain \
  --schedule-at "OFF_PEAK_WINDOW"
```

## Monitoring service software updates<a name="service-software-monitor"></a>

OpenSearch Service sends a [notification](managedomains-notifications.md) when a service software update is available, required, started, completed, or failed\. You can view these notifications on the **Notifications** panel of the OpenSearch Service console\. The notification severity is `Informational` if the update is optional and `High` if it's required\. 

OpenSearch Service also sends service software events to Amazon EventBridge\. You can use EventBridge to configure rules that send an email or perform a specific action when an event is received\. For an example walkthrough, see [Tutorial: Sending Amazon SNS alerts for available software updates](sns-events.md)\.

To see the format of each service software event sent to Amazon EventBridge, see [Service software update events](monitoring-events.md#monitoring-events-sso)\.

## When domains are ineligible for an update<a name="service-software-ineligible"></a>

Your domain is ineligible for a service software update if it's in any of the following states:


| State | Description | 
| --- | --- | 
| Domain in processing |  The domain is in the middle of a configuration change\. Check update eligibility after the operation completes\.  | 
| Red cluster status |  One or more indexes in the cluster is red\. For troubleshooting steps, see [Red cluster status](handling-errors.md#handling-errors-red-cluster-status)\.  | 
| High error rate |  The OpenSearch cluster is returning a large number of 5*xx* errors when attempting to process requests\. This problem is usually the result of too many simultaneous read or write requests\. Consider reducing traffic to the cluster or scaling your domain\.  | 
| Split brain |  *Split brain* means your OpenSearch cluster has more than one master node and has split into two clusters that never will rejoin on their own\. You can avoid split brain by using the recommended number of [dedicated master nodes](managedomains-dedicatedmasternodes.md)\. For help recovering from split brain, contact [AWS Support](https://console.aws.amazon.com/support/home)\.  | 
| Amazon Cognito integration issue |  Your domain uses [authentication for OpenSearch Dashboards](cognito-auth.md), and OpenSearch Service can't find one or more Amazon Cognito resources\. This problem usually occurs if the Amazon Cognito user pool is missing\. To correct the issue, recreate the missing resource and configure the OpenSearch Service domain to use it\.  | 
| Other service issue |  Issues with OpenSearch Service itself might cause your domain to display as ineligible for an update\. If none of the previous conditions apply to your domain and the problem persists for more than a day, contact [AWS Support](https://console.aws.amazon.com/support/home)\.  | 