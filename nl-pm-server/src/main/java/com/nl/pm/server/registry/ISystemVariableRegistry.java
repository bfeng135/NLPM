package com.nl.pm.server.registry;

import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.entity.SystemVariableEntity;
import com.nl.pm.server.service.model.SystemVariableModel;

import java.lang.reflect.InvocationTargetException;

public interface ISystemVariableRegistry {

    Integer setSystemDailyDeadline(SystemVariableEntity systemVariableEntity);

    SystemVariableModel getNewDataInfo() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
}
