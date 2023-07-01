package dev.iakunin.library.feigntracing.configuration;

import feign.micrometer.MicrometerCapability;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignCapabilityConfiguration {

    @Autowired(required = false)
    private MeterRegistry meterRegistry;

    @Bean
    public MicrometerCapability micrometerCapability() {
        if (meterRegistry != null) {
            return new MicrometerCapability(meterRegistry);
        }

        return null;
    }
}
