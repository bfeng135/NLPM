package com.nl.pm.server.service.impl;


import com.nl.pm.server.common.EntityUtils;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.IStatisticsRegistry;
import com.nl.pm.server.registry.entity.DayReportTaskAdvanceEntity;
import com.nl.pm.server.registry.entity.ProjectAdvanceEntity;
import com.nl.pm.server.registry.param.AreaProjectCostEntityParam;
import com.nl.pm.server.registry.param.AreaUsersProjectTimeEntityParam;
import com.nl.pm.server.registry.param.UserTimeEntityParam;
import com.nl.pm.server.service.IStatisticsService;
import com.nl.pm.server.service.model.DayReportTaskAdvanceModel;
import com.nl.pm.server.service.model.ProjectAdvanceModel;
import com.nl.pm.server.service.param.AreaUsersProjectTimeModelParam;
import com.nl.pm.server.service.param.UserTimeModelParam;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class StatisticsServiceImpl implements IStatisticsService {
    @Autowired
    private SecurityContextUtils securityContextUtils;

    @Autowired
    private IStatisticsRegistry iStatisticsRegistry;

    @Override
    public List<AreaUsersProjectTimeModelParam> queryUserProjectTimeList(Integer areaId, Boolean leaveFlag, Date startDate, Date endDate) {
        List<AreaUsersProjectTimeEntityParam> params = iStatisticsRegistry.queryUserProjectTimeList(areaId,leaveFlag,startDate,endDate);
        List<AreaUsersProjectTimeModelParam> modelList = new ArrayList<>();
        if(params!=null && params.size()>0){
            for (AreaUsersProjectTimeEntityParam param : params) {
                AreaUsersProjectTimeModelParam model = new AreaUsersProjectTimeModelParam();
                model.setUserId(param.getUserId());
                model.setNickname(param.getNickname());
                model.setAreaId(param.getAreaId());
                model.setAreaName(param.getAreaName());
                model.setProjectTimeList(param.getProjectTimeList());
                modelList.add(model);
            }

        }
        return modelList;
    }

    @Override
    public List<UserTimeModelParam> queryUserWorkLeaveHours(Integer areaId, Boolean leaveFlag, Date startDate, Date endDate) {
        List<UserTimeEntityParam> params = iStatisticsRegistry.queryUserWorkLeaveHours(areaId,leaveFlag,startDate,endDate);
        List<UserTimeModelParam> modelList = new ArrayList<>();
        if(params!=null && params.size()>0){
            for (UserTimeEntityParam param : params) {
                UserTimeModelParam model = new UserTimeModelParam();
                model.setUserId(param.getUserId());
                model.setNickname(param.getNickname());
                model.setAreaId(param.getAreaId());
                model.setAreaName(param.getAreaName());
                model.setHours(param.getHours());
                model.setLeaveHours(param.getLeaveHours());
                modelList.add(model);
            }

        }
        return modelList;
    }

    @Override
    public List<ProjectAdvanceModel> queryOneSelfProjectTimeList(Integer userId, Date startDate, Date endDate) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<ProjectAdvanceEntity> projectAdvanceEntities = iStatisticsRegistry.queryOneSelfProjectTimeList(userId, startDate, endDate);
        List<ProjectAdvanceModel> modelList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(projectAdvanceEntities)){
            for (ProjectAdvanceEntity projectAdvanceEntity : projectAdvanceEntities) {
                ProjectAdvanceModel model =(ProjectAdvanceModel) EntityUtils.fillModelWithEntity(projectAdvanceEntity);
                modelList.add(model);
            }

        }

        return modelList;
    }

    @Override
    public List<DayReportTaskAdvanceModel> queryDayReportDetailListByProjectIdsAndTimes(List<Integer> projectIds, Date startDate, Date endDate) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Integer currentUserId = securityContextUtils.getCurrentUserId();
        List<DayReportTaskAdvanceEntity> dayReportTaskAdvanceEntities = iStatisticsRegistry.queryDayReportDetailListByProjectIdsAndTimes(currentUserId, projectIds, startDate, endDate);
        List<DayReportTaskAdvanceModel> modelList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(dayReportTaskAdvanceEntities)){
            for (DayReportTaskAdvanceEntity dayReportTaskAdvanceEntity : dayReportTaskAdvanceEntities) {
                DayReportTaskAdvanceModel model =(DayReportTaskAdvanceModel) EntityUtils.fillModelWithEntity(dayReportTaskAdvanceEntity);
                modelList.add(model);
            }
        }
        return modelList;
    }

    @Override
    public List<AreaProjectCostEntityParam> queryAreaProjectCostList(Integer areaId, String costType, Date startDate, Date endDate) {
        List<AreaProjectCostEntityParam> areaProjectCostEntityParams = iStatisticsRegistry.queryAreaProjectCostList(areaId, costType, startDate, endDate);
        return areaProjectCostEntityParams;
    }
}
