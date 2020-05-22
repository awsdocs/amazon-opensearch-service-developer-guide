# Service Software Updates<a name="es-service-software"></a>

**Note**  
Service software updates differ from Elasticsearch version upgrades\. For information about upgrading to a later version of Elasticsearch, see [Upgrading Elasticsearch](es-version-migration.md)\.

Amazon ES regularly releases system software updates that add features or otherwise improve your domains\. The console is the easiest way to see if an update is available\. When new service software becomes available, you can request an update to your domain and benefit from new features more quickly\. You might also want to start the update at a low traffic time\.

Some updates are required\. Others are optional\.
+ If you take no action on required updates, we still update the service software automatically after a certain timeframe \(typically two weeks\)\.
+ If the console does not include an automatic deployment date, the update is optional\.

Your domain might be ineligible for a service software update if it is in any of the states that are shown in the following table\.


| State | Description | 
| --- | --- | 
| Domain in processing | The domain is in the middle of a configuration change\. Check update eligibility after the operation completes\. | 
| Red cluster status | One or more indices in the cluster is red\. For troubleshooting steps, see [Red Cluster Status](aes-handling-errors.md#aes-handling-errors-red-cluster-status)\. | 
| High error rate | The Elasticsearch cluster is returning a large number of 5xx errors when attempting to process requests\. This problem is usually the result of too many simultaneous read or write requests\. Consider reducing traffic to the cluster or scaling your domain\. | 
| Split brain | Split brain means that your Elasticsearch cluster has more than one master node and has split into two clusters that never will rejoin on their own\. You can avoid split brain by using the recommended number of [dedicated master nodes](es-managedomains-dedicatedmasternodes.md)\. For help recovering from split brain, contact [AWS Support](https://console.aws.amazon.com/support/home)\. | 
| Amazon Cognito integration issue | Your domain uses [authentication for Kibana](es-cognito-auth.md), and Amazon ES can't find one or more Amazon Cognito resources\. This problem usually occurs if the Amazon Cognito user pool is missing\. To correct the issue, recreate the missing resource and configure the Amazon ES domain to use it\. | 
| Other Amazon ES service issue | Issues with Amazon ES itself might cause your domain to display as ineligible for an update\. If none of the previous conditions apply to your domain and the problem persists for more than a day, contact [AWS Support](https://console.aws.amazon.com/support/home)\. | 

**To request a service software update \(console\)**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. In the navigation pane, under **My domains**, choose the domain that you want to update\.

1. For **Service software release**, use the documentation link to compare your current version to the latest version\. Then choose **Update**\.

**To request a service software update \(AWS CLI and AWS SDKs\)**

You can use the following commands to see if an update is available, check upgrade eligibility, and request an update:
+ `describe-elasticsearch-domain` \(`DescribeElasticsearchDomain`\)
+ `start-elasticsearch-service-software-update` \(`StartElasticsearchServiceSoftwareUpdate`\)

For more information, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/) and [Amazon Elasticsearch Service Configuration API Reference](es-configuration-api.md)\.

**Tip**  
After requesting an update, you might have a narrow window of time in which you can cancel it\. Use the console or `stop-elasticsearch-service-software-update` \(`StopElasticsearchServiceSoftwareUpdate`\) command\.