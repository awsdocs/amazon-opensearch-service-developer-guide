# Upgrading Elasticsearch<a name="es-version-migration"></a>

Amazon ES offers in\-place Elasticsearch upgrades for domains that run versions 5\.1 and later\. If you use services like Amazon Kinesis Data Firehose or Amazon CloudWatch Logs to stream data to Amazon ES, check that these services support the newer version of Elasticsearch before migrating\.

Currently, Amazon ES supports the following upgrade paths\.


| From Version | To Version | 
| --- | --- | 
| 6\.x | 6\.3 | 
| 5\.6 |  6\.3  Indices created in version 6\.*x* no longer support multiple mapping types\. Indices created in version 5\.*x* still support multiple mapping types when restored into a 6\.*x* cluster\. If you use AWS Lambda, check that your code creates only a single mapping type per index\. To minimize downtime during the upgrade from Elasticsearch 5\.6 to 6\.*x*, Amazon ES reindexes the `.kibana` index to `.kibana-6`, deletes `.kibana`, creates an alias named `.kibana`, and maps the new index to the new alias\.   | 
| 5\.x | 5\.6 | 

In essence, you can move to the latest release within the same major version \(for example, 5\.3 to 5\.6\) or from the latest release in a major version to the latest release in the *next* major version \(for example, 5\.6 to 6\.3\)\. As new Elasticsearch versions become available on Amazon ES, these upgrade paths change\.

The upgrade process consists of three steps:

1. **Pre\-upgrade checks** – Amazon ES performs a series of checks for issues that can block an upgrade and doesn't proceed to the next step unless these checks succeed\.

1. **Snapshot** – Amazon ES takes a snapshot of the Elasticsearch cluster and doesn't proceed to the next step unless the snapshot succeeds\. If the upgrade fails, Amazon ES uses this snapshot to restore the cluster to its original state\.

1. **Upgrade** – Amazon ES starts the upgrade, which can take from 15 minutes to several hours to complete\. Kibana might be unavailable during some or all of the upgrade\.

## Troubleshooting an Upgrade<a name="upgrade-failures"></a>

In\-place Elasticsearch upgrades require healthy domains\. Your domain might be ineligible for an upgrade or fail to upgrade for a wide variety of reasons\. The following table shows the most common issues\.


| Issue | Description | 
| --- | --- | 
| Domain in processing | The domain is in the middle of a configuration change\. Check upgrade eligibility after the operation completes\. | 
| Red cluster status | One or more indices in the cluster is red\. For troubleshooting steps, see [Red Cluster Status](aes-handling-errors.md#aes-handling-errors-red-cluster-status)\. | 
| High error rate | The Elasticsearch cluster is returning a large number of 5xx errors when attempting to process requests\. This problem is usually the result of too many simultaneous read or write requests\. Consider reducing traffic to the cluster or scaling your domain\. | 
| Split brain | Split brain means that your Elasticsearch cluster has more than one master node and has split into two clusters that never will rejoin on their own\. You can avoid split brain by using the recommended number of [dedicated master nodes](es-managedomains-dedicatedmasternodes.md)\. For help recovering from split brain, contact [AWS Support](https://console.aws.amazon.com/support/home)\. | 
| Master node not found | Amazon ES can't find the cluster's master node\. If your domain uses zone awareness, an Availability Zone failure might have caused the cluster to lose quorum and be unable to elect a new [master node](es-managedomains-dedicatedmasternodes.md)\. If the issue does not self\-resolve, contact [AWS Support](https://console.aws.amazon.com/support/home)\. | 
| Too many pending tasks | The master node is under heavy load and has many pending tasks\. Consider reducing traffic to the cluster or scaling your domain\. | 
| Impaired storage volume | The disk volume of one or more nodes isn't functioning properly\. This issue often occurs alongside other issues, like a high error rate or too many pending tasks\. If it occurs in isolation and doesn't self\-resolve, contact [AWS Support](https://console.aws.amazon.com/support/home)\. | 
| KMS key issue | The KMS key that is used to encrypt the domain is either inaccessible or missing\. For more information, see [Monitoring Domains That Encrypt Data at Rest](encryption-at-rest.md#monitoring-ear)\. | 
| Snapshot in progress | The domain is currently taking a snapshot\. Check upgrade eligibility after the snapshot finishes\. Also check that you can list manual snapshot repositories, list snapshots within those repositories, and take manual snapshots\. If Amazon ES is unable to check whether a snapshot is in progress, upgrades can fail\. | 
| Snapshot timeout or failure | The pre\-upgrade snapshot took too long to complete or failed\. Check cluster health, and try again\. If the problem persists, contact [AWS Support](https://console.aws.amazon.com/support/home)\. | 
| Incompatible indices | One or more indices is incompatible with the target Elasticsearch version\. This problem can occur if you migrated the indices from an older version of Elasticsearch, like 2\.3\. Reindex the indices, and try again\. | 
| High disk usage | Disk usage for the cluster is above 90%\. Delete data or scale the domain, and try again\. | 
| High JVM usage | JVM memory pressure is above 75%\. Reduce traffic to the cluster or scale the domain, and try again\. | 
| Kibana alias problem | \.kibana is already configured as an alias and maps to an incompatible index, likely one from an earlier version of Kibana\. Reindex, and try again\. | 
| Red Kibana status | Kibana status is red\. Try using Kibana when the upgrade completes\. If the red status persists, resolve it manually, and try again\. | 
| Other Amazon ES service issue | Issues with Amazon ES itself might cause your domain to display as ineligible for an upgrade\. If none of the preceding conditions apply to your domain and the problem persists for more than a day, contact [AWS Support](https://console.aws.amazon.com/support/home)\. | 

## Starting an Upgrade<a name="starting-upgrades"></a>

The upgrade process is irreversible and can't be paused nor canceled\. During an upgrade, you can't make configuration changes to the domain\. Before starting an upgrade, double\-check that you want to proceed\. You can use these same steps to perform the pre\-upgrade check without actually starting an upgrade\.

**To upgrade a domain to a later version of Elasticsearch \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose the domain that you want to upgrade\.

1. Choose **Upgrade domain**\.

1. For **Operation**, choose **Upgrade**, **Submit**, and **Continue**\.

1. Return to the **Overview** tab and choose **Upgrade status** to monitor the state of the upgrade\.

**To upgrade a domain to a later version of Elasticsearch \(AWS CLI and SDK\)**

You can use the following operations to identify the right Elasticsearch version for your domain, start an in\-place upgrade, perform the pre\-upgrade check, and view progress:
+ `get-compatible-elasticsearch-versions` \(`GetCompatibleElasticsearchVersions`\)
+ `upgrade-elasticsearch-domain` \(`UpgradeElasticsearchDomain`\)
+ `get-upgrade-status` \(`GetUpgradeStatus`\)
+ `get-upgrade-history` \(`GetUpgradeHistory`\)

For more information, see the [AWS CLI Command Reference](http://docs.aws.amazon.com/cli/latest/reference/) and [Amazon Elasticsearch Service Configuration API Reference](es-configuration-api.md)\.

## Using a Snapshot to Migrate Data<a name="snapshot-based-migration"></a>

In\-place upgrades are the easier, faster, and more reliable way to upgrade a domain to a later Elasticsearch version\. Snapshots are a good option if you need to migrate from a pre\-5\.1 version of Elasticsearch or want to migrate to an entirely new cluster\.

The following table shows how to use snapshots to migrate data to a domain that uses a different Elasticsearch version\. Most of the steps require you to create and restore manual index snapshots\. For more information about this process, see [Working with Amazon Elasticsearch Service Index Snapshots](es-managedomains-snapshots.md)\.


****  

| From Version | To Version | Migration Process | 
| --- | --- | --- | 
| 6\.x | 6\.3 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-version-migration.html)  | 
| 5\.x | 6\.x |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-version-migration.html)  | 
| 5\.x | 5\.6 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-version-migration.html)  | 
| 2\.3 | 6\.x |  Elasticsearch 2\.3 snapshots are not compatible with 6\.*x*\. To migrate your data directly from 2\.3 to 6\.*x*, you must manually recreate your indices in the new domain\. Alternately, you can follow the 2\.3 to 5\.*x* steps in this table, perform `_reindex` operations in the new 5\.*x* domain to convert your 2\.3 indices to 5\.*x* indices, and then follow the 5\.*x* to 6\.*x* steps\.  | 
| 2\.3 | 5\.x |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-version-migration.html)  | 
| 1\.5 | 5\.x |  Elasticsearch 1\.5 snapshots are not compatible with 5\.*x*\. To migrate your data from 1\.5 to 5\.*x*, you must manually recreate your indices in the new domain\.  1\.5 snapshots *are* compatible with 2\.3, but Amazon ES 2\.3 domains do not support the `_reindex` operation\. Because you cannot reindex them, indices that originated in a 1\.5 domain still fail to restore from 2\.3 snapshots to 5\.*x* domains\.   | 
| 1\.5 | 2\.3 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-version-migration.html)  | 