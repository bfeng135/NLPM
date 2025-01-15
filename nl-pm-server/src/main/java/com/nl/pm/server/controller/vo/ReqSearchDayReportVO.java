package com.nl.pm.server.controller.vo;

import com.nl.pm.server.common.pages.BasePagesParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;

@ApiModel(value = "ReqSearchDayReportVO",description = "查询日报列表请求 VO")
public class ReqSearchDayReportVO extends BasePagesParam {
    @ApiModelProperty(value = "开始日期")
    private Long startDate;
    @ApiModelProperty(value = "结束日期")
    private Long endDate;
    @ApiModelProperty(value = "员工 id")
    private Integer userId;
    @ApiModelProperty(value = "项目 id")
    private Integer projectId;
    private Map<String,Object> map = new HashMap<>();

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
