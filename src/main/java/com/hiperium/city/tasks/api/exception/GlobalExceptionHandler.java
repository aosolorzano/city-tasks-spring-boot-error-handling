package com.hiperium.city.tasks.api.exception;

import com.hiperium.city.tasks.api.vo.ErrorDetailsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger EXCEPTION_LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${hiperium.city.tasks.time.zone.id}")
    private String zoneId;

    @ExceptionHandler(ResourceNotFoundException.class)
    public final Mono<ResponseEntity<ErrorDetailsVO>> handleResourceNotFoundException(Exception exception,
                                                                                      ServerWebExchange exchange) {
        ErrorDetailsVO errorDetails = getErrorDetailsVO(exchange, exception,
                ((ResourceNotFoundException) exception).getErrorCode(),
                this.zoneId);
        EXCEPTION_LOGGER.error("handleResourceNotFoundException(): {}", errorDetails);
        return Mono.just(new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(TaskException.class)
    public final Mono<ResponseEntity<ErrorDetailsVO>> handleTaskException(Exception exception,
                                                                          ServerWebExchange exchange) {
        ErrorDetailsVO errorDetails = getErrorDetailsVO(exchange, exception,
                ((TaskException) exception).getErrorCode(),
                this.zoneId);
        EXCEPTION_LOGGER.error("handleTaskException(): {}", errorDetails);
        return Mono.just(new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(QuartzException.class)
    public final Mono<ResponseEntity<ErrorDetailsVO>> handleQuartzException(Exception exception,
                                                                          ServerWebExchange exchange) {
        ErrorDetailsVO errorDetails = getErrorDetailsVO(exchange, exception,
                ((QuartzException) exception).getErrorCode(),
                this.zoneId);
        EXCEPTION_LOGGER.error("handleQuartzException(): {}", errorDetails);
        return Mono.just(new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private static ErrorDetailsVO getErrorDetailsVO(ServerWebExchange exchange,
                                                    Exception exception,
                                                    String errorCode,
                                                    String zoneId) {
        EXCEPTION_LOGGER.debug("getErrorDetailsVO() - START");
        return ErrorDetailsVO.builder()
                .errorDate(ZonedDateTime.now(TimeZone.getTimeZone(ZoneId.of(zoneId)).toZoneId()))
                .path(exchange.getRequest().getPath().toString())
                .message(exception.getMessage())
                .errorCode(errorCode)
                .build();
    }
}
