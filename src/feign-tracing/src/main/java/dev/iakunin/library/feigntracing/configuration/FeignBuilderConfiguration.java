package dev.iakunin.library.feigntracing.configuration;

import dev.iakunin.library.feigntracing.FeignJacksonDecoder;
import dev.iakunin.library.feigntracing.FeignJacksonEncoder;
import dev.iakunin.library.feigntracing.FeignLogger;
import dev.iakunin.library.feigntracing.SessionFingerprintInterceptor;
import feign.Capability;
import feign.Feign;
import feign.Logger;
import feign.Response;
import feign.Retryer;
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

    private final FeignJacksonEncoder encoder;

    private final FeignJacksonDecoder decoder;

    private final FeignLogger feignLogger;

    @Autowired(required = false)
    @Bean("commonFeignBuilder")
    public Feign.Builder feignBuilder(List<Capability> capabilities) {
        final Feign.Builder builder = Feign.builder()
            .contract(new SpringMvcContract())
            .retryer(Retryer.NEVER_RETRY)
            .requestInterceptor(interceptor)
            .encoder(encoder)
            .decoder(decoder)
            .errorDecoder(new CommonErrorDecoder())
            .logLevel(Logger.Level.FULL)
            .logger(feignLogger);

        for (final Capability capability : capabilities) {
            builder.addCapability(capability);
        }

        return builder;
    }

    static class CommonErrorDecoder implements feign.codec.ErrorDecoder {

        private final feign.codec.ErrorDecoder defaultErrorDecoder = new Default();

        @Override
        public Exception decode(String methodKey, Response response) {
            log.warn("[Common ErrorDecoder] ####### fallback retry  #######");

            return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
