package uk.gov.companieshouse.payments.admin.web.controller.refunds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.payments.admin.web.controller.BaseController;
import uk.gov.companieshouse.payments.admin.web.exception.ServiceException;
import uk.gov.companieshouse.payments.admin.web.service.payment.PaymentService;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin/payments/summary")
public class RefundsSummaryController extends BaseController {

    private static final String REFUND_SUMMARY = "refunds/refundSummary";

    @Autowired
    private PaymentService paymentService;

    @Override protected String getTemplateName() {
        return REFUND_SUMMARY;
    }

    @GetMapping
    public String getUploadBulkRefund(HttpServletRequest request, Model model) {

        int pendingRefunds = 0;
        // Get request to return pending refunds if any
        try {
            pendingRefunds = paymentService.getPendingRefunds();
        } catch (ServiceException e) {
            LOGGER.errorRequest(request, e.getMessage(), e);
            return ERROR_VIEW;
        }

        model.addAttribute("pendingRefunds", String.valueOf(pendingRefunds));

        return getTemplateName();
    }

    @PostMapping
    public String postRetryRefunds(HttpServletRequest request
                                   ) {
        // Post to retry processing pending refunds
        try {
            paymentService.postProcessPendingRefunds();
        } catch (ServiceException e) {
            LOGGER.errorRequest(request, e.getMessage(), e);
            return ERROR_VIEW;
        }
        return getTemplateName();
    }
}
