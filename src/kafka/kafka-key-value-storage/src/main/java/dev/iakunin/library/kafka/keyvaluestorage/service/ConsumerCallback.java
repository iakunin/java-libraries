package dev.iakunin.library.kafka.keyvaluestorage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.connect.util.Callback;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("checkstyle:WhitespaceAround")
public class ConsumerCallback<K, V> implements Callback<ConsumerRecord<String, byte[]>> {

    private final ObjectMapper objectMapper;
    private final Map<K, V> map;
    private final Class<K> keyType;
    private final Class<V> valueType;

    @Override
    public void onCompletion(Throwable error, ConsumerRecord<String, byte[]> record) {
        if (error != null) {
            log.error("Unexpected in consumer callback: ", error);
            return;
        }

        final K deserializedKey;
        try {
            deserializedKey = objectMapper.readValue(record.key(), keyType);
        } catch (JsonProcessingException ex) {
            log.error("Failed to deserialize key: ", ex);
            return;
        }

        final V deserializedValue;
        try {
            deserializedValue = objectMapper.readValue(record.value(), valueType);
        } catch (IOException ex) {
            log.error("Failed to deserialize value: ", ex);
            return;
        }

        if (record.value() == null) {
            log.info("Removing by key='{}'", deserializedKey);
            map.remove(deserializedKey);
        } else {
            log.info("Adding/Updating by key='{}'", deserializedKey);
            map.put(deserializedKey, deserializedValue);
        }
    }
}
