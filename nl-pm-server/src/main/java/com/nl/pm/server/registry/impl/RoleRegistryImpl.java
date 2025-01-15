package com.nl.pm.server.registry.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nl.pm.server.registry.IRoleRegistry;
import com.nl.pm.server.registry.dao.RoleDao;
import com.nl.pm.server.registry.entity.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleRegistryImpl implements IRoleRegistry {

    @Autowired
    private RoleDao roleDao;

    @Override
    public List<RoleEntity> queryAll() {
        return roleDao.queryAll();
    }

    @Override
    public List<RoleEntity> queryAllqueryAllByRoleId(List<Integer> roleId) {
        return roleDao.queryAllqueryAllByRoleId(roleId);
    }

    @Override
    public RoleEntity queryRoleByRoleId(Integer roleId) {
        return roleDao.queryRoleByRoleId(roleId);
    }

    @Override
    public List<RoleEntity> queryRoleByCode(String roleCode) {
        QueryWrapper<RoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code",roleCode);
        return roleDao.selectList(queryWrapper);
    }

    @Override
    public Integer queryCountByAreaId(Integer areaId) {
        return roleDao.queryCountByAreaId(areaId);
    }
}
