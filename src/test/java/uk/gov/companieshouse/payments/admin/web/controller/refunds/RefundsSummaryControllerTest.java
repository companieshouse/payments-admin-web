package uk.gov.companieshouse.payments.admin.web.controller.refunds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RefundsSummaryControllerTest {

        private MockMvc mockMvc;

        @InjectMocks
        private RefundsSummaryController controller;

        private static final String REFUNDS_SUMMARY_PATH = "/admin/payments/summary";

        private static final String REFUNDS_SUMMARY_VIEW = "refunds/refundSummary";

        @BeforeEach
        private void setup() {
            this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        }

        @Test
        @DisplayName("Get Refunds Summary - Success")
        void getRequestSuccess() throws Exception {

            this.mockMvc.perform(get(REFUNDS_SUMMARY_PATH))
                    .andExpect(status().isOk())
                    .andExpect(view().name(REFUNDS_SUMMARY_VIEW));
        }
}
