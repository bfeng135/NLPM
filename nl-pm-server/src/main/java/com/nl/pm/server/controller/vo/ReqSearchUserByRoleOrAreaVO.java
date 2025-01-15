package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel
public class ReqSearchUserByRoleOrAreaVO {
    @ApiModelProperty(value = "角色code集合")
    private List<String> listRoleCode;
    @ApiModelProperty(value = "区域ID")
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
