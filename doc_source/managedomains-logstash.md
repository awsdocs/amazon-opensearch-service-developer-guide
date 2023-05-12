# Loading data into Amazon OpenSearch Service with Logstash<a name="managedomains-logstash"></a>

The open source version of Logstash \(Logstash OSS\) provides a convenient way to use the bulk API to upload data into your Amazon OpenSearch Service domain\. The service supports all standard Logstash input plugins, including the Amazon S3 input plugin\. OpenSearch Service supports the [logstash\-output\-opensearch](https://github.com/opensearch-project/logstash-output-opensearch) output plugin, which supports both basic authentication and IAM credentials\. The plugin works with version 8\.1 and lower of Logstash OSS\.

## Configuration<a name="logstash-config"></a>

Logstash configuration varies based on the type of authentication your domain uses\.

No matter which authentication method you use, you must set `ecs_compatibility` to `disabled` in the output section of the configuration file\. Logstash 8\.0 introduced a breaking change where all plugins are run in [ECS compatibility mode by default](https://www.elastic.co/guide/en/logstash/current/ecs-ls.html#_specific_plugin_instance)\. You must override the default value to maintain legacy behavior\.

### Fine\-grained access control configuration<a name="logstash-config-fgac"></a>

If your OpenSearch Service domain uses [fine\-grained access control](fgac.md) with HTTP basic authentication, configuration is similar to any other OpenSearch cluster\. This example configuration file takes its input from the open source version of Filebeat \(Filebeat OSS\):

```
input {
  beats  {
    port => 5044
  }
}

output {
  opensearch {
    hosts       => "https://domain-endpoint:443"
    user        => "my-username"
    password    => "my-password"
    index       => "logstash-logs-%{+YYYY.MM.dd}"
    ecs_compatibility => disabled
    ssl_certificate_verification => false
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

### IAM configuration<a name="logstash-config-iam"></a>

If your domain uses an IAM\-based domain access policy or fine\-grained access control with a master user, you must sign all requests to OpenSearch Service using IAM credentials\. The following identity\-based policy grants all HTTP requests to your domain's subresources\.

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "es:ESHttp*"
      ],
      "Resource": "arn:aws:es:region:aws-account-id:domain/domain-name/*"
    }
  ]
}
```

To set up your Logstash configuration, change your configuration file to use the plugin for its output\. This example configuration file takes its input from files in an S3 bucket:

```
input {
  s3 {
    bucket => "my-s3-bucket"
    region => "us-east-1"
  }
}

output {        
  opensearch {     
    hosts => ["domain-endpoint:443"]             
    auth_type => {    
      type => 'aws_iam'     
      aws_access_key_id => 'your-access-key'     
      aws_secret_access_key => 'your-secret-key'     
      region => 'us-east-1'         
      }         
      index  => "logstash-logs-%{+YYYY.MM.dd}"  
      ecs_compatibility => disabled    
  }            
}
```

If you don't want to provide your IAM credentials within the configuration file, you can export them \(or run `aws configure`\):

```
export AWS_ACCESS_KEY_ID="your-access-key"
export AWS_SECRET_ACCESS_KEY="your-secret-key"
export AWS_SESSION_TOKEN="your-session-token"
```

If your OpenSearch Service domain is in a VPC, the Logstash OSS machine must be able to connect to the VPC and have access to the domain through the VPC security groups\. For more information, see [About access policies on VPC domains](vpc.md#vpc-security)\.