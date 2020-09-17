# SQL Support for Amazon Elasticsearch Service<a name="sql-support"></a>

SQL support for Amazon Elasticsearch Service \(Amazon ES\) lets you query your data using SQL rather than the JSON\-based [Elasticsearch query DSL](https://opendistro.github.io/for-elasticsearch-docs/docs/elasticsearch/full-text/)\. This feature is useful if you're already familiar with SQL or want to integrate your domain with an application that uses SQL\.

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

Calls to `_opendistro/_sql` include index names in the request body, so they have the same [access policy considerations](es-ac.md#es-ac-advanced) as the bulk, mget, and msearch operations\. As always, follow the principle of [least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) when you grant permissions to API operations\.

For security considerations related to using SQL with fine\-grained access control, see [Fine\-Grained Access Control in Amazon Elasticsearch Service](fgac.md#fgac-limitations)\.

The Open Distro for Elasticsearch SQL plugin includes many [tuneable settings](https://opendistro.github.io/for-elasticsearch-docs/docs/sql/settings/), but on Amazon ES, use the `_opendistro/_sql/settings` path rather than the standard `_cluster/settings` path:

```
PUT _opendistro/_sql/settings
{
  "persistent": {
    "opendistro.sql.cursor.enabled": true
  }
}
```

## Workbench<a name="workbench"></a>

The SQL Workbench is a Kibana user interface that lets you run on\-demand SQL queries, translate SQL into its REST equivalent, and view and save results as text, JSON, JDBC, or CSV\. For more information, see [Workbench](https://opendistro.github.io/for-elasticsearch-docs/docs/sql/workbench/)\.

## SQL CLI<a name="cli"></a>

The SQL CLI is a standalone Python application that you can launch with the `odfesql` command\. For steps to install, configure, and use, see [SQL CLI](https://opendistro.github.io/for-elasticsearch-docs/docs/sql/cli/)\.

## JDBC Driver<a name="jdbc-driver"></a>

The Java Database Connectivity \(JDBC\) driver lets you integrate Amazon ES domains with your favorite business intelligence \(BI\) applications\. To get started, see the [GitHub repository](https://github.com/opendistro-for-elasticsearch/sql-jdbc)\. The following table summarizes version compatibility for the driver\.


| Elasticsearch Version | JDBC Driver Version | 
| --- | --- | 
| 7\.7 | [1\.8\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-1.8.0.0.jar) | 
| 7\.4 | [1\.4\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-1.4.0.0.jar) | 
| 7\.1 | [1\.0\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-1.0.0.0.jar) | 
| 6\.8 | [0\.9\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-0.9.0.0.jar) | 
| 6\.7 | [0\.9\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-0.9.0.0.jar) | 
| 6\.5 | [0\.9\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-0.9.0.0.jar) | 

## ODBC Driver<a name="odbc"></a>

The Open Database Connectivity \(ODBC\) driver is a read\-only ODBC driver for Windows and macOS that lets you connect business intelligence and data visualization applications like [Tableau](https://github.com/opendistro-for-elasticsearch/sql/blob/develop/sql-odbc/docs/user/tableau_support.md) and [Microsoft Excel](https://github.com/opendistro-for-elasticsearch/sql/blob/develop/sql-odbc/docs/user/microsoft_excel_support.md) to the SQL plugin\. For information on downloading and using the JAR file, see the [SQL repository on GitHub](https://github.com/opendistro-for-elasticsearch/sql/tree/master/sql-odbc)\.