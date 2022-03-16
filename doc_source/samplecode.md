# Sample code for Amazon OpenSearch Service<a name="samplecode"></a>

This chapter contains common sample code for working with Amazon OpenSearch Service: HTTP request signing in a variety of programming languages, compressing HTTP request bodies, and using the AWS SDKs to create domains\.

**Topics**
+ [Elasticsearch client compatibility](#client-compatibility)
+ [Signing HTTP requests to Amazon OpenSearch Service](request-signing.md)
+ [Compressing HTTP requests in Amazon OpenSearch Service](gzip.md)
+ [Using the AWS SDKs to interact with Amazon OpenSearch Service](configuration-samples.md)

## Elasticsearch client compatibility<a name="client-compatibility"></a>

The latest versions of the Elasticsearch clients might include license or version checks that artificially break compatibility\. The following table includes recommendations around which versions of those clients to use for best compatibility with OpenSearch Service\.

**Important**  
These client versions are out of date and are not updated with the latest dependencies, including Log4j\. We highly recommend using the OpenSearch versions of the clients when possible\.


| Client | Recommended version | 
| --- | --- | 
| Java low\-level REST client |  7\.13\.4  | 
| Java high\-level REST client |  7\.13\.4  | 
| Python Elasticsearch client |  7\.13\.4  | 
|  Ruby Elasticsearch client  | 7\.13\.3 | 
| Node\.js Elasticsearch client |  7\.13\.0  | 