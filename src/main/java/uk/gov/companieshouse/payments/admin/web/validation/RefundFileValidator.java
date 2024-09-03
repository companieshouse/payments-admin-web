package uk.gov.companieshouse.payments.admin.web.validation;

import org.springframework.web.multipart.MultipartFile;
import uk.gov.companieshouse.payments.admin.web.annotation.RefundFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RefundFileValidator implements ConstraintValidator<RefundFile, MultipartFile> {

    @Override
    public void initialize(RefundFile refundFile) {}

    @Override
    public boolean isValid(MultipartFile refundFile, ConstraintValidatorContext context) {

        // Return false if no file uploaded
        if (refundFile == null || refundFile.getSize() == 0) {
            return false;
        }

        // Return false if file is not XML and bigger than file size limit (512kb * 1024bytes)
        return refundFile.getOriginalFilename().split("\\.")[1].equals("xml") && refundFile.getSize() < 512 * 1024;
    }
}
