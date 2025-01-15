package com.nl.pm.server.service.impl;

import com.nl.pm.server.common.EntityUtils;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.ISystemJobRegistry;
import com.nl.pm.server.registry.entity.SystemJobEntity;
import com.nl.pm.server.service.ISystemJobService;
import com.nl.pm.server.service.model.SystemJobModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SystemJobServiceImpl implements ISystemJobService {
    @Autowired
    private ISystemJobRegistry systemJobRegistry;

    @Override
    public List<SystemJobModel> getSystemJob() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<SystemJobModel> list = new ArrayList<>();
        List<SystemJobEntity> systemJob = systemJobRegistry.getSystemJob();
        if(systemJob.size() > 0){
            for(SystemJobEntity e:systemJob){
                list.add((SystemJobModel)EntityUtils.fillModelWithEntity(e));
            }
        }
        return list;
    }

    @Override
    public void addSystemJob(SystemJobModel model) throws Exception {
        SystemJobEntity entity = (SystemJobEntity)EntityUtils.convertModelToEntity(model);
        systemJobRegistry.addSystemJob(entity);
    }

    @Override
    public SystemJobModel querySystemJobById(Integer id) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        SystemJobEntity entity = systemJobRegistry.querySystemJobById(id);
        if(entity != null){
           return (SystemJobModel)EntityUtils.fillModelWithEntity(entity);
        }
        return null;
    }

    @Override
    public void editSystemJob(SystemJobModel model) throws Exception {
        SystemJobEntity entity = (SystemJobEntity)EntityUtils.convertModelToEntity(model);
        systemJobRegistry.editSystemJob(entity);
    }

    @Override
    public SystemJobModel querySystemJobByType(String type) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        SystemJobEntity entity = systemJobRegistry.querySystemJobByType(type);
        if(entity != null){
            return (SystemJobModel)EntityUtils.fillModelWithEntity(entity);
        }
        return null;
    }
}
