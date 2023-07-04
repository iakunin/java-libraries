package dev.iakunin.library.logging.service;

import dev.iakunin.library.logging.configuration.Properties;

public final class FieldTrimmer {

    private final int maxLength;

    public FieldTrimmer(Properties properties) {
        this.maxLength = properties.getFieldMaxLength();
    }

    public String trim(String source) {
        final boolean shouldBeTrimmed = source.length() > maxLength;

        if (shouldBeTrimmed) {
            return source.substring(0, maxLength) + "<TRIMMED>";
        }

        return source;
    }
}
