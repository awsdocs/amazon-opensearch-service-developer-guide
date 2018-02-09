# Encryption of Data at Rest for Amazon Elasticsearch Service<a name="encryption-at-rest"></a>

Amazon ES domains offer encryption of data at rest, a security feature that helps prevent unauthorized access to your data\. The feature uses AWS Key Management Service \(KMS\) to store and manage your encryption keys\. If enabled, it encrypts the following aspects of a domain:

+ Indices

+ Automated snapshots

+ Elasticsearch logs

+ Swap files

+ All other data in the application directory

The following are *not* encrypted when you enable encryption of data at rest, but you can take additional steps to protect them:

+ Manual snapshots: Currently, you can't use KMS master keys to encrypt manual snapshots\. You can, however, use [server\-side encryption with S3\-managed keys](http://docs.aws.amazon.com/AmazonS3/latest/dev/UsingServerSideEncryption.html) to encrypt the bucket that you use as a snapshot repository\. For instructions, see [[ERROR] BAD/MISSING LINK TEXT](es-managedomains-snapshots.md#es-managedomains-snapshot-registerdirectory)\.

+ Slow logs: If you publish slow logs and want to encrypt them, you can encrypt their CloudWatch Logs log group using the same KMS master key as the Amazon ES domain\. To learn more, see [Encrypt Log Data in CloudWatch Logs Using AWS KMS](http://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/encrypt-log-data-kms.html) in the Amazon CloudWatch Logs User Guide\.

To learn how to create KMS master keys, see [Creating Keys](http://docs.aws.amazon.com/kms/latest/developerguide/create-keys.html) in the *AWS Key Management Service Developer Guide*\.

## Enabling Encryption of Data at Rest<a name="enabling-ear"></a>

By default, domains do not encrypt data at rest, and you can't configure existing domains to use the feature\. To enable the feature, you must create another domain and migrate your data\. Encryption of data at rest requires Elasticsearch 5\.1 or newer\.

## Disabling Encryption of Data at Rest<a name="disabling-ear"></a>

After you configure a domain to encrypt data at rest, you can't disable the setting\. Instead, you can take a manual snapshot of the existing domain, create another domain, migrate your data, and delete the old domain\.

## Monitoring Domains That Encrypt Data at Rest<a name="monitoring-ear"></a>

Domains that encrypt data at rest have two additional metrics: `KMSKeyError` and `KMSKeyInaccessible`\. For full descriptions of these metrics, see [[ERROR] BAD/MISSING LINK TEXT](es-managedomains.md#es-managedomains-cloudwatchmetrics-cluster-metrics)\. You can view them using the Amazon ES console or Amazon CloudWatch\.

**Tip**  
Each metric represents a significant problem for a domain, so we recommend that you create CloudWatch alarms for both\. For more information, see [[ERROR] BAD/MISSING LINK TEXT](cloudwatch-alarms.md)\.

## Other Considerations<a name="ear-considerations"></a>

+ If you delete the key that you used to encrypt a domain, the domain becomes inaccessible\. The Amazon ES team can't help you recover your data\. AWS Key Management Service deletes master keys only after a waiting period of at least seven days, so the Amazon ES team might contact you if they detect that your domain is at risk\.

+ Automatic key rotation preserves the properties of your KMS master keys, so the rotation has no effect on your ability to access your Elasticsearch data\. Encrypted Amazon ES domains do not support manual key rotation, which involves creating a new master key and updating any references to the old key\. To learn more, see [Rotating Customer Master Keys](http://docs.aws.amazon.com/kms/latest/developerguide/rotate-keys.html) in the *AWS Key Management Service Developer Guide*\.

+ Certain instance types do not support encryption of data at rest\. For details, see [[ERROR] BAD/MISSING LINK TEXT](aes-supported-instance-types.md)\.

+ Encryption of data at rest is not available in the cn\-northwest\-1 \(Ningxia\) region\.

+ Kibana still works on domains that encrypt data at rest\.

+ Domains that encrypt data at rest use a different repository name for their automated snapshots\. To learn more, see [[ERROR] BAD/MISSING LINK TEXT](es-managedomains-snapshots.md#es-managedomains-snapshot-restore)\.

+ Encrypting an Amazon ES domain requires two [grants](http://docs.aws.amazon.com/kms/latest/developerguide/grants.html), and each encryption key has a [limit](http://docs.aws.amazon.com/kms/latest/developerguide/limits.html#grants-per-principal-per-key) of 500 grants per principal\. This limit means that the maximum number of Amazon ES domains you can encrypt using a single key is 250\. At present, Amazon ES supports a maximum of 100 domains per account, so this grant limit is of no consequence\. If the domain limit per account increases, however, the grant limit might become relevant\.

  If you need to encrypt more than 250 domains at that time, you can create additional keys\. Keys are regional, not global, so if you operate in more than one region, you already need multiple keys\.