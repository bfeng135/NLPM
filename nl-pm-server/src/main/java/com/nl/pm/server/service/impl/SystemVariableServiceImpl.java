package com.nl.pm.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.nl.pm.server.common.EntityUtils;
import com.nl.pm.server.controller.vo.ReqAddSystemVariableVO;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.ISystemVariableRegistry;
import com.nl.pm.server.registry.entity.SystemVariableEntity;
import com.nl.pm.server.service.IRoleService;
import com.nl.pm.server.service.ISystemVariableService;
import com.nl.pm.server.service.model.RoleModel;
import com.nl.pm.server.service.model.SystemVariableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Service
@Transactional
public class SystemVariableServiceImpl implements ISystemVariableService {

    @Autowired
    private ISystemVariableRegistry iSystemVariableRegistry;


    @Override
    public Integer setSystemDailyDeadline(SystemVariableModel systemVariableModel) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        SystemVariableEntity systemVariableEntity = null;
        try {
            systemVariableEntity= (SystemVariableEntity) EntityUtils.convertModelToEntity(systemVariableModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iSystemVariableRegistry.setSystemDailyDeadline(systemVariableEntity);

    }

    @Override
    public SystemVariableModel getNewDataInfo() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        return iSystemVariableRegistry.getNewDataInfo();
    }
}
