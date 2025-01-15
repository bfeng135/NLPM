package com.nl.pm.server.registry.entity;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/17 15:08
 */
public class SystemStageEntity {
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

    public SystemStageEntity(String crmStageId, String crmStageName) {
        this.crmStageId = crmStageId;
        this.crmStageName = crmStageName;
    }

    public SystemStageEntity() {
    }

    @Override
    public String toString() {
        return "SystemStageEntity{" +
                "crmStageId=" + crmStageId +
                ", crmStageName='" + crmStageName + '\'' +
                '}';
    }
}
