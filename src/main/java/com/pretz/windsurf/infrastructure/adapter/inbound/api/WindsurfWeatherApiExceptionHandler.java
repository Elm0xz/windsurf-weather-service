package com.pretz.windsurf.infrastructure.adapter.inbound.api;

import com.pretz.windsurf.application.domain.validation.InvalidForecastDateException;
import com.pretz.windsurf.application.port.outbound.exception.ForecastProviderUnavailableException;
import com.pretz.windsurf.application.port.outbound.exception.LocationsUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
class WindsurfWeatherApiExceptionHandler {

    @ExceptionHandler(InvalidForecastDateException.class)
    ResponseEntity<ErrorResponse> handleInvalidForecastDate(InvalidForecastDateException exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ForecastProviderUnavailableException.class)
    ResponseEntity<ErrorResponse> handleForecastProviderUnavailable() {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse("Forecast provider is currently unavailable"));
    }

    @ExceptionHandler(LocationsUnavailableException.class)
    ResponseEntity<ErrorResponse> handleLocationsUnavailable() {
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse("Locations source is currently unavailable"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        if ("date".equals(exception.getName())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Date must be provided in ISO format: yyyy-mm-dd"));
        }

        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Invalid request parameter: " + exception.getName()));
    }

    private record ErrorResponse(String message) {
    }

}
