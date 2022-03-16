# Signing HTTP requests to Amazon OpenSearch Service<a name="request-signing"></a>

This section includes examples of how to send signed HTTP requests to Amazon OpenSearch Service using Elasticsearch and OpenSearch clients and other common libraries\. These code samples are for interacting with the OpenSearch APIs, such as `_index`, `_bulk`, and `_snapshot`\. If your domain access policy includes IAM users or roles \(or you use an IAM master user with [fine\-grained access control](fgac.md)\), you must sign requests to the OpenSearch APIs with your IAM credentials\. 

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

The easiest way of sending a signed request is to use the [Amazon Web Services request signing interceptor](https://github.com/awslabs/aws-request-signing-apache-interceptor)\. The repository contains some samples to help you get started, or you can [download a sample project for OpenSearch Service on GitHub](https://github.com/awsdocs/amazon-opensearch-service-developer-guide/blob/master/sample_code/java/amazon-opensearch-docs-sample-client.zip)\.

The following example uses the [opensearch\-java](https://github.com/opensearch-project/opensearch-java) low\-level Java REST client to perform two unrelated actions: registering a snapshot repository and indexing a document\. You must provide values for `region` and `host`\.

```
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.AWSRequestSigningApacheInterceptor;
import java.io.IOException;

public class AmazonOpenSearchServiceSample {

    private static String serviceName = "es";
    private static String region = "";
    private static String host = ""; // e.g. https://search-mydomain.us-west-1.es.amazonaws.com

    private static String payload = "{ \"type\": \"s3\", \"settings\": { \"bucket\": \"your-bucket\", \"region\": \"us-west-1\", \"role_arn\": \"arn:aws:iam::123456789012:role/TheServiceRole\" } }";
    private static String snapshotPath = "/_snapshot/my-snapshot-repo";

    private static String sampleDocument = "{" + "\"title\":\"Walk the Line\"," + "\"director\":\"James Mangold\"," + "\"year\":\"2005\"}";
    private static String indexingPath = "/my-index/_doc";

    static final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

    public static void main(String[] args) throws IOException {
        RestClient searchClient = searchClient(serviceName, region);

        // Register a snapshot repository
        HttpEntity entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
        Request request = new Request("PUT", snapshotPath);
        request.setEntity(entity);
        // request.addParameter(name, value); // optional parameters
        Response response = searchClient.performRequest(request);
        System.out.println(response.toString());

        // Index a document
        entity = new NStringEntity(sampleDocument, ContentType.APPLICATION_JSON);
        String id = "1";
        request = new Request("PUT", indexingPath + "/" + id);
        request.setEntity(entity);

        // Using a String instead of an HttpEntity sets Content-Type to application/json automatically.
        // request.setJsonEntity(sampleDocument);

        response = searchClient.performRequest(request);
        System.out.println(response.toString());
    }

    // Adds the interceptor to the OpenSearch REST client
    public static RestClient searchClient(String serviceName, String region) {
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(serviceName);
        signer.setRegionName(region);
        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider);
        return RestClient.builder(HttpHost.create(host)).setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)).build();
    }
}
```

If you prefer the high\-level REST client, which offers most of the same features and simpler code, try the following sample, which also uses the [Amazon Web Services Request Signing Interceptor](https://github.com/awslabs/aws-request-signing-apache-interceptor):

```
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.AWSRequestSigningApacheInterceptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AmazonOpenSearchServiceSample {

    private static String serviceName = "es";
    private static String region = ""; // e.g. us-east-1
    private static String host = ""; // e.g. https://search-mydomain.us-west-1.es.amazonaws.com
    private static String index = "my-index";
    private static String type = "_doc";
    private static String id = "1";

    static final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

    public static void main(String[] args) throws IOException {
        RestHighLevelClient searchClient = searchClient(serviceName, region);

        // Create the document as a hash map
        Map<String, Object> document = new HashMap<>();
        document.put("title", "Walk the Line");
        document.put("director", "James Mangold");
        document.put("year", "2005");

        // Form the indexing request, send it, and print the response
        IndexRequest request = new IndexRequest(index, type, id).source(document);
        IndexResponse response = searchClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }

    // Adds the interceptor to the OpenSearch REST client
    public static RestHighLevelClient searchClient(String serviceName, String region) {
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(serviceName);
        signer.setRegionName(region);
        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider);
        return new RestHighLevelClient(RestClient.builder(HttpHost.create(host)).setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));
    }
}
```

**Tip**  
Both signed samples use the default credential chain\. Run `aws configure` using the AWS CLI to set your credentials\.

## Python<a name="request-signing-python"></a>

This sample uses the [opensearch\-py](https://pypi.org/project/opensearch-py/) client for Python, which you can install using [pip](https://pypi.python.org/pypi/pip)\.

Instead of the client, you might prefer [requests](http://docs.python-requests.org/)\. The [requests\-aws4auth](https://pypi.python.org/pypi/requests-aws4auth) and [SDK for Python \(Boto3\)](https://aws.amazon.com/sdk-for-python/) packages simplify the authentication process, but are not strictly required\. From the terminal, run the following commands:

```
pip install boto3
pip install opensearch-py
pip install requests
pip install requests-aws4auth
```

The following sample code establishes a secure connection to the specified OpenSearch Service domain and indexes a single document\. You must provide values for `region` and `host`\.

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

If you don't want to use opensearch\-py, you can just make standard HTTP requests\. This sample creates a new index with seven shards and two replicas:

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

This sample code creates a new client, configures Faraday middleware to sign requests, and indexes a single document\. You must provide values for `full_url_and_port` and `region`\.

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

This section includes examples for versions 2 and 3 of the SDK for JavaScript in Node\.js\. While version 2 is published as a single package, version 3 has a modular architecture with a separate package for each service\.

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

This sample code indexes a single document\. You must provide values for `region` and `domain`\.

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
    await new Promise(() => {
      response.body.on('data', (chunk) => {
        responseBody += chunk;
      });
      response.body.on('end', () => {
        console.log('Response body: ' + responseBody);
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

This sample code indexes a single document\. You must provide values for `region` and `domain`\.

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