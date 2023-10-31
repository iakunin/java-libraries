package dev.iakunin.library.logging.servlet.filter;

import dev.iakunin.library.logging.common.configuration.Properties;
import dev.iakunin.library.logging.common.service.MdcFingerprintService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public final class FingerprintFilter extends OncePerRequestFilter {

    private final MdcFingerprintService mdcFingerprintService;
    private final String sessionFingerprintHeader;

    public FingerprintFilter(MdcFingerprintService mdcFingerprintService, Properties properties) {
        super();
        this.mdcFingerprintService = mdcFingerprintService;
        this.sessionFingerprintHeader = properties.getSessionFingerprintHeader();
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        try {
            mdcFingerprintService.set(
                resolveSessionFingerprint(request),
                generateRandomIdentifier()
            );

            response.setHeader(sessionFingerprintHeader, mdcFingerprintService.getSession());

            chain.doFilter(request, response);
        } finally {
            mdcFingerprintService.unset();
        }
    }

    private String resolveSessionFingerprint(HttpServletRequest request) {
        return StringUtils.hasText(request.getHeader(sessionFingerprintHeader))
            ? request.getHeader(sessionFingerprintHeader)
            : generateRandomIdentifier();
    }

    private String generateRandomIdentifier() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
