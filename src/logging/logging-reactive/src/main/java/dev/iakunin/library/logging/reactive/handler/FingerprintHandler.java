package dev.iakunin.library.logging.reactive.handler;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.service.MdcFingerprintService;
import dev.iakunin.library.logging.reactive.wrapper.ContextWrapper;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public final class FingerprintHandler implements HttpHandler {

    private final HttpHandler decorated;
    private final MdcFingerprintService mdcFingerprintService;
    private final String sessionFingerprintHeader;
    private final ContextWrapper contextWrapper;

    public FingerprintHandler(
        HttpHandler decorated,
        Properties properties,
        ContextWrapper contextWrapper
    ) {
        super();
        this.decorated = decorated;
        this.mdcFingerprintService = new MdcFingerprintService(properties);
        this.sessionFingerprintHeader = properties.getSessionFingerprintHeader();
        this.contextWrapper = contextWrapper;
    }

    @Override
    public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
        return decorated.handle(request, response)
            .contextWrite(context -> {
                final String sessionFingerprint = resolveSessionFingerprint(request);
                final String processFingerprint = generateRandomIdentifier();

                mdcFingerprintService.set(sessionFingerprint, processFingerprint);
                response.getHeaders().set(sessionFingerprintHeader, sessionFingerprint);

                return context.putAll(
                    contextWrapper.putSessionFingerprint(context, sessionFingerprint)
                        .putAll(
                            contextWrapper.putProcessFingerprint(context, processFingerprint)
                                .readOnly()
                        ).readOnly()
                );
            });
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
