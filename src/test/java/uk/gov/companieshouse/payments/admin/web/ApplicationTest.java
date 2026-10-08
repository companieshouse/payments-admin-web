package uk.gov.companieshouse.payments.admin.web;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import uk.gov.companieshouse.payments.admin.web.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.payments.admin.web.interceptor.UserDetailsInterceptor;
import uk.gov.companieshouse.payments.admin.web.interceptor.UserPermissionInterceptor;

class ApplicationTest {

    private static final String HEALTHCHECK_PATH = "/admin/payments/healthcheck";

    @Test
    @DisplayName("Registers all interceptors, excluding the healthcheck and, for permissions, the error page")
    void addInterceptorsRegistersInterceptorsWithExclusions() {
        UserPermissionInterceptor permissionInterceptor = mock(UserPermissionInterceptor.class);
        UserDetailsInterceptor detailsInterceptor = mock(UserDetailsInterceptor.class);
        LoggingInterceptor loggingInterceptor = mock(LoggingInterceptor.class);
        InterceptorRegistry registry = mock(InterceptorRegistry.class);
        InterceptorRegistration permissionRegistration = mock(InterceptorRegistration.class);
        InterceptorRegistration detailsRegistration = mock(InterceptorRegistration.class);
        InterceptorRegistration loggingRegistration = mock(InterceptorRegistration.class);
        when(registry.addInterceptor(permissionInterceptor)).thenReturn(permissionRegistration);
        when(registry.addInterceptor(detailsInterceptor)).thenReturn(detailsRegistration);
        when(registry.addInterceptor(loggingInterceptor)).thenReturn(loggingRegistration);

        new Application(permissionInterceptor, detailsInterceptor, loggingInterceptor).addInterceptors(registry);

        verify(permissionRegistration).excludePathPatterns(HEALTHCHECK_PATH, "/error");
        verify(detailsRegistration).excludePathPatterns(HEALTHCHECK_PATH);
        verify(loggingRegistration).excludePathPatterns(HEALTHCHECK_PATH);
    }

    @Test
    @DisplayName("Main method starts the Spring application")
    void mainRunsSpringApplication() {
        String[] args = {"--test"};
        try (MockedStatic<SpringApplication> springApplication = Mockito.mockStatic(SpringApplication.class)) {
            Application.main(args);

            springApplication.verify(() -> SpringApplication.run(Application.class, args));
        }
    }
}
