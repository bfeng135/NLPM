package com.nl.pm.server.service.model;

/**
 * @author pf
 * @version 1.0
 * @date 2021/8/27 9:51
 */
public class SystemEmailModel {
    private Integer id;
    private String host;
    private String username;
    private String password;
    private Integer sendNum = 0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public Integer getSendNum() {
        return sendNum;
    }

    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }
}
