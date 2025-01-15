package com.nl.pm.server.registry.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.registry.IDayReportRegistry;
import com.nl.pm.server.registry.dao.DayReportDao;
import com.nl.pm.server.registry.entity.*;
import com.nl.pm.server.registry.param.DayReportSearchEntityParam;
import com.nl.pm.server.registry.param.ResEveryWorkTotalTimeEntityParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Repository
public class DayReportRegistryImpl implements IDayReportRegistry {
    @Autowired
    private DayReportDao reportDao;

    @Autowired
    private SecurityContextUtils securityContextUtils;

    @Override
    public List<Date> queryReportByProjectIdAndAreaId(String startTime, String endTime, Integer projectId, Integer areaId) {
        return reportDao.queryReportByProjectIdAndAreaId(startTime, endTime, projectId, areaId);
    }

    @Override
    public List<Map<UserEntity, Double>> queryReportTimeByTimeStrTest(String timeStr, Integer projectId, Integer areaId) {
        return reportDao.queryReportTimeByTimeStrTest(timeStr, projectId, areaId);
    }

    @Override
    public List<UserEntity> queryReportTimeByTimeStr(String timeStr, Integer projectId, Integer areaId) {
        return reportDao.queryReportTimeByTimeStr(timeStr, projectId, areaId);
    }

    @Override
    @Transactional
    public void createDayReport(DayReportAdvanceEntity entity) throws BaseServiceException {
        reportDao.createDayReport(entity);
        List<DayReportTaskEntity> dayReportList = entity.getDayReportList();
        if (dayReportList != null && dayReportList.size() > 0) {
            reportDao.createDayReportTask(entity);
        }
        DayExchangeEntity leaveEntity = entity.getLeaveEntity();
        if (leaveEntity != null && leaveEntity.getLeaveHour() != null && leaveEntity.getLeaveHour() > 0.0) {
            reportDao.createDayReportExchange(entity);
        }

    }

    @Override
    @Transactional
    public void updateDayReport(DayReportAdvanceEntity entity) throws BaseServiceException {
        reportDao.updateDayReport(entity);
        List<DayReportTaskEntity> dayReportList = entity.getDayReportList();
        Set<Integer> checkProjectSet = new HashSet<>();
        if (dayReportList != null && dayReportList.size() > 0) {
            for (DayReportTaskEntity dayReportTaskEntity : dayReportList) {
                Integer projectId = dayReportTaskEntity.getProjectId();
                if (checkProjectSet.contains(projectId)) {
                    throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_PROJECT_EXIST_ERROR);
                } else {
                    checkProjectSet.add(projectId);
                }
            }
        }
        reportDao.createDayReportTask(entity);
        DayExchangeEntity leaveEntity = entity.getLeaveEntity();
        if (leaveEntity != null && leaveEntity.getLeaveHour() != null && leaveEntity.getLeaveHour() > 0.0) {
            reportDao.createDayReportExchange(entity);
        }
        if (leaveEntity != null && leaveEntity.getLeaveHour() == 0.0) {
            entity.setLeaveEntity(null);
            reportDao.createDayReportExchange(entity);
        }
    }

    @Override
    public DayReportAdvanceEntity fetchDayReportDetail(Integer id) throws Exception {
        DayReportEntity dayReportEntity = reportDao.fetchDayReport(id);
        DayExchangeEntity dayExchangeEntity = reportDao.fetchDayReportExchange(id);
        List<DayReportTaskEntity> dayReportTaskEntities = reportDao.fetchDayReportTask(id);
        DayReportAdvanceEntity entity = new DayReportAdvanceEntity();
        entity.setDayReportList(dayReportTaskEntities);
        entity.setLeaveEntity(dayExchangeEntity);
        entity.setId(dayReportEntity.getId());
        entity.setDate(dayReportEntity.getDate());
        entity.setUserId(dayReportEntity.getUserId());
        entity.setNickname(dayReportEntity.getNickname());
        entity.setCreateTime(dayReportEntity.getCreateTime());
        entity.setUpdateTime(dayReportEntity.getUpdateTime());
        return entity;
    }

    @Override
    public DayReportEntity checkDayReportExist(String dateStr, Integer userId) {
        DayReportEntity dayReportEntity = reportDao.checkDayReportExist(dateStr, userId);
        return dayReportEntity;
    }

    @Override
    public IPage<DayReportAdvanceEntity> searchDayReport(DayReportSearchEntityParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Page<DayReportEntity> page = new Page<>();
        page.setCurrent(param.getCurrentPage());
        page.setSize(param.getPageSize());
        param.setCurrentUserId(securityContextUtils.getCurrentUserId());
        param.setCurrentUserRoleCode(securityContextUtils.getCurrentUserRole().getCode());
        IPage<DayReportEntity> dayReportEntityIPage = reportDao.searchDayReport(page, param);
        Map<Integer, DayReportEntity> reportMap = new HashMap<>();
        List<Integer> recordIds = new ArrayList<>();
        List<DayReportEntity> records = dayReportEntityIPage.getRecords();
        for (DayReportEntity record : records) {
            reportMap.put(record.getId(), record);
            recordIds.add(record.getId());
        }

        Set<Integer> limitProjectSet = new HashSet<>();
        List<Integer> limitProject = param.getLimitProject();
        if (!CollectionUtils.isEmpty(limitProject)) {
            for (Integer integer : limitProject) {
                limitProjectSet.add(integer);
            }
        }

        Map<Integer, List<DayReportTaskEntity>> reportAssTaskMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(recordIds)) {
            List<DayReportTaskEntity> dayReportTaskEntities = reportDao.searchDayReportTask(recordIds,securityContextUtils.getCurrentUserId(),securityContextUtils.getCurrentUserRole().getCode());
            for (DayReportTaskEntity dayReportTaskEntity : dayReportTaskEntities) {
                Integer dayReportId = dayReportTaskEntity.getDayReportId();
                DayReportEntity dayReportEntity = reportMap.get(dayReportId);
                Integer projectId = dayReportTaskEntity.getProjectId();

                if (limitProjectSet.contains(projectId) || dayReportEntity.getUserId().equals(securityContextUtils.getCurrentUserId())) {
                    if (reportAssTaskMap.containsKey(dayReportId)) {
                        List<DayReportTaskEntity> taskList = reportAssTaskMap.get(dayReportId);
                        taskList.add(dayReportTaskEntity);
                        reportAssTaskMap.put(dayReportId, taskList);
                    } else {
                        List<DayReportTaskEntity> taskList = new ArrayList<>();
                        taskList.add(dayReportTaskEntity);
                        reportAssTaskMap.put(dayReportId, taskList);
                    }
                }
            }
        }

        Map<Integer, DayExchangeEntity> exchangeMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(recordIds)) {
            List<DayExchangeEntity> dayExchangeEntities = reportDao.searchDayReportExchange(recordIds);
            for (DayExchangeEntity dayExchangeEntity : dayExchangeEntities) {
                exchangeMap.put(dayExchangeEntity.getDayReportId(), dayExchangeEntity);
            }
        }
        List<DayReportAdvanceEntity> resList = new ArrayList<>();
        for (DayReportEntity record : records) {
            Integer reportId = record.getId();

            DayExchangeEntity dayExchangeEntity = exchangeMap.get(reportId);
            List<DayReportTaskEntity> dayReportTaskEntities = reportAssTaskMap.get(reportId);

            if(dayExchangeEntity!=null || (!CollectionUtils.isEmpty(dayReportTaskEntities))) {
                DayReportAdvanceEntity entity = new DayReportAdvanceEntity();
                entity.setId(reportId);
                entity.setUserId(record.getUserId());
                entity.setNickname(record.getNickname());
                entity.setAreaName(record.getAreaName());
                entity.setDate(record.getDate());
                entity.setCreateTime(record.getCreateTime());
                entity.setUpdateTime(record.getUpdateTime());
                entity.setLeaveEntity(exchangeMap.get(reportId));
                entity.setDayReportList(reportAssTaskMap.get(reportId));
                resList.add(entity);
            }
        }

        IPage<DayReportAdvanceEntity> resPage = new Page<DayReportAdvanceEntity>();
        resPage.setRecords(resList);
        resPage.setCurrent(dayReportEntityIPage.getCurrent());
        resPage.setSize(dayReportEntityIPage.getSize());
        resPage.setPages(dayReportEntityIPage.getPages());
        resPage.setTotal(dayReportEntityIPage.getTotal());
        return resPage;
    }

    @Override
    public void deleteDayReport(Integer id) throws Exception {
        reportDao.deleteDayReport(id);
    }

    @Override
    public List<Date> queryTimeDistinctByTime(String startTime, String endTime, Integer[] areaId, String projectName) {
        return reportDao.queryTimeDistinctByTime(startTime, endTime, areaId, projectName);
    }

    @Override
    public List<ResEveryWorkTotalTimeEntityParam> queryEveryDateWorkTotalTime(String startTime, String endTime, Integer[] areaId, String projectName) {
        return reportDao.queryEveryDateWorkTotalTime(startTime, endTime, areaId, projectName);
    }

    @Override
    public List<DayReportEntity> searchDayReportByLimitUser(List<Integer> limitUser,Date startDate,Date endDate,Integer userId) {

        return reportDao.searchDayReportByLimitUser(limitUser,startDate,endDate,userId);
    }

    @Override
    public Integer queryCountDraftDayReportByUserId(Integer userId) {
        return reportDao.queryCountDraftDayReportByUserId(userId);
    }

    @Override
    public DraftDayReportEntity queryDraftDayReport(Integer userId) {
        return reportDao.queryDraftDayReport(userId);
    }

    @Override
    public DraftDayExchangeEntity queryDraftDayExchange(Integer userId) {
        return reportDao.queryDraftDayExchange(userId);
    }

    @Override
    public List<DraftDayReportTaskEntity> queryDraftDayReportTask(Integer userId) {
        return reportDao.queryDraftDayReportTask(userId);
    }

    @Override
    public void delDraft(Integer userId) {
        reportDao.delDraft(userId);
    }

    @Override
    public void saveDraft(DraftDayReportEntity draftDayReportEntity, List<DraftDayReportTaskEntity> draftDayReportTaskEntities, DraftDayExchangeEntity draftDayExchangeEntity) {
        reportDao.saveDraft(draftDayReportEntity,draftDayReportTaskEntities,draftDayExchangeEntity);
    }

}
