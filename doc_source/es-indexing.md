# Indexing Data in Amazon Elasticsearch Service<a name="es-indexing"></a>

Because Elasticsearch uses a REST API, numerous methods exist for indexing documents\. You can use standard clients like [curl](https://curl.haxx.se/) or any programming language that can send HTTP requests\. To further simplify the process of interacting with it, Elasticsearch has [clients for many programming languages](https://www.elastic.co/guide/en/elasticsearch/client/index.html)\. Advanced users can skip directly to [Programmatic Indexing](#es-indexing-programmatic)\.

For situations in which new data arrives incrementally \(for example, customer orders from a small business\), you might use the `_index` API to index documents as they arrive\. For situations in which the flow of data is less frequent \(for example, weekly updates to a marketing website\), you might prefer to generate a file and send it to the `_bulk` API\. For large numbers of documents, lumping requests together and using the `_bulk` API offers superior performance\. If your documents are enormous, however, you might need to index them individually using the `_index` API\.

For information about integrating data from other AWS services, see [Loading Streaming Data into Amazon Elasticsearch Service](es-aws-integrations.md)\.

## Introduction to Indexing<a name="es-indexing-intro"></a>

Before you can search data, you must *index* it\. Indexing is the method by which search engines organize data for fast retrieval\. The resulting structure is called, fittingly, an index\.

In Elasticsearch, the basic unit of data is a JSON *document*\. Within an index, Elasticsearch organizes documents into *types* \(arbitrary data categories that you define\) and identifies them using a unique *ID*\.

A request to the `_index` API looks like the following:

```
PUT elasticsearch_domain/index/type/id
{ "A JSON": "document" }
```

A request to the `_bulk` API looks a little different, because you specify the index, type, and ID in the bulk data:

```
POST elasticsearch_domain/_bulk
{ "index": { "_index" : "index", "_type" : "type", "_id" : "id" } }
{ "A JSON": "document" }
```

Bulk data must conform to a specific format, which requires a newline character \(`\n`\) at the end of every line, including the last line\. This is the basic format:

```
action_and_metadata\n
optional_document\n
action_and_metadata\n
optional_document\n
...
```

For a short sample file, see [Step 2: Upload Data to an Amazon ES Domain for Indexing](es-gsg-upload-data.md)\.

Elasticsearch features [automatic index creation](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html#index-creation) when you add a document to an index that doesn't already exist\. It also features [automatic ID generation](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html#_automatic_id_generation) if you don't specify an ID in the request\. This simple example automatically creates the `movies` index, establishes the document type of `movie`, indexes the document, and assigns it a unique ID:

```
curl -XPOST elasticsearch_domain/movies/movie -d '{"title": "Spirited Away"}' -H 'Content-Type: application/json'
```

**Important**  
To use automatic ID generation, you must use the `POST` method instead of `PUT`\.

To verify that the document exists, you can perform the following search:

```
curl -XGET elasticsearch_domain/movies/_search?pretty
```

The response should contain the following:

```
"hits" : {
  "total" : 1,
  "max_score" : 1.0,
  "hits" : [
    {
      "_index" : "movies",
      "_type" : "movie",
      "_id" : "AV4WaTnYxBoJaZkSFeX9",
      "_score" : 1.0,
      "_source" : {
        "title" : "Spirited Away"
      }
    }
  ]
}
```

Automatic ID generation has a clear downside: because the indexing code didn't specify a document ID, you can't easily update the document at a later time\. To specify an ID of `7`, use the following request:

```
curl -XPUT elasticsearch_domain/movies/movie/7 -d '{"title": "Spirited Away"}' -H 'Content-Type: application/json'
```

Indices default to five primary shards and one replica\. If you want to specify non\-default settings, create the index before adding documents:

```
curl -XPUT elasticsearch_domain/movies -d '{"settings": {"number_of_shards": 6, "number_of_replicas": 2}}' -H 'Content-Type: application/json'
```

**Note**  
Requests using curl are unauthenticated and rely on an IP\-based access policy\. For examples of signed requests, see [Programmatic Indexing](#es-indexing-programmatic)\.

Elasticsearch indices have the following naming restrictions:
+ All letters must be lowercase\.
+ Index names cannot begin with `_` or `-`\.
+ Index names cannot contain spaces, commas, `"`, `*`, `+`, `/`, `\`, `|`, `?`, `>`, or `<`\.

## Programmatic Indexing<a name="es-indexing-programmatic"></a>

This section includes examples of how to use popular [Elasticsearch clients](https://www.elastic.co/guide/en/elasticsearch/client/index.html) to index documents\.

### Python<a name="es-indexing-programmatic-python"></a>

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

### Java<a name="es-indexing-programmatic-java"></a>

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