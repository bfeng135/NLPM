package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * project
 */
@ApiModel(value = "ReqProjectCreateVO",description = "创建项目 VO 类")
public class ReqProjectCreateVO {

    @ApiModelProperty(value = "项目名称")
    private String name;
    @ApiModelProperty(value = "项目描述")
    private String desc;
    @ApiModelProperty(value = "区域ID")
    private Integer areaId;
    @ApiModelProperty(value = "项目负责人ID")
    private Integer managerId;
    @ApiModelProperty(value = "强制更新标识")
    private Boolean forceFlag;

    public Boolean getForceFlag() {
        return forceFlag;
    }

    public void setForceFlag(Boolean forceFlag) {
        this.forceFlag = forceFlag;
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

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }
}
