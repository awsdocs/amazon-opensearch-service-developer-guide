# Migrating Amazon OpenSearch Service indices using remote reindex<a name="remote-reindex"></a>

Remote reindex lets you copy indices from one Amazon OpenSearch Service cluster to another\. You can migrate indices from any OpenSearch Service domains or self\-managed OpenSearch and Elasticsearch clusters\.

Remote reindexing requires OpenSearch 1\.0 or later, or Elasticsearch 6\.7 or later, on the target domain\. The source domain must be lower or the same major version as the target domain\. Elasticsearch versions are considered to be *lower* than OpenSearch versions, meaning you can reindex data from Elasticsearch domains to OpenSearch domains\. Within the same major version, the source domain can be any minor version\. For example, remote reindexing from Elasticsearch 7\.10\.x to 7\.9 is supported, but OpenSearch 1\.0 to Elasticsearch 7\.10\.x isn't supported\.

Full documentation for the `reindex` operation, including detailed steps and supported options, is available in the [OpenSearch documentation](https://opensearch.org/docs/opensearch/reindex-data/)\.

**Topics**
+ [Prerequisites](#remote-reindex-prereq)
+ [Reindex data between OpenSearch Service domains](#remote-reindex-domain)
+ [Reindex data between OpenSearch Service domains in a VPC](#remote-reindex-vpc)
+ [Reindex data between non\-OpenSearch Service domains](#remote-reindex-non-aos)
+ [Reindex large datasets](#remote-reindex-largedatasets)
+ [Remote reindex settings](#remote-reindex-settings)

## Prerequisites<a name="remote-reindex-prereq"></a>

Remote reindex has the following requirements:
+ The source domain must be accessible from the target domain\. For a source domain that resides within a VPC, the target domain must have access to the VPC\. This process varies by network configuration, but likely involves connecting to a VPN or managed network or using a proxy server\. To learn more, see [Launching your Amazon OpenSearch Service domains within a VPC](vpc.md)\. 
+ The request must be authorized by the source domain like any other REST request\. If the source domain has fine\-grained access control enabled, you must have permission to perform reindex on the target domain and read the index on the source domain\. For more security considerations, see [Fine\-grained access control in Amazon OpenSearch Service](fgac.md)\.
+ We recommend you create an index with the desired setting on your target domain before you start the reindex process\.

## Reindex data between OpenSearch Service domains<a name="remote-reindex-domain"></a>

The most basic scenario is that the source index is in the same Region as your target domain with a publicly accessible endpoint and you have signed IAM credentials\.

Specify the source index to reindex from and the target index to reindex to:

```
POST target-domain-endpoint/_reindex
{
  "source": {
    "remote": {
      "host": "https://source-domain-endpoint:443"
    },
    "index": "source_index"
  },
  "dest": {
    "index": "target_index"
  }
}
```

You must add 443 at the end of the source domain endpoint for a validation check\.

To verify that the index is copied over to the target domain:

```
GET target-domain-endpoint/target_index/_search
```

If the source index is in a region different from your target domain, pass in its region name, such as in this sample request:

```
POST target-domain-endpoint/_reindex
{
  "source": {
    "remote": {
      "host": "https://source-domain-endpoint:443",
      "region": "eu-west-1"
    },
    "index": "source_index"
  },
  "dest": {
    "index": "target_index"
  }
}
```

In case of isolated regions like AWS GovCloud \(US\) or China regions, the endpoint might not be accessible because your IAM user is not recognized in those regions\.

If the source domain is secured with basic authorization, specify the username and password:

```
POST target-domain-endpoint/_reindex
{
  "source": {
    "remote": {
      "host": "https://source-domain-endpoint:443",
      "username": "username",
      "password": "password"
    },
    "index": "source_index"
  },
  "dest": {
    "index": "target_index"
  }
}
```

If the source domain is hosted inside a VPC and does not have VPC\-level connectivity, configure a proxy with a publicly accessible endpoint\. The proxy domain must have a certificate signed by a public certificate authority \(CA\)\. Self\-signed or private CA\-signed certificates are not supported\.

## Reindex data between OpenSearch Service domains in a VPC<a name="remote-reindex-vpc"></a>

Every OpenSearch Service domain is made up of its own internal VPC infrastructure\. When you create a new OpenSearch Service domain in an existing virtual private cloud \(VPC\), an Elastic Network Interface \(ENI\) is created for each data node in the OpenSearch Service VPC\. Because the source reindex operation is performed from the target OpenSearch Service domain, and therefore within its own private VPC, you don’t access the source OpenSearch Service domain’s VPC\. Instead, you need a publicly accessible reverse proxy\.

A proxy is required in order to use remote reindex between two VPC domains, even if the domains are located within the same VPC\. Create a proxy with a publicly accessible endpoint in front of the source cluster and pass the proxy endpoint in the reindex body\. The proxy domain must have a certificate signed by a public certificate authority \(CA\)\. Self\-signed or private CA\-signed certificates are not supported\.

## Reindex data between non\-OpenSearch Service domains<a name="remote-reindex-non-aos"></a>

If the source index is hosted outside of OpenSearch Service, like in a self\-managed EC2 instance, set the `external` parameter to `true`:

```
POST target-domain-endpoint/_reindex
{
  "source": {
    "remote": {
      "host": "https://source-domain-endpoint:443",
      "username": "username",
      "password": "password",
      "external": true
    },
    "index": "source_index"
  },
  "dest": {
    "index": "target_index"
  }
}
```

In this case, only basic authorization with a username and password is supported\. The source domain must have a certificate signed by a public CA\. Self\-signed or private CA\-signed certificates are not supported\.

## Reindex large datasets<a name="remote-reindex-largedatasets"></a>

Remote reindex sends a scroll request to the source domain with the following default values: 
+ Search context of 5 minutes
+ Socket timeout of 30 seconds
+ Batch size of 1,000

We recommend tuning these parameters to accommodate your data\. For large documents, consider a smaller batch size and/or longer timeout\. For more information, see [Scroll search](https://opensearch.org/docs/opensearch/ux/#scroll-search)\.

```
POST target-domain-endpoint/_reindex?pretty=true&scroll=10h&wait_for_completion=false
{
  "source": {
    "remote": {
      "host": "https://source-domain-endpoint:443",
      "socket_timeout": "60m"
    },
    "size": 100,
    "index": "source_index"
  },
  "dest": {
    "index": "target_index"
  }
}
```

We also recommend adding the following settings to the target index for better performance:

```
PUT target_index
{
  "settings": {
    "refresh_interval": -1,
    "number_of_replicas": 0
  }
}
```

After the reindex process is complete, you can set your desired replica count and remove the refresh interval setting\.

To reindex only a subset of documents that you select through a query:

```
POST target-domain-endpoint/_reindex
{
  "source": {
    "remote": {
      "host": "https://source-domain-endpoint:443"
    },
    "index": "remote_index",
    "query": {
      "match": {
        "field_name": "text"
      }
    }
  },
  "dest": {
    "index": "target_index"
  }
}
```

Remote reindex doesn't support slicing, so you can't perform multiple scroll operations for the same request in parallel\.

## Remote reindex settings<a name="remote-reindex-settings"></a>

In addition to the standard reindexing options, OpenSearch Service supports the following options:


| Options | Valid values | Description | Required | 
| --- | --- | --- | --- | 
| external | Boolean | If the source domain is not an OpenSearch Service domain, specify as true\. | No | 
| region | String | If the source domain is in a different region, specify the region name\. | No | 