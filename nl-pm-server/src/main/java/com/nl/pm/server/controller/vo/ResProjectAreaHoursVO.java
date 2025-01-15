package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "ResProjectAreaHoursVO",description = "项目列表各区小时数VO")
public class ResProjectAreaHoursVO {

    @ApiModelProperty(value = "项目名称")
    private String name;
    @ApiModelProperty(value = "项目状态")
    private Boolean enable;
    @ApiModelProperty(value = "项目各区小时数列表")
    private List<ResAreaHoursVO> resData;

    public ResProjectAreaHoursVO(String name, List<ResAreaHoursVO> resData,Boolean enable) {
        this.name = name;
        this.resData = resData;
        this.enable = enable;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public ResProjectAreaHoursVO() {
        this.resData = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ResAreaHoursVO> getResData() {
        return resData;
    }

    public void setResData(List<ResAreaHoursVO> resData) {
        this.resData = resData;
    }
}
