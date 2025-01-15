package com.nl.pm.server.service;


import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.param.AreaProjectCostEntityParam;
import com.nl.pm.server.service.model.DayReportTaskAdvanceModel;
import com.nl.pm.server.service.model.ProjectAdvanceModel;
import com.nl.pm.server.service.param.AreaUsersProjectTimeModelParam;
import com.nl.pm.server.service.param.UserTimeModelParam;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

public interface IStatisticsService {

    List<AreaUsersProjectTimeModelParam> queryUserProjectTimeList(Integer areaId, Boolean leaveFlag, Date startDate, Date endDate);
    List<UserTimeModelParam> queryUserWorkLeaveHours(Integer areaId, Boolean leaveFlag, Date startDate, Date endDate);
    List<ProjectAdvanceModel> queryOneSelfProjectTimeList(Integer userId, Date startDate, Date endDate) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
    List<DayReportTaskAdvanceModel> queryDayReportDetailListByProjectIdsAndTimes(List<Integer> projectIds, Date startDate, Date endDate) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
    List<AreaProjectCostEntityParam> queryAreaProjectCostList(Integer areaId, String costType , Date startDate, Date endDate);
}
