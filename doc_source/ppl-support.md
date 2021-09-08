# Querying Amazon OpenSearch Service data using Piped Processing Language<a name="ppl-support"></a>

Piped Processing Language \(PPL\) is a query language that lets you use pipe \(`|`\) syntax to query data stored in Amazon OpenSearch Service\.

The PPL syntax consists of commands delimited by a pipe character \(`|`\) where data flows from left to right through each pipeline\. For example, the PPL syntax to find the number of hosts with HTTP 403 or 503 errors, aggregate them per host, and sort them in the order of impact is as follows:

```
source = dashboards_sample_data_logs | 
where response='403' or response='503' | 
stats count(request) as request_count by host, response | 
sort -request_count
```

PPL requires either OpenSearch or Elasticsearch 7\.9 or later\. Detailed steps and command descriptions are available in the [OpenSearch documentation](https://opensearch.org/docs/search-plugins/ppl/)\.

## <a name="ppl-support-gs"></a>

To get started, choose **Query Workbench** in OpenSearch Dashboards and select **PPL**\. Use the `bulk` operation to index some sample data: 

```
PUT accounts/_bulk?refresh
{"index":{"_id":"1"}}
{"account_number":1,"balance":39225,"firstname":"Amber","lastname":"Duke","age":32,"gender":"M","address":"880 Holmes Lane","employer":"Pyrami","email":"amberduke@pyrami.com","city":"Brogan","state":"IL"}
{"index":{"_id":"6"}}
{"account_number":6,"balance":5686,"firstname":"Hattie","lastname":"Bond","age":36,"gender":"M","address":"671 Bristol Street","employer":"Netagy","email":"hattiebond@netagy.com","city":"Dante","state":"TN"}
{"index":{"_id":"13"}}
{"account_number":13,"balance":32838,"firstname":"Nanette","lastname":"Bates","age":28,"gender":"F","address":"789 Mady Street","employer":"Quility","city":"Nogal","state":"VA"}
{"index":{"_id":"18"}}
{"account_number":18,"balance":4180,"firstname":"Dale","lastname":"Adams","age":33,"gender":"M","address":"467 Hutchinson Court","email":"daleadams@boink.com","city":"Orick","state":"MD"}
```

The following example returns `firstname` and `lastname` fields for documents in an accounts index with `age` greater than 18:

```
search source=accounts | 
where age > 18 | 
fields firstname, lastname
```


**Sample Response**  

| id | firstname | lastname | 
| --- | --- | --- | 
| 0 | Amber | Duke | 
| 1 | Hattie | Bond | 
| 2 | Nanette | Bates | 
| 3 | Dale | Adams | 

You can use a complete set of read\-only commands like `search`, `where`, `fields`, `rename`, `dedup`, `stats`, `sort`, `eval`, `head`, `top`, and `rare`\. For descriptions and examples of each command, see [Commands](https://opensearch.org/docs/search-plugins/ppl/commands/)\.

The PPL plugin supports all SQL functions, including mathematical, trigonometric, date\-time, string, aggregate, and advanced operators and expressions\. To learn more, see [Functions](https://opensearch.org/docs/search-plugins/sql/functions/)\.