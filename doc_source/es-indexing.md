# Indexing Data in Amazon Elasticsearch Service<a name="es-indexing"></a>

Because Elasticsearch uses a REST API, numerous methods exist for indexing documents\. You can use standard clients like [curl](https://curl.haxx.se/) or any programming language that can send HTTP requests\. To further simplify the process of interacting with it, Elasticsearch has clients for many programming languages\. Advanced users can skip directly to [Programmatic Indexing](es-indexing-programmatic.md)\.

For situations in which new data arrives incrementally \(for example, customer orders from a small business\), you might use the `_index` API to index documents as they arrive\. For situations in which the flow of data is less frequent \(for example, weekly updates to a marketing website\), you might prefer to generate a file and send it to the `_bulk` API\. For large numbers of documents, lumping requests together and using the `_bulk` API offers superior performance\. If your documents are enormous, however, you might need to index them individually using the `_index` API\.

For information about integrating data from other AWS services, see [Loading Streaming Data into Amazon Elasticsearch Service](es-aws-integrations.md)\.

## Introduction to Indexing<a name="es-indexing-intro"></a>

Before you can search data, you must *index* it\. Indexing is the method by which search engines organize data for fast retrieval\. The resulting structure is called, fittingly, an index\.

In Elasticsearch, the basic unit of data is a JSON *document*\. Within an index, Elasticsearch organizes documents into *types* \(arbitrary data categories that you define\) and identifies them using a unique *ID*\.

A request to the `_index` API looks like the following:

```
PUT elasticsearch_domain/index/type/id
{ "A JSON": "document" }
```

A request to the `_bulk` API looks a little different, because you specify the index, type, and ID in the bulk data:

```
POST elasticsearch_domain/_bulk
{ "index": { "_index" : "index", "_type" : "type", "_id" : "id" } }
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

Elasticsearch features automatic index creation when you add a document to an index that doesn't already exist\. It also features automatic ID generation if you don't specify an ID in the request\. This simple example automatically creates the `movies` index, establishes the document type of `movie`, indexes the document, and assigns it a unique ID:

```
POST elasticsearch_domain/movies/movie
{"title": "Spirited Away"}
```

**Important**  
To use automatic ID generation, you must use the `POST` method instead of `PUT`\.

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
      "_type" : "movie",
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
PUT elasticsearch_domain/movies/movie/7
{"title": "Spirited Away"}
```

Indices that you create in Elasticsearch versions 6\.0 and later can only contain one document type\. For best compatibility with future versions of Elasticsearch, use a single type, `_doc`, for all indices:

```
PUT elasticsearch_domain/more-movies/_doc/1
{"title": "Back to the Future"}
```

Indices default to five primary shards and one replica\. If you want to specify non\-default settings, create the index before adding documents:

```
PUT elasticsearch_domain/more-movies
{"settings": {"number_of_shards": 6, "number_of_replicas": 2}}
```

**Note**  
For sample code, see [Programmatic Indexing](es-indexing-programmatic.md)\.

Elasticsearch indices have the following naming restrictions:
+ All letters must be lowercase\.
+ Index names cannot begin with `_` or `-`\.
+ Index names cannot contain spaces, commas, `"`, `*`, `+`, `/`, `\`, `|`, `?`, `#`, `>`, or `<`\.

Don't include sensitive information in index, type, or document ID names\. Elasticsearch uses these names in its Uniform Resource Identifiers \(URIs\)\. Servers and applications often log HTTP requests, which can lead to unnecessary data exposure if URIs contain sensitive information:

```
2018-10-03T23:39:43 198.51.100.14 200 "GET https://elasticsearch_domain/dr-jane-doe/flu-patients-2018/202-555-0100/ HTTP/1.1"
```

Even if you don't have [permissions](es-ac.md) to view the associated JSON document, you could infer from this fake log line that one of Dr\. Doe's patients with a phone number of 202\-555\-0100 had the flu in 2018\.