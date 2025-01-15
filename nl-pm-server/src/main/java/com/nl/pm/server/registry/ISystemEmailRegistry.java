package com.nl.pm.server.registry;

import com.nl.pm.server.registry.entity.SystemEmailEntity;

import java.util.List;

/**
 * @author pf
 * @version 1.0
 * @date 2021/8/27 9:53
 */
public interface ISystemEmailRegistry {
    List<SystemEmailEntity> findAll();

    Integer querySystemEmailByUserName(String userName);

    void add(List<SystemEmailEntity> emailEntities);

    void del(Integer id);

    void update(SystemEmailEntity entity);

    SystemEmailEntity queryEmailIsSend();

    void initSystemEmail();
}
