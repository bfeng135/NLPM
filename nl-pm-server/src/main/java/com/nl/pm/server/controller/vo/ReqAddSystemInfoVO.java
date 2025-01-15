package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "添加系统邮件对象",description = "添加系统邮件对象VO类")
public class ReqAddSystemInfoVO {
    @ApiModelProperty(value = "系统名称")
    private String name;
//    @ApiModelProperty(value = "系统邮件服务器地址")
//    private String host;
//    @ApiModelProperty(value = "系统邮件地址")
//    private String email;
//    @ApiModelProperty(value = "系统邮件密码")
//    private String password;
    @ApiModelProperty(value = "系统描述")
    private String desc;

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
}
