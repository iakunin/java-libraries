package dev.iakunin.library.logging.reactive.configuration;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.reactive.wrapper.ContextWrapper;
import dev.iakunin.library.logging.reactive.wrapper.LoggerWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties(Properties.class)
public class WrappersConfiguration {

    private final Properties properties;

    @Bean
    public ContextWrapper contextWrapper() {
        return new ContextWrapper(properties);
    }

    @Bean
    public LoggerWrapper loggerWrapper(
        ContextWrapper contextWrapper
    ) {
        return new LoggerWrapper(contextWrapper);
    }
}
