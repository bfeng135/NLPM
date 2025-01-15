package com.nl.pm.server.registry;

import com.nl.pm.server.registry.entity.DayReportTaskAdvanceEntity;
import com.nl.pm.server.registry.entity.ProjectAdvanceEntity;
import com.nl.pm.server.registry.param.AreaProjectCostEntityParam;
import com.nl.pm.server.registry.param.AreaUsersProjectTimeEntityParam;
import com.nl.pm.server.registry.param.UserTimeEntityParam;

import java.util.Date;
import java.util.List;

public interface IStatisticsRegistry {
    List<AreaUsersProjectTimeEntityParam> queryUserProjectTimeList(Integer areaId, Boolean leaveFlag, Date startDate, Date endDate);

    List<UserTimeEntityParam> queryUserWorkLeaveHours(Integer areaId, Boolean leaveFlag, Date startDate, Date endDate);

    List<ProjectAdvanceEntity> queryOneSelfProjectTimeList(Integer userId, Date startDate, Date endDate);

    List<DayReportTaskAdvanceEntity> queryDayReportDetailListByProjectIdsAndTimes(Integer userId, List<Integer> projectIds, Date startDate, Date endDate);

    List<AreaProjectCostEntityParam> queryAreaProjectCostList(Integer areaId, String costType , Date startDate, Date endDate);
}
