# Working with vector search collections<a name="serverless-vector-search"></a>

The *vector search* collection type provides a scalable and high\-performing similarity search capability in OpenSearch Serverless that makes it easy for you to build modern machine learning \(ML\) augmented search experiences and generative artificial intelligence \(AI\) applications without having to manage the underlying vector database infrastructure\. Use cases for vector search collections include image search, document search, music retrieval, product recommendation, video search, location\-based search, fraud detection, and anomaly detection\. 

Because the vector engine for OpenSearch Serverless is powered by the k\-nearest neighbor \(k\-NN\) search feature in [OpenSearch](https://opensearch.org/docs/latest/search-plugins/knn/index/), you get the same functionality with the simplicity of a serverless environment\. The engine supports the k\-NN OpenSearch APIs, allowing you to take advantage of full\-text search, advanced filtering, aggregations, geo\-spatial query, nested queries for faster retrieval of data, and enhanced search results\.

The vector engine provides distance metrics such as Euclidean, cosine similarity, and dot product, and can accommodate 16,000 dimensions\. You can also store fields with various data types, such as numbers, booleans, dates, keywords, and geopoints for metadata, and text for descriptive information to add more context to stored vectors\. Colocating the data types reduces complexity, increases maintainability, and avoids data duplication, version compatibility challenges, and licensing issues\. 

## Getting started with vector search collections<a name="serverless-vector-tutorial"></a>

In this tutorial, you'll complete the following steps to store, search, and retrieve vector embeddings in real\-time:

1. [Configure permissions](#serverless-vector-permissions)

1. [Create a collection](#serverless-vector-create)

1. [Upload and search data](#serverless-vector-index)

1. [Delete the collection](#serverless-vector-delete)

### Step 1: Configure permissions<a name="serverless-vector-permissions"></a>

In order to complete this tutorial, and to use OpenSearch Serverless in general, you must have the correct IAM permissions\. In this tutorial, you will create a collection, upload and search data, and then delete the collection\.

Your user or role must have an attached [identity\-based policy](security-iam-serverless.md#security-iam-serverless-id-based-policies) with the following minimum permissions:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "aoss:CreateCollection",
        "aoss:ListCollections",
        "aoss:BatchGetCollection",
        "aoss:DeleteCollection",
        "aoss:CreateAccessPolicy",
        "aoss:ListAccessPolicies",
        "aoss:UpdateAccessPolicy",
        "aoss:CreateSecurityPolicy",
        "iam:ListUsers",
        "iam:ListRoles"
      ],
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}
```

For more information about OpenSearch Serverless IAM permissions, see [Identity and Access Management for Amazon OpenSearch Serverless](security-iam-serverless.md)\.

### Step 2: Create a collection<a name="serverless-vector-create"></a>

A collection is a group of OpenSearch indexes that work together to support a specific workload or use case\.

**To create an OpenSearch Serverless collection**

1. Open the Amazon OpenSearch Service console at [https://console\.aws\.amazon\.com/aos/home](https://console.aws.amazon.com/aos/home )\.

1. Choose **Collections** in the left navigation pane and choose **Create collection**\.

1. Name the collection **housing**\.

1. For collection type, choose **Vector search**\. For more information, see [Choosing a collection type](serverless-overview.md#serverless-usecase)\.

1. Under **Security**, select **Easy create** to streamline your security configuration\. All the data in the vector engine is encrypted in transit and at rest by default\. The vector engine supports fine\-grained AWS Identity and Access Management \(IAM\) permissions so that you can define who can create, update, and delete encryptions, networks, collections, and indexes\.

1. Choose **Next**\.

1. Review your collection settings and choose **Submit**\. Wait several minutes for the collection status to become `Active`\.

### Step 3: Upload and search data<a name="serverless-vector-index"></a>

An index is a collection of documents with a common data schema that provides a way for you to store, search, and retrieve your vector embeddings and other fields\. You can create and upload data to indexes in an OpenSearch Serverless collection using [Postman](https://www.postman.com/downloads/) or curl\. Vector search collections aren't compatible with the OpenSearch Dashboards console\.

**To index and search data in the movies collection**

1. To create a single index for your new collection, send the following request with Postman\. By default, this creates an index with a `nmslib` engine and Euclidean distance\.

   ```
   PUT housing-index
   {
      "settings": {
         "index.knn": true
      },
      "mappings": {
         "properties": {
            "housing-vector": {
               "type": "knn_vector",
               "dimension": 3
            },
            "title": {
               "type": "text"
            },
            "price": {
               "type": "long"
            },
            "location": {
               "type": "geo_point"
            }
         }
      }
   }
   ```

1. To index a single document into *housing\-index*, send the following request:

   ```
   POST housing-index/_doc
   {
     "housing-vector": [
       10,
       20,
       30
     ],
     "title": "2 bedroom in downtown Seattle",
     "price": "2800",
     "location": "47.71, 122.00"
   }
   ```

1. To search for properties similar to the ones in your index, send the following query:

   ```
   GET housing-index/_search
   {
       "size": 5,
       "query": {
           "knn": {
               "housing-vector": {
                   "vector": [
                       10,
                       20,
                       30
                   ],
                   "k": 5
               }
           }
       }
   }
   ```

### Step 4: Delete the collection<a name="serverless-vector-delete"></a>

Because the *housing* collection is for test purposes, make sure to delete it when you're done experimenting\.

**To delete an OpenSearch Serverless collection**

1. Go back to the **Amazon OpenSearch Service** console\.

1. Choose **Collections** in the left navigation pane and select the **properties** collection\.

1. Choose **Delete** and confirm deletion\.

## Filtered search<a name="serverless-vector-filter"></a>

You can use filters to refine your semantic search results\. To create an index and perform a filtered search on your documents, substitute [Upload and search data](#serverless-vector-index) in the previous tutorial with the following instructions\. The other steps remain the same\. For more information about filters, see [k\-NN search with filters](https://opensearch.org/docs/latest/search-plugins/knn/filter-search-knn/)\.

**To index and search data in the movies collection**

1. To create a single index for your collection, send the following request with Postman:

   ```
   PUT housing-index-filtered
   {
     "settings": {
       "index.knn": true
     },
     "mappings": {
       "properties": {
         "housing-vector": {
           "type": "knn_vector",
           "dimension": 3,
           "method": {
             "engine": "faiss",
             "name": "hnsw"
           }
         },
         "title": {
           "type": "text"
         },
         "price": {
           "type": "long"
         },
         "location": {
           "type": "geo_point"
         }
       }
     }
   }
   ```

1. To index a single document into *housing\-index\-filtered*, send the following request:

   ```
   POST housing-index-filtered/_doc
   {
     "housing-vector": [
       10,
       20,
       30
     ],
     "title": "2 bedroom in downtown Seattle",
     "price": "2800",
     "location": "47.71, 122.00"
   }
   ```

1. To search your data for an apartment in Seattle under a given price and within a given distance of a geographical point, send the following request:

   ```
   GET housing-index-filtered/_search
   {
     "size": 5,
     "query": {
       "knn": {
         "housing-vector": {
           "vector": [
             0.1,
             0.2,
             0.3
           ],
           "k": 5,
           "filter": {
             "bool": {
               "must": [
                 {
                   "query_string": {
                     "query": "Find me 2 bedroom apartment in Seattle under $3000 ",
                     "fields": [
                       "title"
                     ]
                   }
                 },
                 {
                   "range": {
                     "price": {
                       "lte": 3000
                     }
                   }
                 },
                 {
                   "geo_distance": {
                     "distance": "100miles",
                     "location": {
                       "lat": 48,
                       "lon": 121
                     }
                   }
                 }
               ]
             }
           }
         }
       }
     }
   }
   ```

## Limitations<a name="serverless-vector-limitations"></a>

Vector search collections have the following limitations:
+ Vector search collections don't support the Lucene engine and Faiss engine algorithms IVF and IVFPQ\.
+ Vector search collections don't support the warmup, stats and model trainings APIs\.
+ Vector search collections don't support inline or stored scripts\.
+ Index count information isn't available in the AWS Management Console for vector search collections\. 
+ The refresh interval for indexes on vector search collections is 60 seconds\.
+ Vector search collections aren't compatible with the OpenSearch Dashboards console\.

## Next steps<a name="serverless-vector-next"></a>

Now that you know how to create a vector search collection and index data, you might want to try some of the following exercises:
+ Use the OpenSearch Python client to work with vector search collections\. See this tutorial on [GitHub](https://github.com/opensearch-project/opensearch-py/blob/main/guides/plugins/knn.md)\. 
+ Use the OpenSearch Java client to work with vector search collections\. See this tutorial on [GitHub](https://github.com/opensearch-project/opensearch-java/blob/main/guides/plugins/knn.md)\. 
+ Set up LangChain to use OpenSearch as a vector store\. LangChain is an open\-source framework for developing applications powered by language models\. For more information, see the [LangChain documentation](https://python.langchain.com/docs/integrations/vectorstores/opensearch)\.