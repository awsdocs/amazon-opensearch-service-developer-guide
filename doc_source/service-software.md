# Service software updates in Amazon OpenSearch Service<a name="service-software"></a>

**Note**  
See the [release notes](release-notes.md) for descriptions of the changes and additions made in each major service software release\.

Amazon OpenSearch Service regularly releases system software updates that add features or otherwise improve your domains\. The **Notifications** panel in the console is the easiest way to see if an update is available or check the status of an update\. Each notification includes details about the service software update\. The notification severity is `Informational` if the update is optional and `High` if it's required\.

Service software updates differ from OpenSearch version upgrades\. For information about upgrading to a later version of OpenSearch, see [Upgrading Amazon OpenSearch Service domains](version-migration.md)\.

## Domain update considerations<a name="service-software-considerations"></a>

Consider the following when deciding whether to update your domain:
+ If you take no action on available updates, OpenSearch Service updates your domain service software automatically after a certain timeframe \(typically two weeks\)\. OpenSearch Service sends notifications when it starts the update and when the update is complete\.
+ If you start an update manually, OpenSearch Service doesn't send a notification when it starts the update, only when the update is complete\.
+ Software updates use [blue/green deployments](managedomains-configuration-changes.md) to minimizes downtime\. Updates can temporarily strain a cluster's dedicated master nodes, so make sure to maintain sufficient capacity to handle the associated overhead\. 

Manually updating your domain lets you take advantage of new features more quickly\. When you choose **Update**, OpenSearch Service places the request in a queue and begins the update when it has time\. Updates typically complete within minutes, but can also take several hours or even days if your system is experiencing heavy load\. Consider updating your domain at a low traffic time to avoid long update periods\. 

## Patch releases<a name="service-software-patches"></a>

Service software versions that end in "\-P" and a number, such as R20211203\-*P4*, are patch releases\. Patches are likely to include performance improvements, minor bug fixes, and security fixes or posture improvements\. Patch releases do not include new features or breaking changes, and they generally do not have a direct or noticeable impact on users\.

## Request a service software update \(console\)<a name="service-software-request"></a>

**To request a service software update \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com) and choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Amazon OpenSearch Service**\.

1. In the navigation pane, under **Domains**, choose the domain name to open its settings\.

1. Choose **Actions**, **Update** and confirm the update\.

## Request a service software update \(AWS CLI\)<a name="service-software-request-cli"></a>

Send the following AWS CLI command to request a service software update:

```
aws opensearch start-service-software-update --domain-name my-domain
```

For more information, see [start\-service\-software\-update](https://docs.aws.amazon.com/cli/latest/reference/opensearch/start-service-software-update.html) in the AWS CLI command reference and [StartServiceSoftwareUpdate](configuration-api.md#configuration-api-actions-startupdate) in the configuration API reference\.

**Tip**  
After requesting an update, you might have a narrow window of time in which you can cancel it\. The duration of this `PENDING_UPDATE` state can vary greatly and depends on your AWS Region and the number of concurrent updates OpenSearch Service is performing\. To cancel, use the console or `cancel-service-software-update` \(`CancelServiceSoftwareUpdate`\) command\.

## Request a service software update \(SDK\)<a name="service-software-request-sdk"></a>

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

## Monitoring service software update events<a name="service-software-monitor"></a>

OpenSearch Service sends a [notification](managedomains-notifications.md) when a service software update is available, required, started, completed, or failed\. You can view these notifications on the **Notifications** panel of the OpenSearch Service console\. It also sends these notifications as events to Amazon EventBridge, where you can configure rules that send an email or perform a specific action when an event is received\. For an example walkthrough, see [Tutorial: Sending Amazon SNS alerts for available software updates](sns-events.md)\.

For the format of each service software event sent to Amazon EventBridge, see [Service software update events](monitoring-events.md#monitoring-events-sso)\.

## When domains are ineligible for an update<a name="service-software-ineligible"></a>

Your domain is ineligible for a service software update if it's in any of the following states:


| State | Description | 
| --- | --- | 
| Domain in processing |  The domain is in the middle of a configuration change\. Check update eligibility after the operation completes\.  | 
| Red cluster status |  One or more indexes in the cluster is red\. For troubleshooting steps, see [Red cluster status](handling-errors.md#handling-errors-red-cluster-status)\.  | 
| High error rate |  The OpenSearch cluster is returning a large number of 5*xx* errors when attempting to process requests\. This problem is usually the result of too many simultaneous read or write requests\. Consider reducing traffic to the cluster or scaling your domain\.  | 
| Split brain |  *Split brain* means your OpenSearch cluster has more than one master node and has split into two clusters that never will rejoin on their own\. You can avoid split brain by using the recommended number of [dedicated master nodes](managedomains-dedicatedmasternodes.md)\. For help recovering from split brain, contact [AWS Support](https://console.aws.amazon.com/support/home)\.  | 
| Amazon Cognito integration issue |  Your domain uses [authentication for OpenSearch Dashboards](cognito-auth.md), and OpenSearch Service can't find one or more Amazon Cognito resources\. This problem usually occurs if the Amazon Cognito user pool is missing\. To correct the issue, recreate the missing resource and configure the OpenSearch Service domain to use it\.  | 
| Other OpenSearch Service service issue |  Issues with OpenSearch Service itself might cause your domain to display as ineligible for an update\. If none of the previous conditions apply to your domain and the problem persists for more than a day, contact [AWS Support](https://console.aws.amazon.com/support/home)\.  | 