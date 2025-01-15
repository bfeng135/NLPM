package com.nl.pm.server.registry.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nl.pm.server.registry.ISystemRegistry;
import com.nl.pm.server.registry.dao.SystemDao;
import com.nl.pm.server.registry.entity.SystemInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SystemRegistryImpl implements ISystemRegistry {
    @Autowired
    private SystemDao systemDao;

    @Override
    public SystemInfoEntity querySystemInfo() {
        return systemDao.querySystemInfo();
    }

    @Override
    public List<SystemInfoEntity> queryAllSystemInfo() {
        return systemDao.selectAllSystemInfo();
    }

    @Override
    public void addSystemEmail(SystemInfoEntity entity) {
        systemDao.insert(entity);
    }

    @Override
    public SystemInfoEntity querySystemInfoById(Integer id) {
        return systemDao.selectById(id);
    }

    @Override
    public void editSystemEmail(SystemInfoEntity entity) {
        systemDao.updateById(entity);
    }
}
