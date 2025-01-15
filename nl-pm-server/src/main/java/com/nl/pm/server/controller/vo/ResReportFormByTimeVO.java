package com.nl.pm.server.controller.vo;

public class ResReportFormByTimeVO {
    private String workDay;
    private Double workTime;
    private String projectDesc;

    public String getWorkDay() {
        return workDay;
    }

    public void setWorkDay(String workDay) {
        this.workDay = workDay;
    }

    public Double getWorkTime() {
        return workTime;
    }

    public void setWorkTime(Double workTime) {
        if(workTime == null){
            this.workTime = new Integer(0).doubleValue();
        }else {
            this.workTime = workTime;
        }
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }
}
