package uk.gov.companieshouse.payments.admin.web.fileUpload;

/**
 * Wrapper class for the info returned from the File Upload API request.
 */
public class FileUploadAPIResponse {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
