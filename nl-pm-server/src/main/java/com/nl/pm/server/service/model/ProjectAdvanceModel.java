package com.nl.pm.server.service.model;

/**
 * project
 */
public class ProjectAdvanceModel extends ProjectModel {

    private String areaName;
    private String managerName;
    private Double hours;
    private Boolean forceDescFlag;
    private Integer userId;
    private String nickname;
    private Integer mainAreaId;

    public Integer getMainAreaId() {
        return mainAreaId;
    }

    public void setMainAreaId(Integer mainAreaId) {
        this.mainAreaId = mainAreaId;
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

    public Boolean getForceDescFlag() {
        return forceDescFlag;
    }

    public void setForceDescFlag(Boolean forceDescFlag) {
        this.forceDescFlag = forceDescFlag;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
}
