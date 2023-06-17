package com.example.multitenancy.common;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

public class DBContainerInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final PostgreSQLContainer<?> PRIMARY_POSTGRE_SQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:15.3-alpine")
                    .withDatabaseName("integration-tests-db")
                    .withUsername("username")
                    .withPassword("password");

    private static final MariaDBContainer<?> MARIA_DB_CONTAINER =
            new MariaDBContainer<>("mariadb:11.0")
                    .withDatabaseName("integration-tests-db")
                    .withUsername("username")
                    .withPassword("password");

    static {
        Startables.deepStart(PRIMARY_POSTGRE_SQL_CONTAINER, MARIA_DB_CONTAINER).join();
    }

    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertyValues.of(
                        "datasource.primary.url=" + PRIMARY_POSTGRE_SQL_CONTAINER.getJdbcUrl(),
                        "datasource.primary.username="
                                + PRIMARY_POSTGRE_SQL_CONTAINER.getUsername(),
                        "datasource.primary.password="
                                + PRIMARY_POSTGRE_SQL_CONTAINER.getPassword(),
                        "datasource.secondary.url=" + MARIA_DB_CONTAINER.getJdbcUrl(),
                        "datasource.secondary.username=" + MARIA_DB_CONTAINER.getUsername(),
                        "datasource.secondary.password=" + MARIA_DB_CONTAINER.getPassword())
                .applyTo(configurableApplicationContext.getEnvironment());
    }
}
