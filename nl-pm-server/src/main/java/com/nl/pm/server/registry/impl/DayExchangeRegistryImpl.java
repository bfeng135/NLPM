package com.nl.pm.server.registry.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.registry.IDayExchangeRegistry;
import com.nl.pm.server.registry.dao.DayExchangeDao;
import com.nl.pm.server.registry.entity.DayExchangeAdvanceEntity;
import com.nl.pm.server.registry.entity.UserEntity;
import com.nl.pm.server.registry.param.SearchDayExchangeEntityParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class DayExchangeRegistryImpl implements IDayExchangeRegistry {

    @Autowired
    private DayExchangeDao dayExchangeDao;

    @Override
    public IPage<DayExchangeAdvanceEntity> searchDayExchangeList(SearchDayExchangeEntityParam param) {
        Page<DayExchangeAdvanceEntity> iPage = new Page<DayExchangeAdvanceEntity>();
        iPage.setCurrent(param.getCurrentPage());
        iPage.setSize(param.getPageSize());
        IPage<DayExchangeAdvanceEntity> entityIPage = dayExchangeDao.searchDayExchangeList(iPage,param.getAreaId(),param.getUserId(),param.getNickname());

        return entityIPage;
    }

    @Override
    public List<DayExchangeAdvanceEntity> searchDetail(Integer userId) {
        return dayExchangeDao.searchDetail(userId);
    }

    @Override
    public List<Date> queryDistinctTime(List<Integer> areaId, List<Integer> userId, String startTime, String endTime) {
        return dayExchangeDao.queryDistinctTime(areaId,userId,startTime,endTime);
    }

    @Override
    public List<UserEntity> queryDistinctUser(List<Integer> areaId, List<Integer> userId, String startTime, String endTime) {
        return dayExchangeDao.queryDistinctUser(areaId,userId,startTime,endTime);
    }

    @Override
    public List<UserEntity> queryDistinctUserByTime(List<Integer> areaId, List<Integer> userId, String startTime, String endTime) {
        return dayExchangeDao.queryDistinctUserByTime(areaId,userId,startTime,endTime);
    }
}
