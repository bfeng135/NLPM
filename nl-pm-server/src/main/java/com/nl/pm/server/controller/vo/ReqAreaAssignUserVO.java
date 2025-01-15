package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "ReqAreaAssignUserVO",description = "分配人员到区域")
public class ReqAreaAssignUserVO {

    @ApiModelProperty(value = "区域 id")
    private Integer areaId;

    @ApiModelProperty(value = "人员列表")
    private List<Integer> userIdList;

    public ReqAreaAssignUserVO() {
        userIdList = new ArrayList<>();
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public List<Integer> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<Integer> userIdList) {
        this.userIdList = userIdList;
    }
}
