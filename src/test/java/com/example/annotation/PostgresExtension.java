package com.example.annotation;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Расширение для JUnit 5, которое гарантирует запуск контейнера один раз (Singleton).
 */
public class PostgresExtension implements BeforeAllCallback {

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.2-alpine3.19")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("postgres");

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!postgres.isRunning()) {
            postgres.start();
            
            // Установка системных свойств для тестов (аналогично тому, как это делает Spring)
            System.setProperty("DB_URL", postgres.getJdbcUrl());
            System.setProperty("DB_USER", postgres.getUsername());
            System.setProperty("DB_PASS", postgres.getPassword());
        }
    }

    public static PostgreSQLContainer<?> getPostgres() {
        return postgres;
    }
}
