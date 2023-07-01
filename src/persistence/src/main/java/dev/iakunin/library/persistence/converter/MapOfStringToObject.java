package dev.iakunin.library.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jadira.usertype.spi.shared.AbstractMapUserType;

@Slf4j
public final class MapOfStringToObject extends AbstractMapUserType<String, Object> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected String toString(Map<String, Object> map) {
        try {
            return OBJECT_MAPPER.writeValueAsString(map);
        } catch (final JsonProcessingException ex) {
            log.error("JSON writing error", ex);
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, Object> toMap(String value) {
        try {
            return OBJECT_MAPPER.readValue(value, Map.class);
        } catch (final IOException ex) {
            log.error("JSON reading error", ex);
            return null;
        }
    }
}
