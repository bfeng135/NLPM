package com.nl.pm.server.registry.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

@TableName("user")
public class UserEntity implements UserDetails {
    @TableId(type = IdType.AUTO)
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
    //@TableField(value = "token",updateStrategy = FieldStrategy.IGNORED )
    private String token;
    @TableField(exist = false)
    private String roleCode;
    @TableField(exist = false)
    private String roleName;
    @TableField(exist = false)
    private String areaName;
    @TableField(exist = false)
    private Double everyDayTime;
    @TableField(exist = false)
    private Double totalTime;
    @TableField(exist = false)
    private Date workDay;
    @TableField(exist = false)
    private Date everyLeaveTime;
    @TableField(exist = false)
    private Double everyLeaveHour;
    @TableField(exist = false)
    private String projectDesc;

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

    public Double getEveryDayTime() {
        return everyDayTime;
    }

    public void setEveryDayTime(Double everyDayTime) {
        if(everyDayTime == null){
            this.everyDayTime = new Integer(0).doubleValue();
        }else {
            this.everyDayTime = everyDayTime;
        }
    }

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

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
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

    public Double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Double totalTime) {
        if(totalTime == null){
            this.totalTime = new Integer(0).doubleValue();
        }else {
            this.totalTime = totalTime;
        }
    }

    public Date getWorkDay() {
        return workDay;
    }

    public void setWorkDay(Date workDay) {
        this.workDay = workDay;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }
}
