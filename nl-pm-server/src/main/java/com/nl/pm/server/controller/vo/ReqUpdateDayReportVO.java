package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "ReqUpdateDayReportVO",description = "编辑日报请求 VO")
public class ReqUpdateDayReportVO {
    @ApiModelProperty(value = "日报 id")
    private Integer id;
    @ApiModelProperty(value = "日期")
    private Long date;
    @ApiModelProperty(value = "日报任务列表")
    private List<ReqDayReportVO> dayReportList;
    @ApiModelProperty(value = "请假信息")
    private ReqLeaveVO leaveVO;

    public ReqUpdateDayReportVO() {
        dayReportList = new ArrayList<>();
        leaveVO = new ReqLeaveVO();
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
