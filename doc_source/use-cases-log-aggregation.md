# Event aggregation with Amazon OpenSearch Ingestion<a name="use-cases-log-aggregation"></a>

You can use Amazon OpenSearch Ingestion to aggregate data from different events over a period of time\. Aggregating events can help reduce unnecessary log volume and handle use cases like multi\-line logs that come in as separate events\. The [Aggregate processor](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/aggregate/) is a stateful processor that groups events based on the values for a set of specified identification keys, and performs a configurable action on each group\. 

State in the Aggregate processor is stored in memory\. For example, in order to combine four events into one, the processor needs to retain pieces of the first three events\. The state of an aggregate group of events is kept for a configurable amount of time\. Depending on your logs, the aggregate action being used, and the amount of memory options in the processor configuration, the aggregation could take place over a long period of time\.

In addition to these examples, you can also use the **Log aggregation with conditional routing** blueprint\. For more information about blueprints, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.

**Topics**
+ [Basic usage](#use-cases-log-aggregation-usage)
+ [Removing duplicates](#use-cases-log-aggregation-duplicates)
+ [Log aggregation and conditional routing](#use-cases-log-aggregation-conditional-routing)

## Basic usage<a name="use-cases-log-aggregation-usage"></a>

The following example pipeline extracts the fields `sourceIp`, `destinationIp`, and `port` using the [Grok processor](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/grok/), and then aggregates on those fields over a period of 30 seconds using the [Aggregate processor](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/aggregate/) and the `put_all` action\. At the end of the 30 seconds, the aggregated log is sent to the OpenSearch sink\.

```
version: "2"
aggregate_pipeline:  
   source:
     http:
      path: "/${pipelineName}/logs"
   processor:
     - grok:
         match: 
           log: ["%{IPORHOST:sourceIp} %{IPORHOST:destinationIp} %{NUMBER:port:int}"]
     - aggregate:
         group_duration: "30s"
         identification_keys: ["sourceIp", "destinationIp", "port"]
         action:
           put_all:
   sink:
     - opensearch:
         ...
         index: aggregated_logs
```

For example, consider the following batch of logs:

```
{ "log": "127.0.0.1 192.168.0.1 80", "status": 200 }
{ "log": "127.0.0.1 192.168.0.1 80", "bytes": 1000 }
{ "log": "127.0.0.1 192.168.0.1 80" "http_verb": "GET" }
```

The Grok processor will extract the `identification_keys` to create the following logs:

```
{ "sourceIp": "127.0.0.1", "destinationIp": "192.168.0.1", "port": 80, "status": 200 }
{ "sourceIp": "127.0.0.1", "destinationIp": "192.168.0.1", "port": 80, "bytes": 1000 }
{ "sourceIp": "127.0.0.1", "destinationIp": "192.168.0.1", "port": 80, "http_verb": "GET" }
```

When the group finishes 30 seconds after when the first log is received by the Aggregate processor, the following aggregated log is written to the sink:

```
{ "sourceIp": "127.0.0.1", "destinationIp": "192.168.0.1", "port": 80, "status": 200, "bytes": 1000, "http_verb": "GET" }
```

## Removing duplicates<a name="use-cases-log-aggregation-duplicates"></a>

You can remove duplicate entries by deriving keys from incoming events and specifying the `remove_duplicates` option for the Aggregate processor\. This action immediately processes the first event for a group, and drops all following events in that group\. 

In the following example, the first event is processed with the identification keys `sourceIp` and `destinationIp`:

```
{ "sourceIp": "127.0.0.1", "destinationIp": "192.168.0.1", "status": 200 }
```

The pipeline will then drop the following event because it has the same keys:

```
{ "sourceIp": "127.0.0.1", "destinationIp": "192.168.0.1", "bytes": 1000 }
```

The pipeline processes this event and creates a new group because the `sourceIp` is different:

```
{ "sourceIp": "127.0.0.2", "destinationIp": "192.168.0.1", "bytes": 1000 }
```

## Log aggregation and conditional routing<a name="use-cases-log-aggregation-conditional-routing"></a>

You can use multiple plugins to combine log aggregation with conditional routing\. In this example, the sub\-pipeline `log-aggregate-pipeline` receives logs via an HTTP client like FluentBit and extracts important values from the logs by matching the value in the `log` key against the common Apache log pattern\. 

Two of the values it extracts from the logs with a grok pattern include `response` and `clientip`\. The Aggregate processor then uses the `clientip` value, along with the `remove_duplicates` option, to drop any logs that contain a `clientip` that has already been processed within the given `group_duration`\.

Three routes, or conditional statements, exist in the pipeline\. These routes separate the value of the response into 2xx/3xx, 4xx, and 5xx responses\. Logs with a 2xx and 3xx status are sent to the `aggregated_2xx_3xx` index, logs with a 4xx status are sent to the `aggregated_4xx` index, and logs with a 5xx status are sent to the `aggregated_5xx` index\.

```
version: "2"
log-aggregate-pipeline:
  source:
    http:
      # Provide the path for ingestion. ${pipelineName} will be replaced with pipeline name configured for this pipeline.
      # In this case it would be "/log-aggregate-pipeline/logs". This will be the FluentBit output URI value.
      path: "/${pipelineName}/logs"
  processor:
    - grok:
        match:
          log: [ "%{COMMONAPACHELOG_DATATYPED}" ]
    - aggregate:
        identification_keys: ["clientip"]
        action:
          remove_duplicates:
        group_duration: "180s"
  route:
    - 2xx_status: "/response >= 200 and /response < 300"
    - 3xx_status: "/response >= 300 and /response < 400"
    - 4xx_status: "/response >= 400 and /response < 500"
    - 5xx_status: "/response >= 500 and /response < 600"
  sink:
    - opensearch:
        ...
        index: "aggregated_2xx_3xx"
        routes:
          - 2xx_status
          - 3xx_status
    - opensearch:
        ...
        index: "aggregated_4xx"
        routes:
          - 4xx_status
    - opensearch:
        ...
        index: "aggregated_5xx"
        routes:
          - 5xx_status
```