package com.gulash.overabstract;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Базовый класс для тестов, использующих Testcontainers.
 * 1. Использование статического контейнера позволяет инициализировать его один раз.
 * 2. Жизненный цикл в `@BeforeAll` и `@AfterAll` гарантирует запуск один раз на весь тестовый класс,
 *    что значительно быстрее, чем запуск перед каждым методом (`@BeforeEach`).
 * 3. Настройка системных свойств (DB_URL и др.) позволяет тестам (например, через JDBC или Spring)
 *    автоматически узнавать актуальный порт и адрес БД.
 */
public abstract class AbstractTestBase {
    /**
     * Статический экземпляр контейнера PostgreSQL.
     * Контейнер будет общим для всех тестов в подклассе.
     */
    protected static final PostgreSQLContainer<?> postgres;

    static {
        // Инициализация в статическом блоке или @BeforeAll.
        // Здесь мы создаем объект, но запуск произойдет в @BeforeAll.
        postgres = new PostgreSQLContainer<>("postgres:16.2-alpine3.19")
                .withDatabaseName("maindb")
                .withUsername("admin")
                .withPassword("admin");
    }

    /**
     * Запуск контейнера перед выполнением всех тестов в классе.
     * Это оптимально, так как контейнер стартует один раз.
     */
    @BeforeAll
    public static void beforeAll() {
        System.out.println("=== Starting PostgreSQL Container (BeforeAll) ===");
        if (!postgres.isRunning()) {
            postgres.start();

            // Установка системных свойств, чтобы код тестов мог получить параметры подключения
            System.setProperty("DB_URL", postgres.getJdbcUrl());
            System.setProperty("DB_USER", postgres.getUsername());
            System.setProperty("DB_PASS", postgres.getPassword());
        }
    }

    @BeforeEach
    public void beforeEach() {
        System.out.println("=== BeforeEach: Container is running on " + postgres.getJdbcUrl() + " ===");
    }

    @AfterEach
    public void afterEach() {
        System.out.println("=== AfterEach ===");
    }

    /**
     * Остановка контейнера после всех тестов в классе.
     * Хотя Ryuk (внутренний механизм Testcontainers) сам удалит контейнеры при завершении JVM,
     * явная остановка в @AfterAll позволяет освободить ресурсы сразу после завершения класса.
     */
    @AfterAll
    public static void afterAll() {
        System.out.println("=== Stopping PostgreSQL Container (AfterAll) ===");
        // Если вы хотите переиспользовать контейнер между РАЗНЫМИ тестовыми классами (Singleton Pattern),
        // то stop() здесь вызывать НЕ НУЖНО.
        // Но для изоляции ресурсов между классами — вызываем.
        if (postgres != null && postgres.isRunning()) {
            postgres.stop();
        }
    }
}
