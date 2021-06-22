# Migrating Amazon Elasticsearch Service indices using remote reindex<a name="remote-reindex"></a>

Remote reindex lets you copy indices from a remote Amazon Elasticsearch Service \(Amazon ES\) cluster to a local one\. You can migrate indices from any Amazon ES domains or self\-managed Elasticsearch clusters\.

Remote reindexing requires Elasticsearch 6\.7 or later on the local domain\. The remote domain must be lower or the same major version as the local domain\. Within the same major version, the remote domain can be any minor version\. For example, remote reindexing from 7\.10\.x to 7\.9 is supported, but 7\.x to 6\.x isn't supported\.

Full documentation for the `reindex` operation, including detailed steps and supported options, is available in the [Open Distro for Elasticsearch documentation](https://opendistro.github.io/for-elasticsearch-docs/docs/elasticsearch/reindex-data/)\.

**Topics**
+ [Prerequisites](#remote-reindex-prereq)
+ [Reindex data between Amazon ES domains](#remote-reindex-esdomain)
+ [Reindex data between non\-Amazon ES domains](#remote-reindex-nonesdomain)
+ [Reindex large datasets](#remote-reindex-largedatasets)
+ [Remote reindex settings](#remote-reindex-settings)

## Prerequisites<a name="remote-reindex-prereq"></a>

Remote reindex has the following requirements:
+ The remote domain must be accessible from the local domain\. For a remote domain that resides within a VPC, the local domain must have access to the VPC\. This process varies by network configuration, but likely involves connecting to a VPN or managed network or using a proxy server\. To learn more, see [VPC Support](es-vpc.md)\. 
+ The request must be authorized by the remote domain like any other REST request\. If the remote domain has fine\-grained access control enabled, you must have permission to perform reindex on the local domain and read the index on the remote domain\. For more security considerations, see [Fine\-Grained Access Control](fgac.md)\.
+ We recommend you create an index with the desired setting on your local domain before you start the reindex process\.

## Reindex data between Amazon ES domains<a name="remote-reindex-esdomain"></a>

The most basic scenario is that the remote index is in the same region as your local domain with a publicly accessible endpoint and you have signed IAM credentials\.

Specify the source index in the remote domain that you want to reindex from and the destination index on your local domain that you want to reindex to:

```
POST <local-domain-endpoint>/_reindex
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

To verify that the index is copied over to the local domain:

```
GET <local-domain-endpoint>/local_index/_search
```

If the remote index is in a region different from your local domain, pass in its region name, such as in this sample request:

```
POST <local-domain-endpoint>/_reindex
{
  "source": {
    "remote": {
      "host": "https://remote-domain-endpoint:443",
      "region": "eu-west-1"
    },
    "index": "test_index"
  },
  "dest": {
    "index": "local_index"
  }
}
```

In case of isolated regions like AWS GovCloud \(US\) or China regions, the endpoint might not be accessible because your IAM user is not recognized in those regions\.

If the remote domain is secured with basic authorization, specify the username and password:

```
POST <local-domain-endpoint>/_reindex
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

If the remote domain is hosted inside a VPC and it does not have VPC\-level connectivity, configure a proxy with a publicly accessible endpoint\. The proxy domain must have a certificate signed by a public certificate authority \(CA\)\. Self\-signed or private CA\-signed certificates are not supported\.

## Reindex data between non\-Amazon ES domains<a name="remote-reindex-nonesdomain"></a>

If the remote index is hosted outside of Amazon ES, like in a self\-managed EC2 instance, specify `external` parameter as `true`:

```
POST <local-domain-endpoint>/_reindex
{
  "source": {
    "remote": {
      "host": "https://remote_endpoint:443",
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

In this case, only basic authorization with username and password is supported\. The remote domain must have a certificate signed by a public CA\. Self\-signed or private CA\-signed certificates are not supported\.

## Reindex large datasets<a name="remote-reindex-largedatasets"></a>

Remote reindex sends a scroll request to the remote domain with the following default values: 
+ Search context of 5 minutes
+ Socket timeout of 30 seconds
+ Batch size of 1,000

We recommend tuning these parameters to accommodate your data\. For large documents, consider a smaller batch size and/or longer timeout\. For more information, see [Scroll search](https://opendistro.github.io/for-elasticsearch-docs/docs/elasticsearch/ux/#scroll-search)\.

```
POST <local-domain-endpoint>/_reindex?pretty=true&scroll=10h&wait_for_completion=false
{
  "source": {
    "remote": {
      "host": "https://remote_endpoint:443",
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

To reindex only a subset of documents that you select through a query:

```
POST <local-domain-endpoint>/_reindex
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

Remote reindex does not support slicing, so you cannot perform multiple scroll operations for the same request in parallel\.

## Remote reindex settings<a name="remote-reindex-settings"></a>

In addition to the standard reindexing options, Amazon ES supports the following options:


| Options | Valid values | Description | Required | 
| --- | --- | --- | --- | 
| external | Boolean | If remote domain is not an Amazon ES domain, specify as true\. | No | 
| region | String | If remote domain is in a different region, specify the region name\. | No | 