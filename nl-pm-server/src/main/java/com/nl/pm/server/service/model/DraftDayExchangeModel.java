package com.nl.pm.server.service.model;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/7 10:28
 */
public class DraftDayExchangeModel {
    private Integer userId;
    private Double leaveHour;
    private String desc;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getLeaveHour() {
        return leaveHour;
    }

    public void setLeaveHour(Double leaveHour) {
        this.leaveHour = leaveHour;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
