package com.nl.pm.server.controller.vo;

import java.util.List;

public class ReqReportFromByUserVO {
    private Integer areaId;
    private String areaName;
    private String projectName;
    private Integer id;
    private String nickname;
    private List<ResReportFormByTimeVO> resReportFormByTimeVOS;
    private Double userTotalTime;

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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<ResReportFormByTimeVO> getResReportFormByTimeVOS() {
        return resReportFormByTimeVOS;
    }

    public void setResReportFormByTimeVOS(List<ResReportFormByTimeVO> resReportFormByTimeVOS) {
        this.resReportFormByTimeVOS = resReportFormByTimeVOS;
    }

    public Double getUserTotalTime() {
        return userTotalTime;
    }

    public void setUserTotalTime(Double userTotalTime) {
        if(userTotalTime == null){
            this.userTotalTime = new Integer(0).doubleValue();
        }else {
            this.userTotalTime = userTotalTime;
        }
    }
}
