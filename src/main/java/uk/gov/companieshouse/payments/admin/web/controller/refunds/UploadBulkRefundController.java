package uk.gov.companieshouse.payments.admin.web.controller.refunds;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.companieshouse.payments.admin.web.annotation.NextController;
import uk.gov.companieshouse.payments.admin.web.controller.BaseController;
import uk.gov.companieshouse.payments.admin.web.exception.ServiceException;
import uk.gov.companieshouse.payments.admin.web.models.UploadRefundFile;
import uk.gov.companieshouse.payments.admin.web.service.payment.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@NextController(RefundsSummaryController.class)
@RequestMapping({"/admin/payments/refunds", "/"})
public class UploadBulkRefundController extends BaseController {

    private static final String UPLOAD_BULK_REFUND = "refunds/uploadBulkRefund";
    private static final String VALIDATION_FAILED = "validationFailed";
    private static final String MANDATORY_FIELDS_MISSING = "mandatoryFieldsMissing";

    @Autowired
    private PaymentService paymentService;

    @Override protected String getTemplateName() {
        return UPLOAD_BULK_REFUND;
    }

    @GetMapping
    public String getUploadBulkRefund(Model model) {
        model.addAttribute("uploadRefundFile", new UploadRefundFile());

        return getTemplateName();
    }

    @PostMapping
    public String postUploadBulkRefund(
            @ModelAttribute("uploadRefundFile") @Valid UploadRefundFile uploadRefundFile,
            BindingResult bindingResultFile,
            @RequestParam("paymentProvider") @Valid String bulkRefundType,
            BindingResult bindingResultType,
            Model model,
            HttpServletRequest request) {

        if (bindingResultFile.hasErrors()) {
            return getTemplateName();
        }

        try {
            paymentService.createBulkRefund(uploadRefundFile.getrefundFile(), bulkRefundType);

        } catch (HttpClientErrorException e) {
            HttpStatusCode statusCode = e.getStatusCode();
            if (statusCode.equals(HttpStatus.BAD_REQUEST)) {
                addValidation(model, VALIDATION_FAILED, e.getResponseBodyAsString());
            } else if (statusCode.equals(HttpStatus.UNPROCESSABLE_ENTITY)) {
                addValidation(model, MANDATORY_FIELDS_MISSING, null);
            } else {
                LOGGER.errorRequest(request, e.getMessage(), e);
            }
            return getTemplateName();
        }

        // Post to process pending refunds based on the uploaded file.
        try {
            paymentService.postProcessPendingRefunds();
        } catch (ServiceException e) {
            LOGGER.errorRequest(request, e.getMessage(), e);
            return ERROR_VIEW;
        }
        return navigatorService.getNextControllerRedirect(this.getClass());
    }

    private void addValidation(Model model, String validationType, String errorMessage) {
        model.addAttribute("hasErrors", "1");
        model.addAttribute(validationType, "1");
        if (validationType.equals(VALIDATION_FAILED)) {
            model.addAttribute("errorMessages", formatErrorMessage(errorMessage));
        }
    }

    private List<String> formatErrorMessage(String errorMessage){
        List<String> ValidationErrors;
        try {
            String partialFormat = new JSONObject(errorMessage).getString("message");

            // remove intro sentence of message saying there are errors in the file
            String errorString = partialFormat.substring(partialFormat.indexOf(":") + 1);

            ValidationErrors = Arrays.asList(errorString.split(","));
        } catch (Exception e) {
            LOGGER.error("Exception occurred when formatting validation error message: ", e);
            ValidationErrors = new ArrayList<>();
            ValidationErrors.add("Failed to display validation results");
        }
        return ValidationErrors;
    }
}
