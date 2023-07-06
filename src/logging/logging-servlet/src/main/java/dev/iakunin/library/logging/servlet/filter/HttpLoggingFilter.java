package dev.iakunin.library.logging.servlet.filter;

import dev.iakunin.library.logging.servlet.adapter.RequestLoggerAdapter;
import dev.iakunin.library.logging.servlet.adapter.ResponseLoggerAdapter;
import dev.iakunin.library.logging.servlet.wrapper.StreamingAwareContentCachingResponseWrapper;
import java.io.IOException;
import java.time.Duration;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@RequiredArgsConstructor
public final class HttpLoggingFilter extends OncePerRequestFilter {

    private final RequestLoggerAdapter requestLogger;
    private final ResponseLoggerAdapter responseLogger;
    private final RequestMatcher requestBlacklist;

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
        if (isForm(request)) {
            return request;
        }

        if (request instanceof ContentCachingRequestWrapper) {
            return request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    private boolean isForm(HttpServletRequest request) {
        final String requestContentType = request.getContentType();
        if (StringUtils.hasLength(requestContentType)) {
            try {
                return MediaType.APPLICATION_FORM_URLENCODED.includes(
                    MediaType.parseMediaType(requestContentType)
                );
            } catch (IllegalArgumentException ex) {
                log.debug("Unable to parse MediaType from '{}'", request.getContentType(), ex);
            }
        }

        return false;
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

}
