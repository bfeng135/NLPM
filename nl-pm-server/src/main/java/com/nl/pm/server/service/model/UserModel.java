package com.nl.pm.server.service.model;


import com.baomidou.mybatisplus.annotation.TableField;

import java.util.Date;

public class UserModel {
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private Integer roleId;
    private Integer areaId;
    private String email;
    private String phone;
    private Boolean status;
    private Boolean emailNotice;
    private Date createTime;
    private Date updateTime;
    private String token;
    private String roleCode;
    private String roleName;
    private String areaName;
    private Double everyDayTime;
    private Double totalTime;
    private Date workDay;
    private Date everyLeaveTime;
    private Double everyLeaveHour;

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getEmailNotice() {
        return emailNotice;
    }

    public void setEmailNotice(Boolean emailNotice) {
        this.emailNotice = emailNotice;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Double getEveryDayTime() {
        return everyDayTime;
    }

    public void setEveryDayTime(Double everyDayTime) {
        this.everyDayTime = everyDayTime;
    }

    public Double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Double totalTime) {
        this.totalTime = totalTime;
    }

    public Date getWorkDay() {
        return workDay;
    }

    public void setWorkDay(Date workDay) {
        this.workDay = workDay;
    }

    public Date getEveryLeaveTime() {
        return everyLeaveTime;
    }

    public void setEveryLeaveTime(Date everyLeaveTime) {
        this.everyLeaveTime = everyLeaveTime;
    }

    public Double getEveryLeaveHour() {
        return everyLeaveHour;
    }

    public void setEveryLeaveHour(Double everyLeaveHour) {
        this.everyLeaveHour = everyLeaveHour;
    }

    //    @Override
//    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + username.hashCode();
//        result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
//        return result;
//    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserModel other = (UserModel) obj;
        if (!username.trim().equals(other.username.trim())){
            return false;
        }
        if (nickname == null) {
            if (other.nickname != null)
                return false;
        } else if (!nickname.equals(other.nickname))
            return false;
        return true;
    }

}
