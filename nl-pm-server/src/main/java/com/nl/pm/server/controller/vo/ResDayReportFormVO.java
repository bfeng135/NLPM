package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/3 11:01
 */
public class ResDayReportFormVO {
    private Integer id;
    private Integer userId;
    private String nickname;
    private String date;
    private String leaveHours;
    private String leaveDesc;
    private Integer projectId;
    private String projectName;
    private String projectDesc;
    private String projectHours;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLeaveHours() {
        return leaveHours;
    }

    public void setLeaveHours(Double leaveHours) {
        if(leaveHours == null || leaveHours == 0.0){
            this.leaveHours = StringUtils.EMPTY;
        }else {
            this.leaveHours = String.valueOf(leaveHours);
        }
    }

    public String getLeaveDesc() {
        return leaveDesc;
    }

    public void setLeaveDesc(String leaveDesc) {
        this.leaveDesc = leaveDesc;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }

    public String getProjectHours() {
        return projectHours;
    }

    public void setProjectHours(Double projectHours) {
        if(projectHours == null || projectHours == 0.0){
            this.projectHours = StringUtils.EMPTY;
        }else {
            this.projectHours =String.valueOf(projectHours);
        }
    }
}
