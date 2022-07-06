# Tutorial: Listening for Amazon OpenSearch Service EventBridge events<a name="listening-events"></a>

In this tutorial, you set up a simple AWS Lambda function that listens for Amazon OpenSearch Service events and writes them to a CloudWatch Logs log stream\.

## Prerequisites<a name="listening-prereq"></a>

This tutorial assumes that you have an existing OpenSearch Service domain\. If you haven't created a domain, follow the steps in [Creating and managing Amazon OpenSearch Service domains](createupdatedomains.md) to create one\.

## Step 1: Create the Lambda function<a name="listening-lambda"></a>

In this procedure, you create a simple Lambda function to serve as a target for OpenSearch Service event messages\.

**To create a target Lambda function**

1. Open the AWS Lambda console at [https://console\.aws\.amazon\.com/lambda/](https://console.aws.amazon.com/lambda/)\.

1. Choose **Create function ** and **Author from scratch**\.

1. For **Function name**, enter **event\-handler**\. 

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

   This is a simple Python 3\.8 function that prints the events sent by OpenSearch Service\. If everything is configured correctly, at the end of this tutorial, the event details appear in the CloudWatch Logs log stream that's associated with this Lambda function\.

1. Choose **Deploy**\.

## Step 2: Register an event rule<a name="listening-rule"></a>

In this step, you create an EventBridge rule that captures events from your OpenSearch Service domains\. This rule captures all events within the account where it's defined\. The event messages themselves contain information about the event source, including the domain from which it originated\. You can use this information to filter and sort events programmatically\.

**To create an EventBridge rule** 

1. Open the EventBridge console at [https://console\.aws\.amazon\.com/events/](https://console.aws.amazon.com/events/)\.

1. Choose **Create rule**\.

1. Name the rule **event\-rule**\.

1. Choose **Next**\.

1. For the event pattern, select **AWS services**, **Amazon OpenSearch Service**, and **All Events**\. This pattern applies across all of your OpenSearch Service domains and to every OpenSearch Service event\. Alternatively, you can create a more specific pattern to filter out some results\.

1. Press **Next**\.

1. For the target, choose **Lambda function**\. In the function dropdown, choose **event\-handler**\.

1. Press **Next**\.

1. Skip the tags and press **Next** again\.

1. Review the configuration and choose **Create rule**\.

## Step 3: Test your configuration<a name="listening-test"></a>

The next time you receive a notification in the **Notifications** section of the OpenSearch Service console, if everything is configured properly, your Lambda function is triggered and it writes the event data to a CloudWatch Logs log stream for the function\.

**To test your configuration**

1. Open the CloudWatch console at [https://console\.aws\.amazon\.com/cloudwatch/](https://console.aws.amazon.com/cloudwatch/)\.

1. On the navigation pane, choose **Logs** and select the log group for your Lambda function \(for example, **/aws/lambda/event\-handler**\)\.

1. Select a log stream to view the event data\.