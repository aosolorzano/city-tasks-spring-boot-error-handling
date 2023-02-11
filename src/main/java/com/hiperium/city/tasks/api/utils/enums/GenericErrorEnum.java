package com.hiperium.city.tasks.api.utils.enums;

public enum GenericErrorEnum {

    SCHEDULER_ERROR("TSK-SCH-001", "Error in the Scheduler component."),
    FIELD_VALIDATION_ERROR("TSK-FLD-001", null);

    private final String code;
    private final String message;

    GenericErrorEnum(String code, String message) {
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
