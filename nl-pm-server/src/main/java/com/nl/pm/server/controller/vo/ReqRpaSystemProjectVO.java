package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/16 8:31
 */
@ApiModel(value = "rpa请求对象",description = "rpa请求对象VO类")
public class ReqRpaSystemProjectVO {
    @ApiModelProperty(value = "客户简称")
    private String crmCustomerNameShort;
    @ApiModelProperty(value = "项目名称")
    private String name;
    @ApiModelProperty(value = "Crm系统对应项目id，该字段由rpa传入，且存入后不可更改")
    private String crmProjectId;
    @ApiModelProperty(value = "Crm状态阶段ID,该字段由rpa传入，且存入后不可更改")
    private String crmStageId;
    @ApiModelProperty(value = "Crm状态阶段名称")
    private String crmStageName;

    public String getCrmCustomerNameShort() {
        return crmCustomerNameShort;
    }

    public void setCrmCustomerNameShort(String crmCustomerNameShort) {
        this.crmCustomerNameShort = crmCustomerNameShort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
