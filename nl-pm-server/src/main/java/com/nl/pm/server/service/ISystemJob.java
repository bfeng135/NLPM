package com.nl.pm.server.service;

import com.nl.pm.server.controller.vo.ResSyncSystemProjectVO;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.DynamicServiceException;
import com.nl.pm.server.service.model.SystemJobModel;

import javax.mail.MessagingException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface ISystemJob {
    void systemJob(Integer jobId) throws Exception;

    List<SystemJobModel> queryAllSystemJob() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    void initSendEmail(SystemJobModel model) throws Exception;

    void sendEmailToAllUser() throws MessagingException, GeneralSecurityException;

    public ResSyncSystemProjectVO syncSystemProjectFromCrmAndSendEmail() throws DynamicServiceException, MessagingException, GeneralSecurityException;
}
