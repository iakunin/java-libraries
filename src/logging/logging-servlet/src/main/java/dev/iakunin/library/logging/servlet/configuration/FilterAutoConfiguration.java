package dev.iakunin.library.logging.servlet.configuration;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.service.ContentTypeWhitelist;
import dev.iakunin.library.logging.common.service.FieldTrimmer;
import dev.iakunin.library.logging.common.service.MdcFingerprintService;
import dev.iakunin.library.logging.servlet.adapter.RequestLoggerAdapter;
import dev.iakunin.library.logging.servlet.adapter.ResponseLoggerAdapter;
import dev.iakunin.library.logging.servlet.filter.FingerprintFilter;
import dev.iakunin.library.logging.servlet.filter.HttpLoggingFilter;
import dev.iakunin.library.logging.servlet.filter.RequestPathFilter;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(Properties.class)
@RequiredArgsConstructor
@Import(AdaptersConfiguration.class)
public class FilterAutoConfiguration {

    private final Properties properties;
    private final FieldTrimmer fieldTrimmer;
    private final ContentTypeWhitelist contentTypeWhitelist;
    private final MdcFingerprintService mdcFingerprintService;
    private final RequestLoggerAdapter requestLoggerAdapter;
    private final ResponseLoggerAdapter responseLoggerAdapter;

    @Bean
    public FilterRegistrationBean<FingerprintFilter> fingerprintRegistrationBean() {
        final FilterRegistrationBean<FingerprintFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(
            new FingerprintFilter(mdcFingerprintService, properties)
        );
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return bean;
    }

    @Bean
    public FilterRegistrationBean<RequestPathFilter> requestPathRegistrationBean() {
        final FilterRegistrationBean<RequestPathFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(
            new RequestPathFilter(properties, fieldTrimmer)
        );
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);

        return bean;
    }

    @Bean
    public FilterRegistrationBean<HttpLoggingFilter> httpLoggingRegistrationBean() {
        final FilterRegistrationBean<HttpLoggingFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(
            new HttpLoggingFilter(
                requestLoggerAdapter,
                responseLoggerAdapter,
                requestBlacklist()
            )
        );
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);

        return bean;
    }

    private RequestMatcher requestBlacklist() {
        if (properties.getExcludePaths().isEmpty()) {
            // no requests will be matched
            return new NegatedRequestMatcher(AnyRequestMatcher.INSTANCE);
        }

        return new OrRequestMatcher(
            properties.getExcludePaths()
                .stream()
                .map(AntPathRequestMatcher::new)
                .collect(Collectors.toList())
        );
    }
}
