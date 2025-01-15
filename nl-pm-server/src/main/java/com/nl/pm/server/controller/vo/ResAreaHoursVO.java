package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ResAreaHoursVO",description = "各区小时数VO")
public class ResAreaHoursVO {

    @ApiModelProperty(value = "区域名称")
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
