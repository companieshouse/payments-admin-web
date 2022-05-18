package uk.gov.companieshouse.payments.admin.web.controller.refunds;

import org.springframework.beans.factory.annotation.Autowired;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@NextController(RefundsSummaryController.class)
@RequestMapping({"/admin/payments/refunds", "/"})
public class UploadBulkRefundController extends BaseController {

    private static final String UPLOAD_BULK_REFUND = "refunds/uploadBulkRefund";

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
            try {
                System.out.println("createBulkRefund");
                paymentService.createBulkRefund(uploadRefundFile.getrefundFile(), bulkRefundType);
            } catch (HttpClientErrorException e) {
                System.out.println("createBulkRefund caught exception");
                switch (e.getStatusCode()) {
                    case BAD_REQUEST:
                        addValidation(model, "validationFailed");
                        break;
                    case UNPROCESSABLE_ENTITY:
                        addValidation(model, "mandatoryFieldsMissing");
                        break;
                    default:
                        LOGGER.errorRequest(request, e.getMessage(), e);
                }
            }
        } finally {
            // If an error has been returned from the API don't continue with processing the file
            if (model.containsAttribute("hasErrors")) {
                return getTemplateName();
            }
            else {
                try {
                    System.out.println("postProcessPendingRefunds");
                    paymentService.postProcessPendingRefunds();
                } catch (ServiceException e) {
                    LOGGER.errorRequest(request, e.getMessage(), e);
                } finally {
                    System.out.println("getNextControllerRedirect");
                    return navigatorService.getNextControllerRedirect(this.getClass());
                }
            }
        }
    }

    private void addValidation(Model model, String validationType) {
        model.addAttribute("hasErrors", "1");
        model.addAttribute(validationType, "1");
    }
}
