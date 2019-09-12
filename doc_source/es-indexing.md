# Introduction to Indexing Data in Amazon Elasticsearch Service<a name="es-indexing"></a>

Because Elasticsearch uses a REST API, numerous methods exist for indexing documents\. You can use standard clients like [curl](https://curl.haxx.se/) or any programming language that can send HTTP requests\. To further simplify the process of interacting with it, Elasticsearch has clients for many programming languages\. Advanced users can skip directly to [Signing HTTP Requests to Amazon Elasticsearch Service](es-request-signing.md)\.

For situations in which new data arrives incrementally \(for example, customer orders from a small business\), you might use the `_index` API to index documents as they arrive\. For situations in which the flow of data is less frequent \(for example, weekly updates to a marketing website\), you might prefer to generate a file and send it to the `_bulk` API\. For large numbers of documents, lumping requests together and using the `_bulk` API offers superior performance\. If your documents are enormous, however, you might need to index them individually using the `_index` API\.

For information about integrating data from other AWS services, see [Loading Streaming Data into Amazon Elasticsearch Service](es-aws-integrations.md)\.

## Introduction to Indexing<a name="es-indexing-intro"></a>

Before you can search data, you must *index* it\. Indexing is the method by which search engines organize data for fast retrieval\. The resulting structure is called, fittingly, an index\.

In Elasticsearch, the basic unit of data is a JSON *document*\. Within an index, Elasticsearch identifies each document using a unique *ID*\.

A request to the `_index` API looks like the following:

```
PUT elasticsearch_domain/index/_doc/id
{ "A JSON": "document" }
```

A request to the `_bulk` API looks a little different, because you specify the index and ID in the bulk data:

```
POST elasticsearch_domain/_bulk
{ "index": { "_index" : "index", "_id" : "id" } }
{ "A JSON": "document" }
```

Bulk data must conform to a specific format, which requires a newline character \(`\n`\) at the end of every line, including the last line\. This is the basic format:

```
action_and_metadata\n
optional_document\n
action_and_metadata\n
optional_document\n
...
```

For a short sample file, see [Step 2: Upload Data to an Amazon ES Domain for Indexing](es-gsg-upload-data.md)\.

Elasticsearch features automatic index creation when you add a document to an index that doesn't already exist\. It also features automatic ID generation if you don't specify an ID in the request\. This simple example automatically creates the `movies` index, indexes the document, and assigns it a unique ID:

```
POST elasticsearch_domain/movies/_doc
{"title": "Spirited Away"}
```

**Important**  
For automatic ID generation, use the `POST` method instead of `PUT`\.

To verify that the document exists, you can perform the following search:

```
GET elasticsearch_domain/movies/_search?pretty
```

The response should contain the following:

```
"hits" : {
  "total" : 1,
  "max_score" : 1.0,
  "hits" : [
    {
      "_index" : "movies",
      "_type" : "_doc",
      "_id" : "AV4WaTnYxBoJaZkSFeX9",
      "_score" : 1.0,
      "_source" : {
        "title" : "Spirited Away"
      }
    }
  ]
}
```

Automatic ID generation has a clear downside: because the indexing code didn't specify a document ID, you can't easily update the document at a later time\. To specify an ID of `7`, use the following request:

```
PUT elasticsearch_domain/movies/_doc/7
{"title": "Spirited Away"}
```

Rather than requiring `_doc,` older versions of Elasticsearch support arbitrary names for document types\. Some older versions also support more than one document type per index\. No matter which version of Elasticsearch you choose, we recommend using a single type, `_doc`, for all indices\.

For new indices, self\-hosted Elasticsearch 7\.*x* has a default shard count of one\. Amazon ES 7\.*x* domains retain the previous default of five\. If you want to specify non\-default settings for shards and replicas, create the index before adding documents:

```
PUT elasticsearch_domain/more-movies
{"settings": {"number_of_shards": 6, "number_of_replicas": 2}}
```

**Note**  
For sample code, see [Signing HTTP Requests to Amazon Elasticsearch Service](es-request-signing.md)\.

## Naming Restrictions for Indices<a name="es-indexing-naming"></a>

Elasticsearch indices have the following naming restrictions:
+ All letters must be lowercase\.
+ Index names cannot begin with `_` or `-`\.
+ Index names cannot contain spaces, commas, `:`, `"`, `*`, `+`, `/`, `\`, `|`, `?`, `#`, `>`, or `<`\.

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