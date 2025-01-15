package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "ResNoneReportVO",description = "未写的日报 VO")
public class ResNoneReportVO {

    @ApiModelProperty(value = "日期")
    private Long date;
    @ApiModelProperty(value = "员工列表")
    private List<ResNoneReportUserVO> userList;

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public List<ResNoneReportUserVO> getUserList() {
        return userList;
    }

    public void setUserList(List<ResNoneReportUserVO> userList) {
        this.userList = userList;
    }
}
