package dev.iakunin.library.logging.reactive.adapter;

import dev.iakunin.library.logging.common.logger.ResponseLogger;
import java.time.Duration;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class ResponseLoggerAdapter {

    private final ResponseLogger responseLogger;

    public void log(ServerHttpResponse response, String body, Duration duration) {
        responseLogger.log(
            ResponseLogger.Arguments.builder()
                .statusCode(Objects.requireNonNull(response.getStatusCode()))
                .headers(response.getHeaders())
                .body(body)
                .duration(duration)
                .build()
        );
    }

}
