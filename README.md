# Использование Testcontainers в Java и Spring Boot
**Testcontainers** — библиотека для интеграционного тестирования, которая запускает и останавливает реальные зависимости (БД, брокеры сообщений, кэши) в Docker-контейнерах прямо во время тестов. </br>
Чтобы использовать Testcontainers на машине, должен быть установлен и запущен Docker/Podman.

## Содержание проекта

Проект разделен на два основных модуля:
1.  **`nonspringprj`** — Использование Testcontainers в чистом Java-проекте с JUnit 5.
2.  **`springprj`** — Использование Testcontainers с Spring Boot 3.1+, использование `@ServiceConnection` и мета-аннотаций.

---

## 1. Модуль `nonspringprj` (Чистый JUnit 5)

Примеры использования без Spring Framework.

### Ключевые примеры:
*   **`ManualLifecycleTest`**: Базовое использование `@Testcontainers` и `@Container`. 
    *   Показано, как запустить контейнер один раз на весь класс (`static`) или на каждый метод.
    *   Ручное извлечение параметров подключения: `postgres.getJdbcUrl()`, `getUsername()`, `getPassword()`.
*   **`AnnotationBasedTest` & `PostgresExtension`**: Реализация собственного расширения JUnit 5 (`BeforeAllCallback`).
    *   Позволяет вынести логику запуска контейнера в отдельный класс или мета-аннотацию (`@IntegrationTest`).
    *   Демонстрирует передачу параметров через системные свойства (`System.setProperty`).
*   **`AbstractTestBase`**: Паттерн "Singleton Container" через наследование. 
    *   Контейнер запускается один раз для всей тестовой сессии, что значительно ускоряет выполнение тестов.
*   **`WaitStrategyTest`**: Различные стратегии ожидания готовности сервиса (`Wait Strategies`):
    *   `Wait.forListeningPort()` — ожидание TCP порта (Nginx).
    *   `Wait.forLogMessage()` — ожидание строки в логах (Redis).
    *   `Wait.forHttp()` — ожидание успешного HTTP-ответа (HTTP Echo).

---

## 2. Модуль `springprj` (Spring Boot)

Интеграция Testcontainers со Spring Boot.

### Ключевые технологии и подходы:
*   **`@ServiceConnection`**: Фича Spring Boot 3.1+, которая автоматически настраивает `ConnectionDetails` (URL, username, password) для базы данных. 
    *   Избавляет от необходимости использовать `@DynamicPropertySource` или вручную прописывать `spring.datasource.*`.
*   **`PostgresConfig`**: Централизованная конфигурация контейнера как Spring Bean в тестовом контексте (`@TestConfiguration`).
    *   Используется в тестах через `@Import(PostgresConfig.class)`.
*   **`AnnotationIntegrationTest`**: Пример создания собственной мета-аннотации для тестов.
    *   Объединяет `@SpringBootTest` и `@ExtendWith(PostgresExtension.class)`.
    *   Показывает, как использовать системные свойства для настройки Spring из JUnit 5 Extension.
*   **`ExecInContainerTest`**: Пример выполнения команд внутри запущенного контейнера (например, вызов `psql` для проверки состояния БД).
*   **`SpringTest`**: Интеграционный тест с использованием `UserRepository` (Spring Data JPA) и `JdbcTemplate`.

---

## Шпаргалка по Testcontainers

### Жизненный цикл (JUnit 5)
1.  **`@Testcontainers` + `@Container`**: Самый простой способ. Если поле `static` — один контейнер на класс, если нет — на каждый метод.
2.  **Singleton Pattern**: Объявление контейнера в базовом классе и его ручной запуск `static { container.start(); }`. Оптимально для больших проектов.
3.  **Spring Boot `@ServiceConnection`**: Рекомендуемый способ для Spring. Автоматизирует инъекцию параметров подключения (JDBC, Redis, Kafka и др.).

### Стратегии ожидания (Wait Strategies)
Контейнер считается "готовым" не в момент старта Docker-процесса, а когда сервис внутри него ответит.
*   `Wait.forListeningPort()` — ждем открытия порта.
*   `Wait.forHttp(path)` — ждем 200 OK по указанному пути.
*   `Wait.forLogMessage(regex, times)` — ждем появления текста в логах.
*   `Wait.forHealthcheck()` — использование Docker Healthcheck.

### Полезные возможности
*   **Выполнение команд**: `container.execInContainer("psql", "-U", ...)` — запуск команд внутри.
*   **Инициализация**: `container.withInitScript("init.sql")` — выполнение SQL при первом запуске.
*   **Монтирование файлов**: `.withCopyFileToContainer(MountableFile, "/path/in/container")`.
*   **Динамические порты**: Всегда используйте `container.getMappedPort()`, так как Testcontainers пробрасывает порты на случайные свободные порты хоста для избежания конфликтов.