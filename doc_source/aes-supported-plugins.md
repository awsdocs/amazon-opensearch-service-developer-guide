# Plugins by Elasticsearch version<a name="aes-supported-plugins"></a>

Amazon Elasticsearch Service \(Amazon ES\) domains come prepackaged with plugins from the Elasticsearch community\. The service automatically deploys and manages plugins for you, but it deploys different plugins depending on the version of Elasticsearch you choose for your domain\.

The following table lists plugins by version\. It only includes plugins that you might interact with—it’s not comprehensive\. Amazon ES uses additional plugins to enable core service functionality, such as the S3 Repository plugin for snapshots and the [Open Distro for Elasticsearch Performance Analyzer](https://opendistro.github.io/for-elasticsearch-docs/docs/pa/) plugin for optimization and monitoring\. For a complete list of all plugins running on your domain, make the following request:

```
GET _cat/plugins?v
```


****  
[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-supported-plugins.html)