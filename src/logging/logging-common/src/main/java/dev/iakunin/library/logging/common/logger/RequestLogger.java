package dev.iakunin.library.logging.common.logger;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.service.ContentTypeWhitelist;
import dev.iakunin.library.logging.common.service.FieldTrimmer;
import dev.iakunin.library.logging.common.service.HeadersBuilder;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

@Slf4j
public final class RequestLogger {

    private final Properties.MdcKeys.Request requestMdcKeys;
    private final FieldTrimmer fieldTrimmer;
    private final HeadersBuilder headersBuilder;
    private final ContentTypeWhitelist contentTypeWhitelist;

    public RequestLogger(Properties properties) {
        this.requestMdcKeys = properties.getMdcKeys().getRequest();
        this.fieldTrimmer = new FieldTrimmer(properties);
        this.headersBuilder = new HeadersBuilder();
        this.contentTypeWhitelist = new ContentTypeWhitelist(properties);
    }

    public void log(Arguments arguments) {
        try {
            MDC.put(requestMdcKeys.getMethod(), arguments.getMethod());

            MDC.put(
                requestMdcKeys.getHeaders(),
                fieldTrimmer.trim(headersBuilder.build(arguments.getHeaders()))
            );

            MDC.put(requestMdcKeys.getClientIp(), arguments.getClientIp());

            if (contentTypeWhitelist.contains(arguments.getContentType())) {
                MDC.put(requestMdcKeys.getBody(), fieldTrimmer.trim(arguments.getBody()));
            }

            log.info("HTTP REQUEST");
        } finally {
            MDC.remove(requestMdcKeys.getMethod());
            MDC.remove(requestMdcKeys.getHeaders());
            MDC.remove(requestMdcKeys.getClientIp());
            MDC.remove(requestMdcKeys.getBody());
        }
    }

    @Builder
    @Value
    public static class Arguments {
        @NonNull
        String method;
        @NonNull
        Map<String, List<String>> headers;
        @Nullable
        MediaType contentType;
        @NonNull
        String clientIp;
        @NonNull
        String body;
    }

}
