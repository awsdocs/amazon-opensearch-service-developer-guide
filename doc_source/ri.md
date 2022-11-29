# Reserved Instances in Amazon OpenSearch Service<a name="ri"></a>

Reserved Instances \(RIs\) in Amazon OpenSearch Service offer significant discounts compared to standard On\-Demand Instances\. The instances themselves are identical; RIs are just a billing discount applied to On\-Demand Instances in your account\. For long\-lived applications with predictable usage, RIs can provide considerable savings over time\.

OpenSearch Service RIs require one\- or three\-year terms and have three payment options that affect the discount rate:
+ **No Upfront** – You pay nothing upfront\. You pay a discounted hourly rate for every hour within the term\.
+ **Partial Upfront** – You pay a portion of the cost upfront, and you pay a discounted hourly rate for every hour within the term\.
+ **All Upfront** – You pay the entirety of the cost upfront\. You don't pay an hourly rate for the term\.

Generally speaking, a larger upfront payment means a larger discount\. You can't cancel Reserved Instances—when you reserve them, you commit to paying for the entire term—and upfront payments are nonrefundable\.

RIs are not flexible; they only apply to the exact instance type that you reserve\. For example, a reservation for eight `c5.2xlarge.search` instances does not apply to sixteen `c5.xlarge.search` instances or four `c5.4xlarge.search` instances\. For full details, see [Amazon OpenSearch Service pricing](https://aws.amazon.com/elasticsearch-service/pricing/) and [FAQ](https://aws.amazon.com/elasticsearch-service/faqs/)\.

**Topics**
+ [Purchasing Reserved Instances \(console\)](#ri-console)
+ [Purchasing Reserved Instances \(AWS CLI\)](#ri-cli)
+ [Purchasing Reserved Instances \(AWS SDKs\)](#ri-sdk)
+ [Examining costs](#ri-ce)

## Purchasing Reserved Instances \(console\)<a name="ri-console"></a>

The console lets you view your existing Reserved Instances and purchase new ones\.

**To purchase a reservation**

1. Go to [https://aws\.amazon\.com](https://aws.amazon.com), and then choose **Sign In to the Console**\.

1. Under **Analytics**, choose **Amazon OpenSearch Service**\.

1. Choose **Reserved Instance Leases** from the navigation pane\.

   On this page, you can view your existing reservations\. If you have many reservations, you can filter them to more easily identify and view a particular reservation\.
**Tip**  
If you don't see the **Reserved Instance Leases** link, [create a domain](createupdatedomains.md) in the AWS Region\.

1. Choose **Order Reserved Instance**\.

1. Provide a unique and descriptive name\.

1. Choose an instance type and the number of instances\. For guidance, see [Sizing Amazon OpenSearch Service domains](sizing-domains.md)\.

1. Choose a term length and payment option\. Review the payment details carefully\.

1. Choose **Next**\.

1. Review the purchase summary carefully\. Purchases of Reserved Instances are non\-refundable\.

1. Choose **Order**\.

## Purchasing Reserved Instances \(AWS CLI\)<a name="ri-cli"></a>

The AWS CLI has commands for viewing offerings, purchasing a reservation, and viewing your reservations\. The following command and sample response show the offerings for a given AWS Region:

```
aws opensearch describe-reserved-instance-offerings --region us-east-1
{
  "ReservedInstanceOfferings": [
    {
      "FixedPrice": x,
      "ReservedInstanceOfferingId": "1a2a3a4a5-1a2a-3a4a-5a6a-1a2a3a4a5a6a",
      "RecurringCharges": [
        {
          "RecurringChargeAmount": y,
          "RecurringChargeFrequency": "Hourly"
        }
      ],
      "UsagePrice": 0.0,
      "PaymentOption": "PARTIAL_UPFRONT",
      "Duration": 31536000,
      "InstanceType": "m4.2xlarge.search",
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
| ReservedInstanceOfferingId | The offering ID\. Make note of this value if you want to reserve the offering\. | 
| RecurringCharges | The hourly rate for the reservation\. | 
| UsagePrice | A legacy field\. For OpenSearch Service, this value is always 0\. | 
| PaymentOption | No Upfront, Partial Upfront, or All Upfront\. | 
| Duration | Length of the term in seconds:[\[See the AWS documentation website for more details\]](http://docs.aws.amazon.com/opensearch-service/latest/developerguide/ri.html) | 
| InstanceType | The instance type for the reservation\. For information about the hardware resources that are allocated to each instance type, see [Amazon OpenSearch Service pricing](https://aws.amazon.com/elasticsearch-service/pricing/)\. | 
| CurrencyCode | The currency for FixedPrice and RecurringChargeAmount\. | 

This next example purchases a reservation:

```
aws opensearch purchase-reserved-instance-offering --reserved-instance-offering-id 1a2a3a4a5-1a2a-3a4a-5a6a-1a2a3a4a5a6a --reservation-name my-reservation --instance-count 3 --region us-east-1
{
  "ReservationName": "my-reservation",
  "ReservedInstanceId": "9a8a7a6a-5a4a-3a2a-1a0a-9a8a7a6a5a4a"
}
```

Finally, you can list your reservations for a given Region using the following example:

```
aws opensearch describe-reserved-instances --region us-east-1
{
  "ReservedInstances": [
    {
      "FixedPrice": x,
      "ReservedInstanceOfferingId": "1a2a3a4a5-1a2a-3a4a-5a6a-1a2a3a4a5a6a",
      "ReservationName": "my-reservation",
      "PaymentOption": "PARTIAL_UPFRONT",
      "UsagePrice": 0.0,
      "ReservedInstanceId": "9a8a7a6a-5a4a-3a2a-1a0a-9a8a7a6a5a4a",
      "RecurringCharges": [
        {
          "RecurringChargeAmount": y,
          "RecurringChargeFrequency": "Hourly"
        }
      ],
      "State": "payment-pending",
      "StartTime": 1522872571.229,
      "InstanceCount": 3,
      "Duration": 31536000,
      "InstanceType": "m4.2xlarge.search",
      "CurrencyCode": "USD"
    }
  ]
}
```

**Note**  
`StartTime` is Unix epoch time, which is the number of seconds that have passed since midnight UTC of 1 January 1970\. For example, 1522872571 epoch time is 20:09:31 UTC of 4 April 2018\. You can use online converters\.

To learn more about the commands used in the preceding examples, see the [AWS CLI Command Reference](https://docs.aws.amazon.com/cli/latest/reference/es/index.html)\.

## Purchasing Reserved Instances \(AWS SDKs\)<a name="ri-sdk"></a>

The AWS SDKs \(except the Android and iOS SDKs\) support all the operations that are defined in the [Amazon OpenSearch Service API Reference](https://docs.aws.amazon.com/opensearch-service/latest/APIReference/Welcome.html), including the following:
+ `DescribeReservedInstanceOfferings`
+ `PurchaseReservedInstanceOffering`
+ `DescribeReservedInstances`

This sample script uses the [OpenSearchService](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/opensearch.html) low\-level Python client from the AWS SDK for Python \(Boto3\) to purchase Reserved Instances\. You must provide a value for `instance_type`\.

```
import boto3
from botocore.config import Config

# Build the client using the default credential configuration.
# You can use the CLI and run 'aws configure' to set access key, secret
# key, and default region.

my_config = Config(
    # Optionally lets you specify a region other than your default.
    region_name='us-east-1'
)

client = boto3.client('opensearch', config=my_config)

instance_type = '' # e.g. m4.2xlarge.search


def describe_RI_offerings(client):
    """Gets the Reserved Instance offerings for this account"""

    response = client.describe_reserved_instance_offerings()
    offerings = (response['ReservedInstanceOfferings'])
    return offerings


def check_instance(offering):
    """Returns True if instance type is the one you specified above"""

    if offering['InstanceType'] == instance_type:
        return True

    return False


def get_instance_id():
    """Iterates through the available offerings to find the ID of the one you specified"""

    instance_type_iterator = filter(
        check_instance, describe_RI_offerings(client))
    offering = list(instance_type_iterator)
    id = offering[0]['ReservedInstanceOfferingId']
    return id


def purchase_RI_offering(client):
    """Purchase Reserved Instances"""

    response = client.purchase_reserved_instance_offering(
        ReservedInstanceOfferingId = get_instance_id(),
        ReservationName = 'my-reservation',
        InstanceCount = 1
    )
    print('Purchased reserved instance offering of type ' + instance_type)
    print(response)


def main():
    """Purchase Reserved Instances"""
    purchase_RI_offering(client)
```

For more information about installing and using the AWS SDKs, see [AWS Software Development Kits](http://aws.amazon.com/code)\.

## Examining costs<a name="ri-ce"></a>

Cost Explorer is a free tool that you can use to view your spending data for the past 13 months\. Analyzing this data helps you identify trends and understand if RIs fit your use case\. If you already have RIs, you can [group by](https://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/groupdata.html) **Purchase Option** and [show amortized costs](https://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/advanced.html) to compare that spending to your spending for On\-Demand Instances\. You can also set [usage budgets](https://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/budgets-managing-costs.html) to make sure you are taking full advantage of your reservations\. For more information, see [Analyzing Your Costs with Cost Explorer](https://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/cost-explorer-what-is.html) in the *AWS Billing User Guide*\.