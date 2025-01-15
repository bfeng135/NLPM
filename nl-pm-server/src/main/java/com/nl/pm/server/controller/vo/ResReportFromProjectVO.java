package com.nl.pm.server.controller.vo;

import com.nl.pm.server.registry.entity.UserEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

@ApiModel
public class ResReportFromProjectVO {
    @ApiModelProperty(value = "项目名字")
    private String projectName;
    @ApiModelProperty(value = "项目总时长")
    private Double sumWorkTime;
    @ApiModelProperty(value = "项目总人天")
    private Double sumWorkDay;
    @ApiModelProperty(value = "竖行时间，List<UserEntity>每天每人工作时间")
    private List<Map<String, List<ResShowReportFormByProjectVO>>> mapList;
    @ApiModelProperty(value = "每个人的工作总时长")
    private List<ResShowReportFormByProjectVO> userWorkTotalTime;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Double getSumWorkTime() {
        return sumWorkTime;
    }

    public void setSumWorkTime(Double sumWorkTime) {
        if(sumWorkTime == null){
            this.sumWorkTime = new Integer(0).doubleValue();
        }else{
            this.sumWorkTime = sumWorkTime;
        }
    }

    public Double getSumWorkDay() {
        return sumWorkDay;
    }

    public void setSumWorkDay(Double sumWorkDay) {
        if(sumWorkDay == null){
            this.sumWorkDay = new Integer(0).doubleValue();
        }else {
            this.sumWorkDay = sumWorkDay;
        }
    }

    public List<Map<String, List<ResShowReportFormByProjectVO>>> getMapList() {
        return mapList;
    }

    public void setMapList(List<Map<String, List<ResShowReportFormByProjectVO>>> mapList) {
        this.mapList = mapList;
    }

    public List<ResShowReportFormByProjectVO> getUserWorkTotalTime() {
        return userWorkTotalTime;
    }

    public void setUserWorkTotalTime(List<ResShowReportFormByProjectVO> userWorkTotalTime) {
        this.userWorkTotalTime = userWorkTotalTime;
    }
}
