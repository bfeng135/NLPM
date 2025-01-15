package com.nl.pm.server.service;

import com.nl.pm.server.controller.vo.ReqAddSystemVariableVO;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.service.model.SystemVariableModel;

import java.lang.reflect.InvocationTargetException;

public interface ISystemVariableService {
    Integer setSystemDailyDeadline(SystemVariableModel systemVariableModel) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    SystemVariableModel getNewDataInfo() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

}
