package com.nl.pm.server.registry.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nl.pm.server.registry.entity.SystemJobEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SystemJobDao extends BaseMapper<SystemJobEntity> {
}
