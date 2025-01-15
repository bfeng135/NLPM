package com.nl.pm.server.registry;

import com.nl.pm.server.registry.entity.SystemInfoEntity;

import java.util.List;

public interface ISystemRegistry {
    /**
     * 查询一个邮箱
     */
    SystemInfoEntity querySystemInfo();

    /**
     * 查询所有邮箱配置
     * @return
     */
    List<SystemInfoEntity> queryAllSystemInfo();

    /**
     * 添加邮箱配置
     */
    void addSystemEmail(SystemInfoEntity entity);

    /**
     * 根据ID查询系统邮箱配置
     */
    SystemInfoEntity querySystemInfoById(Integer id);

    /**
     * 修改邮件配置
     * @param entity
     */
    void editSystemEmail(SystemInfoEntity entity);
}
