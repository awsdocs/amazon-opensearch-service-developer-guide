# Grok pattern matching with Amazon OpenSearch Ingestion<a name="use-cases-pattern-matching"></a>

Amazon OpenSearch Ingestion provides pattern matching capabilities with the [Grok processor](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/processors/grok/)\. The Grok processor is based on the `[java\-grok](https://mvnrepository.com/artifact/io.krakens/java-grok)` library and supports all compatible patterns\. The `java-grok` library is built using the `[java\.util\.regex](https://docs.oracle.com/javase/8/docs/api/java/util/regex/package-summary.html)` regular expression library\.

You can add custom patterns to your pipelines using the `patterns_definitions` option\. When debugging custom patterns, the [Grok Debugger](https://grokdebugger.com/) can be helpful\.

In addition to these examples, you can also use the **Apache log pipeline** blueprint\. For more information about blueprints, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.

**Topics**
+ [Basic usage](#use-cases-pattern-matching-usage)
+ [Including named and empty captures](#use-cases-pattern-matching-named)
+ [Overwriting keys](#use-cases-pattern-matching-overwriting)
+ [Using custom patterns](#use-cases-pattern-matching-custom)
+ [Storing captures with a parent key](#use-cases-pattern-matching-parent)

## Basic usage<a name="use-cases-pattern-matching-usage"></a>

To get started with pattern matching, create the following pipeline:

```
version: "2"
patten-matching-pipeline:
  source
    ...
  processor:
    - grok:
        match:
          message: ['%{IPORHOST:clientip} \[%{HTTPDATE:timestamp}\] %{NUMBER:response_status:int}']
  sink:
    - opensearch:
        # Provide an OpenSearch Service domain endpoint
        # Enable the 'serverless' flag if the sink is an OpenSearch Serverless collection
        aws:
          ...
        index: "metrics_for_traces"
        # serverless: true
```

An incoming message to the pipeline might have the following contents:

```
{"message": "127.0.0.1 198.126.12 [10/Oct/2000:13:55:36 -0700] 200"}
```

The pipeline will locate the value in the `message` key of each incoming event and try to match the pattern\. The keywords `IPORHOST`, `HTTPDATE`, and `NUMBER` are built into the plugin\.

When an incoming record matches the pattern, it generates an internal event like the following, with extracted identification keys from the original message\.

```
{ 
  "message":"127.0.0.1 198.126.12 [10/Oct/2000:13:55:36 -0700] 200",
  "response_status":200,
  "clientip":"198.126.12",
  "timestamp":"10/Oct/2000:13:55:36 -0700"
}
```

The `match` configuration for the Grok processor specifies which keys of a record to match which patterns against\.

In the following example, the match configuration checks incoming logs for a `message` key\. If the key exists, it matches the key value against the `SYSLOGBASE` pattern, and then against the `COMMONAPACHELOG` pattern\. It then checks the logs for a `timestamp` key\. If that key exists, it attempts to match the key value against the `TIMESTAMP_ISO8601` pattern\.

```
processor:
  - grok:
      match:
        message: ['%{SYSLOGBASE}', "%{COMMONAPACHELOG}"]
        timestamp: ["%{TIMESTAMP_ISO8601}"]
```

By default, the plugin continues until it finds a successful match\. For example, if there's a successful match against the value in the `message` key for a `SYSLOGBASE` pattern, the plugin doesn't attempt to match the other patterns\. If you want to match logs against *every* pattern, include the `break_on_match` option\.

## Including named and empty captures<a name="use-cases-pattern-matching-named"></a>

Include the `keep_empty_captures` option in your pipeline configuration to include null captures, or the `named_captures_only` option to include only named captures\. *Named* captures follow the pattern `%{SYNTAX:SEMANTIC}`, while *unnamed* captures follow the pattern `%{SYNTAX}`\.

For example, you can modify the Grok configuration above to remove `clientip` from the `%{IPORHOST}` pattern:

```
processor:
  - grok:
      match:
        message: ['%{IPORHOST} \[%{HTTPDATE:timestamp}\] %{NUMBER:response_status:int}']
```

The resulting grokked log will look like this:

```
{
  "message":"127.0.0.1 198.126.12 [10/Oct/2000:13:55:36 -0700] 200",
  "response_status":200,
  "timestamp":"10/Oct/2000:13:55:36 -0700"
}
```

Notice that the `clientip` key no longer exists, because the `%{IPORHOST}` pattern is now an unnamed capture\.

However, if you set `named_captures_only` to `false`:

```
processor:
  - grok:
      match:
        named_captures_only: false
        message: ['%{IPORHOST} \[%{HTTPDATE:timestamp}\] %{NUMBER:message:int}']
```

The resulting grokked log will look like this:

```
{
  "message":"127.0.0.1 198.126.12 [10/Oct/2000:13:55:36 -0700] 200",
  "MONTH":"Oct",
  "YEAR":"2000",
  "response_status":200,
  "HOUR":"13",
  "TIME":"13:55:36",
  "MINUTE":"55",
  "SECOND":"36",
  "IPORHOST":"198.126.12",
  "MONTHDAY":"10",
  "INT":"-0700",
  "timestamp":"10/Oct/2000:13:55:36 -0700"
}
```

Note that the `IPORHOST` capture now shows up as a new key, along with some internal unnamed captures like `MONTH` and `YEAR`\. The `HTTPDATE` keyword is using these patterns, which you can see in the default patterns file\.

## Overwriting keys<a name="use-cases-pattern-matching-overwriting"></a>

Include the `keys_to_overwrite` option to specify which existing keys of a record to overwrite if there's a capture with the same key value\.

For example, you can modify the grok configuration above to replace `%{NUMBER:response_status:int}` with `%{NUMBER:message:int}`, and add `message` to the list of keys to overwrite\.

```
processor:
  - grok:
      match:
        keys_to_overwrite: ["message"]
        message: ['%{IPORHOST:clientip} \[%{HTTPDATE:timestamp}\] %{NUMBER:message:int}']
```

In the resulting grokked log, the original message is overwritten with the number 200\.

```
{ 
  "message":200,
  "clientip":"198.126.12",
  "timestamp":"10/Oct/2000:13:55:36 -0700"
}
```

## Using custom patterns<a name="use-cases-pattern-matching-custom"></a>

Include the `pattern_definitions` option in your grok configuration to specify custom patterns\.

The following configuration creates custom regex patterns named `CUSTOM_PATTERN-1` and `CUSTOM_PATTERN-2`\. By default, the plugin continues until it finds a successful match\.

```
processor:
  - grok:
      pattern_definitions:
        CUSTOM_PATTERN_1: 'this-is-regex-1'
        CUSTOM_PATTERN_2: '%{CUSTOM_PATTERN_1} REGEX'
      match:
        message: ["%{CUSTOM_PATTERN_2:my_pattern_key}"]
```

If you specify `break_on_match` as `false`, the pipeline tries to match *all* patterns and extract keys from the incoming events:

```
processor:
  - grok:
      pattern_definitions:
        CUSTOM_PATTERN_1: 'this-is-regex-1'
        CUSTOM_PATTERN_2: 'this-is-regex-2'
        CUSTOM_PATTERN_3: 'this-is-regex-3'
        CUSTOM_PATTERN_4: 'this-is-regex-4'
      match:
        message: [ "%{PATTERN1}”, "%{PATTERN2}" ]
        log: [ "%{PATTERN3}", "%{PATTERN4}" ]
        break_on_match: false
```

You can define your own custom patterns to use for pattern matching in pipelines\. In the previous example, `my_pattern` will be extracted after matching the custom patterns\. 

## Storing captures with a parent key<a name="use-cases-pattern-matching-parent"></a>

Include the `target_key` option in your grok configuration to wrap all captures for a record in an additional outer key value\.

For example, you can modify the grok configuration above to add a target key named `grokked`\.

```
processor:
   - grok:
       target_key: "grok"
       match:
         message: ['%{IPORHOST} \[%{HTTPDATE:timestamp}\] %{NUMBER:response_status:int}']
```

The resulting grokked log will look like this:

```
{ 
  "message":"127.0.0.1 198.126.12 [10/Oct/2000:13:55:36 -0700] 200",
  "grokked": {
     "response_status":200,
     "clientip":"198.126.12",
     "timestamp":"10/Oct/2000:13:55:36 -0700"
  }
}
```