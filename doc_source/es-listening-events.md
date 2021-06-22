# Tutorial: Listening for Amazon Elasticsearch Service EventBridge events<a name="es-listening-events"></a>

In this tutorial, you set up a simple AWS Lambda function that listens for Amazon Elasticsearch Service \(Amazon ES\) events and writes them to a CloudWatch Logs log stream\.

## Prerequisites<a name="es-listening-prereq"></a>

This tutorial assumes that you have an existing Amazon ES domain\. If you haven't created a domain, follow the steps in [Creating and managing Amazon Elasticsearch Service domains](es-createupdatedomains.md) to create one\.

## Step 1: Create the Lambda function<a name="es-listening-lambda"></a>

In this procedure, you create a simple Lambda function to serve as a target for Amazon ES event messages\.

**To create a target Lambda function**

1. Open the AWS Lambda console at [https://console\.aws\.amazon\.com/lambda/](https://console.aws.amazon.com/lambda/)\.

1. Choose **Create function ** and **Author from scratch**\.

1. For **Function name**, enter **es\-event\-handler**\. 

1. For **Runtime**, choose **Python 3\.8**\.

1. Choose **Create function**\.

1. In the **Function code** section, edit the sample code to match the following example:

   ```
   import json
   
   def lambda_handler(event, context):
       if event["source"] != "aws.es":
           raise ValueError("Function only supports input from events with a source type of: aws.es")
   
       print(json.dumps(event))
   ```

   This is a simple Python 3\.8 function that prints the events sent by Amazon ES\. If everything is configured correctly, at the end of this tutorial, the event details appear in the CloudWatch Logs log stream that's associated with this Lambda function\.

1. Choose **Deploy**\.

## Step 2: Register an event rule<a name="es-listening-rule"></a>

In this step, you create an EventBridge rule that captures events from your Amazon ES domains\. This rule captures all events within the account where it's defined\. The event messages themselves contain information about the event source, including the domain from which it originated\. You can use this information to filter and sort events programmatically\.

**To create an EventBridge rule** 

1. Open the EventBridge console at [https://console\.aws\.amazon\.com/events/](https://console.aws.amazon.com/events/)\.

1. Choose **Create rule**\.

1. Name the rule **es\-event\-rule**\.

1. For **Define pattern**, choose **Event pattern**, then choose **Custom pattern**\.

1. Paste the following event pattern into the text area:

   ```
   {
     "source": ["aws.es"]
   }
   ```

   Press **Save** after adding the pattern\. This pattern applies across all of your Amazon ES domains and to every Amazon ES event\. Alternatively, you can create a more specific pattern to filter out some results\.

1. Leave the target as **Lambda function** and choose **es\-event\-handler** from the dropdown\.

1. Choose **Create**\.

## Step 3: Test your configuration<a name="es-listening-test"></a>

The next time you receive a notification in the **Notifications** section of the Amazon ES console, if everything is configured properly, your Lambda function is triggered and it writes the event data to a CloudWatch Logs log stream for the function\.

**To test your configuration**

1. Open the CloudWatch console at [https://console\.aws\.amazon\.com/cloudwatch/](https://console.aws.amazon.com/cloudwatch/)\.

1. On the navigation pane, choose **Logs** and select the log group for your Lambda function \(for example, **/aws/lambda/es\-event\-handler**\)\.

1. Select a log stream to view the event data\.