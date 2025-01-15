package com.nl.pm.server.registry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * @author pf
 * @version 1.0
 * @date 2021/8/24 14:51
 */
@TableName("system_project")
public class SystemProjectEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Boolean enable;
    @TableField(exist = false)
    private Boolean forceDescFlag;
    private Date createTime;
    private Date updateTime;
    private Integer areaId;
    private Double goalHours;
    private String crmProjectId;
    private String crmStageId;
    @TableField(exist = false)
    private String crmStageName;
    private String def2;
    private String def3;
    private String def4;
    private String def5;
    @TableField(exist = false)
    private String areaName;
    private String crmCustomerNameShort;

    public String getCrmCustomerNameShort() {
        return crmCustomerNameShort;
    }

    public void setCrmCustomerNameShort(String crmCustomerNameShort) {
        this.crmCustomerNameShort = crmCustomerNameShort;
    }

    public Double getGoalHours() {
        return goalHours;
    }

    public void setGoalHours(Double goalHours) {
        this.goalHours = goalHours;
    }

    public Boolean getForceDescFlag() {
        return forceDescFlag;
    }

    public void setForceDescFlag(Boolean forceDescFlag) {
        this.forceDescFlag = forceDescFlag;
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

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getDef2() {
        return def2;
    }

    public void setDef2(String def2) {
        this.def2 = def2;
    }

    public String getDef3() {
        return def3;
    }

    public void setDef3(String def3) {
        this.def3 = def3;
    }

    public String getDef4() {
        return def4;
    }

    public void setDef4(String def4) {
        this.def4 = def4;
    }

    public String getDef5() {
        return def5;
    }

    public void setDef5(String def5) {
        this.def5 = def5;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
