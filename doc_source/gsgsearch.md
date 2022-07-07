# Step 3: Search documents in Amazon OpenSearch Service<a name="gsgsearch"></a>

To search documents in an Amazon OpenSearch Service domain, use the OpenSearch search API\. Alternatively, you can use [OpenSearch Dashboards](dashboards.md) to search documents in the domain\.

## Search documents from the command line<a name="gsgsearch-cli"></a>

Run the following command to search the *movies* domain for the word *mars*:

```
curl -XGET -u 'master-user:master-user-password' 'domain-endpoint/movies/_search?q=mars&pretty=true'
```

If you used the bulk data on the previous page, try searching for *rebel* instead\.

You should see a response similar to the following:

```
{
  "took" : 5,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1,
      "relation" : "eq"
    },
    "max_score" : 0.2876821,
    "hits" : [
      {
        "_index" : "movies",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 0.2876821,
        "_source" : {
          "director" : "Burton, Tim",
          "genre" : [
            "Comedy",
            "Sci-Fi"
          ],
          "year" : 1996,
          "actor" : [
            "Jack Nicholson",
            "Pierce Brosnan",
            "Sarah Jessica Parker"
          ],
          "title" : "Mars Attacks!"
        }
      }
    ]
  }
}
```

## Search documents using OpenSearch Dashboards<a name="gsgsearch-dashboards"></a>

OpenSearch Dashboards is a popular open source visualization tool designed to work with OpenSearch\. It provides a helpful user interface for you to search and monitor your indices\. 

**To search documents from an OpenSearch Service domain using Dashboards**

1. Navigate to the OpenSearch Dashboards URL for your domain\. You can find the URL on the domain's dashboard in the OpenSearch Service console\. The URL follows this format:

   ```
   domain-endpoint/_dashboards/
   ```

1. Log in using your primary user name and password\.

1. To use Dashboards, you need to create at least one index pattern\. Dashboards uses these patterns to identify which indexes you want to analyze\. Open the left navigation panel, choose **Stack Management**, choose **Index Patterns**, and then choose **Create index pattern**\. For this tutorial, enter *movies*\.

1. Choose **Next step** and then choose **Create index pattern**\. After the pattern is created, you can view the various document fields such as `actor` and `director`\.

1. Go back to the **Index Patterns** page and make sure that `movies` is set as the default\. If it's not, select the pattern and choose the star icon to make it the default\.

1. To begin searching your data, open the left navigation panel again and choose **Discover**\.

1. In the search bar, enter *mars* if you uploaded a single document, or *rebel* if you uploaded multiple documents, and then press **Enter**\. You can try searching other terms, such as actor or director names\.

**Next**: [Delete a domain ](gsgdeleting.md)