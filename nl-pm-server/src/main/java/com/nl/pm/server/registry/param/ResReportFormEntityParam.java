package com.nl.pm.server.registry.param;

public class ResReportFormEntityParam {
    private Integer id;
    private String nickname;
    private String projectName;
    private Double totalTime;

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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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
