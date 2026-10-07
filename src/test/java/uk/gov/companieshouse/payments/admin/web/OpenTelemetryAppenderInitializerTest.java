package uk.gov.companieshouse.payments.admin.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;

class OpenTelemetryAppenderInitializerTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withBean(OpenTelemetry.class, () -> mock(OpenTelemetry.class))
            .withUserConfiguration(OpenTelemetryAppenderInitializer.class);

    @Test
    void afterPropertiesSetInstallsAppender() {
        OpenTelemetry openTelemetry = mock(OpenTelemetry.class);

        try (MockedStatic<OpenTelemetryAppender> appender = Mockito.mockStatic(OpenTelemetryAppender.class)) {
            new OpenTelemetryAppenderInitializer(openTelemetry).afterPropertiesSet();

            appender.verify(() -> OpenTelemetryAppender.install(openTelemetry));
        }
    }

    @Test
    void beanAbsentByDefault() {
        contextRunner.run(context ->
                assertEquals(0, context.getBeansOfType(OpenTelemetryAppenderInitializer.class).size()));
    }

    @Test
    void beanAbsentWhenExplicitlyDisabled() {
        contextRunner.withPropertyValues("management.opentelemetry.enabled=false").run(context ->
                assertEquals(0, context.getBeansOfType(OpenTelemetryAppenderInitializer.class).size()));
    }

    @Test
    void beanPresentWhenEnabledAndRealPropertiesLoad() {
        try (MockedStatic<OpenTelemetryAppender> ignored = Mockito.mockStatic(OpenTelemetryAppender.class)) {
            contextRunner.withPropertyValues("management.opentelemetry.enabled=true").run(context -> {
                assertTrue(context.isRunning());
                assertEquals(1, context.getBeansOfType(OpenTelemetryAppenderInitializer.class).size());
                assertEquals("/v1/logs", context.getEnvironment()
                        .getProperty("management.opentelemetry.logging.export.otlp.endpoint"));
            });
        }
    }
}
