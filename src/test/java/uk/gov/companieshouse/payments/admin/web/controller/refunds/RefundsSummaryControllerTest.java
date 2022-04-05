package uk.gov.companieshouse.payments.admin.web.controller.refunds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.companieshouse.payments.admin.web.exception.ServiceException;
import uk.gov.companieshouse.payments.admin.web.service.payment.PaymentService;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RefundsSummaryControllerTest {

        private MockMvc mockMvc;

        @Mock
        private PaymentService mockPaymentService;

        @InjectMocks
        private RefundsSummaryController controller;

        private static final String PENDING_REFUNDS_MODEL_ATTR = "pendingRefunds";

        private static final String REFUNDS_SUMMARY_PATH = "/admin/payments/summary";
        private static final String REFUNDS_SUMMARY_VIEW = "refunds/refundSummary";
        private static final String ERROR_VIEW = "error";

        @BeforeEach
        private void setup() {
            this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        }

        @Test
        @DisplayName("Get Refunds Summary - Success")
        void getRequestSuccess() throws Exception {

            when(mockPaymentService.getPendingRefunds()).thenReturn(0);
            this.mockMvc.perform(get(REFUNDS_SUMMARY_PATH))
                    .andExpect(status().isOk())
                    .andExpect(view().name(REFUNDS_SUMMARY_VIEW))
                    .andExpect(model().attributeExists(PENDING_REFUNDS_MODEL_ATTR))
                    .andExpect(model().attribute(PENDING_REFUNDS_MODEL_ATTR, "0"));
        }

        @Test
        @DisplayName("Get Refunds Summary - Success - 3 pending refunds")
        void getRequestSuccessThreePendingRefunds() throws Exception {

                when(mockPaymentService.getPendingRefunds()).thenReturn(3);
                this.mockMvc.perform(get(REFUNDS_SUMMARY_PATH))
                        .andExpect(status().isOk())
                        .andExpect(view().name(REFUNDS_SUMMARY_VIEW))
                        .andExpect(model().attributeExists(PENDING_REFUNDS_MODEL_ATTR))
                        .andExpect(model().attribute(PENDING_REFUNDS_MODEL_ATTR, "3"));
        }

        @Test
        @DisplayName("Get Refunds Summary - Failure - Error returning pending refunds")
        void getRequestErrorReturningPendingRefunds() throws Exception {

                doThrow(ServiceException.class).when(mockPaymentService).getPendingRefunds();
                this.mockMvc.perform(get(REFUNDS_SUMMARY_PATH))
                        .andExpect(status().isOk())
                        .andExpect(view().name(ERROR_VIEW));
        }

        @Test
        @DisplayName("Post to summary page - Service exception")
        void retryPendingRefundsServiceException() throws Exception {

                doThrow(ServiceException.class).when(mockPaymentService).postProcessPendingRefunds();

                this.mockMvc.perform(post(REFUNDS_SUMMARY_PATH))
                        .andExpect(status().isOk())
                        .andExpect(view().name(ERROR_VIEW));
        }

        @Test
        @DisplayName("Post to summary page - successful")
        void retryPendingRefundsSuccessful() throws Exception {
                this.mockMvc.perform(post(REFUNDS_SUMMARY_PATH))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl(REFUNDS_SUMMARY_PATH + "?templateName=refunds%2FrefundSummary"));
        }
}
