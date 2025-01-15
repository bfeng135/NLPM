package com.nl.pm.server.controller.vo;

import com.nl.pm.server.registry.param.AreaProjectCostEntityParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "ResAreaCostVO",description = "区域项目工时消耗列表 VO")
public class ResAreaCostVO {

    @ApiModelProperty(value = "总工时")
    private Double allHours;
    @ApiModelProperty(value = "项目消耗列表")
    private List<AreaProjectCostEntityParam> projectCostVOList ;

    public ResAreaCostVO() {
        this.projectCostVOList = new ArrayList<>();
    }

    public Double getAllHours() {
        return allHours;
    }

    public void setAllHours(Double allHours) {
        this.allHours = allHours;
    }

    public List<AreaProjectCostEntityParam> getProjectCostVOList() {
        return projectCostVOList;
    }

    public void setProjectCostVOList(List<AreaProjectCostEntityParam> projectCostVOList) {
        this.projectCostVOList = projectCostVOList;
    }
}
