package com.nl.pm.server.common.enums;

public enum UserStatusEnum {
    LEAVE("LEAVE","离开"),
    COME("COME","入职"),
    ;

    private String status;
    private String desc;


    UserStatusEnum(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
