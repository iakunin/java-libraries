package dev.iakunin.library.logging.servlet.service;

import dev.iakunin.library.logging.servlet.configuration.Properties;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

public final class ContentTypeWhitelist {

    private final List<MediaType> whitelist;

    public ContentTypeWhitelist(Properties properties) {
        this.whitelist = properties.getContentTypeWhitelist();
    }

    public boolean isContentTypeInWhitelist(HttpServletRequest request) {
        final String requestContentType = request.getContentType();
        if (StringUtils.hasLength(requestContentType)) {
            try {
                return whitelist.stream().anyMatch(
                    type -> type.includes(
                        MediaType.parseMediaType(requestContentType)
                    )
                );
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }

        return false;
    }
}
