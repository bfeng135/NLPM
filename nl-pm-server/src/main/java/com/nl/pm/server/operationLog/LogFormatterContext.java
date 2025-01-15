package com.nl.pm.server.operationLog;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class LogFormatterContext {


    private final HttpServletRequest request;
    private final String className;
    private final Method method;
    private final Object[] parameters;
    private Object result;

    private Object contextData;
    private String moduleType;
    private String operateType;
    private String content;
    private Integer userId;
    private String username;
    private Integer areaId;


    public LogFormatterContext(HttpServletRequest request, String className, Method method, Object[] parameters) {
        this.request = request;
        this.className = className;
        this.method = method;
        this.parameters = parameters;
    }


    public HttpServletRequest getRequest() {
        return request;
    }

    public String getClassName() {
        return className;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getContextData() {
        return contextData;
    }

    public void setContextData(Object contextData) {
        this.contextData = contextData;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

}
