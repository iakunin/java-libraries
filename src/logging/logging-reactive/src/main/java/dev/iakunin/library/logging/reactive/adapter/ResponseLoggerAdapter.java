package dev.iakunin.library.logging.reactive.adapter;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.logger.ResponseLogger;
import java.time.Duration;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public final class ResponseLoggerAdapter {

    private final ResponseLogger responseLogger;

    public ResponseLoggerAdapter(Properties properties) {
        this.responseLogger = new ResponseLogger(properties);
    }

    public void log(ServerHttpResponse response, String body, Duration duration) {
        responseLogger.log(
            ResponseLogger.Arguments.builder()
                .statusCode(Objects.requireNonNull(response.getStatusCode()))
                .headers(response.getHeaders())
                .body(body)
                .duration(duration)
                .build()
        );

        // final HttpStatus status = Objects.requireNonNull(response.getStatusCode());
        //
        // try {
        // MDC.put(responseMdcKeys.getStatusCode(), String.valueOf(status.value()));
        // MDC.put(responseMdcKeys.getStatusPhrase(), status.getReasonPhrase());
        // MDC.put(
        // responseMdcKeys.getHeaders(),
        // fieldTrimmer.trim(headersBuilder.build(response.getHeaders()))
        // );
        // MDC.put(responseMdcKeys.getBody(), fieldTrimmer.trim(body));
        // MDC.put(responseMdcKeys.getDurationMs(), String.valueOf(duration.toMillis()));
        //
        // log.info("HTTP RESPONSE");
        // } finally {
        // MDC.remove(responseMdcKeys.getStatusCode());
        // MDC.remove(responseMdcKeys.getStatusPhrase());
        // MDC.remove(responseMdcKeys.getHeaders());
        // MDC.remove(responseMdcKeys.getBody());
        // MDC.remove(responseMdcKeys.getDurationMs());
        // }
    }

}
