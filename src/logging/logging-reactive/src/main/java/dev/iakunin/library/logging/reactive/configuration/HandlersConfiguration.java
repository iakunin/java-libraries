package dev.iakunin.library.logging.reactive.configuration;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.service.MdcFingerprintService;
import dev.iakunin.library.logging.reactive.adapter.RequestLoggerAdapter;
import dev.iakunin.library.logging.reactive.adapter.ResponseLoggerAdapter;
import dev.iakunin.library.logging.reactive.handler.FingerprintHandler;
import dev.iakunin.library.logging.reactive.handler.HttpLoggingHandler;
import dev.iakunin.library.logging.reactive.handler.RequestIdHandler;
import dev.iakunin.library.logging.reactive.handler.RequestPathHandler;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.HttpHandlerDecoratorFactory;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@Slf4j
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties(Properties.class)
@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class HandlersConfiguration {

    private final RequestLoggerAdapter requestLogger;
    private final ResponseLoggerAdapter responseLogger;
    private final Properties properties;

    public HandlersConfiguration(Properties properties) {
        this.requestLogger = new RequestLoggerAdapter(properties);
        this.responseLogger = new ResponseLoggerAdapter(properties);
        this.properties = properties;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public HttpHandlerDecoratorFactory fingerprintHandlerDecorator() {
        return decorated -> new FingerprintHandler(
            decorated,
            new MdcFingerprintService(properties),
            properties
        );
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public HttpHandlerDecoratorFactory requestPathHandlerDecorator() {
        return decorated -> new RequestPathHandler(decorated, properties);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 2)
    public HttpHandlerDecoratorFactory requestIdHandlerDecorator() {
        return decorated -> new RequestIdHandler(decorated, properties);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 3)
    public HttpHandlerDecoratorFactory httpLoggingHandlerDecorator() {
        return decorated -> new HttpLoggingHandler(
            decorated,
            requestLogger,
            responseLogger,
            requestBlacklist()
        );
    }

    private RequestMatcher requestBlacklist() {
        if (properties.getExcludePaths().isEmpty()) {
            // no requests will be matched
            return new NegatedRequestMatcher(AnyRequestMatcher.INSTANCE);
        }

        return new OrRequestMatcher(
            properties.getExcludePaths()
                .stream()
                .map(AntPathRequestMatcher::new)
                .collect(Collectors.toList())
        );
    }
}
