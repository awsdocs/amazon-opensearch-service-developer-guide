# Amazon OpenSearch Ingestion<a name="ingestion"></a>

Amazon OpenSearch Ingestion is a fully managed, serverless data collector that delivers real\-time log, metric, and trace data to Amazon OpenSearch Service domains and OpenSearch Serverless collections\.

With OpenSearch Ingestion, you no longer need to use third\-party solutions like Logstash or Jaeger to ingest data into your OpenSearch Service domains and OpenSearch Serverless collections\. You configure your data producers to send data to OpenSearch Ingestion\. Then, it automatically delivers the data to the domain or collection that you specify\. You can also configure OpenSearch Ingestion to transform your data before delivering it\.

Also, with OpenSearch Ingestion, you don't need to worry about provisioning servers, managing and patching software, or scaling your cluster of servers\. You provision ingestion *pipelines* directly within the AWS Management Console, and OpenSearch Ingestion takes care of managing and scaling them\.

OpenSearch Ingestion is a subset of Amazon OpenSearch Service\. It's powered by Data Prepper, which is an open source data collector that can filter, enrich, transform, normalize, and aggregate data for downstream analysis and visualization\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/Ingestion.png)

**Topics**
+ [Key concepts](#ingestion-process)
+ [Benefits of OpenSearch Ingestion](#ingestion-benefits)
+ [Limitations](#ingestion-limitations)
+ [Supported Data Prepper versions](#ingestion-supported-versions)
+ [Scaling pipelines](#ingestion-scaling)
+ [OpenSearch Ingestion pricing](#ingestion-pricing)
+ [Supported AWS Regions](#osis-regions)
+ [OpenSearch Ingestion quotas](#osis-quotas)
+ [Setting up roles and users in Amazon OpenSearch Ingestion](pipeline-security-overview.md)
+ [Getting started with Amazon OpenSearch Ingestion](osis-getting-started-tutorials.md)
+ [Overview of pipeline features in Amazon OpenSearch Ingestion](osis-features-overview.md)
+ [Creating Amazon OpenSearch Ingestion pipelines](creating-pipeline.md)
+ [Viewing Amazon OpenSearch Ingestion pipelines](list-pipeline.md)
+ [Updating Amazon OpenSearch Ingestion pipelines](update-pipeline.md)
+ [Stopping and starting Amazon OpenSearch Ingestion pipelines](pipeline--stop-start.md)
+ [Deleting Amazon OpenSearch Ingestion pipelines](delete-pipeline.md)
+ [Supported plugins and options for Amazon OpenSearch Ingestion pipelines](pipeline-config-reference.md)
+ [Sending data to Amazon OpenSearch Ingestion pipelines](configure-client.md)
+ [Use cases for Amazon OpenSearch Ingestion](use-cases-overview.md)
+ [Security in Amazon OpenSearch Ingestion](pipeline-security-model.md)
+ [Tagging Amazon OpenSearch Ingestion pipelines](tag-pipeline.md)
+ [Logging and monitoring Amazon OpenSearch Ingestion with Amazon CloudWatch](monitoring-pipelines.md)
+ [Best practices for Amazon OpenSearch Ingestion](osis-best-practices.md)

## Key concepts<a name="ingestion-process"></a>

As you get started with OpenSearch Ingestion, you can benefit from understanding the following concepts:

**Pipeline**  
From an OpenSearch Ingestion perspective, a *pipeline* refers to a single provisioned data collector that you create within OpenSearch Service\. You can think of it as the entire YAML configuration file, which includes one or more sub\-pipelines\. For steps to create an ingestion pipeline, see [Creating pipelines](creating-pipeline.md#create-pipeline)\.

**Sub\-pipeline**  
You define sub\-pipelines *within* a YAML configuration file\. Each sub\-pipeline is a combination of a source, a buffer, zero or more processors, and one or more sinks\. You can define multiple sub\-pipelines in a single YAML file, each with unique sources, processors, and sinks\. To aid in monitoring with CloudWatch and other services, we recommend that you specify a pipeline name that's distinct from all of its sub\-pipelines\.  
You can string multiple sub\-pipelines together within a single YAML file, so that the source for one sub\-pipeline is another sub\-pipeline, and its sink is a third sub\-pipeline\. For an example, see [Writing from OpenTelemetry Collector](configure-client.md#configure-client-otel)\.

**Source**  
The input component of a sub\-pipeline\. It defines the mechanism through which a pipeline consumes records\. The source can consume events either by receiving them over HTTPS, or by reading from external endpoints such as Amazon S3\. There are two types of sources: *push\-based* and *pull\-based*\. Push\-based sources, such as [HTTP](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/http-source/) and [OTel logs](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-logs-source/), stream records to ingestion endpoints\. Pull\-based sources, such as [OTel trace group](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/otel-trace-group/) and [S3](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/s3/), pull data from the source\.

**Processors**  
Intermediate processing units that can filter, transform, and enrich records into a desired format before publishing them to the sink\. The processor is an optional component of a pipeline\. If you don't define a processor, records are published in the format defined in the source\. You can have more than one processor\. A pipeline runs processors in the order that you define them\.

**Sink**  
The output component of a sub\-pipeline\. It defines one or more destinations that a sub\-pipeline publishes records to\. OpenSearch Ingestion supports OpenSearch Service domains as sinks\. It also supports sub\-pipelines as sinks\. This means that you can string together multiple sub\-pipelines within a single OpenSearch Ingestion pipeline \(YAML file\)\. Self\-managed OpenSearch clusters aren't supported as sinks\.

**Buffer**  
The part of a processor that acts as the layer between the source and the sink\. You can't manually configure a buffer within your pipeline\. OpenSearch Ingestion uses a default buffer configuration\.

**Route**  
The part of a processor that allows pipeline authors to only send events that match certain conditions to different sinks\.

A valid sub\-pipeline definition must contain a source and a sink\. For more information about each of these pipeline elements, see the [configuration reference](pipeline-config-reference.md#ingestion-parameters)\. 

## Benefits of OpenSearch Ingestion<a name="ingestion-benefits"></a>

OpenSearch Ingestion has the following main benefits:
+ Eliminates the need for you to manually manage a self\-provisioned pipeline\.
+ Automatically scales your pipelines based on capacity limits that you define\.
+ Keeps your pipeline up to date with security and bug patches\.
+ Provides the option to connect pipelines to your virtual private cloud \(VPC\) for an added layer of security\.
+ Allows you to stop and start pipelines in order to control costs\.
+ Provides pipeline configuration blueprints for popular use cases to help you get up and running faster\.
+ Allows you to interact programmatically with your pipelines through the various AWS SDKs and the OpenSearch Ingestion API\.
+ Supports performance monitoring in Amazon CloudWatch and error logging in CloudWatch Logs\.

## Limitations<a name="ingestion-limitations"></a>

OpenSearch Ingestion has the following limitations:
+ You can only ingest data into domains running OpenSearch 1\.0 or later, or Elasticsearch 7\.4 or later\. If you're using the [OTel trace](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/otel-trace/) source, we recommend using Elasticsearch 7\.9 or later so that you can use the [OpenSearch Dashboards plugin](https://opensearch.org/docs/latest/observability-plugin/trace/ta-dashboards/)\.
+ A pipeline must be in the same account as its sink\.
+ Cross\-Region ingestion is only supported for public pipelines\.
+ If a pipeline is provisioned within a VPC, it must be in the same AWS Region as the domain that it's writing to\.
+ You can only configure a single data source within a pipeline definition\.
+ Each pipeline can only send data to a single OpenSearch Service domain\. You can't configure different domains as sinks within the same pipeline definition\.
+ You can't specify [self\-managed OpenSearch clusters](https://opensearch.org/docs/latest/about/#clusters-and-nodes) as sinks\.
+ Pipelines with a push\-based source use an in\-memory buffer to store records while processing\. They can temporarily lose data if the underlying pipeline task fails\.
+ You can't specify a [custom endpoint](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/customendpoint.html) as a sink\. You can still write to a domain that has custom endpoints enabled, but you must specify its standard endpoint\.
+ There are some constraints on the parameters that you can include in a pipeline configuration\. For more information, see [Configuration requirements and constraints](pipeline-config-reference.md#ingestion-parameters)\.

## Supported Data Prepper versions<a name="ingestion-supported-versions"></a>

OpenSearch Ingestion currently supports the following major versions of Data Prepper:
+ 2\.x

When you create a pipeline, use the required `version` option to specify the major version of Data Prepper to use\. For example, `version: "2"`\. OpenSearch Ingestion retrieves the latest supported *minor* version of that major version and provisions the pipeline with that version\. For more information, see [Specifying the pipeline version](creating-pipeline.md#pipeline-version)\.

For information about the features and bug fixes that are in each version of Data Prepper, see the [Releases](https://github.com/opensearch-project/data-prepper/releases) page\. Not every minor version of a particular major version is supported by OpenSearch Ingestion\.

## Scaling pipelines<a name="ingestion-scaling"></a>

You don't need to provision and manage pipeline capacity yourself\. OpenSearch Ingestion automatically scales your pipeline capacity according to your estimated workload, based on the minimum and maximum *Ingestion OpenSearch Compute Units* \(Ingestion OCUs\) that you specify\.

Each Ingestion OCU is a combination of approximately 8 GiB of memory and 2 vCPUs\. You can specify the minimum and maximum OCU values for a pipeline, and OpenSearch Ingestion automatically scales your pipeline capacity based on these limits\.

 You can specify the following values: 
+ **Minimum capacity** – The pipeline can reduce capacity down to this number of Ingestion OCUs\. The specified minimum capacity is also the starting capacity for a pipeline\.
+ **Maximum capacity** – The pipeline can increase capacity up to this number of Ingestion OCUs\.

![\[Image NOT FOUND\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/images/pipeline-scaling.png)

Make sure that the maximum capacity for a pipeline is high enough to handle spikes in workload, and the minimum capacity is low enough to minimize costs when the pipeline isn't busy\. Based on your settings, OpenSearch Ingestion automatically scales the number of Ingestion OCUs for your pipeline to process the ingest workload\. At any specific time, you're charged only for the Ingestion OCUs that are being actively used by your pipeline\.

The capacity allocated to your OpenSearch Ingestion pipeline scales up and down based on the processing requirements of your pipeline and the load generated by your client application\. When capacity is constrained, OpenSearch Ingestion scales up by allocating more compute units \(GiB of memory\)\. When your pipeline is processing smaller workloads, or not processing data at all, it can scale down to the minimum configured Ingestion OCUs\.

You can specify a minimum of 1 Ingestion OCU, a maximum of 96 Ingestion OCUs for stateless pipelines, and a maximum of 48 Ingestion OCUs for stateful pipelines\. We recommend a minimum of at least 2 Ingestion OCUs for push\-based sources\.

Given a standard log pipeline with a single source, a simple grok pattern, and a sink, each compute unit can support up to 2 MiB per second\. For more complex log pipelines with multiple processors, each compute unit might support less ingest load\. Based on pipeline capacity and resource utilization, the OpenSearch Ingestion scaling process kicks in\.

To ensure high availability, Ingestion OCUs are distributed across Availability Zones \(AZs\)\. The number of AZs depends on the minimum capacity that you specify\.

For example, if you specify a minimum of 2 compute units, the Ingestion OCUs that are in use at any given time are evenly distributed across 2 AZs\. If you specify a minimum of 3 or more compute units, the Ingestion OCUs are evenly distributed across 3 AZs\. We recommend that you provision *at least two* Ingestion OCUs to ensure 99\.9% availability for your ingest pipelines\.

You're not billed for Ingestion OCUs when a pipeline is in the `Create failed`, `Creating`, `Deleting`, and `Stopped` states\.

For instructions to configure and retrieve capacity settings for a pipeline, see [Creating pipelines](creating-pipeline.md#create-pipeline)\.

## OpenSearch Ingestion pricing<a name="ingestion-pricing"></a>

At any specific time, you only pay for the number of Ingestion OCUs that are allocated to a pipeline, regardless of whether there's data flowing through the pipeline\. OpenSearch Ingestion immediately accommodates your workloads by scaling pipeline capacity up or down based on usage\.

For full pricing details, see [Amazon OpenSearch Service pricing](https://aws.amazon.com/opensearch-service/pricing/)\.

## Supported AWS Regions<a name="osis-regions"></a>

OpenSearch Ingestion is available in a subset of AWS Regions that OpenSearch Service is available in\. For a list of supported Regions, see [Amazon OpenSearch Service endpoints and quotas](https://docs.aws.amazon.com/general/latest/gr/opensearch-service.html) in the *AWS General Reference*\.

## OpenSearch Ingestion quotas<a name="osis-quotas"></a>

For a list of default quotas for OpenSearch Ingestion resources, see [Amazon OpenSearch Service quotas](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/limits.html)\.