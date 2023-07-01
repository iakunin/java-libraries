package dev.iakunin.library.logging.logger;

import dev.iakunin.library.logging.configuration.Properties;
import dev.iakunin.library.logging.service.ContentTypeWhitelist;
import dev.iakunin.library.logging.service.FieldTrimmer;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public final class RequestLogger {

    private final Properties.MdcKeys.Request requestMdcKeys;
    private final FieldTrimmer fieldTrimmer;
    private final ContentTypeWhitelist contentTypeWhitelist;

    public RequestLogger(
        Properties properties,
        FieldTrimmer fieldTrimmer,
        ContentTypeWhitelist contentTypeWhitelist
    ) {
        this.requestMdcKeys = properties.getMdcKeys().getRequest();
        this.fieldTrimmer = fieldTrimmer;
        this.contentTypeWhitelist = contentTypeWhitelist;
    }

    public void log(HttpServletRequest request) throws IOException {
        try {
            MDC.put(requestMdcKeys.getMethod(), request.getMethod());
            if (shouldBodyBeLogged(request)) {
                MDC.put(requestMdcKeys.getBody(), fieldTrimmer.trim(buildBody(request)));
            }
            MDC.put(requestMdcKeys.getHeaders(), fieldTrimmer.trim(buildHeaders(request)));
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

    private String buildHeaders(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream().distinct().flatMap(
            headerName -> Collections.list(request.getHeaders(headerName)).stream().map(
                headerValue -> String.format("%s: \"%s\"", headerName, headerValue)
            )
        ).collect(
            Collectors.joining(System.lineSeparator())
        );
    }
}
