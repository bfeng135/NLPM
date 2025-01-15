package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "ResAreaProjectCostVO",description = "区域单项目工时消耗 VO")
public class ResAreaProjectCostVO {

    @ApiModelProperty(value = "项目名")
    private String projectName;
    @ApiModelProperty(value = "区域名")
    private String areaName;
    @ApiModelProperty(value = "工时")
    private Double projectHours;
    @ApiModelProperty(value = "明细")
    private List<ResAreaUserCostDetailVO> userCostDetailList;

    public ResAreaProjectCostVO() {
        this.userCostDetailList = new ArrayList<>();
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Double getProjectHours() {
        return projectHours;
    }

    public void setProjectHours(Double projectHours) {
        this.projectHours = projectHours;
    }

    public List<ResAreaUserCostDetailVO> getUserCostDetailList() {
        return userCostDetailList;
    }

    public void setUserCostDetailList(List<ResAreaUserCostDetailVO> userCostDetailList) {
        this.userCostDetailList = userCostDetailList;
    }
}
