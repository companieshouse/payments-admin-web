# payments-admin-web
Web service for use by internal finance users to manage payments

## Running the service
The payments admin service is part of the chs-docker-development environment. It can be found in the platform module.

### Endpoints

| Method | Path                                                                  | Description                                                 |
|--------|-----------------------------------------------------------------------|-------------------------------------------------------------|
| GET    | `/admin/payments/refunds`, `/`                                        | Payment refund upload page                                  |
| POST   | `/admin/payments/refunds`, `/`                                        | Handle payment refund upload                                |
| GET    | `/admin/payments/summary`                                             | Show pending refunds if any                                 |
| POST   | `/admin/payments/summary`                                             | Retry processing pending refunds                            |
| GET    | `/admin/payments/healthcheck`                                         | Healthcheck                                                 |



# TESTING 

1. Need to have access to test dashboard and paypal #servicenowRequest
2. After access, check that you have access to "Companies House"-Test Account(Sandbox), "Make a Payment to Companies House" - Test Account(Sandbox) in https://selfservice.payments.service.gov.uk/my-services.
3. Same Url is for both Cidev and Staging.
4. Any transaction which involves card payment through CHS will reflect here "Companies House"-Test Account(Sandbox).
5. Any webfiling submission and card payment(recommended Mortgage for testing purpose) will reflect in "Make a Payment to Companies House" - Test Account(Sandbox).
6. For card payments, the refund process can be initiated here - https://admin-web.cidev.aws.chdev.org/admin/payments/refunds, https://admin-web-staging.company-information.service.gov.uk/admin/payments/refunds.
7. The refund has to be initiated via admin login only. 
8. The refund xml to be uploaded in the above mentioned urls, will be provided by Finance Team or you can update the existing xml from https://companieshouse.atlassian.net/jira/software/c/projects/CC/boards/598?selectedIssue=CC-2996,
    ->Update refund reference and orderCode with provide_id.
    ->Update the amount value with refund value.
    ->Update batchCode value with _id.
9. We need to provide data to the Finance team, which they would convert into Refund xml or use the updated XML file created above.
10. The data to be provided are - 
Date and Time of Submission	- This can be fetched from transactions in https://selfservice.payments.service.gov.uk/my-services.
Payment Reference Number	- This is the Reference number in https://selfservice.payments.service.gov.uk/my-services.
Webfiling Submission Number	Amount	- This is the amount paid
FormType	- For Webfiling Mortgage , it is 11101(fetched from Payments in Mongo DB). The form types can be found in https://github.com/companieshouse/payment-reconciliation-consumer/blob/master/assets/product_code.yml#L1
Customer Email Address	- Your email address
PSP ID	- CHS payment - > Provider Id in https://selfservice.payments.service.gov.uk/my-services.
        - Paypal payment - > This can be found in the paypal logs where the Id in "payments": {
        "captures": [
          {
            "id": "xxxxxxxxPSP ID",
Payment Method - Card/Paypal
11. Validate the refunds are reflected in Mongo in the Payments Collection.
You can also refer to - https://companieshouse.atlassian.net/browse/JU-566

## PAYPAL:
Log in to your paypal account.
Scroll to the bottom of the page and click on Developers.
Click on Go to Dashboard - Developer Dashboard
Click on Event Logs - API logs
Search for your transaction.
The Captures:Id which is the PPS ID can be found in the Response of POST /v2/checkout/orders/xxxxxx/capture
