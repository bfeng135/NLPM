package com.nl.pm.server.controller.vo;

import java.util.List;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/13 14:21
 */
public class ReqReportFromByProjectVO {
    private List<ReqReportFromByUserVO> reqReportFromByUserVOS;
    private Double currentTotalTime = 0.0;
    private Double totalTime = 0.0;
    private Double percent = 0.00;

    public List<ReqReportFromByUserVO> getReqReportFromByUserVOS() {
        return reqReportFromByUserVOS;
    }

    public void setReqReportFromByUserVOS(List<ReqReportFromByUserVO> reqReportFromByUserVOS) {
        this.reqReportFromByUserVOS = reqReportFromByUserVOS;
    }

    public Double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Double totalTime) {
        if(totalTime == null){
            this.totalTime = 0.0;
        }else {
            this.totalTime = totalTime;
        }
    }

    public Double getCurrentTotalTime() {
        return currentTotalTime;
    }

    public void setCurrentTotalTime(Double currentTotalTime) {
        if(currentTotalTime == null){
            this.currentTotalTime = 0.0;
        }else {
            this.currentTotalTime = currentTotalTime;
        }
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }
}
