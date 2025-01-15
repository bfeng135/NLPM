package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "ResDayReportVO",description = "日报详情返回 VO")
public class ResDayReportVO {
    @ApiModelProperty(value = "日报 id")
    private Integer id;
    @ApiModelProperty(value = "日期")
    private Long date;
    @ApiModelProperty(value = "员工 id")
    private Integer userId;
    @ApiModelProperty(value = "员工区域")
    private String areaName;
    @ApiModelProperty(value = "员工姓名")
    private String nickname;
    @ApiModelProperty(value = "日报任务列表")
    private List<ReqDayReportVO> dayReportList;
    @ApiModelProperty(value = "请假信息")
    private ReqLeaveVO leaveVO;
    @ApiModelProperty(value = "是否支持修改")
    private Boolean isEdit;

    public ResDayReportVO() {
        dayReportList = new ArrayList<>();
        leaveVO = new ReqLeaveVO();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<ReqDayReportVO> getDayReportList() {
        return dayReportList;
    }

    public void setDayReportList(List<ReqDayReportVO> dayReportList) {
        this.dayReportList = dayReportList;
    }

    public ReqLeaveVO getLeaveVO() {
        return leaveVO;
    }

    public void setLeaveVO(ReqLeaveVO leaveVO) {
        this.leaveVO = leaveVO;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Boolean getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(Boolean edit) {
        isEdit = edit;
    }
}
