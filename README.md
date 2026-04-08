# Использование Testcontainers в Java и Spring Boot
**Testcontainers** — библиотека для интеграционного тестирования, которая запускает и останавливает реальные зависимости (БД, брокеры сообщений, кэши) в Docker-контейнерах прямо во время тестов.
Чтобы использовать Testcontainers на машине, должен быть установлен и запущен Docker/Podman.

## Содержание проекта

Проект разделен на два основных модуля:
1.  **`nonspringprj`** — Использование Testcontainers в чистом Java-проекте с JUnit 5.
2.  **`springprj`** — Глубокая интеграция с Spring Boot 3.4+, использование `@ServiceConnection` и Dynamic Properties.

---

## 1. Модуль `nonspringprj` (Чистый JUnit 5)

Без Spring Boot.

### Ключевые примеры:
*   **`ManualLifecycleTest`**: Использование аннотаций `@Testcontainers` и `@Container`. 
    *   Показано, как запустить контейнер один раз на весь класс (`static`) или на каждый метод.
    *   Ручное извлечение параметров подключения: `postgres.getJdbcUrl()`, `getUsername()`, `getPassword()`.
*   **`AnnotationBasedTest` & `PostgresExtension`**: Реализация собственного расширения JUnit 5 (`BeforeAllCallback`).
    *   Позволяет вынести логику запуска контейнера в отдельный класс/аннотацию (`@IntegrationTest`).
    *   Демонстрирует передачу параметров через системные свойства (`System.setProperty`).
*   **`AbstractTestBase`**: Паттерн "Singleton Container" через наследование. 
    *   Контейнер запускается один раз для всей тестовой сессии, что значительно ускоряет выполнение тестов.
*   **`WaitStrategyTest`**: Различные стратегии ожидания готовности сервиса (`Wait Strategies`):
    *   `Wait.forListeningPort()` — ожидание TCP порта (Nginx).
    *   `Wait.forLogMessage()` — ожидание строки в логах (Redis).
    *   `Wait.forHttp()` — ожидание успешного HTTP-ответа (HTTP Echo).

---

## 2. Модуль `springprj` 

Со Spring Boot.

### Ключевые технологии:
*   **`@ServiceConnection`**: Фича Spring Boot 3.1+, которая автоматически настраивает `ConnectionDetails` (URL, username, password) для базы данных, избавляя от ручного прописывания свойств.
*   **`PostgresConfig`**: Централизованная конфигурация контейнера как Spring Bean в тестовом контексте (`@TestConfiguration`).
*   **`ExecInContainerTest`**: Пример выполнения команд внутри запущенного контейнера (например, вызов `psql` или `ls`).
*   **`SpringTest`**: Полноценный интеграционный тест с использованием `UserRepository` (Spring Data JPA) и `JdbcTemplate`.

---

## Шпаргалка по Testcontainers

### Жизненный цикл (JUnit 5)
1.  **`@Testcontainers` + `@Container`**: Самый простой способ. Если поле `static` — один контейнер на класс, если нет — на каждый метод.
2.  **Singleton Pattern**: Объявление контейнера в базовом классе и его ручной запуск `static { container.start(); }`. Самый быстрый вариант для больших проектов.
3.  **Spring Boot `@ServiceConnection`**: Лучший выбор для Spring-приложений. Автоматизирует всё: от запуска до настройки `datasource`.

### Стратегии ожидания (Wait Strategies)
Контейнер считается "готовым" не в момент старта Docker-процесса, а когда сервис внутри него ответит.
*   `Wait.forListeningPort()` — ждем открытия порта.
*   `Wait.forHttp(path)` — ждем 200 OK.
*   `Wait.forLogMessage(regex, times)` — ждем текст в консоли.
*   `Wait.forHealthcheck()` — если в Dockerfile прописан `HEALTHCHECK`.

### Полезные команды
*   `container.getMappedPort(originalPort)` — получить динамический порт на хосте.
*   `container.execInContainer("cmd", "arg")` — выполнить команду внутри.
*   `container.withInitScript("init.sql")` — выполнить SQL при старте.
*   `container.withEnv("KEY", "VALUE")` — прокинуть переменные окружения.