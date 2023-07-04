package dev.iakunin.library.logging.servlet.filter;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.service.FieldTrimmer;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

public final class RequestPathFilter extends OncePerRequestFilter {

    private final Properties.MdcKeys.Request requestMdcKeys;
    private final Boolean logQueryString;
    private final FieldTrimmer fieldTrimmer;

    public RequestPathFilter(Properties properties, FieldTrimmer fieldTrimmer) {
        super();
        this.requestMdcKeys = properties.getMdcKeys().getRequest();
        this.logQueryString = properties.isLogQueryString();
        this.fieldTrimmer = fieldTrimmer;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        try {
            MDC.put(requestMdcKeys.getPath(), fieldTrimmer.trim(buildPath(request)));

            chain.doFilter(request, response);
        } finally {
            MDC.remove(requestMdcKeys.getPath());
        }
    }

    private String buildPath(HttpServletRequest request) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(request.getRequestURI());

        final String queryString = request.getQueryString();
        if (queryString != null && logQueryString) {
            stringBuilder.append('?').append(queryString);
        }

        return stringBuilder.toString();
    }
}
