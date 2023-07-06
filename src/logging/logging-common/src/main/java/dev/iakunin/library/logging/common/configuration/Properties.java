package dev.iakunin.library.logging.common.configuration;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;

@SuppressWarnings(
    {
        "checkstyle:ExplicitInitialization",
        "PMD.RedundantFieldInitializer",
    }
)
@Data
@ConfigurationProperties("dev.iakunin.library.logging")
public final class Properties {

    private String sessionFingerprintHeader = "x-session-fingerprint";
    private String requestIdHeader = "x-server-request-id";
    private MdcKeys mdcKeys = new MdcKeys();
    private Integer fieldMaxLength = 20000;
    private boolean logQueryString = false;
    private List<MediaType> contentTypeWhitelist = List.of(
        new MediaType("text", "*"),
        MediaType.APPLICATION_JSON,
        MediaType.APPLICATION_FORM_URLENCODED
    );

    /**
     * excludePaths support Ant-style path patterns.
     * <p>
     * See following resources for better explanations and examples:
     * <ul>
     * <li><a href="https://ant.apache.org/manual/dirtasks.html#patterns">official ant doc</a></li>
     * <li><a href="https://stackoverflow.com/a/86915/3456163">stackoverflow</a></li>
     * </ul>
     */
    private List<String> excludePaths = List.of();

    @Data
    public static class MdcKeys {
        private Fingerprint fingerprint = new Fingerprint();
        private Request request = new Request();
        private Response response = new Response();

        @Data
        public static class Fingerprint {
            private String session = "session_fingerprint";
            private String process = "process_fingerprint";
        }

        @Data
        public static class Request {
            private String method = "request_method";
            private String path = "request_path";
            private String body = "request_body";
            private String headers = "request_headers";
            private String clientIp = "client_ip";
            private String id = "request_id";
        }

        @Data
        public static class Response {
            private String statusCode = "response_status_code";
            private String statusPhrase = "response_status_phrase";
            private String headers = "response_headers";
            private String body = "response_body";
            private String durationMs = "response_duration_ms";
        }
    }
}
