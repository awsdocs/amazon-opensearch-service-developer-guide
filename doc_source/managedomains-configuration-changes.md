# Making configuration changes in Amazon OpenSearch Service<a name="managedomains-configuration-changes"></a>

Amazon OpenSearch Service uses a *blue/green* deployment process when updating domains\. Blue/green typically refers to the practice of running two production environments, one live and one idle, and switching the two as you make software changes\. In the case of OpenSearch Service, it refers to the practice of creating a new environment for domain updates and routing users to the new environment after those updates are complete\. The practice minimizes downtime and maintains the original environment in the event that deployment to the new environment is unsuccessful\.

## Changes that usually cause blue/green deployments<a name="bg"></a>

The following operations cause blue/green deployments:
+ Changing instance type
+ Enabling fine\-grained access control
+ Performing service software updates
+ If your domain *doesn't* have dedicated master nodes, changing data instance count
+ Enabling or disabling dedicated master nodes
+ Changing dedicated master node count or instance type
+ Enabling or disabling Multi\-AZ
+ Changing storage type, volume type, or volume size
+ Choosing different VPC subnets
+ Adding or removing VPC security groups
+ Enabling or disabling Amazon Cognito authentication for OpenSearch Dashboards
+ Choosing a different Amazon Cognito user pool or identity pool
+ Modifying advanced settings
+ Enabling or disabling the publication of error logs, audit logs, or slow logs to CloudWatch
+ Upgrading to a new OpenSearch version
+ Enabling or disabling **Require HTTPS**
+ Enabling encryption of data at rest or node\-to\-node encryption
+ Enabling or disabling UltraWarm or cold storage
+ Disabling Auto\-Tune and rolling back its changes
+ Modifying the custom endpoint

## Changes that usually don't cause blue/green deployments<a name="nobg"></a>

In *most* cases, the following operations do not cause blue/green deployments:
+ Changing access policy
+ Changing the automated snapshot hour
+ Enabling Auto\-Tune or disabling it without rolling back its changes
+ If your domain has dedicated master nodes, changing data node or UltraWarm node count

There are some exceptions depending on your service software version\. If you want to be absolutely sure that a change will not cause a blue/green deployment, [perform a dry run](#dryrun) before updating your domain\.

## Determine whether a change will cause a blue/green deployment<a name="dryrun"></a>

You can conduct a dry run of a planned configuration change to determine whether it will cause a blue/green deployment\. When making a change in the console, you're prompted to choose **Run analysis**, and OpenSearch Service calculates the type of deployment the change will cause\.

You can also perform a dry run analysis through the configuration API\. For example, this [UpdateDomainConfig](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_UpdateDomainConfig.html) request tests the deployment type caused by enabling UltraWarm:

```
POST https://es.us-east-1.amazonaws.com/2021-01-01/opensearch/domain/my-domain/config
{
   "ClusterConfig": {
    "WarmCount": 3,
    "WarmEnabled": true,
    "WarmType": "ultrawarm1.large.search"
   },
   "DryRun": true
}
```

The request returns the type of deployment the change will cause but doesn't actually perform the update:

```
{
   "ClusterConfig": {
     ...
    },
   "DryRunResults": {
      "DeploymentType": "Blue/Green",
      "Message": "This change will require a blue/green deployment."
    }
}
```

Possible deployment types are:
+ `Blue/Green` \- The change will cause a blue/green deployment\.
+ `DynamicUpdate` \- The change won't cause a blue/green deployment\.
+ `Undetermined` \- The domain is still in a processing state, so the deployment type can't be determined\.
+ `None` \- No configuration change\.

## Initiating a configuration change<a name="initiate"></a>

When you initiate a configuration change, the domain state changes to **Processing** until OpenSearch Service has created a new environment with the latest [service software](service-software.md), at which point it changes back to **Active**\. During certain service software updates, the state remains **Active** the whole time\. In both cases, you can review the cluster health and Amazon CloudWatch metrics and see that the number of nodes in the cluster temporarily increases—often doubling—while the domain update occurs\. In the following illustration, you can see the number of nodes doubling from 11 to 22 during a configuration change and returning to 11 when the update is complete\.

![\[Number of nodes doubling from 11 to 22 during a domain configuration change.\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/NodesDoubled.png)

This temporary increase can strain the cluster's [dedicated master nodes](managedomains-dedicatedmasternodes.md), which suddenly might have many more nodes to manage\. It can also increase search and indexing latencies as OpenSearch Service copies data from the old cluster to the new one\. It's important to maintain sufficient capacity on the cluster to handle the overhead that is associated with these blue/green deployments\.

**Important**  
You do *not* incur any additional charges during configuration changes and service maintenance\. You're billed only for the number of nodes that you request for your cluster\. For specifics, see [Charges for configuration changes](#managedomains-config-charges)\.

To prevent overloading dedicated master nodes, you can [monitor usage with the Amazon CloudWatch metrics](managedomains-cloudwatchmetrics.md)\. For recommended maximum values, see [Recommended CloudWatch alarms for Amazon OpenSearch Service](cloudwatch-alarms.md)\.

## Stages of a configuration change<a name="managedomains-config-stages"></a>

After you initiate a configuration change, OpenSearch Service goes through a series of steps to update your domain\. You can view the progress of the configuration change under **Domain status** in the console\. The exact steps that an update goes through depends on the type of change you're making\. You can also monitor a configuration change using the [DescribeDomainChangeProgress](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_DescribeDomainChangeProgress.html)> API operation\.

In some cases, such as during service software updates, you won't see progress information until the blue/green deployment actually starts\. During this time the domain status is `Pending Updates`\.

The following are possible stages an update can go through during a configuration change: 


| Stage name | Description | 
| --- | --- | 
|  Validation  |  Validating that the domain is eligible for an update, and surfacing [validation issues](#validation) if necessary\.  | 
|  Creating a new environment  |  Completing the necessary prerequisites and creating required resources to start the blue/green deployment\.  | 
|  Provisioning new nodes  |  Creating a new set of instances in the new environment\.  | 
|  Traffic routing on new nodes  |  Redirecting traffic to the newly created data nodes\.  | 
|  Traffic routing on old nodes  | Disabling traffic on the old data nodes\. | 
|  Preparing nodes for removal  |  Preparing to remove nodes\. This step only happens when you're downscaling your domain \(for example, from 8 nodes to 6 nodes\)\.  | 
|  Copying shards to new nodes  |  Moving shards from the old nodes to the new nodes\.  | 
|  Terminating nodes  | Terminating and deleting old nodes after shards are removed\. | 
|  Deleting older resources  |  Deleting resources associated with the old environment \(e\.g\. load balancer\)\.  | 
|  Dynamic update  |  Displayed when the update does not require a blue/green deployment and can be dynamically applied\.  | 

## Troubleshooting validation errors<a name="validation"></a>

When you initiate a configuration change or perform an OpenSearch or Elasticsearch version upgrade, OpenSearch Service first performs a series of validation checks to ensure that your domain is eligible for an update\. If any of these checks fail, you receive a notification in the console containing the specific issues that you must fix before updating your domain\. The following table lists the possible domain issues that OpenSearch Service might surface, and steps to resolve them\.


| Issue | Error code | Troubleshooting steps | 
| --- | --- | --- | 
| Security group not found | SecurityGroupNotFound |  The security group associated with your OpenSearch Service domain does not exist\. To resolve this issue, [create a security group](https://docs.aws.amazon.com/vpc/latest/userguide/VPC_SecurityGroups.html#creating-security-groups) with the specified name\.  | 
| Subnet not found | SubnetNotFound |  The subnet associated with your OpenSearch Service domain does not exist\. To resolve this issue, [create a subnet](https://docs.aws.amazon.com/vpc/latest/userguide/working-with-subnets.html#create-subnets) in your VPC\.  | 
| Service\-linked role not configured | SLRNotConfigured | The [service\-linked role](slr.md) for OpenSearch Service is not configured\. The service\-linked role is predefined by OpenSearch Service and includes all the permissions the service requires to call other AWS services on your behalf\. If the role doesn't exist, you might need to [create it manually](slr-aos.md#create-slr)\. | 
| Not enough IP addresses | InsufficientFreeIPsForSubnets |  One or more of your VPC subnets don't have enough IP addresses to update your domain\. To calculate how many IP addresses you need, see [Reserving IP addresses in a VPC subnet](vpc.md#reserving-ip-vpc-endpoints)\.  | 
| Cognito user pool doesn't exist | CognitoUserPoolNotFound |  OpenSearch Service can't find the Amazon Cognito user pool\. Confirm that you created one and have the correct ID\. To find the ID, you can use the Amazon Cognito console or the following AWS CLI command: <pre>aws cognito-idp list-user-pools --max-results 60 --region us-east-1</pre>  | 
| Cognito identity pool doesn't exist | CognitoIdentityPoolNotFound |  OpenSearch Service can't find the Cognito identity pool\. Confirm that you created one and have the correct ID\. To find the ID, you can use the Amazon Cognito console or the following AWS CLI command: <pre>aws cognito-identity list-identity-pools --max-results 60 --region us-east-1</pre>  | 
| Cognito domain not found for user pool | CognitoDomainNotFound |  The user pool does not have a domain name\. You can configure one using the Amazon Cognito console or the following AWS CLI command: <pre>aws cognito-idp create-user-pool-domain --domain my-domain --user-pool-id id</pre>  | 
| Cognito role not configured | CognitoRoleNotConfigured |  The IAM role that grants OpenSearch Service permission to configure the Amazon Cognito user and identity pools, and use them for authentication, is not configured\. Configure the role with an appropriate permission set and trust relationship\. You can use the console, which creates the default [CognitoAccessForAmazonOpenSearch](cognito-auth.md#cognito-auth-role) role for you, or you can manually configure a role using the AWS CLI or the AWS SDK\.  | 
| Unable to describe user pool | UserPoolNotDescribable | The specified Amazon Cognito role doesn't have permission to describe the user pool associated with your domain\. Make sure the role permissions policy allows the cognito\-identity:DescribeUserPool action\. See [About the CognitoAccessForAmazonOpenSearch role](cognito-auth.md#cognito-auth-role) for the full permissions policy\. | 
| Unable to describe identity pool | IdentityPoolNotDescribable | The specified Amazon Cognito role doesn't have permission to describe the identity pool associated with your domain\. Make sure the role permissions policy allows the cognito\-identity:DescribeIdentityPool action\. See [About the CognitoAccessForAmazonOpenSearch role](cognito-auth.md#cognito-auth-role) for the full permissions policy\. | 
| Unable to describe user and identity pool | CognitoPoolsNotDescribable | The specified Amazon Cognito role doesn't have permission to describe the user and identity pools associated with your domain\. Make sure the role permissions policy allows the cognito\-identity:DescribeIdentityPool and cognito\-identity:DescribeUserPool actions\. See [About the CognitoAccessForAmazonOpenSearch role](cognito-auth.md#cognito-auth-role) for the full permissions policy\. | 
| KMS key not enabled | KMSKeyNotEnabled |  The AWS Key Management Service \(AWS KMS\) key used to encrypt your domain is disabled\. [Re\-enable the key](https://docs.aws.amazon.com/kms/latest/developerguide/enabling-keys) immediately\.  | 
| Custom certificate not in ISSUED state | InvalidCertificate |  If your domain uses a custom endpoint, you secure it by either generating an SSL certificate in AWS Certificate Manager \(ACM\) or importing one of your own\. The certificate status must be **Issued**\. If you receive this error, [check the status of your certificate](https://docs.aws.amazon.com/acm/latest/userguide/gs-acm-describe.html) in the ACM console\. If the status is Expired, Failed, Inactive, or Pending validation, see the ACM [troubleshooting documentation](https://docs.aws.amazon.com/acm/latest/userguide/troubleshooting.html) to resolve the issue\.  | 
| Not enough capacity to launch chosen instance type | InsufficientInstanceCapacity |  The requested instance type capacity is not available\. For example, you might have requested five `i3.16xlarge.search` nodes, but OpenSearch Service doesn't have enough `i3.16xlarge.search` hosts available, so the request can't be fulfilled\. Check the [supported instance types](supported-instance-types.md) in OpenSearch Service and choose a different instance type\.  | 
| Red indexes in cluster | RedCluster |  One or more indexes in your cluster have a red status, leading to an overall red cluster status\. To troubleshoot and remediate this issue, see [Red cluster status](handling-errors.md#handling-errors-red-cluster-status)\.  | 
| Memory circuit breaker, too many requests | TooManyRequests |  There are too many search and write requests to your domain, so OpenSearch Service can't update its configuration\. You can reduce the number of requests, scale instances vertically up to 64 GiB of RAM, or scale horizontally by adding instances\.  | 
| New configuration can't hold data \(low disk space\) | InsufficientStorageCapacity |  The configured storage size can't hold all of the data on your domain\. To resolve this issue, [choose a larger volume](limits.md#ebsresource), [delete unused indexes](https://opensearch.org/docs/latest/opensearch/rest-api/index-apis/delete-index/), or increase the number of nodes in the cluster to immediately free up disk space\.  | 
| Shards pinned to specific nodes | ShardMovementBlocked |  One or more indexes in your domain are attached to specific nodes and can't be reassigned\. This most likely happened because you configured shard allocation filtering, which lets you specify which nodes are allowed to host the shards of a particular index\. To resolve this issue, remove shard allocation filters from all affected indexes: <pre>PUT my-index/_settings<br />{  <br />  "settings": {    <br />    "index.routing.allocation.require._name": null  <br />  }<br />}</pre>  | 
| New configuration can't hold all shards \(shard count\) | TooManyShards | The shard count on your domain is too high, which prevents OpenSearch Service from moving them to the new configuration\. To resolve this issue, scale your domain horizonally by adding nodes of the same configuration type as your current cluster nodes\. Note that the [maximum EBS volume size](limits.md#ebsresource) depends on the node's instance type\.To prevent this issue in the future, see [Choosing the number of shards](sizing-domains.md#bp-sharding) and define a sharding strategy that is appropriate for your use case\. | 

## Charges for configuration changes<a name="managedomains-config-charges"></a>

If you change the configuration for a domain, OpenSearch Service creates a new cluster as described in [Making configuration changes in Amazon OpenSearch Service](#managedomains-configuration-changes)\. During the migration of old to new, you incur the following charges:
+ If you change the instance type, you're charged for both clusters for the first hour\. After the first hour, you're only charged for the new cluster\. EBS volumes aren't charged twice because they're part of your cluster, so their billing follows instance billing\.

  **Example:** You change the configuration from three `m3.xlarge` instances to four `m4.large` instances\. For the first hour, you're charged for both clusters \(3 \* `m3.xlarge` \+ 4 \* `m4.large`\)\. After the first hour, you're charged only for the new cluster \(4 \* `m4.large`\)\.
+ If you don't change the instance type, you're charged only for the largest cluster for the first hour\. After the first hour, you're charged only for the new cluster\.

  **Example:** You change the configuration from six `m3.xlarge` instances to three `m3.xlarge` instances\. For the first hour, you're charged for the largest cluster \(6 \* `m3.xlarge`\)\. After the first hour, you're charged only for the new cluster \(3 \* `m3.xlarge`\)\.