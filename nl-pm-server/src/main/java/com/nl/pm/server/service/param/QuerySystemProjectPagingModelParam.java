package com.nl.pm.server.service.param;

import com.nl.pm.server.common.pages.BasePagesParam;

/**
 * @author pf
 * @version 1.0
 * @date 2021/8/25 9:51
 */
public class QuerySystemProjectPagingModelParam extends BasePagesParam {
    private String systemProjectName;
    private String crmProjectId;
    private String crmStageId;
    private Integer areaId;
    private Boolean enable;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getSystemProjectName() {
        return systemProjectName;
    }

    public void setSystemProjectName(String systemProjectName) {
        this.systemProjectName = systemProjectName;
    }

    public String getCrmProjectId() {
        return crmProjectId;
    }

    public void setCrmProjectId(String crmProjectId) {
        this.crmProjectId = crmProjectId;
    }

    public String getCrmStageId() {
        return crmStageId;
    }

    public void setCrmStageId(String crmStageId) {
        this.crmStageId = crmStageId;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }
}
