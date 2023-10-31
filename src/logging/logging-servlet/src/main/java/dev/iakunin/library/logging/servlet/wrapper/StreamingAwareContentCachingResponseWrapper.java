package dev.iakunin.library.logging.servlet.wrapper;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.web.util.ContentCachingResponseWrapper;

public final class StreamingAwareContentCachingResponseWrapper
    extends ContentCachingResponseWrapper {

    private final HttpServletRequest request;

    public StreamingAwareContentCachingResponseWrapper(
        HttpServletResponse response,
        HttpServletRequest request
    ) {
        super(response);
        this.request = request;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return useRawResponse() ? getResponse().getOutputStream() : super.getOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return useRawResponse() ? getResponse().getWriter() : super.getWriter();
    }

    private boolean useRawResponse() {
        return request.getDispatcherType().equals(DispatcherType.ASYNC);
    }
}
