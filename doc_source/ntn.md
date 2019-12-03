# Node\-to\-node Encryption for Amazon Elasticsearch Service<a name="ntn"></a>

Node\-to\-node encryption provides an additional layer of security on top of the default features of Amazon ES\.

Each Amazon ES domain—regardless of whether the domain uses VPC access—resides within its own, dedicated VPC\. This architecture prevents potential attackers from intercepting traffic between Elasticsearch nodes and keeps the cluster secure\. By default, however, traffic within the VPC is unencrypted\. Node\-to\-node encryption enables TLS 1\.2 encryption for all communications within the VPC\.

If you send data to Amazon ES over HTTPS, node\-to\-node encryption helps ensure that your data remains encrypted as Elasticsearch distributes \(and redistributes\) it throughout the cluster\. If data arrives unencrypted over HTTP, Amazon ES encrypts it after it reaches the cluster\. You can require that all traffic to the domain arrive over HTTPS using the console, AWS CLI, or configuration API\.

## Enabling Node\-to\-node Encryption<a name="enabling-ntn"></a>

By default, domains do not use node\-to\-node encryption, and you can't configure existing domains to use the feature\. To enable the feature, you must [create another domain](es-createupdatedomains.md#es-createdomains) and [migrate your data](es-version-migration.md#snapshot-based-migration)\. Node\-to\-node encryption requires Elasticsearch 6\.0 or later\.

## Disabling Node\-to\-node Encryption<a name="disabling-ntn"></a>

After you configure a domain to use node\-to\-node encryption, you can't disable the setting\. Instead, you can take a [manual snapshot](es-managedomains-snapshots.md) of the encrypted domain, [create another domain](es-createupdatedomains.md#es-createdomains), migrate your data, and delete the old domain\.

## Other Considerations<a name="ntn-considerations"></a>
+ Kibana still works on domains that use node\-to\-node encryption\.