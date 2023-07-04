package dev.iakunin.library.logging.common.service;

import dev.iakunin.library.logging.common.configuration.Properties;
import java.util.List;
import org.springframework.http.MediaType;

public final class ContentTypeWhitelist {

    private final List<MediaType> whitelist;

    public ContentTypeWhitelist(Properties properties) {
        this.whitelist = properties.getContentTypeWhitelist();
    }

    public boolean contains(MediaType contentType) {
        return whitelist.stream().anyMatch(
            type -> type.includes(contentType)
        );
    }

}
