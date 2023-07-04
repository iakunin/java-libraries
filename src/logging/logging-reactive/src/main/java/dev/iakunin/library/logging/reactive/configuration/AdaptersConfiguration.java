package dev.iakunin.library.logging.reactive.configuration;

import dev.iakunin.library.logging.common.configuration.LoggerConfiguration;
import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.logger.RequestLogger;
import dev.iakunin.library.logging.common.logger.ResponseLogger;
import dev.iakunin.library.logging.reactive.adapter.RequestLoggerAdapter;
import dev.iakunin.library.logging.reactive.adapter.ResponseLoggerAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties(Properties.class)
@Import(LoggerConfiguration.class)
@RequiredArgsConstructor
public class AdaptersConfiguration {

    private final RequestLogger requestLogger;
    private final ResponseLogger responseLogger;

    @Bean
    public RequestLoggerAdapter requestLoggerAdapter() {
        return new RequestLoggerAdapter(requestLogger);
    }

    @Bean
    public ResponseLoggerAdapter responseLoggerAdapter() {
        return new ResponseLoggerAdapter(responseLogger);
    }
}
