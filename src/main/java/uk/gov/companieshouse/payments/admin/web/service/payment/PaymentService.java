package uk.gov.companieshouse.payments.admin.web.service.payment;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

public interface PaymentService {

    void createBulkRefund (MultipartFile file)
            throws HttpClientErrorException;
}
