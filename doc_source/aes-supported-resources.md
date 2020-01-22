# Other Supported Resources<a name="aes-supported-resources"></a>

**bootstrap\.mlockall**  
The service enables `bootstrap.mlockall` in `elasticsearch.yml`, which locks JVM memory and prevents the operating system from swapping it to disk\. This applies to all supported instance types except for the following:  
+ `t2.micro.elasticsearch`
+ `t2.small.elasticsearch`
+ `t2.medium.elasticsearch`

**Scripting module**  
The service supports scripting for Elasticsearch 5\.*x* and later domains\. The service does not support scripting for 1\.5 or 2\.3\.  
Supported scripting options include the following:  
+ Painless
+ Lucene Expressions
+ Mustache
For Elasticsearch 5\.5 and later domains, Amazon ES supports stored scripts using the `_scripts` endpoint\. Elasticsearch 5\.3 and 5\.1 domains support inline scripts only\.

**TCP transport**  
The service supports HTTP on port 80 and HTTPS over port 443, but does not support TCP transport\.