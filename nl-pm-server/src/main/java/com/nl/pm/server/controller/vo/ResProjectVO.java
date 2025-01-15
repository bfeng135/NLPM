package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * project
 */
@ApiModel(value = "ResProjectVO",description = "项目 VO 类")
public class ResProjectVO {

    @ApiModelProperty(value = "项目id")
    private Integer id;
    @ApiModelProperty(value = "项目名称")
    private String name;
    @ApiModelProperty(value = "项目描述")
    private String desc;
    @ApiModelProperty(value = "区域ID")
    private Integer areaId;
    @ApiModelProperty(value = "区域名称")
    private String areaName;
    @ApiModelProperty(value = "项目负责人ID")
    private Integer managerId;
    @ApiModelProperty(value = "项目负责人姓名")
    private String managerName;
    @ApiModelProperty(value = "项目状态（启用：true，禁用：false）")
    private Boolean enable;
    @ApiModelProperty(value = "创建时间")
    private Long createTime;
    @ApiModelProperty(value = "更新时间")
    private Long updateTime;
    @ApiModelProperty(value = "领导人标识")
    private Boolean leaderFlag;
    @ApiModelProperty(value = "强制日报描述标识")
    private Boolean forceDescFlag;
    @ApiModelProperty(value = "项目字典ID")
    private Integer systemProjectId;

    public Integer getSystemProjectId() {
        return systemProjectId;
    }

    public void setSystemProjectId(Integer systemProjectId) {
        this.systemProjectId = systemProjectId;
    }

    public Boolean getForceDescFlag() {
        return forceDescFlag;
    }

    public void setForceDescFlag(Boolean forceDescFlag) {
        this.forceDescFlag = forceDescFlag;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
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

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public Boolean getLeaderFlag() {
        return leaderFlag;
    }

    public void setLeaderFlag(Boolean leaderFlag) {
        this.leaderFlag = leaderFlag;
    }
}
