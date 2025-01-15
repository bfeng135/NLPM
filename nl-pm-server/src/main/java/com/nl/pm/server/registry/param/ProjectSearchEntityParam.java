package com.nl.pm.server.registry.param;

import com.nl.pm.server.common.pages.BasePagesParam;

public class ProjectSearchEntityParam extends BasePagesParam {

    private String name;
    private String desc;
    private Integer areaId;
    private Integer managerId;
    private Integer userId;
    private Boolean enableFlag;
    private Integer currentUserId;
    private Integer currentAreaId;

    public Integer getCurrentAreaId() {
        return currentAreaId;
    }

    public void setCurrentAreaId(Integer currentAreaId) {
        this.currentAreaId = currentAreaId;
    }

    public Integer getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Integer currentUserId) {
        this.currentUserId = currentUserId;
    }

    public Boolean getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(Boolean enableFlag) {
        this.enableFlag = enableFlag;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }
}
