package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ResUserHoursVO",description = "各人员小时数VO")
public class ResUserHoursVO {

    @ApiModelProperty(value = "用户名称")
    private String name;
    @ApiModelProperty(value = "小时数")
    private Double value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
