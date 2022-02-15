package uk.gov.companieshouse.payments.admin.web.controller.refunds;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

@Controller
@RequestMapping("/admin/payments/refunds")
public class UploadBulkRefundController {

    private static final String UPLOAD_BULK_REFUND = "refunds/uploadBulkRefund";

    @GetMapping
    public String getUploadBulkRefund() {
        return UPLOAD_BULK_REFUND;
    }

    @PostMapping
    public String postUploadBulkRefund() {

        //TODO - Change to summary page when implemented
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/admin/payments/refunds";
    }
}
