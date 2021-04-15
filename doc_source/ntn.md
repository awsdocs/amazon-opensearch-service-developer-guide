# Node\-to\-node Encryption for Amazon Elasticsearch Service<a name="ntn"></a>

Node\-to\-node encryption provides an additional layer of security on top of the default features of Amazon ES\.

Each Amazon ES domain—regardless of whether the domain uses VPC access—resides within its own, dedicated VPC\. This architecture prevents potential attackers from intercepting traffic between Elasticsearch nodes and keeps the cluster secure\. By default, however, traffic within the VPC is unencrypted\. Node\-to\-node encryption enables TLS 1\.2 encryption for all communications within the VPC\.

If you send data to Amazon ES over HTTPS, node\-to\-node encryption helps ensure that your data remains encrypted as Elasticsearch distributes \(and redistributes\) it throughout the cluster\. If data arrives unencrypted over HTTP, Amazon ES encrypts it after it reaches the cluster\. You can require that all traffic to the domain arrive over HTTPS using the console, AWS CLI, or configuration API\.

## Enabling Node\-to\-node Encryption<a name="enabling-ntn"></a>

Node\-to\-node encryption on new domains requires Elasticsearch 6\.0 or later\. Enabling the feature on existing domains requires Elasticsearch 6\.7 or later\. Choose the existing domain in the AWS console, **Actions**, and **Modify encryption**\.

Alternatively, you can use the AWS CLI or configuration API\.

## Disabling Node\-to\-node Encryption<a name="disabling-ntn"></a>

After you configure a domain to use node\-to\-node encryption, you can't disable the setting\. Instead, you can take a [manual snapshot](es-managedomains-snapshots.md) of the encrypted domain, [create another domain](es-createupdatedomains.md#es-createdomains), migrate your data, and delete the old domain\.