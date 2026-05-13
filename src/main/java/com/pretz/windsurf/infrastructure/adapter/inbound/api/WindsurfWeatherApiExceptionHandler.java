package com.pretz.windsurf.infrastructure.adapter.inbound.api;

import com.pretz.windsurf.application.domain.validation.InvalidForecastDateException;
import com.pretz.windsurf.application.port.outbound.exception.ForecastProviderUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
class WindsurfWeatherApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WindsurfWeatherApiExceptionHandler.class);

    private static final String FORECAST_PROVIDER_UNAVAILABLE_ERROR_MESSAGE = "Forecast provider is currently unavailable";

    @ExceptionHandler(InvalidForecastDateException.class)
    ResponseEntity<ErrorResponse> handleInvalidForecastDate(InvalidForecastDateException exception) {
        log.warn(exception.getMessage(), exception);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ForecastProviderUnavailableException.class)
    ResponseEntity<ErrorResponse> handleForecastProviderUnavailable(ForecastProviderUnavailableException exception) {
        log.warn(FORECAST_PROVIDER_UNAVAILABLE_ERROR_MESSAGE, exception);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(FORECAST_PROVIDER_UNAVAILABLE_ERROR_MESSAGE));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        if ("date".equals(exception.getName())) {
            log.warn("Invalid date format requested: {}", exception.getValue());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Date must be provided in ISO format: yyyy-MM-dd"));
        }

        log.warn("Invalid request parameter {} with value {}", exception.getName(), exception.getValue());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Invalid request parameter: " + exception.getName()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException exception) {
        log.warn("Missing required request parameter: {}", exception.getParameterName());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Required request parameter 'date' is missing"));
    }

    private record ErrorResponse(String message) {
    }
}
