package com.nl.pm.server.service.impl;


import com.nl.pm.server.common.EntityUtils;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.IRoleRegistry;
import com.nl.pm.server.registry.entity.RoleEntity;
import com.nl.pm.server.service.IRoleService;
import com.nl.pm.server.service.model.RoleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private IRoleRegistry registry;

    @Override
    public List<RoleModel> queryAll() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<RoleModel> roleModels = new ArrayList<>();
        List<RoleEntity> roleEntities = registry.queryAll();
        if (roleEntities.size() > 0){
            for (RoleEntity roleEntity:roleEntities){
                roleModels.add((RoleModel)EntityUtils.fillModelWithEntity(roleEntity));
            }
        }
        return roleModels;
    }

    @Override
    public List<RoleModel> queryAllByRoleId(List<Integer> roleId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<RoleModel> roleModels = new ArrayList<>();
        List<RoleEntity> roleEntities = registry.queryAllqueryAllByRoleId(roleId);
        if (roleEntities.size() > 0){
            for (RoleEntity roleEntity:roleEntities){
                roleModels.add((RoleModel)EntityUtils.fillModelWithEntity(roleEntity));
            }
        }
        return roleModels;
    }

    @Override
    public RoleModel queryRoleByRoleId(Integer roleId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        RoleEntity roleEntity = registry.queryRoleByRoleId(roleId);
        return (RoleModel)EntityUtils.fillModelWithEntity(roleEntity);
    }

    @Override
    public Integer queryCountByAreaId(Integer areaId) {
        return registry.queryCountByAreaId(areaId);
    }
}
