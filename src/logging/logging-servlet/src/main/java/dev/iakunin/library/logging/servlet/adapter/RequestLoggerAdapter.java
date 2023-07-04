package dev.iakunin.library.logging.servlet.adapter;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.logger.RequestLogger;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

@Slf4j
public final class RequestLoggerAdapter {

    private final RequestLogger requestLogger;

    public RequestLoggerAdapter(Properties properties) {
        this.requestLogger = new RequestLogger(properties);
    }

    public void log(HttpServletRequest request) throws IOException {
        requestLogger.log(
            RequestLogger.Arguments.builder()
                .method(request.getMethod())
                .headers(buildHeaders(request))
                .contentType(buildContentType(request))
                .clientIp(request.getRemoteAddr())
                .body(buildBody(request))
                .build()
        );

        // try {
        //// MDC.put(requestMdcKeys.getMethod(), request.getMethod());
        //// if (shouldBodyBeLogged(request)) {
        //// MDC.put(requestMdcKeys.getBody(), fieldTrimmer.trim(buildBody(request)));
        //// }
        //// MDC.put(requestMdcKeys.getHeaders(), fieldTrimmer.trim(headersBuilder.build(request)));
        //// MDC.put(requestMdcKeys.getClientIp(), request.getRemoteAddr());
        //
        //// log.info("HTTP REQUEST");
        // } finally {
        // MDC.remove(requestMdcKeys.getMethod());
        // MDC.remove(requestMdcKeys.getBody());
        // MDC.remove(requestMdcKeys.getHeaders());
        // MDC.remove(requestMdcKeys.getClientIp());
        // }
    }

    private static Map<String, List<String>> buildHeaders(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames())
            .stream()
            .distinct()
            .collect(
                Collectors.toMap(
                    headerName -> headerName,
                    headerName -> Collections.list(request.getHeaders(headerName))
                )
            );
    }

    private static MediaType buildContentType(HttpServletRequest request) {
        return MediaType.parseMediaType(request.getContentType());
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
