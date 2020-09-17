# Kibana<a name="es-kibana"></a>

Kibana is a popular open source visualization tool designed to work with Elasticsearch\. Amazon ES provides an installation of Kibana with every Amazon ES domain\. You can find a link to Kibana on your domain dashboard on the Amazon ES console\. The URL is `domain-endpoint/_plugin/kibana/`\. Queries using this default Kibana installation have a 300\-second timeout\.

The following sections address some common Kibana use cases:
+ [Controlling Access to Kibana](#es-kibana-access)
+ [Configuring Kibana to Use a WMS Map Server](#es-kibana-map-server)
+ [Connecting a Local Kibana Server to Amazon ES](#es-kibana-local)

## Controlling Access to Kibana<a name="es-kibana-access"></a>

Kibana does not natively support IAM users and roles, but Amazon ES offers several solutions for controlling access to Kibana:


****  

| Domain Configuration | Access Control Options | 
| --- | --- | 
| Public access |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-kibana.html)  | 
| VPC access |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-kibana.html)  | 

### Using a Proxy to Access Amazon ES from Kibana<a name="es-kibana-proxy"></a>

**Note**  
This process is only applicable if your domain uses public access and you don't want to use [Amazon Cognito Authentication for Kibana](es-cognito-auth.md)\. See [Controlling Access to Kibana](#es-kibana-access)\.

Because Kibana is a JavaScript application, requests originate from the user's IP address\. IP\-based access control might be impractical due to the sheer number of IP addresses you would need to allow in order for each user to have access to Kibana\. One workaround is to place a proxy server between Kibana and Amazon ES\. Then you can add an IP\-based access policy that allows requests from only one IP address, the proxy's\. The following diagram shows this configuration\.

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

## Configuring Kibana to Use a WMS Map Server<a name="es-kibana-map-server"></a>

The default installation of Kibana for Amazon ES includes a map service, except for domains in the India and China regions\. Regardless of your region, you can configure Kibana to use a different Web Map Service \(WMS\) server for coordinate map visualizations\. Region map visualizations only support the default map service\.

**To configure Kibana to use a WMS map server:**

1. Open Kibana\.

1. Choose **Management**\.

1. Choose **Advanced Settings**\.

1. Locate **visualization:tileMap:WMSdefaults**\.

1. Change `enabled` to `true` and `url` to the URL of a valid WMS map server:

   ```
   {
     "enabled": true,
     "url": "wms-server-url",
     "options": {
       "format": "image/png",
       "transparent": true
     }
   }
   ```

1. Choose **Save**\.

To apply the new default value to visualizations, you might need to reload Kibana\. If you have saved visualizations, choose **Options** after opening the visualization\. Verify that **WMS map server** is enabled and **WMS url** contains your preferred map server, and then choose **Apply changes**\.

**Note**  
Map services often have licensing fees or restrictions\. You are responsible for all such considerations on any map server that you specify\. You might find the map services from the [U\.S\. Geological Survey](https://viewer.nationalmap.gov/services/) useful for testing\.

## Connecting a Local Kibana Server to Amazon ES<a name="es-kibana-local"></a>

If you have invested significant time into configuring your own Kibana instance, you can use it instead of \(or in addition to\) the default Kibana instance that Amazon ES provides\.

**To connect a local Kibana server to Amazon ES:**
+ Make the following changes to `config/kibana.yml`:

  ```
  kibana.index: ".kibanalocal"
  # Use elasticsearch.url for versions older than 6.6
  # elasticsearch.url: "https://domain-endpoint:443"
  # Use elasticsearch.hosts for versions 6.6 and later
  elasticsearch.hosts: "https://domain-endpoint:443"
  ```

Older versions of Elasticsearch might only work over HTTP\. In all cases, add the `http` or `https` prefix\. For older versions, you must explicitly specify port 80 or 443\. For newer versions, you can omit the port\.