package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * project
 */
@ApiModel(value = "ResBoardProjectVO",description = "看板项目 VO 类")
public class ResBoardProjectVO {

    @ApiModelProperty(value = "项目id")
    private Integer id;
    @ApiModelProperty(value = "项目名称")
    private String name;
    @ApiModelProperty(value = "工时")
    private Double value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
