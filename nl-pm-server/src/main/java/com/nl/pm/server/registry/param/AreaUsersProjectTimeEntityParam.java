package com.nl.pm.server.registry.param;

import com.nl.pm.server.registry.entity.ProjectAdvanceEntity;

import java.util.ArrayList;
import java.util.List;

public class AreaUsersProjectTimeEntityParam {
    private Integer areaId;
    private String areaName;
    private Integer userId;
    private String nickname;
    private List<ProjectAdvanceEntity> projectTimeList;

    public AreaUsersProjectTimeEntityParam() {
        projectTimeList = new ArrayList<>();
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

    public List<ProjectAdvanceEntity> getProjectTimeList() {
        return projectTimeList;
    }

    public void setProjectTimeList(List<ProjectAdvanceEntity> projectTimeList) {
        this.projectTimeList = projectTimeList;
    }
}
