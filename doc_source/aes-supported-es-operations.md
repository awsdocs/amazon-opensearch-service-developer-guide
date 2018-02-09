# Supported Elasticsearch Operations<a name="aes-supported-es-operations"></a>

Amazon ES currently supports several Elasticsearch versions\. The following topics show the operations that Amazon ES supports for each version\.


+ [Notable API Differences](#es_version_api_notes)
+ [Version 6\.0](#es_version_6_0)
+ [Version 5\.5](#es_version_5_5)
+ [Version 5\.3](#es_version_5_3)
+ [Version 5\.1](#es_version_5_1)
+ [Version 2\.3](#es_version_2_3)
+ [Version 1\.5](#es_version_1_5)

## Notable API Differences<a name="es_version_api_notes"></a>

Prior to Elasticsearch 5\.3, the `_cluster/settings` API on Amazon ES domains supported only the HTTP PUT method, not the GET method\. It now supports the GET method, as shown in the following example:

```
curl -XGET 'https://domain.region.es.amazonaws.com/_cluster/settings?pretty'
```

A sample return follows:

```
{
  "persistent" : {
    "cluster" : {
      "routing" : {
        "allocation" : {
          "cluster_concurrent_rebalance" : "2"
        }
      }
    },
    "indices" : {
      "recovery" : {
        "max_bytes_per_sec" : "20mb"
      }
    }
  },
  "transient" : {
    "cluster" : {
      "routing" : {
        "allocation" : {
          "exclude" : {
            "di_number" : "2"
          }
        }
      }
    }
  }
}
```

+ `cluster_concurrent_rebalance` specifies the number of shards that can be relocated to new nodes at any given time\. For more information, see [Shard Rebalancing Settings](https://www.elastic.co/guide/en/elasticsearch/reference/current/shards-allocation.html#_shard_rebalancing_settings) in the Elasticsearch documentation\.

+ `max_bytes_per_sec` is the maximum data transfer speed that Elasticsearch uses during a [recovery event](https://www.elastic.co/guide/en/elasticsearch/reference/current/cat-recovery.html)\.

+ `di_number` is an internal Amazon ES value that is used to copy shards to new *domain instances* after configuration changes\.

## Version 6\.0<a name="es_version_6_0"></a>

For Elasticsearch 6\.0, Amazon ES supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\.

1. For considerations about using scripts, see [[ERROR] BAD/MISSING LINK TEXT](aes-supported-resources.md)\.

1. Refers to the PUT method\. For information about the GET method, see [[ERROR] BAD/MISSING LINK TEXT](#es_version_api_notes)\.

For more information about Elasticsearch 6\.0 operations, see the [Elasticsearch documentation](https://www.elastic.co/guide/en/elasticsearch/reference/6.0/index.html)\.

## Version 5\.5<a name="es_version_5_5"></a>

For Elasticsearch 5\.5, Amazon ES supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\.

1. For considerations about using scripts, see [[ERROR] BAD/MISSING LINK TEXT](aes-supported-resources.md)\.

1. Refers to the PUT method\. For information about the GET method, see [[ERROR] BAD/MISSING LINK TEXT](#es_version_api_notes)\.

For more information about Elasticsearch 5\.5 operations, see the [Elasticsearch documentation](https://www.elastic.co/guide/en/elasticsearch/reference/5.5/index.html)\.

## Version 5\.3<a name="es_version_5_3"></a>

For Elasticsearch 5\.3, Amazon ES supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\.

1. For considerations about using scripts, see [[ERROR] BAD/MISSING LINK TEXT](aes-supported-resources.md)\.

1. Refers to the PUT method\. For information about the GET method, see [[ERROR] BAD/MISSING LINK TEXT](#es_version_api_notes)\.

For more information about Elasticsearch 5\.3 operations, see the [Elasticsearch documentation](https://www.elastic.co/guide/en/elasticsearch/reference/5.3/index.html)\.

## Version 5\.1<a name="es_version_5_1"></a>

For Elasticsearch 5\.1, Amazon ES supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html) [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  | 

1. Cluster configuration changes might interrupt these operations before completion\. We recommend that you use the `/_tasks` operation along with these operations to verify that the requests completed successfully\.

1. DELETE requests to `/_search/scroll` with a message body must specify `"Content-Length"` in the HTTP header\. Most clients add this header by default\.

1. For considerations about using scripts, see [[ERROR] BAD/MISSING LINK TEXT](aes-supported-resources.md)\.

For more information about Elasticsearch 5\.1 operations, see the [Elasticsearch documentation](https://www.elastic.co/guide/en/elasticsearch/reference/5.1/index.html)\.

## Version 2\.3<a name="es_version_2_3"></a>

For Elasticsearch 2\.3, Amazon ES supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  | 

For more information about Elasticsearch 2\.3 operations, see the [Elasticsearch documentation](https://www.elastic.co/guide/en/elasticsearch/reference/2.3/index.html)\.

## Version 1\.5<a name="es_version_1_5"></a>

For Elasticsearch 1\.5, Amazon ES supports the following operations\.


|  |  |  | 
| --- |--- |--- |
|  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-es-operations.html)  | 

For more information about Elasticsearch 1\.5 operations, see the [Elasticsearch documentation](https://www.elastic.co/guide/en/elasticsearch/reference/1.5/index.html)\.