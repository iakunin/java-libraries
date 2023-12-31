package dev.iakunin.library.logging.reactive.wrapper;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

@SuppressWarnings("PMD.AvoidStringBufferField")
public final class BodyCaptureRequest extends ServerHttpRequestDecorator {

    private final StringBuilder body = new StringBuilder();

    public BodyCaptureRequest(ServerHttpRequest delegate) {
        super(delegate);
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return super.getBody().doOnNext(this::capture);
    }

    private void capture(DataBuffer dataBuffer) {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(dataBuffer.readableByteCount());
        dataBuffer.toByteBuffer(byteBuffer);
        body.append(StandardCharsets.UTF_8.decode(byteBuffer));
    }

    public String getFullBody() {
        return body.toString();
    }
}
