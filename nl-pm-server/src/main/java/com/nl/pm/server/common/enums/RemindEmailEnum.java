package com.nl.pm.server.common.enums;

public enum RemindEmailEnum {
    DAY_EMAIL("DAY_EMAIL","每天提醒"),
    WEEK_EMAIL("WEEK_EMAIL","周提醒"),
    CRM_PROJECT_SYNC_EMAIL("CRM_PROJECT_SYNC_EMAIL","定时同步CRM项目提醒"),
    ;

    private String type;
    private String desc;


    public static Boolean checkEmailType(String type){
        for (RemindEmailEnum e:RemindEmailEnum.values()){
            if(type.equals(e.getType())){
                return true;
            }
        }
        return false;
    }


    RemindEmailEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
