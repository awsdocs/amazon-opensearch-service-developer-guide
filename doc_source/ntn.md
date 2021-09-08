# Node\-to\-node encryption for Amazon OpenSearch Service<a name="ntn"></a>

Node\-to\-node encryption provides an additional layer of security on top of the default features of Amazon OpenSearch Service\.

Each OpenSearch Service domain—regardless of whether the domain uses VPC access—resides within its own, dedicated VPC\. This architecture prevents potential attackers from intercepting traffic between OpenSearch nodes and keeps the cluster secure\. By default, however, traffic within the VPC is unencrypted\. Node\-to\-node encryption enables TLS 1\.2 encryption for all communications within the VPC\.

If you send data to OpenSearch Service over HTTPS, node\-to\-node encryption helps ensure that your data remains encrypted as OpenSearch distributes \(and redistributes\) it throughout the cluster\. If data arrives unencrypted over HTTP, OpenSearch Service encrypts it after it reaches the cluster\. You can require that all traffic to the domain arrive over HTTPS using the console, AWS CLI, or configuration API\.

## Enabling node\-to\-node encryption<a name="enabling-ntn"></a>

Node\-to\-node encryption on new domains requires any version of OpenSearch, or Elasticsearch 6\.0 or later\. Enabling node\-to\-node encryption on existing domains requires any version of OpenSearch, or Elasticsearch 6\.7 or later\. Choose the existing domain in the AWS console, **Actions**, and **Modfy encryptions**\.

Alternatively, you can use the AWS CLI or configuration API\. For more information, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/) and [Configuration API reference for Amazon OpenSearch Service](configuration-api.md)\.

## Disabling node\-to\-node encryption<a name="disabling-ntn"></a>

After you configure a domain to use node\-to\-node encryption, you can't disable the setting\. Instead, you can take a [manual snapshot](managedomains-snapshots.md) of the encrypted domain, [create another domain](createupdatedomains.md#createdomains), migrate your data, and delete the old domain\.