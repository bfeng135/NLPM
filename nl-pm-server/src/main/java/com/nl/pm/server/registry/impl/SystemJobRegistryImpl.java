package com.nl.pm.server.registry.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nl.pm.server.registry.ISystemJobRegistry;
import com.nl.pm.server.registry.dao.SystemJobDao;
import com.nl.pm.server.registry.entity.SystemJobEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SystemJobRegistryImpl implements ISystemJobRegistry {
    @Autowired
    private SystemJobDao systemJobDao;

    @Override
    public List<SystemJobEntity> getSystemJob() {
        QueryWrapper<SystemJobEntity> queryWrapper = new QueryWrapper<>();
        return systemJobDao.selectList(queryWrapper);
    }

    @Override
    public void addSystemJob(SystemJobEntity entity) {
        systemJobDao.insert(entity);
    }

    @Override
    public SystemJobEntity querySystemJobById(Integer id) {
        return systemJobDao.selectById(id);
    }

    @Override
    public void editSystemJob(SystemJobEntity entity) {
        systemJobDao.updateById(entity);
    }

    @Override
    public SystemJobEntity querySystemJobByType(String type) {
        QueryWrapper<SystemJobEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type",type);
        return systemJobDao.selectOne(queryWrapper);
    }
}
