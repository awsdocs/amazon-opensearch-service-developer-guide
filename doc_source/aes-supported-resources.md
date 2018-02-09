# Other Supported Resources<a name="aes-supported-resources"></a>

**bootstrap\.mlockall**  
The service enables `bootstrap.mlockall` in `elasticsearch.yml`, which locks JVM memory and prevents the operating system from swapping it to disk\. This applies to all supported instance types except for the following:  

+ `t2.micro.elasticsearch`

+ `t2.small.elasticsearch`

+ `t2.medium.elasticsearch`

**Scripting module**  
The service supports scripting for Elasticsearch 5\.*x* domains\. The service does not support scripting for 1\.5 or 2\.3\.  
Supported scripting options include the following:  

+ Painless

+ Lucene Expressions

+ Mustache
For Elasticsearch 5\.5 and newer domains, Amazon ES supports stored scripts using the `_scripts` endpoint\. Elasticsearch 5\.3 and 5\.1 domains only support inline scripts\. To learn more, see [How to use scripts](https://www.elastic.co/guide/en/elasticsearch/reference/5.5/modules-scripting-using.html#modules-scripting-stored-scripts) in the Elasticsearch documentation\.

**TCP transport**  
The service supports HTTP on port 80, but does not support TCP transport\.