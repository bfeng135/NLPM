package com.nl.pm.server.registry.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nl.pm.server.registry.entity.SystemInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SystemDao extends BaseMapper<SystemInfoEntity> {
    /**
     * 查询一个邮箱
     */
    SystemInfoEntity querySystemInfo();

    /**
     * 查询所有邮箱配置
     * @return
     */
    List<SystemInfoEntity>  selectAllSystemInfo();
}
