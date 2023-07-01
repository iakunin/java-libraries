package dev.iakunin.library.logging.service;

import dev.iakunin.library.logging.configuration.Properties;

public final class FieldTrimmer {

    private final int maxLength;

    public FieldTrimmer(Properties properties) {
        this.maxLength = properties.getFieldMaxLength();
    }

    public String trim(String source) {
        return source.substring(
            0,
            Math.min(source.length(), maxLength)
        );
    }
}
