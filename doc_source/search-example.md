# Tutorial: Creating a search application with Amazon OpenSearch Service<a name="search-example"></a>

A common way to create a search application with Amazon OpenSearch Service is to use web forms to send user queries to a server\. Then you can authorize the server to call the OpenSearch APIs directly and have the server send requests to OpenSearch Service\. However, if you want to write client\-side code that doesn't rely on a server, you should compensate for the security and performance risks\. Allowing unsigned, public access to the OpenSearch APIs is inadvisable\. Users might access unsecured endpoints or impact cluster performance through overly broad queries \(or too many queries\)\.

This chapter presents a solution: use Amazon API Gateway to restrict users to a subset of the OpenSearch APIs and AWS Lambda to sign requests from API Gateway to OpenSearch Service\.

**Note**  
Standard API Gateway and Lambda pricing applies, but within the limited usage of this tutorial, costs should be negligible\.

## Prerequisites<a name="search-example-prereq"></a>

A prerequisite for this tutorial is an OpenSearch Service domain\. If you don't already have one, follow the steps in [Create an OpenSearch Service domain](gsgcreate-domain.md) to create one\.

## Step 1: Index sample data<a name="search-example-index"></a>

Download [sample\-movies\.zip](samples/sample-movies.zip), unzip it, and use the `_bulk` API to add the 5,000 documents to the `movies` index:

```
POST https://search-my-domain.us-west-1.es.amazonaws.com/_bulk
{ "index": { "_index": "movies", "_type": "movie", "_id": "tt1979320" } }
{"directors":["Ron Howard"],"release_date":"2013-09-02T00:00:00Z","rating":8.3,"genres":["Action","Biography","Drama","Sport"],"image_url":"http://ia.media-imdb.com/images/M/MV5BMTQyMDE0MTY0OV5BMl5BanBnXkFtZTcwMjI2OTI0OQ@@._V1_SX400_.jpg","plot":"A re-creation of the merciless 1970s rivalry between Formula One rivals James Hunt and Niki Lauda.","title":"Rush","rank":2,"running_time_secs":7380,"actors":["Daniel Brühl","Chris Hemsworth","Olivia Wilde"],"year":2013,"id":"tt1979320","type":"add"}
{ "index": { "_index": "movies", "_type": "movie", "_id": "tt1951264" } }
{"directors":["Francis Lawrence"],"release_date":"2013-11-11T00:00:00Z","genres":["Action","Adventure","Sci-Fi","Thriller"],"image_url":"http://ia.media-imdb.com/images/M/MV5BMTAyMjQ3OTAxMzNeQTJeQWpwZ15BbWU4MDU0NzA1MzAx._V1_SX400_.jpg","plot":"Katniss Everdeen and Peeta Mellark become targets of the Capitol after their victory in the 74th Hunger Games sparks a rebellion in the Districts of Panem.","title":"The Hunger Games: Catching Fire","rank":4,"running_time_secs":8760,"actors":["Jennifer Lawrence","Josh Hutcherson","Liam Hemsworth"],"year":2013,"id":"tt1951264","type":"add"}
...
```

For instructions, see [Option 2: Upload multiple documents](gsgupload-data.md#gsgmultiple-document)\.

## Step 2: Create the API in API Gateway<a name="search-example-api"></a>

Using API Gateway lets you create a more limited API and simplifies the process of interacting with the OpenSearch `_search` API\. API Gateway lets you enable security features like Amazon Cognito authentication and request throttling\. Perform the following steps to create and deploy an API:

### Create and configure the API<a name="create-api"></a>

**To create your API using the API Gateway console**

1. Within API Gateway, choose **Create API**\.

1. Locate **REST API** \(not private\) and choose **Build**\.

1. Configure the following fields:
   + API name: **opensearch\-api**
   + Description: **Public API for searching an Amazon OpenSearch Service domain**
   + Endpoint Type: **Regional**

1. Choose **Create API**\. 

1. Choose **Actions** and **Create Method**\.

1. Select **GET** in the dropdown and click the checkmark to confirm\.

1. Configure the following settings, then choose **Save**:


| Setting | Value | 
| --- | --- | 
| Integration type | Lambda function | 
| Use Lambda proxy integration | Yes | 
| Lambda region | us\-west\-1 | 
| Lambda function | opensearch\-lambda \(you'll configure this later in Lambda\) | 
| Use default timeout | Yes | 

**Note**  
If you're performing these steps in order, you'll see an error: "Function not found: arn:aws:lambda:us\-west\-1:123456789012:function:opensearch\-lambda"\. You can ignore this error, as you'll configure the Lambda function in step 3\.

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

1. Choose **Actions** and **Deploy API**\.

1. For **Deployment stage** choose **New Stage** and name the stage `opensearch-api-test`\.

1. Choose **Deploy\.**

1. Configure the following settings in the stage editor, then choose **Save Changes**:


| Setting | Value | 
| --- | --- | 
| Enable throttling | Yes | 
| Rate |  1000  | 
| Burst | 500 | 

These settings configure an API that has only one method: a `GET` request to the endpoint root \(`https://some-id.execute-api.us-west-1.amazonaws.com/search-es-api-test`\)\. The request requires a single parameter \(`q`\), the query string to search for\. When called, the method passes the request to Lambda, which runs the `opensearch-lambda` function\. For more information, see [Creating an API in Amazon API Gateway](https://docs.aws.amazon.com/apigateway/latest/developerguide/how-to-create-api.html) and [Deploying a REST API in Amazon API Gateway](https://docs.aws.amazon.com/apigateway/latest/developerguide/how-to-deploy-api.html)\.

## Step 3: Create and deploy the Lambda function<a name="search-example-lambda"></a>

After you create your API in API Gateway, create the Lambda function that it passes requests to\.

### Create the Lambda function<a name="sample-lamdba-python"></a>

In this solution, API Gateway passes requests to the following Python 3\.8 Lambda function, which queries OpenSearch Service and returns results\. Name the function `opensearch-lambda`\.

Because this sample function uses external libraries, you need to create a deployment package and upload it to Lambda for the code to work\. For more information about creating Lambda functions and deployment packages, see [Deploy Python Lambda functions with \.zip file archives](https://docs.aws.amazon.com/lambda/latest/dg/lambda-python-how-to-create-deployment-package.html) in the *AWS Lambda Developer Guide* and [Create the Lambda deployment package](integrations.md#integrations-s3-lambda-deployment-package) in this guide\.

```
import boto3
import json
import requests
from requests_aws4auth import AWS4Auth

region = '' # For example, us-west-1
service = 'es'
credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

host = '' # The OpenSearch domain endpoint with https:// and without a trailing slash
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
                "fields": ["title^4", "plot^2", "actors", "directors"]
            }
        }
    }

    # Elasticsearch 6.x requires an explicit Content-Type header
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

The *handler* is the method in your function code that processes events\. You need to change the handler name according to the name of the file in your deployment package where the Lambda function is located\. For example, if your file is named `function.py`, rename the handler to `function.lambda_handler`\. For more information, see [Lambda function handler in Python](https://docs.aws.amazon.com/lambda/latest/dg/python-handler.html)\.

#### Configure a trigger<a name="sample-lamdba-trigger"></a>

Choose **Add trigger** and create the HTTP endpoint that invokes your function\. The trigger must have the following configuration:


| Trigger | API | Deployment Stage | Security | 
| --- | --- | --- | --- | 
| API Gateway | opensearch\-api | opensearch\-api\-test | Open | 

## Step 4: \(Optional\) Modify the domain access policy<a name="search-example-perms"></a>

Your OpenSearch Service domain must allow the Lambda function to make `GET` requests to the `movies` index\. If your domain has an open access policy with fine\-grained access control enabled, you can leave it as\-is: 

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "*"
      },
      "Action": "es:*",
      "Resource": "arn:aws:es:us-west-1:123456789012:domain/domain-name/*"
    }
  ]
}
```

Alternatively, you can choose to make your domain access policy more granular\. For example, the following minimum policy provides `opensearch-lambda-role` \(created through Lambda\) read access to the `movies` index\. To get the exact name of the role that Lambda automatically creates, go to the AWS Identity and Access Management \(IAM\) console, choose **Roles**, and search for "lambda"\.

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::123456789012:role/service-role/opensearch-lambda-role-1abcdefg"
      },
      "Action": "es:ESHttpGet",
      "Resource": "arn:aws:es:us-west-1:123456789012:domain/domain-name/movies/_search"
    }
  ]
}
```

**Note**  
If you have fine\-grained access control enabled for the domain, you also need to [map the role to a user](fgac.md#fgac-mapping) in OpenSearch Dashboards, otherwise you'll see permissions errors\.

For more information about access policies, see [Configuring access policies](createupdatedomains.md#createdomain-configure-access-policies)\.

## Map the Lambda role \(if using fine\-grained access control\)<a name="search-example-perms-fgac"></a>

Fine\-grained access control introduces an additional step before you can test the application\. Even if you use HTTP basic authentication for all other purposes, you need to map the Lambda role to a user, otherwise you'll see permissions errors\.

1. Navigate to the OpenSearch Dashboards endpoint for the domain\.

1. From the main menu, choose **Security**, **Roles**, and select user or role to map the Lambda role to\.

1. Choose **Mapped users**, **Manage mapping**\. 

1. Under **Backend roles**, add the Amazon Resource Name \(ARN\) of the Lambda role\. 

   ```
   arn:aws:iam::123456789123:role/opensearch-lambda-role-1abcdefg
   ```

1. Select **Map** and confirm the user or role shows up under **Mapped users**\.

## Step 5: Test the web application<a name="search-example-webpage"></a>

**To test the web application**

1. Download [sample\-site\.zip](samples/sample-site.zip), unzip it, and open `scripts/search.js` in your favorite text editor\.

1. Update the `apigatewayendpoint` variable to point to your API Gateway endpoint\. The endpoint takes the form of `https://some-id.execute-api.us-west-1.amazonaws.com/opensearch-api-test`\. You can quickly find the endpoint in API Gateway by choosing **Stages** and selecting the name of the API\.

1. Open `index.html` and try running searches for *thor*, *house*, and a few other terms\.  
![\[A sample search for thor.\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/search-ui.png)

### Troubleshoot CORS errors<a name="search-example-cors"></a>

Even though the Lambda function includes content in the response to support CORS, you still might see the following error: 

```
Access to XMLHttpRequest at '<api-gateway-endpoint>' from origin 'null' has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present in the requested resource.
```

If this happens, try the following:

1. [Enable CORS](https://docs.aws.amazon.com/apigateway/latest/developerguide/how-to-cors-console.html) on the GET resource\. Under **Advanced**, set **Access\-Control\-Allow\-Credentials** to `'true'`\.

1. Redeploy your API in API Gateway \(**Actions**, **Deploy API**\)\.

1. Delete and re\-add your Lambda function trigger\.

## Next steps<a name="search-example-next"></a>

This chapter is just a starting point to demonstrate a concept\. You might consider the following modifications:
+ Add your own data to the OpenSearch Service domain\.
+ Add methods to your API\.
+ In the Lambda function, modify the search query or boost different fields\.
+ Style the results differently or modify `search.js` to display different fields to the user\.