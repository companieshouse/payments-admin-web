package uk.gov.companieshouse.payments.admin.web.exception;

public class MissingAnnotationException extends RuntimeException {

    public MissingAnnotationException(String message) {
        super(message);
    }
}
