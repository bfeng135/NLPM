package com.nl.pm.server.controller.vo;

import com.nl.pm.server.common.pages.BasePagesParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ResDayExchangeVO",description = "查询人员调休列表请求 VO")
public class ResLeaveVO extends BasePagesParam {

    @ApiModelProperty(value = "区域 id")
    private Integer areaId;

    @ApiModelProperty(value = "区域名称")
    private String areaName;

    @ApiModelProperty(value = "员工 id")
    private Integer userId;

    @ApiModelProperty(value = "员工姓名")
    private String nickname;
    @ApiModelProperty(value = "请假时长")
    private Double leaveHour;


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

    public Double getLeaveHour() {
        return leaveHour;
    }

    public void setLeaveHour(Double leaveHour) {
        this.leaveHour = leaveHour;
    }


}
