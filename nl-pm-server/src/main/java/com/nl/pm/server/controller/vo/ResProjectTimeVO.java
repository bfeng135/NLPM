package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "ResProjectTimeVO",description = "项目工时 VO")
public class ResProjectTimeVO {
    @ApiModelProperty(value = "项目ID")
    private Integer projectId;
    @ApiModelProperty(value = "项目名")
    private String projectName;
    @ApiModelProperty(value = "区域ID")
    private Integer areaId;
    @ApiModelProperty(value = "区域名")
    private String areaName;
    @ApiModelProperty(value = "工时")
    private Double hours;
    @ApiModelProperty(value = "总工时")
    private Double AllHours;
    @ApiModelProperty(value = "明细")
    private List<ResReportDetailVO> resReportDetailVOS;

    public Double getAllHours() {
        return AllHours;
    }

    public void setAllHours(Double allHours) {
        AllHours = allHours;
    }

    public ResProjectTimeVO() {
        this.resReportDetailVOS = new ArrayList<>();
    }

    public List<ResReportDetailVO> getResReportDetailVOS() {
        return resReportDetailVOS;
    }

    public void setResReportDetailVOS(List<ResReportDetailVO> resReportDetailVOS) {
        this.resReportDetailVOS = resReportDetailVOS;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }
}
