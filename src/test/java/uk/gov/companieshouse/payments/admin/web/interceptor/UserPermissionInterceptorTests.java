package uk.gov.companieshouse.payments.admin.web.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.payments.admin.web.session.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPermissionInterceptorTests {

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private UserPermissionInterceptor userPermissionInterceptor;

    @Test
    @DisplayName("Allows the request when the user has refund permission")
    void preHandleForUserPermissionSuccess() throws Exception {
        Map<String, Object> userPermissions = new HashMap<>();
        userPermissions.put("/admin/payments-bulk-refunds", 1);
        when(sessionService.getUserPermissions()).thenReturn(userPermissions);

        assertTrue(userPermissionInterceptor.preHandle(httpServletRequest, httpServletResponse, new Object()));

        verify(httpServletResponse, never()).sendError(anyInt());
    }

    @Test
    @DisplayName("Blocks the request before the controller runs when permission is missing")
    void preHandleForUserPermissionFailure() throws Exception {
        when(sessionService.getUserPermissions()).thenReturn(new HashMap<>());

        assertFalse(userPermissionInterceptor.preHandle(httpServletRequest, httpServletResponse, new Object()));

        verify(httpServletResponse).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Blocks the request when permission value is not 1")
    void preHandleForUserPermissionZero() throws Exception {
        Map<String, Object> userPermissions = new HashMap<>();
        userPermissions.put("/admin/payments-bulk-refunds", 0);
        when(sessionService.getUserPermissions()).thenReturn(userPermissions);

        assertFalse(userPermissionInterceptor.preHandle(httpServletRequest, httpServletResponse, new Object()));
    }

    @Test
    @DisplayName("Blocks the request when user permissions are unavailable")
    void preHandleForNullPermissions() throws Exception {
        when(sessionService.getUserPermissions()).thenReturn(null);

        assertFalse(userPermissionInterceptor.preHandle(httpServletRequest, httpServletResponse, new Object()));
    }
}
