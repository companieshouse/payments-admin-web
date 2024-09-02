package uk.gov.companieshouse.payments.admin.web.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.payments.admin.web.session.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class UserPermissionInterceptor implements HandlerInterceptor{

    @Autowired
    private SessionService sessionService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

        Map<String, Object> userPermissions = sessionService.getUserPermissions();

        Integer refundPermission = (Integer) userPermissions.get("/admin/payments-bulk-refunds");

        if (modelAndView != null && (refundPermission == null || refundPermission != 1)) {
            modelAndView.setViewName("refunds/pageNotFound");
        }
    }
}
