package com.nl.pm.server.exception.model;

public class MessageResult {
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


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
          sb.append("{")
            .append("\"errorCode\"").append(":").append(errorCode)
            .append(",")
            .append("\"errorMessage\"").append(":").append(errorMessage)
            .append("}");

        return sb.toString();
    }
}
