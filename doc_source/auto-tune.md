# Auto\-Tune for Amazon OpenSearch Service<a name="auto-tune"></a>

Auto\-Tune in Amazon OpenSearch Service uses performance and usage metrics from your OpenSearch cluster to suggest memory\-related configuration changes, including queue and cache sizes and Java virtual machine \(JVM\) settings on your nodes\. These optional changes improve cluster speed and stability\. 

Some changes deploy immediately, while others are scheduled during your domain's off\-peak window\. You can revert to the default OpenSearch Service settings at any time\. As Auto\-Tune gathers and analyzes performance metrics for your domain, you can view its recommendations in the OpenSearch Service console on the **Notifications** page\.

Auto\-Tune is available in commercial AWS Regions on domains running any OpenSearch version, or Elasticsearch 6\.7 or later, with a [supported instance type](supported-instance-types.md)\.

**Topics**
+ [Types of changes](#auto-tune-types)
+ [Enabling or disabling Auto\-Tune](#auto-tune-enable)
+ [Scheduling Auto\-Tune enhancements](#auto-tune-schedule)

## Types of changes<a name="auto-tune-types"></a>

Auto\-Tune has two broad categories of changes:
+ Nondisruptive changes that it applies as the cluster runs\.
+ Changes that require a [blue/green deployment](managedomains-configuration-changes.md), which it applies during the domain's off\-peak window\.

Based on your domain's performance metrics, Auto\-Tune can suggest adjustments to the following settings:


| Change type | Category | Description | 
| --- | --- | --- | 
|  JVM heap size  |  Blue/green  |  By default, OpenSearch Service uses 50% of an instance's RAM for the JVM heap, up to a heap size of 32 GiB\.  Increasing this percentage gives OpenSearch more memory, but leaves less for the operating system and other processes\. Larger values can decrease the number of garbage collection pauses, but increase the length of those pauses\.  | 
|  JVM young generation settings  |  Blue/green  |  JVM "young generation" settings affect the frequency of minor garbage collections\. More frequent minor collections can decrease the number of major collections and pauses\.  | 
|  Queue size  |  Nondisruptive  |  By default, the search queue size is `1000` and the write queue size is `10000`\. Auto\-Tune automatically scales the search and write queues if additional heap is available to handle requests\.  | 
|  Cache size  |  Nondisruptive  |  The *field cache* monitors on\-heap data structures, so it's important to monitor the cache's use\. Auto\-Tune scales the field data cache size to avoid out of memory and circuit breaker issues\.  The *shard request cache* is managed at the node level and has a default maximum size of 1% of the heap\. Auto\-Tune scales the shard request cache size to accept more search and index requests than what the configured cluster can handle\.  | 
| Request size | Nondisruptive |  By default, when the aggregated size of in\-flight requests surpasses 10% of total JVM \(2% for `t2` instance types and 1% for `t3.small`\), OpenSearch throttles all new `_search` and `_bulk` requests until the existing requests complete\.  Auto\-Tune automatically tunes this threshold, typically between 5\-15%, based on the amount of JVM that is currently occupied on the system\. For example, if JVM memory pressure is high, Auto\-Tune might reduce the threshold to 5%, at which point you might see more rejections until the cluster stabilizes and the threshold increases\.  | 

## Enabling or disabling Auto\-Tune<a name="auto-tune-enable"></a>

OpenSearch Service enables Auto\-Tune by default on new domains\. To enable or disable Auto\-Tune on existing domains, we recommend using the console, which simplifies the process\. Enabling Auto\-Tune doesn't cause a blue/green deployment\.

You currently can't enable or disable Auto\-Tune using AWS CloudFormation\.

### Console<a name="auto-tune-enable-console"></a>

**To enable Auto\-Tune on an existing domain**

1. Open the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home )\.

1. In the navigation pane, under **Domains**, choose the domain name to open the cluster configuration\.

1. Choose **Turn on** if Auto\-Tune isn't already enabled\.

1. Optionally, select **Off\-peak window** to schedule optimizations that require a blue/green deployment during the domain's configured off\-peak window\. For more information, see [Scheduling Auto\-Tune enhancements](#auto-tune-schedule)\.

1. Choose **Save changes**\.

### CLI<a name="auto-tune-enable-cli"></a>

To enable Auto\-Tune using the AWS CLI, send an [UpdateDomainConfig](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_UpdateDomainConfig.html) request:

```
aws opensearch update-domain-config \
  --domain-name my-domain \
  --auto-tune-options DesiredState=ENABLED
```

## Scheduling Auto\-Tune enhancements<a name="auto-tune-schedule"></a>

Prior to February 16, 2023, Auto\-Tune used *maintenance windows* to schedule changes that required a blue/green deployment\. Maintenance windows are now deprecated in favor of the [off\-peak window](off-peak.md), which is a daily 10\-hour time block during which your domain typically experiences low traffic\. You can modify the default start time for the off\-peak window, but you can't modify the length\.

Any domains that had Auto\-Tune maintenance windows enabled before the introduction of off\-peak windows on February 16, 2023 can continue to use legacy maintenance windows with no interruption\. However, we recommend that you migrate your existing domains to use the off\-peak window for domain maintenance instead\. For instructions, see [Migrating from Auto\-Tune maintenance windows](off-peak.md#off-peak-migrate)\.

### Console<a name="auto-tune-schedule-console"></a>

**To schedule Auto\-Tune actions the off\-peak window**

1. Open the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home )\.

1. In the navigation pane, under **Domains**, choose the domain name to open the cluster configuration\.

1. Go to the **Auto\-Tune** tab and choose **Edit**\.

1. Choose **Turn on** if Auto\-Tune isn't already enabled\.

1. Under **Schedule optimizations during off\-peak window**, select **Off\-peak window**\.

1. Choose **Save changes**\.

### CLI<a name="auto-tune-schedule-cli"></a>

To configure your domain to schedule Auto\-Tune actions during the configured off\-peak window, include `UseOffPeakWindow` in the [UpdateDomainConfig](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_UpdateDomainConfig.html) request:

```
aws opensearch update-domain-config \
  --domain-name my-domain \
  --auto-tune-options DesiredState=ENABLED,UseOffPeakWindow=true,MaintenanceSchedules=null
```