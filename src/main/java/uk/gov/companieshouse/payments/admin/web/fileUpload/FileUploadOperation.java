package uk.gov.companieshouse.payments.admin.web.fileUpload;

import java.io.IOException;

/**
 * Functional interface for the file upload operation.
 */
@FunctionalInterface
public interface FileUploadOperation<T> {

    T execute() throws IOException;

}
