package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ResAreaVO",description = "查询成功返回对象VO类")
public class ResAreaVO {
    /**
     * 区域Id
     */
    @ApiModelProperty(value = "区域Id")
    private Integer id;

    /**
     * 区域名字
     */
    @ApiModelProperty(value = "区域名字")
    private String name;

    /**
     * 区域状态
     */
    @ApiModelProperty(value = "区域状态")
    private Boolean status;

    /**
     * 区域描述
     */
    @ApiModelProperty(value = "区域描述")
    private String desc;

    /**
     * 区域负责人Id
     */
    @ApiModelProperty(value = "区域负责人Id")
    private Integer managerId;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Long updateTime;


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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


    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
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
}
