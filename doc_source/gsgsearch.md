# Step 3: Search documents in OpenSearch Service<a name="gsgsearch"></a>

To search documents in an Amazon OpenSearch Service domain, use the OpenSearch search API\. Alternatively, you can use [OpenSearch Dashboards](dashboards.md) to search documents in the domain\.

## Search documents from the command line<a name="gsgsearch-cli"></a>

Run the following command to search the *movies* domain for the word *mars*:

```
curl -XGET -u 'master-user:master-user-password' 'domain-endpoint/movies/_search?q=mars&pretty=true'
```

If you used the bulk data on the previous page, try searching for *rebel* instead\.

## Search documents using OpenSearch Dashboards<a name="gsgsearch-dashboards"></a>

OpenSearch Dashboards is a popular open source visualization tool designed to work with OpenSearch\. It provides a helpful user interface for you to search and monitor your indices\. 

**To search documents from an OpenSearch Service domain using Dashboards**

1. Point your browser to the Dashboards plugin for your OpenSearch Service domain\. You can find the Dashboards endpoint on your domain dashboard on the OpenSearch Service console\. The URL follows this format:

   ```
   domain-endpoint/_dashboards/
   ```

1. Log in using your primary user name and password\.

1. To use Dashboards, you need to configure at least one index pattern\. Dashboards uses these patterns to identify which indices you want to analyze\. Open the Dashboards main menu, choose **Stack Management**, choose **Index Patterns**, and then choose **Create index pattern**\. For this tutorial, enter *movies*\.

1. Choose **Next step** and then choose **Create index pattern**\. After the pattern is created, you can view the various document fields such as `actor` and `director`\. Go back to the **Index Patterns** tab and make sure `movies` is set as the default\. 

1. To begin searching your data, open the main menu again and choose **Discover**\.

1. In the search bar, enter *mars* if you uploaded a single document, or *rebel* if you uploaded multiple documents, and then press **Enter**\. Note how the similarity score \(`_score`\) increases if you search for a more specific phrase such as *mars attacks*\.

**Next**: [Delete a domain ](gsgdeleting.md)