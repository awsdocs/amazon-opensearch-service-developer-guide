To run this sample, modify `host` and `region` in [IndexDocument.java](src/main/java/com/amazonaws/samples/IndexDocument.java) and [BulkIndexDocuments.java](src/main/java/com/amazonaws/samples/BulkIndexDocuments.java), and set AWS credentials (e.g. via `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` and `AWS_SESSION_TOKEN` variables).

```
mvn compile exec:java -Dexec.mainClass="com.amazonaws.samples.GetVersion"
mvn compile exec:java -Dexec.mainClass="com.amazonaws.samples.IndexDocument"
```
