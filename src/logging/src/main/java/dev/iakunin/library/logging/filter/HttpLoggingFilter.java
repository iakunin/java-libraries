package dev.iakunin.library.logging.filter;

import dev.iakunin.library.logging.logger.RequestLogger;
import dev.iakunin.library.logging.logger.ResponseLogger;
import dev.iakunin.library.logging.service.ContentTypeWhitelist;
import dev.iakunin.library.logging.wrapper.ContentCachingRequestWrapper;
import dev.iakunin.library.logging.wrapper.StreamingAwareContentCachingResponseWrapper;
import java.io.IOException;
import java.time.Duration;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
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
        final var requestWrapper = wrapRequest(request);

        if (requestBlacklist.matches(requestWrapper)) {
            filterChain.doFilter(requestWrapper, response);
            return;
        }

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
            return new ContentCachingRequestWrapper(request);
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
            return new StreamingAwareContentCachingResponseWrapper(response, request);
        }
    }

    private boolean shouldBeWrapped(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return false;
        }

        return contentTypeWhitelist.isContentTypeInWhitelist(request);
    }

}
