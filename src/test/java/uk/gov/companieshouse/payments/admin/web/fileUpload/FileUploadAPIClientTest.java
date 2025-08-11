package uk.gov.companieshouse.payments.admin.web.fileUpload;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.companieshouse.payments.admin.web.session.SessionService;

import jakarta.websocket.Session;

@ExtendWith(MockitoExtension.class)
class FileUploadAPIClientTest {

    private static final String DUMMY_URL = "http://test";

    private static final String GOVPAY = "govpay";

    private static final String PAYPAL = "paypal";

    @Captor
    private ArgumentCaptor<ResponseExtractor<ClientHttpResponse>> responseExtractorArgCaptor;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private FileUploadAPIClient fileUploadAPIClient;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private MultipartFile file;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(fileUploadAPIClient, "paymentsAPIUrl", DUMMY_URL);
        file = new MockMultipartFile("testFile", new byte[10]);
    }

    @Test
    void testUpload_success_govpay() {
        final ResponseEntity<FileUploadAPIResponse> apiResponse = apiSuccessResponse();

        when(sessionService.getUserToken()).thenReturn("1234");
        when(restTemplate.postForEntity(eq(DUMMY_URL + "/admin/payments/bulk-refunds/govpay"), any(), eq(FileUploadAPIResponse.class)))
                .thenReturn(apiResponse);

        FileUploadAPIClientResponse fileUploadAPIClientResponse = fileUploadAPIClient.upload(file, GOVPAY);

        assertEquals(HttpStatus.OK, fileUploadAPIClientResponse.getHttpStatus());
    }

    @Test
    void testUpload_success_paypal() {
        final ResponseEntity<FileUploadAPIResponse> apiResponse = apiSuccessResponse();

        when(sessionService.getUserToken()).thenReturn("1234");
        when(restTemplate.postForEntity(eq(DUMMY_URL + "/admin/payments/bulk-refunds/paypal"), any(), eq(FileUploadAPIResponse.class)))
                .thenReturn(apiResponse);

        FileUploadAPIClientResponse fileUploadAPIClientResponse = fileUploadAPIClient.upload(file, PAYPAL);

        assertEquals(HttpStatus.OK, fileUploadAPIClientResponse.getHttpStatus());
    }


    @Test
    void testUpload_ApiThrowsIOException() throws IOException {
        final ResponseEntity<FileUploadAPIResponse> apiErrorResponse = apiErrorResponse();

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenThrow(new IOException());

        FileUploadAPIClientResponse fileUploadAPIClientResponse = fileUploadAPIClient.upload(mockFile, GOVPAY);

        assertTrue(fileUploadAPIClientResponse.getHttpStatus().isError());
        assertThat(fileUploadAPIClientResponse.getHttpStatus(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    void testUpload_ApiReturnsError() {
        final ResponseEntity<FileUploadAPIResponse> apiErrorResponse = apiErrorResponse();

        when(restTemplate.postForEntity(eq(DUMMY_URL + "/admin/payments/bulk-refunds/govpay"), any(), eq(FileUploadAPIResponse.class))).thenReturn(apiErrorResponse);

        FileUploadAPIClientResponse fileUploadAPIClientResponse = fileUploadAPIClient.upload(file, GOVPAY);

        assertTrue(fileUploadAPIClientResponse.getHttpStatus().isError());
        assertEquals(apiErrorResponse.getStatusCode(), fileUploadAPIClientResponse.getHttpStatus());
        assertTrue(StringUtils.isBlank(fileUploadAPIClientResponse.getFileId()));
    }

    @Test
    void testUpload_ApiReturnsSuccessButNoResponseBody() {
        final ResponseEntity<FileUploadAPIResponse> apiResponse = apiSuccessButNoBodyResponse();

        when(restTemplate.postForEntity(eq(DUMMY_URL + "/admin/payments/bulk-refunds/govpay"), any(), eq(FileUploadAPIResponse.class)))
                .thenReturn(apiResponse);
        FileUploadAPIClientResponse fileUploadAPIClientResponse = fileUploadAPIClient.upload(file, GOVPAY);

        assertTrue(fileUploadAPIClientResponse.getHttpStatus().isError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, fileUploadAPIClientResponse.getHttpStatus());
        assertTrue(StringUtils.isBlank(fileUploadAPIClientResponse.getFileId()));
    }

    @Test
    void testUpload_GenericExceptionResponse() {

        when(restTemplate.postForEntity(eq(DUMMY_URL + "/admin/payments/bulk-refunds/govpay"), any(), eq(FileUploadAPIResponse.class))).thenReturn(null);

        FileUploadAPIClientResponse fileUploadAPIClientResponse = fileUploadAPIClient.upload(file, GOVPAY);

        assertTrue(fileUploadAPIClientResponse.getHttpStatus().isError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, fileUploadAPIClientResponse.getHttpStatus());
    }

    private ResponseEntity<FileUploadAPIResponse> apiSuccessResponse() {
        FileUploadAPIResponse response = new FileUploadAPIResponse();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ResponseEntity<FileUploadAPIResponse> apiSuccessButNoBodyResponse() {
        FileUploadAPIResponse response = null;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ResponseEntity<FileUploadAPIResponse> apiErrorResponse() {
        FileUploadAPIResponse response = new FileUploadAPIResponse();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
