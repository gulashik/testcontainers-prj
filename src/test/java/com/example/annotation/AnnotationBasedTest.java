package com.example.annotation;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Пример 3: Использование кастомной аннотации @IntegrationTest.
 * Контейнер настраивается и запускается через @ExtendWith в аннотации.
 */
@IntegrationTest
class AnnotationBasedTest {

    @Test
    void testAnnotationLifecycle() throws Exception {
        PostgreSQLContainer<?> postgres = PostgresExtension.getPostgres();
        assertTrue(postgres.isRunning());

        // Используем данные из системных свойств (установлены расширением)
        String jdbcUrl = System.getProperty("DB_URL");
        String username = System.getProperty("DB_USER");
        String password = System.getProperty("DB_PASS");

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE items (id SERIAL PRIMARY KEY, title VARCHAR(255))");
                stmt.execute("INSERT INTO items (title) VALUES ('Example Item')");

                try (ResultSet rs = stmt.executeQuery("SELECT title FROM items WHERE id = 1")) {
                    assertTrue(rs.next());
                    assertEquals("Example Item", rs.getString("title"));
                }
            }
        }
    }
}
