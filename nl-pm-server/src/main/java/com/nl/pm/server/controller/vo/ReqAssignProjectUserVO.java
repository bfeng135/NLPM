package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "ReqAssignProjectUserVO",description = "项目配置成员 VO")
public class ReqAssignProjectUserVO {
    @ApiModelProperty(value = "项目 ID")
    private Integer projectId;
    @ApiModelProperty(value = "员工列表")
    private List<Integer> userIdList;

    public ReqAssignProjectUserVO() {
        this.userIdList = new ArrayList<>();
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public List<Integer> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<Integer> userIdList) {
        this.userIdList = userIdList;
    }
}
