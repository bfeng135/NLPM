package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "ResProjectUserHoursVO",description = "项目列表各人员小时数VO")
public class ResProjectUserHoursVO {

    @ApiModelProperty(value = "项目名称")
    private String name;
    @ApiModelProperty(value = "项目状态")
    private Boolean enable;
    @ApiModelProperty(value = "项目各人员小时数列表")
    private List<ResUserHoursVO> resData;

    public ResProjectUserHoursVO(String name, List<ResUserHoursVO> resData, Boolean enable) {
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

    public ResProjectUserHoursVO() {
        this.resData = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ResUserHoursVO> getResData() {
        return resData;
    }

    public void setResData(List<ResUserHoursVO> resData) {
        this.resData = resData;
    }
}
