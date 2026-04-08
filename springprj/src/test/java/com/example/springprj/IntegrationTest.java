package com.example.springprj;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Мета-аннотация для интеграционных тестов с использованием Spring Boot и PostgreSQL.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Import(PostgresConfig.class)
public @interface IntegrationTest {
}
