# Point in time in Amazon OpenSearch Service<a name="pit"></a>

The point in time \(PIT\) feature is a type of search that lets you run different queries against a dataset that's fixed in time\. Typically, when you run the same query on the same index at different points in time, you receive different results because documents are constantly indexed, updated, and deleted\. With PIT, you can query against a constant state of your dataset\.

The main use of the PIT feature is to couple it with `search_after` functionality\. This is the preferred pagination method in OpenSearch, especially for deep pagination, because it operates on a dataset that is frozen in time, it is not bound to a query, and it supports consistent pagination going forward and backward\. You can use PIT with OpenSearch Service version 2\.5 and later\.

For more information about PIT, see [Point in Time](https://opensearch.org/docs/latest/opensearch/point-in-time/) in the OpenSearch documentation\.

## Considerations<a name="pit-considerations"></a>

Consider the following when you configure your PIT searches:
+ If you're upgrading from a 2\.3 domain and need fine\-grain access control on PIT actions, you need to manually add those actions and roles\.
+ There's no resiliency for PIT\. Node reboot, node termination, blue/green deployments, and ES process restarts cause all PIT data to be lost\.
+ If a shard relocates during blue/green deployment, only live data segments are transferred to the new node\. Segments of shards held by PIT \(both exclusively and the one shared with lived data\) remain on the old node\. 
+ PIT searches currently don't work with asynchronous search\.

## Create a PIT<a name="pit-sample"></a>

To create a PIT, send HTTP requests to `_search/point_in_time` using the following format:

```
POST opensearch-domain/my-index/_search/point_in_time?keep_alive=time
```

You can specify the following PIT options:


| Options | Description | Default value | Required | 
| --- | --- | --- | --- | 
| keep\_alive |  The amount of time to keep the PIT\. Every time you access a PIT with a search request, the PIT lifetime is extended by the amount of time equal to the `keep_alive` parameter\. This query parameter is required when you create a PIT, but optional in a search request\.  |  | Yes | 
| preference |  A string that specifies the node or the shard used to perform the search\.  | Random | No | 
| routing | A string that specifies to route search requests to a specific shard\. | The document’s \_id | No | 
| expand\_wildcards | A string that specifies type of index that can match the wildcard pattern\. Supports comma\-separated values\. Valid values are the following:[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/pit.html) | open | No | 
| allow\_partial\_pit\_creation | A boolean that specifies whether to create a PIT with partial failures\. | true | No | 

**Sample response**

```
{
    "pit_id": "o463QQEPbXktaW5kZXgtMDAwMDAxFnNOWU43ckt3U3IyaFVpbGE1UWEtMncAFjFyeXBsRGJmVFM2RTB6eVg1aVVqQncAAAAAAAAAAAIWcDVrM3ZIX0pRNS1XejE5YXRPRFhzUQEWc05ZTjdyS3dTcjJoVWlsYTVRYS0ydwAA",
    "_shards": {
        "total": 1,
        "successful": 1,
        "skipped": 0,
        "failed": 0
    },
    "creation_time": 1658146050064
}
```

When you create a PIT, you receive a PIT ID in the response\. This is the ID that you use to perform searches with the PIT\.

## Point in time permissions<a name="pit-permissions"></a>

PIT supports [fine\-grained access control](fgac.md)\. If you're upgrading to a 2\.5 domain and need fine\-grain access control, you need to manually create roles with the following permissions:

```
# Allows users to use all point in time search search functionality
point_in_time_full_access:
  reserved: true
  index_permissions:
    - index_patterns:
        - '*'
      allowed_actions:
        - "indices:data/read/point_in_time/create"
        - "indices:data/read/point_in_time/delete"
        - "indices:data/read/point_in_time/readall"
        - "indices:data/read/search"
        - "indices:monitor/point_in_time/segments"
        

# Allows users to use point in time search search functionality for specific index
# All type operations like list all PITs, delete all PITs are not supported in this case

point_in_time_index_access:
  reserved: true
  index_permissions:
    - index_patterns:
        - 'my-index-1'
      allowed_actions:
        - "indices:data/read/point_in_time/create"
        - "indices:data/read/point_in_time/delete"
        - "indices:data/read/search"
        - "indices:monitor/point_in_time/segments"
```

For domains with version 2\.5 and above, you can use the built\-in `point_in_time_full_access` role\. For more information, see [Security model]( https://opensearch.org/docs/latest/search-plugins/point-in-time/#security-model) in the OpenSearch documentation\.

## PIT settings<a name="pit-diff"></a>

OpenSearch lets you change all available [PIT settings](https://opensearch.org/docs/latest/search-plugins/point-in-time-api/#pit-settings) using the `_cluster/settings` API\. In OpenSearch Service, you can't currently modify settings\.

## Cross\-cluster search<a name="pit-ccs"></a>

You can create PITs, search with PIT IDs, list PITs, and delete PITs across clusters with the following minor limitations:
+ You can list all and delete all PITs only on the source domain\.
+ You can't minimize network round trips as part of a cross\-cluster search query\.

For more information, see [Cross\-cluster search in Amazon OpenSearch Service](cross-cluster-search.md)\.

## UltraWarm<a name="pit-ultrawarm"></a>

PIT searches with UltraWarm indexes continue to work\. For more information, see [UltraWarm storage for Amazon OpenSearch Service](ultrawarm.md)\.

**Note**  
You can monitor PIT search statistics in CloudWatch\. For a full list of metrics, see [Point in time metrics](managedomains-cloudwatchmetrics.md#managedomains-cloudwatchmetrics-pit)\.