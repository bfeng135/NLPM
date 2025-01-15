package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/14 15:45
 */
@ApiModel
public class ReqDownDayExchangeVO {
    private List<Integer> areaId;
    private List<Integer> userId;
    private Long startTime;
    private Long endTime;

    public List<Integer> getAreaId() {
        return areaId;
    }

    public void setAreaId(List<Integer> areaId) {
        this.areaId = areaId;
    }

    public List<Integer> getUserId() {
        return userId;
    }

    public void setUserId(List<Integer> userId) {
        this.userId = userId;
    }

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
}
