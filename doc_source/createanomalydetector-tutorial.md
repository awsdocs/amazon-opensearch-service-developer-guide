# Tutorial: Detect high CPU usage with anomaly detection<a name="createanomalydetector-tutorial"></a>

This tutorial demonstrates how to create an anomaly detector in Amazon OpenSearch Service to detect high CPU usage\. You'll use OpenSearch Dashboards to configure a detector to monitor CPU usage, and generate an alert when your CPU usage rises above a specified threshold\. 

**Note**  
These steps apply to the latest version of OpenSearch and might differ slightly for past versions\.

## Prerequisites<a name="createanomalydetector-tutorialprerequisites"></a>
+ You must have an OpenSearch Service domain running Elasticsearch 7\.4 or later, or any OpenSearch version\.
+ You must be ingesting application log files into your cluster that contain CPU usage data\.

## Step 1: Create a detector<a name="anomalydetectorcreate"></a>

First, create a detector that identifies anomalies in your CPU usage data\. 

1. Open the left panel menu in OpenSearch Dashboards and choose **Anomaly Detection**, then choose **Create detector**\.

1. Name the detector **high\-cpu\-usage**\. 

1. For your data source, choose your index that contains CPU usage log files where you want to identify anomalies\.

1. Choose the **Timestamp field** from your data\. Optionally, you can add a data filter\. This data filter analyzes only a subset of the data source and reduces the noise from data that's not relevant\.

1. Set the **Detector interval** to **2** minutes\. This interval defines the time \(by minute interval\) for the detector to collect the data\.

1.  In **Window delay**, add a **1\-minute** delay\. This delay adds extra processing time to ensure that all data within the window is present\. 

1. Choose **Next**\. On the anomaly detection dashboard, under the detector name, choose **Configure model**\.

1. For **Feature name**, enter **max\_cpu\_usage**\. For **Feature state**, select **Enable feature**\. 

1. For **Find anomalies based on**, choose **Field value**\.

1. For **Aggregation method**, choose **`max()`**\.

1. For **Field**, select the field in your data to check for anomalies\. For example, it might be called `cpu_usage_percentage`\.

1. Keep all other settings as their defaults and choose **Next**\.

1. Ignore the detector jobs setup and choose **Next**\.

1. In the pop\-up window, choose when to start the detector \(automatically or manually\), and then choose **Confirm**\.

Now that the detector is configured, after it initializes, you will be able to see real\-time results of the CPU usage in the **Real\-time results** section of your detector panel\. The **Live anomalies** section displays any anomalies that occur as data is being ingested in real time\. 

## Step 2: Configure an alert<a name="anomalydetectorcreatealert"></a>

Now that you've created a detector, create a monitor that invokes an alert to send a message to Slack when it detects CPU usage that meets the conditions specified in the detector settings\. You'll receive Slack notifications when data from one or more indexes meets the conditions that invoke the alert\. 

1. Open the left panel menu in OpenSearch Dashboards and choose **Alerting**, then choose **Create monitor**\.

1. Provide a name for the monitor\.

1. For **Monitor type**, choose **Per\-query monitor**\. A per\-query monitor runs a specified query and defines the triggers\.

1. For **Monitor defining method**, choose **Anomaly detector**, then select the detector that you created in the previous section from the **Detector** dropdown menu\.

1. For **Schedule**, choose how often the monitor collects data and how often you receive alerts\. For the purposes of this tutorial, set the schedule to run every **7** minutes\.

1. In the **Triggers** section, choose **Add trigger**\. For **Trigger name**, enter **High CPU usage**\. For this tutorial, for **Severity level**, choose **1**, which is the highest level of severity\.

1. For **Anomaly grade threshold**, choose **IS ABOVE**\. On the menu under that, choose the grade threshold to apply\. For this tutorial, set the **Anomaly grade** to **0\.7**\.

1. For **Anomaly confidence threshold**, choose **IS ABOVE**\. On the menu under that, enter the same number as your Anomaly grade\. For this tutorial, set the **Anomaly confidence threshold** to **0\.7**\.

1. In the **Actions** section, choose **Destination**\. In the **Name** field, choose the name of the destination\. On the **Type** menu, choose **Slack**\. In the **Webhook URL** field, enter a webhook URL to receive alerts to\. For more information, see [Sending messages using incoming webhooks](https://api.slack.com/messaging/webhooks)\.

1. Choose **Create**\.

## Related resources<a name="Anomalydetectorrelatedresources"></a>
+  [Configuring alerts in Amazon OpenSearch Service](alerting.md)
+  [Anomaly detection in Amazon OpenSearch Service](ad.md) 
+  [Anomaly detection API](https://opensearch.org/docs/latest/monitoring-plugins/ad/api/) 