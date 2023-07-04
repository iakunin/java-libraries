package dev.iakunin.library.logging.common.configuration;

import dev.iakunin.library.logging.common.logger.RequestLogger;
import dev.iakunin.library.logging.common.logger.ResponseLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(Properties.class)
@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class LoggerConfiguration {

    private final Properties properties;

    @Bean
    public RequestLogger requestLogger() {
        return new RequestLogger(properties);
    }

    @Bean
    public ResponseLogger responseLogger() {
        return new ResponseLogger(properties);
    }

}
