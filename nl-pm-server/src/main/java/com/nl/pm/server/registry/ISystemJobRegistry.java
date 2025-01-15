package com.nl.pm.server.registry;

import com.nl.pm.server.registry.entity.SystemJobEntity;

import java.util.List;

public interface ISystemJobRegistry {
    List<SystemJobEntity> getSystemJob();
    void addSystemJob(SystemJobEntity entity);
    SystemJobEntity querySystemJobById(Integer id);
    void editSystemJob(SystemJobEntity entity);
    SystemJobEntity querySystemJobByType(String type);
}
