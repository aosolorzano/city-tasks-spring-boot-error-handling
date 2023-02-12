package com.hiperium.city.tasks.api.utils.enums;

public enum LanguageCodeEnum {

    EN("en"),
    ES("es");

    private final String code;

    LanguageCodeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
