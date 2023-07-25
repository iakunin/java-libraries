package dev.iakunin.library.persistence;

import dev.iakunin.library.persistence.testcontainer.PostgreSql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@Slf4j
@SuppressWarnings(
    {"PMD.AvoidThrowingRawExceptionTypes", "PMD.AbstractClassWithoutAbstractMethod", }
)
public abstract class AbstractIntegrationTest {

    protected static final Connection CONNECTION;

    private static final PostgreSQLContainer<?> PSQL_CONTAINER = new PostgreSql<>();

    // For more info about this `static` block see
    // https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/
    static {
        PSQL_CONTAINER.start();

        try {
            CONNECTION = DriverManager.getConnection(
                PSQL_CONTAINER.getJdbcUrl(),
                PSQL_CONTAINER.getUsername(),
                PSQL_CONTAINER.getPassword()
            );
        } catch (SQLException ex) {
            log.error("Exception during `getConnection`", ex);
            throw new RuntimeException(ex);
        }
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", PSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", PSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", PSQL_CONTAINER::getPassword);
    }
}
