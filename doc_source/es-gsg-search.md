# Step 3: Search documents in an Amazon ES domain<a name="es-gsg-search"></a>

To search documents in an Amazon Elasticsearch Service \(Amazon ES\) domain, use the Elasticsearch search API\. Alternatively, you can use [Kibana](es-kibana.md) to search documents in the domain\.

## Search documents from the command line<a name="es-gsg-search-cli"></a>

Run the following command to search the *movies* domain for the word *mars*:

```
curl -XGET -u 'master-user:master-user-password' 'domain-endpoint/movies/_search?q=mars&pretty=true'
```

If you used the bulk data on the previous page, try searching for *rebel* instead\.

## Search documents using Kibana<a name="es-gsg-search-kibana"></a>

Kibana is a popular open source visualization tool designed to work with Elasticsearch\. It provides a helpful user interface for you to search and monitor your indices\. 

**To search documents from an Amazon ES domain using Kibana**

1. Point your browser to the Kibana plugin for your Amazon ES domain\. You can find the Kibana endpoint on your domain dashboard on the Amazon ES console\. The URL follows this format:

   ```
   domain-endpoint/_plugin/kibana/
   ```

1. Log in using your primary user name and password\.

1. To use Kibana, you need to configure at least one index pattern\. Kibana uses these patterns to identify which indices you want to analyze\. Open the Kibana main menu, choose **Stack Management**, choose **Index Patterns**, and then choose **Create index pattern**\. For this tutorial, enter *movies*\.

1. Choose **Next step** and then choose **Create index pattern**\. After the index is created, you can view the various document fields such as `actor` and `director`\. 

1. To begin searching your data, open the main menu again and choose **Discover**\.

1. In the search bar, enter *mars* if you uploaded a single document, or *rebel* if you uploaded multiple documents, and then press **Enter**\. Note how the similarity score \(`_score`\) increases if you search for a more specific phrase such as *mars attacks*\.

**Next**: [Delete an Amazon ES domain ](es-gsg-deleting.md)