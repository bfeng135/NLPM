package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ResNoneReportUserVO",description = "未写的日报的员工 VO")
public class ResNoneReportUserVO {

    @ApiModelProperty(value = "员工 id")
    private Integer userId;
    @ApiModelProperty(value = "员工姓名")
    private String nickname;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
