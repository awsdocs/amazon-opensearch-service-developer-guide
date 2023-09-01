# Getting started with Amazon OpenSearch Service<a name="gsg"></a>

This tutorial shows you how to use Amazon OpenSearch Service to create and configure a test domain\. An OpenSearch Service domain is synonymous with an OpenSearch cluster\. Domains are clusters with the settings, instance types, instance counts, and storage resources that you specify\.

This tutorial walks you through the basic steps to get an OpenSearch Service domain up and running quickly\. For more detailed information, see [Creating and managing Amazon OpenSearch Service domains](createupdatedomains.md) and the other topics within this guide\. For information on migrating to OpenSearch Service from a self\-managed OpenSearch cluster, see [Tutorial: Migrating to Amazon OpenSearch Service](migration.md)\.

You can complete the steps in this tutorial by using the OpenSearch Service console, the AWS CLI, or the AWS SDK\. For information about installing and setting up the AWS CLI, see the [AWS Command Line Interface User Guide](https://docs.aws.amazon.com/cli/latest/userguide/)\.

## Step 1: Create an Amazon OpenSearch Service domain<a name="gsgcreate-domain"></a>

**Important**  
This is a concise tutorial for configuring a *test * Amazon OpenSearch Service domain\. Do not use this process to create production domains\. For a comprehensive version of the same process, see [Creating and managing Amazon OpenSearch Service domains](createupdatedomains.md)\.

An OpenSearch Service domain is synonymous with an OpenSearch cluster\. Domains are clusters with the settings, instance types, instance counts, and storage resources that you specify\. You can create an OpenSearch Service domain by using the console, the AWS CLI, or the AWS SDKs\.

**To create an OpenSearch Service domain using the console**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com) and choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Amazon OpenSearch Service**\.

1. Choose **Create domain**\.

1. Provide a name for the domain\. The examples in this tutorial use the name *movies*\.

1. For the domain creation method, choose **Standard create**\.
**Note**  
To quickly configure a production domain with best practices, you can choose **Easy create**\. For the development and testing purposes of this tutorial, we'll use **Standard create**\.

1. For templates, choose **Dev/test**\.

1. For the deployment option, choose **Domain with standby**\.

1. For **Version**, choose the latest version\.

1. For now, ignore the **Data nodes**, **Warm and cold data storage**, **Dedicated master nodes**, **Snapshot configuration**, and **Custom endpoint** sections\.

1. For simplicity in this tutorial, use a public access domain\. Under **Network**, choose **Public access**\.

1. In the fine\-grained access control settings, keep the **Enable fine\-grained access control** check box selected\. Select **Create master user** and provide a username and password\.

1. For now, ignore the **SAML authentication** and **Amazon Cognito authentication** sections\.

1. For **Access policy**, choose **Only use fine\-grained access control**\. In this tutorial, fine\-grained access control handles authentication, not the domain access policy\.

1. Ignore the rest of the settings and choose **Create**\. New domains typically take 15–30 minutes to initialize, but can take longer depending on the configuration\. After your domain initializes, select it to open its configuration pane\. Note the domain endpoint under **General information** \(for example, `https://search-my-domain.us-east-1.es.amazonaws.com`\), which you'll use in the next step\.

**Next**: [Upload data to an OpenSearch Service domain for indexing](#gsgupload-data)

## Step 2: Upload data to Amazon OpenSearch Service for indexing<a name="gsgupload-data"></a>

**Important**  
This is a concise tutorial for uploading a small amount of test data to Amazon OpenSearch Service\. For more about uploading data in a production domain, see [Indexing data in Amazon OpenSearch Service](indexing.md)\.

You can upload data to an OpenSearch Service domain using the command line or most programming languages\.

The following example requests use [curl](https://curl.haxx.se/) \(a common HTTP client\) for brevity and convenience\. Clients like curl can't perform the request signing that's required if your access policies specify IAM users or roles\. To successfully complete this process, you must use fine\-grained access control with a primary username and password like you configured in [Step 1](#gsgcreate-domain)\.

You can install curl on Windows and use it from the command prompt, but we recommend a tool like [Cygwin](https://www.cygwin.com/) or the [Windows Subsystem for Linux](https://docs.microsoft.com/en-us/windows/wsl/install-win10)\. macOS and most Linux distributions come with curl preinstalled\.

### Option 1: Upload a single document<a name="gsgsingle-document"></a>

Run the following command to add a single document to the *movies* domain:

```
curl -XPUT -u 'master-user:master-user-password' 'domain-endpoint/movies/_doc/1' -d '{"director": "Burton, Tim", "genre": ["Comedy","Sci-Fi"], "year": 1996, "actor": ["Jack Nicholson","Pierce Brosnan","Sarah Jessica Parker"], "title": "Mars Attacks!"}' -H 'Content-Type: application/json'
```

In the command, provide the username and password that you created in [Step 1](#gsgcreate-domain)\.

For a detailed explanation of this command and how to make signed requests to OpenSearch Service, see [Indexing data in Amazon OpenSearch Service](indexing.md)\.

### Option 2: Upload multiple documents<a name="gsgmultiple-document"></a>

**To upload a JSON file that contains multiple documents to an OpenSearch Service domain**

1. Create a local file called `bulk_movies.json`\. Paste the following content into the file and add a trailing newline:

   ```
   { "index" : { "_index": "movies", "_id" : "2" } }
   {"director": "Frankenheimer, John", "genre": ["Drama", "Mystery", "Thriller", "Crime"], "year": 1962, "actor": ["Lansbury, Angela", "Sinatra, Frank", "Leigh, Janet", "Harvey, Laurence", "Silva, Henry", "Frees, Paul", "Gregory, James", "Bissell, Whit", "McGiver, John", "Parrish, Leslie", "Edwards, James", "Flowers, Bess", "Dhiegh, Khigh", "Payne, Julie", "Kleeb, Helen", "Gray, Joe", "Nalder, Reggie", "Stevens, Bert", "Masters, Michael", "Lowell, Tom"], "title": "The Manchurian Candidate"}
   { "index" : { "_index": "movies", "_id" : "3" } }
   {"director": "Baird, Stuart", "genre": ["Action", "Crime", "Thriller"], "year": 1998, "actor": ["Downey Jr., Robert", "Jones, Tommy Lee", "Snipes, Wesley", "Pantoliano, Joe", "Jacob, Ir\u00e8ne", "Nelligan, Kate", "Roebuck, Daniel", "Malahide, Patrick", "Richardson, LaTanya", "Wood, Tom", "Kosik, Thomas", "Stellate, Nick", "Minkoff, Robert", "Brown, Spitfire", "Foster, Reese", "Spielbauer, Bruce", "Mukherji, Kevin", "Cray, Ed", "Fordham, David", "Jett, Charlie"], "title": "U.S. Marshals"}
   { "index" : { "_index": "movies", "_id" : "4" } }
   {"director": "Ray, Nicholas", "genre": ["Drama", "Romance"], "year": 1955, "actor": ["Hopper, Dennis", "Wood, Natalie", "Dean, James", "Mineo, Sal", "Backus, Jim", "Platt, Edward", "Ray, Nicholas", "Hopper, William", "Allen, Corey", "Birch, Paul", "Hudson, Rochelle", "Doran, Ann", "Hicks, Chuck", "Leigh, Nelson", "Williams, Robert", "Wessel, Dick", "Bryar, Paul", "Sessions, Almira", "McMahon, David", "Peters Jr., House"], "title": "Rebel Without a Cause"}
   ```

1. Run the following command in the local directory where the file is stored to upload it to the *movies* domain:

   ```
   curl -XPOST -u 'master-user:master-user-password' 'domain-endpoint/_bulk' --data-binary @bulk_movies.json -H 'Content-Type: application/json'
   ```

For more information about the bulk file format, see [Indexing data in Amazon OpenSearch Service](indexing.md)\.

**Next**: [Search documents](#gsgsearch)

## Step 3: Search documents in Amazon OpenSearch Service<a name="gsgsearch"></a>

To search documents in an Amazon OpenSearch Service domain, use the OpenSearch search API\. Alternatively, you can use [OpenSearch Dashboards](dashboards.md) to search documents in the domain\.

### Search documents from the command line<a name="gsgsearch-cli"></a>

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

### Search documents using OpenSearch Dashboards<a name="gsgsearch-dashboards"></a>

OpenSearch Dashboards is a popular open source visualization tool designed to work with OpenSearch\. It provides a helpful user interface for you to search and monitor your indices\. 

**To search documents from an OpenSearch Service domain using Dashboards**

1. Navigate to the OpenSearch Dashboards URL for your domain\. You can find the URL on the domain's dashboard in the OpenSearch Service console\. The URL follows this format:

   ```
   domain-endpoint/_dashboards/
   ```

1. Log in using your primary username and password\.

1. To use Dashboards, you need to create at least one index pattern\. Dashboards uses these patterns to identify which indexes you want to analyze\. Open the left navigation panel, choose **Stack Management**, choose **Index Patterns**, and then choose **Create index pattern**\. For this tutorial, enter *movies*\.

1. Choose **Next step** and then choose **Create index pattern**\. After the pattern is created, you can view the various document fields such as `actor` and `director`\.

1. Go back to the **Index Patterns** page and make sure that `movies` is set as the default\. If it's not, select the pattern and choose the star icon to make it the default\.

1. To begin searching your data, open the left navigation panel again and choose **Discover**\.

1. In the search bar, enter *mars* if you uploaded a single document, or *rebel* if you uploaded multiple documents, and then press **Enter**\. You can try searching other terms, such as actor or director names\.

**Next**: [Delete a domain ](#gsgdeleting)

## Step 4: Delete an Amazon OpenSearch Service domain<a name="gsgdeleting"></a>

Because the *movies* domain from this tutorial is for test purposes, make sure to delete it when you're done experimenting to avoid incurring charges\.

**To delete an OpenSearch Service domain from the console**

1. Sign in to the **Amazon OpenSearch Service** console\.

1. Under **Domains**, select the **movies** domain\.

1. Choose **Delete** and confirm deletion\.

### Next steps<a name="gsgnextsteps-document"></a>

Now that you know how to create a domain and index data, you might want to try some of the following exercises:
+ Learn about more advanced options for creating a domain\. For more information, see [Creating and managing Amazon OpenSearch Service domains](createupdatedomains.md)\.
+ Discover how to manage the indices in your domain\. For more information, see [Managing indexes in Amazon OpenSearch Service](managing-indices.md)\.
+ Try out one of the tutorials for working with Amazon OpenSearch Service\. For more information, see [Amazon OpenSearch Service tutorials](tutorials.md)\.