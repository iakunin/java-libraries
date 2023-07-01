package dev.iakunin.library.feigntracing.configuration;

import dev.iakunin.library.feigntracing.FeignJacksonDecoder;
import dev.iakunin.library.feigntracing.FeignJacksonEncoder;
import dev.iakunin.library.feigntracing.FeignLogger;
import dev.iakunin.library.feigntracing.SessionFingerprintInterceptor;
import dev.iakunin.library.logging.configuration.Properties;
import dev.iakunin.library.logging.service.MdcFingerprintService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignComponentsConfiguration {

    @Bean
    public FeignLogger feignLogger() {
        return new FeignLogger();
    }

    @Bean
    public FeignJacksonEncoder feignJacksonEncoder() {
        return new FeignJacksonEncoder();
    }

    @Bean
    public FeignJacksonDecoder feignJacksonDecoder() {
        return new FeignJacksonDecoder();
    }

    @Bean
    public SessionFingerprintInterceptor sessionFingerprintHeaderInterceptor(
        Properties properties,
        MdcFingerprintService mdcFingerprintService
    ) {
        return new SessionFingerprintInterceptor(
            properties, mdcFingerprintService
        );
    }
}
