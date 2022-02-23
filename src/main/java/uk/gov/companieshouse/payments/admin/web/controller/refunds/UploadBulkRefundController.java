package uk.gov.companieshouse.payments.admin.web.controller.refunds;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.payments.admin.web.controller.BaseController;
import uk.gov.companieshouse.payments.admin.web.models.UploadRefundFile;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/payments/refunds")
public class UploadBulkRefundController extends BaseController {

    private static final String UPLOAD_BULK_REFUND = "refunds/uploadBulkRefund";

    @Override protected String getTemplateName() {
        return UPLOAD_BULK_REFUND;
    }

    @GetMapping
    public String getUploadBulkRefund(Model model) {
        model.addAttribute("uploadRefundFile", new UploadRefundFile());

        return getTemplateName();
    }

    @PostMapping
    public String postUploadBulkRefund(@ModelAttribute("uploadRefundFile") @Valid UploadRefundFile uploadRefundFile,
                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return getTemplateName();
        }

        //TODO - Change to summary page when implemented
        return "redirect:https://find-and-update.company-information.service.gov.uk/";
    }
}
