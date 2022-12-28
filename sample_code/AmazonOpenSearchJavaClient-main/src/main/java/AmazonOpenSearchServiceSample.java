import com.amazonaws.auth.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;
import com.amazonaws.http.AWSRequestSigningApacheInterceptor;

import java.io.IOException;

public class AmazonOpenSearchServiceSample {

    private static String serviceName = "aoss";
    private static String region = "";
    private static String domainEndpoint = ""; // The collection endpoint. For example, my-test-collection.us-east-1.aoss.amazonaws.com
    private static String index_name = "my-index";

    private static String mapping = "{ \"settings\": { \"number_of_shards\": 1, \"number_of_replicas\": 0 }, \"mappings\": { \"properties\": { \"title\": {\"type\": \"text\"}, \"director\": {\"type\": \"text\"}, \"year\": {\"type\": \"text\"} } } }";
    private static String createIndexPath = host + "/" + index_name;

    private static String sampleDocument = "{" + "\"title\":\"Walk the Line\"," + "\"director\":\"James Mangold\"," + "\"year\":\"2005\"}";
    private static String indexingPath = host + "/" + index_name + "/_doc";

    private static String sampleSearch = "{ \"query\": { \"match_all\": {}}}";
    private static String searchPath = host + "/" + index_name + "/_search";

    static final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

    public static void main(String[] args) throws IOException {
        RestClient searchClient = searchClient(serviceName, region);

        // Create Index
        HttpEntity entity = new NStringEntity(mapping, ContentType.APPLICATION_JSON);
        Request request = new Request("PUT", createIndexPath);
        request.setEntity(entity);
        // request.addParameter(name, value); // optional parameters
        Response response = searchClient.performRequest(request);
        System.out.println("Create Index Response : " + response.toString());

        // Index a document
        entity = new NStringEntity(sampleDocument, ContentType.APPLICATION_JSON);
        String id = "1";
        request = new Request("PUT", indexingPath);
        request.setEntity(entity);

        // Using a String instead of an HttpEntity sets Content-Type to application/json automatically.
        // request.setJsonEntity(sampleDocument);

        response = searchClient.performRequest(request);
        System.out.println("Indexing Document Response : " + response.toString());

        // Match All Search
        entity = new NStringEntity(sampleSearch, ContentType.APPLICATION_JSON);
        request = new Request("GET", searchPath);
        request.setEntity(entity);

        response = searchClient.performRequest(request);
        System.out.println("Match All Search Response : " + response.toString());
    }

    // Adds the interceptor to the OpenSearch REST client
    public static RestClient searchClient(String serviceName, String region) {
        AWS4UnsignedPayloadSigner signer = new AWS4UnsignedPayloadSigner();
        signer.setServiceName(serviceName);
        signer.setRegionName(region);
        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider);
        return RestClient.builder(HttpHost.create(domainEndpoint)).setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)).build();
    }
}
