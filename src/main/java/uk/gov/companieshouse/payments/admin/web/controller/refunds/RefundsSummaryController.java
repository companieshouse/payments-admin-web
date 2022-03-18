package uk.gov.companieshouse.payments.admin.web.controller.refunds;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.payments.admin.web.controller.BaseController;

@Controller
@RequestMapping("/admin/payments/summary")
public class RefundsSummaryController extends BaseController {

    private static final String REFUND_SUMMARY = "refunds/refundSummary";

    @Override protected String getTemplateName() {
        return REFUND_SUMMARY;
    }

    @GetMapping
    public String getUploadBulkRefund() {

        return getTemplateName();
    }
}
