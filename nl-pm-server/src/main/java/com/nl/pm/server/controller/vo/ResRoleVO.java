package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ResRoleVO",description = "角色返回对象VO类")
public class ResRoleVO {
    @ApiModelProperty(value = "角色id")
    private Integer id;
    @ApiModelProperty(value = "用户名称")
    private String name;

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
}
