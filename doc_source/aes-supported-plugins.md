# Supported Plugins<a name="aes-supported-plugins"></a>

Amazon ES domains come prepackaged with plugins from the Elasticsearch community\. The service automatically deploys and manages plugins for you\.

**Note**  
Kibana is a plugin in earlier versions of Amazon ES and a Node\.js application in later versions\. All Amazon ES domains include a preinstalled version of Kibana\.


****  

| Elasticsearch Version | Plugins | 
| --- | --- | 
| 7\.1 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 6\.8 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 6\.7 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 6\.5 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 6\.4 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 6\.3 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 6\.2 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 6\.0 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 5\.6 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 5\.5 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 5\.3 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 5\.1 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 2\.3 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 
| 1\.5 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)  | 

## Output Plugins<a name="outputplugins"></a>

Amazon ES supports two Logstash output plugins to stream data into Amazon ES: the standard Elasticsearch output plugin and [logstash\-output\-amazon\_es](https://github.com/awslabs/logstash-output-amazon_es), which signs and exports Logstash events to Amazon ES\.

For more information about Logstash, see [Loading Bulk Data with the Logstash Plugin](es-kibana.md#es-managedomains-logstash)\.