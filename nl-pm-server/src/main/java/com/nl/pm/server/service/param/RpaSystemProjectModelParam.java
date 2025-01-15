package com.nl.pm.server.service.param;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/16 13:07
 */
public class RpaSystemProjectModelParam {
    private String crmCustomerNameShort;
    private String name;
    private String crmProjectId;
    private String crmStageId;
    private String crmStageName;

    public String getCrmCustomerNameShort() {
        return crmCustomerNameShort;
    }

    public void setCrmCustomerNameShort(String crmCustomerNameShort) {
        this.crmCustomerNameShort = crmCustomerNameShort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCrmStageName() {
        return crmStageName;
    }

    public void setCrmStageName(String crmStageName) {
        this.crmStageName = crmStageName;
    }
}
