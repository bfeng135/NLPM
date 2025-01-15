package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "编辑区域状态",description = "编辑区域状态")
public class ReqUpdateAreaStatusVO {

    @ApiModelProperty(value = "区域id")
    private Integer id;

    @ApiModelProperty(value = "区域状态（true:启用，false:禁用）")
    private Boolean status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
