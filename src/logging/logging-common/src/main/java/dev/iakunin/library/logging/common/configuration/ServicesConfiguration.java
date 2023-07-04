package dev.iakunin.library.logging.common.configuration;

import dev.iakunin.library.logging.common.service.ContentTypeWhitelist;
import dev.iakunin.library.logging.common.service.FieldTrimmer;
import dev.iakunin.library.logging.common.service.HeadersBuilder;
import dev.iakunin.library.logging.common.service.MdcFingerprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(Properties.class)
public class ServicesConfiguration {

    private final Properties properties;

    @Bean
    public ContentTypeWhitelist contentTypeWhitelist() {
        return new ContentTypeWhitelist(properties);
    }

    @Bean
    public FieldTrimmer fieldTrimmer() {
        return new FieldTrimmer(properties);
    }

    @Bean
    public HeadersBuilder headersBuilder() {
        return new HeadersBuilder();
    }

    @Bean
    public MdcFingerprintService mdcFingerprintService() {
        return new MdcFingerprintService(properties);
    }
}
