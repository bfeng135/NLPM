package com.nl.pm.server.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.common.EntityUtils;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.IDayExchangeRegistry;
import com.nl.pm.server.registry.entity.DayExchangeAdvanceEntity;
import com.nl.pm.server.registry.entity.UserEntity;
import com.nl.pm.server.registry.param.SearchDayExchangeEntityParam;
import com.nl.pm.server.service.IDayExchangeService;
import com.nl.pm.server.service.model.DayExchangeAdvanceModel;
import com.nl.pm.server.service.model.UserModel;
import com.nl.pm.server.service.param.SearchDayExchangeModelParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class DayExchangeServiceImpl implements IDayExchangeService {
    @Autowired
    private IDayExchangeRegistry dayExchangeRegistry;

    @Override
    public BasePagesDomain<DayExchangeAdvanceModel> searchDayExchangeList(SearchDayExchangeModelParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        SearchDayExchangeEntityParam entityParam = new SearchDayExchangeEntityParam();
        entityParam.setAreaId(param.getAreaId());
        entityParam.setUserId(param.getUserId());
        entityParam.setNickname(param.getNickname());
        entityParam.setCurrentPage(param.getCurrentPage());
        entityParam.setPageSize(param.getPageSize());
        entityParam.setSearchVal(param.getSearchVal());

        IPage<DayExchangeAdvanceEntity> entityIPage = dayExchangeRegistry.searchDayExchangeList(entityParam);
        List<DayExchangeAdvanceModel> modelList = new ArrayList<>();
        List<DayExchangeAdvanceEntity> entityRecords = entityIPage.getRecords();
        if(!CollectionUtils.isEmpty(entityRecords)) {
            for (DayExchangeAdvanceEntity record : entityRecords) {
                DayExchangeAdvanceModel model = (DayExchangeAdvanceModel)EntityUtils.fillModelWithEntity(record);
                modelList.add(model);
            }
        }
        IPage<DayExchangeAdvanceModel> modelIPage = new Page<>();
        modelIPage.setRecords(modelList);
        modelIPage.setPages(entityIPage.getPages());
        modelIPage.setTotal(entityIPage.getTotal());
        modelIPage.setSize(entityIPage.getSize());
        modelIPage.setTotal(entityIPage.getTotal());
        BasePagesDomain<DayExchangeAdvanceModel> dayExchangeAdvanceModelBasePagesDomain = new BasePagesDomain<>(modelIPage);
        return dayExchangeAdvanceModelBasePagesDomain;
    }

    @Override
    public List<DayExchangeAdvanceModel> searchDetail(Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<DayExchangeAdvanceModel> modelList = new ArrayList<>();
        List<DayExchangeAdvanceEntity> dayExchangeEntities = dayExchangeRegistry.searchDetail(userId);
        if(!CollectionUtils.isEmpty(dayExchangeEntities)){
            for (DayExchangeAdvanceEntity dayExchangeEntity : dayExchangeEntities) {
                DayExchangeAdvanceModel model = (DayExchangeAdvanceModel) EntityUtils.fillModelWithEntity(dayExchangeEntity);
                modelList.add(model);
            }

        }
        return modelList;
    }

    @Override
    public List<Date> queryDistinctTime(List<Integer> areaId, List<Integer> userId, String startTime, String endTime) {
        if(areaId != null){
            areaId = areaId.parallelStream().filter(Objects::nonNull) .collect(Collectors.toList());
        }
        if(userId != null){
            userId = userId.parallelStream().filter(Objects::nonNull) .collect(Collectors.toList());
        }
        return dayExchangeRegistry.queryDistinctTime(areaId,userId,startTime,endTime);
    }

    @Override
    public List<UserModel> queryDistinctUser(List<Integer> areaId, List<Integer> userId, String startTime, String endTime) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(areaId != null){
            areaId = areaId.parallelStream().filter(Objects::nonNull) .collect(Collectors.toList());
        }
        if(userId != null){
            userId = userId.parallelStream().filter(Objects::nonNull) .collect(Collectors.toList());
        }
        List<UserModel> list = new ArrayList<>();
        List<UserEntity> userEntities = dayExchangeRegistry.queryDistinctUser(areaId,userId,startTime,endTime);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userEntities)){
            for(UserEntity entity:userEntities){
                list.add((UserModel)EntityUtils.fillModelWithEntity(entity));
            }
        }
        return list;
    }

    @Override
    public List<UserModel> queryDistinctUserByTime(List<Integer> areaId, List<Integer> userId, String startTime, String endTime) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(areaId != null){
            areaId = areaId.parallelStream().filter(Objects::nonNull) .collect(Collectors.toList());
        }
        if(userId != null){
            userId = userId.parallelStream().filter(Objects::nonNull) .collect(Collectors.toList());
        }
        List<UserModel> list = new ArrayList<>();
        List<UserEntity> userEntities = dayExchangeRegistry.queryDistinctUserByTime(areaId,userId,startTime,endTime);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userEntities)){
            for(UserEntity entity:userEntities){
                list.add((UserModel)EntityUtils.fillModelWithEntity(entity));
            }
        }
        return list;
    }
}
