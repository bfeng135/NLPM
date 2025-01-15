package com.nl.pm.server.controller.vo;

import com.nl.pm.server.common.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/6 9:35
 */
public class ResDownSystemProjectVO {
    private Integer num;
    private String projectName;
    private String enable;
    private String crmProjectId;
    private String crmStageId;
    private String crmStageName;
    private Integer areaId;
    private String areaName;
    private String createTime;
    private String updateTime;

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        if(enable == null){
            this.enable = StringUtils.EMPTY;
        }
        if(enable){
            this.enable = "是";
        }else {
            this.enable = "否";
        }
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        if(createTime == null){
            this.createTime = StringUtils.EMPTY;
        }else {
            this.createTime = DateUtils.convertDateToStr(DateUtils.convertLongToDate(createTime));
        }
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        if(updateTime == null){
            this.updateTime = StringUtils.EMPTY;
        }else {
            this.updateTime = DateUtils.convertDateToStr(DateUtils.convertLongToDate(updateTime));
        }
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
        if(crmStageId == null){
            this.crmStageId = StringUtils.EMPTY;
        }else {
            this.crmStageId = String.valueOf(crmStageId);
        }
    }

    public String getCrmStageName() {
        return crmStageName;
    }

    public void setCrmStageName(String crmStageName) {
        if(crmStageName == null){
            this.crmStageName = StringUtils.EMPTY;
        }else {
            this.crmStageName = crmStageName;
        }
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
}
