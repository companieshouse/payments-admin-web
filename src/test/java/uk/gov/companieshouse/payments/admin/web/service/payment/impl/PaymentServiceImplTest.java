package uk.gov.companieshouse.payments.admin.web.service.payment.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.payment.PrivatePaymentResourceHandler;
import uk.gov.companieshouse.api.handler.payment.request.PaymentGetPendingRefunds;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.paymentsession.BulkRefundsApi;
import uk.gov.companieshouse.payments.admin.web.api.ApiClientService;
import uk.gov.companieshouse.payments.admin.web.exception.ServiceException;
import uk.gov.companieshouse.payments.admin.web.service.payment.PaymentService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentServiceImplTest {

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private PrivatePaymentResourceHandler privatePaymentResourceHandler;

    @Mock
    private PaymentGetPendingRefunds paymentGetPendingRefunds;

    @Mock
    private ApiResponse<BulkRefundsApi> responseWithData;

    @Mock
    private BulkRefundsApi bulkRefundsApi;

    @Mock
    private uk.gov.companieshouse.payments.admin.web.fileUpload.FileUploadAPIClient fileUploadAPIClient;

    @InjectMocks
    private PaymentService mockPaymentService = new PaymentServiceImpl();

    @Test
    @DisplayName("Get Pending Refunds - Success Path")
    void getPendingRefundsSuccess() throws ServiceException, ApiErrorResponseException, URIValidationException {
        initGetPendingRefunds();
        when(paymentGetPendingRefunds.execute()).thenReturn(responseWithData);
        when(responseWithData.getData()).thenReturn(bulkRefundsApi);
        when(bulkRefundsApi.getTotal()).thenReturn(0);
        int pendingRefunds = mockPaymentService.getPendingRefunds();
        assertEquals(0, pendingRefunds);
    }

    @Test
    @DisplayName("Get Pending Refunds - Success Path - 5 failed refunds")
    void getPendingRefundsSuccessFiveFailedRefunds() throws ServiceException, ApiErrorResponseException, URIValidationException {
        initGetPendingRefunds();
        when(paymentGetPendingRefunds.execute()).thenReturn(responseWithData);
        when(responseWithData.getData()).thenReturn(bulkRefundsApi);
        when(bulkRefundsApi.getTotal()).thenReturn(5);
        int pendingRefunds = mockPaymentService.getPendingRefunds();
        assertEquals(5, pendingRefunds);
    }

    @Test
    @DisplayName("Get Pending Refunds - Throws ApiErrorResponseException")
    void getPendingRefundsThrowsApiErrorResponseException() throws ApiErrorResponseException, URIValidationException {
        initGetPendingRefunds();
        when(paymentGetPendingRefunds.execute()).thenThrow(ApiErrorResponseException.class);
        assertThrows(ServiceException.class, () ->
                mockPaymentService.getPendingRefunds());
    }

    @Test
    @DisplayName("Get Pending Refunds - Throws URIValidationException")
    void getPendingRefundsThrowsURIValidationException() throws ApiErrorResponseException, URIValidationException {
        initGetPendingRefunds();
        when(paymentGetPendingRefunds.execute()).thenThrow(URIValidationException.class);
        assertThrows(ServiceException.class, () ->
                mockPaymentService.getPendingRefunds());
    }

    private void initGetPendingRefunds() {
        when(apiClientService.getInternalApiClient()).thenReturn(internalApiClient);
        when(internalApiClient.privatePayment()).thenReturn(privatePaymentResourceHandler);
        when(privatePaymentResourceHandler.getPendingRefunds("/admin/payments/bulk-refunds")).thenReturn(paymentGetPendingRefunds);
    }

    @Test
    @DisplayName("createBulkRefund - delegates to fileUploadAPIClient.upload")
    void createBulkRefund_delegatesToUpload() {
        var file = mock(org.springframework.web.multipart.MultipartFile.class);
        mockPaymentService.createBulkRefund(file, "TYPE");
        verify(fileUploadAPIClient).upload(file, "TYPE");
    }

    @Test
    @DisplayName("createBulkRefund - propagates HttpClientErrorException")
    void createBulkRefund_propagatesHttpClientErrorException() {
        var file = mock(org.springframework.web.multipart.MultipartFile.class);
        doThrow(org.springframework.web.client.HttpClientErrorException.class)
            .when(fileUploadAPIClient).upload(any(), any());
        assertThrows(org.springframework.web.client.HttpClientErrorException.class, () ->
            mockPaymentService.createBulkRefund(file, "TYPE"));
    }

    @Test
    @DisplayName("postProcessPendingRefunds - success")
    void postProcessPendingRefunds_success() throws Exception {
        var internalApiClientMock = mock(uk.gov.companieshouse.api.InternalApiClient.class);
        var handler = mock(uk.gov.companieshouse.api.handler.payment.PrivatePaymentResourceHandler.class);
        var req = mock(uk.gov.companieshouse.api.handler.payment.request.PaymentProcessPendingRefunds.class);
        when(apiClientService.getInternalApiClient()).thenReturn(internalApiClientMock);
        when(internalApiClientMock.privatePayment()).thenReturn(handler);
        when(handler.processPendingRefunds(anyString())).thenReturn(req);
        when(req.execute()).thenReturn(null);
        assertDoesNotThrow(() -> mockPaymentService.postProcessPendingRefunds());
    }

    @Test
    @DisplayName("postProcessPendingRefunds - ApiErrorResponseException")
    void postProcessPendingRefunds_ApiErrorResponseException() throws Exception {
        var internalApiClientMock = mock(uk.gov.companieshouse.api.InternalApiClient.class);
        var handler = mock(uk.gov.companieshouse.api.handler.payment.PrivatePaymentResourceHandler.class);
        var req = mock(uk.gov.companieshouse.api.handler.payment.request.PaymentProcessPendingRefunds.class);
        when(apiClientService.getInternalApiClient()).thenReturn(internalApiClientMock);
        when(internalApiClientMock.privatePayment()).thenReturn(handler);
        when(handler.processPendingRefunds(anyString())).thenReturn(req);
        when(req.execute()).thenThrow(ApiErrorResponseException.class);
        assertThrows(ServiceException.class, () -> mockPaymentService.postProcessPendingRefunds());
    }

    @Test
    @DisplayName("postProcessPendingRefunds - URIValidationException")
    void postProcessPendingRefunds_URIValidationException() throws Exception {
        var internalApiClientMock = mock(uk.gov.companieshouse.api.InternalApiClient.class);
        var handler = mock(uk.gov.companieshouse.api.handler.payment.PrivatePaymentResourceHandler.class);
        var req = mock(uk.gov.companieshouse.api.handler.payment.request.PaymentProcessPendingRefunds.class);
        when(apiClientService.getInternalApiClient()).thenReturn(internalApiClientMock);
        when(internalApiClientMock.privatePayment()).thenReturn(handler);
        when(handler.processPendingRefunds(anyString())).thenReturn(req);
        when(req.execute()).thenThrow(URIValidationException.class);
        assertThrows(ServiceException.class, () -> mockPaymentService.postProcessPendingRefunds());
    }
}
