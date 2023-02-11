package com.hiperium.city.tasks.api.exception;

import com.hiperium.city.tasks.api.utils.enums.GenericErrorEnum;
import org.quartz.SchedulerException;

public class QuartzException extends RuntimeException {

    private final String errorCode;

    public QuartzException(SchedulerException e) {
        super(GenericErrorEnum.SCHEDULER_ERROR.getMessage(), e);
        this.errorCode = GenericErrorEnum.SCHEDULER_ERROR.getCode();
    }

    public String getErrorCode() {
        return errorCode;
    }
}
