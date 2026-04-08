package com.example.annotation;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Расширение для JUnit 5, которое гарантирует запуск контейнера один раз (Singleton).
 */
public class PostgresExtension implements BeforeAllCallback, AfterAllCallback {

    // @SuppressWarnings("resource") используется, так как контейнер живет на протяжении всех тестов (Singleton)
    // и его не нужно закрывать через try-with-resources внутри методов.
    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.2-alpine3.19")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("postgres")
            //.withInitScript("sql/init.sql")
        ;

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

    @Override
    public void afterAll(ExtensionContext context) {
        // Контейнер будет остановлен после завершения всех тестов, использующих это расширение.
        // Хотя Testcontainers использует Ryuk для очистки, явная остановка — хороший тон.
        if (postgres != null && postgres.isRunning()) {
            postgres.stop(); // Если нужно останавливать контейнер после каждого тестового класса
        }
    }

    public static PostgreSQLContainer<?> getPostgres() {
        return postgres;
    }
}
