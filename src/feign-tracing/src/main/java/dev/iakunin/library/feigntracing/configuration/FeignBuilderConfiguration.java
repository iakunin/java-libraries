package dev.iakunin.library.feigntracing.configuration;

import dev.iakunin.library.feigntracing.SessionFingerprintInterceptor;
import feign.Capability;
import feign.Feign;
import feign.Logger;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

@Slf4j
@Configuration
@EnableFeignClients
@RequiredArgsConstructor
@Import(FeignClientsConfiguration.class)
public class FeignBuilderConfiguration {

    private final SessionFingerprintInterceptor sessionFingerprintInterceptor;
    private final Encoder encoder;
    private final Decoder decoder;
    private final ErrorDecoder errorDecoder;
    private final Logger logger;

    @Bean("commonFeignBuilder")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Feign.Builder feignBuilder(List<Capability> capabilities) {
        final Feign.Builder builder = Feign.builder()
            .contract(new SpringMvcContract())
            .retryer(Retryer.NEVER_RETRY)
            .requestInterceptor(sessionFingerprintInterceptor)
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
