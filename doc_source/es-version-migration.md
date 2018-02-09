# Migrating to a Different Elasticsearch Version<a name="es-version-migration"></a>

The following table shows how to migrate your data to a newer Elasticsearch version\. Most of the steps require you to create and restore manual index snapshots\. To learn more about this process, see [[ERROR] BAD/MISSING LINK TEXT](es-managedomains-snapshots.md)\.

Migrating to a newer Elasticsearch version also requires creating a new domain\. To learn more, see [[ERROR] BAD/MISSING LINK TEXT](es-createupdatedomains.md)\.


****  

| From Version | To Version | Migration Process | 
| --- | --- | --- | 
| 5\.x | 6\.0 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-version-migration.html)  | 
| 2\.3 | 6\.0 | Elasticsearch 2\.3 snapshots are not compatible with 6\.0\. To migrate your data directly from 2\.3 to 6\.0, you must manually recreate your indices in the new domain\.Alternately, you can follow the 2\.3 to 5\.*x* steps in this table, perform [https://www.elastic.co/guide/en/elasticsearch/reference/5.5/docs-reindex.html](https://www.elastic.co/guide/en/elasticsearch/reference/5.5/docs-reindex.html) operations in the new 5\.*x* domain to convert your 2\.3 indices to 5\.*x* indices, and then follow the 5\.*x* to 6\.0 steps\. | 
| 5\.1 or 5\.3 | 5\.5 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-version-migration.html)  | 
| 2\.3 | 5\.x |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-version-migration.html)  | 
| 1\.5 | 5\.x | Elasticsearch 1\.5 snapshots are not compatible with 5\.x\. To migrate your data from 1\.5 to 5\.x, you must manually recreate your indices in the new domain\.  1\.5 snapshots *are* compatible with 2\.3, but Amazon ES 2\.3 domains do not support the [https://www.elastic.co/guide/en/elasticsearch/reference/2.3/docs-reindex.html](https://www.elastic.co/guide/en/elasticsearch/reference/2.3/docs-reindex.html) operation\. Because you cannot reindex them, indices that originated in a 1\.5 domain still fail to restore from 2\.3 snapshots to 5\.*x* domains\.  | 
| 1\.5 | 2\.3 |  [\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-version-migration.html)  | 