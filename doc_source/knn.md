# KNN<a name="knn"></a>

Short for its associated *k\-nearest neighbors* algorithm, KNN for Amazon Elasticsearch Service lets you search for points in a vector space and find the "nearest neighbors" for those points by Euclidean distance or cosine similarity\. Use cases include recommendations \(for example, an "other songs you might like" feature in a music application\), image recognition, and fraud detection\.

KNN with Euclidean distance requires Elasticsearch 7\.1 or later\. Cosine similarity requires Elasticsearch 7\.7 or later\.

Full documentation for the Elasticsearch feature, including descriptions of [settings and statistics](https://opendistro.github.io/for-elasticsearch-docs/docs/knn/settings/), is available in the [Open Distro for Elasticsearch documentation](https://opendistro.github.io/for-elasticsearch-docs/docs/knn/)\. For background information about the k\-nearest neighbors algorithm, see [Wikipedia](https://en.wikipedia.org/wiki/K-nearest_neighbors_algorithm)\.

## Getting Started with KNN<a name="knn-gs"></a>

To use KNN, you must create an index with the `index.knn` setting and add one or more fields of the `knn_vector` data type\.

```
PUT my-index
{
  "settings": {
    "index.knn": true
  },
  "mappings": {
    "properties": {
      "my_vector1": {
        "type": "knn_vector",
        "dimension": 2
      },
      "my_vector2": {
        "type": "knn_vector",
        "dimension": 4
      }
    }
  }
}
```

The `knn_vector` data type supports a single list of up to 10,000 floats, with the number of floats defined by the required `dimension` parameter\. After you create the index, add some data to it\.

```
POST _bulk
{ "index": { "_index": "my-index", "_id": "1" } }
{ "my_vector1": [1.5, 2.5], "price": 12.2 }
{ "index": { "_index": "my-index", "_id": "2" } }
{ "my_vector1": [2.5, 3.5], "price": 7.1 }
{ "index": { "_index": "my-index", "_id": "3" } }
{ "my_vector1": [3.5, 4.5], "price": 12.9 }
{ "index": { "_index": "my-index", "_id": "4" } }
{ "my_vector1": [5.5, 6.5], "price": 1.2 }
{ "index": { "_index": "my-index", "_id": "5" } }
{ "my_vector1": [4.5, 5.5], "price": 3.7 }
{ "index": { "_index": "my-index", "_id": "6" } }
{ "my_vector2": [1.5, 5.5, 4.5, 6.4], "price": 10.3 }
{ "index": { "_index": "my-index", "_id": "7" } }
{ "my_vector2": [2.5, 3.5, 5.6, 6.7], "price": 5.5 }
{ "index": { "_index": "my-index", "_id": "8" } }
{ "my_vector2": [4.5, 5.5, 6.7, 3.7], "price": 4.4 }
{ "index": { "_index": "my-index", "_id": "9" } }
{ "my_vector2": [1.5, 5.5, 4.5, 6.4], "price": 8.9 }
```

Then you can search the data using the `knn` query type\.

```
GET my-index/_search
{
  "size": 2,
  "query": {
    "knn": {
      "my_vector2": {
        "vector": [2, 3, 5, 6],
        "k": 2
      }
    }
  }
}
```

In this case, `k` is the number of neighbors you want the query to return, but you must also include the `size` option\. Otherwise, you get `k` results for each shard \(and each segment\) rather than `k` results for the entire query\. KNN supports a maximum `k` value of 10,000\.

If you mix the `knn` query with other clauses, you might receive fewer than `k` results\. In this example, the `post_filter` clause reduces the number of results from 2 to 1\.

```
GET my-index/_search
{
  "size": 2,
  "query": {
    "knn": {
      "my_vector2": {
        "vector": [2, 3, 5, 6],
        "k": 2
      }
    }
  },
  "post_filter": {
    "range": {
      "price": {
        "gte": 6,
        "lte": 10
      }
    }
  }
}
```

## KNN Differences and Tuning<a name="knn-settings"></a>

Open Distro for Elasticsearch lets you modify all [KNN settings](https://opendistro.github.io/for-elasticsearch-docs/docs/knn/settings/) using the `_cluster/settings` API\. On Amazon ES, you can change all settings except `knn.memory.circuit_breaker.enabled` and `knn.circuit_breaker.triggered`\. KNN statistics are included as [Amazon CloudWatch metrics](es-managedomains-cloudwatchmetrics.md)\.

In particular, check the `KNNGraphMemoryUsage` metric on each data node against the `knn.memory.circuit_breaker.limit` statistic and the available RAM for the instance type\. Amazon ES uses half of an instance's RAM for the Java heap \(up to a heap size of 32 GiB\)\. By default, KNN uses up to 60% of the remaining half, so an instance type with 32 GiB of RAM can accommodate 9\.6 GiB of graphs \(32 \* 0\.5 \* 0\.6\)\. Performance can suffer if graph memory usage exceeds this value\.