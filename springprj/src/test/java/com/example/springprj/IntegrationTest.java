package com.example.springprj;

import jakarta.transaction.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

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
// @DirtiesContext - чтобы очистить контекст = пересоздать контейнер после каждого теста
@DirtiesContext
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//@Transactional // для откатов изменений после каждого теста
public @interface IntegrationTest {
}
