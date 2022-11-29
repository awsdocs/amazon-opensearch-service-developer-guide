# Encryption of data at rest for Amazon OpenSearch Service<a name="encryption-at-rest"></a>

OpenSearch Service domains offer encryption of data at rest, a security feature that helps prevent unauthorized access to your data\. The feature uses AWS Key Management Service \(AWS KMS\) to store and manage your encryption keys and the Advanced Encryption Standard algorithm with 256\-bit keys \(AES\-256\) to perform the encryption\. If enabled, the feature encrypts the following aspects of a domain:
+ All indexes \(including those in UltraWarm storage\)
+ OpenSearch logs
+ Swap files
+ All other data in the application directory
+ Automated snapshots

The following are *not* encrypted when you enable encryption of data at rest, but you can take additional steps to protect them:
+ Manual snapshots: You currently can't use AWS KMS keys to encrypt manual snapshots\. You can, however, use server\-side encryption with S3\-managed keys or KMS keys to encrypt the bucket you use as a snapshot repository\. For instructions, see [Registering a manual snapshot repository](managedomains-snapshots.md#managedomains-snapshot-registerdirectory)\.
+ Slow logs and error logs: If you [publish logs](createdomain-configure-slow-logs.md) and want to encrypt them, you can encrypt their CloudWatch Logs log group using the same AWS KMS key as the OpenSearch Service domain\. For more information, see [Encrypt log data in CloudWatch Logs using AWS KMS](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/encrypt-log-data-kms.html) in the *Amazon CloudWatch Logs User Guide*\.

**Note**  
You can't enable encryption at rest on an existing domain if UltraWarm is enabled on the domain\. You must first disable UltraWarm storage, enable encryption at rest, and then re\-enable UltraWarm\.

OpenSearch Service supports only symmetric encryption KMS keys, not asymmetric ones\. To learn how to create symmetric keys, see [Creating keys](https://docs.aws.amazon.com/kms/latest/developerguide/create-keys.html) in the *AWS Key Management Service Developer Guide*\.

Regardless of whether encryption at rest is enabled, all domains automatically encrypt [custom packages](custom-packages.md) using AES\-256 and OpenSearch Service\-managed keys\.

## Permissions<a name="permissions-ear"></a>

To use the OpenSearch Service console to configure encryption of data at rest, you must have read permissions to AWS KMS, such as the following identity\-based policy:

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

If you want to use a key other than the AWS owned key, you must also have permissions to create [grants](https://docs.aws.amazon.com/kms/latest/developerguide/grants.html) for the key\. These permissions typically take the form of a resource\-based policy that you specify when you create the key\.

If you want to keep your key exclusive to OpenSearch Service, you can add the [kms:ViaService](https://docs.aws.amazon.com/kms/latest/developerguide/policy-conditions.html#conditions-kms-via-service) condition to that key policy:

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

For more information, see [Using key policies in AWS KMS](https://docs.aws.amazon.com/kms/latest/developerguide/key-policies.html) in the *AWS Key Management Service Developer Guide*\.

## Enabling encryption of data at rest<a name="enabling-ear"></a>

Encryption of data at rest on new domains requires either OpenSearch or Elasticsearch 5\.1 or later\. Enabling it on existing domains requires either OpenSearch or Elasticsearch 6\.7 or later\.

**To enable encryption of data at rest \(console\)**

1. Open the domain in the AWS console, then choose **Actions** and **Edit security configuration**\.

1. Under **Encryption**, select **Enable encryption of data at rest**\.

1. Choose an AWS KMS key to use, then choose **Save changes**\.

You can also enable encryption through the configuration API\. The following request enables encryption of data at rest on an existing domain:

```
{
   "ClusterConfig":{
      "EncryptionAtRestOptions":{
         "Enabled": true,
         "KmsKeyId":"arn:aws:kms:us-east-1:123456789012:alias/my-key"
      }
   }
}
```

## Disabled or deleted KMS key<a name="disabled-key"></a>

If you disable or delete the key that you used to encrypt a domain, the domain becomes inaccessible\. OpenSearch Service sends you a [notification](managedomains-notifications.md) informing you that it can't access the KMS key\. Re\-enable the key immediately to access your domain\.

The OpenSearch Service team can't help you recover your data if your key is deleted\. AWS KMS deletes keys only after a waiting period of at least seven days\. If your key is pending deletion, either cancel deletion or take a [manual snapshot](managedomains-snapshots.md) of the domain to prevent loss of data\.

## Disabling encryption of data at rest<a name="disabling-ear"></a>

After you configure a domain to encrypt data at rest, you can't disable the setting\. Instead, you can take a [manual snapshot](managedomains-snapshots.md) of the existing domain, [create another domain](createupdatedomains.md#createdomains), migrate your data, and delete the old domain\.

## Monitoring domains that encrypt data at rest<a name="monitoring-ear"></a>

Domains that encrypt data at rest have two additional metrics: `KMSKeyError` and `KMSKeyInaccessible`\. These metrics appear only if the domain encounters a problem with your encryption key\. For full descriptions of these metrics, see [Cluster metrics](managedomains-cloudwatchmetrics.md#managedomains-cloudwatchmetrics-cluster-metrics)\. You can view them using either the OpenSearch Service console or the Amazon CloudWatch console\.

**Tip**  
Each metric represents a significant problem for a domain, so we recommend that you create CloudWatch alarms for both\. For more information, see [Recommended CloudWatch alarms for Amazon OpenSearch Service](cloudwatch-alarms.md)\.

## Other considerations<a name="ear-considerations"></a>
+ Automatic key rotation preserves the properties of your AWS KMS keys, so the rotation has no effect on your ability to access your OpenSearch data\. Encrypted OpenSearch Service domains don't support manual key rotation, which involves creating a new key and updating any references to the old key\. To learn more, see [Rotating keys](https://docs.aws.amazon.com/kms/latest/developerguide/rotate-keys.html) in the *AWS Key Management Service Developer Guide*\.
+ Certain instance types don't support encryption of data at rest\. For details, see [Supported instance types in Amazon OpenSearch Service](supported-instance-types.md)\.
+ Domains that encrypt data at rest use a different repository name for their automated snapshots\. For more information, see [Restoring snapshots](managedomains-snapshots.md#managedomains-snapshot-restore)\.
+ While we highly recommend enabling encryption at rest, it can add additional CPU overhead and a few milliseconds of latency\. Most use cases aren't sensitive to these differences, however, and the magnitude of impact depends on the configuration of your cluster, clients, and usage profile\.