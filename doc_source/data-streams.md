# Managing time\-series data in Amazon OpenSearch Service with data streams<a name="data-streams"></a>

A typical workflow to manage time\-series data involves multiple steps, such as creating a rollover index alias, defining a write index, and defining common mappings and settings for the backing indices\.

Data streams in Amazon OpenSearch Service help simplify this initial setup process\. Data streams work out of the box for time\-based data such as application logs that are typically append\-only in nature\. 

Data streams requires OpenSearch 1\.0 or later\. Full documentation for the feature is available in the [OpenSearch documentation](https://opensearch.org/docs/opensearch/data-streams/)\. 

## Getting started with data streams<a name="data-streams-example"></a>

A data stream is internally composed of multiple backing indices\. Search requests are routed to all the backing indices, while indexing requests are routed to the latest write index\.

### Step 1: Create an index template<a name="data-streams-example-1"></a>

To create a data stream, you first need to create an index template that configures a set of indexes as a data stream\. The `data_stream` object indicates that it’s a data stream and not a regular index template\. The index pattern matches with the name of the data stream:

```
PUT _index_template/logs-template
{
  "index_patterns": [
    "my-data-stream",
    "logs-*"
  ],
  "data_stream": {},
  "priority": 100
}
```

In this case, each ingested document must have an `@timestamp` field\. You can also define your own custom timestamp field as a property in the `data_stream` object:

```
PUT _index_template/logs-template
{
  "index_patterns": "my-data-stream",
  "data_stream": {
    "timestamp_field": {
      "name": "request_time"
    }
  }
}
```

### Step 2: Create a data stream<a name="data-streams-example-2"></a>

After you create an index template, you can directly start ingesting data without creating a data stream\. 

Because we have a matching index template with a `data_stream` object, OpenSearch automatically creates the data stream:

```
POST logs-staging/_doc
{
  "message": "login attempt failed",
  "@timestamp": "2013-03-01T00:00:00"
}
```

### Step 3: Ingest data into the data stream<a name="data-streams-example-3"></a>

To ingest data into a data stream, you can use the regular indexing APIs\. Make sure every document that you index has a timestamp field\. If you try to ingest a document that doesn’t have a timestamp field, you get an error\.

```
POST logs-redis/_doc
{
  "message": "login attempt",
  "@timestamp": "2013-03-01T00:00:00"
}
```

### Step 4: Searching a data stream<a name="data-streams-example-4"></a>

You can search a data stream just like you search a regular index or an index alias\. The search operation applies to all of the backing indexes \(all data present in the stream\)\.

```
GET logs-redis/_search
{
  "query": {
    "match": {
      "message": "login"
    }
  }
}
```

### Step 5: Rollover a data stream<a name="data-streams-example-5"></a>

You can set up an [Index State Management \(ISM\)](ism.md) policy to automate the rollover process for the data stream\. The ISM policy is applied to the backing indexes at the time of their creation\. When you associate a policy to a data stream, it only affects the future backing indexes of that data stream\. You also don’t need to provide the `rollover_alias` setting, because the ISM policy infers this information from the backing index\.

**Note**  
If you migrate a backing index to [cold storage](cold-storage.md), OpenSearch removes this index from the data stream\. Even if you move the index back to [UltraWarm](ultrawarm.md), the index remains independent and not part of the original data stream\. After an index has been removed from the data stream, searching against the stream won't return any data from the index\.

**Warning**  
The write index for a data stream can't be migrated to cold storage\. If you wish to migrate data in your data stream to cold storage, you must rollover the data stream before migration\.

### Step 6: Manage data streams in OpenSearch Dashboards<a name="data-streams-example-6"></a>

To manage data streams from OpenSearch Dashboards, open **OpenSearch Dashboards**, choose **Index Management**, select **Indices** or **Policy managed indices**\.

### Step 7: Delete a data stream<a name="data-streams-example-7"></a>

The delete operation first deletes the backing indexes of a data stream and then deletes the data stream itself\.

To delete a data stream and all of its hidden backing indices:

```
DELETE _data_stream/name_of_data_stream
```