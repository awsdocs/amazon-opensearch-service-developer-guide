# Asynchronous search in Amazon OpenSearch Service<a name="asynchronous-search"></a>

With asynchronous search for Amazon OpenSearch Service you can submit a search query that gets executed in the background, monitor the progress of the request, and retrieve results at a later stage\. You can retrieve partial results as they become available before the search has completed\. After the search finishes, save the results for later retrieval and analysis\.

Asynchronous search requires OpenSearch 1\.0 or later, or Elasticsearch 7\.10 or later\. Full documentation for asynchronous search, including detailed steps and API descriptions, is available in the [OpenSearch documentation](https://opensearch.org/docs/search-plugins/async/index/)\.

## Sample search call<a name="asynchronous-search-sample"></a>

To perform an asynchronous search, send HTTP requests to `_plugins/_asynchronous_search` using the following format:

```
POST opensearch-domain/_plugins/_asynchronous_search
```

**Note**  
If you're using Elasticsearch 7\.10 instead of an OpenSearch version, replace `_plugins` with `_opendistro` in all asynchronous search requests\.

You can specify the following asynchronous search options:


| Options | Description | Default value | Required | 
| --- | --- | --- | --- | 
| wait\_for\_completion\_timeout |  Specifies the amount of time that you plan to wait for the results\. You can see whatever results you get within this time just like in a normal search\. You can poll the remaining results based on an ID\. The maximum value is 300 seconds\.  | 1 second | No | 
| keep\_on\_completion |  Specifies whether you want to save the results in the cluster after the search is complete\. You can examine the stored results at a later time\.  | false | No | 
| keep\_alive |  Specifies the amount of time that the result is saved in the cluster\. For example, `2d` means that the results are stored in the cluster for 48 hours\. The saved search results are deleted after this period or if the search is canceled\. Note that this includes the query runtime\. If the query overruns this time, the process cancels this query automatically\.  | 12 hours | No | 

**Sample request**

```
POST _plugins/_asynchronous_search/?pretty&size=10&wait_for_completion_timeout=1ms&keep_on_completion=true&request_cache=false
{
  "aggs": {
    "city": {
      "terms": {
        "field": "city",
        "size": 10
      }
    }
  }
}
```

**Note**  
All request parameters that apply to a standard `_search` query are supported\. If you're using Elasticsearch 7\.10 instead of an OpenSearch version, replace `_plugins` with `_opendistro`\.

## Asynchronous search permissions<a name="asynchronous-search-permissions"></a>

Asynchronous search supports [fine\-grained access control](fgac.md)\. For details on mixing and matching permissions to fit your use case, see [Asynchronous search security](https://opensearch.org/docs/search-plugins/async/security/)\.

For domains with fine\-grained access control enabled, you need the following minimum permissions for a role: 

```
# Allows users to use all asynchronous search functionality
asynchronous_search_full_access:
  reserved: true
  cluster_permissions:
    - 'cluster:admin/opensearch/asynchronous-search/*'
  index_permissions:
    - index_patterns:
        - '*'
      allowed_actions:
        - 'indices:data/read/search*'

# Allows users to read stored asynchronous search results
asynchronous_search_read_access:
  reserved: true
  cluster_permissions:
    - 'cluster:admin/opensearch/asynchronous-search/get'
```

For domains with fine\-grained access control disabled, use your IAM access and secret key to sign all requests\. You can access the results with the asynchronous search ID\. 

## Asynchronous search settings<a name="asynchronous-search-diff"></a>

OpenSearch lets you change all available [asynchronous search settings](https://opensearch.org/docs/search-plugins/async/settings/) using the `_cluster/settings` API\. In OpenSearch Service, you can only change the following settings: 
+ `plugins.asynchronous_search.node_concurrent_running_searches`
+ `plugins.asynchronous_search.persist_search_failures`

## Cross\-cluster search<a name="asynchronous-search-ccs"></a>

You can perform an asynchronous search across clusters with the following minor limitations:
+ You can run an asynchronous search only on the source domain\.
+ You can't minimize network round trips as part of a cross\-cluster search query\.

If you set up a connection between `domain-a -> domain-b` with connection alias `cluster_b` and `domain-a -> domain-c` with connection alias `cluster_c`, asynchronously search `domain-a`, `domain-b`, and `domain-c` as follows:

```
POST https://src-domain.us-east-1.es.amazonaws.com/local_index,cluster_b:b_index,cluster_c:c_index/_plugins/_asynchronous_search/?pretty&size=10&wait_for_completion_timeout=500ms&keep_on_completion=true&request_cache=false 
{
  "size": 0,
  "_source": {
    "excludes": []
  },
  "aggs": {
    "2": {
      "terms": {
        "field": "clientip",
        "size": 50,
        "order": {
          "_count": "desc"
        }
      }
    }
  },
  "stored_fields": [
    "*"
  ],
  "script_fields": {},
  "docvalue_fields": [
    "@timestamp"
  ],
  "query": {
    "bool": {
      "must": [
        {
          "query_string": {
            "query": "status:404",
            "analyze_wildcard": true,
            "default_field": "*"
          }
        },
        {
          "range": {
            "@timestamp": {
              "gte": 1483747200000,
              "lte": 1488326400000,
              "format": "epoch_millis"
            }
          }
        }
      ],
      "filter": [],
      "should": [],
      "must_not": []
    }
  }
}
```

**Response**

```
{
  "id" : "Fm9pYzJyVG91U19xb0hIQUJnMHJfRFEAAAAAAAknghQ1OWVBczNZQjVEa2dMYTBXaTdEagAAAAAAAAAB",
  "state" : "RUNNING",
  "start_time_in_millis" : 1609329314796,
  "expiration_time_in_millis" : 1609761314796
}
```

For more information, see [Cross\-cluster search in Amazon OpenSearch Service](cross-cluster-search.md)\.

## UltraWarm<a name="asynchronous-search-ultrawarm"></a>

Asynchronous searches with UltraWarm indexes continue to work\. For more information, see [UltraWarm storage for Amazon OpenSearch Service](ultrawarm.md)\.

**Note**  
You can monitor asynchronous search statistics in CloudWatch\. For a full list of metrics, see [Asynchronous search metrics](managedomains-cloudwatchmetrics.md#managedomains-cloudwatchmetrics-asynchronous-search)\.