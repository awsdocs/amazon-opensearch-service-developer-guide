# Other supported resources in Amazon OpenSearch Service<a name="supported-resources"></a>

This topic describes additional resources that Amazon OpenSearch Service supports\.

**bootstrap\.memory\_lock**  
OpenSearch Service enables `bootstrap.memory_lock` in `elasticsearch.yml`, which locks JVM memory and prevents the operating system from swapping it to disk\. This applies to all supported instance types except for the following:  
+ `t2.micro.search`
+ `t2.small.search`
+ `t2.medium.search`
+ `t3.small.search`
+ `t3.medium.search`

**Scripting module**  
OpenSearch Service supports scripting for Elasticsearch 5\.*x* and later domains\. It does not support scripting for 1\.5 or 2\.3\.  
Supported scripting options include the following:  
+ Painless
+ Lucene Expressions
+ Mustache
For Elasticsearch 5\.5 and later domains, and all OpenSearch domains, OpenSearch Service supports stored scripts using the `_scripts` endpoint\. Elasticsearch 5\.3 and 5\.1 domains support inline scripts only\.

**TCP transport**  
OpenSearch Service supports HTTP on port 80 and HTTPS over port 443, but does not support TCP transport\.