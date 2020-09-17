# Compressing HTTP Requests<a name="gzip"></a>

Amazon Elasticsearch Service \(Amazon ES\) domains running Elasticsearch 6\.0 or later support gzip compression for most HTTP requests and responses, which can reduce latency and conserve bandwidth\. Some Elasticsearch clients have built\-in support for gzip compression, and many programming languages have libraries that simplify the process\.

## Enabling gzip Compression<a name="gzip-enable"></a>

Not to be confused with similar Elasticsearch settings, `http_compression.enabled` is specific to Amazon ES and enables or disables gzip compression on a domain\. Domains running Elasticsearch 7\.*x* have the featured enabled by default, whereas domains running 6\.*x* have it disabled by default\.

To enable gzip compression, send the following request:

```
PUT _cluster/settings
{
  "persistent" : {
    "http_compression.enabled": true
  }
}
```

Requests to `_cluster/settings` must be uncompressed, so you might need to use a separate client or standard HTTP request to update cluster settings\.

## Required Headers<a name="gzip-headers"></a>

When including a gzip\-compressed request body, keep the standard `Content-Type: application/json` header, and add the `Content-Encoding: gzip` header\. To accept a gzip\-compressed response, add the `Accept-Encoding: gzip` header, as well\. If an Elasticsearch client supports gzip compression, it likely includes these headers automatically\.

## Sample Code \(Python 3\)<a name="gzip-code"></a>

The following sample uses [elasticsearch\-py](https://elasticsearch-py.readthedocs.io) to perform the compression and send the request\. This code signs the request using your IAM credentials\.

```
from elasticsearch import Elasticsearch, RequestsHttpConnection
from requests_aws4auth import AWS4Auth
import boto3

host = '' # e.g. my-test-domain.us-east-1.es.amazonaws.com
region = '' # e.g. us-west-1
service = 'es'
credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

# Create the client.
es = Elasticsearch(
    hosts = [{'host': host, 'port': 443}],
    http_auth = awsauth,
    use_ssl = True,
    verify_certs = True,
    http_compress = True, # enables gzip compression for request bodies
    connection_class = RequestsHttpConnection
)

document = {
  "title": "Moneyball",
  "director": "Bennett Miller",
  "year": "2011"
}

# Send the request.
print(es.index(index='movies', id='1', body=document, refresh=True))

# # Older versions of the client might require doc_type.
# print(es.index(index='movies', doc_type='_doc', id='1', body=document, refresh=True))
```

Alternately, you can specify the proper headers, compress the request body yourself, and use a standard HTTP library like [Requests](https://2.python-requests.org)\. This code signs the request using HTTP basic credentials, which your domain might support if you use [fine\-grained access control](fgac.md)\.

```
import requests
import gzip
import json

base_url = '' # The domain with https:// and a trailing slash. For example, https://my-test-domain.us-east-1.es.amazonaws.com/
auth = ('master-user', 'master-user-password') # For testing only. Don't store credentials in code.

headers = {'Accept-Encoding': 'gzip', 'Content-Type': 'application/json',
           'Content-Encoding': 'gzip'}

document = {
  "title": "Moneyball",
  "director": "Bennett Miller",
  "year": "2011"
}

# Compress the document.
compressed_document = gzip.compress(json.dumps(document).encode())

# Send the request.
path = 'movies/_doc?refresh=true'
url = base_url + path
response = requests.post(url, auth=auth, headers=headers, data=compressed_document)
print(response.status_code)
print(response.text)
```