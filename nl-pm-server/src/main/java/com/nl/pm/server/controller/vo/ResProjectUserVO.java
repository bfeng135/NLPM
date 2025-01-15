package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ResProjectUserVO",description = "项目关联成员 VO")
public class ResProjectUserVO {
    @ApiModelProperty(value = "员工 ID")
    private Integer userId;
    @ApiModelProperty(value = "用户姓名")
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
