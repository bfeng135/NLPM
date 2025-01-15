package com.nl.pm.server.service.impl;


import com.nl.pm.server.common.EntityUtils;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.ISystemRegistry;
import com.nl.pm.server.registry.entity.SystemInfoEntity;
import com.nl.pm.server.service.ISystemService;
import com.nl.pm.server.service.model.SystemInfoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SystemServiceImpl implements ISystemService {
    @Autowired
    private ISystemRegistry systemRegistry;

    @Override
    public List<SystemInfoModel> queryAllSystemInfo() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<SystemInfoModel> modelList = new ArrayList<>();
        List<SystemInfoEntity> systemInfoEntities = systemRegistry.queryAllSystemInfo();
        if(systemInfoEntities.size() > 0){
            for(SystemInfoEntity entity: systemInfoEntities){
                modelList.add((SystemInfoModel) EntityUtils.fillModelWithEntity(entity));
            }
        }
        return modelList;
    }

    @Override
    public void addSystemEmail(SystemInfoModel model) throws Exception {
        SystemInfoEntity entity = (SystemInfoEntity)EntityUtils.convertModelToEntity(model);
        systemRegistry.addSystemEmail(entity);
    }

    @Override
    public SystemInfoModel querySystemInfoById(Integer id) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        SystemInfoEntity entity = systemRegistry.querySystemInfoById(id);
        return (SystemInfoModel) EntityUtils.fillModelWithEntity(entity);
    }

    @Override
    public void editSystemEmail(SystemInfoModel model) throws Exception {
        SystemInfoEntity entity = (SystemInfoEntity)EntityUtils.convertModelToEntity(model);
        systemRegistry.editSystemEmail(entity);
    }
}
