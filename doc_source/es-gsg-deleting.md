# Step 4: Delete an Amazon ES Domain<a name="es-gsg-deleting"></a>

Because the *movies* domain from this tutorial is for test purposes, you should delete it when you are finished experimenting to avoid incurring charges\.

**To delete an Amazon ES domain \(console\)**

1. Log in to the **Amazon Elasticsearch Service** console\.

1. In the navigation pane, under **My domains**, choose the *movies* domain\.

1. Choose **Delete Elasticsearch domain**\.

1. Choose **Delete domain\.**

1. Select the **Delete the domain** check box, and then choose **Delete**\.

**To delete an Amazon ES domain \(**AWS CLI****\)

+ Run the following command to delete the *movies* domain:

  ```
  aws es delete-elasticsearch-domain --domain-name movies
  ```

**Note**  
Deleting a domain deletes all billable Amazon ES resources\. However, any manual snapshots of the domain that you created using the native Elasticsearch API are not deleted\. Consider saving a snapshot if you might need to recreate the Amazon ES domain in the future\. If you don't plan to recreate the domain, you can safely delete any snapshots that you created manually\.

**To delete an Amazon ES domain \(AWS SDKs\)**

The AWS SDKs \(except the Android and iOS SDKs\) support all the actions defined in the [Amazon ES Configuration API Reference](es-configuration-api.md), including the `DeleteElasticsearchDomain` action\. For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\.