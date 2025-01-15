package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ResAreaUserCostDetailVO",description = "人员消耗明细")
public class ResAreaUserCostDetailVO {

    @ApiModelProperty(value = "员工姓名")
    private String nickname;
    @ApiModelProperty(value = "日期")
    private Long reportDate;
    @ApiModelProperty(value = "描述")
    private String desc;
    @ApiModelProperty(value = "工时")
    private Double hours;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Long getReportDate() {
        return reportDate;
    }

    public void setReportDate(Long reportDate) {
        this.reportDate = reportDate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }
}
