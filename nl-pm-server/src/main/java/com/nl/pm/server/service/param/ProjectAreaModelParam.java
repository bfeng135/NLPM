package com.nl.pm.server.service.param;

import java.util.List;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/9 9:49
 */
public class ProjectAreaModelParam {
    private Integer projectId;
    private Integer areaId;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }
}
