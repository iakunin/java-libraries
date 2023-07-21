package dev.iakunin.library.kafka.keyvaluestorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.iakunin.library.kafka.keyvaluestorage.exception.KafkaStorageException;
import dev.iakunin.library.kafka.keyvaluestorage.factory.StorageConfigsFactory;
import dev.iakunin.library.kafka.keyvaluestorage.wrapper.KafkaBasedLogWrapper;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.common.utils.Timer;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.connect.util.KafkaBasedLog;

@Slf4j
@SuppressWarnings(
    {"checkstyle:IllegalCatch", "PMD.AvoidLiteralsInIfCondition",
        "PMD.AvoidCatchingGenericException", }
)
public class KafkaKeyValueStorage<K, V> {

    private static final long READ_WRITE_TOTAL_TIMEOUT_MS = 30000;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Time time = Time.SYSTEM;

    private final String topicName;

    private final KafkaBasedLog<String, byte[]> kafkaBasedLog;

    private final Producer<String, byte[]> producer;

    // Main hash-map
    private final Map<K, V> map = new ConcurrentHashMap<>();

    public KafkaKeyValueStorage(
        String bootstrapServers,
        String topicName,
        Class<K> keyType,
        Class<V> valueType
    ) {
        this.topicName = topicName;
        if (this.topicName == null || this.topicName.isBlank()) {
            throw new KafkaStorageException("topicName must be specified.");
        }

        final var configsFactory = new StorageConfigsFactory(bootstrapServers, topicName);

        this.producer = new KafkaProducer<>(configsFactory.producerConfigs());

        this.kafkaBasedLog = new KafkaBasedLogWrapper<>(
            this.topicName,
            this.map,
            this.time,
            configsFactory,
            keyType,
            valueType
        );
    }

    public void start() {
        log.info("Starting " + this.getClass().getSimpleName());

        try {
            kafkaBasedLog.start();
        } catch (UnsupportedVersionException ex) {
            throw new KafkaStorageException(
                "Enabling exactly-once support requires a Kafka broker "
                    + "version that allows admin clients to read consumer offsets. "
                    + "Please use a newer Kafka broker version.",
                ex
            );
        }

        final int partitionCount = kafkaBasedLog.partitionCount();
        if (partitionCount > 1) {
            throw new KafkaStorageException(
                String.format(
                    "Topic '%s' supplied via constructor property is required "
                        + "to have a single partition in order to guarantee date consistency, "
                        + "but found %d partitions.",
                    topicName,
                    partitionCount
                )
            );
        }

        try {
            producer.initTransactions();
        } catch (Exception ex) {
            throw new KafkaStorageException("Failed to initialize the producer", ex);
        }

        log.info("Started " + this.getClass().getSimpleName());
    }

    public void stop() {
        log.info("Closing " + this.getClass().getSimpleName());

        Utils.closeQuietly(kafkaBasedLog::stop, "kafkaBasedLog");
        Utils.closeQuietly(() -> producer.close(Duration.ZERO), "producer");

        log.info("Closed " + this.getClass().getSimpleName());
    }

    /**
     * Write date to persistent storage and wait until it has been acknowledged and read back by
     * tailing the Kafka log with a consumer.
     *
     * @param key name of the key to write data for
     * @param value the value to write
     */
    public void put(K key, V value) {
        log.debug("Writing value for key='{}'", key);

        try {
            final Timer timer = time.timer(READ_WRITE_TOTAL_TIMEOUT_MS);
            sendSynchronously(key, value, timer);
            kafkaBasedLog.readToEnd().get(timer.remainingMs(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            log.error("Failed to write data to Kafka: ", ex);
            throw new KafkaStorageException("Error writing data to Kafka", ex);
        }
    }

    public Map<K, V> getAll() {
        return new ConcurrentHashMap<>(map);
    }

    public V get(K key) {
        log.debug("Getting value by key='{}'", key);

        try {
            final Timer timer = time.timer(READ_WRITE_TOTAL_TIMEOUT_MS);
            kafkaBasedLog.readToEnd().get(timer.remainingMs(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            log.error("Failed to get data from Kafka: ", ex);
            throw new KafkaStorageException("Error getting data from Kafka", ex);
        }

        return map.get(key);
    }

    /**
     * Remove data for a given key.
     *
     * @param key name of the key to remove
     */
    public void remove(K key) {
        log.debug("Removing by key='{}'", key);
        try {
            final Timer timer = time.timer(READ_WRITE_TOTAL_TIMEOUT_MS);
            sendSynchronously(key, null, timer);
            kafkaBasedLog.readToEnd().get(timer.remainingMs(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            log.error("Failed to remove data from Kafka: ", ex);
            throw new KafkaStorageException("Error removing data from Kafka", ex);
        }
    }

    public void refresh(long timeout, TimeUnit unit) throws TimeoutException {
        try {
            kafkaBasedLog.readToEnd().get(timeout, unit);
        } catch (InterruptedException | ExecutionException ex) {
            throw new KafkaStorageException("Error trying to read to end of kafkaBasedLog", ex);
        }
    }

    /**
     * Send one record to the topic synchronously.
     *
     * @param key key
     * @param value value
     * @param timer Timer bounding how long this method can block. The timer is updated before the
     *        method returns.
     */
    private void sendSynchronously(K key, V value, Timer timer)
        throws ExecutionException, InterruptedException, TimeoutException {

        final String serializedKey;
        try {
            serializedKey = objectMapper.writeValueAsString(key);
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize key: ", ex);
            return;
        }

        final byte[] serializedValue;
        try {
            serializedValue = objectMapper.writeValueAsBytes(value);
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize value: ", ex);
            return;
        }

        producer.beginTransaction();
        producer.send(new ProducerRecord<>(topicName, serializedKey, serializedValue))
            .get(timer.remainingMs(), TimeUnit.MILLISECONDS);
        producer.commitTransaction();
        timer.update();
    }

}
