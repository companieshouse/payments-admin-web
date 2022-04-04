package uk.gov.companieshouse.payments.admin.web.service.payment.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.paymentsession.BulkRefundsApi;
import uk.gov.companieshouse.payments.admin.web.api.ApiClientService;
import uk.gov.companieshouse.payments.admin.web.exception.ServiceException;
import uk.gov.companieshouse.payments.admin.web.fileUpload.FileUploadAPIClient;
import uk.gov.companieshouse.payments.admin.web.service.payment.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private FileUploadAPIClient fileUploadAPIClient;

    @Autowired
    private ApiClientService apiClientService;

    private static final UriTemplate PROCESS_PENDING_URI =
            new UriTemplate("/admin/payments/bulk-refunds/process-pending");

    private static final UriTemplate GET_PENDING_URI =
            new UriTemplate("/admin/payments/bulk-refunds");

    @Override
    public void createBulkRefund(MultipartFile multipartFile)
            throws HttpClientErrorException {
        fileUploadAPIClient.upload(multipartFile);
    }

    @Override
    public void postProcessPendingRefunds() throws ServiceException {
        InternalApiClient internalApiClient = apiClientService.getInternalApiClient();

            try {
                String uri = PROCESS_PENDING_URI.toString();
                internalApiClient.privatePayment().processPendingRefunds(uri).execute();

            } catch (ApiErrorResponseException e) {
                throw new ServiceException("Error posting to process pending refunds", e);
            } catch (URIValidationException e) {
                throw new ServiceException("Invalid URI for processing pending refunds", e);
            }
    }

    @Override
    public int getPendingRefunds() throws ServiceException {
        InternalApiClient internalApiClient = apiClientService.getInternalApiClient();
        BulkRefundsApi bulkRefundsApi;

        try {
            String uri = GET_PENDING_URI.toString();
            bulkRefundsApi = internalApiClient.privatePayment().getPendingRefunds(uri).execute().getData();

            return bulkRefundsApi.getTotal();
        } catch (ApiErrorResponseException e) {
            throw new ServiceException("Error getting pending refunds", e);
        } catch (URIValidationException e) {
            throw new ServiceException("Invalid URI for getting pending refunds", e);
        }
    }
}
