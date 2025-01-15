package com.nl.pm.server.service;

import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.service.model.SystemEmailModel;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author pf
 * @version 1.0
 * @date 2021/8/27 9:54
 */
public interface ISystemEmailService {
    List<SystemEmailModel> findAll() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    Integer querySystemEmailByUserName(String userName);

    void add(List<SystemEmailModel> modelList) throws Exception;

    void del(Integer id);

    void update(SystemEmailModel model) throws Exception;
}
