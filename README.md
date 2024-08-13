# Task Management System

## Описание
Это простая система управления задачами, которая позволяет пользователям создавать, редактировать и управлять задачами.

## Запуск проекта

1. Установите Docker и Docker Compose.
2. Склонируйте репозиторий `git clone https://github.com/1shukhrat/task-service.git`
3. Запустите команду `docker-compose up` или `docker compose up`.
4. API будет доступно по адресу `http://localhost:8080`.

> [!NOTE]
> При необходимости можете поменять параметры в файле docker-compose.yaml или application.yaml.

## Документация API
Документация API доступна по адресу: `http://localhost:8080/swagger-ui/index.html`.

## Тестирование
Запустите тесты с помощью команды `./gradlew test`.

## Технологии

1. Java 21
2. Spring Boot: Фреймворк для создания приложений.
3. Spring Security: Аутентификация и авторизация с использованием JWT.
4. Spring Data JPA: Работа с реляционной базой данных.
5. PostgreSQL: Реляционная база данных.
6. Swagger/OpenAPI: Документирование и визуализация API.
7. JUnit: Модульное тестирование
8. Gradle: Система сборки

