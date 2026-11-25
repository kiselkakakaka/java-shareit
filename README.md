# 📦 Share It

Бэкенд сервиса аренды вещей: владельцы публикуют вещи, арендаторы бронируют их и оставляют отзывы, а если нужной вещи нет — создают запрос, и другие пользователи могут предложить вариант.

> Проект курса «Java-разработчик» Яндекс.Практикума.

## Стек

![Java](https://img.shields.io/badge/Java-21-6DB33F?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6DB33F?style=flat&logo=spring&logoColor=white)
![JPA / Hibernate](https://img.shields.io/badge/Spring%20Data%20JPA-Hibernate-green?style=flat)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=flat&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-compose-2496ED?style=flat&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-multimodule-C71A36?style=flat&logo=apachemaven&logoColor=white)
![JUnit 5 / Mockito](https://img.shields.io/badge/tests-JUnit%205%20·%20Mockito-green?style=flat)

## Архитектура

Два приложения, собираемые одним Maven-проектом:

| Модуль | Назначение |
|---|---|
| `gateway` | шлюз: валидация запросов (Bean Validation + кастомные валидаторы) и маршрутизация в сервер |
| `server` | бизнес-логика: бронирования, вещи, запросы вещей, комментарии, пользователи |

Шлюз общается с сервером через `RestTemplate` (базовый клиент `BaseClient`). Персистентность — Spring Data JPA поверх PostgreSQL; для локальной проверки есть H2.

## Возможности

- Вещи: CRUD, поиск по названию/описанию
- Бронирования: создание, подтверждение/отклонение владельцем, статусы (`WAITING`, `APPROVED`, `REJECTED`, `CANCELED`)
- Запросы вещей: «ищу такую вещь» с ответами других пользователей
- Отзывы на вещь после завершения аренды
- Проверка пересечения дат бронирования, валидация DTO на шлюзе

## Запуск

```bash
docker compose up --build
# gateway: http://localhost:8080
# server:  http://localhost:9090
```

## Тесты и CI

Юнит- и интеграционные тесты (JUnit 5, Mockito, AssertJ). На pull request — reusable workflow с API-тестами Postman (`postman/sprint.json`) через Newman и проверка Checkstyle.
