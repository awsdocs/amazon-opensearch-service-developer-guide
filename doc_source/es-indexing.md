# Indexing Data in Amazon Elasticsearch Service<a name="es-indexing"></a>

Because Elasticsearch uses a REST API, numerous methods exist for indexing documents\. You can use standard clients like [curl](https://curl.haxx.se/) or any programming language that can send HTTP requests\. To further simplify the process of interacting with it, Elasticsearch has clients for many programming languages\. Advanced users can skip directly to [Signing HTTP Requests to Amazon Elasticsearch Service](es-request-signing.md) or [Loading Streaming Data into Amazon Elasticsearch Service](es-aws-integrations.md)\.

For an introduction to indexing, see the [Open Distro for Elasticsearch documentation](https://opendistro.github.io/for-elasticsearch-docs/docs/elasticsearch/index-data/)\.

## Naming Restrictions for Indices<a name="es-indexing-naming"></a>

Elasticsearch indices have the following naming restrictions:
+ All letters must be lowercase\.
+ Index names cannot begin with `_` or `-`\.
+ Index names can't contain spaces, commas, `:`, `"`, `*`, `+`, `/`, `\`, `|`, `?`, `#`, `>`, or `<`\.

Don't include sensitive information in index, type, or document ID names\. Elasticsearch uses these names in its Uniform Resource Identifiers \(URIs\)\. Servers and applications often log HTTP requests, which can lead to unnecessary data exposure if URIs contain sensitive information:

```
2018-10-03T23:39:43 198.51.100.14 200 "GET https://elasticsearch_domain/dr-jane-doe/flu-patients-2018/202-555-0100/ HTTP/1.1"
```

Even if you don't have [permissions](es-ac.md) to view the associated JSON document, you could infer from this fake log line that one of Dr\. Doe's patients with a phone number of 202\-555\-0100 had the flu in 2018\.

## Reducing Response Size<a name="es-indexing-size"></a>

Responses from the `_index` and `_bulk` APIs contain quite a bit of information\. This information can be useful for troubleshooting requests or for implementing retry logic, but can use considerable bandwidth\. In this example, indexing a 32 byte document results in a 339 byte response \(including headers\):

```
PUT elasticsearch_domain/more-movies/_doc/1
{"title": "Back to the Future"}
```

**Response**

```
{
  "_index": "more-movies",
  "_type": "_doc",
  "_id": "1",
  "_version": 4,
  "result": "updated",
  "_shards": {
    "total": 2,
    "successful": 2,
    "failed": 0
  },
  "_seq_no": 3,
  "_primary_term": 1
}
```

This response size might seem minimal, but if you index 1,000,000 documents per day—approximately 11\.5 documents per second—339 bytes per response works out to 10\.17 GB of download traffic per month\.

If data transfer costs are a concern, use the `filter_path` parameter to reduce the size of the Elasticsearch response, but be careful not to filter out fields that you need in order to identify or retry failed requests\. These fields vary by client\. The `filter_path` parameter works for all Elasticsearch REST APIs, but is especially useful with APIs that you call frequently, such as the `_index` and `_bulk` APIs:

```
PUT elasticsearch_domain/more-movies/_doc/1?filter_path=result,_shards.total
{"title": "Back to the Future"}
```

**Response**

```
{
  "result": "updated",
  "_shards": {
    "total": 2
  }
}
```

Instead of including fields, you can exclude fields with a `-` prefix\. `filter_path` also supports wildcards:

```
POST elasticsearch_domain/_bulk?filter_path=-took,-items.index._*
{ "index": { "_index": "more-movies", "_id": "1" } }
{"title": "Back to the Future"}
{ "index": { "_index": "more-movies", "_id": "2" } }
{"title": "Spirited Away"}
```

**Response**

```
{
  "errors": false,
  "items": [
    {
      "index": {
        "result": "updated",
        "status": 200
      }
    },
    {
      "index": {
        "result": "updated",
        "status": 200
      }
    }
  ]
}
```