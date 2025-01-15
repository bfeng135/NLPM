package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * project
 */
@ApiModel(value = "ResProjectUpdateVO",description = "编辑项目 VO 返回类")
public class ResProjectUpdateVO {
    @ApiModelProperty(value = "是否调用 api 成功")
    private Boolean success;
    @ApiModelProperty(value = "询问用户是否强制更新")
    private Boolean askForceUpdate;

    public Boolean getAskForceUpdate() {
        return askForceUpdate;
    }

    public void setAskForceUpdate(Boolean askForceUpdate) {
        this.askForceUpdate = askForceUpdate;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
