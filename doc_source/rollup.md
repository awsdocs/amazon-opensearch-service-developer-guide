# Summarizing indexes in Amazon OpenSearch Service with index rollups<a name="rollup"></a>

Index rollups in Amazon OpenSearch Service let you reduce storage costs by periodically rolling up old data into summarized indices\.

You pick the fields that interest you and use an index rollup to create a new index with only those fields aggregated into coarser time buckets\. You can store months or years of historical data at a fraction of the cost with the same query performance\.

Index rollups requires OpenSearch or Elasticsearch 7\.9 or later\. Full documentation for the feature is available in the [OpenSearch documentation](https://opensearch.org/docs/im-plugin/index-rollups/)\.

## Creating an index rollup job<a name="rollup-example"></a>

To get started, choose **Index Management** in OpenSearch Dashboards\. Select **Rollup Jobs** and choose **Create rollup job**\.

### Step 1: Set up indices<a name="rollup-example-1"></a>

Set up the source and target indices\. The source index is the one that you want to roll up\. The target index is where the index rollup results are saved\.

After you create an index rollup job, you can’t change your index selections\.

### Step 2: Define aggregations and metrics<a name="rollup-example-2"></a>

Select the attributes with the aggregations \(terms and histograms\) and metrics \(avg, sum, max, min, and value count\) that you want to roll up\. Make sure you don’t add a lot of highly granular attributes, because you won’t save much space\.

### Step 3: Specify schedules<a name="rollup-example-3"></a>

Specify a schedule to roll up your indexes as it’s being ingested\. The index rollup job is enabled by default\.

### Step 4: Review and create<a name="rollup-example-4"></a>

Review your configuration and select **Create**\.

### Step 5: Search the target index<a name="rollup-example-5"></a>

You can use the standard `_search` API to search the target index\. You can’t access the internal structure of the data in the target index because the plugin automatically rewrites the query in the background to suit the target index\. This is to make sure you can use the same query for the source and target index\.

To query the target index, set `size` to 0:

```
GET target_index/_search
{
  "size": 0,
  "query": {
    "match_all": {}
  },
  "aggs": {
    "avg_cpu": {
      "avg": {
        "field": "cpu_usage"
      }
    }
  }
}
```