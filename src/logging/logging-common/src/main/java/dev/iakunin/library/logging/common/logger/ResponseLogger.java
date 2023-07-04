package dev.iakunin.library.logging.common.logger;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.service.FieldTrimmer;
import dev.iakunin.library.logging.common.service.HeadersBuilder;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public final class ResponseLogger {

    private final Properties.MdcKeys.Response responseMdcKeys;
    private final FieldTrimmer fieldTrimmer;
    private final HeadersBuilder headersBuilder;

    public ResponseLogger(Properties properties) {
        this.responseMdcKeys = properties.getMdcKeys().getResponse();
        this.fieldTrimmer = new FieldTrimmer(properties);
        this.headersBuilder = new HeadersBuilder();
    }

    public void log(Arguments arguments) {
        final HttpStatus status = Objects.requireNonNull(arguments.getStatusCode());

        try {
            MDC.put(responseMdcKeys.getStatusCode(), String.valueOf(status.value()));
            MDC.put(responseMdcKeys.getStatusPhrase(), status.getReasonPhrase());
            MDC.put(
                responseMdcKeys.getHeaders(),
                fieldTrimmer.trim(headersBuilder.build(arguments.getHeaders()))
            );
            MDC.put(responseMdcKeys.getBody(), fieldTrimmer.trim(arguments.getBody()));
            MDC.put(responseMdcKeys.getDurationMs(), String.valueOf(arguments.duration.toMillis()));

            log.info("HTTP RESPONSE");
        } finally {
            MDC.remove(responseMdcKeys.getStatusCode());
            MDC.remove(responseMdcKeys.getStatusPhrase());
            MDC.remove(responseMdcKeys.getHeaders());
            MDC.remove(responseMdcKeys.getBody());
            MDC.remove(responseMdcKeys.getDurationMs());
        }
    }

    @Builder
    @Value
    public static class Arguments {
        @NonNull
        HttpStatus statusCode;
        @NonNull
        Map<String, List<String>> headers;
        @NonNull
        String body;
        @NonNull
        Duration duration;
    }

}
