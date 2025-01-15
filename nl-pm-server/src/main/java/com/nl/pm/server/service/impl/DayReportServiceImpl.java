package com.nl.pm.server.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nl.pm.server.common.DateUtils;
import com.nl.pm.server.common.EntityUtils;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.exception.BaseAuthException;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.AuthErrorCodeEnum;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.registry.IDayReportRegistry;
import com.nl.pm.server.registry.entity.*;
import com.nl.pm.server.registry.param.DayReportSearchEntityParam;
import com.nl.pm.server.service.IDayReportService;
import com.nl.pm.server.service.model.*;
import com.nl.pm.server.service.param.DayReportSearchModelParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DayReportServiceImpl implements IDayReportService {
    @Autowired
    private IDayReportRegistry dayReportRegistry;
    @Autowired
    private SecurityContextUtils securityContextUtils;

    @Override
    public List<Date> queryReportByProjectIdAndAreaId(String startTime, String endTime, Integer projectId, Integer areaId) {
        return dayReportRegistry.queryReportByProjectIdAndAreaId(startTime,endTime,projectId,areaId);
    }

    @Override
    public List<Map<UserEntity,Double>> queryReportTimeByTimeStrTest(String timeStr, Integer projectId, Integer areaId) {
        return dayReportRegistry.queryReportTimeByTimeStrTest(timeStr, projectId, areaId);
    }

    @Override
    public List<UserEntity> queryReportTimeByTimeStr(String timeStr, Integer projectId, Integer areaId) {
        return dayReportRegistry.queryReportTimeByTimeStr(timeStr, projectId, areaId);
    }

    @Override
    public void createDayReport(DayReportAdvanceModel model) throws Exception {
        Integer currentUserId = securityContextUtils.getCurrentUserId();
        if(!model.getUserId().equals(currentUserId)){
            throw new BaseAuthException(AuthErrorCodeEnum.DAY_REPORT_NOT_MYSELF_ERROR);
        }

        String dateStr = DateUtils.convertDateToStr(model.getDate());
        DayReportEntity dayReportEntity = dayReportRegistry.checkDayReportExist(dateStr, model.getUserId());
        if(dayReportEntity!=null){
            throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_EXIST_ERROR);
        }
        DayReportAdvanceEntity entity = convertModelToEntity(model);

        dayReportRegistry.createDayReport(entity);
    }

    @Override
    public void updateDayReport(DayReportAdvanceModel model) throws Exception {
        Integer currentUserId = securityContextUtils.getCurrentUserId();
        if(!model.getUserId().equals(currentUserId)){
            throw new BaseAuthException(AuthErrorCodeEnum.DAY_REPORT_NOT_MYSELF_ERROR);
        }

        DayReportAdvanceEntity oldReport = dayReportRegistry.fetchDayReportDetail(model.getId());
        String oldDateStr = DateUtils.convertDateToStr(oldReport.getDate());
        String newDateStr = DateUtils.convertDateToStr(model.getDate());
        if(!oldDateStr.equals(newDateStr)) {
            DayReportEntity dayReportEntity = dayReportRegistry.checkDayReportExist(newDateStr, model.getUserId());
            if (dayReportEntity != null) {
                throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_EXIST_ERROR);
            }
        }

        DayReportAdvanceEntity entity = convertModelToEntity(model);
        dayReportRegistry.updateDayReport(entity);
    }

    @Override
    public DayReportAdvanceModel fetchDayReportDetail(Integer id) throws Exception {
        DayReportAdvanceEntity entity = dayReportRegistry.fetchDayReportDetail(id);
        DayReportAdvanceModel dayReportAdvanceModel = fillModelWithEntity(entity);
        return dayReportAdvanceModel;
    }

    @Override
    public BasePagesDomain<DayReportAdvanceModel> searchDayReport(DayReportSearchModelParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        DayReportSearchEntityParam entityParam = new DayReportSearchEntityParam();
        entityParam.setLimitUser(param.getLimitUser());
        entityParam.setLimitProject(param.getLimitProject());
        entityParam.setCurrentPage(param.getCurrentPage());
        entityParam.setPageSize(param.getPageSize());
        entityParam.setProjectId(param.getProjectId());
        entityParam.setUserId(param.getUserId());
        entityParam.setStartDate(param.getStartDate());
        entityParam.setEndDate(param.getEndDate());
        entityParam.setSearchVal(param.getSearchVal());
        IPage<DayReportAdvanceEntity> entityIPage = dayReportRegistry.searchDayReport(entityParam);
        List<DayReportAdvanceEntity> records = entityIPage.getRecords();
        List<DayReportAdvanceModel> resRecords = new ArrayList<>();
        for (DayReportAdvanceEntity record : records) {
            DayReportAdvanceModel model = fillModelWithEntity(record);
            resRecords.add(model);
        }
        BasePagesDomain<DayReportAdvanceModel> modelPages = new BasePagesDomain<>(entityIPage.getCurrent(), entityIPage.getSize(), entityIPage.getPages(), entityIPage.getTotal());
        modelPages.setTotalList(resRecords);

        return modelPages;
    }

    @Override
    public void deleteDayReport(Integer id) throws Exception {
        dayReportRegistry.deleteDayReport(id);
    }

    @Override
    public List<DayReportModel> searchDayReportByLimitUser(List<Integer> limitUser,Date startDate,Date endDate,Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<DayReportEntity> dayReportEntities = dayReportRegistry.searchDayReportByLimitUser(limitUser,startDate,endDate,userId);
        List<DayReportModel> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(dayReportEntities)){
            for (DayReportEntity dayReportEntity : dayReportEntities) {
                DayReportModel model = (DayReportModel)EntityUtils.fillModelWithEntity(dayReportEntity);
                list.add(model);
            }
        }
        return list;
    }

    @Override
    public Integer queryCountDraftDayReportByUserId(Integer userId) {
        return dayReportRegistry.queryCountDraftDayReportByUserId(userId);
    }

    @Override
    public DraftDayReportModel queryDraftDayReport(Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        DraftDayReportEntity draftDayReportEntity = dayReportRegistry.queryDraftDayReport(userId);
        return (DraftDayReportModel)EntityUtils.fillModelWithEntity(draftDayReportEntity);
    }

    @Override
    public DraftDayExchangeModel queryDraftDayExchange(Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        DraftDayExchangeEntity draftDayExchangeEntity = dayReportRegistry.queryDraftDayExchange(userId);
        return (DraftDayExchangeModel)EntityUtils.fillModelWithEntity(draftDayExchangeEntity);
    }

    @Override
    public List<DraftDayReportTaskModel> queryDraftDayReportTask(Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<DraftDayReportTaskModel> draftDayReportTaskModels = new ArrayList<>();
        List<DraftDayReportTaskEntity> draftDayReportTaskEntities = dayReportRegistry.queryDraftDayReportTask(userId);
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(draftDayReportTaskEntities)){
            for(DraftDayReportTaskEntity entity:draftDayReportTaskEntities){
                draftDayReportTaskModels.add((DraftDayReportTaskModel)EntityUtils.fillModelWithEntity(entity));
            }
        }
        return draftDayReportTaskModels;
    }

    @Override
    public void delDraft(Integer userId) {
        dayReportRegistry.delDraft(userId);
    }

    @Override
    public void saveDraft(DraftDayReportModel draftDayReportModel, List<DraftDayReportTaskModel> draftDayReportTaskModels, DraftDayExchangeModel draftDayExchangeModel) throws Exception {
        DraftDayReportEntity draftDayReportEntity = null;
        if(draftDayReportModel != null){
            draftDayReportEntity = (DraftDayReportEntity) EntityUtils.convertModelToEntity(draftDayReportModel);
        }
        DraftDayExchangeEntity draftDayExchangeEntity = null;
        if(draftDayExchangeModel != null){
            draftDayExchangeEntity = (DraftDayExchangeEntity) EntityUtils.convertModelToEntity(draftDayExchangeModel);
        }
        List<DraftDayReportTaskEntity> draftDayReportTaskEntities = null;
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(draftDayReportTaskModels)){
            draftDayReportTaskEntities = new ArrayList<>();
            for(DraftDayReportTaskModel model:draftDayReportTaskModels){
                draftDayReportTaskEntities.add((DraftDayReportTaskEntity)EntityUtils.convertModelToEntity(model));
            }
        }
        dayReportRegistry.saveDraft(draftDayReportEntity,draftDayReportTaskEntities,draftDayExchangeEntity);
    }

    public DayReportAdvanceModel fillModelWithEntity(DayReportAdvanceEntity entity) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<DayReportTaskEntity> dayReportList = entity.getDayReportList();

        List<DayReportTaskModel> dayReportTaskModelList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(dayReportList)) {
            for (DayReportTaskEntity dayReportTaskEntity : dayReportList) {
                DayReportTaskModel dayReportTaskModel = (DayReportTaskModel) EntityUtils.fillModelWithEntity(dayReportTaskEntity);
                dayReportTaskModelList.add(dayReportTaskModel);

            }
        }

        DayExchangeEntity leaveEntity = entity.getLeaveEntity();
        DayExchangeModel dayExchangeModel = (DayExchangeModel) EntityUtils.fillModelWithEntity(leaveEntity);

        DayReportAdvanceModel dayReportAdvanceModel = new DayReportAdvanceModel();
        dayReportAdvanceModel.setLeaveModel(dayExchangeModel);
        dayReportAdvanceModel.setDayReportList(dayReportTaskModelList);
        dayReportAdvanceModel.setId(entity.getId());
        dayReportAdvanceModel.setDate(entity.getDate());
        dayReportAdvanceModel.setUserId(entity.getUserId());
        dayReportAdvanceModel.setNickname(entity.getNickname());
        dayReportAdvanceModel.setAreaName(entity.getAreaName());
        dayReportAdvanceModel.setCreateTime(entity.getCreateTime());
        dayReportAdvanceModel.setUpdateTime(entity.getUpdateTime());

        return dayReportAdvanceModel;
    }

    public DayReportAdvanceEntity convertModelToEntity(DayReportAdvanceModel model) throws Exception {
        List<DayReportTaskEntity> taskEntityList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(model.getDayReportList())) {
            for (DayReportTaskModel dayReportTaskModel : model.getDayReportList()) {
                DayReportTaskEntity dayReportTaskEntity = (DayReportTaskEntity) EntityUtils.convertModelToEntity(dayReportTaskModel);
                taskEntityList.add(dayReportTaskEntity);
            }
        }
        DayExchangeModel leaveModel = model.getLeaveModel();
        DayExchangeEntity leaveEntity =(DayExchangeEntity) EntityUtils.convertModelToEntity(leaveModel);

        Integer currentUserId = securityContextUtils.getCurrentUserId();

        DayReportAdvanceEntity entity = new DayReportAdvanceEntity();
        entity.setLeaveEntity(leaveEntity);
        entity.setDayReportList(taskEntityList);
        entity.setDate(model.getDate());
        entity.setUserId(currentUserId);
        entity.setId(model.getId());
        return entity;
    }
}
