package com.nl.pm.server.controller.vo;


public class ResShowReportFormByProjectVO {
    private String nickname;
    private Double everyDayTime;
    private Double totalTime;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Double getEveryDayTime() {
        return everyDayTime;
    }

    public void setEveryDayTime(Double everyDayTime) {
        if(everyDayTime == null){
            this.everyDayTime = new Integer(0).doubleValue();
        }else {
            this.everyDayTime = everyDayTime;
        }
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
