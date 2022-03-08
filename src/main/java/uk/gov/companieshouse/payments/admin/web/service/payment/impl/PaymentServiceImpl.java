package uk.gov.companieshouse.payments.admin.web.service.payment.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.payments.admin.web.Application;
import uk.gov.companieshouse.payments.admin.web.exception.ServiceException;
import uk.gov.companieshouse.payments.admin.web.fileUpload.FileUploadAPIClient;
import uk.gov.companieshouse.payments.admin.web.service.payment.PaymentService;

import java.io.IOException;

@Service
public class PaymentServiceImpl implements PaymentService {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(Application.APPLICATION_NAME_SPACE);

    private static final UriTemplate CREATE_BULK_REFUND =
            new UriTemplate("/admin/payments/bulk-refunds/govpay");

    private static final String HEADER_API_KEY = "x-api-key";

    @Autowired
    private FileUploadAPIClient fileUploadAPIClient;

    @Override
    public void createBulkRefund(MultipartFile multipartFile)
            throws ServiceException, IOException {

        fileUploadAPIClient.upload(multipartFile);

        /*
        try {
            String uri = CREATE_BULK_REFUND.toString();
            apiResponse = internalApiClient.privatePayment().bulkRefund(uri, file).execute();
        } catch (ApiErrorResponseException ex) {
            throw new ServiceException("Error creating payments api request", ex);
        } catch (URIValidationException ex) {
            throw new ServiceException("Invalid URI for payments api request", ex);
        }
         */
    }
}
