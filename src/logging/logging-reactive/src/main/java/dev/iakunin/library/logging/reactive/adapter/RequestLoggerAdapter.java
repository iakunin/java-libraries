package dev.iakunin.library.logging.reactive.adapter;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.logger.RequestLogger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public final class RequestLoggerAdapter {

    private final RequestLogger requestLogger;

    public RequestLoggerAdapter(Properties properties) {
        this.requestLogger = new RequestLogger(properties);
    }

    public void log(ServerHttpRequest request, String body) {
        requestLogger.log(
            RequestLogger.Arguments.builder()
                .method(buildMethod(request))
                .headers(request.getHeaders())
                .contentType(buildContentType(request))
                .clientIp(buildClientIp(request))
                .body(body)
                .build()
        );

        // try {
        //// MDC.put(requestMdcKeys.getMethod(), getHttpMethodName(request));
        //
        //// if (isContentTypeInWhitelist(request)) {
        //// MDC.put(requestMdcKeys.getBody(), fieldTrimmer.trim(body));
        //// }
        //
        //// MDC.put(
        //// requestMdcKeys.getHeaders(),
        //// fieldTrimmer.trim(headersBuilder.build(request.getHeaders()))
        //// );
        //
        //// MDC.put(requestMdcKeys.getClientIp(), buildClientIp(request));
        //
        // log.info("HTTP REQUEST");
        // } finally {
        // MDC.remove(requestMdcKeys.getMethod());
        // MDC.remove(requestMdcKeys.getBody());
        // MDC.remove(requestMdcKeys.getHeaders());
        // MDC.remove(requestMdcKeys.getClientIp());
        // }
    }

    private static String buildMethod(ServerHttpRequest request) {
        return Optional.ofNullable(request.getMethod())
            .map(HttpMethod::name)
            .orElseGet(() -> {
                log.warn("Unknown request.method: '{}'", request.getMethod());
                return "not_resolvable";
            });
    }

    private static MediaType buildContentType(ServerHttpRequest request) {
        return Objects.requireNonNull(request.getHeaders().getContentType());
    }

    private String buildClientIp(ServerHttpRequest request) {
        return Optional.ofNullable(request.getRemoteAddress())
            .map(InetSocketAddress::getAddress)
            .map(InetAddress::getHostAddress)
            .orElse("not_resolvable");
    }

}
