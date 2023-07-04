package dev.iakunin.library.logging.reactive.handler;

import dev.iakunin.library.logging.reactive.adapter.RequestLoggerAdapter;
import dev.iakunin.library.logging.reactive.adapter.ResponseLoggerAdapter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public final class HttpLoggingHandler implements HttpHandler {

    private final HttpHandler decorated;
    private final RequestLoggerAdapter requestLogger;
    private final ResponseLoggerAdapter responseLogger;
    private final RequestMatcher requestBlacklist;

    @Override
    public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
        if (requestBlacklist.matches(transform(request))) {
            return decorated.handle(request, response);
        }

        final StopWatch watch = new StopWatch();
        watch.start();

        final BodyCaptureRequest wrappedReq = new BodyCaptureRequest(request);
        final BodyCaptureResponse wrappedResp = new BodyCaptureResponse(response);

        return decorated.handle(wrappedReq, wrappedResp)
            .then(Mono.defer(() -> {
                requestLogger.log(wrappedReq, wrappedReq.getFullBody());
                watch.stop();
                final Duration responseDuration = Duration.ofNanos(watch.getLastTaskTimeNanos());
                responseLogger.log(wrappedResp, wrappedResp.getFullBody(), responseDuration);
                return Mono.empty();
            }));
    }

    private HttpServletRequest transform(ServerHttpRequest request) {
        final MockHttpServletRequest result = new MockHttpServletRequest();
        result.setMethod(Objects.requireNonNull(request.getMethod()).name());
        result.setServletPath(request.getURI().getPath());

        return result;
    }

    @SuppressWarnings("PMD.AvoidStringBufferField")
    private static class BodyCaptureRequest extends ServerHttpRequestDecorator {

        private final StringBuilder body = new StringBuilder();

        BodyCaptureRequest(ServerHttpRequest delegate) {
            super(delegate);
        }

        @Override
        public Flux<DataBuffer> getBody() {
            return super.getBody().doOnNext(this::capture);
        }

        private void capture(DataBuffer buffer) {
            body.append(StandardCharsets.UTF_8.decode(buffer.asByteBuffer()));
        }

        public String getFullBody() {
            return body.toString();
        }
    }

    @SuppressWarnings("PMD.AvoidStringBufferField")
    private static class BodyCaptureResponse extends ServerHttpResponseDecorator {

        private final StringBuilder body = new StringBuilder();

        BodyCaptureResponse(ServerHttpResponse delegate) {
            super(delegate);
        }

        @Override
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            final Flux<DataBuffer> buffer = Flux.from(body);
            return super.writeWith(buffer.doOnNext(this::capture));
        }

        private void capture(DataBuffer buffer) {
            body.append(StandardCharsets.UTF_8.decode(buffer.asByteBuffer()));
        }

        public String getFullBody() {
            return body.toString();
        }
    }

}
