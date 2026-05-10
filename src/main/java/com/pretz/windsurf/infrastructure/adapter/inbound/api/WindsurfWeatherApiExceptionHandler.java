package com.pretz.windsurf.infrastructure.adapter.inbound.api;

import com.pretz.windsurf.application.domain.validation.InvalidForecastDateException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class WindsurfWeatherApiExceptionHandler {

    @ExceptionHandler(InvalidForecastDateException.class)
    ResponseEntity<ErrorResponse> handleInvalidForecastDate(InvalidForecastDateException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
    }

    private record ErrorResponse(String message) {
    }
}
