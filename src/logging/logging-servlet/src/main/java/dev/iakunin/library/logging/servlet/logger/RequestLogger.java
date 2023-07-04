package dev.iakunin.library.logging.servlet.logger;

import dev.iakunin.library.logging.servlet.configuration.Properties;
import dev.iakunin.library.logging.servlet.service.ContentTypeWhitelist;
import dev.iakunin.library.logging.servlet.service.FieldTrimmer;
import dev.iakunin.library.logging.servlet.service.HeadersBuilder;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public final class RequestLogger {

    private final Properties.MdcKeys.Request requestMdcKeys;
    private final FieldTrimmer fieldTrimmer;
    private final ContentTypeWhitelist contentTypeWhitelist;
    private final HeadersBuilder headersBuilder;

    public RequestLogger(
        Properties properties,
        FieldTrimmer fieldTrimmer,
        ContentTypeWhitelist contentTypeWhitelist,
        HeadersBuilder headersBuilder
    ) {
        this.requestMdcKeys = properties.getMdcKeys().getRequest();
        this.fieldTrimmer = fieldTrimmer;
        this.contentTypeWhitelist = contentTypeWhitelist;
        this.headersBuilder = headersBuilder;
    }

    public void log(HttpServletRequest request) throws IOException {
        try {
            MDC.put(requestMdcKeys.getMethod(), request.getMethod());
            if (shouldBodyBeLogged(request)) {
                MDC.put(requestMdcKeys.getBody(), fieldTrimmer.trim(buildBody(request)));
            }
            MDC.put(requestMdcKeys.getHeaders(), fieldTrimmer.trim(headersBuilder.build(request)));
            MDC.put(requestMdcKeys.getClientIp(), request.getRemoteAddr());

            log.info("HTTP REQUEST");
        } finally {
            MDC.remove(requestMdcKeys.getMethod());
            MDC.remove(requestMdcKeys.getBody());
            MDC.remove(requestMdcKeys.getHeaders());
            MDC.remove(requestMdcKeys.getClientIp());
        }
    }

    private boolean shouldBodyBeLogged(HttpServletRequest request) {
        return contentTypeWhitelist.isContentTypeInWhitelist(request);
    }

    private String buildBody(HttpServletRequest request) throws IOException {
        final var paramMap = request.getParameterMap();

        if (!paramMap.isEmpty()) {
            return paramMap.entrySet().stream()
                .map(
                    entry -> String.format(
                        "%s: \"%s\"",
                        entry.getKey(),
                        Arrays.toString(entry.getValue())
                    )
                )
                .collect(
                    Collectors.joining(System.lineSeparator())
                );
        }

        return request.getReader().lines().collect(
            Collectors.joining(System.lineSeparator())
        );
    }

}
