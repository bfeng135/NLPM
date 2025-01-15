package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ResOtherAreaAssUserVO",description = "查询关联的其他区域人员列表")
public class ResOtherAreaAssUserVO {

    @ApiModelProperty(value = "用户 id")
    private Integer userId;
    @ApiModelProperty(value = "用户姓名")
    private String nickname;
    @ApiModelProperty(value = "区域Id")
    private Integer areaId;
    @ApiModelProperty(value = "区域名称")
    private String areaName;

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

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
