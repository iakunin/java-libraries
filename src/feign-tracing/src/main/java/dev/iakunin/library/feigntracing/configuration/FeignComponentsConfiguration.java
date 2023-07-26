package dev.iakunin.library.feigntracing.configuration;

import dev.iakunin.library.feigntracing.FeignLogger;
import dev.iakunin.library.feigntracing.SessionFingerprintInterceptor;
import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.service.MdcFingerprintService;
import feign.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignComponentsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Logger feignLogger() {
        return new FeignLogger();
    }

    @Bean
    @ConditionalOnMissingBean
    public SessionFingerprintInterceptor sessionFingerprintInterceptor(
        Properties properties,
        MdcFingerprintService mdcFingerprintService
    ) {
        return new SessionFingerprintInterceptor(properties, mdcFingerprintService);
    }
}
