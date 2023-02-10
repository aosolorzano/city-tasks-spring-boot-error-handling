package com.hiperium.city.tasks.api.utils.enums;

public enum ResourceErrorEnum {

    TASK_NOT_FOUND("RSC-001", "Task not found with ID: %s."),
    DEVICE_NOT_FOUND("RSC-002", "Device not found with ID: %s.");

    private final String code;
    private final String message;

    ResourceErrorEnum(String code, String message) {
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
