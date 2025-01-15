package com.nl.pm.server.service.param;

import com.nl.pm.server.common.pages.BasePagesParam;

import java.util.Date;
import java.util.List;

public class DayReportSearchModelParam extends BasePagesParam {


    private Integer projectId;
    private Integer userId;
    private Date startDate;
    private Date endDate;
    private List<Integer> limitUser;
    private List<Integer> limitProject;

    public List<Integer> getLimitProject() {
        return limitProject;
    }

    public void setLimitProject(List<Integer> limitProject) {
        this.limitProject = limitProject;
    }

    public List<Integer> getLimitUser() {
        return limitUser;
    }

    public void setLimitUser(List<Integer> limitUser) {
        this.limitUser = limitUser;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
