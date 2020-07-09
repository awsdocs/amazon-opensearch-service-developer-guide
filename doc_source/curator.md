# Using Curator to Rotate Data in Amazon Elasticsearch Service<a name="curator"></a>

This section contains sample code for using AWS Lambda and [Curator](http://curator.readthedocs.io/en/latest/index.html) to manage indices and snapshots\. Curator offers numerous filters to help you identify indices and snapshots that meet certain criteria, such as indices created more than 60 days ago or snapshots that failed to complete\. [Index State Management](ism.md) has some similar features and doesn't require Lambda or a separate EC2 instance\. Depending on your use case, it might be a better choice\.

Although Curator is often used as a command line interface \(CLI\), it also features a Python API, which means that you can use it within Lambda functions\.

For information about configuring Lambda functions and creating deployment packages, see [Loading Streaming Data into Amazon ES from Amazon S3](es-aws-integrations.md#es-aws-integrations-s3-lambda-es)\. For even more information, see the [AWS Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/)\. This section contains only sample code, basic settings, triggers, and permissions\.

**Topics**
+ [Sample Code](#curator-sample)
+ [Basic Settings](#curator-basic)
+ [Triggers](#curator-trigger)
+ [Permissions](#curator-permissions)

## Sample Code<a name="curator-sample"></a>

The following sample code uses Curator and [elasticsearch\-py](https://elasticsearch-py.readthedocs.io/) to delete any index whose name contains a time stamp indicating that the data is more than 30 days old\. For example, if an index name is `my-logs-2014.03.02`, the index is deleted\. Deletion occurs even if you create the index today, because this filter uses the name of the index to determine its age\.

The code also contains some commented\-out examples of other common filters, including one that determines age by creation date\. The AWS SDK for Python \(Boto3\) and [requests\-aws4auth](https://pypi.org/project/requests-aws4auth/) library sign the requests to Amazon ES\.

**Warning**  
Both code samples in this section delete data—potentially a lot of data\. Modify and test each sample on a non\-critical domain until you're satisfied with its behavior\.

**Index Deletion**

```
import boto3
from requests_aws4auth import AWS4Auth
from elasticsearch import Elasticsearch, RequestsHttpConnection
import curator

host = '' # For example, search-my-domain.region.es.amazonaws.com
region = '' # For example, us-west-1
service = 'es'
credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

# Lambda execution starts here.
def lambda_handler(event, context):

    # Build the Elasticsearch client.
    es = Elasticsearch(
        hosts = [{'host': host, 'port': 443}],
        http_auth = awsauth,
        use_ssl = True,
        verify_certs = True,
        connection_class = RequestsHttpConnection
    )

    # A test document.
    document = {
        "title": "Moneyball",
        "director": "Bennett Miller",
        "year": "2011"
    }

    # Index the test document so that we have an index that matches the timestring pattern.
    # You can delete this line and the test document if you already created some test indices.
    es.index(index="movies-2017.01.31", doc_type="movie", id="1", body=document)

    index_list = curator.IndexList(es)

    # Filters by age, anything with a time stamp older than 30 days in the index name.
    index_list.filter_by_age(source='name', direction='older', timestring='%Y.%m.%d', unit='days', unit_count=30)

    # Filters by naming prefix.
    # index_list.filter_by_regex(kind='prefix', value='my-logs-2017')

    # Filters by age, anything created more than one month ago.
    # index_list.filter_by_age(source='creation_date', direction='older', unit='months', unit_count=1)

    print("Found %s indices to delete" % len(index_list.indices))

    # If our filtered list contains any indices, delete them.
    if index_list.indices:
        curator.DeleteIndices(index_list).do_action()
```

You must update the values for `host` and `region`\.

The next code sample deletes any snapshot that is more than two weeks old\. It also takes a new snapshot\.

**Snapshot Deletion**

```
import boto3
from datetime import datetime
from requests_aws4auth import AWS4Auth
from elasticsearch import Elasticsearch, RequestsHttpConnection
import logging
import curator

# Adding a logger isn't strictly required, but helps with understanding Curator's requests and debugging.
logger = logging.getLogger('curator')
logger.addHandler(logging.StreamHandler())
logger.setLevel(logging.INFO)

host = '' # For example, search-my-domain.region.es.amazonaws.com
region = '' # For example, us-west-1
service = 'es'
credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)
repository_name = 'my-repo'

# Lambda execution starts here.
def lambda_handler(event, context):

    now = datetime.now()
    
    # Clunky, but this approach keeps colons out of the URL.
    date_string = '-'.join((str(now.year), str(now.month), str(now.day), str(now.hour), str(now.minute)))
    snapshot_name = 'my-snapshot-prefix-' + date_string

    # Build the Elasticsearch client.
    es = Elasticsearch(
        hosts = [{'host': host, 'port': 443}],
        http_auth = awsauth,
        use_ssl = True,
        verify_certs = True,
        connection_class = RequestsHttpConnection,
        timeout = 120 # Deleting snapshots can take a while, so keep the connection open for long enough to get a response.
    )

    try:
        # Get all snapshots in the repository.
        snapshot_list = curator.SnapshotList(es, repository=repository_name)

        # Filter by age, any snapshot older than two weeks.
        # snapshot_list.filter_by_age(source='creation_date', direction='older', unit='weeks', unit_count=2)

        # Delete the old snapshots.
        curator.DeleteSnapshots(snapshot_list, retry_interval=30, retry_count=3).do_action()
    except (curator.exceptions.SnapshotInProgress, curator.exceptions.NoSnapshots, curator.exceptions.FailedExecution) as e:
        print(e)

    # Split into two try blocks. We still want to try and take a snapshot if deletion failed.
    try:
        # Get the list of indices.
        # You can filter this list if you didn't want to snapshot all indices.
        index_list = curator.IndexList(es)

        # Take a new snapshot. This operation can take a while, so we don't want to wait for it to complete.
        curator.Snapshot(index_list, repository=repository_name, name=snapshot_name, wait_for_completion=False).do_action()
    except (curator.exceptions.SnapshotInProgress, curator.exceptions.FailedExecution) as e:
        print(e)
```

You must update the values for `host`, `region`, `snapshot_name`, and `repository_name`\. If the output is too verbose for your taste, you can change `logging.INFO` to `logging.WARN`\.

Because taking and deleting snapshots can take a while, this code is more sensitive to connection and Lambda timeouts—hence the extra logging code\. In the Elasticsearch client, you can see that we set the timeout to 120 seconds\. If the `DeleteSnapshots` function takes longer to get a response from the Amazon ES domain, you might need to increase this value\. You must also increase the Lambda function timeout from its default value of three seconds\. For a recommended value, see [Basic Settings](#curator-basic)\.

## Basic Settings<a name="curator-basic"></a>

We recommend the following settings for these code samples\.


| Sample Code | Memory | Timeout | 
| --- | --- | --- | 
| Index Deletion | 128 MB | 10 seconds | 
| Snapshot Deletion | 128 MB | 3 minutes | 

## Triggers<a name="curator-trigger"></a>

Rather than reacting to some event \(such as a file upload to Amazon S3\), these functions are meant to be scheduled\. You might prefer to run these functions more or less frequently\.


| Sample Code | Service | Rule Type | Example Expression | 
| --- | --- | --- | --- | 
| Index Deletion | CloudWatch Events | Schedule expression | rate\(1 day\) | 
| Snapshot Deletion | CloudWatch Events | Schedule expression | rate\(4 hours\) | 

## Permissions<a name="curator-permissions"></a>

Both Lambda functions in this section need the basic logging permissions that all Lambda functions need, plus HTTP method permissions for the Amazon ES domain:

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "logs:CreateLogGroup",
      "Resource": "arn:aws:logs:us-west-1:123456789012:*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": [
        "arn:aws:logs:us-west-1:123456789012:log-group:/aws/lambda/your-lambda-function:*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "es:ESHttpPost",
        "es:ESHttpGet",
        "es:ESHttpPut",
        "es:ESHttpDelete"
      ],
      "Resource": "arn:aws:es:us-west-1:123456789012:domain/my-domain/*"
    }
  ]
}
```