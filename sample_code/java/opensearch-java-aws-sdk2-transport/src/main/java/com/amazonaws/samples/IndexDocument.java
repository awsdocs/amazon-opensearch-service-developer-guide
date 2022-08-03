package com.amazonaws.samples;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsRequest;
import org.opensearch.client.transport.aws.AwsSdk2Transport;
import org.opensearch.client.transport.aws.AwsSdk2TransportOptions;

import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;

public class IndexDocument {

    private static Region region = Region.US_WEST_2;
    private static final String endpoint = "search-...us-west-2.es.amazonaws.com";

    public static void main(String[] args) throws IOException, InterruptedException {
        SdkHttpClient httpClient = ApacheHttpClient.builder().build();
        try {

            OpenSearchClient client = new OpenSearchClient(
                    new AwsSdk2Transport(
                            httpClient,
                            endpoint,
                            region,
                            AwsSdk2TransportOptions.builder().build()));

            // create the index
            String index = "sample-index";
            
            CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(index).build();
            client.indices().create(createIndexRequest);

            // add settings to the index
            IndexSettings indexSettings = new IndexSettings.Builder().autoExpandReplicas("0-all").build();
            PutIndicesSettingsRequest putSettingsRequest = new PutIndicesSettingsRequest.Builder()
                    .index(index)
                    .settings(indexSettings)
                    .build();
            client.indices().putSettings(putSettingsRequest);

            // index data
            IndexData indexData = new IndexData("Bruce", "Willis");
            IndexRequest<IndexData> indexDataRequest = new IndexRequest.Builder<IndexData>()
                    .index(index)
                    .id("1")
                    .document(indexData)
                    .build();
            client.index(indexDataRequest);

            // alternative using a hash map
            Map<String, Object> document = new HashMap<>();
            document.put("firstName", "Michael");
            document.put("lastName", "Douglas");
            IndexRequest documentIndexRequest = new IndexRequest.Builder()
                    .index(index)
                    .id("2")
                    .document(document)
                    .build();
            client.index(documentIndexRequest);

            // wait for the document to index
            Thread.sleep(3000);

            // search for the document
            SearchResponse<IndexData> searchResponse = client.search(s -> s.index(index), IndexData.class);
            for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
                System.out.println(searchResponse.hits().hits().get(i).source().toString());
            }

            // delete the document
            client.delete(b -> b.index(index).id("1"));

            // delete the index
            DeleteIndexRequest deleteRequest = new DeleteIndexRequest.Builder().index(index).build();
            client.indices().delete(deleteRequest);
            
        } finally {
            httpClient.close();
        }
    }
}