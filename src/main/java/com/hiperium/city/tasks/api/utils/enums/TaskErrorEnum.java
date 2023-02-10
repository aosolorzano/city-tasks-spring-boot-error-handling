package com.hiperium.city.tasks.api.utils.enums;

public enum TaskErrorEnum {

    TASK_ALREADY_EXIST("TSK-001", "Task already exists with ID: %s."),
    DEVICE_STATUS_NOT_UPDATED("TSK-002", "Device Status was not updated."),
    FIND_TRIGGER_WITHOUT_JOB_ID("TSK-003", "Trying to find Trigger without Job ID for Task: %s."),
    CANNOT_RESCHEDULE_TRIGGER("TSK-004", "Cannot reschedule the Trigger for the Task ID: %s.");

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
