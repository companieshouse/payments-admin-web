# payments-admin-web
Web service for use by internal finance users to manage payments

## Running the service
The payments admin service is part of the chs-docker-development environment. It can be found in the platform module.

### Access

Users must be signed in and hold the `/admin/payments-bulk-refunds` permission. The permission is checked before the request reaches a controller; users without it receive a 404 "Page not found" response. The healthcheck endpoint is not restricted.

#### Granting access in Docker CHS development

Open a MongoDB shell from the `docker-chs-development` directory:

```bash
docker compose exec mongo mongosh
# Alternatively:
docker exec docker-chs-development-mongo-1 mongosh
```

In the shell, select the `account` database and look up your account and the Entra group mapped to the refund permission:

```javascript
use account

const userEmail = "demo@ch.gov.uk"; // Or your signed-in user's email

db.users.findOne(
  { email: userEmail },
  { email: 1, admin_user: 1, roles: 1 }
)

db.admin_permissions.find(
  { permissions: "/admin/payments-bulk-refunds" },
  { group_name: 1, entra_group_id: 1, permissions: 1 }
)
```

The local mapping includes the `chs admin bulk refunds` Entra group. To grant the permission to an existing local test account, add that group's Entra ID to the account's `roles` array and set `admin_user` to `true`:

```javascript
const refundGroup = db.admin_permissions.findOne(
  {
    group_name: "chs admin bulk refunds",
    permissions: "/admin/payments-bulk-refunds"
  },
  { entra_group_id: 1 }
)

if (!refundGroup) {
  throw new Error("The refund permission group was not found")
}

const result = db.users.updateOne(
  { email: userEmail },
  {
    $set: { admin_user: true },
    $addToSet: { roles: refundGroup.entra_group_id }
  }
)

printjson(result)
```

Set `userEmail` to either `demo@ch.gov.uk` or your signed-in user's email. Check that `matchedCount` is `1`; if it is `0`, sign in once to create the account before retrying. `$addToSet` preserves the account's other roles. Sign out and back in after the update to refresh the session. These changes apply only to the local Docker MongoDB; do not run them against shared test or staging databases.

### Build

Requires Java 21 and Maven.

```bash
make build
```

### Test

```bash
mvn test
mvn test -Dtest=UserPermissionInterceptorTests   # a single test class
```

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
8. The refund xml to be uploaded in the above mentioned urls, will be provided by Finance Team.
9. We need to provide data to the Finance team, which they would convert into Refund xml.
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
