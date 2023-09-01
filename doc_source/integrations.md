# Loading streaming data into Amazon OpenSearch Service<a name="integrations"></a>

You can use OpenSearch Ingestion to directly load [streaming data](http://aws.amazon.com/streaming-data/) into your Amazon OpenSearch Service domain, without needing to use third\-party solutions\. To send data to OpenSearch Ingestion, you configure your data producers and the service automatically delivers the data to the domain or collection that you specify\. To get started with OpenSearch Ingestion, see [Tutorial: Ingesting data into a collection using Amazon OpenSearch Ingestion](osis-serverless-get-started.md)\.

You can still use other sources to load streaming data, such as Amazon Kinesis Data Firehose and Amazon CloudWatch Logs, which have built\-in support for OpenSearch Service\. Others, like Amazon S3, Amazon Kinesis Data Streams, and Amazon DynamoDB, use AWS Lambda functions as event handlers\. The Lambda functions respond to new data by processing it and streaming it to your domain\.

**Note**  
Lambda supports several popular programming languages and is available in most AWS Regions\. For more information, see [Getting started with Lambda](https://docs.aws.amazon.com/lambda/latest/dg/lambda-app.html) in the *AWS Lambda Developer Guide* and [AWS service endpoints](https://docs.aws.amazon.com/general/latest/gr/rande.html#lambda_region) in the *AWS General Reference*\.

**Topics**
+ [Loading streaming data from OpenSearch Ingestion](#integrations-osis)
+ [Loading streaming data from Amazon S3](#integrations-s3-lambda)
+ [Loading streaming data from Amazon Kinesis Data Streams](#integrations-kinesis)
+ [Loading streaming data from Amazon DynamoDB](#integrations-dynamodb)
+ [Loading streaming data from Amazon Kinesis Data Firehose](#integrations-fh)
+ [Loading streaming data from Amazon CloudWatch](#integrations-cloudwatch)
+ [Loading streaming data from AWS IoT](#integrations-cloudwatch-iot)

## Loading streaming data from OpenSearch Ingestion<a name="integrations-osis"></a>

You can use Amazon OpenSearch Ingestion to load data into an OpenSearch Service domain\. You configure your data producers to send data to OpenSearch Ingestion, and it automatically delivers the data to the collection that you specify\. You can also configure OpenSearch Ingestion to transform your data before delivering it\. For more information, see [Amazon OpenSearch Ingestion](ingestion.md)\. 

## Loading streaming data from Amazon S3<a name="integrations-s3-lambda"></a>

You can use Lambda to send data to your OpenSearch Service domain from Amazon S3\. New data that arrives in an S3 bucket triggers an event notification to Lambda, which then runs your custom code to perform the indexing\.

This method of streaming data is extremely flexible\. You can [index object metadata](https://aws.amazon.com/blogs/database/indexing-metadata-in-amazon-elasticsearch-service-using-aws-lambda-and-python/), or if the object is plaintext, parse and index some elements of the object body\. This section includes some unsophisticated Python sample code that uses regular expressions to parse a log file and index the matches\.

### Prerequisites<a name="integrations-s3-lambda-prereq"></a>

Before proceeding, you must have the following resources\.


****  

| Prerequisite | Description | 
| --- | --- | 
| Amazon S3 bucket | For more information, see [Create your first S3 bucket](https://docs.aws.amazon.com/AmazonS3/latest/gsg/CreatingABucket.html) in the Amazon Simple Storage Service User Guide\. The bucket must reside in the same Region as your OpenSearch Service domain\. | 
| OpenSearch Service domain | The destination for data after your Lambda function processes it\. For more information, see [ Creating OpenSearch Service domains](createupdatedomains.md#createdomains)\. | 

### Create the Lambda deployment package<a name="integrations-s3-lambda-deployment-package"></a>

Deployment packages are ZIP or JAR files that contain your code and its dependencies\. This section includes Python sample code\. For other programming languages, see [Lambda deployment packages](https://docs.aws.amazon.com/lambda/latest/dg/gettingstarted-package.html) in the *AWS Lambda Developer Guide*\.

1. Create a directory\. In this sample, we use the name `s3-to-opensearch`\.

1. Create a file within the directory named `sample.py`:

   ```
   import boto3
   import re
   import requests
   from requests_aws4auth import AWS4Auth
   
   region = '' # e.g. us-west-1
   service = 'es'
   credentials = boto3.Session().get_credentials()
   awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)
   
   host = '' # the OpenSearch Service domain, e.g. https://search-mydomain.us-west-1.es.amazonaws.com
   index = 'lambda-s3-index'
   datatype = '_doc'
   url = host + '/' + index + '/' + datatype
   
   headers = { "Content-Type": "application/json" }
   
   s3 = boto3.client('s3')
   
   # Regular expressions used to parse some simple log lines
   ip_pattern = re.compile('(\d+\.\d+\.\d+\.\d+)')
   time_pattern = re.compile('\[(\d+\/\w\w\w\/\d\d\d\d:\d\d:\d\d:\d\d\s-\d\d\d\d)\]')
   message_pattern = re.compile('\"(.+)\"')
   
   # Lambda execution starts here
   def handler(event, context):
       for record in event['Records']:
   
           # Get the bucket name and key for the new file
           bucket = record['s3']['bucket']['name']
           key = record['s3']['object']['key']
   
           # Get, read, and split the file into lines
           obj = s3.get_object(Bucket=bucket, Key=key)
           body = obj['Body'].read()
           lines = body.splitlines()
   
           # Match the regular expressions to each line and index the JSON
           for line in lines:
               line = line.decode("utf-8")
               ip = ip_pattern.search(line).group(1)
               timestamp = time_pattern.search(line).group(1)
               message = message_pattern.search(line).group(1)
   
               document = { "ip": ip, "timestamp": timestamp, "message": message }
               r = requests.post(url, auth=awsauth, json=document, headers=headers)
   ```

   Edit the variables for `region` and `host`\.

1. [Install pip](https://pip.pypa.io/en/stable/installation/) if you haven't already, then install the dependencies to a new `package` directory:

   ```
   cd s3-to-opensearch
   
   pip install --target ./package requests
   pip install --target ./package requests_aws4auth
   ```

   All Lambda execution environments have [Boto3](https://aws.amazon.com/sdk-for-python/) installed, so you don't need to include it in your deployment package\.

1. Package the application code and dependencies:

   ```
   cd package
   zip -r ../lambda.zip .
   
   cd ..
   zip -g lambda.zip sample.py
   ```

### Create the Lambda function<a name="integrations-s3-lambda-create"></a>

After you create the deployment package, you can create the Lambda function\. When you create a function, choose a name, runtime \(for example, Python 3\.8\), and IAM role\. The IAM role defines the permissions for your function\. For detailed instructions, see [Create a Lambda function with the console](https://docs.aws.amazon.com/lambda/latest/dg/get-started-create-function.html) in the *AWS Lambda Developer Guide*\.

This example assumes you're using the console\. Choose Python 3\.9 and a role that has S3 read permissions and OpenSearch Service write permissions, as shown in the following screenshot:

![\[Sample configuration for a Lambda function\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/lambda-function.png)

After you create the function, you must add a trigger\. For this example, we want the code to run whenever a log file arrives in an S3 bucket:

1. Choose **Add trigger** and select **S3**\.

1. Choose your bucket\.

1. For **Event type**, choose **PUT**\.

1. For **Prefix**, type `logs/`\.

1. For **Suffix**, type `.log`\.

1. Acknowledge the recursive invocation warning and choose **Add**\.

Finally, you can upload your deployment package:

1. Choose **Upload from** and **\.zip file**, then follow the prompts to upload your deployment package\.

1. After the upload finishes, edit the **Runtime settings** and change the **Handler** to `sample.handler`\. This setting tells Lambda the file \(`sample.py`\) and method \(`handler`\) that it should run after a trigger\.

At this point, you have a complete set of resources: a bucket for log files, a function that runs whenever a log file is added to the bucket, code that performs the parsing and indexing, and an OpenSearch Service domain for searching and visualization\.

### Testing the Lambda Function<a name="integrations-s3-lambda-configure"></a>

After you create the function, you can test it by uploading a file to the Amazon S3 bucket\. Create a file named `sample.log` using following sample log lines:

```
12.345.678.90 - [10/Oct/2000:13:55:36 -0700] "PUT /some-file.jpg"
12.345.678.91 - [10/Oct/2000:14:56:14 -0700] "GET /some-file.jpg"
```

Upload the file to the `logs` folder of your S3 bucket\. For instructions, see [Upload an object to your bucket](https://docs.aws.amazon.com/AmazonS3/latest/gsg/PuttingAnObjectInABucket.html) in the *Amazon Simple Storage Service User Guide*\.

Then use the OpenSearch Service console or OpenSearch Dashboards to verify that the `lambda-s3-index` index contains two documents\. You can also make a standard search request:

```
GET https://domain-name/lambda-s3-index/_search?pretty
{
  "hits" : {
    "total" : 2,
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "lambda-s3-index",
        "_type" : "_doc",
        "_id" : "vTYXaWIBJWV_TTkEuSDg",
        "_score" : 1.0,
        "_source" : {
          "ip" : "12.345.678.91",
          "message" : "GET /some-file.jpg",
          "timestamp" : "10/Oct/2000:14:56:14 -0700"
        }
      },
      {
        "_index" : "lambda-s3-index",
        "_type" : "_doc",
        "_id" : "vjYmaWIBJWV_TTkEuCAB",
        "_score" : 1.0,
        "_source" : {
          "ip" : "12.345.678.90",
          "message" : "PUT /some-file.jpg",
          "timestamp" : "10/Oct/2000:13:55:36 -0700"
        }
      }
    ]
  }
}
```

## Loading streaming data from Amazon Kinesis Data Streams<a name="integrations-kinesis"></a>

You can load streaming data from Kinesis Data Streams to OpenSearch Service\. New data that arrives in the data stream triggers an event notification to Lambda, which then runs your custom code to perform the indexing\. This section includes some unsophisticated Python sample code\.

### Prerequisites<a name="integrations-kinesis-lambda-prereq"></a>

Before proceeding, you must have the following resources\.


| Prerequisite | Description | 
| --- | --- | 
| Amazon Kinesis Data Stream | The event source for your Lambda function\. To learn more, see [Kinesis Data Streams](https://docs.aws.amazon.com/kinesis/latest/dev/amazon-kinesis-streams.html)\. | 
| OpenSearch Service Domain | The destination for data after your Lambda function processes it\. For more information, see [ Creating OpenSearch Service domains](createupdatedomains.md#createdomains) | 
| IAM Role |  This role must have basic OpenSearch Service, Kinesis, and Lambda permissions, such as the following: <pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [<br />    {<br />      "Effect": "Allow",<br />      "Action": [<br />        "es:ESHttpPost",<br />        "es:ESHttpPut",<br />        "logs:CreateLogGroup",<br />        "logs:CreateLogStream",<br />        "logs:PutLogEvents",<br />        "kinesis:GetShardIterator",<br />        "kinesis:GetRecords",<br />        "kinesis:DescribeStream",<br />        "kinesis:ListStreams"<br />      ],<br />      "Resource": "*"<br />    }<br />  ]<br />}</pre> The role must have the following trust relationship: <pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [<br />    {<br />      "Effect": "Allow",<br />      "Principal": {<br />        "Service": "lambda.amazonaws.com"<br />      },<br />      "Action": "sts:AssumeRole"<br />    }<br />  ]<br />}</pre> To learn more, see [Creating IAM roles](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create.html) in the *IAM User Guide*\.  | 

### Create the Lambda function<a name="integrations-kinesis-lambda"></a>

Follow the instructions in [Create the Lambda deployment package](#integrations-s3-lambda-deployment-package), but create a directory named `kinesis-to-opensearch` and use the following code for `sample.py`:

```
import base64
import boto3
import json
import requests
from requests_aws4auth import AWS4Auth

region = '' # e.g. us-west-1
service = 'es'
credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

host = '' # the OpenSearch Service domain, e.g. https://search-mydomain.us-west-1.es.amazonaws.com
index = 'lambda-kine-index'
datatype = '_doc'
url = host + '/' + index + '/' + datatype + '/'

headers = { "Content-Type": "application/json" }

def handler(event, context):
    count = 0
    for record in event['Records']:
        id = record['eventID']
        timestamp = record['kinesis']['approximateArrivalTimestamp']

        # Kinesis data is base64-encoded, so decode here
        message = base64.b64decode(record['kinesis']['data'])

        # Create the JSON document
        document = { "id": id, "timestamp": timestamp, "message": message }
        # Index the document
        r = requests.put(url + id, auth=awsauth, json=document, headers=headers)
        count += 1
    return 'Processed ' + str(count) + ' items.'
```

Edit the variables for `region` and `host`\.

[Install pip](https://pip.pypa.io/en/stable/installation/) if you haven't already, then use the following commands to install your dependencies:

```
cd kinesis-to-opensearch

pip install --target ./package requests
pip install --target ./package requests_aws4auth
```

Then follow the instructions in [Create the Lambda function](#integrations-s3-lambda-create), but specify the IAM role from [Prerequisites](#integrations-kinesis-lambda-prereq) and the following settings for the trigger:
+ **Kinesis stream**: your Kinesis stream
+ **Batch size**: 100
+ **Starting position**: Trim horizon

To learn more, see [What is Amazon Kinesis Data Streams?](https://docs.aws.amazon.com/streams/latest/dev/working-with-kinesis.html) in the *Amazon Kinesis Data Streams Developer Guide*\.

At this point, you have a complete set of resources: a Kinesis data stream, a function that runs after the stream receives new data and indexes that data, and an OpenSearch Service domain for searching and visualization\.

### Test the Lambda Function<a name="integrations-kinesis-testing"></a>

After you create the function, you can test it by adding a new record to the data stream using the AWS CLI:

```
aws kinesis put-record --stream-name test --data "My test data." --partition-key partitionKey1 --region us-west-1
```

Then use the OpenSearch Service console or OpenSearch Dashboards to verify that `lambda-kine-index` contains a document\. You can also use the following request:

```
GET https://domain-name/lambda-kine-index/_search
{
  "hits" : [
    {
      "_index": "lambda-kine-index",
      "_type": "_doc",
      "_id": "shardId-000000000000:49583511615762699495012960821421456686529436680496087042",
      "_score": 1,
      "_source": {
        "timestamp": 1523648740.051,
        "message": "My test data.",
        "id": "shardId-000000000000:49583511615762699495012960821421456686529436680496087042"
      }
    }
  ]
}
```

## Loading streaming data from Amazon DynamoDB<a name="integrations-dynamodb"></a>

You can use AWS Lambda to send data to your OpenSearch Service domain from Amazon DynamoDB\. New data that arrives in the database table triggers an event notification to Lambda, which then runs your custom code to perform the indexing\.

### Prerequisites<a name="integrations-dynamodb-prereq"></a>

Before proceeding, you must have the following resources\.


| Prerequisite | Description | 
| --- | --- | 
| DynamoDB table | The table contains your source data\. For more information, see [Basic Operations on DynamoDB Tables](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithTables.Basics.html) in the *Amazon DynamoDB Developer Guide*\.The table must reside in the same Region as your OpenSearch Service domain and have a stream set to **New image**\. To learn more, see [Enabling a Stream](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Streams.html#Streams.Enabling)\. | 
| OpenSearch Service domain | The destination for data after your Lambda function processes it\. For more information, see [ Creating OpenSearch Service domains](createupdatedomains.md#createdomains)\. | 
| IAM role | This role must have basic OpenSearch Service, DynamoDB, and Lambda execution permissions, such as the following:<pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [<br />    {<br />      "Effect": "Allow",<br />      "Action": [<br />        "es:ESHttpPost",<br />        "es:ESHttpPut",<br />        "dynamodb:DescribeStream",<br />        "dynamodb:GetRecords",<br />        "dynamodb:GetShardIterator",<br />        "dynamodb:ListStreams",<br />        "logs:CreateLogGroup",<br />        "logs:CreateLogStream",<br />        "logs:PutLogEvents"<br />      ],<br />      "Resource": "*"<br />    }<br />  ]<br />}</pre>The role must have the following trust relationship:<pre>{<br />  "Version": "2012-10-17",<br />  "Statement": [<br />    {<br />      "Effect": "Allow",<br />      "Principal": {<br />        "Service": "lambda.amazonaws.com"<br />      },<br />      "Action": "sts:AssumeRole"<br />    }<br />  ]<br />}</pre>To learn more, see [Creating IAM roles](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create.html) in the *IAM User Guide*\. | 

### Create the Lambda function<a name="integrations-dynamodb-lambda"></a>

Follow the instructions in [Create the Lambda deployment package](#integrations-s3-lambda-deployment-package), but create a directory named `ddb-to-opensearch` and use the following code for `sample.py`:

```
import boto3
import requests
from requests_aws4auth import AWS4Auth

region = '' # e.g. us-east-1
service = 'es'
credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

host = '' # the OpenSearch Service domain, e.g. https://search-mydomain.us-west-1.es.amazonaws.com
index = 'lambda-index'
datatype = '_doc'
url = host + '/' + index + '/' + datatype + '/'

headers = { "Content-Type": "application/json" }

def handler(event, context):
    count = 0
    for record in event['Records']:
        # Get the primary key for use as the OpenSearch ID
        id = record['dynamodb']['Keys']['id']['S']

        if record['eventName'] == 'REMOVE':
            r = requests.delete(url + id, auth=awsauth)
        else:
            document = record['dynamodb']['NewImage']
            r = requests.put(url + id, auth=awsauth, json=document, headers=headers)
        count += 1
    return str(count) + ' records processed.'
```

Edit the variables for `region` and `host`\.

[Install pip](https://pip.pypa.io/en/stable/installation/) if you haven't already, then use the following commands to install your dependencies:

```
cd ddb-to-opensearch

pip install --target ./package requests
pip install --target ./package requests_aws4auth
```

Then follow the instructions in [Create the Lambda function](#integrations-s3-lambda-create), but specify the IAM role from [Prerequisites](#integrations-dynamodb-prereq) and the following settings for the trigger:
+ **Table**: your DynamoDB table
+ **Batch size**: 100
+ **Starting position**: Trim horizon

To learn more, see [Process New Items with DynamoDB Streams and Lambda](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Streams.Lambda.Tutorial.html) in the *Amazon DynamoDB Developer Guide*\.

At this point, you have a complete set of resources: a DynamoDB table for your source data, a DynamoDB stream of changes to the table, a function that runs after your source data changes and indexes those changes, and an OpenSearch Service domain for searching and visualization\.

### Test the Lambda function<a name="integrations-dynamodb-lambda-test"></a>

After you create the function, you can test it by adding a new item to the DynamoDB table using the AWS CLI:

```
aws dynamodb put-item --table-name test --item '{"director": {"S": "Kevin Costner"},"id": {"S": "00001"},"title": {"S": "The Postman"}}' --region us-west-1
```

Then use the OpenSearch Service console or OpenSearch Dashboards to verify that `lambda-index` contains a document\. You can also use the following request:

```
GET https://domain-name/lambda-index/_doc/00001
{
    "_index": "lambda-index",
    "_type": "_doc",
    "_id": "00001",
    "_version": 1,
    "found": true,
    "_source": {
        "director": {
            "S": "Kevin Costner"
        },
        "id": {
            "S": "00001"
        },
        "title": {
            "S": "The Postman"
        }
    }
}
```

## Loading streaming data from Amazon Kinesis Data Firehose<a name="integrations-fh"></a>

Kinesis Data Firehose supports OpenSearch Service as a delivery destination\. For instructions about how to load streaming data into OpenSearch Service, see [Creating a Kinesis Data Firehose Delivery Stream](https://docs.aws.amazon.com/firehose/latest/dev/basic-create.html) and [Choose OpenSearch Service for Your Destination](https://docs.aws.amazon.com/firehose/latest/dev/create-destination.html#create-destination-elasticsearch) in the *Amazon Kinesis Data Firehose Developer Guide*\.

Before you load data into OpenSearch Service, you might need to perform transforms on the data\. To learn more about using Lambda functions to perform this task, see [Amazon Kinesis Data Firehose Data Transformation](https://docs.aws.amazon.com/firehose/latest/dev/data-transformation.html) in the same guide\.

As you configure a delivery stream, Kinesis Data Firehose features a "one\-click" IAM role that gives it the resource access it needs to send data to OpenSearch Service, back up data on Amazon S3, and transform data using Lambda\. Because of the complexity involved in creating such a role manually, we recommend using the provided role\.

## Loading streaming data from Amazon CloudWatch<a name="integrations-cloudwatch"></a>

You can load streaming data from CloudWatch Logs to your OpenSearch Service domain by using a CloudWatch Logs subscription\. For information about Amazon CloudWatch subscriptions, see [Real\-time processing of log data with subscriptions](http://docs.aws.amazon.com/AmazonCloudWatch/latest/DeveloperGuide/Subscriptions.html)\. For configuration information, see [Streaming CloudWatch Logs data to Amazon OpenSearch Service](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/CWL_OpenSearch_Stream.html) in the *Amazon CloudWatch Developer Guide*\.

## Loading streaming data from AWS IoT<a name="integrations-cloudwatch-iot"></a>

You can send data from AWS IoT using [rules](https://docs.aws.amazon.com/iot/latest/developerguide/iot-rules.html)\. To learn more, see the [OpenSearch](https://docs.aws.amazon.com/iot/latest/developerguide/opensearch-rule-action.html) action in the *AWS IoT Developer Guide*\.