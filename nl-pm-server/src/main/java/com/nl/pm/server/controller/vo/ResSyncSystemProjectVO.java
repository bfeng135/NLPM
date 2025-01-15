package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ResSyncSystemProjectVO",description = "返回同步项目字典对象VO类")
public class ResSyncSystemProjectVO {
    @ApiModelProperty(value = "同步结果状态")
    private boolean result;
    @ApiModelProperty(value = "返回消息")
    private String message;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
