package dev.iakunin.library.kafka.keyvaluestorage.wrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.iakunin.library.kafka.keyvaluestorage.factory.StorageConfigsFactory;
import dev.iakunin.library.kafka.keyvaluestorage.service.ConsumerCallback;
import dev.iakunin.library.kafka.keyvaluestorage.service.TopicInitializer;
import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.connect.util.Callback;
import org.apache.kafka.connect.util.KafkaBasedLog;
import org.apache.kafka.connect.util.SharedTopicAdmin;
import org.apache.kafka.connect.util.TopicAdmin;

@Slf4j
public class KafkaBasedLogWrapper<K, V> extends KafkaBasedLog<String, byte[]> {

    private final SharedTopicAdmin sharedTopicAdmin;
    private final Consumer<String, byte[]> consumer;

    public KafkaBasedLogWrapper(
        String topicName,
        Map<K, V> map,
        Time time,
        StorageConfigsFactory configsFactory
    ) {
        this(
            topicName,
            new KafkaConsumer<>(configsFactory.consumerConfigs()),
            new SharedTopicAdmin(configsFactory.adminConfigs()),
            new ConsumerCallback<>(new ObjectMapper(), map),
            time,
            new TopicInitializer(
                configsFactory.adminConfigs(),
                TopicAdmin.defineTopic(topicName)
                    .compacted()
                    .partitions(1)
                    .defaultReplicationFactor()
                    .build(),
                time
            )
        );
    }

    private KafkaBasedLogWrapper(
        String topic,
        Consumer<String, byte[]> consumer,
        SharedTopicAdmin sharedTopicAdmin,
        Callback<ConsumerRecord<String, byte[]>> consumedCallback,
        Time time,
        java.util.function.Consumer<TopicAdmin> initializer
    ) {
        super(
            topic,
            Collections.emptyMap(),
            Collections.emptyMap(),
            sharedTopicAdmin,
            consumedCallback,
            time,
            initializer
        );
        this.sharedTopicAdmin = sharedTopicAdmin;
        this.consumer = consumer;
    }

    @Override
    public void stop() {
        log.info("Closing " + this.getClass().getSimpleName());

        Utils.closeQuietly(sharedTopicAdmin, "sharedTopicAdmin");
        super.stop();

        log.info("Closed " + this.getClass().getSimpleName());
    }

    @Override
    protected Producer<String, byte[]> createProducer() {
        return null;
    }

    @Override
    protected Consumer<String, byte[]> createConsumer() {
        return consumer;
    }
}
