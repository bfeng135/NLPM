package com.nl.pm.server.service.model;

import java.util.Date;

public class UserComeLeaveModel {

    private Integer id;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 在职时间
     */
    private Date comeDate;
    /**
     * 离开时间
     */
    private Date leaveDate;

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

    public Date getComeDate() {
        return comeDate;
    }

    public void setComeDate(Date comeDate) {
        this.comeDate = comeDate;
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }
}
