package com.nl.pm.server.registry.param;

import com.nl.pm.server.common.pages.BasePagesParam;

public class QueryUserListEntityParam extends BasePagesParam{
    private Integer roleId;
    private Integer areaId;
    private Integer projectId;
    private String nickname;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
