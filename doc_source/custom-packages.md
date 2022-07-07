# Custom packages for Amazon OpenSearch Service<a name="custom-packages"></a>

Amazon OpenSearch Service lets you upload custom dictionary files, such as stop words and synonyms, for use with your cluster\. The generic term for these types of files is *packages*\. Dictionary files improve your search results by telling OpenSearch to ignore certain high\-frequency words or to treat terms like "frozen custard," "gelato," and "ice cream" as equivalent\. They can also improve [stemming](https://en.wikipedia.org/wiki/Stemming), such as in the Japanese \(kuromoji\) Analysis plugin\.

**Topics**
+ [Package permissions requirements](#custom-packages-iam)
+ [Uploading packages to Amazon S3](#custom-packages-gs)
+ [Importing and associating packages](#custom-packages-assoc)
+ [Using custom packages with OpenSearch](#custom-packages-using)
+ [Updating custom packages \(console\)](#custom-packages-updating)
+ [Updating custom packages \(AWS SDK\)](#custom-packages-update-python)
+ [Manual index updates](#custom-packages-updating-index-analyzers)
+ [Dissociating and removing packages](#custom-packages-dissoc)

## Package permissions requirements<a name="custom-packages-iam"></a>

Users without administrator access require certain AWS Identity and Access Management \(IAM\) actions in order to manage packages:
+ `es:CreatePackage` \- create a package in an OpenSearch Service Region
+ `es:DeletePackage` \- delete a package from an OpenSearch Service Region
+ `es:AssociatePackage` \- associate a package to a domain
+ `es:DissociatePackage` \- dissociate a package from a domain

You also need permissions on the Amazon S3 bucket path or object where the custom package resides\. 

Grant all permission within IAM, not in the domain access policy\. For more information, see [Identity and Access Management in Amazon OpenSearch Service](ac.md)\.

## Uploading packages to Amazon S3<a name="custom-packages-gs"></a>

Before you can associate a package with your domain, you must upload it to an Amazon S3 bucket\. For instructions, see [Uploading objects](https://docs.aws.amazon.com/AmazonS3/latest/user-guide/upload-objects.html) in the *Amazon Simple Storage Service User Guide*\.

If your package contains sensitive information, specify [server\-side encryption with S3\-managed keys](https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingServerSideEncryption.html) when you upload it\. OpenSearch Service can't access files on S3 that you protect using an AWS KMS key\.

After you upload the file, make note of its S3 path\. The path format is `s3://bucket-name/file-path/file-name`\.

You can use the following synonyms file for testing purposes\. Save it as `synonyms.txt`\.

```
danish, croissant, pastry
ice cream, gelato, frozen custard
sneaker, tennis shoe, running shoe
basketball shoe, hightop
```

Certain dictionaries, such as Hunspell dictionaries, use multiple files and require their own directories on the file system\. At this time, OpenSearch Service only supports single\-file dictionaries\.

## Importing and associating packages<a name="custom-packages-assoc"></a>

The console is the simplest way to import a package into OpenSearch Service and associate the package with a domain\. When you import a package from Amazon S3, OpenSearch Service stores its own copy of the package and automatically encrypts that copy using AES\-256 with OpenSearch Service\-managed keys\.

**To import and associate a package with a domain \(console\)**

1. In the Amazon OpenSearch Service console, choose **Packages**\.

1. Choose **Import package**\.

1. Give the package a descriptive name\.

1. Provide the S3 path to the file, and then choose **Submit**\.

1. Return to the **Packages** screen\.

1. When the package status is **Available**, select it\. Then choose **Associate to a domain**\.

1. Select a domain, and then choose **Associate**\.

1. In the navigation pane, choose your domain and go to the **Packages** tab\.

1. When the package status is **Available**, note its ID\. Use `analyzers/id` as the file path in [requests to OpenSearch](#custom-packages-using)\.

Alternately, use the AWS CLI, SDKs, or configuration API to import and associate packages\. For more information, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/) and [Configuration API reference for Amazon OpenSearch Service](configuration-api.md)\.

## Using custom packages with OpenSearch<a name="custom-packages-using"></a>

After you associate a file with a domain, you can use it in parameters such as `synonyms_path`, `stopwords_path`, and `user_dictionary` when you create tokenizers and token filters\. The exact parameter varies by object\. Several objects support `synonyms_path` and `stopwords_path`, but `user_dictionary` is exclusive to the kuromoji plugin\.

For the IK \(Chinese\) Analysis plugin, you can upload a custom dictionary file as a custom package and associate it to a domain, and the plugin automatically picks it up without requiring a `user_dictionary` parameter\. If your file is a synonyms file, use the `synonyms_path` parameter\.

The following example adds a synonyms file to a new index:

```
PUT my-index
{
  "settings": {
    "index": {
      "analysis": {
        "analyzer": {
          "my_analyzer": {
            "type": "custom",
            "tokenizer": "standard",
            "filter": ["my_filter"]
          }
        },
        "filter": {
          "my_filter": {
            "type": "synonym",
            "synonyms_path": "analyzers/F111111111",
            "updateable": true
          }
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "description": {
        "type": "text",
        "analyzer": "standard",
        "search_analyzer": "my_analyzer"
      }
    }
  }
}
```

This request creates a custom analyzer for the index that uses the standard tokenizer and a synonym token filter\.
+ Tokenizers break streams of characters into *tokens* \(typically words\) based on some set of rules\. The simplest example is the whitespace tokenizer, which breaks the preceding characters into a token each time it encounters a whitespace character\. A more complex example is the standard tokenizer, which uses a set of grammar\-based rules to work across many languages\.
+ Token filters add, modify, or delete tokens\. For example, a synonym token filter adds tokens when it finds a word in the synonyms list\. The stop token filter removes tokens when finds a word in the stop words list\.

This request also adds a text field \(`description`\) to the mapping and tells OpenSearch to use the new analyzer as its search analyzer\. You can see that it still uses the standard analyzer as its index analyzer\.

Finally, note the line `"updateable": true` in the token filter\. This field only applies to search analyzers, not index analyzers, and is critical if you later want to [update the search analyzer](#custom-packages-updating) automatically\.

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

In this case, OpenSearch returns the following response:

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
Dictionary files use Java heap space proportional to their size\. For example, a 2 GiB dictionary file might consume 2 GiB of heap space on a node\. If you use large files, ensure that your nodes have enough heap space to accommodate them\. [Monitor](managedomains-cloudwatchmetrics.md#managedomains-cloudwatchmetrics-cluster-metrics) the `JVMMemoryPressure` metric, and scale your cluster as necessary\.

## Updating custom packages \(console\)<a name="custom-packages-updating"></a>

Uploading a new version of a package to Amazon S3 does *not* automatically update the package on Amazon OpenSearch Service\. OpenSearch Service stores its own copy of the file, so if you upload a new version to S3, you must manually update it\.

Each of your associated domains stores *its* own copy of the file, as well\. To keep search behavior predictable, domains continue to use their current package version until you explicitly update them\. To update a custom package, modify the file in Amazon S3 Control, update the package in OpenSearch Service, and then apply the update\.

1. In the OpenSearch Service console, choose **Packages**\.

1. Choose a package and **Update**\.

1. Provide the S3 path to the file, and then choose **Update package**\.

1. Return to the **Packages** screen\.

1. When the package status changes to **Available**, select it\. Then choose one or more associated domains, **Apply update**, and confirm\. Wait for the association status to change to **Active**\.

1. The next steps vary depending on how you configured your indices:
   + If your domains runs OpenSearch or Elasticsearch 7\.8 or later and only uses search analyzers with the [updateable](#custom-packages-using) field set to true, you don't need to take any further action\. OpenSearch Service automatically updates your indices using the [\_plugins/\_refresh\_search\_analyzers API](https://opensearch.org/docs/im-plugin/refresh-analyzer/index/)\.
   + If your domain runs Elasticsearch 7\.7 or earlier, uses index analyzers, or doesn't use the `updateable` field, see [Manual index updates](#custom-packages-updating-index-analyzers)\.

Although the console is the simplest method, you can also use the AWS CLI, SDKs, or configuration API to update OpenSearch Service packages\. For more information, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/) and [Configuration API reference for Amazon OpenSearch Service](configuration-api.md)\.

## Updating custom packages \(AWS SDK\)<a name="custom-packages-update-python"></a>

Instead of manually updating a package in the console, you can use the SDKs to automate the update process\. The following sample Python script uploads a new package file to Amazon S3, updates the package in OpenSearch Service, and applies the new package to the specified domain\. After confirming the update was successful, it makes a sample call to OpenSearch demonstrating the new synonyms have been applied\.

You must provide values for `host`, `region`, `file_name`, `bucket_name`, `s3_key`, `package_id`, `domain_name`, and `query`\.

```
from requests_aws4auth import AWS4Auth
import boto3
import requests
import time
import json
import sys

host = ''  # The OpenSearch domain endpoint with https:// and a trailing slash. For example, https://my-test-domain.us-east-1.es.amazonaws.com/
region = ''  # For example, us-east-1
file_name = ''  # The path to the file to upload
bucket_name = ''  # The name of the S3 bucket to upload to
s3_key = ''  # The name of the S3 key (file name) to upload to
package_id = ''  # The unique identifier of the OpenSearch package to update
domain_name = ''  # The domain to associate the package with
query = ''  # A test query to confirm the package has been successfully updated

service = 'es'
credentials = boto3.Session().get_credentials()
client = boto3.client('opensearch')
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key,
                   region, service, session_token=credentials.token)


def upload_to_s3(file_name, bucket_name, s3_key):
    """Uploads file to S3"""
    s3 = boto3.client('s3')
    try:
        s3.upload_file(file_name, bucket_name, s3_key)
        print('Upload successful')
        return True
    except FileNotFoundError:
        sys.exit('File not found. Make sure you specified the correct file path.')


def update_package(package_id, bucket_name, s3_key):
    """Updates the package in OpenSearch Service"""
    print(package_id, bucket_name, s3_key)
    response = client.update_package(
        PackageID=package_id,
        PackageSource={
            'S3BucketName': bucket_name,
            'S3Key': s3_key
        }
    )
    print(response)


def associate_package(package_id, domain_name):
    """Associates the package to the domain"""
    response = client.associate_package(
        PackageID=package_id, DomainName=domain_name)
    print(response)
    print('Associating...')


def wait_for_update(domain_name, package_id):
    """Waits for the package to be updated"""
    response = client.list_packages_for_domain(DomainName=domain_name)
    package_details = response['DomainPackageDetailsList']
    for package in package_details:
        if package['PackageID'] == package_id:
            status = package['DomainPackageStatus']
            if status == 'ACTIVE':
                print('Association successful.')
                return
            elif status == 'ASSOCIATION_FAILED':
                sys.exit('Association failed. Please try again.')
            else:
                time.sleep(10)  # Wait 10 seconds before rechecking the status
                wait_for_update(domain_name, package_id)


def sample_search(query):
    """Makes a sample search call to OpenSearch"""
    path = '_search'
    params = {'q': query}
    url = host + path
    response = requests.get(url, params=params, auth=awsauth)
    print('Searching for ' + '"' + query + '"')
    print(response.text)
```

**Note**  
If you receive a "package not found" error when you run the script using the AWS CLI, it likely means Boto3 is using whichever Region is specified in \~/\.aws/config, which isn't the Region your S3 bucket is in\. Either run `aws configure` and specify the correct Region, or explicitly add the Region to the client:   

```
client = boto3.client('opensearch', region_name='us-east-1')
```

## Manual index updates<a name="custom-packages-updating-index-analyzers"></a>

To use an updated package, you must manually update your indexes if you meet any of the following conditions:
+ Your domain runs Elasticsearch 7\.7 or earlier\.
+ You use custom packages as index analyzers\.
+ You use custom packages as search analyzers, but don't include the [updateable](#custom-packages-using) field\.

To update analyzers with the new package files, you have two options:
+ Close and open any indexes that you want to update:

  ```
  POST my-index/_close
  POST my-index/_open
  ```
+ Reindex the indexes\. First, create an index that uses the updated synonyms file \(or an entirely new file\):

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

  Then [reindex](https://opensearch.org/docs/opensearch/reindex-data/) the old index to that new index:

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

  If you frequently update index analyzers, use [index aliases](https://opensearch.org/docs/opensearch/index-alias/) to maintain a consistent path to the latest index:

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

  If you don't need the old index, delete it:

  ```
  DELETE my-index
  ```

## Dissociating and removing packages<a name="custom-packages-dissoc"></a>

Dissociating a package from a domain means that you can no longer use that file when you create new indexes\. Any indexes that already use the file can continue using it\.

The console is the simplest way to dissociate a package from a domain and remove it from OpenSearch Service\. Removing a package from OpenSearch Service does *not* remove it from its original location on Amazon S3\.

**To dissociate a package from a domain and remove it from OpenSearch Service \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Amazon OpenSearch Service**\.

1. In the navigation pane, choose your domain, and then choose the **Packages** tab\.

1. Select a package, **Actions**, and then choose **Dissociate**\. Confirm your choice\.

1. Wait for the package to disappear from the list\. You might need to refresh your browser\.

1. If you want to use the package with other domains, stop here\. To continue with removing the package, choose **Packages** in the navigation pane\.

1. Select the package and choose **Delete**\.

Alternately, use the AWS CLI, SDKs, or configuration API to dissociate and remove packages\. For more information, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/) and [Configuration API reference for Amazon OpenSearch Service](configuration-api.md)\.