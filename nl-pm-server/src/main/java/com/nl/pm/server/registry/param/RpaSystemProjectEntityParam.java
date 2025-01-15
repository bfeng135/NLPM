package com.nl.pm.server.registry.param;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/16 13:44
 */
public class RpaSystemProjectEntityParam {
    private Integer id;
    private String name;
    private String crmProjectId;
    private String crmStageId;
    private String crmStageName;
    private String crmCustomerNameShort;
    private Boolean enable;

    public String getCrmCustomerNameShort() {
        return crmCustomerNameShort;
    }

    public void setCrmCustomerNameShort(String crmCustomerNameShort) {
        this.crmCustomerNameShort = crmCustomerNameShort;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        if(enable == null){
            this.enable = true;
        }else {
            this.enable = false;
        }
    }
}
