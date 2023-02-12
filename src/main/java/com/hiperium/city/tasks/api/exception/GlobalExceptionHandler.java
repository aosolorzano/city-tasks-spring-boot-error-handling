package com.hiperium.city.tasks.api.exception;

import com.hiperium.city.tasks.api.dto.ErrorDetailsDTO;
import com.hiperium.city.tasks.api.utils.ErrorUtil;
import com.hiperium.city.tasks.api.utils.enums.GenericErrorEnum;
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
        super();
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final Mono<ResponseEntity<ErrorDetailsDTO>> handleResourceNotFoundException(Exception exception,
                                                                                       ServerWebExchange exchange) {
        ErrorDetailsDTO errorDetails = ErrorUtil.getErrorDetailsVO(exchange, exception.getMessage(),
                ((ResourceNotFoundException) exception).getErrorCode(),
                this.zoneId);
        super.logger.error("handleResourceNotFoundException(): " + errorDetails);
        return Mono.just(new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(TaskException.class)
    public final Mono<ResponseEntity<ErrorDetailsDTO>> handleTaskException(Exception exception,
                                                                           ServerWebExchange exchange) {
        ErrorDetailsDTO errorDetails = ErrorUtil.getErrorDetailsVO(exchange, exception.getMessage(),
                ((TaskException) exception).getErrorCode(),
                this.zoneId);
        super.logger.error("handleTaskException(): " + errorDetails);
        return Mono.just(new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(QuartzException.class)
    public final Mono<ResponseEntity<ErrorDetailsDTO>> handleQuartzException(Exception exception,
                                                                             ServerWebExchange exchange) {
        ErrorDetailsDTO errorDetails = ErrorUtil.getErrorDetailsVO(exchange, exception.getMessage(),
                ((QuartzException) exception).getErrorCode(),
                this.zoneId);
        super.logger.error("handleQuartzException(): " + errorDetails);
        return Mono.just(new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    public Mono<ResponseEntity<Object>> handleWebExchangeBindException(WebExchangeBindException exception,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          ServerWebExchange exchange) {
        String errorMessage = this.loadI18nMessage(exception, exchange);
        ErrorDetailsDTO errorDetails = ErrorUtil.getErrorDetailsVO(exchange, errorMessage,
                GenericErrorEnum.FIELD_VALIDATION_ERROR.getCode(), this.zoneId);
        super.logger.error("handleWebExchangeBindException(): " + errorDetails);
        return Mono.just(new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST));
    }

    private String loadI18nMessage(WebExchangeBindException exception, ServerWebExchange exchange) {
        super.logger.debug("loadI18nMessage() - START");
        FieldError fieldError = exception.getFieldError();
        if (Objects.nonNull(fieldError)) {
            String messageKey = fieldError.getDefaultMessage();
            super.logger.debug("loadI18nMessage() - Message key found: " + messageKey);
            if (Objects.nonNull(messageKey)) {
                Locale locale = exchange.getRequest().getHeaders().getAcceptLanguageAsLocales().get(0);
                super.logger.debug("loadI18nMessage() - Locale found: " + locale);
                return this.messageSource.getMessage(messageKey, null, locale);
            }
        }
        return exception.getMessage();
    }
}
