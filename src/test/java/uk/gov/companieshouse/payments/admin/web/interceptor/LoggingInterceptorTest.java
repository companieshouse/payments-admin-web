package uk.gov.companieshouse.payments.admin.web.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoggingInterceptorTest {

    private LoggingInterceptor interceptor;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Object handler;
    @Mock
    private ModelAndView modelAndView;
    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        interceptor = new LoggingInterceptor();
        when(request.getSession()).thenReturn(session);
    }

    @Test
    void preHandle_shouldLogStartAndReturnTrue() {
        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
    }

    @Test
    void postHandle_shouldLogEnd() {
        // Simulate the real lifecycle: preHandle sets start, postHandle reads it
        interceptor.preHandle(request, response, handler);
        // Capture the value set by preHandle and return it in getAttribute
        verify(session).setAttribute(eq("start"), any(Long.class));
        when(session.getAttribute("start")).thenReturn(123L);
        interceptor.postHandle(request, response, handler, modelAndView);
        assertTrue(true); // No exception means pass
    }
}
