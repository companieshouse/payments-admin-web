package uk.gov.companieshouse.payments.admin.web.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.companieshouse.payments.admin.web.session.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class UserPermissionInterceptor implements HandlerInterceptor{

    private static final String REFUND_PERMISSION = "/admin/payments-bulk-refunds";

    @Value("${commonweb.chs-url}")
    private String chsUrl;

    @Autowired
    private SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Map<String, Object> userPermissions = sessionService.getUserPermissions();

        Object refundPermission = userPermissions == null ? null : userPermissions.get(REFUND_PERMISSION);

        if (!Integer.valueOf(1).equals(refundPermission)) {
            request.setAttribute("chsUrl", chsUrl);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }
        return true;
    }
}
