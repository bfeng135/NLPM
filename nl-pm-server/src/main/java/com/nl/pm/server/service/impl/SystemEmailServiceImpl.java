package com.nl.pm.server.service.impl;

import com.nl.pm.server.common.EntityUtils;
import com.nl.pm.server.exception.BaseServiceException;
import org.apache.commons.collections.CollectionUtils;
import com.nl.pm.server.registry.ISystemEmailRegistry;
import com.nl.pm.server.registry.entity.SystemEmailEntity;
import com.nl.pm.server.service.ISystemEmailService;
import com.nl.pm.server.service.model.SystemEmailModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pf
 * @version 1.0
 * @date 2021/8/27 9:54
 */
@Service
public class SystemEmailServiceImpl implements ISystemEmailService {
    @Autowired
    private ISystemEmailRegistry systemEmailRegistry;
    @Override
    public List<SystemEmailModel> findAll() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<SystemEmailModel> list = new ArrayList<>();
        List<SystemEmailEntity> all = systemEmailRegistry.findAll();
        if(CollectionUtils.isNotEmpty(all)){
            for(SystemEmailEntity entity:all){
                list.add((SystemEmailModel)EntityUtils.fillModelWithEntity(entity));
            }
        }
        return list;
    }

    @Override
    public Integer querySystemEmailByUserName(String userName) {
        return systemEmailRegistry.querySystemEmailByUserName(userName);
    }

    @Override
    public void add(List<SystemEmailModel> modelList) throws Exception {
        List<SystemEmailEntity> emailEntities = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(modelList)){
            for(SystemEmailModel model:modelList){
                emailEntities.add((SystemEmailEntity)EntityUtils.convertModelToEntity(model));
            }
        }
        systemEmailRegistry.add(emailEntities);
    }

    @Override
    public void del(Integer id) {
        systemEmailRegistry.del(id);
    }

    @Override
    public void update(SystemEmailModel model) throws Exception {
        SystemEmailEntity entity = (SystemEmailEntity) EntityUtils.convertModelToEntity(model);
        systemEmailRegistry.update(entity);
    }
}
