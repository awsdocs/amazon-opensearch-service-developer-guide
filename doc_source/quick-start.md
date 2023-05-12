# Tutorial: Creating and searching for documents in Amazon OpenSearch Service<a name="quick-start"></a>

In this tutorial, you learn how to create and search for a document in Amazon OpenSearch Service\. You add data to an index in the form of a JSON document\. OpenSearch Service creates an index around the first document that you add\.

This tutorial explains how to make HTTP requests to create documents, automatically generate an ID for a document, and perform basic and advanced searches on your documents\.

**Note**  
This tutorial uses a domain with open access\. For the highest level of security, we recommend that you put your domain inside a virtual private cloud \(VPC\)\.

## Prerequisites<a name="quick-start-prereqs"></a>

This tutorial has the following prerequisites:
+ You must have an AWS account\.
+ You must have an active OpenSearch Service domain\.

## Adding a document to an index<a name="quick-start-create"></a>

To add a document to an index, you can use any HTTP tool, such as [Postman](https://www.getpostman.com/), cURL, or the OpenSearch Dashboards console\. These examples assume that you’re using the developer console in OpenSearch Dashboards\. If you’re using a different tool, adjust accordingly by providing the full URL and credentials, if necessary\.

**To add a document to an index**

1. Navigate to the OpenSearch Dashboards URL for your domain\. You can find the URL on the domain's dashboard in the OpenSearch Service console\. The URL follows this format:

   ```
   domain-endpoint/_dashboards/
   ```

1. Sign in using your primary username and password\.

1. Open the left navigation panel and choose **Dev Tools**\.

1. The HTTP verb for creating a new resource is PUT, which is what you use to create a new document and index\. Enter the following command in the console:

   ```
   PUT fruit/_doc/1
   {
     "name":"strawberry",
     "color":"red"
   }
   ```

   The `PUT` request creates an index named *fruit* and adds a single document to the index with an ID of 1\. It produces the following response:

   ```
   {
     "_index" : "fruit",
     "_type" : "_doc",
     "_id" : "1",
     "_version" : 1,
     "result" : "created",
     "_shards" : {
       "total" : 2,
       "successful" : 2,
       "failed" : 0
     },
     "_seq_no" : 0,
     "_primary_term" : 1
   }
   ```

## Creating automatically generated IDs<a name="quick-start-id"></a>

OpenSearch Service can automatically generate an ID for your documents\. The command to generate IDs uses a POST request instead of a PUT request, and it requires no document ID \(in comparison to the previous request\)\. 

Enter the following request in the developer console:

```
POST veggies/_doc
{
  "name":"beet",
  "color":"red",
  "classification":"root"
}
```

This request creates an index named *veggies* and adds the document to the index\. It produces the following response:

```
{
  "_index" : "veggies",
  "_type" : "_doc",
  "_id" : "3WgyS4IB5DLqbRIvLxtF",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 2,
    "failed" : 0
  },
  "_seq_no" : 0,
  "_primary_term" : 1
}
```

Note that addditional `_id` field in the response, which indicates that an ID was automatically created\.

**Note**  
You don't provide anything after `_doc` in the URL, where the ID normally goes\. Because you’re creating a document with a generated ID, you don’t provide one yet\. That’s reserved for updates\. 

## Updating a document with a POST command<a name="quick-start-update"></a>

To update a document, you use an HTTP `POST` command with the ID number\.

First, create a document with an ID of `42`:

```
POST fruits/_doc/42
{
  "name":"banana",
  "color":"yellow"
}
```

Then use that ID to update the document:

```
POST fruits/_doc/42
{
  "name":"banana",
  "color":"yellow",
  "classification":"berries"
}
```

This command updates the document with the new field `classification`\. It produces the following response:

```
{
  "_index" : "fruits",
  "_type" : "_doc",
  "_id" : "42",
  "_version" : 2,
  "result" : "updated",
  "_shards" : {
    "total" : 2,
    "successful" : 2,
    "failed" : 0
  },
  "_seq_no" : 1,
  "_primary_term" : 1
}
```

**Note**  
If you try to update a document that does not exist, OpenSearch Service creates the document\.

## Performing bulk actions<a name="quick-start-bulk"></a>

You can use the `POST _bulk` API operation to perform multiple actions on one or more indexes in one request\. Bulk action commands take the following format:

```
POST /_bulk
<action_meta>\n
<action_data>\n
<action_meta>\n
<action_data>\n
```

Each action requires two lines of JSON\. First, you provide the action description or metadata\. On the next line, you provide the data\. Each part is separated by a newline \(\\n\)\. An action description for an insert might look like this:

```
{ "create" : { "_index" : "veggies", "_type" : "_doc", "_id" : "7" } }
```

And the next line containing the data might look like this:

```
{ "name":"kale", "color":"green", "classification":"leafy-green" }
```

Taken together, the metadata and the data represent a single action in a bulk operation\. You can perform many operations in one request, like this:

```
POST /_bulk
{ "create" : { "_index" : "veggies", "_type" : "_doc", "_id" : "35" } }
{ "name":"kale", "color":"green", "classification":"leafy-green" }
{ "create" : { "_index" : "veggies", "_type" : "_doc", "_id" : "36" } }
{ "name":"spinach", "color":"green", "classification":"leafy-green" }
{ "create" : { "_index" : "veggies", "_type" : "_doc", "_id" : "37" } }
{ "name":"arugula", "color":"green", "classification":"leafy-green" }
{ "create" : { "_index" : "veggies", "_type" : "_doc", "_id" : "38" } }
{ "name":"endive", "color":"green", "classification":"leafy-green" }
{ "create" : { "_index" : "veggies", "_type" : "_doc", "_id" : "39" } }
{ "name":"lettuce", "color":"green", "classification":"leafy-green" }
{ "delete" : { "_index" : "vegetables", "_type" : "_doc", "_id" : "1" } }
```

Notice that the last action is a `delete`\. There’s no data following the `delete` action\.

## Searching for documents<a name="quick-start-search"></a>

Now that data exists in your cluster, you can search for it\. For example, you might want to search for all root vegetables, or get a count of all leafy greens, or find the number of errors logged per hour\.

**Basic searches**

A basic search looks something like this:

```
GET veggies/_search?q=name:l*
```

The request produces a JSON response that contains the lettuce document\.

**Advanced searches**

You can perform more advanced searches by providing the query options as JSON in the request body:

```
GET veggies/_search
{
  "query": {
    "term": {
      "name": "lettuce"
    }
  }
}
```

This example also produces a JSON response with the lettuce document\.

**Sorting**

You can perform more of this type of query using sorting\. First, you need to recreate the index, because the automatic field mapping chose types that can’t be sorted by default\. Send the following requests to delete and recreate the index:

```
DELETE /veggies

PUT /veggies
{
   "mappings":{
      "properties":{
         "name":{
            "type":"keyword"
         },
         "color":{
            "type":"keyword"
         },
         "classification":{
            "type":"keyword"
         }
      }
   }
}
```

Then repopulate the index with data:

```
POST /_bulk
{ "create" : { "_index" : "veggies", "_type" : "_doc", "_id" : "7"  } }
{ "name":"kale", "color":"green", "classification":"leafy-green" }
{ "create" : { "_index" : "veggies", "_type" : "_doc", "_id" : "8" } }
{ "name":"spinach", "color":"green", "classification":"leafy-green" }
{ "create" : { "_index" : "veggies", "_type" : "_doc", "_id" : "9" } }
{ "name":"arugula", "color":"green", "classification":"leafy-green" }
{ "create" : { "_index" : "veggies", "_type" : "_doc", "_id" : "10" } }
{ "name":"endive", "color":"green", "classification":"leafy-green" }
{ "create" : { "_index" : "veggies", "_type" : "_doc", "_id" : "11" } }
{ "name":"lettuce", "color":"green", "classification":"leafy-green" }
```

Now you can search with a sort\. This request adds an ascending sort by the classification:

```
GET veggies/_search
{
  "query" : {
    "term": { "color": "green" }
  },
  "sort" : [
      "classification"
  ]
}
```

## Related resources<a name="quick-start-resources"></a>

For more information, see the following resources:
+ [Getting started with Amazon OpenSearch Service](gsg.md)
+ [Indexing data in Amazon OpenSearch Service](indexing.md)
+ [ Searching data in Amazon OpenSearch Service](searching.md)