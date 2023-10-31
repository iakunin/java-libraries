package dev.iakunin.library.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.UUID;

@Converter
public final class Uuid implements AttributeConverter<UUID, String> {

    @Override
    public String convertToDatabaseColumn(UUID attribute) {
        return attribute != null
            ? attribute.toString()
            : null;
    }

    @Override
    public UUID convertToEntityAttribute(String dbData) {
        return dbData != null
            ? UUID.fromString(dbData)
            : null;
    }
}
