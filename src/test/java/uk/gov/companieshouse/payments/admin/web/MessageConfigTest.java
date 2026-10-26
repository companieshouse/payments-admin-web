package uk.gov.companieshouse.payments.admin.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.LocaleResolver;

class MessageConfigTest {

    private MessageConfig messageConfig;
    private MessageSource messageSource;
    private LocaleResolver localeResolver;

    @BeforeEach
    void setUp() {
        messageConfig = new MessageConfig();
        messageSource = messageConfig.messageSource();
        localeResolver = messageConfig.localeResolver();
    }

    @Test
    @DisplayName("MessageSource includes local and shared message bundles")
    void testMessageSourceBeanConfiguration() {
        var resourceBundleMessageSource = assertInstanceOf(ResourceBundleMessageSource.class, messageSource);

        assertEquals(Set.of("locales/messages", "locales/common-messages"),
                resourceBundleMessageSource.getBasenameSet());
    }

    @Test
    @DisplayName("MessageSource resolves English language selector text")
    void testMessageSourceResolvesEnglishMessages() {
        assertEquals("English", messageSource.getMessage("label.lang.en", null, Locale.ENGLISH));
    }

    @Test
    @DisplayName("MessageSource resolves Welsh language selector text")
    void testMessageSourceResolvesWelshMessages() {
        assertEquals("Cymraeg", messageSource.getMessage("label.lang.cy", null, Locale.forLanguageTag("cy")));
    }

    @Test
    @DisplayName("MessageSource resolves local refund messages in English")
    void testMessageSourceResolvesLocalEnglishMessages() {
        assertEquals("Payment service refunds",
                messageSource.getMessage("refund.summary.heading", null, Locale.ENGLISH));
    }

    @Test
    @DisplayName("MessageSource resolves local refund messages in Welsh")
    void testMessageSourceResolvesLocalWelshMessages() {
        Locale welsh = Locale.forLanguageTag("cy");

        assertEquals("Ail-geisio ad-daliadau",
                messageSource.getMessage("refund.summary.retry", null, welsh));
        assertEquals("Lanlwytho ffeil ad-daliad",
                messageSource.getMessage("refund.summary.upload-file", null, welsh));
        assertEquals("Mae 3 ad-daliadau wedi methu",
                messageSource.getMessage("refund.summary.failed-count", new Object[] {3}, welsh));
        assertEquals("Ad-daliadau gwasanaeth taliadau",
                messageSource.getMessage("refund.summary.heading", null, welsh));
    }

    @Test
    @DisplayName("MessageSource resolves shared common-web-java footer messages")
    void testMessageSourceResolvesSharedFooterMessages() {
        assertEquals("Cookies", messageSource.getMessage("label.footer.cookies", null, Locale.ENGLISH));
        assertEquals("Cwcis", messageSource.getMessage("label.footer.cookies", null, Locale.forLanguageTag("cy")));
    }

    @Test
    @DisplayName("Resolves to English when no locale is stored in the session")
    void testResolveDefaultLocale() {
        var request = new MockHttpServletRequest();

        assertEquals(Locale.ENGLISH, localeResolver.resolveLocale(request));
    }

    @Test
    @DisplayName("Selected Welsh locale is retained for subsequent requests in the same session")
    void testSetLocale() {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        Locale welsh = Locale.forLanguageTag("cy");

        localeResolver.setLocale(request, response, welsh);
        var nextRequest = new MockHttpServletRequest();
        nextRequest.setSession(request.getSession(false));

        assertEquals(welsh, nextRequest.getSession().getAttribute("current.locale"));
        assertEquals(welsh, localeResolver.resolveLocale(nextRequest));
    }

    @Test
    @DisplayName("Language selection uses the lang request parameter")
    void testLocaleChangeInterceptorConfiguration() {
        assertEquals("lang", messageConfig.localeChangeInterceptor().getParamName());
    }
}
