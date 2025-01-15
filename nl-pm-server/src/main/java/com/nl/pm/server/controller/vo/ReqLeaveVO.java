package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ReqLeaveVO",description = "请假详情 VO")
public class ReqLeaveVO {

    @ApiModelProperty(value = "请假时长")
    private Double leaveHours;
    @ApiModelProperty(value = "请假描述")
    private String desc;



    public Double getLeaveHours() {
        return leaveHours;
    }

    public void setLeaveHours(Double leaveHours) {
        this.leaveHours = leaveHours;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
