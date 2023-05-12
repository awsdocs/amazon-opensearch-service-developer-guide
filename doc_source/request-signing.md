# Signing HTTP requests to Amazon OpenSearch Service<a name="request-signing"></a>

This section includes examples of how to send signed HTTP requests to Amazon OpenSearch Service using Elasticsearch and OpenSearch clients and other common libraries\. These code examples are for interacting with the OpenSearch APIs, such as `_index`, `_bulk`, and `_snapshot`\. If your domain access policy includes IAM roles, or you use a user with [fine\-grained access control](fgac.md), you must sign requests to the OpenSearch APIs with your IAM credentials\. 

For examples of how to interact with the configuration API, including operations like creating, updating, and deleting OpenSearch Service domains, see [Using the AWS SDKs to interact with Amazon OpenSearch Service](configuration-samples.md)\.

**Important**  
The latest versions of the Elasticsearch clients might include license or version checks that artificially break compatibility\. For the correct client version to use, see [Elasticsearch client compatibility](samplecode.md#client-compatibility)\.

**Topics**
+ [Java](#request-signing-java)
+ [Python](#request-signing-python)
+ [Ruby](#request-signing-ruby)
+ [Node](#request-signing-node)
+ [Go](#request-signing-go)

## Java<a name="request-signing-java"></a>

The easiest way to send a signed request with Java is to use `AwsSdk2Transport`, introduced in [opensearch\-java](https://github.com/opensearch-project/opensearch-java) version 2\.1\.0\. The following [example](https://github.com/awsdocs/amazon-opensearch-service-developer-guide/tree/master/sample_code/java/opensearch-java-aws-sdk2-transport) creates an index, writes a document, and deletes the index\. You must provide values for `region` and `host`\.

```
package com.amazonaws.samples;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.transport.aws.AwsSdk2Transport;
import org.opensearch.client.transport.aws.AwsSdk2TransportOptions;

import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;

public class IndexDocument {

    private static final String host = "search-....us-west-2.es.amazonaws.com";
    private static Region region = Region.US_WEST_2;

    public static void main(String[] args) throws IOException, InterruptedException {
        SdkHttpClient httpClient = ApacheHttpClient.builder().build();
        try {

            OpenSearchClient client = new OpenSearchClient(
                    new AwsSdk2Transport(
                            httpClient,
                            host,
                            region,
                            AwsSdk2TransportOptions.builder().build()));

            // create the index
            String index = "sample-index";
            
            CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(index).build();
            client.indices().create(createIndexRequest);

            // index data
            Map<String, Object> document = new HashMap<>();
            document.put("firstName", "Michael");
            document.put("lastName", "Douglas");
            IndexRequest documentIndexRequest = new IndexRequest.Builder()
                    .index(index)
                    .id("2")
                    .document(document)
                    .build();
            client.index(documentIndexRequest);

            // delete the index
            DeleteIndexRequest deleteRequest = new DeleteIndexRequest.Builder().index(index).build();
            client.indices().delete(deleteRequest);
            
        } finally {
            httpClient.close();
        }
    }
}
```

Other alternatives include using an AWS Request Signing Interceptor and/or the high\-level REST client\. See [this sample](https://github.com/awsdocs/amazon-opensearch-service-developer-guide/tree/master/sample_code/java/aws-request-signing-apache-interceptor)

**Tip**  
This sample uses the default credential chain\. Run `aws configure` using the AWS CLI to set your credentials\.

## Python<a name="request-signing-python"></a>

This example uses the [opensearch\-py](https://pypi.org/project/opensearch-py/) client for Python, which you can install using [pip](https://pypi.python.org/pypi/pip)\. You must provide values for `region` and `host`\.

```
from opensearchpy import OpenSearch, RequestsHttpConnection, AWSV4SignerAuth
import boto3

host = '' # cluster endpoint, for example: my-test-domain.us-east-1.es.amazonaws.com
port = 443
region = '' # e.g. us-west-1

credentials = boto3.Session().get_credentials()
auth = AWSV4SignerAuth(credentials, region)
index_name = 'movies'

client = OpenSearch(
    hosts = [f'{host}:{port}'],
    http_auth = auth,
    use_ssl = True,
    verify_certs = True,
    connection_class = RequestsHttpConnection
)

q = 'miller'
query = {
  'size': 5,
  'query': {
    'multi_match': {
      'query': q,
      'fields': ['title^2', 'director']
    }
  }
}

response = client.search(
    body = query,
    index = index_name
)

print('\nSearch results:')
print(response)
```

Instead of the client, you might prefer [requests](http://docs.python-requests.org/)\. The [requests\-aws4auth](https://pypi.python.org/pypi/requests-aws4auth) and [SDK for Python \(Boto3\)](https://aws.amazon.com/sdk-for-python/) packages simplify the authentication process, but are not strictly required\. From the terminal, run the following commands:

```
pip install boto3
pip install opensearch-py
pip install requests
pip install requests-aws4auth
```

The following example code establishes a secure connection to the specified OpenSearch Service domain and indexes a single document\. You must provide values for `region` and `host`\.

```
from opensearchpy import OpenSearch, RequestsHttpConnection
from requests_aws4auth import AWS4Auth
import boto3

host = '' # For example, my-test-domain.us-east-1.es.amazonaws.com
region = '' # e.g. us-west-1

service = 'es'
credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

search = OpenSearch(
    hosts = [{'host': host, 'port': 443}],
    http_auth = awsauth,
    use_ssl = True,
    verify_certs = True,
    connection_class = RequestsHttpConnection
)

document = {
    "title": "Moneyball",
    "director": "Bennett Miller",
    "year": "2011"
}

search.index(index="movies", doc_type="_doc", id="5", body=document)

print(search.get(index="movies", doc_type="_doc", id="5"))
```

If you don't want to use opensearch\-py, you can just make standard HTTP requests\. This example creates a new index with seven shards and two replicas:

```
from requests_aws4auth import AWS4Auth
import boto3
import requests

host = '' # The domain with https:// and trailing slash. For example, https://my-test-domain.us-east-1.es.amazonaws.com/
path = 'my-index' # the OpenSearch API endpoint
region = '' # For example, us-west-1

service = 'es'
credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

url = host + path

# The JSON body to accompany the request (if necessary)
payload = {
    "settings" : {
        "number_of_shards" : 7,
        "number_of_replicas" : 2
    }
}

r = requests.put(url, auth=awsauth, json=payload) # requests.get, post, and delete have similar syntax

print(r.text)
```

Rather than static credentials, you can construct an AWS4Auth instance with automatically refreshing credentials, which is suitable for long\-running applications using [AssumeRole](https://docs.aws.amazon.com/STS/latest/APIReference/API_AssumeRole.html)\. The refreshable credentials instance is used to generate valid static credentials for each request, eliminating the need to recreate the AWS4Auth instance when temporary credentials expire:

```
from requests_aws4auth import AWS4Auth
from botocore.session import Session

credentials = Session().get_credentials()

auth = AWS4Auth(region=us-west-1', service='es',
                    refreshable_credentials=credentials)
```

This next example uses the [Beautiful Soup](https://www.crummy.com/software/BeautifulSoup/bs4/doc/) library to help build a bulk file from a local directory of HTML files\. Using the same client as the first example, you can send the file to the `_bulk` API for indexing\. You could use this code as the basis for adding search functionality to a website:

```
from bs4 import BeautifulSoup
from opensearchpy import OpenSearch, RequestsHttpConnection
from requests_aws4auth import AWS4Auth
import boto3
import glob
import json

bulk_file = ''
id = 1

# This loop iterates through all HTML files in the current directory and
# indexes two things: the contents of the first h1 tag and all other text.

for html_file in glob.glob('*.htm'):

    with open(html_file) as f:
        soup = BeautifulSoup(f, 'html.parser')

    title = soup.h1.string
    body = soup.get_text(" ", strip=True)
    # If get_text() is too noisy, you can do further processing on the string.

    index = { 'title': title, 'body': body, 'link': html_file }
    # If running this script on a website, you probably need to prepend the URL and path to html_file.

    # The action_and_metadata portion of the bulk file
    bulk_file += '{ "index" : { "_index" : "site", "_type" : "_doc", "_id" : "' + str(id) + '" } }\n'

    # The optional_document portion of the bulk file
    bulk_file += json.dumps(index) + '\n'

    id += 1

host = '' # For example, my-test-domain.us-east-1.es.amazonaws.com
region = '' # e.g. us-west-1

service = 'es'
credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service)

search = OpenSearch(
    hosts = [{'host': host, 'port': 443}],
    http_auth = awsauth,
    use_ssl = True,
    verify_certs = True,
    connection_class = RequestsHttpConnection
)

search.bulk(bulk_file)

print(search.search(q='some test query'))
```

## Ruby<a name="request-signing-ruby"></a>

This first example uses the Elasticsearch Ruby client and [Faraday middleware](https://github.com/winebarrel/faraday_middleware-aws-sigv4) to perform the request signing\. Note that the latest versions of the client might include license or version checks that artificially break compatibility\. For the correct client version to use, see [Elasticsearch client compatibility](samplecode.md#client-compatibility)\. This example uses the recommended version 7\.13\.3\. 

From the terminal, run the following commands:

```
gem install elasticsearch -v 7.13.3
gem install faraday_middleware-aws-sigv4
```

This example code creates a new client, configures Faraday middleware to sign requests, and indexes a single document\. You must provide values for `full_url_and_port` and `region`\.

```
require 'elasticsearch'
require 'faraday_middleware/aws_sigv4'

full_url_and_port = '' # e.g. https://my-domain.region.es.amazonaws.com:443
index = 'ruby-index'
type = '_doc'
id = '1'
document = {
  year: 2007,
  title: '5 Centimeters per Second',
  info: {
    plot: 'Told in three interconnected segments, we follow a young man named Takaki through his life.',
    rating: 7.7
  }
}

region = '' # e.g. us-west-1
service = 'es'

client = Elasticsearch::Client.new(url: full_url_and_port) do |f|
  f.request :aws_sigv4,
    service: service,
    region: region,
    access_key_id: ENV['AWS_ACCESS_KEY_ID'],
    secret_access_key: ENV['AWS_SECRET_ACCESS_KEY'],
    session_token: ENV['AWS_SESSION_TOKEN'] # optional
end

puts client.index index: index, type: type, id: id, body: document
```

If your credentials don't work, export them at the terminal using the following commands:

```
export AWS_ACCESS_KEY_ID="your-access-key"
export AWS_SECRET_ACCESS_KEY="your-secret-key"
export AWS_SESSION_TOKEN="your-session-token"
```

This next example uses the [AWS SDK for Ruby](https://aws.amazon.com/sdk-for-ruby/) and standard Ruby libraries to send a signed HTTP request\. Like the first example, it indexes a single document\. You must provide values for host and region\.

```
require 'aws-sdk-opensearchservice'

host = '' # e.g. https://my-domain.region.es.amazonaws.com
index = 'ruby-index'
type = '_doc'
id = '2'
document = {
  year: 2007,
  title: '5 Centimeters per Second',
  info: {
    plot: 'Told in three interconnected segments, we follow a young man named Takaki through his life.',
    rating: 7.7
  }
}

service = 'es'
region = '' # e.g. us-west-1

signer = Aws::Sigv4::Signer.new(
  service: service,
  region: region,
  access_key_id: ENV['AWS_ACCESS_KEY_ID'],
  secret_access_key: ENV['AWS_SECRET_ACCESS_KEY'],
  session_token: ENV['AWS_SESSION_TOKEN']
)

signature = signer.sign_request(
  http_method: 'PUT',
  url: host + '/' + index + '/' + type + '/' + id,
  body: document.to_json
)

uri = URI(host + '/' + index + '/' + type + '/' + id)

Net::HTTP.start(uri.host, uri.port, :use_ssl => true) do |http|
  request = Net::HTTP::Put.new uri
  request.body = document.to_json
  request['Host'] = signature.headers['host']
  request['X-Amz-Date'] = signature.headers['x-amz-date']
  request['X-Amz-Security-Token'] = signature.headers['x-amz-security-token']
  request['X-Amz-Content-Sha256']= signature.headers['x-amz-content-sha256']
  request['Authorization'] = signature.headers['authorization']
  request['Content-Type'] = 'application/json'
  response = http.request request
  puts response.body
end
```

## Node<a name="request-signing-node"></a>

This example uses the [opensearch\-js](https://www.npmjs.com/package/@opensearch-project/opensearch) client for JavaScript to create an index and add a single document\. To sign the request, it first locates credentials using the [credential\-provider\-node](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/modules/_aws_sdk_credential_provider_node.html) module from version 3 of the SDK for JavaScript in Node\.js\. It then calls [aws4](https://www.npmjs.com/package/aws4) to sign the request using [Signature Version 4](https://docs.aws.amazon.com/general/latest/gr/signature-version-4.html)\. You must provide a value for `host`\.

```
const { Client, Connection } = require("@opensearch-project/opensearch");
const { defaultProvider } = require("@aws-sdk/credential-provider-node");
const aws4 = require("aws4");

var host = '' // e.g. https://my-domain.region.es.amazonaws.com

const createAwsConnector = (credentials, region) => {
  class AmazonConnection extends Connection {
      buildRequestObject(params) {
          const request = super.buildRequestObject(params);
          request.service = 'es';
          request.region = region;
          request.headers = request.headers || {};
          request.headers['host'] = request.hostname;

          return aws4.sign(request, credentials);
      }
  }
  return {
      Connection: AmazonConnection
  };
};

const getClient = async () => {
  const credentials = await defaultProvider()();
  return new Client({
      ...createAwsConnector(credentials, 'us-east-1'),
      node: host,
  });
}

async function search() {

  // Initialize the client.
  var client = await getClient();

  // Create an index.
  var index_name = "test-index";

  var response = await client.indices.create({
      index: index_name,
  });

  console.log("Creating index:");
  console.log(response.body);

  // Add a document to the index.
  var document = {
      "title": "Moneyball",
      "director": "Bennett Miller",
      "year": "2011"
  };

  var response = await client.index({
      index: index_name,
      body: document
  });

  console.log(response.body);
}

search().catch(console.log);
```

This similar example uses [aws\-opensearch\-connector](https://www.npmjs.com/package/aws-opensearch-connector) rather than aws4\. You must provide a value for `host`\.

```
const { Client } = require("@opensearch-project/opensearch");
const { defaultProvider } = require("@aws-sdk/credential-provider-node");
const createAwsOpensearchConnector = require("aws-opensearch-connector");

var host = '' // e.g. https://my-domain.region.es.amazonaws.com

const getClient = async () => {
    const awsCredentials = await defaultProvider()();
    const connector = createAwsOpensearchConnector({
        credentials: awsCredentials,
        region: process.env.AWS_REGION ?? 'us-east-1',
        getCredentials: function(cb) {
            return cb();
        }
    });
    return new Client({
        ...connector,
        node: host,
    });
}

async function search() {

    // Initialize the client.
    var client = await getClient();

    // Create an index.
    var index_name = "test-index";
    var response = await client.indices.create({
        index: index_name,
    });

    console.log("Creating index:");
    console.log(response.body);

    // Add a document to the index.
    var document = {
        "title": "Moneyball",
        "director": "Bennett Miller",
        "year": "2011"
    };

    var response = await client.index({
        index: index_name,
        body: document
    });

    console.log(response.body);
}

search().catch(console.log);
```

If you don't want to use opensearch\-js, you can just make standard HTTP requests\. This section includes examples for versions 2 and 3 of the SDK for JavaScript in Node\.js\. While version 2 is published as a single package, version 3 has a modular architecture with a separate package for each service\.

------
#### [ Version 3 ]

This example uses [version 3](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/) of the SDK for JavaScript in Node\.js\. From the terminal, run the following commands:

```
npm i @aws-sdk/protocol-http
npm i @aws-sdk/credential-provider-node
npm i @aws-sdk/signature-v4
npm i @aws-sdk/node-http-handler
npm i @aws-crypto/sha256-browser
```

This example code indexes a single document\. You must provide values for `region` and `domain`\.

```
const { HttpRequest} = require("@aws-sdk/protocol-http");
const { defaultProvider } = require("@aws-sdk/credential-provider-node");
const { SignatureV4 } = require("@aws-sdk/signature-v4");
const { NodeHttpHandler } = require("@aws-sdk/node-http-handler");
const { Sha256 } = require("@aws-crypto/sha256-browser");

var region = ''; // e.g. us-west-1
var domain = ''; // e.g. search-domain.region.es.amazonaws.com
var index = 'node-test';
var type = '_doc';
var id = '1';
var json = {
    "title": "Moneyball",
    "director": "Bennett Miller",
    "year": "2011"
};

indexDocument(json).then(() => process.exit())

async function indexDocument(document) {

    // Create the HTTP request
    var request = new HttpRequest({
        body: JSON.stringify(document),
        headers: {
            'Content-Type': 'application/json',
            'host': domain
        },
        hostname: domain,
        method: 'PUT',
        path: index + '/' + type + '/' + id
    });
        
    // Sign the request
    var signer = new SignatureV4({
        credentials: defaultProvider(),
        region: region,
        service: 'es',
        sha256: Sha256
    });
    
    var signedRequest = await signer.sign(request);

    // Send the request
    var client = new NodeHttpHandler();
    var { response } =  await client.handle(signedRequest)
    console.log(response.statusCode + ' ' + response.body.statusMessage);
    var responseBody = '';
    await new Promise((resolve) => {
      response.body.on('data', (chunk) => {
        responseBody += chunk;
      });
      response.body.on('end', () => {
        console.log('Response body: ' + responseBody);
        resolve(responseBody);
      });
    }).catch((error) => {
        console.log('Error: ' + error);
    });
};
```

------
#### [ Version 2 ]

This example uses [version 2](https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/) of the SDK for JavaScript in Node\.js\. From the terminal, run the following command:

```
npm install aws-sdk
```

This example code indexes a single document\. You must provide values for `region` and `domain`\.

```
var AWS = require('aws-sdk');

var region = ''; // e.g. us-west-1
var domain = ''; // e.g. search-domain.region.es.amazonaws.com
var index = 'node-test';
var type = '_doc';
var id = '1';
var json = {
    "title": "Moneyball",
    "director": "Bennett Miller",
    "year": "2011"
}

indexDocument(json);

function indexDocument(document) {
    var endpoint = new AWS.Endpoint(domain);
    var request = new AWS.HttpRequest(endpoint, region);

    request.method = 'PUT';
    request.path += index + '/' + type + '/' + id;
    request.body = JSON.stringify(document);
    request.headers['host'] = domain;
    request.headers['Content-Type'] = 'application/json';
    request.headers['Content-Length'] = Buffer.byteLength(request.body);

    var credentials = new AWS.EnvironmentCredentials('AWS');
    var signer = new AWS.Signers.V4(request, 'es');
    signer.addAuthorization(credentials, new Date());


    var client = new AWS.HttpClient();
    return new Promise((resolve, reject) => {
        client.handleRequest(
            request,
            null,
            (response) => {
                const {statusCode, statusMessage, headers} = response;
                let body = '';
                response.on('data', (chunk) => {
                    body += chunk;
                });
                response.on('end', () => {
                    const data = {statusCode, statusMessage, headers};
                    if (body) {
                        data.body = body;
                    }
                    resolve(data);
                console.log("Response body:" + body);
                });
            },
            (error) => {
                reject(error);
                console.log("Error:" + error)
            }
        );
    })
};
```

------

If your credentials don't work, export them at the terminal using the following commands:

```
export AWS_ACCESS_KEY_ID="your-access-key"
export AWS_SECRET_ACCESS_KEY="your-secret-key"
export AWS_SESSION_TOKEN="your-session-token"
```

## Go<a name="request-signing-go"></a>

This example uses the [AWS SDK for Go](https://aws.amazon.com/sdk-for-go/) and indexes a single document\. You must provide values for `domain` and `region`\.

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

  // Basic information for the Amazon OpenSearch Service domain
  domain := "" // e.g. https://my-domain.region.es.amazonaws.com
  index := "my-index"
  id := "1"
  endpoint := domain + "/" + index + "/" + "_doc" + "/" + id
  region := "" // e.g. us-east-1
  service := "es"

  // Sample JSON document to be included as the request body
  json := `{ "title": "Thor: Ragnarok", "director": "Taika Waititi", "year": "2017" }`
  body := strings.NewReader(json)

  // Get credentials from environment variables and create the Signature Version 4 signer
  credentials := credentials.NewEnvCredentials()
  signer := v4.NewSigner(credentials)

  // An HTTP client for sending the request
  client := &http.Client{}

  // Form the HTTP request
  req, err := http.NewRequest(http.MethodPut, endpoint, body)
  if err != nil {
    fmt.Print(err)
  }

  // You can probably infer Content-Type programmatically, but here, we just say that it's JSON
  req.Header.Add("Content-Type", "application/json")

  // Sign the request, send it, and print the response
  signer.Sign(req, body, service, region, time.Now())
  resp, err := client.Do(req)
  if err != nil {
    fmt.Print(err)
  }
  fmt.Print(resp.Status + "\n")
}
```

If your credentials don't work, export them at the terminal using the following commands:

```
export AWS_ACCESS_KEY_ID="your-access-key"
export AWS_SECRET_ACCESS_KEY="your-secret-key"
export AWS_SESSION_TOKEN="your-session-token"
```
