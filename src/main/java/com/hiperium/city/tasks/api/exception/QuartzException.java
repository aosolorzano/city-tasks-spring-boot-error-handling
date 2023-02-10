package com.hiperium.city.tasks.api.exception;

import org.quartz.SchedulerException;

public class QuartzException extends RuntimeException {

    private static final String DEFAULT_ERROR_MESSAGE = "Error in the Scheduler component.";
    private static final String DEFAULT_ERROR_CODE = "CTY-TSK-TIMER";
    private final String errorCode;
    private final String errorDescription;

    public QuartzException(SchedulerException e) {
        super(DEFAULT_ERROR_MESSAGE, e);
        this.errorCode = DEFAULT_ERROR_CODE;
        this.errorDescription = e.getMessage();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
