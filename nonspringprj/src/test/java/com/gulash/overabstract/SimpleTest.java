package com.gulash.overabstract;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Пример теста, наследующего базовый класс с контейнером.
 * Тесты в этом классе используют общее подключение к одному и тому же экземпляру БД,
 * который стартует один раз перед всеми тестами этого класса.
 */
class SimpleTest extends AbstractTestBase {
    /**
     * Проверка подключения к БД через стандартный JDBC.
     */
    @Test
    void testDbConnectionInInheritedTest() throws Exception {
        assertTrue(postgres.isRunning());

        try (Connection conn = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword())) {

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT 1")) {
                    assertTrue(rs.next());
                }
            }
        }
    }

    @Test
    void testDbConnectionInInheritedTest2() throws Exception {
        assertTrue(postgres.isRunning());

        try (Connection conn = DriverManager.getConnection(
            postgres.getJdbcUrl(),
            postgres.getUsername(),
            postgres.getPassword())) {

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT 1")) {
                    assertTrue(rs.next());
                }
            }
        }
    }
}
