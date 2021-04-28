# Encryption of data at rest for Amazon Elasticsearch Service<a name="encryption-at-rest"></a>

Amazon ES domains offer encryption of data at rest, a security feature that helps prevent unauthorized access to your data\. The feature uses AWS Key Management Service \(AWS KMS\) to store and manage your encryption keys and the Advanced Encryption Standard algorithm with 256\-bit keys \(AES\-256\) to perform the encryption\. If enabled, the feature encrypts the following aspects of a domain:
+ All indices \(including those in UltraWarm storage\)
+ Elasticsearch logs
+ Swap files
+ All other data in the application directory
+ Automated snapshots

The following are *not* encrypted when you enable encryption of data at rest, but you can take additional steps to protect them:
+ Manual snapshots: You currently can't use KMS master keys to encrypt manual snapshots\. You can, however, use server\-side encryption with S3\-managed keys or customer master keys \(CMKs\) to encrypt the bucket you use as a snapshot repository\. For instructions, see [Registering a manual snapshot repository](es-managedomains-snapshots.md#es-managedomains-snapshot-registerdirectory)\.
+ Slow logs and error logs: If you [publish logs](es-createdomain-configure-slow-logs.md) and want to encrypt them, you can encrypt their CloudWatch Logs log group using the same AWS KMS master key as the Amazon ES domain\. For more information, see [Encrypt Log Data in CloudWatch Logs Using AWS KMS](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/encrypt-log-data-kms.html) in the *Amazon CloudWatch Logs User Guide*\.

Amazon ES supports only symmetric customer master keys, not asymmetric ones\. To learn how to create symmetric customer master keys, see [Creating Keys](https://docs.aws.amazon.com/kms/latest/developerguide/create-keys.html) in the *AWS Key Management Service Developer Guide*\.

Regardless of whether encryption at rest is enabled, all domains automatically encrypt [custom packages](custom-packages.md) using AES\-256 and Amazon ES\-managed keys\.

## Enabling encryption of data at rest<a name="enabling-ear"></a>

Encryption of data at rest on new domains requires Elasticsearch 5\.1 or later\. Enabling the feature on existing domains requires Elasticsearch 6\.7 or later\. Choose the existing domain in the AWS console, **Actions**, and **Modify encryption**\.

To use the Amazon ES console to configure encryption of data at rest, you must have read permissions to AWS KMS, such as the following identity\-based policy:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "kms:List*",
        "kms:Describe*"
      ],
      "Resource": "*"
    }
  ]
}
```

If you want to use a master key other than **\(Default\) aws/es**, you must also have permissions to create [grants](https://docs.aws.amazon.com/kms/latest/developerguide/grants.html) for the key\. These permissions typically take the form of a resource\-based policy that you specify when you create the key\.

If you want to keep your key exclusive to Amazon ES, you can add the [kms:ViaService](https://docs.aws.amazon.com/kms/latest/developerguide/policy-conditions.html#conditions-kms-via-service) condition to that key policy:

```
"Condition": {
  "StringEquals": {
    "kms:ViaService": "es.us-west-1.amazonaws.com"
  },
  "Bool": {
    "kms:GrantIsForAWSResource": "true"
  }
}
```

For more information, see [Using Key Policies in AWS KMS](https://docs.aws.amazon.com/kms/latest/developerguide/key-policies.html) in the *AWS Key Management Service Developer Guide*\.

**Warning**  
If you delete the key that you used to encrypt a domain, the domain becomes inaccessible\. The Amazon ES team can't help you recover your data\. AWS KMS deletes master keys only after a waiting period of at least seven days, so the Amazon ES team might contact you if they detect that your domain is at risk\.

## Disabling encryption of data at rest<a name="disabling-ear"></a>

After you configure a domain to encrypt data at rest, you can't disable the setting\. Instead, you can take a [manual snapshot](es-managedomains-snapshots.md) of the existing domain, [create another domain](es-createupdatedomains.md#es-createdomains), migrate your data, and delete the old domain\.

## Monitoring domains that encrypt data at rest<a name="monitoring-ear"></a>

Domains that encrypt data at rest have two additional metrics: `KMSKeyError` and `KMSKeyInaccessible`\. These metrics appear only if the domain encounters a problem with your encryption key\. For full descriptions of these metrics, see [Cluster metrics](es-managedomains-cloudwatchmetrics.md#es-managedomains-cloudwatchmetrics-cluster-metrics)\. You can view them using either the Amazon ES console or the Amazon CloudWatch console\.

**Tip**  
Each metric represents a significant problem for a domain, so we recommend that you create CloudWatch alarms for both\. For more information, see [Recommended CloudWatch alarms for Amazon Elasticsearch Service](cloudwatch-alarms.md)\.

## Other considerations<a name="ear-considerations"></a>
+ Automatic key rotation preserves the properties of your AWS KMS master keys, so the rotation has no effect on your ability to access your Elasticsearch data\. Encrypted Amazon ES domains don't support manual key rotation, which involves creating a new master key and updating any references to the old key\. To learn more, see [Rotating Customer Master Keys](https://docs.aws.amazon.com/kms/latest/developerguide/rotate-keys.html) in the *AWS Key Management Service Developer Guide*\.
+ Certain instance types don't support encryption of data at rest\. For details, see [Supported instance types in Amazon Elasticsearch Service](aes-supported-instance-types.md)\.
+ Domains that encrypt data at rest use a different repository name for their automated snapshots\. For more information, see [Restoring snapshots](es-managedomains-snapshots.md#es-managedomains-snapshot-restore)\.
+ Encrypting an Amazon ES domain requires a [grant](https://docs.aws.amazon.com/kms/latest/developerguide/grants.html), and each encryption key has a [limit](https://docs.aws.amazon.com/kms/latest/developerguide/limits.html#grants-per-principal-per-key) of 500 grants per principal\. This limit means that the maximum number of Amazon ES domains that you can encrypt using a single key is 500\. Currently, Amazon ES supports a maximum of 100 domains per account \(per Region\), so this grant limit is of no consequence\. If the domain limit per account increases, however, the grant limit might become relevant\.

  If you need to encrypt more than 500 domains at that time, you can create additional keys\. Keys are regional, not global, so if you operate in more than one AWS Region, you already need multiple keys\.