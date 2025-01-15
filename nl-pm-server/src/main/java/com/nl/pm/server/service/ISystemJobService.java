package com.nl.pm.server.service;

import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.service.model.SystemJobModel;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface ISystemJobService {
    List<SystemJobModel> getSystemJob() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
    void addSystemJob(SystemJobModel model) throws Exception;
    SystemJobModel querySystemJobById(Integer id) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
    void editSystemJob(SystemJobModel model) throws Exception;
    SystemJobModel querySystemJobByType(String type) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
}
