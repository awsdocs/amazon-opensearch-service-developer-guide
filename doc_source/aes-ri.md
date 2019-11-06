# Amazon Elasticsearch Service Reserved Instances<a name="aes-ri"></a>

Amazon Elasticsearch Service Reserved Instances \(RIs\) offer significant discounts compared to standard On\-Demand Instances\. The instances themselves are identical; RIs are just a billing discount applied to On\-Demand Instances in your account\. For long\-lived applications with predictable usage, RIs can provide considerable savings over time\.

Amazon ES RIs require one\- or three\-year terms and have three payment options that affect the discount rate:
+ **No Upfront** – You pay nothing upfront\. You pay a discounted hourly rate for every hour within the term\.
+ **Partial Upfront** – You pay a portion of the cost upfront, and you pay a discounted hourly rate for every hour within the term\.
+ **All Upfront** – You pay the entirety of the cost upfront\. You don't pay an hourly rate for the term\.

Generally speaking, a larger upfront payment means a larger discount\. You can't cancel Reserved Instances—when you reserve them, you commit to paying for the entire term—and upfront payments are nonrefundable\. For full details, see [Amazon Elasticsearch Service Pricing](https://aws.amazon.com/elasticsearch-service/pricing/) and [FAQ](https://aws.amazon.com/elasticsearch-service/faqs/)\.

**Topics**
+ [Purchasing Reserved Instances \(Console\)](#aes-ri-console)
+ [Purchasing Reserved Instances \(AWS CLI\)](#aes-ri-cli)
+ [Purchasing Reserved Instances \(AWS SDKs\)](#aes-ri-sdk)
+ [Examining Costs](#aes-ri-ce)

## Purchasing Reserved Instances \(Console\)<a name="aes-ri-console"></a>

The console lets you view your existing Reserved Instances and purchase new ones\.

**To purchase a reservation**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Elasticsearch Service**\.

1. Choose **Reserved Instances**\.

   On this page, you can view your existing reservations\. If you have many reservations, you can filter them to more easily identify and view a particular reservation\.
**Tip**  
If you don't see the **Reserved Instances** link, [create a domain](es-createupdatedomains.md) in the region\.

1. Choose **Purchase Reserved Instance**\.

1. For **Reservation Name**, type a unique, descriptive name\.

1. Choose an instance type, size, and number of instances\. For guidance, see [Sizing Amazon ES Domains](sizing-domains.md)\.

1. Choose a term length and payment option\.

1. Review the payment details carefully\.

1. Choose **Submit**\.

1. Review the purchase summary carefully\. Purchases of Reserved Instances are non\-refundable\.

1. Choose **Purchase**\.

## Purchasing Reserved Instances \(AWS CLI\)<a name="aes-ri-cli"></a>

The AWS CLI has commands for viewing offerings, purchasing a reservation, and viewing your reservations\. The following command and sample response show the offerings for a given AWS Region:

```
aws es describe-reserved-elasticsearch-instance-offerings --region us-east-1
{
  "ReservedElasticsearchInstanceOfferings": [
    {
      "FixedPrice": x,
      "ReservedElasticsearchInstanceOfferingId": "1a2a3a4a5-1a2a-3a4a-5a6a-1a2a3a4a5a6a",
      "RecurringCharges": [
        {
          "RecurringChargeAmount": y,
          "RecurringChargeFrequency": "Hourly"
        }
      ],
      "UsagePrice": 0.0,
      "PaymentOption": "PARTIAL_UPFRONT",
      "Duration": 31536000,
      "ElasticsearchInstanceType": "m4.2xlarge.elasticsearch",
      "CurrencyCode": "USD"
    }
  ]
}
```

For an explanation of each return value, see the following table\.


****  

| Field | Description | 
| --- | --- | 
| FixedPrice | The upfront cost of the reservation\. | 
| ReservedElasticsearchInstanceOfferingId | The offering ID\. Make note of this value if you want to reserve the offering\. | 
| RecurringCharges | The hourly rate for the reservation\. | 
| UsagePrice | A legacy field\. For Amazon ES, this value is always 0\. | 
| PaymentOption | No Upfront, Partial Upfront, or All Upfront\. | 
| Duration | Length of the term in seconds:[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/aes-ri.html) | 
| ElasticsearchInstanceType | The instance type for the reservation\. For information about the hardware resources that are allocated to each instance type, see [Amazon Elasticsearch Service Pricing](https://aws.amazon.com/elasticsearch-service/pricing/)\. | 
| CurrencyCode | The currency for FixedPrice and RecurringChargeAmount\. | 

This next example purchases a reservation:

```
aws es purchase-reserved-elasticsearch-instance-offering --reserved-elasticsearch-instance-offering-id 1a2a3a4a5-1a2a-3a4a-5a6a-1a2a3a4a5a6a --reservation-name my-reservation --instance-count 3 --region us-east-1
{
  "ReservationName": "my-reservation",
  "ReservedElasticsearchInstanceId": "9a8a7a6a-5a4a-3a2a-1a0a-9a8a7a6a5a4a"
}
```

Finally, you can list your reservations for a given region using the following example:

```
aws es describe-reserved-elasticsearch-instances --region us-east-1
{
  "ReservedElasticsearchInstances": [
    {
      "FixedPrice": x,
      "ReservedElasticsearchInstanceOfferingId": "1a2a3a4a5-1a2a-3a4a-5a6a-1a2a3a4a5a6a",
      "ReservationName": "my-reservation",
      "PaymentOption": "PARTIAL_UPFRONT",
      "UsagePrice": 0.0,
      "ReservedElasticsearchInstanceId": "9a8a7a6a-5a4a-3a2a-1a0a-9a8a7a6a5a4a",
      "RecurringCharges": [
        {
          "RecurringChargeAmount": y,
          "RecurringChargeFrequency": "Hourly"
        }
      ],
      "State": "payment-pending",
      "StartTime": 1522872571.229,
      "ElasticsearchInstanceCount": 3,
      "Duration": 31536000,
      "ElasticsearchInstanceType": "m4.2xlarge.elasticsearch",
      "CurrencyCode": "USD"
    }
  ]
}
```

**Note**  
`StartTime` is Unix epoch time, which is the number of seconds that have passed since midnight UTC of 1 January 1970\. For example, 1522872571 epoch time is 20:09:31 UTC of 4 April 2018\. You can use online converters\.

To learn more about the commands used in the preceding examples, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/es/index.html)\.

## Purchasing Reserved Instances \(AWS SDKs\)<a name="aes-ri-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all the operations that are defined in the [Amazon ES Configuration API Reference](es-configuration-api.md), including the following:
+ `DescribeReservedElasticsearchInstanceOfferings`
+ `PurchaseReservedElasticsearchInstanceOffering`
+ `DescribeReservedElasticsearchInstances`

For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\.

## Examining Costs<a name="aes-ri-ce"></a>

Cost Explorer is a free tool that you can use to view your spending data for the past 13 months\. Analyzing this data helps you identify trends and understand if RIs fit your use case\. If you already have RIs, you can [group by](https://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/groupdata.html) **Purchase Option** and [show amortized costs](https://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/advanced.html) to compare that spending to your spending for On\-Demand Instances\. You can also set [usage budgets](https://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/budgets-managing-costs.html) to make sure you are taking full advantage of your reservations\. For more information, see [Analyzing Your Costs with Cost Explorer](https://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/cost-explorer-what-is.html) in the *AWS Billing and Cost Management User Guide*\.