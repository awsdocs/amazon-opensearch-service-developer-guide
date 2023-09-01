# Migrating Amazon OpenSearch Service indexes using remote reindex<a name="remote-reindex"></a>

Remote reindex lets you copy indexes from one Amazon OpenSearch Service domain to another\. You can migrate indexes from any OpenSearch Service domains or self\-managed OpenSearch and Elasticsearch clusters\. 

A *remote* domain and index refers to the source of the data, or the domain and index that you want to copy data from\. A *local* domain and index refers to the target for the data, or the domain and index that you want to copy data to\.

Remote reindexing requires OpenSearch 1\.0 or later, or Elasticsearch 6\.7 or later, on the local domain\. The remote domain must be lower or the same major version as the local domain\. Elasticsearch versions are considered to be *lower* than OpenSearch versions, meaning you can reindex data from Elasticsearch domains to OpenSearch domains\. Within the same major version, the remote domain can be any minor version\. For example, remote reindexing from Elasticsearch 7\.10\.x to 7\.9 is supported, but OpenSearch 1\.0 to Elasticsearch 7\.10\.x isn't supported\.

Full documentation for the `reindex` operation, including detailed steps and supported options, is available in the [OpenSearch documentation](https://opensearch.org/docs/opensearch/reindex-data/)\.

**Topics**
+ [Prerequisites](#remote-reindex-prereq)
+ [Reindex data between OpenSearch Service internet domains](#remote-reindex-domain)
+ [Reindex data between OpenSearch Service domains when the remote is in a VPC](#remote-reindex-vpc)
+ [Reindex data between non\-OpenSearch Service domains](#remote-reindex-non-aos)
+ [Reindex large datasets](#remote-reindex-largedatasets)
+ [Remote reindex settings](#remote-reindex-settings)

## Prerequisites<a name="remote-reindex-prereq"></a>

Remote reindex has the following requirements:
+ The remote domain must be accessible from the local domain\. For a remote domain that resides within a VPC, the local domain must have access to the VPC\. This process varies by network configuration, but likely involves connecting to a VPN or managed network, or using the native [VPC endpoint connection](#remote-reindex-vpc)\. To learn more, see [Launching your Amazon OpenSearch Service domains within a VPC](vpc.md)\. 
+ The request must be authorized by the remote domain like any other REST request\. If the remote domain has fine\-grained access control enabled, you must have permission to perform reindex on the remote domain and read the index on the local domain\. For more security considerations, see [Fine\-grained access control in Amazon OpenSearch Service](fgac.md)\.
+ We recommend you create an index with the desired setting on your local domain before you start the reindex process\.

## Reindex data between OpenSearch Service internet domains<a name="remote-reindex-domain"></a>

The most basic scenario is that the remote index is in the same AWS Region as your local domain with a publicly accessible endpoint and you have signed IAM credentials\.

From the remote domain, specify the remote index to reindex from and the local index to reindex to:

```
POST _reindex
{
  "source": {
    "remote": {
      "host": "https://remote-domain-endpoint:443"
    },
    "index": "remote_index"
  },
  "dest": {
    "index": "local_index"
  }
}
```

You must add 443 at the end of the remote domain endpoint for a validation check\.

To verify that the index is copied over to the local domain, send this request to the local domain:

```
GET local_index/_search
```

If the remote index is in a Region different from your local domain, pass in its Region name, such as in this sample request:

```
POST _reindex
{
  "source": {
    "remote": {
      "host": "https://remote-domain-endpoint:443",
      "region": "eu-west-1"
    },
    "index": "remote_index"
  },
  "dest": {
    "index": "local_index"
  }
}
```

In case of isolated Region like AWS GovCloud \(US\) or China Regions, the endpoint might not be accessible because your IAM user is not recognized in those Regions\.

If the remote domain is secured with basic authorization, specify the username and password:

```
POST _reindex
{
  "source": {
    "remote": {
      "host": "https://remote-domain-endpoint:443",
      "username": "username",
      "password": "password"
    },
    "index": "remote_index"
  },
  "dest": {
    "index": "local_index"
  }
}
```

## Reindex data between OpenSearch Service domains when the remote is in a VPC<a name="remote-reindex-vpc"></a>

Every OpenSearch Service domain is made up of its own internal virtual private cloud \(VPC\) infrastructure\. When you create a new domain in an existing OpenSearch Service VPC, an elastic network interface is created for each data node in the VPC\. 

Because the remote reindex operation is performed from the remote OpenSearch Service domain, and therefore within its own private VPC, you need a way to access the local domain’s VPC\. You can either do this by using the built\-in VPC endpoint connection feature to establish a connection through AWS PrivateLink, or by configuring a proxy\.

If your local domain uses OpenSearch version 1\.0 or later, you can use the console or the AWS CLI to create an AWS PrivateLink connection\. An AWS PrivateLink connection allows resources in the local VPC to privately connect to resources in the remote VPC within the same AWS Region\.

### Reindex data with the AWS Management Console<a name="reindex-console"></a>

You can use remote reindex with the console to copy indexes between two domains that share a VPC endpoint connection\.

1. Navigate to the Amazon OpenSearch Service console at [https://console.aws.amazon.com/aos/](https://console.aws.amazon.com/aos/)\.

1. In the left navigation pane, choose **Domains**\. 

1. Select the local domain, or the domain that you want to copy data to\. This opens the domain details page\. Choose the **Connections** tab below the general information and choose **Request**\.

1. On the **Request connection** page, select **VPC Endpoint Connection** for your connection mode and enter other relevant details\. These details include the remote domain, which is the domain that you want to copy data from\. Then, choose **Request**\.

1. Navigate to the remote domain's details page, choose the **Connections** tab, and find the **Inbound connections** table\. Select the check box next to the name of the domain that you just created the connection from \(the local domain\)\. Choose **Approve**\.

1. Navigate back to the local domain, choose the **Connections** tab, and find the **Outbound connections** table\. After the connection between the two domains is active, an endpoint becomes available in the **Endpoint** column in the table\. Copy the endpoint\.

1. Open the dashboard for the local domain and choose **Dev Tools** in the left navigation\. To confirm that the remote domain index doesn't exist on your local domain yet, run the following GET request\. Replace *remote\-domain\-index\-name* with your own index name\.

   ```
   GET remote-domain-index-name/_search
   {
      "query":{
         "match_all":{}
      }
   }
   ```

   In the output, you should see an error that indicates that the index wasn't found\.

1. Below your GET request, create a POST request and use your endpoint as the remote host, as follows\.

   ```
   POST _reindex
   {
      "source":{
         "remote":{
            "host":"endpoint",
            "username":"username",
            "password":"password"
         },
         "index":"remote-domain-index-name"
      },
      "dest":{
         "index":"local-domain-index-name"
      }
   }
   ```

   Run this request\.

1. Run the GET request again\. The output should now indicate that the local index exists\. You can query this index to verify that OpenSearch copied all the data from the remote index\.

### Reindex data with OpenSearch Service API operations<a name="reindex-api"></a>

You can use remote reindex with the API to copy indexes between two domains that share a VPC endpoint connection\.

1. Use the [CreateOutboundConnection](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_CreateOutboundConnection.html) API operation to request a new connection from your local domain to your remote domain\.

   ```
   POST https://es.region.amazonaws.com/2021-01-01/opensearch/cc/outboundConnection
   
   {
      "ConnectionAlias": "remote-reindex-example",
      "ConnectionMode": "VPC_ENDPOINT",
      "LocalDomainInfo": { 
         "AWSDomainInformation": { 
            "DomainName": "local-domain-name",
            "OwnerId": "aws-account-id",
            "Region": "region"
         }
      },
      "RemoteDomainInfo": { 
         "AWSDomainInformation": { 
            "DomainName": "remote-domain-name",
            "OwnerId": "aws-account-id",
            "Region": "region"
         }
      }
   }
   ```

   You receive a `ConnectionId` in the response\. Save this ID to use in the next step\.

1. Use the [AcceptInboundConnection](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_AcceptInboundConnection.html) API operation with your connection ID to approve the request from the local domain\.

   ```
   PUT https://es.region.amazonaws.com/2021-01-01/opensearch/cc/inboundConnection/ConnectionId/accept
   ```

1. Use the [DescribeOutboundConnections](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/API_DescribeOutboundConnections.html) API operation to retrieve the endpoint for your remote domain\. 

   ```
   {
       "Connections": [
           {
               "ConnectionAlias": "remote-reindex-example",
               "ConnectionId": "connection-id",
               "ConnectionMode": "VPC_ENDPOINT",
               "ConnectionProperties": {
                   "Endpoint": "connection-endpoint"
               },
               ...
           }
       ]
   }
   ```

   Save the *connection\-endpoint* to use in Step 5\.

1. To confirm that the remote domain index doesn't exist on your local domain yet, run the following GET request\. Replace *remote\-domain\-index\-name* with your own index name\.

   ```
   GET local-domain-endpoint/remote-domain-index-name/_search
   {
      "query":{
         "match_all":{}
      }
   }
   ```

   In the output, you should see an error that indicates that the index wasn't found\.

1. Create a POST request and use your endpoint as the remote host, as follows\.

   ```
   POST local-domain-endpoint/_reindex
   {
      "source":{
         "remote":{
            "host":"connection-endpoint",
            "username":"username",
            "password":"password"
         },
         "index":"remote-domain-index-name"
      },
      "dest":{
         "index":"local-domain-index-name"
      }
   }
   ```

   Run this request\.

1. Run the GET request again\. The output should now indicate that the local index exists\. You can query this index to verify that OpenSearch copied all the data from the remote index\.

If the remote domain is hosted inside a VPC and you don't want to use the VPC endpoint connection feature, you must configure a proxy with a publicly accessible endpoint\. In this case, OpenSearch Service requires a public endpoint because it doesn't have the ability to send traffic into your VPC\. 

When you run a domain in [VPC mode](vpc.md), one or more endpoints are placed in your VPC\. However, these endpoints are only for traffic coming into the domain within the VPC, and they don't permit traffic into the VPC itself\. 

The remote reindex command is run from the local domain, so the originating traffic isn't able to use those endpoints to access the remote domain\. That's why a proxy is required in this use case\. The proxy domain must have a certificate signed by a public certificate authority \(CA\)\. Self\-signed or private CA\-signed certificates are not supported\.

## Reindex data between non\-OpenSearch Service domains<a name="remote-reindex-non-aos"></a>

If the remote index is hosted outside of OpenSearch Service, like in a self\-managed EC2 instance, set the `external` parameter to `true`:

```
POST _reindex
{
  "source": {
    "remote": {
      "host": "https://remote-domain-endpoint:443",
      "username": "username",
      "password": "password",
      "external": true
    },
    "index": "remote_index"
  },
  "dest": {
    "index": "local_index"
  }
}
```

In this case, only basic authorization with a username and password is supported\. The remote domain must have a publicly accessible endpoint \(even if it's in the same VPC as the local OpenSearch Service domain\) and a certificate signed by a public CA\. Self\-signed or private CA\-signed certificates aren't supported\.

## Reindex large datasets<a name="remote-reindex-largedatasets"></a>

Remote reindex sends a scroll request to the remote domain with the following default values: 
+ Search context of 5 minutes
+ Socket timeout of 30 seconds
+ Batch size of 1,000

We recommend tuning these parameters to accommodate your data\. For large documents, consider a smaller batch size and/or longer timeout\. For more information, see [Scroll search](https://opensearch.org/docs/opensearch/ux/#scroll-search)\.

```
POST _reindex?pretty=true&scroll=10h&wait_for_completion=false
{
  "source": {
    "remote": {
      "host": "https://remote-domain-endpoint:443",
      "socket_timeout": "60m"
    },
    "size": 100,
    "index": "remote_index"
  },
  "dest": {
    "index": "local_index"
  }
}
```

We also recommend adding the following settings to the local index for better performance:

```
PUT local_index
{
  "settings": {
    "refresh_interval": -1,
    "number_of_replicas": 0
  }
}
```

After the reindex process is complete, you can set your desired replica count and remove the refresh interval setting\.

To reindex only a subset of documents that you select through a query, send this request to the local domain:

```
POST _reindex
{
  "source": {
    "remote": {
      "host": "https://remote-domain-endpoint:443"
    },
    "index": "remote_index",
    "query": {
      "match": {
        "field_name": "text"
      }
    }
  },
  "dest": {
    "index": "local_index"
  }
}
```

Remote reindex doesn't support slicing, so you can't perform multiple scroll operations for the same request in parallel\.

## Remote reindex settings<a name="remote-reindex-settings"></a>

In addition to the standard reindexing options, OpenSearch Service supports the following options:


| Options | Valid values | Description | Required | 
| --- | --- | --- | --- | 
| external | Boolean | If the remote domain is not an OpenSearch Service domain, or if you're reindexing between two VPC domains, specify as true\. | No | 
| region | String | If the remote domain is in a different Region, specify the Region name\. | No | 