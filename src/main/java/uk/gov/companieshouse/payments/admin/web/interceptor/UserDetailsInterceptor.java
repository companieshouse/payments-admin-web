package uk.gov.companieshouse.payments.admin.web.interceptor;

import java.util.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.payments.admin.web.session.SessionService;

@Component
public class UserDetailsInterceptor implements HandlerInterceptor {

    private static final String USER_EMAIL = "userEmail";

    private static final String SIGN_IN_KEY = "signin_info";
    private static final String USER_PROFILE_KEY = "user_profile";
    private static final String EMAIL_KEY = "email";

    private final SessionService sessionService;

    public UserDetailsInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {

        if (modelAndView == null) {
            return;
        }

        if (request.getMethod().equalsIgnoreCase("POST")) {
            String viewName = modelAndView.getViewName();
            if (viewName == null) {
                return;
            }
            if (viewName.startsWith(UrlBasedViewResolver.REDIRECT_URL_PREFIX)) {
                return;
            }
        } else if (!request.getMethod().equalsIgnoreCase("GET")) {
            return;
        }

        Map<String, Object> sessionData = sessionService.getSessionDataFromContext();
        Map<String, Object> signInInfo = (Map<String,Object>) sessionData.get(SIGN_IN_KEY);
        if (signInInfo != null) {
            Map<String, Object> userProfile = (Map<String, Object>) signInInfo.get(USER_PROFILE_KEY);
            if (userProfile != null) {
                modelAndView.addObject(USER_EMAIL, userProfile.get(EMAIL_KEY));
            }
        }
    }
}
