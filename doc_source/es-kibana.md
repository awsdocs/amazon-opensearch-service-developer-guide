# Kibana and Logstash<a name="es-kibana"></a>

This chapter describes some considerations for using Kibana and Logstash with Amazon Elasticsearch Service\.

**Topics**
+ [Kibana](#es-managedomains-kibana)
+ [Loading Bulk Data with the Logstash Plugin](#es-managedomains-logstash)

## Kibana<a name="es-managedomains-kibana"></a>

Kibana is a popular open source visualization tool designed to work with Elasticsearch\. Amazon ES provides an installation of Kibana with every Amazon ES domain\. You can find a link to Kibana on your domain dashboard on the Amazon ES console\. The URL is `domain-endpoint/_plugin/kibana/`\. Queries using this default Kibana installation have a 300\-second timeout\.

The following sections address some common Kibana use cases:
+ [Controlling Access to Kibana](#es-kibana-access)
+ [Configuring Kibana to Use a WMS Map Server](#es-kibana-map-server)
+ [Connecting a Local Kibana Server to Amazon ES](#es-kibana-local)

### Controlling Access to Kibana<a name="es-kibana-access"></a>

Kibana does not natively support IAM users and roles, but Amazon ES offers several solutions for controlling access to Kibana:


****  

| Domain Configuration | Access Control Options | 
| --- | --- | 
| Public access |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-kibana.html)  | 
| VPC access |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-kibana.html)  | 

#### Using a Proxy to Access Amazon ES from Kibana<a name="es-kibana-proxy"></a>

**Note**  
This process is only applicable if your domain uses public access and you don't want to use [Amazon Cognito Authentication for Kibana](es-cognito-auth.md)\. See [Controlling Access to Kibana](#es-kibana-access)\.

Because Kibana is a JavaScript application, requests originate from the user's IP address\. IP\-based access control might be impractical due to the sheer number of IP addresses you would need to whitelist in order for each user to have access to Kibana\. One workaround is to place a proxy server between Kibana and Amazon ES\. Then you can add an IP\-based access policy that allows requests from only one IP address, the proxy's\. The following diagram shows this configuration\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/KibanaProxy.png)

1. This is your Amazon ES domain\. IAM provides authorized access to this domain\. An additional, IP\-based access policy provides access to the proxy server\.

1. This is the proxy server, running on an Amazon EC2 instance\.

1. Other applications can use the Signature Version 4 signing process to send authenticated requests to Amazon ES\.

1. Kibana clients connect to your Amazon ES domain through the proxy\.

To enable this sort of configuration, you need a resource\-based policy that specifies roles and IP addresses\. Here's a sample policy:

```
{
  "Version": "2012-10-17",
  "Statement": [{
      "Resource": "arn:aws:es:us-west-2:111111111111:domain/my-domain/*",
      "Principal": {
        "AWS": "arn:aws:iam::111111111111:role/allowedrole1"
      },
      "Action": [
        "es:ESHttpGet"
      ],
      "Effect": "Allow"
    },
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "*"
      },
      "Action": "es:*",
      "Condition": {
        "IpAddress": {
          "aws:SourceIp": [
            "123.456.789.123"
          ]
        }
      },
      "Resource": "arn:aws:es:us-west-2:111111111111:domain/my-domain/*"
    }
  ]
}
```

We recommend that you configure the EC2 instance running the proxy server with an Elastic IP address\. This way, you can replace the instance when necessary and still attach the same public IP address to it\. To learn more, see [Elastic IP Addresses](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/elastic-ip-addresses-eip.html) in the *Amazon EC2 User Guide for Linux Instances*\.

If you use a proxy server *and* [Amazon Cognito Authentication for Kibana](es-cognito-auth.md), you might need to add settings for Kibana and Amazon Cognito to avoid `redirect_mismatch` errors\. See the following `nginx.conf` example:

```
{
server {
    listen 443;
    server_name $host;
    rewrite ^/$ https://$host/_plugin/kibana redirect;

    ssl_certificate           /etc/nginx/cert.crt;
    ssl_certificate_key       /etc/nginx/cert.key;

    ssl on;
    ssl_session_cache  builtin:1000  shared:SSL:10m;
    ssl_protocols  TLSv1 TLSv1.1 TLSv1.2;
    ssl_ciphers HIGH:!aNULL:!eNULL:!EXPORT:!CAMELLIA:!DES:!MD5:!PSK:!RC4;
    ssl_prefer_server_ciphers on;

    location /_plugin/kibana {
        # Forward requests to Kibana
        proxy_pass https://$kibana_host/_plugin/kibana;

        # Handle redirects to Cognito
        proxy_redirect https://$cognito_host https://$host;

        # Update cookie domain and path
        proxy_cookie_domain $kibana_host $host;
        proxy_cookie_path / /_plugin/kibana/;

        # Response buffer settings
        proxy_buffer_size 128k;
        proxy_buffers 4 256k;
        proxy_busy_buffers_size 256k;
    }

    location ~ \/(log|sign|fav|forgot|change|saml|oauth2) {
        # Forward requests to Cognito
        proxy_pass https://$cognito_host;

        # Handle redirects to Kibana
        proxy_redirect https://$kibana_host https://$host;

        # Update cookie domain
        proxy_cookie_domain $cognito_host $host;
    }
}
```

### Configuring Kibana to Use a WMS Map Server<a name="es-kibana-map-server"></a>

Due to licensing restrictions, the default installation of Kibana on Amazon ES domains that use Elasticsearch 5\.*x* or greater does *not* include a map server for tile map visualizations\. Use the following procedure to configure Kibana to use a Web Map Service \(WMS\) map server\.

**To configure Kibana to use a WMS map server:**

1. Open Kibana\. You can find a link to Kibana in the domain summary at [https://console\.aws\.amazon\.com/es/](https://console.aws.amazon.com/es/)\.

1. Choose **Management**\.

1. Choose **Advanced Settings**\.

1. Locate **visualization:tileMap:WMSdefaults**, and then choose the **edit** button to modify the default value\.

1. Change `enabled` to `true` and `url` to the URL of a valid WMS map server\.

1. \(Optional\) Locate **visualization:tileMap:WMSdefaults**, and then choose the **edit** button to modify the default value\.

1. \(Optional\) Change `"layers": "0"` to a comma\-separated list of map layers that you want to display\. Layers vary by map service\. The default value of `0` is often appropriate\.

1. Choose the **save** button\.

 To apply the new default value to visualizations, you might need to reload Kibana\.

**Note**  
Map services often have licensing fees or restrictions\. You are responsible for all such considerations on any map server that you specify\. You might find the map services from the [U\.S\. Geological Survey](https://viewer.nationalmap.gov/services/) useful for testing\.

### Connecting a Local Kibana Server to Amazon ES<a name="es-kibana-local"></a>

If you have invested significant time into configuring your own Kibana instance, you can use it instead of \(or in addition to\) the default Kibana instance that Amazon ES provides\.

**To connect a local Kibana server to Amazon ES:**
+ Make the following changes to `config/kibana.yml`:

  ```
  kibana.index: ".kibana_1"
  # Use elasticsearch.url for versions older than 6.6
  # elasticsearch.url: "https://domain-endpoint:443"
  # Use elasticsearch.hosts for versions 6.6 and later
  elasticsearch.hosts: "https://domain-endpoint:443"
  ```

Older versions of Elasticsearch might only work over HTTP\. In all cases, add the `http` or `https` prefix\. For older versions, you must explicitly specify port 80 or 443\. For newer versions, you can omit the port\.

## Loading Bulk Data with the Logstash Plugin<a name="es-managedomains-logstash"></a>

Logstash provides a convenient way to use the bulk API to upload data into your Amazon ES domain with the S3 plugin\. The service also supports all other standard Logstash input plugins that are provided by Elasticsearch\. Amazon ES also supports two Logstash output plugins: the standard Elasticsearch plugin and the [logstash\-output\-amazon\-es](https://github.com/awslabs/logstash-output-amazon_es) plugin, which signs and exports Logstash events to Amazon ES\.

You must install your own local instance of Logstash and make the following changes in the Logstash configuration file to enable interaction with Amazon ES\.


****  

| Configuration Field | Input \| Output Plugin | Description | 
| --- | --- | --- | 
| bucket | Input | Specifies the Amazon S3 bucket containing the data that you want to load into an Amazon ES domain\. | 
| region | Input | Specifies the AWS Region where the Amazon S3 bucket resides\. | 
| hosts | Output | Specifies the service endpoint for the target Amazon ES domain\. | 
| ssl | Output | Specifies whether to use SSL to connect to Amazon ES\.  | 

This example configures Logstash to do the following:
+ Point the output plugin to an Amazon ES endpoint
+ Point to the input plugin to the `wikipedia-stats-log` bucket in S3
+ Use SSL to connect to Amazon ES 

```
input{
    s3 {
        bucket => "wikipedia-stats-log"
        access_key_id => "lizards"
        secret_access_key => "lollipops"
        region => "us-east-1"
    }
}
output{
    elasticsearch {
        hosts => "search-logs-demo0-cpxczkdpi4bkb4c44g3csyln5a.us-east-1.es.example.com"
        ssl => true
    }
}
```

**Note**  
The service request in the preceding example must be signed\. For more information about signing requests, see [Making and Signing Amazon ES Requests](es-ac.md#es-managedomains-signing-service-requests)\. Use the [logstash\-output\-amazon\-es](https://github.com/awslabs/logstash-output-amazon_es) output plugin to sign and export Logstash events to Amazon ES\. For instructions, see the plugin [https://github.com/awslabs/logstash-output-amazon_es/blob/master/README.md](https://github.com/awslabs/logstash-output-amazon_es/blob/master/README.md)\.