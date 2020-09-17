# Creating a Search Application with Amazon Elasticsearch Service<a name="search-example"></a>

A common way to create a search application with Amazon ES is to use web forms to send user queries to a server\. Then you can authorize the server to call the Elasticsearch APIs directly and have the server send requests to Amazon ES\.

If you want to write client\-side code that doesn't rely on a server, however, you should compensate for the security and performance risks\. Allowing unsigned, public access to the Elasticsearch APIs is inadvisable\. Users might access unsecured endpoints or impact cluster performance through overly broad queries \(or too many queries\)\.

This chapter presents a solution: use Amazon API Gateway to restrict users to a subset of the Elasticsearch APIs and AWS Lambda to sign requests from API Gateway to Amazon ES\.

**Note**  
Standard API Gateway and Lambda pricing applies, but within the limited usage of this tutorial, costs should be negligible\.

## Step 1: Index Sample Data<a name="search-example-index"></a>

A prerequisite for these steps is an Amazon ES domain\. Download [sample\-movies\.zip](samples/sample-movies.zip), unzip it, and use the `_bulk` API to add the 5,000 documents to the `movies` index:

```
POST https://search-my-domain.us-west-1.es.amazonaws.com/_bulk
{ "index": { "_index": "movies", "_type": "movie", "_id": "tt1979320" } }
{"fields":{"directors":["Ron Howard"],"release_date":"2013-09-02T00:00:00Z","rating":8.3,"genres":["Action","Biography","Drama","Sport"],"image_url":"http://ia.media-imdb.com/images/M/MV5BMTQyMDE0MTY0OV5BMl5BanBnXkFtZTcwMjI2OTI0OQ@@._V1_SX400_.jpg","plot":"A re-creation of the merciless 1970s rivalry between Formula One rivals James Hunt and Niki Lauda.","title":"Rush","rank":2,"running_time_secs":7380,"actors":["Daniel Brühl","Chris Hemsworth","Olivia Wilde"],"year":2013},"id":"tt1979320","type":"add"}
{ "index": { "_index": "movies", "_type": "movie", "_id": "tt1951264" } }
{"fields":{"directors":["Francis Lawrence"],"release_date":"2013-11-11T00:00:00Z","genres":["Action","Adventure","Sci-Fi","Thriller"],"image_url":"http://ia.media-imdb.com/images/M/MV5BMTAyMjQ3OTAxMzNeQTJeQWpwZ15BbWU4MDU0NzA1MzAx._V1_SX400_.jpg","plot":"Katniss Everdeen and Peeta Mellark become targets of the Capitol after their victory in the 74th Hunger Games sparks a rebellion in the Districts of Panem.","title":"The Hunger Games: Catching Fire","rank":4,"running_time_secs":8760,"actors":["Jennifer Lawrence","Josh Hutcherson","Liam Hemsworth"],"year":2013},"id":"tt1951264","type":"add"}
...
```

To learn more, see [Indexing Data in Amazon Elasticsearch Service](es-indexing.md)\.

## Step 2: Create the API<a name="search-example-api"></a>

Using API Gateway to create a more limited API simplifies the process of interacting with the Elasticsearch `_search` API\. It also lets you enable security features like Amazon Cognito authentication and request throttling\. Create and deploy an API according to the following table\.


****  

| Setting | Values | 
| --- | --- | 
| API |  Type: New API **Settings** API name: search\-es\-api Description: Public API for searching an Amazon Elasticsearch Service domain Endpoint type: Regional  | 
| Resource |  `/`  | 
| HTTP Method |  `GET`  | 
| Method Request |  **Settings** Authorization: none Request validator: Validate query string parameters and headers API key required: false **URL Query String Parameters** Name: q Required: Yes  | 
| Integration Request |  Integration type: Lambda function Use Lambda proxy integration: Yes Lambda Region: *us\-west\-1* Lambda function: search\-es\-lambda Invoke with caller credentials: No Credentials cache: Do not add caller credentials to cache key Use default timeout: Yes  | 
| Stage |  Name: search\-es\-api\-test **Default Method Throttling** Enable throttling: Yes Rate: 1000 Burst: 500  | 

These settings configure an API that has only one method: a `GET` request to the endpoint root \(`https://some-id.execute-api.us-west-1.amazonaws.com/search-es-api-test`\)\. The request requires a single parameter \(`q`\), the query string to search for\. When called, the method passes the request to Lambda, which runs the `search-es-lambda` function\. For more information, see [Creating an API in Amazon API Gateway](https://docs.aws.amazon.com/apigateway/latest/developerguide/how-to-create-api.html) and [Deploying an API in Amazon API Gateway](https://docs.aws.amazon.com/apigateway/latest/developerguide/how-to-deploy-api.html)\.

## Step 3: Create the Lambda Function<a name="search-example-lambda"></a>

In this solution, API Gateway passes requests to the following Python 2\.7 Lambda function, which queries Amazon ES and returns results:

```
import boto3
import json
import requests
from requests_aws4auth import AWS4Auth

region = '' # For example, us-west-1
service = 'es'
credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

host = '' # For example, search-mydomain-id.us-west-1.es.amazonaws.com
index = 'movies'
url = 'https://' + host + '/' + index + '/_search'

# Lambda execution starts here
def handler(event, context):

    # Put the user query into the query DSL for more accurate search results.
    # Note that certain fields are boosted (^).
    query = {
        "size": 25,
        "query": {
            "multi_match": {
                "query": event['queryStringParameters']['q'],
                "fields": ["fields.title^4", "fields.plot^2", "fields.actors", "fields.directors"]
            }
        }
    }

    # ES 6.x requires an explicit Content-Type header
    headers = { "Content-Type": "application/json" }

    # Make the signed HTTP request
    r = requests.get(url, auth=awsauth, headers=headers, data=json.dumps(query))

    # Create the response and add some extra content to support CORS
    response = {
        "statusCode": 200,
        "headers": {
            "Access-Control-Allow-Origin": '*'
        },
        "isBase64Encoded": False
    }

    # Add the search results to the response
    response['body'] = r.text
    return response
```

The function must have the following trigger\.


****  

| Trigger | API | Deployment Stage | Security | 
| --- | --- | --- | --- | 
| API Gateway | search\-es\-api | search\-es\-api\-test | Open | 

Because this sample function uses external libraries, you must create a deployment package and upload it to Lambda for the code to work\. For more information about creating Lambda functions and deployment packages, see [Creating a Deployment Package \(Python\)](https://docs.aws.amazon.com/lambda/latest/dg/lambda-python-how-to-create-deployment-package.html) in the *AWS Lambda Developer Guide* and [Creating the Lambda Deployment Package](es-aws-integrations.md#es-aws-integrations-s3-lambda-es-deployment-package) in this guide\.

## Step 4: Modify the Domain Access Policy<a name="search-example-perms"></a>

Your Amazon ES domain must allow the Lambda function to make `GET` requests to the `movies` index\. The following policy provides `search-es-role` \(created through Lambda\) access to the `movies` index:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::123456789012:role/service-role/search-es-role"
      },
      "Action": "es:ESHttpGet",
      "Resource": "arn:aws:es:us-west-1:123456789012:domain/web/movies/_search"
    }
  ]
}
```

For more information, see [Configuring Access Policies](es-createupdatedomains.md#es-createdomain-configure-access-policies)\.

## Step 5: Test the Web Application<a name="search-example-webpage"></a>

**To test the web application**

1. Download [sample\-site\.zip](samples/sample-site.zip), unzip it, and open `scripts/search.js` in your favorite text editor\.

1. Update the `apigatewayendpoint` variable to point to your API Gateway endpoint\. The endpoint takes the form of `https://some-id.execute-api.us-west-1.amazonaws.com/search-es-api-test`\.

1. Open `index.html` and try running searches for *thor*, *house*, and a few other terms\.  
![\[A sample search for thor.\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/search-ui.png)

## Next Steps<a name="search-example-next"></a>

This chapter is just a starting point to demonstrate a concept\. You might consider the following modifications:
+ Add your own data to the Amazon ES domain\.
+ Add methods to your API\.
+ In the Lambda function, modify the search query or boost different fields\.
+ Style the results differently or modify `search.js` to display different fields to the user\.