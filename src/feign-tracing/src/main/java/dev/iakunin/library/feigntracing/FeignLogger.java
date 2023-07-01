package dev.iakunin.library.feigntracing;

import feign.Request;
import feign.Response;
import feign.Util;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignLogger extends feign.Logger {

    protected final Logger logger;

    public FeignLogger() {
        this(feign.Logger.class);
    }

    public FeignLogger(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    public FeignLogger(Logger logger) {
        super();
        this.logger = logger;
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        // Not using SLF4J's support for parameterized messages (even though it would be more
        // efficient)
        // because it would require the incoming message formats to be SLF4J-specific.
        logger.info(String.format(methodTag(configKey) + format, args));
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        log(configKey, "FEIGN EXTERNAL REQUEST:\n%s", request.toString());
    }

    @Override
    protected Response logAndRebufferResponse(
        String configKey,
        Level logLevel,
        Response response,
        long elapsedTime
    ) throws IOException {
        // rebuild response so that response.toString outputs the actual content
        if (response.body() != null && !(response.status() == 204 || response.status() == 205)) {
            // HTTP 204 No Content "...response MUST NOT include a message-body"
            // HTTP 205 Reset Content "...response MUST NOT include an entity"
            final byte[] bodyData = Util.toByteArray(response.body().asInputStream());
            final var responseString =
                getResponseAsString(response)
                    + "\n"
                    + Util.decodeOrDefault(bodyData, Util.UTF_8, "Binary data");

            log(configKey, "FEIGN EXTERNAL RESPONSE:\n%s", responseString);

            return response.toBuilder().body(bodyData).build();
        }

        log(configKey, "FEIGN EXTERNAL RESPONSE:\n%s", getResponseAsString(response));

        return response;
    }

    @Override
    protected IOException logIOException(
        String configKey,
        Level logLevel,
        IOException ioe,
        long elapsedTime
    ) {
        logger.error(methodTag(configKey) + "FEIGN EXTERNAL ERROR:", ioe);

        return ioe;
    }

    private String getResponseAsString(Response response) {
        final StringBuilder builder = new StringBuilder("HTTP/1.1 ").append(response.status());
        if (response.reason() != null) {
            builder.append(' ').append(response.reason());
        }

        builder.append('\n');
        for (final String field : response.headers().keySet()) {
            for (final String value : Util.valuesOrEmpty(response.headers(), field)) {
                builder.append(field).append(": ").append(value).append('\n');
            }
        }

        return builder.toString();
    }
}
