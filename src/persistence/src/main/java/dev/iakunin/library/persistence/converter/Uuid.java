package dev.iakunin.library.persistence.converter;

import java.util.UUID;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
