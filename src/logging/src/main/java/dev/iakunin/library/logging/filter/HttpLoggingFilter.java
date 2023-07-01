package dev.iakunin.library.logging.filter;

import dev.iakunin.library.logging.logger.RequestLogger;
import dev.iakunin.library.logging.logger.ResponseLogger;
import dev.iakunin.library.logging.service.ContentTypeWhitelist;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@RequiredArgsConstructor
public final class HttpLoggingFilter extends OncePerRequestFilter {

    private final RequestLogger requestLogger;
    private final ResponseLogger responseLogger;
    private final RequestMatcher requestBlacklist;
    private final ContentTypeWhitelist contentTypeWhitelist;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        if (requestBlacklist.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        final var requestWrapper = wrapRequest(request);
        final var responseWrapper = wrapResponse(response, requestWrapper);

        doFilterWrapped(requestWrapper, responseWrapper, filterChain);
    }

    private void doFilterWrapped(
        HttpServletRequest request,
        ContentCachingResponseWrapper response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        final StopWatch watch = new StopWatch();
        requestLogger.log(request);

        try {
            watch.start();
            filterChain.doFilter(request, response);
        } finally {
            watch.stop();
            responseLogger.log(response, Duration.ofNanos(watch.getLastTaskTimeNanos()));
            response.copyBodyToResponse();
        }
    }

    private HttpServletRequest wrapRequest(HttpServletRequest request) {
        if (shouldBeWrapped(request)) {
            return new MultiReadHttpServletRequest(request);
        }

        return request;
    }

    private ContentCachingResponseWrapper wrapResponse(
        HttpServletResponse response,
        HttpServletRequest request
    ) {
        if (isAsyncDispatch(request) || response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new HttpStreamingAwareContentCachingResponseWrapper(response, request);
        }
    }

    private boolean shouldBeWrapped(HttpServletRequest request) {
        if (request instanceof MultiReadHttpServletRequest) {
            return false;
        }

        return contentTypeWhitelist.isContentTypeInWhitelist(request);
    }

    private static class HttpStreamingAwareContentCachingResponseWrapper
        extends ContentCachingResponseWrapper {

        private final HttpServletRequest request;

        HttpStreamingAwareContentCachingResponseWrapper(
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
}
