# SQL Support for Amazon Elasticsearch Service<a name="sql-support"></a>

SQL support for Amazon Elasticsearch Service lets you query your data using SQL rather than the JSON\-based [Elasticsearch query DSL](https://opendistro.github.io/for-elasticsearch-docs/docs/elasticsearch/full-text/)\. This feature is useful if you're already familiar with SQL or want to integrate your domain with an application that uses SQL\.

SQL support is available on domains running Elasticsearch 6\.5 or higher\. Full documentation is available in the [Open Distro for Elasticsearch documentation](https://opendistro.github.io/for-elasticsearch-docs/docs/sql/)\.

## Sample Call<a name="sql-sample"></a>

To query your data using SQL, send HTTP requests to `_opendistro/_sql` using the following format:

```
POST elasticsearch_domain/_opendistro/_sql
{
  "query": "SELECT * FROM my-index LIMIT 50"
}
```

## Notes and Differences<a name="sql-diff"></a>

Calls to `_opendistro/_sql` include index names in the request body and thus have the same [access policy considerations](es-ac.md#es-ac-advanced) as the bulk, mget, and msearch APIs\. As always, follow the principle of [least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) when granting permissions to APIs\.

For a security consideration regarding using SQL with fine\-grained access control, see [Fine\-Grained Access Control in Amazon Elasticsearch Service](fgac.md#fgac-limitations)\.

## JDBC Driver<a name="jdbc-driver"></a>

The Java Database Connectivity \(JDBC\) driver lets you integrate Amazon ES domains with your favorite business intelligence \(BI\) applications\. To get started, see [the GitHub repository\.](https://github.com/opendistro-for-elasticsearch/sql-jdbc) The following table summarizes version compatibility for the driver\.


| Elasticsearch Version | JDBC Driver Version | 
| --- | --- | 
| 7\.1 | [1\.0\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-1.0.0.0.jar) | 
| 6\.8 | [0\.9\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-0.9.0.0.jar) | 
| 6\.7 | [0\.9\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-0.9.0.0.jar) | 
| 6\.5 | [0\.9\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-0.9.0.0.jar) | 