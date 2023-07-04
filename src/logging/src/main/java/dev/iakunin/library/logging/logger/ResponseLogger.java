package dev.iakunin.library.logging.logger;

import dev.iakunin.library.logging.configuration.Properties;
import dev.iakunin.library.logging.service.FieldTrimmer;
import dev.iakunin.library.logging.service.HeadersBuilder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

@Slf4j
public final class ResponseLogger {

    private final Properties.MdcKeys.Response responseMdcKeys;
    private final FieldTrimmer fieldTrimmer;
    private final HeadersBuilder headersBuilder;

    public ResponseLogger(
        Properties properties,
        FieldTrimmer fieldTrimmer,
        HeadersBuilder headersBuilder
    ) {
        this.responseMdcKeys = properties.getMdcKeys().getResponse();
        this.fieldTrimmer = fieldTrimmer;
        this.headersBuilder = headersBuilder;
    }

    public void log(HttpServletResponse response, Duration responseDuration) throws IOException {
        try {
            MDC.put(responseMdcKeys.getStatusCode(), String.valueOf(response.getStatus()));
            MDC.put(
                responseMdcKeys.getStatusPhrase(),
                HttpStatus.valueOf(response.getStatus()).getReasonPhrase()
            );
            MDC.put(
                responseMdcKeys.getHeaders(),
                fieldTrimmer.trim(headersBuilder.build(response))
            );
            MDC.put(responseMdcKeys.getBody(), fieldTrimmer.trim(buildBody(response)));
            MDC.put(responseMdcKeys.getDurationMs(), String.valueOf(responseDuration.toMillis()));

            log.info("HTTP RESPONSE");
        } finally {
            MDC.remove(responseMdcKeys.getStatusCode());
            MDC.remove(responseMdcKeys.getStatusPhrase());
            MDC.remove(responseMdcKeys.getHeaders());
            MDC.remove(responseMdcKeys.getBody());
            MDC.remove(responseMdcKeys.getDurationMs());
        }
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
