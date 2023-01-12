# Ingesting data into Amazon OpenSearch Serverless collections<a name="serverless-clients"></a>

****  
***This is prerelease documentation for Amazon OpenSearch Serverless, which is in preview release\. The documentation and the feature are both subject to change\. We recommend that you use this feature only in test environments, and not in production environments\. For preview terms and conditions, see *Beta Service Participation* in [AWS Service Terms](https://aws.amazon.com/service-terms/)\. *** 

These sections provide details about the supported ingest pipelines for data ingestion into Amazon OpenSearch Serverless collections\. They also cover some of the clients that you can use to interact with the OpenSearch API operations\. Your clients should be compatible with OpenSearch 2\.x in order to integrate with OpenSearch Serverless\.

**Topics**
+ [Signing requests to OpenSearch Serverless](#serverless-signing)
+ [Minimum required permissions](#serverless-ingestion-permissions)
+ [Logstash](#serverless-logstash)
+ [Fluentd](#serverless-fluentd)
+ [Amazon Kinesis Data Firehose](#serverless-kdf)
+ [JavaScript](#serverless-javascript)
+ [Java](#serverless-java)
+ [Python](#serverless-python)
+ [Go](#serverless-go)

## Signing requests to OpenSearch Serverless<a name="serverless-signing"></a>

The following requirements apply when [signing requests](https://docs.aws.amazon.com/general/latest/gr/signature-version-4.html) to OpenSearch Serverless collections:
+ You must specify the service name as `aoss`\.
+ You can't include `Content-Length` as a signed header, otherwise you'll get an invalid signature error\.
+ The `x-amz-content-sha256` header is required for all AWS Signature Version 4 requests\. It provides a hash of the request payload\. For OpenSearch Serverless, include it with one of the following \+ "/" \+ id values when you build the canonical request for signing:
  + If there's a request payload, set the value to its Secure Hash Algorithm \(SHA\) cryptographic hash \(SHA256\)\.
  + If there's no request payload, set the value to `e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855`, which is the hash of an empty string\.
  + In either of the above two cases, you can also use the literal string `UNSIGNED-PAYLOAD` as the value of the `x-amz-content-sha256` header\.

## Minimum required permissions<a name="serverless-ingestion-permissions"></a>

In order to ingest data into an OpenSearch Serverless collection, the principal that is writing the data must have the following minimum permissions assigned in a [data access policy](serverless-data-access.md):

```
[
   {
      "Rules":[
         {
            "ResourceType":"index",
            "Resource":[
               "index/target-collection/*"
            ],
            "Permission":[
               "aoss:CreateIndex",
               "aoss:WriteDocument",
               "aoss:UpdateIndex"
            ]
         }
      ],
      "Principal":[
         "arn:aws:iam::123456789012:user/my-user"
      ]
   }
]
```

The permissions can be more broad if you plan to write to additional indexes\. For example, rather than specifying a single target index, you can allow permission to all indexes \(index/*target\-collection*/\*\), or a subset of indexes \(index/*target\-collection*/*logs\**\)\.

For a reference of all available OpenSearch API operations and their associated permissions, see [Supported operations and plugins in Amazon OpenSearch Serverless](serverless-genref.md)\.

## Logstash<a name="serverless-logstash"></a>

You must use version *2\.0\.0 or later* of the [logstash\-output\-opensearch](https://github.com/opensearch-project/logstash-output-opensearch) plugin to publish logs to OpenSearch Serverless collections\.

### Docker installation<a name="serverless-logstash-docker"></a>

Docker hosts the Logstash OSS software with the OpenSearch output plugin preinstalled: [opensearchproject/logstash\-oss\-with\-opensearch\-output\-plugin](https://hub.docker.com/r/opensearchproject/logstash-oss-with-opensearch-output-plugin/tags?page=1&ordering=last_updated&name=8.4.0)\.

You can pull the image just like any other image:

```
docker pull opensearchproject/logstash-oss-with-opensearch-output-plugin:latest
```

### Linux installation<a name="serverless-logstash-linux"></a>

First, [install the latest version of Logstash](https://www.elastic.co/guide/en/logstash/current/installing-logstash.html) if you haven't already\.

Then, install version 2\.0\.0 of the output plugin:

```
cd logstash-8.5.0/
bin/logstash-plugin install --version 2.0.0 logstash-output-opensearch
```

If the plugin is already installed, update it to the latest version:

```
bin/logstash-plugin update logstash-output-opensearch 
```

Starting with version 2\.0\.0 of the plugin, the AWS SDK uses version 3\. If you're using a Logstash version earlier than 8\.4\.0, you must remove any pre\-installed AWS plugins and install the `logstash-integration-aws` plugin:

```
/usr/share/logstash/bin/logstash-plugin remove logstash-input-s3
/usr/share/logstash/bin/logstash-plugin remove logstash-input-sqs
/usr/share/logstash/bin/logstash-plugin remove logstash-output-s3
/usr/share/logstash/bin/logstash-plugin remove logstash-output-sns
/usr/share/logstash/bin/logstash-plugin remove logstash-output-sqs
/usr/share/logstash/bin/logstash-plugin remove logstash-output-cloudwatch

/usr/share/logstash/bin/logstash-plugin install --version 0.1.0.pre logstash-integration-aws
```

### Configuring the OpenSearch output plugin<a name="serverless-logstash-configure"></a>

In order for the OpenSearch output plugin to work with OpenSearch Serverless, you must make the following modifications to the `opensearch` output section of logstash\.conf:
+ Specify `aoss` as the `service_name` under `auth_type`\.
+ Specify your collection endpoint for `hosts`\.
+ Add the parameters `default_server_major_version` and `legacy_template`\. These parameters are required for the plugin to work with OpenSearch Serverless\.

```
output {
  opensearch {
    hosts => "collection-endpoint:443"
    auth_type => {
      ...
      service_name => 'aoss'
    }
    default_server_major_version => 2
    legacy_template => false
  }
}
```

This example configuration file takes its input from files in an S3 bucket and sends them to an OpenSearch Serverless collection:

```
input {
  s3  {
    bucket => "my-s3-bucket"
    region => "us-east-1"
  }
}

output {
  opensearch {
    ecs_compatibility => disabled
    hosts => "https://my-collection-endpoint.us-east-1.aoss.amazonaws.com:443"
    index => my-index
    auth_type => {
      type => 'aws_iam'
      aws_access_key_id => 'your-access-key'
      aws_secret_access_key => 'your-secret-key'
      region => 'us-east-1'
      service_name => 'aoss'
    }
    default_server_major_version => 2
    legacy_template => false
  }
}
```

Then, run Logstash with the new configuration to test the plugin:

```
bin/logstash -f config/test-plugin.conf 
```

## Fluentd<a name="serverless-fluentd"></a>

You can use the [Fluentd OpenSearch plugin](https://docs.fluentd.org/output/opensearch) to collect data from your infrastructure, containers, and network devices and send them to OpenSearch Serverless collections\. Calyptia maintains a distribution of Fluentd that contains all of the downstream dependencies of Ruby and SSL\.

**To use Fluentd to send data to OpenSearch Serverless**

1. Download version 1\.4\.2 or later of Calyptia Fluentd from [https://www\.fluentd\.org/download](https://www.fluentd.org/download)\. This version includes the OpenSearch plugin by default, which supports OpenSearch Serverless\. 

1. Install the package\. Follow the instructions in the Fluentd documentation based on your operating system:
   + [Red Hat Enterprise Linux / CentOS / Amazon Linux](https://docs.fluentd.org/installation/install-by-rpm)
   + [Debian / Ubuntu](https://docs.fluentd.org/installation/install-by-deb)
   + [Windows](https://docs.fluentd.org/installation/install-by-msi)
   + [MacOSX](https://docs.fluentd.org/installation/install-by-dmg)

1. Add a configuration that sends data to OpenSearch Serverless\. This sample configuration sends the message "test" to a single collection\. Replace the `host` parameter with the endpoint of your collection, and optionally modify the `index_name` parameter\.

   ```
   <source>
   @type sample
   tag test
   test {"hello":"world"}
   </source>
   
   <match test>
   @type opensearch
   host https://collection-endpoint.us-east-1.aoss.amazonaws.com
   port 443
   index_name fluentd
   aws_service_name aoss
   </match>
   ```

1. Run Calyptia Fluentd to start sending data to the collection\. For example, on Mac you can run the following command:

   ```
   sudo launchctl load /Library/LaunchDaemons/calyptia-fluentd.plist
   ```

## Amazon Kinesis Data Firehose<a name="serverless-kdf"></a>

Kinesis Data Firehose supports OpenSearch Serverless as a delivery destination\. For instructions to send data into OpenSearch Serverless, see [Creating a Kinesis Data Firehose Delivery Stream](https://docs.aws.amazon.com/firehose/latest/dev/basic-create.html) and [Choose OpenSearch Serverless for Your Destination](https://docs.aws.amazon.com/firehose/latest/dev/create-destination.html#create-destination-opensearch-serverless) in the *Amazon Kinesis Data Firehose Developer Guide*\.

The IAM role that you provide to Kinesis Data Firehose for delivery must be specified within a data access policy with the `aoss:WriteDocument` minimum permission for the target collection\. For more information, see [Minimum required permissions](#serverless-ingestion-permissions)\.

Before you send data to OpenSearch Serverless, you might need to perform transforms on the data\. To learn more about using Lambda functions to perform this task, see [Amazon Kinesis Data Firehose Data Transformation](https://docs.aws.amazon.com/firehose/latest/dev/data-transformation.html) in the same guide\.

## JavaScript<a name="serverless-javascript"></a>

The following sample code uses the [opensearch\-js](https://www.npmjs.com/package/@opensearch-project/opensearch) client for JavaScript to establish a secure connection to the specified OpenSearch Serverless collection, create a single index, add a document, and search the index\. You must provide values for `node` and `region`\.

The important difference compared to OpenSearch Service *domains* is the service name \(`aoss` instead of `es`\)\.

```
var AWS = require('aws-sdk');
var aws4  = require('aws4');
var { Client, Connection } = require("@opensearch-project/opensearch");

var client = new Client({
  node: '', // The collection endpoint. For example, https://07tjusf2h91cunochc.us-east-1.aoss.amazonaws.com
  Connection: class extends Connection {
    buildRequestObject (params) {
      var request = super.buildRequestObject(params)
      request.service = 'aoss';
      request.region = ''; // e.g. us-east-1

      // You can't include Content-Length as a signed header, otherwise you'll get an invalid signature error.
      // The body and content-length should be removed before signing to avoid the header being included
      // and the signature being incorrectly calculated including the body.
      var body = request.body;
      request.body = undefined;
      delete request.headers['content-length'];

      // Serverless collections expect this header in all requests with an HTTP body. The payload doesn't need to be signed; any value is fine.
      request.headers['x-amz-content-sha256'] = 'UNSIGNED-PAYLOAD';

      request = aws4.sign(request, AWS.config.credentials);

      // Add the body back into the request now the signature has been calculated without it
      signedRequest.body = body;

      return request
    }
  }
});

async function index_document() {
  // Create an index with non-default settings.
  var index_name = "my-index";
  var settings = "{ \"settings\": { \"number_of_shards\": 1, \"number_of_replicas\": 0 }, \"mappings\": { \"properties\": { \"title\": {\"type\": \"text\"}, \"director\": {\"type\": \"text\"}, \"year\": {\"type\": \"text\"} } } }";

  var response = await client.indices.create({
    index: index_name,
    body: settings
  });

  console.log("Creating index:");
  console.log(response.body);

  // Add a document to the index
  var document = "{ \"title\": \"Avatar\", \"director\": \"James Cameron\", \"year\": \"2003\" }\n";

  var response = await client.index({
    index: index_name,
    body: document
  });

  console.log("Adding document:");
  console.log(response.body);
}

index_document().catch(console.log);
```

## Java<a name="serverless-java"></a>

To ingest data into an OpenSearch Serverless with Java, use [AmazonOpenSearchJavaClient](https://github.com/awsdocs/amazon-opensearch-service-developer-guide/tree/master/sample_code/AmazonOpenSearchJavaClient-main), which is similar to the existing OpenSearch low\-level Java REST client\. The sample code in the repository establishes a secure connection to the specified collection, creates an index, and indexes a single document\. You must provide values for `region` and `host`\.

## Python<a name="serverless-python"></a>

The following sample code uses the [opensearch\-py](https://pypi.org/project/opensearch-py/) client for Python to establish a secure connection to the specified OpenSearch Serverless collection and create a single index\. You must provide values for `region` and `host`\.

The important difference compared to OpenSearch Service *domains* is the service name \(`aoss` instead of `es`\)\.

```
from opensearchpy import OpenSearch, RequestsHttpConnection
from requests_aws4auth import AWS4Auth
import boto3

host = '' # The collection endpoint without https://. For example, 07tjusf2h91cunochc.us-east-1.aoss.amazonaws.com
region = '' # e.g. us-east-1

service = 'aoss'
credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

# Build the OpenSearch client
client = OpenSearch(
    hosts = [{'host': host, 'port': 443}],
    http_auth = awsauth,
    use_ssl = True,
    verify_certs = True,
    connection_class = RequestsHttpConnection
)

# Create an index
index_name = 'my-index'
index_body = {
    "mappings": {
        "properties": {
            "title": {"type": "text"},
            "director": {"type": "text"},
            "year": {"type": "integer"}
        }
    }
}
response = client.indices.create(index_name, body=index_body)
print('\nCreating index:')
print(response)
```

## Go<a name="serverless-go"></a>

The following sample code uses the [AWS SDK for Go](https://aws.amazon.com/sdk-for-go/) to establish a secure connection to the specified OpenSearch Serverless collection and create a single index\. You must provide values for `region` and `host`\.

The important difference compared to OpenSearch Service *domains* is the service name \(`aoss` instead of `es`\), as well as the additional request header `X-Amz-Content-Sha256`\.

```
package main

import (
  "fmt"
  "net/http"
  "strings"
  "time"
  "github.com/aws/aws-sdk-go/aws/credentials"
  "github.com/aws/aws-sdk-go/aws/signer/v4"
)

func main() {

  // Basic information for the serverless collection
  host := "" // The collection endpoint without https://. For example, 07tjusf2h91cunochc.us-east-1.aoss.amazonaws.com
  index := "my-index"
  endpoint := host + "/" + index 
  region := "" // e.g. us-east-1
  service := "aoss"

  // Sample index mapping
  json := `{
        "mappings": {
            "properties": {
                "title": {"type": "text"},
                "director": {"type": "text"},
                "year": {"type": "integer"}
            }
        }
}`
  body := strings.NewReader(json)

  // Get credentials from environment variables and create the Signature Version 4 signer
  //export AWS_ACCESS_KEY_ID="your-access-key"
  //export AWS_SECRET_ACCESS_KEY="your-secret-key"
  
  credentials := credentials.NewEnvCredentials()
  signer := v4.NewSigner(credentials)

  // An HTTP client for sending the request
  client := &http.Client{}

  // Form the HTTP request
  req, err := http.NewRequest(http.MethodPut, endpoint, body)
  if err != nil {
    fmt.Print(err)
  }

  req.Header.Add("Content-Type", "application/json")
  req.Header.Add("X-Amz-Content-Sha256", "UNSIGNED-PAYLOAD") // Serverless collections expect this header in all requests with an HTTP body. The payload doesn't need to be signed; any value is fine.

  // Sign the request, send it, and print the response
  signer.Sign(req, body, service, region, time.Now())
  resp, err := client.Do(req)
  if err != nil {
    fmt.Print(err)
  }

  fmt.Print(resp.Status + "\n")
}
```
