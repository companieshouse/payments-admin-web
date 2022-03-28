package uk.gov.companieshouse.payments.admin.web.service.payment;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.companieshouse.payments.admin.web.exception.ServiceException;

public interface PaymentService {

    void createBulkRefund (MultipartFile file)
            throws HttpClientErrorException;

    void processPendingRefunds()
        throws ServiceException;
}
