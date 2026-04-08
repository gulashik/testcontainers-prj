package com.example.springprj;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest // внутри есть @DirtiesContext и @Transactional
public class SpringTest {

    @Autowired
    private PostgreSQLContainer<?> postgres;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() {
        // Проверяем, что контейнер успешно запущен и работает
        assertTrue(postgres.isRunning());
    }

    @Test
    void testDatabaseOperations() {
        // Создаем нового пользователя
        User user = new User("John Doe", "john.doe@example.com");

        // Сохраняем пользователя через репозиторий
        User savedUser = userRepository.save(user);

        // Проверяем, что ID был сгенерирован
        assertTrue(savedUser.getId() > 0);

        // Читаем пользователя обратно
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("John Doe", foundUser.get().getName());

        // Используем JdbcTemplate для проверки данных напрямую
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM users", Integer.class);
        assertEquals(1, count);
    }
}
