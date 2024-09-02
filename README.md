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
