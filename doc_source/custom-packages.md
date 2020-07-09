# Custom Packages for Amazon Elasticsearch Service<a name="custom-packages"></a>

Amazon Elasticsearch Service lets you upload custom dictionary files \(for example, stop words and synonyms\) for use with your cluster\. The generic term for these types of files is *packages*\. Dictionary files improve your search results by telling Elasticsearch to ignore certain high\-frequency words or to treat terms like "frozen custard," "gelato," and "ice cream" as equivalent\. They can also improve [stemming](https://en.wikipedia.org/wiki/Stemming), such as in the Japanese \(kuromoji\) Analysis plugin\.

**Topics**
+ [Uploading Packages to Amazon S3](#custom-packages-gs)
+ [Importing and Associating Packages](#custom-packages-assoc)
+ [Using Custom Packages with Elasticsearch](#custom-packages-using)
+ [Updating Custom Packages](#custom-packages-updating)
+ [Dissociating and Removing Packages](#custom-packages-dissoc)

## Uploading Packages to Amazon S3<a name="custom-packages-gs"></a>

Before you can associate a package with your domain, you must upload it to an Amazon S3 bucket\. For instructions, see [Uploading S3 Objects](https://docs.aws.amazon.com/AmazonS3/latest/gsg/upload-objects.html) in the *Amazon Simple Storage Service Getting Started Guide*\.

If your package contains sensitive information, specify [server\-side encryption with S3\-managed keys](https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingServerSideEncryption.html) when you upload it\. Amazon ES can't access files on S3 that you protect using an AWS KMS master key\.

After you upload the file, make note of its S3 path\. The path format is `s3://bucket-name/file-path/file-name`\.

You can use the following synonyms file for testing purposes\. Save it as `synonyms.txt`\.

```
danish, croissant, pastry
ice cream, gelato, frozen custard
sneaker, tennis shoe, running shoe
basketball shoe, hightop
```

Certain dictionaries, such as Hunspell dictionaries, use multiple files and require their own directories on the file system\. At this time, Amazon ES only supports single\-file dictionaries\.

## Importing and Associating Packages<a name="custom-packages-assoc"></a>

The console is the simplest way to import a package into Amazon ES and associate the package with a domain\. When you import a package from Amazon S3, Amazon ES stores its own copy of the package and automatically encrypts that copy using AES\-256 with Amazon ES\-managed keys\.

**To import and associate a package with a domain \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, choose **Packages**\.

1. Choose **Import**\.

1. Give the package a descriptive name\.

1. Provide the S3 path to the file, and then choose **Import**\.

1. Return to the **Packages** screen\.

1. When the package status is **Available**, select it\. Then choose **Associate to a domain**\.

1. Choose a domain, and then choose **Associate**\.

1. In the navigation pane, choose your domain, and then choose the **Packages** tab\.

1. When the package status is **Available**, note its ID\. Use `analyzers/id` as the file path in [requests to Elasticsearch](#custom-packages-using)\.

Alternately, use the AWS CLI, SDKs, or configuration API to import and associate packages\. For more information, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/) and [Amazon Elasticsearch Service Configuration API Reference](es-configuration-api.md)\.

## Using Custom Packages with Elasticsearch<a name="custom-packages-using"></a>

After you associate a file with a domain, you can use it in parameters such as `synonyms_path`, `stopwords_path`, and `user_dictionary` when you create tokenizers and token filters\. The exact parameter varies by object\. Several objects support `synonyms_path` and `stopwords_path`, but `user_dictionary` is exclusive to the kuromoji plugin\. The following example request adds a synonym file to a new index:

```
PUT my-index
{
  "settings": {
    "index": {
      "analysis": {
        "analyzer": {
          "synonym_analyzer": {
            "type": "custom",
            "tokenizer": "standard",
            "filter": ["synonym_filter"]
          }
        },
        "filter": {
          "synonym_filter": {
            "type": "synonym",
            "synonyms_path": "analyzers/F111111111"
          }
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "description": {
        "type": "text",
        "analyzer": "synonym_analyzer"
      }
    }
  }
}
```

This request creates a custom analyzer for the index that uses the standard tokenizer and a synonym token filter\.
+ Tokenizers break streams of characters into *tokens* \(typically words\) based on some set of rules\. The simplest example is the whitespace tokenizer, which breaks the preceding characters into a token each time it encounters a whitespace character\. A more complex example is the standard tokenizer, which uses a set of grammar\-based rules to work across many languages\.
+ Token filters add, modify, or delete tokens\. For example, a synonym token filter adds tokens when it finds a word in the synonyms list\. The stop token filter removes tokens when finds a word in the stop words list\.

This request also adds a text field \(`description`\) to the mapping and tells Elasticsearch to use the new analyzer for that field\. 

For testing purposes, add some documents to the index:

```
POST _bulk
{ "index": { "_index": "my-index", "_id": "1" } }
{ "description": "ice cream" }
{ "index": { "_index": "my-index", "_id": "2" } }
{ "description": "croissant" }
{ "index": { "_index": "my-index", "_id": "3" } }
{ "description": "tennis shoe" }
{ "index": { "_index": "my-index", "_id": "4" } }
{ "description": "hightop" }
```

Then search them using a synonym:

```
GET my-index/_search
{
  "query": {
    "match": {
      "description": "gelato"
    }
  }
}
```

In this case, Elasticsearch returns the following response:

```
{
  "hits": {
    "total": {
      "value": 1,
      "relation": "eq"
    },
    "max_score": 0.99463606,
    "hits": [{
      "_index": "my-index",
      "_type": "_doc",
      "_id": "1",
      "_score": 0.99463606,
      "_source": {
        "description": "ice cream"
      }
    }]
  }
}
```

**Tip**  
Dictionary files use Java heap space proportional to their size\. For example, a 2 GiB dictionary file might consume 2 GiB of heap space on a node\. If you use large files, ensure that your nodes have enough heap space to accommodate them\. [Monitor](es-managedomains-cloudwatchmetrics.md#es-managedomains-cloudwatchmetrics-cluster-metrics) the `JVMMemoryPressure` metric, and scale your cluster as necessary\.

## Updating Custom Packages<a name="custom-packages-updating"></a>

Uploading a new version of a package to Amazon S3 does *not* automatically update the package on Amazon Elasticsearch Service\. Amazon ES stores its own copy of the file, so if you upload a new version to S3, you must [import the file into Amazon ES again and associate it with your domains](#custom-packages-assoc)\.

After you associate the updated file with your domain, you can use it with new indices by using the requests in [Using Custom Packages with Elasticsearch](#custom-packages-using)\.

If you want to use the updated file with existing indices though, you must reindex them\. First, create an index that uses the updated synonyms file:

```
PUT my-new-index
{
  "settings": {
    "index": {
      "analysis": {
        "analyzer": {
          "synonym_analyzer": {
            "type": "custom",
            "tokenizer": "standard",
            "filter": ["synonym_filter"]
          }
        },
        "filter": {
          "synonym_filter": {
            "type": "synonym",
            "synonyms_path": "analyzers/F222222222"
          }
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "description": {
        "type": "text",
        "analyzer": "synonym_analyzer"
      }
    }
  }
}
```

Then [reindex](https://opendistro.github.io/for-elasticsearch-docs/docs/elasticsearch/reindex-data/) the old index to that new index:

```
POST _reindex
{
  "source": {
    "index": "my-index"
  },
  "dest": {
    "index": "my-new-index"
  }
}
```

If you frequently update synonym files, use [index aliases](https://opendistro.github.io/for-elasticsearch-docs/docs/elasticsearch/index-alias/) to maintain a consistent path to the latest index:

```
POST _aliases
{
  "actions": [
    {
      "remove": {
        "index": "my-index",
        "alias": "latest-index"
      }
    },
    {
      "add": {
        "index": "my-new-index",
        "alias": "latest-index"
      }
    }
  ]
}
```

If you don't need the old index, delete it\. If you no longer need the older version of the package, [dissociate and remove it](#custom-packages-dissoc)\.

## Dissociating and Removing Packages<a name="custom-packages-dissoc"></a>

Dissociating a package from a domain means that you can no longer use that file when you create new indices\. Any indices that already use the file can continue using it\.

The console is the simplest way to dissociate a package from a domain and remove it from Amazon ES\. Removing a package from Amazon ES does *not* remove it from its original location on Amazon S3\.

**To dissociate a package from a domain and remove it from Amazon ES \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, choose your domain, and then choose the **Packages** tab\.

1. Choose a package, **Actions**, and then choose **Dissociate**\. Confirm your choice\.

1. Wait for the package to disappear from the list\. You might need to refresh your browser\.

1. If you want to use the package with other domains, stop here\. To continue with removing the package, choose **Packages** in the navigation pane\.

1. Select the package and choose **Delete**\.

Alternately, use the AWS CLI, SDKs, or configuration API to dissociate and remove packages\. For more information, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/) and [Amazon Elasticsearch Service Configuration API Reference](es-configuration-api.md)\.