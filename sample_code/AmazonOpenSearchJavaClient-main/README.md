# Example code for accessing the Amazon OpenSearch Service

This sample code shows how to set up an authenticated connection to the service and how to access its key APIs.  

# Instructions

1. Clone the repository.
2. Configure the following parameters in the AmazonOpenSearchServiceSample.java file:

- `region` - The AWS Region that your collection is in. For example, `us-east-1`.
- `host` - The collection endpoint with https://. For example, `https://07tjusf2h91cunochc.us-east-1.aoss.amazonaws.com`.
- `index_name` - The name of the OpenSearch index to create. The example uses `my-index`.

  You can also optionally modify the index mapping, the sample document, and the search request.

3. Save and run the file.
