package dev.iakunin.library.feigntracing.configuration;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.iakunin.library.feigntracing.CommonErrorDecoder;
import dev.iakunin.library.feigntracing.FeignLogger;
import dev.iakunin.library.feigntracing.SessionFingerprintInterceptor;
import dev.iakunin.library.logging.configuration.Properties;
import dev.iakunin.library.logging.service.MdcFingerprintService;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import java.util.List;
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
    public Encoder feignJacksonEncoder() {
        return new JacksonEncoder(
            List.of(
                new JavaTimeModule()
            )
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public Decoder feignJacksonDecoder() {
        return new JacksonDecoder(
            List.of(
                new JavaTimeModule()
            )
        );
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
    public SessionFingerprintInterceptor sessionFingerprintHeaderInterceptor(
        Properties properties,
        MdcFingerprintService mdcFingerprintService
    ) {
        return new SessionFingerprintInterceptor(properties, mdcFingerprintService);
    }
}
