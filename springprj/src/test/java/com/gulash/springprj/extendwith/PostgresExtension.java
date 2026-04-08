package com.gulash.springprj.extendwith;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * JUnit 5 Extension для Spring-тестов: поднимает контейнер PostgreSQL и
 * прокидывает параметры подключения в Spring через системные свойства.
 *
 * Подходит для сценария, когда не хочется использовать @ServiceConnection,
 * а хочется явный пример через @ExtendWith(PostgresExtension.class).
 */
public class PostgresExtension implements BeforeAllCallback, AfterAllCallback {

    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.2-alpine3.19");

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!postgres.isRunning()) {
            postgres.start();
            // Прокидываем свойства, которые подхватит Spring Boot автонастройка DataSource
            System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
            System.setProperty("spring.datasource.username", postgres.getUsername());
            System.setProperty("spring.datasource.password", postgres.getPassword());
            // Для JPA/Hibernate создаём схему автоматически для примеров
            System.setProperty("spring.jpa.hibernate.ddl-auto", "create-drop");
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if (postgres != null && postgres.isRunning()) {
            postgres.stop();
        }
    }

    public static PostgreSQLContainer<?> getPostgres() {
        return postgres;
    }
}
