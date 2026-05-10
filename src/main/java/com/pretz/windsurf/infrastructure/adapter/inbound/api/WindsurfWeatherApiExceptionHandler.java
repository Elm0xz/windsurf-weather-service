package com.pretz.windsurf.infrastructure.adapter.inbound.api;

import com.pretz.windsurf.application.domain.validation.InvalidForecastDateException;
import com.pretz.windsurf.application.port.outbound.exception.ForecastProviderUnavailableException;
import com.pretz.windsurf.application.port.outbound.exception.LocationsUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class WindsurfWeatherApiExceptionHandler {

    @ExceptionHandler(InvalidForecastDateException.class)
    ResponseEntity<ErrorResponse> handleInvalidForecastDate(InvalidForecastDateException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ForecastProviderUnavailableException.class)
    ResponseEntity<ErrorResponse> handleForecastProviderUnavailable() {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse("Forecast provider is currently unavailable"));
    }

    @ExceptionHandler(LocationsUnavailableException.class)
    ResponseEntity<ErrorResponse> handleLocationsUnavailable() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Locations source is currently unavailable"));
    }

    private record ErrorResponse(String message) {
    }

}
