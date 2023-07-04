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
import org.springframework.util.StringUtils;

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
        if (StringUtils.hasLength(request.getContentType())) {
            return MediaType.parseMediaType(request.getContentType());
        }

        return null;
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
