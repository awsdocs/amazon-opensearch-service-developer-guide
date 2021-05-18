# Troubleshooting Amazon Elasticsearch Service<a name="aes-handling-errors"></a>

This topic describes how to identify and solve common Amazon Elasticsearch Service \(Amazon ES\) issues\. Consult the information in this section before contacting [AWS Support](https://aws.amazon.com/premiumsupport/)\.

## Can't access Kibana<a name="aes-troubleshooting-kibana-configure-anonymous-access"></a>

The Kibana endpoint doesn't support signed requests\. If the access control policy for your domain only grants access to certain IAM users or roles and you haven't configured [Amazon Cognito authentication](es-cognito-auth.md), you might receive the following error when you attempt to access Kibana:

```
"User: anonymous is not authorized to perform: es:ESHttpGet"
```

If your Amazon ES domain uses VPC access, you might not receive this error, but the request might time out\. To learn more about correcting this issue and the various configuration options available to you, see [Controlling access to Kibana](es-kibana.md#es-kibana-access), [About access policies on VPC domains](es-vpc.md#es-vpc-security), and [Identity and Access Management in Amazon Elasticsearch Service](es-ac.md)\.

## Can't access VPC domain<a name="aes-troubleshooting-vpc-domain"></a>

See [About access policies on VPC domains](es-vpc.md#es-vpc-security) and [Testing VPC domains](es-vpc.md#kibana-test)\.

## Cluster in read\-only state<a name="aes-troubleshooting-7x"></a>

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

If cluster health is red, we recommend restoring the cluster from a snapshot\. You can also see [Red cluster status](#aes-handling-errors-red-cluster-status) for troubleshooting steps\. If cluster health is green, check that all expected indices are present using the following request:

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

## Red cluster status<a name="aes-handling-errors-red-cluster-status"></a>

A red cluster status means that at least one primary shard and its replicas are not allocated to a node\. Amazon ES keeps trying to take automated snapshots of all indices regardless of their status, but the snapshots fail while the red cluster status persists\.

The most common causes of a red cluster status are [failed cluster nodes](#aes-handling-errors-failed-cluster-nodes) and the Elasticsearch process crashing due to a continuous heavy processing load\.

**Note**  
Amazon ES stores automated snapshots for 14 days regardless of the cluster status\. Therefore, if the red cluster status persists for more than two weeks, the last healthy automated snapshot will be deleted and you could permanently lose your cluster's data\. If your Amazon ES domain enters a red cluster status, AWS Support might contact you to ask whether you want to address the problem yourself or you want the support team to assist\. You can [set a CloudWatch alarm](cloudwatch-alarms.md) to notify you when a red cluster status occurs\.

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

### Recovering from a continuous heavy processing load<a name="aes-handling-errors-red-cluster-status-heavy-processing-load"></a>

To determine if a red cluster status is due to a continuous heavy processing load on a data node, monitor the following cluster metrics\.


****  

| Relevant Metric | Description | Recovery | 
| --- | --- | --- | 
| JVMMemoryPressure |  Specifies the percentage of the Java heap used for all data nodes in a cluster\. View the **Maximum** statistic for this metric, and look for smaller and smaller drops in memory pressure as the Java garbage collector fails to reclaim sufficient memory\. This pattern likely is due to complex queries or large data fields\. The Concurrent Mark Sweep \(CMS\) garbage collector triggers when 75% of the “old generation” object space is full\. This collector runs alongside other threads to keep pauses to a minimum\. If CMS is unable to reclaim enough memory during these normal collections, Elasticsearch triggers a different garbage collection algorithm that halts all threads\. Nodes are unresponsive during these stop\-the\-world collections, which can affect cluster stability\. If memory usage continues to grow, Elasticsearch eventually crashes due to an out of memory error\. A good rule of thumb is to keep usage below 80%\. The `_nodes/stats/jvm` API offers a useful summary of JVM statistics, memory pool usage, and garbage collection information: <pre>GET elasticsearch_domain/_nodes/stats/jvm?pretty</pre>  |  Set memory circuit breakers for the JVM\. For more information, see [JVM OutOfMemoryError](#aes-handling-errors-jvm_out_of_memory_error)\. If the problem persists, delete unnecessary indices, reduce the number or complexity of requests to the domain, add instances, or use larger instance types\.  | 
| CPUUtilization | Specifies the percentage of CPU resources used for data nodes in a cluster\. View the Maximum statistic for this metric, and look for a continuous pattern of high usage\. | Add data nodes or increase the size of the instance types of existing data nodes\. | 
| Nodes | Specifies the number of nodes in a cluster\. View the Minimum statistic for this metric\. This value fluctuates when the service deploys a new fleet of instances for a cluster\. | Add data nodes\. | 

## Yellow cluster status<a name="aes-handling-errors-yellow-cluster-status"></a>

A yellow cluster status means that the primary shards for all indices are allocated to nodes in a cluster, but the replica shards for at least one index are not\. Single\-node clusters always initialize with a yellow cluster status because there is no other node to which Amazon ES can assign a replica\. To achieve green cluster status, increase your node count\. For more information, see [Sizing Amazon Elasticsearch Service domains](sizing-domains.md)\.

Multi\-node clusters might briefly have a yellow cluster status after creating a new index or after a node failure\. This status self\-resolves as Elasticsearch replicates data across the cluster\. [Lack of disk space](#aes-handling-errors-watermark) can also cause yellow cluster status; the cluster can only distribute replica shards if nodes have the disk space to accommodate them\.

## ClusterBlockException<a name="troubleshooting-cluster-block"></a>

You might receive a `ClusterBlockException` error for the following reasons\.

### Lack of available storage space<a name="aes-handling-errors-watermark"></a>

If no nodes have enough storage space to accommodate shard relocation, basic write operations like adding documents and creating indices can begin to fail\. [Calculating storage requirements](sizing-domains.md#aes-bp-storage) provides a summary of how Amazon ES uses disk space\.

To avoid issues, monitor the `FreeStorageSpace` metric in the Amazon ES console and [create CloudWatch alarms](cloudwatch-alarms.md) to trigger when `FreeStorageSpace` drops below a certain threshold\. `GET /_cat/allocation?v` also provides a useful summary of shard allocation and disk usage\. To resolve issues associated with a lack of storage space, scale your Amazon ES domain to use larger instance types, more instances, or more EBS\-based storage\.

### Blocked disks due to low memory<a name="aes-handling-errors-block-disks"></a>

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

## Failed cluster nodes<a name="aes-handling-errors-failed-cluster-nodes"></a>

Amazon EC2 instances might experience unexpected terminations and restarts\. Typically, Amazon ES restarts the nodes for you\. However, it's possible for one or more nodes in an Elasticsearch cluster to remain in a failed condition\.

To check for this condition, open your domain dashboard on the Amazon ES console\. Choose the **Cluster health** tab, and then choose the **Nodes** metric\. See if the reported number of nodes is fewer than the number that you configured for your cluster\. If the metric shows that one or more nodes is down for more than one day, contact [AWS Support](https://aws.amazon.com/premiumsupport/)\.

You can also [set a CloudWatch alarm](cloudwatch-alarms.md) to notify you when this issue occurs\.

**Note**  
The **Nodes** metric is not accurate during changes to your cluster configuration and during routine maintenance for the service\. This behavior is expected\. The metric will report the correct number of cluster nodes soon\. To learn more, see [Making configuration changes in Amazon ES](es-managedomains-configuration-changes.md)\.

To protect your clusters from unexpected node terminations and restarts, create at least one replica for each index in your Amazon ES domain\.

## Exceeded maximum shard limit<a name="aes-troubleshooting-shard-limit"></a>

The 7\.*x* versions of Elasticsearch have a default setting of no more than 1,000 shards per node\. Elasticsearch throws an error if a request, such as creating a new index, would cause you to exceed this limit\. If you encounter this error, you have several options:
+ Add more data nodes to the cluster\.
+ Increase the `_cluster/settings/cluster.max_shards_per_node` setting\.
+ Use the [\_shrink API](aes-supported-es-operations.md#es_version_api_notes-shrink) to reduce the number of shards on the node\.

## Can't enable audit logs<a name="aes-troubleshooting-audit-logs-error"></a>

You might encounter the following error when you try to enable audit log publishing using the Amazon ES console:

The Resource Access Policy specified for the CloudWatch Logs log group does not grant sufficient permissions for Amazon Elasticsearch Service to create a log stream\. Please check the Resource Access Policy\.

If you encounter this error, verify that the `resource` element of your policy includes the correct log group ARN\. If it does, take the following steps:

1. Wait several minutes\.

1. Refresh the page in your web browser\.

1. Choose **Use existing log group**\.

1. Under **Existing log group**, choose the log group that you created before receiving the error message\.

1. Choose **Select an existing policy**\.

1. Under **Existing policy**, choose the policy that you created before receiving the error message\.

1. Choose **Enable**\.

If the error persists after repeating steps 1–7 several times, contact [AWS Support](https://aws.amazon.com/premiumsupport/)\.

## Can't close index<a name="aes-troubleshooting-close-api"></a>

Amazon ES supports the `_close` API only for Elasticsearch versions 7\.4 or later\. If you're using an older version and are restoring an index from a snapshot, you can delete the existing index \(before or after reindexing it\)\. The other option is to use the `rename_pattern` and `rename_replacement` fields to rename the index as you restore it:

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

## Mapper parsing exception while indexing<a name="aes-troubleshooting-dynamic-template"></a>

Elasticsearch 7\.10 deprecated several parameters \(`ignore_malformed`, `coerce`, and others\) for use within dynamic templates\. If you add a document to an index with a dynamic template that contains a deprecated parameter, Elasticsearch throws an exception:

```
"error" : {
    "root_cause" : [
      {
        "type" : "mapper_parsing_exception",
        "reason" : "unknown parameter [ignore_malformed] on mapper [mykeyword] of type [text]"
      }
    ]
  }
```

If you encounter this error, remove the deprecated parameter from your template and retry the request\. 

## Client license checks<a name="aes-troubleshooting-license"></a>

The default distributions of Logstash and Beats include a proprietary license check and fail to connect to the open source version of Elasticsearch\. Make sure you use the Apache 2\.0 \(OSS\) distributions of these clients with Amazon ES\.

## Request throttling<a name="aes-troubleshooting-throttle-api"></a>

If you receive persistent `403 Request throttled due to too many requests` or `429 Too Many Requests` errors, consider scaling vertically\. Amazon Elasticsearch Service throttles requests if the payload would cause memory usage to exceed the maximum size of the Java heap\.

## Can't SSH into node<a name="aes-troubleshooting-ssh"></a>

You can't use SSH to access any of the nodes in your Elasticsearch cluster, and you can't directly modify `elasticsearch.yml`\. Instead, use the console, AWS CLI, or SDKs to configure your domain\. You can specify a few cluster\-level settings using the Elasticsearch REST APIs, as well\. To learn more, see [Configuration API reference for Amazon Elasticsearch Service](es-configuration-api.md) and [Supported Elasticsearch operations](aes-supported-es-operations.md)\.

If you need more insight into the performance of the cluster, you can [publish error logs and slow logs to CloudWatch](es-createdomain-configure-slow-logs.md)\.

## "Not Valid for the Object's Storage Class" snapshot error<a name="aes-troubleshooting-glacier-snapshots"></a>

Amazon ES snapshots do not support the S3 Glacier storage class\. You might encounter this error when you attempt to list snapshots if your S3 bucket includes a lifecycle rule that transitions objects to the S3 Glacier storage class\.

If you need to restore a snapshot from the bucket, restore the objects from S3 Glacier, copy the objects to a new bucket, and [register the new bucket](es-managedomains-snapshots.md#es-managedomains-snapshot-registerdirectory) as a snapshot repository\.

## Invalid host header<a name="aes-troubleshooting-host-header"></a>

Amazon ES requires that clients specify `Host` in the request headers\. A valid `Host` value is the domain endpoint without `https://`, such as:

```
Host: search-my-sample-domain-ih2lhn2ew2scurji.us-west-2.es.amazonaws.com
```

If you receive an `Invalid Host Header` error when making a request, check that your client or proxy includes the Amazon ES domain endpoint \(and not, for example, its IP address\) in the `Host` header\.

## Invalid M3 instance type<a name="aes-m3-instance-types"></a>

Amazon ES does not support adding or modifying M3 instances to existing domains running Elasticsearch versions 6\.7 or later\. You can continue to use M3 instances with Elasticsearch 6\.5 and earlier\.

We recommend choosing a newer instance type\. For domains running Elasticsearch 6\.7 and later, the following restriction apply:
+ If your existing domain does not use M3 instances, you can no longer change to them\.
+ If you change an existing domain from an M3 instance type to another instance type, you can't switch back\.

## Hot queries stop working after enabling UltraWarm<a name="aes-troubleshooting-uw"></a>

When you enable UltraWarm on a domain, if there are no preexisting overrides to the `search.max_buckets` setting, Amazon ES automatically sets the value to `10000` to prevent memory\-heavy queries from saturating warm nodes\. If your hot queries are using more than 10,000 buckets, they might stop working when you enable UltraWarm\. 

Because you can't modify this setting due to the managed nature of Amazon Elasticsearch Service, you need to open a support case to increase the limit\. Limit increases don't require a premium support subscription\.

## Can't downgrade after upgrade<a name="aes-troubleshooting-upgrade-snapshot"></a>

[In\-place upgrades](es-version-migration.md) are irreversible, but if you contact [AWS Support](https://aws.amazon.com/premiumsupport/), they can help you restore the automatic, pre\-upgrade snapshot on a new domain\. For example, if you upgrade a domain from Elasticsearch 5\.6 to 6\.4, AWS Support can help you restore the pre\-upgrade snapshot on a new Elasticsearch 5\.6 domain\. If you took a manual snapshot of the original domain, you can [perform that step yourself](es-managedomains-snapshots.md)\.

## Need summary of domains for all regions<a name="aes-troubleshooting-domain-summary"></a>

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

## Browser error when using Kibana<a name="aes-troubleshooting-kibana-debug-browser-errors"></a>

Your browser wraps service error messages in HTTP response objects when you use Kibana to view data in your Amazon ES domain\. You can use developer tools commonly available in web browsers, such as Developer Mode in Chrome, to view the underlying service errors and assist your debugging efforts\.

**To view service errors in Chrome**

1. From the menu, choose **View**, **Developer**, **Developer Tools**\.

1. Choose the **Network** tab\.

1. In the **Status** column, choose any HTTP session with a status of 500\.

**To view service errors in Firefox**

1. From the menu, choose **Tools**, **Web Developer**, **Network**\.

1. Choose any HTTP session with a status of 500\.

1. Choose the **Response** tab to view the service response\.

## Unauthorized operation after selecting VPC access<a name="es-vpc-permissions"></a>

When you create a new domain using the Amazon ES console, you have the option to select VPC or public access\. If you select **VPC access**, Amazon ES queries for VPC information and fails if you don't have the proper permissions:

```
You are not authorized to perform this operation. (Service: AmazonEC2; Status Code: 403; Error Code: UnauthorizedOperation
```

To enable this query, you must have access to the `ec2:DescribeVpcs`, `ec2:DescribeSubnets`, and `ec2:DescribeSecurityGroups` operations\. This requirement is only for the console\. If you use the AWS CLI to create and configure a domain with a VPC endpoint, you don't need access to those operations\.

## Stuck at loading after creating VPC domain<a name="es-vpc-sts"></a>

After creating a new domain that uses VPC access, the domain's **Configuration state** might never progress beyond **Loading**\. If this issue occurs, you likely have AWS Security Token Service \(AWS STS\) *disabled* for your region\.

To add VPC endpoints to your VPC, Amazon ES needs to assume the `AWSServiceRoleForAmazonElasticsearchService` role\. Thus, AWS STS must be enabled to create new domains that use VPC access in a given region\. To learn more about enabling and disabling AWS STS, see the [IAM User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_temp_enable-regions.html)\.

## Can't connect from Alpine Linux<a name="aes-troubleshooting-alpine"></a>

Alpine Linux limits DNS response size to 512 bytes\. If you try to connect to your Amazon ES domain from Alpine Linux, DNS resolution can fail if the domain is in a VPC and has more than 20 nodes\. If your domain is in a VPC, we recommend using other Linux distributions, such as Debian, Ubuntu, CentOS, Red Hat Enterprise Linux, or Amazon Linux 2, to connect to it\.

## Certificate error when using SDK<a name="aes-troubleshooting-certificates"></a>

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