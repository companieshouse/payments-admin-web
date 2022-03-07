package uk.gov.companieshouse.payments.admin.web.fileUpload;

/**
 * Wrapper class for the info returned from the File Transfer API.
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
