package com.nl.pm.server.service.param;

import java.util.ArrayList;
import java.util.List;

public class AreaProjectCostModelParam {

    private Integer projectId;
    private String projectName;
    private Integer areaId;
    private String areaName;
    private Double projectHours;

    private List<AreaProjectCostDetailModelParam> detailList;

    public AreaProjectCostModelParam() {
        detailList = new ArrayList<>();
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Double getProjectHours() {
        return projectHours;
    }

    public void setProjectHours(Double projectHours) {
        this.projectHours = projectHours;
    }

    public List<AreaProjectCostDetailModelParam> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<AreaProjectCostDetailModelParam> detailList) {
        this.detailList = detailList;
    }
}
