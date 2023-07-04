package dev.iakunin.library.logging.reactive.handler;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.reactive.wrapper.ContextWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

@Slf4j
public final class RequestIdHandler implements HttpHandler {

    private final HttpHandler decorated;
    private final String requestIdHeaderName;
    private final ContextWrapper contextWrapper;

    public RequestIdHandler(
        HttpHandler decorated,
        Properties properties,
        ContextWrapper contextWrapper
    ) {
        super();
        this.decorated = decorated;
        this.requestIdHeaderName = properties.getRequestIdHeader();
        this.contextWrapper = contextWrapper;
    }

    @Override
    public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
        return decorated.handle(request, response)
            .contextWrite(context -> {
                final String requestId = request.getId();

                response.getHeaders().set(requestIdHeaderName, requestId);

                return context.putAll(
                    contextWrapper.putRequestId(context, requestId).readOnly()
                );
            });
    }

}
