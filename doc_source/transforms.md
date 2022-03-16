# Transforming indexes in Amazon OpenSearch Service<a name="transforms"></a>

Whereas [index rollup jobs](rollup.md) let you reduce data granularity by rolling up old data into condensed indices, transform jobs let you create a different, summarized view of your data centered around certain fields, so you can visualize or analyze the data in different ways\.

Index transforms have an OpenSearch Dashboards user interface and REST API\. The feature requires OpenSearch 1\.0 or later\. Full documentation is available in the [OpenSearch documentation](https://opensearch.org/docs/im-plugin/index-transforms/)\.

## Creating an index transform job<a name="transforms-example"></a>

If you don’t have any data in your cluster, use the sample flight data within OpenSearch Dashboards to try out transform jobs\. After adding the data, launch OpenSearch Dashboards\. Then choose **Index Management**, **Transform Jobs**, and **Create Transform Job**\.

### Step 1: Choose indices<a name="transforms-example-1"></a>

In the **Indices** section, select the source and target index\. You can either select an existing target index or create a new one by entering a name for it\.

If you want to transform just a subset of your source index, choose **Add Data Filter**, and use the OpenSearch [query DSL](https://opensearch.org/docs/opensearch/query-dsl/) to specify a subset of your source index\.

### Step 2: Choose fields<a name="transforms-example-2"></a>

After choosing your indices, choose the fields you want to use in your transform job, as well as whether to use groupings or aggregations\.
+ You can use groupings to place your data into separate buckets in your transformed index\. For example, if you want to group all of the airport destinations within the sample flight data, group the `DestAirportID` field into a target field of `DestAirportID_terms` field, and you can find the grouped airport IDs in your transformed index after the transform job finishes\.
+ On the other hand, aggregations let you perform simple calculations\. For example, you might include an aggregation in your transform job to define a new field of `sum_of_total_ticket_price` that calculates the sum of all airplane tickets\. Then you can analyze the new data in your transformed index\.

### Step 3: Specify a schedule<a name="transforms-example-3"></a>

Transform jobs are enabled by default and run on schedules\. For **transform execution interval**, specify an interval in minutes, hours, or days\.

### Step 4: Review and monitor<a name="transforms-example-4"></a>

Review your configuration and select **Create**\. Then monitor the **Transform job status** column\.

### Step 5: Search the target index<a name="transforms-example-5"></a>

After the job finishes, you can use the standard `_search` API to search the target index\. 

For example, after running a transform job that transforms the flight data based on the `DestAirportID` field, you can run the following request to return all fields that have a value of `SFO`:

```
GET target_index/_search
{
  "query": {
    "match": {
      "DestAirportID_terms" : "SFO"
    }
  }
}
```