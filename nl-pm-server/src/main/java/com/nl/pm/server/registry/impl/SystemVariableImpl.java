package com.nl.pm.server.registry.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nl.pm.server.common.EntityUtils;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.ISystemVariableRegistry;
import com.nl.pm.server.registry.dao.SystemVariableDao;
import com.nl.pm.server.registry.entity.SystemVariableEntity;
import com.nl.pm.server.service.model.SystemVariableModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SystemVariableImpl implements ISystemVariableRegistry {

    @Autowired
    private SystemVariableDao systemVariableDao;

    @Override
    public Integer setSystemDailyDeadline(SystemVariableEntity systemVariableEntity) {
        systemVariableEntity.setCreateTime(new Date());
        return systemVariableDao.insert(systemVariableEntity);
    }

    @Override
    public SystemVariableModel getNewDataInfo() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        SystemVariableEntity systemVariableEntity = systemVariableDao.selectOne(new LambdaQueryWrapper<SystemVariableEntity>()
                .orderByDesc(SystemVariableEntity::getCreateTime).last("limit 1"));
        return (SystemVariableModel)EntityUtils.fillModelWithEntity(systemVariableEntity);
    }
}
