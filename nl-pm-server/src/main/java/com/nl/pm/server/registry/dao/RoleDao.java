package com.nl.pm.server.registry.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nl.pm.server.registry.entity.RoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleDao extends BaseMapper<RoleEntity> {
    List<RoleEntity> queryAll();
    List<RoleEntity> queryAllqueryAllByRoleId(@Param("roleId") List<Integer> roleId);
    RoleEntity queryRoleByRoleId(@Param("roleId")Integer roleId);

    Integer queryCountByAreaId(@Param("areaId") Integer areaId);
}
