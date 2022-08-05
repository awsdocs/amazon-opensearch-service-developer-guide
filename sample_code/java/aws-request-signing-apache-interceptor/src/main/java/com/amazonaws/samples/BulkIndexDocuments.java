package com.amazonaws.samples;

import com.amazonaws.http.AwsRequestSigningApacheInterceptor;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.opensearch.OpenSearchStatusException;
import org.opensearch.action.bulk.BulkItemResponse;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BulkIndexDocuments {

    private static final String serviceName = "es";
    private static final String region = "us-west-2";
    private static final String endpoint = "https://search-...us-west-2.es.amazonaws.com";
    private static final String type = "_doc";

    static final AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();

    public static void main(String[] args) throws IOException {

        RestHighLevelClient client = getOpenSearchClient();

        try {
            String index = "java-client-test-index";

            // Create a document that simulates a simple log line from a web server
            Map<String, Object> document = new HashMap<>();
            document.put("method", "GET");
            document.put("client_ip_address", "123.456.78.90");
            document.put("timestamp", "10/Oct/2000:14:56:14 -0700");

            System.out.println("Demoing a single index request:");
            String id = "1";
            IndexRequest indexRequest = new IndexRequest(index, type, id).source(document);
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            System.out.println(indexResponse.toString());

            System.out.println("Demoing a 1 MB bulk request:");
            BulkRequest bulkRequest = new BulkRequest();

            // Add documents (the simple log line from earlier) to the request until it
            // exceeds 1 MB
            while (bulkRequest.estimatedSizeInBytes() < 1000000) {
                // By not specifying an ID, these documents get auto-assigned IDs
                bulkRequest.add(new IndexRequest(index, type).source(document));
            }

            try {
                // Send the request and get the response
                BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

                // Check the response for failures
                if (bulkResponse.hasFailures()) {
                    System.out.println("Encountered failures:");
                    for (BulkItemResponse bulkItemResponse : bulkResponse) {
                        if (bulkItemResponse.isFailed()) {
                            System.out.println(bulkItemResponse.getFailureMessage());
                        }
                    }
                } else {
                    System.out.println("No failures!");
                    // Uncomment these lines for a line-by-line summary
                    // for (BulkItemResponse bulkItemResponse : bulkResponse) {
                    //      System.out.println(bulkItemResponse.getResponse().toString());
                    // }
                }
            }

            // Usually happens when the request size is too large
            catch (OpenSearchStatusException e) {
                System.out.println("Encountered exception:");
                System.out.println(e);
            }
        } finally {
            client.close();
        }
    }

    // Adds the interceptor to the OpenSearch REST client
    public static RestHighLevelClient getOpenSearchClient() {
        Aws4Signer signer = Aws4Signer.create();
        HttpRequestInterceptor interceptor = new AwsRequestSigningApacheInterceptor(serviceName, signer,
                credentialsProvider, region);
        return new RestHighLevelClient(RestClient.builder(HttpHost.create(endpoint))
                .setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));
    }
}