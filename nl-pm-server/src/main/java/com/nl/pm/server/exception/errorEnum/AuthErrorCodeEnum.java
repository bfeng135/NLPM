package com.nl.pm.server.exception.errorEnum;

public enum AuthErrorCodeEnum {

    //代码相关  从 401-0001 开始


    //用户管理相关  从 401-1001 开始
    NO_AUTH(401,"401-1001","未获得权限进行此操作！"),
    TOKEN_SIGNATURE_ERROR(401,"401-1002","TOKEN密钥错误！"),
    TOKEN_EXPIRE_ERROR(401,"401-1003","TOKEN已过期！"),
    TOKEN_NOT_EXIST_ERROR(401,"401-1004","TOKEN不存在！"),
    USER_OPERATE_AREA_ERROR(401,"401-1005","用户无操作此区域权限"),
    USER_ADD_AREA_ERROR(401,"401-1006","用户添加其他区域用户权限"),
    USER_RESET_ERROR(401,"401-1007","该用户无修改密码权限"),
    PASSWORD_NULL_ERROR(401,"401-1008","密码不能为空！"),
    LOGIN_ERROR(401,"401-1009","登录失败！请核对区域、人员、密码！"),
    USER_NONE_ERROR(401,"401-1010","当前用户不存在！"),

    //区域管理相关  从 401-2001 开始
    NO_AUTH_OPERATE_AREA(401,"401-2001","您无管理区域的权限！"),
    NO_AUTH_CHOOSE_AREA_TO_USER(401,"401-2002","您没有给人员分配区域的权限！"),

    //项目管理相关  从 401-3001 开始
    PROJECT_CREATE_AUTH_ERROR(401,"401-3001","新建项目权限错误！"),
    PROJECT_FETCH_AUTH_ERROR(401,"401-3002","您无权限查看此项目！"),
    PROJECT_OPERATE_AUTH_ERROR(401,"401-3003","您无权限操作此项目！"),
    PROJECT_AREA_BELONG_ERROR(401,"401-3004","您无权操作其他区下的项目"),
    PROJECT_USER_BELONG_ERROR(401,"401-3005","您无权查看其他区下的员工"),
    //日报管理相关  从 401-4001 开始
    DAY_REPORT_NOT_MYSELF_ERROR(401,"401-4001","您只能操作自己的日报"),

    //系统管理相关  从 401-5001 开始


    //假日管理相关  从 401-6001 开始



    ;


    private int httpCode;
    private String errorCode;
    private String errorMessage;

    AuthErrorCodeEnum(int httpCode, String errorCode, String errorMessage) {
        this.httpCode = httpCode;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

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
