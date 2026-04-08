package com.gulash.annotation;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Мета-аннотация для интеграционных тестов.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(PostgresExtension.class) // Подключает PostgresExtension, который управляет жизненным циклом контейнера
public @interface IntegrationTest {
}
