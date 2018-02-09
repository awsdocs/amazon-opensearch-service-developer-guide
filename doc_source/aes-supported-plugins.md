# Supported Plugins<a name="aes-supported-plugins"></a>

Amazon ES comes prepackaged with several plugins that are available from the Elasticsearch community\. Plugins are automatically deployed and managed for you\.


****  

| Elasticsearch Version | Plugins | 
| --- | --- | 
| 6\.0 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 5\.5 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  Elasticsearch 5\.5 supports Kibana 5, but it runs as a Node\.js application \(not as a plugin\)\.   | 
| 5\.3 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  Elasticsearch 5\.3 supports Kibana 5, but it runs as a Node\.js application \(not as a plugin\)\.   | 
| 5\.1 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  Elasticsearch 5\.1 supports Kibana 5, but it runs as a Node\.js application \(not as a plugin\)\.   | 
| 2\.3 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 1\.5 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 

## Output Plugins<a name="outputplugins"></a>

Amazon ES supports two Logstash output plugins to stream data into Amazon ES: the standard [Elasticsearch output plugin](https://www.elastic.co/guide/en/logstash/current/plugins-outputs-elasticsearch.html) and the [logstash\-output\-amazon\-es](https://github.com/awslabs/logstash-output-amazon_es) plugin, which signs and exports Logstash events to Amazon ES\. 

For more information about Logstash, see [[ERROR] BAD/MISSING LINK TEXT](es-kibana.md#es-managedomains-logstash)\.