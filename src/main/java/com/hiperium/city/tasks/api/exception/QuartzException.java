package com.hiperium.city.tasks.api.exception;

import com.hiperium.city.tasks.api.utils.enums.SchedulerErrorEnum;
import org.quartz.SchedulerException;

public class QuartzException extends HiperiumException {

    public QuartzException(SchedulerException e, SchedulerErrorEnum errorEnum, Object... args) {
        super(e, errorEnum.getCode(), errorEnum.getMessage(), args);
    }
}
