package com.hiperium.city.tasks.api.utils.enums;

public enum TaskErrorEnum {

    DEVICE_STATUS_NOT_UPDATED("TSK-001", "Device Status was not updated.");

    private final String code;
    private final String message;

    TaskErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
