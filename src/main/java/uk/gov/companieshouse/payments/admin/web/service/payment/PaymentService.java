package uk.gov.companieshouse.payments.admin.web.service.payment;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.companieshouse.payments.admin.web.exception.ServiceException;

public interface PaymentService {

    void createBulkRefund (MultipartFile file, String selectedBulkRefundType)
            throws HttpClientErrorException;

    void postProcessPendingRefunds()
        throws ServiceException;

    int getPendingRefunds()
            throws ServiceException;
}
