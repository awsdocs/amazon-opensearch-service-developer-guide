# Supported operations and plugins in Amazon OpenSearch Serverless<a name="serverless-genref"></a>

****  
***This is prerelease documentation for Amazon OpenSearch Serverless, which is in preview release\. The documentation and the feature are both subject to change\. We recommend that you use this feature only in test environments, and not in production environments\. For preview terms and conditions, see *Beta Service Participation* in [AWS Service Terms](https://aws.amazon.com/service-terms/)\. *** 

Amazon OpenSearch Serverless supports a variety of OpenSearch plugins, as well as a subset of the indexing, search, and metadata [API operations](https://opensearch.org/docs/latest/opensearch/rest-api/index/) available in OpenSearch\. You can include the permissions in the left column of the table within [data access policies](serverless-data-access.md) in order to limit access to certain operations\.

**Topics**
+ [Supported OpenSearch API operations and permissions](#serverless-operations)
+ [Supported OpenSearch plugins](#serverless-plugins)

## Supported OpenSearch API operations and permissions<a name="serverless-operations"></a>

The following table lists the API operations that OpenSearch Serverless supports, along with their corresponding IAM permissions:


| Data access policy permission | OpenSearch API operations | Description | 
| --- | --- | --- | 
|  `aoss:CreateIndex`  | PUT <index> |  Create indexes\. For more information, see [Create index](https://opensearch.org/docs/latest/api-reference/index-apis/create-index/)\.  | 
|  `aoss:DescribeIndex`  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-genref.html)  |  Describe indexes\. For more information, see the following resources: [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-genref.html)  | 
|  `aoss:WriteDocument`  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-genref.html)  |  Write and update documents\. For more information, see the following resources: [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-genref.html)  Some operations are only allowed for collections of type `SEARCH`\. For more information, see [Choosing a collection type](serverless-overview.md#serverless-usecase)\.   | 
|  `aoss:ReadDocument`  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-genref.html)  | Read documents\. For more information, see the following resources:[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-genref.html) | 
|  `aoss:DeleteIndex`  | DELETE <target> | Delete indexes\. For more information, see [Delete index](https://opensearch.org/docs/latest/api-reference/index-apis/delete-index/)\. | 
|  `aoss:UpdateIndex`  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-genref.html)  |  Update index settings\. For more information, see the following resources: [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-genref.html)  | 
|  `aoss:CreateCollectionItems`  | POST \_aliases | Create index aliases\. For more information, see [Create aliases](https://opensearch.org/docs/latest/opensearch/index-alias/#create-aliases)\. | 
|  `aoss:DescribeCollectionItems`  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-genref.html)  |  Describe aliases and index templates\. For more information, see the following resources: [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-genref.html)  | 
|  `aoss:UpdateCollectionItems`  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-genref.html)  |  Update aliases and index templates\. For more information, see the following resources: [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-genref.html)  | 
|  `aoss:DeleteCollectionItems`  |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-genref.html)  |  Delete aliases and index templates\. For more information, see the following resources: [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-genref.html)  | 

## Supported OpenSearch plugins<a name="serverless-plugins"></a>

OpenSearch Serverless collections come prepackaged with the following plugins from the OpenSearch community\. Serverless automatically deploys and manages plugins for you\.

**Analysis plugins**
+  [ICU Analysis](https://www.elastic.co/guide/en/elasticsearch/plugins/7.10/analysis-icu.html) 
+  [Japanese \(kuromoji\) Analysis](https://www.elastic.co/guide/en/elasticsearch/plugins/7.10/analysis-kuromoji.html)
+  [Korean \(Nori\) Analysis](https://www.elastic.co/guide/en/elasticsearch/plugins/7.10/analysis-nori.html) 
+  [Phonetic Analysis](https://www.elastic.co/guide/en/elasticsearch/plugins/7.10/analysis-phonetic.html) 
+  [Smart Chinese Analysis](https://www.elastic.co/guide/en/elasticsearch/plugins/7.10/analysis-smartcn.html) 
+  [Stempel Polish Analysis](https://www.elastic.co/guide/en/elasticsearch/plugins/7.10/analysis-stempel.html)
+  [Ukranian Analysis](https://www.elastic.co/guide/en/elasticsearch/plugins/7.10/analysis-ukrainian.html)

**Mapper plugins**
+  [Mapper Size](https://www.elastic.co/guide/en/elasticsearch/plugins/7.10/mapper-size.html) 
+  [Mapper Murmur3](https://www.elastic.co/guide/en/elasticsearch/plugins/7.10/mapper-murmur3.html) 
+  [Mapper Annotated Text](https://www.elastic.co/guide/en/elasticsearch/plugins/7.10/mapper-annotated-text.html)

**Scripting plugins**
+  [Painless](https://www.elastic.co/guide/en/elasticsearch/reference/7.10/modules-scripting-painless.html)
+  [Expression](https://www.elastic.co/guide/en/elasticsearch/reference/7.10/modules-scripting-expression.html) 
+  [Mustache](https://www.elastic.co/guide/en/elasticsearch/reference/7.10/search-template.html)

In addition, OpenSearch Serverless includes all plugins that ship as modules\. 