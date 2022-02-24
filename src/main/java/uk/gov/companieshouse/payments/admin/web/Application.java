package uk.gov.companieshouse.payments.admin.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.payments.admin.web.interceptor.UserDetailsInterceptor;
import uk.gov.companieshouse.payments.admin.web.interceptor.UserPermissionInterceptor;

@SpringBootApplication
public class Application implements WebMvcConfigurer{

	public static final String APPLICATION_NAME_SPACE = "payments-admin-web";

	private UserPermissionInterceptor userPermissionInterceptor;
	private UserDetailsInterceptor userDetailsInterceptor;
	protected static final Logger LOGGER = LoggerFactory
			.getLogger(Application.APPLICATION_NAME_SPACE);

	@Autowired
	public Application(UserPermissionInterceptor userPermissionInterceptor, UserDetailsInterceptor userDetailsInterceptor) {
		this.userPermissionInterceptor = userPermissionInterceptor;
		this.userDetailsInterceptor = userDetailsInterceptor;

		// FIXME Temporary logging - to be deleted
		LOGGER.info("Application start. Configs:");
		LOGGER.info("COOKIE_DOMAIN: " + System.getenv("COOKIE_DOMAIN"));
		LOGGER.info("COOKIE_NAME: " + System.getenv("COOKIE_NAME"));
		LOGGER.info("COOKIE_SECRET: " + System.getenv("COOKIE_SECRET"));
		LOGGER.info("CACHE_POOL_SIZE: " + System.getenv("CACHE_POOL_SIZE"));
		LOGGER.info("CACHE_SERVER: " + System.getenv("CACHE_SERVER"));
		LOGGER.info("DEFAULT_SESSION_EXPIRATION: " + System.getenv("DEFAULT_SESSION_EXPIRATION"));
		LOGGER.info("CDN_HOST: " + System.getenv("CDN_HOST"));
		LOGGER.info("CHS_API_KEY: " + System.getenv("CHS_API_KEY"));
		LOGGER.info("CHS_URL: " + System.getenv("CHS_URL"));
		LOGGER.info("JAVA_TOOL_OPTIONS: " + System.getenv("JAVA_TOOL_OPTIONS"));
		LOGGER.info("PIWIK_SITE: " + System.getenv("PIWIK_SITE"));
		LOGGER.info("PIWIK_URL: " + System.getenv("PIWIK_URL"));
		LOGGER.info("OAUTH2_CLIENT_ID: " + System.getenv("OAUTH2_CLIENT_ID"));
		LOGGER.info("OAUTH2_CLIENT_SECRET: " + System.getenv("OAUTH2_CLIENT_SECRET"));
		LOGGER.info("OAUTH2_TOKEN_URI: " + System.getenv("OAUTH2_TOKEN_URI"));
		LOGGER.info("OAUTH2_REQUEST_KEY: " + System.getenv("OAUTH2_REQUEST_KEY"));
		LOGGER.info("OAUTH2_AUTH_URI: " + System.getenv("OAUTH2_AUTH_URI"));
		LOGGER.info("OAUTH2_REDIRECT_URI: " + System.getenv("OAUTH2_REDIRECT_URI"));
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(userPermissionInterceptor).excludePathPatterns("/admin/payments/healthcheck");
		registry.addInterceptor(userDetailsInterceptor).excludePathPatterns("/admin/payments/healthcheck");
	}

}
