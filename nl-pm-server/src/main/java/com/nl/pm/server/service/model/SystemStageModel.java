package com.nl.pm.server.service.model;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/17 15:07
 */
public class SystemStageModel {
    private String crmStageId;
    private String crmStageName;

    public String getCrmStageId() {
        return crmStageId;
    }

    public void setCrmStageId(String crmStageId) {
        this.crmStageId = crmStageId;
    }

    public String getCrmStageName() {
        return crmStageName;
    }

    public void setCrmStageName(String crmStageName) {
        this.crmStageName = crmStageName;
    }
}
