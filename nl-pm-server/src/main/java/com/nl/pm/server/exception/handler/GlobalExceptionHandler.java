package com.nl.pm.server.exception.handler;

import com.nl.pm.server.exception.BaseAuthException;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.DynamicServiceException;
import com.nl.pm.server.exception.errorEnum.AuthErrorCodeEnum;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.exception.model.MessageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    MessageResult result = new MessageResult();


    /**
     * 业务异常
     */
    @ExceptionHandler({ BaseServiceException.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public MessageResult handleException(BaseServiceException e) {
        log.error(e.getMessage(), e);
        ServiceErrorCodeEnum serviceErrorCodeEnum = e.getServiceErrorCodeEnum();
        result.setErrorCode(serviceErrorCodeEnum.getErrorCode());
        result.setErrorMessage(serviceErrorCodeEnum.getErrorMessage());
        return result;
    }

    /**
     * 权限异常
     */
    @ExceptionHandler({ BaseAuthException.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public MessageResult handleException(BaseAuthException e) {
        log.error(e.getMessage(), e);
        AuthErrorCodeEnum authErrorCodeEnum = e.getAuthErrorCodeEnum();
        result.setErrorCode(authErrorCodeEnum.getErrorCode());
        result.setErrorMessage(authErrorCodeEnum.getErrorMessage());
        return result;
    }

    /**
     * 动态异常
     */
    @ExceptionHandler({ DynamicServiceException.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public MessageResult handleException(DynamicServiceException e) {
        log.error(e.getMessage(), e);
        result.setErrorCode("500");
        result.setErrorMessage(e.getErrorMSG());
        return result;
    }




    /**
     * 请求方式不支持
     */
    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public MessageResult handleException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        result.setErrorCode("500");
        result.setErrorMessage("不支持' " + e.getMethod() + "'请求");
        return result;
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public MessageResult notFount(RuntimeException e) {
        log.error("运行时异常:", e);
        result.setErrorCode("500");
        result.setErrorMessage("运行时异常:" + e.getMessage());
        return result;
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public MessageResult handleException(Exception e) {
        log.error(e.getMessage(), e);
        result.setErrorCode("500");
        result.setErrorMessage("服务器错误，请联系管理员");
        return result;
    }
}
