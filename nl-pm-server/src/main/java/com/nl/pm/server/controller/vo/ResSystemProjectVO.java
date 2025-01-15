package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author pf
 * @version 1.0
 * @date 2021/8/24 15:10
 */
@ApiModel
public class ResSystemProjectVO {
    @ApiModelProperty(value = "项目id")
    private Integer id;
    @ApiModelProperty(value = "项目名称")
    private String name;
    @ApiModelProperty(value = "项目状态")
    private Boolean enable;
    @ApiModelProperty(value = "创建时间")
    private Long createTime;
    @ApiModelProperty(value = "更新时间")
    private Long updateTime;
    @ApiModelProperty(value = "区域Id")
    private Integer areaId;
    @ApiModelProperty(value = "项目完结目标小时数")
    private Double goalHours;
    @ApiModelProperty(value = "区域名称")
    private String areaName;
    @ApiModelProperty(value = "Crm系统对应项目id，该字段由rpa传入，且存入后不可更改")
    private String crmProjectId;
    @ApiModelProperty(value = "Crm状态阶段ID,该字段由rpa传入，且存入后不可更改")
    private String crmStageId;
    @ApiModelProperty(value = "Crm状态阶段名称")
    private String crmStageName;
    @ApiModelProperty(value = "日报描述强制标识")
    private Boolean forceDescFlag;

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

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
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
