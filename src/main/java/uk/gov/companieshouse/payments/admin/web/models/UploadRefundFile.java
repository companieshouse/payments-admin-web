package uk.gov.companieshouse.payments.admin.web.models;


import org.springframework.web.multipart.MultipartFile;
import uk.gov.companieshouse.payments.admin.web.annotation.RefundFile;

public class UploadRefundFile {

    /**
     * Returns false if the file is empty, too big, or not XML
     */
    @RefundFile()
    private MultipartFile refundFile;

    public MultipartFile getrefundFile() {
        return refundFile;
    }

    public void setrefundFile(MultipartFile refundFile) {
        this.refundFile = refundFile;
    }

}
