package com.nl.pm.server.registry.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nl.pm.server.registry.ISystemEmailRegistry;
import com.nl.pm.server.registry.dao.SystemEmailDao;
import com.nl.pm.server.registry.entity.SystemEmailEntity;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author pf
 * @version 1.0
 * @date 2021/8/27 9:53
 */
@Repository
public class SystemEmailRegistryImpl implements ISystemEmailRegistry {
    @Autowired
    private SystemEmailDao systemEmailDao;
    @Override
    public List<SystemEmailEntity> findAll() {
        QueryWrapper<SystemEmailEntity> queryWrapper = new QueryWrapper<>();
        return systemEmailDao.selectList(queryWrapper);
    }

    @Override
    public Integer querySystemEmailByUserName(String userName) {
        return systemEmailDao.querySystemEmailByUserName(userName);
    }

    @Override
    public void add(List<SystemEmailEntity> emailEntities) {
        if(CollectionUtils.isNotEmpty(emailEntities)){
            for(SystemEmailEntity entity:emailEntities){
                systemEmailDao.insert(entity);
            }
        }
    }

    @Override
    public void del(Integer id) {
        systemEmailDao.deleteById(id);
    }

    @Override
    public void update(SystemEmailEntity entity) {
        systemEmailDao.updateById(entity);
    }

    @Override
    public SystemEmailEntity queryEmailIsSend() {
        return systemEmailDao.queryEmailIsSend();
    }

    @Override
    public void initSystemEmail() {
        systemEmailDao.initSystemEmail();
    }
}
