package uk.gov.companieshouse.payments.admin.web.controller.refunds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String postUploadBulkRefund(@ModelAttribute("uploadRefundFile")
            @Valid UploadRefundFile uploadRefundFile,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return getTemplateName();
        }

        try {
            paymentService.createBulkRefund(uploadRefundFile.getrefundFile());

        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode()) {
                case BAD_REQUEST:
                    addValidation(model, "validationFailed");
                    break;
                case UNPROCESSABLE_ENTITY:
                    addValidation(model, "mandatoryFieldsMissing");
                    break;
                default:
                    // TODO
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

    private void addValidation(Model model, String validationType) {
        model.addAttribute("hasErrors", "1");
        model.addAttribute(validationType, "1");
    }
}
