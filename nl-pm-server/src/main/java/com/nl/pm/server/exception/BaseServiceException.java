package com.nl.pm.server.exception;

import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;

public class BaseServiceException extends Exception{
    private ServiceErrorCodeEnum serviceErrorCodeEnum;

    public BaseServiceException(ServiceErrorCodeEnum serviceErrorCodeEnum) {
        super(serviceErrorCodeEnum.name());
        this.serviceErrorCodeEnum = serviceErrorCodeEnum;
    }

    public ServiceErrorCodeEnum getServiceErrorCodeEnum() {
        return serviceErrorCodeEnum;
    }

    public void setServiceErrorCodeEnum(ServiceErrorCodeEnum serviceErrorCodeEnum) {
        this.serviceErrorCodeEnum = serviceErrorCodeEnum;
    }

}
