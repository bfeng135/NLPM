package com.nl.pm.server.service.param;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/1 15:32
 */
public class ProjectUserCountModelParam {
    private String projectName;
    private Integer countUser;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getCountUser() {
        return countUser;
    }

    public void setCountUser(Integer countUser) {
        this.countUser = countUser;
    }
}
