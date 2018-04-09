# Supported Plugins<a name="aes-supported-plugins"></a>

Amazon ES domains come prepackaged with plugins that are available from the Elasticsearch community\. The service automatically deploys and manages plugins for you\.

**Note**  
Kibana is a plugin in older versions of Amazon ES and a Node\.js application in newer versions\. All Amazon ES domains include a preinstalled version of Kibana\.


****  

| Elasticsearch Version | Plugins | 
| --- | --- | 
| 6\.2 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 6\.0 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 5\.5 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 5\.3 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 5\.1 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 2\.3 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 1\.5 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 

## Output Plugins<a name="outputplugins"></a>

Amazon ES supports two Logstash output plugins to stream data into Amazon ES: the standard [Elasticsearch output plugin](https://www.elastic.co/guide/en/logstash/current/plugins-outputs-elasticsearch.html) and the [logstash\-output\-amazon\-es](https://github.com/awslabs/logstash-output-amazon_es) plugin, which signs and exports Logstash events to Amazon ES\. 

For more information about Logstash, see [Loading Bulk Data with the Logstash Plugin](es-kibana.md#es-managedomains-logstash)\.