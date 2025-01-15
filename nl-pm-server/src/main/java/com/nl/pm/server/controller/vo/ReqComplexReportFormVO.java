package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ReqComplexReportFormVO {
    @ApiModelProperty(value = "开始时间")
    private Long startTime;
    @ApiModelProperty(value = "结束时间")
    private Long endTime;
    @ApiModelProperty(value = "区域ID数组")
    private Integer[] areaId;
    @ApiModelProperty(value = "项目ID数组[该字段已弃用]")
    private Integer[] projectId;
    private String[] projectName;

    @ApiModelProperty(value = "移除离开人员标识")
    private Boolean containAwayUserFlag;


    public Boolean getContainAwayUserFlag() {
        return containAwayUserFlag;
    }

    public void setContainAwayUserFlag(Boolean containAwayUserFlag) {
        this.containAwayUserFlag = containAwayUserFlag;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer[] getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer[] areaId) {
        this.areaId = areaId;
    }

    public Integer[] getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer[] projectId) {
        this.projectId = projectId;
    }

    public String[] getProjectName() {
        return projectName;
    }

    public void setProjectName(String[] projectName) {
        this.projectName = projectName;
    }
}
