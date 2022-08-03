package com.amazonaws.samples;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.InfoResponse;
import org.opensearch.client.transport.aws.AwsSdk2Transport;
import org.opensearch.client.transport.aws.AwsSdk2TransportOptions;

import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;

public class GetVersion {

    private static Region region = Region.US_WEST_2;
    private static final String endpoint = "search-...us-west-2.es.amazonaws.com";

    public static void main(String[] args) throws IOException {
        SdkHttpClient httpClient = ApacheHttpClient.builder().build();
        try {

            OpenSearchClient client = new OpenSearchClient(
                    new AwsSdk2Transport(
                            httpClient,
                            endpoint,
                            region,
                            AwsSdk2TransportOptions.builder().build()));

            InfoResponse info = client.info();
            System.out.println(info.version().distribution() + ": " + info.version().number());
        } finally {
            httpClient.close();
        }
    }
}