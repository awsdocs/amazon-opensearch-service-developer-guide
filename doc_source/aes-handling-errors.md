# Amazon Elasticsearch Service Troubleshooting<a name="aes-handling-errors"></a>

This section describes how to identity and solve common Amazon Elasticsearch Service issues\. Consult the information in this section before contacting [AWS Support](https://aws.amazon.com/premiumsupport/)\.

## Can't Access Kibana<a name="aes-troubleshooting-kibana-configure-anonymous-access"></a>

The Kibana endpoint doesn't support signed requests\. If the access control policy for your domain only grants access to certain IAM users or roles and you haven't configured [Amazon Cognito Authentication for Kibana](es-cognito-auth.md), you might receive the following error when you attempt to access Kibana:

```
"User: anonymous is not authorized to perform: es:ESHttpGet"
```

If your Amazon ES domain uses VPC access, you might not receive this error\. Instead, the request might time out\. To learn more about correcting this issue and the various configuration options available to you, see [Controlling Access to Kibana](es-kibana.md#es-kibana-access), [About Access Policies on VPC Domains](es-vpc.md#es-vpc-security), and [Amazon Elasticsearch Service Access Control](es-ac.md)\.

## Can't Access VPC Domain<a name="aes-troubleshooting-vpc-domain"></a>

See [About Access Policies on VPC Domains](es-vpc.md#es-vpc-security) and [Testing VPC Domains](es-vpc.md#kibana-test)\.

## Red Cluster Status<a name="aes-handling-errors-red-cluster-status"></a>

A red cluster status means that at least one primary shard and its replicas are not allocated to a node\. Amazon ES stops taking automatic snapshots, even of healthy indices, while the red cluster status persists\.

The most common causes of a red cluster status are [failed cluster nodes](#aes-handling-errors-failed-cluster-nodes) and the Elasticsearch process crashing due to a continuous heavy processing load\.

**Note**  
Amazon ES stores up to 14 daily automated snapshots for 30 days, so if the red cluster status persists for more than 16 days, permanent data loss can occur\. If your Amazon ES domain enters a red cluster status, AWS Support might contact you to ask whether you want to address the problem yourself or you want the support team to assist\. You can [set a CloudWatch alarm](cloudwatch-alarms.md) to notify you when a red cluster status occurs\.

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
| JVMMemoryPressure |  Specifies the percentage of the Java heap used for all data nodes in a cluster\. View the **Maximum** statistic for this metric, and look for smaller and smaller drops in memory pressure as the Java garbage collector fails to reclaim sufficient memory\. This pattern likely is due to complex queries or large data fields\. At 75% memory usage, Elasticsearch triggers the Concurrent Mark Sweep \(CMS\) garbage collector, which runs alongside other processes to keep pauses and disruptions to a minimum\. If CMS fails to reclaim enough memory and usage remains above 75%, Elasticsearch triggers a different garbage collection algorithm that halts or slows other processes in order to free up sufficient memory to prevent an out of memory error\. At 95% memory usage, Elasticsearch kills processes that attempt to allocate memory\. It might kill a critical process and bring down one or more nodes in the cluster\. The `_nodes/stats/jvm` API offers a useful summary of JVM statistics, memory pool usage, and garbage collection information: <pre>GET elasticsearch_domain/_nodes/stats/jvm?pretty</pre>  |  Set memory circuit breakers for the JVM\. For more information, see [JVM OutOfMemoryError](#aes-handling-errors-jvm_out_of_memory_error)\. If the problem persists, delete unnecessary indices, reduce the number or complexity of requests to the domain, add instances, or use larger instance types\.  | 
| CPUUtilization | Specifies the percentage of CPU resources used for data nodes in a cluster\. View the Maximum statistic for this metric, and look for a continuous pattern of high usage\. | Add data nodes or increase the size of the instance types of existing data nodes\. For more information, see [Configuring Amazon ES Domains](es-createupdatedomains.md#es-createdomains-configure-cluster)\. | 
| Nodes | Specifies the number of nodes in a cluster\. View the Minimum statistic for this metric\. This value fluctuates when the service deploys a new fleet of instances for a cluster\. | Add data nodes\. For more information, see [Configuring Amazon ES Domains](es-createupdatedomains.md#es-createdomains-configure-cluster)\. | 

## Yellow Cluster Status<a name="aes-handling-errors-yellow-cluster-status"></a>

A yellow cluster status means that the primary shards for all indices are allocated to nodes in a cluster, but the replica shards for at least one index are not\. Single\-node clusters always initialize with a yellow cluster status because there is no other node to which Amazon ES can assign a replica\. To achieve green cluster status, increase your node count\. For more information, see [Sizing Amazon ES Domains](sizing-domains.md) and [Configuring Amazon ES Domains](es-createupdatedomains.md#es-createdomains-configure-cluster)\.

## ClusterBlockException<a name="aes-handling-errors-yellow-index-error"></a>

You might receive a `ClusterBlockException` error for the following reasons\.

### Lack of Available Storage Space<a name="aes-handling-errors-watermark"></a>

If no nodes have enough storage space to accommodate shard relocation, basic write operations like adding documents and creating indices can begin to fail\. [Calculating Storage Requirements](sizing-domains.md#aes-bp-storage) provides a summary of how Amazon ES uses disk space\.

To avoid issues, monitor the `FreeStorageSpace` metric in the Amazon ES console and [create CloudWatch alarms](cloudwatch-alarms.md) to trigger when `FreeStorageSpace` drops below a certain threshold\. `GET /_cat/allocation?v` also provides a useful summary of shard allocation and disk usage\. To resolve issues associated with a lack of storage space, scale your Amazon ES domain to use larger instance types, more instances, or more EBS\-based storage\. For instructions, see [Configuring Amazon ES Domains](es-createupdatedomains.md#es-createdomains-configure-cluster)\.

### Block Disks Due to Low Memory<a name="aes-handling-errors-block-disks"></a>

When the **JVMMemoryPressure** metric exceeds 92% for 30 minutes, Amazon ES triggers a protection mechanism and blocks all write operations to prevent the cluster from reaching red status\. When the protection is on, write operations fail with a `ClusterBlockException` error, new indices can't be created, and the `IndexCreateBlockException` error is thrown\.

When the **JVMMemoryPressure** metric returns to 88% or lower for five minutes, the protection is disabled, and write operations to the cluster are unblocked\.

## JVM OutOfMemoryError<a name="aes-handling-errors-jvm_out_of_memory_error"></a>

A JVM `OutOfMemoryError` typically means that one of the following JVM circuit breakers was reached\.


****  

| Circuit Breaker | Description | Cluster Setting Property | 
| --- | --- | --- | 
| Parent Breaker | Total percentage of JVM heap memory allowed for all circuit breakers\. The default value is 70%\. | indices\.breaker\.total\.limit | 
| Field Data Breaker | Percentage of JVM heap memory allowed to load a single data field into memory\. The default value is 60%\. If you upload data with large fields, we recommend raising this limit\. | indices\.breaker\.fielddata\.limit | 
| Request Breaker | Percentage of JVM heap memory allowed for data structures used to respond to a service request\. The default value is 40%\. If your service requests involve calculating aggregations, we recommend raising this limit\. | indices\.breaker\.request\.limit | 

## Failed Cluster Nodes<a name="aes-handling-errors-failed-cluster-nodes"></a>

Amazon EC2 instances might experience unexpected terminations and restarts\. Typically, Amazon ES restarts the nodes for you\. However, it's possible for one or more nodes in an Elasticsearch cluster to remain in a failed condition\.

To check for this condition, open your domain dashboard on the Amazon ES console\. Choose the **Monitoring** tab, and then choose the **Nodes** metric\. See if the reported number of nodes is fewer than the number that you configured for your cluster\. If the metric shows that one or more nodes is down for more than one day, contact [AWS Support](https://aws.amazon.com/premiumsupport/)\.

You can also [set a CloudWatch alarm](cloudwatch-alarms.md) to notify you when this issue occurs\.

**Note**  
The **Nodes** metric is not accurate during changes to your cluster configuration and during routine maintenance for the service\. This behavior is expected\. The metric will report the correct number of cluster nodes soon\. To learn more, see [About Configuration Changes](es-managedomains.md#es-managedomains-configuration-changes)\.

To protect your clusters from unexpected node terminations and restarts, create at least one replica for each index in your Amazon ES domain\.

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

## "Not Valid for the Object's Storage Class" Snapshot Error<a name="aes-troubleshooting-glacier-snapshots"></a>

Amazon ES snapshots do not support the Amazon Glacier storage class\. You might encounter this error when you attempt to list snapshots if your S3 bucket includes a lifecycle rule that transitions objects to the Amazon Glacier storage class\.

If you need to restore a snapshot from the bucket, restore the objects from Amazon Glacier, copy the objects to a new bucket, and [register the new bucket](es-managedomains-snapshots.md#es-managedomains-snapshot-registerdirectory) as a snapshot respository\.

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

To add VPC endpoints to your VPC, Amazon ES needs to assume the `AWSServiceRoleForAmazonElasticsearchService` role\. Thus, AWS STS must be enabled to create new domains that use VPC access in a given region\. To learn more about enabling and disabling AWS STS, see the [IAM User Guide](http://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_temp_enable-regions.html)\.

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