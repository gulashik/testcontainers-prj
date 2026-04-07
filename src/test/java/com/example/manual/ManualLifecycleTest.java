package com.example.manual;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Пример 1: Использование аннотации @Testcontainers и @Container.
 * В этом режиме Testcontainers автоматически управляет жизненным циклом (start/stop)
 * для каждого тестового класса или метода (в зависимости от static/non-static).
 */
@Testcontainers
class ManualLifecycleTest {

    // static означает, что контейнер запустится один раз для всего класса
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.2-alpine3.19")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Test
    void testDatabaseInteraction() throws Exception {
        assertTrue(postgres.isRunning());

        // Получаем параметры подключения
        String jdbcUrl = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();

        // Попробуем выполнить реальный SQL-запрос
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
            try (Statement stmt = conn.createStatement()) {
                // Создаем таблицу
                stmt.execute("CREATE TABLE users (id SERIAL PRIMARY KEY, name VARCHAR(255))");

                // Вставляем данные
                stmt.execute("INSERT INTO users (name) VALUES ('Junie')");

                // Читаем данные
                try (ResultSet rs = stmt.executeQuery("SELECT name FROM users WHERE id = 1")) {
                    assertTrue(rs.next());
                    assertEquals("Junie", rs.getString("name"));
                }
            }
        }
    }
}
