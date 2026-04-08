package com.example.wait;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class WaitStrategyTest {

    // 1. Стандартная стратегия для баз данных (через JDBC)
    // Специализированные контейнеры (как PostgreSQLContainer) уже имеют встроенные стратегии.
    // Но для GenericContainer часто нужно указывать их явно.

    @Container
    private static final GenericContainer<?> nginx = new GenericContainer<>("nginx:alpine")
            .withExposedPorts(80)
            // Ждем, пока порт 80 станет доступен для TCP-соединения
            .waitingFor(Wait.forListeningPort());

    @Container
    private static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379)
            // Ждем появления определенной строки в логах контейнера
            .waitingFor(Wait.forLogMessage(".*Ready to accept connections.*\\n", 1));

    @Container
    private static final GenericContainer<?> echoServer = new GenericContainer<>("hashicorp/http-echo")
            .withExposedPorts(5678)
            .withCommand("-text=hello")
            // Ждем HTTP-ответа 200 OK по определенному пути
            .waitingFor(Wait.forHttp("/")
                    .forStatusCode(200)
                    .withStartupTimeout(Duration.ofSeconds(30)));

    @Test
    void testWaitStrategies() {
        assertTrue(nginx.isRunning());
        assertTrue(redis.isRunning());
        assertTrue(echoServer.isRunning());
        
        System.out.println("Nginx mapped port: " + nginx.getMappedPort(80));
        System.out.println("Redis mapped port: " + redis.getMappedPort(6379));
        System.out.println("Echo server mapped port: " + echoServer.getMappedPort(5678));
    }
}
