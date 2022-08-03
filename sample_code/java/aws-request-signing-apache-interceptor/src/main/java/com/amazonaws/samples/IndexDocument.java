package com.amazonaws.samples;

import com.amazonaws.http.AwsRequestSigningApacheInterceptor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import java.io.IOException;

public class IndexDocument {

    private static String serviceName = "es";
    private static String region = "us-west-2";
    private static String endpoint = "https://search-...us-west-2.es.amazonaws.com";

    private static String sampleDocument = "{" + "\"title\":\"Walk the Line\"," + "\"director\":\"James Mangold\","
            + "\"year\":\"2005\"}";
    private static String indexingPath = "/my-index/_doc";

    static final AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();

    public static void main(String[] args) throws IOException {
        RestClient client = getOpenSearchClient(serviceName, region);

        try {
            // Index a document
            HttpEntity entity = new NStringEntity(sampleDocument, ContentType.APPLICATION_JSON);
            String id = "1";
            Request request = new Request("PUT", indexingPath + "/" + id);
            request.setEntity(entity);

            // Using a String instead of an HttpEntity sets Content-Type to application/json
            // automatically.
            // request.setJsonEntity(sampleDocument);

            Response response = client.performRequest(request);
            System.out.println(response.toString());
        } finally {
            client.close();
        }
    }

    // Adds the interceptor to the OpenSearch REST client
    public static RestClient getOpenSearchClient(String serviceName, String region) {
        Aws4Signer signer = Aws4Signer.create();
        HttpRequestInterceptor interceptor = new AwsRequestSigningApacheInterceptor(serviceName, signer,
                credentialsProvider, region);
        return RestClient.builder(HttpHost.create(endpoint))
                .setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)).build();
    }
}