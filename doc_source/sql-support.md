# Querying your Amazon OpenSearch Service data with SQL<a name="sql-support"></a>

You can use SQL to query your Amazon OpenSearch Service, rather than using the JSON\-based [OpenSearch query DSL](https://opensearch.org/docs/opensearch/query-dsl/full-text/)\. Querying with SQL is useful if you're already familiar with the language or want to integrate your domain with an application that uses it\.

Use the following table to find the version of the SQL plugin that's supported by each OpenSearch and Elasticsearch version\.


**OpenSearch**  

| OpenSearch version | SQL plugin version | Notable features | 
| --- | --- | --- | 
| 1\.1\.0 | [1\.1\.0\.0](https://github.com/opensearch-project/sql/releases/tag/1.1.0.0) |  Support match function as filter in SQL and PPL  | 
| 1\.0\.0 | [1\.0\.0\.0](https://github.com/opensearch-project/sql/releases/tag/1.0.0.0) | Support querying a data stream | 


**Open Distro for Elasticsearch**  

| Elasticsearch version | SQL plugin version | Notable features | 
| --- | --- | --- | 
| 7\.10 | [1\.13\.0](https://github.com/opendistro-for-elasticsearch/sql/releases/tag/v1.13.0.0) | NULL FIRST and LAST for window functions, CAST\(\) function, SHOW and DESCRIBE commands | 
| 7\.9 | [1\.11\.0](https://github.com/opendistro-for-elasticsearch/sql/releases/tag/v1.11.0.0) | Add additional date/time functions, ORDER BY keyword | 
| 7\.8 | [1\.9\.0](https://github.com/opendistro-for-elasticsearch/sql/releases/tag/v1.9.0.0) |  | 
| 7\.7 | [1\.8\.0](https://github.com/opendistro-for-elasticsearch/sql/releases/tag/v1.8.0.0) |  | 
|  7\.3  | [1\.3\.0](https://github.com/opendistro-for-elasticsearch/sql/releases/tag/v1.3.0.0) | Multiple string and number operators | 
| 7\.1 | [1\.1\.0](https://github.com/opendistro-for-elasticsearch/sql/releases/tag/v1.1.0.0) |  | 

SQL support is available on domains running OpenSearch or Elasticsearch 6\.5 or higher\. Full documentation of the SQL plugin is available in the [OpenSearch documentation](https://opensearch.org/docs/search-plugins/sql/index/)\.

## Sample call<a name="sql-sample"></a>

To query your data with SQL, send HTTP requests to `_sql` using the following format:

```
POST domain-endpoint/_plugins/_sql
{
  "query": "SELECT * FROM my-index LIMIT 50"
}
```

**Note**  
If your domain is running Elasticsearch rather than OpenSearch, the format is `_opendistro/_sql`\.

## Notes and differences<a name="sql-diff"></a>

Calls to `_plugins/_sql` include index names in the request body, so they have the same [access policy considerations](ac.md#ac-advanced) as the bulk, mget, and msearch operations\. As always, follow the principle of [least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) when you grant permissions to API operations\.

For security considerations related to using SQL with fine\-grained access control, see [Fine\-grained access control in Amazon OpenSearch Service](fgac.md)\.

The OpenSearch SQL plugin includes many [tunable settings](https://opensearch.org/docs/search-plugins/sql/settings/)\. In OpenSearch Service, use the `_cluster/settings` path, not the plugin settings path \(`_plugins/_query/settings`\):

```
PUT _cluster/settings
{
  "transient" : {
    "plugins.sql.enabled" : true
  }
}
```

For legacy Elasticsearch domains, replace `plugins` with `opendistro`:

```
PUT _cluster/settings
{
  "transient" : {
    "opendistro.sql.enabled" : true
  }
}
```

## SQL Workbench<a name="workbench"></a>

The SQL Workbench is an OpenSearch Dashboards user interface that lets you run on\-demand SQL queries, translate SQL into its REST equivalent, and view and save results as text, JSON, JDBC, or CSV\. For more information, see [Query Workbench](https://opensearch.org/docs/search-plugins/sql/workbench/)\.

## SQL CLI<a name="cli"></a>

The SQL CLI is a standalone Python application that you can launch with the `opensearchsql` command\. For steps to install, configure, and use, see [SQL CLI](https://opensearch.org/docs/search-plugins/sql/cli/)\.

## JDBC driver<a name="jdbc-driver"></a>

The Java Database Connectivity \(JDBC\) driver lets you integrate OpenSearch Service domains with your favorite business intelligence \(BI\) applications\. To get started, see the [GitHub repository](https://github.com/opensearch-project/sql/tree/main/sql-jdbc)\. The following tables summarize version compatibility for the driver\. The Open Distro for Elasticsearch driver is available for download [here](https://opendistro.github.io/for-elasticsearch/downloads.html#connect)\. OpenSearch drivers are not yet available for download\.


**OpenSearch**  

| OpenSearch version | JDBC driver version | 
| --- | --- | 
| 1\.1 | 1\.0\.0\.0 | 
| 1\.0 | 1\.0\.0\.0 | 


**Open Distro for Elasticsearch**  

| Elasticsearch version | JDBC driver version | 
| --- | --- | 
| 7\.10 | [1\.13\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-1.13.0.0.jar) | 
| 7\.9 | [1\.11\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-1.11.0.0.jar) | 
| 7\.8 | [1\.9\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-1.9.0.0.jar) | 
| 7\.7 | [1\.8\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-1.8.0.0.jar) | 
| 7\.4 | [1\.4\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-1.4.0.0.jar) | 
| 7\.1 | [1\.0\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-1.0.0.0.jar) | 
| 6\.8 | [0\.9\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-0.9.0.0.jar) | 
| 6\.7 | [0\.9\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-0.9.0.0.jar) | 
| 6\.5 | [0\.9\.0](https://d3g5vo6xdbdb9a.cloudfront.net/downloads/elasticsearch-clients/opendistro-sql-jdbc/opendistro-sql-jdbc-0.9.0.0.jar) | 

## ODBC driver<a name="odbc"></a>

The Open Database Connectivity \(ODBC\) driver is a read\-only ODBC driver for Windows and macOS that lets you connect business intelligence and data visualization applications like [Tableau](https://github.com/opensearch-project/sql/blob/main/sql-odbc/docs/user/tableau_support.md), [Microsoft Excel](https://github.com/opensearch-project/sql/blob/main/sql-odbc/docs/user/microsoft_excel_support.md), and [Power BI](https://github.com/opensearch-project/sql/blob/main/sql-odbc/docs/user/power_bi_support.md) to the SQL plugin\. The Open Distro for Elasticsearch driver is available for download [here](https://opendistro.github.io/for-elasticsearch/downloads.html#connect)\. OpenSearch drivers are not yet available for download\. For information about installing the driver, see the [SQL repository on GitHub](https://github.com/opensearch-project/sql/tree/main/sql-odbc)\.