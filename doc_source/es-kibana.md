# Kibana and Logstash<a name="es-kibana"></a>

This chapter describes some considerations for using Kibana and Logstash with Amazon ES\.

**Topics**
+ [Kibana](#es-managedomains-kibana)
+ [Loading Bulk Data with the Logstash Plugin](#es-managedomains-logstash)

## Kibana<a name="es-managedomains-kibana"></a>

[Kibana](https://www.elastic.co/guide/en/kibana/current/introduction.html) is a popular open source visualization tool designed to work with Elasticsearch\. Amazon Elasticsearch Service \(Amazon ES\) provides a default installation of Kibana with every Amazon ES domain\. You can find a link to Kibana on your domain dashboard on the Amazon ES console\. The URL is `https://domain.region.es.amazonaws.com/_plugin/kibana/`\. Queries on this default Kibana installation have a 60\-second timeout\.

For information about using Kibana to visualize your data, see the [Kibana User Guide](https://www.elastic.co/guide/en/kibana/current/index.html)\.

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
  "Statement": [
    {
      "Resource": "arn:aws:es:us-west-2:111111111111:domain/recipes1/analytics",
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
      "Resource": "arn:aws:es:us-west-2:111111111111:domain/recipes1/analytics"
    }
  ]
}
```

We recommend that you configure the EC2 instance running the proxy server with an Elastic IP address\. This way, you can replace the instance when necessary and still attach the same public IP address to it\. To learn more, see [Elastic IP Addresses](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/elastic-ip-addresses-eip.html) in the *Amazon EC2 User Guide for Linux Instances*\.

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
  kibana_index: ".kibana-5"
  elasticsearch_url: "http://elasticsearch_domain_endpoint:80"
  ```

You must use the `http` prefix and explicitly specify port 80\.

## Loading Bulk Data with the Logstash Plugin<a name="es-managedomains-logstash"></a>

Logstash provides a convenient way to use the [bulk API](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html) to upload data into your Amazon ES domain with the [S3](https://www.elastic.co/guide/en/logstash/current/plugins-inputs-s3.html) plugin\. The service also supports all other standard Logstash input plugins that are provided by Elasticsearch\. Amazon ES also supports two Logstash output plugins: the standard [Elasticsearch](https://www.elastic.co/guide/en/logstash/current/plugins-outputs-elasticsearch.html) plugin and the [logstash\-output\-amazon\-es](https://github.com/awslabs/logstash-output-amazon_es) plugin, which signs and exports Logstash events to Amazon ES\.

You must install your own local instance of Logstash and make the following changes in the Logstash configuration file to enable interaction with Amazon ES\.


****  

| Configuration Field | Input \| Output Plugin | Description | 
| --- | --- | --- | 
| bucket | Input | Specifies the Amazon S3 bucket containing the data that you want to load into an Amazon ES domain\. You can find this service endpoint in the Amazon Elasticsearch Service console dashboard\. | 
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
The service request in the preceding example must be signed\. For more information about signing requests, see [Signing Amazon ES Requests](es-ac.md#es-managedomains-signing-service-requests)\. Use the [logstash\-output\-amazon\-es](https://github.com/awslabs/logstash-output-amazon_es) output plugin to sign and export Logstash events to Amazon ES\. For instructions, see the [https://github.com/awslabs/logstash-output-amazon_es/blob/master/README.md](https://github.com/awslabs/logstash-output-amazon_es/blob/master/README.md)\.