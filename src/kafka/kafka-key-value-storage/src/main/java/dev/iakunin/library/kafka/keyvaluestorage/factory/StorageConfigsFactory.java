package dev.iakunin.library.kafka.keyvaluestorage.factory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.IsolationLevel;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

@SuppressWarnings("PMD.UseConcurrentHashMap")
public class StorageConfigsFactory {

    private final Map<String, Object> commonConfigs;

    public StorageConfigsFactory(
        String bootstrapServers,
        String topicName
    ) {
        Objects.requireNonNull(bootstrapServers);
        Objects.requireNonNull(topicName);

        this.commonConfigs = new HashMap<>();
        this.commonConfigs.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        this.commonConfigs.put(CommonClientConfigs.GROUP_ID_CONFIG, topicName + "-group1");
        this.commonConfigs.put(CommonClientConfigs.CLIENT_ID_CONFIG, topicName + "-storage");
    }

    public Map<String, Object> producerConfigs() {
        final Map<String, Object> configs = new HashMap<>(commonConfigs);

        configs.put(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class.getName()
        );
        configs.put(
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            ByteArraySerializer.class.getName()
        );
        configs.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, Integer.MAX_VALUE);

        // Always require producer acks to all to ensure durable writes
        configs.put(ProducerConfig.ACKS_CONFIG, "all");

        // We can set this to 5 instead of 1 without risking reordering because we are using an
        // idempotent producer
        configs.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        configs.put(
            ProducerConfig.TRANSACTIONAL_ID_CONFIG,
            "transactional-producer-" + configs.get(CommonClientConfigs.GROUP_ID_CONFIG)
        );
        configs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        return configs;
    }

    public Map<String, Object> consumerConfigs() {
        final Map<String, Object> configs = new HashMap<>(commonConfigs);

        configs.put(
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class.getName()
        );
        configs.put(
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            ByteArrayDeserializer.class.getName()
        );

        // Always force reset to the beginning of the log since
        // we want to consume all available log data
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Turn off autocommit since we always want to consume the full log
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        configs.put(
            ConsumerConfig.ISOLATION_LEVEL_CONFIG,
            IsolationLevel.READ_COMMITTED.name().toLowerCase(Locale.ROOT)
        );

        return configs;
    }

    public Map<String, Object> adminConfigs() {
        return new HashMap<>(commonConfigs);
    }

}
