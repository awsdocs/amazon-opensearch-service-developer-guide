# Anomaly Detection for Amazon Elasticsearch Service<a name="ad"></a>

The anomaly detection feature in Amazon Elasticsearch Service \(Amazon ES\) automatically detects anomalies in your Elasticsearch data in near\-real time by using the Random Cut Forest \(RCF\) algorithm\. RCF is an unsupervised machine learning algorithm that models a sketch of your incoming data stream\. It computes an `anomaly grade` and `confidence score` value for each incoming data point\. The anomaly detection feature uses these values to differentiate an anomaly from normal variations in your data\. 

You can pair the anomaly detection plugin with the [Alerting for Amazon Elasticsearch Service](alerting.md) plugin to notify you as soon as an anomaly is detected\. 

Anomaly detection requires Elasticsearch 7\.4 or later\. Full documentation for the feature, including detailed steps and API descriptions, is available in the [Open Distro for Elasticsearch documentation](https://opendistro.github.io/for-elasticsearch-docs/docs/ad/)\.

**Note**  
To use the anomaly detection plugin, your user role must be mapped to the master role that gives you full access to the domain\. To learn more, see [Modifying the Master User](fgac.md#fgac-forget)\.

## Getting Started with Anomaly Detection<a name="ad-example-es"></a>

To get started, choose **Anomaly Detection** in Kibana\.

### Step 1: Create a Detector<a name="ad-example-es1"></a>

A detector is an individual anomaly detection task\. You can create multiple detectors, and all the detectors can run simultaneously, with each analyzing data from different sources\.

### Step 2: Add Features to Your Detector<a name="ad-example-es2"></a>

In this case, a feature is the field in your index that you check for anomalies\. A detector can discover anomalies across one or more features\. You must choose an aggregation for each feature: `average()`, `sum()`, `count()`, `min()`, or `max()`\. The aggregation method determines what constitutes an anomaly\.

For example, if you choose `min()`, the detector focuses on finding anomalies based on the minimum values of your feature\. If you choose `average()`, the detector finds anomalies based on the average values of your feature\.

You can add a maximum of five features per detector\.

### Step 3: Observe the Results<a name="ad-example-es3"></a>

![\[Sample anomaly detection dashboard.\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/ad.png)
+ The **Live anomalies** chart displays the live anomaly results for the last 60 intervals\. For example, if the interval is set to 10, it shows the results for the last 600 minutes\. This chart refreshes every 30 seconds\.
+ The **Anomaly history** chart plots the anomaly grade with the corresponding measure of confidence\.
+ The **Feature breakdown** graph plots the features based on the aggregation method\. You can vary the date\-time range of the detector\.
+ The **Anomaly occurrence** table shows the `Start time`, `End time`, `Data confidence`, and `Anomaly grade` for each anomaly detected\.

### Step 4: Set Up Alerts<a name="ad-example-es4"></a>

To create a monitor to send you notifications when any anomalies are detected, choose **Set up alerts**\. The plugin redirects you to the [https://opendistro.github.io/for-elasticsearch-docs/docs/alerting/monitors/#create-monitors](https://opendistro.github.io/for-elasticsearch-docs/docs/alerting/monitors/#create-monitors) page where you can set up an alert\.