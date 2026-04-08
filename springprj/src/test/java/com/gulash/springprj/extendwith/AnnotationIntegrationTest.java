package com.gulash.springprj.extendwith;

import com.gulash.springprj.importconfig.PostgresConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Дополнительная мета-аннотация для интеграционных Spring-тестов
 * через @ExtendWith(PostgresExtension.class).
 *
 * В отличие от варианта с @ServiceConnection (см. {@link PostgresConfig}),
 * здесь контейнер поднимается JUnit-расширением, а параметры подключения
 * прокидываются в Spring через системные свойства.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest // свойства подключения к БД выставляет PostgresExtension
@ExtendWith(PostgresExtension.class)
@DirtiesContext // чтобы корректно закрыть контекст и остановить контейнер после класса
public @interface AnnotationIntegrationTest {
}
