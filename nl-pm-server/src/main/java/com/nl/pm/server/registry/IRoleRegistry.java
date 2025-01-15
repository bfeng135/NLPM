package com.nl.pm.server.registry;

import com.nl.pm.server.registry.entity.RoleEntity;

import java.util.List;

public interface IRoleRegistry {
    /**
     * 查询所有角色
     * @return
     */
    List<RoleEntity> queryAll();

    /**
     * 根据角色Id查询角色
     * @param roleId
     * @return
     */
    List<RoleEntity> queryAllqueryAllByRoleId(List<Integer> roleId);

    /**
     * 根据角色Id查询角色
     */
    RoleEntity queryRoleByRoleId(Integer roleId);

    /**
     * 根据roleCode查询角色
     */
    List<RoleEntity> queryRoleByCode(String roleCode);

    Integer queryCountByAreaId(Integer areaId);
}
