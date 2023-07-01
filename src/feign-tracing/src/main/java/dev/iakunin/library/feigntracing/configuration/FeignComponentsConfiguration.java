package dev.iakunin.library.feigntracing.configuration;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.iakunin.library.feigntracing.CommonErrorDecoder;
import dev.iakunin.library.feigntracing.FeignLogger;
import dev.iakunin.library.feigntracing.SessionFingerprintInterceptor;
import dev.iakunin.library.logging.configuration.Properties;
import dev.iakunin.library.logging.service.MdcFingerprintService;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignComponentsConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public FeignLogger feignLogger() {
        return new FeignLogger();
    }

    @ConditionalOnMissingBean
    @Bean
    public JacksonEncoder feignJacksonEncoder() {
        return new JacksonEncoder(
            List.of(
                new JavaTimeModule()
            )
        );
    }

    @ConditionalOnMissingBean
    @Bean
    public JacksonDecoder feignJacksonDecoder() {
        return new JacksonDecoder(
            List.of(
                new JavaTimeModule()
            )
        );
    }

    @ConditionalOnMissingBean
    @Bean
    public ErrorDecoder commonErrorDecoder() {
        return new CommonErrorDecoder(
            new ErrorDecoder.Default()
        );
    }

    @ConditionalOnMissingBean
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
