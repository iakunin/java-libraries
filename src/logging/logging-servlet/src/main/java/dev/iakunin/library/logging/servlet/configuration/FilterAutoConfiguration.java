package dev.iakunin.library.logging.servlet.configuration;

import dev.iakunin.library.logging.servlet.filter.FingerprintFilter;
import dev.iakunin.library.logging.servlet.filter.HttpLoggingFilter;
import dev.iakunin.library.logging.servlet.filter.RequestPathFilter;
import dev.iakunin.library.logging.servlet.logger.RequestLogger;
import dev.iakunin.library.logging.servlet.logger.ResponseLogger;
import dev.iakunin.library.logging.servlet.service.ContentTypeWhitelist;
import dev.iakunin.library.logging.servlet.service.FieldTrimmer;
import dev.iakunin.library.logging.servlet.service.HeadersBuilder;
import dev.iakunin.library.logging.servlet.service.MdcFingerprintService;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
@Configuration
@EnableConfigurationProperties(Properties.class)
public class FilterAutoConfiguration {

    private final Properties properties;
    private final FieldTrimmer fieldTrimmer;
    private final ContentTypeWhitelist contentTypeWhitelist;
    private final HeadersBuilder headersBuilder;

    public FilterAutoConfiguration(Properties properties) {
        this.properties = properties;
        this.fieldTrimmer = new FieldTrimmer(properties);
        this.contentTypeWhitelist = new ContentTypeWhitelist(properties);
        this.headersBuilder = new HeadersBuilder();
    }

    @Bean
    public FilterRegistrationBean<FingerprintFilter> fingerprintRegistrationBean(
        MdcFingerprintService mdcFingerprintService
    ) {
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
                new RequestLogger(properties, fieldTrimmer, contentTypeWhitelist, headersBuilder),
                new ResponseLogger(properties, fieldTrimmer, headersBuilder),
                requestBlacklist(),
                contentTypeWhitelist
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
