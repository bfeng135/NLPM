package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "编辑区域请求参数",description = "区域对象VO类")
public class ReqUpdateAreaVO {

    @ApiModelProperty(value = "区域id")
    private Integer id;

    @ApiModelProperty(value = "区域名字")
    private String name;

    @ApiModelProperty(value = "区域描述")
    private String desc;

    @ApiModelProperty(value = "区域管理员id")
    private Integer managerId;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }
}
