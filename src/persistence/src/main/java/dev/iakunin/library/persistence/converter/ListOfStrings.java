package dev.iakunin.library.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * Has been taken from here: https://stackoverflow.com/a/50448142/3456163
 */
@Converter
public final class ListOfStrings implements AttributeConverter<List<String>, String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        return stringList != null ? String.join(DELIMITER, stringList) : "";
    }

    @Override
    public List<String> convertToEntityAttribute(String string) {
        return string != null ? Arrays.asList(string.split(DELIMITER)) : Collections.emptyList();
    }
}
