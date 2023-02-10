package com.hiperium.city.tasks.api.exception;

import com.hiperium.city.tasks.api.utils.enums.TaskErrorEnum;

public class TaskException extends RuntimeException {

    private String errorCode;

    public TaskException(TaskErrorEnum errorEnum, Object... args) {
        super(String.format(errorEnum.getMessage(), args));
        this.errorCode = errorEnum.getCode();
    }

    public String getErrorCode() {
        return errorCode;
    }
}
