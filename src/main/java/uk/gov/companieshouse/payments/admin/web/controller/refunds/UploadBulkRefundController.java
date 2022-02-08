package uk.gov.companieshouse.payments.admin.web.controller.refunds;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UploadBulkRefundController {

    private static final String UPLOAD_BULK_REFUND = "refunds/uploadBulkRefund";

    @GetMapping(value = "/admin/payments/refunds")
    public String getUploadBulkRefund() {
        return UPLOAD_BULK_REFUND;
    }
}
