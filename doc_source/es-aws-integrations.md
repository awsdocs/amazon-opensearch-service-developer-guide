# Loading Streaming Data into Amazon Elasticsearch Service<a name="es-aws-integrations"></a>

You can load [streaming data](http://aws.amazon.com/streaming-data/) into your Amazon ES domain from Amazon S3 buckets, Amazon Kinesis streams, Amazon DynamoDB Streams, and Amazon CloudWatch metrics\. For example, to load streaming data from Amazon S3 and Amazon Kinesis, you use a Lambda function as an event handler in the AWS Cloud\. The Lambda function responds to new data by processing it and streaming the data to your domain\. 

**Topics**
+ [Loading Streaming Data into Amazon ES from Amazon S3](#es-aws-integrations-s3-lambda-es)
+ [Loading Streaming Data into Amazon ES from Amazon Kinesis](#es-aws-integrations-kinesis-lambda-es)
+ [Loading Streaming Data into Amazon ES from Amazon Kinesis Firehose](#es-aws-integrations-fh)
+ [Loading Streaming Data into Amazon ES from Amazon DynamoDB](#es-aws-integrations-dynamodb-es)
+ [Loading Streaming Data into Amazon ES from Amazon CloudWatch](#es-aws-integrations-cloudwatch-es)
+ [Loading Data into Amazon ES from AWS IoT](#es-aws-integrations-cloudwatch-iot)

Streaming data provides fresh data for search and analytic queries\. Amazon S3 pushes event notifications to AWS Lambda\. For more information, see [Using AWS Lambda with Amazon S3](http://docs.aws.amazon.com/lambda/latest/dg/with-s3.html) in the *AWS Lambda Developer Guide*\. Amazon Kinesis requires AWS Lambda to poll for, or pull, event notifications\. For more information, see [Using AWS Lambda with Amazon Kinesis](http://docs.aws.amazon.com/lambda/latest/dg/with-kinesis.html)\. 

You should be familiar with these service integrations before attempting to use them to load streaming data into your Amazon ES domain\. For more information about these services, see the following AWS documentation:
+ [AWS Lambda Developer Guide](http://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
+ [Amazon S3 Developer Guide](http://docs.aws.amazon.com/AmazonS3/latest/dev/Welcome.html)
+ [Amazon Kinesis Developer Guide](http://docs.aws.amazon.com/kinesis/latest/dev/introduction.html)
+ [Amazon DynamoDB Developer Guide](http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
+ [Amazon CloudWatch User Guide](http://docs.aws.amazon.com/AmazonCloudWatch/latest/DeveloperGuide/WhatIsCloudWatch.html)
+ [AWS IoT Developer Guide](http://docs.aws.amazon.com/iot/latest/developerguide/)

**Note**  
AWS Lambda is available in limited regions\. For more information, see the list of [AWS Lambda regions](http://docs.aws.amazon.com/general/latest/gr/rande.html#lambda_region) in the *AWS General Reference*\.

## Loading Streaming Data into Amazon ES from Amazon S3<a name="es-aws-integrations-s3-lambda-es"></a>

You can integrate your Amazon ES domain with Amazon S3 and AWS Lambda\. Any new data sent to an S3 bucket triggers an event notification to Lambda, which then runs your custom Java or Node\.js application code\. After your application processes the data, it streams the data to your domain\. At a high level, setting up to load streaming data to Amazon ES requires the following steps:

1. [Creating a Lambda deployment package](#es-aws-integrations-s3-lambda-es-deployment-package)

1. [Configuring a Lambda function](#es-aws-integrations-s3-lambda-es-function-configuration)

1. [Granting authorization to add data to your Amazon ES domain](#es-aws-integrations-s3-lambda-es-authorizations)

You also must create an Amazon S3 bucket and an Amazon ES domain\. Setting up this integration path has the following prerequisites\.


****  

| Prerequisite | Description | 
| --- | --- | 
| Amazon S3 Bucket | The event source that triggers your Lambda function\. For more information, see [Create a Bucket](http://docs.aws.amazon.com/AmazonS3/latest/gsg/CreatingABucket.html) in the Amazon Simple Storage Service Getting Started Guide\. The bucket must reside in the same AWS Region as your Amazon ES domain\. | 
| Amazon ES Domain | The destination for data after it is processed by the application code in your Lambda function\. For more information, see [Creating Amazon ES Domains](es-createupdatedomains.md#es-createdomains)\. | 
| Lambda Function | The Java or Node\.js application code that runs when S3 pushes an event notification to Lambda\. Amazon ES provides a sample application in Node\.js, s3\_lambda\_es\.js, that you can download to get started\. See the [Lambda sample code for Amazon ES](https://github.com/awslabs/amazon-elasticsearch-lambda-samples)\.  | 
| Lambda Deployment Package | A \.zip file that consists of your Java or Node\.js application code and any dependencies\. For information about the required folder hierarchy, see [Creating a Lambda Deployment Package](#es-aws-integrations-s3-lambda-es-deployment-package)\. For information about creating specific Lambda deployment packages, see [Creating a Deployment Package \(Node\.js\)](http://docs.aws.amazon.com/lambda/latest/dg/nodejs-create-deployment-pkg.html) and [Creating a Deployment Package \(Java\)](http://docs.aws.amazon.com/lambda/latest/dg/lambda-java-how-to-create-deployment-package.html)\. | 
| Amazon ES Authorization | An IAM access policy that permits Lambda to add data to your domain\. Attach the policy to the Amazon S3 execution role that you create as part of your Lambda function\. For details, see [Granting Authorization to Add Data to Your Amazon ES Domain](#es-aws-integrations-s3-lambda-es-authorizations)\. | 

### Setting Up to Load Streaming Data into Amazon ES from Amazon S3<a name="es-aws-integrations-s3-lambda-es-prereq-configurations"></a>

This section provides additional details about setting up the prerequisites for loading streaming data into Amazon ES from Amazon S3\. After you finish configuring the integration, data streams automatically to your Amazon ES domain whenever new data is added to your Amazon S3 bucket\. 

#### Creating a Lambda Deployment Package<a name="es-aws-integrations-s3-lambda-es-deployment-package"></a>

Create a \.zip file that contains your Lambda application code and any dependencies\.

To create a deployment package:

1. Create a directory structure like the following:

   ```
   eslambda
       \node_modules
   ```

   This example uses `eslambda` for the name of the top\-level folder, but you can use any name\. However, the subfolder must be named `node_modules`\.

1. Place your application source code in the `eslambda` folder\.

1. Add or edit the following four global variables:
   + `endpoint`, the Amazon ES domain endpoint\.
   + `region`, the AWS Region in which you created your Amazon ES domain\.
   + `index`, the name of the Amazon ES index to use for data that is streamed from Amazon S3\.
   + `doctype`, the Amazon ES document type of the streamed data\. For more information, see [Mapping Types](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html#all-mapping-types) in the Elasticsearch documentation\.

   The following example from `s3_lambda_es.js` configures the sample application to use the `streaming-logs` domain endpoint in the us\-east\-1 AWS Region:

   ```
   /* Globals */
   var esDomain = {
       endpoint: 'search-streaming-logs-okga24ftzsbz2a2hzhsqw73jpy.us-east-1.es.example.com',
       region: 'us-east-1',
       index: 'streaming-logs',
       doctype: 'apache'
   };
   ```

1. Install any dependencies that are required by your application\.

   For example, if you use Node\.js, you must execute the following command for each `require` statement in your application code:

   ```
   npm install <dependency>
   ```

1. Verify that all runtime dependencies that are required by your application code are located in the `node_modules` folder\.

1. Execute the following command to package the application code and dependencies:

   ```
   zip -r eslambda.zip *
   ```

   The name of the zip file must match the top\-level folder\.

For more information about creating Lambda deployment packages, see [Creating a Deployment Package \(Node\.js\)](http://docs.aws.amazon.com/lambda/latest/dg/nodejs-create-deployment-pkg.html) and [Creating a Deployment Package \(Java\)](http://docs.aws.amazon.com/lambda/latest/dg/lambda-java-how-to-create-deployment-package.html)\.

#### Configuring a Lambda Function<a name="es-aws-integrations-s3-lambda-es-function-configuration"></a>

Use AWS Lambda to create and configure your Lambda function\. To do that, you can use either the AWS CLI or the AWS Lambda console\. For a tutorial about creating and configuring a Lambda function using the AWS CLI, see [Using AWS Lambda with Amazon S3](http://docs.aws.amazon.com/lambda/latest/dg/with-s3-example.html)\. For configuration settings on the AWS Lambda console, see the following table\. 

**Note**  
For more information about creating and configuring a Lambda function, see the [AWS Lambda Developer Guide](http://docs.aws.amazon.com/lambda/latest/dg/welcome.html)\. 


****  

| Function Configuration | Description | 
| --- | --- | 
| IAM Execution Role | The name of the IAM role that is used to execute actions on Amazon S3\. While creating your Lambda function, the Lambda console automatically opens the IAM console to help you create the execution role\.  Later, you also must attach an IAM access policy to this role that permits Lambda to add data to your domain\. For details, see [Granting Authorization to Add Data to Your Amazon ES Domain](#es-aws-integrations-s3-lambda-es-authorizations)\. | 
| Event Source | Specifies the S3 bucket as the event source for the Lambda function\. For instructions, see the [Using AWS Lambda with Amazon S3](http://docs.aws.amazon.com/lambda/latest/dg/with-s3-example.html) tutorial\. AWS Lambda automatically adds the necessary permissions for Amazon S3 to invoke your Lambda function from this event source\. Optionally, specify a file suffix to filter what kinds of files, such as \.log, trigger the Lambda function\. | 
| Handler | The name of the file that contains the application source code, but with the \.handler file suffix\. For example, if your application source code resides in a file named s3\_lambda\_es\.js, you must configure the handler as s3\_lambda\_es\.handler\. For more information, see [Getting Started](http://docs.aws.amazon.com/lambda/latest/dg/getting-started.html) in the AWS Lambda Developer Guide\. Amazon ES provides a sample application in Node\.js that you can download to get started: [Lambda Sample Code for Amazon ES](https://github.com/awslabs/amazon-elasticsearch-lambda-samples)\. | 
| Timeout | The length of time that Lambda should wait before canceling an invocation request\. The default value of three seconds is too short for the Amazon ES use case\. We recommend configuring your timeout for 10 seconds\. | 

For more function configuration details, see [Configuring a Lambda Function](#es-aws-integrations-s3-lambda-es-function-configuration) in this guide\. For general information, see [Lambda Functions](http://docs.aws.amazon.com/lambda/latest/dg/lambda-introduction-function.html) in the *AWS Lambda Developer Guide*\.

#### Granting Authorization to Add Data to Your Amazon ES Domain<a name="es-aws-integrations-s3-lambda-es-authorizations"></a>

When you choose **S3 Execution Role** as the IAM role to execute actions on S3, Lambda opens the IAM console and helps you to create a new execution role\. Lambda automatically adds the necessary permissions to invoke your Lambda function from this event source\. After you create the role, open it in the IAM console and attach the following IAM access policy to the role\. This grants permissions to Lambda to stream data to Amazon ES:

```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": [
                "es:*"
            ],
            "Effect": "Allow",
            "Resource": "arn:aws:es:us-west-2:123456789012:domain/streaming-logs/*"
        }
    ]
}
```

For more information about attaching IAM access policies to roles, see [Tutorial: Create and Attach Your First Customer Managed Policy](http://docs.aws.amazon.com/IAM/latest/UserGuide/walkthru_managed-policies.html) in the *IAM User Guide*\.

## Loading Streaming Data into Amazon ES from Amazon Kinesis<a name="es-aws-integrations-kinesis-lambda-es"></a>

You can load streaming data from Amazon Kinesis to Amazon ES\. This integration relies on AWS Lambda as an event handler in the cloud\. Amazon Kinesis requires Lambda to poll your Amazon Kinesis stream to determine whether it has new data that will automatically invoke your Lambda function\. After your Lambda function finishes processing any new data, it streams the data to your Amazon ES domain\.

At a high level, setting up to streaming data to Amazon ES requires the following steps:

1. [Creating a Lambda deployment package](#es-aws-integrations-kinesis-lambda-es-setting-up-deployment-package)\.

1. [Configuring a Lambda function](#es-aws-integrations-kinesis-lambda-es-setting-up-function)\.

1. [Granting authorization to add data to your Amazon ES domain](#es-aws-integrations-kinesis-lambda-es-setting-up-authorizations)\.

You also must create an Amazon Kinesis stream and an Amazon ES domain\. Setting up this integration path has the following prerequisites\.


****  

| Prerequisite | Description | 
| --- | --- | 
| Amazon Kinesis Stream | The event source for your Lambda function\. For instructions about creating Amazon Kinesis streams, see [Amazon Kinesis Streams](https://docs.aws.amazon.com/kinesis/latest/dev/amazon-kinesis-streams.html)\. | 
| Elasticsearch Domain | The destination for data after it is processed by the application code in your Lambda function\. For more information, see [Creating Amazon ES Domains](es-createupdatedomains.md#es-createdomains) in this guide\. | 
| Lambda Function | The Java or Node\.js application code that runs when Amazon Kinesis pushes an event notification to Lambda\. Amazon ES provides a sample application in Node\.js, kinesis\_lambda\_es\.js, that you can download to get started: [Lambda Sample Code for Amazon ES](https://github.com/awslabs/amazon-elasticsearch-lambda-samples)\.  | 
| Lambda Deployment Package | A \.zip file that consists of your Java or Node\.js application code and any dependencies\. For information about the required folder hierarchy, see [Creating a Lambda Deployment Package](#es-aws-integrations-s3-lambda-es-deployment-package)\. For general information about creating Lambda deployment packages, see [Creating a Deployment Package \(Node\.js\)](http://docs.aws.amazon.com/lambda/latest/dg/nodejs-create-deployment-pkg.html) and [Creating a Deployment Package \(Java\)](http://docs.aws.amazon.com/lambda/latest/dg/lambda-java-how-to-create-deployment-package.html)\. | 
| Amazon ES Authorization |  An IAM access policy that permits Lambda to add data to your domain\. Attach the policy to the Amazon Kinesis execution role that you create as part of your Lambda function\. For details, see [Granting Authorization to Add Data to Your Amazon ES Domain](#es-aws-integrations-s3-lambda-es-authorizations)\.  | 

### Setting Up to Load Streaming Data into Amazon ES from Amazon Kinesis<a name="es-aws-integrations-kinesis-lambda-es-setting-up"></a>

This section provides more details about setting up the prerequisites for loading streaming data from Amazon Kinesis into Amazon ES\. After you finish configuring the integration, Lambda automatically streams data to your Amazon ES domain whenever new data is added to your Amazon Kinesis stream\.

#### Creating a Lambda Deployment Package<a name="es-aws-integrations-kinesis-lambda-es-setting-up-deployment-package"></a>

Create a \.zip file that contains your Lambda application code and any dependencies\.

To create a deployment package:

1. Create a directory structure like the following:

   ```
   eslambda
       \node_modules
   ```

   You can use any name for the top\-level folder rather than `eslambda`\. However, you must name the subfolder `node_modules`\.

1. Place your application source code in the `eslambda` folder\.

1. Add or edit the following global variables in your sample application:
   + `endpoint`, the Amazon ES domain endpoint\.
   + `region`, the AWS Region in which you created your Amazon ES domain\.
   + `index`, the name of the Amazon ES index to use for data that is streamed from Amazon Kinesis\.
   + `doctype`, the Amazon ES document type of the streamed data\. For more information, see [Mapping Types](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html#all-mapping-types) in the Elasticsearch documentation\.

   The following example from `kinesis_lambda_es.js` configures the sample application to use the `streaming-logs` Amazon ES domain endpoint in the us\-east\-1 AWS Region\.

   ```
   /* Globals */
   var esDomain = {
       endpoint: 'search-streaming-logs-okga24ftzsbz2a2hzhsqw73jpy.us-east-1.es.example.com',
       region: 'us-east-1',
       index: 'streaming-logs',
       doctype: 'apache'
   };
   ```

1. Install any dependencies that are required by your application\.

   For example, if you use Node\.js, you must execute the following command for each `require` statement in your application code:

   ```
   npm install <dependency>
   ```

1. Verify that all runtime dependencies that are required by your application code are located in the `node_modules` folder\.

1. Execute the following command to package the application code and dependencies:

   ```
   zip -r eslambda.zip *
   ```

   The name of the zip file must match the top\-level folder\.

For more information about creating Lambda deployment packages, see [Creating a Deployment Package \(Node\.js\)](http://docs.aws.amazon.com/lambda/latest/dg/nodejs-create-deployment-pkg.html) and [Creating a Deployment Package \(Java\)](http://docs.aws.amazon.com/lambda/latest/dg/lambda-java-how-to-create-deployment-package.html)\.

#### Configuring a Lambda Function<a name="es-aws-integrations-kinesis-lambda-es-setting-up-function"></a>

Use AWS Lambda to create and configure your Lambda function\. To do that, you can use either the AWS CLI or the [Creating a Deployment Package \(Node\.js\)](http://docs.aws.amazon.com/lambda/latest/dg/nodejs-create-deployment-pkg.html) console\. For a tutorial about creating and configuring a Lambda function using the AWS CLI, see [Using AWS Lambda with Amazon Kinesis](http://docs.aws.amazon.com/lambda/latest/dg/with-kinesis.html)\. For configuration settings on the AWS Lambda console, see the following table\. 

**Note**  
 For more information about creating and configuring a Lambda function, see the [Getting Started](http://docs.aws.amazon.com/lambda/latest/dg/java-gs.html#java-gs-create-lambda-function) tutorial in the *AWS Lambda Developer Guide*\. 


****  

| Configuration | Description | 
| --- | --- | 
| Amazon Kinesis stream | The event source of your Lambda function\. For instructions, see [Amazon Kinesis Streams](http://docs.aws.amazon.com/kinesis/latest/dev/amazon-kinesis-streams.html)\. | 
| IAM execution role | The name of the IAM role that is used to execute actions on Amazon Kinesis\. While configuring your Lambda function, the Lambda console automatically opens the IAM console to help you create the execution role\.  Later, you also must attach an IAM access policy to this role that permits Lambda to send data to your Amazon ES domain\. For details, see [Granting Authorization to Add Data to Your Amazon ES Domain](#es-aws-integrations-kinesis-lambda-es-setting-up-authorizations)\. | 
| Handler | The name of the file that contains the application source code, but with the \.handler file suffix\. For example, if your application source code is in a file named kinesis\_lambda\_es\.js, you must configure the handler as kinesis\_lambda\_es\.handler\. For more information, see [Lambda Function Handler](http://docs.aws.amazon.com/lambda/latest/dg/nodejs-prog-model-handler.html)\. Amazon ES provides a sample application in Node\.js that you can download to get started: [Lambda Sample Code for Amazon ES](https://github.com/awslabs/amazon-elasticsearch-lambda-samples)\. | 
| Timeout | The length of time that Lambda should wait before canceling an invocation request\. The default value of three seconds is too short for this use case\. We recommend configuring your timeout for 10 seconds\. | 

For more information, see [Lambda Functions](http://docs.aws.amazon.com/lambda/latest/dg/lambda-introduction-function.html) in the *AWS Lambda Developer Guide*\.

#### Granting Authorization to Add Data to Your Amazon ES Domain<a name="es-aws-integrations-kinesis-lambda-es-setting-up-authorizations"></a>

When you choose **Kinesis Execution Role** as the IAM role to execute actions on Amazon Kinesis, Lambda opens the IAM console and requires you to create a new execution role\. Lambda automatically adds the necessary permissions to invoke your Lambda function from this event source\. After you create the role, open it in the IAM console and attach the following IAM access policy to the role so that Lambda has permissions to stream data to Amazon ES:

```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": [
                "es:*"
            ],
            "Effect": "Allow",
            "Resource": "arn:aws:es:us-west-2:123456789012:domain/streaming-logs/*"
        }
    ]
}
```

For more information about attaching IAM access policies to roles, see [Tutorial: Create and Attach Your First Customer Managed Policy](http://docs.aws.amazon.com/IAM/latest/UserGuide/walkthru_managed-policies.html) in the *IAM User Guide*\.

## Loading Streaming Data into Amazon ES from Amazon Kinesis Firehose<a name="es-aws-integrations-fh"></a>

Amazon Kinesis supports Amazon ES a delivery destination\. For instructions on how to load streaming data into Amazon ES, see [Creating an Amazon Kinesis Firehose Delivery Stream](http://docs.aws.amazon.com/firehose/latest/dev/basic-create.html) and [Choose Amazon ES for your destination](http://docs.aws.amazon.com/firehose/latest/dev/create-destination.html#create-destination-elasticsearch) in the *Amazon Kinesis Firehose Developer Guide*\.

You might need to perform transforms on your data before loading it into Amazon ES\. To learn more about using Lambda functions perform this task, see [Data Transformation](http://docs.aws.amazon.com/firehose/latest/dev/data-transformation.html) in the *Amazon Kinesis Firehose Developer Guide*\.

As you configure a delivery stream, Kinesis Firehose features a "one click" IAM role that gives it the resource access it needs to send data to Amazon ES, back up data on Amazon S3, and transform data using Lambda\. Because of the complexity involved in creating such a role manually, we recommend using the provided role\.

## Loading Streaming Data into Amazon ES from Amazon DynamoDB<a name="es-aws-integrations-dynamodb-es"></a>

You can load streaming data from Amazon DynamoDB Streams to your Amazon ES domain\. To do that, use the [Logstash input plugin for Amazon DynamoDB](https://github.com/awslabs/logstash-input-dynamodb) and the [logstash\-output\-amazon\-es](https://github.com/awslabs/logstash-output-amazon_es) plugin, which signs and exports Logstash events to Amazon ES\. 

## Loading Streaming Data into Amazon ES from Amazon CloudWatch<a name="es-aws-integrations-cloudwatch-es"></a>

You can load streaming data from CloudWatch Logs to your Amazon ES domain by using a CloudWatch Logs subscription\. For information about Amazon CloudWatch subscriptions, see [Real\-time Processing of Log Data with Subscriptions](http://docs.aws.amazon.com/AmazonCloudWatch/latest/DeveloperGuide/Subscriptions.html)\. For configuration information, see [Streaming CloudWatch Logs Data to Amazon Elasticsearch Service](http://docs.aws.amazon.com/AmazonCloudWatch/latest/DeveloperGuide/CWL_ES_Stream.html) in the *Amazon CloudWatch Developer Guide\.*

## Loading Data into Amazon ES from AWS IoT<a name="es-aws-integrations-cloudwatch-iot"></a>

You can send data from AWS IoT using [rules](http://docs.aws.amazon.com/iot/latest/developerguide/iot-rules.html)\. To learn more, see [Amazon ES Action](http://docs.aws.amazon.com/iot/latest/developerguide/elasticsearch-rule.html) in the *AWS IoT Developer Guide*\.