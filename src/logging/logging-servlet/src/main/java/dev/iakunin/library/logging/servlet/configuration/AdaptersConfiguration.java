package dev.iakunin.library.logging.servlet.configuration;

import dev.iakunin.library.logging.common.configuration.LoggerConfiguration;
import dev.iakunin.library.logging.common.logger.RequestLogger;
import dev.iakunin.library.logging.common.logger.ResponseLogger;
import dev.iakunin.library.logging.servlet.adapter.RequestLoggerAdapter;
import dev.iakunin.library.logging.servlet.adapter.ResponseLoggerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(LoggerConfiguration.class)
public class AdaptersConfiguration {

    @Bean
    public RequestLoggerAdapter requestLoggerAdapter(RequestLogger requestLogger) {
        return new RequestLoggerAdapter(requestLogger);
    }

    @Bean
    public ResponseLoggerAdapter responseLoggerAdapter(ResponseLogger responseLogger) {
        return new ResponseLoggerAdapter(responseLogger);
    }
}
