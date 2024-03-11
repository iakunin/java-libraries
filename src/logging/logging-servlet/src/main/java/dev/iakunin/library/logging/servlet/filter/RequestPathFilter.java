package dev.iakunin.library.logging.servlet.filter;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.service.FieldTrimmer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

public final class RequestPathFilter extends OncePerRequestFilter {

    private final Properties.MdcKeys.Request requestMdcKeys;
    private final FieldTrimmer fieldTrimmer;

    public RequestPathFilter(Properties properties, FieldTrimmer fieldTrimmer) {
        super();
        this.requestMdcKeys = properties.getMdcKeys().getRequest();
        this.fieldTrimmer = fieldTrimmer;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        try {
            MDC.put(requestMdcKeys.getPath(), fieldTrimmer.trim(request.getRequestURI()));
            MDC.put(requestMdcKeys.getQueryString(), fieldTrimmer.trim(request.getQueryString()));

            chain.doFilter(request, response);
        } finally {
            MDC.remove(requestMdcKeys.getPath());
            MDC.remove(requestMdcKeys.getQueryString());
        }
    }

}
