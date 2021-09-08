# Loading data into Amazon OpenSearch Service with Logstash<a name="managedomains-logstash"></a>

The open source version of Logstash \(Logstash OSS\) provides a convenient way to use the bulk API to upload data into your Amazon OpenSearch Service domain\. The service supports all standard Logstash input plugins, including the Amazon S3 input plugin\. OpenSearch Service currently supports three Logstash output plugins, depending on your Logstash version, authentication method, and whether your domain is running Elasticsearch or OpenSearch:
+ Standard Elasticsearch plugin
+ [logstash\-output\-amazon\_es](https://github.com/opensearch-project/logstash-output-opensearch), which uses IAM credentials to sign and export Logstash events to OpenSearch Service
+ [logstash\-output\-opensearch](https://github.com/opensearch-project/logstash-output-opensearch), which currently only supports basic authentication

The following table describes the compatibility between various authentication mechanisms and Logstash output plugins:

## <a name="logstash-prereq"></a>


**OpenSearch**  
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/managedomains-logstash.html)

In order for OpenSearch domains to be compatible with version 7\.12 of Logstash OSS, you need to choose **Enable compatibility mode** in the console when creating or upgrading to an OpenSearch version\. This setting makes the domain artificially report its version as 7\.10\.2 so the plugin continues to work\. To use the [AWS CLI](https://docs.aws.amazon.com/cli/latest/reference/es/) or [configuration API](configuration-api.md), set `override_main_response_version` to `true` in the advanced settings\.

To enable or disable compatibility mode on *existing* OpenSearch domains, you need to use the OpenSearch `_cluster/settings` API:

```
PUT _cluster/settings
{
  "compatibility": {
    "override_main_response_version": "true" 
  }
}
```

For an Elasticsearch OSS domain, you can continue to use the standard Elasticsearch plugin or the [logstash\-output\-amazon\_es](https://github.com/awslabs/logstash-output-amazon_es) plugin based on your authentication mechanism\. 


**Elasticsearch**  
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/managedomains-logstash.html)

## Configuration<a name="logstash-config"></a>

If your OpenSearch Service domain uses [fine\-grained access control](fgac.md) with HTTP basic authentication, configuration is similar to any other OpenSearch cluster\. This example configuration file takes its input from the open source version of Filebeat \(Filebeat OSS\):

```
input {
  beats {
    port => 5044
  }
}

output {
  opensearch {
    hosts => ["https://domain-endpoint:443"]
    ssl => true
    index => "%{[@metadata][beat]}-%{[@metadata][version]}-%{+YYYY.MM.dd}"
    user => "my-username"
    password => "my-password"
    ilm_enabled => false
  }
}
```

Configuration varies by Beats application and use case, but your Filebeat OSS configuration might look like this:

```
filebeat.inputs:
- type: log
  enabled: true
  paths:
    - /path/to/logs/dir/*.log
filebeat.config.modules:
  path: ${path.config}/modules.d/*.yml
  reload.enabled: false
setup.ilm.enabled: false
setup.ilm.check_exists: false
setup.template.settings:
  index.number_of_shards: 1
output.logstash:
  hosts: ["logstash-host:5044"]
```

If your domain uses an IAM\-based domain access policy or fine\-grained access control with an IAM master user, you must sign all requests to OpenSearch Service using IAM credentials\. In this case, the simplest solution to sign requests from Logstash OSS is to use the [logstash\-output\-amazon\_es](https://github.com/opensearch-project/logstash-output-opensearch) plugin\. 

First, install the plugin\.

```
bin/logstash-plugin install logstash-output-amazon_es
```

Then export your IAM credentials \(or run `aws configure`\)\.

```
export AWS_ACCESS_KEY_ID="your-access-key"
export AWS_SECRET_ACCESS_KEY="your-secret-key"
export AWS_SESSION_TOKEN="your-session-token"
```

Finally, change your configuration file to use the plugin for its output\. This example configuration file takes its input from files in an S3 bucket\.

```
input {
  s3 {
    bucket => "my-s3-bucket"
    region => "us-east-1"
  }
}

output {
  amazon_es {
    hosts => ["domain-endpoint"]
    ssl => true
    region => "us-east-1"
    index => "production-logs-%{+YYYY.MM.dd}"
  }
}
```

If your OpenSearch Service domain is in a VPC, the Logstash OSS machine must be able to connect to the VPC and have access to the domain through the VPC security groups\. For more information, see [About access policies on VPC domains](vpc.md#vpc-security)\.