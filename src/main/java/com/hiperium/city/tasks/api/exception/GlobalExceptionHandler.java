package com.hiperium.city.tasks.api.exception;

import com.hiperium.city.tasks.api.dto.ErrorDetailsDTO;
import com.hiperium.city.tasks.api.utils.ErrorUtil;
import com.hiperium.city.tasks.api.utils.enums.ValidationErrorEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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

import java.util.Locale;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    @Value("${hiperium.city.tasks.time.zone.id}")
    private String zoneId;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final Mono<ResponseEntity<ErrorDetailsDTO>> handleResourceNotFoundException(
            ResourceNotFoundException exception,
            ServerWebExchange exchange) {
        ErrorDetailsDTO errorDetails = this.constructErrorDetailsDTO(exchange, exception);
        super.logger.error("handleResourceNotFoundException(): " + errorDetails);
        return Mono.just(new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(QuartzException.class)
    public final Mono<ResponseEntity<ErrorDetailsDTO>> handleQuartzException(
            QuartzException exception,
            ServerWebExchange exchange) {
        ErrorDetailsDTO errorDetails = this.constructErrorDetailsDTO(exchange, exception);
        super.logger.error("handleQuartzException(): " + errorDetails);
        return Mono.just(new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    public Mono<ResponseEntity<Object>> handleWebExchangeBindException(
            WebExchangeBindException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            ServerWebExchange exchange) {
        String errorMessage = null;
        FieldError fieldError = exception.getFieldError();
        if (Objects.nonNull(fieldError)) {
            String messageKey = fieldError.getDefaultMessage();
            errorMessage = this.getMessageFromProperties(messageKey, ErrorUtil.getLocale(exchange));
        }
        if (Objects.isNull(errorMessage)) {
            errorMessage = exception.getMessage();
        }
        ErrorDetailsDTO errorDetails = ErrorUtil.getErrorDetailsVO(exchange, errorMessage,
                ValidationErrorEnum.FIELD_VALIDATION_ERROR.getCode(), this.zoneId);
        super.logger.error("handleWebExchangeBindException(): " + errorDetails);
        return Mono.just(new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST));
    }

    private ErrorDetailsDTO constructErrorDetailsDTO(ServerWebExchange exchange, HiperiumException exception) {
        String errorMessage = this.getMessageFromProperties(exception.getErrorMessageKey(),
                ErrorUtil.getLocale(exchange), exception.getArgs());
        return ErrorUtil.getErrorDetailsVO(exchange, errorMessage, exception.getErrorCode(), this.zoneId);
    }

    private String getMessageFromProperties(String messageKey, Locale locale, Object... args) {
        super.logger.debug("getMessageFromProperties() - Key: " + messageKey + " - Locale: " + locale);
        return this.messageSource.getMessage(messageKey, args, locale);
    }
}
