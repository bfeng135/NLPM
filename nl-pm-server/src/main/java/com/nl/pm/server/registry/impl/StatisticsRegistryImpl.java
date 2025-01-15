package com.nl.pm.server.registry.impl;


import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.registry.IStatisticsRegistry;
import com.nl.pm.server.registry.dao.StatisticsDao;
import com.nl.pm.server.registry.entity.DayReportTaskAdvanceEntity;
import com.nl.pm.server.registry.entity.ProjectAdvanceEntity;
import com.nl.pm.server.registry.param.AreaProjectCostDetailEntityParam;
import com.nl.pm.server.registry.param.AreaProjectCostEntityParam;
import com.nl.pm.server.registry.param.AreaUsersProjectTimeEntityParam;
import com.nl.pm.server.registry.param.UserTimeEntityParam;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class StatisticsRegistryImpl implements IStatisticsRegistry {

    @Autowired
    private StatisticsDao statisticsDao;
    @Autowired
    private SecurityContextUtils securityContextUtils;

    @Override
    public List<AreaUsersProjectTimeEntityParam> queryUserProjectTimeList(Integer areaId, Boolean leaveFlag, Date startDate, Date endDate) {
        List<AreaUsersProjectTimeEntityParam> paramsList = statisticsDao.queryUserProjectTimeList(areaId,leaveFlag,startDate,endDate);
        return paramsList;
    }

    @Override
    public List<UserTimeEntityParam> queryUserWorkLeaveHours(Integer areaId, Boolean leaveFlag, Date startDate, Date endDate) {
        List<UserTimeEntityParam> paramsList = statisticsDao.queryUserWorkLeaveHours(areaId,leaveFlag,startDate,endDate);

        return paramsList;
    }

    @Override
    public List<ProjectAdvanceEntity> queryOneSelfProjectTimeList(Integer userId, Date startDate, Date endDate) {
        List<ProjectAdvanceEntity> projectAdvanceEntities = statisticsDao.queryOneSelfProjectTimeList(userId, startDate, endDate);

        return projectAdvanceEntities;
    }

    @Override
    public List<DayReportTaskAdvanceEntity> queryDayReportDetailListByProjectIdsAndTimes(Integer userId,List<Integer> projectIds, Date startDate, Date endDate) {
        List<DayReportTaskAdvanceEntity> dayReportTaskAdvanceEntities = statisticsDao.queryDayReportDetailListByProjectIdsAndTimes(userId, projectIds, startDate, endDate);
        return dayReportTaskAdvanceEntities;
    }

    @Override
    public List<AreaProjectCostEntityParam> queryAreaProjectCostList(Integer areaId, String costType, Date startDate, Date endDate) {
        List<AreaProjectCostEntityParam> areaProjectCostEntityParams = statisticsDao.queryAreaProjectCostList(areaId, costType, startDate, endDate);
        //这一部分涉及到公司默认的那些项目，无法自动分组计算，因为不存在项目字典
//        List<AreaProjectCostEntityParam> groupList = statisticsDao.queryAreaProjectCostGroupList(areaId, costType, startDate, endDate);
//        Map<String,Double> costMap = new HashMap<>();
//        if(CollectionUtils.isNotEmpty(groupList)){
//            for (AreaProjectCostEntityParam param : groupList) {
//                costMap.put(param.getProjectName()+"_"+param.getAreaName(),param.getProjectHours());
//            }
//        }
        if(CollectionUtils.isNotEmpty(areaProjectCostEntityParams)){
            for (AreaProjectCostEntityParam param : areaProjectCostEntityParams) {
                //这一部分涉及到公司默认的那些项目，无法自动分组计算，因为不存在项目字典
//                String id_str = param.getProjectName()+"_"+param.getAreaName();
//                if(costMap.containsKey(id_str)){
//                    param.setProjectHours(costMap.get(id_str));
//                }

                List<AreaProjectCostDetailEntityParam> detailList = param.getDetailList();
                if(CollectionUtils.isNotEmpty(detailList)){
                    Double cost = 0.0;
                    for (AreaProjectCostDetailEntityParam pcd : detailList) {
                        cost = cost + pcd.getHours();
                    }
                    param.setProjectHours(cost);
                }

            }

        }

        return areaProjectCostEntityParams;
    }
}
