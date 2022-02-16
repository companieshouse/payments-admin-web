package uk.gov.companieshouse.payments.admin.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.payments.admin.web.interceptor.UserDetailsInterceptor;
import uk.gov.companieshouse.payments.admin.web.interceptor.UserPermissionInterceptor;

@SpringBootApplication
public class Application implements WebMvcConfigurer{

	public static final String APPLICATION_NAME_SPACE = "payments-admin-web";

	private UserPermissionInterceptor userPermissionInterceptor;
	private UserDetailsInterceptor userDetailsInterceptor;

	@Autowired
	public Application(UserPermissionInterceptor userPermissionInterceptor, UserDetailsInterceptor userDetailsInterceptor) {
		this.userPermissionInterceptor = userPermissionInterceptor;
		this.userDetailsInterceptor = userDetailsInterceptor;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(userPermissionInterceptor);
		registry.addInterceptor(userDetailsInterceptor);
	}

}
