# Windsurf Weather Service

A Spring Boot service that helps with choosing the best windsurfing location for a selected date. It uses a predefined list of windsurfing locations and picks one of them based on weather forecasts fetched from an external service (Weatherbit API).

## Technologies

- Java 25
- Spring Boot 4.0.3
- Gradle 9.2.0
- JUnit 6.0.3 & AssertJ, Mockito, WireMock

## Feature

A location is considered suitable for windsurfing when:

- wind speed is between `5.0` and `18.0` m/s
- temperature is between `5.0°C` and `35.0°C`

When multiple locations match the criteria, the service chooses the one with the highest value:

```text
value = windSpeed * 3 + temperature
```

The requested forecast date must be in the 7 forecast day range beginning from today.

## API

### Get optimal windsurfing location

```http
GET /api/windsurfing-location?date=2026-05-15
Accept: application/json
```

Example of a successful response:

```json
{
  "location": "Pissouri",
  "windSpeed": 17.7,
  "temperature": 29.3
}
```

### Possible responses

| Status                      | Meaning                                             |
|-----------------------------|-----------------------------------------------------|
| `200 OK`                    | Optimal location found                              |
| `400 Bad Request`           | Invalid date format or date outside supported range |
| `404 Not Found`             | No location matches windsurfing criteria            |
| `502 Bad Gateway`           | Weather provider is unavailable                     |

## Setup

This project is intended as a small local service/demo application. Deployment is handled through local Gradle execution or a packaged Spring Boot JAR. Weatherbit API key is needed for access to weather service and it should be provided via environment variable.

### Running locally

```bash
WEATHERBIT_API_KEY=<your_api_key> ./gradlew bootRun
```

The service starts on:

```text
http://localhost:8080
```

Example request:

```bash
curl "http://localhost:8080/api/windsurfing-location?date=2026-05-15"
```

### Building the application

```bash
./gradlew clean bootJar
```

Run the packaged application:

```bash
WEATHERBIT_API_KEY=<your_api_key> java -jar build/libs/windsurf-weather-service.jar
```

### Running tests

```bash
./gradlew test
```

## Project structure

```text
src/main/java/com/pretz/windsurf
├── application
│   ├── domain
│   └── port
├── infrastructure
│   ├── adapter
│   └── configuration
└── WindsurfWeatherApp.java
```

The project follows a hexagonal architecture:

- `application` contains business logic, domain objects and ports
- `infrastructure` contains infrastructure: Spring MVC controller & configuration, adapters implementing external API client and JSON loading module. 


