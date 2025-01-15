package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author pf
 * @version 1.0
 * @date 2021/8/24 15:16
 */
@ApiModel
public class ReqSystemProjectGoalHoursVO {
    @ApiModelProperty(value = "项目id")
    private Integer id;
    @ApiModelProperty(value = "目标小时数")
    private Double goalHours;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getGoalHours() {
        return goalHours;
    }

    public void setGoalHours(Double goalHours) {
        this.goalHours = goalHours;
    }
}
