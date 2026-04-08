package com.gulash.springprj.importconfig;

import com.gulash.springprj.User;
import com.gulash.springprj.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest // внутри есть @DirtiesContext и @Transactional
public class ExecInContainerTest {

    @Autowired
    private PostgreSQLContainer<?> postgres;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testExecInContainer() throws IOException, InterruptedException {
        // Подготовка данных: создаем пользователя
        userRepository.save(new User("John Doe", "john.doe@example.com"));

        // Выполнение команды psql внутри контейнера
        // PostgreSQLContainer по умолчанию использует пользователя "test", пароль "test" и БД "test"
        PostgreSQLContainer.ExecResult result = postgres.execInContainer(
            "psql", "-U", postgres.getUsername(), "-d", postgres.getDatabaseName(), "-c", "SELECT count(*) FROM users;"
        );

        // Проверка результата
        // Stdout содержит вывод команды, Stderr — ошибки (если есть)
        System.out.println("Stdout: " + result.getStdout());
        System.out.println("Stderr: " + result.getStderr());
        assertEquals(0, result.getExitCode(), "Команда должна завершиться успешно");

        // Проверяем, что в выводе есть число 1 (количество пользователей)
        assertTrue(result.getStdout().contains("1"), "Результат SELECT должен содержать '1'");
    }
}
