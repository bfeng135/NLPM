package com.nl.pm.server.registry.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.registry.entity.DayExchangeAdvanceEntity;
import com.nl.pm.server.registry.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface DayExchangeDao {

    public IPage<DayExchangeAdvanceEntity> searchDayExchangeList(Page<DayExchangeAdvanceEntity> page , @Param("areaId") Integer areaId, @Param("userId")Integer userId, @Param("nickname")String nickname);
    List<DayExchangeAdvanceEntity> searchDetail(@Param("userId") Integer userId);
    List<Date> queryDistinctTime(@Param("areaId") List<Integer> areaId,@Param("userId") List<Integer> userId,
                                 @Param("startTime")String startTime,@Param("endTime") String endTime);

    List<UserEntity> queryDistinctUser(@Param("areaId") List<Integer> areaId,@Param("userId") List<Integer> userId,
                                       @Param("startTime")String startTime,@Param("endTime") String endTime);

    List<UserEntity> queryDistinctUserByTime(@Param("areaId") List<Integer> areaId,@Param("userId") List<Integer> userId,
                                       @Param("startTime")String startTime,@Param("endTime") String endTime);
}
