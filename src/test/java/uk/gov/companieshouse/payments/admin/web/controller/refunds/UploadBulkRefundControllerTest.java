package uk.gov.companieshouse.payments.admin.web.controller.refunds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.payments.admin.web.exception.ServiceException;
import uk.gov.companieshouse.payments.admin.web.models.BulkRefundType;
import uk.gov.companieshouse.payments.admin.web.service.navigation.NavigatorService;
import uk.gov.companieshouse.payments.admin.web.service.payment.PaymentService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UploadBulkRefundControllerTest {
    private static final String GOVPAY = "govpay";

    private MockMvc mockMvc;

    private BulkRefundType bulkRefundType = new BulkRefundType();

    @Mock
    private PaymentService paymentService;

    @Mock
    private NavigatorService navigatorService;

    @InjectMocks
    private UploadBulkRefundController controller;

    private static final String UPLOAD_BULK_REFUND_PATH = "/admin/payments/refunds";

    private static final String UPLOAD_BULK_REFUND_VIEW = "refunds/uploadBulkRefund";

    private static final String UPLOAD_REFUND_FILE_MODEL = "uploadRefundFile";

    private static final String TEMPLATE_NAME_MODEL_ATTR = "templateName";

    private static final String ERROR_VIEW = "error";

    private static final String MOCK_CONTROLLER_PATH = UrlBasedViewResolver.REDIRECT_URL_PREFIX + "mockControllerPath";


    @BeforeEach
    private void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        bulkRefundType.setSelectedBulkRefundType(GOVPAY);
    }

    @Test
    @DisplayName("Get Upload Bulk Refund - Success")
    void getRequestSuccess() throws Exception {

        this.mockMvc.perform(get(UPLOAD_BULK_REFUND_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(UPLOAD_BULK_REFUND_VIEW))
                .andExpect(model().attributeExists(UPLOAD_REFUND_FILE_MODEL));
    }

    @Test
    @DisplayName("Post Upload Bulk Refund - Failure - No file uploaded")
    void postRequestFailureNoFileUploaded() throws Exception {

        this.mockMvc.perform(post(UPLOAD_BULK_REFUND_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(UPLOAD_BULK_REFUND_VIEW))
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeHasFieldErrors(UPLOAD_REFUND_FILE_MODEL))
                .andExpect(model().attributeErrorCount(UPLOAD_REFUND_FILE_MODEL, 1));
    }

    @Test
    @DisplayName("Post Upload Bulk Refund - Failure - Non XML file")
    void postRequestFailureNonXMLFile() throws Exception {

        Path path = Paths.get("src/test/java/uk/gov/companieshouse/payments/admin/web/controller/refunds/mockFiles/nonXmlRefundFile.txt");
        MockMultipartFile mockValidRefundFile = new MockMultipartFile("refundFile", "nonXmlRefundFile.txt",
                "xml", Files.readAllBytes(path));

        this.mockMvc.perform(multipart(UPLOAD_BULK_REFUND_PATH).file(mockValidRefundFile))
                .andExpect(status().isOk())
                .andExpect(view().name(UPLOAD_BULK_REFUND_VIEW))
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeHasFieldErrors(UPLOAD_REFUND_FILE_MODEL))
                .andExpect(model().attributeErrorCount(UPLOAD_REFUND_FILE_MODEL, 1));
    }

    @Test
    @DisplayName("Post Upload Bulk Refund - Failure - File too big")
    void postRequestFailureFileTooBig() throws Exception {

        Path path = Paths.get("src/test/java/uk/gov/companieshouse/payments/admin/web/controller/refunds/mockFiles/tooBigRefundFile.xml");
        MockMultipartFile mockValidRefundFile = new MockMultipartFile("refundFile", "tooBigRefundFile.xml",
                "xml", Files.readAllBytes(path));

        this.mockMvc.perform(multipart(UPLOAD_BULK_REFUND_PATH).file(mockValidRefundFile).flashAttr("bulkRefundType", bulkRefundType))
                .andExpect(status().isOk())
                .andExpect(view().name(UPLOAD_BULK_REFUND_VIEW))
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeHasFieldErrors(UPLOAD_REFUND_FILE_MODEL))
                .andExpect(model().attributeErrorCount(UPLOAD_REFUND_FILE_MODEL, 1));
    }

    @Test
    @DisplayName("Post Upload Bulk Refund - Success")
    void postRequestSuccess() throws Exception {

        Path path = Paths.get("src/test/java/uk/gov/companieshouse/payments/admin/web/controller/refunds/mockFiles/validRefundFile.xml");
        MockMultipartFile mockValidRefundFile = new MockMultipartFile("refundFile", "refundFile.xml",
                "xml", Files.readAllBytes(path));

        this.mockMvc.perform(multipart(UPLOAD_BULK_REFUND_PATH).file(mockValidRefundFile))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Post Upload Bulk Refund - Bad request")
    void postRequestBadRequest() throws Exception {

        Path path = Paths.get("src/test/java/uk/gov/companieshouse/payments/admin/web/controller/refunds/mockFiles/validRefundFile.xml");
        MockMultipartFile mockValidRefundFile = new MockMultipartFile("refundFile", "refundFile.xml",
                "xml", Files.readAllBytes(path));


        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(paymentService).createBulkRefund(mockValidRefundFile, bulkRefundType.getSelectedBulkRefundType());

        this.mockMvc.perform(multipart(UPLOAD_BULK_REFUND_PATH).file(mockValidRefundFile).flashAttr("bulkRefundType", bulkRefundType))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("hasErrors"))
                .andExpect(model().attributeExists("validationFailed"));
    }

    @Test
    @DisplayName("Post Upload Bulk Refund - Unprocessable entity")
    void postRequestUnprocessableEntity() throws Exception {

        Path path = Paths.get("src/test/java/uk/gov/companieshouse/payments/admin/web/controller/refunds/mockFiles/validRefundFile.xml");
        MockMultipartFile mockValidRefundFile = new MockMultipartFile("refundFile", "refundFile.xml",
                "xml", Files.readAllBytes(path));


        doThrow(new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY)).when(paymentService).createBulkRefund(mockValidRefundFile, bulkRefundType.getSelectedBulkRefundType());

        this.mockMvc.perform(multipart(UPLOAD_BULK_REFUND_PATH).file(mockValidRefundFile).flashAttr("bulkRefundType", bulkRefundType))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("hasErrors"))
                .andExpect(model().attributeExists("mandatoryFieldsMissing"));
    }

    @Test
    @DisplayName("Post to summary page - Service exception")
    void processPendingRefundsServiceException() throws Exception {
        Path path = Paths.get("src/test/java/uk/gov/companieshouse/payments/admin/web/controller/refunds/mockFiles/validRefundFile.xml");
        MockMultipartFile mockValidRefundFile = new MockMultipartFile("refundFile", "refundFile.xml",
                "xml", Files.readAllBytes(path));

        doThrow(ServiceException.class).when(paymentService).postProcessPendingRefunds();

        this.mockMvc.perform(multipart(UPLOAD_BULK_REFUND_PATH).file(mockValidRefundFile))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_VIEW));
    }

    @Test
    @DisplayName("Post to summary page - successful")
    void processPendingRefundsSuccessful() throws Exception {
        Path path = Paths.get("src/test/java/uk/gov/companieshouse/payments/admin/web/controller/refunds/mockFiles/validRefundFile.xml");
        MockMultipartFile mockValidRefundFile = new MockMultipartFile("refundFile", "refundFile.xml",
                "xml", Files.readAllBytes(path));

        when(navigatorService.getNextControllerRedirect(any(), any())).thenReturn(MOCK_CONTROLLER_PATH);
        this.mockMvc.perform(multipart(UPLOAD_BULK_REFUND_PATH).file(mockValidRefundFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(MOCK_CONTROLLER_PATH));
    }
}
