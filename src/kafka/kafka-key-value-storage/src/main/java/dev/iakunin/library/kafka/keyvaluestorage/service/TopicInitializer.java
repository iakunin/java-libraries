package dev.iakunin.library.kafka.keyvaluestorage.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.InvalidReplicationFactorException;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.common.utils.Timer;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.util.TopicAdmin;

@Slf4j
@RequiredArgsConstructor
public class TopicInitializer implements Consumer<TopicAdmin> {

    private static final List<Class<? extends Exception>> CAUSES_TO_RETRY_TOPIC_CREATION =
        Arrays.asList(
            InvalidReplicationFactorException.class,
            TimeoutException.class
        );

    private final Map<String, Object> adminConfigs;
    private final NewTopic newTopic;
    private final Time time;

    @Override
    public void accept(TopicAdmin admin) {
        log.debug("Creating internal topic");
        // Create the topic if it doesn't exist
        final Set<String> newTopics = createTopics(newTopic, admin, time);

        if (!newTopics.contains(newTopic.name())) {
            // It already existed, so check that the topic cleanup policy is compact only and not
            // delete
            log.debug(
                "Using admin client to check cleanup policy of '{}' topic is '{}'",
                newTopic.name(),
                TopicConfig.CLEANUP_POLICY_COMPACT
            );

            final Set<String> cleanupPolicies = admin.topicCleanupPolicy(newTopic.name());
            if (cleanupPolicies.isEmpty()) {
                log.info(
                    "Unable to use admin client to verify the cleanup policy of '{}' "
                        + "topic is '{}', either because the broker is an older "
                        + "version or because the Kafka principal "
                        + "does not have the required permission to "
                        + "describe topic configurations.",
                    newTopic.name(),
                    TopicConfig.CLEANUP_POLICY_COMPACT
                );
                return;
            }
            final Set<String> expectedPolicies =
                Collections.singleton(TopicConfig.CLEANUP_POLICY_COMPACT);
            if (!cleanupPolicies.equals(expectedPolicies)) {
                final String expectedPolicyStr = String.join(",", expectedPolicies);
                final String cleanupPolicyStr = String.join(",", cleanupPolicies);
                final String msg =
                    String.format(
                        "Topic '%s' is required "
                            + "to have '%s=%s' to guarantee consistency and durability, "
                            + "but found the topic currently has '%s=%s'. Continuing would likely "
                            + "result in eventually losing data and problems restarting this  "
                            + "cluster in the future. Change the configurations to use a topic "
                            + "with '%s=%s'.",
                        newTopic.name(),
                        TopicConfig.CLEANUP_POLICY_CONFIG,
                        expectedPolicyStr,
                        TopicConfig.CLEANUP_POLICY_CONFIG,
                        cleanupPolicyStr,
                        TopicConfig.CLEANUP_POLICY_CONFIG,
                        expectedPolicyStr
                    );
                throw new ConfigException(msg);
            }
        }
    }

    private Set<String> createTopics(
        NewTopic topicDescription,
        TopicAdmin admin,
        Time time
    ) {
        final AdminClientConfig adminClientConfig = new AdminClientConfig(adminConfigs);
        return createTopicsWithRetry(
            admin,
            topicDescription,
            adminClientConfig.getLong(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG),
            adminClientConfig.getLong(AdminClientConfig.RETRY_BACKOFF_MS_CONFIG),
            time
        );
    }

    private Set<String> createTopicsWithRetry(
        TopicAdmin admin,
        NewTopic topicDescription,
        long timeoutMs,
        long backOffMs,
        Time time
    ) {
        final Timer timer = time.timer(timeoutMs);
        do {
            try {
                return admin.createTopics(topicDescription);
            } catch (ConnectException ex) {
                if (timer.notExpired() && retryableTopicCreationException(ex)) {
                    log.info(
                        "'{}' topic creation failed due to '{}', retrying, {}ms remaining",
                        topicDescription.name(),
                        ex.getMessage(),
                        timer.remainingMs()
                    );
                } else {
                    throw ex;
                }
            }
            timer.sleep(backOffMs);
        } while (timer.notExpired());
        throw new TimeoutException("Timeout expired while trying to create topic(s)");
    }

    private boolean retryableTopicCreationException(ConnectException ex) {
        // createTopics wraps the exception into ConnectException
        // to retry the creation, it should be an ExecutionException from future get which was
        // caused by InvalidReplicationFactorException or can be a TimeoutException
        Throwable cause = ex.getCause();
        while (cause != null) {
            final Throwable finalCause = cause;
            if (
                CAUSES_TO_RETRY_TOPIC_CREATION.stream()
                    .anyMatch(exceptionClass -> exceptionClass.isInstance(finalCause))
            ) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

}
