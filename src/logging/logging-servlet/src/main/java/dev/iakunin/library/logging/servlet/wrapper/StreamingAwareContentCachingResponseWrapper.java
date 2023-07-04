package dev.iakunin.library.logging.servlet.wrapper;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.DispatcherType;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
