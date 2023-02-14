package com.hiperium.city.tasks.api.utils.enums;

public enum ValidationErrorEnum {

    FIELD_VALIDATION_ERROR("TSK-FLD-001");

    private final String code;

    ValidationErrorEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
