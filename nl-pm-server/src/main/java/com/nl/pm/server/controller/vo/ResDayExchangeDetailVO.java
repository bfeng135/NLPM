package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ResDayExchangeDetailVO",description = "查询人员调休详细内容返回 VO")
public class ResDayExchangeDetailVO{

    @ApiModelProperty(value = "请假日期")
    private Long date;

    @ApiModelProperty(value = "请假时长")
    private Double leaveHour;

    @ApiModelProperty(value = "请假原因")
    private String desc;


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Double getLeaveHour() {
        return leaveHour;
    }

    public void setLeaveHour(Double leaveHour) {
        this.leaveHour = leaveHour;
    }
}
