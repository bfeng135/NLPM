package com.nl.pm.server.controller.vo;

import java.util.List;

public class ResReportFormComplexVO {
    private Integer id;
    private String projectName;
    private List<ResReportFormComplexUserVO> resReportFormComplexUserVO;
    private Double totalTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<ResReportFormComplexUserVO> getResReportFormComplexUserVO() {
        return resReportFormComplexUserVO;
    }

    public void setResReportFormComplexUserVO(List<ResReportFormComplexUserVO> resReportFormComplexUserVO) {
        this.resReportFormComplexUserVO = resReportFormComplexUserVO;
    }

    public Double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Double totalTime) {
        if(totalTime == null){
            this.totalTime = new Integer(0).doubleValue();
        }else {
            this.totalTime = totalTime;
        }
    }
}
