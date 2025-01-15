package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
@ApiModel
public class ReqEditSystemInfoVO {
    @ApiModelProperty(value = "id")
    private Integer id;
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

//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

//    public String getHost() {
//        return host;
//    }
//
//    public void setHost(String host) {
//        this.host = host;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
}
