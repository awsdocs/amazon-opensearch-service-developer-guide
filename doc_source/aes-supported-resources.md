# Other supported resources in Amazon Elasticsearch Service<a name="aes-supported-resources"></a>

This topic describes additional resources that Amazon Elasticsearch Service \(Amazon ES\) supports\.

**bootstrap\.memory\_lock**  
Amazon ES enables `bootstrap.memory_lock` in `elasticsearch.yml`, which locks JVM memory and prevents the operating system from swapping it to disk\. This applies to all supported instance types except for the following:  
+ `t2.micro.elasticsearch`
+ `t2.small.elasticsearch`
+ `t2.medium.elasticsearch`
+ `t3.small.elasticsearch`
+ `t3.medium.elasticsearch`

**Scripting module**  
Amazon ES supports scripting for Elasticsearch 5\.*x* and later domains\. It does not support scripting for 1\.5 or 2\.3\.  
Supported scripting options include the following:  
+ Painless
+ Lucene Expressions
+ Mustache
For Elasticsearch 5\.5 and later domains, Amazon ES supports stored scripts using the `_scripts` endpoint\. Elasticsearch 5\.3 and 5\.1 domains support inline scripts only\.

**TCP transport**  
Amazon ES supports HTTP on port 80 and HTTPS over port 443, but does not support TCP transport\.