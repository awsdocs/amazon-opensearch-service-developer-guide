# Programmatic Indexing<a name="es-indexing-programmatic"></a>

This section includes examples of how to use popular [Elasticsearch clients](https://www.elastic.co/guide/en/elasticsearch/client/index.html) and standard HTTP requests to index documents\.

## Python<a name="es-indexing-programmatic-python"></a>

You can install [elasticsearch\-py](https://www.elastic.co/guide/en/elasticsearch/client/python-api/current/index.html), the official Elasticsearch client for Python, using [pip](https://pypi.python.org/pypi/pip)\. Instead of the client, you might prefer [requests](http://docs.python-requests.org/)\. The [requests\-aws4auth](https://pypi.python.org/pypi/requests-aws4auth) package simplifies the authentication process, but is not strictly required\. From the terminal, run the following commands:

```
pip install elasticsearch
pip install requests
pip install requests-aws4auth
```

The following sample code establishes a secure connection to the specified Amazon ES domain and indexes a single document using the `_index` API\. You must provide values for `AWS_ACCESS_KEY`, `AWS_SECRET_KEY`, `region`, and `host`:

```
from elasticsearch import Elasticsearch, RequestsHttpConnection
from requests_aws4auth import AWS4Auth

AWS_ACCESS_KEY = ''
AWS_SECRET_KEY = ''
region = '' # For example, us-east-1
service = 'es'

awsauth = AWS4Auth(AWS_ACCESS_KEY, AWS_SECRET_KEY, region, service)

host = '' # For example, my-test-domain.us-east-1.es.amazonaws.com

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

es.index(index="movies", doc_type="movie", id="5", body=document)

print(es.get(index="movies", doc_type="movie", id="5"))
```

**Important**  
These samples are for testing purposes\. We do not recommend storing your AWS access key and AWS secret key directly in code\.

If you don't want to use the `elasticsearch.py` client, you can just make standard HTTP requests\. This sample creates a new index with seven shards and two replicas:

```
import requests
from requests_aws4auth import AWS4Auth

AWS_ACCESS_KEY = ''
AWS_SECRET_KEY = ''
region = '' # For example, us-east-1
service = 'es'

awsauth = AWS4Auth(AWS_ACCESS_KEY, AWS_SECRET_KEY, region, service)

host = '' # The domain with https:// and trailing slash. For example, https://my-test-domain.us-east-1.es.amazonaws.com/
path = 'my-index' # the Elasticsearch API endpoint

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
    bulk_file += '{ "index" : { "_index" : "site", "_type" : "page", "_id" : "' + str(id) + '" } }\n'

    # The optional_document portion of the bulk file
    bulk_file += json.dumps(index) + '\n'

    id += 1

AWS_ACCESS_KEY = ''
AWS_SECRET_KEY = ''
region = '' # For example, us-east-1
service = 'es'

awsauth = AWS4Auth(AWS_ACCESS_KEY, AWS_SECRET_KEY, region, service)

host = '' # For example, my-test-domain.us-east-1.es.amazonaws.com

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

## Java<a name="es-indexing-programmatic-java"></a>

This first example uses the [Elasticsearch low\-level Java REST Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/index.html), which you must [configure as a dependency](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/_maven_repository.html)\. The request is unauthenticated and relies on an IP\-based access policy\. You must provide a value for `host`:

```
import java.io.IOException;
import java.util.Collections;

import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.http.entity.ContentType;
import org.elasticsearch.client.http.HttpEntity;
import org.elasticsearch.client.http.HttpHost;
import org.elasticsearch.client.http.nio.entity.NStringEntity;

public class JavaRestClientExample {

    public static void main(String[] args) throws IOException {

        String host = ""; // For example, my-test-domain.us-east-1.es.amazonaws.com
        String index = "movies";
        String type = "movie";
        String id = "6";

        String json = "{" + "\"title\":\"Walk the Line\"," + "\"director\":\"James Mangold\"," + "\"year\":\"2005\""
            + "}";

        RestClient client = RestClient.builder(new HttpHost(host, 443, "https")).build();

        HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);

        Response response = client.performRequest("PUT", "/" + index + "/" + type + "/" + id,
            Collections.<String, String>emptyMap(), entity);

        System.out.println(response.toString());
    }
}
```

The easiest way of sending a signed request is to use the [AWS Request Signing Interceptor](https://github.com/awslabs/aws-request-signing-apache-interceptor)\. The repository contains some samples to help you get started\. The following example uses the [Elasticsearch low\-level Java REST client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/index.html) to perform two unrelated actions: registering a snapshot repository and indexing a document\.

```
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.AWSRequestSigningApacheInterceptor;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class AmazonElasticsearchServiceSample {

    private static String serviceName = "es";
    private static String region = "us-west-1";
    private static String aesEndpoint = "https://domain.us-west-1.es.amazonaws.com"; 

    private static String payload = "{ \"type\": \"s3\", \"settings\": { \"bucket\": \"your-bucket\", \"region\": \"us-west-1\", \"role_arn\": \"arn:aws:iam::123456789012:role/TheServiceRole\" } }";
    private static String snapshotPath = "/_snapshot/my-snapshot-repo";

    private static String sampleDocument = "{" + "\"title\":\"Walk the Line\"," + "\"director\":\"James Mangold\"," + "\"year\":\"2005\"}";
    private static String indexingPath = "/my-index/my-type";
    
    static final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();
    
    public static void main(String[] args) throws IOException {
        RestClient esClient = esClient(serviceName, region);

        // Register a snapshot repository
        HttpEntity entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
        Map<String, String> params = Collections.emptyMap();
        Response response = esClient.performRequest("PUT", snapshotPath, params, entity);
        System.out.println(response.toString());

        // Index a document
        entity = new NStringEntity(sampleDocument, ContentType.APPLICATION_JSON);
        String id = "1";
        response = esClient.performRequest("PUT", indexingPath + "/" + id, params, entity);
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

## Ruby<a name="es-indexing-programmatic-ruby"></a>

This first example uses the [Elasticsearch Ruby client](https://www.elastic.co/guide/en/elasticsearch/client/ruby-api/current/index.html) and [Faraday middleware](https://github.com/winebarrel/faraday_middleware-aws-sigv4) to perform the request signing\. From the terminal, run the following commands:

```
gem install elasticsearch
gem install faraday_middleware-aws-sigv4
```

This sample code creates a new Elasticsearch client, configures Faraday middleware to sign requests, and indexes a single document\. You must provide values for `host` and `region`\.

```
require 'elasticsearch'
require 'faraday_middleware/aws_sigv4'

host = '' # e.g. https://my-domain.region.es.com
index = 'ruby-index'
type = 'ruby-type'
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

client = Elasticsearch::Client.new(url: host) do |f|
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
export AWS_SESSION_TOKEN=""your-session-token"
```

This next example uses the [AWS SDK for Ruby](https://aws.amazon.com/sdk-for-ruby/) and standard Ruby libraries to send a signed HTTP request\. Like the first example, it indexes a single document\. You must provide values for host and region\.

```
require 'aws-sdk-elasticsearchservice'

host = '' # e.g. https://my-domain.region.es.com
index = 'ruby-index'
type = 'ruby-type'
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

## Node<a name="es-indexing-programmatic-node"></a>

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
var type = 'node-type';
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
export AWS_SESSION_TOKEN=""your-session-token"
```