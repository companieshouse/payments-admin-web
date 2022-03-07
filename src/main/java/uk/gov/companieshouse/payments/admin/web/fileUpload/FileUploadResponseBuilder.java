package uk.gov.companieshouse.payments.admin.web.fileUpload;

import java.io.IOException;

/**
 * Functional interface for the file transfer response builder.
 */
@FunctionalInterface
public interface FileUploadResponseBuilder<T> {

   FileUploadAPIClientResponse createResponse(T input) throws IOException;

}
