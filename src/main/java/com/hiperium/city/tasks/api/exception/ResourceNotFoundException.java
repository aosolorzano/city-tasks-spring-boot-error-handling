package com.hiperium.city.tasks.api.exception;

import com.hiperium.city.tasks.api.utils.enums.ResourceErrorEnum;

public class ResourceNotFoundException extends HiperiumException {

    public ResourceNotFoundException(ResourceErrorEnum errorEnum, Object... args) {
        super(errorEnum.getCode(), errorEnum.getMessage(), args);
    }
}
