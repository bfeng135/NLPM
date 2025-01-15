package com.nl.pm.server.service.param;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/1 15:32
 */
public class CrmProjectSyncParam {
    private String customerNameShort;
    private String projectId;
    private String projectdecription;
    private String saleStageID;
    private String stageStatus;


    public String getCustomerNameShort() {
        return customerNameShort;
    }

    public void setCustomerNameShort(String customerNameShort) {
        this.customerNameShort = customerNameShort;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectdecription() {
        return projectdecription;
    }

    public void setProjectdecription(String projectdecription) {
        this.projectdecription = projectdecription;
    }

    public String getSaleStageID() {
        return saleStageID;
    }

    public void setSaleStageID(String saleStageID) {
        this.saleStageID = saleStageID;
    }

    public String getStageStatus() {
        return stageStatus;
    }

    public void setStageStatus(String stageStatus) {
        this.stageStatus = stageStatus;
    }
}
