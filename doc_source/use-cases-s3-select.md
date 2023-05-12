# Selective download with Amazon OpenSearch Ingestion<a name="use-cases-s3-select"></a>

If your pipeline uses an [S3 source](https://opensearch.org/docs/latest/data-prepper/pipelines/configuration/sources/s3/), you can use SQL expressios to perform filtering and computations on the contents of S3 objects before ingesting them into a pipeline\.

The `s3_select` option supports objects in Parquet format\. It also works with objects that are compressed with GZIP or BZIP2 \(for CSV and JSON objects only\), and supports columnar compression for Parquet using GZIP and Snappy\.

The following example pipeline downloads data in incoming S3 objects, encoded in Parquet format:

```
pipeline:
  source:
    s3:
      s3_select:
        expression: "select * from s3object s"  
        input_serialization: parquet
      notification_type: "sqs"
...
```

The following example downloads only the first 1,000 records in the objects:

```
pipeline:
  source:
    s3:
      s3_select:
        expression: "select * from s3object s LIMIT 10000"
        input_serialization: parquet
      notification_type: "sqs"
...
```

The following example checks for the minimum and maximum value of `data_value` before ingesting events into the pipeline:

```
pipeline:
  source:
    s3:
      s3_select:
        expression: "select s.* from s3object s where s.data_value > 200 and s.data_value < 500 "
        input_serialization: parquet
      notification_type: "sqs"
...
```

In addition to these examples, you can also use the **S3 select pipeline** blueprint\. For more information about blueprints, see [Using blueprints to create a pipeline](creating-pipeline.md#pipeline-blueprint)\.

For more information, see the following resources:
+ [Filtering and retrieving data using Amazon S3 Select](https://docs.aws.amazon.com/AmazonS3/latest/user-guide/selecting-content-from-objects.html)
+ [SQL reference for Amazon S3 Select](https://docs.aws.amazon.com/AmazonS3/latest/user-guide/s3-select-sql-reference.html)