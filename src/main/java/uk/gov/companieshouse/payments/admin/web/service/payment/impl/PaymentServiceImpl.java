package uk.gov.companieshouse.payments.admin.web.service.payment.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.companieshouse.payments.admin.web.fileUpload.FileUploadAPIClient;
import uk.gov.companieshouse.payments.admin.web.service.payment.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private FileUploadAPIClient fileUploadAPIClient;

    @Override
    public void createBulkRefund(MultipartFile multipartFile)
            throws HttpClientErrorException {
        fileUploadAPIClient.upload(multipartFile);
    }
}
