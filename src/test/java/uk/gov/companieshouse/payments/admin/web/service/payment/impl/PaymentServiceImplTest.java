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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentServiceImplTest {

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

        assertEquals(pendingRefunds, 0);
    }

    @Test
    @DisplayName("Get Pending Refunds - Success Path - 5 failed refunds")
    void getPendingRefundsSuccessFiveFailedRefunds() throws ServiceException, ApiErrorResponseException, URIValidationException {

        initGetPendingRefunds();

        when(paymentGetPendingRefunds.execute()).thenReturn(responseWithData);
        when(responseWithData.getData()).thenReturn(bulkRefundsApi);
        when(bulkRefundsApi.getTotal()).thenReturn(5);

        int pendingRefunds = mockPaymentService.getPendingRefunds();

        assertEquals(pendingRefunds, 5);
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
}
