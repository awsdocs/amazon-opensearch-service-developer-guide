# Step 3: Search Documents in an Amazon ES Domain<a name="es-gsg-search"></a>

To search documents in an Amazon Elasticsearch Service domain, use the Elasticsearch search API\. Or you can use [Kibana](es-kibana.md) to search documents in the domain\.

**To search documents from the command line**
+ Run the following command to search the *movies* domain for the word *mars*:

  ```
  curl -XGET -u master-user:master-user-password 'domain-endpoint/movies/_search?q=mars&pretty=true'
  ```

If you used the bulk data on the previous page, try searching for *rebel* instead\.

**To search documents from an Amazon ES domain by using Kibana**

1. Point your browser to the Kibana plugin for your Amazon ES domain\. You can find the Kibana endpoint on your domain dashboard on the Amazon ES console\. The URL follows this format:

   ```
   domain-endpoint/_plugin/kibana/
   ```

1. Log in using your master user name and password\.

1. To use Kibana, you must configure at least one index pattern\. Kibana uses these patterns to identify which indices you want to analyze\. For this tutorial, enter *movies*, and then choose **Create**\.

1. The **Index Patterns** page shows your various document fields, such as `actor` and `director`\. For now, choose **Discover** to search your data\.

1. In the search bar, enter *mars*, and then press **Enter**\. Note how the similarity score \(`_score`\) increases when you search for the phrase *mars attacks*\.