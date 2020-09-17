# Amazon Elasticsearch Service Troubleshooting<a name="aes-handling-errors"></a>

This section describes how to identify and solve common Amazon Elasticsearch Service issues\. Consult the information in this section before contacting [AWS Support](https://aws.amazon.com/premiumsupport/)\.

## Can't Access Kibana<a name="aes-troubleshooting-kibana-configure-anonymous-access"></a>

The Kibana endpoint doesn't support signed requests\. If the access control policy for your domain only grants access to certain IAM users or roles and you haven't configured [Amazon Cognito Authentication for Kibana](es-cognito-auth.md), you might receive the following error when you attempt to access Kibana:

```
"User: anonymous is not authorized to perform: es:ESHttpGet"
```

If your Amazon ES domain uses VPC access, you might not receive this error\. Instead, the request might time out\. To learn more about correcting this issue and the various configuration options available to you, see [Controlling Access to Kibana](es-kibana.md#es-kibana-access), [About Access Policies on VPC Domains](es-vpc.md#es-vpc-security), and [Identity and Access Management in Amazon Elasticsearch Service](es-ac.md)\.

## Can't Access VPC Domain<a name="aes-troubleshooting-vpc-domain"></a>

See [About Access Policies on VPC Domains](es-vpc.md#es-vpc-security) and [Testing VPC Domains](es-vpc.md#kibana-test)\.

## Cluster in Read\-Only State<a name="aes-troubleshooting-7x"></a>

Compared to earlier versions, Elasticsearch 7\.*x* uses a different system for cluster coordination\. In this new system, when the cluster loses quorum, the cluster is unavailable until you take action\. Loss of quorum can take two forms:
+ If your cluster uses dedicated master nodes, quorum loss occurs when half or more are unavailable\.
+ If your cluster does not use dedicated master nodes, quorum loss occurs when half or more of your data nodes are unavailable\.

If quorum loss occurs and your cluster has more than one node, Amazon ES restores quorum and places the cluster into a read\-only state\. You have two options:
+ Remove the read\-only state and use the cluster as\-is\.
+ [Restore the cluster or individual indices from a snapshot](es-managedomains-snapshots.md#es-managedomains-snapshot-restore)\.

If you prefer to use the cluster as\-is, verify that cluster health is green using the following request:

```
GET _cat/health?v
```

If cluster health is red, we recommend restoring the cluster from a snapshot\. You can also see [Red Cluster Status](#aes-handling-errors-red-cluster-status) for troubleshooting steps\. If cluster health is green, check that all expected indices are present using the following request:

```
GET _cat/indices?v
```

Then run some searches to verify that the expected data is present\. If it is, you can remove the read\-only state using the following request:

```
PUT _cluster/settings
{
  "persistent": {
    "cluster.blocks.read_only": false
  }
}
```

If quorum loss occurs and your cluster has only one node, Amazon ES replaces the node and does *not* place the cluster into a read\-only state\. Otherwise, your options are the same: use the cluster as\-is or restore from a snapshot\.

In both situations, Amazon ES sends two events to your [Personal Health Dashboard](https://phd.aws.amazon.com/phd/home#/)\. The first informs you of the loss of quorum\. The second occurs after Amazon ES successfully restores quorum\. For more information about using the Personal Health Dashboard, see the [AWS Health User Guide](https://docs.aws.amazon.com/health/latest/ug/)\.

## Red Cluster Status<a name="aes-handling-errors-red-cluster-status"></a>

A red cluster status means that at least one primary shard and its replicas are not allocated to a node\. Amazon ES stops taking automatic snapshots, even of healthy indices, while the red cluster status persists\.

The most common causes of a red cluster status are [failed cluster nodes](#aes-handling-errors-failed-cluster-nodes) and the Elasticsearch process crashing due to a continuous heavy processing load\.

**Note**  
Amazon ES stores automatic snapshots for 14 days, so if the red cluster status persists for more than two weeks, you can permanently lose your cluster's data\. If your Amazon ES domain enters a red cluster status, AWS Support might contact you to ask whether you want to address the problem yourself or you want the support team to assist\. You can [set a CloudWatch alarm](cloudwatch-alarms.md) to notify you when a red cluster status occurs\.

Ultimately, red shards cause red clusters, and red indices cause red shards\. To identity the indices causing the red cluster status, Elasticsearch has some helpful APIs\.
+ `GET /_cluster/allocation/explain` chooses the first unassigned shard that it finds and explains why it cannot be allocated to a node:

  ```
  {
      "index": "test4",
      "shard": 0,
      "primary": true,
      "current_state": "unassigned",
      "can_allocate": "no",
      "allocate_explanation": "cannot allocate because allocation is not permitted to any of the nodes"
  }
  ```
+ `GET /_cat/indices?v` shows the health status, number of documents, and disk usage for each index:

  ```
  health status index            uuid                   pri rep docs.count docs.deleted store.size pri.store.size
  green  open   test1            30h1EiMvS5uAFr2t5CEVoQ   5   0        820            0       14mb           14mb
  green  open   test2            sdIxs_WDT56afFGu5KPbFQ   1   0          0            0       233b           233b
  green  open   test3            GGRZp_TBRZuSaZpAGk2pmw   1   1          2            0     14.7kb          7.3kb
  red    open   test4            BJxfAErbTtu5HBjIXJV_7A   1   0
  green  open   test5            _8C6MIXOSxCqVYicH3jsEA   1   0          7            0     24.3kb         24.3kb
  ```

Deleting red indices is the fastest way to fix a red cluster status\. Depending on the reason for the red cluster status, you might then scale your Amazon ES domain to use larger instance types, more instances, or more EBS\-based storage and try to recreate the problematic indices\.

If deleting a problematic index isn't feasible, you can [restore a snapshot](es-managedomains-snapshots.md#es-managedomains-snapshot-restore), delete documents from the index, change the index settings, reduce the number of replicas, or delete other indices to free up disk space\. The important step is to resolve the red cluster status *before* reconfiguring your Amazon ES domain\. Reconfiguring a domain with a red cluster status can compound the problem and lead to the domain being stuck in a configuration state of **Processing** until you resolve the status\.

### Recovering from a Continuous Heavy Processing Load<a name="aes-handling-errors-red-cluster-status-heavy-processing-load"></a>

To determine if a red cluster status is due to a continuous heavy processing load on a data node, monitor the following cluster metrics\.


****  

| Relevant Metric | Description | Recovery | 
| --- | --- | --- | 
| JVMMemoryPressure |  Specifies the percentage of the Java heap used for all data nodes in a cluster\. View the **Maximum** statistic for this metric, and look for smaller and smaller drops in memory pressure as the Java garbage collector fails to reclaim sufficient memory\. This pattern likely is due to complex queries or large data fields\. The Concurrent Mark Sweep \(CMS\) garbage collector triggers when 75% of the “old generation” object space is full\. This collector runs alongside other threads to keep pauses to a minimum\. If CMS is unable to reclaim enough memory during these normal collections, Elasticsearch triggers a different garbage collection algorithm that halts all threads\. Nodes are unresponsive during these stop\-the\-world collections, which can affect cluster stability\. If memory usage continues to grow, Elasticsearch eventually crashes due to an out of memory error\. A good rule of thumb is to keep usage below 80%\. The `_nodes/stats/jvm` API offers a useful summary of JVM statistics, memory pool usage, and garbage collection information: <pre>GET elasticsearch_domain/_nodes/stats/jvm?pretty</pre>  |  Set memory circuit breakers for the JVM\. For more information, see [JVM OutOfMemoryError](#aes-handling-errors-jvm_out_of_memory_error)\. If the problem persists, delete unnecessary indices, reduce the number or complexity of requests to the domain, add instances, or use larger instance types\.  | 
| CPUUtilization | Specifies the percentage of CPU resources used for data nodes in a cluster\. View the Maximum statistic for this metric, and look for a continuous pattern of high usage\. | Add data nodes or increase the size of the instance types of existing data nodes\. | 
| Nodes | Specifies the number of nodes in a cluster\. View the Minimum statistic for this metric\. This value fluctuates when the service deploys a new fleet of instances for a cluster\. | Add data nodes\. | 

## Yellow Cluster Status<a name="aes-handling-errors-yellow-cluster-status"></a>

A yellow cluster status means that the primary shards for all indices are allocated to nodes in a cluster, but the replica shards for at least one index are not\. Single\-node clusters always initialize with a yellow cluster status because there is no other node to which Amazon ES can assign a replica\. To achieve green cluster status, increase your node count\. For more information, see [Sizing Amazon ES Domains](sizing-domains.md)\.

## ClusterBlockException<a name="troubleshooting-cluster-block"></a>

You might receive a `ClusterBlockException` error for the following reasons\.

### Lack of Available Storage Space<a name="aes-handling-errors-watermark"></a>

If no nodes have enough storage space to accommodate shard relocation, basic write operations like adding documents and creating indices can begin to fail\. [Calculating Storage Requirements](sizing-domains.md#aes-bp-storage) provides a summary of how Amazon ES uses disk space\.

To avoid issues, monitor the `FreeStorageSpace` metric in the Amazon ES console and [create CloudWatch alarms](cloudwatch-alarms.md) to trigger when `FreeStorageSpace` drops below a certain threshold\. `GET /_cat/allocation?v` also provides a useful summary of shard allocation and disk usage\. To resolve issues associated with a lack of storage space, scale your Amazon ES domain to use larger instance types, more instances, or more EBS\-based storage\.

### Block Disks Due to Low Memory<a name="aes-handling-errors-block-disks"></a>

When the **JVMMemoryPressure** metric exceeds 92% for 30 minutes, Amazon ES triggers a protection mechanism and blocks all write operations to prevent the cluster from reaching red status\. When the protection is on, write operations fail with a `ClusterBlockException` error, new indices can't be created, and the `IndexCreateBlockException` error is thrown\.

When the **JVMMemoryPressure** metric returns to 88% or lower for five minutes, the protection is disabled, and write operations to the cluster are unblocked\.

## JVM OutOfMemoryError<a name="aes-handling-errors-jvm_out_of_memory_error"></a>

A JVM `OutOfMemoryError` typically means that one of the following JVM circuit breakers was reached\.


****  

| Circuit Breaker | Description | Cluster Setting Property | 
| --- | --- | --- | 
| Parent Breaker | Total percentage of JVM heap memory allowed for all circuit breakers\. The default value is 95%\. | indices\.breaker\.total\.limit | 
| Field Data Breaker | Percentage of JVM heap memory allowed to load a single data field into memory\. The default value is 40%\. If you upload data with large fields, you might need to raise this limit\. | indices\.breaker\.fielddata\.limit | 
| Request Breaker | Percentage of JVM heap memory allowed for data structures used to respond to a service request\. The default value is 60%\. If your service requests involve calculating aggregations, you might need to raise this limit\. | indices\.breaker\.request\.limit | 

## Failed Cluster Nodes<a name="aes-handling-errors-failed-cluster-nodes"></a>

Amazon EC2 instances might experience unexpected terminations and restarts\. Typically, Amazon ES restarts the nodes for you\. However, it's possible for one or more nodes in an Elasticsearch cluster to remain in a failed condition\.

To check for this condition, open your domain dashboard on the Amazon ES console\. Choose the **Cluster health** tab, and then choose the **Nodes** metric\. See if the reported number of nodes is fewer than the number that you configured for your cluster\. If the metric shows that one or more nodes is down for more than one day, contact [AWS Support](https://aws.amazon.com/premiumsupport/)\.

You can also [set a CloudWatch alarm](cloudwatch-alarms.md) to notify you when this issue occurs\.

**Note**  
The **Nodes** metric is not accurate during changes to your cluster configuration and during routine maintenance for the service\. This behavior is expected\. The metric will report the correct number of cluster nodes soon\. To learn more, see [Configuration Changes](es-managedomains-configuration-changes.md)\.

To protect your clusters from unexpected node terminations and restarts, create at least one replica for each index in your Amazon ES domain\.

## Maximum Shard Limit<a name="aes-troubleshooting-shard-limit"></a>

The 7\.*x* versions of Elasticsearch have a default setting of no more than 1,000 shards per node\. Elasticsearch throws an error if a request, such as creating a new index, would cause you to exceed this limit\. If you encounter this error, you have several options:
+ Add more data nodes to the cluster\.
+ Increase the `_cluster/settings/cluster.max_shards_per_node` setting\.
+ Use the [\_shrink API](aes-supported-es-operations.md#es_version_api_notes-shrink) to reduce the number of shards on the node\.

## Can't Close Index<a name="aes-troubleshooting-close-api"></a>

Amazon ES doesn't support the `_close` API\. If you are restoring an index from a snapshot, you can delete the existing index \(before or after reindexing it\)\. The other option is to use the `rename_pattern` and `rename_replacement` fields to rename the index as you restore it:

```
POST /_snapshot/my-repository/my-snapshot/_restore
{
  "indices": "my-index-1,myindex-2",
  "include_global_state": true,
  "rename_pattern": "my-index-(\\d)",
  "rename_replacement": "restored-my-index-$1"
}
```

If you plan to reindex, shrink, or split an index, you likely want to stop writing to it before performing the operation\.

## Request Throttling<a name="aes-troubleshooting-throttle-api"></a>

If you receive persistent `403 Request throttled due to too many requests` errors, consider scaling vertically\. Amazon Elasticsearch Service throttles requests if the payload would cause memory usage to exceed the maximum size of the Java heap\.

## Can't SSH into Node<a name="aes-troubleshooting-ssh"></a>

You can't use SSH to access any of the nodes in your Elasticsearch cluster, and you can't directly modify `elasticsearch.yml`\. Instead, use the console, AWS CLI, or SDKs to configure your domain\. You can specify a few cluster\-level settings using the Elasticsearch REST APIs, as well\. To learn more, see [Amazon Elasticsearch Service Configuration API Reference](es-configuration-api.md) and [Supported Elasticsearch Operations](aes-supported-es-operations.md)\.

If you need more insight into the performance of the cluster, you can [publish error logs and slow logs to CloudWatch](es-createdomain-configure-slow-logs.md)\.

## "Not Valid for the Object's Storage Class" Snapshot Error<a name="aes-troubleshooting-glacier-snapshots"></a>

Amazon ES snapshots do not support the S3 Glacier storage class\. You might encounter this error when you attempt to list snapshots if your S3 bucket includes a lifecycle rule that transitions objects to the S3 Glacier storage class\.

If you need to restore a snapshot from the bucket, restore the objects from S3 Glacier, copy the objects to a new bucket, and [register the new bucket](es-managedomains-snapshots.md#es-managedomains-snapshot-registerdirectory) as a snapshot repository\.

## Invalid Host Header<a name="aes-troubleshooting-host-header"></a>

Amazon ES requires that clients specify `Host` in the request headers\. A valid `Host` value is the domain endpoint without `https://`, such as:

```
Host: search-my-sample-domain-ih2lhn2ew2scurji.us-west-2.es.amazonaws.com
```

If you receive an `Invalid Host Header` error when making a request, check that your client includes the Amazon ES domain endpoint \(and not, for example, its IP address\) in the `Host` header\.

## Invalid M3 Instance Type<a name="aes-m3-instance-types"></a>

Amazon ES does not support adding or modifying M3 instances to existing domains running Elasticsearch versions 6\.7 or later\. You can continue to use M3 instances with Elasticsearch 6\.5 and earlier\.

We recommend choosing a newer instance type\. For domains running Elasticsearch 6\.7 and later, the following restriction apply:
+ If your existing domain does not use M3 instances, you can no longer change to them\.
+ If you change an existing domain from an M3 instance type to another instance type, you can't switch back\.

## Can't Downgrade After Upgrade<a name="aes-troubleshooting-upgrade-snapshot"></a>

[In\-place upgrades](es-version-migration.md) are irreversible, but if you contact [AWS Support](https://aws.amazon.com/premiumsupport/), they can help you restore the automatic, pre\-upgrade snapshot on a new domain\. For example, if you upgrade a domain from Elasticsearch 5\.6 to 6\.4, AWS Support can help you restore the pre\-upgrade snapshot on a new Elasticsearch 5\.6 domain\. If you took a manual snapshot of the original domain, you can [perform that step yourself](es-managedomains-snapshots.md)\.

## Need Summary of Domains for All Regions<a name="aes-troubleshooting-domain-summary"></a>

The following script uses the Amazon EC2 [describe\-regions](https://docs.aws.amazon.com/cli/latest/reference/ec2/describe-regions.html) AWS CLI command to create a list of all regions in which Amazon ES could be available\. Then it calls [list\-domain\-names](https://docs.aws.amazon.com/cli/latest/reference/es/list-domain-names.html) for each region:

```
for region in `aws ec2 describe-regions --output text | cut -f4`
do
    echo "\nListing domains in region '$region':"
    aws es list-domain-names --region $region --query 'DomainNames'
done
```

You receive the following output for each region:

```
Listing domains in region:'us-west-2'...
[
  {
    "DomainName": "sample-domain"
  }
]
```

Regions in which Amazon ES is not available return "Could not connect to the endpoint URL\."

## Browser Error When Using Kibana<a name="aes-troubleshooting-kibana-debug-browser-errors"></a>

Your browser wraps service error messages in HTTP response objects when you use Kibana to view data in your Amazon ES domain\. You can use developer tools commonly available in web browsers, such as Developer Mode in Chrome, to view the underlying service errors and assist your debugging efforts\.

**To view service errors in Chrome**

1. From the menu, choose **View**, **Developer**, **Developer Tools**\.

1. Choose the **Network** tab\.

1. In the **Status** column, choose any HTTP session with a status of 500\.

**To view service errors in Firefox**

1. From the menu, choose **Tools**, **Web Developer**, **Network**\.

1. Choose any HTTP session with a status of 500\.

1. Choose the **Response** tab to view the service response\.

## Unauthorized Operation After Selecting VPC Access<a name="es-vpc-permissions"></a>

When you create a new domain using the Amazon ES console, you have the option to select VPC or public access\. If you select **VPC access**, Amazon ES queries for VPC information and fails if you don't have the proper permissions:

```
You are not authorized to perform this operation. (Service: AmazonEC2; Status Code: 403; Error Code: UnauthorizedOperation
```

To enable this query, you must have access to the `ec2:DescribeVpcs`, `ec2:DescribeSubnets`, and `ec2:DescribeSecurityGroups` operations\. This requirement is only for the console\. If you use the AWS CLI to create and configure a domain with a VPC endpoint, you don't need access to those operations\.

## Stuck at Loading After Creating VPC Domain<a name="es-vpc-sts"></a>

After creating a new domain that uses VPC access, the domain's **Configuration state** might never progress beyond **Loading**\. If this issue occurs, you likely have AWS Security Token Service \(AWS STS\) *disabled* for your region\.

To add VPC endpoints to your VPC, Amazon ES needs to assume the `AWSServiceRoleForAmazonElasticsearchService` role\. Thus, AWS STS must be enabled to create new domains that use VPC access in a given region\. To learn more about enabling and disabling AWS STS, see the [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_temp_enable-regions.html)\.

## Can't Connect from Alpine Linux<a name="aes-troubleshooting-alpine"></a>

Alpine Linux limits DNS response size to 512 bytes\. If you try to connect to your Amazon ES domain from Alpine Linux, DNS resolution can fail if the domain is in a VPC and has more than 20 nodes\. If your domain is in a VPC, we recommend using other Linux distributions, such as Debian, Ubuntu, CentOS, Red Hat Enterprise Linux, or Amazon Linux 2, to connect to it\.

## Certificate Error When Using SDK<a name="aes-troubleshooting-certificates"></a>

Because AWS SDKs use the CA certificates from your computer, changes to the certificates on the AWS servers can cause connection failures when you attempt to use an SDK\. Error messages vary, but typically contain the following text:

```
Failed to query Elasticsearch
...
SSL3_GET_SERVER_CERTIFICATE:certificate verify failed
```

You can prevent these failures by keeping your computer's CA certificates and operating system up\-to\-date\. If you encounter this issue in a corporate environment and do not manage your own computer, you might need to ask an administrator to assist with the update process\.

The following list shows minimum operating system and Java versions:
+ Microsoft Windows versions that have updates from January 2005 or later installed contain at least one of the required CAs in their trust list\.
+ Mac OS X 10\.4 with Java for Mac OS X 10\.4 Release 5 \(February 2007\), Mac OS X 10\.5 \(October 2007\), and later versions contain at least one of the required CAs in their trust list\.
+ Red Hat Enterprise Linux 5 \(March 2007\), 6, and 7 and CentOS 5, 6, and 7 all contain at least one of the required CAs in their default trusted CA list\.
+ Java 1\.4\.2\_12 \(May 2006\), 5 Update 2 \(March 2005\), and all later versions, including Java 6 \(December 2006\), 7, and 8, contain at least one of the required CAs in their default trusted CA list\.

The three certificate authorities are:
+ Amazon Root CA 1
+ Starfield Services Root Certificate Authority \- G2
+ Starfield Class 2 Certification Authority

Root certificates from the first two authorities are available from [Amazon Trust Services](https://www.amazontrust.com/repository/), but keeping your computer up\-to\-date is the more straightforward solution\. To learn more about ACM\-provided certificates, see [AWS Certificate Manager FAQs](https://aws.amazon.com/certificate-manager/faqs/#certificates)\.

**Note**  
Currently, Amazon ES domains in the us\-east\-1 region use certificates from a different authority\. We plan to update the region to use these new certificate authorities in the near future\.