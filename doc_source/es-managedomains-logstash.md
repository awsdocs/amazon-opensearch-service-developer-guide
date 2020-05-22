# Loading Data with Logstash<a name="es-managedomains-logstash"></a>

The open source version of Logstash \(Logstash OSS\) provides a convenient way to use the bulk API to upload data into your Amazon ES domain\. The service supports all standard Logstash input plugins, including the Amazon S3 input plugin\. Amazon ES supports two Logstash output plugins: the standard Elasticsearch plugin and the [logstash\-output\-amazon\-es](https://github.com/awslabs/logstash-output-amazon_es) plugin, which uses IAM credentials to sign and export Logstash events to Amazon ES\.

If your Amazon ES domain uses [fine\-grained access control](fgac.md) with HTTP basic authentication, configuration is similar to any other Elasticsearch cluster\. This example configuration file takes its input from the open source version of Filebeat \(Filebeat OSS\)\.

```
input {
  beats {
    port => 5044
  }
}

output {
  elasticsearch {
    hosts => ["https://domain-endpoint:443"]
    ssl => true
    index => "%{[@metadata][beat]}-%{[@metadata][version]}-%{+YYYY.MM.dd}"
    user => "some-user"
    password => "some-user-password"
    ilm_enabled => false
  }
}
```

If your domain uses an IAM\-based domain access policy or fine\-grained access control with an IAM master user, you must sign all requests to Amazon ES using IAM credentials\. In this case, the simplest solution to sign requests from Logstash is to use the [logstash\-output\-amazon\-es](https://github.com/awslabs/logstash-output-amazon_es) plugin\. First, install the plugin\.

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

If your Amazon ES domain is in a VPC, Logstash must run on a machine in that same VPC and have access to the domain through the VPC security groups\. For more information, see [About Access Policies on VPC Domains](es-vpc.md#es-vpc-security)\.