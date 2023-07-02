package dev.iakunin.library.feigntracing.configuration;

import dev.iakunin.library.feigntracing.CommonErrorDecoder;
import dev.iakunin.library.feigntracing.FeignLogger;
import dev.iakunin.library.feigntracing.SessionFingerprintInterceptor;
import dev.iakunin.library.logging.configuration.Properties;
import dev.iakunin.library.logging.service.MdcFingerprintService;
import feign.Logger;
import feign.codec.ErrorDecoder;
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
    public ErrorDecoder commonErrorDecoder() {
        return new CommonErrorDecoder(
            new ErrorDecoder.Default()
        );
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
