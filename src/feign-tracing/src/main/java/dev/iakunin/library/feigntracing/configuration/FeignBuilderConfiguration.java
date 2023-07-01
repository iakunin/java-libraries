package dev.iakunin.library.feigntracing.configuration;

import dev.iakunin.library.feigntracing.FeignLogger;
import dev.iakunin.library.feigntracing.SessionFingerprintInterceptor;
import feign.Capability;
import feign.Feign;
import feign.Logger;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableFeignClients
@RequiredArgsConstructor
public class FeignBuilderConfiguration {

    private final SessionFingerprintInterceptor interceptor;
    private final JacksonEncoder encoder;
    private final JacksonDecoder decoder;
    private final ErrorDecoder errorDecoder;
    private final FeignLogger logger;

    @Autowired(required = false)
    @Bean("commonFeignBuilder")
    public Feign.Builder feignBuilder(List<Capability> capabilities) {
        final Feign.Builder builder = Feign.builder()
            .contract(new SpringMvcContract())
            .retryer(Retryer.NEVER_RETRY)
            .requestInterceptor(interceptor)
            .encoder(encoder)
            .decoder(decoder)
            .errorDecoder(errorDecoder)
            .logLevel(Logger.Level.FULL)
            .logger(logger);

        for (final Capability capability : capabilities) {
            builder.addCapability(capability);
        }

        return builder;
    }

}
