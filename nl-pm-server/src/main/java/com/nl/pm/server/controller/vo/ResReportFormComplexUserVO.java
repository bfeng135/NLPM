package com.nl.pm.server.controller.vo;

public class ResReportFormComplexUserVO {
    private Integer id;
    private String nickname;
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
