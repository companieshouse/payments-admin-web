package uk.gov.companieshouse.payments.admin.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.payments.admin.web.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.payments.admin.web.interceptor.UserDetailsInterceptor;
import uk.gov.companieshouse.payments.admin.web.interceptor.UserPermissionInterceptor;

@SpringBootApplication
public class Application implements WebMvcConfigurer{

	public static final String APPLICATION_NAME_SPACE = "payments-admin-web";
	private static final String HEALTHCHECK_PATH = "/admin/payments/healthcheck";

	private UserPermissionInterceptor userPermissionInterceptor;
	private UserDetailsInterceptor userDetailsInterceptor;
	private LoggingInterceptor loggingInterceptor;

	@Autowired
	public Application(UserPermissionInterceptor userPermissionInterceptor, UserDetailsInterceptor userDetailsInterceptor, LoggingInterceptor loggingInterceptor) {
		this.userPermissionInterceptor = userPermissionInterceptor;
		this.userDetailsInterceptor = userDetailsInterceptor;
		this.loggingInterceptor = loggingInterceptor;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(userPermissionInterceptor).excludePathPatterns(HEALTHCHECK_PATH, "/error");
		registry.addInterceptor(userDetailsInterceptor).excludePathPatterns(HEALTHCHECK_PATH);
		registry.addInterceptor(loggingInterceptor).excludePathPatterns(HEALTHCHECK_PATH);
	}

}
