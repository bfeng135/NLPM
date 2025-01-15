package com.nl.pm.server.common.enums;

public enum AreaEnum {
    公司管理大区("公司管理层大区")
    ;

    private String code;


    AreaEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
