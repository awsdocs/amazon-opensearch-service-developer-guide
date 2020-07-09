# Signing HTTP Requests to Amazon Elasticsearch Service<a name="es-request-signing"></a>

This section includes examples of how to send signed HTTP requests to Amazon Elasticsearch Service using Elasticsearch clients and other common libraries\. These code samples are for interacting with the Elasticsearch APIs, such as `_index`, `_bulk`, and `_snapshot`\. If your domain access policy includes IAM users or roles \(or you use an IAM master user with [fine\-grained access control](fgac.md)\), you must sign requests to the Elasticsearch APIs with your IAM credentials\.

**Important**  
For examples of how to interact with the configuration API, including operations like creating, updating, and deleting Amazon ES domains, see [Using the AWS SDKs with Amazon Elasticsearch Service](es-configuration-samples.md)\.

**Topics**
+ [Java](#es-request-signing-java)
+ [Python](#es-request-signing-python)
+ [Ruby](#es-request-signing-ruby)
+ [Node](#es-request-signing-node)
+ [Go](#es-request-signing-go)

## Java<a name="es-request-signing-java"></a>

The easiest way of sending a signed request is to use the [AWS Request Signing Interceptor](https://github.com/awslabs/aws-request-signing-apache-interceptor)\. The repository contains some samples to help you get started, or you can [download a sample project for Amazon ES on GitHub](https://github.com/awsdocs/amazon-elasticsearch-service-developer-guide/blob/master/sample_code/java/amazon-es-docs-sample-client.zip)\.

The following example uses the Elasticsearch low\-level Java REST client to perform two unrelated actions: registering a snapshot repository and indexing a document\. You must provide values for `region` and `host`\.

```
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.AWSRequestSigningApacheInterceptor;
import java.io.IOException;

public class AmazonElasticsearchServiceSample {

    private static String serviceName = "es";
    private static String region = "us-west-1";
    private static String aesEndpoint = "https://domain.us-west-1.es.amazonaws.com";

    private static String payload = "{ \"type\": \"s3\", \"settings\": { \"bucket\": \"your-bucket\", \"region\": \"us-west-1\", \"role_arn\": \"arn:aws:iam::123456789012:role/TheServiceRole\" } }";
    private static String snapshotPath = "/_snapshot/my-snapshot-repo";

    private static String sampleDocument = "{" + "\"title\":\"Walk the Line\"," + "\"director\":\"James Mangold\"," + "\"year\":\"2005\"}";
    private static String indexingPath = "/my-index/_doc";

    static final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

    public static void main(String[] args) throws IOException {
        RestClient esClient = esClient(serviceName, region);

        // Register a snapshot repository
        HttpEntity entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
        Request request = new Request("PUT", snapshotPath);
        request.setEntity(entity);
        // request.addParameter(name, value); // optional parameters
        Response response = esClient.performRequest(request);
        System.out.println(response.toString());
        
        // Index a document
        entity = new NStringEntity(sampleDocument, ContentType.APPLICATION_JSON);
        String id = "1";
        request = new Request("PUT", indexingPath + "/" + id);
        request.setEntity(entity);
        
        // Using a String instead of an HttpEntity sets Content-Type to application/json automatically.
        // request.setJsonEntity(sampleDocument);
        
        response = esClient.performRequest(request);
        System.out.println(response.toString());
    }

    // Adds the interceptor to the ES REST client
    public static RestClient esClient(String serviceName, String region) {
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(serviceName);
        signer.setRegionName(region);
        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider);
        return RestClient.builder(HttpHost.create(aesEndpoint)).setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)).build();
    }
}
```

If you prefer the high\-level REST client, which offers most of the same features and simpler code, try the following sample, which also uses the [AWS Request Signing Interceptor](https://github.com/awslabs/aws-request-signing-apache-interceptor):

```
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.AWSRequestSigningApacheInterceptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AmazonElasticsearchServiceSample {

    private static String serviceName = "es";
    private static String region = "us-west-1";
    private static String aesEndpoint = ""; // e.g. https://search-mydomain.us-west-1.es.amazonaws.com
    private static String index = "my-index";
    private static String type = "_doc";
    private static String id = "1";
    
    static final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();
    
    public static void main(String[] args) throws IOException {
        RestHighLevelClient esClient = esClient(serviceName, region);

        // Create the document as a hash map
        Map<String, Object> document = new HashMap<>();
        document.put("title", "Walk the Line");
        document.put("director", "James Mangold");
        document.put("year", "2005");

        // Form the indexing request, send it, and print the response
        IndexRequest request = new IndexRequest(index, type, id).source(document);
        IndexResponse response = esClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }
    
    // Adds the interceptor to the ES REST client
    public static RestHighLevelClient esClient(String serviceName, String region) {
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(serviceName);
        signer.setRegionName(region);
        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider);
        return new RestHighLevelClient(RestClient.builder(HttpHost.create(aesEndpoint)).setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));
    }
}
```

**Tip**  
Both signed samples use the default credential chain\. Run `aws configure` using the AWS CLI to set your credentials\.

## Python<a name="es-request-signing-python"></a>

You can install [elasticsearch\-py](https://elasticsearch-py.readthedocs.io/), an Elasticsearch client for Python, using [pip](https://pypi.python.org/pypi/pip)\. Instead of the client, you might prefer [requests](http://docs.python-requests.org/)\. The [requests\-aws4auth](https://pypi.python.org/pypi/requests-aws4auth) and [SDK for Python \(Boto3\)](https://aws.amazon.com/sdk-for-python/) packages simplify the authentication process, but are not strictly required\. From the terminal, run the following commands:

```
pip install boto3
pip install elasticsearch
pip install requests
pip install requests-aws4auth
```

The following sample code establishes a secure connection to the specified Amazon ES domain and indexes a single document\. You must provide values for `region` and `host`\.

```
from elasticsearch import Elasticsearch, RequestsHttpConnection
from requests_aws4auth import AWS4Auth
import boto3

host = '' # For example, my-test-domain.us-east-1.es.amazonaws.com
region = '' # e.g. us-west-1

service = 'es'
credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

es = Elasticsearch(
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

es.index(index="movies", doc_type="_doc", id="5", body=document)

print(es.get(index="movies", doc_type="_doc", id="5"))
```

If you don't want to use elasticsearch\-py, you can just make standard HTTP requests\. This sample creates a new index with seven shards and two replicas:

```
from requests_aws4auth import AWS4Auth
import boto3
import requests

host = '' # The domain with https:// and trailing slash. For example, https://my-test-domain.us-east-1.es.amazonaws.com/
path = 'my-index' # the Elasticsearch API endpoint
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
from elasticsearch import Elasticsearch, RequestsHttpConnection
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

es = Elasticsearch(
    hosts = [{'host': host, 'port': 443}],
    http_auth = awsauth,
    use_ssl = True,
    verify_certs = True,
    connection_class = RequestsHttpConnection
)

es.bulk(bulk_file)

print(es.search(q='some test query'))
```

## Ruby<a name="es-request-signing-ruby"></a>

This first example uses the Elasticsearch Ruby client and [Faraday middleware](https://github.com/winebarrel/faraday_middleware-aws-sigv4) to perform the request signing\. From the terminal, run the following commands:

```
gem install elasticsearch
gem install faraday_middleware-aws-sigv4
```

This sample code creates a new Elasticsearch client, configures Faraday middleware to sign requests, and indexes a single document\. You must provide values for `full_url_and_port` and `region`\.

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
require 'aws-sdk-elasticsearchservice'

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

## Node<a name="es-request-signing-node"></a>

This example uses the [SDK for JavaScript in Node\.js](https://aws.amazon.com/sdk-for-node-js/)\. From the terminal, run the following commands:

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
  // Content-Length is only needed for DELETE requests that include a request
  // body, but including it for all requests doesn't seem to hurt anything.
  request.headers['Content-Length'] = Buffer.byteLength(request.body);

  var credentials = new AWS.EnvironmentCredentials('AWS');
  var signer = new AWS.Signers.V4(request, 'es');
  signer.addAuthorization(credentials, new Date());

  var client = new AWS.HttpClient();
  client.handleRequest(request, null, function(response) {
    console.log(response.statusCode + ' ' + response.statusMessage);
    var responseBody = '';
    response.on('data', function (chunk) {
      responseBody += chunk;
    });
    response.on('end', function (chunk) {
      console.log('Response body: ' + responseBody);
    });
  }, function(error) {
    console.log('Error: ' + error);
  });
}
```

If your credentials don't work, export them at the terminal using the following commands:

```
export AWS_ACCESS_KEY_ID="your-access-key"
export AWS_SECRET_ACCESS_KEY="your-secret-key"
export AWS_SESSION_TOKEN="your-session-token"
```

## Go<a name="es-request-signing-go"></a>

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

  // Basic information for the Amazon Elasticsearch Service domain
  domain := "" // e.g. https://my-domain.region.es.amazonaws.com
  index := "my-index"
  id := "1"
  endpoint := domain + "/" + index + "/" + "_doc" + "/" + id
  region := "" // e.g. us-east-1
  service := "es"

  // Sample JSON document to be included as the request body
  json := `{ "title": "Thor: Ragnarok", "director": "Taika Waititi", "year": "2017" }`
  body := strings.NewReader(json)

  // Get credentials from environment variables and create the AWS Signature Version 4 signer
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