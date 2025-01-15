package com.nl.pm.server.registry;

import com.nl.pm.server.registry.entity.OperationLogEntity;

public interface IOperationLogRegistry {
    void writeToDataBase(OperationLogEntity log);
}
