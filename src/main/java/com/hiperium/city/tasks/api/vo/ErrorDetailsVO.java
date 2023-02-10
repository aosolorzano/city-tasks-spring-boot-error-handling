package com.hiperium.city.tasks.api.vo;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class ErrorDetailsVO {
    public ZonedDateTime errorDate;
    public String path;
    public String message;
    public String errorCode;
}
