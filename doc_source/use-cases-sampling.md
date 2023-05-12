# Sampling with Amazon OpenSearch Ingestion<a name="use-cases-sampling"></a>

Amazon OpenSearch Ingestion provides the following sampling capabilities\. In addition to these examples, you can also use the **Apache log sampling** blueprint\. For more information about blueprints, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.

**Topics**
+ [Time sampling](#use-cases-sampling-time)
+ [Percentage sampling](#use-cases-sampling-percentage)
+ [Tail sampling](#use-cases-sampling)

## Time sampling<a name="use-cases-sampling-time"></a>

You can use the `rate_limiter` action within the [Aggregate processor](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/aggregate/) to limit the number of events that can be processed per second\. You can choose to either drop excess events or carry them forward to the next time period\.

In this example, only 100 events per second with a status code of `200` are sent to the sink from a given IP address\. It drops all excess events from the configured time window\.

```
...
  processor:
   - aggregate:                                                                                                                                          
        identification_keys: ["clientip"]                                                                                                      
        action:                                                                                                                                           
          rate_limiter:                                                                                                                                   
            events_per_second: 100                                                                                                                        
            when_exceeds: drop
        when: "/status == 200"  
...
```

If you instead set the `when_exceeds` option to `block`, the processor will process excess events in the next time window\. 

## Percentage sampling<a name="use-cases-sampling-percentage"></a>

Use the `percent_sampler` action within the Aggregate processor to limit the number of events that are sent to a sink\. All excess events will be dropped\.

In this example, only 20 percent of events with a status code of `200` are sent to the sink from a given IP address:

```
...
  processor:
  - aggregate:                                                                                                                                          
        identification_keys: ["clientip"]  
        duration :                                                                                                    
        action:                                                                                                                                           
          percent_sampler:                                                                                                                                   
            percent: 20                                                                                                                        
        when: "/status == 200" 
...
```

## Tail sampling<a name="use-cases-sampling"></a>

Use the `tail_sampler` action within the Aggregate processor to sample events based on a set of defined policies\. This action waits for an aggregation to complete across different aggregation periods based on the configured wait period\. When an aggregation is complete, and if it matches the specific error condition, it's sent to the sink\. Otherwise, only a configured percentage of events are sent to the sink\.

The following example pipeline sends all OpenTelemetry traces with an error condition status of `2` to the sink\. It only sends 20% of the traces that don't match this error condition to the sink\.

```
...
  processor:
   - aggregate:                                                                                                                                          
        identification_keys: ["traceId"]                                                                                                                   
        action:                                                                                                                                           
          tail_sampler:                                                                                                                                   
            percent: 20                                                                                                                                   
            wait_period: "10s"                                                                                                                            
            error_condition: "/status == 2"                                                                                                              
          
...
```

If you set the error condition to `false` or don't include it, only a the configured percentage of events is allowed to pass through, determined by a probabilistic outcome\.

Because it's difficult to determine exactly when tail sampling should occur, you can use the `wait_period` option to measure the idle time after the last event was received\.