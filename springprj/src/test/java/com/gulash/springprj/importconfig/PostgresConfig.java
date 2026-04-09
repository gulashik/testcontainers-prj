package com.gulash.springprj.importconfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

// Конфиг один раз — используется во всех тестах
@TestConfiguration(proxyBeanMethods = false)
public class PostgresConfig {

    // @ServiceConnection с Spring Boot 3.1 и предназначена для упрощения интеграции с Testcontainers.
    // Основная цель @ServiceConnection — автоматическая настройка параметров подключения к внешнему сервису (в нашем случае к базе данных PostgreSQL)
    // Без этой аннотации вам пришлось бы вручную прописывать свойства подключения через @DynamicPropertySource:
    // @DynamicPropertySource
    // static void configureProperties(DynamicPropertyRegistry registry) {
    //    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    //    registry.add("spring.datasource.username", postgres::getUsername);
    //    registry.add("spring.datasource.password", postgres::getPassword);
    // }
    @ServiceConnection
    @Bean
    public PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>("postgres:16.2-alpine3.19")
            //.waitingFor(Wait.forListeningPort()) // Явное указание Wait Strategy
            //.withDatabaseName("testdb_spring") // можно и без
            //.withUsername("postgres_spring") // можно и без
            //.withPassword("postgres_spring") // можно и без
            //.withInitScript("sql/init.sql") // можно и без
            ;
    }
}
