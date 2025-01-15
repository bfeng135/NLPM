package com.nl.pm.server.service;


import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.service.model.SystemInfoModel;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface ISystemService {
    /**
     * 查询所有系统邮箱配置
     * @return
     * @throws BaseServiceException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    List<SystemInfoModel> queryAllSystemInfo() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    /**
     * 添加系统邮箱配置
     */
    void addSystemEmail(SystemInfoModel model) throws Exception;

    /**
     * 根据ID查询系统邮箱配置
     */
    SystemInfoModel querySystemInfoById(Integer id) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    /**
     * 修改邮箱配置
     */
    void editSystemEmail(SystemInfoModel model) throws Exception;
}
