package com.nl.pm.server.registry.entity;

import java.util.Date;

public class DayExchangeAdvanceEntity extends DayExchangeEntity {
    private Date date;
    private Integer userId;
    private String nickname;
    private Integer areaId;
    private String areaName;

    private Double workHour;
    private Double overHour;
    private Double exchangeHour;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Double getWorkHour() {
        return workHour;
    }

    public void setWorkHour(Double workHour) {
        this.workHour = workHour;
    }

    public Double getOverHour() {
        return overHour;
    }

    public void setOverHour(Double overHour) {
        this.overHour = overHour;
    }

    public Double getExchangeHour() {
        return exchangeHour;
    }

    public void setExchangeHour(Double exchangeHour) {
        this.exchangeHour = exchangeHour;
    }
}
