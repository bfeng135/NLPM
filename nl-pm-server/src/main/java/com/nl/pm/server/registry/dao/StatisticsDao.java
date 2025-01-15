package com.nl.pm.server.registry.dao;

import com.nl.pm.server.registry.entity.DayReportTaskAdvanceEntity;
import com.nl.pm.server.registry.entity.ProjectAdvanceEntity;
import com.nl.pm.server.registry.param.AreaProjectCostEntityParam;
import com.nl.pm.server.registry.param.AreaUsersProjectTimeEntityParam;
import com.nl.pm.server.registry.param.UserTimeEntityParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface StatisticsDao {
    List<AreaUsersProjectTimeEntityParam> queryUserProjectTimeList(@Param("areaId") Integer areaId, @Param("leaveFlag")Boolean leaveFlag, @Param("startDate")Date startDate, @Param("endDate")Date endDate);
    List<UserTimeEntityParam> queryUserWorkLeaveHours(@Param("areaId") Integer areaId, @Param("leaveFlag")Boolean leaveFlag, @Param("startDate")Date startDate, @Param("endDate")Date endDate);

    List<ProjectAdvanceEntity> queryOneSelfProjectTimeList(@Param("userId")Integer userId, @Param("startDate")Date startDate, @Param("endDate")Date endDate);

    List<DayReportTaskAdvanceEntity> queryDayReportDetailListByProjectIdsAndTimes(@Param("userId")Integer userId, @Param("projectIds")List<Integer> projectIds, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    List<AreaProjectCostEntityParam> queryAreaProjectCostList(@Param("areaId")Integer areaId,@Param("costType")String costType ,@Param("startDate")Date startDate, @Param("endDate")Date endDate);
    List<AreaProjectCostEntityParam> queryAreaProjectCostGroupList(@Param("areaId")Integer areaId,@Param("costType")String costType ,@Param("startDate")Date startDate, @Param("endDate")Date endDate);
}
