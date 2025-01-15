package com.nl.pm.server.exception;

public class DynamicServiceException extends Exception{
    private String errorMSG;

    public DynamicServiceException(String errorMSG) {
        super(errorMSG);
        this.errorMSG = errorMSG;
    }

    public String getErrorMSG() {
        return errorMSG;
    }

    public void setErrorMSG(String errorMSG) {
        this.errorMSG = errorMSG;
    }
}
