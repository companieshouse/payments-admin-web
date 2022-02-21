package uk.gov.companieshouse.payments.admin.web.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.payments.admin.web.session.SessionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPermissionInterceptorTests {

    private static final String PAGE_NOT_FOUND_TEMPLATE = "refunds/pageNotFound";

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private ModelAndView modelAndView;

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private UserPermissionInterceptor userPermissionInterceptor;

    @Test
    @DisplayName("Tests the interceptor for User Permissions success")
    void postHandleForUserPermissionSuccess() throws Exception {
        Map<String, Object> userPermissions = new HashMap<>();
        userPermissions.put("/admin/payments-bulk-refunds", 1);
        when(sessionService.getUserPermissions()).thenReturn(userPermissions);

        userPermissionInterceptor.postHandle(httpServletRequest, httpServletResponse, new Object(), modelAndView);

        verify(modelAndView, times(0)).setViewName(PAGE_NOT_FOUND_TEMPLATE);
    }

    @Test
    @DisplayName("Tests the interceptor for User Permissions failure")
    void postHandleForUserPermissionFailure() throws Exception {
        Map<String, Object> userPermissions = new HashMap<>();
        when(sessionService.getUserPermissions()).thenReturn(userPermissions);

        userPermissionInterceptor.postHandle(httpServletRequest, httpServletResponse, new Object(), modelAndView);

        verify(modelAndView, times(1)).setViewName(PAGE_NOT_FOUND_TEMPLATE);
    }
}

