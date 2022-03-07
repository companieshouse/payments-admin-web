package uk.gov.companieshouse.payments.admin.web.fileUpload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.companieshouse.payments.admin.web.session.SessionService;

import java.io.IOException;
import java.util.Map;

@Component
public class FileUploadAPIClient {

    private static final String CONTENT_DISPOSITION_VALUE = "form-data; name=%s; filename=%s";

    private static final String FILE = "file";

    private static final String AUTHORIZATION = "authorization";

    private RestTemplate restTemplate;

    @Autowired
    private SessionService sessionService;



    @Value("${payments.api.url}")
    private String paymentsAPIUrl;

    private <T> FileUploadAPIClientResponse makeApiCall(FileUploadOperation<T> operation, FileUploadResponseBuilder<T> responseBuilder) {
        FileUploadAPIClientResponse response = new FileUploadAPIClientResponse();

        try {
            T operationResponse = operation.execute();

            response = responseBuilder.createResponse(operationResponse);

        } catch (IOException ex) {
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }


    /**
     * Uploads a file to the file-transfer-api
     * Creates a multipart form request containing the file and sends to
     * the file-transfer-api. The response from the file-transfer-api contains
     * the new unique id for the file. This is captured and returned in the FileTransferApiClientResponse.
     *
     * @param fileToUpload The file to upload
     * @return FileTransferApiClientResponse containing the file id if successful, and http status
     */
    public FileUploadAPIClientResponse upload(final MultipartFile fileToUpload) {
        restTemplate = new RestTemplate();
        return makeApiCall(
                // FileUploadOperation
                () -> {
                    HttpHeaders headers = createFileUploadAPIHttpHeaders();
                    LinkedMultiValueMap<String, String> fileHeaderMap = createUploadFileHeader(fileToUpload);
                    HttpEntity<byte[]> fileHttpEntity = new HttpEntity<>(fileToUpload.getBytes(), fileHeaderMap);
                    LinkedMultiValueMap<String, Object> body = createUploadBody(fileHttpEntity);
                    HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
                    return restTemplate.postForEntity(paymentsAPIUrl + "/admin/payments/bulk-refunds/govpay", requestEntity, FileUploadAPIResponse.class);
                },

                // FileTransferResponseBuilder - the output from FileTransferOperation is the
                // input into this FileTransferResponseBuilder
                // TODO
                responseEntity -> {
                    FileUploadAPIClientResponse fileTransferApiClientResponse = new FileUploadAPIClientResponse();
                    if (responseEntity != null) {
                        fileTransferApiClientResponse.setHttpStatus(responseEntity.getStatusCode());
                        FileUploadAPIResponse apiResponse = responseEntity.getBody();
                        if (apiResponse != null) {
                            fileTransferApiClientResponse.setFileId(apiResponse.getId());
                        } else {
                            fileTransferApiClientResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    } else {
                        fileTransferApiClientResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    return fileTransferApiClientResponse;
                }

        );
    }

    private HttpHeaders createFileUploadAPIHttpHeaders() {
        HttpHeaders headers = createOAuth2Header();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    private HttpHeaders createOAuth2Header() {
        HttpHeaders headers = new HttpHeaders();
        String userToken = sessionService.getUserToken();
        headers.add(AUTHORIZATION, "Bearer " + userToken);
        return headers;

    }

    private LinkedMultiValueMap<String, String> createUploadFileHeader(final MultipartFile fileToUpload) {
        LinkedMultiValueMap<String, String> fileHeaderMap = new LinkedMultiValueMap<>();
        fileHeaderMap.add(HttpHeaders.CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_VALUE, FILE, fileToUpload.getOriginalFilename()));
        return fileHeaderMap;
    }


    private LinkedMultiValueMap<String, Object> createUploadBody(final HttpEntity<byte[]> fileHttpEntity) {
        LinkedMultiValueMap<String, Object> multipartReqMap = new LinkedMultiValueMap<>();
        multipartReqMap.add(FILE, fileHttpEntity);
        return multipartReqMap;
    }
}
