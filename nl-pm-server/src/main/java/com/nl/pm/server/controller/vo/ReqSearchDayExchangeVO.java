package com.nl.pm.server.controller.vo;

import com.nl.pm.server.common.pages.BasePagesParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ReqSearchDayExchangeVO",description = "查询人员调休列表请求 VO")
public class ReqSearchDayExchangeVO extends BasePagesParam {

    @ApiModelProperty(value = "区域 id")
    private Integer areaId;

    @ApiModelProperty(value = "员工姓名")
    private String nickname;

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
