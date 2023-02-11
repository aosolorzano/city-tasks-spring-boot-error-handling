package com.hiperium.city.tasks.api.exception;

import com.hiperium.city.tasks.api.dto.ErrorDetailsDTO;
import com.hiperium.city.tasks.api.utils.enums.GenericErrorEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.TimeZone;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${hiperium.city.tasks.time.zone.id}")
    private String zoneId;

    @ExceptionHandler(ResourceNotFoundException.class)
    public final Mono<ResponseEntity<ErrorDetailsDTO>> handleResourceNotFoundException(Exception exception,
                                                                                       ServerWebExchange exchange) {
        ErrorDetailsDTO errorDetails = getErrorDetailsVO(exchange, exception.getMessage(),
                ((ResourceNotFoundException) exception).getErrorCode(),
                this.zoneId);
        super.logger.error("handleResourceNotFoundException(): " + errorDetails);
        return Mono.just(new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(TaskException.class)
    public final Mono<ResponseEntity<ErrorDetailsDTO>> handleTaskException(Exception exception,
                                                                           ServerWebExchange exchange) {
        ErrorDetailsDTO errorDetails = getErrorDetailsVO(exchange, exception.getMessage(),
                ((TaskException) exception).getErrorCode(),
                this.zoneId);
        super.logger.error("handleTaskException(): " + errorDetails);
        return Mono.just(new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(QuartzException.class)
    public final Mono<ResponseEntity<ErrorDetailsDTO>> handleQuartzException(Exception exception,
                                                                             ServerWebExchange exchange) {
        ErrorDetailsDTO errorDetails = getErrorDetailsVO(exchange, exception.getMessage(),
                ((QuartzException) exception).getErrorCode(),
                this.zoneId);
        super.logger.error("handleQuartzException(): " + errorDetails);
        return Mono.just(new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleWebExchangeBindException(WebExchangeBindException exception,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          ServerWebExchange exchange) {
        String errorMessage;
        FieldError fieldError = exception.getFieldError();
        if (Objects.isNull(fieldError)) {
            errorMessage = exception.getMessage();
        } else {
            errorMessage = fieldError.getDefaultMessage();
        }
        ErrorDetailsDTO errorDetails = getErrorDetailsVO(exchange, errorMessage,
                GenericErrorEnum.FIELD_VALIDATION_ERROR.getCode(), this.zoneId);
        super.logger.error("handleWebExchangeBindException(): " + errorDetails);
        return Mono.just(new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST));
    }

    private static ErrorDetailsDTO getErrorDetailsVO(ServerWebExchange exchange,
                                                     String errorMessage,
                                                     String errorCode,
                                                     String zoneId) {
        return ErrorDetailsDTO.builder()
                .errorDate(ZonedDateTime.now(TimeZone.getTimeZone(ZoneId.of(zoneId)).toZoneId()))
                .requestedPath(exchange.getRequest().getPath().toString())
                .errorMessage(errorMessage)
                .errorCode(errorCode)
                .build();
    }
}
