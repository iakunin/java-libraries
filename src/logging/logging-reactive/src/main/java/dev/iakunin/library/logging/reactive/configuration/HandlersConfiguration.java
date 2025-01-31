package dev.iakunin.library.logging.reactive.configuration;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.service.FieldTrimmer;
import dev.iakunin.library.logging.reactive.adapter.RequestLoggerAdapter;
import dev.iakunin.library.logging.reactive.adapter.ResponseLoggerAdapter;
import dev.iakunin.library.logging.reactive.handler.FingerprintHandler;
import dev.iakunin.library.logging.reactive.handler.HttpLoggingHandler;
import dev.iakunin.library.logging.reactive.handler.RequestIdHandler;
import dev.iakunin.library.logging.reactive.handler.RequestPathHandler;
import dev.iakunin.library.logging.reactive.handler.RequestQueryStringHandler;
import dev.iakunin.library.logging.reactive.wrapper.ContextWrapper;
import dev.iakunin.library.logging.reactive.wrapper.LoggerWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.HttpHandlerDecoratorFactory;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties(Properties.class)
@Import({AdaptersConfiguration.class, WrappersConfiguration.class, })
@RequiredArgsConstructor
@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class HandlersConfiguration {

    private final RequestLoggerAdapter requestLogger;
    private final ResponseLoggerAdapter responseLogger;
    private final Properties properties;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public HttpHandlerDecoratorFactory fingerprintHandlerDecorator(
        ContextWrapper contextWrapper
    ) {
        return decorated -> new FingerprintHandler(
            decorated,
            properties,
            contextWrapper
        );
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public HttpHandlerDecoratorFactory requestPathHandlerDecorator(
        FieldTrimmer fieldTrimmer,
        ContextWrapper contextWrapper
    ) {
        return decorated -> new RequestPathHandler(decorated, fieldTrimmer, contextWrapper);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 2)
    public HttpHandlerDecoratorFactory requestQueryStringHandlerDecorator(
        FieldTrimmer fieldTrimmer,
        ContextWrapper contextWrapper
    ) {
        return decorated -> new RequestQueryStringHandler(decorated, fieldTrimmer, contextWrapper);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 3)
    public HttpHandlerDecoratorFactory requestIdHandlerDecorator(
        ContextWrapper contextWrapper
    ) {
        return decorated -> new RequestIdHandler(decorated, properties, contextWrapper);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 4)
    public HttpHandlerDecoratorFactory httpLoggingHandlerDecorator(
        LoggerWrapper loggerWrapper
    ) {
        return decorated -> new HttpLoggingHandler(
            requestBlacklist(),
            decorated,
            requestLogger,
            responseLogger,
            loggerWrapper
        );
    }

    private RequestPredicate requestBlacklist() {
        // no requests will be matched
        final RequestPredicate noRequestsMather = __ -> false;

        if (properties.getExcludePaths().isEmpty()) {
            return noRequestsMather;
        }

        return properties.getExcludePaths()
            .stream()
            .map(RequestPredicates::path)
            .reduce(noRequestsMather, RequestPredicate::or);
    }
}
