# Creating a search application with Amazon Elasticsearch Service<a name="search-example"></a>

A common way to create a search application with Amazon Elasticsearch Service \(Amazon ES\) is to use web forms to send user queries to a server\. Then you can authorize the server to call the Elasticsearch APIs directly and have the server send requests to Amazon ES\.

If you want to write client\-side code that doesn't rely on a server, however, you should compensate for the security and performance risks\. Allowing unsigned, public access to the Elasticsearch APIs is inadvisable\. Users might access unsecured endpoints or impact cluster performance through overly broad queries \(or too many queries\)\.

This chapter presents a solution: use Amazon API Gateway to restrict users to a subset of the Elasticsearch APIs and AWS Lambda to sign requests from API Gateway to Amazon ES\.

**Note**  
Standard API Gateway and Lambda pricing applies, but within the limited usage of this tutorial, costs should be negligible\.

## Step 1: Index sample data<a name="search-example-index"></a>

A prerequisite for these steps is an Amazon ES domain\. Download [sample\-movies\.zip](samples/sample-movies.zip), unzip it, and use the `_bulk` API to add the 5,000 documents to the `movies` index:

```
POST https://search-my-domain.us-west-1.es.amazonaws.com/_bulk
{ "index": { "_index": "movies", "_type": "movie", "_id": "tt1979320" } }
{"fields":{"directors":["Ron Howard"],"release_date":"2013-09-02T00:00:00Z","rating":8.3,"genres":["Action","Biography","Drama","Sport"],"image_url":"http://ia.media-imdb.com/images/M/MV5BMTQyMDE0MTY0OV5BMl5BanBnXkFtZTcwMjI2OTI0OQ@@._V1_SX400_.jpg","plot":"A re-creation of the merciless 1970s rivalry between Formula One rivals James Hunt and Niki Lauda.","title":"Rush","rank":2,"running_time_secs":7380,"actors":["Daniel Brühl","Chris Hemsworth","Olivia Wilde"],"year":2013},"id":"tt1979320","type":"add"}
{ "index": { "_index": "movies", "_type": "movie", "_id": "tt1951264" } }
{"fields":{"directors":["Francis Lawrence"],"release_date":"2013-11-11T00:00:00Z","genres":["Action","Adventure","Sci-Fi","Thriller"],"image_url":"http://ia.media-imdb.com/images/M/MV5BMTAyMjQ3OTAxMzNeQTJeQWpwZ15BbWU4MDU0NzA1MzAx._V1_SX400_.jpg","plot":"Katniss Everdeen and Peeta Mellark become targets of the Capitol after their victory in the 74th Hunger Games sparks a rebellion in the Districts of Panem.","title":"The Hunger Games: Catching Fire","rank":4,"running_time_secs":8760,"actors":["Jennifer Lawrence","Josh Hutcherson","Liam Hemsworth"],"year":2013},"id":"tt1951264","type":"add"}
...
```

To learn more, see [Indexing data in Amazon Elasticsearch Service](es-indexing.md)\.

## Step 2: Create the API in API Gateway<a name="search-example-api"></a>

Using API Gateway lets you create a more limited API and simplifies the process of interacting with the Elasticsearch `_search` API\. API Gateway lets you enable security features like Amazon Cognito authentication and request throttling\. Perform the following steps to create and deploy an API:

### Create and configure the API<a name="create-api"></a>

**To create your API using the API Gateway console**

1. Within API Gateway, choose **Create API**\.

1. Locate **REST API** \(not private\) and choose **Build**\.

1. Configure the following fields:
   + API name: **search\-es\-api**
   + Description: **Public API for searching an Amazon Elasticsearch Service domain**
   + Endpoint Type: **Regional**

1. Choose **Create API**\. 

1. Choose **Actions** > **Create Method**\.

1. Choose **GET** in the dropdown and click the checkmark to confirm\.

1. Configure the following settings, then click **Save**:


| Setting | Value | 
| --- | --- | 
| Integration type | Lambda function | 
| Use Lambda proxy integration | Yes | 
| Lambda region | us\-west\-1 | 
| Lambda function | search\-es\-lambda \(you'll configure this later in Lambda\) | 
| Use default timeout | Yes | 

### Configure the method request<a name="method-request"></a>

Choose **Method Request** and configure the following settings:


| Setting | Value | 
| --- | --- | 
| Authorization | NONE | 
| Request Validator |  Validate query string parameters and headers   | 
| API Key Required | false | 

**URL Query String Parameters**


| Setting | Value | 
| --- | --- | 
| Name | q | 
| Required |  Yes  | 

### Deploy the API and configure a stage<a name="deploy-api"></a>

 The API Gateway console lets you deploy an API by creating a deployment and associating it with a new or existing stage\. 

1. Choose **Actions** > **Deploy API**\.

1. For **Deployment stage** choose **New Stage** and name the stage `search-es-api-test`\.

1. Choose **Deploy\.**

1. Configure the following settings in the stage editor, then choose **Save Changes**:


| Setting | Value | 
| --- | --- | 
| Enable throttling | Yes | 
| Rate |  1000  | 
| Burst | 500 | 

These settings configure an API that has only one method: a `GET` request to the endpoint root \(`https://some-id.execute-api.us-west-1.amazonaws.com/search-es-api-test`\)\. The request requires a single parameter \(`q`\), the query string to search for\. When called, the method passes the request to Lambda, which runs the `search-es-lambda` function\. For more information, see [Creating an API in Amazon API Gateway](https://docs.aws.amazon.com/apigateway/latest/developerguide/how-to-create-api.html) and [Deploying an API in Amazon API Gateway](https://docs.aws.amazon.com/apigateway/latest/developerguide/how-to-deploy-api.html)\.

## Step 3: Create and deploy the Lambda function<a name="search-example-lambda"></a>

After you create your API in API Gateway, create the Lambda function that it passes requests to\.

### Create the Lambda function<a name="sample-lamdba-python"></a>

In this solution, API Gateway passes requests to the following Python 3\.8 Lambda function, which queries Amazon ES and returns results\. Name the function `search-es-lambda`\.

Because this sample function uses external libraries, you need to create a deployment package and upload it to Lambda for the code to work\. For more information about creating Lambda functions and deployment packages, see [Creating a Deployment Package \(Python\)](https://docs.aws.amazon.com/lambda/latest/dg/lambda-python-how-to-create-deployment-package.html) in the *AWS Lambda Developer Guide* and [Create the Lambda deployment package](es-aws-integrations.md#es-aws-integrations-s3-lambda-es-deployment-package) in this guide\.

```
import boto3
import json
import requests
from requests_aws4auth import AWS4Auth

region = '' # For example, us-west-1
service = 'es'
credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

host = '' # The ES domain endpoint with https:// and a trailing slash
index = 'movies'
url = host + '/' + index + '/_search'

# Lambda execution starts here
def lambda_handler(event, context):

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

#### Modify the handler<a name="sample-lamdba-handler"></a>

The *handler* is the method in your function code that processes events\. You need to change the handler name according to the name of the file in your deployment package where the Lambda function is located\. For example, if your file is named `es-function.py`, rename the handler to `es-function.lambda_handler`\. For more information, see [Lambda function handler in Python](https://docs.aws.amazon.com/lambda/latest/dg/python-handler.html)\.

#### Configure a trigger<a name="sample-lamdba-trigger"></a>

Choose **Add trigger** and create the HTTP endpoint that invokes your function\. The trigger must have the following configuration:


| Trigger | API | Deployment Stage | Security | 
| --- | --- | --- | --- | 
| API Gateway | search\-es\-api | search\-es\-api\-test | Open | 

## Step 4: Modify the domain access policy<a name="search-example-perms"></a>

Your Amazon ES domain must allow the Lambda function to make `GET` requests to the `movies` index\. The following policy provides `search-es-lambda-role` \(created through Lambda\) access to the `movies` index:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::123456789012:role/service-role/search-es-lambda-role-1abcdefg"
      },
      "Action": "es:ESHttpGet",
      "Resource": "arn:aws:es:us-west-1:123456789012:domain/domain-name/movies/_search"
    }
  ]
}
```

**Note**  
To get the exact name of the role that Lambda automatically creates, go to the AWS Identity and Access Management \(IAM\) console, choose **Roles**, and search for "lambda"\.

For more information about access policies, see [Configuring access policies](es-createupdatedomains.md#es-createdomain-configure-access-policies)\.

## Step 5: Test the web application<a name="search-example-webpage"></a>

**To test the web application**

1. Download [sample\-site\.zip](samples/sample-site.zip), unzip it, and open `scripts/search.js` in your favorite text editor\.

1. Update the `apigatewayendpoint` variable to point to your API Gateway endpoint\. The endpoint takes the form of `https://some-id.execute-api.us-west-1.amazonaws.com/search-es-api-test`\.

1. Open `index.html` and try running searches for *thor*, *house*, and a few other terms\.  
![\[A sample search for thor.\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/search-ui.png)

## Next steps<a name="search-example-next"></a>

This chapter is just a starting point to demonstrate a concept\. You might consider the following modifications:
+ Add your own data to the Amazon ES domain\.
+ Add methods to your API\.
+ In the Lambda function, modify the search query or boost different fields\.
+ Style the results differently or modify `search.js` to display different fields to the user\.