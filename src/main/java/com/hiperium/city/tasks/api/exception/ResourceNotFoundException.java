package com.hiperium.city.tasks.api.exception;

import com.hiperium.city.tasks.api.utils.enums.ResourceErrorEnum;

public class ResourceNotFoundException extends RuntimeException {

    private final String errorCode;

    public ResourceNotFoundException(ResourceErrorEnum errorEnum, Object... args) {
        super(String.format(errorEnum.getMessage(), args));
        this.errorCode = errorEnum.getCode();
    }

    public String getErrorCode() {
        return errorCode;
    }
}
