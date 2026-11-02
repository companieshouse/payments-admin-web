package uk.gov.companieshouse.payments.admin.web.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.LocaleResolver;

/**
 * Provides model attributes required by the shared language selector.
 */
@ControllerAdvice
public class GlobalController {

    private final LocaleResolver localeResolver;

    /**
     * Creates the global model attribute provider.
     *
     * @param localeResolver resolver for the current language
     */
    public GlobalController(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    /**
     * Provides the original page URI so language links do not target the error endpoint.
     *
     * @param request the current request
     * @return the original request URI, or the current URI for normal requests
     */
    @ModelAttribute("requestURI")
    String getRequestURI(HttpServletRequest request) {
        Object originalURI = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        return originalURI instanceof String uri ? uri : request.getRequestURI();
    }

    /**
     * Provides the current language for the shared language selector.
     *
     * @param request the current request
     * @return the resolved locale as a string
     */
    @ModelAttribute("locale")
    String getLocale(HttpServletRequest request) {
        return localeResolver.resolveLocale(request).toString();
    }

    /**
     * Provides the request so the shared language selector can read query parameters.
     *
     * @param request the current request
     * @return the current request
     */
    @ModelAttribute("request")
    HttpServletRequest getRequest(HttpServletRequest request) {
        return request;
    }
}