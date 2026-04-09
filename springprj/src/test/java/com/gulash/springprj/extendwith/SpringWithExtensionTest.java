package com.gulash.springprj.extendwith;

import com.gulash.springprj.User;
import com.gulash.springprj.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AnnotationIntegrationTest
public class SpringWithExtensionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        // Только для проверки, что контейнер поднят
        PostgreSQLContainer<?> postgres = PostgresExtension.getPostgres();
        assertTrue(postgres.isRunning());
    }

    @Test
    void testCrudThroughRepositoryAndJdbcTemplate() {
        User user = new User("Jane Roe", "jane.roe@example.com");
        User saved = userRepository.save(user);
        assertTrue(saved.getId() > 0);

        Optional<User> byId = userRepository.findById(saved.getId());
        assertTrue(byId.isPresent());
        assertEquals("Jane Roe", byId.get().getName());

        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM users", Integer.class);
        assertEquals(1, count);
    }
}