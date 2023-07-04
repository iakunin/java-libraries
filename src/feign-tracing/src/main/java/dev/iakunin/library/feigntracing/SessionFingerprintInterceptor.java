package dev.iakunin.library.feigntracing;

import dev.iakunin.library.logging.servlet.configuration.Properties;
import dev.iakunin.library.logging.servlet.service.MdcFingerprintService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.util.StringUtils;

public class SessionFingerprintInterceptor implements RequestInterceptor {

    private final String sessionFingerprintHeader;
    private final MdcFingerprintService mdcFingerprintService;

    public SessionFingerprintInterceptor(
        Properties properties,
        MdcFingerprintService mdcFingerprintService
    ) {
        this.sessionFingerprintHeader = properties.getSessionFingerprintHeader();
        this.mdcFingerprintService = mdcFingerprintService;
    }

    @Override
    public void apply(RequestTemplate template) {
        final var session = mdcFingerprintService.getSession();
        if (StringUtils.hasText(session)) {
            template.header(sessionFingerprintHeader, session);
        }
    }
}
