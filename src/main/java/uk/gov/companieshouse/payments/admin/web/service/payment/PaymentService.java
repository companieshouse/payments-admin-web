package uk.gov.companieshouse.payments.admin.web.service.payment;

import org.springframework.web.multipart.MultipartFile;
import uk.gov.companieshouse.payments.admin.web.exception.ServiceException;

import java.io.IOException;

public interface PaymentService {

    void createBulkRefund (MultipartFile file)
            throws ServiceException, IOException;
}
