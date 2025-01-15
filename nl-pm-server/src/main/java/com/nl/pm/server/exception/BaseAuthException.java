package com.nl.pm.server.exception;

import com.nl.pm.server.exception.errorEnum.AuthErrorCodeEnum;

public class BaseAuthException extends Exception{
    private AuthErrorCodeEnum authErrorCodeEnum;


    public BaseAuthException(AuthErrorCodeEnum authErrorCodeEnum) {
        super(authErrorCodeEnum.name());
        this.authErrorCodeEnum = authErrorCodeEnum;
    }

    public AuthErrorCodeEnum getAuthErrorCodeEnum() {
        return authErrorCodeEnum;
    }

    public void setAuthErrorCodeEnum(AuthErrorCodeEnum authErrorCodeEnum) {
        this.authErrorCodeEnum = authErrorCodeEnum;
    }
}
