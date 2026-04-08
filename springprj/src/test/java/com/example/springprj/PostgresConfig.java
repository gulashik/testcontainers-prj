package com.example.springprj;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

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
    /*
    Выполнение команд внутри контейнера (execInContainer).
    Иногда нужно выполнить команду внутри работающего контейнера (например, psql, pg_dump, ls и т.д.).

    Пример использования:
    Container.ExecResult result = postgres.execInContainer(
        "psql", "-U", postgres.getUsername(), "-d", postgres.getDatabaseName(), "-c", "SELECT count(*) FROM users;"
    );

    - result.getStdout(): стандартный вывод команды.
    - result.getStderr(): поток ошибок.
    - result.getExitCode(): код завершения (0 = успех).

    Wait Strategies (Стратегии ожидания).
    Testcontainers по умолчанию считает, что контейнер готов к работе, когда он запущен.
    Однако, само приложение внутри контейнера (например, PostgreSQL) может еще не успеть загрузиться.
    Для этого существуют Wait Strategies:

    1. Специализированные контейнеры (PostgreSQLContainer, MySQLContainer):
       Уже имеют встроенные стратегии ожидания. Например, для БД это ожидание возможности установить JDBC-соединение.
       Поэтому ЯВНО прописывать .waitingFor(...) для них часто не требуется.

    2. Общие контейнеры (GenericContainer):
       Часто требуют явного указания, как понять, что сервис готов.
       Примеры:
       - Wait.forListeningPort(): ждем готовности TCP-порта.
       - Wait.forHttp("/health"): ждем успешного HTTP-ответа (200 OK).
       - Wait.forLogMessage(".*Ready.*", 1): ждем определенной фразы в логах.

    3. Настройка таймаута:
       По умолчанию ожидание длится 60 секунд. Если контейнер не успел — тест упадет.
       Можно изменить: .withStartupTimeout(Duration.ofSeconds(90)).

    Volumes и Files (Работа с файлами).
    Нужны для прокидывания конфигов, инициализационных скриптов или сохранения данных.

    1. Файлы (Mounting Files/Classpath):
       Используйте .withClasspathResourceMapping(resourcePath, containerPath, mode)
       или .withCopyFileToContainer(MountableFile, containerPath).
       - Зачем: Прокинуть конфиг (например, redis.conf) или init-скрипт.
       - Нужно ли явно: Только если вам не хватает стандартных методов (например, .withInitScript()).

    2. Volumes (Тома):
       В тестах используются редко, так как контейнеры обычно эфемерны (уничтожаются после тестов).
       - Зачем: Если нужно сохранить состояние между перезапусками контейнера ВНУТРИ одного теста.

    Networks (Сети).
    Нужны для взаимодействия нескольких контейнеров между собой (например, App -> DB).

    - Как использовать:
      Network network = Network.newNetwork();
      container1.withNetwork(network).withNetworkAliases("db");
      container2.withNetwork(network);
    - Нужно ли явно:
      • Если контейнер один — НЕ НУЖНО.
      • Если используете @ServiceConnection — НЕ НУЖНО (Spring общается с контейнером через проброшенные порты localhost).
      • Если один контейнер должен стучаться в другой по имени хоста (DNS) — ДА, нужна общая сеть.

    Как это работает с @ServiceConnection.
    Когда помечается @Bean бин PostgreSQLContainer аннотацией @ServiceConnection:
    1. Spring Boot обнаруживает, что в контексте теста есть контейнер с базой данных.
    2. Автоматически создает объект ConnectionDetails (в данном случае JdbcConnectionDetails).
    3. Извлекает из контейнера все необходимые данные: JDBC URL, имя пользователя и пароль.
    4. Подставляет эти данные в стандартные настройки Spring Data JPA / JDBC (spring.datasource.*).
    5. УПРАВЛЯЕТ ЖИЗНЕННЫМ ЦИКЛОМ: Spring сам вызывает start() при создании бина и stop() при закрытии контекста.

    Нужны ли методы @BeforeAll и @AfterAll?
    • При использовании @ServiceConnection и @Bean: НЕТ. Spring берет на себя запуск и остановку контейнера.
    • В обычном JUnit 5 (без Spring): ДА. Вы используете их (явно или через @Container на static поле),
      чтобы контейнер запустился один раз для всех тестов в классе (или через Singleton Pattern для всех классов).

    Преимущества @ServiceConnection:
    • Меньше шаблонного кода: Не нужно помнить названия всех свойств (например, spring.datasource.url или spring.redis.host) и вручную их связывать с методами контейнера.
    • Типобезопасность: Spring Boot понимает тип контейнера (PostgreSQLContainer) и знает, какие именно параметры ему нужны для работы.
    • Универсальность: Это работает не только для SQL баз данных, но и для Redis, MongoDB, Cassandra, RabbitMQ и многих других модулей Testcontainers, поддерживаемых Spring Boot.
    */
}
