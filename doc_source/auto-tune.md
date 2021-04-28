# Auto\-Tune for Amazon Elasticsearch Service<a name="auto-tune"></a>

Auto\-Tune in Amazon Elasticsearch Service \(Amazon ES\) uses performance and usage metrics from your Elasticsearch cluster to suggest changes to the Java virtual machine \(JVM\) settings on your nodes\. These optional changes improve cluster speed and stability\. 

Some changes deploy immediately, while others require you to schedule a maintenance window\. You can revert to the default Amazon ES settings at any time\.

As Auto\-Tune gathers and analyzes performance metrics for your domain, view its recommendations in the Amazon ES console on the **Notifications** page\.

Auto\-Tune is available in commercial Regions on domains running Elasticsearch 6\.7 or later with a [supported instance type](aes-supported-instance-types.md)\.

## Enabling or disabling Auto\-Tune<a name="auto-tune-enable"></a>

Amazon ES enables Auto\-Tune by default on new domains\. To disable it, clear the check box for the option in the console, or specify `DesiredState` as `"DISABLED"` in the AWS CLI or configuration API\.

To enable or disable Auto\-Tune on existing domains, we recommend using the console, which greatly simplifies the process\. In the console, choose your domain and then choose **Edit domain**\.

Auto\-Tune has two broad categories of changes:
+ Nondisruptive changes that it applies as the cluster runs
+ Changes that require a [blue/green deployment](es-managedomains-configuration-changes.md)

If you enable Auto\-Tune without setting a maintenance window, Auto\-Tune only applies nondisruptive changes\. The performance benefits over time are generally smaller, but you avoid the overhead associated with blue/green deployments\.

For guidance on configuring maintenance windows, see [Scheduling changes](#auto-tune-schedule)\.

## Scheduling changes<a name="auto-tune-schedule"></a>

To apply changes that require a blue/green deployment, you schedule a maintenance window for your domain—for example, between 6:00 and 9:00 AM on a Friday morning\. We recommend scheduling maintenance windows for low\-traffic times\.
+ To review all changes before deploying them, wait for Auto\-Tune to notify you of a suggested optimization\. Then schedule a one\-time maintenance window to deploy the changes\.
+ For a more automated experience, set a weekly maintenance window, such as every Saturday at 2:00 AM, or use a custom [Cron expression](#auto-tune-cron) for more complex schedules\.

To schedule changes in the console, choose your domain, choose the **Auto\-Tune** tab, choose **Modify**, and then choose **Submit**\. This tab also shows your current maintenance window and whether Auto\-Tune will make any changes during the next window\.

## Cron expressions<a name="auto-tune-cron"></a>

Cron expressions for Auto\-Tune use the same six\-field syntax as [Amazon CloudWatch Events](https://docs.aws.amazon.com/AmazonCloudWatch/latest/events/ScheduledEvents.html#CronExpressions):

```
minute hour day-of-month month day-of-week year
```

For example, the following expression translates to "every Tuesday and Friday at 1:15 AM from 2021 through 2024":

```
15 1 ? * 2,5 2021-2024
```

The following table includes valid values for each field\.


| Field | Valid Values | 
| --- | --- | 
|  Minute  |  0–59  | 
|  Hour  |  0–23  | 
|  Day of month  |  1–31  | 
|  Month  |  1–12 or JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC  | 
|  Day of week  |  1–7 or SUN, MON, TUE, WED, THU, FRI, SAT  | 
|  Year  |  1970–2199  | 

Day of month and day of week overlap, so you can specify one, but not both\. You must mark the other as `?`\. For a full summary of wildcard options, see the [Amazon CloudWatch Events User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/events/ScheduledEvents.html#CronExpressions)\.

## Types of changes<a name="auto-tune-types"></a>

Based on your domain's performance metrics, Auto\-Tune can suggest adjustments to the following settings\.


| Change Type | Description | 
| --- | --- | 
|  JVM heap size  |  By default, Amazon ES uses 50% of an instance's RAM for the JVM heap, up to a heap size of 32 GiB\.  Increasing this percentage gives Elasticsearch more memory, but leaves less for the operating system and other processes\. Larger values can decrease the number of garbage collection pauses, but increase the length of those pauses\.  | 
|  JVM young generation settings  |  JVM "young generation" settings affect the frequency of minor garbage collections\. More frequent minor collections can decrease the number of major collections and pauses\.  | 