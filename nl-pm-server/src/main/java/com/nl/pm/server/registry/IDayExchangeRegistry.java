package com.nl.pm.server.registry;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nl.pm.server.registry.entity.DayExchangeAdvanceEntity;
import com.nl.pm.server.registry.entity.UserEntity;
import com.nl.pm.server.registry.param.SearchDayExchangeEntityParam;

import java.util.Date;
import java.util.List;

public interface IDayExchangeRegistry {
    public IPage<DayExchangeAdvanceEntity> searchDayExchangeList(SearchDayExchangeEntityParam param);
    List<DayExchangeAdvanceEntity> searchDetail(Integer userId);

    List<Date> queryDistinctTime(List<Integer> areaId, List<Integer> userId, String startTime, String endTime);
    List<UserEntity> queryDistinctUser(List<Integer> areaId, List<Integer> userId, String startTime, String endTime);

    List<UserEntity> queryDistinctUserByTime(List<Integer> areaId, List<Integer> userId, String startTime, String endTime);


}
