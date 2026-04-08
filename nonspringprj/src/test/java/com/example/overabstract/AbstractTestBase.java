package com.example.overabstract;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Пример 2: Singleton-контейнер для всех тестов (Best Practice для больших проектов).
 * Вместо того чтобы перезапускать контейнер для каждого класса, мы запускаем его один раз
 * и переиспользуем. Testcontainers сам завершит его по окончании работы JVM (Ryuk).
 */
public abstract class AbstractTestBase {
    static final PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>("postgres:16.2-alpine3.19")
                .withDatabaseName("maindb")
                .withUsername("admin")
                .withPassword("admin")
                //.withInitScript("sql/init.sql")
                ;
        postgres.start();
    }
}
