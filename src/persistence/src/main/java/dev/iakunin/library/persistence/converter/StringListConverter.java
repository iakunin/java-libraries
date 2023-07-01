package dev.iakunin.library.persistence.converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/*
 * Has been taken from here: https://stackoverflow.com/a/50448142/3456163
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
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
