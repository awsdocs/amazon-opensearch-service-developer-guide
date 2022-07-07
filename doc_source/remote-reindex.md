# Migrating Amazon OpenSearch Service indexes using remote reindex<a name="remote-reindex"></a>

Remote reindex lets you copy indexes from one Amazon OpenSearch Service cluster to another\. You can migrate indexes from any OpenSearch Service domains or self\-managed OpenSearch and Elasticsearch clusters\.

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

The most basic scenario is that the source index is in the same AWS Region as your target domain with a publicly accessible endpoint and you have signed IAM credentials\.

From the target domain, specify the source index to reindex from and the target index to reindex to:

```
POST _reindex
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

To verify that the index is copied over to the target domain, send this request to the target domain:

```
GET target_index/_search
```

If the source index is in a Region different from your target domain, pass in its Region name, such as in this sample request:

```
POST _reindex
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

In case of isolated Region like AWS GovCloud \(US\) or China Regions, the endpoint might not be accessible because your IAM user is not recognized in those Regions\.

If the source domain is secured with basic authorization, specify the username and password:

```
POST _reindex
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

**To reindex data between OpenSearch Service domains in a VPC**

1. Create an IAM user that has been granted access to both the local and remote OpenSearch Service domain\. The following is an example access policy:

   ```
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Principal": {
           "AWS": "arn:aws:iam::123456789012:user/test-user"
         },
         "Action": "es:*",
         "Resource": "arn:aws:es:us-east-1:123456789012:domain/test-domain/my-index/*"
       }
     ]
   }
   ```

1. Set up an EC2 instance with a NGINX reverse proxy for the remote OpenSearch Service VPC endpoint\. The EC2 instance must be within the same VPC as the OpenSearch Service domain\. Because you’re signing your requests, make sure that the NGINX configuration contains the following parameters:

   ```
   proxy_set_header Host $host;
   proxy_set_header X-Real-IP $remote_addr;
   ```

1. Send the `_reindex` request and sign it with IAM credentials using [Signature Version 4](https://docs.aws.amazon.com/general/latest/gr/sigv4_signing.html)\. Send the request from a machine in the same VPC as the OpenSearch Service domain \(either a running EC2 instance or a local machine connected through a VPN\)\. Set the `external` parameter to `true`\. For the source domain, specify the externally accessible URL for the NGINX reverse proxy\.

## Reindex data between non\-OpenSearch Service domains<a name="remote-reindex-non-aos"></a>

If the source index is hosted outside of OpenSearch Service, like in a self\-managed EC2 instance, set the `external` parameter to `true`:

```
POST _reindex
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
POST _reindex?pretty=true&scroll=10h&wait_for_completion=false
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

To reindex only a subset of documents that you select through a query, send this request to the target domain:

```
POST _reindex
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
| external | Boolean | If the source domain is not an OpenSearch Service domain, or if you're reindexing between two VPC domains, specify as true\. | No | 
| region | String | If the source domain is in a different Region, specify the Region name\. | No | 