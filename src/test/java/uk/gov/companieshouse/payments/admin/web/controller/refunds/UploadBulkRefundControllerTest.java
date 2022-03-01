package uk.gov.companieshouse.payments.admin.web.controller.refunds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UploadBulkRefundControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UploadBulkRefundController controller;

    private static final String UPLOAD_BULK_REFUND_PATH = "/admin/payments/refunds";

    private static final String UPLOAD_BULK_REFUND_VIEW = "refunds/uploadBulkRefund";

    private static final String UPLOAD_REFUND_FILE_MODEL = "uploadRefundFile";

    private static final String TEMPLATE_NAME_MODEL_ATTR = "templateName";

    @BeforeEach
    private void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
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

        this.mockMvc.perform(multipart(UPLOAD_BULK_REFUND_PATH).file(mockValidRefundFile))
                .andExpect(status().isOk())
                .andExpect(view().name(UPLOAD_BULK_REFUND_VIEW))
                .andExpect(model().attributeExists(TEMPLATE_NAME_MODEL_ATTR))
                .andExpect(model().attributeHasFieldErrors(UPLOAD_REFUND_FILE_MODEL))
                .andExpect(model().attributeErrorCount(UPLOAD_REFUND_FILE_MODEL, 1));
    }

//    @Test
//    @DisplayName("Post Upload Bulk Refund - Success")
//    void postRequestSuccess() throws Exception {
//
//        Path path = Paths.get("src/test/java/uk/gov/companieshouse/payments/admin/web/controller/refunds/mockFiles/validRefundFile.xml");
//        MockMultipartFile mockValidRefundFile = new MockMultipartFile("refundFile", "refundFile.xml",
//                "xml", Files.readAllBytes(path));
//
//        this.mockMvc.perform(multipart(UPLOAD_BULK_REFUND_PATH).file(mockValidRefundFile))
//                .andExpect(status().is3xxRedirection());
//    }
}
