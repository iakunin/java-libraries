package dev.iakunin.library.logging.reactive.handler;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.service.FieldTrimmer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

@Slf4j
public final class RequestPathHandler implements HttpHandler {

    private final HttpHandler decorated;
    private final Properties.MdcKeys.Request requestMdcKeys;
    private final Boolean logQueryString;
    private final FieldTrimmer fieldTrimmer;

    public RequestPathHandler(HttpHandler decorated, Properties properties) {
        super();
        this.decorated = decorated;
        this.requestMdcKeys = properties.getMdcKeys().getRequest();
        this.logQueryString = properties.isLogQueryString();
        this.fieldTrimmer = new FieldTrimmer(properties);
    }

    @Override
    public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
        MDC.put(requestMdcKeys.getPath(), fieldTrimmer.trim(buildPath(request)));

        return decorated.handle(request, response)
            .then(Mono.defer(() -> {
                MDC.remove(requestMdcKeys.getPath());
                return Mono.empty();
            }));
    }

    private String buildPath(ServerHttpRequest request) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(request.getURI().getPath());

        final String queryString = request.getURI().getQuery();
        if (queryString != null && logQueryString) {
            stringBuilder.append('?').append(queryString);
        }

        return stringBuilder.toString();
    }

}
