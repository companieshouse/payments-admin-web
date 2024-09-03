package uk.gov.companieshouse.payments.admin.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import uk.gov.companieshouse.auth.filter.HijackFilter;
import uk.gov.companieshouse.auth.filter.UserAuthFilter;
import uk.gov.companieshouse.session.handler.SessionHandler;

@Configuration
@EnableWebSecurity
public class WebSecurity {

    @Bean
    public SecurityFilterChain healthcheckSecurityFilterChain(HttpSecurity http) throws Exception {

        http.securityMatcher("/admin/payments/healthcheck")
            .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public SecurityFilterChain apiKeyPaymentsAdminWebSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/admin/payments/refunds", "/admin/payments/summary", "/")
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(new SessionHandler(), BasicAuthenticationFilter.class)
            .addFilterBefore(new HijackFilter(), BasicAuthenticationFilter.class)
            .addFilterBefore(new UserAuthFilter(), BasicAuthenticationFilter.class);
        return http.build();
    }
}
