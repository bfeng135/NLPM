package com.nl.pm.server.registry.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nl.pm.server.registry.entity.SystemEmailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author pf
 * @version 1.0
 * @date 2021/8/27 9:51
 */
@Mapper
public interface SystemEmailDao extends BaseMapper<SystemEmailEntity> {
    Integer querySystemEmailByUserName(@Param("userName") String userName);

    SystemEmailEntity queryEmailIsSend();

    void initSystemEmail();
}
