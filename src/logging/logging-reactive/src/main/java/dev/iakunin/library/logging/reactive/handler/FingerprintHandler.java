package dev.iakunin.library.logging.reactive.handler;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.service.MdcFingerprintService;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Slf4j
public final class FingerprintHandler implements HttpHandler {

    private final HttpHandler decorated;
    private final MdcFingerprintService mdcFingerprintService;
    private final String sessionFingerprintHeader;

    public FingerprintHandler(
        HttpHandler decorated,
        MdcFingerprintService mdcFingerprintService,
        Properties properties
    ) {
        super();
        this.decorated = decorated;
        this.mdcFingerprintService = mdcFingerprintService;
        this.sessionFingerprintHeader = properties.getSessionFingerprintHeader();
    }

    @Override
    public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
        mdcFingerprintService.set(resolveSessionFingerprint(request), generateRandomIdentifier());

        response.getHeaders().set(sessionFingerprintHeader, mdcFingerprintService.getSession());

        return decorated.handle(request, response)
            .then(Mono.defer(() -> {
                mdcFingerprintService.unset();
                return Mono.empty();
            }));
    }

    private String resolveSessionFingerprint(ServerHttpRequest request) {
        final Map<String, String> headers = request.getHeaders().toSingleValueMap();
        return StringUtils.hasText(headers.get(sessionFingerprintHeader))
            ? headers.get(sessionFingerprintHeader)
            : generateRandomIdentifier();
    }

    private String generateRandomIdentifier() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
