package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel
public class ReqAddSystemVariableVO {

    @ApiModelProperty(value = "截止时间")
    private String deadline;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
