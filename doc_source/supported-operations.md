# Supported operations<a name="supported-operations"></a>

OpenSearch Service supports many versions of OpenSearch and legacy Elasticsearch OSS\. The following sections show the operations that OpenSearch Service supports for each version\.

**Topics**
+ [Notable API differences](#version_api_notes)
+ [OpenSearch version 1\.1](#version_opensearch_1.1)
+ [OpenSearch version 1\.0](#version_opensearch_1.0)
+ [Elasticsearch version 7\.10](#version_7_10)
+ [Elasticsearch version 7\.9](#version_7_9)
+ [Elasticsearch version 7\.8](#version_7_8)
+ [Elasticsearch version 7\.7](#version_7_7)
+ [Elasticsearch version 7\.4](#version_7_4)
+ [Elasticsearch version 7\.1](#version_7_1)
+ [Elasticsearch version 6\.8](#version_6_8)
+ [Elasticsearch version 6\.7](#version_6_7)
+ [Elasticsearch version 6\.5](#version_6_5)
+ [Elasticsearch version 6\.4](#version_6_4)
+ [Elasticsearch version 6\.3](#version_6_3)
+ [Elasticsearch version 6\.2](#version_6_2)
+ [Elasticsearch version 6\.0](#version_6_0)
+ [Elasticsearch version 5\.6](#version_5_6)
+ [Elasticsearch version 5\.5](#version_5_5)
+ [Elasticsearch version 5\.3](#version_5_3)
+ [Elasticsearch version 5\.1](#version_5_1)
+ [Elasticsearch version 2\.3](#version_2_3)
+ [Elasticsearch version 1\.5](#version_1_5)

## Notable API differences<a name="version_api_notes"></a>

### Settings and statistics<a name="version_api_notes-cs"></a>

OpenSearch Service only accepts PUT requests to the `_cluster/settings` API that use the "flat" settings form\. It rejects requests that use the expanded settings form\.

```
// Accepted
PUT _cluster/settings
{
  "persistent" : {
    "action.auto_create_index" : false
  }
}

// Rejected
PUT _cluster/settings
{
  "persistent": {
    "action": {
      "auto_create_index": false
    }
  }
}
```

The high\-level Java REST client uses the expanded form, so if you need to send settings requests, use the low\-level client\.

Prior to Elasticsearch 5\.3, the `_cluster/settings` API on OpenSearch Service domains supported only the HTTP `PUT` method, not the `GET` method\. OpenSearch and later versions of Elasticsearch support the `GET` method, as shown in the following example:

```
GET https://domain-name.region.es.amazonaws.com/_cluster/settings?pretty
```

Here is a return example:

```
{
  "persistent": {
    "cluster": {
      "routing": {
        "allocation": {
          "cluster_concurrent_rebalance": "2",
          "node_concurrent_recoveries": "2",
          "disk": {
            "watermark": {
              "low": "1.35gb",
              "flood_stage": "0.45gb",
              "high": "0.9gb"
            }
          },
          "node_initial_primarirecoveries": "4"
        }
      }
    },
    "indices": {
      "recovery": {
        "max_bytper_sec": "40mb"
      }
    }
  }
}
```

If you compare responses from an open source OpenSearch cluster and OpenSearch Service for certain settings and statistics APIs, you might notice missing fields\. OpenSearch Service redacts certain information that exposes service internals, such as the file system data path from `_nodes/stats` or the operating system name and version from `_nodes`\.

### Shrink<a name="version_api_notes-shrink"></a>

The `_shrink` API can cause upgrades, configuration changes, and domain deletions to fail\. We don't recommend using it on domains that run Elasticsearch versions 5\.3 or 5\.1\. These versions have a bug that can cause snapshot restoration of shrunken indices to fail\.

If you use the `_shrink` API on other Elasticsearch or OpenSearch versions, make the following request before starting the shrink operation:

```
PUT https://domain-name.region.es.amazonaws.com/source-index/_settings
{
  "settings": {
    "index.routing.allocation.require._name": "name-of-the-node-to-shrink-to",
    "index.blocks.read_only": true
  }
}
```

Then make the following requests after completing the shrink operation:

```
PUT https://domain-name.region.es.amazonaws.com/source-index/_settings
{
  "settings": {
    "index.routing.allocation.require._name": null,
    "index.blocks.read_only": false
  }
}

PUT https://domain-name.region.es.amazonaws.com/shrunken-index/_settings
{
  "settings": {
    "index.routing.allocation.require._name": null,
    "index.blocks.read_only": false
  }
}
```

## OpenSearch version 1\.1<a name="version_opensearch_1.1"></a>

For OpenSearch 1\.1, OpenSearch Service supports the following operations\. For information about most of the operations, see the [OpenSearch REST API reference](https://opensearch.org/docs/latest/opensearch/rest-api/index/), or the API reference for the specific plugin\. 


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic OpenSearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

## OpenSearch version 1\.0<a name="version_opensearch_1.0"></a>

For OpenSearch 1\.0, OpenSearch Service supports the following operations\. For information about most of the operations, see the [OpenSearch REST API reference](https://opensearch.org/docs/latest/opensearch/rest-api/index/), or the API reference for the specific plugin\. 


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic OpenSearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

## Elasticsearch version 7\.10<a name="version_7_10"></a>

For Elasticsearch 7\.10, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic Elasticsearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

1. Legacy index templates \(`_template`\) were replaced by composable templates \(`_index_template`\) starting with Elasticsearch 7\.8\. Composable templates take precedence over legacy templates\. If no composable template matches a given index, a legacy template can still match and be applied\. The `_template` operation still works on OpenSearch and later versions of Elasticsearch OSS, but GET calls to the two template types return different results\.

## Elasticsearch version 7\.9<a name="version_7_9"></a>

For Elasticsearch 7\.9, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic OpenSearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

1. Legacy index templates \(`_template`\) were replaced by composable templates \(`_index_template`\) starting with Elasticsearch 7\.8\. Composable templates take precedence over legacy templates\. If no composable template matches a given index, a legacy template can still match and be applied\. The `_template` operation still works on OpenSearch and later versions of Elasticsearch OSS, but GET calls to the two template types return different results\.

## Elasticsearch version 7\.8<a name="version_7_8"></a>

For Elasticsearch 7\.8, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic Elasticsearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

1. Legacy index templates \(`_template`\) were replaced by composable templates \(`_index_template`\) starting with Elasticsearch 7\.8\. Composable templates take precedence over legacy templates\. If no composable template matches a given index, a legacy template can still match and be applied\. The `_template` operation still works on OpenSearch and later versions of Elasticsearch OSS, but GET calls to the two template types return different results\.

## Elasticsearch version 7\.7<a name="version_7_7"></a>

For Elasticsearch 7\.7, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic Elasticsearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

## Elasticsearch version 7\.4<a name="version_7_4"></a>

For Elasticsearch 7\.4, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic Elasticsearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

## Elasticsearch version 7\.1<a name="version_7_1"></a>

For Elasticsearch 7\.1, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic Elasticsearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

## Elasticsearch version 6\.8<a name="version_6_8"></a>

For Elasticsearch 6\.8, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic Elasticsearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

## Elasticsearch version 6\.7<a name="version_6_7"></a>

For Elasticsearch 6\.7, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic Elasticsearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

## Elasticsearch version 6\.5<a name="version_6_5"></a>

For Elasticsearch 6\.5, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic Elasticsearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

## Elasticsearch version 6\.4<a name="version_6_4"></a>

For Elasticsearch 6\.4, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic Elasticsearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

## Elasticsearch version 6\.3<a name="version_6_3"></a>

For Elasticsearch 6\.3, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic Elasticsearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

## Elasticsearch version 6\.2<a name="version_6_2"></a>

For Elasticsearch 6\.2, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic Elasticsearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

## Elasticsearch version 6\.0<a name="version_6_0"></a>

For Elasticsearch 6\.0, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic Elasticsearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

## Elasticsearch version 5\.6<a name="version_5_6"></a>

For Elasticsearch 5\.6, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic Elasticsearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

## Elasticsearch version 5\.5<a name="version_5_5"></a>

For Elasticsearch 5\.5, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. For considerations about using scripts, see [Other supported resources in Amazon OpenSearch Service](supported-resources.md)\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic Elasticsearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

## Elasticsearch version 5\.3<a name="version_5_3"></a>

For Elasticsearch 5\.3, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. Refers to the `PUT` method\. For information about the `GET` method, see [Notable API differences](#version_api_notes)\. This list only refers to the generic Elasticsearch operations that OpenSearch Service supports and does not include plugin\-specific supported operations for anomaly detection, ISM, and so on\.

1. See [Shrink](#version_api_notes-shrink)\.

## Elasticsearch version 5\.1<a name="version_5_1"></a>

For Elasticsearch 5\.1, OpenSearch Service supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\. To avoid a problem with `=` characters in `scroll_id` values, use the request body, not the query string, to pass `scroll_id` values to OpenSearch Service\.

1. See [Shrink](#version_api_notes-shrink)\.

## Elasticsearch version 2\.3<a name="version_2_3"></a>

For Elasticsearch 2\.3, OpenSearch Service supports the following operations\.


|  |  | 
| --- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 

## Elasticsearch version 1\.5<a name="version_1_5"></a>

For Elasticsearch 1\.5, OpenSearch Service supports the following operations\.


|  |  | 
| --- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/supported-operations.html)  | 