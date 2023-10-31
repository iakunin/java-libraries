package dev.iakunin.library.logging.servlet.adapter;

import dev.iakunin.library.logging.common.logger.ResponseLogger;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

@Slf4j
@RequiredArgsConstructor
public final class ResponseLoggerAdapter {

    private final ResponseLogger responseLogger;

    public void log(HttpServletResponse response, Duration duration) throws IOException {
        responseLogger.log(
            ResponseLogger.Arguments.builder()
                .statusCode(HttpStatus.valueOf(response.getStatus()))
                .headers(buildHeaders(response))
                .body(buildBody(response))
                .duration(duration)
                .build()
        );
    }

    private static Map<String, List<String>> buildHeaders(HttpServletResponse response) {
        return response.getHeaderNames()
            .stream()
            .distinct()
            .collect(
                Collectors.toMap(
                    headerName -> headerName,
                    headerName -> new ArrayList<>(response.getHeaders(headerName))
                )
            );
    }

    private String buildBody(HttpServletResponse response) throws IOException {
        final ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(
            response,
            ContentCachingResponseWrapper.class
        );

        if (wrapper == null) {
            final String error = "Empty wrapper during response body building";
            log.error(error);
            throw new IOException(error);
        }

        final byte[] content = wrapper.getContentAsByteArray();
        if (content.length == 0) {
            return "[empty]";
        }

        try {
            return new String(content, wrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException exception) {
            log.error("UnsupportedEncodingException during body building", exception);
            return "[unknown]";
        }
    }
}
