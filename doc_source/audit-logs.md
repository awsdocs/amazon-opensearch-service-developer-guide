# Audit Logs for Amazon Elasticsearch Service<a name="audit-logs"></a>

If your domain uses fine\-grained access control, Amazon Elasticsearch Service offers optional audit logs for your data\. These logs are highly customizable and show a variety of user activity, including failed login attempts and which users accessed certain indices, documents, or fields\. The default configuration tracks a popular set of user actions, but we recommend tailoring the settings to your exact needs\.

Just like [Elasticsearch application logs and slow logs](es-createdomain-configure-slow-logs.md), Amazon ES publishes audit logs to CloudWatch Logs\. If enabled, [standard CloudWatch pricing](https://aws.amazon.com/cloudwatch/pricing/) applies\.

**Note**  
To enable audit logs, your user role must be mapped to the `security_manager` role, which gives you access to the `_opendistro/_security` REST API\. To learn more, see [Modifying the Master User](fgac.md#fgac-forget)\.

## Limitations<a name="audit-logs-limitations"></a>

Audit logs have the following limitations:
+ Audit logs don't include cross\-cluster search requests that were rejected by the destination's domain access policy\.
+ The maximum size of each audit log message is 10,000 characters\. The audit log message is truncated if it exceeds this limit\.

## Enabling Audit Logs<a name="audit-log-enabling"></a>

Enabling audit logs is a two\-step process\. First, you must configure your domain to publish audit logs to CloudWatch Logs using the console, AWS CLI, or configuration API\. Then you can tune audit log settings using Kibana or the fine\-grained access control REST API\.

**To enable audit logs for an Amazon ES domain \(console\)**

1. Choose the domain and then the **Logs** tab\.

1. Under **Set up audit logs**, choose **Setup**\.

1. Create a CloudWatch log group, or choose an existing one\.

1. Choose an access policy that contains the appropriate permissions, or create a policy using the JSON that the console provides:

   ```
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Principal": {
           "Service": "es.amazonaws.com"
         },
         "Action": [
           "logs:PutLogEvents",
           "logs:CreateLogStream"
         ],
         "Resource": "cw_log_group_arn"
       }
     ]
   }
   ```

1. Choose **Enable**\.

### Sample CLI Command<a name="audit-log-enabling-cli"></a>

The following AWS CLI command enables audit logs on an existing domain:

```
aws es update-elasticsearch-domain-config --domain-name my-domain --log-publishing-options "AUDIT_LOGS={CloudWatchLogsLogGroupArn=arn:aws:logs:us-east-1:123456789012:log-group:my-log-group,Enabled=true}"
```

You can also enable audit logs when you create a domain\. For detailed information, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/)\.

### Sample Configuration API Request<a name="audit-log-enabling-api"></a>

The following request to the configuration API enables audit logs on an existing domain:

```
POST https://es.us-east-1.amazonaws.com/2015-01-01/es/domain/my-domain/config
{
  "LogPublishingOptions": {
    "AUDIT_LOGS": {
      "CloudWatchLogsLogGroupArn":"arn:aws:logs:us-east-1:123456789012:log-group1:sample-domain",
      "Enabled":true|false
    }
  }
}
```

For detailed information, see [Amazon Elasticsearch Service Configuration API Reference](es-configuration-api.md)\.

## Audit Log Kibana UI<a name="audit-log-kibana-ui"></a>

After you enable audit logs, configure them to match your needs\.

1. Open Kibana, and choose **Security**\.

1. Under **System**, choose **Audit logging**\.

1. Choose **Enable audit logging**\.

![\[Audit logs Kibana UI screenshot\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/images/audit-logs.png)

The Kibana UI offers full control of audit log settings under **General settings** and **Compliance settings**\. For a description of all configuration options, see [Audit Log Settings](#audit-log-settings)\.

## Audit Log Layers and Categories<a name="audit-log-layers"></a>

Cluster communication occurs over two separate *layers*: the REST layer and the transport layer\.
+ The REST layer covers communication with HTTP clients such as curl, Logstash, Kibana, the [Java high\-level REST client](es-request-signing.md#es-request-signing-java), the Python [Requests](https://2.python-requests.org/) library—all HTTP requests that arrive at the cluster\.
+ The transport layer covers communication between nodes\. For example, after a search request arrives at the cluster \(over the REST layer\), the coordinating node serving the request sends the query to other nodes, receives their responses, gathers the necessary documents, and collates them into the final response\. Operations such as shard allocation and rebalancing also occur over the transport layer\.

You can enable or disable audit logs for entire layers, as well as individual audit categories for a layer\. The following table contains a summary of audit categories and the layers for which they are available\.


| Category | Description | Available for REST | Available for Transport | 
| --- | --- | --- | --- | 
|  FAILED\_LOGIN  | A request contained invalid credentials, and authentication failed\. | Yes | Yes | 
|  MISSING\_PRIVILEGES  | A user did not have the privileges to make the request\. | Yes | Yes | 
|  GRANTED\_PRIVILEGES  | A user had the privileges to make the request\. | Yes | Yes | 
|  OPENDISTRO\_SECURITY\_INDEX\_ATTEMPT  | A request tried to modify the \.opendistro\_security index\. | No | Yes | 
|  AUTHENTICATED  | A request contained valid credentials, and authentication succeeded\. | Yes | Yes | 
|  INDEX\_EVENT  | A request performed an administrative operation on an index, such as creating one, setting an alias, or performing a force merge\. The full list of indices:admin/ actions that this category includes are available in the [Open Distro for Elasticsearch documentation](https://opendistro.github.io/for-elasticsearch-docs/docs/security/access-control/permissions/#indices)\. | No | Yes | 

In addition to these standard categories, fine\-grained access control offers several additional categories designed to meet data compliance requirements\.


| Category | Description | 
| --- | --- | 
|  COMPLIANCE\_DOC\_READ  | A request performed a read event on a document in an index\. | 
|  COMPLIANCE\_DOC\_WRITE  | A request performed a write event on a document in an index\. | 
|  COMPLIANCE\_INTERNAL\_CONFIG\_READ  |  A request performed a read event on the `.opendistro_security` index\.  | 
|  COMPLIANCE\_INTERNAL\_CONFIG\_WRITE  | A request performed a write event on the `.opendistro_security` index\. | 

You can have any combination of categories and message attributes\. For example, if you send a REST request to index a document, you might see the following lines in the audit logs:
+ AUTHENTICATED on REST layer \(authentication\)
+ GRANTED\_PRIVILEGE on transport layer \(authorization\)
+ COMPLIANCE\_DOC\_WRITE \(document written to an index\)

## Audit Log Settings<a name="audit-log-settings"></a>

Audit logs have numerous configuration options\.

### General Settings<a name="audit-logs-general-settings"></a>

General settings let you enable or disable individual categories or entire layers\. We highly recommend leaving GRANTED\_PRIVILEGES and AUTHENTICATED as excluded categories\. Otherwise, these categories are logged for every valid request to the cluster\.


| Name | Backend Setting | Description | 
| --- | --- | --- | 
|  REST layer  |  enable\_rest  |  Enable or disable events that occur on the REST layer\.  | 
|  REST disabled categories  |  disabled\_rest\_categories  |  Specify audit categories to ignore on the REST layer\. Modifying these categories can dramatically increase the size of the audit logs\.  | 
|  Transport layer  |  enable\_transport  |  Enable or disable events that happen on the transport layer\.  | 
|  Transport disabled categories  |  disabled\_transport\_categories  |  Specify audit categories which must be ignored on the transport layer\. Modifying these categories can dramatically increase the size of the audit logs\.  | 

Attribute settings let you customize the amount of detail in each log line\.


| Name | Backend Setting | Description | 
| --- | --- | --- | 
|  Bulk requests  |  resolve\_bulk\_requests  |  Enabling this setting generates a log for each document in a bulk request, which can dramatically increase the size of the audit logs\.  | 
|  Request body  |  log\_request\_body  |  Include the request body of the requests\.  | 
|  Resolve indices  |  resolve\_indices  |  Resolve aliases to indices\.  | 

Use ignore settings to exclude a set of users or API paths:


| Name | Backend Setting | Description | 
| --- | --- | --- | 
|  Ignored users  |  ignore\_users  |  Specify users that you want to exclude\.  | 
|  Ignored requests  |  ignore\_requests  |  Specify request patterns that you want to exclude\.  | 

### Compliance Settings<a name="audit-logs-compliance-settings"></a>

Compliance settings let you tune for index, document, or field\-level access\.


| Name | Backend Setting | Description | 
| --- | --- | --- | 
|  Compliance logging  |  enable\_compliance  |  Enable or disable compliance logging\.  | 

You can specify the following settings for read events\.


| Name | Backend Setting | Description | 
| --- | --- | --- | 
|  Read metadata  |  read\_metadata\_only  |  Include only metadata for read events\. Do not include any document fields\.  | 
|  Ignored users  |  read\_ignore\_users  |  Do not include certain users for read events\.  | 
|  Watched fields  |  read\_watched\_fields  |  Specify the indices and fields to watch for read events\. Adding watched fields generates one log per document access, which can dramatically increase the size of the audit logs\. Watched fields support index patterns and field patterns: <pre>{<br />  "index-name-pattern": [<br />    "field-name-pattern"<br />  ],<br />  "logs*": [<br />    "message"<br />  ],<br />  "twitter": [<br />    "id",<br />    "user*"<br />  ]<br />}</pre>  | 

You can specify the following settings for write events\.


| Name | Backend Setting | Description | 
| --- | --- | --- | 
|  Write metadata  |  write\_metadata\_only  |  Include only metadata for write events\. Do not include any document fields\.  | 
|  Log diffs  |  write\_log\_diffs  |  If write\_metadata\_only is false, include only the differences between write events\.  | 
|  Ignored users  |  write\_ignore\_users  |  Do not include certain users for write events\.  | 
|  Watch indices  |  write\_watched\_indices  |  Specify the indices or index patters to watch for write events\. Adding watched fields generates one log per document access, which can dramatically increase the size of the audit logs\.  | 

## Audit Log Example<a name="audit-log-example"></a>

This section includes an example configuration, search request, and the resulting audit log for all read and write events of an index\.

### Step 1: Configure audit logs<a name="audit-log-example-step1"></a>

After you enable the publishing of audit logs to a CloudWatch Logs group, navigate to the Kibana audit logging page and choose **Enable audit logging**\.

1. In **General Settings**, choose **Configure** and make sure that the **REST layer** is enabled\.

1. In **Compliance Settings**, choose **Configure**\.

1. Under **Write**, in **Watched Fields**, add `accounts` for all write events to this index\.

1. Under **Read**, in **Watched Fields**, add `ssn` and `id-` fields of the `accounts` index:

   ```
   {
     "accounts-": [
       "ssn",
       "id-"
     ]
   }
   ```

### Step 2: Perform read and write events<a name="audit-log-example-step2"></a>

1. Navigate to Kibana, choose **Dev Tools** and index a sample document:

   ```
   PUT accounts/_doc/0
   {
     "ssn": "123",
     "id-": "456"
   }
   ```

1. To test a read event, send the following request:

   ```
   GET accounts/_search
   {
     "query": {
       "match_all": {}
     }
   }
   ```

### Step 3: Observe the logs<a name="audit-log-example-step2"></a>

1. Open the CloudWatch console at [https://console\.aws\.amazon\.com/cloudwatch/](https://console.aws.amazon.com/cloudwatch/)\.

1. In the navigation pane, choose **Log groups**\.

1. Choose the log group that you specified while enabling audit logs\. Within the log group, Amazon ES creates a log stream for each node in your domain\.

1. In **Log streams**, choose **Search all**\.

1. For the read and write events, see the corresponding logs\. You can expect a delay of 5 seconds before the log appears\.

   **Sample write audit log** 

   ```
   {
     "audit_compliance_operation": "CREATE",
     "audit_cluster_name": "824471164578:audit-test",
     "audit_node_name": "be217225a0b77c2bd76147d3ed3ff83c",
     "audit_category": "COMPLIANCE_DOC_WRITE",
     "audit_request_origin": "REST",
     "audit_compliance_doc_version": 1,
     "audit_node_id": "3xNJhm4XS_yTzEgDWcGRjA",
     "@timestamp": "2020-08-23T05:28:02.285+00:00",
     "audit_format_version": 4,
     "audit_request_remote_address": "3.236.145.227",
     "audit_trace_doc_id": "lxnJGXQBqZSlDB91r_uZ",
     "audit_request_effective_user": "admin",
     "audit_trace_shard_id": 8,
     "audit_trace_indices": [
       "accounts"
     ],
     "audit_trace_resolved_indices": [
       "accounts"
     ]
   }
   ```

    **Sample read audit log** 

   ```
   {
     "audit_cluster_name": "824471164578:audit-docs",
     "audit_node_name": "806f6050cb45437e2401b07534a1452f",
     "audit_category": "COMPLIANCE_DOC_READ",
     "audit_request_origin": "REST",
     "audit_node_id": "saSevm9ASte0-pjAtYi2UA",
     "@timestamp": "2020-08-31T17:57:05.015+00:00",
     "audit_format_version": 4,
     "audit_request_remote_address": "54.240.197.228",
     "audit_trace_doc_id": "config:7.7.0",
     "audit_request_effective_user": "admin",
     "audit_trace_shard_id": 0,
     "audit_trace_indices": [
       "accounts"
     ],
     "audit_trace_resolved_indices": [
       "accounts"
     ]
   }
   ```

To include the request body, return to **Compliance settings** in Kibana and disable **Write metadata**\. To exclude events by a specific user, add the user to **Ignored Users**\. 

For a description of each audit log field, see [Audit log field reference](https://opendistro.github.io/for-elasticsearch-docs/docs/security/audit-logs/field-reference/)\. For information on searching and analyzing your audit log data, see [Analyzing Log Data with CloudWatch Logs Insights](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/AnalyzingLogData.html) in the *Amazon CloudWatch Logs User Guide*\. 

## Audit Log REST API<a name="audit-log-rest-api"></a>

We recommend using Kibana to configure audit logs, but you can also use the fine\-grained access control REST API\. This section contains a sample request\. Full documentation on the REST API is available in the [Open Distro for Elasticsearch documentation](https://opendistro.github.io/for-elasticsearch-docs/docs/security/access-control/api/)\.

```
PUT _opendistro/_security/api/audit/config
{
  "config": {
    "enabled": true,
    "audit": {
      "enable_rest": true,
      "disabled_rest_categories": [
        "GRANTED_PRIVILEGES",
        "AUTHENTICATED"
      ],
      "enable_transport": true,
      "disabled_transport_categories": [
        "GRANTED_PRIVILEGES",
        "AUTHENTICATED"
      ],
      "resolve_bulk_requests": true,
      "log_request_body": true,
      "resolve_indices": true,
      "exclude_sensitive_headers": true,
      "ignore_users": [
        "kibanaserver"
      ],
      "ignore_requests": [
        "SearchRequest",
        "indices:data/read/*"
      ]
    },
    "compliance": {
      "enabled": true,
      "internal_config": true,
      "external_config": true,
      "read_metadata_only": true,
      "read_watched_fields": {
        "read-index-1": [
          "field-1",
          "field-2"
        ],
        "read-index-2": [
          "field-3"
        ]
      },
      "read_ignore_users": [
        "read-ignore-1"
      ],
      "write_metadata_only": true,
      "write_log_diffs": false,
      "write_watched_indices": [
        "write-index-1",
        "write-index-2",
        "log-*",
        "*"
      ],
      "write_ignore_users": [
        "write-ignore-1"
      ]
    }
  }
}
```