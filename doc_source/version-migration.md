# Upgrading Amazon OpenSearch Service domains<a name="version-migration"></a>

**Note**  
OpenSearch and Elasticsearch version upgrades differ from service software updates\. For information on updating the service software for your OpenSearch Service domain, see [Service software updates in Amazon OpenSearch Service](service-software.md)\.

Amazon OpenSearch Service offers in\-place upgrades for domains that run OpenSearch 1\.0 or later, or Elasticsearch 5\.1 or later\. If you use services like Amazon Kinesis Data Firehose or Amazon CloudWatch Logs to stream data to OpenSearch Service, check that these services support the newer version of OpenSearch before migrating\.

## Supported upgrade paths<a name="supported-upgrade-paths"></a>

Currently, OpenSearch Service supports the following upgrade paths:


| From version | To version | 
| --- | --- | 
| OpenSearch 1\.3 |  OpenSearch 2\.3 Version 2\.3 has the following breaking changes: [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/version-migration.html)  | 
| OpenSearch 1\.x | OpenSearch 1\.x | 
| Elasticsearch 7\.x |  Elasticsearch 7\.*x* or OpenSearch 1\.*x*  OpenSearch 1\.*x* introduces numerous breaking changes\. For details, see [Amazon OpenSearch Service rename \- Summary of changes](rename.md)\.   | 
|  Elasticsearch 6\.8  |  Elasticsearch 7\.*x* or OpenSearch 1\.*x*  Elasticsearch 7\.0 and OpenSearch 1\.0 include numerous breaking changes\. Before initiating an in\-place upgrade, we recommend [taking a manual snapshot](managedomains-snapshots.md) of the 6\.*x* domain, restoring it on a test 7\.*x* or OpenSearch 1\.*x* domain, and using that test domain to identify potential upgrade issues\. For breaking changes in OpenSearch 1\.0, see [Amazon OpenSearch Service rename \- Summary of changes](rename.md)\. Like Elasticsearch 6\.*x*, indexes can only contain one mapping type, but that type must now be named `_doc`\. As a result, certain APIs no longer require a mapping type in the request body \(such as the `_bulk` API\)\. For new indexes, self\-hosted Elasticsearch 7\.*x* and OpenSearch 1\.*x* have a default shard count of one\. OpenSearch Service domains on Elasticsearch 7\.*x* and later retain the previous default of five\.   | 
| Elasticsearch 6\.*x* | Elasticsearch 6\.*x* | 
| Elasticsearch 5\.6 |  Elasticsearch 6\.*x*  Indexes created in version 6\.*x* no longer support multiple mapping types\. Indexes created in version 5\.*x* still support multiple mapping types when restored into a 6\.*x* cluster\. Check that your client code creates only a single mapping type per index\. To minimize downtime during the upgrade from Elasticsearch 5\.6 to 6\.*x*, OpenSearch Service reindexes the `.kibana` index to `.kibana-6`, deletes `.kibana`, creates an alias named `.kibana`, and maps the new index to the new alias\.   | 
| Elasticsearch 5\.x | Elasticsearch 5\.x | 

The upgrade process consists of three steps:

1. **Pre\-upgrade checks** – OpenSearch Service checks for issues that can block an upgrade and doesn't proceed to the next step unless these checks succeed\.

1. **Snapshot** – OpenSearch Service takes a snapshot of the OpenSearch or Elasticsearch cluster and doesn't proceed to the next step unless the snapshot succeeds\. If the upgrade fails, OpenSearch Service uses this snapshot to restore the cluster to its original state\. For more information see [Can't downgrade after upgrade](handling-errors.md#troubleshooting-upgrade-snapshot)\.

1. **Upgrade** – OpenSearch Service starts the upgrade, which can take from 15 minutes to several hours to complete\. OpenSearch Dashboards might be unavailable during some or all of the upgrade\.

## Starting an upgrade \(console\)<a name="starting-upgrades"></a>

The upgrade process is irreversible and can't be paused or cancelled\. During an upgrade, you can't make configuration changes to the domain\. Before starting an upgrade, double\-check that you want to proceed\. You can use these same steps to perform the pre\-upgrade check without actually starting an upgrade\.

If the cluster has dedicated master nodes, OpenSearch upgrades complete without downtime\. Otherwise, the cluster might be unresponsive for several seconds post\-upgrade while it elects a master node\.

**To upgrade a domain to a later version of OpenSearch or Elasticsearch**

1. [Take a manual snapshot](managedomains-snapshots.md) of your domain\. This snapshot serves as a backup that you can [restore on a new domain](managedomains-snapshots.md#managedomains-snapshot-restore) if you want to return to using the prior OpenSearch version\.

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com) and choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Amazon OpenSearch Service**\.

1. In the navigation pane, under **Domains**, choose the domain that you want to upgrade\.

1. Choose **Actions** and **Upgrade**\.

1. Select the version to upgrade to\. If you're upgrading to an OpenSearch version, the **Enable compatibility mode** option appears\. If you enable this setting, OpenSearch reports its version as 7\.10 to allow Elasticsearch OSS clients and plugins like Logstash to continue working with Amazon OpenSearch Service\. You can disable this setting later

1. Choose **Upgrade**\.

1. Check the **Status** on the domain dashboard to monitor the status of the upgrade\.

## Starting an upgrade \(CLI\)<a name="starting-upgrades-cli"></a>

You can use the following operations to identify the correct version of OpenSearch or Elasticsearch for your domain, start an in\-place upgrade, perform the pre\-upgrade check, and view progress:
+ `get-compatible-versions` \(`GetCompatibleVersions`\)
+ `upgrade-domain` \(`UpgradeDomain`\)
+ `get-upgrade-status` \(`GetUpgradeStatus`\)
+ `get-upgrade-history` \(`GetUpgradeHistory`\)

For more information, see the [AWS CLI command reference](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/opensearch/index.html) and [Amazon OpenSearch Service API Reference](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/Welcome.html)\.

## Starting an upgrade \(SDK\)<a name="starting-upgrades-sdk"></a>

This sample uses the [OpenSearchService](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/opensearch.html) low\-level Python client from the AWS SDK for Python \(Boto\) to check if a domain is eligible for upgrade to a specific version, upgrades it, and continuously checks the upgrade status\.

```
import boto3
from botocore.config import Config
import time

# Build the client using the default credential configuration.
# You can use the CLI and run 'aws configure' to set access key, secret
# key, and default Region.

DOMAIN_NAME = ''  # The name of the domain to upgrade
TARGET_VERSION = ''  # The version you want to upgrade the domain to. For example, OpenSearch_1.1

my_config = Config(
    # Optionally lets you specify a Region other than your default.
    region_name='us-east-1'
)
client = boto3.client('opensearch', config=my_config)


def check_versions():
    """Determine whether domain is eligible for upgrade"""
    response = client.get_compatible_versions(
        DomainName=DOMAIN_NAME
    )
    compatible_versions = response['CompatibleVersions']
    for i in range(len(compatible_versions)):
        if TARGET_VERSION in compatible_versions[i]["TargetVersions"]:
            print('Domain is eligible for upgrade to ' + TARGET_VERSION)
            upgrade_domain()
            print(response)
        else:
            print('Domain not eligible for upgrade to ' + TARGET_VERSION)


def upgrade_domain():
    """Upgrades the domain"""
    response = client.upgrade_domain(
        DomainName=DOMAIN_NAME,
        TargetVersion=TARGET_VERSION
    )
    print('Upgrading domain to ' + TARGET_VERSION + '...' + response)
    time.sleep(5)
    wait_for_upgrade()


def wait_for_upgrade():
    """Get the status of the upgrade"""
    response = client.get_upgrade_status(
        DomainName=DOMAIN_NAME
    )
    if (response['UpgradeStep']) == 'UPGRADE' and (response['StepStatus']) == 'SUCCEEDED':
        print('Domain successfully upgraded to ' + TARGET_VERSION)
    elif (response['StepStatus']) == 'FAILED':
        print('Upgrade failed. Please try again.')
    elif (response['StepStatus']) == 'SUCCEEDED_WITH_ISSUES':
        print('Upgrade succeeded with issues')
    elif (response['StepStatus']) == 'IN_PROGRESS':
        time.sleep(30)
        wait_for_upgrade()


def main():
    check_versions()


if __name__ == "__main__":
    main()
```

## Troubleshooting validation failures<a name="upgrade-validation"></a>

When you initiate an OpenSearch or Elasticsearch version upgrade, OpenSearch Service first performs a series of validation checks to ensure that your domain is eligible for an upgrade\. If any of these checks fail, you receive a notification containing the specific issues that you must fix before upgrading your domain\. For a list of potential issues and steps to resolve them, see [Troubleshooting validation errors](managedomains-configuration-changes.md#validation)\.

## Troubleshooting an upgrade<a name="upgrade-failures"></a>

In\-place upgrades require healthy domains\. Your domain might be ineligible for an upgrade or fail to upgrade for a wide variety of reasons\. The following table shows the most common issues\.


| Issue | Description | 
| --- | --- | 
| Too many shards per node | OpenSearch, as well as 7\.x versions of Elasticsearch, have a default setting of no more than 1,000 shards per node\. If a node in your current cluster exceeds this setting, OpenSearch Service doesn't allow you to upgrade\. See [Exceeded maximum shard limit](handling-errors.md#troubleshooting-shard-limit) for troubleshooting options\. | 
| Domain in processing | The domain is in the middle of a configuration change\. Check upgrade eligibility after the operation completes\. | 
| Red cluster status | One or more indexes in the cluster is red\. For troubleshooting steps, see [Red cluster status](handling-errors.md#handling-errors-red-cluster-status)\. | 
| High error rate | The cluster is returning a large number of 5xx errors when attempting to process requests\. This problem is usually the result of too many simultaneous read or write requests\. Consider reducing traffic to the cluster or scaling your domain\. | 
| Split brain | Split brain means that your cluster has more than one master node and has split into two clusters that never will rejoin on their own\. You can avoid split brain by using the recommended number of [dedicated master nodes](managedomains-dedicatedmasternodes.md)\. For help recovering from split brain, contact [AWS Support](https://console.aws.amazon.com/support/home)\. | 
| Master node not found | OpenSearch Service can't find the cluster's master node\. If your domain uses [multi\-AZ](managedomains-multiaz.md), an Availability Zone failure might have caused the cluster to lose quorum and be unable to elect a new [master node](managedomains-dedicatedmasternodes.md)\. If the issue does not self\-resolve, contact [AWS Support](https://console.aws.amazon.com/support/home)\. | 
| Too many pending tasks | The master node is under heavy load and has many pending tasks\. Consider reducing traffic to the cluster or scaling your domain\. | 
| Impaired storage volume | The disk volume of one or more nodes isn't functioning properly\. This issue often occurs alongside other issues, like a high error rate or too many pending tasks\. If it occurs in isolation and doesn't self\-resolve, contact [AWS Support](https://console.aws.amazon.com/support/home)\. | 
| KMS key issue | The KMS key that is used to encrypt the domain is either inaccessible or missing\. For more information, see [Monitoring domains that encrypt data at rest](encryption-at-rest.md#monitoring-ear)\. | 
| Snapshot in progress | The domain is currently taking a snapshot\. Check upgrade eligibility after the snapshot finishes\. Also check that you can list manual snapshot repositories, list snapshots within those repositories, and take manual snapshots\. If OpenSearch Service is unable to check whether a snapshot is in progress, upgrades can fail\. | 
| Snapshot timeout or failure | The pre\-upgrade snapshot took too long to complete or failed\. Check cluster health, and try again\. If the problem persists, contact [AWS Support](https://console.aws.amazon.com/support/home)\. | 
| Incompatible indexes | One or more indexes is incompatible with the target version\. This problem can occur if you migrated the indexes from an older version of OpenSearch or Elasticsearch\. Reindex the indexes and try again\. | 
| High disk usage | Disk usage for the cluster is above 90%\. Delete data or scale the domain, and try again\. | 
| High JVM usage | JVM memory pressure is above 75%\. Reduce traffic to the cluster or scale the domain, and try again\. | 
| OpenSearch Dashboards alias problem | \.kibana is already configured as an alias and maps to an incompatible index, likely one from an earlier version of OpenSearch Dashboards\. Reindex, and try again\. | 
| Red Dashboards status | OpenSearch Dashboards status is red\. Try using Dashboards when the upgrade completes\. If the red status persists, resolve it manually, and try again\. | 
| Cross\-cluster compatibility |  You can only upgrade if cross\-cluster compatibility is maintained between the source and destination domains after the upgrade\. During the upgrade process, any incompatible connections are identified\. To proceed, either upgrade the remote domain or delete the incompatible connections\. Note that if replication is active on the domain, you can't resume it once you delete the connection\.   | 
| Other OpenSearch Service service issue | Issues with OpenSearch Service itself might cause your domain to display as ineligible for an upgrade\. If none of the preceding conditions apply to your domain and the problem persists for more than a day, contact [AWS Support](https://console.aws.amazon.com/support/home)\. | 

## Using a snapshot to migrate data<a name="snapshot-based-migration"></a>

In\-place upgrades are the easier, faster, and more reliable way to upgrade a domain to a later OpenSearch or Elasticsearch version\. Snapshots are a good option if you need to migrate from a pre\-5\.1 version of Elasticsearch or want to migrate to an entirely new cluster\.

The following table shows how to use snapshots to migrate data to a domain that uses a different OpenSearch or Elasticsearch version\. For more information about taking and restoring snapshots, see [Creating index snapshots in Amazon OpenSearch Service](managedomains-snapshots.md)\.


| From version | To version | Migration process | 
| --- | --- | --- | 
| Elasticsearch 6\.x or 7\.x | OpenSearch 1\.x |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/version-migration.html)  | 
| Elasticsearch 6\.x | Elasticsearch 7\.x |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/version-migration.html)  | 
| Elasticsearch 6\.x | Elasticsearch 6\.8 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/version-migration.html)  | 
| Elasticsearch 5\.x | Elasticsearch 6\.x |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/version-migration.html)  | 
| Elasticsearch 5\.x | Elasticsearch 5\.6 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/version-migration.html)  | 
| Elasticsearch 2\.3 | Elasticsearch 6\.x |  Elasticsearch 2\.3 snapshots are not compatible with 6\.*x*\. To migrate your data directly from 2\.3 to 6\.*x*, you must manually recreate your indexes in the new domain\. Alternately, you can follow the 2\.3 to 5\.*x* steps in this table, perform `_reindex` operations in the new 5\.*x* domain to convert your 2\.3 indexes to 5\.*x* indexes, and then follow the 5\.*x* to 6\.*x* steps\.  | 
| Elasticsearch 2\.3 | Elasticsearch 5\.x |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/version-migration.html)  | 
| Elasticsearch 1\.5 | Elasticsearch 5\.x |  Elasticsearch 1\.5 snapshots are not compatible with 5\.*x*\. To migrate your data from 1\.5 to 5\.*x*, you must manually recreate your indexes in the new domain\.  1\.5 snapshots *are* compatible with 2\.3, but OpenSearch Service 2\.3 domains do not support the `_reindex` operation\. Because you cannot reindex them, indexes that originated in a 1\.5 domain still fail to restore from 2\.3 snapshots to 5\.*x* domains\.   | 
| Elasticsearch 1\.5 | Elasticsearch 2\.3 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/version-migration.html)  | 