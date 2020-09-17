# Cross\-Cluster Search for Amazon Elasticsearch Service<a name="cross-cluster-search"></a>

Cross\-cluster search for Amazon Elasticsearch Service lets you perform queries and aggregations across multiple connected domains\.

It often makes more sense to use multiple smaller domains instead of a single large domain, especially when you're running different types of workloads\.

Workload\-specific domains enable you to:
+ Optimize each domain by choosing instance types for specific workloads\.
+ Establish fault\-isolation boundaries across workloads\. This means that if one of your workloads fails, the fault is contained within that specific domain and doesn't impact your other workloads\. 
+ Scale more easily across domains\.

Cross\-cluster search supports Kibana, so you can create visualizations and dashboards across all your domains\.

**Topics**
+ [Limitations](#cross-cluster-search-limitations)
+ [Cross\-Cluster Search Prerequisites](#cross-cluster-search-pp)
+ [Cross\-Cluster Search Pricing](#cross-cluster-search-pricing)
+ [Setting Up a Connection](#cross-cluster-search-set-up-connection)
+ [Removing a Connection](#cross-cluster-search-remove-connection)
+ [Setting Up Security and Sample Walkthrough](#cross-cluster-search-walkthrough)
+ [Kibana](#cross-cluster-search-kibana)

## Limitations<a name="cross-cluster-search-limitations"></a>

Cross\-cluster search has several important limitations:
+ You can only implement cross\-cluster search on domains created on or after June 3rd, 2020\.
+ You can't connect to self\-managed Elasticsearch clusters\.
+ You can't connect to domains in different AWS Regions\.
+ A domain can have a maximum of 20 outgoing connections\. Similarly, a domain can have a maximum of 20 incoming connections\.
+ Domains must either share the same major version, or be on the final minor version and the next major version \(for example, 6\.8 and 7\.x are compatible\)\.
+ You can't use custom dictionaries or SQL with cross\-cluster search\.
+ You can't use AWS CloudFormation to connect domains\.
+ You can't use cross\-cluster search on M3 and T2 instances\.

## Cross\-Cluster Search Prerequisites<a name="cross-cluster-search-pp"></a>

Before you set up cross\-cluster search, make sure that your domains meet the following requirements:
+ Elasticsearch version 6\.7 or later
+ Fine\-grained access control enabled
+ Node\-to\-node encryption enabled

## Cross\-Cluster Search Pricing<a name="cross-cluster-search-pricing"></a>

There is no additional charge for searching across domains\.

## Setting Up a Connection<a name="cross-cluster-search-set-up-connection"></a>

The “source” domain refers to the domain that a cross\-cluster search request originates from\. In other words, the source domain is the one that you send the initial search request to\.

The “destination” domain is the domain that the source domain queries\.

A cross\-cluster connection is unidirectional from the source to the destination domain\. This means that the destination domain can’t query the source domain\. However, you can set up another connection in the opposite direction\.

![\[Cross-cluster search authorization flow\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/ccs.png)

The source domain creates an "outbound" connection to the destination domain\. The destination domain receives an "inbound" connection request from the source domain\. 

**To set up a connection**

1. On your domain dashboard, choose a domain, and choose the **Cross\-cluster search connections** tab\.

1. In the **Outbound cluster connections** section, choose **Connect**\.

1. On the **Connect clusters** page, in **Connection alias**, enter a name for your connection\.

1. Choose between connecting a domain in your AWS account or in another account\.
   + To connect to a domain in your AWS account, from the dropdown list, choose the domain that you want to connect to and choose **Submit**\.
   + To connect to a domain in another AWS account, specify the ARN of the remote domain and choose **Submit**\.

1. Cross\-cluster search first validates the connection request to make sure that the prerequisites are met to ensure compatibility\. If the domains are found to be incompatible, the connection request enters the “Validation failed” state\.

1. After the connection request is validated successfully, it is sent to the destination domain, where it needs to be approved\. Until this approval happens, the connection remains in a “Pending acceptance” state\. When the connection request is accepted at the destination domain, the state changes to “Active” and the destination domain becomes available for queries\.
   + The domain page shows you the overall domain health and instance health details of your destination domain\. Only domain owners have the flexibility to create, view, remove, and monitor connections to or from their domains\.

After the connection is established, any traffic that flows between the nodes of the connected domains is encrypted\. If you connect a VPC domain to a non\-VPC domain and the non\-VPC domain is a public endpoint that can receive traffic from the internet, the cross\-cluster traffic between the domains is still encrypted and secure\. 

## Removing a Connection<a name="cross-cluster-search-remove-connection"></a>

Removing a connection stops any cross\-cluster operation on its indices\.

1. On your domain dashboard, choose the **Cross\-cluster search connections** tab\.

1. Select the domain connections that you want to remove and choose **Remove**\.

1. To confirm deletion, choose **Remove** in the pop\-up box\.

You could perform these steps on either the source or destination domain to remove the connection\. After the connection is removed, it's still visible with a "Deleted" status for a period of 15 days\. 

You can't delete a domain with active cross\-cluster connections\. To delete a domain, first remove all incoming and outgoing connections from that domain\. This is to make sure you take into account the cross\-cluster domain users before deleting the domain\.

## Setting Up Security and Sample Walkthrough<a name="cross-cluster-search-walkthrough"></a>

1. You send a cross\-cluster search request to the source domain\.

1. The source domain evaluates that request against its domain access policy\. Because cross\-cluster search requires fine\-grained access control, we recommend an open access policy on the source domain\. 

   ```
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Principal": {
           "AWS": [
             "*"
           ]
         },
         "Action": [
           "es:ESHttp*"
         ],
         "Resource": "arn:aws:es:region:account:domain/src-domain/*"
       }
     ]
   }
   ```
**Note**  
The domain resource policy evaluates the URI literally, so if you include remote indices in the path, use `arn:aws:es:us-east-1:123456789012:domain/my-domain/local_index,dst%3Aremote_index` rather than `arn:aws:es:us-east-1:123456789012:domain/my-domain/local_index,dst:remote_index`\.

   If you choose to use a restrictive access policy in addition to fine\-grained access control, your policy must allow access to `es:ESHttpGet` at a minimum\.

   ```
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Principal": {
           "AWS": [
             "arn:aws:iam::123456789012:user/test-es-user"
           ]
         },
         "Action": "es:ESHttpGet",
         "Resource": "arn:aws:es:region:account:domain/src-domain/*"
       }
     ]
   }
   ```

1. [Fine\-grained access control](fgac.md) on the source domain evaluates the request:
   + Is the request signed with valid IAM or HTTP basic credentials?
   + If so, does the user have permission to perform the search and access the data?

   If the request only searches data on the destination domain \(for example, `dest-alias:dest-index/_search`\), you only need permissions on the destination domain\. 

   If the request searches data on both domains \(for example, `source-index,dest-alias:dest-index/_search`\), you need permissions on both domains\. 

   In fine\-grained access control, users must have the `indices:admin/shards/search_shards` permission in addition to standard `read` or `search` permissions for the relevant indices\.

1. The source domain passes the request to the destination domain\. The destination domain evaluates this request against its domain access policy\. You must include the `es:ESCrossClusterGet` permission on the destination domain:

   ```
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Principal": {
           "AWS": "*"
         },
         "Action": "es:ESCrossClusterGet",
         "Resource": "arn:aws:es:region:account:domain/dst-domain"
       }
     ]
   }
   ```

   Make sure that the `es:ESCrossClusterGet` permission is applied for `/dst-domain` and not `/dst-domain/*`\.

   However, this minimum policy only allows cross\-cluster searches\. To perform other operations, such as indexing documents and performing standard searches, you need additional permissions\. We recommend the following policy on the destination domain:

   ```
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Principal": {
           "AWS": [
             "*"
           ]
         },
         "Action": [
           "es:ESHttp*"
         ],
         "Resource": "arn:aws:es:region:account:domain/dst-domain/*"
       },
       {
         "Effect": "Allow",
         "Principal": {
           "AWS": "*"
         },
         "Action": "es:ESCrossClusterGet",
         "Resource": "arn:aws:es:region:account:domain/dst-domain"
       }
     ]
   }
   ```
**Note**  
All cross\-cluster search requests between domains are encrypted in transit by default as part of node\-to\-node encryption\.

1. The destination domain performs the search and returns the results to the source domain\.

1. The source domain combines its own results \(if any\) with the results from the destination domain and returns them to you\.

1. We recommend [Postman](https://www.postman.com/) for testing requests:
   + On the destination domain, index a document:

     ```
     POST https://dst-domain.us-east-1.es.amazonaws.com/books/_doc/1
     
     {
       "Dracula": "Bram Stoker"
     }
     ```
   + To query this index from the source domain, include the connection alias of the destination domain within the query\.

     ```
     GET https://src-domain.us-east-1.es.amazonaws.com/<connection_alias>:books/_search
     
     {
         ...
       "hits": [
         {
           "_index": "source-destination:books",
           "_type": "_doc",
           "_id": "1",
           "_score": 1,
           "_source": {
             "Dracula": "Bram Stoker"
           }
         }
       ]
     }
     ```

     You can find the connection alias on the **Cross\-cluster search connections** tab on your domain dashboard\.
   + If you set up a connection between `domain-a -> domain-b` with connection alias `cluster_b` and `domain-a -> domain-c` with connection alias `cluster_c`, search `domain-a`, `domain-b`, and `domain-c` as follows:

     ```
     GET https://src-domain.us-east-1.es.amazonaws.com/local_index,cluster_b:b_index,cluster_c:c_index/_search
     {
       "query": {
         "match": {
           "user": "domino"
         }
       }
     }
     ```

     **Response**

     ```
     {
       "took": 150,
       "timed_out": false,
       "_shards": {
         "total": 3,
         "successful": 3,
         "failed": 0,
         "skipped": 0
       },
       "_clusters": {
         "total": 3,
         "successful": 3,
         "skipped": 0
       },
       "hits": {
         "total": 3,
         "max_score": 1,
         "hits": [
           {
             "_index": "local_index",
             "_type": "_doc",
             "_id": "0",
             "_score": 1,
             "_source": {
               "user": "domino",
               "message": "Lets unite the new mutants",
               "likes": 0
             }
           },
           {
             "_index": "cluster_b:b_index",
             "_type": "_doc",
             "_id": "0",
             "_score": 2,
             "_source": {
               "user": "domino",
               "message": "I'm different",
               "likes": 0
             }
           },
           {
             "_index": "cluster_c:c_index",
             "_type": "_doc",
             "_id": "0",
             "_score": 3,
             "_source": {
               "user": "domino",
               "message": "So am I",
               "likes": 0
             }
           }
         ]
       }
     }
     ```

     All destination clusters that you search need to be available for your search request to run successfully\. Otherwise, the whole request fails—even if one of the domains is not available, no search results are returned\.

## Kibana<a name="cross-cluster-search-kibana"></a>

You can visualize data from multiple connected domains in the same way as from a single domain, except that you must access the remote indices using `connection-alias:index`\. So, your index pattern must match `connection-alias:index`\.