package uk.gov.companieshouse.payments.admin.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.RequestDispatcher;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.LocaleResolver;

@ExtendWith(MockitoExtension.class)
class GlobalControllerTest {

    private static final String REFUNDS_SUMMARY_PATH = "/admin/payments/summary";

    @Mock
    private HttpServletRequest request;

    @Mock
    private LocaleResolver localeResolver;

    @InjectMocks
    private GlobalController controller;

    @Test
    @DisplayName("Provides the current request URI for language switch links")
    void shouldReturnCurrentRequestURI() {
        when(request.getRequestURI()).thenReturn(REFUNDS_SUMMARY_PATH);

        assertEquals(REFUNDS_SUMMARY_PATH, controller.getRequestURI(request));
    }

    @Test
    @DisplayName("Provides the original request URI for language links on error pages")
    void shouldReturnOriginalRequestURIForErrorPage() {
        when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI))
                .thenReturn(REFUNDS_SUMMARY_PATH);

        assertEquals(REFUNDS_SUMMARY_PATH, controller.getRequestURI(request));
    }

    @Test
    @DisplayName("Provides the English locale for the language selector")
    void shouldReturnEnglishLocale() {
        when(localeResolver.resolveLocale(request)).thenReturn(Locale.ENGLISH);

        assertEquals("en", controller.getLocale(request));
    }

    @Test
    @DisplayName("Provides the Welsh locale for the language selector")
    void shouldReturnWelshLocale() {
        when(localeResolver.resolveLocale(request)).thenReturn(Locale.forLanguageTag("cy"));

        assertEquals("cy", controller.getLocale(request));
    }

    @Test
    @DisplayName("Provides the current request for the language selector query string")
    void shouldReturnCurrentRequest() {
        assertSame(request, controller.getRequest(request));
    }
}
