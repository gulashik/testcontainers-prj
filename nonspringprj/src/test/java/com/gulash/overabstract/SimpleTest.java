package com.gulash.overabstract;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleTest extends AbstractTestBase {
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
}
