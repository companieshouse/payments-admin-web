package uk.gov.companieshouse.payments.admin.web.validation;

import org.springframework.web.multipart.MultipartFile;
import uk.gov.companieshouse.payments.admin.web.annotation.RefundFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RefundFileValidator implements ConstraintValidator<RefundFile, MultipartFile> {

    @Override
    public void initialize(RefundFile refundFile) {
        // No initialization required for this validator
    }

    @Override
    public boolean isValid(MultipartFile refundFile, ConstraintValidatorContext context) {

        // Return false if no file uploaded
        if (refundFile == null || refundFile.getSize() == 0) {
            return false;
        }

        String originalFilename = refundFile.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }
        String[] parts = originalFilename.split("\\.");
        if (parts.length < 2) {
            return false;
        }
        return parts[1].equals("xml") && refundFile.getSize() < 512 * 1024;
    }
}
