package dev.iakunin.library.logging.reactive.handler;

import dev.iakunin.library.logging.reactive.adapter.RequestLoggerAdapter;
import dev.iakunin.library.logging.reactive.adapter.ResponseLoggerAdapter;
import dev.iakunin.library.logging.reactive.wrapper.BodyCaptureRequest;
import dev.iakunin.library.logging.reactive.wrapper.BodyCaptureResponse;
import dev.iakunin.library.logging.reactive.wrapper.LoggerWrapper;
import java.time.Duration;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public final class HttpLoggingHandler implements HttpHandler {

    private final RequestPredicate requestBlacklist;
    private final HttpHandler decorated;
    private final RequestLoggerAdapter requestLogger;
    private final ResponseLoggerAdapter responseLogger;
    private final LoggerWrapper loggerWrapper;

    @Override
    public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
        if (requestBlacklist.test(transform(request))) {
            return decorated.handle(request, response);
        }

        final StopWatch watch = new StopWatch();
        watch.start();

        final BodyCaptureRequest wrappedReq = new BodyCaptureRequest(request);
        final BodyCaptureResponse wrappedResp = new BodyCaptureResponse(response);

        return decorated.handle(wrappedReq, wrappedResp)
            .then(Mono.deferContextual(context -> {
                loggerWrapper.logWithinMDC(
                    () -> {
                        requestLogger.log(wrappedReq, wrappedReq.getFullBody());
                        watch.stop();
                        final Duration responseDuration =
                            Duration.ofNanos(watch.getLastTaskTimeNanos());

                        responseLogger.log(
                            wrappedResp,
                            wrappedResp.getFullBody(),
                            responseDuration
                        );
                    },
                    context
                );
                return Mono.empty();
            }));
    }

    private ServerRequest transform(ServerHttpRequest request) {
        return MockServerRequest.builder()
            .method(Objects.requireNonNull(request.getMethod()))
            .uri(request.getURI())
            .build();
    }
}
