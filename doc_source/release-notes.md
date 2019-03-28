# Document History for Amazon Elasticsearch Service<a name="release-notes"></a>

This topic describes important changes to Amazon Elasticsearch Service\.

**Relevant Dates to this History:**
+ **Current product version—**2015\-01\-01
+ **Latest product release—**March 25, 2019
+ **Latest documentation update—**March 25, 2019

## Release Notes<a name="release-table"></a>

The following table describes important changes to Amazon ES\. For notifications about updates, you can subscribe to the RSS feed\.

| Change | Description | Date | 
| --- |--- |--- |
| [R20190221 \(Service Software\)](#release-notes) | Includes the alerting feature, bug fixes, and support for upcoming features\. | March 25, 2019 | 
| [Alerting](#release-notes) | The alerting feature notifies you when data from one or more Elasticsearch indices meets certain conditions\. To learn more, see [Alerting](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/alerting.html)\. | March 25, 2019 | 
| [R20190113 \(Service Software\)](#release-notes) | Bug fixes for snapshots and multi\-AZ domain upgrades and support for upcoming features\. | March 6, 2019 | 
| [Three Availability Zone Support](#release-notes) | Amazon Elasticsearch Service now supports three Availability Zones in many regions\. This release also includes a streamlined console experience\. To learn more, see [Configuring a Multi\-AZ Domain](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-managedomains.html#es-managedomains-multiaz)\. This feature requires service software R20181023 or later\. | February 7, 2019 | 
| [R20181023 \(Service Software\)](#release-notes) | Improvements to snapshots and support for new features\. | February 7, 2019 | 
| [Elasticsearch 6\.4 Support](#release-notes) | Amazon Elasticsearch Service now supports Elasticsearch version 6\.4\. To learn more, see [Supported Elasticsearch Versions](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/what-is-amazon-elasticsearch-service.html#aes-choosing-version)\. | January 23, 2019 | 
| [200\-Node Clusters](#release-notes) | Amazon ES now lets you create clusters with up to 200 data nodes for a total of 3 PB of storage\. To learn more, see [Petabyte Scale](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/petabyte-scale.html)\. | January 22, 2019 | 
| [Service Software Updates](#release-notes) | Amazon ES now lets you manually update the service software for your domain in order to benefit from new features more quickly or update at a low traffic time\. To learn more, see [Service Software Updates](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-managedomains.html#es-service-software)\. | November 20, 2018 | 
| [New CloudWatch Metrics](#release-notes) | Amazon ES now offers node\-level metrics and new **Cluster health** and **Instance health** tabs in the Amazon ES console\. To learn more, see [Monitoring Cluster Metrics](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-managedomains.html#es-managedomains-cloudwatchmetrics)\. | November 20, 2018 | 
| [R20180914 \(Service Software\)](#release-notes) | Adds detailed cluster health monitoring, additional patches, and service enhancements\. | November 19, 2018 | 
| [China \(Beijing\) Support](#release-notes) | Amazon Elasticsearch Service is now available in the cn\-north\-1 region, where it supports the M4, C4, and R4 instance types\. | October 17, 2018 | 
| [Node\-to\-node Encryption](#release-notes) | Amazon Elasticsearch Service now supports node\-to\-node encryption, which keeps your data encrypted as Elasticsearch distributes it throughout your cluster\. To learn more, see [Node\-to\-node Encryption](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/ntn.html)\. | September 18, 2018 | 
| [R20180817 \(Service Software\)](#release-notes) | Support for node\-to\-node encryption and new CloudWatch metrics\. | August 17, 2018 | 
| [In\-place version upgrades](#release-notes) | Amazon Elasticsearch Service now supports in\-place version upgrades for Elasticsearch\. To learn more, see [Upgrading Elasticsearch](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-version-migration.html)\. | August 14, 2018 | 
| [Elasticsearch 6\.3 and 5\.6 Support](#release-notes) | Amazon Elasticsearch Service now supports Elasticsearch version 6\.3 and 5\.6\. To learn more, see [Supported Elasticsearch Versions](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/what-is-amazon-elasticsearch-service.html#aes-choosing-version)\. | August 14, 2018 | 
| [Error Logs](#release-notes) | Amazon ES now lets you publish Elasticsearch error logs to Amazon CloudWatch\. To learn more, see [Configuring Logs](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-createupdatedomains.html#es-createdomain-configure-slow-logs)\. | July 31, 2018 | 
| [R20180719 \(Service Software\)](#release-notes) | Support for new Elasticsearch versions and bug fixes\. | July 19, 2018 | 
| [R20180621 \(Service Software\)](#release-notes) | Improvements to automated snapshots\. | June 21, 2018 | 
| [China \(Ningxia\) Reserved Instances](#release-notes) | Amazon ES now offers Reserved Instances in the China \(Ningxia\) region\. | May 29, 2018 | 
| [R20180522 \(Service Software\)](#release-notes) | Security fixes\. | May 22, 2018 | 
| [Reserved Instances](#release-notes) | Amazon ES now offers Reserved Instances\. To learn more, see [Amazon ES Reserved Instances](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-ri.html)\. | May 7, 2018 | 

## Earlier Updates<a name="earlier-updates"></a>

The following table describes important changes Amazon ES before May 2018\.


| Change | Description | Date | 
| --- | --- | --- | 
| Amazon Cognito Authentication for Kibana | Amazon ES now offers login page protection for Kibana\. To learn more, see [Amazon Cognito Authentication for Kibana](es-cognito-auth.md)\. | April 2, 2018 | 
| Elasticsearch 6\.2 Support |  Amazon Elasticsearch Service now supports Elasticsearch version 6\.2\.  | March 14, 2018 | 
| Korean Analysis Plugin | Amazon ES now supports a memory\-optimized version of the [Seunjeon](https://bitbucket.org/eunjeon/seunjeon/raw/master/elasticsearch/) Korean analysis plugin\. | March 13, 2018 | 
| Instant Access Control Updates | Changes to the access control policies on Amazon ES domains now take effect instantly\. | March 7, 2018 | 
| Petabyte Scale | Amazon ES now supports I3 instance types and total domain storage of up to 1\.5 PB\. To learn more, see [Petabyte Scale for Amazon Elasticsearch Service](petabyte-scale.md)\. | 19 December 2017 | 
| Encryption of Data at Rest | Amazon ES now supports encryption of data at rest\. To learn more, see [Encryption of Data at Rest for Amazon Elasticsearch Service](encryption-at-rest.md)\. | December 7, 2017 | 
| Elasticsearch 6\.0 Support | Amazon ES now supports Elasticsearch version 6\.0\. For migration considerations and instructions, see [Upgrading Elasticsearch](es-version-migration.md)\. | December 6, 2017 | 
| VPC Support | Amazon ES now lets you launch domains within an Amazon Virtual Private Cloud\. VPC support provides an additional layer of security and simplifies communications between Amazon ES and other services within a VPC\. To learn more, see [VPC Support for Amazon Elasticsearch Service Domains](es-vpc.md)\. | October 17, 2017 | 
| Slow Logs Publishing | Amazon ES now supports the publishing of slow logs to CloudWatch Logs\. To learn more, see [Configuring Logs](es-createupdatedomains.md#es-createdomain-configure-slow-logs)\. | October 16, 2017 | 
| Elasticsearch 5\.5 Support | Amazon ES now supports Elasticsearch version 5\.5\. For new feature summaries, see the [Amazon announcement](https://aws.amazon.com/about-aws/whats-new/2017/09/elasticsearch-5_5-now-available-on-amazon-elasticsearch-service/) of availability\. You can now restore automated snapshots without contacting AWS Support and store scripts using the Elasticsearch `_scripts` API\. | September 7, 2017 | 
| Elasticsearch 5\.3 Support | Amazon ES added support for Elasticsearch version 5\.3\. | June 1, 2017 | 
| More Instances and EBS Capacity per Cluster | Amazon ES now supports up to 100 nodes and 150 TB EBS capacity per cluster\. | April 5, 2017 | 
| Canada \(Central\) and EU \(London\) Support | Amazon ES added support for the following regions: Canada \(Central\), ca\-central\-1, and EU \(London\), eu\-west\-2\. | March 20, 2017 | 
| More Instances and Larger EBS Volumes | Amazon ES added support for more instances and larger EBS volumes\. | February 21, 2017 | 
| Elasticsearch 5\.1 Support | Amazon ES added support for Elasticsearch version 5\.1\. | January 30, 2017 | 
| Support for the Phonetic Analysis Plugin | Amazon ES now provides built\-in integration with the Phonetic Analysis plugin, which allows you to run “sounds\-like” queries on your data\.  | December 22, 2016 | 
| US East \(Ohio\) Support | Amazon ES added support for the following region: US East \(Ohio\), us\-east\-2\. | October 17, 2016 | 
| New Performance Metric | Amazon ES added a performance metric, ClusterUsedSpace\. | July 29, 2016 | 
| Elasticsearch 2\.3 Support | Amazon ES added support for Elasticsearch version 2\.3\. | July 27, 2016 | 
| Asia Pacific \(Mumbai\) Support | Amazon ES added support for the following region: Asia Pacific \(Mumbai\), ap\-south\-1\. | June 27, 2016 | 
| More Instances per Cluster | Amazon ES increased the maximum number of instances \(instance count\) per cluster from 10 to 20\. | May 18, 2016 | 
| Asia Pacific \(Seoul\) Support | Amazon ES added support for the following region: Asia Pacific \(Seoul\), ap\-northeast\-2\. | January 28, 2016 | 
| Amazon ES | Initial release\. | October 1, 2015 | 