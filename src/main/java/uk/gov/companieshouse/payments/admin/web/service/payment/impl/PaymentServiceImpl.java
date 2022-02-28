package uk.gov.companieshouse.payments.admin.web.service.payment.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.payment.PaymentApi;
import uk.gov.companieshouse.payments.admin.web.api.ApiClientService;
import uk.gov.companieshouse.payments.admin.web.exception.ServiceException;
import uk.gov.companieshouse.payments.admin.web.service.payment.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final UriTemplate CREATE_BULK_REFUND =
            new UriTemplate("/admin/payments/bulk-refunds/govpay");

    @Autowired
    private ApiClientService apiClientService;

    @Override
    public void createBulkRefund(MultipartFile file)
        throws ServiceException {

        PaymentApi paymentApi = new PaymentApi();


        InternalApiClient internalApiClient = apiClientService.getPrivateApiClient();
        ApiResponse<PaymentApi> apiResponse;

        try {
            String uri = CREATE_BULK_REFUND.toString();
            apiResponse = internalApiClient.privatePayment().bulkRefund(uri, paymentApi).execute();
        } catch (ApiErrorResponseException ex) {
            throw new ServiceException("Error creating payments api request", ex);
        } catch (URIValidationException ex) {
            throw new ServiceException("Invalid URI for payments api request", ex);
        }
    }
}
