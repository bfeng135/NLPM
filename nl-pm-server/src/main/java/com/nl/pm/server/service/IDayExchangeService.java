package com.nl.pm.server.service;


import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.service.model.DayExchangeAdvanceModel;
import com.nl.pm.server.service.model.UserModel;
import com.nl.pm.server.service.param.SearchDayExchangeModelParam;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

public interface IDayExchangeService {

    public BasePagesDomain<DayExchangeAdvanceModel> searchDayExchangeList(SearchDayExchangeModelParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
    List<DayExchangeAdvanceModel> searchDetail(Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    List<Date> queryDistinctTime(List<Integer> areaId,List<Integer> userId,String startTime,String endTime);

    List<UserModel> queryDistinctUser (List<Integer> areaId,List<Integer> userId,String startTime,String endTime) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    List<UserModel> queryDistinctUserByTime (List<Integer> areaId,List<Integer> userId,String startTime,String endTime) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

}
