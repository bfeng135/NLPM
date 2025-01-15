package com.nl.pm.server.service.param;


import java.util.List;

public class SearchUserByRoleOrAreaModelParam {
    private List<String> listRoleCode;
    private Integer areaId;

    public List<String> getListRoleCode() {
        return listRoleCode;
    }

    public void setListRoleCode(List<String> listRoleCode) {
        this.listRoleCode = listRoleCode;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }
}
