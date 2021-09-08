# Using the AWS SDKs to interact with Amazon OpenSearch Service<a name="configuration-samples"></a>

This section includes examples of how to use the AWS SDKs to interact with the Amazon OpenSearch Service configuration API\. These code samples show how to create, update, and delete OpenSearch Service domains\.

**Important**  
For examples of how to interact with the OpenSearch APIs, such as `_index`, `_bulk`, `_search`, and `_snapshot`, see [Signing HTTP requests to Amazon OpenSearch Service](request-signing.md)\.

## Java<a name="configuration-samples-java"></a>

This example uses the AWS SDK for Java to create a domain, update its configuration, and delete it\. Uncomment the calls to `waitForDomainProcessing` \(and comment the call to `deleteDomain`\) to allow the domain to come online and be useable\.

```
package com.amazonaws.samples;

import java.util.concurrent.TimeUnit;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.opensearch.AmazonOpenSearch;
import com.amazonaws.services.opensearch.AmazonOpenSearchClientBuilder;
import com.amazonaws.services.opensearch.model.CreateDomainRequest;
import com.amazonaws.services.opensearch.model.CreateDomainResult;
import com.amazonaws.services.opensearch.model.DeleteDomainRequest;
import com.amazonaws.services.opensearch.model.DeleteDomainResult;
import com.amazonaws.services.opensearch.model.DescribeDomainRequest;
import com.amazonaws.services.opensearch.model.DescribeDomainResult;
import com.amazonaws.services.opensearch.model.EBSOptions;
import com.amazonaws.services.opensearch.model.ClusterConfig;
import com.amazonaws.services.opensearch.model.NodeToNodeEncryptionOptions;
import com.amazonaws.services.opensearch.model.ResourceNotFoundException;
import com.amazonaws.services.opensearch.model.SnapshotOptions;
import com.amazonaws.services.opensearch.model.UpdateDomainConfigRequest;
import com.amazonaws.services.opensearch.model.UpdateDomainConfigResult;
import com.amazonaws.services.opensearch.model.VolumeType;

/**
 * Sample class demonstrating how to use the Amazon Web Services SDK for Java to create, update,
 * and delete Amazon OpenSearch Service domains.
 */

public class OpenSearchSample {

    public static void main(String[] args) {

        final String domainName = "my-test-domain";

        // Build the client using the default credentials chain.
        // You can use the CLI and run `aws configure` to set access key, secret
        // key, and default region.
        final AmazonOpenSearch client = AmazonOpenSearchClientBuilder
                .standard()
                // Unnecessary, but lets you use a region different than your default.
                .withRegion(Regions.US_WEST_2)
                // Unnecessary, but if desired, you can use a different provider chain.
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();

        // Create a new domain, update its configuration, and delete it.
        createDomain(client, domainName);
        // waitForDomainProcessing(client, domainName);
        updateDomain(client, domainName);
        // waitForDomainProcessing(client, domainName);
        deleteDomain(client, domainName);
    }

    /**
     * Creates an Amazon OpenSearch Service domain with the specified options.
     * Some options require other Amazon Web Services resources, such as an Amazon Cognito user pool
     * and identity pool, whereas others require just an instance type or instance
     * count.
     *
     * @param client
     *            The client to use for the requests to Amazon OpenSearch Service
     * @param domainName
     *            The name of the domain you want to create
     */
    private static void createDomain(final AmazonOpenSearch client, final String domainName) {

        // Create the request and set the desired configuration options
        CreateDomainRequest createRequest = new CreateDomainRequest()
                .withDomainName(domainName)
                .withEngineVersion("OpenSearch_1.0")
                .withClusterConfig(new ClusterConfig()
                        .withDedicatedMasterEnabled(true)
                        .withDedicatedMasterCount(3)
                        // Small, inexpensive instance types for testing. Not recommended for production
                        // domains.
                        .withDedicatedMasterType("t2.small.search")
                        .withInstanceType("t2.small.search")
                        .withInstanceCount(5))
                // Many instance types require EBS storage.
                .withEBSOptions(new EBSOptions()
                        .withEBSEnabled(true)
                        .withVolumeSize(10)
                        .withVolumeType(VolumeType.Gp2))
                // You can uncomment this line and add your account ID, a user name, and the
                // domain name to add an access policy.
                // .withAccessPolicies("{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"arn:aws:iam::123456789012:user/user-name\"]},\"Action\":[\"es:*\"],\"Resource\":\"arn:aws:es:region:123456789012:domain/domain-name/*\"}]}")
                .withNodeToNodeEncryptionOptions(new NodeToNodeEncryptionOptions()
                        .withEnabled(true));

        // Make the request.
        System.out.println("Sending domain creation request...");
        CreateDomainResult createResponse = client.createDomain(createRequest);
        System.out.println("Domain creation response from Amazon OpenSearch Service:");
        System.out.println(createResponse.getDomainStatus().toString());
    }

    /**
     * Updates the configuration of an Amazon OpenSearch Service domain with the
     * specified options. Some options require other Amazon Web Services resources, such as an
     * Amazon Cognito user pool and identity pool, whereas others require just an
     * instance type or instance count.
     *
     * @param client
     *            The client to use for the requests to Amazon OpenSearch Service
     * @param domainName
     *            The name of the domain to update
     */
    private static void updateDomain(final AmazonOpenSearch client, final String domainName) {
        try {
            // Updates the domain to use three data instances instead of five.
            // You can uncomment the Cognito lines and fill in the strings to enable Cognito
            // authentication for OpenSearch Dashboards.
            final UpdateDomainConfigRequest updateRequest = new UpdateDomainConfigRequest()
                    .withDomainName(domainName)
                    // .withCognitoOptions(new CognitoOptions()
                            // .withEnabled(true)
                            // .withUserPoolId("user-pool-id")
                            // .withIdentityPoolId("identity-pool-id")
                            // .withRoleArn("role-arn"))
                    .withClusterConfig(new ClusterConfig()
                            .withInstanceCount(3));

            System.out.println("Sending domain update request...");
            final UpdateDomainConfigResult updateResponse = client
                    .updateDomainConfig(updateRequest);
            System.out.println("Domain update response from Amazon OpenSearch Service:");
            System.out.println(updateResponse.toString());
        } catch (ResourceNotFoundException e) {
            System.out.println("Domain not found. Please check the domain name.");
        }
    }

    /**
     * Deletes an Amazon OpenSearch Service domain. Deleting a domain can take
     * several minutes.
     *
     * @param client
     *            The client to use for the requests to Amazon OpenSearch Service
     * @param domainName
     *            The name of the domain that you want to delete
     */
    private static void deleteDomain(final AmazonOpenSearch client, final String domainName) {
        try {
            final DeleteDomainRequest deleteRequest = new DeleteDomainRequest()
                    .withDomainName(domainName);

            System.out.println("Sending domain deletion request...");
            final DeleteDomainResult deleteResponse = client.deleteDomain(deleteRequest);
            System.out.println("Domain deletion response from Amazon OpenSearch Service:");
            System.out.println(deleteResponse.toString());
        } catch (ResourceNotFoundException e) {
            System.out.println("Domain not found. Please check the domain name.");
        }
    }

    /**
     * Waits for the domain to finish processing changes. New domains typically take 15-30 minutes
     * to initialize, but can take longer depending on the configuration. Most updates to existing domains
     * take a similar amount of time. This method checks every 15 seconds and finishes only when
     * the domain's processing status changes to false.
     *
     * @param client
     *            The client to use for the requests to Amazon OpenSearch Service
     * @param domainName
     *            The name of the domain that you want to check
     */
    private static void waitForDomainProcessing(final AmazonOpenSearch client, final String domainName) {
        // Create a new request to check the domain status.
        final DescribeDomainRequest describeRequest = new DescribeDomainRequest()
                .withDomainName(domainName);

        // Every 15 seconds, check whether the domain is processing.
        DescribeDomainResult describeResponse = client.describeDomain(describeRequest);
        while (describeResponse.getDomainStatus().isProcessing()) {
            try {
                System.out.println("Domain still processing...");
                TimeUnit.SECONDS.sleep(15);
                describeResponse = client.describeDomain(describeRequest);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Once we exit that loop, the domain is available
        System.out.println("Amazon OpenSearch Service has finished processing changes for your domain.");
        System.out.println("Domain description response from Amazon OpenSearch Service:");
        System.out.println(describeResponse.toString());
    }
}
```