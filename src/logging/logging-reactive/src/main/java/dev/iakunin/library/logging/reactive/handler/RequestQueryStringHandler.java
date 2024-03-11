package dev.iakunin.library.logging.reactive.handler;

import dev.iakunin.library.logging.common.service.FieldTrimmer;
import dev.iakunin.library.logging.reactive.wrapper.ContextWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public final class RequestQueryStringHandler implements HttpHandler {

    private final HttpHandler decorated;
    private final FieldTrimmer fieldTrimmer;
    private final ContextWrapper contextWrapper;

    @Override
    public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
        return decorated.handle(request, response)
            .contextWrite(
                context -> contextWrapper
                    .putRequestQueryString(
                        context,
                        fieldTrimmer.trim(
                            request.getURI().getQuery()
                        )
                    )
            );
    }

}
