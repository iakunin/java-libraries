package dev.iakunin.library.exceptionhandling.configuration;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.service.MdcFingerprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Properties.class)
@RequiredArgsConstructor
public class MdcFingerprintConfiguration {

    private final Properties properties;

    @Bean
    public MdcFingerprintService mdcFingerprintService() {
        return new MdcFingerprintService(properties);
    }
}
