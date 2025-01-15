package com.nl.pm.server.controller.vo;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/16 16:32
 */
public class ResCrmSyncVO {
    private String errorCode;
    private String errorMessage;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
