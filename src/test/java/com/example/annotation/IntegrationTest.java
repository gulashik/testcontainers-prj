package com.example.annotation;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Мета-аннотация для интеграционных тестов.
 * Подключает PostgresExtension, который управляет жизненным циклом контейнера.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(PostgresExtension.class)
public @interface IntegrationTest {
}
