# task-api-spring

A **Spring Boot 3** REST API for managing tasks: Spring Data JPA with an in-memory H2 database,
Bean Validation, centralized error handling (`@RestControllerAdvice`) and MockMvc integration tests.

## Endpoints
| Method | Path | Description |
|---|---|---|
| GET | `/tasks?status=TODO\|DOING\|DONE` | List tasks (optional filter) |
| POST | `/tasks` | Create `{"title": "...", "status": "TODO"}` (status optional) |
| GET | `/tasks/{id}` | Get one task (404 if missing) |
| PUT | `/tasks/{id}` | Update title and status |
| DELETE | `/tasks/{id}` | Delete (204) |

Validation errors return `400` with `{"error": "title is required"}`.

## Requirements
Java 17+ and Maven 3.9+.

## Run and test
```bash
mvn spring-boot:run
curl -X POST localhost:8080/tasks -H "Content-Type: application/json" -d '{"title":"learn spring"}'
curl localhost:8080/tasks

mvn test
```

## Structure
`Task` (JPA entity), `TaskRepository` (Spring Data), `TaskController` (REST), `TaskRequest` (validated DTO record),
`GlobalExceptionHandler` (error mapping), `TaskApiTests` (MockMvc integration tests).

## Ideas to extend
PostgreSQL profile, pagination, Spring Security with JWT, Dockerfile, OpenAPI docs (springdoc).

> Built with AI assistance (Claude). I reviewed and run the code myself.
