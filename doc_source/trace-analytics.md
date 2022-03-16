# Trace Analytics for Amazon OpenSearch Service<a name="trace-analytics"></a>

The default installation of OpenSearch Dashboard for Amazon OpenSearch Service includes the Trace Analytics plugin, which you can use to analyze trace data from distributed applications\. The plugin requires OpenSearch or Elasticsearch 7\.9 or later\.

In a distributed application, a single operation, such as a user clicking a button, can trigger an extended series of events\. For example, the application front end might call a backend service, which calls another service, which queries a database, which processes the query and returns a result\. Then the first backend service sends a confirmation to the front end, which updates the UI\.

You can use Trace Analytics to help you visualize this flow of events and identify performance problems\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/ta-dashboards-trace.png)

## Prerequisites<a name="trace-prereq"></a>

Trace Analytics requires you to add [instrumentation](https://opentelemetry.io/docs/concepts/instrumenting/) to your application and generate trace data using an OpenTelemetry\-supported library such as [Jaeger](https://www.jaegertracing.io) or [Zipkin](https://zipkin.io)\. This step occurs entirely outside of OpenSearch Service\. The [AWS Distro for OpenTelemetry documentation](https://aws-otel.github.io/docs/introduction) contains example applications for many programming languages that can help you get started, including Java, Python, Go, and JavaScript\.

After you add instrumentation to your application, the [OpenTelemetry Collector](https://aws-otel.github.io/docs/getting-started/collector) receives data from the application and formats it into OpenTelemetry data\. See the list of receivers on [GitHub](https://github.com/open-telemetry/opentelemetry-collector/blob/main/receiver/README.md)\. AWS Distro for OpenTelemetry includes a [receiver for AWS X\-Ray](https://aws-otel.github.io/docs/components/x-ray-receiver)\.

Finally, [Data Prepper](https://opensearch.org/docs/latest/clients/data-prepper/index/), an independent OpenSearch component, formats that OpenTelemetry data for use with OpenSearch\. Data Prepper runs on a machine outside of the OpenSearch Service cluster, similar to Logstash\.

For a Docker Compose file that demonstrates the end\-to\-end flow of data, see the [OpenSearch documentation](https://opensearch.org/docs/latest/clients/data-prepper/get-started/)\.

## OpenTelemetry Collector sample configuration<a name="trace-otc"></a>

To use the OpenTelemetry Collector with Data Prepper, try the following sample configuration:

```
receivers:
  jaeger:
    protocols:
      grpc:
  otlp:
    protocols:
      grpc:
  zipkin:

exporters:
  otlp/data-prepper:
    endpoint: data-prepper-host:21890
    insecure: true

service:
  pipelines:
    traces:
      receivers: [jaeger, otlp, zipkin]
      exporters: [otlp/data-prepper]
```

## Data Prepper sample configuration<a name="trace-dp"></a>

To send trace data to an OpenSearch Service domain, try the following sample configuration files\.

**data\-prepper\-config\.yaml**

```
ssl: true
keyStoreFilePath: "/usr/share/data-prepper/keystore.jks" # required if ssl is true
keyStorePassword: "password" # optional, defaults to empty string
privateKeyPassword: "other_password" # optional, defaults to empty string
serverPort: 4900 # port for administrative endpoints, default is 4900
```

**pipelines\.yaml**

```
entry-pipeline:
  # Workers is the number of application threads.
  # Try setting this value to the number of CPU cores on the machine.
  # We recommend the same number of workers for all pipelines.
  workers: 4
  delay: "100" # milliseconds
  source:
    otel_trace_source:
      ssl: true
      sslKeyCertChainFile: "config/demo-data-prepper.crt"
      sslKeyFile: "config/demo-data-prepper.key"
  buffer:
    bounded_blocking:
      # Buffer size is the number of export requests to hold in memory.
      # We recommend the same value for all pipelines.
      # Batch size is the maximum number of requests each worker thread processes within the delay.
      # Keep buffer size >= number of workers * batch size.
      buffer_size: 1024
      batch_size: 256
  sink:
    - pipeline:
        name: "raw-pipeline"
    - pipeline:
        name: "service-map-pipeline"
raw-pipeline:
  workers: 4
  # We recommend the default delay for the raw pipeline.
  delay: "3000"
  source:
    pipeline:
      name: "entry-pipeline"
  prepper:
    - otel_trace_raw_prepper:
  buffer:
    bounded_blocking:
      buffer_size: 1024
      batch_size: 256
  sink:
    - opensearch:
        hosts: ["https://domain-endpoint"]
        # # Basic authentication
        # username: "ta-user"
        # password: "ta-password"
        # IAM signing
        aws_sigv4: true
        aws_region: "us-east-1"
        trace_analytics_raw: true
service-map-pipeline:
  workers: 4
  delay: "100"
  source:
    pipeline:
      name: "entry-pipeline"
  prepper:
    - service_map_stateful:
  buffer:
    bounded_blocking:
      buffer_size: 1024
      batch_size: 256
  sink:
    - opensearch:
        hosts: ["https://domain-endpoint"]
        # # Basic authentication
        # username: "ta-user"
        # password: "ta-password"
        # IAM signing
        aws_sigv4: true
        aws_region: "us-east-1"
        trace_analytics_service_map: true
```
+ For IAM signing, run `aws configure` using the AWS CLI to set your credentials\.
+ If you use [fine\-grained access control](fgac.md) with the internal user database, use the basic authentication lines instead\.

If your domain uses fine\-grained access control, you must map the Data Prepper user or role to the [all\_access role](fgac.md#fgac-more-masters)\.

If your domain doesn't use fine\-grained access control, the Data Prepper user or role must have write permissions to several indices and templates, along with permissions to access an Index State Management \(ISM\) policy and retrieve cluster settings\. The following policy shows the required permissions:

```
{
  "Version": "2012-10-17",
  "Statement": [{
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::123456789012:user/data-prepper-sink-user"
      },
      "Action": "es:ESHttp*",
      "Resource": [
        "arn:aws:es:us-east-1:123456789012:domain/domain-name/otel-v1*",
        "arn:aws:es:us-east-1:123456789012:domain/domain-name/_template/otel-v1*",
        "arn:aws:es:us-east-1:123456789012:domain/domain-name/_plugins/_ism/policies/raw-span-policy",
        "arn:aws:es:us-east-1:123456789012:domain/domain-name/_alias/otel-v1*"
      ]
    },
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::123456789012:user/data-prepper-sink-user"
      },
      "Action": "es:ESHttpGet",
      "Resource": "arn:aws:us-east-1:123456789012:domain/domain-name/_cluster/settings"
    }
  ]
}
```

Data Prepper uses port 21890 to receive data, and it must be able to connect to both the OpenTelemetry Collector and the OpenSearch cluster\. For performance tuning, adjust the worker count and buffer settings in your configuration file, along with the Java virtual machine \(JVM\) heap size for the machine\.

Full documentation for Data Prepper is available in the [OpenSearch documentation](https://opensearch.org/docs/latest/clients/data-prepper/index/)\. For convenience, we also provide an [AWS CloudFormation template](https://github.com/opensearch-project/data-prepper/blob/main/deployment-template/ec2/data-prepper-ec2-deployment-cfn.yaml) that installs Data Prepper on an Amazon EC2 instance\.

## Exploring trace data<a name="trace-dashboards"></a>

The **Dashboard** view groups traces together by HTTP method and path so that you can see the average latency, error rate, and trends associated with a particular operation\. For a more focused view, try filtering by trace group name\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/ta-dashboards-dash.png)

To drill down on the traces that make up a trace group, choose the number of traces in the right\-hand column\. Then choose an individual trace for a detailed summary\.

The **Services** view lists all services in the application, plus an interactive map that shows how the various services connect to each other\. In contrast to the dashboard \(which helps identify problems by operation\), the service map helps you identify problems by service\. Try sorting by error rate or latency to get a sense of potential problem areas of your application\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/ta-dashboards-services.png)