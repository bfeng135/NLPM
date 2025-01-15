package com.nl.pm.server.service;


import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.service.model.RoleModel;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface IRoleService {
    /**
     * 查询所有角色
     * @return
     * @throws BaseServiceException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    List<RoleModel> queryAll() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    /**
     * 根据角色Id查询所有角色
     * @param roleId
     * @return
     */
    List<RoleModel> queryAllByRoleId(List<Integer> roleId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    /**
     * 根据角色Id查询角色
     * @param roleId
     * @return
     */
    RoleModel queryRoleByRoleId(Integer roleId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    Integer queryCountByAreaId(Integer areaId);
}
