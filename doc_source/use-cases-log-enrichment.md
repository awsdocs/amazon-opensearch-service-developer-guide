# Log enrichment with Amazon OpenSearch Ingestion<a name="use-cases-log-enrichment"></a>

You can perform different types of log enrichment with Amazon OpenSearch Ingestion\. In addition to these examples, you can also use the **Generic log pipeline** blueprint\. For more information about blueprints, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.

**Topics**
+ [Filtering](#use-cases-log-enrichment-filtering)
+ [Extracting key\-value pairs from strings](#use-cases-log-enrichment-extract-kv)
+ [Mutating events](#use-cases-log-enrichment-mutating-events)
+ [Mutating strings](#use-cases-log-enrichment-mutating-strings)
+ [Converting lists to maps](#use-cases-log-enrichment-list-map)
+ [Processing incoming timestamps](#use-cases-log-enrichment-timestamps)

## Filtering<a name="use-cases-log-enrichment-filtering"></a>

Use the [Drop events](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/drop-events/) processor to filter out specific log events before sending them to a sink\. For example, say you're collecting web request logs and only want to store unsuccessful requests\. You create the following pipeline, which drops any requests where the response is less than 400 so that only log events with HTTP status codes 400 and above remain\.

```
version: "2"
log-pipeline:
  source:
  ...
  processor:
    - grok:
        match:
          log: [ "%{COMMONAPACHELOG_DATATYPED}" ]
    - drop:
        drop_when: "/response < 400"
  sink:
    - opensearch:
        ...
        index: failure_logs
```

The `drop_when` option specifies which evens to drop from the pipeline\.

## Extracting key\-value pairs from strings<a name="use-cases-log-enrichment-extract-kv"></a>

Log data often includes strings of key\-value pairs\. One common scenario is an HTTP query string\. For example, if a web user queries a pageable URL, the HTTP logs might have the following HTTP query string:

```
page=3&q=my-search-term
```

To perform analysis using the search terms, you can extract the value of `q` from a query string\. The [Key value](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/key-value/) processor provides robust support for extracting keys and values from strings\.

The following example combines the `split_string` and `key_value` processors to extract query parameters from an Apache log line:

```
version: "2"
pipeline
 ...
  processor:
    - grok:
      match:
        message: [ "%{COMMONAPACHELOG_DATATYPED}" ]
    - split_string:
      entries:
        - source: request
          delimiter: "?"
    - key_value:
      source: "/request/1"
      field_split_characters: "&"
      value_split_characters: "="
      destination: query_params
```

## Mutating events<a name="use-cases-log-enrichment-mutating-events"></a>

The different [Mutate event](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/mutate-event/) processors let you rename, copy, add, and delete event entries\.

In this example, the first processor sets the value of the `debug` key to `true` if the key already exists in the event\. The second processor only sets the `debug` key to `true` if the key doesn't exist in the event, because `overwrite_if_key_exists` is set to `true`\.

```
...
processor:
  - add_entries:
    entries:
    - key: "debug"
      value: true 
...
processor:
  - add_entries:
    entries:
    - key: "debug"
      value: true 
      overwrite_if_key_exists: true
...
```

You can also use a format string to construct new entries from existing entries\. For example, `${date}-${time}` will create a new entry based on the values of the existing entries `date` and `time`\. 

For example, the following pipeline adds new event entries dynamically from existing events:

```
processor:
  - add_entries:
    entries:
    - key: "key_three"
      format: "${key_one}-${key_two}
```

For example, consider the following incoming event:

```
{
   "key_one": "value_one",
   "key_two": "value_two"
}
```

The processor transforms it into an event with a new key `key_three`, which combines values of other keys in the original event\.

```
{
   "key_one": "value_one",
   "key_two": "value_two",
   "key_three": "value_one-value_two"
}
```

## Mutating strings<a name="use-cases-log-enrichment-mutating-strings"></a>

The various [Mutate string](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/mutate-string/) processors offer tools to manipulate strings in incoming data\. For example, if you need to split a string into an array, use the `split_string` processor:

```
...
processor:
  - split_string:
    entries:
    - source: "message"
      delimiter: "&"
...
```

The processor will transform a string such as `a&b&c` into `["a", "b", "c"].`

## Converting lists to maps<a name="use-cases-log-enrichment-list-map"></a>

The [List\-to\-map](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/mutate-string/) processor, which is one of the Mutate events processors, converts a list of objects in an event to a map\.

For example, consider the following processor configuration: 

```
...
processor:
    - list_to_map:
        key: "name"
        source: "A-car-as-list"
        target: "A-car-as-map"
        value_key: "value"
        flatten: true
...
```

This processor will convert an event that contains a list of objects like this:

```
{
  "A-car-as-list": [
    {
      "name": "make",
      "value": "tesla"
    },
    {
      "name": "model",
      "value": "model 3"
    },
    {
      "name": "color",
      "value": "white"
    }
  ]
}
```

Into a map:

```
{
  "A-car-as-map": {
    "make": "tesla",
    "model": "model 3",
    "color": "white"
  }
}
```

As another example, say you have an incoming event with the following structure:

```
{
  "mylist" : [
    {
      "somekey" : "a",
      "somevalue" : "val-a1",
      "anothervalue" : "val-a2"
    },
    {
      "somekey" : "b",
      "somevalue" : "val-b1",
      "anothervalue" : "val-b2"
    },
    {
      "somekey" : "b",
      "somevalue" : "val-b3",
      "anothervalue" : "val-b4"
    },
    {
      "somekey" : "c",
      "somevalue" : "val-c1",
      "anothervalue" : "val-c2"
    }
  ]
}
```

You can define the following options in the processor configuration:

```
...
processor:            
    - list_to_map:
        key: "somekey"
        source: "mylist"
        target: "myobject"
        value_key: "value"
        flatten: true
...
```

The processor modifies the event by removing `mylist` and adding the new `myobject` object:

```
{
  "myobject" : {
    "a" : [
      {
        "somekey" : "a",
        "somevalue" : "val-a1",
        "anothervalue" : "val-a2"
      }  
    ],
    "b" : [
      {
        "somekey" : "b",
        "somevalue" : "val-b1",
        "anothervalue" : "val-b2"
      },
      {
        "somekey" : "b",
        "somevalue" : "val-b3",
        "anothervalue" : "val-b4"
      }
    "c" : [
      {
        "somekey" : "c",
        "somevalue" : "val-c1",
        "anothervalue" : "val-c2"
      }  
    ]
  }
}
```

In many cases, you might want to flatten the array for each key\. In these situations, you must choose only one object to remain\. The processor offers the choice of either first or last\.

```
...
processor:
    - list_to_map:
        key: "somekey"
        source: "mylist"
        target: "myobject"
        flatten: true
...
```

The incoming event structure is then flattened accordingly:

```
{
  "myobject" : {
    "a" : {
      "somekey" : "a",
      "somevalue" : "val-a1",
      "anothervalue" : "val-a2"
    },
    "b" : {
      "somekey" : "b",
      "somevalue" : "val-b1",
      "anothervalue" : "val-b2"
    }
    "c" : {
      "somekey" : "c",
      "somevalue" : "val-c1",
      "anothervalue" : "val-c2"
    }
  }
}
```

You can use the List\-to\-map processor to process AWS WAF logs\. For example, consider a sample WAF log like this:

```
{
    "webaclId": "arn:aws:wafv2:ap-southeast-2:111122223333:regional/webacl/STMTest/1EXAMPLE-2ARN-3ARN-4ARN-123456EXAMPLE",
    "httpRequest": {
        "headers": [
            {
                "name": "Host",
                "value": "localhost:1989"
            },
            {
                "name": "User-Agent",
                "value": "curl/7.61.1"
            }
        ]
    }
}
```

If the following pipeline processes the event:

```
...
processor:
    - list_to_map:
        key: "name"
        source: "httpRequest/headers"
        value_key: "value"
        flatten: true
...
```

It will create the following new event:

```
{
    "webaclId": "arn:aws:wafv2:ap-southeast-2:111122223333:regional/webacl/STMTest/1EXAMPLE-2ARN-3ARN-4ARN-123456EXAMPLE",
    "httpRequest": {
        "headers": [
            {
                "name": "Host",
                "value": "localhost:1989"
            },
            {
                "name": "User-Agent",
                "value": "curl/7.61.1"
            }
        ]
    },
    "Host": "localhost:1989",
    "User-Agent": "curl/7.61.1"
}
```

## Processing incoming timestamps<a name="use-cases-log-enrichment-timestamps"></a>

The [Date](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/date/) processor parses the timestamp key from incoming events by converting it to ISO 8601 format\.

```
...
  processor:          
    - date:
        match:
          - key: timestamp
            patterns: ["dd/MMM/yyyy:HH:mm:ss"] 
        destination: "@timestamp"
        source_timezone: "America/Los_Angeles"
        destination_timezone: "America/Chicago"
        locale: "en_US"
...
```

If the pipeline above processes the following event:

```
{"timestamp": "10/Feb/2000:13:55:36"}
```

It converts the event into the following format:

```
{
  "timestamp":"10/Feb/2000:13:55:36",
  "@timestamp":"2000-02-10T15:55:36.000-06:00"
}
```

### Generating timestamps<a name="use-cases-log-enrichment-generating-timestamp"></a>

The Date processor can generate timestamps for incoming events if you specify `@timestamp` for the `destination` option\.

```
...
    processor:
    - date:
        from_time_received: true
        destination: "@timestamp"
...
```

### Deriving punctuation patterns<a name="use-cases-log-enrichment-timestamps-deriving"></a>

[Substitute string](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/date/) processor \(which is one of the Mutate events processors\) lets you derive a punctuation pattern from incoming events\. In the following example pipeline, the processor will scan incoming Apache log events and derive punctuation patterns from them\. 

```
processor:                                                                                                                                              
    - substitute_string:                                                                                                                                  
        entries:                                                                                                                                          
          - source: "message"                                                                                                                             
            from: "[a-zA-Z0-9_]+"                                                                                                                         
            to: ""                                                                                                                                        
          - source: "message"                                                                                                                             
            from: "[ ]+"                                                                                                                                  
            to: "_"
```

The following incoming Apache HTTP log will generate a punctuation pattern:

```
[{"message":"10.10.10.11 - admin [19/Feb/2015:15:50:36 -0500] \"GET /big2.pdf HTTP/1.1\" 200 33973115 0.202 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.111 Safari/537.36\""}]

{"message":"..._-_[//:::_-]_\"_/._/.\"_._\"-\"_\"/._(;_)_/._(,_)_/..._/.\""}
```

You can count these generated patterns by passing them through the [Aggregate](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/aggregate/) processor with the `count` action\. 