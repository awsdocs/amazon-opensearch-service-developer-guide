# Sample code for accessing Amazon OpenSearch Serverless

The sample code in this directory demonstrates how to set up an authenticated connection to an Amazon OpenSearch Serverless collection and perform basic API operations such as creating an index, indexing a document, and performing a match all search. For more information about OpenSearch Serverless, see [Amazon OpenSearch Serverless (preview)](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless.html).

**_NOTE:_**  Amazon OpenSearch Serverless is in preview release. The documentation and the feature are both subject to change. We recommend that you use this feature only in test environments, and not in production environments. For preview terms and conditions, see [Beta Service Participation](https://aws.amazon.com/service-terms/) in AWS Service Terms. 

## Prerequisites

Before you can use this code sample, you must complete the following prerequisites:
- Create an OpenSearch Serverless collection. For instructions, see [Creating and managing Amazon OpenSearch Serverless collections](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-manage.html).
- [Configure data access](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-data-access.html) for your collection. If you don't complete this step, all requests to the collection endpoint will fail with permissions errors. At minimum, the IAM user or role that is running the script must have the following minimum permissions: `aoss:CreateIndex`, `aoss:WriteDocument`, `aoss:UpdateIndex`, `aoss:ReadDocument`.

## Instructions

1. Download the `AmazonOpenSearchJavaClient-main` directory.
2. Configure the following parameters in the [AmazonOpenSearchServiceSample.java](https://github.com/awsdocs/amazon-opensearch-service-developer-guide/blob/master/sample_code/AmazonOpenSearchJavaClient-main/src/main/java/AmazonOpenSearchServiceSample.java) file.

- `region` - The AWS Region that your collection is in. For example, `us-east-1`.
- `host` - The collection endpoint with https://. For example, `https://07tjusf2h91cunochc.us-east-1.aoss.amazonaws.com`.
- `index_name` - The name of the index to create in the collection. The example uses `my-index`.

  You can also optionally modify the index mapping, sample document, and search request.

3. Save and run the file.

### Usage notes

The OpenSearch Serverless connection string and other aspects of the request signing process differ from the authentication protocols 
used by basic Amazon OpenSearch Service domains. The standard clients for self-hosted OpenSearch and OpenSearch Service won't work for serverless collections without modifications like those in this code sample.

For more information, see [Signing requests to OpenSearch Serverless](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless-clients.html#serverless-signing).
