package dev.iakunin.library.logging.reactive.handler;

import dev.iakunin.library.logging.common.configuration.Properties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

@Slf4j
public final class RequestIdHandler implements HttpHandler {

    private final HttpHandler decorated;
    private final Properties.MdcKeys.Request requestMdcKeys;
    private final String requestIdHeaderName;

    public RequestIdHandler(HttpHandler decorated, Properties properties) {
        super();
        this.decorated = decorated;
        this.requestMdcKeys = properties.getMdcKeys().getRequest();
        this.requestIdHeaderName = properties.getRequestIdHeader();
    }

    @Override
    public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
        MDC.put(requestMdcKeys.getId(), request.getId());

        response.getHeaders().set(requestIdHeaderName, request.getId());

        return decorated.handle(request, response)
            .then(Mono.defer(() -> {
                MDC.remove(requestMdcKeys.getId());
                return Mono.empty();
            }));
    }

}
