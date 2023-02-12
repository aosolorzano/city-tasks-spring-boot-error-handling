package com.hiperium.city.tasks.api.utils;

import com.hiperium.city.tasks.api.dto.ErrorDetailsDTO;
import org.springframework.web.server.ServerWebExchange;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

public final class ErrorUtil {

    private ErrorUtil() {
        // Empty constructor.
    }

    public static ErrorDetailsDTO getErrorDetailsVO(ServerWebExchange exchange,
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
