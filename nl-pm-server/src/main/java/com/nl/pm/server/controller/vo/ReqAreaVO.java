package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "区域查询请求参数",description = "区域对象VO类")
public class ReqAreaVO {

    @ApiModelProperty(value = "页码")
    private Integer pageNumber;

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;

    @ApiModelProperty(value = "全局搜索遍历")
    private String golbalName;

    @ApiModelProperty(value = "区域状态标识（true为启用，false为禁用，null为全部）")
    private Boolean statusFlag;

    public Boolean getStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(Boolean statusFlag) {
        this.statusFlag = statusFlag;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getGolbalName() {
        return golbalName;
    }

    public void setGolbalName(String golbalName) {
        this.golbalName = golbalName;
    }
}
