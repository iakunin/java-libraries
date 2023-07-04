package dev.iakunin.library.logging.reactive.adapter;

import dev.iakunin.library.logging.common.logger.RequestLogger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public final class RequestLoggerAdapter {

    private final RequestLogger requestLogger;

    public void log(ServerHttpRequest request, String body) {
        requestLogger.log(
            RequestLogger.Arguments.builder()
                .method(buildMethod(request))
                .headers(request.getHeaders())
                .contentType(request.getHeaders().getContentType())
                .clientIp(buildClientIp(request))
                .body(body)
                .build()
        );
    }

    private static String buildMethod(ServerHttpRequest request) {
        return Optional.ofNullable(request.getMethod())
            .map(HttpMethod::name)
            .orElseGet(() -> {
                log.warn("Unknown request.method: '{}'", request.getMethod());
                return "not_resolvable";
            });
    }

    private String buildClientIp(ServerHttpRequest request) {
        return Optional.ofNullable(request.getRemoteAddress())
            .map(InetSocketAddress::getAddress)
            .map(InetAddress::getHostAddress)
            .orElse("not_resolvable");
    }

}
