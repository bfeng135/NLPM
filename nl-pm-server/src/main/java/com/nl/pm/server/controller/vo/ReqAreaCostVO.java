package com.nl.pm.server.controller.vo;

import com.nl.pm.server.common.enums.CostTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ReqAreaCostVO {


    @ApiModelProperty(value = "开始时间")
    private Long startTime;
    @ApiModelProperty(value = "结束时间")
    private Long endTime;
    @ApiModelProperty(value = "区域ID")
    private Integer areaId;
    @ApiModelProperty(value = "消耗类型枚举")
    private CostTypeEnum costTypeEnum;


    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public CostTypeEnum getCostTypeEnum() {
        return costTypeEnum;
    }

    public void setCostTypeEnum(CostTypeEnum costTypeEnum) {
        this.costTypeEnum = costTypeEnum;
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
