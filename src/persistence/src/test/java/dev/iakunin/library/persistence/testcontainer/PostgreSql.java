package dev.iakunin.library.persistence.testcontainer;

import java.time.Duration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public final class PostgreSql<T extends PostgreSQLContainer<T>> extends PostgreSQLContainer<T> {
    public PostgreSql() {
        super(
            DockerImageName.parse(
                "postgres:14.2"
            ).asCompatibleSubstituteFor("postgres")
        );
        withStartupTimeout(Duration.ofSeconds(90));
        withCommand("postgres", "-c", "log_statement=all");
        withNetworkAliases("db");
    }
}
