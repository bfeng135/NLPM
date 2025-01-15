package com.nl.pm.server.controller.vo;

import com.nl.pm.server.common.pages.BasePagesParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ReqReportFormByProjectVO extends BasePagesParam {
    @ApiModelProperty(value = "开始时间")
    private Long startTime;
    @ApiModelProperty(value = "结束时间")
    private Long endTime;
    @ApiModelProperty(value = "区域ID数组")
    private Integer[] areaId;
    @ApiModelProperty(value = "项目名称数组[已弃用]")
    private String[] projectName;
    @ApiModelProperty(value = "项目名称")
    private String currentProjectName;

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

    public String[] getProjectName() {
        return projectName;
    }

    public void setProjectName(String[] projectName) {
        this.projectName = projectName;
    }

    public String getCurrentProjectName() {
        return currentProjectName;
    }

    public void setCurrentProjectName(String currentProjectName) {
        this.currentProjectName = currentProjectName;
    }
}
